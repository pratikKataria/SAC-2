package com.caringaide.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.caringaide.user.fragments.PaidListFragment;
import com.caringaide.user.utils.CommonUtilities;

public class InvoiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setView() {
        changeFragment(new PaidListFragment());
    }

    @Override
    protected String TAG() {
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CommonUtilities.moveToHomeActivity();
    }
}
