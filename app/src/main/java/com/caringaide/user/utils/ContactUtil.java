package com.caringaide.user.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.caringaide.user.R;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.permission.PermissionCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.CommonUtilities.callCacheMap;
import static com.caringaide.user.utils.CommonUtilities.callLmit;
import static com.caringaide.user.utils.CommonUtilities.getServerMessageCode;
import static com.caringaide.user.utils.CommonUtilities.showActionSnackbar;
import static com.caringaide.user.utils.CommonUtilities.showProgressDialog;
import static com.caringaide.user.utils.CommonUtilities.toastShort;


/**
 * Created by renjit.
 */
public class ContactUtil {

    public static final String TAG = "Contact Util";
    private static ProgressDialog progressDialog;
    private static String callBookingId = null;

    public static void connectContact(Activity activity,String contactName, String contactNumber){
        if(null!=contactNumber && !contactNumber.isEmpty()){
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},
                        PermissionCodes.CALL_PERMISSION.getPermissionCode());
            }
            else
            {
                getProxyNumberAndCall(activity, contactName, contactNumber);
            }

        }

    }

    /**
     * should be called only if you need to set call limit
     * @param activity
     * @param contactName
     * @param contactNumber
     * @param bookingId
     */
    public static void connectContact(Activity activity,String contactName, String contactNumber,String bookingId){
        if(null!=contactNumber && !contactNumber.isEmpty()){
            callBookingId = bookingId;
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},
                        PermissionCodes.CALL_PERMISSION.getPermissionCode());
            }
            else
            {
                if (!isPhoneLimitExceeded(bookingId)){
                    getProxyNumberAndCall(activity, contactName, contactNumber);
                }else{
                    showActionSnackbar(activity.getString(R.string.call_limit_exceeded)
                            ,activity.getString(R.string.ok),null);
                }

            }

        }

    }

    private static void getProxyNumberAndCall(final Activity activity,String calleeName, String calleeNumber){
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleProxyNumber(activity,remoteResponse);
            }
        },activity,null);
        Map<String,String> params = new HashMap<>();
        params.put("callee",calleeName);
        params.put("callee_number",calleeNumber);
        requestParams.setRequestParams(params);
        UserServiceHandler.getProxyNumber(requestParams);
        progressDialog = showProgressDialog(activity,activity.getString(R.string.connecting_label));
    }

    private static void handleProxyNumber(Activity activity,RemoteResponse remoteResponse) {
        progressDialog.dismiss();
        String customErrorMsg = activity.getString(R.string.call_user_error);
        if(null == remoteResponse){
            Log.e(TAG, "handleProxyNumber: unexpected error from server" );
            toastShort(customErrorMsg);
        }else{
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                String response = remoteResponse.getResponse();
                if (null==response){
                    Log.e(TAG, "handleProxyNumber: response is null from server" );
                    toastShort(customErrorMsg);
                    return;
                }
                JSONObject proxyResponseObj = new JSONObject(response);
                if (proxyResponseObj.has("Error")) {
                    if (proxyResponseObj.getString("Error").equalsIgnoreCase("false")) {
                        if (proxyResponseObj.has("data")) {
                            String proxyNumber = proxyResponseObj.getJSONObject("data").getString("proxy");
                            callUser(activity, proxyNumber);
                            incrementCallCounter(callBookingId);
                        }
                    } else {
                        JSONObject proxyErrorResponseObj = new JSONObject(response);
                        if (proxyErrorResponseObj.has("message")) {
                            String message = proxyErrorResponseObj.getString("message");
                            toastShort(getServerMessageCode(message));
                        } else{
                            toastShort(customErrorMsg);
                        }
                    }
                }
            } catch (JSONException e) {
                toastShort(customErrorMsg);
                e.printStackTrace();
            } catch (SecurityException e){
                e.getMessage();
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},
                        PermissionCodes.CALL_PERMISSION.getPermissionCode());
            }
        }
    }

    private static void callUser(Activity activity, String proxyNumber) throws SecurityException{
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + proxyNumber));
        Log.d(TAG,"telephone number is: " + proxyNumber);
        activity.startActivity(intent);
    }

    /**
     * check whether the call limit is exceed with number of user-try
     * @param bookingId
     * @return
     */
    private static boolean isPhoneLimitExceeded(String bookingId){
        int callCount = null!=CommonUtilities.callCacheMap.get(bookingId)?CommonUtilities.callCacheMap.get(bookingId):0;
        return callCount >= callLmit;
    }

    /**
     * incrementing the call limit while making the call
     * @param bookingId
     */
    private  static void incrementCallCounter(String bookingId){
        int callCount = null!=CommonUtilities.callCacheMap.get(bookingId)?CommonUtilities.callCacheMap.get(bookingId):0;
        callCount+=1;
        callCacheMap.put(bookingId,callCount);
    }

}
