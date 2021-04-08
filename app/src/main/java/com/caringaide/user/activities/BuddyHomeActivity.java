package com.caringaide.user.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.caringaide.user.R;
import com.caringaide.user.fragments.HomeFragment;
import com.caringaide.user.model.direction.Direction;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.ImagePickerMarker;
import com.caringaide.user.utils.ImagePickerUtil;
import com.caringaide.user.utils.LoadImageUtils;
import com.caringaide.user.utils.location.DrawDirectionUtil;
import com.caringaide.user.utils.location.PolyLineContext;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;

/**
 * demo activity from
 */
public class BuddyHomeActivity extends AppLocationActivity implements ImagePickerMarker, LoadImageUtils.ImageLoaderListener,
        DrawDirectionUtil.DirectionsListener {

    public static final String TAG = "BuddyHomeActivity";
    //TextView locationDetails, locationAddress, dropLocationDetails,dropLocationAddress,distanceBtw;
    Button matButton,goToLoginBtn;
    GoogleMap googleMap;
    private Double mLat=0.0,mLng=0.0,currentLat=0.0,currentLng=0.0;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void moveToActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
   /* private void setDestinationLocation() {
        Double destLat  = 8.6828;
        Double destLng = 76.9096;
        dropLocationDetails.setText(destLat+","+destLng);
        dropLocationAddress.setText("Venjaramoodu, Kerala 695607, India");
        Marker destMarker = addMarker(new LatLng(destLat,destLng),R.drawable.icon_marker_destination);
        getDirections(destLat,destLng);
    }*/

    private void getDirections(Double destLat, Double destLng) {
        Double pickLat=0.0;
        Double pickLng=0.0;
        if (currentLat==0.0||currentLng ==0.0){
            pickLat = mLat;
            pickLng = mLng;
        }
        if (mLat==0.0||mLng==0.0){
            pickLat = currentLat;
            pickLng = currentLng;
        }
        DrawDirectionUtil directionUtil = new DrawDirectionUtil();
        //create polyline context
        PolyLineContext polyCtx = new PolyLineContext();
        polyCtx.setStartLat(pickLat);
        polyCtx.setStartLng(pickLng);
        polyCtx.setEndLat(destLat);
        polyCtx.setEndLng(destLng);
        polyCtx.setPaths(1);
        polyCtx.setContext(getApplicationContext());
        polyCtx.setGoogleMap(googleMap);
        polyCtx.setDirectionsListener(this);
        directionUtil.getDirections(polyCtx,DrawDirectionUtil.COMPARE_DISTANCE);
    }

    @Override
    protected void setView() {
        //setContentView(R.layout.activity_main);
        changeFragment(new HomeFragment());
    }

    @Override
    protected String TAG() {
        return TAG;
    }

    @Override
    protected void customizeMapSettings(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Toast.makeText(this,"is my Location enabled?"+googleMap.isMyLocationEnabled(),Toast.LENGTH_LONG);
    }

    /**
     * updated location
     * @param location
     **/

    @Override
    protected void updatedLocation(Location location) {
        mLat = location.getLatitude();
        mLng = location.getLongitude();
        Toast.makeText(this,"update location",Toast.LENGTH_LONG);
        Log.d(TAG, "updatedLocation: "+location.getLatitude());
        Log.d(TAG, "updatedLocation: "+location.getLongitude());
    }

    /**
     * current location update
     * @param lat
     * @param lng
     **/

    @Override
    protected void currentLocation(Double lat, Double lng) {
        currentLat = lat ;currentLng = lng;
        LatLng latLng = new LatLng(lat,lng);
        Marker marker = addMarkerAndAnimateWithZoom(latLng,12.0f, R.drawable.icon_marker_pickup);
        //marker.setOnclickListeber
        //setInfoWindowTotheMarker(this,latLng);
        //locationDetails.setText(currentLat+","+currentLng);
        //locationAddress.setText(LatitudeLongitude.CURRENT_LOCATION_ADDRESS);
        Log.d(TAG, "currentLocation: "+lat);
        Log.d(TAG, "currentLocation: "+lng);
       // setDestinationLocation();
    }

    /**
     * Set a photo by capturing or choosing image from gallery.
     */
    private void selectImage() {
        final ImagePickerUtil imagePickerUtil = ImagePickerUtil.getInstance(this);
        imagePickerUtil.selectImage(BuddyConstants.PROFILE_TAKE_PHOTO, BuddyConstants.PROFILE_CHOOSE_PHOTO);
    }

    /**
     * choose photo/take photo  response
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_OK && data != null) {
            if (requestCode == BuddyConstants.PROFILE_TAKE_PHOTO) {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                //set newly captured image to the field
            }
            if (requestCode == BuddyConstants.PROFILE_CHOOSE_PHOTO) {
                Uri selectedImageUri = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    //set selected image to the field
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == BuddyConstants.PICK_CONTACT) {
                //set contact image
            }
        }
    }

    /**
     * response call back to get image from the server url.
     * @param image
     * @param imageCode
     */
    @Override
    public void imageLoaded(Bitmap image, String imageCode) {
        //set image from the server

    }

    /**
     * DrawDirectionUtil callback
     * @param directions
     * @param flag
     */
    @Override
    public void listDirections(List<Direction> directions, String... flag) {
        if (null != flag && flag.length>0 && null!=directions && !directions.isEmpty()) {
            if (flag[0].equalsIgnoreCase(DrawDirectionUtil.COMPARE_DISTANCE)) {
                int distance = directions.get(0).getLegList().get(0).getDistance();
                //distanceBtw.setText("distance between "+locationDetails.getText().toString()+" & "+
                //        dropLocationDetails.getText().toString()+" is: "+distance+"miles");
                CommonUtilities.toastShort("Distance from pickup to destination is "+distance);
            }
        }

    }
}
