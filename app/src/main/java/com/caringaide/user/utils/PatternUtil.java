package com.caringaide.user.utils;

public class PatternUtil {
    public static String nameRegex ="^[A-Za-z]+$";
    public static String fullNameRegex ="^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";
    public static String relationRegex ="^[a-zA-Z ]+$";
    public static String mobileRegex ="^[0-9]+$";
    public static String mobileWithCntryCodeRegex ="^\\+[1-9]{1,2}[0-9]{10}$";
    public static String userNameRegex ="^[A-Za-z0-9_.@-]{5,30}+$";
    public static String emailRegex ="^[\\w\\-\\.\\+]+\\@[a-zA-Z0-9\\.\\-]+\\.[a-zA-z0-9]{2,4}$";
    public static String passwordRegex ="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static String amexRegex ="^3[47][0-9]{13}$";
    public static String visaRegex ="^4\\d{0,12}|^4\\d{0,15}$";
    public static String maestroRegex ="^(?:5[0678]\\d\\d|6304|6390|67\\d\\d)\\d{15}$";
    public static String additionalInfoRegex ="^[a-zA-Z0-9,.!? ]{7,}$";
    public static String maestroRegex2 ="^(?:5[0678]\\d\\d|6304|6390|67\\d\\d)\\d{12}$";
    public static String masterRegex ="^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$";
    public static String discoverRegex ="^6(?:011|5[0-9]{2})[0-9]{0,16}$";
    public static String jcbRegex ="^(?:2131|1800|35\\d{3})\\d{11}$";
    public static String dinersRegex ="^3(?:0[0-5]|[68][0-9])[0-9]{11}$";
    public static String appartment_pattern="^[A-Za-z0-9$-.@_ ',\\/]{3,20}$";
    public static String zipcode_pattern="^[0-9]{5,6}$";//**
    public static String address_pattern="^[a-zA-Z0-9,/'.-_() ]{4,200}$";
    public static String cvvPattern ="^[0-9]{3}$";
}
