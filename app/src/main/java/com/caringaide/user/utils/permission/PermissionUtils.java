package com.caringaide.user.utils.permission;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import com.caringaide.user.R;
import com.caringaide.user.context.BuddyApp;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


/**
 * Created by renjit.
 */
public class PermissionUtils {

    private static final String TAG = "PermissionUtils";

    public static final String[] locationPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static final String[] locationFinePermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static final String[] contactPermissions = {
            Manifest.permission.READ_CONTACTS
    };

    public static final String[] callPermissions ={
            Manifest.permission.CALL_PHONE
    };

    public static final String[] camera = {
            Manifest.permission.CAMERA,
    };

    public static final String[] storage ={
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * return some text for the permission dialog titles
     * @param permission
     * @return
     */
    private static String getPermissionTitle(Activity activity ,String permission){
        String title = activity.getString(R.string.PERM_RATIONALE_TITLE);
        switch (permission){
            case Manifest.permission.ACCESS_FINE_LOCATION:
                title = activity.getString(R.string.Manifest_permission_ACCESS_FINE_LOCATION);
                break;
            case Manifest.permission.CAMERA:
                title = activity.getString(R.string.Manifest_permission_CAMERA);
                break;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                title = activity.getString(R.string.Manifest_permission_WRITE_EXTERNAL_STORAGE);
                break;
            case Manifest.permission.READ_CONTACTS:
            case Manifest.permission.WRITE_CONTACTS:
                title = activity.getString(R.string.Manifest_permission_READ_CONTACTS);
                break;
            case Manifest.permission.CALL_PHONE:
                title = activity.getString(R.string.Manifest_permission_CALL_PHONE);
                break;
            default:
                //do nothing;

        }
        return title;
    }

    /**
     * request runtime permissions
     * @param activity
     * @param requiredPermissions
     * @param permissionCodes
     */
    public static boolean requestPermission(@NonNull final Activity activity, @NonNull final String []requiredPermissions, @NonNull final PermissionCodes permissionCodes){
        if (!(activity instanceof ActivityCompat.OnRequestPermissionsResultCallback)) {
            throw new IllegalStateException("The Activity must implement ActivityCompat.OnRequestPermissionsResultCallback");
        }
        if(ifVersionHigher()) {
            final String reqArr[] = new String[1];
            //select options without permissions.
            for (String reqPermission : requiredPermissions) {
                if (ContextCompat.checkSelfPermission(activity, reqPermission) != PackageManager.PERMISSION_GRANTED) {
                    reqArr[0] = reqPermission;
                    //if its denied, then check if an explanation is required for one by one
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, reqPermission)) {
                        new AlertDialog.Builder(activity)
                                .setTitle(activity.getString(R.string.PERM_RATIONALE_TITLE))
                                .setMessage(activity.getString(permissionCodes.getMessageCode()))
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(activity,
                                                reqArr,
                                                permissionCodes.getPermissionCode());
                                    }
                                })
                                .show();
                    }else {
                        ActivityCompat.requestPermissions(activity,
                                reqArr,
                                permissionCodes.getPermissionCode());
                        //checkIfEditSettings(reqArr,activity);
                    }

                }

            }

        }
        return true;
    }

    /**
     * check if we need to take the user to setting(if user selected never option) or grant permission
     * @param permission
     * @param activity
     */
    public static void checkIfEditSettings(String[] permission, final Activity activity){
        String reqPermission = permission[0];
        if (ContextCompat.checkSelfPermission(activity, reqPermission) != PackageManager.PERMISSION_GRANTED) {
            String title = activity.getString(R.string.PERM_RATIONALE_TITLE);
            new AlertDialog.Builder(activity)
                    .setTitle(getPermissionTitle(activity,reqPermission))
                    .setMessage(activity.getString(R.string.PERM_GENERAL_MSG))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
                            activity.startActivity(intent);
                        }
                    }).setNegativeButton(android.R.string.cancel, null)
                    .show();

        }
    }

    /**
     * check if the sdk verion is greater than marshmallow (android 6, API 23)
     * @return
     */
    public static Boolean ifVersionHigher(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
    /**
     * chac if google play services is available and connected.
     * @return
     */
    public static boolean checkPlayServices() {
        Activity currentActivity = BuddyApp.getCurrentActivity();
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int resultCode = googleApi.isGooglePlayServicesAvailable(currentActivity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApi.isUserResolvableError(resultCode)) {
                googleApi.getErrorDialog(currentActivity,resultCode,1000).show();
            } else {
                Toast.makeText(currentActivity.getApplicationContext(),currentActivity.getString(R.string.device_not_supported),Toast.LENGTH_SHORT);
                currentActivity.finish();
            }
            return false;
        }
        return true;
    }

}
