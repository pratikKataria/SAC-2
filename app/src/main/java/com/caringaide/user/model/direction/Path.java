package com.caringaide.user.model.direction;

import java.util.List;

/**
 * Created by renjit.
 */
public class Path {

    private List<Point> pointList;
    int distance;
    int duration;
    String travelMode;
    String htmlText;
    String start_lat = "0.0";
    String start_lng = "0.0";
    String end_lat = "0.0";
    String end_lng = "0.0";

    public Path(List<Point> pointList){
        super();
        this.pointList = pointList;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    public String getStart_lat() {
        return start_lat;
    }

    public void setStart_lat(String start_lat) {
        this.start_lat = start_lat;
    }

    public String getStart_lng() {
        return start_lng;
    }

    public void setStart_lng(String start_lng) {
        this.start_lng = start_lng;
    }

    public String getEnd_lat() {
        return end_lat;
    }

    public void setEnd_lat(String end_lat) {
        this.end_lat = end_lat;
    }

    public String getEnd_lng() {
        return end_lng;
    }

    public void setEnd_lng(String end_lng) {
        this.end_lng = end_lng;
    }

    public String toString(){
        StringBuilder strBuf=new StringBuilder("Path\r\n");
        for(Point point:pointList) {
            strBuf.append(point.toString());
            strBuf.append(point.toString());
            strBuf.append(",");
        }
        return strBuf.toString();
    }
}
