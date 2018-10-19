package com.asuprojects.walletok.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarConverter {

    public static final String ISO8601 = "yyyy-MM-dd";

    public static String toStringFormatada(Calendar data){
        return new SimpleDateFormat(ISO8601).format(data.getTime());
    }

    public static Calendar toCalendar(String dataString){
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat(ISO8601).parse(dataString);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao converter data para Calendar");
        }
        return calendar;
    }
}
