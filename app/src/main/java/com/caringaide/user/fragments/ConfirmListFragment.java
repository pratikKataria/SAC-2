package com.caringaide.user.fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.adapters.BuddyConfirmAdapter;
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
public class ConfirmListFragment extends Fragment implements RefreshListAdapterListener {

    private static final String TAG = "ConfirmListFragments";
    private View confirmView;
    private BuddyConfirmAdapter buddyServiceAdapter;
    private ListView confirmListView;
    private TextView noData;
    private Context context = getActivity();
    private SwipeRefreshLayout swipeRefreshLayout;
    public ConfirmListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        confirmView = inflater.inflate(R.layout.confirm_list_fragment_layout, container, false);
        setView();
        loadConfirmRequestsData(getString(R.string.data_confirm).toLowerCase());
        return confirmView;
    }
    /**
     * set the list of data to be displayed
     */
    private void setView() {
        swipeRefreshLayout = confirmView.findViewById(R.id.swiperefresh_confirm);
        confirmListView = confirmView.findViewById(R.id.service_data_list);
        buddyServiceAdapter = new BuddyConfirmAdapter(getContext(), R.layout.confirm_service_request_layout,
                serviceArrayList, ConfirmListFragment.this,this);
        confirmListView.setAdapter(buddyServiceAdapter);
        noData = confirmView.findViewById(R.id.no_data_tv_confirm);
        confirmListView.setVisibility(View.VISIBLE);
        noData.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadConfirmRequestsData(getString(R.string.data_confirm).toLowerCase());
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

    private void showNoDataView() {
        confirmListView.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
    }

    /**
     * load  the detailed list of confirmed bookings  from server
     * @param status
     */
    private void loadConfirmRequestsData(String status) {
        RequestParams requestParams = new RequestParams((remoteResponse) ->{
            serviceListResponse(remoteResponse,getString(R.string.confirm_list_error));
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
    /**
     * callback of RefreshDataListener, which is invoked when an entry in data is updated
     */
    @Override
    public void onDataRefresh() {
        if (serviceArrayList.size()==0){
            showNoDataView();
        }
    }
}
