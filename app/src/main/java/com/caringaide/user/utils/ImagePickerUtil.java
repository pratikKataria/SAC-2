package com.caringaide.user.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.caringaide.user.utils.permission.PermissionCodes;


/**
 * singleton
 * Created by renjit.
 */
public class ImagePickerUtil {


    private Activity activity;
    private Fragment fragment;
    private static ImagePickerUtil imagePickerUtil = new ImagePickerUtil();
    private static final String TAG = "ImagePickerUtil";

    private boolean boolActiv=false;
    private ImagePickerUtil(){
    }

    /**
     * create as a singleton instance for ImagePickerUtil, any activity or fragment using this util should implement the ImagePickerMarker
     * @param imagePicker
     * @return
     */
    public  static ImagePickerUtil getInstance(ImagePickerMarker imagePicker){
        if(null == imagePickerUtil){
            imagePickerUtil = new ImagePickerUtil();
        }
        imagePickerUtil.setImagePickerMarker(imagePicker);
        return imagePickerUtil;
    }

    /**
      *this gets called from either activity or fragment that implements the param type ImagePickerMarker
      * @param imagePicker
     */
    private void setImagePickerMarker(ImagePickerMarker imagePicker){
        if(imagePicker instanceof Fragment){
            this.fragment = (Fragment)imagePicker;
            this.activity = fragment.getActivity();
            boolActiv=false;
            Log.d(TAG,"The current fragment set is " + fragment.getId());
        }else if(imagePicker instanceof Activity){
            this.activity = (Activity)imagePicker;
            boolActiv = true;
            Log.d(TAG,"The current activity set is " + activity.getLocalClassName());
        }

    }

    public void selectImage( final int type_camera,final int type_gallery)
    {
        final CharSequence[] items = { "Take Photo", "Choose Image", "Cancel" };
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setTitle("What do you want to do?");
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if ( ContextCompat.checkSelfPermission( activity, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
                       ActivityCompat.requestPermissions( activity, new String[] {  Manifest.permission.CAMERA  },
                                PermissionCodes.PHOTO_PERMISSION.getPermissionCode());
                    }else{
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if(boolActiv)
                            activity.startActivityForResult(intent, type_camera);
                        else
                            fragment.startActivityForResult(intent, type_camera);
                    }

                } else if (items[item].equals("Choose Image")) {
                    if ( ContextCompat.checkSelfPermission( activity, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions( activity, new String[] {  Manifest.permission.WRITE_EXTERNAL_STORAGE  },
                                PermissionCodes.STORAGE_PERMISSION.getPermissionCode());
                    }else{
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/jpeg|image/png|image/jpg");
                        if(boolActiv)
                            activity.startActivityForResult(Intent.createChooser(intent, "Select File"),type_gallery);
                        else
                            fragment.startActivityForResult(Intent.createChooser(intent, "Select File"),type_gallery);
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

}
