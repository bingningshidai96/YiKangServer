package com.yikang.app.yikangserver.api.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yikang.app.yikangserver.api.client.ResponseContent;
import com.yikang.app.yikangserver.api.parser.sealizer.BooleanSerializer;
import com.yikang.app.yikangserver.api.parser.sealizer.ResponseSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liu on 16/3/10.
 */
public class GsonFactory {

    private static Gson aesGson;
    private static Gson notAesGson;

    private static HashMap<Class<? extends GsonProvider>,Gson> registry;

    public static Gson newInstance(GsonProvider provider){
        Class<? extends GsonProvider> clazz = provider.getClass();
        Gson cacheGson = registry.get(clazz);

        if(cacheGson == null){
            cacheGson = provider.newInstance();
            registry.put(clazz,cacheGson);
        }
        return cacheGson;
    }


    public interface GsonProvider{
       Gson newInstance();
    }


    public static class EncryptedProvider implements GsonProvider{
        @Override
        public Gson newInstance() {
            BooleanSerializer booleanSerializer = new BooleanSerializer();
            ResponseSerializer responseSerializer = new ResponseSerializer(true);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Boolean.class, booleanSerializer)
                    .registerTypeAdapter(boolean.class, booleanSerializer)
                    .registerTypeAdapter(ResponseContent.class, responseSerializer)
                    .create();

            return gson;
        }
    }


    public static class NonEncryptedProvider implements GsonProvider{
        @Override
        public Gson newInstance() {
            BooleanSerializer booleanSerializer = new BooleanSerializer();
            ResponseSerializer responseSerializer = new ResponseSerializer(false);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Boolean.class, booleanSerializer)
                    .registerTypeAdapter(boolean.class, booleanSerializer)
                    .registerTypeAdapter(ResponseContent.class, responseSerializer)
                    .create();
            return gson;
        }
    }



}


