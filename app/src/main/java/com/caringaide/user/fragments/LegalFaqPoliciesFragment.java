package com.caringaide.user.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.caringaide.user.R;

import static com.caringaide.user.utils.BuddyConstants.FAQ_URL;
import static com.caringaide.user.utils.BuddyConstants.TC_URL;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class LegalFaqPoliciesFragment extends Fragment {

    private View faqView;

    private ProgressDialog progDailog;
    private WebView faqWebView;

    public LegalFaqPoliciesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        faqView = inflater.inflate(R.layout.fragment_legal_faq_policies, container, false);
        faqWebView = faqView.findViewById(R.id.wv_faq_user);
        openLegalFAQ();
        return faqView;
    }
    private void openLegalFAQ(){
        progDailog = ProgressDialog.show(getActivity(), getString(R.string.loading),
                getString(R.string.please_wait), true);
        progDailog.setCancelable(false);
        faqWebView.getSettings().setJavaScriptEnabled(true);
        faqWebView.getSettings().setLoadWithOverviewMode(true);
        faqWebView.getSettings().setUseWideViewPort(true);
        faqWebView.setWebViewClient(new WebViewClient(){
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

        faqWebView.loadUrl(FAQ_URL);
    }

}
