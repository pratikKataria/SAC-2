package com.caringaide.user.fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.adapters.BuddyTransitAdapter;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackBuddyFragment extends Fragment {

    View trackView;
    TextView noData;
    ArrayList<BuddyService> buddyServiceList;
    ListView trackBuddyListView;
    BuddyTransitAdapter buddyTransitAdapter;
    private static String TAG = "TrackBuddyFragment";
    public TrackBuddyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        trackView = inflater.inflate(R.layout.track_buddy_layout, container, false);
        buddyServiceList = new ArrayList<>();
        setView();
        return trackView;
    }

    private void setView() {
        trackBuddyListView = trackView.findViewById(R.id.track_buddy_list);
        buddyTransitAdapter = new BuddyTransitAdapter(getActivity(),R.layout.transit_service_request_layout,buddyServiceList,this);
        trackBuddyListView.setAdapter(buddyTransitAdapter);
        noData = trackView.findViewById(R.id.no_data_tv_transit);
        trackBuddyListView.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadTransitBookingsData();
    }

    /**
     * load bookings in stransit and start
     */
    private void loadTransitBookingsData() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                transitListResponse(remoteResponse);
            }
        },getContext(),null);
        Map<String,String> params = new HashMap<>();
        params.put("statuses",getString(R.string.transit_label)+","+getString(R.string.data_start));
        requestParams.setRequestParams(params);
        // infoStartLayout.setAnimation(rightAnimation);
        //            infoStartLayout.setVisibility(View.VISIBLE);
        UserServiceHandler.getTrackingRequestStatus(requestParams);
    }

    /**
     * remote response of transit bookings
     * @param remoteResponse
     */
    private void transitListResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.transit_list_error);
            if (null == remoteResponse) {
                toastShort(customErrorMsg);
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);

                try {
                    buddyServiceList.clear();
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        Log.d(TAG, "transitListResponse: " + jsonObject);
                        if (jsonObject.has("data")) {
                            JSONArray responseArray = jsonObject.getJSONArray("data");
                            if (null != responseArray) {
                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject respObj = responseArray.getJSONObject(i);
                                    BuddyService buddyService = new BuddyService();
                                    buddyService.setId(respObj.getString("id"));
                                    buddyService.setDisplayId(respObj.getString("booking_id"));
                                    buddyService.setBeneficiaryId(respObj.getString("beneficiary_id"));
                                    buddyService.setBuddyId(respObj.getString("buddy_id"));
                                    buddyService.setUserId(respObj.getString("user_id"));
                                    buddyService.setStatus(respObj.getString("status"));
                                    if (respObj.has("buddy_rated"))
                                        buddyService.setRated(respObj.getBoolean("buddy_rated"));
                                    buddyService.setBookingData(respObj.getString("booking_date"));
                                    if (!respObj.isNull("exp_end_date")) {
                                        buddyService.setEndDate(respObj.getString("exp_end_date"));
                                    }
                                    buddyService.setStartDate(respObj.getString("start_date"));
                                    buddyService.setMessage(respObj.getString("message"));
                                    buddyService.setLatitude(respObj.getString("latitude"));
                                    buddyService.setLongitude(respObj.getString("longitude"));
                                    buddyService.setLocation(respObj.getString("client_location"));
                                    buddyService.setZipcode(respObj.getString("client_zipcode"));
                                    buddyService.setExpectedServicetime(respObj.getString("expected_service_time"));
                                    buddyService.setExtnDuration(respObj.getString("extn_hrs"));
                                    buddyService.setBenDob(respObj.getJSONObject("beneficiary").getString("dob"));
                                    buddyService.setGender(respObj.getJSONObject("beneficiary").getString("gender"));
                                    buddyService.setBenFullName(respObj.getJSONObject("beneficiary").getString("full_name"));
                                    buddyService.setBenNickName(respObj.getJSONObject("beneficiary").getString("nickname"));
                                    buddyService.setBenComments(respObj.getJSONObject("beneficiary").getString("comments"));
                                    buddyService.setBenMobile(respObj.getJSONObject("beneficiary").getString("mobile"));
                                    if (respObj.has("buddy")) {
                                        buddyService.setBuddyName(respObj.getJSONObject("buddy").getString("full_name"));
                                        buddyService.setBuddyMobile(respObj.getJSONObject("buddy").getString("mobile"));
                                        buddyService.setBuddyEmail(respObj.getJSONObject("buddy").getString("email"));
//                                    buddyService.setBuddyLat(respObj.getJSONObject("buddy").
//                                            getJSONObject("user_location").getString("latitude"));
//                                    buddyService.setBuddyLng(respObj.getJSONObject("buddy").
//                                            getJSONObject("user_location").getString("longitude"));
//                                    buddyService.setBuddyLoc(respObj.getJSONObject("buddy").
//                                            getJSONObject("user_location").getString("location"));

                                    }
                                    buddyServiceList.add(buddyService);
                                }
                                buddyTransitAdapter.notifyDataSetChanged();
                            }
                        } else {
                            showNoDataView();
                        }
                    } else {
                        showNoDataView();
                    }

                } catch (JSONException e) {
                    showNoDataView();
                    e.getLocalizedMessage();
                }
            }
        }

    }

    /**
     * show no data
     */
    private void showNoDataView() {
        trackBuddyListView.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
    }
}
