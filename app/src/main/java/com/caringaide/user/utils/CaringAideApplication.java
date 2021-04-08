package com.caringaide.user.utils;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

/**
 * Application class that can be used to override the default font family
 */
public class CaringAideApplication extends MultiDexApplication {

    private static Context applicationContext = null;
    @Override
    public void onCreate() {
        /*TypefaceUtil.overrideFont(this,"SANS", "fonts/nunito.ttf");
        TypefaceUtil.overrideFont(this,"SERIF", "fonts/nunito.ttf");
        TypefaceUtil.overrideFont(this,"SANS-SERIF", "fonts/nunito.ttf");*/
        super.onCreate();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        applicationContext = getApplicationContext();
    }

    //returns the application context
    public static Context getAppBaseContext() {
        return applicationContext;
    }
}
