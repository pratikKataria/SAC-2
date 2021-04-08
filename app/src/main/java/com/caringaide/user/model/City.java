package com.caringaide.user.model;

public class City {
    private String cityId;
    private String cityName;
    private String cityAbbrv;
    private String stateId;
    private String stateName;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityAbbrv() {
        return cityAbbrv;
    }

    public void setCityAbbrv(String cityAbbrv) {
        this.cityAbbrv = cityAbbrv;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
