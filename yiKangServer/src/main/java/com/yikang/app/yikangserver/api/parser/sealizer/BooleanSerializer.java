package com.yikang.app.yikangserver.api.parser.sealizer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;


public class BooleanSerializer implements JsonSerializer<Boolean>,JsonDeserializer<Boolean>{
    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return !(json.getAsInt()==0);
    }

    @Override
    public JsonElement serialize(Boolean src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src?1:0);
    }
}
