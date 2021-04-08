package com.caringaide.user.model;

import java.util.ArrayList;
import java.util.List;

public class ScheduledTime {

    public static enum DaysOfWeek{
        Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday
    }
    private String day;
    private boolean isDefault = true;
    private String id;
    private String buddyId;
    private String dayOfWeek;
    private List<TimeRange> timeRange = new ArrayList<>();

    public ScheduledTime(String day) {
        this.day = day;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuddyId() {
        return buddyId;
    }

    public void setBuddyId(String buddyId) {
        this.buddyId = buddyId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public List<TimeRange> getTimeRangeList() {
        return timeRange;
    }

    public void setTimeRange(List<TimeRange> timeRange) {
        this.timeRange = timeRange;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public TimeRange instance(){
        return new TimeRange();
    }

    public class TimeRange{
        private String timeRangeId = "";
        private String fromDate = "00:00:00";
        private String toDate = "00:00:00";

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        public String getToDate() {
            return toDate;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }

        public String getTimeRangeId() {
            return timeRangeId;
        }

        public void setTimeRangeId(String timeRangeId) {
            this.timeRangeId = timeRangeId;
        }
    }

}
