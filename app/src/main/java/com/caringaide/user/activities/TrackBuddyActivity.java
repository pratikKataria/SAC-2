package com.caringaide.user.activities;

import android.content.Intent;
import android.os.Bundle;

import com.caringaide.user.R;
import com.caringaide.user.fragments.TrackBuddyFragment;

import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;

public class TrackBuddyActivity extends BaseActivity {

    private static final String TAG = "TrackBuddyAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showNavMenu(false);
        showToolBar(true,getString(R.string.track_buddy_label));
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);
        //setContentView(R.layout.track_buddy_layout);
    }

    @Override
    protected void setView() {
        changeFragment(new TrackBuddyFragment());
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
