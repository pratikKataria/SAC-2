package com.caringaide.user.fragments;


import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.caringaide.user.R;
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
import com.caringaide.user.utils.card.CardValidator;
import com.caringaide.user.utils.location.LocationGeocodeData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.caringaide.user.utils.BuddyConstants.CARD_MAX_VALIDITY_YEAR;
import static com.caringaide.user.utils.BuddyConstants.FULL_NAME_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.FULL_NAME_MIN_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.MONTH_MAX;
import static com.caringaide.user.utils.BuddyConstants.MONTH_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.MONTH_MIN;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.getServerMessageCode;
import static com.caringaide.user.utils.CommonUtilities.hideKeyPad;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;
import static com.caringaide.user.utils.CommonUtilities.notifyWithVibration;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.showProgressDialog;
import static com.caringaide.user.utils.CommonUtilities.toastLong;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} for adding new card for a beneficiary.
 */
public class AddCardFragment extends Fragment implements LocationAutoCompleteFragment.LocationFragmentListener {

    private View addCardView;
    private EditText billingApartment, billingAddressTxt, billingCountryTxt, billingStateTxt, billingCityTxt, billingZipcodeTxt,
            cardHolderNameTxt, cardNumberTxt, cardCvvTxt, cardExpMonthTxt, cardExpYearTxt;
    private Button submitCardButton;
    private String cardNum = "";
    private static String TAG = "AddCardFragment";
    private String cardName;
    private String cardToken = "";
    private boolean isCardNum = false;
    private boolean isCardExp;
    private int expMonth = 0;
    private String benID = "";
    private Map<String, String> cardTypeMap;
    private String cardExpMnthYear,cardType;
    private ProgressDialog progressDialog;

    public AddCardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addCardView =  inflater.inflate(R.layout.add_card_fragment_layout, container, false);
        Bundle bundle = getArguments();
        if (null != bundle){
            benID = bundle.getString("ben_id");
        }
        setUIComponents();
        return addCardView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null!=progressDialog)
        progressDialog.dismiss();
    }

    private void setUIComponents() {
        hideKeyPad();
        cardTypeMap = new HashMap<>();
        cardTypeMap.put("United States","USD");
        cardTypeMap.put("India","INR");
        billingApartment = addCardView.findViewById(R.id.add_card_apartment_details);
        billingAddressTxt = addCardView.findViewById(R.id.add_card_billing_address);
        billingCountryTxt = addCardView.findViewById(R.id.add_card_billing_country);
        billingStateTxt = addCardView.findViewById(R.id.add_card_billing_state);
        billingCityTxt = addCardView.findViewById(R.id.add_card_billing_city);
        billingZipcodeTxt = addCardView.findViewById(R.id.add_card_billing_zipcode);
        cardHolderNameTxt = addCardView.findViewById(R.id.add_new_card_holder_name);
        cardNumberTxt = addCardView.findViewById(R.id.add_card_number);
        cardCvvTxt = addCardView.findViewById(R.id.add_new_card_cvv);
        cardExpMonthTxt = addCardView.findViewById(R.id.add_new_cardexp_month);
        cardExpYearTxt = addCardView.findViewById(R.id.add_new_cardexp_year);
        submitCardButton = addCardView.findViewById(R.id.submit_new_card);
        submitCardButton.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                validateCardDetails();
            }
        });
        billingApartment.clearFocus();
        billingAddressTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LocationAutoCompleteFragment locationFragment = new LocationAutoCompleteFragment();
                locationFragment.setLocationListener(AddCardFragment.this);
                changeToLocFragment(locationFragment);
            }
        });
        cardNumberTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null != cardNumberTxt.getText() && cardNumberTxt.getText().length() >0) {
                    cardNum = cardNumberTxt.getText().toString();
                    //check if card is valid with card validator
                    CardValidator.CardDetails checkCard = CardValidator.checkCardValidity(getActivity(), cardNum);
                    Drawable img = ContextCompat.getDrawable(getActivity(),checkCard.getCardImageId());
                    cardNumberTxt.setCompoundDrawablesWithIntrinsicBounds( img, null,null, null);
                    boolean isValid = checkCard.isCardValid();
                    if (isValid) {
                        cardName = checkCard.getCardName();
                        Log.d(TAG, "card is valid " + cardName);
                        isCardNum =true;
                    }else{
                        String message = checkCard.getMessage();
                        cardNumberTxt.setError(message);
                        isCardNum = false;
                    }
                }else {
                    cardNumberTxt.setError(getString(R.string.ask_card_num));
                    cardNumberTxt.setCompoundDrawablesWithIntrinsicBounds( 0,0,R.drawable.card_error, 0);
                    isCardNum = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        cardExpMonthTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cardExpMonthTxt.getText().toString().length() > 0) {
                    expMonth = Integer.parseInt(cardExpMonthTxt.getText().toString());
                    if (expMonth < MONTH_MIN || expMonth > MONTH_MAX) {
                        cardExpMonthTxt.setError(getString(R.string.ask_expiry_month));
                        cardExpYearTxt.setText("");
                    }
                    if (cardExpMonthTxt.getText().toString().length() == MONTH_MAX_LENGTH) {
                        cardExpYearTxt.setEnabled(true);
                        cardExpYearTxt.requestFocus();
                    }
                }else{
                    isCardExp = false;
                    cardExpMonthTxt.setError(getString(R.string.ask_expiry_month));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        cardExpYearTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cardExpYearTxt.getText().toString().length()>0) {
                    Calendar cal = Calendar.getInstance(Locale.getDefault());
                    cal.setTimeInMillis(System.currentTimeMillis());
                    int year = cal.get(Calendar.YEAR);
                    int suppliedYear = Integer.parseInt(cardExpYearTxt.getText().toString());
                    if (suppliedYear > year + CARD_MAX_VALIDITY_YEAR) {
                        cardExpYearTxt.setError(getString(R.string.ask_expiry_year));
                        isCardExp = false;
                    } else if (suppliedYear < year) {
                        cardExpYearTxt.setError(getString(R.string.ask_expiry_year));
                        isCardExp = false;
                    } else if (suppliedYear == year && expMonth < cal.get(Calendar.MONTH)) {
                        cardExpYearTxt.setError(getString(R.string.ask_expiry_date));
                        isCardExp = false;
                    } else {
                        isCardExp = true;
                        cardCvvTxt.requestFocus();
                    }
                }else{
                    cardExpYearTxt.setError(getString(R.string.ask_expiry_date));
                    isCardExp = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * validate and add card
     */
    private void validateCardDetails(){
        if (!billingApartment.getText().toString().isEmpty() && !billingApartment.getText().toString().matches(PatternUtil.appartment_pattern)){
                billingApartment.setError(getString(R.string.ask_apartment));
                notifyWithVibration(getActivity());
                billingApartment.requestFocus();
        }else if (billingAddressTxt.getText().toString().isEmpty()){
            CommonUtilities.toastLong(getString(R.string.ask_address));
            notifyWithVibration(getActivity());
            billingAddressTxt.requestFocus();
        } else if (billingCountryTxt.getText().toString().isEmpty()){
            billingCountryTxt.setError(getString(R.string.ask_billing_country));
            notifyWithVibration(getActivity());
            billingCountryTxt.requestFocus();
        } else if (billingStateTxt.getText().toString().isEmpty()){
            billingStateTxt.setError(getString(R.string.ask_billing_state));
            notifyWithVibration(getActivity());
            billingStateTxt.requestFocus();
        } else if (billingCityTxt.getText().toString().isEmpty()){
            billingCityTxt.setError(getString(R.string.ask_billing_city));
            notifyWithVibration(getActivity());
            billingCityTxt.requestFocus();
        } else if (billingZipcodeTxt.getText().toString().isEmpty()){
            billingZipcodeTxt.setError(getString(R.string.ask_zip));
            notifyWithVibration(getActivity());
        } else if (!billingZipcodeTxt.getText().toString().trim().matches(PatternUtil.zipcode_pattern)){
            billingZipcodeTxt.setError(getString(R.string.ask_zip));
            notifyWithVibration(getActivity());
            billingZipcodeTxt.requestFocus();
        } else if (cardHolderNameTxt.getText().toString().isEmpty()){
            cardHolderNameTxt.setError(getString(R.string.ask_valid_fullname));
            notifyWithVibration(getActivity());
        }else if (cardHolderNameTxt.getText().toString().length()< FULL_NAME_MIN_LENGTH ||
                cardHolderNameTxt.getText().toString().length()>BuddyConstants.FULL_NAME_MAX_LENGTH ){
            cardHolderNameTxt.setError(getString(R.string.ask_fullname_length)+FULL_NAME_MIN_LENGTH+getString(R.string.and_label)+FULL_NAME_MAX_LENGTH);
            notifyWithVibration(getActivity());
            cardHolderNameTxt.requestFocus();
        } else if (!cardHolderNameTxt.getText().toString().trim().matches(PatternUtil.fullNameRegex)){
            cardHolderNameTxt.setError(getString(R.string.ask_valid_fullname));
            notifyWithVibration(getActivity());
            cardHolderNameTxt.requestFocus();
        }
        else if (cardNumberTxt.getText().toString().trim().isEmpty()){
            cardNumberTxt.setError(getString(R.string.ask_card_num));
            notifyWithVibration(getActivity());
            cardNumberTxt.requestFocus();
        }else if (cardExpMonthTxt.getText().toString().trim().isEmpty()){
            cardExpMonthTxt.setError(getString(R.string.ask_card_exp));
            notifyWithVibration(getActivity());
            cardExpMonthTxt.requestFocus();
        }else if (cardExpYearTxt.getText().toString().trim().isEmpty()){
            cardExpYearTxt.setError(getString(R.string.ask_card_exp));
            notifyWithVibration(getActivity());
            cardExpYearTxt.requestFocus();
        }else if (cardCvvTxt.getText().toString().trim().isEmpty()){
            cardCvvTxt.setError(getString(R.string.ask_card_cvv));
            notifyWithVibration(getActivity());
            cardCvvTxt.requestFocus();
        }else if (!cardCvvTxt.getText().toString().trim().matches(PatternUtil.cvvPattern)){
            cardCvvTxt.setError(getString(R.string.ask_card_cvv));
            notifyWithVibration(getActivity());
            cardCvvTxt.requestFocus();
        }else if (!isCardNum){
            cardNumberTxt.setError(getString(R.string.ask_card_num));
            notifyWithVibration(getActivity());
            cardNumberTxt.requestFocus();
        }else if (!isCardExp){
            cardCvvTxt.setError(getString(R.string.ask_card_cvv));
            notifyWithVibration(getActivity());
            cardCvvTxt.requestFocus();
        } else {
            cardExpMnthYear = cardExpMonthTxt.getText().toString().trim().concat(getString(R.string.slash))
                    .concat(cardExpYearTxt.getText().toString().trim());
            cardType = cardTypeMap.get(billingCountryTxt.getText().toString());
            if (null==cardType||cardType.isEmpty()){
                cardType = "USD";
            }
            alertAndAction(getActivity(), getString(R.string.card_details), getString(R.string.proceed_adding_card),
                    getString(R.string.proceed_label), getString(R.string.no), new AlertAction() {
                        @Override
                        public void positiveAction() {
                            addCardForClient();

                        }

                        @Override
                        public void negativeAction() {

                        }
                    });
        }
    }

    /**
     * remote call to get token
     */
    private void getToken(){
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            String customErrorMsg = getString(R.string.card_token_failed);
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                if(null == remoteResponse){
                    toastShort(customErrorMsg);
                }else{
                    String response = remoteResponse.getResponse();
                    Log.d(TAG,response);
                }
            }
        },getActivity(),null);

        String expMonth = cardExpMonthTxt.getText().toString().trim();
        String expYear = cardExpYearTxt.getText().toString().trim();
        String cardNum = cardNumberTxt.getText().toString().trim();
        String cardCVV = cardCvvTxt.getText().toString().trim();
        Map<String, String> params = new HashMap<>();
        params.put("card_cvv", cardCvvTxt.getText().toString().trim());
        params.put("card_expiry_mnth", expMonth);
        params.put("card_expiry_year", expYear);
        params.put("card_type",cardType);
        params.put("card_number",cardNum);
        params.put("billing_address", billingAddressTxt.getText().toString().trim());
        params.put("billing_city", billingCityTxt.getText().toString().trim());
        params.put("billing_state", billingStateTxt.getText().toString().trim());
        params.put("billing_country", billingCountryTxt.getText().toString().trim());
        params.put("billing_zipcode", billingZipcodeTxt.getText().toString().trim());
       // Map<String,String>reqHeaderMap = requestParams.getRequestHeaderMap();
        //byte[] creds = String.format("%s:", "pkapi_cert_NwMMOFx8YM9xhnS16Z").getBytes();
        //String auth = String.format("Basic %s", Base64.encodeToString(creds,Base64.URL_SAFE));
        //reqHeaderMap.put("Authorization",auth);
        requestParams.setRequestParams(params);
        UserServiceHandler.getToken(requestParams);
    }


    /**
     * remote call to add card
     */
    private void addCardForClient() {
        progressDialog = showProgressDialog(this.getActivity(),getString(R.string.please_wait));
        String billingAddress = billingApartment.getText().toString().trim().concat(" ")
                .concat(billingAddressTxt.getText().toString().trim());
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleAddCardResponse(remoteResponse);
            }
        },getActivity(),null);
        String expMonth = cardExpMonthTxt.getText().toString().trim();
        String expYear = cardExpYearTxt.getText().toString().trim();
        Map<String, String> params = new HashMap<>();
        params.put("card_cvv", cardCvvTxt.getText().toString().trim());
        params.put("card_expiry", cardExpMnthYear);
        params.put("card_expiry_mnth", expMonth);
        params.put("card_expiry_year", expYear);
        params.put("card_company",cardName);
        params.put("card_active","1");
        params.put("card_type",cardType);
        params.put("card_number",cardNum);
        params.put("user_id", SharedPrefsManager.getInstance().getUserID());
        params.put("beneficiary_id",benID);
        params.put("billing_address", billingAddress);
        params.put("billing_city", billingCityTxt.getText().toString().trim());
        params.put("billing_state", billingStateTxt.getText().toString().trim());
        params.put("billing_country", billingCountryTxt.getText().toString().trim());
        params.put("billing_zipcode", billingZipcodeTxt.getText().toString().trim());
        requestParams.setRequestParams(params);
        UserServiceHandler.addCardForClient(requestParams);
    }

    /**
     * remote response of addCardForClient method
     * @param remoteResponse
     */
    private void handleAddCardResponse(RemoteResponse remoteResponse) {
        progressDialog.dismiss();
        String customErrorMsg = getString(R.string.card_added_failed);
        if (null == remoteResponse) {
            toastShort(getString(R.string.card_added_failed));
        } else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    alertAndAction(getActivity(), getString(R.string.card_details), getString(R.string.card_added_success),
                            getString(R.string.ok), null, new AlertAction() {
                                @Override
                                public void positiveAction() {
                                    moveToHomeActivity();

                                }

                                @Override
                                public void negativeAction() {

                                }
                            });
                }else{
                    JSONObject errorObject = new JSONObject(remoteResponse.getResponse());
                    if (errorObject.has("message")) {
                        showAlertMsg(getString(R.string.add_card_title),
                                getServerMessageCode("ADD_CARD_FAILED"));
                    }
                }
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    /**
     * change fragment
     * @param fragment
     */
    private void changeToLocFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.addcard_location_frame_layout,fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    /**
     * callbak method of location listener
     * @param locationInfo
     */
    @Override
    public void onLocationSelection(LocationGeocodeData.LocationInfo locationInfo) {
        if (null!=locationInfo){
            billingAddressTxt.setText(locationInfo.getRawAddress());
            billingZipcodeTxt.setText(locationInfo.getPostalCode());
            billingCityTxt.setText(locationInfo.getCity());
            billingStateTxt.setText(locationInfo.getState());
            billingCountryTxt.setText(locationInfo.getCountry());
        }

    }
}
