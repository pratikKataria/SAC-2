package com.caringaide.user.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.ContactUtil;

import static com.caringaide.user.utils.CommonUtilities.adminAddress;
import static com.caringaide.user.utils.CommonUtilities.adminContact;
import static com.caringaide.user.utils.CommonUtilities.adminEmail;

/**
 * A simple {@link Fragment} subclass.
 */
public class LegalContactUsFragment extends Fragment {


    private TextView mobileTextView,emailTextView,addressTextView;
    private View view;
    public LegalContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_legal_contact_us, container, false);
        setView();
        return view;
    }

    private void setView() {
        mobileTextView = view.findViewById(R.id.call_us_result);
        emailTextView = view.findViewById(R.id.email_result);
        addressTextView = view.findViewById(R.id.address_result);
        mobileTextView.setText(adminContact);
        emailTextView.setText(adminEmail);
        addressTextView.setText(adminAddress);
        mobileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactUtil.connectContact(getActivity(),"admin",adminContact);
            }
        });

    }

}
