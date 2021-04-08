package com.caringaide.user.activities;

import android.content.Intent;
import android.os.Bundle;

import com.caringaide.user.R;
import com.caringaide.user.fragments.RegisterBeneficiaryFragment;

import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;

public class RegisterBeneficiaryActivity extends BaseActivity {

    private static final String TAG ="RegisterBenActivity";
    private String clickContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showNavMenu(false);
        showToolBar(true,getString(R.string.register_ben));
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);
        Bundle b = getIntent().getExtras();
        if(b != null){
            clickContext = b.getString("clickContext");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void setView() {
        changeFragment(new RegisterBeneficiaryFragment());
    }

    @Override
    public void onBackPressed() {
        if (null!=clickContext){
            super.onBackPressed();
        }else {
            moveToHomeActivity();
        }
//        Intent intent = new Intent(this,UserHomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
    }

    @Override
    protected String TAG() {
        return TAG;
    }
}
