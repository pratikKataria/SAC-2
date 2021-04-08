package com.caringaide.user.activities;

import android.content.Intent;
import android.os.Bundle;

import com.caringaide.user.R;
import com.caringaide.user.fragments.SettingsFragment;

import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showNavMenu(false);
        showToolBar(true,getString(R.string.settings));
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void setView() {
        changeFragment(new SettingsFragment());
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        moveToHomeActivity();
    }

    @Override
    protected String TAG() {
        return TAG;
    }
}
