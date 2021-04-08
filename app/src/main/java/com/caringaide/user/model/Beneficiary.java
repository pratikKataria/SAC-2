package com.caringaide.user.model;

import java.util.ArrayList;

public class Beneficiary {
    private String benId;
    private String benType;
    private String benFullName;
    private String benNickName;
    private String benMobile;
    private String benAddress;
    private String benCityId;
    private String benStateId;
    private String benZipcode;
    private String benDob;
    private String benGender;
    private String benComments;
    private String benActive;
    private String benUserId;
    private String benServiceId;
    /*private String benLanguageId;
    private String languageId;
    private String benLanguage;*/
    private String benService;
    private String benRelation;
    private String benServiceDescription;
    private boolean haveCard = false;
    private ArrayList<Cards> cardsList = new ArrayList<>();

    public String getBenId() {
        return benId;
    }

    public void setBenId(String benId) {
        this.benId = benId;
    }

    public String getBenType() {
        return benType;
    }

    public void setBenType(String benType) {
        this.benType = benType;
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

    public String getBenDob() {
        return benDob;
    }

    public void setBenDob(String benDob) {
        this.benDob = benDob;
    }

    public String getBenGender() {
        return benGender;
    }

    public void setBenGender(String benGender) {
        this.benGender = benGender;
    }

    public String getBenComments() {
        return benComments;
    }

    public void setBenComments(String benComments) {
        this.benComments = benComments;
    }

    public String getBenActive() {
        return benActive;
    }

    public void setBenActive(String benActive) {
        this.benActive = benActive;
    }

    public String getBenUserId() {
        return benUserId;
    }

    public void setBenUserId(String benUserId) {
        this.benUserId = benUserId;
    }

    public String getBenServiceId() {
        return benServiceId;
    }

    public void setBenServiceId(String benServiceId) {
        this.benServiceId = benServiceId;
    }

  /*  public String getBenLanguageId() {
        return benLanguageId;
    }

    public void setBenLanguageId(String benLanguageId) {
        this.benLanguageId = benLanguageId;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getBenLanguage() {
        return benLanguage;
    }

    public void setBenLanguage(String benLanguage) {
        this.benLanguage = benLanguage;
    }*/

    public String getBenService() {
        return benService;
    }

    public void setBenService(String benService) {
        this.benService = benService;
    }

    public String getBenServiceDescription() {
        return benServiceDescription;
    }

    public void setBenServiceDescription(String benServiceDescription) {
        this.benServiceDescription = benServiceDescription;
    }

    public boolean isHaveCard() {
        return haveCard;
    }

    public void setHaveCard(boolean haveCard) {
        this.haveCard = haveCard;
    }

    public ArrayList<Cards> getCardsList() {
        return cardsList;
    }

    public void setCardsList(ArrayList<Cards> cardsList) {
        this.cardsList = cardsList;
    }

    public String getBenRelation() {
        return benRelation;
    }

    public void setBenRelation(String benRelation) {
        this.benRelation = benRelation;
    }
}
