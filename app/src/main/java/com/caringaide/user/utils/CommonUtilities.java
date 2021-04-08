package com.caringaide.user.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.BitmapCompat;

import com.caringaide.user.R;
import com.caringaide.user.activities.LaunchActivity;
import com.caringaide.user.activities.LoginActivity;
import com.caringaide.user.activities.UserHomeActivity;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.RefreshListAdapterListener;
import com.caringaide.user.model.AvailableBuddy;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.model.Zipcodes;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteService;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.permission.PermissionCodes;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.caringaide.user.utils.BuddyConstants.CONST_SERVER_URL;
import static com.caringaide.user.utils.BuddyConstants.MOBILE_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.MOBILE_LENGTH_IND;
import static com.caringaide.user.utils.BuddyConstants.REASON_MAX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.REASON_MIN_LENGTH;


public class CommonUtilities {

    private static final String TAG = "CommonUtilities";
    private static final String AMEX = "amex";
    private static final String VISA = "visa";
    private static final String MAESTRO = "maestro";
    private static final String DISCOVER = "discover";
    private static final String MASTERCARD = "mastercard";
    private static final String JCB = "jcb";
    private static final String DINERS = "diners";
    public static final int IS_ONLINE = 1;
    public static final int serviceMaxDuration = 9;
    public static final int serviceMaxTimeinHr = 21;
    public static String USER_ID = SharedPrefsManager.getInstance().getUserID();
    public static Map<String, String> currencyMap = new HashMap<>();
    public static Map<String, String> countryAbbrvMap = new HashMap<>();
//    public static Map<String, String> zipcodeIdMap = new HashMap<>();
    public static String selectedBenId="";
    public static String selectedserviceId="";
    public static String adminContact = "+15715002433";
    public static int callLmit = 3;
    //US data
    public static String appCountryDef = "United States";
    public static String appCountryAbbrvDef = "US";
    public static String appCountryCodeDef = "+1";
    //India data
  /*  public static String appCountryDef = "India";
    public static String appCountryAbbrvDef = "IN";
    public static String appCountryCodeDef = "+91";*/
    public static int minWaitOngoingRefresh = 300;//5 mins
    public static int maxOtpVal = 3;
    public static String adminEmail="support@caringaide.com";
    public static String adminAddress="1924 Mill Site Place,\n Leesburg, VA 20176";
    public static int startTimeThreshold = 90;
    private static boolean showPopup = false;
    private static boolean openOtpDialog = false;
    public static boolean showActiveServices = false;
    public static int defaultRide = 4;
    public static int defaultSchdInterval = 15;
    public static int minServiceDuration = 3;
    public static int minTipAmount = 5;
    public static boolean isResendOtpEnabled = true;

    public static boolean isPendingOpen=false,isPeerOpen = false,isConfirmOpen = false;

    static TextView networkUnAvailableTv;
    static RelativeLayout networkAvailLayout;
    static View view;
    private static Handler handler = new Handler();
    private static Map<String, Integer> genderImageMap = new HashMap<>();
    public static Map<String, String> serviceTypeMap = new HashMap<>();
    public static Map<String, String> timeZoneMap = new HashMap<>();
    public static Map<String, Integer> mobileMinValidation = new HashMap<>();
//    public static Map<String, Integer> mobileMaxValidation = new HashMap<>();
//    public static Map<String, Integer> countryCodeMap = new HashMap<>();
    private static Map<String, String> countryIdMap = new HashMap<>();
    public static Map<String, Integer> ratingMap = new HashMap<>();
    public static Map<String, Integer> callCacheMap = new HashMap<>();
//    public static Map<String, Integer>cardImageMap = new HashMap<>();
    public static List<AvailableBuddy> allBuddiesList = new ArrayList<AvailableBuddy>();
    private static androidx.appcompat.app.AlertDialog cancelReasonDialog;
    public static ArrayList<BuddyService> serviceArrayList = new ArrayList<>();
    private static int wrongOtpCount =0;

    static {

        genderImageMap.put("M", R.drawable.maleben);
        genderImageMap.put("F", R.drawable.femaleben);
        genderImageMap.put("O", R.drawable. other);
        //getServerUrl(BuddyConstants.QA_STATUS);

        countryAbbrvMap.put("IN","+91");
        countryAbbrvMap.put("US","+1");

        currencyMap.put("+1", "\u0024");
        currencyMap.put("+91", "\u20B9");
//        currencyMap.put("+971", "Dhs");

        //country code to country id from db
        countryIdMap.put("+91","2");
        countryIdMap.put("+1","1");


        serviceTypeMap.put("1",BuddyApp.getCurrentActivity().getString(R.string.premium_label));
        serviceTypeMap.put("2",BuddyApp.getCurrentActivity().getString(R.string.basic_label));

        ratingMap.put("0",R.drawable.notrated);
        ratingMap.put("1",R.drawable.star1);
        ratingMap.put("2",R.drawable.star2);
        ratingMap.put("3",R.drawable.star3);
        ratingMap.put("4",R.drawable.star4);
        ratingMap.put("5",R.drawable.star5);

        timeZoneMap.put("United States", "America/New_York");
        timeZoneMap.put("India", "Asia/Kolkata");

        mobileMinValidation.put("+1", MOBILE_LENGTH);
        mobileMinValidation.put("+91", MOBILE_LENGTH_IND);
/*
        mobileMaxValidation.put("+1",MOBILE_MAX_LENGTH);
        mobileMaxValidation.put("+91",MOBILE_MAX_LENGTH_IND);*/

//        countryCodeMap.put("US",R.string.us_ccode);
//        countryCodeMap.put("IND",R.string.ind_ccode);

        /*cardImageMap.put("None", R.drawable.card_error);
        cardImageMap.put(AMEX, R.drawable.amex);
        cardImageMap.put(VISA, R.drawable.visa);
        cardImageMap.put(MASTERCARD, R.drawable.mastercard);
        cardImageMap.put(MAESTRO, R.drawable.maestro);
        cardImageMap.put(DISCOVER, R.drawable.discover);
        cardImageMap.put(JCB, R.drawable.jcb);
        cardImageMap.put(DINERS, R.drawable.dinersclub);*/
    }

    public static void clearCallCache(String bookingId){
        for (Map.Entry<String,Integer> entry: callCacheMap.entrySet()){
            if (entry.getKey().equals(bookingId)){
                callCacheMap.remove(bookingId);
            }

        }
    }

    public static Integer getGenderImage(String gender){
        Integer imageId = genderImageMap.get(gender.toUpperCase());
        if (null != imageId){
            return imageId;
        }else{
            return genderImageMap.get("O");
        }
    }
    public static String getCountryIdFromCountryCode(String countryCode){
        if (null!=countryCode || !countryCode.isEmpty()){
            return null==countryIdMap.get(countryCode)?"":countryIdMap.get(countryCode);
        }
        return "";
    }
    public static Typeface fontStyle(Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                "font/nunito.ttf");
        return typeface;

    }

    public static boolean isOpenOtpDialog() {
        return openOtpDialog;
    }

    public static void setOpenOtpDialog(boolean openOtpDialog) {
        CommonUtilities.openOtpDialog = openOtpDialog;
    }

    /**
     * check  whether the user is logged using shared preference data
     * @return
     */
    public static boolean isLoggedIn(){
        return SharedPrefsManager.getInstance().getUserTypeID().equals("2") && SharedPrefsManager.getInstance().getUserID().length() != 0;
    }

    public static void toastShort(String message) {

        Toast.makeText(BuddyApp.getCurrentActivity(), message, Toast.LENGTH_SHORT).show();

    }

    public static void toastLong(String message) {

        Toast.makeText(BuddyApp.getCurrentActivity(), message, Toast.LENGTH_LONG).show();
    }
    public static void moveToHomeActivity() {
        Intent intent = new Intent(BuddyApp.getCurrentActivity(), UserHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        BuddyApp.getCurrentActivity().startActivity(intent);
        BuddyApp.getCurrentActivity().overridePendingTransition(R.anim.view_flipper_right_in_fast,R.anim.animate_left_out_fast);
    }
    /**
     * get Server url
     * @return
     */
    public static String getServerUrl() {
        return BuddyApp.getApplicationContext().getString(R.string.server_url);
    }

    /**
     * get currency code from country id from the static map
     * @param countryId
     * @return
     */
    public static String getCurrencyCodeFromCountryId(String countryId){
        String currency = currencyMap.get("USD");
        for (Map.Entry<String, String> entries : countryIdMap.entrySet()){
            if (entries.getValue().equalsIgnoreCase(countryId)){
                currency = currencyMap.get(entries.getKey());
            }
        }
        return currency;
    }

    /**
     * get currency code from the mobile country code
     * @param mobile
     * @return
     */
    public static String getCurrencyCodeFromMobile(String mobile){
//		String
        String cCode = currencyMap.get("+1");
        for (Map.Entry<String, String> entrySet : currencyMap.entrySet()){
            if (mobile.startsWith(entrySet.getKey())){
                return entrySet.getValue();
            }
        }
        return cCode;
    }
    /**
     * get given date in yyyy-MM-dd HH:mm:ss format
     *
     * @param date
     * @return
     */
/*	public static String getDateAndTimeAsString(String date) {
		String newDate = "";
		if (null != date && !date.equals("null")) {
            if (date.contains("T")) {
                newDate = date.replace("T", " ").substring(0, 19);
            } else {
                newDate = date.substring(0, 19);
            }
        }
		return newDate;
	}*/

    /**
     * display date and time in 12hr format
     * @param date
     * @return
     */
    public static String getDateAndTimeAsString(String date){
        String newDate = "";
        if (null != date && !date.isEmpty()) {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
            DateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa",Locale.getDefault());
            Date date1= null;
            try {
//                date = date.replace("T", " ").substring(0, 16);
                date = date.replace("T", " ");
                date1 = inputFormat.parse(date);

                newDate = outputFormat.format(date1);
                /*if (date.contains("T")) {
                    newDate = date.replace("T", " ").substring(0, 19);
                } else {
                    newDate = date.substring(0, 19);
                }*/
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return newDate;
    }
    public static String convertTimeTo12Hr(String time){
        String newDate = "";
        if (null != time && !time.isEmpty()) {
            DateFormat inputFormat = new SimpleDateFormat("HH:mm",Locale.getDefault());
            DateFormat outputFormat = new SimpleDateFormat("hh:mm aa",Locale.getDefault());
            Date date1= null;
            try {
                date1 = inputFormat.parse(time);

                newDate = outputFormat.format(date1);
            } catch (ParseException e) {
                Log.e(TAG, "convertTimeTo12Hr: Error in time. Is that in proper format",e.getCause() );
//                e.printStackTrace();
            }
        }
        return newDate;
    }
    public static String convertTimeTo24Hr(String time){
        String newDate = "";
        if (null != time && !time.isEmpty()) {
            DateFormat inputFormat = new SimpleDateFormat("hh:mm aa",Locale.getDefault());
            DateFormat outputFormat = new SimpleDateFormat("HH:mm",Locale.getDefault());
            Date date1= null;
            try {
                date1 = inputFormat.parse(time);

                newDate = outputFormat.format(date1);
            } catch (ParseException e) {
                Log.e(TAG, "convertTimeTo24Hr: Error in time. Is that is proper format" );
//                e.printStackTrace();
            }
        }
        return newDate;
    }
    public static String getDateAndTimeIn24hrFormat(String date){
        String newDate = "";
        if (null != date && !date.trim().isEmpty()) {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a",Locale.getDefault());
            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
            Date date1= null;
            try {
//                date = date.replace("T", " ").substring(0, 16);
                date = date.replace("T", " ");
                date1 = inputFormat.parse(date);

                newDate = outputFormat.format(date1);
                /*if (date.contains("T")) {
                    newDate = date.replace("T", " ").substring(0, 19);
                } else {
                    newDate = date.substring(0, 19);
                }*/
            } catch (ParseException e) {
                toastShort(BuddyApp.getApplicationContext().getString(R.string.error_date_time));
                Log.e(TAG, "getDateAndTimeIn24hrFormat: Error in date time. Is that is proper format" );
//                e.printStackTrace();
            }
        }
        return newDate;
    }

    /**
     * get date in MM/dd/yyyy format
     * @param date
     * @return
     */
    public static String getDateAsString(String date){
        String newDate = "";
        if (null != date && !date.isEmpty()) {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            DateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy",Locale.getDefault());
            Date date1= null;
            try {
                date = date.replace("T", " ").substring(0, 10);
                date1 = inputFormat.parse(date);
                newDate = outputFormat.format(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return newDate;
    }

    /**
     * get date in yyyy-MM-dd format
     * @param date input date which is in US format
     * @return
     */
    public static String getDateInServerFormatString(String date){
        String newDate = "";
        if (null != date && !date.isEmpty()) {
            DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy",Locale.getDefault());
            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            Date date1= null;
            try {
                date = date.replace("T", " ").substring(0, 10);
                date1 = inputFormat.parse(date);

                newDate = outputFormat.format(date1);
                /*if (date.contains("T")) {
                    newDate = date.replace("T", " ").substring(0, 19);
                } else {
                    newDate = date.substring(0, 19);
                }*/
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return newDate;
    }

    /**
     * get hour added date
     * @param date in yyyy-MM-dd HH:mm format
     * @param duration in hours
     * @return resultant date with an added hour
     */
    public static String getDayAfterGivenHour(String date, int duration){
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(convertStringToDate(date)); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, duration); // adds total duration hour
        String resultingDate = convertDateToString(cal.getTime());
        Log.d(TAG, "getDayAfterGivenHour: "+resultingDate);
        return resultingDate;
    }


    public static Date convertStringToDate(String date){
        Date newDate= null;
        DateFormat inputFormat;
        if (null != date && !date.isEmpty()) {
            if (date.contains("T"))
                inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.getDefault());
            else if (date.contains("/"))
                inputFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa",Locale.getDefault());
            else
                inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());

            try {
//				date = date.replace("T", " ").substring(0, 16);
                newDate = inputFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return newDate;
    }

    /**
     * convert date to string format
     * @param date date as Date object
     * @return string value of date  in yyyy-MM-dd HH:mm:ss format
     */
    public static String convertDateToString(Date date){
        String newDate= null;
        if (null != date) {
            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
            newDate = outputFormat.format(date);
            Log.d(TAG, "convertDateToString: resultant date "+newDate);

        }
        return newDate;
    }
    /**
     * separate country code and mobile number from the mobile string
     * @param mobile
     * @return
     */
    public static String[] getCountryCodeFromMobile(String mobile){
        String[] cntryCode = new String[2];
        if (null!= mobile && !mobile.isEmpty()){
            if (mobile.startsWith("+91")){
                cntryCode[0] = mobile.substring(0,3);
                cntryCode[1] = mobile.substring(3);
            }else if (mobile.startsWith("+1")){
                cntryCode[0] = mobile.substring(0,2);
                cntryCode[1] = mobile.substring(2);
            }else{
                cntryCode[0] = mobile.substring(0,2);
                cntryCode[1] = mobile.substring(2);
            }
        }
        return cntryCode;
    }
    /**
     * get registration id
     *
     * @param context
     * @return
     */
    public static String getRegistrationId(Context context) {
        String registrationId = SharedPrefsManager.getInstance().getDeviceToken();
        if (null == registrationId || registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found. Registering with FireBase");
            FirebaseApp.initializeApp(context);
            registrationId = FirebaseInstanceId.getInstance().getToken();
//			Log.d(TAG, "Firebase token is " + registrationId);
            SharedPrefsManager.getInstance().storeDeviceToken(registrationId);
        }
//		Log.d(TAG, "Firebase token is " + registrationId);
        return registrationId;
    }

    /**
     * show alert for entering reason for cancelling a request.
     * @param context
     * @param buddySerList
     * @param position
     */
    public static void showReasonAlert(final ArrayAdapter context, final ArrayList<BuddyService> buddySerList,
                                       final int position, final RefreshListAdapterListener listener) {
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(BuddyApp.getCurrentActivity());
        builder.setCustomTitle(View.inflate(BuddyApp.getCurrentActivity(),R.layout.custom_header_layout_white,null));
        builder.setView(R.layout.cancel_reason_layout);
        cancelReasonDialog = builder.create();
        cancelReasonDialog.setCancelable(false);
        cancelReasonDialog.show();
        final EditText reasonForCancelling = cancelReasonDialog.findViewById(R.id.reason_cancel);
        Button proceedReqBtn=cancelReasonDialog.findViewById(R.id.proceed_cancel_req);
        Button cancelReqBtn = cancelReasonDialog.findViewById(R.id.cancel_op_req);
        proceedReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCancelReasonValid(reasonForCancelling)) {
                    cancelRequests(reasonForCancelling.getText().toString(), context, buddySerList, position, listener);
                }
            }
        });
        cancelReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelReasonDialog.dismiss();
            }
        });
    }

    /**
     * validation method for cancel reason
     * @param editText
     * @return
     */
    public static boolean isCancelReasonValid(EditText editText){
        String reason = editText.getText().toString().trim();
        String resonMsg = BuddyApp.getCurrentActivity().getString(R.string.ask_reason)
                .concat(String.valueOf(REASON_MIN_LENGTH))
                .concat(BuddyApp.getCurrentActivity().getString(R.string.to))
                .concat(String.valueOf(REASON_MAX_LENGTH))
                .concat(BuddyApp.getCurrentActivity().getString(R.string.charecters_allowed));
        if (reason.isEmpty()){
            editText.setError(resonMsg);
        }else if (reason.length()<REASON_MIN_LENGTH || reason.length()>REASON_MAX_LENGTH){
            editText.setError(BuddyApp.getCurrentActivity().getString(R.string.ask_reason_length)
                    .concat(String.valueOf(REASON_MIN_LENGTH))
                    .concat(BuddyApp.getCurrentActivity().getString(R.string.and_label))
                    .concat(String.valueOf(REASON_MAX_LENGTH)));
        }else{
            return true;
        }
        return false;
    }
    /**
     * remote call to cancel a request.
     * @param reason
     * @param context
     * @param buddySerList
     * @param position
     * @param listener
     */
    private static void cancelRequests(String reason, final ArrayAdapter context, final ArrayList<BuddyService> buddySerList, final int position, final RefreshListAdapterListener listener) {
        cancelReasonDialog.dismiss();
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                cancelConfirmReqResponse(remoteresponse,context,buddySerList,position,listener);
                Log.d(TAG, "onResponse: "+remoteresponse.getResponse());
            }
        },BuddyApp.getCurrentActivity(),null);
        Map<String,String> params = new HashMap<>();
        params.put("cancel_message",reason);
        requestParams.setRequestParams(params);
        UserServiceHandler.changeBookingStatus(buddySerList.get(position).getId(),BuddyApp.getCurrentActivity().getString(R.string.data_cancel),requestParams);
    }
    /**
     * remote response of cancel call
     * @param remoteResponse
     * @param context
     * @param buddyserviceList
     * @param position
     * @param listener
     */
    private static void cancelConfirmReqResponse(RemoteResponse remoteResponse, ArrayAdapter context,
                                                 ArrayList<BuddyService> buddyserviceList, int position,
                                                 RefreshListAdapterListener listener) {
        String customErrorMsg = BuddyApp.getCurrentActivity().getString(R.string.cancel_request_error);
        if(null == remoteResponse){
            toastShort(BuddyApp.getCurrentActivity().getString(R.string.cancel_request_error));
        }else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);

            try {
                if (!isErrorsFromResponse(BuddyApp.getCurrentActivity(), remoteResponse)) {
                    String response = remoteResponse.getResponse();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("data")) {
                        JSONObject responseObject = jsonObject.getJSONObject("data");
                        Log.d(TAG, "cancelResponse: " + responseObject);
                        buddyserviceList.remove(position);
                        context.notifyDataSetChanged();
                        listener.onDataRefresh();
                        if (buddyserviceList.size()==0){
                            alertAndAction(BuddyApp.getCurrentActivity(), BuddyApp.getCurrentActivity().getString(R.string.ask_cancel_title),
                                    BuddyApp.getCurrentActivity().getString(R.string.cancel_done), BuddyApp.getCurrentActivity().getString(R.string.ok),
                                    null, new AlertAction() {
                                        @Override
                                        public void positiveAction() {
                                            moveToHomeActivity();
                                        }

                                        @Override
                                        public void negativeAction() {

                                        }
                                    });
                        }else{
                            showAlertMsg(BuddyApp.getCurrentActivity().getString(R.string.ask_cancel_title),
                                    BuddyApp.getCurrentActivity().getString(R.string.cancel_done));
                        }

                    } else {
                        toastShort(BuddyApp.getCurrentActivity().getString(R.string.cancel_request_error));
                    }
                }
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }
    /**
     * logout from the app
     */
    public static void logout(){
        SharedPrefsManager.getInstance().clearSharedPreferences();
        Intent intent = new Intent(BuddyApp.getCurrentActivity(), LaunchActivity.class);
        BuddyApp.getCurrentActivity().startActivity(intent);
    }
    /**
     * return circled bitmap image
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        // Bitmap returnBitmap = bitmap;
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
                TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        return circleBitmap;
    }


    public static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            float x = (source.getWidth() - size) / 2;
            float y = (source.getHeight() - size) / 2;

            // Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size,
            // size);

            Bitmap squaredBitmap = Bitmap.createBitmap(source, (int) x,
                    (int) y, size, size);

            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    TileMode.CLAMP, TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    /**
     * will alert the message and use ok to cancel
     *
     * @param title
     * @param message
     */
    public static void showAlertMsg(String title,
                                    String message) {
        showAlertMsg(title, message, "Ok");
    }

    /**
     * alert message and set the button of your choice
     *
     * @param title
     * @param message
     * @param positiveButton
     */
    public static void showAlertMsg(String title,
                                    String message, final String positiveButton) {

        AlertDialog.Builder builder = new AlertDialog.Builder(BuddyApp.getCurrentActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    public static void startInternetCheckTimer(final RequestParams reqParams) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                UserServiceHandler.handShake(reqParams);
                handler.postDelayed(this, 5 * 1000);
                Log.d(TAG, "checking is online every 5 secs");
            }
        });

    }

    public static boolean checkInternet(RequestParams requestParams) {
        String url = CONST_SERVER_URL + "bookings/isOnline/.json";
        Log.d(TAG, "checking if internet present" + url);
        RemoteService remoteService = RemoteService.getInstance(requestParams.getContext());
        remoteService.doGet(url, requestParams);
        return false;
    }

    /**
     * remote response of checking whether the user is online
     * @param remoteresponse
     */
    private static void handleIsOnlineResponse(RemoteResponse remoteresponse) {
        if (null != remoteresponse && remoteresponse.isError()) {
            showNoInternetSnackbar();
        } else {
            handler.removeCallbacksAndMessages(null);
            showActionSnackbar(BuddyApp.getCurrentActivity().getString(R.string.back_online),
                    BuddyApp.getCurrentActivity().getString(R.string.go), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPopup = false;
                            Intent intent = new Intent(BuddyApp.getCurrentActivity(),LaunchActivity.class);
                            BuddyApp.getCurrentActivity().startActivity(intent);
                        }
                    });
        }

    }

    /**
     * show snackbar for no internet
     */
    private static void showNoInternetSnackbar() {
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) (BuddyApp.getCurrentActivity().getWindow().getDecorView().findViewById(android.R.id.content))).getChildAt(0);
        view = viewGroup.getRootView();
        Snackbar.make(view, BuddyApp.getCurrentActivity().getString(R.string.no_internet)
                , Snackbar.LENGTH_LONG)
                .setAction("", null).show();
    }


    /**
     * shows snackbar for different actions
     *
     * @param message
     * @param btnName
     * @param listener
     */
    public static void showActionSnackbar(String message, String btnName, View.OnClickListener listener) {
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) (BuddyApp.getCurrentActivity().getWindow().getDecorView().findViewById(android.R.id.content))).getChildAt(0);
        view = viewGroup.getRootView();
        Snackbar.make(view, message
                , Snackbar.LENGTH_LONG)
                .setAction(btnName, listener).show();
    }

    /**
     * open android's settings intent
     */
    public static void openSettingsView() {
        Context context = BuddyApp.getCurrentActivity();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * show an one-time laert message
     * @param title
     */
    public static void showAlertMsgInternetConnectivity(String title) {
        Activity activity = BuddyApp.getCurrentActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(BuddyApp.getCurrentActivity().getString(R.string.no_internet));
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        if(activity.getWindow().getDecorView().getRootView().isShown()) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            } else {
                showPopup = true;
                alertDialog.show();
            }
        }
    }

    /**
     * makes first letter of the string to uppercase
     * @param line
     * @return
     */
    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
    /**
     * makes first letter of the string to uppercase
     * @param line
     * @return
     */
    public static String setToCamelCase(final String line) {
        String outputStr = "";
        if (null!=line && !line.isEmpty()) {
            String[] words = line.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (!words[i].isEmpty()) {
                    outputStr = outputStr.concat(Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1) + " ");
                }
            }
        }
        return outputStr;
    }

    /**
     * hide key pad
     */
    public static void hideKeyPad() {
        View view = BuddyApp.getCurrentActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) BuddyApp.getCurrentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    /**
     * show key pad
     */
    public static void showKeyPad() {
        View view = BuddyApp.getCurrentActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) BuddyApp.getCurrentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * show progress dialog for a time consuming remote call
     * @param message
     * @return
     */
/*	public static ProgressDialog showProgressDialog(String message) {
		ProgressDialog progressDialog = new ProgressDialog(BuddyApp.getCurrentActivity());
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		progressDialog.show();
		return progressDialog;

	}*/


    /**
     * return byte array for the image.
     *
     * @param context
     * @param id
     * @return
     */
    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
        return baos.toByteArray();
    }

    /**
     * returns byte array for the drawable
     *
     * @param context
     * @param drawable
     * @return
     */
    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
        return baos.toByteArray();
    }

    /**
     * convert bitmap to string for uploading it to the server
     *
     * @param bitmap
     * @return
     */
    public static String getStringFromImage(Bitmap bitmap) {
        String encodedImage = "";
        if (null == bitmap) {
            Log.e(TAG, "bitmap is null");
        } else {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
                byte[] imageInBytes = byteArrayOutputStream.toByteArray();
                encodedImage = Base64.encodeToString(imageInBytes, Base64.DEFAULT);
                byteArrayOutputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "error converting img");
                e.printStackTrace();
            }
        }
        return encodedImage;

    }

    /**
     * convert string to bitmap
     * @param encodedString the image string
     * @param width width of the view
     * @param height height of the view
     * @return image in bitmap
     */
    public static Bitmap getImageFromString(String encodedString,int width, int height) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //bitmap byte array is decoded for getting actual size of bitmap.
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length,options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
            //bitmap is decoded with the sampled size and the resultant bitmap will have a reduced size in memory.
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length,options);
            int bitmapByteCount= BitmapCompat.getAllocationByteCount(bitmap);
            Log.d(TAG, "getImageFromString:\n--------------\nAfter sampling image\n\t #bitmap size "+bitmapByteCount+"\n\t #width & height "
                    +bitmap.getWidth()+" "+bitmap.getHeight()+"\n--------------------------");
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
//		Uri uri = Uri.
    }

    /**
     * calculate the sampleSize for an image inorder to reduce the image size
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return sampled value
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        String imageType = options.outMimeType;
        Log.d(TAG, "calculateInSampleSize: "+height+" width "+width+" imdType "+imageType);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    /**
     * shows a progress dialog
     * @param activity
     * @param message
     * @return
     */
    public static ProgressDialog showProgressDialog(Activity activity,String message){
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;

    }
    /**
     * checks if the response from server has any errors and displays the custom message in the app.
     *
     * @param context
     * @param remoteResponse
     * @return
     * @throws JSONException
     */
    public static boolean isErrorsFromResponse(final Context context, RemoteResponse remoteResponse) throws JSONException {
        boolean isError = false;
        String networkError = remoteResponse.getErrorMessage();
        String userShownMessage = "";
        if (remoteResponse.isError()) {
            if (!(context instanceof Service)) {
                if (!showPopup && networkError.equals(context.getString(R.string.connect_error))) {
                    showAlertMsgInternetConnectivity(context.getString(R.string.app_name));
//					showPopup = true;
                    RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                        @Override
                        public void onResponse(RemoteResponse remoteresponse) {
                            switch (remoteresponse.getRequestType()) {
                                case IS_ONLINE:
                                    handleIsOnlineResponse(remoteresponse);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }, context, IS_ONLINE);
                    requestParams.setFragment(remoteResponse.getFragment());
                    startInternetCheckTimer(requestParams);
                }
            }
            if (null != remoteResponse.getErrorMessage() && !remoteResponse.getErrorMessage().isEmpty()) {
                Log.e(TAG, remoteResponse.getErrorMessage());
                //Log.e(TAG, remoteResponse.getError().getLocalizedMessage());
                isError = true;
            }
        }
        //now handle the error message from the server action
        String response = remoteResponse.getResponse();
        if (null == response) {
            isError = true;
            throw new JSONException("The response is null");
        }else{
            JSONObject error = new JSONObject(response);
            if (error.has("Error")) {
                if (error.getString("Error").equalsIgnoreCase("true")) {
                    String respMessage = error.getString("message");
                    Log.e("error", "true; Message: " + respMessage);
                    if (null != respMessage) {
                        userShownMessage = getServerMessageCode(respMessage);
//						showAlertMsg("",userShownMessage);
                    }
                    Log.d(TAG, respMessage);
                    isError = true;
                }
            }
            if (error.has("cause")) {
                String causeMsg = "";
                JSONObject cause = error.getJSONObject("cause");
                Iterator<String> iter = cause.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    causeMsg = key + " : " + getServerMessageCode(cause.getString(key));
                }
                userShownMessage = userShownMessage.concat(" ").concat(causeMsg);
                toastLong(getServerMessageCode(causeMsg));
            }
			/*}else{
				showAlertMsg(context.getString(R.string.error_label),
						context.getString(R.string.error_label));
			}*/
        }

       /* JSONObject error = new JSONObject(response);
        if (error.has("Error")) {
            if (error.getString("Error").equalsIgnoreCase("true")) {
                Log.e("error", "true");
                String respMessage = error.getString("message");
                if (null != respMessage) {
                    if (error.has("cause")){
                        String causeMsg = "";
                        JSONObject cause = error.getJSONObject("cause");
                        Iterator<String> iter = cause.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            causeMsg = key + " : " + getServerMessageCode(cause.getString(key));
                        }
//                toastLong(context,getServerMessageCode(context,causeMsg));
                        userShownMessage = userShownMessage.concat(" ").concat(causeMsg);
                    }else{

                        userShownMessage = getServerMessageCode(respMessage);
                    }
                }
                Log.d(TAG, respMessage);
                isError = true;
            }
        }*/
        if (isError) {
            Log.e(TAG, "isErrorsFromResponse: "+ userShownMessage);
//			toastLong(userShownMessage );

        }
        return isError;
    }


    /**
     * if the message has a code in the response and if there is a matching pattern locally
     * then you can show it on screen.
     *
     * @param message
     * @return
     */
    public static String getServerMessageCode(String message) {
        String userShownMessage = "";
        BuddyConstants.ServerMsgCodes[] serverMsgCodes = BuddyConstants.ServerMsgCodes.values();
        for (BuddyConstants.ServerMsgCodes msgCode : serverMsgCodes) {
            if (msgCode.name().equals(message)) {
                int intMsgCode = msgCode.getIntegerCode();
                Log.d(TAG, "messagecode" + intMsgCode);
                userShownMessage = BuddyApp.getCurrentActivity().getString(intMsgCode);
            }
        }
        if (userShownMessage.isEmpty()){
            userShownMessage = message;
        }
        return userShownMessage;
    }

    /**
     * get permission messages to show in snackbar
     * @param permissionCode
     * @return
     */
    public static String getPermissionMessage(int permissionCode){
        String userShownMessage = "";
        PermissionCodes[] permissionCodes = PermissionCodes.values();
        for (PermissionCodes msgCode : permissionCodes) {
            if (msgCode.getPermissionCode()== permissionCode) {
                int intMsgCode = msgCode.getMessageCode();
                userShownMessage = BuddyApp.getCurrentActivity().getString(intMsgCode);
            }
        }
        return userShownMessage;
    }
    /**
     *  alert message with custom action
     * @param activity
     * @param title
     * @param message
     * @param ok
     * @param cancel
     * @param action
     */
    public static void alertAndAction(final Activity activity, String title, String message,
                                      final String ok,final String cancel,
                                      final AlertAction action) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
//		builder.setCustomTitle(View.inflate(activity,R.layout.custom_header_layout_white,null));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                action.positiveAction();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                action.negativeAction();
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
    /**
     *  alert message having appname as title with custom action
     * @param activity
     * @param message
     * @param ok
     * @param cancel
     * @param action
     */
    public static void alertAndAction(final Activity activity, String message,
                                      final String ok,final String cancel,
                                      final AlertAction action) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCustomTitle(View.inflate(activity,R.layout.custom_header_layout_white,null));
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                action.positiveAction();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                action.negativeAction();
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public static Double getMilesForMeters(int distInMetres) {
        Double distInMiles = distInMetres * BuddyConstants.ONE_METRE_MILE;
        DecimalFormat df = new DecimalFormat(".##");
        df.setRoundingMode(RoundingMode.UP);
        distInMiles = Double.valueOf(df.format(distInMiles));
        return distInMiles;
    }

    public static double distanceCalc(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515; // miles
        if (unit == "K") {
            dist = dist * 1.609344; // kilo
        } else if (unit == "N") {
            dist = dist * 0.8684; //nautical miles
        }

        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    public static String getHoursAndMinutesFromSecond(Integer seconds) {
        int hours = seconds / 3600;
        int remainder = seconds - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;
        if (hours > 1) {
            return hours + " hrs " + mins + " mins";
        } else if (hours == 1) {
            return hours + " hrs " + mins + " mins";
        } else {
            return mins + " mins"; // no hours
        }

    }

    public static String getHoursAndMinutes(Integer minutes) {
        int mins = 0;
        int hours = minutes / 60;
        int remainder = minutes % 60;
        // int mins = remainder / 60;
        //remainder = remainder - mins * 60;
        //int secs = remainder;
        if (hours > 0) {
            return hours + " hrs " + remainder + " mins";
        } else {
            return remainder + " mins"; // no hours
        }

    }

    /**
     * get current date in "yyyy-MM-dd HH:mm:ss a"  string format
     *
     * @return
     */
    public static String getCurrentDateTimeAsString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", Locale.getDefault());
        String date = dateFormat.format(new Date());
        return date;
    }

    public static long diffTwoDatesInMinutes(String start,String end){
        Date startDate = convertStringToDate(start);// Set start date
        Date endDate  = convertStringToDate(end);// Set end date
        long duration  = endDate.getTime() - startDate.getTime();
//        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
//        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
//        long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
        return diffInMinutes>=0?diffInMinutes:0;
    }

    /**
     * check if a year is withing the range
     *
     * @param year
     * @param before
     * @return
     */
    public static boolean checkYearWithinRange(String year, int before) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        Date date = cal.getTime();
        int currentCalYear = cal.get(Calendar.YEAR);
        if (null != year && !year.isEmpty()) {
            //check if year within range
            int receivedYear = Integer.parseInt(year);
            if (receivedYear <= currentCalYear && receivedYear >= (currentCalYear - before)) {
                return true;
            }
        }
        return false;
    }

    /**
     * parse datetime string to timestamp
     *
     * @param date
     * @return
     */
    public static Long getTimeStampFromStringDate(String date) {
        long dateTimeStamp = 0l;
        Log.d(TAG, "DATE for conversion is  " + date);
        SimpleDateFormat dateFormat = null;
        if (date.contains("T")) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
        }
        try {
            Date dateTime = dateFormat.parse(date);
            dateTimeStamp = dateTime.getTime();
        } catch (ParseException e) {
            Log.d(TAG, "Exception while parsing the date so returning 0l ");
            e.printStackTrace();
        }
        return dateTimeStamp;
    }
//	public static String getDateFormat(String date) {
//		String formatedDate = "";
//		if (date.contains("+")) {
//			formatedDate = date.substring(0, 19);
//		}
//		return formatedDate;
//	}

    public static String getDateInAppFormat(String date) {
        if (null == date || date.isEmpty()) {
            return "";
        }
        //"dob": "1997-07-31" 1993-07-20
        //the space is added to handle dates that comes without time.
        // So the substring operation works without array out of bound exception
        String token = (date.contains("/") ? "/" : "-");
        String dobString = date.substring(0, 10);
        String[] dateOfBirth = dobString.split(token);
        String bYear = dateOfBirth[0];
        String bDay = dateOfBirth[2];
        String bMonth = dateOfBirth[1];
        String formattedDate = bMonth + "-" + bDay + "-" + bYear;
        Log.d(TAG, "formatted date " + formattedDate);
        return formattedDate;

    }


    /**
     * split and return latlng string
     *
     * @param latLng
     * @return
     */
    public static String[] getLatAndLng(String latLng) {
        String[] latlong = latLng.split(",");
        String startLat = latlong[0];
        String startLong = latlong[1];
        return new String[]{startLat, startLong};
    }

    //unused
    public static String splitContactName(String contact) {
        String contact_name = contact.replaceAll("[\\s0-9]{10,15}", "");
        return contact_name;
    }

    //unused
    public static String splitContactNumber(String contact) {
        String contatc_num = contact.replaceAll("[a-zA-Z]", "");

        return contatc_num;
    }

    /**
     * animate the hint above the edit text (Settings)
     *
     * @param context
     * @param editText
     * @param targetView
     * @return
     */
    public static TextWatcher getTextWatcher(final Context context, final EditText editText, final TextView targetView) {
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText.getText().length() == 0) {
                    Animation fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
                    Animation editFadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                    fadeOut.setDuration(300);
                    editFadeIn.setDuration(1200);
                    targetView.startAnimation(fadeOut);
                    targetView.setVisibility(View.GONE);
                    editText.startAnimation(editFadeIn);
                } else {
                    //do nothing
                    targetView.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

	/*public static boolean checkValidDob(String userTypeId,String dob){
		boolean valid=true;String eDob = "";
		int minAge = 0,maxAge =0;
		if (null == dob || dob.isEmpty()){
			valid =false;
		}else {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
				if (dob.contains("-")) {
                    eDob = dob.replace("-", "/");
                }else{
					eDob = dob;
				}
				Date userDOB = sdf.parse(eDob);
				Calendar now = Calendar.getInstance();
				Calendar cDob = Calendar.getInstance();
				cDob.setTime(userDOB);
				int cYear = now.get(Calendar.YEAR);
				int cMonth = now.get(Calendar.MONTH);
				int cDay = now.get(Calendar.DAY_OF_MONTH);

				int sYear = cDob.get(Calendar.YEAR);
				int sMonth = cDob.get(Calendar.MONTH);
				int sDay = cDob.get(Calendar.DAY_OF_MONTH);
				if (userTypeId.equals("3")){
					minAge = Integer.parseInt(ConfigUtils.getConfigValue(ConfigUtils.DRIVER_MIN_AGE));
					maxAge = Integer.parseInt(ConfigUtils.getConfigValue(ConfigUtils.DRIVER_MAX_AGE));
				} else if (userTypeId.equals("2")){
					minAge = Integer.parseInt(ConfigUtils.getConfigValue(ConfigUtils.USER_MIN_AGE));
					maxAge = Integer.parseInt(ConfigUtils.getConfigValue(ConfigUtils.USER_MAX_AGE));
				}

				if ((cYear - sYear) < minAge || cYear - sYear > maxAge) {
					valid = false;
					Log.e(TAG, "not eligible to drive");
				} else if ((cYear - sYear) == minAge && (cMonth < sMonth)) {
                    valid = false;
                    Log.e(TAG, "not eligible to drive");
                } else if ((cYear - sYear) == minAge && (cMonth == sMonth) && (cDay < sDay)) {
                    valid = false;
                    Log.e(TAG, "not eligible to drive");
                } else {
                    Log.e(TAG, "You are eligible to drive");

                }

			} catch (Exception e) {
				valid = false;
			}
		}
		return valid;
	}*/



    /**
     * parse google road snapshot data.
     *
     * @param locationJson
     * @return
     */
    public static List<Location> parseRoadLocations(String locationJson) throws Exception {

        try {
            if (null == locationJson || locationJson.isEmpty()) {
                throw new Exception("road location string is null or empty");
            }
            List<Location> locations = new ArrayList<>();
            JSONObject jsonObj = new JSONObject(locationJson);
            if (jsonObj.length() > 0) {
                JSONArray jsonArray = jsonObj.getJSONArray("snappedPoints");
                if (jsonArray.isNull(0) || jsonArray.length() > 0) {
                    return locations;
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject locJsonObj = jsonArray.getJSONObject(i);
                        JSONObject locObj = locJsonObj.getJSONObject("location");
                        String latitude = locObj.getString("latitude");
                        String longitude = locObj.getString("longitude");
                        Location loc = new Location(LocationManager.GPS_PROVIDER);
                        loc.setLatitude(Double.valueOf(latitude));
                        loc.setLongitude(Double.valueOf(longitude));
                        locations.add(loc);
                    }
                }
            }
            return locations;
        } catch (Exception ex) {
            Log.d(TAG, ex.getLocalizedMessage());
            throw ex;
        }

    }

    /**
     * convert duration to given units
     *
     * @param duration
     * @param unit
     * @return
     */
    public static Long getTimeInMillis(long duration, String unit) {
        long convertedTime = 0l;
        switch (unit) {
            case "days":
                convertedTime = duration * 24 * 60 * 60 * 1000;
                break;
            case "hours":
                convertedTime = duration * 60 * 60 * 1000;
                break;
            case "seconds":
                convertedTime = duration * 1000;
                break;
            case "minutes":
                convertedTime = duration * 60 * 1000;
                break;
            case "milli":
                convertedTime = duration;
                break;
            default:
                break;
        }
        return convertedTime;
    }
    /**
     * convert given date to milliseconds
     * @param date
     * @return
     */
    public static Long getTimeInMillis(String date) {
        long timeInMilliseconds = 0;
        if (null!=date && !date.isEmpty()) {
            SimpleDateFormat dateFormat = null;
            if (date.contains("T")) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            } else {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", Locale.getDefault());
            }

            try {
                Date mDate = dateFormat.parse(date);
                timeInMilliseconds = mDate.getTime();
                Log.d(TAG, "getTimeInMillis: " + timeInMilliseconds);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return timeInMilliseconds;
    }

    /**
     * check for a string, if it is null or empty
     *
     * @param givenString
     * @return
     */
    public static boolean checkNullOrEmptyForString(String givenString) {
        return null != givenString && !givenString.isEmpty();
    }

    /**
     * mask confidential given string and replace it with some *
     * @param givenString
     * @return
     */
    public static String getMaskedString(String givenString){
        StringBuilder newString = new StringBuilder(givenString.length());
        if (checkNullOrEmptyForString(givenString)){
            for (int i=0; i< givenString.length()-4; i++){
                newString.append("*");
            }
            newString.append(givenString.substring(givenString.length() - 4));
        }
        return newString.toString();
    }

  /*  public static boolean checkServiceIsAvailable(Context context,String service){
        if (null == ACTIVE_SERVICES_MAP || ACTIVE_SERVICES_MAP.isEmpty()) {
            ACTIVE_SERVICES_MAP = SharedPrefsManager.getInstance(context).getAvailableServices();
        }
        if (ACTIVE_SERVICES_MAP.containsKey(service)){
            return true;
        }
        return false;
    }*/

    public static int calculateAge(String birthDate)
    {
        String age = "";
        int years = 0;
        int months = 0;
        int days = 0;
        if (null!=birthDate && !birthDate.isEmpty()) {
//        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy",Locale.getDefault());
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy", Locale.getDefault());
            try {
                Date dateTime = dateFormat.parse(birthDate);

                //create calendar object for birth day
                Calendar birthDay = Calendar.getInstance();
                birthDay.setTimeInMillis(dateTime.getTime());

                //create calendar object for current day
                long currentTime = System.currentTimeMillis();
                Calendar now = Calendar.getInstance();
                now.setTimeInMillis(currentTime);

                //Get difference between years
                years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
                int currMonth = now.get(Calendar.MONTH) + 1;
                int birthMonth = birthDay.get(Calendar.MONTH) + 1;

                //Get difference between months
                months = currMonth - birthMonth;

                //if month difference is in negative then reduce years by one
                //and calculate the number of months.
                if (months < 0) {
                    years--;
                    months = 12 - birthMonth + currMonth;
                    if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                        months--;
                } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                    years--;
                    months = 11;
                }

                //day calculation not done
                else {
                    days = 0;
                    if (months == 12) {
                        years++;
                        months = 0;
                    }
                }
                age = String.valueOf(years);
            } catch (ParseException e) {
                Log.d(TAG, "Exception while parsing the date so returning 0l ");
                e.printStackTrace();
            }
        }
        return years;
    }

    /**
     * a 0.1 sec length vibration to notify something, typically some error.
     * @param context
     */
    public static void notifyWithVibration(Context context){
        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

    }

    /**
     * provide the list of country code in the region
     * @return list of country codes
     */
    public static List<String> getMobileCountryCodes(){
        List<String> countryCodes = new ArrayList<>();
        for (Map.Entry<String,String> entrySet : countryAbbrvMap.entrySet()){
            if (entrySet.getKey().equalsIgnoreCase(BuddyApp.getCountryAbbrv())) {
                countryCodes.add(entrySet.getValue());
            }
        }
        if (countryCodes.size()==0){
            countryCodes.add(appCountryCodeDef);
        }
        return countryCodes;
    }
    /**
     * Populate a country code spinner with country code array
     * @param context, context of the spinner.
     * @param cntrySpinner, the spinner in which the data to be populated.
     */
    public static void populateCountryCodeAdapter(Context context, Spinner cntrySpinner){
        ArrayAdapter<String> ccodeAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, getMobileCountryCodes());
        ccodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item	);
        cntrySpinner.setAdapter(ccodeAdapter);
        cntrySpinner.setSelected(true);
    }

    /**
     * set the given value to the spinner
     * @param context context of the spinner
     * @param cntrySpinner the spinner in which the data needs to be populated
     * @param cntryCode the data to be populated
     **/
    public static void setValueToCountryCodeAdapter(Context context, Spinner cntrySpinner,String cntryCode){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.country_code_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cntrySpinner.setAdapter(adapter);
        if (cntryCode != null && !cntryCode.isEmpty()) {
            int spinnerPosition = adapter.getPosition(cntryCode);
            cntrySpinner.setSelection(spinnerPosition);
        }else{
            int spinnerPosition = adapter.getPosition("+1");
            cntrySpinner.setSelection(spinnerPosition);
        }
    }

    /**
     * get booking response
     * @param remoteResponse
     * @return
     * @throws JSONException
     */
    public static List<BuddyService> getBookingsResponse(RemoteResponse remoteResponse) throws JSONException{
        List<BuddyService> buddyServiceList = new ArrayList<>();
        //try {
        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
        if (jsonObject.has("data")) {
            JSONArray responseArray = jsonObject.getJSONArray("data");
            if (null != responseArray) {
                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject respObj = responseArray.getJSONObject(i);
                    BuddyService buddyService = new BuddyService();
                    buddyService.setId(respObj.getString("id"));
                    buddyService.setBeneficiaryId(respObj.getString("beneficiary_id"));
                    if (null!=respObj.getString("buddy_id") && !respObj.isNull("buddy_id")) {
                        buddyService.setBuddyId(respObj.getString("buddy_id"));
                    }
                    buddyService.setUserId(respObj.getString("user_id"));
                    buddyService.setCardId(respObj.getString("card_id"));
                    String displayId = respObj.getString("booking_id");
                    buddyService.setDisplayId(displayId);
                    buddyService.setStatus(respObj.getString("status"));
                    buddyService.setExtnDuration(respObj.getString("extn_hrs"));
                    buddyService.setBookingData(respObj.getString("booking_date"));
                    buddyService.setMessage(respObj.getString("message"));
                    if (respObj.has("total_time"))
                        buddyService.setTotalTime(respObj.getString("total_time"));
                    if (respObj.has("latitude"))
                        buddyService.setLatitude(respObj.getString("latitude"));
                    if (respObj.has("longitude"))
                        buddyService.setLongitude(respObj.getString("longitude"));
                    if (respObj.has("client_location"))
                        buddyService.setLocation(respObj.getString("client_location"));
                    if (respObj.has("client_zipcode"))
                        buddyService.setZipcode(respObj.getString("client_zipcode"));
                    if (respObj.has("bookings_skills")) {
                        StringBuilder skills = new StringBuilder();
                        JSONArray skillsArray = respObj.getJSONArray("bookings_skills");
                        for (int j = 0; j < skillsArray.length(); j++) {
                            JSONObject sO = skillsArray.getJSONObject(j).getJSONObject("buddy_skill");
                            skills.append(sO.getString("skill"));
                            skills.append(",");
                        }
                        if (skills.length() > 0) {
                            skills.deleteCharAt(skills.lastIndexOf(","));
                            buddyService.setBuddySkills(skills.toString());
                        }
                    }
                    if (respObj.has("expected_service_time"))
                        buddyService.setExpectedServicetime(respObj.getString("expected_service_time"));
                    if (respObj.has("exp_end_date"))
                        buddyService.setEndDate(respObj.getString("exp_end_date"));
                    buddyService.setBenDob(respObj.getJSONObject("beneficiary").getString("dob"));
                    buddyService.setGender(respObj.getJSONObject("beneficiary").getString("gender"));
                    buddyService.setBenFullName(respObj.getJSONObject("beneficiary").getString("full_name"));
                    buddyService.setBenNickName(respObj.getJSONObject("beneficiary").getString("nickname"));
                    String comments = respObj.getJSONObject("beneficiary").getString("comments");
                    buddyService.setBenComments(comments.isEmpty()?BuddyApp.getCurrentActivity().getString(R.string.no_data):comments);
                    buddyService.setBenMobile(respObj.getJSONObject("beneficiary").getString("mobile"));
                    if (respObj.has("bookings_updates")&& !respObj.isNull("bookings_updates")) {
                        JSONArray bookingUpdatesArray = respObj.getJSONArray("bookings_updates");
                        if (bookingUpdatesArray.length()>0){
                            if (bookingUpdatesArray.getJSONObject(0).has("cancel_message")) {
                                buddyService.setCancelledMessage(bookingUpdatesArray.getJSONObject(0).getString("cancel_message"));
                            }
                        }
                    }
                    if (respObj.has("cancelled_by") && !respObj.isNull("cancelled_by")) {
                        buddyService.setCancelledBy(respObj.getString("cancelled_by"));
                    }
                    if (respObj.has("end_date") && !respObj.isNull("end_date")) {
                        buddyService.setEndDate(respObj.getString("end_date"));
                    }
                    if (respObj.has("start_date") && !respObj.isNull("start_date")) {
                        buddyService.setStartDate(respObj.getString("start_date"));
                    }

                    if (respObj.has("feedbacks")) {
                        JSONArray feedbackArray = respObj.getJSONArray("feedbacks");
                        if (feedbackArray.length() > 0) {
                            for (int j=0;j<feedbackArray.length();j++){
                                if (feedbackArray.getJSONObject(j).has("user_type")){
                                    if (feedbackArray.getJSONObject(j).getString("user_type").equals("2")){
                                        //rating to buddy by user
                                        if (feedbackArray.getJSONObject(0).has("points")) {
                                            buddyService.setRated(true);
//											buddyService.setFeedbackRating(respObj.getJSONArray("feedbacks").getJSONObject(0).getString("points"));
                                        }
                                    }else {
                                        //might be 3, rating from buddy
                                        if (feedbackArray.getJSONObject(0).has("points")) {
                                            buddyService.setFeedbackRating(respObj.getJSONArray("feedbacks").getJSONObject(j).getString("points"));
                                        }
                                        if (feedbackArray.getJSONObject(0).has("comments")) {
                                            buddyService.setFeedbackComments(respObj.getJSONArray("feedbacks").getJSONObject(j).getString("comments"));
                                        }

                                    }

                                }
                            }

                        }
                    }
                    if (respObj.has("booking_fares")) {
                        JSONArray bookingFaresArray = respObj.getJSONArray("booking_fares");
                        if (bookingFaresArray.length() > 0) {
                            if (bookingFaresArray.getJSONObject(0).has("total_fare")) {
                                buddyService.setTotalAmount(respObj.getJSONArray("booking_fares").getJSONObject(0).getString("total_fare"));
                            }
                            if (bookingFaresArray.getJSONObject(0).has("base_fare")) {
                                buddyService.setBaseFare(respObj.getJSONArray("booking_fares").getJSONObject(0).getString("base_fare"));
                            }
                            if (bookingFaresArray.getJSONObject(0).has("tax")) {
                                buddyService.setTax(respObj.getJSONArray("booking_fares").getJSONObject(0).getString("tax"));
                            }
                            if (bookingFaresArray.getJSONObject(0).has("tips")) {
                                buddyService.setTipAmount(respObj.getJSONArray("booking_fares").getJSONObject(0).getString("tips"));
                            }
                        }
                    }
                    if (respObj.has("buddy")) {
                        buddyService.setBuddyName(respObj.getJSONObject("buddy").getString("full_name"));
                        buddyService.setBuddyMobile(respObj.getJSONObject("buddy").getString("mobile"));
                        buddyService.setBuddyEmail(respObj.getJSONObject("buddy").getString("email"));

                    }
                    buddyServiceList.add(buddyService);
                }
            }
        }
        return buddyServiceList;
    }

    /**
     * get zipcode from country code of mobile nums
     * @param countryCode
     * @param zipArray
     * @return
     */
    public static ArrayList<JSONObject> getZipcodeFromCountryCode(String countryCode,JSONArray zipArray){
        ArrayList<JSONObject> zipcodesArrayList = new ArrayList<>();
        String countryId = countryIdMap.get(countryCode);
        for (int i=0;i<zipArray.length();i++){
            try {
                if (countryId != null && zipArray.getJSONObject(i).getJSONObject("city").getJSONObject("state").getString("country_id").
                        contentEquals(countryId)) {
                    zipcodesArrayList.add(zipArray.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return zipcodesArrayList;

    }

    /**
     * check edit text data for any special charecters or numbers
     * @param editText
     * @return
     */
    public static boolean checkStringData(EditText editText){
        String data = editText.getText().toString();
        Pattern specialCharPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = specialCharPattern.matcher(data);
        boolean hasSpecialChar = m.find();

        Pattern numberCharPattern = Pattern.compile("[^a-z\\W]", Pattern.CASE_INSENSITIVE);
        Matcher m2 = numberCharPattern.matcher(data);
        boolean hasNumberChar = m2.find();
        if (hasSpecialChar && hasNumberChar) {
            editText.setError(BuddyApp.getApplicationContext().getString(R.string.err_string_spchar_number));
            return false;
        } else if (hasSpecialChar && !hasNumberChar) {
            editText.setError(BuddyApp.getApplicationContext().getString(R.string.err_string_spchar));
            return false;
        }else if (!hasSpecialChar && hasNumberChar) {
            editText.setError(BuddyApp.getApplicationContext().getString(R.string.err_string_number));
            return false;
        }
        return true;
    }

    /**
     * increse otp counter when wrong otp is entered repeatedly
     */
    public static void setWrongOtpCount(){
        wrongOtpCount++;
    }

    /**
     * reset otp counter
     */
    public static void resetWrongOtpCount(){
        wrongOtpCount =0;
    }

    /**
     * manage otp counter
     * @param context
     * @return
     */
    public static boolean getWrongOtpCount(Context context) {
        Intent intent = null;
        if (wrongOtpCount>=maxOtpVal){
            toastShort(context.getString(R.string.info_max_wrong_otp_attempts));
            if (isLoggedIn()) {
                intent = new Intent(BuddyApp.getCurrentActivity(), UserHomeActivity.class);
            }else {
                intent = new Intent(BuddyApp.getCurrentActivity(), LoginActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            setOpenOtpDialog(false);
            resetWrongOtpCount();
            return false;
        }
        setWrongOtpCount();
        return true;
    }
  /*  public static String getMinServiceDuration() {
        // DEFAULT_MIN_SERVICE_DURATION should be the config_key of minimum service duration config from the server
        Config dataConfig = SharedPrefsManager.getInstance().getConfigData(MIN_SERVICE_DURATION);
        return null!=dataConfig.getConfigValue()?dataConfig.getConfigValue():DEFAULT_MIN_SERVICE_DURATION;
    }
    public static String getMaxRideDistance() {
        // DEFAULT_RIDE_MiLES_KEY should be the config_key of ride-radius config from the server
        Config dataConfig = SharedPrefsManager.getInstance().getConfigData(DEFAULT_RIDE_MiLES_KEY);
        return null!=dataConfig.getConfigValue()?dataConfig.getConfigValue():DEFAULT_RIDE_MILES_VALUE;
    }
    public static String getMaxSchdInterval() {
        // DEFAULT_SCHEDULE_INTERVAL should be the config_key of schedule time interval config from the server
        Config dataConfig = SharedPrefsManager.getInstance().getConfigData(SCHEDULE_INTERVAL);
        return null!=dataConfig.getConfigValue()?dataConfig.getConfigValue():DEFAULT_SCHEDULE_INTERVAL;
    }*/
}
