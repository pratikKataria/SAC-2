package com.caringaide.user.fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.caringaide.user.R;
import com.caringaide.user.adapters.PendingPaymentsAdapter;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.RefreshListAdapterListener;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.BuddyConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.caringaide.user.utils.CommonUtilities.getServerMessageCode;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingPaymentsFragment extends Fragment implements RefreshListAdapterListener{


    private static final String TAG = "PendingPayFrag";
    private View pendingPayView;
    ListView pendingListView;
    PendingPaymentsAdapter buddyServiceAdapter;
    private ArrayList<BuddyService> buddyServiceList;
    private PendingPaymentsAdapter pendingPaymentsAdapter;
    private View noData;
    private String paymentOtpVal = null;

    public PendingPaymentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        pendingPayView  = inflater.inflate(R.layout.payment_listview, container, false);
        setView();
        setBackAction();
        return pendingPayView;
    }

    /**
     * set back action
     */
    private void setBackAction() {
        pendingPayView.setFocusableInTouchMode(true);
        pendingPayView.requestFocus();
        pendingPayView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().beginTransaction().remove(PendingPaymentsFragment.this).commit();
                    return true;
                }
                return false;
            }
        } );
    }

    /**
     * initializes view
     */
    private void setView() {
        pendingListView = pendingPayView.findViewById(R.id.payment_listview_layout);
        ImageButton closePage = pendingPayView.findViewById(R.id.close_pending_payment);
        buddyServiceList = new ArrayList<>();
        pendingPaymentsAdapter = new PendingPaymentsAdapter(getActivity(), R.layout.pending_payments_fragment_layout, buddyServiceList,this,this);
        pendingListView.setAdapter(pendingPaymentsAdapter);
//        noData = pendingPayView.findViewById(R.id.no_data_tv_end);
//        noData.setVisibility(View.GONE);
        pendingListView.setVisibility(View.VISIBLE);
        closePage.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getFragmentManager().beginTransaction().remove(PendingPaymentsFragment.this).commit();
            }
        });
        getPendingFares();
    }

    /**
     * get tipable booking details
     */
    private void getPendingFares() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handlePendingFareResponse(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.getPendingPayments(requestParams);
    }

    /**
     * remote response of tipable bookings
     * @param remoteResponse
     */
    private void handlePendingFareResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.err_pending_fares);
            if (null == remoteResponse) {
                Log.e(TAG, "handlePendingFareResponse: " + getString(R.string.empty_response)
                        .concat(getString(R.string.err_pending_fares)));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                buddyServiceList.clear();
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        Log.d(TAG, "handlePendingFareResponse: " + jsonObject);
                        if (jsonObject.has("message")) {
                            if (jsonObject.getString("message").equalsIgnoreCase(BuddyConstants.VERIFY_OTP)) {
//                                openOtpDialog();
                            } else {
                                if (jsonObject.has("data")) {
                                    JSONArray responseArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < responseArray.length(); i++) {
                                        BuddyService buddyService = new BuddyService();
                                        buddyService.setId(responseArray.getJSONObject(i).getString("booking_id"));
                                        buddyService.setTotalAmount(responseArray.getJSONObject(i).getString("total_fare"));
                                        buddyService.setPaymentId(responseArray.getJSONObject(i).getString("id"));
                                        buddyService.setBuddyName(responseArray.getJSONObject(i).getJSONObject("user_buddy").getString("full_name"));
                                        buddyService.setBenFullName(responseArray.getJSONObject(i).getJSONObject("beneficiary").getString("full_name"));
                                        buddyService.setStartDate(responseArray.getJSONObject(i).getJSONObject("booking").getString("start_date"));
                                        buddyService.setEndDate(responseArray.getJSONObject(i).getJSONObject("booking").getString("end_date"));
                                        buddyService.setDisplayId(responseArray.getJSONObject(i).getJSONObject("booking").getString("booking_id"));
                                        buddyService.setBuddyMobile(responseArray.getJSONObject(i).getJSONObject("user_buddy").getString("mobile"));
                                        buddyService.setBuddyId(responseArray.getJSONObject(i).getJSONObject("user_buddy").getString("id"));
                                        if (responseArray.getJSONObject(i).getJSONObject("booking").has("feedbacks")) {
                                            JSONArray feedbackArray = responseArray.getJSONObject(i).getJSONObject("booking").getJSONArray("feedbacks");
                                            if (feedbackArray.length() > 0) {
                                                for (int j = 0; j < feedbackArray.length(); j++) {
                                                    if (feedbackArray.getJSONObject(j).getString("user_type").equalsIgnoreCase("2")) {
                                                        if (feedbackArray.getJSONObject(j).has("points")) {
                                                            buddyService.setRated(true);
                                                            buddyService.setFeedbackRating(feedbackArray.getJSONObject(j).getString("points"));
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                        buddyServiceList.add(buddyService);
                                    }

                                    pendingPaymentsAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    } else {

                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        Log.e(TAG, "handlePendingFareResponse: " + getServerMessageCode(jsonObject.getString("message")));
                        getFragmentManager().beginTransaction().remove(PendingPaymentsFragment.this).commit();
                    }
                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
            }
        }
    }



    /**
     * callback of refresh data listener
     */
    @Override
    public void onDataRefresh() {
        getPendingFares();
    }

}
