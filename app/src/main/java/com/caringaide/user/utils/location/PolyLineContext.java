package com.caringaide.user.utils.location;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by renju on 11/07/17.
 */

public class PolyLineContext {

    private GoogleMap googleMap;//google map is required only if polyline is required
    private Context context;
    private Double startLat,startLng,endLat,endLng;
    private int paths;
    private DrawDirectionUtil.DirectionsListener directionsListener;

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    //google map needs to be set only if polyline is required, for directions alone-not required.
    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getStartLng() {
        return startLng;
    }

    public void setStartLng(Double startLng) {
        this.startLng = startLng;
    }

    public Double getEndLat() {
        return endLat;
    }

    public void setEndLat(Double endLat) {
        this.endLat = endLat;
    }

    public Double getEndLng() {
        return endLng;
    }

    public void setEndLng(Double endLng) {
        this.endLng = endLng;
    }

    public int getPaths() {
        return paths;
    }

    public void setPaths(int paths) {
        this.paths = paths;
    }

    public DrawDirectionUtil.DirectionsListener getDirectionsListener() {
        return directionsListener;
    }

    public void setDirectionsListener(DrawDirectionUtil.DirectionsListener directionsListener) {
        this.directionsListener = directionsListener;
    }
}
