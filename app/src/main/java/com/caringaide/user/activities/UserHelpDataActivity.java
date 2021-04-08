package com.caringaide.user.activities;

import android.content.Intent;
import android.os.Bundle;

import com.caringaide.user.R;
import com.caringaide.user.fragments.UserHelpDataFragment;

import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;

public class UserHelpDataActivity extends BaseActivity {

    private static final String TAG = "UserHelpActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showNavMenu(false);
        showToolBar(true,getString(R.string.help));
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void setView() {
        changeFragment(new UserHelpDataFragment());
    }

    @Override
    protected String TAG() {
        return TAG;
    }
    @Override
    public void onBackPressed() {
        moveToHomeActivity();
//        super.onBackPressed();
//        Intent intent = new Intent(this,UserHomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
    }
}
