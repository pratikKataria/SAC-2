package com.caringaide.user.utils;


import com.caringaide.user.R;
import com.caringaide.user.context.BuddyApp;

/**
 * Created by renjit.
 */

public interface BuddyConstants {
    int PROFILE_TAKE_PHOTO = 1;
    int PROFILE_CHOOSE_PHOTO = 2;
    int PICK_CONTACT = 3;
    int SETTINGS_VIEW = 4;
    public static int MAX_IMG_SIZE_BYTES = 1048576;//2mb
    public static String MAX_IMG_SIZE_MB = "2 Mb.";//2mb
    public static String VERIFY_OTP = "VERIFY_OTP";
    public static String WRONG_OTP = "WRONG_OTP";
    public static String JSON_MESSAGE = "message";
    public static String NOT_AUTHORIZED = "NOT_AUTHORIZED";
    public static String INVALID_REF_CODE = "INVALID_REF_CODE";
    public static String NOT_AUTHORIZED_MKT_CODE = "NOT_AUTHORIZED_MKT_CODE";
    public static String ADDITION_FAILED_MKT_CODE = "ADDITION_FAILED_MKT_CODE";
    public static String REF_MKT_CLIENT = "REF_MKT_CLIENT";
    public static String REF_CLIENT = "REF_FRIEND";
    public static String ADDED_SUCCESSFULLY = "ADDED_SUCCESSFULLY";
    public static String BOOKING_DONE = "BOOKING_DONE";
//    public static String DEFAULT_RIDE_MILES_VALUE = "4";
    public static String DEFAULT_RIDE_MiLES_KEY = "max_ride_miles";
    public static String SCHEDULE_INTERVAL = "schedule_interval";
//    public static String DEFAULT_SCHEDULE_INTERVAL = "15";
    public static String MIN_SERVICE_DURATION = "min_service_duration";
    public static String MIN_TIP_AMOUNT = "min_tip_value";
    public static String ADMIN_CONTACT = "admin_contact";
    public static String CUSTOMER_SUPPORT_EMAIL = "customer_support_email";
    public static String COMPANY_ADDRESS = "company_address";
    public static String BUDDY_SYNC_TIME = "buddy_sync_time";
    public static String MAX_OTP_VAL = "max_otp_attempts";
    public static String APP_COUNTRY = "country";
    public static String APP_COUNTRY_ABBRV = "country_abbrv";
    public static String APP_COUNTRY_CODE_PHN = "country_code";
    public static String MIN_CALL_LIMIT = "call_limit";
//    public static String DEFAULT_MIN_SERVICE_DURATION = "3";
    public static String JSON_DATA = "data";
    //public static final String CONST_SERVER_URL = "https://taxipoliticiantaxiservice.us/";
    public static final String ALERTMSG = "CabAide";
    public static final String STATIC_MAP_URL =
            "https://maps.googleapis.com/maps/api/staticmap?size=400x190&path=weight:7%7Ccolor:blue%7Cenc:";
    public static final String INTERNET_ALERTMSG = "Please check your internet connection...";
    public static final String pushNotifParam = "CabAide";
    public static String DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/json";

    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    public static final String OUT_JSON = "/json";
    public static String LOC_ADDRESS_URL = "https://maps.googleapis.com/maps/api/geocode/json?";

    //public static String GOOGLE_MAP_API_KEY = "AIzaSyBSI_QhJo0LBMrzd4pXAEm-WajRYvLDVyo";

    Double ONE_METRE_MILE = 0.000621371;
    String CONNECTION_ERROR_CODE = "error_code";
    int REQUEST_GOOGLE_API_CLIENT = 2;
//length validation
    int FIRST_NAME_MIN_LENGTH = 2;
    int FIRST_NAME_MAX_LENGTH = 20;
    int LAST_NAME_MIN_LENGTH = 2;
    int LAST_NAME_MAX_LENGTH = 20;
    int FULL_NAME_MIN_LENGTH = 5;
    int FULL_NAME_MAX_LENGTH = 50;
    int USERNAME_MIN_LENGTH = 5;
    int USERNAME_MAX_LENGTH = 30;
    int MOBILE_LENGTH = 10;
//    int MOBILE_MAX_LENGTH = 15;
    int MOBILE_LENGTH_IND = 10;
//    int MOBILE_MAX_LENGTH_IND = 11;
    int ZIPCODE_MIN_LENGTH = 5;
    int ZIPCODE_MAX_LENGTH = 7;
    int PASSWORD_MIN_LENGTH = 8;
    int PASSWORD_MAX_LENGTH = 20;
    int REQUIREMENT_MIN_LENGTH = 5;
    int REASON_MIN_LENGTH = 5;
    int REASON_MAX_LENGTH = 100;
    int MAX_SERVICE_EXTENSION_TIME = 1;
    int SERVICEABLE_AGE = 18;
    int RELATION_MIN_LENGTH = 3;
    int RELATION_MAX_LENGTH = 35;
    int ADDRESS_MIN_LENGTH = 10;
    int MONTH_MIN = 1;
    int MONTH_MAX = 12;
    int MONTH_MAX_LENGTH = 2;
    int YEAR_MAX_LENGTH = 4;
    int CARD_MAX_VALIDITY_YEAR = 6;
    int AMEX_LENGTH = 15;
    int VISA_LENGTH1 = 13;
    int VISA_LENGTH2 = 16;
    int VISA_LENGTH3 = 19;
    int MASTER_LENGTH = 16;
    int MAESTRO_LENGTH1 = 16;
    int MAESTRO_LENGTH2 = 19;
    int DISCOVER_LENGTH = 16;
    int JCB_LENGTH = 16;
    int DINERS_LENGTH = 14;
    int DINERS_LENGTH2 = 16;

    int serviceMaxDuration = 14;
    int serviceMinTimeinHr = 7;
    int serviceMaxTimeinHr = 21;
//    int SERVICE_REQUEST_DURATION_MIN_INTERVAL = 3;//1
    long timeDiffMillis = 15 * 60 * 1000;//15 minutes
    String BOOKING_DURATION_FROM_NOW = "15 minutes";
    String PENDING_COUNT_TITLE = "pending";
    String PEER_COUNT_TITLE = "pending_for_user";
    String CONFIRM_COUNT_TITLE = "confirm_for_user";

    String ZIP_ID = "ZipID";

    static final int minWaitSecForLogin = 3;

    public static final String CHANNEL_ID = "buddy_channel";

    String APP_NAME = "buddy-user-and";
    String CONST_SERVER_URL = CommonUtilities.getServerUrl();
    String TC_URL = BuddyApp.getApplicationContext().getString(R.string.server_url).concat("terms_and_conditions.html");
    String REFUND_URL = BuddyApp.getApplicationContext().getString(R.string.server_url).concat("refundPolicy.html");
    String PRIVACY_POLICY_URL = BuddyApp.getApplicationContext().getString(R.string.server_url).concat("privacy_policy_structure.html");
    String FAQ_URL = BuddyApp.getApplicationContext().getString(R.string.server_url).concat("faq.html");
    int USER_TYPE = 2;
    int MIN_DURATION_HR = 7;
    int MAX_DURATION_HR = 21;
    String CHANGE_PASSWORD_CONTEXT = "RESET_PASSWORD";
    String EDIT_PROFILE_CONTEXT = "EDIT_PROFILE";
    String ADD_TIP_CONTEXT = "ADD_TIP";
    String PAY_FARE_CONTEXT = "PAY_FARE";
    String SIGNUP_USER_CONTEXT = "USER_SIGNUP";
    String SELF = "Self";
    String OTHERS = "Others";
    //OTP keys
    String OTP_RESEND_REASON = "reason";
    String OTP_RESEND_KEY = "key";
    String OTP_RESEND_MOBILE = "mobile";



    String APP_GOOGLE_API = "google_map_api_key";

    /*
    * RESET_PASSWORD for change password from settings
EDIT_PROFILE for editing profile
ADD_TIP for adding tip from client app
PAY_FARE for payment from buddy app*/

    enum ServerMsgCodes{
        NO_LOGIN_IN_THIS_APP(R.string.unauthorized_login),
        UPDATE_SUCCESSFULL(R.string.UPDATE_SUCCESSFULL),
        UPDATED_SUCCESSFULLY(R.string.UPDATE_SUCCESSFULL),
        UPDATION_FAILED(R.string.UPDATE_FAILED),
        ADDITION_FAILED(R.string.ADDITION_FAILED),
        DELETION_FAILED(R.string.DELETE_FAILED),
        DELETE_FAILED(R.string.DELETE_FAILED),
        DELETED_SUCCESSFULLY(R.string.DELETED_SUCCESSFULLY),
        ADDED_SUCCESSFULLY(R.string.ADDED_SUCCESSFULLY),
        ADD_FAILED(R.string.ADD_FAILED),
        FAILED_USER_REGISTRATION(R.string.signup_failed),
        USER_NOT_EXISTS(R.string.user_not_exists),
        WRONG_OTP(R.string.wrong_otp),
        ALREADY_IN_SERVICE(R.string.already_in_service),
        NOT_AUTHORIZED_MKT_CODE(R.string.not_authorized_mkt_code),
        NOT_VALID_OFFER(R.string.not_valid_offer),
        ADDITION_FAILED_MKT_CODE(R.string.addition_failed_mkt_code),
        BOOKING_DONE(R.string.booking_success),
        NO_PROXY(R.string.no_proxy),
        INVALID_REF_CODE(R.string.invalid_ref_code),
        ADD_CARD_FAILED(R.string.card_added_failed),
        DUPLICATE_SELF(R.string.duplicate_self);

        private int integerCode;
        ServerMsgCodes(int integerCode){
            this.integerCode = integerCode;
        }

        public int getIntegerCode() {
            return integerCode;
        }
    }

    /**
     * Notification constants
     */
    public static final String MESSAGE_TYPE = "message_type";
    public static final String MESSAGE_TEXT = "message_text";
    public enum NotificationTypes {
        GENERAL("General"),
        NEW_BOOKING_TITLE(BuddyApp.getApplicationContext().getString(R.string.new_booking_title)),
        NEW_BOOKING_TITLE_FOR_BUDDY(BuddyApp.getApplicationContext().getString(R.string.new_booking_title_for_buddy)),
        NEW_BOOKING_TITLE_FOR_USER(BuddyApp.getApplicationContext().getString(R.string.new_booking_title_for_buddy)),
        BOOKING_CANCELLED_TITLE(BuddyApp.getApplicationContext().getString(R.string.booking_cancelled_title)),
        BOOKING_CONFIRMED_TITLE_BY_BUDDY(BuddyApp.getApplicationContext().getString(R.string.booking_confirmed_title_by_buddy)),
        SERVICE_STARTED_TITLE_BY_BUDDY(BuddyApp.getApplicationContext().getString(R.string.booking_start_title_by_buddy)),
        SERVICE_ENDED_TITLE_BY_BUDDY(BuddyApp.getApplicationContext().getString(R.string.booking_ended_title_by_buddy)),

        BUDDY_IN_TRANSIT_TITLE(BuddyApp.getApplicationContext().getString(R.string.buddy_in_transit_title)),
        VERIFY_BUDDY_TITLE(BuddyApp.getApplicationContext().getString(R.string.verify_buddy_title));

        private String messageType;

        NotificationTypes(String msgType){
            this.messageType = msgType;
        }

        public String getMessageType() {
            return messageType;
        }
    }
    public enum NotificationMsgs{
        GENERAL("Notification for buddy"),
        NEW_BOOKING_BODY(BuddyApp.getApplicationContext().getString(R.string.new_booking_body)),
        NEW_BOOKING_BODY_FOR_BUDDY(BuddyApp.getApplicationContext().getString(R.string.new_booking_body_for_buddy)),
        NEW_BOOKING_BODY_FOR_USER(BuddyApp.getApplicationContext().getString(R.string.new_booking_body_for_buddy)),
        BOOKING_CANCELLED_BODY(BuddyApp.getApplicationContext().getString(R.string.booking_cancelled_body)),
        BOOKING_CONFIRMED_BY_BUDDY(BuddyApp.getApplicationContext().getString(R.string.booking_confirmed_by_buddy)),
        SERVICE_STARTED_BY_BUDDY(BuddyApp.getApplicationContext().getString(R.string.service_started_body)),
        SERVICE_ENDED_BY_BUDDY(BuddyApp.getApplicationContext().getString(R.string.booking_ended_body)),
        VERIFY_BUDDY(BuddyApp.getApplicationContext().getString(R.string.verify_buddy_title)),
        BUDDY_IN_TRANSIT(BuddyApp.getApplicationContext().getString(R.string.buddy_in_transit_title));
        private String message;

        NotificationMsgs(String msg){
            this.message = msg;
        }

        public String getMessage() {
            return message;
        }

    }

    class UserInfo{
        public static final String USER_LOGIN_ID = "loginID";
        public static final String USER_TYPE_LOGIN_ID = "loginTypeID";
        public static final String USER_LOGIN_FULLNAME = "FandLName";
        public static final String USER_LOGIN_FNAME = "FName";
        public static final String USER_LOGIN_EMAIL = "loginEmail";
        public static final String USER_LOGIN_MOBILE = "loginMobile";
        public static final String USER_LOGIN_DOB = "loginDob";
        public static final String USER_LOGIN_USERNAME = "loginUName";
        public static final String USER_LOGIN_TOKEN = "loginToken";
        public static final String USER_CURRENCY = "currency";
        public static final String ORGANIZATION_ID = "organizationId";
        public static final String USER_LOGIN_LNAME = "LName";
        public static final String USER_LOGIN_PHONE = "loginPhone";
        public static final String USER_LOGIN_ISACTIVE = "loginIsActive";
        public static final String USER_LOGIN_SSN = "loginSSN";
        public static final String USER_LOGIN_ORG = "loginOrg";
        public static final String USER_LOGIN_GENDER = "loginGender";
        public static final String USER_LOGIN_BEN = "loginBen";
        public static final String USER_MKT_TOKEN = "mktToken";
    }
    class ConfigData{
        public static final String CONFIG_ID = "id";
        public static final String CONFIG_KEY = "config_name";
        public static final String CONFIG_VALUE = "config_val";
        public static final String CONFIG_UNIT = "unit";
        public static final String CONFIG_DESCRIPTION = "description";
    }
    class ServiceInfo{
        public static final String SERVICE_ID = "service_id";
        public static final String SERVICE_TYPE = "service_type";
        public static final String SERVICE_DESCRIPTION = "service_desciption";
        public static final String SERVICE_FEE = "service_fee";
        public static final String SERVICE_CREATED = "service_created";
    }
    class CountryInfo{
        public static final String COUNTRY_ID = "country_id";
        public static final String COUNTRY_NAME = "country_name";
        public static final String COUNTRY_ABBRV = "country_abbrv";
        public static final String CURRENCY = "currency";
        public static final String CURRENCY_CODE = "currency_code";
    }
    class CityInfo{
        public static final String CITY_ID = "city_id";
        public static final String CITY_NAME = "city_name";
        public static final String CITY_ABBRV = "city_abbrv";
        public static final String STATE_ID = "state_id";
        public static final String STATE_NAME = "state_name";
    }
    class ZipcodeInfo{
        public static final String ZIP_ID = "zip_id";
        public static final String ZIP_NAME = "zip_name";
        public static final String ORG_ID = "org_id";
        public static final String CITY_ID = "city_id";
        public static final String COUNTRY_ID = "country_id";
        public static final String STATE_ID = "state_id";
        public static final String STATE_NAME = "state_name";
        public static final String CITY_NAME = "city_name";
        public static final String CITY_ABBRV = "city_abbrv";
        public static final String STATE_ABBRV = "state_abbrv";
    }
    class OrgInfo{
        public static final String ORG_ID = "org_id";
        public static final String COUNTRY_ID = "country_id";
        public static final String ORG_NAME = "org_name";
        public static final String REG_CODE = "regCode";
        public static final String MOBILE = "mobile";
        public static final String EMAIL = "email";
        public static final String FOUNDING_YEAR = "foundingYear";
        public static final String CITY_ID = "cityId";
        public static final String STATE_ID = "stateId";
        public static final String ZIPCODE = "zipcode";
        public static final String ACTIVE_STATUS = "activeStatus";
    }

    class HomeDataContext{
        public static final String PENDING = "pending";
        public static final String PENDING_30 = "pending_30";
        public static final String FAV = "pending_fav";
        public static final String FAV_30 = "pending_fav_30";
        public static final String PEER = "peer";
        public static final String CONFIRM = "confirm";
        public static final String CONFIRM_90 = "confirm_in_90";
        public static final String CONFIRM_DURATION = "confirm_within_30";
        public static final String CONFIRM_EXPIRY = "confirm_expired";
        public static final String CONFIRM_UPCOMING = "confirm_upcoming";
        public static final String OTHER = "other";
        public static final String CANCEL = "cancel";
    }


}
