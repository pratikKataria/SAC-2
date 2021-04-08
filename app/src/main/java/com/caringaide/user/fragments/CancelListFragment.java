package com.caringaide.user.fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.adapters.BuddyCancelAdapter;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.CommonUtilities;

import org.json.JSONException;

import java.util.ArrayList;

import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;
import static com.caringaide.user.utils.CommonUtilities.serviceArrayList;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class CancelListFragment extends Fragment {

    private static final String TAG = "PendingListFragments";
    private View cancelView;
    private ArrayList buddyServiceList;
    private BuddyCancelAdapter buddyServiceAdapter;
    private ListView cancelListView;
    private TextView noData;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    public CancelListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cancelView = inflater.inflate(R.layout.fragment_cancel_list, container, false);
        buddyServiceList = new ArrayList<BuddyService>();
        setView();
        loadCancelBookings(getString(R.string.data_cancel));
        return cancelView;
    }
    /**
     * set the list of data to be displayed
     */
    private void setView() {
        swipeRefreshLayout = cancelView.findViewById(R.id.swiperefresh_cancel);
        cancelListView = cancelView.findViewById(R.id.service_data_list);
        buddyServiceAdapter = new BuddyCancelAdapter(getContext(), R.layout.cancel_service_layout, serviceArrayList,CancelListFragment.this);
        cancelListView.setAdapter(buddyServiceAdapter);
        noData = cancelView.findViewById(R.id.no_data_tv_cancel);
        noData.setVisibility(View.GONE);
        cancelListView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCancelBookings(getString(R.string.data_cancel));
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    private void setBackAction() {
        cancelView.setFocusableInTouchMode(true);
        cancelView.requestFocus();
        cancelView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().beginTransaction().remove(CancelListFragment.this).commit();
                    return true;
                }
                return false;
            }
        } );
    }

    /**
     * load the detailed list of cancelled services from server
     * @param status
     */
    private void loadCancelBookings(String status) {
        RequestParams requestParams = new RequestParams((remoteResponse) -> {
            if (null != getActivity() && isAdded()) {
                serviceListResponse(remoteResponse, getString(R.string.cancel_list_error));
            }
        },getContext(),null);
        UserServiceHandler.getRequestsStatus(status,requestParams);
    }


    private void serviceListResponse(RemoteResponse remoteResponse, String errorMsg) {
        if (null != getActivity() && isAdded()) {
            if (null != swipeRefreshLayout) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (null == remoteResponse) {
                toastShort(errorMsg);
            }else {
                remoteResponse.setCustomErrorMessage(errorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(),remoteResponse)) {
                        serviceArrayList.clear();
                        serviceArrayList.addAll(CommonUtilities.getBookingsResponse(remoteResponse));
                        if (serviceArrayList.isEmpty()){
                            showNoDataView();
                        }else{
                            buddyServiceAdapter.notifyDataSetChanged();
                        }
                    }else{
                        showNoBookingsAlert();
                    }
                } catch (JSONException e) {
                    toastShort(errorMsg);
                    showNoDataView();
                    e.getLocalizedMessage();
                }
            }
        }
    }
    /**
     * show no bookings alert
     */
    private void showNoBookingsAlert(){
        alertAndAction(getActivity(), getString(R.string.info_label), getString(R.string.no_bookings),
                getString(R.string.ok), null, new AlertAction() {
                    @Override
                    public void positiveAction() {
                        moveToHomeActivity();
                    }

                    @Override
                    public void negativeAction() {

                    }
                });
    }

    private void showNoDataView() {
        cancelListView.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
    }

}

