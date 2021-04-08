package com.caringaide.user.utils.card;

import android.content.Context;
import android.util.Log;

import com.caringaide.user.R;
import com.caringaide.user.utils.BuddyConstants;
import com.caringaide.user.utils.PatternUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.caringaide.user.utils.BuddyConstants.AMEX_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.DINERS_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.DINERS_LENGTH2;
import static com.caringaide.user.utils.BuddyConstants.DISCOVER_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.JCB_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.MAESTRO_LENGTH1;
import static com.caringaide.user.utils.BuddyConstants.MAESTRO_LENGTH2;
import static com.caringaide.user.utils.BuddyConstants.MASTER_LENGTH;
import static com.caringaide.user.utils.BuddyConstants.VISA_LENGTH1;
import static com.caringaide.user.utils.BuddyConstants.VISA_LENGTH2;
import static com.caringaide.user.utils.BuddyConstants.VISA_LENGTH3;

/**
 * Created by renjit on 22-06-2017.
 */
public class CardValidator {

    private static Map<String, String> cardRegexMap;
    public static Map<String, Integer> cardImageMap;
    static CardDetails cardDetails;
    private static final String TAG = "CardValidator";
    private static final String AMEX = "amex";
    private static final String VISA = "visa";
    private static final String MAESTRO = "maestro";
    private static final String DISCOVER = "discover";
    private static final String MASTERCARD = "mastercard";
    private static final String JCB = "jcb";
    private static final String DINERS = "dinersclub";

    static{
        cardRegexMap = new LinkedHashMap<String,String>();
        cardRegexMap.put(PatternUtil.amexRegex, AMEX); //upto 15
        cardRegexMap.put(PatternUtil.visaRegex, VISA); //length upto 19
        cardRegexMap.put(PatternUtil.maestroRegex,MAESTRO); //between 12 and 19
        cardRegexMap.put(PatternUtil.masterRegex, MASTERCARD);//upto 16
        cardRegexMap.put(PatternUtil.discoverRegex, DISCOVER); //upto 19
        cardRegexMap.put(PatternUtil.jcbRegex, JCB); //upto 15
        cardRegexMap.put(PatternUtil.dinersRegex, DINERS); //upto 16

        cardImageMap = new HashMap<>();
        cardImageMap.put("None", R.drawable.card_error_layer);
        cardImageMap.put(AMEX, R.drawable.amex_layer);
        cardImageMap.put(VISA, R.drawable.visa_layer);
        cardImageMap.put(MASTERCARD, R.drawable.mastercard_layer);
        cardImageMap.put(MAESTRO, R.drawable.maestro_layer);
        cardImageMap.put(DISCOVER, R.drawable.discover_layer);
        cardImageMap.put(JCB, R.drawable.jcb_layer);
        cardImageMap.put(DINERS, R.drawable.diners_layer);
    }


    /**
     * validate the card
     * @param context
     * @param cardNumber
     * @return
     */
    public static CardDetails checkCardValidity(Context context, String cardNumber) {
        cardDetails = new CardDetails();
        if (null==cardNumber || cardNumber.isEmpty()) {
            cardDetails.setCardValid(false);
            cardDetails.setCardImageId(cardImageMap.get("None"));
            return cardDetails;
        }else{
            Iterator iterator = cardRegexMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry regexPair = (Map.Entry) iterator.next();
                Pattern pattern = Pattern.compile(String.valueOf(regexPair.getKey()));
                Matcher matcher = pattern.matcher(cardNumber);
                while(matcher.find()){
                    String cardName = String.valueOf(regexPair.getValue());
                    cardDetails.setCardName(cardName);
                    if (cardImageMap.containsKey(cardName)) {
                        Log.d(TAG, "got card name from cardImage map" + cardName);
                        cardDetails.setCardImageId(cardImageMap.get(cardName));
                        cardDetails.setCardValid(false);
                        //found a valid card, check length and checkluhn algo
                        if(checkLength(cardDetails.getCardName(),cardNumber.length()) && Luhn.validate(cardNumber)){
                            cardDetails.setCardValid(true);
                            cardDetails.setMessage("Valid card");
                            return cardDetails;
                        }
                        //invalid conditions
                        if(cardNumber.length() >=19){
                            //you are trying with a number that exceeds limit,,, scumbag!!!!
                            cardDetails.setMessage(context.getString(R.string.invalid_card));
                            cardDetails.setCardValid(false);
                            cardDetails.setCardImageId(cardImageMap.get("None"));
                            return cardDetails;
                        }else{
                            // you might not have entered the complete card number
                            cardDetails.setMessage(context.getString(R.string.ask_whole_card_num));
                            cardDetails.setCardValid(false);
                            return cardDetails;
                        }
                    } else {
                        cardDetails.setMessage(context.getString(R.string.card_not_handled));
                        cardDetails.setCardValid(false);
                        cardDetails.setCardImageId(cardImageMap.get("None"));
                        Log.d(TAG, "card not available");
                        return cardDetails;
                    }
                }

            }//end while
            cardDetails.setMessage(context.getString(R.string.ask_card_num));
            cardDetails.setCardValid(false);
            cardDetails.setCardImageId(cardImageMap.get("None"));
            return cardDetails;
        }
    }

    /**
     * check the length of each card
     * @param cardName
     * @param length
     * @return
     */
    private static boolean checkLength(String cardName,int length){
        boolean isValid=false;
        switch (cardName){
            case AMEX:
                if(length == AMEX_LENGTH)
                    isValid = true;
                break;
            case VISA:
                if(length ==VISA_LENGTH1 || length == VISA_LENGTH2 || length == VISA_LENGTH3)
                    isValid = true;
                break;
            case MASTERCARD:
                if(length == MASTER_LENGTH)
                    isValid = true;
                break;
            case MAESTRO:
               if(length == MAESTRO_LENGTH1 || length == MAESTRO_LENGTH2)
                    isValid = true;
                    break;
            case DISCOVER:
                if(length == DISCOVER_LENGTH)
                isValid = true;
                break;
            case JCB:
                if (length == JCB_LENGTH)
                    isValid =true;
                break;
            case DINERS:
                if (length == DINERS_LENGTH ||length == DINERS_LENGTH2)
                    isValid =true;
                break;
            default:
                break;

        }
        return isValid;
    }

    /**
     * a class to represent a cards
     */
    public static class CardDetails {
        private String cardName;
        private int cardImageId;
        private String validationMessage;
        private boolean isCardValid;

        public String getCardName() {
            return cardName;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
        }

        public int getCardImageId() {
            return cardImageId;
        }

        public void setCardImageId(int cardImageId) {
            this.cardImageId = cardImageId;
        }

        public String getMessage() {
            return validationMessage;
        }

        public void setMessage(String message) {
            this.validationMessage = message;
        }

        public boolean isCardValid() {
            return isCardValid;
        }

        public void setCardValid(boolean cardValid) {
            isCardValid = cardValid;
        }

    }

}
