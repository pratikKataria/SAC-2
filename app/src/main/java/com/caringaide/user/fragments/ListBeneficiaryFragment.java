package com.caringaide.user.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.activities.RegisterBeneficiaryActivity;
import com.caringaide.user.adapters.ListBeneficiaryAdapter;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.ChangeFragmentListener;
import com.caringaide.user.interfaces.RefreshListAdapterListener;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.Beneficiary;
import com.caringaide.user.model.Cards;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.SharedPrefsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass for viewing list of the beneficiaries
 * RefreshListAdapterListener is used for updating view after a change in the adapter view classes.
 * ChangeFragmentListener is used for moving to another fragment from the click events in the adapter.
 */
public class ListBeneficiaryFragment extends Fragment implements RefreshListAdapterListener , ChangeFragmentListener {


    private View bookNowView;
    List<Beneficiary> beneficiaryDetailsList = new ArrayList<>();
    private ListBeneficiaryAdapter listBeneficiaryAdapter;
    private TextView noDataTextView;
    private ListView beneficiaryListView;
    private Map<String,String> benTypeMap = new HashMap();
    private Button registerBeneficiary;
    private Context context;


    public ListBeneficiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        getBenTypes();
//        listBeneficiaries();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bookNowView = inflater.inflate(R.layout.list_booking_layout, container, false);
        beneficiaryListView = bookNowView.findViewById(R.id.list_beneficiary_lv);
        noDataTextView = bookNowView.findViewById(R.id.no_data_tv_list_booking);
        registerBeneficiary = bookNowView.findViewById(R.id.register_client_btn);
        registerBeneficiary.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent addBenIntent = new Intent(getActivity(), RegisterBeneficiaryActivity.class);
                addBenIntent.putExtra("clickContext","listBen");
                startActivity(addBenIntent);
            }
        });
        return bookNowView;
    }

    /**
     * get beneficiary list
     */
    private void listBeneficiaries(){
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                listBeneficiariesResponse(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.listBeneficiaries(requestParams);
    }

    /**
     * get beneficiary remote response
     * @param remoteResponse
     */
    private void listBeneficiariesResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.list_ben_failed);
            if (null == remoteResponse) {
                toastShort(getString(R.string.list_ben_failed));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        beneficiaryDetailsList.clear();
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        if (jsonObject.has("data")) {
                            JSONArray responseArray = jsonObject.getJSONArray("data");
                            if (null != responseArray) {
                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject responseObj = responseArray.getJSONObject(i);
                                    Beneficiary beneficiary = new Beneficiary();
                                    String benId = responseObj.getString("id");
                                    beneficiary.setBenId(benId);
                                    String benType = responseObj.getString("type");
                                    beneficiary.setBenType(benType);
                                    String benFullName = responseObj.getString("full_name");
                                    beneficiary.setBenFullName(benFullName);
                                    String benRelation = responseObj.getString("relation");
                                    beneficiary.setBenRelation(benRelation);
                                    String benNickname = responseObj.getString("nickname");
                                    beneficiary.setBenNickName(benNickname);
                                    String benMobile = responseObj.getString("mobile");
                                    beneficiary.setBenMobile(benMobile);
                                    String benAddress = responseObj.getString("address");
                                    beneficiary.setBenAddress(benAddress);
                                    String cityId = responseObj.getString("city_id");
                                    beneficiary.setBenCityId(cityId);
                                    String benStateId = responseObj.getString("state_id");
                                    beneficiary.setBenStateId(benStateId);
                                    String benZipcode = responseObj.getString("zipcode");
                                    beneficiary.setBenZipcode(benZipcode);
                                    String benDob = responseObj.getString("dob").substring(0, 10);
                                    beneficiary.setBenDob(benDob);
                                    String benGender = responseObj.getString("gender");
                                    beneficiary.setBenGender(benGender);
                                    String benComments = responseObj.getString("comments");
                                    beneficiary.setBenComments(benComments);
                                    String benActive = responseObj.getString("active");
                                    beneficiary.setBenActive(benActive);
                                    String benServiceId = responseObj.getString("service_id");
                                    beneficiary.setBenServiceId(benServiceId);
                                    String benUserId = responseObj.getString("user_id");
                                    beneficiary.setBenUserId(benUserId);
                                    if (responseObj.has("cards")) {
                                        JSONArray cardsArray = responseObj.getJSONArray("cards");
                                        if (cardsArray.length() != 0) {
                                            beneficiary.setHaveCard(true);
                                            ArrayList<Cards> list = new ArrayList<>();
                                            for (int j = 0; j < cardsArray.length(); j++) {
                                                Cards card = new Cards();
                                                card.setCardId(cardsArray.getJSONObject(j).getString("id"));
                                                card.setCardExpiry(cardsArray.getJSONObject(j).getString("card_expiry"));
                                                card.setCardNumber(cardsArray.getJSONObject(j).getString("card_number"));
                                                card.setCardType(cardsArray.getJSONObject(j).getString("id"));
                                                card.setCardCompany(cardsArray.getJSONObject(j).getString("card_company"));
                                                list.add(card);
                                            }
                                            beneficiary.setCardsList(list);
                                        }
                                    }
//                                String benLanguageId = responseObj.getJSONArray("beneficiary_languages").getJSONObject(0).getString("id");
//                                beneficiary.setBenLanguageId(benLanguageId);
//                                String languageId = responseObj.getJSONArray("beneficiary_languages").getJSONObject(0).getString("languages_id");
//                                beneficiary.setLanguageId(languageId);
//                                String language = responseObj.getJSONArray("beneficiary_languages").getJSONObject(0).getJSONObject("language").getString("language");
//                                beneficiary.setBenLanguage(language);
                                    String benServiceType = responseObj.getJSONObject("service").getString("type");
                                    beneficiary.setBenService(benServiceType);
                                    String serviceDescription = responseObj.getJSONObject("service").getString("description");
                                    beneficiary.setBenServiceDescription(serviceDescription);
                                    beneficiaryDetailsList.add(beneficiary);
                                    listBeneficiaryAdapter = new ListBeneficiaryAdapter(getActivity(), R.layout.list_beneficiary_layout, beneficiaryDetailsList
                                            , benTypeMap, beneficiaryListView, this, this, this);
                                    beneficiaryListView.setAdapter(listBeneficiaryAdapter);

                                }
                            } else {
                                beneficiaryListView.setVisibility(View.GONE);
                                noDataTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        beneficiaryListView.setVisibility(View.GONE);
                        noDataTextView.setVisibility(View.VISIBLE);
                        if (beneficiaryDetailsList.size()==0) {
                            Map<String, String> infoMap = new HashMap<>();
                            infoMap.put(BuddyConstants.UserInfo.USER_LOGIN_BEN, "0");
                            SharedPrefsManager.createSession(infoMap);
                        }
                    }

                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
            }
        }

    }

    /**
     * get beneficiary types
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

    private void handleBenTypesResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.update_failed);
            if (null == remoteResponse) {
                toastShort(getString(R.string.update_failed));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        if (jsonObject.has("data")) {
                            JSONArray responseArray = jsonObject.getJSONArray("data");
                            if (null != responseArray) {
                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject responseObj = responseArray.getJSONObject(i);
                                    String benType = responseObj.getString("type");
                                    benTypeMap.put(responseObj.getString("id"), benType);
                                }
                                listBeneficiaries();
                            } else {
                                toastShort(customErrorMsg);
                            }
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        if (jsonObject.has("message")) {
                            toastShort(jsonObject.getString("message"));
                        }
                    }

                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
            }
        }
    }

    /**
     * callback method of refresh data listener from adapter
     */
    @Override
    public void onDataRefresh() {
        listBeneficiaries();
    }

    /**
     * callback method of change fragment listener from adapter
     * @param fragment
     */
    @Override
    public void changeToTargetFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fav_buddy_frame_layout,fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.attach(fragment);
//        this.getFragmentManager().popBackStackImmediate();
        transaction.commit();
    }

}
