package com.caringaide.user.remote;

import android.util.Log;

import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.SharedPrefsManager;

import static com.caringaide.user.utils.BuddyConstants.CONST_SERVER_URL;
import static com.caringaide.user.utils.CommonUtilities.USER_ID;

/**
 * urls will be listed here
 */
public class UserServiceHandler {
    public static final int GET_DIRECTIONS = 1;
    private static final String TAG = "UserServiceHandler";

    /**
     * handshake
     * @param requestParams
     */
    public static void handShake(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"users/handshake.json";
        get(url,requestParams);
    }
    /**
     * login remote call
     * @param requestParams
     */
    public static void userLogin(RequestParams requestParams){
        String url = CONST_SERVER_URL+ "users/login.json";
        Log.d(TAG,"customer login url " + url);
        post(url,requestParams);
    }

    /**
     * set device details remote call
     * @param requestParams
     */
    public static void shareRegId(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"users/addDeviceToken.json";
        post(url,requestParams);
    }
    /**
     * to get the directions
     * @param startLat
     * @param startLng
     * @param endLat
     * @param endLag
     * @param requestParams
     */
    public static void getDirections(Double startLat, Double startLng, Double endLat, Double endLag,String DirectionsApiKey, RequestParams requestParams) {
        String url = BuddyConstants.DIRECTIONS_API_URL+"?origin="+ startLat + "," + startLng+
                "&destination="+endLat + "," + endLag +"&mode=driving&alternatives=true&avoid=ferries|indoors&unit=metric"+"&key="+DirectionsApiKey;
        Log.d(TAG, "Directions URL  " + url);
        post(url,requestParams);

    }

    /**
     * resend otp remote call
     * @param requestParams
     */
    public static void resendOtp(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"Users/resendOtp.json";
        post(url,requestParams);
    }



    public static void forgotPassword(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"Users/forgotPassword.json";
        post(url,requestParams);
    }
    /**
     * get available services
     * @param requestParams
     */
    public static void getServices(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"services/index.json";
        get(url,requestParams);
    }

    /**
     * get c
     * @param requestParams
     */
    public static void getCountries(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"countries.json";
        get(url,requestParams);
    }/**
     * get c
     * @param requestParams
     */
    public static void getOrganizationData(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"organizations/index.json";
        get(url,requestParams);
    }

    /**
     * get c
     * @param requestParams
     */
    public static void getCities(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"cities.json";
        get(url,requestParams);
    }/**
     * get c
     * @param requestParams
     */
    public static void getZipcodes(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"zipcodes/allzipcodes.json";
        get(url,requestParams);
    }/**
     * get c
     * @param requestParams
     */
    public static void getBuddySkills(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"users/buddySkills.json";
        get(url,requestParams);
    }

    /**
     * get available languages
     * @param stateId
     * @param requestParams
     */
    public static void getAvailableLanguages(String stateId, RequestParams requestParams) {
        String url = CONST_SERVER_URL+"states/getLanguages/"+stateId+".json";
        get(url,requestParams);
    }

    public static void signUpUser(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"users/signUp.json";
        post(url,requestParams);
    }
    public static void getBenTypes(RequestParams requestParams){
        String url = CONST_SERVER_URL+"beneficiaries/types.json";
        get(url,requestParams);
    }
    /**
     * change request status
     * @param bookingId
     * @param status
     * @param requestParams
     */
    public static void changeBookingStatus(String bookingId,String status,RequestParams requestParams) {
        String url = CONST_SERVER_URL+"bookings/"+status+"/"+bookingId+".json";
        post(url,requestParams);
    }
    /**
     * get request details according to status
     * @param status
     * @param requestParams
     */
    public static void getRequestsStatus(String status, RequestParams requestParams){
        String url = CONST_SERVER_URL+ "bookings/listForStatus/"+status+"/"+ USER_ID+ ".json";
        Log.d(TAG, "getRequestsStatus: "+url);
        get(url,requestParams);
    }
    /**
     * get request details according to status
     * @param requestParams
     */
    public static void getTrackingRequestStatus(RequestParams requestParams){
        String url = CONST_SERVER_URL+ "bookings/forUser.json";
        Log.d(TAG, "getTrackingStatus: "+url);
        post(url,requestParams);
    }
    /**
     * get request details according to status
     * @param requestParams
     */
    public static void getPendingPeerRequests(RequestParams requestParams){
        String url = CONST_SERVER_URL+ "bookings/pendingPeerListForUser.json";
        Log.d(TAG, "getRequestsStatus: "+url);
        get(url,requestParams);
    }


    public static void addBeneficiary(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"beneficiaries/add.json";
        post(url,requestParams);
    }
    public static void listBeneficiaries(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"beneficiaries/benForUser/"+ SharedPrefsManager.getInstance().getUserID() +".json";
        get(url,requestParams);
    }
    public static void editBeneficiaries(String benId, RequestParams requestParams) {
        String url = CONST_SERVER_URL+"beneficiaries/edit/"+benId +".json";
        post(url,requestParams);
    }
    public static void editUserProfile(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"users/edit/.json";
        post(url,requestParams);
    }

    public static void bookService(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"Bookings/add.json";
        post(url,requestParams);
    }
    public static void getBuddiesForBeneficiary(String benId, RequestParams requestParams) {
        String url = CONST_SERVER_URL+"Beneficiaries/getBuddies/"+benId+".json";
        get(url,requestParams);
    }
    public static void getAllBuddies(String benId, RequestParams requestParams) {
        String url = CONST_SERVER_URL+"users/filteredBuddies/"+benId+".json";
        get(url,requestParams);
    }
    public static void addBuddies(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"Beneficiaries/addBuddies.json";
        post(url,requestParams);
    }
    public static void deleteBuddies(String benId,String buddyId, RequestParams requestParams) {
        String url = "";
        if (buddyId.isEmpty()) {
            url = CONST_SERVER_URL + "Beneficiaries/deleteBuddies/" + benId + ".json";
        }else{
            url = CONST_SERVER_URL + "Beneficiaries/deleteBuddies/" + benId + "/" + buddyId + ".json";
        }
        post(url,requestParams);
    }

    public static void deleteBeneficiary(String benId, RequestParams requestParams) {
        String url = CONST_SERVER_URL+"Beneficiaries/delete/"+benId+".json";
        post(url,requestParams);
    }
    public static void deleteBeneficiaryCard(String cardId, RequestParams requestParams) {
        String url = CONST_SERVER_URL+"cards/delete/"+cardId+".json";
        post(url,requestParams);
    }

    public static void getBuddyScheduledTimings(String buddyId, RequestParams requestParams) {
        String url = CONST_SERVER_URL+"users/getBuddyTime/"+buddyId+".json";
        get(url,requestParams);
    }
    /**
     * change password remote call
     * @param requestParams
     */
    public static void changePassword(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"users/resetPassword.json";
        post(url,requestParams);
    }

    public static void getServiceFare(String bookingId, RequestParams requestParams) {
        String url = CONST_SERVER_URL+"bookings/getFare/"+bookingId+".json";
        get(url,requestParams);
    }

    public static void getBuddieLocationDetails(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"users/locationData.json";
        get(url,requestParams);
    }

    public static void payAmount(String bookingId, RequestParams requestParams) {
        String url = CONST_SERVER_URL+"bookings/saveFare/"+bookingId+".json";
        post(url,requestParams);
    }

    public static void saveFeedBack(String bookingId, RequestParams requestParams) {
        String url = CONST_SERVER_URL+"bookings/feedback/"+bookingId+".json";
        post(url,requestParams);
    }
    public static void logout(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"users/logout/"+BuddyConstants.APP_NAME+".json";
        post(url,requestParams);
    }
    /**
     * get user image
     * @param userId
     * @param requestParams
     */
    public static void getUserImage(String userId, RequestParams requestParams) {
        String url = CONST_SERVER_URL+"attachments/getImage/"+ userId +
                "/profile/jpeg.json";
        get(url,requestParams);
    }
    /**
     * post the url
     * @param url
     * @param requestParams
     */
    private static void post(String url, RequestParams requestParams){
        RemoteService remoteService = RemoteService.getInstance(requestParams.getContext());
        remoteService.doPost(url,requestParams);
    }


    /**
     * put for update the url
     * @param url
     * @param requestParams
     */
    private static void put(String url, RequestParams requestParams){
        RemoteService remoteService = RemoteService.getInstance(requestParams.getContext());
        remoteService.doPut(url,requestParams);
    }

    /**
     * delete data from backend
     * @param url
     * @param requestParams
     */
    private static void delete(String url, RequestParams requestParams){
        RemoteService remoteService = RemoteService.getInstance(requestParams.getContext());
        remoteService.doDelete(url,requestParams);
    }

    /**
     * do a get
     * @param url
     * @param requestParams
     */
    private static void get(String url, RequestParams requestParams){
        RemoteService remoteService = RemoteService.getInstance(requestParams.getContext());
        remoteService.doGet(url,requestParams);
    }

    public static void addProfileImage(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"attachments/add.json";
        post(url,requestParams);
    }

    public static void getCardsForBen(String selectedBenId, RequestParams requestParams) {
        String url = CONST_SERVER_URL+"cards/cardsForBen/"+selectedBenId+".json";
        get(url,requestParams);
    }
    public static void getAnnouncements(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"announcements/active.json";
        get(url,requestParams);
    }

    public static void addFriend(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"offers/addClient.json";
        post(url,requestParams);
    }

    public static void getPendingPayments(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"bookings/pendingFares/"+SharedPrefsManager.getInstance().getUserID()+".json";
        get(url,requestParams);
    }

    public static void addTip(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"bookings/addTip.json";
        post(url,requestParams);
    }

    public static void addCardForClient(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"cards/add.json";
        post(url,requestParams);
    }

    public static void addCardAndGetToken(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"cards/add.json";
        post(url,requestParams);
    }

    public static void getToken(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"cards/getToken.json";
        //String url = "https://cert.api2.heartlandportico.com/Hps.Exchange.PosGateway.Hpf.v1/api/token";
        post(url,requestParams);
    }

    /**
     * get number of bookings in transit and start state
     * @param requestParams
     */
    public static void getBookingCount(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"bookings/bookingCount/user/"+SharedPrefsManager.getInstance().getUserID()+".json";
        get(url,requestParams);
    }

    public static void getConfigData(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"configs/getConfigs.json";
        get(url,requestParams);
    }

    public static void getProxyNumber(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"bookings/callProxy.json";
        post(url,requestParams);
    }
    /**
     * update location details
     * @param requestParams
     */
    public static void saveLocationInfo(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"users/updateLocation.json";
        post(url,requestParams);
    }

    /**
     * get summary of pending list
     * @param requestParams
     */
    public static void getPendingSummary(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"bookings/pendingSummary/user/"
                +SharedPrefsManager.getInstance().getUserID()+".json";
        get(url,requestParams);
    }

    /**
     * get summary of confirm requests
     * @param requestParams
     */
    public static void getConfirmSummary(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"bookings/confirmSummary/user/"
                +SharedPrefsManager.getInstance().getUserID()+".json";
        get(url,requestParams);
    }

    /**
     * get summary of cancelled services
     * @param requestParams
     */
    public static void getCancelSummary(RequestParams requestParams) {
        String url = CONST_SERVER_URL+"bookings/cancelSummary/user/"
                +SharedPrefsManager.getInstance().getUserID()+".json";
        get(url,requestParams);
    }
}
