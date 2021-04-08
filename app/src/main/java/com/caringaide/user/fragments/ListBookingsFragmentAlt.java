package com.caringaide.user.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.caringaide.user.R;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.CommonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import static com.caringaide.user.utils.BuddyConstants.CONFIRM_COUNT_TITLE;
import static com.caringaide.user.utils.BuddyConstants.MESSAGE_TEXT;
import static com.caringaide.user.utils.BuddyConstants.MESSAGE_TYPE;
import static com.caringaide.user.utils.BuddyConstants.PEER_COUNT_TITLE;
import static com.caringaide.user.utils.BuddyConstants.PENDING_COUNT_TITLE;
import static com.caringaide.user.utils.CommonUtilities.isConfirmOpen;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.isPeerOpen;
import static com.caringaide.user.utils.CommonUtilities.isPendingOpen;
import static com.caringaide.user.utils.CommonUtilities.serviceArrayList;
import static com.caringaide.user.utils.CommonUtilities.showActiveServices;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.showProgressDialog;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass that lists all booking .
 */
public class ListBookingsFragmentAlt extends Fragment {
    private static final String TAG = "HomeFrag";
    private Animation leftAnimation, rightAnimation,bounceAnimation;
    private LinearLayout peerLayout,broadcastLayout,confirmLayout,cancelLayout,endLayout,paidLayout,ongoingLayout;
    private String clickContext = "";
    private ProgressDialog progressDialog;
    private TextView notifyPendingBadge, notifyPeerBadge,notifyConfirmBadge;

    public ListBookingsFragmentAlt() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.list_bookings_layout_alt, container, false);
        Bundle bundle = getArguments();
        if (null!= bundle){
            handleEventNotifications(bundle);
        }
        setUIComponents(homeView);
        return homeView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getBookingCount();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null!=progressDialog)
        progressDialog.dismiss();
    }

    /**
     * get number of bookings in transit/start state
     */
    private void getBookingCount() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleBookingCountResponse(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.getBookingCount(requestParams);
    }

    /**
     * remote response for getting booking count
     * @param remoteResponse
     */
    private void handleBookingCountResponse(RemoteResponse remoteResponse) {
        Context context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        String customErrorMsg = context.getString(R.string.no_bookings);
        if(null == remoteResponse){
            toastShort(getString(R.string.no_bookings));
        }else{
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                String response = remoteResponse.getResponse();
                if (null==response){
                    return;
                }
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("Error")) {
                    if (jsonObject.getString("Error").equalsIgnoreCase("false")) {
                        if (jsonObject.has("data")) {
                            JSONObject responseObject = jsonObject.getJSONObject("data");
                            Log.d(TAG, "handleBookingsCountResponse: " + responseObject);
                            if (responseObject.has(PENDING_COUNT_TITLE)) {
                                String pendingCount = responseObject.getString(PENDING_COUNT_TITLE);
                                if (!pendingCount.equalsIgnoreCase("0") && !isPendingOpen) {
                                    notifyPendingBadge.setText(pendingCount);
//                                notifyPendingBadge.setAnimation(bubbleAnimation);
                                    notifyPendingBadge.setVisibility(View.VISIBLE);
                                    notifyPendingBadge.setAnimation(bounceAnimation);
                                }
                            }
                            if (responseObject.has(PEER_COUNT_TITLE)) {
                                String peerCount = responseObject.getString(PEER_COUNT_TITLE);
                                if (!peerCount.equalsIgnoreCase("0") && !isPeerOpen) {
                                    notifyPeerBadge.setText(peerCount);
//                                notifyPeerBadge.setAnimation(bubbleAnimation);
                                    notifyPeerBadge.setVisibility(View.VISIBLE);
                                    notifyPeerBadge.setAnimation(bounceAnimation);
                                }
                            }
                            if (responseObject.has(CONFIRM_COUNT_TITLE)) {
                                String confirmCount = responseObject.getString(CONFIRM_COUNT_TITLE);
                                if (!confirmCount.equalsIgnoreCase("0") && !isConfirmOpen) {
                                    notifyConfirmBadge.setText(confirmCount);
//                                notifyConfirmBadge.setAnimation(bubbleAnimation);
                                    notifyConfirmBadge.setVisibility(View.VISIBLE);
                                    notifyConfirmBadge.setAnimation(bounceAnimation);
                                }
                            }

                        } else {
                            Log.d(TAG, "handleBookingsCountResponse: " +
                                    getString(R.string.no_bookings).concat(getString(R.string.no_data)));
//                        toastShort(context, getString(R.string.no_bookings));
                        }
                    }
                }

            }catch (JSONException e){
                toastShort(getString(R.string.error_label).concat(getString(R.string.no_bookings)));
                e.getLocalizedMessage();
            }
        }
    }

    /**
     * initailize view
     * @param homeView
     */
    private void setUIComponents(View homeView) {
        peerLayout = homeView.findViewById(R.id.req_peer_layout);
        broadcastLayout = homeView.findViewById(R.id.req_broadcast_layout);
        confirmLayout = homeView.findViewById(R.id.req_confirm_layout);
        cancelLayout = homeView.findViewById(R.id.req_cancel_layout);
        endLayout = homeView.findViewById(R.id.req_end_layout);
        paidLayout = homeView.findViewById(R.id.req_paid_layout);
        ongoingLayout = homeView.findViewById(R.id.req_ongoing_layout);
        notifyPeerBadge = homeView.findViewById(R.id.peer_notify_badge);
        notifyPendingBadge = homeView.findViewById(R.id.pending_notify_badge);
        notifyConfirmBadge = homeView.findViewById(R.id.confirm_notify_badge);
        setAnimation();
        setRequestData();
    }

    /**
     * set animation variables
     */
    private void setAnimation() {
        leftAnimation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        rightAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.animate_rigth_in);
        bounceAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up);
        leftAnimation.setDuration(1000);
        rightAnimation.setDuration(700);
        peerLayout.startAnimation(leftAnimation);
        confirmLayout.startAnimation(leftAnimation);
        cancelLayout.startAnimation(rightAnimation);
        paidLayout.startAnimation(leftAnimation);
        broadcastLayout.startAnimation(rightAnimation);
        ongoingLayout.startAnimation(rightAnimation);
        endLayout.startAnimation(rightAnimation);
    }

    /**
     * set onclick action of different status tiles
     */
    private void setRequestData() {
        peerLayout.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                listPeerBookings();
            }
        });
        broadcastLayout.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                listBroadcastBookings();
            }
        });
        confirmLayout.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                listConfirmedBookings();
            }
        });
        cancelLayout.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {listCancelledServices();
            }
        });
        endLayout.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {listEndServices();
            }
        });
        paidLayout.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {listPaidService();
            }
        });
        ongoingLayout.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showActiveServices = true;
                listStartedServices();
            }
        });

    }

    /**
     * this will take care of handling intents from notifications
     * @param bundle
     */
    private void handleEventNotifications(Bundle bundle){
        String messageType = null;
        if(bundle!=null &&  (null!= (messageType=bundle.getString(MESSAGE_TYPE)))){
            Log.d(TAG," messageType is "+messageType);
            String notificationMsg = bundle.getString(MESSAGE_TEXT);
            Log.d(TAG,"notification msg " + notificationMsg);
            //get the enum for the message type
            BuddyConstants.NotificationTypes notificationType = BuddyConstants.NotificationTypes.valueOf(messageType);
            switch(notificationType){
                case NEW_BOOKING_TITLE:
                    listBroadcastBookings();
                    break;
                case NEW_BOOKING_TITLE_FOR_BUDDY:
                    listPeerBookings();
                    break;
                case BOOKING_CANCELLED_TITLE:
                    listCancelledServices();
                    break;
                case BOOKING_CONFIRMED_TITLE_BY_BUDDY:
                    listConfirmedBookings();
                    break;
                case SERVICE_STARTED_TITLE_BY_BUDDY:
                    listStartedServices();
                    break;
                case SERVICE_ENDED_TITLE_BY_BUDDY:
                    listEndServices();
                    break;
                default:
                    break;
            }
        }else{
            Log.e(TAG,"bundle is null");
        }

    }

    /**
     * set paid list
     */
    private void listPaidService() {
        clickContext = "PaidListFragment";
        progressDialog = showProgressDialog(getActivity(),getString(R.string.loading));
        setPaidView(getString(R.string.data_paid));
    }

    /**
     * set end list
     */
    private void listEndServices() {
        clickContext = "EndListFragment";
        progressDialog = showProgressDialog(getActivity(),getString(R.string.loading));
        setEndView(getString(R.string.data_end));

    }

    /**
     * set start list
     */
    private void listStartedServices() {
        clickContext = "OngoingListFragment";
        progressDialog = showProgressDialog(getActivity(),getString(R.string.loading));
        setOngoingView(getString(R.string.data_start));
    }

    /**
     * set confirmed list
     */
    private void listConfirmedBookings() {
        isConfirmOpen = true;
        clickContext = "ConfirmListFragment";
        progressDialog = showProgressDialog(getActivity(),getString(R.string.loading));
        setConfirmView(getString(R.string.data_confirm));
    }

    /**
     * set cancelled list
     */
    private void listCancelledServices() {
        clickContext = "CancelListFragment";
        progressDialog = showProgressDialog(getActivity(),getString(R.string.loading));
        loadCancelRequests(getString(R.string.data_cancel));
    }

    /**
     * set booking list with favorite buddies
     */
    private void listPeerBookings() {
        isPeerOpen = true;
        progressDialog = showProgressDialog(getActivity(),getString(R.string.loading));
        clickContext = "PeerListFragment";
        loadPeerRequests();
    }

    /**
     * set broadcast list
     */
    private void listBroadcastBookings() {
        isPendingOpen = true;
        progressDialog = showProgressDialog(getActivity(),getString(R.string.loading));
        clickContext = "BroadcastFragment";
        loadPendingRequestData(getString(R.string.data_pending));
    }

    /**
     * open another fragment
     * @param fragment
     */
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * load peer requests
     */
    private void loadPeerRequests() {
        final Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleServiceListResponse(remoteResponse,context.getString(R.string.pending_list_error));
            }
        },getContext(),null);
        UserServiceHandler.getPendingPeerRequests(requestParams);
    }

    /**
     * load broadcast request
     * @param status
     */
    private void loadPendingRequestData(String status) {
        final Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleServiceListResponse(remoteResponse,context.getString(R.string.pending_list_error));
            }
        },getContext(),null);
        UserServiceHandler.getRequestsStatus(status,requestParams);
    }

    /**
     * load confirm requests
     * @param status
     */
    private void setConfirmView(String status) {

        final Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleServiceListResponse(remoteResponse, context.getString(R.string.confirm_list_error));
            }
        },getContext(),null);
        UserServiceHandler.getRequestsStatus(status,requestParams);

    }
    private void loadCancelRequests(String status) {
        final Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleServiceListResponse(remoteResponse,context.getString(R.string.cancel_list_error));
            }
        },getContext(),null);
        UserServiceHandler.getRequestsStatus(status,requestParams);
    }
    private void setEndView(String name) {
        final Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleServiceListResponse(remoteResponse,context.getString(R.string.end_list_error));
            }
        },getContext(),null);
        UserServiceHandler.getRequestsStatus(name,requestParams);
    }

    private void setPaidView(String status) {
        final Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleServiceListResponse(remoteResponse,context.getString(R.string.end_list_error));
            }
        },getContext(),null);
        UserServiceHandler.getRequestsStatus(status,requestParams);
    }

    private void setOngoingView(String status) {
        final Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleServiceListResponse(remoteResponse,context.getString(R.string.ongoing_list_error));
            }
        },getContext(),null);
        UserServiceHandler.getRequestsStatus(status,requestParams);
    }

    /**
     * handle the  bookings response and add it to the corresponding fragment's -
     * list view by bridging it with an adapter
     * @param remoteResponse remote response
     * @param errorMsg error message to show on list
     */
    private void handleServiceListResponse(RemoteResponse remoteResponse,String errorMsg) {
        progressDialog.dismiss();
        if (null != getActivity() && isAdded()) {
            if (null == remoteResponse) {
                toastShort(errorMsg);
            }else {
                remoteResponse.setCustomErrorMessage(errorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(),remoteResponse)) {
                        serviceArrayList.clear();
                        serviceArrayList.addAll(CommonUtilities.getBookingsResponse(remoteResponse));
                        if (serviceArrayList.isEmpty()){
                            showNoBookings();
                        }else{
                            loadCorrespondingFragments(clickContext);
                        }
                    }else{
                        showNoBookings();
                    }
                } catch (JSONException e) {
                    toastShort( errorMsg);
                    showNoBookings();
                    e.getLocalizedMessage();
                }
            }
        }
    }

    /**
     * open corresponding fragments according to the clicked tiles
     * @param clickContext
     */
    private void loadCorrespondingFragments(String clickContext) {
        switch (clickContext){
            case "PeerListFragment":
                changeFragment(new PeerListFragment());
                break;
            case "BroadcastFragment":
                changeFragment(new BroadcastFragment());
                break;
            case "ConfirmListFragment":
                changeFragment(new ConfirmListFragment());
                break;
            case "CancelListFragment":
                changeFragment(new CancelListFragment());
                break;
            case "PaidListFragment":
                changeFragment(new PaidListFragment());
                break;
            case "OngoingListFragment":
                changeFragment(new OngoingListFragment());
                break;

        }
    }

    /**
     * show no bookings message when there is no data in that particular list
     */
    private void showNoBookings() {
        showAlertMsg(getString(R.string.info_label),getString(R.string.no_bookings));
    }
}
