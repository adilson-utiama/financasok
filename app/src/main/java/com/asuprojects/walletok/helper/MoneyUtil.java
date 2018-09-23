package com.asuprojects.walletok.helper;

import java.text.DecimalFormat;

public class MoneyUtil {

    public static String valorEmDouble(String stringValor){
        String changedString = stringValor.replace(",", ".");
        String formatedValue = changedString;
        if(changedString.length() > 6){
            formatedValue = changedString.replaceFirst("[.]", "");
        }
        return formatedValue;
    }

    public static String formatar(double valor){
        DecimalFormat fm = new DecimalFormat("#,###,##0.00");
        return fm.format(valor);
    }

}
