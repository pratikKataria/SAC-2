package com.caringaide.user.remote;

import android.content.Context;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by renjit on 17-10-2016.
 */
public class RequestParams implements Serializable {
    private RemoteServiceListener remoteListener;
    private Context mContext;
    private Fragment fragment;
    private Integer requestType;
    private Map<String,String> requestParamMap = new HashMap<>();
    private Map<String,String> requestHeaderMap = new HashMap<>();
    //private Map<String,VolleyMultipartRequest.DataPart> requestDataPart = new HashMap<>();

    public RequestParams(RemoteServiceListener remoteListener, Context context, Integer requestType){
        this.remoteListener = remoteListener;
        this.mContext = context;
        this.requestType = requestType;
    }

    public RemoteServiceListener getRemoteListener(){
        return remoteListener;
    }

    public Context getContext(){
        return mContext;
    }

    public void setRequestParams(Map<String,String> requestParams){
        if(null != requestParams)
            this.requestParamMap = requestParams;
    }

    public <K,V> void setRequestHeader(Map<String,String> requestHeader){
        if(null != requestHeader)
            this.requestHeaderMap = requestHeader;
    }

    public Map<String,String> getRequestParamMap(){
        return requestParamMap;
    }

    public Map<String,String> getRequestHeaderMap(){
        return requestHeaderMap;
    }


    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Integer getRequestType(){
        return requestType;
    }

}
