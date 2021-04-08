package com.caringaide.user.model.direction;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by renjit.
 */
public class Direction {

    LatLng northEastBound;
    LatLng southWestBound;
    List<Leg> legList;

    public Direction(List<Leg> legList){
        super();
        this.legList = legList;
    }

    public LatLng getNorthEastBound() {
        return northEastBound;
    }

    public void setNorthEastBound(LatLng northEastBound) {
        this.northEastBound = northEastBound;
    }

    public LatLng getSouthWestBound() {
        return southWestBound;
    }

    public void setSouthWestBound(LatLng southWestBound) {
        this.southWestBound = southWestBound;
    }

    public List<Leg> getLegList() {
        return legList;
    }

    public void setLegList(List<Leg> legList) {
        this.legList = legList;
    }

    public String toString(){
        StringBuilder strBuff = new StringBuilder("Direction\r\n");
        for (Leg path : legList) {
            strBuff.append(path.toString());
            strBuff.append("\r\n");
        }
        return strBuff.toString();
    }
}
