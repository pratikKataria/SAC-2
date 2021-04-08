package com.caringaide.user.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caringaide.user.R;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

public class RatingActivity extends AppCompatActivity {
    
    ImageView buddyImage;
    TextView etBuddyName;
    EditText ratingDetailedTxt,ratingNegativeDetailsText;
    Button submitBtn,cancelBtn;
    RatingBar benRatingBar;
    LinearLayout specifyRatingLayout,underRateLayout;
    ScrollView userRatingLayout;
    String buddyName ="", benefBookingId = "", buddyGender ="",buddyMobile = "",buddyId="";
    int totalRating = 0;
    Bitmap bitmap;
    private static final String TAG = "RatingScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_fragment_layout);
        BuddyApp.setCurrentActivity(this);
        Intent bundle = this.getIntent();
        if (bundle != null) {
            benefBookingId = bundle.getStringExtra("ben_booking_id");
            buddyName = bundle.getStringExtra("buddy_name");
            buddyGender = bundle.getStringExtra("buddy_gen");
            buddyMobile = bundle.getStringExtra("buddy_mobile");
            buddyId = bundle.getStringExtra("buddy_id");
        }
        getBuddyImage();
        setUiComponents();
    }

    private void setUiComponents() {
        buddyImage = findViewById(R.id.iv_feedback_driver_img);
        etBuddyName = findViewById(R.id.feedback_ben_name);
        benRatingBar = findViewById(R.id.rate_buddy_star);
        userRatingLayout = findViewById(R.id.sv_user_choice_layout);
        specifyRatingLayout = findViewById(R.id.rate_spec_layout);
        underRateLayout = findViewById(R.id.under_rate_layout);
        ratingDetailedTxt = findViewById(R.id.user_ride_experience);
        ratingNegativeDetailsText = findViewById(R.id.user_ride_pexperience);
        submitBtn = findViewById(R.id.rate_buddy_btn);
        cancelBtn = findViewById(R.id.rate_ben_not_now);
        etBuddyName.setText(buddyName);
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
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }

    @Override
    protected void onDestroy() {
        Bitmap buddyImg=((BitmapDrawable)buddyImage.getDrawable()).getBitmap();
        buddyImg.recycle();
        super.onDestroy();
    }

    /**
     * fetch the buddy image
     */
    private void getBuddyImage() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                handleBuddyImage(remoteresponse);
            }
        }, this, null);
        UserServiceHandler.getUserImage(buddyId, requestParams);
    }

    /**
     * save the user rating for the buddy
     */
    private void setRatingDetails() {
        //remote call for saveRating
        String bookingId = "";
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                handleRatingResponse(remoteresponse);
            }
        },this,null);
        Map<String,String> params = new HashMap<>();
        params.put("points", String.valueOf(totalRating));
        params.put("likes",getSelectedPositives());
        params.put("improvements",getSelectedImprovements());
        params.put("comments",ratingDetailedTxt.getText().toString().concat(" ").concat(ratingNegativeDetailsText.getText().toString()));
        requestParams.setRequestParams(params);
        UserServiceHandler.saveFeedBack(benefBookingId,requestParams);
    }

    /**
     * remote response for save rating
     * @param remoteResponse
     */
    private void handleRatingResponse(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.save_feedback_fail);
        if (null == remoteResponse) {
            toastShort(customErrorMsg);
            return;
        }
        remoteResponse.setCustomErrorMessage(customErrorMsg);
        try {
            if (!isErrorsFromResponse(this, remoteResponse)) {
                JSONObject responseObject = new JSONObject(remoteResponse.getResponse());
                Log.d(TAG, "handleTimeSchedulesResponse: " + responseObject);
                if (responseObject.has("data")){
                    Log.d(TAG, "handleRatingResponse: Successs");
                }
                showRatingResponse();
            }
        } catch (JSONException e) {
            e.getLocalizedMessage();
        }
    }

    /**
     * get the positive comments for the buddy given by user
     * @return a string of positive comments
     */
    private String getSelectedPositives(){
        String ratingCheckBoxText = "";
        int[] checkBoxIds = {R.id.chk1,R.id.chk2,R.id.chk3,R.id.chk4};
        for(int i=0 ; i<checkBoxIds.length;i++){
            CheckBox chk = findViewById(checkBoxIds[i]);
            if (chk.isChecked()){
                ratingCheckBoxText += String.valueOf(chk.getText())+",";
            }
        }
        if (!ratingCheckBoxText.isEmpty()){
            ratingCheckBoxText.substring(0, ratingCheckBoxText.lastIndexOf(","));
        }
        return ratingCheckBoxText;
    }

    /**
     * get the negative commnets fro the buddy given by user
     * @return a string of negative comments
     */
    private String getSelectedImprovements(){
        String ratingCheckBoxText = "";
        int[] checkBoxIds = {R.id.uchk1,R.id.uchk2,R.id.uchk3,R.id.uchk4};
        for(int i=0 ; i<checkBoxIds.length;i++){
            CheckBox chk = findViewById(checkBoxIds[i]);
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

    /**
     * show an acknowledgement alert for a successful rating
     */
    private void showRatingResponse(){
        alertAndAction(this, getString(R.string.rating_success_title), getString(R.string.rating_success_data),
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
     * remote response for fetching buddy image
     * @param remoteResponse
     */
    private void handleBuddyImage(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.no_profile_image);
        if(null == remoteResponse){
            Log.d(TAG, "handleProfileImage: "+getString(R.string.no_profile_image));
        }else{
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(this,remoteResponse)) {
                    String response = remoteResponse.getResponse();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("data")) {
                        JSONArray responseArr = jsonObject.getJSONArray("data");
                        Log.d(TAG, "handleBuddyImage: "+responseArr);
//                        Bitmap profileImageData = CommonUtilities.getImageFromString(responseArr.getString(0),
//                                70,70);
//                        buddyImage.setImageBitmap(profileImageData);
                        byte[] imageByteArray = Base64.decode(responseArr.getString(0), Base64.DEFAULT);
                        if (!this.isDestroyed()) {
                            Glide.with(this)
                                    .load(imageByteArray)
                                    .asBitmap()
                                    .placeholder(R.drawable.user_avatar)
                                    .fitCenter()
                                    .into(buddyImage);
                        }
                    } else {
                        buddyImage.setImageResource(R.drawable.user_avatar);
                        Log.d(TAG, "handleBuddyImage: "+getString(R.string.no_profile_image));
                    }
                }

            }catch (JSONException e){
                Log.e(TAG, "handleBuddyImage: "+getString(R.string.no_profile_image));
                e.getLocalizedMessage();
            }
        }

    }
}
