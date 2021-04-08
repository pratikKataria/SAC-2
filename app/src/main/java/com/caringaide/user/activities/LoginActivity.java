package com.caringaide.user.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.caringaide.user.R;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.SharedPrefsManager;
import com.caringaide.user.utils.location.AppLocationManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.BuddyConstants.minWaitSecForLogin;
import static com.caringaide.user.utils.CommonUtilities.USER_ID;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.countryAbbrvMap;
import static com.caringaide.user.utils.CommonUtilities.currencyMap;
import static com.caringaide.user.utils.CommonUtilities.getCurrentDateTimeAsString;
import static com.caringaide.user.utils.CommonUtilities.getRegistrationId;
import static com.caringaide.user.utils.CommonUtilities.getTimeInMillis;
import static com.caringaide.user.utils.CommonUtilities.hideKeyPad;
import static com.caringaide.user.utils.CommonUtilities.notifyWithVibration;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.showProgressDialog;
import static com.caringaide.user.utils.CommonUtilities.toastShort;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActitivty";
    private static int loginCount = 0;
    private static int lastLoginFailSec = 0;
    private static int currentTimeInSec = 0;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BuddyApp.setCurrentActivity(this);
        setContentView(R.layout.login_activity_layout);
        setUicomponents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null!=progressDialog)
        progressDialog.dismiss();
    }

    private void setUicomponents() {
        final EditText loginUsername = findViewById(R.id.login_username);
        final EditText loginPassword = findViewById(R.id.login_password);
        Button loginSubmitBtn = findViewById(R.id.login_button);
        TextView forgotPasswordView = findViewById(R.id.forgot_password_btn);
        TextView signUpButtonView = findViewById(R.id.signup_btn);
        loginSubmitBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                setUpLoginAction(loginUsername, loginPassword);
            }
        });
        forgotPasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivty.class);
                startActivity(intent);
            }
        });
        signUpButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpLoginAction(EditText loginUsername, EditText loginPassword) {
        final String userName = loginUsername.getText().toString().trim();
        final String password = loginPassword.getText().toString().trim();
        if (userName.isEmpty()){
            loginUsername.setError(getString(R.string.ask_username));notifyWithVibration(this);
        }else if (password.isEmpty()){
            loginPassword.setError(getString(R.string.ask_password));notifyWithVibration(this);
        }else{
            if (checkLoginActivity()) {
                RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                    @Override
                    public void onResponse(RemoteResponse remoteresponse) {
                        loginResponse(remoteresponse);
                    }
                }, LoginActivity.this, null);
                Map<String, String> params = new HashMap<>();
                params.put("username", userName);
                params.put("password", password);
                requestParams.setRequestParams(params);
                UserServiceHandler.userLogin(requestParams);
                progressDialog = showProgressDialog(this,getString(R.string.signng_in));
            }

        }
    }
    private void loginResponse(RemoteResponse remoteResponse){
        progressDialog.dismiss();
        String customErrorMsg = getString(R.string.login_fail);
        if(null == remoteResponse){
            toastShort(getString(R.string.login_fail) + "." + getString(R.string.no_internet) );
        }else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            hideKeyPad();
            try {
                if (!CommonUtilities.isErrorsFromResponse(this, remoteResponse)) {
                    String loginResponse = remoteResponse.getResponse();
                    Log.d(TAG, "Login Response  " + loginResponse);
                    if (null != loginResponse) {
                        JSONObject loginObj = new JSONObject(loginResponse);
                        Map<String, String> usrInfoMap = new HashMap<>();
                        if (loginObj.has("data")) {
                            JSONObject loginData = loginObj.getJSONObject("data");
                            String loginFName = loginData.getString("first_name");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_FNAME, loginFName);
                            String loginLName = loginData.getString("last_name");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_LNAME, loginLName);
                            String fullName = loginData.getString("full_name");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_FULLNAME, fullName);
                            String userName = loginData.getString("username");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_USERNAME, userName);
                            String loginMobile = loginData.getString("mobile");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_MOBILE, loginMobile);
                            String loginPhone = loginData.getString("phone");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_PHONE, loginPhone);
                            String loginEmail = loginData.getString("email");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_EMAIL, loginEmail);
                            String loginId = loginData.getString("id");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_ID, loginId);
                            String loginDob = loginData.getString("dob");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_DOB, loginDob);

                            String loginActive = loginData.getString("active");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_ISACTIVE, loginActive);
                            String loginToken = loginData.getString("token");
                            Log.d(TAG, "loginResponse: token" + loginToken);
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_TOKEN, loginToken);
                            String loginSSN = loginData.getString("ssn");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_SSN, loginSSN);
                            String loginOrgId = loginData.getString("org_id");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_ORG, loginOrgId);
                            String loginGender = loginData.getString("gender");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_GENDER, loginGender);
                            String loginType = loginData.getString("type");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_TYPE_LOGIN_ID, loginType);
                            String loginBen = loginData.getString("beneficiary");
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_LOGIN_BEN, loginBen);
                            if (loginData.has("mkt_user") && !loginData.isNull("mkt_user")){
                                if (loginData.getJSONObject("mkt_user").getBoolean("status")) {
                                    String mktRefToken = loginData.getJSONObject("mkt_user").getString("mkt_code");
                                    usrInfoMap.put(BuddyConstants.UserInfo.USER_MKT_TOKEN, mktRefToken);
                                }
                            }
                            String currency = currencyMap.get(countryAbbrvMap.get(BuddyApp.getCountryAbbrv()));
                            usrInfoMap.put(BuddyConstants.UserInfo.USER_CURRENCY, currency);
                            shareDeviceToken(usrInfoMap);
                            SharedPrefsManager.getInstance().storeAuthToken(loginToken);

                        }

                    } else {
                        showAlertMsg(getString(R.string.info_label),customErrorMsg);
                        Log.d(TAG, "################## Remote Response############\n\n" + remoteResponse.toString() + "\n\n");
                        Log.e(TAG, "Error in the LoginActivity  " + customErrorMsg);

                    }
                } else {
                    loginCount+=1;
                    lastLoginFailSec = (int) (getTimeInMillis(getCurrentDateTimeAsString())/1000);
                    String loginResponse = remoteResponse.getResponse();
                    JSONObject loginObj = new JSONObject(loginResponse);
                    if (loginObj.has("message")){
                        String message = loginObj.getString("message");
                        customErrorMsg = CommonUtilities.getServerMessageCode(message);
                    }
                    showAlertMsg(getString(R.string.info_label),customErrorMsg);
                }


            } catch (JSONException e) {
                toastShort(customErrorMsg);
                e.printStackTrace();
            }
        }

    }

    private void shareDeviceToken(final Map<String, String> userInfoMap) {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                Log.e(TAG, "onResponse: of Share RegId "+remoteresponse.getResponse(),null );
                deviceDetailResponse(userInfoMap,remoteresponse);
            }
        },this,null);
        Map<String,String> params = new HashMap<>();
        params.put("device_token",getRegistrationId(this));
        params.put("user_id",USER_ID);
        params.put("app_name", BuddyConstants.APP_NAME);
        params.put("app_version","1.0");
        params.put("device_name",manufacturer);
        params.put("device_model","Android");
        params.put("device_version",androidVersion);
        params.put("status","1");
        params.put("latitude", AppLocationManager.getCurrentLatitude()+"");
        params.put("longitude",AppLocationManager.getCurrentLongitude()+"");
        params.put("locality",AppLocationManager.getCurrentLocationAddress());
        requestParams.setRequestParams(params);
        UserServiceHandler.shareRegId(requestParams);
    }


    /**
     * handle response from device detail remote call. If no issues call HomeActivity
     * @param userInfoMap
     * @param remoteResponse
     */
    private void deviceDetailResponse(Map<String,String> userInfoMap,RemoteResponse remoteResponse){
        String customErrorMsg = getString(R.string.post_login_fail);
        if(null == remoteResponse){
            toastShort(customErrorMsg);
        }else{
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!CommonUtilities.isErrorsFromResponse(LoginActivity.this, remoteResponse)) {
                    loginCount =0;
                    lastLoginFailSec = 0;
                    currentTimeInSec = 0;
                    CommonUtilities.USER_ID = userInfoMap.get(BuddyConstants.UserInfo.USER_LOGIN_ID);
                    SharedPrefsManager.createSession(userInfoMap);
                    toastShort(getString(R.string.login_succ));
                    alertAndAction(this, getString(R.string.disclaimer_title), getString(R.string.agree_disclaimer),
                            getString(R.string.ok), null, new AlertAction() {
                                @Override
                                public void positiveAction() {
                                    Intent homeIntent = new Intent(LoginActivity.this, LaunchActivity.class);
                                    startActivity(homeIntent);
                                }

                                @Override
                                public void negativeAction() {

                                }
                            });
//                    if (!CommonUtilities.hav)
                    /*if (userInfoMap.get(BuddyConstants.UserInfo.USER_LOGIN_BEN).equals("0")) {
                        Intent regIntent = new Intent(this, RegisterBeneficiaryActivity.class);
                        startActivity(regIntent);
                    }else{*/

//                    }
                }else{
                    showAlertMsg(getString(R.string.info_label),customErrorMsg);
                }
            }catch(Exception ex){
                toastShort(customErrorMsg);
                ex.printStackTrace();
            }
        }

    }
    private boolean checkLoginActivity(){
        if (loginCount>=3){
            currentTimeInSec = (int) (getTimeInMillis(getCurrentDateTimeAsString())/1000);
            int secDiff = currentTimeInSec- lastLoginFailSec;
            Log.d(TAG, "checkLoginActivity: secoDiff "+secDiff);
            if (loginCount*minWaitSecForLogin>secDiff){
                int timeToLogin = loginCount*minWaitSecForLogin-secDiff;
                toastShort(getString(R.string.try_login_aftr_label)+timeToLogin+getString(R.string.seconds_label));
                return false;
            }else {
                return true;
            }
        }//else bypass login call -- initial condition
        return true;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
