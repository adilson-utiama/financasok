package com.asuprojects.walletok.util;

public class StringUtils {

    public static String mesParaString(int value) {
        String mes;
        if(value < 10){
            mes = String.valueOf("0" + value);
        }else{
            mes = String.valueOf(value);
        }
        return mes;
    }
}
