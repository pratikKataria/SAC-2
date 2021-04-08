package com.caringaide.user.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.caringaide.user.R;
import com.caringaide.user.activities.UserHomeActivity;
import com.caringaide.user.adapters.BuddyOngoingAdapter;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.CommonUtilities;

import org.json.JSONException;

import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.getCurrentDateTimeAsString;
import static com.caringaide.user.utils.CommonUtilities.getTimeInMillis;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.minWaitOngoingRefresh;
import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;
import static com.caringaide.user.utils.CommonUtilities.serviceArrayList;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class OngoingListFragment extends Fragment {

    private static final String TAG = "ConfirmListFragments";
    View ongoingView;
    BuddyOngoingAdapter buddyServiceAdapter;
    ListView ongoingListView;
    TextView noData;
    private Button refreshListBtn;
    private  static int refreshCount = 0;
    private static int currentTimeInSec = 0;
    private static int lastRefreshSec = 0;
    Activity activity = getActivity();
    public OngoingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ongoingView = inflater.inflate(R.layout.ongoing_list_layout, container, false);
        //setBackAction();
        setView();
        //setOngoingView(getString(R.string.data_confirm));

        return ongoingView;
    }

    /**
     * initializes the view components
     */
    private void setView() {
        ongoingListView = ongoingView.findViewById(R.id.ongoing_service_data_list);
        buddyServiceAdapter = new BuddyOngoingAdapter(getContext(), R.layout.ongoing_service_request_layout, serviceArrayList, ongoingListView, OngoingListFragment.this);
        ongoingListView.setAdapter(buddyServiceAdapter);
        refreshListBtn = ongoingView.findViewById(R.id.refresh_list_btn);
        noData = ongoingView.findViewById(R.id.no_data_tv_start);
        ongoingListView.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        refreshListBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                refreshOngoingList();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    private void setBackAction() {
        ongoingView.setFocusableInTouchMode(true);
        ongoingView.requestFocus();
        ongoingView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().beginTransaction().remove(OngoingListFragment.this).commit();
                    return true;
                }
                return false;
            }
        } );
    }
    private void showNoDataView() {
        ongoingListView.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
        alertAndAction(BuddyApp.getCurrentActivity(), BuddyApp.getCurrentActivity().getString(R.string.info_label),
                BuddyApp.getCurrentActivity().getString(R.string.no_bookings_available),
                BuddyApp.getCurrentActivity().getString(R.string.ok),
                null, new AlertAction() {
                    @Override
                    public void positiveAction() {

                        moveToHomeActivity();
                    }

                    @Override
                    public void negativeAction() {

                    }
                });
    }
    private void refreshOngoingList(){
        if (checkRefreshList()) {
            RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                @Override
                public void onResponse(RemoteResponse remoteResponse) {
                    handleOngoingServiceListResponse(remoteResponse);
                }
            }, getActivity(), null);
            UserServiceHandler.getRequestsStatus(getString(R.string.data_start), requestParams);
        }
    }

    private void handleOngoingServiceListResponse(RemoteResponse remoteResponse) {
        if (null == remoteResponse) {
            toastShort(getString(R.string.ongoing_list_error));
        }else {
            remoteResponse.setCustomErrorMessage(getString(R.string.ongoing_list_error));
            try {
                if (!isErrorsFromResponse(getActivity(),remoteResponse)) {
                    refreshCount+=1;
                    lastRefreshSec = (int) (getTimeInMillis(getCurrentDateTimeAsString())/1000);
                    serviceArrayList.clear();
                    serviceArrayList.addAll(CommonUtilities.getBookingsResponse(remoteResponse));
                    if (serviceArrayList.isEmpty()){
                        showNoDataView();
                    }else{
//                        ongoingListView.setAdapter(buddyServiceAdapter);
                        buddyServiceAdapter.notifyDataSetChanged();
                    }
                }else{
                    showNoDataView();
                }
            } catch (JSONException e) {
                toastShort( getString(R.string.ongoing_list_error));
                showNoDataView();
                e.getLocalizedMessage();
            }
        }
    }
    private boolean checkRefreshList(){
        if (refreshCount>0){
            currentTimeInSec = (int) (getTimeInMillis(getCurrentDateTimeAsString())/1000);
            int secDiff = currentTimeInSec- lastRefreshSec;
            Log.d(TAG, "checkRefreshList: secoDiff "+secDiff);
            if (secDiff>0) {
                if (refreshCount * minWaitOngoingRefresh > secDiff) {
                    int timeToLogin = minWaitOngoingRefresh - secDiff;
                    toastShort(getString(R.string.try_refresh_aftr_label) + timeToLogin + getString(R.string.seconds_label));
                    return false;
                } else {
                    return true;
                }
            }else{
                return true;
            }
        }//else bypass login call -- initial condition
        return true;
    }
}
