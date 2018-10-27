package com.asuprojects.walletok.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalConverter {

    public static String toStringFormatado(BigDecimal valor){
        DecimalFormat format = new DecimalFormat("¤ ###,##0.00");
        return format.format(valor.doubleValue());
    }

    public static String toStringFormatado(double valor){
        DecimalFormat format = new DecimalFormat("¤ ###,##0.00");
        return format.format(valor);
    }

    public static BigDecimal toBigDecimal(String valorString){
        double valor = 0;
        try{
            valor = Double.parseDouble(valorString);
        }catch(Exception e){
            e.printStackTrace();
        }
        return new BigDecimal(valor);
    }
}
