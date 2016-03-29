package com.yikang.app.yikangserver.api.parser.sealizer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by liu on 16/3/24.
 */
public class DoubleSealizer implements JsonSerializer<Double> {
    @Override
    public JsonElement serialize(Double src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        if(src%1 == 0){
            Integer value = (int) Math.round(src);
            return new JsonPrimitive(value);
        }
        return new JsonPrimitive(src);
    }
}
