package com.caringaide.user.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.BitmapCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.caringaide.user.R;
import com.caringaide.user.activities.UserHomeActivity;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.ImagePickerMarker;
import com.caringaide.user.utils.ImagePickerUtil;
import com.caringaide.user.utils.LoadImageUtils;
import com.caringaide.user.utils.PatternUtil;
import com.caringaide.user.utils.SharedPrefsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.caringaide.user.utils.BuddyConstants.FIRST_NAME_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.FIRST_NAME_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.LAST_NAME_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.LAST_NAME_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.OTP_RESEND_KEY;
import static com.caringaide.user.utils.BuddyConstants.OTP_RESEND_MOBILE;
import static com.caringaide.user.utils.BuddyConstants.OTP_RESEND_REASON;
import static com.caringaide.user.utils.BuddyConstants.PASSWORD_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.PASSWORD_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.USERNAME_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.USERNAME_MIN_LENGTH;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.checkStringData;
import static com.caringaide.user.utils.CommonUtilities.getCountryCodeFromMobile;
import static com.caringaide.user.utils.CommonUtilities.getServerMessageCode;
import static com.caringaide.user.utils.CommonUtilities.getWrongOtpCount;
import static com.caringaide.user.utils.CommonUtilities.hideKeyPad;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.isOpenOtpDialog;
import static com.caringaide.user.utils.CommonUtilities.mobileMinValidation;
import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;
import static com.caringaide.user.utils.CommonUtilities.notifyWithVibration;
import static com.caringaide.user.utils.CommonUtilities.populateCountryCodeAdapter;
import static com.caringaide.user.utils.CommonUtilities.resetWrongOtpCount;
import static com.caringaide.user.utils.CommonUtilities.setValueToCountryCodeAdapter;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.showProgressDialog;
import static com.caringaide.user.utils.CommonUtilities.toastLong;
import static com.caringaide.user.utils.CommonUtilities.toastShort;


public class SettingsFragment extends Fragment implements ImagePickerMarker,
        LoadImageUtils.ImageLoaderListener,OTPDialogFragment.DialogListener {
    private View settingsView;
    private TextView fullName,mobile,otpResponse;
    private EditText etNewPassword,etConfirmPassword,etOtp;
    private Button btnChangePass, cancelChangePass;
    private ImageButton changeImgBtn,passwordInfo,editProfileBtn;
    private ImageView profileImage;
    private EditText editFName,editLName,editEmail,editMobile,editUname;
    private Spinner editCcode;
//    android.support.v7.app.AlertDialog otpDialog;
    private String netOtpValue;
    private static String otpContext = "";//changePassword / editProfile
    private static final String TAG = "SettingsFragment";
    private TextView email;
    private ProgressDialog dialog;

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        settingsView = inflater.inflate(R.layout.fragment_settings, container, false);
//        setBackAction();
        setUiComponents();
        setUserData();
        return settingsView;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (isOpenOtpDialog()){
            openOtpDialog();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null!=dialog)
        dialog.dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resetWrongOtpCount();
    }

    /**
     * initilaizes ui
     */
    private void setUiComponents() {
        profileImage = settingsView.findViewById(R.id.settings_profileimg);
        changeImgBtn = settingsView.findViewById(R.id.settings_change_profileimg);
        passwordInfo = settingsView.findViewById(R.id.password_info);
        fullName = settingsView.findViewById(R.id.settings_fullname);
        mobile = settingsView.findViewById(R.id.settings_mobile);
        editProfileBtn = settingsView.findViewById(R.id.edit_user_profile_btn);
        email = settingsView.findViewById(R.id.settings_email);
        etNewPassword = settingsView.findViewById(R.id.etNewPassword);
        etConfirmPassword = settingsView.findViewById(R.id.etConfirmPassword);
        passwordInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertMsg(getString(R.string.info_password_title),getString(R.string.info_password));
            }
        });
        btnChangePass = settingsView.findViewById(R.id.submit_change_password);
        btnChangePass.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                getChangePasswordData(null);
            }
        });
        editProfileBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {showEditProfile();
            }
        });
    }

    /**
     * send reset password info to ui
     * @param otp
     */
    private void getChangePasswordData(final String otp) {
        final String newPassword = etNewPassword.getText().toString();
        final String confirmPassword = etConfirmPassword.getText().toString();
        if(checkPassword()) {
            RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                @Override
                public void onResponse(RemoteResponse remoteresponse) {
                    if (getActivity() !=null && isAdded())  {
                        changePasswordResponse(remoteresponse);
                    }else{
                        Log.e(TAG, "onResponse: ####BINDING ERROR####",null );
                    }
                }
            }, getActivity(), null);
            Map<String, String> params = new HashMap<>();
            params.put("password", newPassword);
            params.put("confirmPassword", confirmPassword);
            if (null != otp) {
                params.put("otp", otp);
            }
            requestParams.setRequestParams(params);
            UserServiceHandler.changePassword(requestParams);
        }
    }

    /**
     * set profile image
     */
    private void manageProfileImage() {
//        Bitmap profileImageData = CommonUtilities.getImageFromString(SharedPrefsManager.getInstance().getProfileImage(),
//                80,80);
        byte[] imageByteArray = Base64.decode(SharedPrefsManager.getInstance().getProfileImage(), Base64.DEFAULT);
        Glide.with(getActivity())
                .load(imageByteArray)
                .asBitmap()
                .placeholder(R.drawable.user_avatar)
                .fitCenter()
                .into(profileImage);

        changeImgBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ImagePickerUtil.getInstance(SettingsFragment.this).selectImage(BuddyConstants.PROFILE_TAKE_PHOTO,
                        BuddyConstants.PROFILE_CHOOSE_PHOTO);
            }
        });
    }

    /**
     * validation method to reset password
     * @return
     */
    private boolean checkPassword() {
        Activity curActivity = null!=getActivity()?getActivity(): BuddyApp.getCurrentActivity();
        boolean isValidPassword = false;
        if (etNewPassword.getText().toString().isEmpty()){
            etNewPassword.setError(getString(R.string.ask_password));notifyWithVibration(curActivity);
            etNewPassword.requestFocus();
        } else if (etNewPassword.getText().toString().length()<PASSWORD_MIN_LENGTH || etNewPassword.getText().toString().length()>PASSWORD_MAX_LENGTH){
            String passwordLengthErr = String.format(getString(R.string.ask_password_length),PASSWORD_MIN_LENGTH,PASSWORD_MAX_LENGTH);
            etNewPassword.setError(passwordLengthErr);
            notifyWithVibration(curActivity);
            etNewPassword.requestFocus();
        }else if (!etNewPassword.getText().toString().matches(PatternUtil.passwordRegex)){
            etNewPassword.setError(getString(R.string.ask_password_format));
            notifyWithVibration(curActivity);
            etNewPassword.requestFocus();
        }else if (etConfirmPassword.getText().toString().isEmpty()){
            etConfirmPassword.setError(getString(R.string.ask_password));
            notifyWithVibration(curActivity);
            etConfirmPassword.requestFocus();
        }else if (!etConfirmPassword.getText().toString().equals(etNewPassword.getText().toString())){
            etConfirmPassword.setError(getString(R.string.match_password));
            notifyWithVibration(curActivity);
            etConfirmPassword.requestFocus();
        }else{
            isValidPassword = true;
        }
        return isValidPassword;
    }

    /**
     * remote response of change password
     * @param remoteResponse
     */
    private void changePasswordResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            String customErrorMsg = getActivity().getString(R.string.cancel_request_error);
            if (null == remoteResponse) {
                toastShort(getActivity().getString(R.string.cancel_request_error));
            }
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {

                if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                    String response = remoteResponse.getResponse();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("message")) {
                        String message = jsonObject.getString("message");
                        if (message.equalsIgnoreCase("VERIFY_OTP")) {
                            otpContext = BuddyConstants.CHANGE_PASSWORD_CONTEXT;

                            openOtpDialog();
                        } else {
                            //if (jsonObject.has("data")){
//                        otpDialog.dismiss();
                            //JSONObject responseObject = jsonObject.getJSONObject("data");
                            toastShort(getString(R.string.update_successfull));
                            CommonUtilities.logout();
                            //}
                        }
                    } else {
                        toastShort(getActivity().getString(R.string.update_failed));
                    }
                } else {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase(BuddyConstants.WRONG_OTP)){

                        if (getWrongOtpCount(getActivity())) {
                            openOtpDialog();
                            toastLong(getServerMessageCode(message));
                        }//else is handled in method itself
                    }else{
                        toastShort(getServerMessageCode(message));
                    }
                   /* if (null != otpResponse) {
                        otpResponse.setText(message);
                    } else {
                        toastShort(getActivity().getString(R.string.update_failed).concat(message));
                    }*/
                }

            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }

    }

    /**
     * open otp dialog fragment
     */
    private void openOtpDialog() {
        OTPDialogFragment dialogFragment = new OTPDialogFragment();
        dialogFragment.setDialogListener(SettingsFragment.this);
        Bundle bundle = new Bundle();
        bundle.putBoolean("notAlertDialog", true);
        bundle.putString(OTP_RESEND_REASON, otpContext);
        bundle.putString(OTP_RESEND_KEY, SharedPrefsManager.getInstance().getUserName());
        bundle.putString(OTP_RESEND_MOBILE, SharedPrefsManager.getInstance().getUserMobile());
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
       /* if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft,"dialog");*/
    }

    /**
     * edit profile remote call
     * @param netOtpValue
     */
    private void editProfile(String netOtpValue) {
        // firtstname,lastname,email,otp,mobile- users/edit
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleEditProfile(remoteResponse);
            }
        },getActivity(),null);
        Map<String,String> params = new HashMap<>();
        params.put("first_name",editFName.getText().toString().trim());
        params.put("last_name",editLName.getText().toString().trim());
        params.put("username",editUname.getText().toString().trim());
        params.put("email",editEmail.getText().toString().trim());
        params.put("mobile",editCcode.getSelectedItem().toString().trim().concat(editMobile.getText().toString().trim()));
        if (null!=netOtpValue){
            params.put("otp", netOtpValue);
        }
        requestParams.setRequestParams(params);
        UserServiceHandler.editUserProfile(requestParams);
    }

    /**
     * remote response of edit profile
     * @param remoteResponse
     */
    private void handleEditProfile(RemoteResponse remoteResponse) {
        hideKeyPad();
        if (isAdded()) {
            Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.edit_user_failed);
            if (null == remoteResponse) {
                toastShort(customErrorMsg);
                return;
            }
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {

                if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                    String response = remoteResponse.getResponse();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("message")) {
                        String message = jsonObject.getString("message");
                        if (message.equalsIgnoreCase("VERIFY_OTP")) {
                            otpContext = BuddyConstants.EDIT_PROFILE_CONTEXT;
                            openOtpDialog();
                        } else {
                            alertAndAction(getActivity(),getString(R.string.user_profile_title), getString(R.string.already_logout),
                                    getString(R.string.ok), null, new AlertAction() {
                                        @Override
                                        public void positiveAction() {

                                            CommonUtilities.logout();
                                        }

                                        @Override
                                        public void negativeAction() {

                                        }
                                    });
                            showAlertMsg(getString(R.string.user_profile_title),
                                    getString(R.string.user_profile_edited_success));
                            //}
                        }
                    } else {
                        toastShort(context.getString(R.string.update_failed));
                    }
                } else {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    String message = jsonObject.getString("message");
                    if (message.equalsIgnoreCase(BuddyConstants.WRONG_OTP)){
                        if (getWrongOtpCount(getActivity())) {
                            toastShort(getServerMessageCode(message));
                            openOtpDialog();
                        }
                    }else{
                        toastShort(getServerMessageCode(message));
                    }
                   /* if (null != otpResponse) {
                        otpResponse.setText(getServerMessageCode(message));
                    } else {
                        toastShort(context.getString(R.string.edit_user_failed).concat(getServerMessageCode(message)));
                    }*/
                }

            } catch (JSONException e) {
                toastShort(customErrorMsg);
                e.getLocalizedMessage();
            }
        }

    }

    private void resendOtpResponse(RemoteResponse remoteresponse) {
        Log.d(TAG, "resendOtpResponse: "+remoteresponse.getResponse());
    }

    /**
     * set ui data values
     */
    private void setUserData() {
        fullName.setText(SharedPrefsManager.getInstance().getUserFullName());
        mobile.setText(SharedPrefsManager.getInstance().getUserMobile());
        email.setText(SharedPrefsManager.getInstance().getUserEmail());
        manageProfileImage();
    }

    private void setBackAction() {
        settingsView.setFocusableInTouchMode(true);
        settingsView.requestFocus();
        settingsView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {

                    moveToHomeActivity();
                    //changeFragment(new HomeFragment());
                    return true;
                }
                return false;
            }
        } );
    }
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    /**
     * load image of user
     * callback of image loader listener
     * @param image
     * @param imageCode
     */
    @Override
    public void imageLoaded(Bitmap image, String imageCode) {
        int bitmapByteCount= BitmapCompat.getAllocationByteCount(image);
        Log.d(TAG, "manageProfileImage: bitmap size###  "+bitmapByteCount);
        profileImage.setImageBitmap(image);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        getActivity();
        if (resultCode == Activity.RESULT_OK && data != null) {
            Bitmap imageBitmap = null;
            if (requestCode == BuddyConstants.PROFILE_TAKE_PHOTO) {
                imageBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                //set newly captured image to the field
            }
            if (requestCode == BuddyConstants.PROFILE_CHOOSE_PHOTO) {
                Uri selectedImageUri = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    //set selected image to the field
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            int bitmapByteCount= BitmapCompat.getAllocationByteCount(imageBitmap);
            Log.d(TAG, "getImageFromString: choosephoto bitmap size###  "+bitmapByteCount);
            /*ByteArrayOutputStream bos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            byte[] imageInBytes = bos.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageInBytes, 0, imageInBytes.length);
            int bitmapByteCount1= BitmapCompat.getAllocationByteCount(bitmap);
            Log.d(TAG, "getImageFromString: choose photo bitmap size###  "+bitmapByteCount1);*/
            if (bitmapByteCount > BuddyConstants.MAX_IMG_SIZE_BYTES){
                showAlertMsg(getString(R.string.error_label),getServerMessageCode(getString(R.string.too_large_image)
                        .concat(BuddyConstants.MAX_IMG_SIZE_MB).concat(getString(R.string.try_another_img))));
//                profileImage.setImageResource(R.drawable.user_avatar);
            } else{
                profileImage.setImageBitmap(imageBitmap);
                saveProfileImage(imageBitmap);
            }
        }
    }

    /**
     * remote call for updating profile image
     * @param imageBitmap
     */
    private void saveProfileImage(final Bitmap imageBitmap) {
        if (null!= imageBitmap){
            dialog = showProgressDialog(getActivity(),"Loading..");
            final String image = CommonUtilities.getStringFromImage(imageBitmap);
            SharedPrefsManager.getInstance().storeProfileImage(image);
            RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                @Override
                public void onResponse(RemoteResponse remoteResponse) {
                    handleProfileImage(remoteResponse,image);
                    dialog.dismiss();
                }
            },getActivity(),null);
            Map<String,String> params = new HashMap<>();
            params.put("user_id",SharedPrefsManager.getInstance().getUserID());
            params.put("class","profile");
            params.put("profile",image);
            params.put("mime_type","jpeg");
            requestParams.setRequestParams(params);
            UserServiceHandler.addProfileImage(requestParams);
//            Log.d(TAG, "saveProfileImage: "+image);
        }
    }

    /**
     * remote response for updating profile image
     * @param remoteResponse
     * @param image
     */
    private void handleProfileImage(RemoteResponse remoteResponse, String image) {
        if (isAdded()) {
            Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.end_list_error);
            if (null == remoteResponse) {
                toastShort(context.getString(R.string.end_list_error));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        Log.d(TAG, "handleProfileImage: success");
                        showAlertMsg(getString(R.string.user_profile_img_title), getString(R.string.user_profile_img_success));
                    } else {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        Log.d(TAG, "handleProfileImage: failed");
                        showAlertMsg(getString(R.string.info_label), getServerMessageCode(jsonObject.getString("message")));
                    }
                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
            }
        }
    }

    /**
     * validation msg for edit profile
     */
    private void showEditProfile(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(BuddyApp.getCurrentActivity());
        builder.setCustomTitle(View.inflate(BuddyApp.getCurrentActivity(),R.layout.custom_header_layout_white,null));
        builder.setView(R.layout.edit_user_profile_layout);
        final AlertDialog editProfileDialog = builder.create();
        editProfileDialog.setCancelable(false);
        editProfileDialog.show();
        editFName = editProfileDialog.findViewById(R.id.edit_profile_fname);
        editLName = editProfileDialog.findViewById(R.id.edit_profile_lname);
        editUname = editProfileDialog.findViewById(R.id.edit_profile_uname);
        editEmail = editProfileDialog.findViewById(R.id.edit_profile_email);
        editCcode = editProfileDialog.findViewById(R.id.edit_profile_ccode);
        editMobile = editProfileDialog.findViewById(R.id.edit_profile_mobile);
        editFName.setText(SharedPrefsManager.getInstance().getUserFName());
        editUname.setText(SharedPrefsManager.getInstance().getUserName());
        editLName.setText(SharedPrefsManager.getInstance().getUserLName());
        editEmail.setText(SharedPrefsManager.getInstance().getUserEmail());
        populateCountryCodeAdapter(getActivity(),editCcode);
        String[] mobNum = getCountryCodeFromMobile(SharedPrefsManager.getInstance().getUserMobile());
//        setValueToCountryCodeAdapter(getActivity(),editCcode,mobNum[0]);
        editMobile.setText(mobNum[1]);
        editLName.setText(SharedPrefsManager.getInstance().getUserLName());
        Button proceedReqBtn=editProfileDialog.findViewById(R.id.proceed_edit_profile);
        Button cancelReqBtn = editProfileDialog.findViewById(R.id.cancel_edit_profile);
        proceedReqBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Context curActivity = null==getActivity()? BuddyApp.getCurrentActivity() : getActivity();
                String mobNum = "";
                String firstName = editFName.getText().toString().trim();
                String lastName = editLName.getText().toString().trim();
                String userName = editUname.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
//                if (editCcode.getText().toString().isEmpty()){
//                    String ccode = "+1";
//                    mobNum = ccode.concat(editMobile.getText().toString().trim());
//                }else{
                mobNum = editCcode.getSelectedItem().toString().trim().concat(editMobile.getText().toString().trim());
//                }
                if (firstName.isEmpty()){
                    editFName.setError(getString(R.string.ask_fname));notifyWithVibration(curActivity);
                    editFName.requestFocus();
                }else if( firstName.length()<FIRST_NAME_MIN_LENGTH
                        || firstName.length()>FIRST_NAME_MAX_LENGTH){
                    editFName.setError(getString(R.string.ask_name_length).
                            concat(String.valueOf(FIRST_NAME_MIN_LENGTH)).concat(getString(R.string.and_label))
                    .concat(String.valueOf(FIRST_NAME_MAX_LENGTH)));
                    notifyWithVibration(curActivity);
                    editFName.requestFocus();
                }else if (!checkStringData(editFName)){
//                    editFName.setError(getString(R.string.ask_fname));
                    notifyWithVibration(curActivity);
                    editFName.requestFocus();
                }else if (!firstName.matches(PatternUtil.nameRegex)){
                    editFName.setError(getString(R.string.ask_fname));notifyWithVibration(curActivity);
                    editFName.requestFocus();
                } else if (lastName.isEmpty()){
                    editLName.setError(getString(R.string.ask_lname));notifyWithVibration(curActivity);
                    editLName.requestFocus();
                }else if(lastName.length()<LAST_NAME_MIN_LENGTH || lastName.length()>LAST_NAME_MAX_LENGTH){
                    editLName.setError(getString(R.string.ask_name_length).
                            concat(String.valueOf(LAST_NAME_MIN_LENGTH)).concat(getString(R.string.and_label))
                            .concat(String.valueOf(LAST_NAME_MAX_LENGTH)));notifyWithVibration(curActivity);
                    editLName.requestFocus();
                }else if (!checkStringData(editLName)){
//                    editLName.setError(getString(R.string.ask_lname));
                    notifyWithVibration(curActivity);
                    editLName.requestFocus();
                }else if (!lastName.matches(PatternUtil.nameRegex)){
                    editLName.setError(getString(R.string.ask_lname));
                    notifyWithVibration(curActivity);
                    editLName.requestFocus();
                } else if (userName.isEmpty()){
                    editUname.setError(getString(R.string.ask_username));notifyWithVibration(curActivity);
                    editUname.requestFocus();
                }else if(userName.length()<USERNAME_MIN_LENGTH || userName.length()>USERNAME_MAX_LENGTH){
                    editUname.setError(getString(R.string.info_username));
                    notifyWithVibration(curActivity);
                    editUname.requestFocus();
                }else if (!userName.matches(PatternUtil.userNameRegex)){
                    editUname.setError(getString(R.string.ask_username));
                    notifyWithVibration(curActivity);
                    editUname.requestFocus();
                }else if (email.isEmpty()||!email.matches(PatternUtil.emailRegex)) {
                    editEmail.setError(getString(R.string.ask_email));notifyWithVibration(curActivity);
                    editEmail.requestFocus();
                }else if (mobNum.isEmpty() || editMobile.getText().toString().trim().isEmpty()
                        || editMobile.getText().toString().length()!=
                        mobileMinValidation.get(editCcode.getSelectedItem().toString())){
                    editMobile.setError(getString(R.string.ask_mobile));notifyWithVibration(curActivity);
                    editMobile.requestFocus();
                }else if (!editMobile.getText().toString().matches(PatternUtil.mobileRegex)){
                    editMobile.setError(getString(R.string.ask_mobile));notifyWithVibration(curActivity);
                    editMobile.requestFocus();
                }else {
                    editProfile(null);
                    editProfileDialog.dismiss();
                }
            }
        });
        cancelReqBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                editProfileDialog.dismiss();
            }
        });


    }

    /**
     * callback method from otp dialog fragment
     * @param otpVal
     */
    @Override
    public void onFinishGettingOtp(String otpVal) {
        if (otpContext.equalsIgnoreCase(BuddyConstants.CHANGE_PASSWORD_CONTEXT)){
            getChangePasswordData(otpVal);
//            otpValue.setError(null);
        } else if (otpContext.equalsIgnoreCase(BuddyConstants.EDIT_PROFILE_CONTEXT)){
            editProfile(otpVal);
//            otpValue.setError(null);
        }
    }
}