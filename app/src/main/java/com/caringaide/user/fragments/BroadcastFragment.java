package com.caringaide.user.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.adapters.BuddyBroadcastAdapter;
import com.caringaide.user.interfaces.RefreshListAdapterListener;

import static com.caringaide.user.utils.CommonUtilities.serviceArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BroadcastFragment extends Fragment implements RefreshListAdapterListener {

    private static final String TAG = "PendingListFragments";
    View pendingView;
    BuddyBroadcastAdapter buddyBroadcastAdapter;
    ListView broadcastListView;
    TextView noData;
    public BroadcastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pendingView = inflater.inflate(R.layout.broadcast_fragment_layout, container, false);
        setView();
        return pendingView;
    }

    /**
     * set the list of data to be displayed
     */
    private void setView() {
        broadcastListView = pendingView.findViewById(R.id.service_data_list);
        buddyBroadcastAdapter = new BuddyBroadcastAdapter(getContext(), R.layout.service_request_layout,
                serviceArrayList, BroadcastFragment.this,this);
        broadcastListView.setAdapter(buddyBroadcastAdapter);
        noData = pendingView.findViewById(R.id.no_data_tv_broadcast);
    }

    private void setBackAction() {
        pendingView.setFocusableInTouchMode(true);
        pendingView.requestFocus();
        pendingView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().beginTransaction().remove(BroadcastFragment.this).commit();
                    return true;
                }
                return false;
            }
        } );
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void showNoDataView() {
        broadcastListView.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * callback of RefreshDataListener, which is invoked when an entry in data is updated
     */
    @Override
    public void onDataRefresh() {
        if (serviceArrayList.size()==0){
            showNoDataView();

        }
    }
}
