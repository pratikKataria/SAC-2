package com.caringaide.user.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.model.direction.Direction;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.location.DrawDirectionUtil;
import com.caringaide.user.utils.location.PolyLineContext;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * Called from Route map button in transit list
 */
public class TrackBuddyDetails extends AppLocationActivity implements DrawDirectionUtil.DirectionsListener {

    private static final String TAG = "TrackBuddyDetails";
    private GoogleMap googleMap;
    TextView trackTitle;
    Handler handler = new Handler();
    private String buddyId="",benName="",clientLat="",clientLng="",clientLocation ="",distanceBtw="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_buddy_details_activity_layout);
        trackTitle = findViewById(R.id.track_title);
        Intent intent = getIntent();
        if (null!=intent){
            buddyId = intent.getStringExtra("buddy_id");
            benName = intent.getStringExtra("ben_name");
            clientLat = intent.getStringExtra("client_lat");
            clientLng = intent.getStringExtra("client_lng");
            clientLocation = intent.getStringExtra("client_loc");

        }
        getBuddies();
    }

    /**
     * add marker to the destination location
     */
    private void addMarkerToBenLocation() {
        LatLng latLng = new LatLng(Double.valueOf(clientLat),Double.valueOf(clientLng));
        Marker marker = addMarkerWithTitle(latLng,R.drawable.icon_marker_destination,getString(R.string.beneficiary_label)+benName);
    }

    /**
     * get the buddy's current location and details
     */
    private void getBuddies() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                    @Override
                    public void onResponse(RemoteResponse remoteResponse) {
                        handleBuddyDetials(remoteResponse);
                    }
                },TrackBuddyDetails.this,null);
                UserServiceHandler.getBuddieLocationDetails(requestParams);
                handler.postDelayed(this,60*1000);
            }
        });
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }

    /**
     * remote response for buddy location details
     * @param remoteResponse
     */
    private void handleBuddyDetials(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.buddy_details_error);
        if(null == remoteResponse){
            toastShort(customErrorMsg);
        }else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(this, remoteResponse)) {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    Log.d(TAG, "handleBuddyDetials: "+jsonObject);
                    if (jsonObject.has("data")) {
                        JSONArray buddyLocArray = jsonObject.getJSONArray("data");
                        for (int i=0;i<buddyLocArray.length();i++){
                            JSONObject responseObj = buddyLocArray.getJSONObject(i);
                            if (responseObj.getString("user_id").equals(buddyId)){
                                String buddyName = responseObj.getJSONObject("user").getString("full_name");
                                trackTitle.setText("");
                                trackTitle.setText(getString(R.string.track_buddy_label).concat(" - ").concat(buddyName));
                                String buddyMobile = responseObj.getJSONObject("user").getString("mobile");
                                String latitude = responseObj.getString("latitude");
                                String longitude = responseObj.getString("longitude");
                                String location = responseObj.getString("location");
                                int avatar;
                                if (responseObj.getJSONObject("user").getString("gender").equalsIgnoreCase("F")){
                                    avatar = R.drawable.girl_avatar;
                                }else{
                                    avatar = R.drawable.boy_avatar;
                                }
                                LatLng buddyLatLng = new LatLng(Double.valueOf(latitude),Double.valueOf(longitude));
                                //Marker marker = addMarkerAndAnimateWithZoom(buddyLatLng,15.0f,avatar);
                               // String title = location ;
                                Marker marker = addMarkerAndAnimateWithTitle(buddyLatLng,15.0f,location,avatar);
                                getDirections(latitude,longitude);
                            }
                        }
                    } else {
                    }
                } else {
                }

            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    @Override
    protected void customizeMapSettings(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    protected void updatedLocation(Location location) {
        Log.d(TAG, "updatedLocation: "+location.getLatitude());
    }

    @Override
    protected void currentLocation(Double lat, Double lng) {
        addMarkerToBenLocation();
    }

    @Override
    protected void setView() {

    }

    @Override
    protected String TAG() {
        return TAG;
    }

    @Override
    public void listDirections(List<Direction> directions, String... flag) {
        if (null != flag && flag.length>0 && null!=directions && !directions.isEmpty()) {
            if (flag[0].equalsIgnoreCase(DrawDirectionUtil.COMPARE_DISTANCE)) {
                int distance = directions.get(0).getLegList().get(0).getDistance();
                distanceBtw = distance+" miles";
                //distanceBtw.setText("distance between "+locationDetails.getText().toString()+" & "+
                //        dropLocationDetails.getText().toString()+" is: "+distance+"miles");
                //CommonUtilities.toastShort(this,"Distance from pickup to destination is "+distance);
            }
        }else{
            Log.d(TAG, "listDirections: "+"no route");
            //CommonUtilities.toastShort(this,"no route");
            //Snackbar.make()
        }
    }

    /**
     * draw polyline to mark the route
     * @param destLat
     * @param destLng
     */
    private void getDirections(String destLat, String destLng) {
        DrawDirectionUtil directionUtil = new DrawDirectionUtil();
        //create polyline context
        PolyLineContext polyCtx = new PolyLineContext();
        polyCtx.setStartLat(Double.valueOf(clientLat));
        polyCtx.setStartLng(Double.valueOf(clientLng));
        polyCtx.setEndLat(Double.valueOf(destLat));
        polyCtx.setEndLng(Double.valueOf(destLng));
        polyCtx.setPaths(1);
        polyCtx.setContext(getApplicationContext());
        polyCtx.setGoogleMap(googleMap);
        polyCtx.setDirectionsListener(this);
        directionUtil.getDirections(polyCtx,DrawDirectionUtil.COMPARE_DISTANCE);
    }
}
