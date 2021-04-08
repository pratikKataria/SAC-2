package com.caringaide.user.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.model.City;
import com.caringaide.user.model.Config;
import com.caringaide.user.model.Countries;
import com.caringaide.user.model.Fares;
import com.caringaide.user.model.Organization;
import com.caringaide.user.model.Services;
import com.caringaide.user.model.Zipcodes;
import com.caringaide.user.utils.location.LocationGeocodeData;
import com.google.android.gms.dynamic.IFragmentWrapper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.caringaide.user.utils.BuddyConstants.ADMIN_CONTACT;
import static com.caringaide.user.utils.BuddyConstants.APP_COUNTRY;
import static com.caringaide.user.utils.BuddyConstants.APP_COUNTRY_ABBRV;
import static com.caringaide.user.utils.BuddyConstants.APP_COUNTRY_CODE_PHN;
import static com.caringaide.user.utils.BuddyConstants.BUDDY_SYNC_TIME;
import static com.caringaide.user.utils.BuddyConstants.COMPANY_ADDRESS;
import static com.caringaide.user.utils.BuddyConstants.CUSTOMER_SUPPORT_EMAIL;
import static com.caringaide.user.utils.BuddyConstants.DEFAULT_RIDE_MiLES_KEY;
import static com.caringaide.user.utils.BuddyConstants.MAX_OTP_VAL;
import static com.caringaide.user.utils.BuddyConstants.MIN_CALL_LIMIT;
import static com.caringaide.user.utils.BuddyConstants.MIN_SERVICE_DURATION;
import static com.caringaide.user.utils.BuddyConstants.MIN_TIP_AMOUNT;
import static com.caringaide.user.utils.BuddyConstants.SCHEDULE_INTERVAL;
import static com.caringaide.user.utils.CommonUtilities.adminAddress;
import static com.caringaide.user.utils.CommonUtilities.adminContact;
import static com.caringaide.user.utils.CommonUtilities.adminEmail;
import static com.caringaide.user.utils.CommonUtilities.appCountryAbbrvDef;
import static com.caringaide.user.utils.CommonUtilities.appCountryCodeDef;
import static com.caringaide.user.utils.CommonUtilities.appCountryDef;
import static com.caringaide.user.utils.CommonUtilities.callLmit;
import static com.caringaide.user.utils.CommonUtilities.defaultRide;
import static com.caringaide.user.utils.CommonUtilities.defaultSchdInterval;
import static com.caringaide.user.utils.CommonUtilities.maxOtpVal;
import static com.caringaide.user.utils.CommonUtilities.minServiceDuration;
import static com.caringaide.user.utils.CommonUtilities.minTipAmount;
import static com.caringaide.user.utils.CommonUtilities.minWaitOngoingRefresh;

/**
 * Created by renjit.
 */
public class SharedPrefsManager {
    public static final String FIREBASE_PREFS_NAME="FCMPrefs";
    public static final String FIREBASE_TOKEN = "firebaseToken";
    private static final String FARE_DETAILS = "fare_details";
    private static final String COUNTRY_DETAILS = "country_details";
    private static final String ORGANIZATION_DETAILS = "organization_details";
    private static final String ZIPCODE_DETAILS = "zipcode_details";
    private static final String ZIPCODE_ID = "zipcode_id";
    private static final String CONFIG_DETAILS = "config_details";
    private static final String SERVICE_DETAILS = "service_details";

    private static final String CURRENT_ADDRESS = "current_addr_data";
    private static final String LOCATION_PREFERNCES = "location_prefernces";
    private static final String CITY_PREFERNCES = "city_prefernces";
    private static final String PROF_IMG_PREFERNCES = "prof_img_prefernces";



    private static final String TAG = "sharedPreferenceManager";
    private static final String AUTH_TOKEN = "auth_token" ;
    private static final String CSRF_TOKEN = "csrf_token";
    private static Context mContext;
    private static SharedPrefsManager sharedPrefsManager;

    private SharedPreferences currentAddressPref;
    private SharedPreferences farePreferences;
    private SharedPreferences servicePreferences;
    private SharedPreferences countryPreferences;
    private SharedPreferences organizationPreferences;
    private SharedPreferences cityPreferences;
    private SharedPreferences zipcodePreferences;
    private SharedPreferences configPreferences;
    private SharedPreferences locationPreferences;
    private SharedPreferences servicesPreferences;
    private SharedPreferences profileImgPreferences;


    private SharedPrefsManager(){
        mContext = BuddyApp.getApplicationContext();
    }


    public static synchronized SharedPrefsManager getInstance(){
        if(null == sharedPrefsManager){
            sharedPrefsManager = new SharedPrefsManager();
        }
        return sharedPrefsManager;
    }

  /*  public static synchronized SharedPrefsManager getInstance(Context context){
        if(null == sharedPrefsManager){
            sharedPrefsManager = new SharedPrefsManager(context.getApplicationContext());
        }
        return sharedPrefsManager;
    }
*/
    private static SharedPreferences getSharedPreferences() {

        return PreferenceManager.getDefaultSharedPreferences(mContext);

    }


    public boolean storeDeviceToken(String token){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FIREBASE_PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FIREBASE_TOKEN,token);
        editor.apply();
        return true;
    }

    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FIREBASE_PREFS_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(FIREBASE_TOKEN,null);
    }

    public void storeAuthToken(String token){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(AUTH_TOKEN,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN,token);
        editor.apply();
        Log.d(TAG,"###### the auth token stored...####"+token);
    }

    public String getAuthToken(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(AUTH_TOKEN,Context.MODE_PRIVATE);
        // Log.d(TAG,"###### the auth token from the preference is "+sharedPreferences.getString("auth-token",""));
        return sharedPreferences.getString(AUTH_TOKEN,"");
    }

    public void storeCsrfToken(String token){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(CSRF_TOKEN,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CSRF_TOKEN,token);
        editor.apply();
        Log.d(TAG,"###### the csrf token stored...####"+token);
    }

    public String getCsrfToken(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(CSRF_TOKEN,Context.MODE_PRIVATE);
        // Log.d(TAG,"###### the csrf token from the preference is "+sharedPreferences.getString("csrf-token",""));
        return sharedPreferences.getString(CSRF_TOKEN,"");
    }
    /**
     * splits a set into key value map.
     * The set is expected to have values like "key=value". This will get split as key and value and saved to map.
     * @param values
     * @return
     */
    private Map<String,String> getMapfromSet(Set<String> values){
        Map<String, String> keyValMap = new HashMap<>();
        for(String keyVal:values){
            String []keyValArr = keyVal.split("=");
            if(null!=keyValArr && keyValArr.length>0) {
                if (keyValArr.length>1) {
                    keyValMap.put(keyValArr[0], keyValArr[1]);
                }else{
                    keyValMap.put(keyValArr[0], null);
                }
            }
        }
        return keyValMap;
    }

    public LocationGeocodeData.LocationInfo createCurrentAddressSession(LocationGeocodeData.LocationInfo locationInfo){
        String strCurrentLoc = locationInfo.getFormattedAddress();
        String strLat = String.valueOf(locationInfo.getLatLng().latitude);
        String strLng = String.valueOf(locationInfo.getLatLng().longitude);
        String state = locationInfo.getState();
        String country = locationInfo.getCountry();
        String postalCode = locationInfo.getPostalCode();
        String city = locationInfo.getCity();
        String route = locationInfo.getArea();
        currentAddressPref = mContext.getSharedPreferences(CURRENT_ADDRESS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = currentAddressPref.edit();
        editor.putString("currentLoc",strCurrentLoc);
        editor.putString("strLat",strLat);
        editor.putString("strLng",strLng);
        editor.putString("state",state);
        editor.putString("country",country);
        editor.putString("city",city);
        //add state as city for few countries, like united states
        if(LocationGeocodeData.containCountry(country)){
            editor.putString("city",state);
            locationInfo.setCity(state);
        }
        editor.putString("postalCode",postalCode);
        editor.putString("area",route);
        //this is temporary and we neeed to remove the City Session  class
        //CitySession.createCitySession(mContext, city);
        editor.apply();
        return locationInfo;
    }

    public void updateCurrentLatLng(String strLat, String strLng){
        currentAddressPref = mContext.getSharedPreferences(CURRENT_ADDRESS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = currentAddressPref.edit();
        editor.putString("strLat",strLat);
        editor.putString("strLng",strLng);
        editor.apply();
    }


    public String getCurrentLat(){
        if(null != currentAddressPref){
            return currentAddressPref.getString("strLat",null);
        }
        return null;
    }

    public String getCurrentLng(){
        if(null != currentAddressPref){
            return currentAddressPref.getString("strLng",null);
        }
        return null;
    }

    public String getCurrentLocation(){
        if(null != currentAddressPref){
            return currentAddressPref.getString("currentLoc",null);
        }
        return null;
    }

    public String getCurrentState(){
        if(null != currentAddressPref){
            return currentAddressPref.getString("state",null);
        }
        return null;
    }

    public String getCountry(){
        if(null != currentAddressPref){
            return currentAddressPref.getString("country",null);
        }
        return null;
    }

    public String getPostalCode(){
        if(null != currentAddressPref){
            return currentAddressPref.getString("postalCode",null);
        }
        return null;
    }

    public String getRoute(){
        if(null != currentAddressPref){
            return currentAddressPref.getString("area",null);
        }
        return null;
    }

    public String getCity(){
        if(null != currentAddressPref){
            return currentAddressPref.getString("city",null);
        }
        return null;
    }

  /*  *//**
     * save the new city to the existing service available cities
     * @param city
     *//*
    public void saveAvailableCities(String city){
        cityPreferences = mContext.getSharedPreferences(CITY_PREFERNCES,Context.MODE_PRIVATE);
        String cities = cityPreferences.getString("cities","");
        if(null != city && !cities.contains(city)) {
            SharedPreferences.Editor editor = cityPreferences.edit();
            StringBuffer strBuf = new StringBuffer(cities);
            strBuf.append(","+city);
            editor.putString("cities", strBuf.toString());
            editor.apply();
        }
    }

    *//**
     * get available cities list as csv string
     * @return
     *//*
    public String getAvailableCity(){
        cityPreferences = mContext.getSharedPreferences(CITY_PREFERNCES,Context.MODE_PRIVATE);
        if(null != cityPreferences){
            return cityPreferences.getString("cities","");
        }
        return null;
    }*/

    /**
     * save user information to shared preferences
     * @param userInfoMap
     */
    public static void createSession(Map<String,String> userInfoMap) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        for(Map.Entry<String,String> entrySet:userInfoMap.entrySet()){
            String value = entrySet.getValue();
            if(null==value){
                value = "";
            }
            editor.putString(entrySet.getKey(),value);
        }
        editor.commit();
    }

    /**
     * get user id from shared preferences
     * @return
     */
    public String getUserID()
    {
        Log.d("UserId-shared ",getSharedPreferences().getString(BuddyConstants.UserInfo.USER_LOGIN_ID, ""));
        return getSharedPreferences().getString("loginID", "");
    }
    /**
     * get user type id from shared preferences
     * @return
     */
    public String getUserTypeID()
    {
        Log.d(TAG, "getUserTypeID: "+getSharedPreferences().getString(BuddyConstants.UserInfo.USER_TYPE_LOGIN_ID,""));
        return getSharedPreferences().getString(BuddyConstants.UserInfo.USER_TYPE_LOGIN_ID, "");
    }
    /**
     * get user email from shared preferences
     * @return
     */
    public String getUserEmail()
    {
        return getSharedPreferences().getString(BuddyConstants.UserInfo.USER_LOGIN_EMAIL, "");
    }
    /**
     * get user full name from shared preferences
     * @return
     */
    public String getUserFullName()
    {
        return getSharedPreferences().getString(BuddyConstants.UserInfo.USER_LOGIN_FULLNAME, "");
    }
    /**
     * get user full name from shared preferences
     * @return
     */
    public String getUserFName()
    {
        return getSharedPreferences().getString(BuddyConstants.UserInfo.USER_LOGIN_FNAME, "");
    }
    /**
     * get user full name from shared preferences
     * @return
     */
    public String getUserLName()
    {
        return getSharedPreferences().getString(BuddyConstants.UserInfo.USER_LOGIN_LNAME, "");
    }
    /**
     * get user mobile from shared preferences
     * @return
     */
    public String getUserMobile()
    {
        return getSharedPreferences().getString(BuddyConstants.UserInfo.USER_LOGIN_MOBILE, "");
    }
    /**
     * get user dob from shared preferences
     * @return
     */
    public String getUserDob()
    {
        return getSharedPreferences().getString(BuddyConstants.UserInfo.USER_LOGIN_DOB, "");
    }/**
     * get user gender from shared preferences
     * @return
     */
    public String getUserGender()
    {
        return getSharedPreferences().getString(BuddyConstants.UserInfo.USER_LOGIN_GENDER, "");
    }
    /**
     * get user's username from shared preferences
     * @return
     */
    public String getUserName()
    {
        return getSharedPreferences().getString(BuddyConstants.UserInfo.USER_LOGIN_USERNAME, "");
    }
    public String getBen()
    {
        return getSharedPreferences().getString(BuddyConstants.UserInfo.USER_LOGIN_BEN, "");
    }
    public String getMktToken()
    {
        return getSharedPreferences().getString(BuddyConstants.UserInfo.USER_MKT_TOKEN, "");
    }
    /**
     * get user's username from shared preferences
     * @return
     */
    public String getUserCurrency()
    {
        return getSharedPreferences().getString(BuddyConstants.UserInfo.USER_CURRENCY, "");
    }

    /**
     * save fare detai;s to shared preferences.
     * @param fare
     */
    public void saveFares(Fares fare) {
        farePreferences = mContext.getSharedPreferences(FARE_DETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = farePreferences.edit();
        editor.putString("fare_id",fare.getFareId());
        editor.putString("serviceId",fare.getServiceId());
        editor.putString("baseFare",fare.getBaseFare());
        editor.putString("extraTime",fare.getExtraTime());
        editor.putString("extraRate",fare.getExtraRate());
        editor.putString("tax",fare.getTax());
        editor.putString("stateId",fare.getStateId());
        editor.putString("countryId",fare.getCountryId());
        editor.putString("cancelFee",fare.getCancelFee());
        editor.putString("extraMileRate",fare.getExtraMileRate());
        editor.putString("countryName",fare.getCountryName());
        editor.putString("countryAbbr",fare.getCountryAbbr());
        editor.putString("currencyCode",fare.getCurrencyCode());
        editor.putString("currency",fare.getCurrency());
        editor.putString("stateName",fare.getStateName());
        editor.putString("stateAbbrv",fare.getSatteAbbrv());
        editor.putString("timeZoneId",fare.getTimeZoneId());
        editor.putString("nationality",fare.getNationality());
        editor.commit();
    }


    /**
     * get fares from shared preferences.
     * @return
     */
    public Fares getFares(){
        farePreferences = mContext.getSharedPreferences(FARE_DETAILS,Context.MODE_PRIVATE);
        Fares fare = new Fares();
        if (null!=farePreferences) {
            fare.setFareId(farePreferences.getString("fare_id",""));
            fare.setFareId(farePreferences.getString("serviceId",""));
            fare.setFareId(farePreferences.getString("baseFare",""));
            fare.setFareId(farePreferences.getString("extraTime",""));
            fare.setFareId(farePreferences.getString("extraRate",""));
            fare.setFareId(farePreferences.getString("tax",""));
            fare.setFareId(farePreferences.getString("stateId",""));
            fare.setFareId(farePreferences.getString("countryId",""));
            fare.setFareId(farePreferences.getString("cancelFee",""));
            fare.setFareId(farePreferences.getString("extraMileRate",""));
            fare.setFareId(farePreferences.getString("countryName",""));
            fare.setFareId(farePreferences.getString("countryAbbr",""));
            fare.setFareId(farePreferences.getString("currencyCode",""));
            fare.setFareId(farePreferences.getString("currency",""));
            fare.setFareId(farePreferences.getString("stateName",""));
            fare.setFareId(farePreferences.getString("stateAbbrv",""));
            fare.setFareId(farePreferences.getString("timeZoneId",""));
            fare.setFareId(farePreferences.getString("nationality",""));
            return fare;
        }return null;
    }

    /**
     * save services to shared preferences
     */
    public void saveService(Map<String, Services> serviceMap) {
        servicePreferences = mContext.getSharedPreferences(SERVICE_DETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = servicePreferences.edit();
        for(Map.Entry<String,Services> services:serviceMap.entrySet()) {
            Services serviceObj = services.getValue();
            Set serviceSet = new TreeSet();
            serviceSet.add(BuddyConstants.ServiceInfo.SERVICE_ID + "=" + serviceObj.getId());
            serviceSet.add(BuddyConstants.ServiceInfo.SERVICE_TYPE + "=" + serviceObj.getType());
            serviceSet.add(BuddyConstants.ServiceInfo.SERVICE_DESCRIPTION + "=" + serviceObj.getDescription());
            serviceSet.add(BuddyConstants.ServiceInfo.SERVICE_FEE + "=" + serviceObj.getSubscriptionFee());
            serviceSet.add(BuddyConstants.ServiceInfo.SERVICE_CREATED + "=" + serviceObj.getCreated());
            editor.putStringSet(serviceObj.getType(),serviceSet);
        }

        editor.commit();
    }

    /**
     * get services from shared preferences
     * @return
     */
    public Services getService(String serviceId){
        servicePreferences = mContext.getSharedPreferences(SERVICE_DETAILS,Context.MODE_PRIVATE);
        Set<String> serviceSet = servicePreferences.getStringSet(serviceId,null);
        if (null != serviceSet){
            Map<String,String> serviceKeyVals = getMapfromSet(serviceSet);
            Services services = new Services();
            if(null != serviceKeyVals) {
                services.setId(serviceKeyVals.get(BuddyConstants.ServiceInfo.SERVICE_ID));
                services.setType(serviceKeyVals.get(BuddyConstants.ServiceInfo.SERVICE_TYPE));
                services.setDescription(serviceKeyVals.get(BuddyConstants.ServiceInfo.SERVICE_DESCRIPTION));
                services.setSubscriptionFee(serviceKeyVals.get(BuddyConstants.ServiceInfo.SERVICE_FEE));
                services.setCreated(serviceKeyVals.get(BuddyConstants.ServiceInfo.SERVICE_CREATED));
                return services;
            }
        }
        return null;
    }
    /**
     * save countries to shared preferences
     */
    public void saveCountry(Map<String, Countries> countryMap) {
        countryPreferences = mContext.getSharedPreferences(COUNTRY_DETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = countryPreferences.edit();
        for(Map.Entry<String,Countries> countries:countryMap.entrySet()) {
            Countries countryObj = countries.getValue();
            Set<String> countrySet = new TreeSet<>();
            countrySet.add(BuddyConstants.CountryInfo.COUNTRY_ID + "=" + countryObj.getCountryId());
            countrySet.add(BuddyConstants.CountryInfo.COUNTRY_NAME + "=" + countryObj.getCountryName());
            countrySet.add(BuddyConstants.CountryInfo.COUNTRY_ABBRV + "=" + countryObj.getCountryAbbrv());
            countrySet.add(BuddyConstants.CountryInfo.CURRENCY + "=" + countryObj.getCurrency());
            countrySet.add(BuddyConstants.CountryInfo.CURRENCY_CODE + "=" + countryObj.getCurrencyCode());
            editor.putStringSet(countryObj.getCountryId(),countrySet);
        }

        editor.commit();
    }
    /**
     * save countries to shared preferences
     */
    public void saveOrganization(Map<String, Organization> organizationMap) {
        organizationPreferences = mContext.getSharedPreferences(ORGANIZATION_DETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = organizationPreferences.edit();
        for(Map.Entry<String,Organization> organization:organizationMap.entrySet()) {
            Organization organizationObj = organization.getValue();
            Set<String> orgSet = new TreeSet<>();
            orgSet.add(BuddyConstants.OrgInfo.ORG_ID+"="+organizationObj.getId());
            orgSet.add(BuddyConstants.OrgInfo.COUNTRY_ID+"="+organizationObj.getCountryId());
            orgSet.add(BuddyConstants.OrgInfo.ORG_NAME+"="+organizationObj.getName());
            orgSet.add(BuddyConstants.OrgInfo.REG_CODE+"="+organizationObj.getRegCode());
            orgSet.add(BuddyConstants.OrgInfo.MOBILE+"="+organizationObj.getMobile());
            orgSet.add(BuddyConstants.OrgInfo.EMAIL+"="+organizationObj.getEmail());
            orgSet.add(BuddyConstants.OrgInfo.FOUNDING_YEAR+"="+organizationObj.getFoundingYear());
            orgSet.add(BuddyConstants.OrgInfo.CITY_ID+"="+organizationObj.getCityId());
            orgSet.add(BuddyConstants.OrgInfo.STATE_ID+"="+organizationObj.getStateId());
            orgSet.add(BuddyConstants.OrgInfo.ZIPCODE+"="+organizationObj.getZipcode());
            orgSet.add(BuddyConstants.OrgInfo.ACTIVE_STATUS+"="+organizationObj.getActiveStatus());
            editor.putStringSet(organizationObj.getCountryId(),orgSet);
        }

        editor.commit();
    }


    /**
     * get country from shared preferences
     * @return
     */
    public Organization getOrganization(String countryId){
        organizationPreferences = mContext.getSharedPreferences(ORGANIZATION_DETAILS,Context.MODE_PRIVATE);
        if (null!=organizationPreferences) {
            Set<String> orgSet = organizationPreferences.getStringSet(countryId, null);
            if (null != orgSet) {
                Map<String, String> orgKeyVals = getMapfromSet(orgSet);
                Organization organization = new Organization();
                if (null != orgKeyVals) {
                    organization.setId(orgKeyVals.get(BuddyConstants.OrgInfo.ORG_ID));
                    organization.setCountryId(orgKeyVals.get(BuddyConstants.OrgInfo.COUNTRY_ID));
                    organization.setName(orgKeyVals.get(BuddyConstants.OrgInfo.ORG_NAME));
                    organization.setRegCode(orgKeyVals.get(BuddyConstants.OrgInfo.REG_CODE));
                    organization.setMobile(orgKeyVals.get(BuddyConstants.OrgInfo.MOBILE));
                    organization.setEmail(orgKeyVals.get(BuddyConstants.OrgInfo.EMAIL));
                    organization.setFoundingYear(orgKeyVals.get(BuddyConstants.OrgInfo.FOUNDING_YEAR));
                    organization.setCityId(orgKeyVals.get(BuddyConstants.OrgInfo.CITY_ID));
                    organization.setStateId(orgKeyVals.get(BuddyConstants.OrgInfo.STATE_ID));
                    organization.setZipcode(orgKeyVals.get(BuddyConstants.OrgInfo.ZIPCODE));
                    organization.setActiveStatus(orgKeyVals.get(BuddyConstants.OrgInfo.ACTIVE_STATUS));
                    return organization;
                }
            }
        }
        return null;
    }
    public void saveCity(Map<String, City> cityMap) {
        cityPreferences = mContext.getSharedPreferences(CITY_PREFERNCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = cityPreferences.edit();
        for(Map.Entry<String,City> cities:cityMap.entrySet()) {
            City cityObj = cities.getValue();
            Set<String> citySet = new TreeSet<>();
            citySet.add(BuddyConstants.CityInfo.CITY_ID+"="+cityObj.getCityId());
            citySet.add(BuddyConstants.CityInfo.CITY_NAME+"="+cityObj.getCityName());
            citySet.add(BuddyConstants.CityInfo.CITY_ABBRV+"="+cityObj.getCityAbbrv());
            citySet.add(BuddyConstants.CityInfo.STATE_ID+"="+cityObj.getStateId());
            citySet.add(BuddyConstants.CityInfo.STATE_NAME+"="+cityObj.getStateName());
            editor.putStringSet(cityObj.getCityName(),citySet);
        }
        editor.commit();
    }
    public void saveZipcode(ArrayList<Zipcodes> zipcodeMap) {
        zipcodePreferences = mContext.getSharedPreferences(ZIPCODE_DETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = zipcodePreferences.edit();
        for(Zipcodes zipObj:zipcodeMap) {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("zip_id=" + zipObj.getZipId()+"@@");
            strBuf.append("zip_name=" + zipObj.getZipcode()+"@@");
            strBuf.append("org_id="+zipObj.getOrgId()+"@@");
            strBuf.append("city_id="+zipObj.getCityId()+"@@");
            strBuf.append("country_id="+zipObj.getCountryId()+"@@");
            strBuf.append("state_id="+zipObj.getStateId()+"@@");
            strBuf.append("state_name="+zipObj.getStateName()+"@@");
            strBuf.append("city_name="+zipObj.getCityName()+"@@");
            strBuf.append("city_abbrv="+zipObj.getCityAbbrv()+"@@");
            strBuf.append("state_abbrv="+zipObj.getStateAbbrv()+"@@");
            editor.putString(zipObj.getZipId(),strBuf.toString());

        }
        editor.commit();
    }
    public void saveZipcodeId(Map<String,String> zipcodeIdMap) {
        zipcodePreferences = mContext.getSharedPreferences(ZIPCODE_ID,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = zipcodePreferences.edit();
        JSONObject jsonObject = new JSONObject(zipcodeIdMap);
        String jsonString = jsonObject.toString();
        editor.remove(BuddyConstants.ZIP_ID).commit();
        editor.putString(BuddyConstants.ZIP_ID, jsonString);
        editor.commit();
    }
    private static Map<String,String> getZipIdMap(){
        Map<String,String> zipIdMap = new HashMap<String,String>();
        SharedPreferences pSharedPref = mContext.getSharedPreferences(ZIPCODE_ID, Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString(BuddyConstants.ZIP_ID, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String k = keysItr.next();
                    String v = (String) jsonObject.get(k);
                    zipIdMap.put(k,v);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return zipIdMap;
    }

    public ArrayList<Zipcodes> getZipcodeList(String zipcode){
        Map<String,String> zipcodeIdMap = getZipIdMap();//from shared pref itself
        zipcodePreferences = mContext.getSharedPreferences(ZIPCODE_DETAILS,Context.MODE_PRIVATE);
        if(null != zipcodePreferences) {
            ArrayList<Zipcodes> zipCodeList = new ArrayList<>();
            for(Map.Entry<String,String> zipcodeName:zipcodeIdMap.entrySet()) {
                if(zipcodeName.getValue().equals(zipcode)){
                    Zipcodes zipcodeObj = new Zipcodes();
                    String cityObj = zipcodePreferences.getString(zipcodeName.getKey(), "");
                    if (null != cityObj) {
                        String[] valArr = cityObj.split("@@");
                        for (int i = 0; i < valArr.length; i++) {
                            String keyValue = valArr[i];
                            if (keyValue.length()>1) {
                                String k = keyValue.split("=")[0];
                                String v = keyValue.split("=")[1];
                                if (k.equals("zip_id"))
                                    zipcodeObj.setZipId(v);
                                if (k.equals("zip_name"))
                                    zipcodeObj.setZipcode(v);
                                if (k.equals("org_id"))
                                    zipcodeObj.setOrgId(v);
                                if (k.equals("country_id"))
                                    zipcodeObj.setCountryId(v);
                                if (k.equals("state_id"))
                                    zipcodeObj.setStateId(v);
                                if (k.equals("state_name"))
                                    zipcodeObj.setStateName(v);
                                if (k.equals("city_id"))
                                    zipcodeObj.setCityId(v);
                                if (k.equals("city_name"))
                                    zipcodeObj.setCityName(v);
                                if (k.equals("city_abbrv"))
                                    zipcodeObj.setCityAbbrv(v);
                                if (k.equals("state_abbrv"))
                                    zipcodeObj.setStateAbbrv(v);
                            }
                        }
                        zipCodeList.add(zipcodeObj);
                    }
                }
            }
            return zipCodeList;
        }
        return null;
    }
    public Zipcodes getZipcodeDetails(String zipcode){
        Map<String,String> zipcodeIdMap = getZipIdMap();//from shared pref itself
        zipcodePreferences = mContext.getSharedPreferences(ZIPCODE_DETAILS,Context.MODE_PRIVATE);
        if(null != zipcodePreferences) {
            for(Map.Entry<String,String> zipcodeName:zipcodeIdMap.entrySet()) {
                if (zipcodeName.getValue().equals(zipcode)) {
                    Zipcodes zipcodeObj = new Zipcodes();
                    String cityObj = zipcodePreferences.getString(zipcodeName.getKey(), "");
                    if (null != cityObj) {
                        String[] valArr = cityObj.split("@@");
                        for (int i = 0; i < valArr.length; i++) {
                            String keyValue = valArr[i];
                            if (keyValue.length() > 1) {
                                String k = keyValue.split("=")[0];
                                String v = keyValue.split("=")[1];
                                if (k.equals("zip_id"))
                                    zipcodeObj.setZipId(v);
                                if (k.equals("zip_name"))
                                    zipcodeObj.setZipcode(v);
                                if (k.equals("org_id"))
                                    zipcodeObj.setOrgId(v);
                                if (k.equals("country_id"))
                                    zipcodeObj.setCountryId(v);
                                if (k.equals("state_id"))
                                    zipcodeObj.setStateId(v);
                                if (k.equals("state_name"))
                                    zipcodeObj.setStateName(v);
                                if (k.equals("city_id"))
                                    zipcodeObj.setCityId(v);
                                if (k.equals("city_name"))
                                    zipcodeObj.setCityName(v);
                                if (k.equals("city_abbrv"))
                                    zipcodeObj.setCityAbbrv(v);
                                if (k.equals("state_abbrv"))
                                    zipcodeObj.setStateAbbrv(v);
                            }
                        }
                    }
                    return zipcodeObj;
                }
            }
        }
        return null;
    }
    public void saveConfig(Map<String, Config> configMap) {
        configPreferences = mContext.getSharedPreferences(CONFIG_DETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = configPreferences.edit();
        for(Map.Entry<String,Config> config:configMap.entrySet()) {
            Config configObj = config.getValue();
            Set<String> configSet = new TreeSet<>();
            configSet.add(BuddyConstants.ConfigData.CONFIG_ID + "=" + configObj.getId());
            configSet.add(BuddyConstants.ConfigData.CONFIG_KEY+"=" + configObj.getConfigKey());
            configSet.add(BuddyConstants.ConfigData.CONFIG_VALUE+"="+configObj.getConfigValue());
            configSet.add(BuddyConstants.ConfigData.CONFIG_UNIT+"="+configObj.getConfigUnit());
            configSet.add(BuddyConstants.ConfigData.CONFIG_DESCRIPTION+"="+configObj.getConfigDescription());
            editor.putStringSet(configObj.getConfigKey(),configSet);
            //set default values to variables
            if (config.getKey().equalsIgnoreCase(DEFAULT_RIDE_MiLES_KEY)) {
                defaultRide = null!=configObj.getConfigValue()?Integer.parseInt(configObj.getConfigValue()):defaultRide;
            }
            if (config.getKey().equalsIgnoreCase(SCHEDULE_INTERVAL)) {
                defaultSchdInterval = null!=configObj.getConfigValue()?Integer.parseInt(configObj.getConfigValue()):defaultSchdInterval;
            }
            if (config.getKey().equalsIgnoreCase(MIN_SERVICE_DURATION)) {
                minServiceDuration = null!=configObj.getConfigValue()?Integer.parseInt(configObj.getConfigValue()):minServiceDuration;
            }
            if (config.getKey().equalsIgnoreCase(MIN_TIP_AMOUNT)) {
                minTipAmount = null!=configObj.getConfigValue()?Integer.parseInt(configObj.getConfigValue()):minTipAmount;
            }
            if (config.getKey().equalsIgnoreCase(BUDDY_SYNC_TIME)) {
                minWaitOngoingRefresh = null!=configObj.getConfigValue()?Integer.parseInt(configObj.getConfigValue()):minWaitOngoingRefresh;
            }
            if (config.getKey().equalsIgnoreCase(MAX_OTP_VAL)) {
                maxOtpVal = null!=configObj.getConfigValue()?Integer.parseInt(configObj.getConfigValue()):maxOtpVal;
            }
            if (config.getKey().equalsIgnoreCase(ADMIN_CONTACT)) {
                adminContact = null!=configObj.getConfigValue()?configObj.getConfigValue():adminContact;
            }
            if (config.getKey().equalsIgnoreCase(CUSTOMER_SUPPORT_EMAIL)) {
                adminEmail = null!=configObj.getConfigValue()?configObj.getConfigValue():adminEmail;
            }
            if (config.getKey().equalsIgnoreCase(COMPANY_ADDRESS)) {
                adminAddress = null!=configObj.getConfigValue()?configObj.getConfigValue():adminAddress;
            }
            if (config.getKey().equalsIgnoreCase(APP_COUNTRY)) {
                appCountryDef = null!=configObj.getConfigValue()?configObj.getConfigValue():appCountryDef;
            }
            if (config.getKey().equalsIgnoreCase(APP_COUNTRY_CODE_PHN)) {
                appCountryCodeDef = null!=configObj.getConfigValue()?configObj.getConfigValue():appCountryCodeDef;
            }
            if (config.getKey().equalsIgnoreCase(APP_COUNTRY_ABBRV)) {
                appCountryAbbrvDef = null!=configObj.getConfigValue()?configObj.getConfigValue():appCountryAbbrvDef;
            }
            if (config.getKey().equalsIgnoreCase(MIN_CALL_LIMIT)) {
                callLmit = null!=configObj.getConfigValue()?Integer.parseInt(configObj.getConfigValue()):callLmit;
            }

        }

        BuddyApp.setCountryName(appCountryDef);
        BuddyApp.setCountryAbbrv(appCountryAbbrvDef);

        editor.commit();
    }
    public Config getConfigData(String configKey){
        configPreferences = mContext.getSharedPreferences(CONFIG_DETAILS,Context.MODE_PRIVATE);
        if(null != configPreferences) {
            Config configObj = new Config();
            Set<String> configStr = configPreferences.getStringSet(configKey, null);
            if (null != configStr) {
                Map<String,String> configMap = getMapfromSet(configStr);
                configObj.setId(configMap.get(BuddyConstants.ConfigData.CONFIG_ID));
                configObj.setConfigKey(configMap.get(BuddyConstants.ConfigData.CONFIG_KEY));
                configObj.setConfigValue(configMap.get(BuddyConstants.ConfigData.CONFIG_VALUE));
                configObj.setConfigUnit(configMap.get(BuddyConstants.ConfigData.CONFIG_UNIT));
                configObj.setConfigDescription(configMap.get(BuddyConstants.ConfigData.CONFIG_DESCRIPTION));
                return configObj;
            }
            return configObj;
        }
        return null;
    }
    /**
     * get country from shared preferences
     * @return
     */
    public Countries getCountryDetails(String id){
        countryPreferences = mContext.getSharedPreferences(COUNTRY_DETAILS,Context.MODE_PRIVATE);
        if(null != countryPreferences) {
            Set<String> countrySet = countryPreferences.getStringSet(id, null);
            if (null != countrySet) {
                Map<String, String> countryKeyVal = getMapfromSet(countrySet);
                Countries countries = new Countries();
                if (null != countryKeyVal) {
                    countries.setCountryId(countryKeyVal.get(BuddyConstants.CountryInfo.COUNTRY_ID));
                    countries.setCountryAbbrv(countryKeyVal.get(BuddyConstants.CountryInfo.COUNTRY_ABBRV));
                    countries.setCountryName(countryKeyVal.get(BuddyConstants.CountryInfo.COUNTRY_NAME));
                    countries.setCurrency(countryKeyVal.get(BuddyConstants.CountryInfo.CURRENCY));
                    countries.setCurrencyCode(countryKeyVal.get(BuddyConstants.CountryInfo.CURRENCY_CODE));
                    return countries;
                }
            }
        }
        return null;
    }
   /* public Countries getCountryDetails(String id){
        countryPreferences = mContext.getSharedPreferences(COUNTRY_DETAILS,Context.MODE_PRIVATE);
        if(null != countryPreferences) {
            Countries countries = new Countries();
            String countryObj = countryPreferences.getString(id, "");
            if (null != countryObj) {
                String[] valArr = countryObj.split("@@");
                for (int i = 0; i < valArr.length; i++) {
                    String keyValue = valArr[i];
                    if (keyValue.length()>1) {
                        String k = keyValue.split("=")[0];
                        String v = keyValue.split("=")[1];
                        if (k.equals("country_id"))
                            countries.setCountryId(v);
                        if (k.equals("country_name"))
                            countries.setCountryName(v);
                        if (k.equals("country_abbrv"))
                            countries.setCountryAbbrv(v);
                        if (k.equals("currency"))
                            countries.setCurrency(v);
                        if (k.equals("currency_code"))
                            countries.setCurrencyCode(v);
                    }
                }
            }
            return countries;
        }
        return null;
    }*/
    public City getCityDetails(String name){
        cityPreferences = mContext.getSharedPreferences(CITY_PREFERNCES,Context.MODE_PRIVATE);
        if(null != cityPreferences) {
            Set<String> citySet = cityPreferences.getStringSet(name, null);
            if (null != citySet) {
                Map<String, String> cityKeyVal = getMapfromSet(citySet);
                City city = new City();
                if (null != cityKeyVal) {
                    city.setCityId(cityKeyVal.get(BuddyConstants.CityInfo.CITY_ID));
                    city.setCityName(cityKeyVal.get(BuddyConstants.CityInfo.CITY_NAME));
                    city.setCityAbbrv(cityKeyVal.get(BuddyConstants.CityInfo.CITY_ABBRV));
                    city.setStateId(cityKeyVal.get(BuddyConstants.CityInfo.STATE_ID));
                    city.setStateName(cityKeyVal.get(BuddyConstants.CityInfo.STATE_NAME));
                    return city;
                }
            }
        }
        return null;
    }
    /**
     * store csrfToken to shared preferences.
     * @param token
     */
    public void storeProfileImage(String token){
        profileImgPreferences = mContext.getSharedPreferences(PROF_IMG_PREFERNCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = profileImgPreferences.edit();
        editor.putString(PROF_IMG_PREFERNCES,token);
        editor.apply();
        Log.d(TAG,"###### the profile stored...####");
    }

    /**
     * get csrf token from shared preferences.
     * @return
     */
    public String getProfileImage(){
        profileImgPreferences = mContext.getSharedPreferences(PROF_IMG_PREFERNCES,Context.MODE_PRIVATE);
        return profileImgPreferences.getString(PROF_IMG_PREFERNCES,"");
    }

    /**
     * clears user session as well as stored datas
     */
    public  void clearSharedPreferences(){

        //choice
        /*choicePref = mContext.getSharedPreferences(CHOICE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = choicePref.edit().clear();
        editor.apply();*/
        //device token
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(FIREBASE_PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor deviceTokenSession = sharedPreferences.edit();
        deviceTokenSession.clear();
        deviceTokenSession.apply();
        //car type
       /* carTypePreferences = mContext.getSharedPreferences(CAR_TYPE_PREFERNCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor carTypeSession = carTypePreferences.edit();
        carTypeSession.clear();
        carTypeSession.apply();*/
        //address session
        currentAddressPref = mContext.getSharedPreferences(CURRENT_ADDRESS,Context.MODE_PRIVATE);
        SharedPreferences.Editor currentAddSession = currentAddressPref.edit();
        currentAddSession.clear();
        currentAddSession.apply();
        //zipcode session
        clearZipcodePrefs();
  /*      //driver ride status
        appStatusPrefs = mContext.getSharedPreferences(DRIVER_RIDE_STATUS,Context.MODE_PRIVATE);
        SharedPreferences.Editor rideStatusSession = appStatusPrefs.edit();
        rideStatusSession.clear();
        rideStatusSession.apply();
        //aide
        appStatusPrefs = mContext.getSharedPreferences(DIVER_AIDE_STATUS,Context.MODE_PRIVATE);
        SharedPreferences.Editor aideSessionDetails = appStatusPrefs.edit().clear();
        aideSessionDetails.apply();*/
        //ride details
        /*rideBookingPrefs = mContext.getSharedPreferences(RIDE_BOOKING_DETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor rideDetailsSession = rideBookingPrefs.edit().clear();
        rideDetailsSession.apply();*/
        //aide details
//        aideBookingPrefs = mContext.getSharedPreferences(AIDE_BOOKING_DETAILS,Context.MODE_PRIVATE);
//        SharedPreferences.Editor aideDetailsSession = aideBookingPrefs.edit().clear();
//        aideDetailsSession.apply();
        //fare
        farePreferences = mContext.getSharedPreferences(FARE_DETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor fareEstSession = farePreferences.edit().clear();
        fareEstSession.apply();
        //location
        locationPreferences = mContext.getSharedPreferences(LOCATION_PREFERNCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor locationSession = locationPreferences.edit().clear();
        locationSession.apply();
        //city pref
        cityPreferences = mContext.getSharedPreferences(CITY_PREFERNCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor cityPrefSession = cityPreferences.edit().clear();
        cityPrefSession.apply();
        //country pref
        countryPreferences = mContext.getSharedPreferences(COUNTRY_DETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor countryPreferencesSession = countryPreferences.edit().clear();
        countryPreferencesSession.apply();
        //available services
       /* servicesPreferences = mContext.getSharedPreferences(ACTIVE_SERVICES_PREFERNCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor availableServiceSession = servicesPreferences.edit().clear();
        availableServiceSession.apply();*/
        //auth-token
        SharedPreferences authTokenPreferences = mContext.getSharedPreferences(AUTH_TOKEN,Context.MODE_PRIVATE);
        SharedPreferences.Editor authSession = authTokenPreferences.edit().clear();
        authSession.apply();
        //csrf-token
        SharedPreferences csrfTokenPreferences = mContext.getSharedPreferences(CSRF_TOKEN,Context.MODE_PRIVATE);
        SharedPreferences.Editor csrfSession = csrfTokenPreferences.edit().clear();
        csrfSession.apply();
        //profile image
        profileImgPreferences = mContext.getSharedPreferences(PROF_IMG_PREFERNCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor profileImgSession = profileImgPreferences.edit().clear();
        profileImgSession.apply();
        clearSharedPrefs();
    }

    public static void clearSharedPrefs()
    {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear(); //clear all stored data
        editor.apply();
    }
    public void clearZipcodePrefs()
    {
        zipcodePreferences = mContext.getSharedPreferences(ZIPCODE_DETAILS,Context.MODE_PRIVATE);
        SharedPreferences.Editor zipSession = zipcodePreferences.edit();
        zipSession.clear();
        zipSession.apply();
    }

}
