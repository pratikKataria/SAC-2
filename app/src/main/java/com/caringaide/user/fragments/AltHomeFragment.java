package com.caringaide.user.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.caringaide.user.R;
import com.caringaide.user.activities.ListBeneficiaryActivity;
import com.caringaide.user.activities.ListBookingsActivity;
import com.caringaide.user.activities.RegisterBeneficiaryActivity;
import com.caringaide.user.adapters.HomeImageAdapter;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.model.City;
import com.caringaide.user.model.Countries;
import com.caringaide.user.model.Organization;
import com.caringaide.user.model.Services;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.SharedPrefsManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.CommonUtilities.getServerMessageCode;
import static com.caringaide.user.utils.CommonUtilities.isConfirmOpen;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.isPeerOpen;
import static com.caringaide.user.utils.CommonUtilities.isPendingOpen;
import static com.caringaide.user.utils.CommonUtilities.showActiveServices;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class AltHomeFragment extends Fragment {

    private static final String TAG = "AltHomeFrag";
    private View homeView;
    private LinearLayout bookNowImageView,listBookingsImageView;
    Animation animation,leftAnimation,bubbleAnimation;
    private FrameLayout announcementFrameLayout;
    private JSONArray announcementArray;
    TableLayout announcementTableLayout;
    private FrameLayout infoRegisterBen;
    private Animation upAnimation;
    private ViewFlipper homeImageFlipper;


    public AltHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.user_home_activity_layout, container, false);
        setView();
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.animate_rigth_in);
        leftAnimation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
        bubbleAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.push_up);
        animation.setDuration(1500);
        leftAnimation.setDuration(1000);

        /*ViewPager viewPager = (ViewPager) homeView.findViewById(R.id.viewPager);
        HomeImageAdapter adapter = new HomeImageAdapter(getActivity());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(adapter.getCount()-1);*/

        return homeView;
    }

    private void setView() {
        announcementTableLayout = homeView.findViewById(R.id.scrollabe_announcement_layout);
        bookNowImageView = homeView.findViewById(R.id.home_book_now);
        listBookingsImageView = homeView.findViewById(R.id.home_my_bookings);
        announcementFrameLayout = homeView.findViewById(R.id.announcement_layout);
        FloatingActionButton showAnnouncementBtn = homeView.findViewById(R.id.show_announcements_btn);
        infoRegisterBen = homeView.findViewById(R.id.register_ben_notify);
        homeImageFlipper = homeView.findViewById(R.id.imageFlipper);
        bookNowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListBeneficiaryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        listBookingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPendingOpen = isPeerOpen = isConfirmOpen = false;
                Intent intent = new Intent(getActivity(), ListBookingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        showAnnouncementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get announcements
                try {
                    if ((announcementTableLayout).getChildCount() > 0)
                        (announcementTableLayout).removeAllViews();
                    if (null != announcementArray) {
                        for (int i = 0; i < announcementArray.length(); i++) {
                            JSONObject responseObj = announcementArray.getJSONObject(i);
                            String recipients = responseObj.getString("who");
                            String message = responseObj.getString("message");

                            // Create TextBox Dynamically
                            if (recipients.equalsIgnoreCase("users") || recipients.equalsIgnoreCase("all")) {
                                setAnnouncementLayout(announcementTableLayout, message);
                            }
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        if (SharedPrefsManager.getInstance().getBen().equalsIgnoreCase("0")){

            upAnimation= AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_upwards);
            upAnimation.setDuration(2000);
            infoRegisterBen.setVisibility(View.VISIBLE);
            infoRegisterBen.setAnimation(upAnimation);
            infoRegisterBen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), RegisterBeneficiaryActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }else{
            infoRegisterBen.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        getUserAnnouncements();
//        loadTransitBookingsData();
        getPendingFares();
        super.onStart();
        showActiveServices = false;
        bookNowImageView.startAnimation(leftAnimation);
        listBookingsImageView.startAnimation(animation);
    }

    private void getPendingFares() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handlePendingFareResponse(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.getPendingPayments(requestParams);
    }

    private void handlePendingFareResponse(RemoteResponse remoteResponse) {
        Context context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        if (isAdded()) {
            String customErrorMsg = context.getString(R.string.err_pending_fares);
            if (null == remoteResponse) {
                Log.e(TAG, "handlePendingFareResponse: " + context.getString(R.string.empty_response)
                        .concat(context.getString(R.string.err_pending_fares)));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        Log.d(TAG, "handlePendingFareResponse: " + jsonObject);
                        if (jsonObject.has("data")) {
                            changeFragment(new PendingPaymentsFragment());
                        }
                    } else {

                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        Log.e(TAG, "handlePendingFareResponse: " + getServerMessageCode(jsonObject.getString("message")));
                    }
                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
            }
        }
    }



    private void getUserAnnouncements() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleUserAnnouncements(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.getAnnouncements(requestParams);
    }

    private void handleUserAnnouncements(RemoteResponse remoteResponse) {
        Context context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        if (isAdded()) {
            String customErrorMsg = getString(R.string.get_announcement_failed);
            if (null == remoteResponse) {
                toastShort(getString(R.string.get_announcement_failed));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        if (jsonObject.has("data")) {
                            announcementFrameLayout.setVisibility(View.VISIBLE);
                            announcementArray = jsonObject.getJSONArray("data");
                        }
                    }
                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
            }
        }
    }

    private void setAnnouncementLayout(TableLayout tableLayout,final  String message) {
        if (message != null) {
            bubbleAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up);
            String labelMsg = message.length() > 34 ? message.substring(0, 34) + "..." : message;
            TableRow tbrow = new TableRow(getActivity());
            tbrow.setBackground(getResources().getDrawable(R.drawable.rounded_button_blue_bordered));
            tbrow.setMinimumHeight(120);
//            tbrow.setAnimation(bubbleAnimation);
            TextView tv = new TextView(getActivity());
            tv.setTextAppearance(getActivity(),R.style.NormalTextStyle);
            tv.setText(labelMsg);
            tv.setPadding(8, 8, 8, 8);
            tv.setTextSize(18);
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(2, 2, 2, 2);
            tbrow.setLayoutParams(params);
            tbrow.addView(tv);
            if (tableLayout != null) {
                tableLayout.addView(tbrow);
            }
            announcementFrameLayout.setVisibility(View.VISIBLE);
            announcementTableLayout.setAnimation(bubbleAnimation);
            announcementTableLayout.setVisibility(View.VISIBLE);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = getLayoutInflater();
                    View toastLayout = inflater.inflate(R.layout.custom_announcement_toast_layout,
                            (ViewGroup) homeView.findViewById(R.id.l_custom_announcement_toast_layout));
                    TextView textValue = toastLayout.findViewById(R.id.announcement_text);
                    textValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shout, 0, 0, 0);
                    textValue.setText(message);
                    Toast toast = new Toast(getActivity());
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(toastLayout);
                    toast.show();
                }
            });
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    announcementFrameLayout.setVisibility(View.GONE);
                    announcementTableLayout.setVisibility(View.GONE);
                }
            }, 15 * 1000);
        }
    }


    private void loadTransitBookingsData() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                transitListResponse(remoteResponse);
            }
        },getActivity(),null);
        Map<String,String> params = new HashMap<>();
        params.put("statuses",getString(R.string.transit_label)+","+getString(R.string.data_start));
        requestParams.setRequestParams(params);
        UserServiceHandler.getTrackingRequestStatus(requestParams);
    }

    private void transitListResponse(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.transit_list_error);
        if(null == remoteResponse){
            toastShort(customErrorMsg);
        }else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    Log.d(TAG, "transitListResponse: "+jsonObject);
                    if (jsonObject.has("data")) {
                        JSONArray responseArray = jsonObject.getJSONArray("data");
                        if (null != responseArray) {
                            for (int i=0;i<responseArray.length();i++){
                                JSONObject resObj = responseArray.getJSONObject(i);
                                String status = resObj.getString("status");
                                if (status.equalsIgnoreCase(getString(R.string.data_start))){
                                    openToastForActiveServices();
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "transitListResponse: "+remoteResponse.getResponse());
//                        showNoDataView();
                    }
                } else {
//                    showNoDataView();

                    Log.d(TAG, "transitListResponse: error");
                }

            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }
    private void openToastForActiveServices(){
        LayoutInflater inflater = getLayoutInflater();
        View toastLayout = inflater.inflate(R.layout.custom_announcement_toast_layout, (ViewGroup) homeView.findViewById(R.id.l_custom_announcement_toast_layout));
        toastLayout.setBackgroundColor(getResources().getColor(R.color.black_light));
        TextView textValue = toastLayout.findViewById(R.id.announcement_text);
        textValue.setText(getString(R.string.alert_active_services));
        Toast toast = new Toast(getActivity());
        toast.setGravity(Gravity.TOP,0,0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastLayout);
        toast.show();
    }

    private void changeFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.pending_pay_frame_layout,fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        this.getFragmentManager().popBackStackImmediate();
        transaction.commitAllowingStateLoss();
    }

}
