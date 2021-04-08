package com.caringaide.user.model;

public class BuddyService {
    private String id;
    private String buddyId;
    private String beneficiaryId;
    private String userId;
    private String status;
    private String bookingData;
    private String endDate="";
    private String startDate;
    private String totalTime;
    private String serviceId;
    private String message;
    private String latitude;
    private String longitude;
    private String location;
    private String zipcode;
    private String expectedServicetime;
    private String benFullName;
    private String benNickName;
    private String benMobile;
    private String benAddress;
    private String benCityId;
    private String benStateId;
    private String benZipcode;
    private String benComments;
    private String benDob;
    private String userFName;
    private String userLName;
    private String userEmail;
    private String userMobile;
    private String benType;
    private String benUserId;
    private String gender;
    private String benAbout;
    private String cancelledBy;
    private String cancelledMessage = "";
    private String feedbackRating="0";
    private String feedbackComments;
    private String totalAmount;
    private String countryId;
    private String buddySkills;
    private String buddyName;
    private String buddyGender;
    private String buddyMobile;
    private String buddyEmail;
    private String buddyLat;
    private String buddyLng;
    private String buddyLoc;
    private boolean isRated=false;
    private String paymentId;
    private String tipAmount;
    private String cardId;
    private String baseFare;
    private String tax;
    private String extnDuration;
    private String displayId;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
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

    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(String beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookingData() {
        return bookingData;
    }

    public void setBookingData(String bookingData) {
        this.bookingData = bookingData;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getExpectedServicetime() {
        return expectedServicetime;
    }

    public void setExpectedServicetime(String expectedServicetime) {
        this.expectedServicetime = expectedServicetime;
    }

    public String getBenFullName() {
        return benFullName;
    }

    public void setBenFullName(String benFullName) {
        this.benFullName = benFullName;
    }

    public String getBenNickName() {
        return benNickName;
    }

    public void setBenNickName(String benNickName) {
        this.benNickName = benNickName;
    }

    public String getBenMobile() {
        return benMobile;
    }

    public void setBenMobile(String benMobile) {
        this.benMobile = benMobile;
    }

    public String getBenAddress() {
        return benAddress;
    }

    public void setBenAddress(String benAddress) {
        this.benAddress = benAddress;
    }

    public String getBenCityId() {
        return benCityId;
    }

    public void setBenCityId(String benCityId) {
        this.benCityId = benCityId;
    }

    public String getBenStateId() {
        return benStateId;
    }

    public void setBenStateId(String benStateId) {
        this.benStateId = benStateId;
    }

    public String getBenZipcode() {
        return benZipcode;
    }

    public void setBenZipcode(String benZipcode) {
        this.benZipcode = benZipcode;
    }

    public String getBenComments() {
        return benComments;
    }

    public void setBenComments(String benComments) {
        this.benComments = benComments;
    }

    public String getBenDob() {
        return benDob;
    }

    public void setBenDob(String benDob) {
        this.benDob = benDob;
    }

    public String getUserFName() {
        return userFName;
    }

    public void setUserFName(String userFName) {
        this.userFName = userFName;
    }

    public String getUserLName() {
        return userLName;
    }

    public void setUserLName(String userLName) {
        this.userLName = userLName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getBenType() {
        return benType;
    }

    public void setBenType(String benType) {
        this.benType = benType;
    }

    public String getBenUserId() {
        return benUserId;
    }

    public void setBenUserId(String benUserId) {
        this.benUserId = benUserId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBenAbout() {
        return benAbout;
    }

    public void setBenAbout(String benAbout) {
        this.benAbout = benAbout;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getCancelledMessage() {
        return cancelledMessage;
    }

    public void setCancelledMessage(String cancelledMessage) {
        this.cancelledMessage = cancelledMessage;
    }

    public String getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(String feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public String getFeedbackComments() {
        return feedbackComments;
    }

    public void setFeedbackComments(String feedbackComments) {
        this.feedbackComments = feedbackComments;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public boolean isRated() {
        return isRated;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }


    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getBuddySkills() {
        return buddySkills;
    }

    public void setBuddySkills(String buddySkills) {
        this.buddySkills = buddySkills;
    }

    public String getBuddyName() {
        return buddyName;
    }

    public void setBuddyName(String buddyName) {
        this.buddyName = buddyName;
    }

    public String getBuddyMobile() {
        return buddyMobile;
    }

    public void setBuddyMobile(String buddyMobile) {
        this.buddyMobile = buddyMobile;
    }

    public String getBuddyEmail() {
        return buddyEmail;
    }

    public void setBuddyEmail(String buddyEmail) {
        this.buddyEmail = buddyEmail;
    }

    public String getBuddyLat() {
        return buddyLat;
    }

    public void setBuddyLat(String buddyLat) {
        this.buddyLat = buddyLat;
    }

    public String getBuddyLng() {
        return buddyLng;
    }

    public void setBuddyLng(String buddyLng) {
        this.buddyLng = buddyLng;
    }

    public String getBuddyLoc() {
        return buddyLoc;
    }

    public void setBuddyLoc(String buddyLoc) {
        this.buddyLoc = buddyLoc;
    }

    public String getBuddyGender() {
        return buddyGender;
    }

    public void setBuddyGender(String buddyGender) {
        this.buddyGender = buddyGender;
    }

    public String getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(String tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getExtnDuration() {
        return extnDuration;
    }

    public void setExtnDuration(String extnDuration) {
        this.extnDuration = extnDuration;
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }
}
