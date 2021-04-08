package com.caringaide.user.fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.caringaide.user.R;
import com.caringaide.user.adapters.AllBuddiesAdapter;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.RefreshListAdapterListener;
import com.caringaide.user.model.AvailableBuddy;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.CommonUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class AvailableBuddyFragment extends Fragment implements RefreshListAdapterListener {

    View availableBuddyView;
    private TextView noDataView;
    ListView availBuddyListView;
    ArrayList<AvailableBuddy> buddyArrayList = new ArrayList<>();
    private AllBuddiesAdapter allBuddiesAdapter;
    private String benId = "";
    private AvailableBuddyListener availableBuddyListener;
    private Context context;

    public AvailableBuddyFragment() {
        // Required empty public constructor
    }

    /**
     * interface to agree when a buddy is being selected as a favorite buddy
     */
    interface AvailableBuddyListener{
        void onBuddySelected();
    }

    /**
     * initialize the listener
     * @param buddyListener
     */
    public void setBuddyListener(AvailableBuddyListener buddyListener){
        this.availableBuddyListener = buddyListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        availableBuddyView = inflater.inflate(R.layout.available_buddy_fragment_layout, container, false);
        setView();
        setBackAction();
        return availableBuddyView;
    }

    /**
     * initialize ui values
     */
    private void setView() {
        noDataView = availableBuddyView.findViewById(R.id.no_buddies_tv);
        availBuddyListView =availableBuddyView.findViewById(R.id.list_available_buddy);
        allBuddiesAdapter = new AllBuddiesAdapter(getContext(), R.layout.show_available_buddy_layout, buddyArrayList, availBuddyListView, AvailableBuddyFragment.this,this);
        availBuddyListView.setAdapter(allBuddiesAdapter);
    }

    /**
     * back action
     */
    private void setBackAction() {
        availableBuddyView.setFocusableInTouchMode(true);
        availableBuddyView.requestFocus();
        availableBuddyView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().beginTransaction().remove(AvailableBuddyFragment.this).commit();
                    return true;
                }
                return false;
            }
        } );
    }

    /**
     * get all buddies
     */
    private void getBuddies() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleAllBuddyListsResponse(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.getAllBuddies(CommonUtilities.selectedBenId,requestParams);
    }

    /**
     * remote response for getting all buddies
     * @param remoteResponse
     */
    private void handleAllBuddyListsResponse(RemoteResponse remoteResponse) {
        context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        if (isAdded()) {
            String customErrorMsg = context.getString(R.string.buddy_list_failed);
            if (null == remoteResponse) {
                toastShort(getString(R.string.buddy_list_failed));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    buddyArrayList.clear();
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                        if (jsonObject.has("data")) {
                            JSONArray responseArray = jsonObject.getJSONArray("data");
                            if (null != responseArray) {
                                for (int i = 0; i < responseArray.length(); i++) {
                                    String id = responseArray.getJSONObject(i).getString("id");
                                    String fullName = responseArray.getJSONObject(i).getString("full_name");
                                    String gender = responseArray.getJSONObject(i).getString("gender");
                                    String mobile = responseArray.getJSONObject(i).getString("mobile");
                                    String email = responseArray.getJSONObject(i).getString("email");
                                    String address = responseArray.getJSONObject(i).getJSONArray("addresses")
                                            .getJSONObject(1).getString("address");
                                    String city = responseArray.getJSONObject(i).getJSONArray("addresses")
                                            .getJSONObject(1).getString("city");
                                    String state = responseArray.getJSONObject(i).getJSONArray("addresses")
                                            .getJSONObject(1).getString("state");
                                    String zipcode = responseArray.getJSONObject(i).getJSONArray("addresses")
                                            .getJSONObject(1).getString("zipcode");
                                    AvailableBuddy availableBuddy = new AvailableBuddy();
                                    availableBuddy.setBuddyId(id);
                                    availableBuddy.setBenId(CommonUtilities.selectedBenId);
                                    availableBuddy.setBuddyFullName(fullName);
                                    availableBuddy.setBuddyGender(gender);
                                    availableBuddy.setBuddyPhone(mobile);
                                    availableBuddy.setBuddyEmail(email);
                                    availableBuddy.setBuddyAddress(address);
                                    availableBuddy.setBuddyZipcode(zipcode);
                                    buddyArrayList.add(availableBuddy);
                                }
                                allBuddiesAdapter.notifyDataSetChanged();
                            }
                        } else {
                            showNoDataView();
                        }
                    }else{
                        showNoDataView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void changeFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.available_buddy_frag_frame,targetFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        this.getFragmentManager().popBackStackImmediate();
        transaction.commit();
    }
    private void showNoDataView() {
        availableBuddyView.setVisibility(View.GONE);
        noDataView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getBuddies();
    }

    /**
     * callback method from refresh data listener
     */
    @Override
    public void onDataRefresh() {
        availableBuddyListener.onBuddySelected();
        getFragmentManager().beginTransaction().remove(AvailableBuddyFragment.this).commit();
    }

}
