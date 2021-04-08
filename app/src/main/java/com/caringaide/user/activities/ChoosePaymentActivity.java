package com.caringaide.user.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.caringaide.user.R;
import com.caringaide.user.context.BuddyApp;

/**
 * unused
 */
public class ChoosePaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_payment);
        BuddyApp.setCurrentActivity(this);
    }
}
