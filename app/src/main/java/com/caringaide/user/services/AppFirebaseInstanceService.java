package com.caringaide.user.services;

import android.content.ContextWrapper;
import android.util.Log;

import com.caringaide.user.utils.SharedPrefsManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by renjit.
 */
public class AppFirebaseInstanceService extends FirebaseInstanceIdService {

    private static final String TAG = "AppFirebaseService";

    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Displaying token in logcat
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        Log.d("TAG"+"-token", token);
        SharedPrefsManager.getInstance().storeDeviceToken(token);
    }
}
