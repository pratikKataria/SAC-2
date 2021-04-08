package com.caringaide.user.activities;

import android.os.Bundle;

import com.caringaide.user.R;
import com.caringaide.user.fragments.ListBeneficiaryFragment;
import com.caringaide.user.utils.CommonUtilities;

public class ListBeneficiaryActivity extends BaseActivity {

    private static final String TAG = "ListBeneficiaryActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showToolBar(true,getString(R.string.select_beneficiary));
    }

    @Override
    protected void setView() {
        changeFragment(new ListBeneficiaryFragment());
    }

    @Override
    protected String TAG() {
        return TAG;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        CommonUtilities.moveToHomeActivity();
    }
}
