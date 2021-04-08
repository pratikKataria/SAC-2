package com.caringaide.user.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import com.caringaide.user.activities.AddCardActivity;
import com.caringaide.user.activities.UserHomeActivity;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.BuddySkills;
import com.caringaide.user.model.ScheduledTime;
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
import com.caringaide.user.utils.card.CardValidator;
import com.caringaide.user.utils.location.LocationGeocodeData;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.caringaide.user.utils.BuddyConstants.serviceMaxTimeinHr;
import static com.caringaide.user.utils.BuddyConstants.serviceMinTimeinHr;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.defaultRide;
import static com.caringaide.user.utils.CommonUtilities.defaultSchdInterval;
import static com.caringaide.user.utils.CommonUtilities.getDateAndTimeIn24hrFormat;
import static com.caringaide.user.utils.CommonUtilities.getDateInServerFormatString;
import static com.caringaide.user.utils.CommonUtilities.getServerMessageCode;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.minServiceDuration;
import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;
import static com.caringaide.user.utils.CommonUtilities.notifyWithVibration;
import static com.caringaide.user.utils.CommonUtilities.selectedBenId;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.showProgressDialog;
import static com.caringaide.user.utils.CommonUtilities.timeZoneMap;
import static com.caringaide.user.utils.CommonUtilities.toastLong;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} for booking a service.
 */
public class BookNowFragment extends Fragment implements LocationAutoCompleteFragment.LocationFragmentListener,
        FavoriteBuddyListFragment.FavBuddyListener, TimePickerDialogFragment.TimePickerListener {

    private View bookView;
    private static final String TAG = "BookNowFrag";
    private Map<String,BuddySkills> buddySkillMap = new HashMap<>();
    private EditText bookingDate,address,requirements,apartmentDetails,bookingZipcode,bookingTime;//,bookingToDate;//,bookingTotime;
//    private Spinner hrSpinner,minSpinner,timeDurHrSpinner,meridianSpinner;
    private Spinner timeDurHrSpinner;
    private Button submitBookingBtn;
    private String latitude = "",longitude="",country = "";
    private StringBuffer checkBoxIdBuffer = new StringBuffer();
    private StringBuffer checkBoxNameBuffer = new StringBuffer();
    private TextView choosenBuddy;
    private Button infoChooseBuddy;
    private Button infoChooseSkills;
    private LinearLayout viewSelectedSkillLayout;
    private TextView selectedSkills,selectedSkillMoreInfo;
    private ImageButton removeSkill;
    private String buddyId = "";
    private RadioGroup viewCardRadioGroup;
    private RadioButton card1,card2;
    private ArrayList<ScheduledTime> scheduleList;
    private HashMap<String, ScheduledTime> scheduleMap;
    private String cardId1,cardId2;
    private Context context;
    private int cardArrayLength = 0;
    private ArrayList<String> durationList = new ArrayList<>();
    private static int startTimeHr = 7,startTimeMin = 0, endTimeHr = 21;
//    private String startHour="",startMin ="",startMeriadian="";
    private TextInputLayout bookFromLayout,addressLayout,apartmentLayout,zipcodeLayout, requrementlayout;
    private ProgressDialog progressDialog;
    private static JSONArray skillResponseArr;
    private boolean checkBookValAgain = true;
//    private int service_request_duration_min_interval;

    public BookNowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       bookView = inflater.inflate(R.layout.book_now_fragment_layout, container, false);
        scheduleList = new ArrayList<ScheduledTime>();
        scheduleMap = new HashMap<>();
        setUIComponents();
        return bookView;
    }

    @Override
    public void onStart() {
        checkBookValAgain = true;
        getBuddySkills();
        getCardsForBen();
        infoChooseBuddy.setVisibility(View.VISIBLE);
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null!=progressDialog)
        progressDialog.dismiss();
    }

    /**
     * initialize ui values
     */
    private void setUIComponents() {
        viewCardRadioGroup = bookView.findViewById(R.id.ben_view_card_radiogroup);
        bookingDate = bookView.findViewById(R.id.book_ben_date);
        bookingTime = bookView.findViewById(R.id.book_ben_time);
        choosenBuddy = bookView.findViewById(R.id.choosen_buddy);
        timeDurHrSpinner = bookView.findViewById(R.id.book_time_dur_hour);
        address = bookView.findViewById(R.id.book_ben_address);
        apartmentDetails = bookView.findViewById(R.id.book_ben_apartment);
        bookingZipcode = bookView.findViewById(R.id.book_ben_zipcode);
        bookingZipcode.setEnabled(false);
        requirements = bookView.findViewById(R.id.book_ben_requirements);
        submitBookingBtn = bookView.findViewById(R.id.submit_booking);
        //skill_details
        viewSelectedSkillLayout = bookView.findViewById(R.id.select_skill_details_layout);
        infoChooseSkills = bookView.findViewById(R.id.select_skill_btn);
        selectedSkills = bookView.findViewById(R.id.select_skill_result);
        selectedSkillMoreInfo = bookView.findViewById(R.id.select_skill_info);
        removeSkill = bookView.findViewById(R.id.remove_selected_skill_btn);

        bookFromLayout = bookView.findViewById(R.id.booking_from_layout);
        addressLayout = bookView.findViewById(R.id.book_ben_address_layout);
        apartmentLayout = bookView.findViewById(R.id.book_ben_apartment_layout);
        zipcodeLayout = bookView.findViewById(R.id.book_ben_zipcode_layout);
        bookFromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookFromLayout.setError(null);
            }
        });zipcodeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zipcodeLayout.setError(null);
            }
        });

        card1 = bookView.findViewById(R.id.rb_ben_card_1);
        card2 = bookView.findViewById(R.id.rb_ben_card_2);
        infoChooseBuddy = bookView.findViewById(R.id.info_choose_buddy_text);
        //view schedule id
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zipcodeLayout.setError(null);
                bookingZipcode.setText("");
                LocationAutoCompleteFragment locationFragment = new LocationAutoCompleteFragment();
                locationFragment.setLocationListener(BookNowFragment.this);
                changeToLocFragment(locationFragment);
            }
        });
        bookingDate.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                final Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                mcurrentDate.set(Calendar.YEAR, year);
                                mcurrentDate.set(Calendar.MONTH, monthOfYear);
                                mcurrentDate.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                                String myFormat = getString(R.string.general_date_alone_format);
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                bookingDate.setText(sdf.format(mcurrentDate.getTime()));
                                bookingTime.setEnabled(true);

                            }
                        }, mYear, mMonth, mDay);

                Calendar cal = Calendar.getInstance(Locale.getDefault());
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                if ((hour+minServiceDuration+(defaultSchdInterval/60))>=serviceMaxTimeinHr){
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()+(8*24*60*60*1000));
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()+(1*24*60*60*1000));
                }else{
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()+(7*24*60*60*1000));
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                }
                datePickerDialog.setTitle(getResources().getString(R.string.select_booking_fromdate));
                datePickerDialog.show();
            }
        });
        bookingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePickerDialog();
            }
        });
        infoChooseBuddy.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                alertAndAction(getActivity(), getString(R.string.choose_buddy_txt), getString(R.string.info_choose_buddy_premium),
                        getString(R.string.proceed_label), getString(R.string.no), new AlertAction() {
                            @Override
                            public void positiveAction() {
                                FavoriteBuddyListFragment favoriteBuddyListFragment = new FavoriteBuddyListFragment();
                                favoriteBuddyListFragment.setFavBuddyListener(BookNowFragment.this);
                                Bundle bundle = new Bundle();
                                bundle.putString("choose_context","bookNowFragment");
                                favoriteBuddyListFragment.setArguments(bundle);
                                changeToLocFragment(favoriteBuddyListFragment);
                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        });
        infoChooseSkills.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //To-do:
                openSkillsAlert();
            }
        });
        removeSkill.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String skillText = selectedSkills.getText().toString();
//                String trimedText = skillText.substring(skillText.lastIndexOf(",",skillText.length()-1));
//                String crText =
                int index= skillText.lastIndexOf(",");
                String removedText = skillText.substring(index+1, skillText.length());
                if (removedText.equalsIgnoreCase(getString(R.string.ride_label))) {
                    Log.d(TAG, "onClick: " + removedText);
                    selectedSkillMoreInfo.setText("");
                    selectedSkillMoreInfo.setVisibility(View.GONE);
                }
//                System.out.println(skillText.substring(0, index));
                if (index!=-1) {
                    selectedSkills.setText(skillText.substring(0, index));
                }else{
                    removeSkill.setVisibility(View.GONE);
                }
            }
        });
        submitBookingBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                bookNowValidation();
            }
        });
        choosenBuddy.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                alertAndAction(getActivity(), getString(R.string.info_label), getString(R.string.info_remove_buddy),
                        getString(R.string.ok), getString(R.string.cancel), new AlertAction() {
                            @Override
                            public void positiveAction() {
                                buddyId = "";
                                choosenBuddy.setText("");
                                choosenBuddy.setVisibility(View.GONE);
                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        });
    }

    private void openSkillsAlert() {
        checkBoxNameBuffer.setLength(0);
        selectedSkills.setText("");
        selectedSkillMoreInfo.setText("");
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BuddyApp.getCurrentActivity());
        builder.setCustomTitle(View.inflate(getActivity(), R.layout.custom_header_layout_white, null));
        builder.setView(View.inflate(getActivity(), R.layout.select_skill_linear_layout, null));
        //builder.setMessage(message);
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button submitSkillBtn = alertDialog.findViewById(R.id.add_select_skill_btn);
        submitSkillBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String skills = "";
                if (checkBoxNameBuffer.length() == 0) {
                    skills = "";
                    toastShort(getString(R.string.ask_select_skill));
                    viewSelectedSkillLayout.setVisibility(View.GONE);
                    selectedSkillMoreInfo.setVisibility(View.GONE);
                } else {
                    checkBoxNameBuffer.deleteCharAt(checkBoxNameBuffer.lastIndexOf(","));
                    skills = checkBoxNameBuffer.toString();
                    if (null != selectedSkills) {
                        selectedSkills.setText("");
                        selectedSkills.setText(skills);
                    }
                    viewSelectedSkillLayout.setVisibility(View.VISIBLE);
                    removeSkill.setVisibility(View.VISIBLE);
                    selectedSkillMoreInfo.setVisibility(View.VISIBLE);
                    alertDialog.cancel();
                }
            }
        });
//start creating checkbox layout
        LinearLayout linearLayout = alertDialog.findViewById(R.id.select_skill_layout);
        if ((linearLayout).getChildCount() > 0)
            (linearLayout).removeAllViews();
        try{
            if (null != skillResponseArr) {
                for (int i = 0; i < skillResponseArr.length(); i++) {
                    JSONObject responseObj = skillResponseArr.getJSONObject(i);
                    String id = responseObj.getString("id");
                    String skill = responseObj.getString("skill");
                    String description = responseObj.getString("description");
                    BuddySkills buddySkills = new BuddySkills();
                    buddySkills.setId(id);
                    buddySkills.setSkill(skill);
                    buddySkills.setDescription(description);
                    buddySkillMap.put(skill, buddySkills);


                    // Create Checkbox Dynamically
                    CheckBox checkBox = new CheckBox(getActivity());
                    checkBox.setText(skill);
                    checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (buttonView.getText().toString().equalsIgnoreCase("ride") && buttonView.isChecked()) {
                                String message = getString(R.string.info_ride_limit_miles) + defaultRide
                                        + getString(R.string.miles_label);
                                showAlertMsg(getString(R.string.info_label), message);
                                if (null!=selectedSkillMoreInfo){
                                    selectedSkillMoreInfo.setText(message);
                                }
                            }
                            BuddySkills buddySkills = buddySkillMap.get(buttonView.getText().toString());
                            if (isChecked) {
                                /*if (checkBoxIdBuffer.length() > 0) {
                                    if (checkBoxIdBuffer.charAt(checkBoxIdBuffer.length() - 1) != ',') {
                                        checkBoxIdBuffer.append(",");
                                    }
                                }
                                checkBoxIdBuffer.append(buddySkills.getId()).append(",");*/
                                //populate names
                                if (checkBoxNameBuffer.length() > 0) {
                                    if (checkBoxNameBuffer.charAt(checkBoxNameBuffer.length() - 1) != ',') {
                                        checkBoxNameBuffer.append(",");
                                    }
                                }
                                checkBoxNameBuffer.append(buddySkills.getSkill()).append(",");
                            } else {
                                if (checkBoxNameBuffer.length() > 0) {
                                    String skillNames = checkBoxNameBuffer.toString();
                                    if (skillNames.length()>0) {
                                        String[] skillNameArr = skillNames.split(",");
                                        checkBoxNameBuffer.setLength(0);
                                        for (int i = 0; i < skillNameArr.length; i++) {
                                            if (!skillNameArr[i].equals(buttonView.getText().toString())) {
                                                checkBoxNameBuffer.append(skillNameArr[i]).append(",");
                                            }
                                        }
                                    }
                                }
                                if (buttonView.getText().toString().equalsIgnoreCase("ride")
                                        && !buttonView.isChecked()) {
                                    selectedSkillMoreInfo.setText("");
                                }


                                }
                        }
                    });
                    if (linearLayout != null) {
                        linearLayout.addView(checkBox);
                    }
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }


//create checkbox layout ends
    }
    /**
     * get cards for beneficiary
     */
    private void getCardsForBen() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleCardForBen(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.getCardsForBen(selectedBenId, requestParams);
    }

    /**
     * remote response for get cards
     * @param remoteResponse
     */
    private void handleCardForBen(RemoteResponse remoteResponse) {
        context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        if (isAdded()) {
            String customErrorMsg = context.getString(R.string.get_card_error);
            if (null == remoteResponse) {
                toastShort(context.getString(R.string.get_card_error));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        viewCardRadioGroup.setVisibility(View.VISIBLE);
                        Log.d(TAG, "handleCardForBen: Response1 " + remoteResponse.getResponse());
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        if (jsonObject.has("data")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            cardArrayLength = jsonArray.length();
                            if (jsonArray.length() <= 2) {
                                card1.setVisibility(View.VISIBLE);
                                String cardNumber = jsonArray.getJSONObject(0).getString("card_number");
                                String cardCompany = jsonArray.getJSONObject(0).getString("card_company").toLowerCase();
                                cardId1 = jsonArray.getJSONObject(0).getString("id");
                                if (null!= CardValidator.cardImageMap) {
                                    Integer drawable = CardValidator.cardImageMap.get(cardCompany);
                                    if (null!=drawable) {
                                        card1.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
                                        card1.setText(cardNumber);
                                    }else {
                                        card1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                        card1.setText(cardCompany.concat(" - ")
                                                .concat(cardNumber));
                                    }
                                }else{
                                    card1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                    card1.setText(cardCompany.concat(" - ")
                                            .concat(cardNumber));
                                }
                                if (jsonArray.length() == 2) {
                                    card2.setVisibility(View.VISIBLE);
                                    String cardNumber2 = jsonArray.getJSONObject(1).getString("card_number");
                                    String cardCompany2 = jsonArray.getJSONObject(1).getString("card_company").toLowerCase();
                                    cardId2 = jsonArray.getJSONObject(1).getString("id");
                                    if (null!= CardValidator.cardImageMap) {
                                        Integer drawableImg = CardValidator.cardImageMap.get(cardCompany2);
                                        if (null!=drawableImg){
                                            card2.setText(cardNumber2);
                                            card2.setCompoundDrawablesWithIntrinsicBounds(drawableImg, 0, 0, 0);
                                        }else{
                                            card2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                            card2.setText(cardCompany2.concat(" - ")
                                                    .concat(cardNumber2));
                                        }
                                    }else{
                                        card2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                        card2.setText(cardCompany2.concat(" - ")
                                                .concat(cardNumber2));
                                    }
                                }
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
     * get buddy skills
     */
    private void getBuddySkills() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                handleBuddySkillsResponse(remoteresponse);
            }
        },getActivity(),null);
        UserServiceHandler.getBuddySkills(requestParams);
    }

    /**
     * remote response for buddy skills
     * @param remoteResponse
     */
    private void handleBuddySkillsResponse(RemoteResponse remoteResponse) {
        context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        if (isAdded()) {
            String customErrorMsg = context.getString(R.string.buddy_skill_failed);
            if (null == remoteResponse) {
                toastShort(context.getString(R.string.buddy_skill_failed));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        if (jsonObject.has("data")) {
                            //do all the processing inside the alrt
                            JSONArray responseArray = jsonObject.getJSONArray("data");
                            skillResponseArr = responseArray;
//                            openSkillsAlert(responseArray);
                        }
                    }
                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
            }
        }
    }


   /* private String getMinServiceDuration() {
        // DEFAULT_MIN_SERVICE_DURATION should be the config_key of minimum service duration config from the server
        Config dataConfig = SharedPrefsManager.getInstance().getConfigData(MIN_SERVICE_DURATION);
        return null!=dataConfig.getConfigValue()?dataConfig.getConfigValue():DEFAULT_MIN_SERVICE_DURATION;
    }*/

    /**
     * callback for location listener
     * @param locationInfo
     */
    @Override
    public void onLocationSelection(LocationGeocodeData.LocationInfo locationInfo) {
        if (null!=locationInfo) {
            address.setText(locationInfo.getRawAddress());
            address.setError(null);
            latitude = String.valueOf(locationInfo.getLatLng().latitude);
            longitude = String.valueOf(locationInfo.getLatLng().longitude);
            String zipcode = locationInfo.getPostalCode();
            country = locationInfo.getCountry();
            if (null == zipcode || zipcode.isEmpty()){
                bookingZipcode.setEnabled(true);
            }else {
                bookingZipcode.setEnabled(false);
                bookingZipcode.setText(zipcode);
            }
            bookingZipcode.setVisibility(View.VISIBLE);
            apartmentDetails.setVisibility(View.VISIBLE);
        }
    }


    private void populateDurationArray(){
        getDurationArray();
        ArrayAdapter<String> hourDurationAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item,durationList);
        hourDurationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeDurHrSpinner.setAdapter(hourDurationAdapter);
        timeDurHrSpinner.setSelected(true);

    }
    private  void getDurationArray(){
        int schdIntervalTime = defaultSchdInterval/60;
        durationList.clear();
        int count=0;
        if (startTimeMin==0){
            endTimeHr = 21;
        }else{
            endTimeHr =20;
        }
        for (int i = startTimeHr; i < endTimeHr; i++) {
            if (startTimeHr<serviceMinTimeinHr){
                break;
            }
            count++;
            if (count>=minServiceDuration) {
                durationList.add(String.valueOf(count));
            }

        }
        if (durationList.isEmpty()){
            bookingTime.setText("");
            String minDurationMsg   = getString(R.string.min_duration_label)+" "+minServiceDuration+
                    getString(R.string.hour_label)+getString(R.string.dot_label);
            String maxDurMessage = getString(R.string.err_duration_btw0)
                    .concat(String.valueOf(serviceMinTimeinHr)).concat(getString(R.string.am_label))
                    .concat(getString(R.string.to)).concat(String.valueOf((serviceMaxTimeinHr)%12))
                    .concat(getString(R.string.err_max_duration1)).concat(minDurationMsg);
            showAlertMsg(getString(R.string.info_label),maxDurMessage);

        }
    }
    /**
     * change fragment
     * @param fragment
     */
    private void changeToLocFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.location_frame_layout,fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    /**
     * function to book buddy visit
     */
    private void bookNow(){
        //calculate service duration in mins
        String durationInHrs = String.valueOf(timeDurHrSpinner.getSelectedItem());
        int timeDurationInMins = Integer.parseInt(durationInHrs) * 60;
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleBookNowResponse(remoteResponse);
            }
        },getActivity(),null);
        Map<String,String> params = new HashMap();
        params.put("booking_date",getBookingDateTime());
        params.put("exp_end_date",getEndDateTime());
        params.put("message",requirements.getText().toString().trim());
        params.put("service_id", CommonUtilities.selectedserviceId);
        params.put("beneficiary_id",CommonUtilities.selectedBenId);
        params.put("expected_service_time",String.valueOf(timeDurationInMins));
        params.put("latitude",latitude);
        params.put("longitude",longitude);
        params.put("client_location",getAddress());
        params.put("client_zipcode",bookingZipcode.getText().toString());
        params.put("skills",getSkillString());
        params.put("buddy_id",buddyId);
        params.put("timezone",timeZoneMap.get(country));
        params.put("card_id",getCardId());
        requestParams.setRequestParams(params);
        UserServiceHandler.bookService(requestParams);
        progressDialog = showProgressDialog(getActivity(),getString(R.string.please_wait));
    }

    /**
     * getget booking date and time
     * @return string in date and time format
     */
    private String getBookingDateTime(){
        String givenDate = getDateInServerFormatString(bookingDate.getText().toString().trim());
        String givenTime = bookingTime.getText().toString().trim();
        String bookingDateTime =  givenDate+" "+givenTime;
        return getDateAndTimeIn24hrFormat(bookingDateTime.trim());
    }

    /**
     * get end date and time
     * @return string of date and time
     */
    private String getEndDateTime(){
        String endTime ="";
        if (null!= timeDurHrSpinner.getSelectedItem()) {
            String bookingDate = getBookingDateTime().trim();
            String durationInHrs = String.valueOf(timeDurHrSpinner.getSelectedItem());
            endTime = CommonUtilities.getDayAfterGivenHour(bookingDate, Integer.valueOf(durationInHrs)).trim();
        }
        return endTime;
    }

    /**
     * get skills
     * @return string of skills
     */
    private String getSkillString(){
        checkBoxIdBuffer.setLength(0);
        String skillsString = selectedSkills.getText().toString();
        if (skillsString.lastIndexOf(",")!=-1) {
            String[] allSelectedSkills = skillsString.split(",");
            for (int i=0;i<allSelectedSkills.length;i++){
                BuddySkills buddySkills = buddySkillMap.get(allSelectedSkills[i]);
                checkBoxIdBuffer.append(buddySkills.getId()).append(",");
            }
            checkBoxIdBuffer.deleteCharAt(checkBoxIdBuffer.lastIndexOf(","));
        }else{
            BuddySkills buddySkills = buddySkillMap.get(skillsString);
            checkBoxIdBuffer.append(buddySkills.getId());
        }
       /* if (checkBoxIdBuffer.length() > 0 && checkBoxIdBuffer.charAt(checkBoxIdBuffer.length()-1) == ',') {
            checkBoxIdBuffer.deleteCharAt(checkBoxIdBuffer.lastIndexOf(","));
        }*/
        return checkBoxIdBuffer.toString();
    }

    /**
     * get address
     * @return string of address
     */
    private String getAddress(){
        if (apartmentDetails.getText().toString().isEmpty()){
            return address.getText().toString();
        }else {
            return apartmentDetails.getText().toString().concat(" , ").concat(address.getText().toString());
        }
    }

    /**
     * get card id
     * @return string of card id
     */
    private String getCardId(){
        String cardId = "";
        if (card1.isChecked()) {
            cardId =  cardId1;
        }else if (card2.isChecked()) {
            cardId =  cardId2;
        }
        return cardId;
    }

    /**
     * validation before booking
     */
    private void bookNowValidation(){
        Date startDate = null; long startTimeMillis = 0;
        Activity curActivity = null!=getActivity()?getActivity(): BuddyApp.getCurrentActivity();
        String addressVal = address.getText().toString();
        String zipcode = bookingZipcode.getText().toString();
        String newZip = "";
        ArrayList<Zipcodes> zipcodes = SharedPrefsManager.getInstance().getZipcodeList(zipcode);
        for (Zipcodes zip:zipcodes){
            newZip = zip.getZipcode();//bcoz same zipcode can have multiple cities
        }
        long currentTimeMillis = System.currentTimeMillis();
        String startDateStr = (null==bookingDate.getText()||bookingDate.getText().toString().isEmpty()) ? "" : bookingDate.getText().toString().trim();
        String startDateTime = getBookingDateTime();
        String endDateTime = getEndDateTime();
        int expEndHrs = endDateTime.isEmpty()? 0:Integer.valueOf(endDateTime.substring(11,13));
        String givenMin = String.valueOf(startTimeMin);
        long timeDiffMillisec = Long.valueOf(defaultSchdInterval)* 60 * 1000;
        String BOOKING_DURATION_FROM_NOW = defaultSchdInterval+getString(R.string.min_label);
        if (startDateStr.isEmpty()) {
            toastLong(getString(R.string.ask_date));
            return;
        }else if (startDateTime.isEmpty()){
            toastLong(getString(R.string.ask_date_time));
            return;
        }else{
            startTimeMillis =  getDateTimeInMillis(startDateTime);
        }
        String maxDurMessage = getString(R.string.err_max_duration0)
                .concat(String.valueOf(serviceMaxTimeinHr%12)).concat(getString(R.string.err_max_duration1));
        if (startDateStr.isEmpty()){
            toastLong(getString(R.string.ask_date));notifyWithVibration(curActivity);
            bookingDate.requestFocus();
        }else if(startTimeMillis < currentTimeMillis+timeDiffMillisec){
            toastLong(getString(R.string.ask_proper_time).concat(BOOKING_DURATION_FROM_NOW)
            .concat(getString(R.string.ahead_of_time)));notifyWithVibration(curActivity);
        }else if(startTimeHr == expEndHrs){
            toastLong(getString(R.string.ask_time_duration));
        }else if(expEndHrs==0){
            toastShort(maxDurMessage);
            bookingDate.requestFocus();
        }else if(expEndHrs > serviceMaxTimeinHr){
            int durationDiff = expEndHrs - serviceMaxTimeinHr ;
            toastShort(maxDurMessage.concat(" "+durationDiff).concat(getString(R.string.err_max_duration2))
                    .concat(getString(R.string.hour_label)).concat(" "+givenMin)
                    .concat(getString(R.string.min_label)).concat(getString(R.string.dot_label)));notifyWithVibration(curActivity);
            bookingDate.requestFocus();
        }else if(expEndHrs == serviceMaxTimeinHr && Integer.parseInt(givenMin) != 0){
            toastShort(maxDurMessage.concat(getString(R.string.err_max_duration2))
                    .concat(" "+givenMin)
                    .concat(getString(R.string.min_label)).concat(getString(R.string.dot_label)));notifyWithVibration(curActivity);
        }else if (addressVal.isEmpty()){
            address.setError(getString(R.string.ask_address));notifyWithVibration(curActivity);
            address.requestFocus();
        }else if(null==addressVal||addressVal.equalsIgnoreCase("undefined")){
            address.setError(getString(R.string.ask_address));notifyWithVibration(curActivity);
            address.requestFocus();
        }else if(latitude.isEmpty()||longitude.isEmpty()){
            address.setError(getString(R.string.ask_address));notifyWithVibration(curActivity);
            address.requestFocus();
        }else if(zipcode.isEmpty()){
            zipcodeLayout.setError(getString(R.string.enter_zipcode));notifyWithVibration(curActivity);
            bookingZipcode.requestFocus();
        }else if (!zipcode.equals(newZip)){
            zipcodeLayout.setError(getString(R.string.no_service_area));notifyWithVibration(curActivity);
            bookingZipcode.requestFocus();
        }else if (!zipcode.matches(PatternUtil.zipcode_pattern)){
            zipcodeLayout.setError(getString(R.string.ask_zip));notifyWithVibration(curActivity);
            bookingZipcode.requestFocus();
        }else if(selectedSkills.getText().toString().trim().isEmpty()){
            toastShort(getString(R.string.ask_select_skill));notifyWithVibration(curActivity);
        }else {
            checkBookValAgain = false;
            alertAndAction(getActivity(), getString(R.string.confirm_booking_title),
                    getString(R.string.ask_confirm_booking), getString(R.string.ok),
                    getString(R.string.cancel), new AlertAction() {
                @Override
                public void positiveAction() {
                    if (!checkBookValAgain) {
                        bookNow();
                    }else{
                        bookNowValidation();
                    }
                }

                @Override
                public void negativeAction() {

                }
            });
        }
    }

    private long getDateTimeInMillis(String startDateTime) {
        Date startDate = null;
        try {
            startDate = new SimpleDateFormat(getString(R.string.general_book_date_time_format),Locale.getDefault())
                        .parse(startDateTime);

        } catch (ParseException e) {
            Log.e(TAG, "bookNowValidation: check the format of booking date and time ",e.getCause() );
        }
        if (null!= startDate) {
            return startDate.getTime();
        }
        return 0;
    }

    /**
     * remote response for booking service
     * @param remoteResponse
     */
    private void handleBookNowResponse(RemoteResponse remoteResponse) {
        progressDialog.dismiss();
        context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        if (isAdded()) {
            String customErrorMsg = context.getString(R.string.booking_failed);
            if (null == remoteResponse) {
                toastShort(context.getString(R.string.booking_failed));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
//                    toastShort("Booked Successfully");
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        if (jsonObject.has("message")) {
                            if (jsonObject.getString("message").equalsIgnoreCase(BuddyConstants.ADDED_SUCCESSFULLY)) {
                                    alertAndAction(getActivity(), getString(R.string.booking_done_title),
                                        getServerMessageCode(BuddyConstants.BOOKING_DONE), getString(R.string.ok),null,
                                        new AlertAction() {
                                            @Override
                                            public void positiveAction() {
                                                moveToHomeActivity();
                                            }

                                            @Override
                                            public void negativeAction() {

                                            }
                                        });
                            } else {
                                String msg = jsonObject.getString("message");
                                toastShort(getServerMessageCode(msg));
                            }
                        }
                    } else {

                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        if (jsonObject.has("message")) {
                            String msg = "";
                            if (jsonObject.getString("message").equalsIgnoreCase("CARD_NOT_AUTHORIZED")){
                                if (cardArrayLength<2){
                                    msg = getServerMessageCode(getString(R.string.book_with_new_card));
                                    addCard();
                                }else{
                                    msg = getServerMessageCode(getString(R.string.book_with_another_card));
                                }
                            }else{
                                msg = getServerMessageCode(jsonObject.getString("message"));
                            }
                            toastLong(msg);
                        }
                    }

                } catch (JSONException e) {
                    toastShort(customErrorMsg);
                    e.getLocalizedMessage();
                }
            }
        }
    }

    private void addCard() {
        Intent intent = new Intent(getActivity(), AddCardActivity.class);
        intent.putExtra("ben_id",CommonUtilities.selectedBenId);
        startActivity(intent);
        /*AddCardFragment cardFragment = new AddCardFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ben_id",CommonUtilities.selectedBenId);
        cardFragment.setArguments(bundle);
        changeToLocFragment(cardFragment);*/
    }
   /* private void createDefaultTimings() {
        for(ScheduledTime.DaysOfWeek day: ScheduledTime.DaysOfWeek.values()){
            ScheduledTime scehduleData = new ScheduledTime(day.name());
            scheduleList.add(scehduleData);
            scheduleMap.put(day.name(),scehduleData);
        }
    }*/

    /**
     * callback method of FavBuddy Listener
     * this is invoked after a buddy is chosed from favorite lsit
     * @param buddyId id of the selected buddy, to be sent to the server along with the booking details
     * @param buddyName name of the selected buddy, to be displayed to the user
     */
    @Override
    public void onFavBuddy(String buddyId, String buddyName) {
        this.buddyId = buddyId;
        choosenBuddy.setText(buddyName);
        choosenBuddy.setVisibility(View.VISIBLE);
    }
    private void openTimePickerDialog(){
        TimePickerDialogFragment dialogFragment = new TimePickerDialogFragment();
        dialogFragment.setTimePickerListener(BookNowFragment.this);
        Bundle bundle = new Bundle();
        bundle.putBoolean("notAlertDialog", true);
        dialogFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft,"dialog");
    }

    @Override
    public void onTimePickerSelected(String hr,String min,String meridian) {
        int iHr = Integer.valueOf(hr);
        switch (meridian){
            case "AM":
                if (iHr==12){
                    startTimeHr = iHr+12;
                }else{
                    startTimeHr = iHr;
                }
                break;
            case "PM":
                if (iHr==12){
                    startTimeHr = iHr;
                }else{
                    startTimeHr = iHr+12;
                }
                break;
            default:
                startTimeHr = iHr;
                break;
        }
        startTimeMin = Integer.valueOf(min);
        if (min.equals("0")){
            min = "00";
        }
        String startTime = hr+":"+min+" "+meridian;
        bookingTime.setText(startTime);
        populateDurationArray();
    }
}
