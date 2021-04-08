package com.caringaide.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.caringaide.user.R;
import com.caringaide.user.activities.RatingActivity;
import com.caringaide.user.activities.UserHomeActivity;
import com.caringaide.user.fragments.OTPDialogFragment;
import com.caringaide.user.interfaces.RefreshListAdapterListener;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.SharedPrefsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.getDateAndTimeAsString;
import static com.caringaide.user.utils.CommonUtilities.getServerMessageCode;
import static com.caringaide.user.utils.CommonUtilities.getWrongOtpCount;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.minTipAmount;
import static com.caringaide.user.utils.CommonUtilities.notifyWithVibration;
import static com.caringaide.user.utils.CommonUtilities.ratingMap;
import static com.caringaide.user.utils.CommonUtilities.toastLong;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * Tips list
 */
public class PendingPaymentsAdapter extends ArrayAdapter<BuddyService> implements OTPDialogFragment.DialogListener {
    private Context context;
    private int resource;
    private List<BuddyService> bookingListObject;
    private ViewHolder viewHolder;
    private LayoutInflater inflater;
    private static final String TAG = "PendingPayAdapter";
    private int position;
    private Fragment parentFragment;
    private RefreshListAdapterListener listener;
    private String paymentOtpVal = null;
    private static BuddyService buddyServiceData;
    static String tipAmount = "";
    static AlertDialog tipAlertDialog;
    TextView totalAmount;TextView etAmt;
    private ScrollView tipScroll;
    //    private static String selectedTipVal ="";
    private LinearLayout otherTipsLayout;

    public PendingPaymentsAdapter(Context context, int resource, List<BuddyService> bookingObject, RefreshListAdapterListener listener,Fragment fragment) {
        super(context, resource,bookingObject);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.resource = resource;
        this.bookingListObject = bookingObject;
        this.listener = listener;
        this.parentFragment = fragment;
    }


    @Override
    public BuddyService getItem(int position) {
        return bookingListObject.get(position);
    }

    @Override
    public int getCount() {
        return bookingListObject.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View row = convertView;
        if (row == null) {

            viewHolder = new ViewHolder();
            row = inflater.inflate(resource, null);
            viewHolder.benName = (TextView) row
                    .findViewById(R.id.pendingpay_ben_name_result);
            viewHolder.buddyName = row
                    .findViewById(R.id.pendingpay_buddy_name_result);
            viewHolder.bookingId = row
                    .findViewById(R.id.pendingpay_booking_id_result);
            viewHolder.bookingStartDate = row
                    .findViewById(R.id.pendingpay_start_date_result);
            viewHolder.bookingEndDate = row
                    .findViewById(R.id.pendingpay_end_date_result);
            viewHolder.tipBuddy = row
                    .findViewById(R.id.pendingpay_btn_tip_buddy);
            viewHolder.rateBuddy = row
                    .findViewById(R.id.pendingpay_btn_rate_buddy);
            viewHolder.givenRating = row
                    .findViewById(R.id.given_rating_to_buddy);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        final BuddyService buddyService = getItem(position);
        this.position = position;
        viewHolder.bookingId.setText(buddyService.getDisplayId());
        viewHolder.benName.setText(buddyService.getBenFullName());
        viewHolder.buddyName.setText(buddyService.getBuddyName());
        viewHolder.bookingStartDate.setText(getDateAndTimeAsString(buddyService.getStartDate()));
        viewHolder.bookingEndDate.setText(CommonUtilities.getDateAndTimeAsString(buddyService.getEndDate()));
        viewHolder.buddyName.setText(buddyService.getBuddyName());
        viewHolder.tipBuddy.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openTipLayout(buddyService);
            }
        });
        if (buddyService.isRated()){
            viewHolder.givenRating.setImageResource(ratingMap.get(buddyService.getFeedbackRating()));
            viewHolder.givenRating.setVisibility(View.VISIBLE);
            viewHolder.rateBuddy.setVisibility(View.GONE);
        }else{
            viewHolder.givenRating.setVisibility(View.GONE);
            viewHolder.rateBuddy.setVisibility(View.VISIBLE);
        }
        viewHolder.rateBuddy.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //rating
                setRatingLayout(buddyService);
            }
        });

        return row;
    }
    private void setRatingLayout(BuddyService buddyService){
        Intent intent = new Intent(parentFragment.getActivity(), RatingActivity.class);
        intent.putExtra("ben_booking_id", buddyService.getId());
        intent.putExtra("buddy_name", buddyService.getBuddyName());
        intent.putExtra("buddy_gen", buddyService.getBuddyGender());
        intent.putExtra("buddy_mobile", buddyService.getBuddyMobile());
        intent.putExtra("buddy_id", buddyService.getBuddyId());
        context.startActivity(intent);
    }
    /**
     * alert dialog for giving tip amount
     * @param buddyService
     */
    private void openTipLayout(final BuddyService buddyService) {
        tipAmount ="";
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(R.layout.tip_alertdialog_layout);
        final AlertDialog tipDialog = builder.create();
        tipDialog.setCancelable(false);
        tipDialog.show();
        tipScroll = tipDialog.findViewById(R.id.tip_scrollview);
        TextView bookingId = tipDialog.findViewById(R.id.tip_booking_id_result);
        TextView buddyName = tipDialog.findViewById(R.id.tip_buddy_name_result);
        totalAmount = tipDialog.findViewById(R.id.amount_paid_result);
        TextView tvTipCurrency = tipDialog.findViewById(R.id.tip_currency);
        etAmt = tipDialog.findViewById(R.id.tip_et_amount);
        Button payTip = tipDialog.findViewById(R.id.btn_pay_tip);
        Button cancelTip = tipDialog.findViewById(R.id.btn_cancel_tip);
        totalAmount.setText(SharedPrefsManager.getInstance().getUserCurrency().concat(buddyService.getTotalAmount()));
        otherTipsLayout = tipDialog.findViewById(R.id.show_other_tip_layout);
        final Button tip25percent = tipDialog.findViewById(R.id._25_pcent_tip_value);
        getTipAmount(buddyService.getTotalAmount(),25,tip25percent);
        final Button tip20percent = tipDialog.findViewById(R.id._20_pcent_tip_value);
        getTipAmount(buddyService.getTotalAmount(),20,tip20percent);
        final Button tip15percent = tipDialog.findViewById(R.id._15_pcent_tip_value);
        getTipAmount(buddyService.getTotalAmount(),15,tip15percent);
        final Button tip10percent = tipDialog.findViewById(R.id._10_pcent_tip_value);
        getTipAmount(buddyService.getTotalAmount(),10,tip10percent);
        final Button tip5percent = tipDialog.findViewById(R.id._5_pcent_tip_value);
        getTipAmount(buddyService.getTotalAmount(),5,tip5percent);
        final Button otherTip = tipDialog.findViewById(R.id.other_tip_value);
        final Button[] tipSelectableButtonArr = {tip25percent,tip20percent,tip15percent,tip10percent,tip5percent,otherTip};
        tip25percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnClickTipBtns(tip25percent,tipSelectableButtonArr);

            }
        });tip20percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnClickTipBtns(tip20percent,tipSelectableButtonArr);

            }
        });tip15percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnClickTipBtns(tip15percent,tipSelectableButtonArr);

            }
        });tip10percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnClickTipBtns(tip10percent,tipSelectableButtonArr);

            }
        });tip5percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnClickTipBtns(tip5percent,tipSelectableButtonArr);

            }
        });otherTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOnClickTipBtns(otherTip,tipSelectableButtonArr);
                otherTipsLayout.setVisibility(View.VISIBLE);


            }
        });
        etAmt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey: which key "+keyCode);
                Log.d(TAG, "onKey: which event "+event.toString());
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    //this is enter /tick button in numpad
                    etAmt.clearFocus();
                }
                return false;
            }
        });
        bookingId.setText(buddyService.getId());
        buddyName.setText(buddyService.getBuddyName());
        tvTipCurrency.setText(SharedPrefsManager.getInstance().getUserCurrency());
        cancelTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipAmount = "0";
                tipAlertDialog = tipDialog;
                buddyServiceData = buddyService;
                payTipAmount();
//                tipDialog.dismiss();
            }
        });
        //add tip remote call
        payTip.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (tipAmount.isEmpty()) {//tip amount will be empty when 1)  nothing is selected. 2) "other" is selected
                    double otherTip = !etAmt.getText().toString().isEmpty() ?
                            Double.valueOf(etAmt.getText().toString()) : 0;
                    if (!etAmt.getText().toString().isEmpty() && otherTip > getMinimumTipAmt()) {
                        tipAmount = etAmt.getText().toString();
                        tipAlertDialog = tipDialog;
                        buddyServiceData = buddyService;
                        payTipAmount();

                    } else {
                        if (otherTipsLayout.getVisibility()==View.VISIBLE) {
                        etAmt.setError(context.getString(R.string.ask_enter_tip_amount));
                            notifyWithVibration(context);
                        }else{
                            toastShort(context.getString(R.string.ask_choose_tip));
                        }
                    }
                }else{
                        tipAlertDialog = tipDialog;
                        buddyServiceData = buddyService;
                        payTipAmount();
                }
            }
        });
    }

//    private void setOnClickTipBtns(final Button[] tipSelectableButtonArr) {

    /**
     * handle selectable amt of
     * @param selectableBtn
     * @param tipSelectableButtonArr
     */
    private void setOnClickTipBtns(Button selectableBtn, final Button[] tipSelectableButtonArr) {
        selectableBtn.setBackground(context.getResources().getDrawable(R.drawable.gradient_button));
        selectableBtn.setTextColor(context.getResources().getColor(R.color.white));
        String selectedBtnTxt =  selectableBtn.getText().toString();
        if (!selectedBtnTxt.equalsIgnoreCase(context.getString(R.string.others))) {
            tipAmount = selectedBtnTxt.substring(1);
            otherTipsLayout.setVisibility(View.GONE);
        }else{
            tipAmount ="";
            etAmt.setText("");
            otherTipsLayout.setVisibility(View.VISIBLE);
        }
        for (Button button1 : tipSelectableButtonArr) {
            if (selectableBtn != button1 ){
                button1.setBackground(context.getResources().getDrawable(R.drawable.rounded_border));
                selectableBtn.setTextColor(context.getResources().getColor(R.color.black));
            }
        }

    }

    /**
     * get each division of tip amount
     * @param totalAmt
     * @param tipPercent
     * @param btn
     * @return
     */
    private double getTipAmount(String totalAmt, int tipPercent, Button btn){
        double tipableAmt =0.0;
        if (null!= totalAmt&&!totalAmt.isEmpty()&& tipPercent>0) {
            double amt = Double.parseDouble(totalAmt);
            tipableAmt = amt * tipPercent / 100;
            tipableAmt = Math.round(tipableAmt * 100.0) / 100.0;
            if (null != btn) {
                btn.setText(SharedPrefsManager.getInstance().getUserCurrency() + tipableAmt);
            }
        }
        return tipableAmt;
    }

    /**
     * return min amount of tip when user chose other to give tip
     * @return
     */
    private int getMinimumTipAmt(){
//        int tipAmt = (int)getTipAmount(buddyServiceData.getTotalAmount(),25,null);
        return  minTipAmount;//from config
    }

    /**
     * request to pay tip
     */
    private void payTipAmount(){
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleAddTips(remoteResponse);
            }
        },getContext(),null);
        Map<String,String> params = new HashMap<>();
        params.put("booking_id",buddyServiceData.getId());
        params.put("id",buddyServiceData.getPaymentId());
        params.put("tips",tipAmount);
        if (null!=paymentOtpVal){
            Log.d(TAG, "onClick: payment");
            params.put("otp",paymentOtpVal);
        }
        requestParams.setRequestParams(params);
        UserServiceHandler.addTip(requestParams);
    }
    /**
     * remote response of add tips
     * @param remoteResponse
     */
    private void handleAddTips(RemoteResponse remoteResponse) {
        String customErrorMsg = context.getString(R.string.err_pending_fares);
        if (null == remoteResponse) {
            Log.e(TAG, "handlePendingFareResponse: " + context.getString(R.string.empty_response)
                    .concat(context.getString(R.string.err_pending_fares)));
        } else {
            if(null==remoteResponse.getResponse()){
                toastShort(context.getString(R.string.empty_response)
                        .concat(context.getString(R.string.err_pending_fares)));
            }
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(context, remoteResponse)) {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    if (jsonObject.has("message")) {
                        if (jsonObject.getString("message").equalsIgnoreCase(BuddyConstants.VERIFY_OTP)) {
                            openOtpDialog();
                        } else {
                            if (!tipAmount.equals("0")){
                                toastShort(context.getString(R.string.tip_added_success));
                            }
                            tipAlertDialog.dismiss();
                            if (!buddyServiceData.isRated()) {
                                alertAndAction(parentFragment.getActivity(), context.getString(R.string.rate_beneficiary_title), context.getString(R.string.rate_buddy_data),
                                        context.getString(R.string.ok), context.getString(R.string.not_now), new AlertAction() {
                                            @Override
                                            public void positiveAction() {

                                                setRatingLayout(buddyServiceData);
                                            }

                                            @Override
                                            public void negativeAction() {
                                                listener.onDataRefresh();
                                            }
                                        });
                            }else{
                                listener.onDataRefresh();
                            }
                        }
                    }
                }else{
                    JSONObject errorJsonObject = new JSONObject(remoteResponse.getResponse());
                    if (errorJsonObject.has("message")) {
                        if (errorJsonObject.getString("message").equalsIgnoreCase(BuddyConstants.WRONG_OTP)) {

                            if (getWrongOtpCount(context)) {
                                toastShort(getServerMessageCode(errorJsonObject.getString("message")));
                                openOtpDialog();
                            }//else is handled in method itself
                        }else{
                            toastShort(customErrorMsg);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onFinishGettingOtp(String otpVal) {
        paymentOtpVal = otpVal;
        payTipAmount();

    }

    static class ViewHolder {
        TextView benName;
        TextView bookingStartDate;
        TextView bookingEndDate;
        TextView buddyName;
        TextView bookingId;
        ImageView givenRating;
        Button tipBuddy;
        Button rateBuddy;
//        EditText etTipAmt;
//        Button submitTipamt;
//        LinearLayout tipbuddyLayout;

    }
    private void openOtpDialog() {
        OTPDialogFragment dialogFragment = new OTPDialogFragment();
        dialogFragment.setDialogListener(this);
        Bundle bundle = new Bundle();
        bundle.putBoolean("notAlertDialog", true);
        bundle.putString("alertContext", BuddyConstants.ADD_TIP_CONTEXT);
        dialogFragment.setArguments(bundle);
        FragmentTransaction ft = parentFragment.getFragmentManager().beginTransaction();
        Fragment prev = parentFragment.getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
            ft.addToBackStack(null);
            if (!prev.isVisible()) {
                dialogFragment.show(ft, "dialog");
            }
        }else{
            ft.addToBackStack(null);
            dialogFragment.show(ft, "dialog");
        }
        /*if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft, "dialog");*/
    }
}
