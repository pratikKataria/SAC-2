package com.caringaide.user.model;

public class BookingSummary {
    private String bookingId;
    private String bookingStatus;
    //pending
    private String pendingBookingDate;
    private String pendingBookingLoc;
    private int pendingBookingOtherCount;
    private int pendingBookingFavCount;
    private String pendingBeneficiary;
    private String pendingFavBuddy;
    private boolean isFavBooking;
    //confirm
    private String confirmNearestBookingDate;
    private int confirmBookingCount;
    private String confirmBookingLocation;
    private String confirmBookingZipcode;
    private String confirmTotalBookingTime;
    private int nearestIntervalMins;
    private String confirmBuddyName;
    private String lastBookingBuddyname;
    private String lastBookingId;
    private int durationBtwLastBooking;
    private int durationBtwConfirmBooking;
    private String lastBookingDate;
    private String lastBookingLoc;
    private String confirmBeneficiary;
    private String confirmLastBookingBeneficiary;
    private boolean lastBooking = false;
    //cancel
    private String cancelBookingId;
    private int cancelBookingCount;
    private String cancelReason;
    private String cancelledBy;
    private String cancelledBookingDate;
    private String cancelUsername;
    private boolean cancel;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getPendingBookingDate() {
        return pendingBookingDate;
    }

    public void setPendingBookingDate(String pendingBookingDate) {
        this.pendingBookingDate = pendingBookingDate;
    }

    public String getPendingBookingLoc() {
        return pendingBookingLoc;
    }

    public void setPendingBookingLoc(String pendingBookingLoc) {
        this.pendingBookingLoc = pendingBookingLoc;
    }

    public int getPendingBookingOtherCount() {
        return pendingBookingOtherCount;
    }

    public void setPendingBookingOtherCount(int pendingBookingOtherCount) {
        this.pendingBookingOtherCount = pendingBookingOtherCount;
    }

    public int getPendingBookingFavCount() {
        return pendingBookingFavCount;
    }

    public void setPendingBookingFavCount(int pendingBookingFavCount) {
        this.pendingBookingFavCount = pendingBookingFavCount;
    }

    public String getConfirmNearestBookingDate() {
        return confirmNearestBookingDate;
    }

    public void setConfirmNearestBookingDate(String confirmNearestBookingDate) {
        this.confirmNearestBookingDate = confirmNearestBookingDate;
    }

    public int getConfirmBookingCount() {
        return confirmBookingCount;
    }

    public void setConfirmBookingCount(int confirmBookingCount) {
        this.confirmBookingCount = confirmBookingCount;
    }

    public String getConfirmBookingLocation() {
        return confirmBookingLocation;
    }

    public void setConfirmBookingLocation(String confirmBookingLocation) {
        this.confirmBookingLocation = confirmBookingLocation;
    }

    public String getConfirmBookingZipcode() {
        return confirmBookingZipcode;
    }

    public void setConfirmBookingZipcode(String confirmBookingZipcode) {
        this.confirmBookingZipcode = confirmBookingZipcode;
    }

    public String getConfirmTotalBookingTime() {
        return confirmTotalBookingTime;
    }

    public void setConfirmTotalBookingTime(String confirmTotalBookingTime) {
        this.confirmTotalBookingTime = confirmTotalBookingTime;
    }

    public int getNearestIntervalMins() {
        return nearestIntervalMins;
    }

    public void setNearestIntervalMins(int nearestIntervalMins) {
        this.nearestIntervalMins = nearestIntervalMins;
    }

    public String getConfirmBuddyName() {
        return confirmBuddyName;
    }

    public void setConfirmBuddyName(String confirmBuddyName) {
        this.confirmBuddyName = confirmBuddyName;
    }

    public String getLastBookingBuddyname() {
        return lastBookingBuddyname;
    }

    public void setLastBookingBuddyname(String lastBookingBuddyname) {
        this.lastBookingBuddyname = lastBookingBuddyname;
    }

    public String getLastBookingId() {
        return lastBookingId;
    }

    public void setLastBookingId(String lastBookingId) {
        this.lastBookingId = lastBookingId;
    }

    public int getDurationBtwLastBooking() {
        return durationBtwLastBooking;
    }

    public void setDurationBtwLastBooking(int durationBtwLastBooking) {
        this.durationBtwLastBooking = durationBtwLastBooking;
    }

    public int getDurationBtwConfirmBooking() {
        return durationBtwConfirmBooking;
    }

    public void setDurationBtwConfirmBooking(int durationBtwConfirmBooking) {
        this.durationBtwConfirmBooking = durationBtwConfirmBooking;
    }

    public String getLastBookingDate() {
        return lastBookingDate;
    }

    public void setLastBookingDate(String lastBookingDate) {
        this.lastBookingDate = lastBookingDate;
    }

    public String getLastBookingLoc() {
        return lastBookingLoc;
    }

    public void setLastBookingLoc(String lastBookingLoc) {
        this.lastBookingLoc = lastBookingLoc;
    }

    public boolean isLastBooking() {
        return lastBooking;
    }

    public void setLastBooking(boolean lastBooking) {
        this.lastBooking = lastBooking;
    }

    public String getCancelBookingId() {
        return cancelBookingId;
    }

    public void setCancelBookingId(String cancelBookingId) {
        this.cancelBookingId = cancelBookingId;
    }

    public int getCancelBookingCount() {
        return cancelBookingCount;
    }

    public void setCancelBookingCount(int cancelBookingCount) {
        this.cancelBookingCount = cancelBookingCount;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getCancelledBookingDate() {
        return cancelledBookingDate;
    }

    public void setCancelledBookingDate(String cancelledBookingDate) {
        this.cancelledBookingDate = cancelledBookingDate;
    }

    public String getCancelUsername() {
        return cancelUsername;
    }

    public void setCancelUsername(String cancelUsername) {
        this.cancelUsername = cancelUsername;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getPendingBeneficiary() {
        return pendingBeneficiary;
    }

    public void setPendingBeneficiary(String pendingBeneficiary) {
        this.pendingBeneficiary = pendingBeneficiary;
    }

    public String getConfirmBeneficiary() {
        return confirmBeneficiary;
    }

    public void setConfirmBeneficiary(String confirmBeneficiary) {
        this.confirmBeneficiary = confirmBeneficiary;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public boolean isFavBooking() {
        return isFavBooking;
    }

    public void setFavBooking(boolean favBooking) {
        isFavBooking = favBooking;
    }

    public String getPendingFavBuddy() {
        return pendingFavBuddy;
    }

    public void setPendingFavBuddy(String pendingFavBuddy) {
        this.pendingFavBuddy = pendingFavBuddy;
    }

    public String getConfirmLastBookingBeneficiary() {
        return confirmLastBookingBeneficiary;
    }

    public void setConfirmLastBookingBeneficiary(String confirmLastBookingBeneficiary) {
        this.confirmLastBookingBeneficiary = confirmLastBookingBeneficiary;
    }
}
