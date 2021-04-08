package com.caringaide.user.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.Touch;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.caringaide.user.R;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.BookingSummary;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.SharedPrefsManager;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.diffTwoDatesInMinutes;
import static com.caringaide.user.utils.CommonUtilities.getCurrentDateTimeAsString;
import static com.caringaide.user.utils.CommonUtilities.getDateAndTimeAsString;
import static com.caringaide.user.utils.CommonUtilities.getHoursAndMinutes;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;
import static com.caringaide.user.utils.CommonUtilities.serviceArrayList;
import static com.caringaide.user.utils.CommonUtilities.setToCamelCase;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.showProgressDialog;
import static com.caringaide.user.utils.CommonUtilities.startTimeThreshold;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * unused.
 * A simple {@link Fragment} subclass that lists all the bookings
 */
public class ListBookingsFragment extends Fragment {

    private static final String TAG = "ListBookingsFragments";
    private View homeView;
    private Button pendingMoreDetailBtn,confirmMoreDetailsBtn,cancelMoreDetailsBtn;
    private TextView notifyPendingBadge,notifyConfirmBadge,notifyCancelBadge;
    private TextView noBookingConfirmBubble,noBookingCancelBubble;
    private TextView pendingBookingMsgBubble,pendingFavBookingMsgBubble,pendingOtherBookingMsgBubble,
            noBookingBubble,confirmBookingMsgBubble,confirmUpcomingBookingBubble;
    private TextView cancelBookingText, noCancelBooking;
    private Animation bounceAnimation,rightAnimation,leftAnimation;
    private MaterialCardView pendingCardView,confirmCardView,cancelCardView;
    private String clickContext ="";
    private String confirmStatusContext ="";
    private ProgressDialog progressDialog;
    private Animation fadeAnimation,bubbleAnimation;
    private boolean isPendingLoaded = false,isConfirmLoaded = false,isCancelLoaded = false;
    ProgressDialog onStartProgress;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scrollView;

    public ListBookingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeView = inflater.inflate(R.layout.list_bookings_fragment_layout, container, false);
        setView();
        return homeView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadSummaryData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCardViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null!=onStartProgress)
        onStartProgress.dismiss();
    }

    private void setView() {
        onStartProgress = showProgressDialog(getActivity(),getString(R.string.loading));
        scrollView = homeView.findViewById(R.id.scrollview_listbookings);
        swipeRefreshLayout = homeView.findViewById(R.id.swiperefresh);
        pendingCardView = homeView.findViewById(R.id.pending_card_view);
        confirmCardView = homeView.findViewById(R.id.confirm_card_view);
        cancelCardView = homeView.findViewById(R.id.cancel_card_view);
        notifyPendingBadge = homeView.findViewById(R.id.pending_booking_notify_badge);
        notifyConfirmBadge = homeView.findViewById(R.id.confirm_notify_badge);
        pendingMoreDetailBtn = homeView.findViewById(R.id.pending_more_details);
        confirmMoreDetailsBtn = homeView.findViewById(R.id.confirm_more_details);
        cancelMoreDetailsBtn = homeView.findViewById(R.id.cancel_more_details);
        pendingBookingMsgBubble = homeView.findViewById(R.id.booking_info_bubble);
        confirmBookingMsgBubble = homeView.findViewById(R.id.confirm_nearest_booking);
        confirmUpcomingBookingBubble = homeView.findViewById(R.id.confirm_other_info);
        pendingFavBookingMsgBubble = homeView.findViewById(R.id.fav_client_bubble);
        pendingOtherBookingMsgBubble = homeView.findViewById(R.id.other_client_bubble);
        noBookingBubble = homeView.findViewById(R.id.no_booking_bubble);
        noBookingConfirmBubble = homeView.findViewById(R.id.no_bookings_confirm_bubble);
        cancelBookingText = homeView.findViewById(R.id.cancel_nearest_booking);
        noCancelBooking = homeView.findViewById(R.id.no_cancel_bookings);
        notifyPendingBadge.setOnClickListener(new PendingClickListener());
        pendingMoreDetailBtn.setOnClickListener(new PendingClickListener());
        confirmMoreDetailsBtn.setOnClickListener(new ConfirmClickListener());
        notifyConfirmBadge.setOnClickListener(new ConfirmClickListener());
        cancelMoreDetailsBtn.setOnClickListener(new CancelClickListener());
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadSummaryData();
                    }
                }
        );
    }

    private void loadSummaryData() {
        isPendingLoaded = isConfirmLoaded = isCancelLoaded = false;
        getPendingSummary();
        getConfirmSummary();
        getCancelSummary();
        scrollView.scrollTo(0,0);
        clearTextView();
    }
    private void clearTextView(){
        pendingBookingMsgBubble.setText("");
        pendingFavBookingMsgBubble.setText("");
        pendingOtherBookingMsgBubble.setText("");
        noBookingBubble.setText("");
        confirmBookingMsgBubble.setText("");
        confirmUpcomingBookingBubble.setText("");
        noBookingConfirmBubble.setText("");
        cancelBookingText.setText("");
        noCancelBooking.setText("");
        pendingBookingMsgBubble.setVisibility(View.GONE);
        pendingFavBookingMsgBubble.setVisibility(View.GONE);
        pendingOtherBookingMsgBubble.setVisibility(View.GONE);
        noBookingBubble.setVisibility(View.GONE);
        confirmBookingMsgBubble.setVisibility(View.GONE);
        confirmUpcomingBookingBubble.setVisibility(View.GONE);
        noBookingConfirmBubble.setVisibility(View.GONE);
        cancelBookingText.setVisibility(View.GONE);
        noCancelBooking.setVisibility(View.GONE);
        pendingMoreDetailBtn.setVisibility(View.GONE);
        confirmMoreDetailsBtn.setVisibility(View.GONE);
        cancelMoreDetailsBtn.setVisibility(View.GONE);
        notifyPendingBadge.setVisibility(View.GONE);
        notifyConfirmBadge.setVisibility(View.GONE);
    }

    private void loadCardViews(){
        if (isPendingLoaded&&isConfirmLoaded&&isCancelLoaded){
            onStartProgress.dismiss();
            swipeRefreshLayout.setRefreshing(false);
//            setViewAnimation();
            pendingCardView.setVisibility(View.VISIBLE);
            confirmCardView.setVisibility(View.VISIBLE);
            cancelCardView.setVisibility(View.VISIBLE);
        }
    }
    private void setViewAnimation() {
        bounceAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.animate_grow);
        bounceAnimation.setDuration(2000);
        rightAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.animate_rigth_in);
        leftAnimation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        rightAnimation.setDuration(1500);
        leftAnimation.setDuration(1000);
        pendingCardView.setAnimation(leftAnimation);
        confirmCardView.setAnimation(rightAnimation);
        cancelCardView.setAnimation(leftAnimation);
    }

    /**
     * summary of both broadcast and peer requests
     */
    private void getPendingSummary() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handlePendingSummary(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.getPendingSummary(requestParams);
    }

    /**
     * confirm summary
     */
    private void getConfirmSummary() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleConfirmSummary(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.getConfirmSummary(requestParams);
    }
    /**
     * confirm summary
     */
    private void getCancelSummary() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleCancelSummary(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.getCancelSummary(requestParams);
    }


    /**
     * remote response of pending summary
     * @param remoteResponse
     */
    private void handlePendingSummary(RemoteResponse remoteResponse) {
        Context context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        if (null != context && isAdded()) {
            JSONObject nearestBooking = null,lastBooking = null;
            String customErrorMsg = context.getString(R.string.no_bookings);
            if (null == remoteResponse) {
                toastShort(getString(R.string.no_bookings));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    String response = remoteResponse.getResponse();
                    if (null == response) {
                        showNoPendingBookings();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("Error")) {
                        if (jsonObject.getString("Error").equalsIgnoreCase("false")) {
                            if (jsonObject.has("data")) {
                                BookingSummary bookingSummary = new BookingSummary();
                                bookingSummary.setBookingStatus(getString(R.string.data_pending));
                                JSONObject responseObject = jsonObject.getJSONObject("data");
                                if (responseObject.has("nearest_booking")&&!responseObject.isNull("nearest_booking")) {
                                    nearestBooking = responseObject.getJSONObject("nearest_booking");
                                    if (null!= nearestBooking.getString("buddy_id")&&!nearestBooking.isNull("buddy_id")){
                                        bookingSummary.setFavBooking(true);
                                        if (null!= nearestBooking.getString("buddy_name")&&!nearestBooking.isNull("buddy_name")) {
                                            String buddyName = nearestBooking.getString("buddy_name");
                                            bookingSummary.setPendingFavBuddy(buddyName);
                                        }
                                    }else{
                                        bookingSummary.setFavBooking(false);
                                    }
                                    String nearestBookingDate = nearestBooking.getString("booking_date");
                                    if (null != nearestBookingDate && !nearestBookingDate.isEmpty()) {
                                        nearestBookingDate = getDateAndTimeAsString(nearestBookingDate);
                                    }
                                    bookingSummary.setPendingBookingDate(nearestBookingDate);
                                    bookingSummary.setPendingBookingLoc(nearestBooking.getString("client_location"));
                                    bookingSummary.setPendingBeneficiary(nearestBooking.getJSONObject("beneficiary").getString("full_name"));
                                    String totalBooking = responseObject.getString("total_bookings");
                                    int totalBookingCount = !totalBooking.isEmpty() ? Integer.parseInt(totalBooking) : 0;
                                    //show count badge
                                    showNotifyBadge(totalBookingCount, notifyPendingBadge, pendingMoreDetailBtn);
                                    String nearestInterVal = responseObject.getString("nearest_interval_in_mins");
                                    int nearestIntervalVal = !nearestInterVal.isEmpty() ? Integer.parseInt(nearestInterVal) : 0;
                                    bookingSummary.setNearestIntervalMins(nearestIntervalVal);
                                    createMessageBubbles(pendingBookingMsgBubble, nearestIntervalVal, getMessageText(BuddyConstants.HomeDataContext.PENDING, bookingSummary,false));
                                    String fav = responseObject.getString("fav_count");
                                    int favCount = !fav.isEmpty() ? Integer.parseInt(fav) : 0;
                                    String other = responseObject.getString("others_count");
                                    int otherCount = !other.isEmpty() ? Integer.parseInt(other) : 0;
                                    bookingSummary.setPendingBookingFavCount(favCount);
                                    bookingSummary.setPendingBookingOtherCount(otherCount);
                                    if (favCount>0) {
                                        createMessageBubbles(pendingFavBookingMsgBubble, -999, getMessageText(BuddyConstants.HomeDataContext.PEER, bookingSummary,false));
                                    }
                                    if (otherCount>0)
                                        createMessageBubbles(pendingOtherBookingMsgBubble, -999, getMessageText(BuddyConstants.HomeDataContext.OTHER, bookingSummary,false));
                                }
                            } else {
                                Log.d(TAG, "handleBookingsCountResponse: " +
                                        getString(R.string.no_bookings).concat(getString(R.string.no_data)));
                                showNoPendingBookings();
                            }
                        }else{
                            showNoPendingBookings();
                        }
                    }

                } catch (JSONException e) {
                    toastShort(getString(R.string.error_label).concat(getString(R.string.no_bookings)));
                    e.getLocalizedMessage();
                    showNoPendingBookings();
                }
                isPendingLoaded = true;
                loadCardViews();
            }
        }
    }


    private void handleConfirmSummary(RemoteResponse remoteResponse) {
        Context context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        if (null != context && isAdded()) {
            noBookingConfirmBubble.setVisibility(View.GONE);
            JSONObject nearestBooking = null;
            JSONArray upcomingBookngArray = null;
            String customErrorMsg = context.getString(R.string.no_bookings);
            if (null == remoteResponse) {
                toastShort(getString(R.string.no_bookings));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    String response = remoteResponse.getResponse();
                    if (null == response) {
                        showNoConfirmedBookings();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("Error")) {
                        if (jsonObject.getString("Error").equalsIgnoreCase("false")) {
                            if (jsonObject.has("data")) {
                                JSONObject responseObject = jsonObject.getJSONObject("data");
                                BookingSummary bookingSummary = new BookingSummary();
                                bookingSummary.setBookingStatus(getString(R.string.data_confirm));
                                Log.d(TAG, "handleBookingsCountResponse: " + responseObject);
                                if (responseObject.has("bookings")&&!responseObject.isNull("bookings")) {
                                    upcomingBookngArray = responseObject.getJSONArray("bookings");
                                    if (upcomingBookngArray.length()>=2) {
                                        bookingSummary.setLastBooking(true);
                                        JSONObject upcomingBooking = upcomingBookngArray.getJSONObject(upcomingBookngArray.length()-2);
                                        bookingSummary.setLastBookingId(upcomingBooking.getString("booking_id"));
                                        bookingSummary.setLastBookingDate(getDateAndTimeAsString(upcomingBooking.getString("booking_date")));
                                        bookingSummary.setLastBookingLoc(upcomingBooking.getString("client_location"));
                                        bookingSummary.setLastBookingBuddyname(upcomingBooking.getString("buddy_name"));
                                        bookingSummary.setConfirmLastBookingBeneficiary(upcomingBooking.getJSONObject("beneficiary").getString("full_name"));
                                    }
                                }
                                if (responseObject.has("nearest_booking")&&!responseObject.isNull("nearest_booking")) {
                                    nearestBooking = responseObject.getJSONObject("nearest_booking");
                                    String nearestBookingId = nearestBooking.getString("booking_id");
                                    String nearestBookingDate = nearestBooking.getString("booking_date");
                                    String username = nearestBooking.getString("buddy_name");
                                    String nearestLocation = nearestBooking.getString("client_location");
                                    String nearestLocZipcode = nearestBooking.getString("client_zipcode");
                                    String expectedServiceTime = nearestBooking.getString("expected_service_time");
                                    if (null != nearestBookingDate && !nearestBookingDate.isEmpty()) {
                                        nearestBookingDate = getDateAndTimeAsString(nearestBookingDate);
                                    }
                                    bookingSummary.setBookingId(nearestBookingId);
                                    bookingSummary.setConfirmBuddyName(username);
                                    bookingSummary.setConfirmNearestBookingDate(nearestBookingDate);
                                    bookingSummary.setConfirmBookingLocation(nearestLocation);
                                    bookingSummary.setConfirmBookingZipcode(nearestLocZipcode);
                                    bookingSummary.setConfirmTotalBookingTime(expectedServiceTime);
                                    bookingSummary.setConfirmBeneficiary(nearestBooking.getJSONObject("beneficiary").getString("full_name"));
                                    String totalBooking = responseObject.getString("total_bookings");
                                    int count = !totalBooking.isEmpty() ? Integer.parseInt(totalBooking) : 0;
                                    //show count badge
                                    showNotifyBadge(count, notifyConfirmBadge, confirmMoreDetailsBtn);
                                    String nearestInterVal = responseObject.getString("nearest_interval_in_mins");
                                    int nearestIntervalVal = !nearestInterVal.isEmpty() ? Integer.parseInt(nearestInterVal) : 0;
                                    bookingSummary.setNearestIntervalMins(nearestIntervalVal);
                                    createConfirmContext(confirmBookingMsgBubble,nearestIntervalVal,bookingSummary,false);
                                    if (nearestIntervalVal==0){
                                        checkForUpcomingBooking(bookingSummary);
                                    }
                                }
                            } else {
                                Log.d(TAG, "handleBookingsCountResponse: " +
                                        getString(R.string.no_bookings).concat(getString(R.string.no_data)));
                                showNoConfirmedBookings();
                            }
                        }else{
                            showNoConfirmedBookings();
                        }
                    }

                } catch (JSONException e) {
                    toastShort(getString(R.string.error_label).concat(getString(R.string.no_bookings)));
                    e.getLocalizedMessage();
                    showNoConfirmedBookings();
                }

                isConfirmLoaded = true;
                loadCardViews();
            }
        }
    }


    private void handleCancelSummary(RemoteResponse remoteResponse) {
        Context context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        if (null != context && isAdded()) {
            noCancelBooking.setVisibility(View.GONE);
            JSONObject nearestBooking = null;
            JSONArray recentCancelledBookngArray = null;
            String customErrorMsg = context.getString(R.string.no_bookings);
            if (null == remoteResponse) {
                toastShort(getString(R.string.no_bookings));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    String response = remoteResponse.getResponse();
                    if (null == response) {
                        showNoCancelledBookings();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("Error")) {
                        if (jsonObject.getString("Error").equalsIgnoreCase("false")) {
                            if (jsonObject.has("data")) {
                                JSONObject responseObject = jsonObject.getJSONObject("data");
                                BookingSummary bookingSummary = new BookingSummary();
                                bookingSummary.setBookingStatus(getString(R.string.data_cancel));
                                Log.d(TAG, "handleBookingsCountResponse: " + responseObject);
                                //if total count 0, no booking and no more details button
                                //if count >0, show more details button
                                String totalBookings = responseObject.getString("total_bookings");
                                int totalBookingsCount = null!=totalBookings&&!totalBookings.isEmpty()? Integer.parseInt(totalBookings) :0;
                                bookingSummary.setCancelBookingCount(totalBookingsCount);
                                showNotifyBadge(Integer.parseInt(totalBookings), null, cancelMoreDetailsBtn);
                                if (responseObject.has("nearest_booking")&&!responseObject.isNull("nearest_booking")) {
                                    nearestBooking = responseObject.getJSONObject("nearest_booking");
                                    bookingSummary.setCancelledBookingDate(getDateAndTimeAsString(nearestBooking.getString("cancelled_time")));
                                    bookingSummary.setCancelBookingId(nearestBooking.getString("booking_id"));
                                    bookingSummary.setCancelUsername(nearestBooking.getString("buddy_name"));

                                    if (nearestBooking.has("cancelled_by")) {
                                        if(nearestBooking.isNull("cancelled_by")){
                                            bookingSummary.setCancelledBy(getString(R.string.system));
                                        }else {
                                            String cancelledBy = nearestBooking.getString("cancelled_by");
                                            if (cancelledBy.equals("4")) {
                                                bookingSummary.setCancelledBy(getString(R.string.system));
                                            } else {
                                                bookingSummary.setCancelledBy(SharedPrefsManager.getInstance().getUserID()
                                                        .equals(cancelledBy) ? context.getString(R.string.user) : context.getString(R.string.buddy));
                                            }
                                        }
                                    }
                                }
                                checkForCancelBooking(bookingSummary);
                            } else {
                                Log.d(TAG, "handleBookingsCountResponse: " + getString(R.string.no_bookings).concat(getString(R.string.no_data)));
                                showNoCancelledBookings();
                            }
                        }else{
                            createMessageBubbles(noCancelBooking,-1,getString(R.string.no_recent_cancel));
                        }
                    }


                } catch (JSONException e) {
                    toastShort(getString(R.string.error_label).concat(getString(R.string.no_bookings)));
                    e.getLocalizedMessage();
                    createMessageBubbles(noCancelBooking,-1,getString(R.string.no_recent_cancel));
                }

                isCancelLoaded = true;
                loadCardViews();
            }
        }
    }

    private void showNoPendingBookings() {
        createMessageBubbles(noBookingBubble,-1,getString(R.string.no_bookings));
        noBookingBubble.setVisibility(View.VISIBLE);
        pendingFavBookingMsgBubble.setVisibility(View.GONE);
        pendingBookingMsgBubble.setVisibility(View.GONE);
        pendingOtherBookingMsgBubble.setVisibility(View.GONE);
        pendingOtherBookingMsgBubble.setVisibility(View.GONE);
    }

    private void showNoConfirmedBookings(){
        createMessageBubbles(noBookingConfirmBubble,-1,getString(R.string.no_bookings));
        noBookingConfirmBubble.setVisibility(View.VISIBLE);
        confirmBookingMsgBubble.setVisibility(View.GONE);
    }

    private void showNoCancelledBookings() {
        createMessageBubbles(noCancelBooking,-999,getString(R.string.no_recent_cancel));
        noCancelBooking.setVisibility(View.VISIBLE);
    }

    /**
     * create message views
     * @param messageBubble
     * @param timeInMins
     * @param text
     * timeInMins can be 0 or more .
     * -1 will be for "no bookings" with no image.
     * -999 for warning with iamge
     */
    private void createMessageBubbles(TextView messageBubble,int timeInMins,String text){
        if (null!=text && !text.isEmpty()) {
            int warningDrawable = 0, warningTextColor = 0;
            if (timeInMins>=0) {
                if (timeInMins < CommonUtilities.startTimeThreshold) {
                    warningDrawable = R.drawable.ic_warning_red_24dp;
                    warningTextColor = getResources().getColor(R.color.colorAccent);
                } else {
                    warningDrawable = R.drawable.ic_warning_yellow_24dp;
                    warningTextColor = getResources().getColor(R.color.theme_blue);
                }
                messageBubble.setCompoundDrawablesWithIntrinsicBounds(warningDrawable, 0, 0, 0);
                messageBubble.setTextColor(warningTextColor);
            }
            if (timeInMins == -999){
                warningDrawable = R.drawable.ic_warning_yellow_24dp;
                warningTextColor = getResources().getColor(R.color.theme_blue);
                messageBubble.setCompoundDrawablesWithIntrinsicBounds(warningDrawable, 0, 0, 0);
                messageBubble.setTextColor(warningTextColor);
            }
            messageBubble.setText(text);
            messageBubble.setVisibility(View.VISIBLE);
        }else{
            messageBubble.setVisibility(View.GONE);
        }
    }

    private void createConfirmContext(TextView confirmView,int interval,BookingSummary bookingSummary,boolean isupcoming){
        if (interval == 0){
            createMessageBubbles(confirmView, interval, getMessageText(BuddyConstants.HomeDataContext.CONFIRM_EXPIRY, bookingSummary,isupcoming));
        }else if (interval>0 && interval<=30){
            createMessageBubbles(confirmView, interval, getMessageText(BuddyConstants.HomeDataContext.CONFIRM_DURATION, bookingSummary,isupcoming));
        }else if (interval>30 && interval<=90){
            createMessageBubbles(confirmView, interval, getMessageText(BuddyConstants.HomeDataContext.CONFIRM_90, bookingSummary,isupcoming));
        }else{
            createMessageBubbles(confirmView, interval, getMessageText(BuddyConstants.HomeDataContext.CONFIRM, bookingSummary,isupcoming));

        }

    }

    private void checkForUpcomingBooking(BookingSummary bookingSummary) {
        if (bookingSummary.isLastBooking()){
            int minDiff = 0;
            minDiff = (int) (diffTwoDatesInMinutes(getCurrentDateTimeAsString(),bookingSummary.getLastBookingDate()));
            bookingSummary.setDurationBtwLastBooking(minDiff);
            createConfirmContext(confirmUpcomingBookingBubble,minDiff,bookingSummary,true);
        }
    }
    private void checkForCancelBooking(BookingSummary bookingSummary) {
        int minDiff = 0;
        minDiff = (int) (diffTwoDatesInMinutes(bookingSummary.getCancelledBookingDate(),getCurrentDateTimeAsString()));
        if (minDiff<180) {
            bookingSummary.setCancel(true);
            createMessageBubbles(cancelBookingText,-1,getMessageText(BuddyConstants.HomeDataContext.CANCEL,bookingSummary,false));
        }else{
            bookingSummary.setCancel(false);showNoCancelledBookings();
        }
    }

    /**
     * get data  text
     * @param context
     * @param bookingSummary
     * @return
     */
    private String getMessageText(String context,BookingSummary bookingSummary,boolean isUpcomingBooking ){
        Resources res = getResources();
        String message = "";
        switch (context) {
            case BuddyConstants.HomeDataContext.PENDING:
                if (bookingSummary.getNearestIntervalMins()>startTimeThreshold) {
                    if (bookingSummary.isFavBooking()) {
                        message = String.format(res.getString(R.string.pending_fav_nearest_booking), setToCamelCase(bookingSummary.getPendingFavBuddy()), setToCamelCase(bookingSummary.getPendingBeneficiary())
                                , bookingSummary.getPendingBookingDate());
                    } else {
                        message = String.format(res.getString(R.string.pending_nearest_booking), setToCamelCase(bookingSummary.getPendingBeneficiary()), bookingSummary.getPendingBookingDate());
                    }
                }else{
                    if (bookingSummary.isFavBooking()) {
                        message = String.format(res.getString(R.string.pending_30_fav_nearest_booking), setToCamelCase(bookingSummary.getPendingFavBuddy()), setToCamelCase(bookingSummary.getPendingBeneficiary()),bookingSummary.getPendingBookingDate()
                                ,getHoursAndMinutes(bookingSummary.getNearestIntervalMins()));
                    } else {
                        message = String.format(res.getString(R.string.pending_30_nearest_booking), setToCamelCase(bookingSummary.getPendingBeneficiary()), bookingSummary.getPendingBookingDate(), getHoursAndMinutes(bookingSummary.getNearestIntervalMins()));
                    }
                }
                break;
            case BuddyConstants.HomeDataContext.PEER:
                if (bookingSummary.getPendingBookingFavCount()>1){
                    message = String.format(res.getString(R.string.pending_favcounts_booking), bookingSummary.getPendingBookingFavCount());
                }else{
                    message = getString(R.string.pending_favcount_booking);
                }
                break;
            case BuddyConstants.HomeDataContext.CONFIRM:
                if (isUpcomingBooking){
                    message = String.format(res.getString(R.string.confirm_booking_status),
                            bookingSummary.getLastBookingId(), bookingSummary.getLastBookingDate(),bookingSummary.getLastBookingLoc(),setToCamelCase(bookingSummary.getLastBookingBuddyname()));
                }else {
                    message = String.format(res.getString(R.string.confirm_booking_status),bookingSummary.getBookingId(),
                            bookingSummary.getConfirmNearestBookingDate(),bookingSummary.getConfirmBookingLocation()
                            ,setToCamelCase(bookingSummary.getConfirmBuddyName()));
                }
                break;
            case BuddyConstants.HomeDataContext.CONFIRM_DURATION:
                if (isUpcomingBooking){
                    message = String.format(res.getString(R.string.confirm_30m_booking_status),setToCamelCase(bookingSummary.getConfirmLastBookingBeneficiary()),bookingSummary.getLastBookingId()
                            ,getHoursAndMinutes(bookingSummary.getDurationBtwLastBooking()),setToCamelCase(bookingSummary.getConfirmBuddyName()));
                }else {
                    message = String.format(res.getString(R.string.confirm_30m_booking_status),setToCamelCase(bookingSummary.getConfirmBeneficiary()),
                            bookingSummary.getBookingId(),
                            getHoursAndMinutes(bookingSummary.getNearestIntervalMins()),setToCamelCase(bookingSummary.getConfirmBuddyName()));
                }
                break;
            case BuddyConstants.HomeDataContext.CONFIRM_90:
                if (isUpcomingBooking){
                    message = String.format(res.getString(R.string.confirm_90m_booking_status),setToCamelCase(bookingSummary.getConfirmLastBookingBeneficiary()),bookingSummary.getLastBookingId()
                            ,getHoursAndMinutes(bookingSummary.getDurationBtwLastBooking()),
                            bookingSummary.getLastBookingBuddyname());

                }else {
                    message = String.format(res.getString(R.string.confirm_90m_booking_status),setToCamelCase(bookingSummary.getConfirmBeneficiary()),bookingSummary.getBookingId(),
                            getHoursAndMinutes(bookingSummary.getNearestIntervalMins()),
                            setToCamelCase(bookingSummary.getConfirmBuddyName()));
                }
                break;
            case BuddyConstants.HomeDataContext.CONFIRM_EXPIRY:
                if (isUpcomingBooking){
                    message = String.format(res.getString(R.string.confirm_0m_booking_status),bookingSummary.getLastBookingId(),
                            bookingSummary.getLastBookingDate(),setToCamelCase(bookingSummary.getLastBookingBuddyname()));
                }else {
                    message = String.format(res.getString(R.string.confirm_0m_booking_status),
                            bookingSummary.getBookingId(),
                            bookingSummary.getConfirmNearestBookingDate(),setToCamelCase(bookingSummary.getConfirmBuddyName()));
                }
                break;
            case BuddyConstants.HomeDataContext.OTHER:
                int pendingCount = bookingSummary.getPendingBookingOtherCount();
                if (pendingCount>1){
                    message = String.format(res.getString(R.string.only_other_booking), bookingSummary.getPendingBookingOtherCount());
                }else{
                    message = String.format(res.getString(R.string.other_count_booking), bookingSummary.getPendingBookingOtherCount());
                }
                break;
            case BuddyConstants.HomeDataContext.CANCEL:
                message = String.format(res.getString(R.string.cancel_booking_status),bookingSummary.getCancelBookingId()
                        ,bookingSummary.getCancelledBookingDate(),bookingSummary.getCancelledBy());
                break;
            default:
                message = getString(R.string.no_bookings);
                break;
        }
//        }
        return message;
    }

    /**
     * manage count badge and moredeatils btn visibility
     * @param count
     * @param bubbleView
     * @param moreDetailBtn
     */
    private void showNotifyBadge(int count, TextView bubbleView,Button moreDetailBtn){
        if (count>0) {
            if (null!=bubbleView) {
                bubbleView.setText("" + count);
                bubbleView.setAnimation(bounceAnimation);
                bubbleView.setVisibility(View.VISIBLE);
            }
            moreDetailBtn.setVisibility(View.VISIBLE);
        }else{
            if (null!=bubbleView) {
                bubbleView.setVisibility(View.GONE);
            }
            moreDetailBtn.setVisibility(View.GONE);
        }
    }

    /**
     * load corresponding fragments
     * @param clickContext
     */
    private void loadCorrespondingFragments(String clickContext) {
        switch (clickContext){
            case "BroadcastFragment":
                changeFragment(new PeerListFragment());
                break;
            case "ConfirmListFragment":
                ConfirmListFragment confirmListFragment = new ConfirmListFragment();
                changeFragment(confirmListFragment);
                break;
            case "CancelListFragment":
                changeFragment(new CancelListFragment());
                break;

        }
    }

    /**
     * changes fragment
     * @param fragment
     */
    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    /*===================================================================
     * ====================inner class area===============================
     * ===================================================================*/
    private class PendingClickListener extends SingleClickListener{
        @Override
        public void onSingleClick(View v) {
            clickContext = "BroadcastFragment";
            loadCorrespondingFragments(clickContext);
        }
    }
    private class ConfirmClickListener extends SingleClickListener {
        @Override
        public void onSingleClick(View v) {
            clickContext = "ConfirmListFragment";
            loadCorrespondingFragments(clickContext);
        }
    }
    private class CancelClickListener extends SingleClickListener {
        @Override
        public void onSingleClick(View v) {
            clickContext = "CancelListFragment";
            loadCorrespondingFragments(clickContext);
        }
    }
}
