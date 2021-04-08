package com.caringaide.user.activities;

import android.content.Intent;
import android.os.Bundle;

import com.caringaide.user.R;
import com.caringaide.user.fragments.SignupFragment;

public class SignupActivity extends BaseActivity {

    private final String TAG = "SignupActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showNavMenu(false);
        showToolBar(true,getString(R.string.signup_title));
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);

        //setContentView(R.layout.signup_activity_layout);
    }
    @Override
    protected void setView() {
        changeFragment(new SignupFragment());
    }

    @Override
    protected String TAG() {
        return TAG;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
