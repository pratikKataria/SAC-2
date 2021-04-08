package com.caringaide.user.fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.adapters.BuddyEndAdapter;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.ChangeFragmentListener;
import com.caringaide.user.model.BuddyService;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.CommonUtilities;

import org.json.JSONException;

import java.util.ArrayList;

import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.toastShort;


/**
 * A simple {@link Fragment} subclass.
 */
public class EndListFragment extends Fragment implements ChangeFragmentListener {

    private static final String TAG = "PendingListFragments";
    View endView;
    ArrayList buddyServiceList;
    BuddyEndAdapter buddyServiceAdapter;
    ListView endListView;
    TextView noData;
    Context context;
    public EndListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        endView = inflater.inflate(R.layout.end_list_fragment_layout, container, false);
        //setBackAction();
        buddyServiceList = new ArrayList<BuddyService>();
        setView();
        return endView;
    }
    /**
     * set the list of data to be displayed
     */
    private void setView() {
        endListView = endView.findViewById(R.id.service_data_list);
        buddyServiceAdapter = new BuddyEndAdapter(getContext(), R.layout.end_service_request_layout, buddyServiceList, EndListFragment.this,this);
        endListView.setAdapter(buddyServiceAdapter);
        noData = endView.findViewById(R.id.no_data_tv_end);
        noData.setVisibility(View.GONE);
        endListView.setVisibility(View.VISIBLE);
    }

    private void setBackAction() {
        endView.setFocusableInTouchMode(true);
        endView.requestFocus();
        endView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().beginTransaction().remove(EndListFragment.this).commit();
                    return true;
                }
                return false;
            }
        } );
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void showNoDataView() {
        endListView.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
    }

    /**
     * ChangeFragmentListener
     * @param fragment
     */
    @Override
    public void changeToTargetFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.end_fragment_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
