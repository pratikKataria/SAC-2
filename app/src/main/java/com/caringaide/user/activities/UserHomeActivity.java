package com.caringaide.user.activities;

import android.os.Bundle;

import com.caringaide.user.R;
import com.caringaide.user.fragments.AltHomeFragment;

public class UserHomeActivity extends BaseActivity {

    private static final String TAG = "AltHomeAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showToolBar(true,getString(R.string.app_name));
    }

    @Override
    protected void setView() {
        changeFragment(new AltHomeFragment());
    }

    @Override
    protected String TAG() {
        return TAG;
    }

    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
    }
}
