package com.caringaide.user.activities;

import android.content.Intent;
import android.os.Bundle;

import com.caringaide.user.R;
import com.caringaide.user.fragments.LegalContactUsFragment;
import com.caringaide.user.fragments.LegalFaqPoliciesFragment;
import com.caringaide.user.fragments.LegalTcPoliciesFragment;

public class UserHelpActionActivity extends BaseActivity {

    private String clickContext = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showNavMenu(false);
        Intent intent = getIntent();
        if (null!= intent){
            clickContext = intent.getStringExtra("HelpContext");
            setView();
        }
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(false);
    }

    private void openFragmentBaseOnContext() {
        switch (clickContext){
            case "faqLayout":
                showToolBar(true,getString(R.string.faq));
                changeFragment(new LegalFaqPoliciesFragment());
                break;
            case "TCLayout":
                showToolBar(true,getString(R.string.agree_terms_privacy));
                changeFragment(new LegalTcPoliciesFragment());
                break;
            case "contactLayout":
                showToolBar(true,getString(R.string.contact_us_label));
                changeFragment(new LegalContactUsFragment());
                break;
                default:
                    break;

        }
    }

    @Override
    protected void setView() {
        if (null!=clickContext) {
            openFragmentBaseOnContext();
        }
    }

    @Override
    protected String TAG() {
        return null;
    }
}
