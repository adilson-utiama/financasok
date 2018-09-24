package com.asuprojects.walletok.helper;

import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.util.BigDecimalConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

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

    public static String valorTotalFrom(List<?> lista){
        BigDecimal total = BigDecimal.ZERO;
        for(Object o : lista){
            if(o instanceof Despesa){
                total = total.add(((Despesa) o).getValor());
            }
            if(o instanceof Receita){
                total = total.add(((Receita) o).getValor());
            }
        }
        return BigDecimalConverter.toStringFormatado(total);
    }


}
