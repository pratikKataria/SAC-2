package com.caringaide.user.utils.location;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.caringaide.user.R;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteService;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.SharedPrefsManager;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by renjit.
 */
public class LocationGeocodeData {
    private static final String TAG = "LocationGeocodeData";
    private static String LOC_ADDRESS_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
    private static int LOC_ADDRESS=1;
    private static String JSON_ADDRESS_COMPONENTS = "address_components";
    private static String JSON_RESULTS = "results";
    private static String JSON_STATUS = "status";
    private static String JSON_TYPES = "types";
    private static String JSON_STATE = "administrative_area_level_1";
    private static String JSON_POSTAL = "postal_code";
    private static String JSON_COUNTRY = "country";
    private static String JSON_CITY = "administrative_area_level_2";
    private static String JSON_CITY2 = "locality";
    private static String JSON_LOCALITY = "route";
    private static String JSON_STREET_NUMBER = "street_number";
    private static String LONG_NAME= "long_name";
    private static String SHORT_NAME = "short_name";
    private static String JSON_FORMATTED_ADDR = "formatted_address";
    private static String JSON_LAT = "lat";
    private static String JSON_LONG = "lng";
    private static String JSON_GEOMETRY = "geometry";
    private static String JSON_LOCATION = "location";
    private static String JSON_SHORT_NAME = "short_name";
    private static List<String> CITY_EXCEPTIONAL_COUNTRIES = new ArrayList<>();
    private static LocationGeocodeListener s_locationGeocodeListener;

    static{
        CITY_EXCEPTIONAL_COUNTRIES.add("United States");
        CITY_EXCEPTIONAL_COUNTRIES.add("Australia");
    }
    public interface LocationGeocodeListener {
        void onLocationUpdate(LocationInfo locationInfo);
    }

    public static boolean containCountry(String country){
        return CITY_EXCEPTIONAL_COUNTRIES.contains(country);
    }

    /**
     * gets lat and lon for address
     * @param activity
     * @param address
     * @param locationGeocodeListener
     */
    public static void getLocationInfo(Activity activity,String address,LocationGeocodeListener locationGeocodeListener){
        s_locationGeocodeListener = locationGeocodeListener;
        String API_KEY = SharedPrefsManager.getInstance().getConfigData(BuddyConstants.APP_GOOGLE_API).getConfigValue();

        final LocationInfo locationInfo = new LocationInfo();
        locationInfo.setRawAddress(address); //raw address is set to location object
        String url = getAddressUrl(address,API_KEY);
        Log.d(TAG,"geocode url for address " + url);
        makeRemoteCall(activity,url,locationInfo);
    }

    /**
     * gets lat and lon for address. The response obj will have the key send across as a marker for results
     * @param activity
     * @param key
     * @param address
     * @param locationGeocodeListener
     */
    public static void getLocationInfo(Activity activity,String key,String address,LocationGeocodeListener locationGeocodeListener){
        s_locationGeocodeListener = locationGeocodeListener;
        String API_KEY = SharedPrefsManager.getInstance().getConfigData(BuddyConstants.APP_GOOGLE_API).getConfigValue();
        final LocationInfo locationInfo = new LocationInfo();
        locationInfo.setKey(key);
        locationInfo.setRawAddress(address); //raw address is set to location object
        String url = getAddressUrl(address,API_KEY);
        Log.d(TAG,"geocode url for address " + url);
        makeRemoteCall(activity,url,locationInfo);
    }


    /**
     * gets the address for lat and lon
     * @param activity
     * @param lat
     * @param lng
     * @param locationGeocodeListener
     */
    public static void getLocationInfo(Activity activity, Double lat, Double lng, LocationGeocodeListener locationGeocodeListener){
        s_locationGeocodeListener = locationGeocodeListener;
        String API_KEY = SharedPrefsManager.getInstance().getConfigData(BuddyConstants.APP_GOOGLE_API).getConfigValue();
        final LocationInfo locationInfo = new LocationInfo();
        locationInfo.setLatLng(new LatLng(lat,lng)); //raw address is set to location object
        String url = getLatLngUrl(lat+","+lng,API_KEY);
        Log.d(TAG,"geocode url for lat lng" + url);
        makeRemoteCall(activity,url,locationInfo);
    }

    private static String getAddressUrl(String address,String API_KEY) {
        String encodedAddress = null;
        try {
            encodedAddress = URLEncoder.encode(address,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return BuddyConstants.LOC_ADDRESS_URL+"address="+encodedAddress+"&key="+API_KEY;
        //return "https://maps.googleapis.com/maps/api/geocode/json?address=Virginia%20Beach%20Boulevard,%20Virginia%20Beach,%20VA,%20United%20States&key="+API_KEY;
    }

    private static String getLatLngUrl(String latlng,String API_KEY) {
        return BuddyConstants.LOC_ADDRESS_URL+"latlng="+latlng+"&key="+API_KEY;
        //https://maps.googleapis.com/maps/api/geocode/json?latlng=23.0043673,72.5411868999996&key=AIzaSyBO3-9SjQnBibVnJz5-F-nsyr3-SmtAbio
    }


    private static void makeRemoteCall(final Activity activity,String url,final LocationInfo locationInfo){
        final RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                Log.d(TAG,"Handling the response " + remoteResponse);
                handleResponse(activity,remoteResponse,locationInfo);
            }
        }, activity, LOC_ADDRESS);

        RemoteService remoteService = RemoteService.getInstance(activity);
        remoteService.doGet(url,requestParams);
        Log.d(TAG,"submitted the get request for " + url);
    }

    private static void handleResponse(Context context,RemoteResponse remoteResponse,LocationInfo locationInfo){
        if(null != remoteResponse){
            String response = remoteResponse.getResponse();
            Log.d(TAG,"Response for geocode data " + response);
            if(null==response || response.isEmpty()){
               // CommonUtilities.toastShort(context,context.getString(R.string.no_google_location));
                Log.e(TAG,context.getString(R.string.no_google_location));
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString(JSON_STATUS);
                if(status.equalsIgnoreCase("ok")){
                    JSONArray jsonArray = jsonObject.getJSONArray(JSON_RESULTS);
                    //for(int i=0;i<jsonArray.length();i++){ //only first address component is required
                        JSONObject jsonObj = jsonArray.getJSONObject(0);
                        //this object contains address_components array.
                        JSONArray addressComponents = jsonObj.getJSONArray(JSON_ADDRESS_COMPONENTS);
                        parseAddressComponents(addressComponents,locationInfo);
                        String formattedAddress = jsonObj.getString(JSON_FORMATTED_ADDR);
                        locationInfo.setFormattedAddress(formattedAddress);
                        JSONObject locJsonObj = jsonObj.getJSONObject(JSON_GEOMETRY).getJSONObject(JSON_LOCATION);
                        Double latitude = Double.parseDouble(locJsonObj.getString(JSON_LAT));
                        Double longitude = Double.parseDouble(locJsonObj.getString(JSON_LONG));
                       locationInfo.setLatLng(new LatLng(latitude,longitude));
                    //}
                }else{
                    //CommonUtilities.toastShort(context,context.getString(R.string.no_google_location));
                    Log.e(TAG,context.getString(R.string.no_google_location));
                }
                //update location to listener
                s_locationGeocodeListener.onLocationUpdate(locationInfo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static void parseAddressComponents(JSONArray addressComponents,LocationInfo locationInfo) throws JSONException{
        String localCity="";
        //get only the first component
        for(int i=0;i<addressComponents.length();i++){
            JSONObject addressElement = addressComponents.getJSONObject(i);
            JSONArray typesArray = addressElement.getJSONArray(JSON_TYPES);
            if(null != typesArray && typesArray.length() > 0) {
                String type = typesArray.getString(0);
                /** get all types **/
                if (type.equalsIgnoreCase(JSON_COUNTRY)) {
                    locationInfo.setCountry(addressElement.getString(LONG_NAME));
                    locationInfo.setCountryAbbr(addressElement.getString(SHORT_NAME));
                }
                if (type.equalsIgnoreCase(JSON_STATE)) {
                    locationInfo.setState(addressElement.getString(LONG_NAME));
                    locationInfo.setStateAbbr(addressElement.getString(SHORT_NAME));
                }
                if (type.equalsIgnoreCase(JSON_CITY)) {
                    locationInfo.setCity(addressElement.getString(LONG_NAME));
                }
                if (type.equalsIgnoreCase(JSON_CITY2)) {
                    localCity = addressElement.getString(LONG_NAME);
                }
                if (type.equalsIgnoreCase(JSON_POSTAL)) {
                    locationInfo.setPostalCode(addressElement.getString(LONG_NAME));
                }
                if (type.equalsIgnoreCase(JSON_LOCALITY)) {
                    locationInfo.setArea(addressElement.getString(LONG_NAME));
                }
                if (type.equalsIgnoreCase(JSON_STREET_NUMBER)) {
                    locationInfo.setStreetNumber(addressElement.getString(LONG_NAME));
                }
                /**    end getting types  **/
            }
        }
        //check if these are exceptional countries
        if(CITY_EXCEPTIONAL_COUNTRIES.contains(locationInfo.getCountry())){
            locationInfo.setCity(locationInfo.getState());
        }

        //check if city was populated , else choose locality as city
        if(null == locationInfo.getCity() || locationInfo.getCity().isEmpty()){
            locationInfo.setCity(localCity);
        }
    }

    public static class LocationInfo{
        String key;
        String postalCode;
        String streetNumber;
        String city;
        String state;
        String country;
        String area;
        String stateAbbr;
        String countryAbbr;
        String formattedAddress;
        String rawAddress;
        LatLng latLng;


        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getStreetNumber() {
            return streetNumber;
        }

        public void setStreetNumber(String streetNumber) {
            this.streetNumber = streetNumber;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getStateAbbr() {
            return stateAbbr;
        }

        public void setStateAbbr(String stateAbbr) {
            this.stateAbbr = stateAbbr;
        }

        public String getCountryAbbr() {
            return countryAbbr;
        }

        public void setCountryAbbr(String countryAbbr) {
            this.countryAbbr = countryAbbr;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }

        public String getRawAddress() {
            return rawAddress;
        }

        public void setRawAddress(String rawAddress) {
            this.rawAddress = rawAddress;
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public void setLatLng(LatLng latLng) {
            this.latLng = latLng;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

}
