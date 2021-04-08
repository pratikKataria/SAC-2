package com.caringaide.user.activities;

import android.os.Bundle;

import com.caringaide.user.R;
import com.caringaide.user.fragments.BookNowFragment;
import com.caringaide.user.utils.CommonUtilities;

public class BookNowActivity extends BaseActivity {

    private static final String TAG = "BookNowActivity";
    String userName = "",serviceId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if(b != null){
            userName = b.getString("user_name");
        }
        showNavMenu(true);
        showToolBar(true,getString(R.string.booking_for).concat(" ").concat(CommonUtilities.setToCamelCase(userName)));
    }

    @Override
    protected void setView() {
        BookNowFragment bookNowFragment = new BookNowFragment();
        changeFragment(bookNowFragment);
    }

    @Override
    protected String TAG() {
        return TAG;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
