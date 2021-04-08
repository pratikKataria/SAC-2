package com.caringaide.user.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.caringaide.user.R;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.model.AvailableBuddy;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.caringaide.user.utils.CommonUtilities.adminEmail;
import static com.caringaide.user.utils.CommonUtilities.alertAndAction;
import static com.caringaide.user.utils.CommonUtilities.hideKeyPad;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteBuddyListFragment extends Fragment implements AvailableBuddyFragment.AvailableBuddyListener,
ViewScheduleTimingFragment.ScheduleTimingListener{

    private View favBuddyView;
    private CardView listBuddy1,listBuddy2, listBuddy3;
    private Button addBuddy1,removeAllBuddies,infoChooseBuddy;
    private Button selectBuddyBtn1,selectBuddyBtn2,selectBuddyBtn3,removeBuddyBtn1,removeBuddyBtn2,removeBuddyBtn3,addBuddyBtn;
    private Button viewSch1,viewSch2,viewSch3;
    private TextView buddyName1,buddyName2,buddyName3;
    private TextView buddyDetails1,buddyDetails2,buddyDetails3;
//    private ImageButton buddyPhone1,buddyPhone2,buddyPhone3;
    private TextView removeSelection,infoTimeDuartion;
    private List<AvailableBuddy> availableBuddyList = new ArrayList<>();
    private LinearLayout selectBuddyLayout;
    private static final String TAG = "FavBuddyListFragment";
    private String buddyId = "";
    private String callerContext = "";
    private RoundedImageView favBuddyImage1,favBuddyImage2,favBuddyImage3;
    FavBuddyListener favBuddyListener;
    private Context context;
    private ProgressDialog waitDialog;

    public FavoriteBuddyListFragment() {
        // Required empty public constructor
    }

    /**
     * after viewing timing (when comes from the booknowfragment), buddy should be set as the selected buddy.
     * @param buddyId
     * @param buddyName
     * @param context
     */
    @Override
    public void afterConfirmingTiming(String buddyId, String buddyName, String context) {
        if (context.equalsIgnoreCase("bookNowFragment")) {
            favBuddyListener.onFavBuddy(buddyId, buddyName);
            isBuddySelected();
        }
        getFragmentManager().beginTransaction().remove(FavoriteBuddyListFragment.this).commit();
    }

    /**
     * interface to agree when a favorite buddy is selected for booking
     */
    interface FavBuddyListener{
        void onFavBuddy(String buddyId, String buddyName);
    }
    public void setFavBuddyListener(FavBuddyListener buddyListener){
        this.favBuddyListener = buddyListener;
    }

    @Override
    public void onStart() {
        getBuddiesForBeneficiaries();
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        onAttach(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null!=waitDialog)
        waitDialog.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        favBuddyView = inflater.inflate(R.layout.favorite_buddy_list_fragment_layout, container, false);
        Bundle bundle = getArguments();
        hideKeyPad();
        if (null != bundle) {
            callerContext = bundle.getString("choose_context");
        }
        setUiComponents();
        setBackAction();
        return favBuddyView;
    }

    private void setUiComponents() {
        viewSch1 = favBuddyView.findViewById(R.id.view_schedule_buddy1);
        viewSch2 = favBuddyView.findViewById(R.id.view_schedule_buddy2);
        viewSch3 = favBuddyView.findViewById(R.id.view_schedule_buddy3);
        listBuddy1 = favBuddyView.findViewById(R.id.buddy_card_view1);
        listBuddy2 = favBuddyView.findViewById(R.id.buddy_card_view2);
        listBuddy3 = favBuddyView.findViewById(R.id.buddy_card_view3);
        addBuddy1 = favBuddyView.findViewById(R.id.buddy_card_view4);
        selectBuddyBtn1 = favBuddyView.findViewById(R.id.book_buddy1);
        selectBuddyBtn2 = favBuddyView.findViewById(R.id.book_buddy2);
        selectBuddyBtn3 = favBuddyView.findViewById(R.id.book_buddy3);
        removeBuddyBtn1 = favBuddyView.findViewById(R.id.remove_buddy1);
        removeBuddyBtn2 = favBuddyView.findViewById(R.id.remove_buddy2);
        removeBuddyBtn3 = favBuddyView.findViewById(R.id.remove_buddy3);
        buddyName1 = favBuddyView.findViewById(R.id.book_buddy_name1);
        buddyName2 = favBuddyView.findViewById(R.id.book_buddy_name2);
        buddyName3 = favBuddyView.findViewById(R.id.book_buddy_name3);
        buddyDetails1 = favBuddyView.findViewById(R.id.book_buddy_details1);
        buddyDetails2 = favBuddyView.findViewById(R.id.book_buddy_details2);
        buddyDetails3 = favBuddyView.findViewById(R.id.book_buddy_details3);
      /*  buddyPhone1 = favBuddyView.findViewById(R.id.book_buddy_phone1);
        buddyPhone2 = favBuddyView.findViewById(R.id.book_buddy_phone2);
        buddyPhone3 = favBuddyView.findViewById(R.id.book_buddy_phone3);*/
        selectBuddyLayout = favBuddyView.findViewById(R.id.select_buddy_premium);
        removeAllBuddies = favBuddyView.findViewById(R.id.remove_all_buddies_tv);
        infoChooseBuddy = favBuddyView.findViewById(R.id.info_choose_buddy_text);
//       infoTimeDuartion = favBuddyView.findViewById(R.id.info_time_duration);
        removeSelection = favBuddyView.findViewById(R.id.remove_selected_buddies_tv);
        //buddy images
        favBuddyImage1 = favBuddyView.findViewById(R.id.favbuddy_img1);
        favBuddyImage2 = favBuddyView.findViewById(R.id.favbuddy_img2);
        favBuddyImage3 = favBuddyView.findViewById(R.id.favbuddy_img3);

        if (callerContext.equalsIgnoreCase("listBeneficiaryFragment")){
            selectBuddyBtn1.setVisibility(View.GONE);
            selectBuddyBtn2.setVisibility(View.GONE);
            selectBuddyBtn3.setVisibility(View.GONE);
            removeSelection.setVisibility(View.GONE);
        }
        isBuddySelected();
     /*   buddyPhone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.connectContact(getActivity(),availableBuddyList.get(0).getBuddyFullName(),availableBuddyList.get(0).getBuddyPhone());
            }
        });
        buddyPhone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.connectContact(getActivity(),availableBuddyList.get(1).getBuddyFullName(),availableBuddyList.get(1).getBuddyPhone());
            }
        });
        buddyPhone3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactUtil.connectContact(getActivity(),availableBuddyList.get(2).getBuddyFullName(),availableBuddyList.get(2).getBuddyPhone());
            }
        });*/
        removeSelection.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                isBuddySelected();
                listBuddy3.setCardBackgroundColor(getResources().getColor(R.color.theme_background));
                listBuddy2.setCardBackgroundColor(getResources().getColor(R.color.theme_background));
                listBuddy1.setCardBackgroundColor(getResources().getColor(R.color.theme_background));
            }
        });
        viewSch1.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showTimeScheduleAndProceed(availableBuddyList.get(0).getBuddyId(),
                        availableBuddyList.get(0).getBuddyFullName());
            }
        });
        viewSch2.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showTimeScheduleAndProceed(availableBuddyList.get(1).getBuddyId(),
                        availableBuddyList.get(1).getBuddyFullName());
            }
        });
        viewSch3.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                showTimeScheduleAndProceed(availableBuddyList.get(2).getBuddyId(),
                        availableBuddyList.get(2).getBuddyFullName());
            }
        });
        selectBuddyBtn1.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (availableBuddyList.size()>0) {
                    buddyId = availableBuddyList.get(0).getBuddyId();
                    listBuddy1.setCardBackgroundColor(getResources().getColor(R.color.theme_purple_light));
                    listBuddy2.setCardBackgroundColor(getResources().getColor(R.color.theme_background));
                    listBuddy3.setCardBackgroundColor(getResources().getColor(R.color.theme_background));
                    //show time schedule. when the user is moving out from the time schedule, go alert the user about the selection
                    //if it is ok, then select the user and move back to the book now fragment
                    showTimeScheduleAndProceed(buddyId, availableBuddyList.get(0).getBuddyFullName());
                    //move it to the new above method
                }

            }
        });
        selectBuddyBtn2.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (availableBuddyList.size()>1) {
                    buddyId = availableBuddyList.get(1).getBuddyId();
                    listBuddy2.setCardBackgroundColor(getResources().getColor(R.color.theme_purple_light));
                    listBuddy1.setCardBackgroundColor(getResources().getColor(R.color.theme_background));
                    listBuddy3.setCardBackgroundColor(getResources().getColor(R.color.theme_background));
                    showTimeScheduleAndProceed(buddyId, availableBuddyList.get(1).getBuddyFullName());
                }
            }
        });
        selectBuddyBtn3.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (availableBuddyList.size()>2) {
                    buddyId = availableBuddyList.get(2).getBuddyId();
                    listBuddy3.setCardBackgroundColor(getResources().getColor(R.color.theme_purple_light));
                    listBuddy2.setCardBackgroundColor(getResources().getColor(R.color.theme_background));
                    listBuddy1.setCardBackgroundColor(getResources().getColor(R.color.theme_background));
                    showTimeScheduleAndProceed(buddyId, availableBuddyList.get(2).getBuddyFullName());
                }
            }
        });
        removeBuddyBtn1.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                alertAndAction(getActivity(), getString(R.string.remove_buddy_title),
                        getString(R.string.ask_delete_buddy),
                        getString(R.string.yes), getString(R.string.no), new AlertAction() {
                            @Override
                            public void positiveAction() {
                                if (availableBuddyList.size()>0) {

                                    buddyId = availableBuddyList.get(0).getBuddyId();
                                    String benId = availableBuddyList.get(0).getBenId();
                                    RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                                        @Override
                                        public void onResponse(RemoteResponse remoteResponse) {
                                            handleRemoveBuddiesResponse(remoteResponse);
                                        }
                                    }, getActivity(), null);
                                    UserServiceHandler.deleteBuddies(benId, buddyId, requestParams);
                                }
                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        });
        removeBuddyBtn2.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                alertAndAction(getActivity(), getString(R.string.ask_delete_buddy),
                        getString(R.string.yes), getString(R.string.no), new AlertAction() {
                            @Override
                            public void positiveAction() {
                                if (availableBuddyList.size()>1) {
                                    buddyId = availableBuddyList.get(1).getBuddyId();
                                    String benId = availableBuddyList.get(1).getBenId();
                                    RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                                        @Override
                                        public void onResponse(RemoteResponse remoteResponse) {
                                            handleRemoveBuddiesResponse(remoteResponse);
                                        }
                                    }, getActivity(), null);
                                    UserServiceHandler.deleteBuddies(benId, buddyId, requestParams);
                                }
                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        });
        removeBuddyBtn3.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                alertAndAction(getActivity(), getString(R.string.app_name), getString(R.string.ask_delete_buddy),
                        getString(R.string.yes), getString(R.string.no), new AlertAction() {
                            @Override
                            public void positiveAction() {
                                if (availableBuddyList.size()>2) {

                                    buddyId = availableBuddyList.get(2).getBuddyId();
                                    String benId = availableBuddyList.get(2).getBenId();
                                    RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                                        @Override
                                        public void onResponse(RemoteResponse remoteResponse) {
                                            handleRemoveBuddiesResponse(remoteResponse);
                                        }
                                    }, getActivity(), null);
                                    UserServiceHandler.deleteBuddies(benId, buddyId, requestParams);
                                }
                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        });
        removeAllBuddies.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                alertAndAction(getActivity(), getString(R.string.delete_all_buddy), getString(R.string.ask_delete_all_buddy),
                        getString(R.string.yes), getString(R.string.no), new AlertAction() {
                            @Override
                            public void positiveAction() {
                                if (availableBuddyList.size()>0) {
                                    String buddyid = "";
                                    String benId = availableBuddyList.get(0).getBenId();
                                    RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                                        @Override
                                        public void onResponse(RemoteResponse remoteResponse) {
                                            handleRemoveAllBuddiesResponse(remoteResponse);

                                        }
                                    }, getActivity(), null);
                                    UserServiceHandler.deleteBuddies(benId, buddyid, requestParams);
                                }
                            }

                            @Override
                            public void negativeAction() {

                            }
                        });
            }
        });
        addBuddy1.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Fragment fragment = getParentFragment();
                AvailableBuddyFragment availableBuddyFragment = new AvailableBuddyFragment();
                availableBuddyFragment.setBuddyListener(FavoriteBuddyListFragment.this);
                changeFragment(availableBuddyFragment);
            }
        });
    }

    private void showTimeScheduleAndProceed(String buddyId, String buddyFullName) {
        //show time schedule
        ViewScheduleTimingFragment viewScheduleTimingFragment = new ViewScheduleTimingFragment();
        Bundle bundle = new Bundle();
        bundle.putString("buddy_id",buddyId);
        bundle.putString("buddy_name",buddyFullName);
        bundle.putString("context",callerContext);
        viewScheduleTimingFragment.setArguments(bundle);
        viewScheduleTimingFragment.setTimingListener(FavoriteBuddyListFragment.this);
        changeFragment(viewScheduleTimingFragment);
    }

    private void setBackAction() {
        favBuddyView.setFocusableInTouchMode(true);
        favBuddyView.requestFocus();
        favBuddyView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().beginTransaction().remove(FavoriteBuddyListFragment.this).commit();
                    return true;
                }
                return false;
            }
        } );
    }
    private void getBuddiesForBeneficiaries() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleBuddyListsResponse(remoteResponse);
            }
        },getActivity(),null);
        UserServiceHandler.getBuddiesForBeneficiary(CommonUtilities.selectedBenId,requestParams);

        waitDialog = CommonUtilities.showProgressDialog(getActivity(),getString(R.string.please_wait));
    }

    private void handleBuddyListsResponse(RemoteResponse remoteResponse) {
        waitDialog.dismiss();
        context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        String customErrorMsg = context.getString(R.string.buddy_list_failed);
        if (null == remoteResponse) {
            toastShort(getString(R.string.buddy_list_failed));
        } else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                availableBuddyList.clear();
                if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    if (jsonObject.has("data")) {
                        JSONArray responseArray = jsonObject.getJSONArray("data");
                        if (null != responseArray) {
                            for (int i=0;i<responseArray.length();i++){
                                String id = responseArray.getJSONObject(i).getString("id");
                                String benId = responseArray.getJSONObject(i).getString("beneficiaries_id");
                                String buddyId = responseArray.getJSONObject(i).getJSONObject("user").getString("id");
                                String fullName = responseArray.getJSONObject(i).getJSONObject("user")
                                        .getString("full_name");
                                String gender = responseArray.getJSONObject(i).getJSONObject("user")
                                        .getString("gender");
                                String mobile = responseArray.getJSONObject(i).getJSONObject("user")
                                        .getString("mobile");
                                String email = responseArray.getJSONObject(i).getJSONObject("user")
                                        .getString("email");
                                AvailableBuddy availableBuddy  = new AvailableBuddy();
                                availableBuddy.setId(id);
                                availableBuddy.setBuddyId(buddyId);
                                availableBuddy.setBenId(benId);
                                availableBuddy.setBuddyFullName(fullName);
                                availableBuddy.setBuddyGender(gender);
                                availableBuddy.setBuddyPhone(mobile);
                                availableBuddy.setBuddyEmail(email);
                                availableBuddyList.add(availableBuddy);
                            }

                        }
                    }else{
                        Log.d(TAG, "handleBuddyListsResponse: some error occured no data from server");
                    }
                }
                populateBuddyList();
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    private void populateBuddyList() {
        int noOfBuddies = availableBuddyList.size();
        if (noOfBuddies<3){
            addBuddy1.setVisibility(View.VISIBLE);
            switch (noOfBuddies){
                case 0:
                    listBuddy1.setVisibility(View.GONE);
                    listBuddy2.setVisibility(View.GONE);
                    listBuddy3.setVisibility(View.GONE);
                    alertAndAction(getActivity(), getString(R.string.info_choose_fav_buddy), getString(R.string.proceed_label),
                            getString(R.string.data_cancel), new AlertAction() {
                                @Override
                                public void positiveAction() {

                                }

                                @Override
                                public void negativeAction() {
                                    getFragmentManager().beginTransaction().remove(FavoriteBuddyListFragment.this).commit();
                                }
                            });
                    break;
                case 1:
                    listBuddy1.setVisibility(View.VISIBLE);
                    listBuddy2.setVisibility(View.GONE);
                    listBuddy3.setVisibility(View.GONE);
                    populateDataValues(noOfBuddies);
                    break;
                case 2:
                    listBuddy1.setVisibility(View.VISIBLE);
                    listBuddy2.setVisibility(View.VISIBLE);
                    listBuddy3.setVisibility(View.GONE);
                    populateDataValues(noOfBuddies);
                    break;

            }
        }else{
            addBuddy1.setVisibility(View.GONE);
            listBuddy1.setVisibility(View.VISIBLE);
            listBuddy2.setVisibility(View.VISIBLE);
            listBuddy3.setVisibility(View.VISIBLE);
            populateDataValues(noOfBuddies);
        }

        isBuddySelected();
    }
    private void populateDataValues(int pos){
        buddyName1.setText(availableBuddyList.get(0).getBuddyFullName());
        buddyDetails1.setText(availableBuddyList.get(0).getBuddyGender()+", "
                +adminEmail);
        getBuddyImage(availableBuddyList.get(0).getBuddyId(),favBuddyImage1);
        if (pos>=2){
            buddyName2.setText(availableBuddyList.get(1).getBuddyFullName());
            buddyDetails2.setText(availableBuddyList.get(1).getBuddyGender()+", "
                    +adminEmail);
            getBuddyImage(availableBuddyList.get(1).getBuddyId(),favBuddyImage2);
        }
        if (pos==3){
            buddyName3.setText(availableBuddyList.get(2).getBuddyFullName());
            buddyDetails3.setText(availableBuddyList.get(2).getBuddyGender()+", "
                    +adminEmail);
            getBuddyImage(availableBuddyList.get(2).getBuddyId(),favBuddyImage3);
        }
    }

    private void isBuddySelected() {
        if (buddyId.isEmpty()){
            removeSelection.setVisibility(View.GONE);
        }else {
            if (!callerContext.equalsIgnoreCase("listBeneficiaryFragment")) {
                removeSelection.setVisibility(View.VISIBLE);
            }
        }
        if (availableBuddyList.isEmpty()){
            removeSelection.setVisibility(View.GONE);
            removeAllBuddies.setVisibility(View.GONE);
        }else{
            removeAllBuddies.setVisibility(View.VISIBLE);
        }
    }

    private void changeFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fav_buddies_frame_layout,fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        this.getFragmentManager().popBackStackImmediate();
        transaction.commit();
    }

    private void handleRemoveBuddiesResponse(RemoteResponse remoteResponse) {
        context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        String customErrorMsg = context.getString(R.string.remove_buddies_failed);
        if (null == remoteResponse) {
            toastShort(getString(R.string.remove_buddies_failed));
        } else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    showAlertMsg(getString(R.string.remove_buddy_title),getString(R.string.remove_buddies_success));
                    getBuddiesForBeneficiaries();
                }
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }
    private void handleRemoveAllBuddiesResponse(RemoteResponse remoteResponse) {
        context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        String customErrorMsg = context.getString(R.string.remove_buddies_failed);
        if (null == remoteResponse) {
            toastShort(getString(R.string.remove_buddies_failed));
        } else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    showAlertMsg(getString(R.string.delete_all_buddy),getString(R.string.delete_all_buddy_success));
                    getBuddiesForBeneficiaries();
                }
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }
    @Override
    public void onBuddySelected() {
        getBuddiesForBeneficiaries();
    }

    private void getBuddyImage(String buddyId, final ImageView imgView) {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteresponse) {
                handleBuddyImage(remoteresponse, imgView);
            }
        }, getActivity(), null);
        UserServiceHandler.getUserImage(buddyId, requestParams);
    }

    private void handleBuddyImage(RemoteResponse remoteResponse, ImageView imgView) {
        context = null!=getContext()?getContext() : BuddyApp.getApplicationContext();
        if (isAdded()) {
            String customErrorMsg = context.getString(R.string.no_profile_image);
            if (null == remoteResponse) {
                Log.d(TAG, "handleProfileImage: " + getString(R.string.no_profile_image));
            } else {
                remoteResponse.setCustomErrorMessage(customErrorMsg);
                try {
                    if (!isErrorsFromResponse(getActivity(), remoteResponse)) {
                        String response = remoteResponse.getResponse();
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("data")) {
                            JSONArray responseArr = jsonObject.getJSONArray("data");
                            Log.d(TAG, "handleBuddyImage: " + responseArr);
//                            Bitmap profileImageData = CommonUtilities.getImageFromString(responseArr.getString(0),
//                                    70, 70);
//                            imgView.setImageBitmap(profileImageData);
                            byte[] imageByteArray = Base64.decode(responseArr.getString(0), Base64.DEFAULT);
                            Glide.with(getActivity())
                                    .load(imageByteArray)
                                    .asBitmap()
                                    .placeholder(R.drawable.user_avatar)
                                    .fitCenter()
                                    .into(imgView);
                        } else {
                            imgView.setImageResource(R.drawable.user_avatar);
                            Log.d(TAG, "handleBuddyImage: " + getString(R.string.no_profile_image));
                        }
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "handleBuddyImage: " + getString(R.string.no_profile_image));
                    e.getLocalizedMessage();
                }
            }
        }

    }
}
