package com.caringaide.user.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.caringaide.user.R;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.interfaces.SingleClickListener;
import com.caringaide.user.remote.RemoteResponse;
import com.caringaide.user.remote.RemoteServiceListener;
import com.caringaide.user.remote.RequestParams;
import com.caringaide.user.remote.UserServiceHandler;
import com.caringaide.user.utils.AlertAction;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.CommonUtilities;
import com.caringaide.user.utils.RoundedImageView;
import com.caringaide.user.utils.SharedPrefsManager;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.caringaide.user.utils.CommonUtilities.clearCallCache;
import static com.caringaide.user.utils.CommonUtilities.isErrorsFromResponse;
import static com.caringaide.user.utils.CommonUtilities.isLoggedIn;
import static com.caringaide.user.utils.CommonUtilities.serviceArrayList;
import static com.caringaide.user.utils.CommonUtilities.showActiveServices;
import static com.caringaide.user.utils.CommonUtilities.showAlertMsg;
import static com.caringaide.user.utils.CommonUtilities.showProgressDialog;
import static com.caringaide.user.utils.CommonUtilities.toastLong;
import static com.caringaide.user.utils.CommonUtilities.toastShort;

public abstract class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected ActionBar actionbar;
    private FrameLayout activeServicesLayout;
    private Animation upAnimation;
    private static final String TAG = "BaseActivity";
    private Dialog progressDialog;
    private String clickContext;
    private TextView addClient,trackBuddy,startBuddy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BuddyApp.setCurrentActivity(this);
        setContentView(R.layout.activity_base);
        setView();
        //setMenu();
        showToolBar(true,getString(R.string.app_name));
        showNavMenu(true);
        if (isLoggedIn() && !showActiveServices) {
            loadTransitBookingsData();
        }
        activeServicesLayout = findViewById(R.id.ongoing_notify);
        activeServicesLayout.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                loadOngoing();
                activeServicesLayout.setVisibility(View.GONE);
            }
        });
        upAnimation= AnimationUtils.loadAnimation(this, R.anim.push_up_upwards);
        upAnimation.setDuration(2000);
    }
    protected abstract void setView();
    protected abstract String TAG();

    //protected void setMenu(){ }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!=progressDialog)
        progressDialog.dismiss();
    }

    @Override
    protected void onStart() {
        BuddyApp.setCurrentActivity(this);
        if(null == navigationView) {
            navigationView = findViewById(R.id.base_navigation_view);
        }
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.logo_username);
        TextView nav_mktcode = (TextView)hView.findViewById(R.id.logo_usermkt_code);
        nav_user.setText(SharedPrefsManager.getInstance().getUserFullName());
        if (SharedPrefsManager.getInstance().getProfileImage().isEmpty()){
            getProfileImage();
        }else{
            setProfileImageData(SharedPrefsManager.getInstance().getProfileImage());
        }
        if (!SharedPrefsManager.getInstance().getMktToken().isEmpty()){
            nav_mktcode.setText(SharedPrefsManager.getInstance().getMktToken());
        }
        super.onStart();
    }

    /**
     * show tool bar
     * @param show
     * @param title
     */
    protected void showToolBar(boolean show, String title){
        if(show){
            androidx.appcompat.widget.Toolbar toolBar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolBar);
            actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + title + "</font>"));
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    /**
     * show side menu bar
     * @param show
     */
    protected void showNavMenu(boolean show){
        if(show) {
            drawerLayout = findViewById(R.id.base_drawer_layout);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            navigationView =  findViewById(R.id.base_navigation_view);
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            // set item as selected to persist highlight
                            menuItem.setChecked(true);
                            // close drawer when item is tapped
                            drawerLayout.closeDrawers();

                            onMenuItemClick(menuItem);
                            // Add code here to update the UI based on the item selected
                            // For example, swap UI fragments here

                            return true;
                        }
                    });
            addClient=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                    findItem(R.id.add_ben));
            trackBuddy=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                    findItem(R.id.show_route));
            startBuddy=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                    findItem(R.id.nav_ongoing_service));
            initializeMenuBubble(addClient,SharedPrefsManager.getInstance().getBen().equalsIgnoreCase("0"));
        }else {
            //disable swipe option
            drawerLayout = findViewById(R.id.base_drawer_layout);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        TextView referBuddy = findViewById(R.id.refer_buddy);
        referBuddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this,ReferFriendsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void initializeMenuBubble(TextView textView,boolean needBubble) {
        if (null!=textView) {
            if (needBubble) {
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.small_bubble_layer, 0);
//        trackBuddy.setTypeface(null,Typeface.BOLD);
                textView.setVisibility(View.VISIBLE);
            }else{
                textView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * add another set of menu for child activities
     * @param menuID id of the menu layout
     */
    protected void addNewMenu(int menuID){
        navigationView.getMenu().clear();
        navigationView.inflateMenu(menuID);
    }

    /**
     *  handle corresponding actions when the menu item is clicked
     * @param item id of the menu item
     */
    protected void onMenuItemClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.add_ben:
                Intent addBenIntent = new Intent(this,RegisterBeneficiaryActivity.class);
                addBenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(addBenIntent);
                break;
            case R.id.show_route:
                clickContext = "TrackBuddy";
                progressDialog = showProgressDialog(this,getString(R.string.loading));
                loadTransitBookingsData();
                break;
            case R.id.nav_ongoing_service:
                loadOngoing();
                break;
            case R.id.nav_invoice:
                clickContext = "Invoice";
                progressDialog = showProgressDialog(BaseActivity.this,getString(R.string.loading));
                loadEndServiceData(getString(R.string.data_paid));
                break;
            case R.id.nav_manage:
                Intent settingsIntent = new Intent(this,SettingsActivity.class);
                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(settingsIntent);
                break;
            case R.id.nav_help:
                Intent policiesIntent = new Intent(this,UserHelpDataActivity.class);
                policiesIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(policiesIntent);
                break;
            case R.id.nav_logout:
                CommonUtilities.alertAndAction(this, getString(R.string.ask_logout_title),getString(R.string.ask_logout), getString(R.string.yes),
                        getString(R.string.no), new AlertAction() {
                    @Override
                    public void positiveAction() {
                        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                            @Override
                            public void onResponse(RemoteResponse remoteresponse) {
                                Log.d(TAG, "logout onResponse: "+remoteresponse.getResponse());
                                handleLogoutResponse(remoteresponse);
//                                CommonUtilities.logout();
                            }
                        },BaseActivity.this,null);
                        UserServiceHandler.logout(requestParams);
                    }

                    @Override
                    public void negativeAction() {

                    }
                });
                break;
        }
    }

    /**
     * handle logout response
     * @param remoteResponse
     */
    private void handleLogoutResponse(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.logout_failed);
        if(null == remoteResponse){
            toastShort(customErrorMsg);
            return;
        }
        remoteResponse.setCustomErrorMessage(customErrorMsg);
        try {
            if (!isErrorsFromResponse(this, remoteResponse)) {
                toastLong(getString(R.string.logout_success));
                CommonUtilities.logout();
            }else{
                showAlertMsg(getString(R.string.logout),getString(R.string.logout_failed));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * overriden method from activity which opens the side menu on clicking the menu icon
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                if (activeServicesLayout.getVisibility() == View.VISIBLE){
                    activeServicesLayout.setVisibility(View.GONE);
            }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * opens another fragment on top of the activity
     * @param targetFragment
     */
    protected void changeFragment(Fragment targetFragment) {
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame,targetFragment,"fragment");
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.attach(targetFragment);
        this.getSupportFragmentManager().popBackStackImmediate();
        transaction.commit();
    }

    /**
     * overriden method of request permission callback
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
      //  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG(),"permission granted"+requestCode+"permissions"+permissions);
        Intent intent=null;
        if (grantResults[0]==0) {
            switch(requestCode) {
                case 1:
                    toastShort(getString(R.string.permission_granted));
                    break;
                case 3:
                    Log.d(TAG,"telephone number is: ");
                    toastShort(getString(R.string.permission_granted).concat(getString(R.string.access_call)));
                    break;
                case 4:
                    toastShort(getString(R.string.permission_granted).concat(getString(R.string.access_take_photo)));
                    break;
                case 5:
                    toastShort(getString(R.string.permission_granted).concat(getString(R.string.access_choose_photo)));
                    break;
                default:
                    break;
            }
        }else{
            CommonUtilities.showActionSnackbar(CommonUtilities.getPermissionMessage(requestCode),
                    getString(R.string.settings),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonUtilities.openSettingsView();
                        }
                    });
        }
    }

    /**
     * get profile image from shared preference. If it is not available, get image from server.
     */
    private void getProfileImage() {
        if (SharedPrefsManager.getInstance().getUserTypeID().equals(String.valueOf(BuddyConstants.USER_TYPE)) &&
                SharedPrefsManager.getInstance().getUserID().length() != 0) {
            if (SharedPrefsManager.getInstance().getProfileImage().isEmpty()) {
                RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
                    @Override
                    public void onResponse(RemoteResponse remoteresponse) {
                        handleProfileImage(remoteresponse);
                    }
                }, this, null);
                UserServiceHandler.getUserImage(SharedPrefsManager.getInstance().getUserID(), requestParams);
            }
        }
    }

    /**
     * response after getting image from server.
     * @param remoteResponse contains the json data
     */
    private void handleProfileImage(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.no_profile_image);
        if(null == remoteResponse){
            Log.d(TAG, "handleProfileImage: "+getString(R.string.no_profile_image));
            return;
        }else{
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(this,remoteResponse)) {
                    String response = remoteResponse.getResponse();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("data")) {
                        JSONArray responseArr = jsonObject.getJSONArray("data");
                        Log.d(TAG, "handleBookingsResponse: "+responseArr);
                        SharedPrefsManager.getInstance().storeProfileImage(responseArr.getString(0));
                        setProfileImageData(responseArr.getString(0));
                    } else {
                        Log.d(TAG, "handleProfileResponse: "+getString(R.string.no_profile_image));
                    }
                }

            }catch (JSONException e){
                Log.e(TAG, "handleProfileImage: "+getString(R.string.no_profile_image));
                e.getLocalizedMessage();
            }
        }

    }

    /**
     * set existing image to the image view
     * @param profileImage the image string
     */
    private void setProfileImageData(String profileImage){
        View hView =  navigationView.getHeaderView(0);
        RoundedImageView profImage  = hView.findViewById(R.id.logo_profileimg);
        Bitmap profileImageData = CommonUtilities.getImageFromString(profileImage,100,100);
        profImage.setImageBitmap(profileImageData);
    }

    /**
     * load liast of bookings which are in transit state
     */
    private void loadTransitBookingsData() {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                if (null!= clickContext){
                    if(clickContext.equalsIgnoreCase("TrackBuddy")) {
                        handleServiceListResponse(remoteResponse, getString(R.string.transit_list_error));
                    }else{
                        transitListResponse(remoteResponse);
                    }
                }else{
                    transitListResponse(remoteResponse);
                }
            }
        },this,null);
        Map<String,String> params = new HashMap<>();
        params.put("statuses",getString(R.string.transit_label)+","+getString(R.string.data_start));
//        params.put("statuses",getString(R.string.data_start));
        requestParams.setRequestParams(params);
        UserServiceHandler.getTrackingRequestStatus(requestParams);
    }

    /**
     * server response of transit request
     * @param remoteResponse
     */
    private void transitListResponse(RemoteResponse remoteResponse) {
        String customErrorMsg = getString(R.string.transit_list_error);
        if(null == remoteResponse){
            toastShort(customErrorMsg);
        }else {
            remoteResponse.setCustomErrorMessage(customErrorMsg);
            try {
                if (!isErrorsFromResponse(this, remoteResponse)) {
                    JSONObject jsonObject = new JSONObject(remoteResponse.getResponse());
                    Log.d(TAG, "transitListResponse: "+jsonObject);
                    if (jsonObject.has("data")) {
                        JSONArray responseArray = jsonObject.getJSONArray("data");
                        if (responseArray.length()>0) {
                            for (int i=0;i<responseArray.length();i++){
                                JSONObject resObj = responseArray.getJSONObject(i);
                                String status = resObj.getString("status");
                                if (status.equalsIgnoreCase(getString(R.string.data_start))){
                                    activeServicesLayout.setAnimation(upAnimation);
                                    activeServicesLayout.setVisibility(View.VISIBLE);
                                    showActiveServices = true;
                                    initializeMenuBubble(startBuddy,true);
                                    initializeMenuBubble(trackBuddy,true);
                                }else if (status.equalsIgnoreCase(getString(R.string.transit_label))){
                                    clearCallCache(resObj.getString("id"));
                                    initializeMenuBubble(trackBuddy,true);
                                    initializeMenuBubble(startBuddy,false);
                                }
                            }
                        }else{
                            initializeMenuBubble(trackBuddy,false);
                            initializeMenuBubble(startBuddy,false);
                        }
                    }
                }else {
                    initializeMenuBubble(trackBuddy,false);
                    initializeMenuBubble(startBuddy,false);
                }
            } catch (JSONException e) {
                e.getLocalizedMessage();
            }
        }
    }

    /**
     * handle the  bookings response and add it to the corresponding fragment's -
     * list view by bridging it with an adapter
     * @param remoteResponse remote response
     * @param errorMsg error message to show on list
     */
    private void handleServiceListResponse(RemoteResponse remoteResponse,String errorMsg) {
        progressDialog.dismiss();
            if (null == remoteResponse) {
                toastShort(errorMsg);
            }else {
                remoteResponse.setCustomErrorMessage(errorMsg);
                try {
                    if (!isErrorsFromResponse(this,remoteResponse)) {
                        serviceArrayList.clear();
                        serviceArrayList.addAll(CommonUtilities.getBookingsResponse(remoteResponse));
                        if (serviceArrayList.isEmpty()){
                            showNoBookings();
                        }else{
                            loadCorrespondingPages(clickContext);
                        }
                    }else{
                        showNoBookings();
                    }
                } catch (JSONException e) {
                    toastShort( errorMsg);
                    showNoBookings();
                    e.getLocalizedMessage();
                }
            }
    }
     private void loadOngoing(){
         clickContext = "OngoingList";
         progressDialog = showProgressDialog(BaseActivity.this,getString(R.string.loading));
         setOngoingView(getString(R.string.data_start));
     }
    /**
     *  load liast of bookings which are in start state
     * @param status
     */
    private void setOngoingView(String status) {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleServiceListResponse(remoteResponse,getString(R.string.ongoing_list_error));
            }
        },this,null);
        UserServiceHandler.getRequestsStatus(status,requestParams);
    }

    /**
     * booking lists
     * @param name
     */
    private void loadEndServiceData(String name) {
        RequestParams requestParams = new RequestParams(new RemoteServiceListener() {
            @Override
            public void onResponse(RemoteResponse remoteResponse) {
                handleServiceListResponse(remoteResponse,getString(R.string.end_list_error));
            }
        },this,null);
        UserServiceHandler.getRequestsStatus(name,requestParams);
    }


    /**
     * show no bookings alerts
     */
    private void showNoBookings() {
        showAlertMsg(getString(R.string.info_label),getString(R.string.no_bookings_available));
    }

    /**
     * load pages according to the context
     * @param clickContext
     */
    private void loadCorrespondingPages(String clickContext) {
        switch (clickContext){
            case "OngoingList":
                Intent startIntent = new Intent(this,OngoingBookingListActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startIntent);clickContext=null;
                break;
            case "TrackBuddy":
                Intent showRouteIntent = new Intent(this,TrackBuddyActivity.class);
                showRouteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(showRouteIntent);clickContext=null;
                break;
            case "Invoice":
                Intent invoiceIntent = new Intent(this,InvoiceActivity.class);
                invoiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(invoiceIntent);clickContext=null;
                break;
        }
    }

}
