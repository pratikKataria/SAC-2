package com.caringaide.user.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.caringaide.user.R;
import com.caringaide.user.interfaces.SingleClickListener;

import static com.caringaide.user.utils.BuddyConstants.PRIVACY_POLICY_URL;
import static com.caringaide.user.utils.BuddyConstants.REFUND_URL;
import static com.caringaide.user.utils.BuddyConstants.TC_URL;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class LegalTcPoliciesFragment extends Fragment {

    private View view;
    private LinearLayout refundPoicyLayout, privacyPolicyLayout,termsAndConditionsLayout;
    private WebView legalDocWebview;
    private RelativeLayout legalDocWebViewLayout;
    private Button closeWebView;
    private ProgressDialog progDailog;

    public LegalTcPoliciesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.legal_tc_policies_fragment_layout, container, false);
        setView();
        return view;
    }


    private void setView() {
        legalDocWebview = view.findViewById(R.id.wv_legal_data);
        legalDocWebViewLayout = view.findViewById(R.id.legal_layout_user);
        closeWebView = view.findViewById(R.id.close_legal_webview);
        refundPoicyLayout = view.findViewById(R.id.help_refund_policy);
        privacyPolicyLayout = view.findViewById(R.id.help_privacy_policiy);
        termsAndConditionsLayout = view.findViewById(R.id.help_terms_conditions);
        refundPoicyLayout.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openWebView(REFUND_URL);
            }
        });
        privacyPolicyLayout.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openWebView(PRIVACY_POLICY_URL);
            }
        });
        termsAndConditionsLayout.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openWebView(TC_URL);
            }
        });
        closeWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                legalDocWebViewLayout.setVisibility(View.GONE);
            }
        });
    }
    private void openWebView(String url){
        legalDocWebViewLayout.setVisibility(View.VISIBLE);
        // create a WebView
        progDailog = ProgressDialog.show(getActivity(), getString(R.string.loading),
                getString(R.string.please_wait), true);
        progDailog.setCancelable(false);
        legalDocWebview.getSettings().setJavaScriptEnabled(true);
        legalDocWebview.getSettings().setLoadWithOverviewMode(true);
        legalDocWebview.getSettings().setUseWideViewPort(true);
        legalDocWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progDailog.dismiss();
//                        super.onPageFinished(view, url);

            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                toastShort(description+" "+ failingUrl);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        legalDocWebview.loadUrl(url);
    }

}
