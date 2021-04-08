package com.caringaide.user.utils.notification;

import android.content.Intent;

import com.caringaide.user.utils.BuddyConstants;

import java.util.Random;

/**
 * Created by renju.
 */

public class Message {
    private String messageType;
    private String messageText;
    private BuddyConstants.NotificationTypes notificationType;
    private Response response;
    private int requestCode;
    private static Random random = new Random();

    public Message(String messageType){
        this.messageType = messageType;
        this.response = new Response();
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public BuddyConstants.NotificationTypes getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(BuddyConstants.NotificationTypes notificationType) {
        this.notificationType = notificationType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Response getResponse(){
        return this.response;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode() {
        this.requestCode = random.nextInt(1000);
    }

    public class Response{
        private boolean error = false;
        private String errorDescription;
        private Intent intent;

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

        public String getErrorDescription() {
            return errorDescription;
        }

        public void setErrorDescription(String errorDescription) {
            this.errorDescription = errorDescription;
        }

        public Intent getIntent() {
            return intent;
        }

        public void setIntent(Intent intent) {
            this.intent = intent;
        }
    }
}
