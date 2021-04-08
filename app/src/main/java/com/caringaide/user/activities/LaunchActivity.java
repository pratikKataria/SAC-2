package com.caringaide.user.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.caringaide.user.R;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.model.City;
import com.caringaide.user.model.Config;
import com.caringaide.user.model.Countries;
import com.caringaide.user.model.Organization;
import com.caringaide.user.model.Services;
import com.caringaide.user.model.Zipcodes;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.SharedPrefsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.CommonUtilities.getDateAndTimeAsString;
import static com.caringaide.user.utils.CommonUtilities.getZipcodeFromCountryCode;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.isLoggedIn;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

public class LaunchActivity extends AppCompatActivity { // implements AppLocationManager.GeoCodeListener {
    private static final long LOC_UPDATE_INTERVAL = 1000*60*5; // interval for location updates, 5 minutes
    private static final long FASTEST_INTERVAL = 1000; // fastest interval

    private ProgressBar progressBar;
    private ImageView spalshLogo;
    private Animation animation;
    private Map<String, Countries> country_map = new HashMap<>();
    private Map<String, Organization> organizationMap = new HashMap<>();
    private Map<String, City> city_map = new HashMap<>();
    private Map<String, Zipcodes> zipcode_map = new HashMap<>();
    private Map<String, String> zipcodeIdMap = new HashMap<>();
    private ArrayList<Zipcodes> zipCodeArrayList = new ArrayList<>();
    private Map<String, Config> config_map = new HashMap<>();
    private Map<String, Services> service_map = new HashMap<>();
    private boolean isLocationPermissionAdded = false;
    private static final String TAG = "LaunchActivity";
    private boolean isLocationCaptured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BuddyApp.setCurrentActivity(this);
        setContentView(R.layout.launch_activity_layout);
        spalshLogo = findViewById(R.id.splash_logo);
        progressBar = findViewById(R.id.splashProgressBar);
//        animation = AnimationUtils.loadAnimation(this, R.anim.animate_left_to_right);
        animation = AnimationUtils.loadAnimation(this, R.anim.animate_grow);
//        animation.setDuration(1000);
        spalshLogo.startAnimation(animation);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                handShake();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*if (PermissionUtils.checkPlayServices()) {
            if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )
                    != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PermissionCodes.LOCATION_PERMISSION.getPermissionCode());
                isLocationPermissionAdded = false;
            }else {
                isLocationPermissionAdded = true;
                handleLocationInfo();
            }
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
       /* AppLocationManager appLocationManager = AppLocationManager.getInstance(LaunchActivity.this);
        if(appLocationManager.isConnected()){
            appLocationManager.disconnect();
        }*/
    }

    /**
     * handshake method
     */
    private void handShake() {
        RequestParams requestParams = new RequestParams(this::onResponse, this, null);
        UserServiceHandler.handShake(requestParams);
    }


    /**
     * handshake response
     * @param remoteResponse
     */
    private void handShakeResponse(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.handshake_error);
        if (null == remoteResponse) {
            toastShort(getString(R.string.network_error) + "." + getString(R.string.no_internet));
        }else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {

                if (!isErrorsFromResponse(this, remoteResponse)) {
                    JSONObject handShakeObj = new JSONObject(remoteResponse.getResponse());
                    String csrf = handShakeObj.getString("data");
                    SharedPrefsManager.getInstance().storeCsrfToken(csrf);
                    progressBar.setProgress(70);
//                    if (isLocationPermissionAdded) {
                    checkIfLoggedIn();
//                    }
                } else {
                    toastShort(customErrorMsg);
                }
            } catch (JSONException e) {
                toastShort(customErrorMsg);
                e.getLocalizedMessage();
            }
        }
    }

    /**
     * method to check whether the app user is logged-in.
     * If so, it will fetch all the initial data from server and load activities based on the num of clients
     * else it will go to login activity
     */
    private void checkIfLoggedIn() {
        getConfigData();
        getZipcodes();
        getOrganizations();
        if (isLoggedIn()){
            getServices();
            getCountries();
            getCities();
            Log.d(TAG, "User login");
            CommonUtilities.USER_ID = SharedPrefsManager.getInstance().getUserID();
            Log.d(TAG, "USER_ID : " + CommonUtilities.USER_ID);
            String loginBen = SharedPrefsManager.getInstance().getBen();
            //check loginBen to 0 , to go to register ben page
            //no need of adding flags such as ACTIVITY_FLAG_NEW_TASK etc
            Intent intent = new Intent(this, UserHomeActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            overridePendingTransition(R.anim.view_flipper_right_in,R.anim.view_flipper_left_out);
            startActivity(intent);

        }
    }

    private void getOrganizations() {
        RequestParams requestParams = new RequestParams(this::organizatonResponse, this, null);
        UserServiceHandler.getOrganizationData(requestParams);
    }

    private void organizatonResponse(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.country_error);
        if (null == remoteResponse) {
            toastShort(getString(R.string.country_error));
        }else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(this, remoteResponse)) {
                    String response = remoteResponse.getResponse();
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG, "organizationsResponse: " + jsonObject);
                    if (jsonObject.has("data")) {
                        JSONArray responseArr = jsonObject.getJSONArray("data");
                        for (int i = 0; i < responseArr.length(); i++) {
                            JSONObject orgJson = responseArr.getJSONObject(i);
                            Organization organization = new Organization();
                            organization.setId(orgJson.getString("id"));
                            organization.setName(orgJson.getString("name"));
                            organization.setEmail(orgJson.getString("email"));
                            organization.setRegCode(orgJson.getString("registration_code"));
                            organization.setMobile(orgJson.getString("mobile"));
                            organization.setActiveStatus(orgJson.getString("status"));
                            organization.setFoundingYear(orgJson.getString("founding_year"));
                            organization.setCityId(orgJson.getString("city"));
                            organization.setStateId(orgJson.getString("state"));
                            organization.setZipcode(orgJson.getString("zipcode"));
                            String countryId = orgJson.getString("country");
                            organization.setCountryId(countryId);
                            organizationMap.put(countryId,organization);
                        }

                        SharedPrefsManager.getInstance().saveOrganization(organizationMap);


                    } else {
                        Log.e(TAG, "country: " + getString(R.string.country_error));
                        toastShort(getString(R.string.country_error));
                    }
                }

            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    private void getConfigData() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                configDataResponse(remoteresponse);
            }
        }, this, null);
        UserServiceHandler.getConfigData(requestParams);
    }

    private void configDataResponse(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.config_error);
        if (null == remoteResponse) {
            toastShort(getString(R.string.config_error));
        }else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);

            try {
                if (!isErrorsFromResponse(this, remoteResponse)) {
                    String response = remoteResponse.getResponse();
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG, "configDataResponse: "+jsonObject);
                    if (jsonObject.has("data")) {
                        JSONArray responseArray = jsonObject.getJSONArray("data");
                       for (int i=0;i<responseArray.length();i++){
                           Config config = new Config();
                           JSONObject configObject = responseArray.getJSONObject(i);
                           String id  = configObject.getString(BuddyConstants.ConfigData.CONFIG_ID);
                           config.setId(id);
                           String config_name  = configObject.getString(BuddyConstants.ConfigData.CONFIG_KEY);
                           config.setConfigKey(config_name);
                           String config_val  = configObject.getString(BuddyConstants.ConfigData.CONFIG_VALUE);
                           config.setConfigValue(config_val);
                           String config_unit  = configObject.getString(BuddyConstants.ConfigData.CONFIG_UNIT);
                           config.setConfigUnit(config_unit);
                           String config_description  = configObject.getString(BuddyConstants.ConfigData.CONFIG_DESCRIPTION);
                           config.setConfigDescription(config_description);
                           config_map.put(config_name,config);
                       }
                       SharedPrefsManager.getInstance().saveConfig(config_map);


                    } else {
                        Log.e(TAG, "config: " + getString(R.string.config_error));
                        toastShort(getString(R.string.config_error));
                    }
                }

            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    /**
     * get all serviceable zipcodes
     */
    private void getZipcodes() {
        RequestParams requestParams = new RequestParams(this::zipcodeResponse, this, null);
        UserServiceHandler.getZipcodes(requestParams);
    }

    /**
     * server response for getting zipcodes
     * @param remoteResponse response from server
     */
    private void zipcodeResponse(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.zip_error);
        if (null == remoteResponse) {
            toastShort(getString(R.string.zip_error));
        }else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);

            try {
                if (!isErrorsFromResponse(this, remoteResponse)) {
                    String response = remoteResponse.getResponse();
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG, "zipcodeResponse: " + jsonObject);
                    SharedPrefsManager.getInstance().clearZipcodePrefs();
                    if (jsonObject.has("data")) {
                        zipCodeArrayList.clear();
                        String countryCode = CommonUtilities.appCountryCodeDef;
                        JSONArray responseArray = jsonObject.getJSONArray("data");
                        ArrayList<JSONObject> responseArr = null;
                        if (null!=countryCode) {
                            if (countryCode.isEmpty()) {
                                responseArr = getZipcodeFromCountryCode("+1", responseArray);
                            } else {
                                responseArr = getZipcodeFromCountryCode(countryCode, responseArray);
                            }
                        }else{
                            responseArr = getZipcodeFromCountryCode("+1", responseArray);
                        }
                        for (int i = 0; i < responseArr.size(); i++) {
                            JSONObject resObj = responseArr.get(i);
                            Zipcodes zipcodes = new Zipcodes();
                            String zipId = resObj.getString("id");
                            zipcodes.setZipId(zipId);
                            String zipcode = resObj.getString("zipcode");
                            zipcodes.setZipcode(zipcode);
                            String org = resObj.getString("org_id");
                            zipcodes.setOrgId(org);
                            String cityId = resObj.getString("city_id");
                            zipcodes.setCityId(cityId);
                            String cityAbbr = resObj.getJSONObject("city").getString("abbrv");
                            zipcodes.setCityAbbrv(cityAbbr);
                            String cityName = resObj.getJSONObject("city").getString("name");
                            zipcodes.setCityName(cityName);
                            String stateName = resObj.getJSONObject("city").getJSONObject("state").getString("name");
                            zipcodes.setStateName(stateName);
                            String countryId = resObj.getJSONObject("city").getJSONObject("state").getString("country_id");
                            zipcodes.setCountryId(countryId);
                            String stateId = resObj.getJSONObject("city").getJSONObject("state").getString("id");
                            zipcodes.setStateId(stateId);
                            String stateAbbrv = resObj.getJSONObject("city").getJSONObject("state").getString("abbrv");
                            zipcodes.setStateAbbrv(stateAbbrv);
                            zipcodeIdMap.put(zipId,zipcode);
                            zipCodeArrayList.add(zipcodes);
                        }
                        SharedPrefsManager.getInstance().saveZipcodeId(zipcodeIdMap);
                        SharedPrefsManager.getInstance().saveZipcode(zipCodeArrayList);


                    } else {
                        Log.e(TAG, "zipcode: " + getString(R.string.city_error));
                        toastShort(getString(R.string.city_error));
                    }
                }

            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    /**
     * load serviceable cities
     */
    private void getCities() {
        RequestParams requestParams = new RequestParams(this::citiesResponse, this, null);
        UserServiceHandler.getCities(requestParams);
    }

    /**
     * server response for getting cities
     * @param remoteResponse
     */
    private void citiesResponse(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.city_error);
        if (null == remoteResponse) {
            toastShort(getString(R.string.city_error));
        }else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);

            try {
                if (!isErrorsFromResponse(this, remoteResponse)) {
                    String response = remoteResponse.getResponse();
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG, "countriesResponse: " + jsonObject);
                    if (jsonObject.has("data")) {
                        JSONArray responseArr = jsonObject.getJSONArray("data");
                        for (int i = 0; i < responseArr.length(); i++) {
                            City city = new City();
                            String cityId = responseArr.getJSONObject(i).getString("id");
                            city.setCityId(cityId);
                            String cityName = responseArr.getJSONObject(i).getString("name");
                            city.setCityName(cityName);
                            String cityAbbr = responseArr.getJSONObject(i).getString("abbrv");
                            city.setCityAbbrv(cityAbbr);
                            String stateId = responseArr.getJSONObject(i).getString("state_id");
                            city.setStateId(stateId);
                            String stateName = responseArr.getJSONObject(i).getJSONObject("state").getString("name");
                            city.setStateName(stateName);
                            city_map.put(cityId, city);
                        }

                        SharedPrefsManager.getInstance().saveCity(city_map);


                    } else {
                        Log.e(TAG, "city: " + getString(R.string.city_error));
                        toastShort(getString(R.string.city_error));
                    }
                }

            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    /**
     * load serviceable countries
     */
    private void getCountries() {
        RequestParams requestParams = new RequestParams(this::countriesResponse, this, null);
        UserServiceHandler.getCountries(requestParams);
    }

    /**
     * server reponse for getting all countries
     * @param remoteResponse
     */
    private void countriesResponse(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.country_error);
        if (null == remoteResponse) {
            toastShort(getString(R.string.country_error));
        }else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);

            try {
                if (!isErrorsFromResponse(this, remoteResponse)) {
                    String response = remoteResponse.getResponse();
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG, "countriesResponse: " + jsonObject);
                    if (jsonObject.has("data")) {
                        JSONArray responseArr = jsonObject.getJSONArray("data");
                        for (int i = 0; i < responseArr.length(); i++) {
                            Countries country = new Countries();
                            String countryId = responseArr.getJSONObject(i).getString("id");
                            country.setCountryId(countryId);
                            String countryName = responseArr.getJSONObject(i).getString("nationality");
                            country.setCountryName(countryName);
                            String countryAbbr = responseArr.getJSONObject(i).getString("abbrv");
                            country.setCountryAbbrv(countryAbbr);
                            String currencyCode = responseArr.getJSONObject(i).getString("currency_code");
                            country.setCurrencyCode(currencyCode);
                            String currency = responseArr.getJSONObject(i).getString("currency");
                            country.setCurrency(currency);
                            country_map.put(countryId, country);
                        }

                        SharedPrefsManager.getInstance().saveCountry(country_map);


                    } else {
                        Log.e(TAG, "country: " + getString(R.string.country_error));
                        toastShort(getString(R.string.country_error));
                    }
                }

            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    /**
     * unused
     * get all services
     */
    private void getServices() {
        RequestParams requestParams = new RequestParams(this::serviceResponse, this, null);
        UserServiceHandler.getServices(requestParams);
    }

    private void serviceResponse(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.service_error);
        if (null == remoteResponse) {
            toastShort(getString(R.string.service_error));
        }else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(this, remoteResponse)) {
                    String response = remoteResponse.getResponse();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("data")) {
                        JSONArray responseArr = jsonObject.getJSONArray("data");
                        Log.d(TAG, "serviceResponse: " + responseArr);
                        for (int i = 0; i < responseArr.length(); i++) {
                            Services service = new Services();
                            service.setCreated(getDateAndTimeAsString(responseArr.getJSONObject(i).getString("created")));
                            service.setModified(getDateAndTimeAsString(responseArr.getJSONObject(i).getString("modified")));
                            service.setId(responseArr.getJSONObject(i).getString("id"));
                            service.setDescription(responseArr.getJSONObject(i).getString("description"));
                            service.setSubscriptionFee(responseArr.getJSONObject(i).getString("subscription_fee"));
                            service.setType(responseArr.getJSONObject(i).getString("type"));
                            service_map.put(service.getType(), service);
                        }

                        SharedPrefsManager.getInstance().saveService(service_map);
                        Log.d(TAG, "serviceResponse: " + "updated services");

                    } else {
                        Log.e(TAG, "serviceResponse: " + getString(R.string.service_error));
                        toastShort(getString(R.string.service_error));
                    }
                }

            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    private void onResponse(RemoteResponse remoteresponse) {
        handShakeResponse(remoteresponse);
        Log.d(TAG, "handshake onResponse: " + remoteresponse.getResponse());
    }
    /**
     * connect with AppLocationManager for location updates.
     */
   /* private void handleLocationInfo(){
        AppLocationManager appLocationManager = AppLocationManager.getInstance(LaunchActivity.this);
        appLocationManager.createLocationRequest(LOC_UPDATE_INTERVAL,FASTEST_INTERVAL);
        appLocationManager.enableLocationUpdates(true);
        if(!appLocationManager.isConnected()){
            appLocationManager.connect();
        }else{
            appLocationManager.updateCurrentLocation();
        }
    }*/

    /**
     * listener method for location
//     * @param location
     */
  /*  @Override
    public void geocodeUpdates(final Location location) {
        LocationGeocodeData.getLocationInfo(LaunchActivity.this, location.getLatitude(), location.getLongitude(),
                new LocationGeocodeData.LocationGeocodeListener() {
                    @Override
                    public void onLocationUpdate(LocationGeocodeData.LocationInfo locationInfo) {
                        BuddyApp.setCountryName(locationInfo.getCountry());
                        BuddyApp.setCountryAbbrv(locationInfo.getCountryAbbr());
//                        if (!isLocationPermissionAdded) {
                            progressBar.setProgress(100);
                            isLocationPermissionAdded = true;
                        if (!isLocationCaptured) {
                            isLocationCaptured = true;
//                            checkIfLoggedIn();
                        }
//                        }
                        String currentLoc = locationInfo.getFormattedAddress();
                        if (isLoggedIn()) {
                            saveLocationInfo(String.valueOf(locationInfo.getLatLng().latitude), String.valueOf(location.getLongitude()),
                                    currentLoc);
                        }
                        //update to db
                        LocationGeocodeData.LocationInfo locInfo = SharedPrefsManager.getInstance().createCurrentAddressSession(locationInfo);
                        Log.d(TAG,"Location lat and lng " + location.getLatitude() + " , " + location.getLongitude());
                    }
                });
    }*/


    /**
     * listener method of GeoCodelistner. This if for getting location update from location manager
//     * @param locationInfo reference variable which contains all the location data
     */
  /*  @Override
    public void currentLocationUpdate(LocationGeocodeData.LocationInfo locationInfo) {
        Log.d(TAG,"current location update " + locationInfo.getLatLng().toString());
        BuddyApp.setCountryName(locationInfo.getCountry());
        BuddyApp.setCountryAbbrv(locationInfo.getCountryAbbr());
            progressBar.setProgress(100);
            isLocationPermissionAdded = true;
        if (!isLocationCaptured) {
            isLocationCaptured = true;
//            checkIfLoggedIn();
        }
        if (isLoggedIn()) {
            String lat = String.valueOf(locationInfo.getLatLng().latitude);
            String lon = String.valueOf(locationInfo.getLatLng().longitude);
            String address = locationInfo.getFormattedAddress();
//            saveLocationInfo(lat, lon, address);
        }
    }*/

    /**
     * save the user location info in server
//     * @param latitude current latitude of application
//     * @param longitude current longitude of application
//     * @param address current address
     */
  /*  private void saveLocationInfo(final String latitude, final String longitude,String address) {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                SharedPrefsManager.getInstance().updateCurrentLatLng(latitude,longitude );
            }
        },this,null);
        Map<String,String> params = new HashMap<>();
        params.put("latitude",latitude);
        params.put("longitude",longitude);
        params.put("location",address);
        requestParams.setRequestParams(params);
        UserServiceHandler.saveLocationInfo(requestParams);
    }*/

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0]==0) {
            if (requestCode ==1) {
//                handleLocationInfo();
                toastShort(getString(R.string.permission_granted));
            }
        }else{
           showActionSnackbar(CommonUtilities.getPermissionMessage(requestCode),
                    getString(R.string.settings),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonUtilities.openSettingsView();
                        }
                    });
        }
    }*/
}

