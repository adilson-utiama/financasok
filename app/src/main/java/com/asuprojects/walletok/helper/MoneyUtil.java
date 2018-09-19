package com.asuprojects.walletok.helper;

import android.util.Log;

public class MoneyUtil {

    public static String valorEmDouble(String stringValor){
        String changedString = stringValor.replace(",", ".");
        String formatedValue = changedString;
        if(changedString.length() > 6){
            formatedValue = changedString.replaceFirst("[.]", "");
        }
        return formatedValue;
    }

}
