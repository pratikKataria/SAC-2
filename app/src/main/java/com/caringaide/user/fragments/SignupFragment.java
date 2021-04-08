package com.caringaide.user.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.caringaide.user.R;
import com.caringaide.user.activities.LoginActivity;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.Zipcodes;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.PatternUtil;
import com.caringaide.user.utils.SharedPrefsManager;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.BuddyConstants.FIRST_NAME_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.FIRST_NAME_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.JSON_MESSAGE;
import static com.caringaide.user.utils.BuddyConstants.LAST_NAME_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.LAST_NAME_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.OTP_RESEND_KEY;
import static com.caringaide.user.utils.BuddyConstants.OTP_RESEND_MOBILE;
import static com.caringaide.user.utils.BuddyConstants.OTP_RESEND_REASON;
import static com.caringaide.user.utils.BuddyConstants.PASSWORD_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.PASSWORD_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.PRIVACY_POLICY_URL;
import static com.caringaide.user.utils.BuddyConstants.TC_URL;
import static com.caringaide.user.utils.BuddyConstants.USERNAME_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.USERNAME_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.WRONG_OTP;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.checkStringData;
import static com.caringaide.user.utils.CommonUtilities.countryAbbrvMap;
import static com.caringaide.user.utils.CommonUtilities.getServerMessageCode;
import static com.caringaide.user.utils.CommonUtilities.getWrongOtpCount;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.isOpenOtpDialog;
import static com.caringaide.user.utils.CommonUtilities.mobileMinValidation;
import static com.caringaide.user.utils.CommonUtilities.notifyWithVibration;
import static com.caringaide.user.utils.CommonUtilities.populateCountryCodeAdapter;
import static com.caringaide.user.utils.CommonUtilities.resetWrongOtpCount;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment implements OTPDialogFragment.DialogListener {

    private static final String TAG = "SignupFragment";
    private View signupView;
    private EditText signUpFirstName, signUpLastName, signUpUserName,signUpEmail, signUpZipcode,
            signUpMobnum, signUpPassword, signUpConfirmPass, signUpReferralCode;
    private TextInputLayout etReferralCodeLayout,etPasswordLayout,etConfirmPasswordLayout;
    private Spinner signUpCCode;
    private Button signUpSubmit;
    private String mobile = "",netOtpValue=null;
//    private AlertDialog otpDialog;
    private TextView otpResponse;
    private Button proceedReqBtn,cancelReqBtn;
    private WebView tcWebview;
    private RelativeLayout tcWebViewLayout;
    private CheckBox checkTc, checkAge;
    private TextView checkTcText,checkPrivacyTxt,signUpReferralLink;
    private ProgressDialog progDailog;
    private Button closeTc;
    private ImageButton usernameInfo, passwordInfo;
    private static String userUname = "",userMobile = "";


    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        signupView = inflater.inflate(R.layout.signup_activity_layout, container, false);
        setUpViewComponents();
        return signupView;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (isOpenOtpDialog()){
            openOtpDialog();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetWrongOtpCount();
    }

    /**
     * initializes ui components
     */
    private void setUpViewComponents() {
        etPasswordLayout = signupView.findViewById(R.id.signup_password_layout);
        etConfirmPasswordLayout = signupView.findViewById(R.id.signup_confirm_password_layout);
        tcWebview = signupView.findViewById(R.id.wv_tc_user);
        tcWebViewLayout = signupView.findViewById(R.id.tc_layout_user);
        checkTc = signupView.findViewById(R.id.signup_terms_conditions_checkbox);
        checkAge = signupView.findViewById(R.id.signup_age_checkbox);
        checkTcText = signupView.findViewById(R.id.signup_terms_conditions);
        checkPrivacyTxt = signupView.findViewById(R.id.signup_privacy_policy);
        closeTc = signupView.findViewById(R.id.close_webview);
        signUpFirstName = signupView.findViewById(R.id.signup_fname);
        signUpLastName = signupView.findViewById(R.id.signup_lname);
        signUpUserName = signupView.findViewById(R.id.signup_uname);
        signUpEmail = signupView.findViewById(R.id.signup_email);
        signUpCCode = signupView.findViewById(R.id.signup_ccode);
        populateCountryCodeAdapter(getActivity(),signUpCCode);
        signUpMobnum = signupView.findViewById(R.id.signup_mobile);
        signUpZipcode =signupView.findViewById(R.id.signup_zipcode);
        signUpPassword = signupView.findViewById(R.id.signup_password);
        signUpConfirmPass = signupView.findViewById(R.id.signup_confirm_password);
        signUpSubmit = signupView.findViewById(R.id.signup_submit_btn);
        signUpReferralLink = signupView.findViewById(R.id.apply_referral_code_link);
        signUpReferralCode = signupView.findViewById(R.id.signup_apply_refcode);
        etReferralCodeLayout = signupView.findViewById(R.id.signup_apply_refcode_layout);
        passwordInfo = signupView.findViewById(R.id.signup_password_info);
        usernameInfo = signupView.findViewById(R.id.signup_username_info);
        passwordInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertMsg(getString(R.string.info_password_title),getString(R.string.info_password));
            }
        });
        usernameInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertMsg(getString(R.string.info_username_title),getString(R.string.info_uname));
            }
        });
        signUpSubmit.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                signUp();
//                Log.d(TAG, "onClick: isOnce?");
            }

//
//            }
        });
        signUpReferralLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etReferralCodeLayout.setVisibility(etReferralCodeLayout.getVisibility() == View.VISIBLE?View.GONE:View.VISIBLE);
            }
        });
        checkTcText.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                tcWebViewLayout.setVisibility(View.VISIBLE);
                // create a WebView
                progDailog = ProgressDialog.show(getActivity(), getString(R.string.loading),
                        getString(R.string.please_wait), true);
                progDailog.setCancelable(false);
                tcWebview.getSettings().setJavaScriptEnabled(true);
                tcWebview.getSettings().setLoadWithOverviewMode(true);
                tcWebview.getSettings().setUseWideViewPort(true);
                tcWebview.setWebViewClient(new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        progDailog.show();
                        view.loadUrl(url);
                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        progDailog.dismiss();
//                        super.onPageFinished(view, url);

                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode,
                                                String description, String failingUrl) {
                        toastShort(description+" "+ failingUrl);
                        super.onReceivedError(view, errorCode, description, failingUrl);
                    }
                });

                tcWebview.loadUrl(TC_URL);
            }
        });
        checkPrivacyTxt.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                tcWebViewLayout.setVisibility(View.VISIBLE);
                // create a WebView
                progDailog = ProgressDialog.show(getActivity(), getString(R.string.loading),
                        getString(R.string.please_wait), true);
                progDailog.setCancelable(false);
                tcWebview.getSettings().setJavaScriptEnabled(true);
                tcWebview.getSettings().setLoadWithOverviewMode(true);
                tcWebview.getSettings().setUseWideViewPort(true);
                tcWebview.setWebViewClient(new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        progDailog.show();
                        view.loadUrl(url);
                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        progDailog.dismiss();
//                        super.onPageFinished(view, url);

                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode,
                                                String description, String failingUrl) {
                        toastShort(description+" "+ failingUrl);
                        super.onReceivedError(view, errorCode, description, failingUrl);
                    }
                });

                tcWebview.loadUrl(PRIVACY_POLICY_URL);
            }
        });
    closeTc.setOnClickListener(new SingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            tcWebViewLayout.setVisibility(View.GONE);
        }
    });
    }

    /**
     * validation method for signup fields
     */
    private void signUp() {
        Context curActivity = null == getActivity()? BuddyApp.getCurrentActivity() : getActivity();
        boolean isZipAvailable = false;
        String firstName = signUpFirstName.getText().toString().trim();
        String lastName = signUpLastName.getText().toString().trim();
        String password = signUpPassword.getText().toString().trim();
        String confirmPassword = signUpConfirmPass.getText().toString();
        String userName = signUpUserName.getText().toString().trim();
        String email = signUpEmail.getText().toString().trim();
        String zip = signUpZipcode.getText().toString().trim();
        ArrayList<Zipcodes> zipcodes = SharedPrefsManager.getInstance().getZipcodeList(zip);
        for (Zipcodes zipcode : zipcodes) {
            if (zip.equals(zipcode.getZipcode())) {
                isZipAvailable = true;
                break;
            }
        }
        if (signUpCCode.getSelectedItem().toString().isEmpty()){
            String ccode = "+1";
            mobile = ccode.concat(signUpMobnum.getText().toString().trim());
        }else{
            mobile = signUpCCode.getSelectedItem().toString().trim().concat(signUpMobnum.getText().toString().trim());
        }
        if (firstName.isEmpty()){
            signUpFirstName.setError(getString(R.string.ask_fname));notifyWithVibration(curActivity);
            signUpFirstName.requestFocus();
        }else if( firstName.length()<FIRST_NAME_MIN_LENGTH
                || firstName.length()>FIRST_NAME_MAX_LENGTH){
            signUpFirstName.setError(getString(R.string.ask_name_length)
                    .concat(String.valueOf(FIRST_NAME_MIN_LENGTH))
                    .concat(getString(R.string.and_label))
                    .concat(String.valueOf(FIRST_NAME_MAX_LENGTH)));
            notifyWithVibration(curActivity);signUpFirstName.requestFocus();
        }else if (!checkStringData(signUpFirstName)){
//            signUpFirstName.setError(getString(R.string.ask_fname));
            notifyWithVibration(curActivity);
            signUpFirstName.requestFocus();
        }else if (!firstName.matches(PatternUtil.nameRegex)){
            signUpFirstName.setError(getString(R.string.ask_fname));notifyWithVibration(curActivity);
            signUpFirstName.requestFocus();
        } else if (lastName.isEmpty()){
            signUpLastName.setError(getString(R.string.ask_lname));notifyWithVibration(curActivity);
            signUpLastName.requestFocus();
        }else if(lastName.length()<LAST_NAME_MIN_LENGTH || lastName.length()>LAST_NAME_MAX_LENGTH){
            signUpLastName.setError(getString(R.string.ask_name_length)
                    .concat(String.valueOf(LAST_NAME_MIN_LENGTH))
                    .concat(getString(R.string.and_label))
                    .concat(String.valueOf(LAST_NAME_MAX_LENGTH)))
            ;notifyWithVibration(curActivity);
            signUpLastName.requestFocus();
        }else if (!checkStringData(signUpFirstName)){
//            signUpLastName.setError(getString(R.string.ask_lname));
            notifyWithVibration(curActivity);
            signUpLastName.requestFocus();
        }else if (!lastName.matches(PatternUtil.nameRegex)){
            signUpLastName.setError(getString(R.string.ask_lname));notifyWithVibration(curActivity);
            signUpLastName.requestFocus();
        }else if (userName.isEmpty()){
            signUpUserName.setError(getString(R.string.ask_username));notifyWithVibration(curActivity);
            signUpUserName.requestFocus();
        }else if(userName.length()<USERNAME_MIN_LENGTH || userName.length()>USERNAME_MAX_LENGTH){
            signUpUserName.setError(getString(R.string.ask_name_length)
                    .concat(String.valueOf(USERNAME_MIN_LENGTH))
                    .concat(getString(R.string.and_label))
                    .concat(String.valueOf(USERNAME_MAX_LENGTH)));
            notifyWithVibration(curActivity);signUpUserName.requestFocus();
        }else if (!userName.matches(PatternUtil.userNameRegex)){
            signUpUserName.setError(getString(R.string.ask_username));notifyWithVibration(curActivity);
            signUpUserName.requestFocus();
        }else if (signUpCCode.getSelectedItem().toString().trim().isEmpty()){
            signUpMobnum.setError(getString(R.string.ask_country_code));notifyWithVibration(curActivity);
            signUpMobnum.requestFocus();
        }else if (signUpMobnum.getText().toString().trim().isEmpty()|| mobile.isEmpty() ){
            signUpMobnum.setError(getString(R.string.ask_mobile));notifyWithVibration(curActivity);
            signUpMobnum.requestFocus();
        }else if(signUpMobnum.getText().toString().trim().length()!=
                mobileMinValidation.get(signUpCCode.getSelectedItem().toString())){
            signUpMobnum.setError(getString(R.string.ask_mobile));notifyWithVibration(curActivity);
            signUpMobnum.requestFocus();
        }else if (!signUpMobnum.getText().toString().trim().matches(PatternUtil.mobileRegex)){
            signUpMobnum.setError(getString(R.string.ask_mobile));notifyWithVibration(curActivity);
            signUpMobnum.requestFocus();
        }else if (email.isEmpty()||!email.matches(PatternUtil.emailRegex)) {
            signUpEmail.setError(getString(R.string.ask_email));notifyWithVibration(curActivity);
            signUpEmail.requestFocus();
        }else if (password.isEmpty()){
            signUpPassword.setError(getString(R.string.ask_password));notifyWithVibration(curActivity);
            etPasswordLayout.requestFocus();
        }else if( password.length()<PASSWORD_MIN_LENGTH || password.length()>PASSWORD_MAX_LENGTH){
            String passwordLengthErr = String.format(getString(R.string.ask_password_length),PASSWORD_MIN_LENGTH,PASSWORD_MAX_LENGTH);
            signUpPassword.setError(passwordLengthErr);
            notifyWithVibration(curActivity);
            etPasswordLayout.requestFocus();
            Log.d(TAG, "signUp: "+etPasswordLayout.getEditText().getText().toString());
        }else if (!password.matches(PatternUtil.passwordRegex)){
            signUpPassword.setError(getString(R.string.ask_password_format));
            notifyWithVibration(curActivity);
            etPasswordLayout.requestFocus();
            Log.d(TAG, "signUp: "+etPasswordLayout.getEditText().getText().toString());
        }else if (confirmPassword.isEmpty() || confirmPassword.length()<PASSWORD_MIN_LENGTH
                || confirmPassword.length()>PASSWORD_MAX_LENGTH){
            signUpPassword.setError(getString(R.string.ask_password_format));
            notifyWithVibration(curActivity);
            etConfirmPasswordLayout.requestFocus();
        }else if (!confirmPassword.matches(PatternUtil.passwordRegex)){
            signUpConfirmPass.setError(getString(R.string.ask_password_format));
            notifyWithVibration(curActivity);
            etConfirmPasswordLayout.requestFocus();
        }else if (!password.equals(confirmPassword)){
            signUpConfirmPass.setError(getString(R.string.match_password));
            notifyWithVibration(curActivity); etConfirmPasswordLayout.requestFocus();
        }else if (zip.isEmpty()){
            signUpZipcode.setError(getString(R.string.ask_zip));notifyWithVibration(curActivity);
            signUpZipcode.requestFocus();
        }else if (!zip.matches(PatternUtil.zipcode_pattern)){
            signUpZipcode.setError(getString(R.string.ask_zip));notifyWithVibration(curActivity);
            signUpZipcode.requestFocus();
        }else if (!isZipAvailable){
            signUpZipcode.setError(getString(R.string.no_service_area));notifyWithVibration(curActivity);
            signUpZipcode.requestFocus();
        }else if (!checkAge.isChecked()){
            toastShort(getString(R.string.ask_check_age));notifyWithVibration(curActivity);
        }else if (!checkTc.isChecked()){
            toastShort(getString(R.string.ask_check_tc));notifyWithVibration(curActivity);
        }else{
            userUname = userName;
            userMobile = mobile;
            goToSignUp(null);
        }


    }

    /**
     * remote call for signup
     * @param otp
     */
    private void goToSignUp(String otp){
        String countryId = CommonUtilities.getCountryIdFromCountryCode(countryAbbrvMap.get(BuddyApp.getCountryAbbrv()));
        String orgId = null==SharedPrefsManager.getInstance().getOrganization(countryId)?"1":
                SharedPrefsManager.getInstance().getOrganization(countryId).getId();
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                handleSignUpResponse(remoteresponse);
            }
        },getActivity(),null);
        Map<String,String> params = new HashMap<>();
        params.put("email",signUpEmail.getText().toString().trim());
        params.put("password",signUpPassword.getText().toString().trim());
        params.put("first_name",signUpFirstName.getText().toString().trim());
        params.put("last_name",signUpLastName.getText().toString().trim());
        params.put("username",signUpUserName.getText().toString().trim());
        params.put("mobile",mobile);
        params.put("dob","1970-01-01");
        params.put("phone",mobile);
        params.put("active","1");
        params.put("agree_terms",checkTc.isChecked()? "1":"0");
        params.put("ssn","510234341");
        params.put("org_id",orgId);
//        params.put("org_id","1");
        params.put("gender","N");
        params.put("offline","0");
        params.put("ref_token",signUpReferralCode.getText().toString());
        params.put("type","2");
        if (null != otp){
            params.put("otp",otp);
        }
        requestParams.setRequestParams(params);
        UserServiceHandler.signUpUser(requestParams);
    }

    /**
     * remote response of signup
     * @param remoteResponse
     */
    private void handleSignUpResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.signup_failed);
            if (null == remoteResponse) {
                toastShort(context.getString(R.string.signup_failed));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        String response = remoteResponse.getResponse();
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has(JSON_MESSAGE)) {
                            String message = jsonObject.getString(JSON_MESSAGE);
                            if (message.equalsIgnoreCase(BuddyConstants.VERIFY_OTP)) {
                                openOtpDialog();
                            } else {
                                alertAndAction(getActivity(), getString(R.string.signup_succ_title),
                                        getString(R.string.signup_succ), getString(R.string.ok), null,
                                        new AlertAction() {
                                            @Override
                                            public void positiveAction() {
                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void negativeAction() {

                                            }
                                        });
                            }
                        } else {
                            toastShort(getString(R.string.signup_failed));
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        String message = jsonObject.getString(JSON_MESSAGE);
                        if (message.equalsIgnoreCase(BuddyConstants.NOT_AUTHORIZED)) {
                            customErrorMsg = getServerMessageCode(BuddyConstants.NOT_AUTHORIZED_MKT_CODE);
                            toastShort(customErrorMsg);
                        }else if (message.equalsIgnoreCase(WRONG_OTP)) {
                            if (getWrongOtpCount(getActivity())) {
                                openOtpDialog();
                                toastShort(getServerMessageCode(WRONG_OTP));
                            }//else is handled in method itself
                        } else {
                            customErrorMsg = getServerMessageCode(message);
                            toastShort(customErrorMsg);
                        }
                       /* if (null != otpResponse) {
                            otpResponse.setText(customErrorMsg);
                        } else {
                            toastShort(customErrorMsg);
                        }*/
                    }
                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
            }
        }

    }

    /**
     * open otp dialog
     */
    private void openOtpDialog() {
        OTPDialogFragment dialogFragment = new OTPDialogFragment();
        dialogFragment.setDialogListener(SignupFragment.this);
        Bundle bundle = new Bundle();
        bundle.putBoolean("notAlertDialog", true);
        bundle.putString(OTP_RESEND_REASON, BuddyConstants.SIGNUP_USER_CONTEXT);
        bundle.putString(OTP_RESEND_KEY, userUname);
        bundle.putString(OTP_RESEND_MOBILE, userMobile);
        dialogFragment.setArguments(bundle);
        FragmentManager fm = null != getFragmentManager()? getFragmentManager(): getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
            ft.addToBackStack(null);
            if (!prev.isVisible()) {
                dialogFragment.show(ft, "dialog");
            }
        }else{
            ft.addToBackStack(null);
            dialogFragment.show(ft, "dialog");
        }
       /* if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft, "dialog");*/
    }

    private void resendOtpResponse(RemoteResponse remoteresponse) {
        Log.d(TAG, "resendOtpResponse: ");
    }

    /**
     * override method for getting otp value from otp dialog
     * @param otpVal
     */
    @Override
    public void onFinishGettingOtp(String otpVal) {
        goToSignUp(otpVal);
    }
}
