package com.caringaide.user.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.caringaide.user.R;
import com.caringaide.user.fragments.CustomMapFragment;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.location.AppLocationManager;
import com.caringaide.user.utils.location.LocationGeocodeData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;


/**
 * Created by renjit.
 */
public abstract class AppLocationActivity extends BaseActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener,
        AppLocationManager.GeoCodeListener{


    public enum MAP_TYPES {SATELLITE, NORMAL, HYBRID}
    protected GoogleMap googleMap;
    protected UiSettings googleMapUiSettings;
    protected LatLng userTappedLoc;
    protected LatLng userLongTappedLoc;
    protected Location currentLocation;
    protected Double currentLat,currentLong;
    protected String currentLocationAddress;
    private CustomMapFragment mapFragment;
    //private AddressResultReceiver addressResultReceiver;
    protected boolean isInternetPresent = false;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    protected AppLocationManager locationManager;
    protected float bearing;

    protected abstract void customizeMapSettings(GoogleMap googleMap);
    //this method is called when there are location updates
    protected abstract void updatedLocation(Location location);
    //this method is called when the current location is fetched
    protected abstract void currentLocation(Double lat,Double lng);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected boolean checkPlayServices() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int resultCode = googleApi.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApi.isUserResolvableError(resultCode)) {
                googleApi.getErrorDialog(this,resultCode,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this,getString(R.string.device_not_supported),Toast.LENGTH_SHORT);
                //toastLong(this, getString(R.string.device_not_supported));
                finish();
            }
            return false;
        }
        return true;
    }

    private void initializeGoogleMaps() {

        if (null == mapFragment){
            mapFragment = (CustomMapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.googleMap.setBuildingsEnabled(true);
        //this.googleMap.setTrafficEnabled(true);
        this.googleMap.setMaxZoomPreference(20.0f);
        this.googleMap.setMinZoomPreference(6.0f);
        this.googleMapUiSettings = this.googleMap.getUiSettings();
        this.googleMap.setOnMapClickListener(this);
        this.googleMap.setOnMapLongClickListener(this);
        this.googleMap.setOnMarkerClickListener(this);
        this.googleMap.setPadding(10,30,10,2);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.googleMap.setMyLocationEnabled(false);
        }
        this.googleMapUiSettings.setZoomControlsEnabled(true);
        this.googleMapUiSettings.setMyLocationButtonEnabled(true);
        this.googleMapUiSettings.setCompassEnabled(true);
        this.googleMapUiSettings.setRotateGesturesEnabled(true);
        customizeMapSettings(googleMap);
    }

    protected void setType(MAP_TYPES mapType) {
        if (mapType.equals(MAP_TYPES.NORMAL))
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (mapType.equals(MAP_TYPES.SATELLITE))
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if (mapType.equals(MAP_TYPES.HYBRID))
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    protected void enableBuildings(boolean enable){
        googleMap.setBuildingsEnabled(enable);
    }

    /**
     * set camera position by adusting camera bearing
     * @param latLngPos
     * @param zoom
     * @param tilt
     * @param bearing
     */
    protected void setCameraPosition(LatLng latLngPos, float zoom, float tilt, float bearing) {
        Log.d(TAG(),"Setting camera to " + latLngPos + "," + zoom + "," + tilt + "," + bearing);
        CameraPosition cameraPos = new CameraPosition.Builder()
                .target(latLngPos)
                .bearing(bearing)
                .tilt(tilt)
                .zoom(zoom)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
    }

    /**
     * set camera position without setting bearing
     * @param latLngPos
     * @param zoom
     * @param tilt
     */
    protected void setCameraPosition(LatLng latLngPos, float zoom, float tilt) {
        Log.d(TAG(),"Setting camera to " + latLngPos + "," + zoom + "," + tilt + "," + bearing);
        CameraPosition cameraPos = new CameraPosition.Builder()
                .target(latLngPos)
                .tilt(tilt)
                .zoom(zoom)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
    }

    protected void setCameraPositionWithinBounds(Map<String,LatLng> markerPoints) {
        Log.d(TAG(),"Setting camera within bounds ");
        LatLngBounds.Builder latlngBuider = new LatLngBounds.Builder();
        for (Map.Entry<String, LatLng> entrySet : markerPoints.entrySet()) {
            latlngBuider.include(entrySet.getValue());
        }
        LatLngBounds latLngBounds = latlngBuider.build();
        googleMap.setLatLngBoundsForCameraTarget(latLngBounds);
        //googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, mapPadding));
    }

    protected void setCameraPositionWithinBounds(Map<String,LatLng> markerPoints,LatLng center) {
        Log.d(TAG(),"Setting camera within bounds ");
        LatLngBounds.Builder latlngBuider = new LatLngBounds.Builder();
        for (Map.Entry<String, LatLng> entrySet : markerPoints.entrySet()) {
            latlngBuider.include(entrySet.getValue());
        }
        LatLngBounds latLngBounds = latlngBuider.build();
        googleMap.setLatLngBoundsForCameraTarget(latLngBounds);
        this.setCameraPosition(center, 15.0f,45,bearing);
    }

    protected void setCameraPosition(LatLng latLngPos) {
        this.setCameraPosition(latLngPos, 15.0f,45,bearing);
    }

    protected Marker addMarker(LatLng latLng,int icon){
        if (null!=BitmapDescriptorFactory.fromResource(icon)) {
            MarkerOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(icon))
                    .visible(true);
            options.position(latLng);
            return googleMap.addMarker(options);
        }
        return null;
    }

    /**
     * add marker to google map with position
     * @param latLng
     * @param icon
     * @param zindex
     * @return
     */
    protected Marker addMarkerWithZIndex(LatLng latLng,int icon,float zindex){
        MarkerOptions options = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(icon))
                .zIndex(zindex)
                .visible(true);
        options.position(latLng);
        return googleMap.addMarker(options);
    }

    protected Marker addMarkerWithOptions(MarkerOptions options){
        return googleMap.addMarker(options);
    }

    protected Marker addMarkerWithTitle(LatLng latLng,int icon,String title){
        MarkerOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(icon))
                .title(title)
                .flat(true)
                .visible(true);
        options.position(latLng);
        return googleMap.addMarker(options);
    }


    protected Marker addMarkerAndAnimate(LatLng latLng, int icon){
        Marker marker = addMarker(latLng,icon);
        setCameraPosition(latLng);
        return marker;
    }

    protected Marker addMarkerAndAnimateWithTitle(LatLng latLng, float zoom,String title,int icon){
        MarkerOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(icon))
                .title(title)
                .flat(true)
                .visible(true);
        options.position(latLng);
        Marker marker = googleMap.addMarker(options);
        this.setCameraPosition(latLng, zoom,0,0);
        return marker;
    }

    protected Marker addMarkerAndAnimateWithZoom(LatLng latLng, float zoom,int icon){
        Marker marker = addMarker(latLng,icon);
        this.setCameraPosition(latLng, zoom,0,0);
        return marker;
    }

    protected Marker addMarkerAndAnimateToBearingWithZoom(LatLng latLng,float bearing,float zoom,int icon){
        Marker marker = addMarker(latLng,icon);
        this.setCameraPosition(latLng, zoom,0,bearing);
        return marker;
    }

    /**
     * gives the bearing between two locations
     * @param begin
     * @param end
     * @return
     */
    protected float getBearing(LatLng begin,LatLng end){
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }


    /*********************************** Google Maps callbacks *******************************************************/
    @Override
    public void onMapClick(LatLng latLng) {
        this.userTappedLoc = latLng;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        this.userLongTappedLoc = latLng;
    }


    public Location getCurrentLocation() {
        return currentLocation;
    }

    /************** activity lifecycle methods *********************/
    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        MapsInitializer.initialize(getApplicationContext());
        super.onStart();
        Log.d(TAG() , "***********onStart***********");
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // sensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG(),"***********onResume***********");
        if( checkPlayServices()){
            initializeGoogleMaps();
            //instantiate location manager
            initializeLocationManager();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //sensorManager.unregisterListener(this);
        Log.d(TAG(),"***********onPause***********");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG(),"***********onStop***********");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG(),"***********onRestart*************");
    }


    /**
     * Destroy all fragments and loaders.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    // For location updates

    /**
     * initialize location manager with the activity and geocode listener
     */
    private void initializeLocationManager(){
        //instantiate
        Runnable locRun = new Runnable(){
            @Override
            public void run() {

                Log.d(TAG(),"*************** instantiating location manager ***************");
                locationManager = AppLocationManager.getInstance(AppLocationActivity.this);
                //this will enable location updates and its callback
                locationManager.enableLocationUpdates(true);

                if(!locationManager.isConnected()){
                    locationManager.connect();
                    Log.d(TAG(),"*************** connecting location manager ***************");
                }else{
                    locationManager.updateCurrentLocation();
                }
            }
        };
        Handler handler = new Handler();
        handler.post(locRun);
    }

    /**
     * stop location updates
     */
    protected void stopLocationManager(){
        if(null != locationManager) {
            locationManager.stopLocationUpdates();
            locationManager = null;
            Log.d(TAG(),"Stopped location manager");
        }
    }

    /**
     * call back form location manager
     * @param location
     */
    @Override
    public void geocodeUpdates(Location location) {
        currentLocation = location;
        //if(location.hasAccuracy() && location.getAccuracy() <200.0f) {
        if(location.hasAccuracy()) {
            this.updatedLocation(location);
        }else{
            Log.e(TAG(),"Location is not accurate....");
        }
    }

    /**
     * call back form location manager
     */
    @Override
    public void currentLocationUpdate(LocationGeocodeData.LocationInfo locationInfo) {
        currentLat = locationManager.getCurrentLatitude();
        currentLong = locationManager.getCurrentLongitude();
        currentLocationAddress = locationManager.getCurrentLocationAddress();
        this.currentLocation(currentLat,currentLong);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BuddyConstants.REQUEST_GOOGLE_API_CLIENT) {
            locationManager.onErrorDialogDismissed();
            if (resultCode == RESULT_OK) {
                GoogleApiClient googleApiClient = locationManager.getGoogleApiClient();
                if (!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
            }
        }
    }

}
