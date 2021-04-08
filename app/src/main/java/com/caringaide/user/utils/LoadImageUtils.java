package com.caringaide.user.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by renjit.
 */
public class LoadImageUtils {
    private Context context;
    private Bitmap imageBitmap;
    private final ImageLoaderListener imageLoaderListener;
    private static final String TAG = "LoadImageUtils";

    // this needs to be implemented by any class that use this utility
    public interface ImageLoaderListener{
        String PROFILE_IMAGE = "PROFILE_IMAGE";
        String CONTACT_IMAGE = "CONTACT_IMAGE";
        String DRIVER_IMAGE = "DRIVER_IMAGE";
        void imageLoaded(Bitmap image, String imageCode);
    }

    public LoadImageUtils(Context context, ImageLoaderListener imageLoaderListener) {
        this.context = context;
        this.imageLoaderListener = imageLoaderListener;
    }

    /**
     * this will execute the async method for loading profile image
     * @param loadImageUrl
     */
    public void executeTask(String loadImageUrl,String imageCode) {
        Log.d(TAG,"loadImageUrl from executeTask method " + loadImageUrl);
        //if loadImageFromStorage return true,set the bitmap and request code to the imageLoaderListener
        if ((null!=loadImageUrl && !loadImageUrl.isEmpty())&& (null!=imageCode && !imageCode.isEmpty())) {
            loadImageFromStorage(loadImageUrl, imageCode);
        }

    }

    /**
     * this will load profile image from storage, if available or take image from the server
     * @param imageUrl
     * @param imageCode
     * @throws FileNotFoundException
     */
    private void loadImageFromStorage(final String imageUrl,final String imageCode){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                FileInputStream imageInputStream = null;
                try {
                    String urlWithoutSlash = imageUrl.replace("/", "");
                    Log.d(TAG, "url without slash " + urlWithoutSlash);
                    try {
                        imageInputStream = context.openFileInput(imageUrl);
                    } catch (FileNotFoundException fne) {
                        //do nothing
                        Log.e(TAG, "stream: not found ");
                        fne.printStackTrace();
                    }
                    if (null != imageInputStream){
                        imageBitmap = BitmapFactory.decodeStream(imageInputStream);
                        Log.d(TAG, "got image from the stream  " + imageBitmap);
                        if (null != imageBitmap) {
                            Log.d(TAG, "loading image from the internal storage");
                          //  Bitmap circleBitmap = CommonUtilities.getCircleBitmap(imageBitmap);
                            imageLoaderListener.imageLoaded(imageBitmap, imageCode);
                        } else {
                            Log.d(TAG, "image bitmap is null. So getting image from server");
                            loadImage(imageUrl, imageCode);
                        }
                    }else{
                        Log.d(TAG, "no image in the storage. So getting image from server");
                        loadImage(imageUrl, imageCode);
                    }
                } finally {
                    try {
                        if(null!=imageInputStream)
                            imageInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /**
     * use volley library for loading images
     * @param imageUrl
     * @param imageRequestCode
     */
    private void loadImage(final String imageUrl,final String imageRequestCode){
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading Image....");
        pDialog.setCancelable(false);
        pDialog.show();
        ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap respBitmap) {
                //updateImageToStorage(imageUrl,respBitmap);
                pDialog.dismiss();
                Log.d(TAG, "Image loaded by volley ");
                imageLoaderListener.imageLoaded(respBitmap,imageRequestCode);
            }
        }, 80, 80, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888,
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if(null != networkResponse){
                    Log.e(TAG,"Image Request failed with status code " + networkResponse.statusCode);
                }
                String errorMessage = error.getLocalizedMessage();
                Log.e(TAG, "Erroor while loading the image " + errorMessage);
                pDialog.dismiss();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(imageRequest);
        queue.getCache().remove(imageUrl);
    }



    /**
     * this will store the image to storage after loading it from the server
     * only if the image not exists
     * @param imageUrl
     * @param image
     */
    public  void saveImageIfExistsInStorage(final String imageUrl,final Bitmap image){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                FileInputStream imageInputStream = null;
                try {
                    String urlWithoutSlash = imageUrl.replace("/", "");
                    Log.d(TAG, "url without slash " + urlWithoutSlash);
                    try {
                        imageInputStream = context.openFileInput(urlWithoutSlash);
                    } catch (FileNotFoundException fne) {
                        //do nothing
                    }
                    if (null == imageInputStream) {
                        updateImageToStorage(imageUrl,image);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if(null!=imageInputStream)
                            imageInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            });
                    }

    /**
     * this will store the image to storage after loading it from the server
     * @param url
     * @param image
     */
    public void updateImageToStorage(final String url,final Bitmap image){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (null!=image ) {
                    FileOutputStream imageOutputStream = null;
                    try {
                        String urlWithoutSlash = url.replace("/","");
                        Log.d(TAG,"url without slash "+urlWithoutSlash);
                        imageOutputStream = context.openFileOutput(urlWithoutSlash,Context.MODE_PRIVATE);
                        image.compress(Bitmap.CompressFormat.JPEG, 100, imageOutputStream);
                        Log.d(TAG,"saved image to the internal storage");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if(null!=imageOutputStream)
                                imageOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    /**
     * remove the image file from internal storage
     * @param imageUrl
     */
    public void removeImageFromStorage(final String imageUrl){
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String urlWithoutSlash = imageUrl.replace("/", "");
                    Log.d(TAG, "url without slash " + urlWithoutSlash);
                    context.deleteFile(urlWithoutSlash);
                    Log.d(TAG,"removing image from the storage, url = "+urlWithoutSlash);
                }
            });
    }

}
