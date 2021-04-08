package com.caringaide.user.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.activities.UserHomeActivity;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.fontStyle;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * unused
 * A {@link Fragment} subclass for showing Ratings.
 */
public class RatingFragment extends Fragment {

    View ratingView;
    ImageView profileImage;
    TextView etBuddyName;
    EditText ratingDetailedTxt,negativeRatingDetailsTxt;
    Button submitBtn,cancelBtn;
    RatingBar benRatingBar;
    LinearLayout specifyRatingLayout,underRateLayout;
    ScrollView userRatingLayout;
    String buddyName ="", benefBookingId = "", buddyGender ="",buddyMobile = "";
    int totalRating = 0;
    private static final String TAG = "RatingScreen";
    public RatingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            benefBookingId = bundle.getString("ben_booking_id", "");
            buddyName = bundle.getString("buddy_name", "");
            buddyGender = bundle.getString("buddy_gen", "");
            buddyMobile = bundle.getString("buddy_mobile", "");
        }
        ratingView = inflater.inflate(R.layout.rating_fragment_layout, container, false);
        profileImage = ratingView.findViewById(R.id.iv_feedback_driver_img);
        etBuddyName = ratingView.findViewById(R.id.feedback_ben_name);
        benRatingBar = ratingView.findViewById(R.id.rate_buddy_star);
        userRatingLayout = ratingView.findViewById(R.id.sv_user_choice_layout);
        specifyRatingLayout = ratingView.findViewById(R.id.rate_spec_layout);
        underRateLayout = ratingView.findViewById(R.id.under_rate_layout);
        ratingDetailedTxt = ratingView.findViewById(R.id.user_ride_experience);
        negativeRatingDetailsTxt = ratingView.findViewById(R.id.user_ride_pexperience);
        submitBtn = ratingView.findViewById(R.id.rate_buddy_btn);
        cancelBtn = ratingView.findViewById(R.id.rate_ben_not_now);
        etBuddyName.setText(buddyName);
        if (buddyGender.equalsIgnoreCase("F")){
            profileImage.setImageResource(R.drawable.girl_avatar);
        }else{
            profileImage.setImageResource(R.drawable.boy_avatar);
        }

        submitBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (totalRating==0){
                    toastShort(getString(R.string.rating_error));
                }else {
                    setRatingDetails();
                }
            }
        });
        cancelBtn.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                moveToHomeActivity();
            }
        });
        benRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d(TAG, "onRatingChanged: "+rating);
                if (rating>0){
                    userRatingLayout.setVisibility(View.VISIBLE);
                    submitBtn.setVisibility(View.VISIBLE);
                    if (rating<=3){
                        underRateLayout.setVisibility(View.VISIBLE);
                    }else{
                        underRateLayout.setVisibility(View.GONE);
                    }
                    submitBtn.setEnabled(true);
                    totalRating = (int) rating;
                }else{
                    userRatingLayout.setVisibility(View.GONE);
                    submitBtn.setVisibility(View.GONE);
                }
            }
        });
        return ratingView;
    }

    private void setRatingDetails() {
        //remote call for saveRating
        String bookingId = "";
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                handleRatingResponse(remoteresponse);
            }
        },getActivity(),null);
        Map<String,String> params = new HashMap<>();
        params.put("points", String.valueOf(totalRating));
        params.put("likes",getSelectedPositives());
        params.put("improvements",getSelectedImprovements());
        params.put("comments",ratingDetailedTxt.getText().toString());
        requestParams.setRequestParams(params);
        UserServiceHandler.saveFeedBack(benefBookingId,requestParams);
    }

    private void handleRatingResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = getString(R.string.save_feedback_fail);
            if (null == remoteResponse) {
                toastShort(customErrorMsg);
                return;
            }
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                    JSONObject responseObject = new JSONObject(remoteResponse.getResponse());
                    Log.d(TAG, "handleTimeSchedulesResponse: " + responseObject);
                    if (responseObject.has("data")) {
                        Log.d(TAG, "handleRatingResponse: Successs");
                    }
                    showRatingResponse();

                }
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    private String getSelectedPositives(){
        String ratingCheckBoxText = "";
        int[] checkBoxIds = {R.id.chk1,R.id.chk2,R.id.chk3,R.id.chk4};
        for(int i=0 ; i<checkBoxIds.length;i++){
            CheckBox chk = ratingView.findViewById(checkBoxIds[i]);
            if (chk.isChecked()){
                ratingCheckBoxText += String.valueOf(chk.getText())+",";
            }
        }
        if (!ratingCheckBoxText.isEmpty()){
            ratingCheckBoxText.substring(0, ratingCheckBoxText.lastIndexOf(","));
        }
        return ratingCheckBoxText;
    }
    private String getSelectedImprovements(){
        String ratingCheckBoxText = "";
        int[] checkBoxIds = {R.id.uchk1,R.id.uchk2,R.id.uchk3,R.id.uchk4};
        for(int i=0 ; i<checkBoxIds.length;i++){
            CheckBox chk = ratingView.findViewById(checkBoxIds[i]);
            if (chk.isChecked()){
                ratingCheckBoxText+=(chk.getText().toString().concat(","));
                //ratingCheckBoxText += String.valueOf(chk.getText())+",";
            }
        }
        if (!(ratingCheckBoxText.toString()).isEmpty()){
            ratingCheckBoxText.substring(0, ratingCheckBoxText.lastIndexOf(","));
        }
        return ratingCheckBoxText.toString();
    }

     private void showRatingResponse(){
        alertAndAction(getActivity(), getString(R.string.rating_success_title), getString(R.string.rating_success_data),
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

}
