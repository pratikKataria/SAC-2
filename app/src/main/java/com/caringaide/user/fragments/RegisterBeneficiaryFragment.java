package com.caringaide.user.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.caringaide.user.R;
import com.caringaide.user.activities.AddCardActivity;
import com.caringaide.user.activities.LaunchActivity;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.Services;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.caringaide.user.utils.BuddyConstants.ADDRESS_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.FULL_NAME_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.FULL_NAME_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.RELATION_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.RELATION_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.SERVICEABLE_AGE;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.checkStringData;
import static com.caringaide.user.utils.CommonUtilities.getCountryCodeFromMobile;
import static com.caringaide.user.utils.CommonUtilities.getDateInServerFormatString;
import static com.caringaide.user.utils.CommonUtilities.getServerMessageCode;
import static com.caringaide.user.utils.CommonUtilities.hideKeyPad;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.mobileMinValidation;
import static com.caringaide.user.utils.CommonUtilities.notifyWithVibration;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * Fragment for registering beneficiary.
 */
public class RegisterBeneficiaryFragment extends Fragment {

    private static final String TAG = "RegisterBeneficiary";
    private View regBenView;
    private EditText checkZipcode,benFullName,benEmail,
            benMobile,benAddress,benState,benZipcode,
    benRequirements,benDob,benRelationType;
    //,benCardNum,benCardMonth,benCardYear,benCardCvv
    private Button checkZipButton,submitBenRegButton;//selectBasicButton,selectPremiumButton,submitBenRegButton;
    private Spinner benTypeSpinner, benLanguageSpinner,serviceableCitySpinner;
    private TextView checkZipResponse;
    private RadioGroup benGenderRadio;
    private Spinner benCity,benCCode;
    //CheckBox basicCheckBox, premiumCheckBox;
    //CardView basicCardView,premiumCardView;
    private ArrayList<String> benTypeList = new ArrayList<>();
    private ArrayList<String> benLanguageList = new ArrayList<>();
    private Map<String,String> languageMap = new HashMap<>();
    private Map<String,String> benTypeMap = new HashMap<>();
    private Map<String, Services> serviceMap = new HashMap<>();
    private LinearLayout checkZipcodeLayout,benDetailsLayout;//,benCreditCardLayout,benPlanLayout;
    private String selectedLanguage = "";
    private String benGender="";
    private Zipcodes serviceableZipcodes;
//    ImageView cardViewer;
    private String serviceID = "1";
    private String CARDNO="";
    private String cardName="";
    private boolean isCardNum=false;
    private int expMonth;
    private int expYear;
    private boolean isCardExp=false;
    private TextInputLayout otherRelationLayout;
    private String selectedBenType;

    public RegisterBeneficiaryFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        regBenView =  inflater.inflate(R.layout.register_beneficiary_activity_layout, container, false);
        setUiComponents();
        return regBenView;
    }

    /**
     * initializes ui
     */
    private void setUiComponents() {
        otherRelationLayout = regBenView.findViewById(R.id.regben_relation_layout);
        benRequirements = regBenView.findViewById(R.id.regben_requirement);
        benZipcode = regBenView.findViewById(R.id.regben_zipcode);
        benState = regBenView.findViewById(R.id.regben_state);
        benCity = regBenView.findViewById(R.id.regben_city);
        benDob = regBenView.findViewById(R.id.regben_dob);
        benAddress = regBenView.findViewById(R.id.address_mobile);
        benMobile = regBenView.findViewById(R.id.regben_mobile);
        benCCode = regBenView.findViewById(R.id.regben_ccode);
        benEmail = regBenView.findViewById(R.id.regben_email);
        benFullName = regBenView.findViewById(R.id.regben_fullname);
        checkZipcodeLayout = regBenView.findViewById(R.id.check_zipcode_layout);
        benDetailsLayout = regBenView.findViewById(R.id.ben_details_layout);
//        benCreditCardLayout = regBenView.findViewById(R.id.regben_card_layout); not available
        //benPlanLayout = regBenView.findViewById(R.id.choose_plan_layout);
        submitBenRegButton = regBenView.findViewById(R.id.submit_register_ben);
//        cardViewer = regBenView.findViewById(R.id.image_card_view);
        ArrayAdapter<String> benTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, benTypeList);
        benTypeSpinner = regBenView.findViewById(R.id.select_regben_type);
        benTypeAdapter.notifyDataSetChanged();
        benTypeSpinner.setAdapter(benTypeAdapter);
        benTypeSpinner.setSelected(true);
        benTypeSpinner.setSelection(benTypeList.indexOf("Self"));
        ArrayAdapter<String> benLanguageAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, benLanguageList);
        benLanguageSpinner = regBenView.findViewById(R.id.regben_language);
        benLanguageAdapter.notifyDataSetChanged();
        benLanguageSpinner.setAdapter(benLanguageAdapter);
        benLanguageSpinner.setSelected(true);
        CommonUtilities.populateCountryCodeAdapter(getActivity(),benCCode);
        benGenderRadio = regBenView.findViewById(R.id.bengender_radio_group);
        checkZipButton = regBenView.findViewById(R.id.check_zipcode_button);
        checkZipResponse = regBenView.findViewById(R.id.info_servicablility);
        checkZipcode = regBenView.findViewById(R.id.regben_check_zipcode);
        benRelationType = regBenView.findViewById(R.id.regben_relationtype_txt);
        checkZipcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                benDetailsLayout.setVisibility(View.GONE);
                submitBenRegButton.setVisibility(View.GONE);
                checkZipResponse.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        checkZipButton.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                hideKeyPad();
                ArrayList<Zipcodes> zipcodes = SharedPrefsManager.getInstance().getZipcodeList(checkZipcode.getText().toString().trim());
                String zipcode = checkZipcode.getText().toString();
                ArrayList<String> cityListForZip = new ArrayList<String>();
                if (zipcodes.size()>0) {
                    for (Zipcodes zip : zipcodes) {
                        cityListForZip.add(zip.getCityName());
                    }
                    for (Zipcodes zip : zipcodes) {
                        if (zipcode.equals(zip.getZipcode())) {
                            serviceableZipcodes = zip;
                            //load available languages in that state
                            getAvailableLanguages(zip.getStateId());
                            checkZipResponse.setText(getString(R.string.servicable_area));
                            checkZipResponse.setTextColor(getResources().getColor(R.color.text_green));
                            benDetailsLayout.setVisibility(View.VISIBLE);
                            submitBenRegButton.setVisibility(View.VISIBLE);
//                        benCity.setText(zip.getCityName());//set adapter for spinner instead
//                        benCity.setEnabled(false);
                            benState.setText(zip.getStateName());
                            benState.setEnabled(false);
                            benZipcode.setText(zip.getZipcode());
                            benZipcode.setEnabled(false);

                        } else {
                            notifyNoServiceArea();
                        }
                    }
                    //populate city
                    ArrayAdapter<String> benCityAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cityListForZip);
                    benCity = regBenView.findViewById(R.id.regben_city);
                    benCityAdapter.notifyDataSetChanged();
                    benCity.setAdapter(benCityAdapter);
                    benCity.setSelected(true);
                }else{
                    notifyNoServiceArea();
                }
            }
        });
        benGenderRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton genderRadioButton = group.findViewById(checkedId);
                if(genderRadioButton.getText().toString().equalsIgnoreCase("male")){
                    benGender = "M";
                }else if (genderRadioButton.getText().toString().equalsIgnoreCase("female")){
                    benGender = "F";
                }else{
                    benGender = "O";
                }
            }
        });
        benTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RadioButton male = regBenView.findViewById(R.id.gender_radio_male);
                RadioButton female = regBenView.findViewById(R.id.gender_radio_female);
                RadioButton otherGender = regBenView.findViewById(R.id.gender_radio_others);
                benTypeSpinner.setSelection(position);
                otherRelationLayout.setVisibility(View.GONE);
                selectedBenType = parent.getItemAtPosition(position).toString();
                if (selectedBenType.equalsIgnoreCase(getString(R.string.others_label))){
                    otherRelationLayout.setVisibility(View.VISIBLE);
                }else if (selectedBenType.equalsIgnoreCase(getString(R.string.self))){
                    //populate all the fields with user details
                    benFullName.setText(SharedPrefsManager.getInstance().getUserFullName());
                    benEmail.setText(SharedPrefsManager.getInstance().getUserEmail());
                    benDob.setText(CommonUtilities.getDateAsString(SharedPrefsManager.getInstance().getUserDob()));
                    String[] mobileNumArr = getCountryCodeFromMobile(SharedPrefsManager.getInstance().getUserMobile());
                    //set  the given country code to the spinner
                    //To-do: populate country code from
                    //                    benCCode.setSelection();
//                    CommonUtilities.setValueToCountryCodeAdapter(getActivity(),benCCode,mobileNumArr[0]);
                    benMobile.setText(mobileNumArr[1]);
//                    benDob.setText(SharedPrefsManager.getInstance().getUserDob().substring(0,10));
                    String gender = SharedPrefsManager.getInstance().getUserGender();
                    if (gender.equalsIgnoreCase("M")){
                        male.setChecked(true);
                    }else if (gender.equalsIgnoreCase("F")){
                        female.setChecked(true);
                    }else{
                        otherGender.setChecked(true);
                    }
                }else {
                    benRelationType.getText().clear();
                    otherRelationLayout.setVisibility(View.GONE);
                }
                if (!parent.getItemAtPosition(position).toString().equalsIgnoreCase(getString(R.string.self))){
                    benFullName.setText("");
                    benEmail.setText("");
                    benDob.setText("");benMobile.setText("");
                    male.setChecked(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                alertAndAction(getActivity(), getString(R.string.select_relation),
                        getString(R.string.ok), null, new AlertAction() {
                            @Override
                            public void positiveAction() {

                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        });
        benLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: benLanguageSpinner "+position+" id "+id);
                benLanguageSpinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                alertAndAction(getActivity(), getString(R.string.select_language),
                        getString(R.string.ok), null, new AlertAction() {
                            @Override
                            public void positiveAction() {

                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        });
       /* selectBasicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Services service = SharedPrefsManager.getInstance().getService("Basic");
                if (null!=service)
                    serviceID = service.getId();
                basicCardView.setCardBackgroundColor(getResources().getColor(R.color.cadetblue));
                premiumCardView.setCardBackgroundColor(getResources().getColor(R.color.theme_gradient_green));
                premiumCheckBox.setChecked(false);
                basicCheckBox.setChecked(true);

            }
        });*/
       /* selectPremiumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Services service = SharedPrefsManager.getInstance().getService("Premium");
                if (null!=service)
                    serviceID = service.getId();
                basicCardView.setCardBackgroundColor(getResources().getColor(R.color.theme_gradient_blue));
                premiumCardView.setCardBackgroundColor(getResources().getColor(R.color.cadetblue));
                premiumCheckBox.setChecked(true);
                basicCheckBox.setChecked(false);
            }
        });*/
     /*   basicCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Services service = SharedPrefsManager.getInstance().getService("Basic");
                    if (null!=service)
                        serviceID = service.getId();
                    basicCardView.setCardBackgroundColor(getResources().getColor(R.color.cadetblue));
                    premiumCardView.setCardBackgroundColor(getResources().getColor(R.color.theme_gradient_green));
                    premiumCheckBox.setChecked(false);
                }
            }
        });
        premiumCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Services service = SharedPrefsManager.getInstance().getService("Premium");
                    if (null!=service)
                        serviceID = service.getId();
                    basicCardView.setCardBackgroundColor(getResources().getColor(R.color.theme_gradient_blue));
                    premiumCardView.setCardBackgroundColor(getResources().getColor(R.color.cadetblue));
                    basicCheckBox.setChecked(false);
                }
            }
        });*/
        benDob.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                final Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                // dobLayout.setError(null);
                benDob.setError(null);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        mcurrentDate.set(Calendar.YEAR, year);
                        mcurrentDate.set(Calendar.MONTH, monthOfYear);
                        mcurrentDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        String myFormat = getString(R.string.general_date_alone_format);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        benDob.setText(sdf.format(mcurrentDate.getTime()));

                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.setTitle(getResources().getString(R.string.select_time));
                datePickerDialog.show();
            }
        });
        submitBenRegButton.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                addBeneficiaryValidation();
                //addBeneficiary();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getBenTypes();
    }

    private void notifyNoServiceArea(){
        checkZipResponse.setText(getString(R.string.no_service_area));
        checkZipResponse.setTextColor(getResources().getColor(R.color.colorAccent));
        benDetailsLayout.setVisibility(View.GONE);
        submitBenRegButton.setVisibility(View.GONE);
    }
    /**
     * get ben relations
     */
    private void getBenTypes() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                handleBenTypesResponse(remoteresponse);
            }
        },getActivity(),null);
        UserServiceHandler.getBenTypes(requestParams);
    }

    /**
     * if zip code is serviceable, load the available languages in that state
     * @param stateId
     */
    private void getAvailableLanguages(String stateId) {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                handleLanguageResponse(remoteresponse);
            }
        },getActivity(),null);
        UserServiceHandler.getAvailableLanguages(stateId,requestParams);
    }

    private void handleLanguageResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            Context context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.update_failed);
            if (null == remoteResponse) {
                toastShort(context.getString(R.string.update_failed));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        benLanguageList.clear();
                        if (jsonObject.has("data")) {
                            JSONArray responseArray = jsonObject.getJSONArray("data");
                            if (null != responseArray) {
                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject responseObj = responseArray.getJSONObject(i);
                                    String language = responseObj.getJSONObject("language").getString("language");
                                    benLanguageList.add(language);
                                    benLanguageSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, benLanguageList));
                                    languageMap.put(responseObj.getJSONObject("language").getString("language"),
                                            responseObj.getJSONObject("language").getString("id"));
                                }
                            } else {

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
            }
        }
    }

    /**
     * remote response of getting ben types
     * @param remoteResponse
     */
    private void handleBenTypesResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.update_failed);
            if (null == remoteResponse) {
                toastShort(getString(R.string.update_failed));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        benTypeList.clear();
                        benTypeList.add(BuddyConstants.SELF);
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        if (jsonObject.has("data")) {
                            JSONArray responseArray = jsonObject.getJSONArray("data");
                            if (null != responseArray) {
                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject responseObj = responseArray.getJSONObject(i);
                                    String benType = responseObj.getString("type");
                                    if (!benType.equalsIgnoreCase(BuddyConstants.SELF)&&!benType.equalsIgnoreCase(BuddyConstants.OTHERS)) {
                                        benTypeList.add(benType); //benTypeList.add
                                    }
                                    benTypeSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, benTypeList));
                                    benTypeMap.put(benType, responseObj.getString("id"));
                                }
                                benTypeList.add(BuddyConstants.OTHERS);
                            } else {

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
            }
        }

    }

    /**
     * error validation function in add beneficiary
     */
    private void addBeneficiaryValidation() {
        Activity curActivity = null==getActivity()? BuddyApp.getCurrentActivity() : getActivity();
        String benRelation = benRelationType.getText().toString();
        String fullName  = benFullName.getText().toString();
        String email = benEmail.getText().toString();
        String ccode = benCCode.getSelectedItem().toString();
        String mob = benMobile.getText().toString();
        String address = benAddress.getText().toString();
        String mobile = ccode.concat(mob);
        String dob = benDob.getText().toString();
        if (otherRelationLayout.getVisibility() == View.VISIBLE &&
                (benRelation.length()<RELATION_MIN_LENGTH || benRelation.length()>RELATION_MAX_LENGTH)){
            benRelationType.setError(getString(R.string.ask_relation_length)
                    .concat(String.valueOf(RELATION_MIN_LENGTH)).concat(getString(R.string.and_label))
            .concat(String.valueOf(RELATION_MAX_LENGTH)));notifyWithVibration(curActivity);
            benRelationType.requestFocus();
        }else if (otherRelationLayout.getVisibility() == View.VISIBLE && (benRelation.isEmpty()||!benRelation.matches(PatternUtil.relationRegex))){
            benRelationType.setError(getString(R.string.ask_valid_relaation));notifyWithVibration(Objects.requireNonNull(curActivity));
        }else if (benFullName.getText().toString().isEmpty()){
            benFullName.setError(getString(R.string.ask_valid_fullname));
            notifyWithVibration(curActivity);
            benFullName.requestFocus();
        }else if (fullName.length()<FULL_NAME_MIN_LENGTH || fullName.length()>FULL_NAME_MAX_LENGTH){
            benFullName.setError(getString(R.string.ask_fullname_length)
                    .concat(String.valueOf(FULL_NAME_MIN_LENGTH)).concat(getString(R.string.and_label))
                    .concat(String.valueOf(FULL_NAME_MAX_LENGTH)));notifyWithVibration(curActivity);
            benFullName.requestFocus();
        }else if (!checkStringData(benFullName)){
//            benFullName.setError(getString(R.string.ask_valid_fullname));
            if(benFullName.getError().toString().isEmpty()){benFullName.setError(getString(R.string.ask_valid_fullname));}
            notifyWithVibration(curActivity);
            benFullName.requestFocus();
        }else if (!fullName.matches(PatternUtil.fullNameRegex)){
            benFullName.setError(getString(R.string.ask_valid_fullname));notifyWithVibration(curActivity);
            benFullName.requestFocus();
        }else if (benGender.isEmpty()){
            toastShort(getString(R.string.ask_gender));notifyWithVibration(curActivity);
        }else if (benMobile.getText().toString().isEmpty()){
            benMobile.setError(getString(R.string.ask_mobile));notifyWithVibration(curActivity);
            benMobile.requestFocus();
        }else if (benMobile.getText().toString().length()!= mobileMinValidation.get(ccode)){
            benMobile.setError(getString(R.string.ask_mobile));notifyWithVibration(curActivity);
            benMobile.requestFocus();
        }else if (!benMobile.getText().toString().trim().matches(PatternUtil.mobileRegex)){
            benMobile.setError(getString(R.string.ask_mobile));notifyWithVibration(curActivity);
            benMobile.requestFocus();
        }else if (ccode.isEmpty()||mob.isEmpty()){
            benMobile.setError(getString(R.string.ask_mobile));notifyWithVibration(curActivity);
            benMobile.requestFocus();
        }else if (email.isEmpty()){
            benEmail.setError(getString(R.string.ask_email));notifyWithVibration(curActivity);
            benEmail.requestFocus();
        } else if (!email.matches(PatternUtil.emailRegex)){
            benEmail.setError(getString(R.string.ask_email));notifyWithVibration(curActivity);
            benEmail.requestFocus();
        }else if (dob.isEmpty()){
            toastShort(getString(R.string.ask_dob));
            benDob.setError(getString(R.string.ask_dob));notifyWithVibration(curActivity);
            benDob.requestFocus();
        }else if (CommonUtilities.calculateAge(dob)<SERVICEABLE_AGE){
            String msg = getString(R.string.ask_valid_dob).concat(String.valueOf(SERVICEABLE_AGE))
                    .concat(getString(R.string.to_avail_service));
            toastShort(msg);
            benDob.setError(msg);notifyWithVibration(curActivity);
            benDob.requestFocus();
        }else if (address.isEmpty()||address.length()<ADDRESS_MIN_LENGTH){
            benAddress.setError(getString(R.string.ask_address));notifyWithVibration(curActivity);
            benAddress.requestFocus();
        }else if (!address.matches(PatternUtil.address_pattern)){
            benAddress.setError(getString(R.string.ask_address));notifyWithVibration(curActivity);
            benAddress.requestFocus();
        }else if (!benRequirements.getText().toString().isEmpty()&&!benRequirements.getText().toString().matches(PatternUtil.additionalInfoRegex)){
            benRequirements.requestFocus();
            benRequirements.setError(getString(R.string.ask_requirements));notifyWithVibration(curActivity);
        }else{
            alertAndAction(getActivity(),getString(R.string.confirm_adding_ben), getString(R.string.ask_register_beneficiary), getString(R.string.ok),
                    getString(R.string.cancel), new AlertAction() {
                        @Override
                        public void positiveAction() {
                            addBeneficiary();
                        }

                        @Override
                        public void negativeAction() {

                        }
                    });
        }
    }

    /**
     * remote call method to add beneficiary
     */
    private void addBeneficiary(){
        Log.d(TAG, "addBeneficiary date format : "+getDateInServerFormatString(benDob.getText().toString()));
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                addBeneficiaryResponse(remoteresponse);
            }
        },getActivity(),null);
        Map<String,String> params = new HashMap<>();
        params.put("type",benTypeMap.get(benTypeSpinner.getSelectedItem().toString()));
       // params.put("type",benTypeMap.get(benTypeSpinner.getSelectedItem().toString()));
        params.put("full_name",benFullName.getText().toString());
        if (selectedBenType.equalsIgnoreCase(getString(R.string.self))){
            params.put("nickname","Self");
        }else{
            params.put("nickname",benFullName.getText().toString());
        }
        params.put("mobile",benCCode.getSelectedItem().toString().trim().concat(benMobile.getText().toString().trim()));
        params.put("address",benAddress.getText().toString().trim());
        params.put("city_id", serviceableZipcodes.getCityId());
        params.put("state_id", serviceableZipcodes.getStateId());
        params.put("zipcode",benZipcode.getText().toString().trim());
        params.put("dob",benDob.getText().toString());
        params.put("gender",benGender);
        params.put("comments",benRequirements.getText().toString().isEmpty()?getString(R.string.no_data):benRequirements.getText().toString());
        params.put("active","1");
        params.put("user_id",SharedPrefsManager.getInstance().getUserID());
        params.put("card_type","credit");
        /*params.put("card_number",benCardNum.getText().toString());
        params.put("card_cvv",benCardCvv.getText().toString().trim());
        params.put("card_expiry",benCardMonth.getText().toString()+"/"+benCardYear.getText().toString());*/
        params.put("card_company",cardName);
        params.put("card_active","1");
        params.put("relation",benRelationType.getText().toString());
        params.put("languages_id",languageMap.get(benLanguageSpinner.getSelectedItem().toString()));
        //params.put("languages_id",languageMap.get(benLanguageSpinner.getSelectedItem().toString()));
        params.put("service_id",serviceID);
        requestParams.setRequestParams(params);
        UserServiceHandler.addBeneficiary(requestParams);
    }

    /**
     * remote response for adding beneficiary
     * @param remoteResponse
     */
    private void addBeneficiaryResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            Context context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.add_ben_failed);
            if (null == remoteResponse) {
                toastShort(context.getString(R.string.add_ben_failed));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        if (jsonObject.has("data")) {
                            Log.d(TAG, "addBeneficiaryResponse: " + jsonObject);
                            final int benId= jsonObject.getJSONObject("data").getInt("id");
                            Map<String, String> infoMap = new HashMap<>();
                            infoMap.put(BuddyConstants.UserInfo.USER_LOGIN_BEN, "1");
                            SharedPrefsManager.createSession(infoMap);
//                        toastLong(getString(R.string.add_ben_success));
                            alertAndAction(getActivity(), getString(R.string.add_ben_success), getString(R.string.client_added_success), getString(R.string.ok),
                                    getString(R.string.not_now), new AlertAction() {
                                        @Override
                                        public void positiveAction() {
                                            //go to add new card fragment.
                                            Intent intent = new Intent(getActivity(), AddCardActivity.class);
                                            intent.putExtra("ben_id",String.valueOf(benId));
                                            startActivity(intent);
//                                        Intent intent = new Intent(getActivity(), LaunchActivity.class);
//                                        startActivity(intent);
                                        }

                                        @Override
                                        public void negativeAction() {
                                            Intent intent = new Intent(getActivity(), LaunchActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                        }
                    } else {

                        JSONObject error = new JSONObject(remoteResponse.getResponse());
                        if (error.has("message")){
                            toastShort(getServerMessageCode(error.getString("message")));
                        }
//                        toastShort(getString(R.string.add_ben_failed));
                    }

                } catch (JSONException e) {
                    toastShort(customErrorMsg);
                    e.getLocalizedMessage();
                }
            }
        }
    }

    /**
     * open another fragment
     * @param fragment
     */
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.add_card_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
