package com.caringaide.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.caringaide.user.R;
import com.caringaide.user.fragments.ReferFriendsFragment;

import static com.caringaide.user.utils.CommonUtilities.moveToHomeActivity;

public class ReferFriendsActivity extends BaseActivity {

    private static final String TAG = "ReferFriends";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showToolBar(true,getString(R.string.refer_friends));

    }

    @Override
    protected void setView() {
        changeFragment(new ReferFriendsFragment());
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
