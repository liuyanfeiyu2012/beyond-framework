package org.lyfy.beyond.es.client.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * @author sujiani 2020/4/30 11:31
 */
public class DateTypeAdapter implements JsonDeserializer<Date> {
    private DateFormat format;

    public DateTypeAdapter() {
    }
    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        if (!(json instanceof JsonPrimitive)) {
            throw new JsonParseException("This is not a primitive value");
        }
        String jsonStr = json.getAsString();
        return new Date(Long.parseLong(jsonStr));
    }
}
