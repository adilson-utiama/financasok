package com.asuprojects.walletok.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarConverter {

    public static final String ISO8601 = "yyyy-MM-dd";
    public static final String BR_FORMATO = "dd/MM/yyyy";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat(ISO8601);

    public static String toStringFormatadaBR(Calendar data){
        SimpleDateFormat format = new SimpleDateFormat(BR_FORMATO);
        return format.format(data.getTime());
    }

    public static String toStringFormatada(Calendar data){

        return dateFormat.format(data.getTime());

//        String separador = "/";
//        StringBuilder builder = new StringBuilder();
//        int dia = data.get(Calendar.DAY_OF_MONTH);
//        int mes = data.get(Calendar.MONTH) + 1;
//        int ano = data.get(Calendar.YEAR);
//        builder.append(dia)
//                .append(separador)
//                .append(mes)
//                .append(separador)
//                .append(ano);
//        return builder.toString();
    }

    public static Calendar toCalendar(String dataString){
        //String formato aceitavel "10/5/2018" ou "10/10/2018"
        Calendar calendar = Calendar.getInstance();

        try {
            Date date = dateFormat.parse(dataString);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao converter data para Calendar");
        }

//        if(dataString.matches("[0-9]{1,2}\\/[0-9]{1,2}\\/[0-9]{4}")){
//            String[] array = dataString.split("/");
//            int dia = Integer.valueOf(array[0]);
//            int mes = Integer.valueOf(array[1]) - 1;
//            int ano = Integer.valueOf(array[2]);
//            calendar.set(ano, mes, dia);
//
//        } else {
//            //throw new RuntimeException("Formato de entrada invalido");
//            Log.i("CALENDAR_CONVERTER: ", dataString);
//        }
        return calendar;
    }
}
