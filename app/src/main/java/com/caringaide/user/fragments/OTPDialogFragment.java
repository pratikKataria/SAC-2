package com.caringaide.user.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.caringaide.user.R;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.SharedPrefsManager;

import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.BuddyConstants.OTP_RESEND_KEY;
import static com.caringaide.user.utils.BuddyConstants.OTP_RESEND_MOBILE;
import static com.caringaide.user.utils.BuddyConstants.OTP_RESEND_REASON;
import static com.caringaide.user.utils.CommonUtilities.isResendOtpEnabled;

/**
 * A simple {@link DialogFragment} subclass that is used as otp dialog
 */
public class OTPDialogFragment extends DialogFragment {

    private static final String TAG ="OtpDialog";
    private View  otpDialogView;
    private static String netOtpValue;
    private DialogListener dialogListener;
    private String resendOtpReason,resendOtpKey;
    private EditText otpValue,otpValue1,otpValue2,otpValue3,otpValue4;
    private String userUname,userMobile;

    public OTPDialogFragment() {
        // Required empty public constructor
    }
    /**
     * Listener for passing otp value to the caller
     */
    public interface DialogListener{
        void onFinishGettingOtp(String otpVal);
    }

    /**
     * this will initialize the listener in the caller
     * calls before the fragment calls otpdialogfrag
     * @param dialogListener
     */
    public void setDialogListener(DialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        otpDialogView = inflater.inflate(R.layout.otp_layout,container,false);
        return otpDialogView;
    }

    /**
     * traditional alert dialog
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String dialogTitle ="",dialogMessage = "";
        if (getArguments() != null) {
            if (getArguments().getBoolean("notAlertDialog")) {
                return super.onCreateDialog(savedInstanceState);
            }else{
                dialogTitle = getArguments().getString("dialog_title");
                dialogMessage = getArguments().getString("dialog_content");
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogTitle);
        builder.setMessage(dialogMessage);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogListener.onFinishGettingOtp("set");
                dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }

    /**
     * custom alert dialog
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CommonUtilities.setOpenOtpDialog(true);
        final TextView resendOtp = view.findViewById(R.id.btn_resend_otp);
        resendOtp.setEnabled(isResendOtpEnabled);
        otpValue = view.findViewById(R.id.etOtp0);
        otpValue1 = view.findViewById(R.id.etOtp1);
        otpValue2 = view.findViewById(R.id.etOtp2);
        otpValue3 = view.findViewById(R.id.etOtp3);
        otpValue4 = view.findViewById(R.id.etOtp4);
        otpValue1.setEnabled(false);
        otpValue2.setEnabled(false);
        otpValue3.setEnabled(false);
        otpValue4.setEnabled(false);
        setOtpBackAction();
        otpValue.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        CommonUtilities.showKeyPad();

        if (getArguments() != null) {
            if (null!=getArguments().getString(OTP_RESEND_REASON)) {
                resendOtpReason = getArguments().getString(OTP_RESEND_REASON);
                resendOtpKey = getArguments().getString(OTP_RESEND_KEY);
                userMobile = getArguments().getString(OTP_RESEND_MOBILE);
                if (getArguments().getString(OTP_RESEND_REASON).equalsIgnoreCase("forgot")) {
                    resendOtp.setVisibility(View.GONE);
                }
/*
                if (resendOtpReason.equalsIgnoreCase(BuddyConstants.SIGNUP_USER_CONTEXT)) {
                    userUname = getArguments().getString("username");
                    userMobile = getArguments().getString("mobile");
                }*/
            }
        }
        final TextView otpResponse = view.findViewById(R.id.otp_response);
        resendOtp.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                    @Override
                    public void onResponse(RemoteResponse remoteresponse) {
                        Log.d(TAG, "resend otp onResponse: "+remoteresponse.getResponse());
                        isResendOtpEnabled = false;
                        resendOtp.setEnabled(isResendOtpEnabled);
                    }

                }, getActivity(),null);
                Map<String, String> params = new HashMap<>();
                /*if (resendOtpReason.equalsIgnoreCase(BuddyConstants.SIGNUP_USER_CONTEXT)){
                    params.put("username", userUname);
                    params.put("mobile",userMobile);
                }else {
                    params.put("username", SharedPrefsManager.getInstance().getUserName());
                    params.put("mobile", SharedPrefsManager.getInstance().getUserMobile());
                }*/
                params.put(OTP_RESEND_REASON,resendOtpReason);
                params.put(BuddyConstants.OTP_RESEND_KEY,resendOtpKey);
                params.put(BuddyConstants.OTP_RESEND_MOBILE,userMobile);
                requestParams.setRequestParams(params);
                UserServiceHandler.resendOtp(requestParams);
            }
        });

        Button proceedReqBtn= view.findViewById(R.id.send_otp);
        Button cancelReqBtn = view.findViewById(R.id.cancel_otp);
        cancelReqBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                CommonUtilities.setOpenOtpDialog(false);
                CommonUtilities.hideKeyPad();
                dismiss();
            }
        });
        proceedReqBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                netOtpValue = otpValue.getText().toString() + otpValue1.getText().toString() +
                        otpValue2.getText().toString() + otpValue3.getText().toString() + otpValue4.getText().toString();
                if (netOtpValue.length() == 5) {
                    CommonUtilities.setOpenOtpDialog(false);
                    CommonUtilities.hideKeyPad();
                    otpValue.setError(null);
                    dialogListener.onFinishGettingOtp(netOtpValue);
                    dismiss();
                } else {
                    otpValue.setError(getString(R.string.otp_error));
                }
            }
        });
        otpValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==1){
                    otpResponse.setText("");
                    otpValue1.setEnabled(true);
                    otpValue1.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpValue1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==1){
                    otpValue2.setEnabled(true);
                    otpValue2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpValue2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count==1){
                    otpValue3.setEnabled(true);
                    otpValue3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpValue3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count==1){
                    otpValue4.setEnabled(true);
                    otpValue4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otpValue4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count==1){
                    //otpValue4.requestFocus();
                    otpValue4.clearFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * back action click of otp edit textss
     */
    private void setOtpBackAction() {

        otpValue.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    otpValue.requestFocus();
                }
                return false;
            }
        });
        otpValue1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    otpValue1.setText("");

                    otpValue.setEnabled(true);
                    otpValue.requestFocus();
                    otpValue.setText("");
                }
                return false;
            }
        });
        otpValue2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace

                    otpValue1.setEnabled(true);
                    otpValue1.requestFocus();
                    otpValue1.setText("");
                }
                return false;
            }
        });
        otpValue3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    otpValue2.setEnabled(true);
                    otpValue2.requestFocus();
                    otpValue2.setText("");
                }
                return false;
            }
        });
        otpValue4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    otpValue4.setText("");

                    otpValue3.setEnabled(true);
                    otpValue3.requestFocus();
                    otpValue3.setText("");

                }
                return false;
            }
        });
    }
}
