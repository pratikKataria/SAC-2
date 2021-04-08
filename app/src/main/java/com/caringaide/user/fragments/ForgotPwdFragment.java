package com.caringaide.user.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.caringaide.user.utils.PatternUtil;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.BuddyConstants.OTP_RESEND_REASON;
import static com.caringaide.user.utils.BuddyConstants.PASSWORD_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.PASSWORD_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.WRONG_OTP;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.getServerMessageCode;
import static com.caringaide.user.utils.CommonUtilities.getWrongOtpCount;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.isOpenOtpDialog;
import static com.caringaide.user.utils.CommonUtilities.mobileMinValidation;
import static com.caringaide.user.utils.CommonUtilities.notifyWithVibration;
import static com.caringaide.user.utils.CommonUtilities.populateCountryCodeAdapter;
import static com.caringaide.user.utils.CommonUtilities.resetWrongOtpCount;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.toastLong;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPwdFragment extends Fragment implements OTPDialogFragment.DialogListener {

    private static final String TAG = "ForgotPwdFragment";
    private View forgotView;
    private Button verifyUserBtn;
    private EditText forgotUname;
    private Spinner forgotCcode;
    private EditText forgotMobile;
    private EditText forgotPassword;
    private EditText forgotConfirmPassword;
    private Button submitForgotPwd;
    private ImageButton forgotPwdInfo;
    private LinearLayout passwordLayout;
//    private AlertDialog otpDialog;
    private TextView otpResponse;
    private String netOtpValue = "";
    private Button proceedReqBtn;
    private Button cancelReqBtn;
    private RadioGroup resetPasswordGroup;
    private RadioButton userNameRadio,mobileRadio;
    private LinearLayout mobileLayout;
    private TextInputLayout usernameLayout;
    private Context context;

    public ForgotPwdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        forgotView = inflater.inflate(R.layout.forgot_password_activty_layout, container, false);
        setUIcomponents();
//        setBackAction();
        return forgotView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isOpenOtpDialog()){
            openOtpDialog();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetWrongOtpCount();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    private void setBackAction() {
        forgotView.setFocusableInTouchMode(true);
        forgotView.requestFocus();
        forgotView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().beginTransaction().remove(ForgotPwdFragment.this).commit();
                    getActivity().onBackPressed();
                    return true;
                }
                return false;
            }
        } );
    }
    /**
     * initialize ui components
     */
    private void setUIcomponents() {
        resetPasswordGroup = forgotView.findViewById(R.id.param_to_reset_radio_group);
        userNameRadio = forgotView.findViewById(R.id.username_to_reset_pass);
        mobileRadio = forgotView.findViewById(R.id.mobile_to_reset_pass);
        mobileLayout = forgotView.findViewById(R.id.reset_pass_mobile_layout);
        usernameLayout = forgotView.findViewById(R.id.forgot_username_layout);
        forgotPwdInfo = forgotView.findViewById(R.id.forgot_password_info);
        verifyUserBtn = forgotView.findViewById(R.id.verify_user);
        forgotUname = forgotView.findViewById(R.id.forgot_uname);
        forgotCcode = forgotView.findViewById(R.id.forgot_ccode);
        populateCountryCodeAdapter(getActivity(),forgotCcode);
        forgotMobile = forgotView.findViewById(R.id.forgot_mobile);
        forgotPassword = forgotView.findViewById(R.id.forgot_password);
        forgotConfirmPassword = forgotView.findViewById(R.id.forgot_confirm_password);
        submitForgotPwd = forgotView.findViewById(R.id.submit_user_password);
        passwordLayout = forgotView.findViewById(R.id.forgot_password_layout);
        resetPasswordGroup.setEnabled(true);
        forgotPwdInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertMsg(getString(R.string.info_password_title),getString(R.string.info_password));
            }
        });
        verifyUserBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {getChangePasswordData(null);
            }
        });
        submitForgotPwd.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (null!= netOtpValue && !netOtpValue.isEmpty()){
                    getChangePasswordData(netOtpValue);
                }
            }
        });
        resetPasswordGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.username_to_reset_pass){
                    usernameLayout.setVisibility(View.VISIBLE);
                    mobileLayout.setVisibility(View.GONE);
                } else if (checkedId == R.id.mobile_to_reset_pass){
                    usernameLayout.setVisibility(View.GONE);
                    mobileLayout.setVisibility(View.VISIBLE);
                }
                //close password layout whwnever the reset option changed
                if (passwordLayout.getVisibility() == View.VISIBLE){
                    netOtpValue = "";
                    passwordLayout.setVisibility(View.GONE);
                    verifyUserBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * check validation of fields
     * @param editText
     * @param pattern
     * @param message
     * @return
     */
    private boolean checkValidity(EditText editText, String pattern, String message) {
        Context curActivity = BuddyApp.getCurrentActivity();
        if (editText.getText().toString().trim().isEmpty()){
            editText.setError(message);notifyWithVibration(curActivity);
            editText.requestFocus();
        }else if (!editText.getText().toString().trim().matches(pattern)){
            editText.setError(message);notifyWithVibration(curActivity);
            editText.requestFocus();
        }else{
            return true;
        }
        return false;
    }

    /**
     * open the OTP dialog fragment for entering otp
     */
    private void openOtpDialog() {
        OTPDialogFragment dialogFragment = new OTPDialogFragment();
        dialogFragment.setDialogListener(ForgotPwdFragment.this);
        Bundle bundle = new Bundle();
        bundle.putBoolean("notAlertDialog", true);
        bundle.putString(OTP_RESEND_REASON, "forgot");
        dialogFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
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
    }

    /**
     * send server request to change password
     * @param netOtpValue
     */
    private void getChangePasswordData(String netOtpValue) {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                forgotPasswordResponse(remoteresponse);
            }
        },getActivity(),null);
        Map<String,String> params = new HashMap<>();
        params.put("user_type", "2");
        if (userNameRadio.isChecked()){
            if (checkValidity(forgotUname, PatternUtil.userNameRegex, getString(R.string.ask_username))) {
                params.put("username", forgotUname.getText().toString().trim());
            }else{
                return;
            }
        }else {
            String mobile =  forgotCcode.getSelectedItem().toString().trim().concat(forgotMobile.getText().toString().trim());
            if (forgotCcode.getSelectedItem().toString().isEmpty() || forgotMobile.getText().toString().isEmpty()){
                forgotMobile.setError(getString(R.string.ask_mobile));notifyWithVibration(getActivity());
                forgotMobile.requestFocus();
                return;
            }else if(forgotCcode.getSelectedItem().toString().isEmpty()) {
                forgotMobile.setError(getString(R.string.ask_country_code));
                notifyWithVibration(getActivity());
                forgotMobile.requestFocus();
                return;
            }else if(forgotMobile.getText().toString().trim().length()!=
                    mobileMinValidation.get(forgotCcode.getSelectedItem().toString())) {
                forgotMobile.setError(getString(R.string.ask_mobile));
                notifyWithVibration(getActivity());
                forgotMobile.requestFocus();
                return;
            }else if (!forgotMobile.getText().toString().trim().matches(PatternUtil.mobileRegex)){
                forgotMobile.setError(getString(R.string.ask_mobile));notifyWithVibration(getActivity());
                forgotMobile.requestFocus();
                return;
            }else {
                params.put("mobile", forgotCcode.getSelectedItem().toString().trim().concat(forgotMobile.getText().toString().trim()));
            }
        }
            if (null != netOtpValue) {
                params.put("otp", netOtpValue);
                if (forgotPassword.getText().toString().length()<PASSWORD_MIN_LENGTH ||
                        forgotPassword.getText().toString().length()>PASSWORD_MAX_LENGTH){
                    String passwordLengthErr = String.format(getString(R.string.ask_password_length),
                            PASSWORD_MIN_LENGTH,PASSWORD_MAX_LENGTH);
                    forgotPassword.setError(passwordLengthErr);
                    notifyWithVibration(getActivity());
                    forgotPassword.requestFocus();
                    return;
                }
                if (checkValidity(forgotPassword, PatternUtil.passwordRegex,
                        getString(R.string.ask_password_format))) {
                    params.put("password", forgotPassword.getText().toString().trim());
                }else {
                    return;
                }
                if(!forgotPassword.getText().toString().trim()
                        .equals(forgotConfirmPassword.getText().toString().trim())){
                    forgotConfirmPassword.setError(getString(R.string.match_password));
                    return;
                }
                if (checkValidity(forgotConfirmPassword, PatternUtil.passwordRegex,
                        getString(R.string.ask_password_format))) {
                    params.put("confirmPassword", forgotConfirmPassword.getText().toString().trim());

                }else {
                    return;
                }
            }
//            submitForgotPwd.setEnabled(false);
            requestParams.setRequestParams(params);
            UserServiceHandler.forgotPassword(requestParams);

    }

    /**
     * remote response of change password
     * @param remoteResponse
     */
    private void forgotPasswordResponse(RemoteResponse remoteResponse) {
        context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        if (isAdded()) {
            String customErrorMsg = getString(R.string.forgot_failed);
            if (null == remoteResponse) {
                toastShort(getString(R.string.forgot_failed));
//                submitForgotPwd.setEnabled(true);
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        String response = remoteResponse.getResponse();
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("message")) {
                            String message = jsonObject.getString("message");
                            if (message.equalsIgnoreCase(BuddyConstants.VERIFY_OTP)) {
                                openOtpDialog();
                            } else {
                               alertAndAction(getActivity(), getString(R.string.reset_password_success_title),
                                        getString(R.string.forgot_success), getString(R.string.ok), null,
                                        new AlertAction() {
                                            @Override
                                            public void positiveAction() {
                                                CommonUtilities.logout();
                                            }

                                            @Override
                                            public void negativeAction() {

                                            }
                                        });
                            }
                        } else {
                            showAlertMsg(getString(R.string.info_label),getString(R.string.update_failed));
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        String message = jsonObject.getString("message");
                        if (message.equals(WRONG_OTP)) {
                            if (getWrongOtpCount(getActivity())) {
                                openOtpDialog();
                                toastLong(getServerMessageCode(message));
                            }//else is handled in method itself
                        }else{
                            showAlertMsg(getString(R.string.info_label),getServerMessageCode(message));
                        }
                    }

                } catch (JSONException e) {
                    toastShort(customErrorMsg);
                    e.getLocalizedMessage();
                }
            }
        }

    }

    /**
     * callback from dialogfragment
     * @param otpVal
     */
    @Override
    public void onFinishGettingOtp(String otpVal) {
        if(otpVal.length()==5){
            netOtpValue = otpVal;
            passwordLayout.setVisibility(View.VISIBLE);
            verifyUserBtn.setVisibility(View.GONE);
            resetPasswordGroup.setEnabled(false);
        }
    }
}
