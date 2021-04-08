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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.adapters.BuddyPaidAdapter;
import com.caringaide.user.interfaces.ChangeFragmentListener;

import static com.caringaide.user.utils.CommonUtilities.serviceArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaidListFragment extends Fragment implements ChangeFragmentListener {
    private static final String TAG = "PaidListFragments";
    View paidView;
    BuddyPaidAdapter buddyServiceAdapter;
    RelativeLayout paymentLayout;
    ListView paidListView;
    TextView noData;
    Context context;

    public PaidListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        paidView = inflater.inflate(R.layout.paid_list_fragment_layout, container, false);
        //setBackAction();
        setView();
        return paidView;
    }

    /**
     * initializes  view
     */
    private void setView() {
        paidListView = paidView.findViewById(R.id.paid_service_data_list);
        buddyServiceAdapter = new BuddyPaidAdapter(getContext(), R.layout.paid_service_request_layout, serviceArrayList, paidListView, PaidListFragment.this,this);
        paidListView.setAdapter(buddyServiceAdapter);
        noData = paidView.findViewById(R.id.no_data_tv_end);
        noData.setVisibility(View.GONE);
        paidListView.setVisibility(View.VISIBLE);
    }

    private void setBackAction() {
        paidView.setFocusableInTouchMode(true);
        paidView.requestFocus();
        paidView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().beginTransaction().remove(PaidListFragment.this).commit();
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

    private void showNoDataView(){
        paidListView.setVisibility(View.GONE);
        noData.setVisibility(View.VISIBLE);
    }

    /**
     * callback of change fragment listner
     * @param fragment
     */
    @Override
    public void changeToTargetFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.paid_fragment_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
