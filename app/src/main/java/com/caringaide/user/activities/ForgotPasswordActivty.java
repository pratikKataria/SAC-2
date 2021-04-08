package com.caringaide.user.activities;

import android.content.Intent;
import android.os.Bundle;

import com.caringaide.user.R;
import com.caringaide.user.fragments.ForgotPwdFragment;
import com.caringaide.user.utils.CommonUtilities;

public class ForgotPasswordActivty extends BaseActivity {

    private static final String TAG = "ForgotPasswordAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showNavMenu(false);
        showToolBar(true,getString(R.string.reset_password));
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);

    }

    @Override
    protected void setView() {
        changeFragment(new ForgotPwdFragment());
    }

    @Override
    protected String TAG() {
        return TAG;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finishAfterTransition();
    }
}
