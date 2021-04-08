package com.caringaide.user.utils.notification;

import android.content.Intent;
import android.util.Log;

import com.caringaide.user.activities.ListBookingsActivity;
import com.caringaide.user.activities.UserHomeActivity;
import com.caringaide.user.context.BuddyApp;
import com.caringaide.user.services.AppFirebaseMessagingService;
import com.caringaide.user.utils.BuddyConstants;


/**
 * Created by renju.
 */

public class AppNotificationManager {

    //private Message notificationMessage;
    //private BuddyConstants.NotificationTypes notificationType;
    private AppFirebaseMessagingService firebaseMessagingService;
    private static AppNotificationManager notificationManager = null;
    private static final String TAG = "AppNotificationManager";

    // private constructor
    private AppNotificationManager(AppFirebaseMessagingService fireBaseService){
        this.firebaseMessagingService = fireBaseService;
    }


    /**
     * get singleton instance of AppNotificationManager
     * @param fireBaseService
     * @return
     */
    public static synchronized AppNotificationManager getInstance(AppFirebaseMessagingService fireBaseService){
        if(null == notificationManager)
            notificationManager = new AppNotificationManager(fireBaseService);

        return notificationManager;
    }

    /**
     * executes the notification message and returns with the next action.
     * @return
     */
    public synchronized Message execute(Message message){
        //initialize();
        Message notificationMessage = message;
        BuddyConstants.NotificationTypes notificationType=null;
        BuddyConstants.NotificationMsgs notificationMsg=null;
        // if message itself is null
        if(null == notificationMessage){
            notificationMessage = new Message("Error");
            notificationMessage.getResponse().setError(true);
            notificationMessage.getResponse().setErrorDescription(TAG+"Message itself is empty");
            return notificationMessage;
        }
        String messageType = notificationMessage.getMessageType();
        String messageKey = notificationMessage.getMessageText();
        Log.d(TAG, "execute: messageType="+messageType+" messageKey= "+messageKey);
        //if message type is null
        if(null == messageType || messageType.isEmpty()){
            notificationMessage.getResponse().setError(true);
            notificationMessage.getResponse().setErrorDescription(TAG+"Message type is null or empty");
            return notificationMessage;
        }
        // get the different notification types and messages handled by the app
        if (null!= BuddyApp.getApplicationContext()) {
            BuddyConstants.NotificationTypes[] notificationTypes = BuddyConstants.NotificationTypes.values();
            BuddyConstants.NotificationMsgs[] notificationMsgs = BuddyConstants.NotificationMsgs.values();
            for (int i = 0; i < notificationTypes.length; i++) {
                //get the notification type for the message type
                if (notificationTypes[i].name().equals(messageType)) {
                    notificationType = notificationTypes[i];
                    notificationMessage.setMessageType(notificationType.getMessageType()); //TITLE
                    notificationMessage.setNotificationType(notificationType);
                    break;
                }
            }

            for (int i = 0; i < notificationMsgs.length; i++) {
                //get the notification type for the message type
                if (notificationMsgs[i].name().equals(messageKey)) {
                    notificationMsg = notificationMsgs[i];
                    notificationMessage.setMessageText(notificationMsg.getMessage()); //message
                    break;
                }
            }
        }
        //no specific type
        if(null != notificationType && null != notificationMessage){
            //hanlde the intents
            handleNotification(notificationMessage,notificationType);
        }else{
            defaultAction(notificationMessage);
        }

        return notificationMessage;
    }

    /**
     * executes the action based on the message type
     */
    private void handleNotification(Message notificationMessage,BuddyConstants.NotificationTypes notificationType){
//        if(null==notificationType){
//            Log.i(TAG,"THis notification type is not handled " + notificationMessage.getMessageType());
//            defaultAction(notificationType,notificationMessage);
////            return;
//        }else {
            switch (notificationType) {
                case NEW_BOOKING_TITLE:
                    goToBookingListsAction(notificationType, notificationMessage);
                    break;
                case NEW_BOOKING_TITLE_FOR_BUDDY:
                    goToBookingListsAction(notificationType, notificationMessage);
                    break;
                case BOOKING_CANCELLED_TITLE:
                    goToBookingListsAction(notificationType, notificationMessage);
                    break;
                case BOOKING_CONFIRMED_TITLE_BY_BUDDY:
                    goToBookingListsAction(notificationType, notificationMessage);
                    break;
                case SERVICE_STARTED_TITLE_BY_BUDDY:
                    goToBookingListsAction(notificationType, notificationMessage);
                    break;
                case SERVICE_ENDED_TITLE_BY_BUDDY:
                    goToBookingListsAction(notificationType, notificationMessage);
                    break;
                // case NEW_BOOKING_TITLE:
                //     defaultAction(notificationType,notificationMessage);
                //     break;
                default:
                    defaultAction(notificationMessage);
                    break;
            }
//        }
    }

    private void defaultAction(Message notificationMessage) {
        Intent intent = new Intent(firebaseMessagingService, UserHomeActivity.class);
//        Intent intent = new Intent();
//        intent.setClassName("com.caringaide.user.activities","com.caringaide.user.activities.ListBookingsActivity");
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (null!=intent) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(BuddyConstants.MESSAGE_TEXT, notificationMessage.getMessageText());
            intent.putExtra(BuddyConstants.MESSAGE_TYPE, notificationMessage.getMessageType());
            notificationMessage.getResponse().setIntent(intent);
        }
    }
    private void goToBookingListsAction(BuddyConstants.NotificationTypes notificationType, Message notificationMessage) {
        Intent intent = new Intent(firebaseMessagingService, ListBookingsActivity.class);
//        Intent intent = new Intent();
//        intent.setClassName("com.caringaide.user.activities","com.caringaide.user.activities.ListBookingsActivity");
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (null!=intent) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(BuddyConstants.MESSAGE_TEXT, notificationMessage.getMessageText());
            intent.putExtra(BuddyConstants.MESSAGE_TYPE, notificationType.name());
            notificationMessage.getResponse().setIntent(intent);
        }
    }

}
