package com.caringaide.user.model;

public class Countries {
    private String countryId;
    private String countryName;
    private String countryAbbrv;
    private String currency;
    private String currencyCode;

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryAbbrv() {
        return countryAbbrv;
    }

    public void setCountryAbbrv(String countryAbbrv) {
        this.countryAbbrv = countryAbbrv;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
