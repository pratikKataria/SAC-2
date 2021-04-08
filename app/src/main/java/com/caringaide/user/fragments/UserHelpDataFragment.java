package com.caringaide.user.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.activities.UserHelpActionActivity;
import com.caringaide.user.activities.UserHelpDataActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserHelpDataFragment extends Fragment {

    private View view;
    LinearLayout faqLayout,legalTcLayout,contactLayout;
    public UserHelpDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_help_activity_layout,container,false);
        setView();
        return view;
    }

    private void setView() {
        faqLayout = view.findViewById(R.id.help_faq);
        legalTcLayout = view.findViewById(R.id.help_tc_policies);
        contactLayout = view.findViewById(R.id.help_contact_admin);
        legalTcLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserHelpActionActivity.class);
                intent.putExtra("HelpContext","TCLayout");
                startActivity(intent);
//                changeFragment(new LegalTcPoliciesFragment());
            }
        });
        faqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserHelpActionActivity.class);
                intent.putExtra("HelpContext","faqLayout");
                startActivity(intent);
            }
        });
        contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserHelpActionActivity.class);
                intent.putExtra("HelpContext","contactLayout");
                startActivity(intent);
            }
        });
    }

    private void changeFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.help_container,fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commitAllowingStateLoss();
    }

}
