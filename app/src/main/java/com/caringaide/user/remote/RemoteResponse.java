package com.caringaide.user.remote;

import android.content.Context;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import java.io.Serializable;

/**
 * Created by renjit.
 */
public class RemoteResponse implements Serializable {

    private VolleyError error;
    private Context context;
    private Fragment fragment;
    private String response;
    private String errorMessage = "";
    private String customErrorMessage;
    private boolean isFailed;
    private boolean isConnectivityIssue = false;
    private Integer requestType;

    public Integer getRequestType() {
        return requestType;
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }

    public VolleyError getError() {
        return error;
    }

    public void setError(VolleyError error) {
        this.error = error;
        this.isFailed = true;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isError(){
        return isFailed;
    }

    public void setConnectivityIssue(boolean yes){
        this.isConnectivityIssue = yes;
    }

    public boolean isConnectivityIssue(){
        return isConnectivityIssue;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


    public String getCustomErrorMessage() {
        return customErrorMessage;
    }

    public void setCustomErrorMessage(String customErrorMessage) {
        this.customErrorMessage = customErrorMessage;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
