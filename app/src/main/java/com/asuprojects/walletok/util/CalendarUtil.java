package com.asuprojects.walletok.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarUtil {

    public static final String BR_FORMAT_REGEX = "[0-9]{1,2}\\\\/[0-9]{1,2}\\\\/[0-9]{4}";
    public static final String US_FORMAT_REGEX = "[0-9]{4}\\\\/[0-9]{1,2}\\\\/[0-9]{1,2}";
    public static final String BR_FORMATO = "dd/MM/yyyy";
    public static final String ISO8601 = "yyyy-MM-dd";

    public static String toStringFormatadaPelaRegiao(Calendar data){
        Locale locale = Locale.getDefault();
        if(locale.getLanguage().equals("pt")){
            return new SimpleDateFormat(BR_FORMATO).format(data.getTime());
        }
        return new SimpleDateFormat(ISO8601).format(data.getTime());
    }

    public static String getDataAtualFormatada(String separador){
        Calendar atual = Calendar.getInstance();
        int dia = atual.get(Calendar.DAY_OF_MONTH);
        int mes = atual.get(Calendar.MONTH);
        int ano = atual.get(Calendar.YEAR);
        StringBuilder builder = new StringBuilder();
        builder.append(dia).append(separador)
                .append(mes).append(separador)
                .append(ano);
        return builder.toString();
    }

    public Calendar stringToCalendar(String data){
        Calendar calendar = Calendar.getInstance();
        if(data.matches(BR_FORMAT_REGEX)) {
            String[] numeros = data.split("\\\\/D");
            int dia = Integer.parseInt(numeros[0]);
            int mes = Integer.parseInt(numeros[1]);
            int ano = Integer.parseInt(numeros[2]);
            calendar.set(ano, mes, dia);
            return calendar;
        }
        if(data.matches(US_FORMAT_REGEX)){
            String[] numeros = data.split("\\\\/D");
            int ano = Integer.parseInt(numeros[0]);
            int mes = Integer.parseInt(numeros[1]);
            int dia = Integer.parseInt(numeros[2]);
            calendar.set(ano, mes, dia);
            return calendar;
        }
        throw new RuntimeException("Formato de data não identificado.");
    }
}
