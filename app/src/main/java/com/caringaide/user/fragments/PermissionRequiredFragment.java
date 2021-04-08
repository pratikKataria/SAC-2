package com.caringaide.user.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.caringaide.user.R;


/**
 * A simple {@link Fragment} subclass to open the settings view.
 */
public class PermissionRequiredFragment extends Fragment {

    View reqPermissionView;
    Context context;
    Button openSettingsBtn;
    private static final String TAG = "PermissionRequiredFrag";
    public PermissionRequiredFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        reqPermissionView = inflater.inflate(R.layout.permission_required_fragment_layout, container, false);
        openSettingsBtn = reqPermissionView.findViewById(R.id.open_settings);
        openSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsView();
            }
        });
        return reqPermissionView;
    }

    /**
     * open settings
     */
    private void openSettingsView() {
       // Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        // startActivityForResult(intent, BuddyConstants.SETTINGS_VIEW);

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

}
