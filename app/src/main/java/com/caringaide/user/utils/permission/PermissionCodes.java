package com.caringaide.user.utils.permission;


import com.caringaide.user.R;

/**
 * Created by renjit on 29-09-2016.
 */
public enum PermissionCodes {

    ANY_PERMISSION (0, R.string.ANY_PERMISSION),
    LOCATION_PERMISSION (1,R.string.LOCATION_PERMISSION),
    LOCATION_FINE_PERMISSION (1,R.string.LOCATION_PERMISSION),
    CONTACT_PERMISSION(2,R.string.CONTACT_PERMISSION),
    CALL_PERMISSION(3, R.string.CALL_PERMISSION),
    PHOTO_PERMISSION(4,R.string.PHOTO_PERMISSION),
    STORAGE_PERMISSION(5,R.string.STORAGE_PERMISSION);

    private int PERMISSION_CODE;
    private int MESSAGE_CODE;

    PermissionCodes(int permissionCode, int messageCode){
        this.PERMISSION_CODE = permissionCode;
        this.MESSAGE_CODE = messageCode;
    }

    public int getPermissionCode() {
        return PERMISSION_CODE;
    }

    public int getMessageCode(){
        return MESSAGE_CODE;
    }



}
