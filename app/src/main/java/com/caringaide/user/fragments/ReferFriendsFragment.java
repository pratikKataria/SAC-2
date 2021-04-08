package com.caringaide.user.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.caringaide.user.utils.PatternUtil;
import com.caringaide.user.utils.SharedPrefsManager;
import com.caringaide.user.utils.permission.PermissionCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.BuddyConstants.FIRST_NAME_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.FIRST_NAME_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.LAST_NAME_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.LAST_NAME_MIN_LENGTH;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.checkStringData;
import static com.caringaide.user.utils.CommonUtilities.getCountryCodeFromMobile;
import static com.caringaide.user.utils.CommonUtilities.getServerMessageCode;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.mobileMinValidation;
import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;
import static com.caringaide.user.utils.CommonUtilities.notifyWithVibration;
import static com.caringaide.user.utils.CommonUtilities.setValueToCountryCodeAdapter;
import static com.caringaide.user.utils.CommonUtilities.toastLong;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReferFriendsFragment extends Fragment {

    private static final String TAG = "ReferFrndFragment";
    private static final int CONTACT_REQ = 1 ;
    private View referFrndView;
    private EditText firstName, lastName, email, mobile;
    private Spinner countrycode;
    private Button submitReferralBtn;
    private ImageButton contactBook;
    public ReferFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        referFrndView = inflater.inflate(R.layout.refer_friends_fragment_layout, container, false);
        setUiComponents();
        return referFrndView;
    }

    /**
     * initializes the ui components
     */
    private void setUiComponents() {
        firstName = referFrndView.findViewById(R.id.referral_buddy_first_name);
        lastName = referFrndView.findViewById(R.id.referral_buddy_last_name);
        email = referFrndView.findViewById(R.id.referral_buddy_email);
        countrycode = referFrndView.findViewById(R.id.referral_ccode);
        contactBook = referFrndView.findViewById(R.id.open_contactbook);
        CommonUtilities.populateCountryCodeAdapter(getActivity(),countrycode);
        mobile = referFrndView.findViewById(R.id.referral_mobile);
        submitReferralBtn = referFrndView.findViewById(R.id.submit_referral);
        submitReferralBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                validateAndReferFrnd();
            }
        });
        contactBook.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                readFromContacts();
            }
        });
    }

    /**
     * error validation of fields
     */
    private void validateAndReferFrnd() {
        Activity curActivity = null!=getActivity()?getActivity(): BuddyApp.getCurrentActivity();
        String fname = firstName.getText().toString().trim();
        String lname = lastName.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String mobNum = countrycode.getSelectedItem().toString().concat(mobile.getText().toString().trim());
        if (fname.isEmpty()){
            firstName.setError(getString(R.string.ask_fname));notifyWithVibration(curActivity);
            firstName.requestFocus();
        }else if(fname.length() < FIRST_NAME_MIN_LENGTH || fname.length() >FIRST_NAME_MAX_LENGTH) {
            firstName.setError(getString(R.string.ask_name_length).
                    concat(String.valueOf(FIRST_NAME_MIN_LENGTH)).concat(getString(R.string.and_label))
                    .concat(String.valueOf(FIRST_NAME_MAX_LENGTH)));
            ;
            notifyWithVibration(curActivity);
            firstName.requestFocus();
        }else if (!checkStringData(firstName)){
//            firstName.setError(getString(R.string.ask_fname));
            notifyWithVibration(curActivity);
            firstName.requestFocus();
        }else if (!fname.matches(PatternUtil.nameRegex)){
            firstName.setError(getString(R.string.ask_fname));notifyWithVibration(curActivity);
            firstName.requestFocus();
        }else if (lname.isEmpty()){
            lastName.setError(getString(R.string.ask_fname));notifyWithVibration(curActivity);
            lastName.requestFocus();
        }else if(lname.length() < LAST_NAME_MIN_LENGTH || lname.length() >LAST_NAME_MAX_LENGTH){
            lastName.setError(getString(R.string.ask_name_length).
                    concat(String.valueOf(LAST_NAME_MIN_LENGTH)).concat(getString(R.string.and_label))
                    .concat(String.valueOf(LAST_NAME_MAX_LENGTH)));
            notifyWithVibration(curActivity);
            lastName.requestFocus();
        }else if (!checkStringData(lastName)){
//            lastName.setError(getString(R.string.ask_lname));
            notifyWithVibration(curActivity);
            lastName.requestFocus();
        }else if (!lname.matches(PatternUtil.nameRegex)){
            lastName.setError(getString(R.string.ask_lname));notifyWithVibration(curActivity);
            lastName.requestFocus();
        }else if (countrycode.getSelectedItem().toString().trim().isEmpty()){
            mobile.setError(getString(R.string.ask_country_code));notifyWithVibration(curActivity);
            mobile.requestFocus();
        }else if (mobNum.isEmpty() || mobile.getText().toString().trim().isEmpty()){
            mobile.setError(getString(R.string.ask_mobile));notifyWithVibration(curActivity);
            mobile.requestFocus();
        }else if(mobile.getText().toString().trim().length()!=
                mobileMinValidation.get(countrycode.getSelectedItem().toString())){
            mobile.setError(getString(R.string.ask_mobile));notifyWithVibration(curActivity);
            mobile.requestFocus();
        }else if (!mobile.getText().toString().trim().matches(PatternUtil.mobileRegex)){
            mobile.setError(getString(R.string.ask_mobile));notifyWithVibration(curActivity);
            mobile.requestFocus();
        }else if (email.getText().toString().isEmpty()){
            email.setError(getString(R.string.ask_email));notifyWithVibration(curActivity);
            email.requestFocus();
        }else if (!mail.matches(PatternUtil.emailRegex)){
            email.setError(getString(R.string.ask_email));notifyWithVibration(curActivity);
            email.requestFocus();
        }else {
            RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                @Override
                public void onResponse(RemoteResponse remoteresponse) {
                    handleReferFriend(remoteresponse);
                }
            },getActivity(),null);
            Map<String, String> params = new HashMap<>();
            params.put("f_first_name",firstName.getText().toString().trim());
            params.put("f_last_name",lastName.getText().toString().trim());
            params.put("email",email.getText().toString().trim());
            params.put("mobile",countrycode.getSelectedItem().toString().trim().concat(mobile.getText().toString().trim()));

            if (!SharedPrefsManager.getInstance().getMktToken().isEmpty()){
                params.put("ref_token",SharedPrefsManager.getInstance().getMktToken());
                params.put("offer_name", BuddyConstants.REF_MKT_CLIENT);
            }else{
                params.put("offer_name", BuddyConstants.REF_CLIENT);
            }
            requestParams.setRequestParams(params);
            UserServiceHandler.addFriend(requestParams);
        }
    }

    /**
     * remote response for refer friend
     * @param remoteResponse
     */
    private void handleReferFriend(RemoteResponse remoteResponse) {
        if (isAdded()) {
            Context context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.refer_friend_failed);
            if (null == remoteResponse) {
                toastShort(customErrorMsg);
                return;
            }
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                    JSONObject responseObject = new JSONObject(remoteResponse.getResponse());
                    Log.d(TAG, "handleReferFriend: response: " + responseObject);
                    String message ="";

                    if (responseObject.has("message")) {
                        Log.d(TAG, "handleReferFriend: " + responseObject.getString("message"));
                        message = getString(R.string.refer_succesfull_message);

                    } if (responseObject.has("data")) {
                            String  refToken = responseObject.getJSONObject("data").getString("ref_token");
                            message = message.concat(refToken);
                    }
                    alertAndAction(getActivity(), getString(R.string.info_label),
                                getServerMessageCode(message),
                                getString(R.string.ok), null, new AlertAction() {
                                    @Override
                                    public void positiveAction() {
                                        moveToHomeActivity();
                                    }

                                    @Override
                                    public void negativeAction() {

                                    }
                                });
                } else {
                    JSONObject responseObject = new JSONObject(remoteResponse.getResponse());
                    if (responseObject.has("message")) {
                        if (responseObject.getString("message").equalsIgnoreCase("ADDITION_FAILED")) {

                            toastLong(getServerMessageCode(BuddyConstants.ADDITION_FAILED_MKT_CODE));
                        }
                        if (responseObject.getString("message").equalsIgnoreCase(BuddyConstants.NOT_AUTHORIZED)) {

                            toastLong(getServerMessageCode(BuddyConstants.NOT_AUTHORIZED_MKT_CODE));
                        } else {
                            toastLong(getServerMessageCode(responseObject.getString("message")));
                        }

                    }
                }
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    /**
     * open contacts for selecting
     */
    private void readFromContacts() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},
                    PermissionCodes.CONTACT_PERMISSION.getPermissionCode());
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, CONTACT_REQ);
            //check for onActivityResult()
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (CONTACT_REQ):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String num = "";
                        if (Integer.valueOf(hasNumber) == 1) {
                            Cursor numbers = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (numbers.moveToNext()) {
                                num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                if (num.contains("-")){
                                    num = num.replaceAll("-"," ");
                                }
                                String[] mobileNum = getCountryCodeFromMobile(num);
                                if (num.contains("+")){
                                    setValueToCountryCodeAdapter(getActivity(),countrycode,mobileNum[0]);
                                    String num1 = mobileNum[1].replaceAll("\\s", "");
                                    mobile.setText(num1);
                                }else{
                                    num = num.replaceAll("\\s", "");
                                    mobile.setText(num);
                                }
                            }
                        }
                    }
                    break;
                }
        }
    }
}
