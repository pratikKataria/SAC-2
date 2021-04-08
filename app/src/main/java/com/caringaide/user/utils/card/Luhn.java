package com.caringaide.user.utils.card;

/**
 * Luhn validator for cardss
 * Created by renju on 26/06/17.
 */

public class Luhn {

    /**
     * validate cards against Luhn algorithm
     * @param numberString
     * @return
     */

    public static boolean validate(String numberString){
        if(null == numberString || numberString.isEmpty())
            return false;

        //cards should be atleast 13 number long
        if(numberString.length()-1 < 13)
            return false;

        return checkAlgo10(numberString);
    }

    /**
     * @param numberString
     * @return
     */
    private static boolean checkAlgo10(String numberString) {
        int sum = 0;
        // first set alternate to false;
        boolean isAlternate = false;
        for (int i = numberString.length() - 1; i >= 0; i--) {
            //get each digit from rightmost end
            int k = Integer.parseInt(String.valueOf(numberString.charAt(i)));
            //if the doubling results in two digits, then make it single digit by adding both digits.
            sum += sumToSingleDigit((k * (isAlternate ? 2 : 1)));
            //reverse the alternate, make it true if false after each iteration
            isAlternate = !isAlternate;
        }

        //if the modulo 10 operation results in 0 , then valid card number.
        return (sum%10==0 ? true : false);
    }

    /**
     * convert doble to single digit
     * @param k
     * @return
     */
    private static int sumToSingleDigit(int k) {
        if (k < 10)
            return k;
        return sumToSingleDigit(k / 10) + (k % 10);
    }
}
