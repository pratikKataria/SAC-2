package com.caringaide.user.remote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.caringaide.user.R;
import com.caringaide.user.activities.LoginActivity;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.SharedPrefsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by renjit on 16-10-2016.
 */
public class RemoteService {
    private static RemoteService remoteService;
    private RequestQueue requestQueue;
    private Context mContext;

    static final String TAG = "Remote Service";
    private static boolean haveAlertLogin = false;

    private RemoteService(Context context) {
        mContext = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized RemoteService getInstance(Context context) {
        if (null == remoteService) {
            remoteService = new RemoteService(context);
        }
        return remoteService;
    }

    private RequestQueue getRequestQueue() {
        if (null == requestQueue) {
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
        getRequestQueue().getCache().remove(request.getUrl());
    }


    /**
     * fire a post request
     *
     * @param url
     * @param requestParams
     */
    public void doPost(final String url, final RequestParams requestParams) {
        final Context requestContext = requestParams.getContext();
        final RemoteServiceListener remoteListener = requestParams.getRemoteListener();
        final RemoteResponse remoteResponse = new RemoteResponse();
        remoteResponse.setContext(requestContext);
        remoteResponse.setFragment(requestParams.getFragment());
//        final ProgressDialog progDailog = ProgressDialog.show(requestContext, requestContext.getString(R.string.loading),
//                requestContext.getString(R.string.please_wait), true);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Remote Service ", response);
                remoteResponse.setResponse(response);
//                progDailog.dismiss();
                remoteResponse.setRequestType(requestParams.getRequestType());
                remoteListener.onResponse(remoteResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (requestContext!=null ) {
                    String errorMessage = requestContext.getString(R.string.unknown_error);
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMessage = requestContext.getString(R.string.req_timeout);
                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = requestContext.getString(R.string.connect_error);
                        }
                        remoteResponse.setConnectivityIssue(true);
                        Log.e("Remote Service ", errorMessage);
                    } else {
                        String result = new String(networkResponse.data);
                        try {
                            if (result != null) {
                                JSONObject response = new JSONObject(result);
                                JSONObject errorObj = response.getJSONObject("error");
                                if (null != errorObj) {
                                    String status = "", message = "";
                                    if (errorObj.has("status")) {

                                        status = errorObj.getString("status");
                                    }
                                    if (errorObj.has("message")) {

                                        message = errorObj.getString("message");
                                    }
                                    Log.e("Error Status", status);
                                    Log.e("Error Message", message);

                                    if (networkResponse.statusCode == 404) {
                                        errorMessage = message + " " + requestContext.getString(R.string.res_not_found);
                                    } else if (networkResponse.statusCode == 401) {
                                        errorMessage = message + " " + requestContext.getString(R.string.login_again);
                                        alertDoLogin(errorMessage);
                                    } else if (networkResponse.statusCode == 400) {
                                        errorMessage = message + " " + requestContext.getString(R.string.wrong_input);
                                    } else if (networkResponse.statusCode == 500) {
                                        errorMessage = message + " " + requestContext.getString(R.string.wrong_input);
                                    } else {
                                        if (null != message && !message.isEmpty()) {
                                            errorMessage = "Status = " + status + ", " + message;
                                        }
                                    }
                                }
                                Log.e("Remote Service ", errorMessage);
                            } else {
                                Log.d(TAG, "error networkResponse.data is null " + result);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    remoteResponse.setRequestType(requestParams.getRequestType());
                    remoteResponse.setErrorMessage(errorMessage);
                    remoteResponse.setError(error);
//                    progDailog.dismiss();
                    remoteListener.onResponse(remoteResponse);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> reqParams = requestParams.getRequestParamMap();
                reqParams.put("app_name", BuddyConstants.APP_NAME);
                return reqParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> reqHeader = requestParams.getRequestHeaderMap();
                //reqHeader.put("Content-Type", "application/json");
                reqHeader.put("app-name", BuddyConstants.APP_NAME);
                reqHeader = setCsrfToken(reqHeader, requestContext);
                reqHeader = setAuthToken(reqHeader, requestContext);
                return reqHeader;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                handleNetworkResponse(requestContext,url, response);
                return super.parseNetworkResponse(response);
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(request);
    }

    /**
     * fire a get request
     *
     * @param url
     * @param requestParams
     */
    public void doGet(final String url, final RequestParams requestParams) {
        final Context requestContext = requestParams.getContext();
        final RemoteServiceListener remoteListener = requestParams.getRemoteListener();
        final RemoteResponse remoteResponse = new RemoteResponse();
        remoteResponse.setContext(requestContext);
        remoteResponse.setFragment(requestParams.getFragment());
//        final ProgressDialog progDailog = ProgressDialog.show(requestContext, requestContext.getString(R.string.loading),
//                requestContext.getString(R.string.please_wait), true);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Remote Service ", response);
                remoteResponse.setResponse(response);
                remoteResponse.setRequestType(requestParams.getRequestType());
                remoteListener.onResponse(remoteResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (requestContext!=null) {
                    String errorMessage = requestContext.getString(R.string.unknown_error);
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMessage = requestContext.getString(R.string.connect_error);
                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = requestContext.getString(R.string.connect_error);
                        }
                        remoteResponse.setConnectivityIssue(true);
                        Log.e("Remote Service ", errorMessage);
                    } else {
                        String result = new String(networkResponse.data);
                        try {
                            if (null != result) {
                                JSONObject response = new JSONObject(result);
                                JSONObject errorObj = response.getJSONObject("error");
                                String status = "", message = "";
                                if (errorObj.has("status")) {

                                    status = errorObj.getString("status");
                                }
                                if (errorObj.has("message")) {

                                    message = errorObj.getString("message");
                                }
                                Log.e("Error Status", status);
                                Log.e("Error Message", message);
                                if (networkResponse.statusCode == 404) {
                                    errorMessage = message + " " + requestContext.getString(R.string.res_not_found);
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + " " + requestContext.getString(R.string.login_again);
                                    alertDoLogin(errorMessage);

                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + " " + requestContext.getString(R.string.wrong_input);
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + " " + requestContext.getString(R.string.wrong_input);
                                } else {
                                    if (null != message && !message.isEmpty()) {
                                        errorMessage = "Status = " + status + ", " + message;
                                    }
                                }
                            } else {
                                Log.e(TAG, "network response is null ");
                            }
                            Log.e("Remote Service ", errorMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    remoteResponse.setRequestType(requestParams.getRequestType());
                    remoteResponse.setErrorMessage(errorMessage);
                    remoteResponse.setError(error);
//                    progDailog.dismiss();
                    remoteListener.onResponse(remoteResponse);
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> reqHeader = requestParams.getRequestHeaderMap();
                reqHeader.put("app-name", BuddyConstants.APP_NAME);
                reqHeader = setCsrfToken(reqHeader, requestContext);
                reqHeader = setAuthToken(reqHeader, requestContext);
//                if (!url.contains("googleapis.com")) {
//                    reqHeader = setAuthToken(reqHeader, requestContext);
//                }
                return reqHeader;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                handleNetworkResponse(requestContext,url, response);
                return super.parseNetworkResponse(response);
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(request);
    }

   /* private void alertDoLogin(String errorMessage) {
        SharedPrefsManager.clearSharedPrefs();
        final Activity activity = BuddyApp.getCurrentActivity();
        CommonUtilities.alertAndAction(activity, activity.getString(R.string.app_name),
                errorMessage, activity.getString(R.string.ok), null, new AlertAction() {
                    @Override
                    public void positiveAction() {
                        Intent intent = new Intent(BuddyApp.getCurrentActivity(), LoginActivity.class);
                        activity.startActivity(intent);
                    }

                    @Override
                    public void negativeAction() {
                    }
                });
    }*/

    private void alertDoLogin(String errorMessage) {
        if (!haveAlertLogin) {
            SharedPrefsManager.getInstance().clearSharedPreferences();
            final Activity activity = BuddyApp.getCurrentActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(activity.getString(R.string.app_name));
            builder.setMessage(errorMessage);
            builder.setNegativeButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CommonUtilities.logout();
                    BuddyApp.getApplicationContext().getCacheDir().deleteOnExit();
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            } else {
                haveAlertLogin = true;
                alertDialog.show();
            }
        }
    }

    /**
     * delete operations
     *
     * @param url
     * @param requestParams
     */
    public void doDelete(final String url, final RequestParams requestParams) {
        final Context requestContext = requestParams.getContext();
        final RemoteServiceListener remoteListener = requestParams.getRemoteListener();
        final RemoteResponse remoteResponse = new RemoteResponse();
        remoteResponse.setContext(requestContext);
        remoteResponse.setFragment(requestParams.getFragment());
        StringRequest request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Remote Service ", response);
                remoteResponse.setResponse(response);
                remoteResponse.setRequestType(requestParams.getRequestType());
                remoteListener.onResponse(remoteResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (requestContext!=null) {
                    String errorMessage = requestContext.getString(R.string.unknown_error);
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMessage = requestContext.getString(R.string.req_timeout);
                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = requestContext.getString(R.string.connect_error);
                        }
                        remoteResponse.setConnectivityIssue(true);
                        Log.e("Remote Service ", errorMessage);
                    } else {
                        String result = new String(networkResponse.data);
                        try {
                            if (null != result) {
                                JSONObject response = new JSONObject(result);
                                JSONObject errorObj = response.getJSONObject("error");
                                String status = "", message = "";
                                if (errorObj.has("status")) {

                                    status = errorObj.getString("status");
                                }
                                if (errorObj.has("message")) {

                                    message = errorObj.getString("message");
                                }
                                Log.e("Error Status", status);
                                Log.e("Error Message", message);
                                if (networkResponse.statusCode == 404) {
                                    errorMessage = message + " " + requestContext.getString(R.string.res_not_found);
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + " " + requestContext.getString(R.string.login_again);
                                    alertDoLogin(errorMessage);
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + " " + requestContext.getString(R.string.wrong_input);
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + " " + requestContext.getString(R.string.wrong_input);
                                } else {
                                    if (null != message && !message.isEmpty()) {
                                        errorMessage = "Status = " + status + ", " + message;
                                    }
                                }
                            } else {
                                Log.e(TAG, "network response is null ");
                            }
                            Log.e("Remote Service ", errorMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    remoteResponse.setRequestType(requestParams.getRequestType());
                    remoteResponse.setErrorMessage(errorMessage);
                    remoteResponse.setError(error);
                    remoteListener.onResponse(remoteResponse);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> reqParams = requestParams.getRequestParamMap();
                reqParams.put("app_ame",BuddyConstants.APP_NAME);
                //if (null != reqParams && !reqParams.isEmpty()) {
                return reqParams;
                // }
                //return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> reqHeader = requestParams.getRequestHeaderMap();
                reqHeader.put("Content-type", "application/x-www-form-urlencoded");
                reqHeader = setCsrfToken(reqHeader, requestContext);
                reqHeader = setAuthToken(reqHeader, requestContext);
                return reqHeader;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                handleNetworkResponse(requestContext,url, response);
                return super.parseNetworkResponse(response);
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(request);
    }

    /**
     * put for update operations
     *
     * @param url
     * @param requestParams
     */
    public void doPut(final String url, final RequestParams requestParams) {
        final Context requestContext = requestParams.getContext();
        final RemoteServiceListener remoteListener = requestParams.getRemoteListener();
        final RemoteResponse remoteResponse = new RemoteResponse();
        remoteResponse.setContext(requestContext);
        remoteResponse.setFragment(requestParams.getFragment());
        StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Remote Service ", response);
                remoteResponse.setResponse(response);
                remoteResponse.setRequestType(requestParams.getRequestType());
                remoteListener.onResponse(remoteResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (requestContext!=null) {
                    String errorMessage = requestContext.getString(R.string.unknown_error);
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMessage = requestContext.getString(R.string.req_timeout);
                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = requestContext.getString(R.string.connect_error);
                        }
                        remoteResponse.setConnectivityIssue(true);
                        Log.e("Remote Service ", errorMessage);
                    } else {
                        String result = new String(networkResponse.data);
                        try {
                            if (null != result) {
                                JSONObject response = new JSONObject(result);
                                JSONObject errorObj = response.getJSONObject("error");
                                String status = "", message = "";
                                if (errorObj.has("status")) {

                                    status = errorObj.getString("status");
                                }
                                if (errorObj.has("message")) {

                                    message = errorObj.getString("message");
                                }
                                Log.e("Error Status", status);
                                Log.e("Error Message", message);
                                if (networkResponse.statusCode == 404) {
                                    errorMessage = message + " " + requestContext.getString(R.string.res_not_found);
                                } else if (networkResponse.statusCode == 401) {
                                    errorMessage = message + " " + requestContext.getString(R.string.login_again);
                                    alertDoLogin(errorMessage);
                                } else if (networkResponse.statusCode == 400) {
                                    errorMessage = message + " " + requestContext.getString(R.string.wrong_input);
                                } else if (networkResponse.statusCode == 500) {
                                    errorMessage = message + " " + requestContext.getString(R.string.wrong_input);
                                } else {
                                    if (null != message && !message.isEmpty()) {
                                        errorMessage = "Status = " + status + ", " + message;
                                    }
                                }
                            } else {
                                Log.e(TAG, "network response is null ");
                            }
                            Log.e("Remote Service ", errorMessage);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    remoteResponse.setRequestType(requestParams.getRequestType());
                    remoteResponse.setErrorMessage(errorMessage);
                    remoteResponse.setError(error);
                    remoteListener.onResponse(remoteResponse);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> reqParams = requestParams.getRequestParamMap();
                reqParams.put("app_name", BuddyConstants.APP_NAME);
                //if (null != reqParams && !reqParams.isEmpty()) {
                return reqParams;
                // }
                //return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> reqHeader = requestParams.getRequestHeaderMap();
                reqHeader.put("Content-type", "application/x-www-form-urlencoded");
                reqHeader = setCsrfToken(reqHeader, requestContext);
                reqHeader = setAuthToken(reqHeader, requestContext);
                return reqHeader;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                handleNetworkResponse(requestContext,url, response);
                return super.parseNetworkResponse(response);
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        addToRequestQueue(request);
    }

    /**
     * interpret request and add required tokens
     *
     * @param reqHeader
     * @param requestContext
     * @return
     */
    private Map<String, String> setCsrfToken(Map<String, String> reqHeader, Context requestContext) {
        String csrfToken = SharedPrefsManager.getInstance().getCsrfToken();
        if (null != csrfToken && !csrfToken.isEmpty()) {
            reqHeader.put("Cookie", "csrfToken=" + csrfToken);
            reqHeader.put("X-CSRF-Token", csrfToken);
        }
        return reqHeader;
    }

    /**
     * set auth token in header
     *
     * @param reqHeader
     * @param requestContext
     * @return
     */
    private Map<String, String> setAuthToken(Map<String, String> reqHeader, Context requestContext) {
        //set auth token in request headers
        String authToken = SharedPrefsManager.getInstance().getAuthToken();
        Log.d(TAG,"auth token"+authToken);
        reqHeader.put("auth-token", authToken);
        return reqHeader;
    }

    /**
     * store required data from network response
     *
     * @param url,response
     */
    private void handleNetworkResponse(Context context,String url, NetworkResponse response) {
        if (null != response && null != response.headers) {
            //get csrf token from header and save it to preferences
            String rawCookies = response.headers.get("Set-Cookie");
            Log.d("parseNetworkResponse", "###### the csrf token rawCookie will be " + rawCookies + " for url ::: " + url);
            if (null != rawCookies) {
                String[] splitCookie = rawCookies.split(";");
                for (String eachCookie : splitCookie) {
                    if (eachCookie.toUpperCase().contains("CSRFTOKEN")) {
                        String[] csrfTokenArr = eachCookie.split("=");
                        String csrfToken = csrfTokenArr[1];
                        SharedPrefsManager.getInstance().storeCsrfToken(csrfToken);
                    }
                }

            }
            //get auth token from header and safe to shared preferences
            String authToken = response.headers.get("auth-token");
            if (null != authToken && !authToken.isEmpty()) {
                // SharedPrefsManager.getInstance().storeAuthToken(authToken);
            }

        }
    }
}