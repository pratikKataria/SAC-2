package com.caringaide.user.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.caringaide.user.R;
import com.caringaide.user.activities.UserHomeActivity;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.CommonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.toastLong;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFragment extends Fragment {

    View paymentView;
    private static String TAG = "PaymentFragment";
    String bookingId = "",benName="",serviceId="",baseFareVal="",taxVal="",extraRateVal="";
    TextView baseFare,tax,extraTime,extraRate,totalFare;
    EditText etUserExperience;
    LinearLayout ratingLayout,underRateLayout;
    RelativeLayout paymentLayout;
    ScrollView userSpecLayout;
    RatingBar rateBuddy;
    Button payFareBtn,rateButton;
    Animation pushUpAnimation;
    private int totalRating;

    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        paymentView = inflater.inflate(R.layout.payment_fragment_layout, container, false);
        Bundle bundle = getArguments();
        if (null != bundle){
            bookingId = bundle.getString("ben_booking_id");
            benName = bundle.getString("ben_name");
            serviceId = bundle.getString("service_id");
        }
        setView();
        getFaresOnEnd();
        return paymentView;
    }

    /**
     * initiaizes the view components
     */
    private void setView() {
        pushUpAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.push_up);
        pushUpAnimation.setDuration(1000);
        baseFare = paymentView.findViewById(R.id.base_fare_val);
        tax = paymentView.findViewById(R.id.tax_val);
        extraRate = paymentView.findViewById(R.id.extra_rate_val);
        extraTime = paymentView.findViewById(R.id.extra_time_val);
        totalFare = paymentView.findViewById(R.id.total_amount_val);
        payFareBtn = paymentView.findViewById(R.id.pay_fare);
        rateButton = paymentView.findViewById(R.id.rate_buddy_btn);
        ratingLayout = paymentView.findViewById(R.id.payment_done_layout);
        rateBuddy = paymentView.findViewById(R.id.rate_buddy_star);
        underRateLayout = paymentView.findViewById(R.id.under_rate_layout);
        userSpecLayout = paymentView.findViewById(R.id.sv_user_choice_layout);
        paymentLayout = paymentView.findViewById(R.id.fare_details_layout);
        etUserExperience = paymentView.findViewById(R.id.user_ride_experience);
        payFareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double totalFare = Double.valueOf(baseFareVal)+Double.valueOf(taxVal);//+Double.valueOf(extraRateVal);
                RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                    @Override
                    public void onResponse(RemoteResponse remoteResponse) {
                        handlePayResponse(remoteResponse);
                    }
                },getContext(),null);
                Map<String,String> params = new HashMap<>();
                params.put("service_id",serviceId);
                params.put("base_fare",baseFareVal);
                params.put("tax",taxVal);
                params.put("total_fare",String.valueOf(totalFare));
                params.put("discount","0");
                params.put("tips","0");
                requestParams.setRequestParams(params);
                UserServiceHandler.payAmount(bookingId,requestParams);
                //service_id,base_fare,tax,total_fare,discount-0,tips
                // toastLong(" Feature is on progress..Action can't be done right now.");
            }
        });
          rateBuddy.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d(TAG, "onRatingChanged: "+rating);
                if (rating>0){
                    userSpecLayout.setVisibility(View.VISIBLE);
                    if (rating<=3){
                        underRateLayout.setVisibility(View.VISIBLE);
                    }else{
                        underRateLayout.setVisibility(View.GONE);
                    }
                    rateButton.setEnabled(true);
                    totalRating = (int) rating;
                }else{
                    userSpecLayout.setVisibility(View.GONE);
                    //submitBtn.setEnabled(false);
                }
            }
        });
          rateButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                setRatingDetails();
              }
          });
    }

    /**
     * remote response in payment
     * @param remoteResponse
     */
    private void handlePayResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.payment_failed);
            if (null == remoteResponse) {
                toastShort(getString(R.string.payment_failed));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {

                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        toastLong(getString(R.string.paid_success));
                        ratingLayout.startAnimation(pushUpAnimation);
                        paymentLayout.setVisibility(View.GONE);
                        ratingLayout.setVisibility(View.VISIBLE);
                    } else {
                        toastShort(getString(R.string.payment_failed));
                    }

                } catch (JSONException e) {
                    toastShort(getString(R.string.payment_failed));
                    e.getLocalizedMessage();
                }
            }
        }
    }

    /**
     * get fares
     */
    private void getFaresOnEnd() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleFaresResponse(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.getServiceFare(bookingId,requestParams);
    }

    /**
     * get fare remote response
     * @param remoteResponse
     */
    private void handleFaresResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.get_fare_error);
            if (null == remoteResponse) {
                toastShort(getString(R.string.get_fare_error));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {

                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        if (jsonObject.has("data")) {
                            Log.d(TAG, "handleFaresResponse: " + jsonObject);
                            String countryId = jsonObject.getJSONObject("data").getString("country_id");
                            String currency = CommonUtilities.getCurrencyCodeFromCountryId(countryId);
                            baseFareVal = jsonObject.getJSONObject("data").getString("base_fare");
                            baseFare.setText(currency + "" + baseFareVal);
                            taxVal = jsonObject.getJSONObject("data").getString("tax");
                            tax.setText(currency + "" + taxVal);
                            extraRateVal = jsonObject.getJSONObject("data").getString("extra_rate");
                            extraRate.setText(currency + "" + extraRateVal);
                            extraTime.setText(CommonUtilities.getHoursAndMinutes(Integer.parseInt(jsonObject.getJSONObject("data").getString("extra_time"))));
                            totalFare.setText(currency + "" + jsonObject.getJSONObject("data").getString("total_fare"));
                        }
                    } else {
                        toastShort(getString(R.string.get_fare_error));
                    }

                } catch (JSONException e) {
                    e.getLocalizedMessage();
                }
            }
        }
    }

    /**
     * set rating
     */
    private void setRatingDetails() {
        //remote call for saveRating
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
        params.put("comments",etUserExperience.getText().toString());
        requestParams.setRequestParams(params);
        UserServiceHandler.saveFeedBack(bookingId,requestParams);
    }

    /**
     * rating remote response
     * @param remoteResponse
     */
    private void handleRatingResponse(RemoteResponse remoteResponse) {
        if (isAdded()) {
            Context context = null != getContext() ? getContext() : BuddyApp.getApplicationContext();
            String customErrorMsg = context.getString(R.string.rate_fail);
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

    /**
     * get positive check ids
     * @return
     */
    private String getSelectedPositives(){
        String ratingCheckBoxText = "";
        int[] checkBoxIds = {R.id.chk1,R.id.chk2,R.id.chk3,R.id.chk4};
        for(int i=0 ; i<checkBoxIds.length;i++){
            CheckBox chk = paymentView.findViewById(checkBoxIds[i]);
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
     * get negative ids
     * @return
     */
    private String getSelectedImprovements(){
        String ratingCheckBoxText = "";
        int[] checkBoxIds = {R.id.uchk1,R.id.uchk2,R.id.uchk3,R.id.uchk4};
        for(int i=0 ; i<checkBoxIds.length;i++){
            CheckBox chk = paymentView.findViewById(checkBoxIds[i]);
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
     * rating response
     */
    private void showRatingResponse(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BuddyApp.getCurrentActivity());
        //builder.setTitle(title);
//        builder.setCustomTitle(View.inflate(getActivity(),R.layout.feedback_ack_layout,null));
        builder.setMessage("Thank You");
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.done_label), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), UserHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dialog.cancel();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
