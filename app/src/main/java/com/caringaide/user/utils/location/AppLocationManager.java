package com.caringaide.user.utils.location;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.caringaide.user.R;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.SharedPrefsManager;
import com.caringaide.user.utils.permission.PermissionCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by renjit.
 */
public class AppLocationManager implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    private static final String TAG = "AppLocationManager";
    private SharedPreferences sharedPreferences;
    private static AppLocationManager appLocationManager;
    private static GoogleApiClient googleApiClient;
    private Context mContext;
    private AppCompatActivity currentActivity;
    private LocationRequest mLocationRequest;
    private boolean mResolvingError = false;
    private boolean requiresUpdates = false; //by default regular updates are not required.

    private Location mCurrentlocation;
    private static Double CURRENT_LATITUDE = 0.0;
    private static Double CURRENT_LONGITUDE = 0.0;
    private static String CURRENT_LOCATION_ADDRESS = "";
    public static  LocationGeocodeData.LocationInfo CURRENT_LOC_INFO ;
    // flag for GPS status
    private boolean isGPSEnabled = false;
    // The minimum time between updates in milliseconds
    private static long MIN_TIME_BW_UPDATES = 1000*10; //10 seconds
    // fastest interval for updates
    private static long FASTEST_INTERVAL = 1000; // 500 milli second
    public static final int REQUEST_CHECK_SETTINGS = 1;
    private GeoCodeListener mGeoCodeListener;
    private boolean requestCreated = false;
    private boolean locationUpdates = false;

    /**
     * Any class including activities that require location callbacks need to implement this interface
     */
    public interface GeoCodeListener{
        /**
         * This is a callback method for location updates,Location updates will happen only if the enableLocationUpdates
         * is flagged to true.
         **/
        void geocodeUpdates(Location location);

        /**
         * This callback happens as soon as the google api client is connected and the FusedLocationApi is invoked.
         * @param locationInfo
         */
        void currentLocationUpdate(LocationGeocodeData.LocationInfo locationInfo);
    }

    /**
     * private constructor for AppLocation Manager
     * @param activity
     */
    private AppLocationManager(AppCompatActivity activity) {
        registerActivity(activity);
//        enableGPS();
        buildGoogleApiClient();
    }

    /**
     * register any class that implements GeoCode listener for location callbacks
     * @param geoCodeListener
     */
    public void registerGeocodeListener(GeoCodeListener geoCodeListener){
        mGeoCodeListener = geoCodeListener;
    }

    /**
     * register any activity for receiving location callbacks.
     * @param activity
     */
    private void registerActivity(AppCompatActivity activity){
        this.mContext = activity.getApplicationContext();
        this.currentActivity = activity;
        registerGeocodeListener((GeoCodeListener)currentActivity);
    }

    /**
     * build google api client
     */
    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * create location request with necessary properties
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setInterval(MIN_TIME_BW_UPDATES);
        //mLocationRequest.setSmallestDisplacement(20.0f);
    }

    /**
     * create location request with user defined interval and collection interval
     * @param collInterval
     * @param fastestInterval
     */
    public void createLocationRequest(long collInterval , long fastestInterval) {
        FASTEST_INTERVAL = fastestInterval;
        MIN_TIME_BW_UPDATES = collInterval;
        requestCreated = true;
        createLocationRequest();
    }

    /**
     * this will check the current mCurrentlocation settings and opens a dialog box for changing the settings
     */
    private void checkCurrentLocationSettings() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        // check if mCurrentlocation settings are satisfied
        final PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.
                checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates states = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        //do mCurrentlocation updates here
                        Log.d(TAG, "Location success");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.d(TAG, "Location settings resolution required");
                            status.startResolutionForResult(currentActivity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "Unable to change settings ");
                        Toast.makeText(mContext, mContext.getString(R.string.no_location),Toast.LENGTH_SHORT);

                }
            }
        });
    }

    /**
     * switch of regular location updates
     * @param locationUpdates
     */
    public void enableLocationUpdates(boolean locationUpdates){
        this.locationUpdates = locationUpdates;
    }

    /**
     * a singleton instance of the AppLocationManager
     * @param currentActivity
     * @return
     */
    public static synchronized AppLocationManager getInstance(AppCompatActivity currentActivity) {
        if (appLocationManager == null) {
            appLocationManager = new AppLocationManager(currentActivity);
            Log.d(TAG, "##################### Iam new AppLocationManager#################");
        }else{
            //register any activity
            appLocationManager.registerActivity(currentActivity);
        }
        return appLocationManager;
    }

    /**
     * returns an instance of the google api client.
     * @return
     */
    public GoogleApiClient getGoogleApiClient() {
        if (null == googleApiClient) {
            buildGoogleApiClient();
        }
        return googleApiClient;
    }

    public void connect() {
        if (null != googleApiClient && !googleApiClient.isConnecting()){
            if(!googleApiClient.isConnected()){
                googleApiClient.connect();
            }
        }else if(null != googleApiClient && !googleApiClient.isConnected()){
            googleApiClient.connect();
        }else{
            Log.e(TAG,"Google API Client not connected");
        }

    }

    public void disconnect() {
        if (this.isConnected())
            googleApiClient.disconnect();
    }

    public boolean isConnected() {
        if (null != googleApiClient) {
            return googleApiClient.isConnected();
        }
        return false;
    }

    /**
     * The locationUpdates flag is off by default and location updates wont get collected by default. The flag can be explicitly switched on
     * by calling the "requireLocationUpdates" method and setting the flag to true.
     * In default updates need to be collected, then set the flag to true as soon as you get an instance of the AppLocationManager and
     * then connect with AppLocationManager. This will enable the default collection.
     * Otherwise you can enable it later using the AppLocationManager's setting the flag true using the "requireLocationUpdates" method and call
     * "startLocationUpdates"
     **/
    public void startLocationUpdates() {
        if(locationUpdates){
            //if create Location result not called by caller, then call default
            if(!requestCreated){
                createLocationRequest(); //default request
            }
            if ( ContextCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                ActivityCompat.requestPermissions(currentActivity, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                        PermissionCodes.LOCATION_PERMISSION.getPermissionCode());
            }
            else {
                if(isConnected()) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
                }
            }

        }
    }

    /**
     * will stop location updates by the manager
     */
    public void stopLocationUpdates(){
        if(isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
            //disconnect google api services
            this.disconnect();
        }
    }


    /**
     * callback for location updates
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentlocation = location;
        if(mCurrentlocation != null){
            CURRENT_LATITUDE = mCurrentlocation.getLatitude();
            CURRENT_LONGITUDE = mCurrentlocation.getLongitude();
            mGeoCodeListener.geocodeUpdates(mCurrentlocation);
            //update to db
            SharedPrefsManager.getInstance().updateCurrentLatLng(String.valueOf(CURRENT_LATITUDE),String.valueOf(CURRENT_LONGITUDE));
            Log.i(TAG,"Location updating lat and lng and provider is " + CURRENT_LATITUDE + " , " + CURRENT_LONGITUDE + "  " + location.getProvider());
        }
    }

    /**
     * call back for google api client connection
     * here we get the last known location and enable the location settings for the device
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        if (isGPSEnabled) {
            updateCurrentLocation();
//        }
    }

    /**
     * call back when google api connection gets suspended
     * @param reasonCode
     */
    @Override
    public void onConnectionSuspended(int reasonCode) {
        Log.e(TAG,"GoogleApiClient connection suspended");
        if(GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST == reasonCode){
            Toast.makeText(mContext,mContext.getString(R.string.no_internet),Toast.LENGTH_SHORT);
        }
        connect();
    }

    /**
     * call when google api client connection fails
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG,"GoogleApiClient connection failed " + connectionResult.toString());
        if(mResolvingError) {
            //already attempting to resolve an error
            return;
        }else if(connectionResult.hasResolution()){
            try {
                mResolvingError = true; //starting to resolve the error
                connectionResult.startResolutionForResult(currentActivity, BuddyConstants.REQUEST_GOOGLE_API_CLIENT);
            } catch (IntentSender.SendIntentException e) {
                //there was some issue with the resolution intent, try again
                googleApiClient.connect();
                e.printStackTrace();
            }
        }else{
            // if has resolution return false, then try to launch the dialog provided by google play services thats appropriate for the error
            // the dialog may simply provide a message for the error or it may try to launch an activity to reolve the error, such as updating the
            // updating to newer version of play services.
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    /**
     * gets the current location details, update to SharedPreferences
     * and handle city state from the shared pref method
     */
    public void updateCurrentLocation(){
        if ( ContextCompat.checkSelfPermission( mContext, Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(currentActivity, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                    PermissionCodes.LOCATION_PERMISSION.getPermissionCode());
        }else{
            mCurrentlocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(mCurrentlocation != null){
                CURRENT_LATITUDE =  mCurrentlocation.getLatitude();
                CURRENT_LONGITUDE =  mCurrentlocation.getLongitude();
                //update current location session too.
                LocationGeocodeData.getLocationInfo(currentActivity, CURRENT_LATITUDE, CURRENT_LONGITUDE, new LocationGeocodeData.LocationGeocodeListener() {
                    @Override
                    public void onLocationUpdate(LocationGeocodeData.LocationInfo locationInfo) {
                        String currentLoc = locationInfo.getFormattedAddress();
                        CURRENT_LOCATION_ADDRESS = currentLoc;
                        //update to db
                        CURRENT_LOC_INFO = SharedPrefsManager.getInstance().createCurrentAddressSession(locationInfo);
                        mGeoCodeListener.currentLocationUpdate(locationInfo);
                        Log.d(TAG,"Location lat and lng " + CURRENT_LATITUDE + " , " + CURRENT_LONGITUDE);
                    }
                });

            }
            if(locationUpdates){
                startLocationUpdates();
            }
        }
        checkCurrentLocationSettings();
    }


    public void onErrorDialogDismissed(){
        appLocationManager.mResolvingError = false;
    }


    public void enableGPS(){
        // getting GPS status
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSEnabled){
            Log.d(TAG, "enableGPS: GPS is not enabled");
            showGPSSettingsAlert();
        }
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     * */
    public void showGPSSettingsAlert() {
        if (!isGPSEnabled) {
            //AlertDialog.Builder alertDialog = new AlertDialog.Builder(currentActivity);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(BuddyApp.getCurrentActivity());
            alertDialog.setTitle(BuddyApp.getCurrentActivity().getString(R.string.app_name));
            alertDialog.setMessage(BuddyApp.getCurrentActivity().getString(R.string.gps_enable_msg));
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    BuddyApp.getCurrentActivity().startActivity(intent);
                }
            });

            alertDialog.show();
        }
    }

    private void showErrorDialog(int errorCode){
        //custom dialog fragment
        ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
        //pass error code that should be displayed
        Bundle args = new Bundle();
        args.putInt(BuddyConstants.CONNECTION_ERROR_CODE,errorCode);
        errorDialogFragment.setArguments(args);
        errorDialogFragment.show(currentActivity.getSupportFragmentManager(),"errorDialog");
    }

    /**
     * Dialog fragment to display the error dialog from the google play services while attempting a client connection
     */
    public static class ErrorDialogFragment extends DialogFragment{
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int errorCode = this.getArguments().getInt(BuddyConstants.CONNECTION_ERROR_CODE);
            return GoogleApiAvailability.getInstance().getErrorDialog(appLocationManager.currentActivity,errorCode,BuddyConstants.REQUEST_GOOGLE_API_CLIENT);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            appLocationManager.onErrorDialogDismissed();
        }
    }

    public static Double getCurrentLatitude() {
        return CURRENT_LATITUDE;
    }

    public static Double getCurrentLongitude() {
        return CURRENT_LONGITUDE;
    }

    public static String getCurrentLocationAddress() {
        return CURRENT_LOCATION_ADDRESS;
    }
}