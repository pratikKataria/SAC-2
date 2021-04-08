package com.caringaide.user.activities;

import android.content.Intent;
import android.os.Bundle;

import com.caringaide.user.fragments.ListBookingsFragment;
import com.caringaide.user.fragments.ListBookingsFragmentAlt;

import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;
import static com.caringaide.user.utils.CommonUtilities.showActiveServices;

public class ListBookingsActivity extends BaseActivity {

    Bundle notifyBundle;
    private static final String TAG = "ListBookingsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notifyBundle = getIntent().getExtras();
    }

    @Override
    protected void setView() {
        ListBookingsFragment listBookingsFragment = new ListBookingsFragment(); //new ui
//        ListBookingsFragmentAlt listBookingsFragment = new ListBookingsFragmentAlt(); //old ui
        if (null!=notifyBundle){
            listBookingsFragment.setArguments(notifyBundle);
        }else{
            notifyBundle = getIntent().getExtras();
            listBookingsFragment.setArguments(notifyBundle);
        }
        changeFragment(listBookingsFragment);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (null!=notifyBundle) {
            moveToHomeActivity();
        }else{
            super.onBackPressed();
        }
    }


    @Override
    protected String TAG() {
        return TAG;
    }

}
