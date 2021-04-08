package com.caringaide.user.fragments;


import android.content.Context;
import android.content.Intent;
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
import com.caringaide.user.activities.UserHomeActivity;
import com.caringaide.user.adapters.BuddyPendingAdapter;
import com.caringaide.user.interfaces.RefreshListAdapterListener;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.CommonUtilities;

import org.json.JSONException;

import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;
import static com.caringaide.user.utils.CommonUtilities.serviceArrayList;
import static com.caringaide.user.utils.CommonUtilities.toastShort;


/**
 * A simple {@link Fragment} subclass.
 */
public class PeerListFragment extends Fragment implements RefreshListAdapterListener {

    private static final String TAG = "PendingListFragments";
    private View pendingView;
    private BuddyPendingAdapter buddyPendingAdapter;
    private ListView peerListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noData;
    public PeerListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pendingView = inflater.inflate(R.layout.pending_list_fragment_layout, container, false);
        loadPendingRequests(getString(R.string.data_pending).toLowerCase());
        //setBackAction();
        setView();
        return pendingView;
    }

    /**
     * initializes ui
     */
    private void setView() {
        swipeRefreshLayout = pendingView.findViewById(R.id.swiperefresh_pending);
        peerListView = pendingView.findViewById(R.id.service_data_list);
        buddyPendingAdapter = new BuddyPendingAdapter(getContext(), R.layout.peer_service_request_layout, serviceArrayList, peerListView,
                PeerListFragment.this,this);
        peerListView.setAdapter(buddyPendingAdapter);
        noData = pendingView.findViewById(R.id.no_data_tv_pending);
        peerListView.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPendingRequests(getString(R.string.data_pending).toLowerCase());
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

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setBackAction() {
        pendingView.setFocusableInTouchMode(true);
        pendingView.requestFocus();
        pendingView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().beginTransaction().remove(PeerListFragment.this).show(new ListBookingsFragmentAlt()).commit();
                    return true;
                }
                return false;
            }
        } );
    }

    /**
     * show no data
     */
    private void showNoDataView() {
        peerListView.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
    }


    /**
     * load detailed list of pending requests from server
     * @param status
     */
    private void loadPendingRequests(String status) {
        RequestParams requestParams = new RequestParams((remoteResponse)-> {
            serviceListResponse(remoteResponse,getString(R.string.pending_list_error));
        },getContext(),null);
        UserServiceHandler.getRequestsStatus(status,requestParams);
    }

    private void serviceListResponse(RemoteResponse remoteResponse, String errorMsg) {
       if (null != getActivity() && isAdded()) {
            if (null != swipeRefreshLayout) {
                swipeRefreshLayout.setRefreshing(false);
            }if (null == remoteResponse) {
                toastShort(errorMsg);
            }else {
                remoteResponse.setCustomErrorMessage(errorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(),remoteResponse)) {
                        serviceArrayList.clear();
                        serviceArrayList.addAll(CommonUtilities.getBookingsResponse(remoteResponse));
                        if (serviceArrayList.isEmpty()){
                            showNoBookingsAlert();
                        }else{
                            buddyPendingAdapter.notifyDataSetChanged();
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

    /**
     * callback method of refreshlistener
     */
    @Override
    public void onDataRefresh() {
        if (serviceArrayList.size()==0){
            showNoDataView();
        }
    }
}
