package com.caringaide.user.context;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.caringaide.user.utils.CaringAideApplication;
import com.caringaide.user.utils.SharedPrefsManager;

import static com.caringaide.user.utils.BuddyConstants.APP_COUNTRY_ABBRV;
import static com.caringaide.user.utils.CommonUtilities.appCountryAbbrvDef;

public class BuddyApp {

    private static Activity currentActivity;
    private static String countryName = "";
    private static String countryAbbrv = "";
    private static final String TAG = "BuddyApp";

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        BuddyApp.currentActivity = currentActivity;
        Log.d(TAG," :: " +currentActivity.getLocalClassName());
    }

    public static Context getApplicationContext(){
        return null == BuddyApp.currentActivity? CaringAideApplication.getAppBaseContext():
                BuddyApp.currentActivity.getApplicationContext();
    }

    //static setter and getter country , country abbr

    public static String getCountryName() {
        return countryName;
    }

    /**
     * this value is from locationinfo from HomeActivity
     * @param countryName
     */
    public static void setCountryName(String countryName) {
        BuddyApp.countryName = countryName;
    }

    public static String getCountryAbbrv() {
        return countryAbbrv.isEmpty()? SharedPrefsManager.getInstance().getConfigData(APP_COUNTRY_ABBRV).getConfigValue() :countryAbbrv;
    }

    public static void setCountryAbbrv(String countryAbbrv) {
        BuddyApp.countryAbbrv = countryAbbrv;
    }
}
