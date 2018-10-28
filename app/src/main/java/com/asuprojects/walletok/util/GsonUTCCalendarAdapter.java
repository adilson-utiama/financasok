package com.asuprojects.walletok.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GsonUTCCalendarAdapter implements JsonSerializer<Calendar>,JsonDeserializer<Calendar> {

    private final DateFormat dateFormat;

    public GsonUTCCalendarAdapter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public Calendar deserialize(JsonElement jsonElement, Type arg1, JsonDeserializationContext arg2)
            throws JsonParseException {
        try {
            Calendar instance = Calendar.getInstance();
            instance.setTime(dateFormat.parse(jsonElement.getAsString()));
            return instance;
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Calendar data, Type arg1, JsonSerializationContext arg2) {
        String format = dateFormat.format(data.getTime());
        return new JsonPrimitive(format);
    }

}