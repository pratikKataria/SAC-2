package com.caringaide.user.model.direction;

import java.util.List;

/**
 * Created by renjit.
 */
public class Leg {

    List<Path> pathList;
    int distance;
    int duration;
    String startAddress;
    String endAddress;

    public Leg(List<Path> pathList){
        super();
        this.pathList = pathList;
    }

    public List<Path> getPathList() {
        return pathList;
    }

    public void setPathList(List<Path> pathList) {
        this.pathList = pathList;
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

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }
}
