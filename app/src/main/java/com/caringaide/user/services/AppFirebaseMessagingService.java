package com.caringaide.user.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.caringaide.user.R;
import com.caringaide.user.utils.SharedPrefsManager;
import com.caringaide.user.utils.notification.AppNotificationManager;
import com.caringaide.user.utils.notification.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static com.caringaide.user.utils.BuddyConstants.CHANNEL_ID;

//import com.google.firebase.


/**
 * Created by renjit.
 */
public class AppFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FireBaseMessageService";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        Log.d("TAG"+"-token", token);
        SharedPrefsManager.getInstance().storeDeviceToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG,"On Message received ..................... "+remoteMessage);

        if(null!= remoteMessage && remoteMessage.getData().size() > 0){
            Log.d(TAG, remoteMessage.getData().toString());
            try{

                //String msg = remoteMessage.getData().toString();
                JSONObject jsonObj = new JSONObject(remoteMessage.getData());
                Log.d(TAG, "push notification as json "+jsonObj);
                // JSONObject jsonObj = new JSONObject(remoteMessage.getData().toString());
                //Calling method to generate notification
                Log.d(TAG,"Pushing the notification ..................... ");
                pushNotification(jsonObj);
            }catch(Exception ex){

                Log.e(TAG,ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        }else{
            Log.e(TAG,"No message in the notification .................");
        }

    }

    /**
     * pushes the notification based on message types(title is the message type)
     * @param jsonObject
     */
    protected void pushNotification(JSONObject jsonObject){
        String title = "";
        String messageText = "";
        //get Json Data
        try {
            //JSONObject data = jsonObject.getJSONObject("data");
            //parsing json data
            title = jsonObject.getString("title");
            messageText = jsonObject.getString("body");
            Log.d(TAG,"notification json message " + messageText);
            Message message = new Message(title);
            message.setMessageText(messageText);
            message.setMessageType(title);
            Log.d(TAG,"Sending the message to be processed ..................... ");
            //process the message
            AppNotificationManager appNotificationManager = AppNotificationManager.getInstance(this);
            message = appNotificationManager.execute(message);
            Log.d(TAG,"message executed ..................... ");
            if(message.getResponse().isError()){
                Log.e(TAG,"************ Error processing the notification *****************");
            }else{
                // get the intent after processing the message
                Intent intent = message.getResponse().getIntent();
                if(null!=intent) {
                    if (message.getRequestCode() == 0){
                        buildNotificationWithoutRequestCode(intent,message);
                    }else{
                        buildNotificationWithRequestCode(intent,message);
                    }

                }
            }
        } catch (JSONException e) {
            Log.e(TAG,"Error processing the notification of type " + title);
            e.printStackTrace();
        }

    }

    /**
     * notification with random request code. Won't overwrite the other notification
     * @param intent
     * @param message
     */
    private void buildNotificationWithRequestCode(Intent intent, Message message){
        Log.d(TAG, "Got itent with request code..................... ");
        String title = message.getMessageType();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), message.getRequestCode(),
                intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle(title)
                .setContentText(message.getMessageText())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.care_icon)
                .setContentIntent(pendingIntent)
                .setContent(getCustomNotificationView(title,message.getMessageText()));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(message.getRequestCode(), builder.build());
    }

    /**
     * notify without request code
     * @param intent
     * @param message
     */
    private void buildNotificationWithoutRequestCode(Intent intent, Message message){
        Log.d(TAG, "Got intent without request code ..................... ");
        String title = message.getMessageType();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle(title)
                .setContentText(message.getMessageText())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.care_icon)
                .setContentIntent(pendingIntent)
                .setContent(getCustomNotificationView(title,message.getMessageText()));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
    /**
     * create custom layout for notification
     * @param title
     * @param messageText
     * @return
     */
    private RemoteViews getCustomNotificationView(String title,String messageText) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews notificationView = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.styled_notification_layout
        );

        /*notificationView.setImageViewResource(
                R.id.cabaide_icon_img,
                R.drawable.car_edited_img_notification);*/

        // Locate and set the Text into customnotificationtext.xml TextViews
        notificationView.setTextViewText(R.id.notification_header,title);
        notificationView.setTextViewText(R.id.notification_content, messageText);

        return notificationView;
    }

}
