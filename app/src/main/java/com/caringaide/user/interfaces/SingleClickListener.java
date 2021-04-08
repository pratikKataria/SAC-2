package com.caringaide.user.interfaces;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;

public abstract class SingleClickListener implements View.OnClickListener {
    private long clickTime = 0;
    private static final String TAG = "SingleClickListener";
    @Override
    public void onClick(View v) {
        if(clickTime == 0){
            onSingleClick(v);
            clickTime = SystemClock.elapsedRealtime();
            Log.d(TAG,"clickTime "+ clickTime);
        }else if(SystemClock.elapsedRealtime() - clickTime > 5000){
            clickTime = SystemClock.elapsedRealtime();
            onSingleClick(v);
            Log.d(TAG,"clicked and the clickTime is "+ clickTime);
        }else{
            //no clicks---
        }
    }
    public abstract void onSingleClick(View v);
}
