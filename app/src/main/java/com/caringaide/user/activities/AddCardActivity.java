package com.caringaide.user.activities;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.caringaide.user.R;
import com.caringaide.user.fragments.AddCardFragment;

public class AddCardActivity extends BaseActivity {

    private static final String TAG = "AddCardAct";
    private String benId = "";
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showNavMenu(true);
        showToolBar(true,getString(R.string.add_card_title));
        intent = getIntent();
        if (null!=intent){
            benId = intent.getStringExtra("ben_id");
        }
        setView();

    }

    @Override
    protected void setView() {
        if (null!=intent) {
            AddCardFragment cardFragment = new AddCardFragment();
            Bundle bundle = new Bundle();
            bundle.putString("ben_id", String.valueOf(benId));
            cardFragment.setArguments(bundle);
            changeFragment(cardFragment);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,ListBeneficiaryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        overridePendingTransition(R.anim.view_flipper_right_in,R.anim.view_flipper_left_out);
        startActivity(intent);
//        super.onBackPressed();
//        finish();
    }

    @Override
    protected String TAG() {
        return TAG;
    }
}
