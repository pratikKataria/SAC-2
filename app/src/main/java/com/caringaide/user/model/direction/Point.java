package com.caringaide.user.model.direction;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by renjit.
 */
public class Point {

    double lat;
    double lng;
    private LatLng latLng = null;

    public Point(double lat,double lng){
        super();
        this.lat = lat;
        this.lng = lng;
    }

    public LatLng getLatLng(){
        if(null == latLng){
            latLng = new LatLng(lat,lng);
        }
        return latLng;
    }

    public String toString(){
        return "["+lat+","+lng+"]";
    }

}
