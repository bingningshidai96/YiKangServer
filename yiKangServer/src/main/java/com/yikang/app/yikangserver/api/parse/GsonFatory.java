package com.yikang.app.yikangserver.api.parse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yikang.app.yikangserver.api.client.ResponseContent;
import com.yikang.app.yikangserver.api.parse.sealizer.BooleanSerializer;
import com.yikang.app.yikangserver.api.parse.sealizer.ResponseSerializer;

/**
 * Created by liu on 16/3/10.
 */
public class GsonFatory {

    private static Gson aesGson;
    private static Gson notAesGson;

    public static Gson getCommonGsonInstance(boolean isAes){
        return getCommonInstance(isAes);
    }

    public static Gson getCommonGsonInstance(){
        return getCommonInstance(false);
    }

    private static Gson getCommonInstance(boolean isAes) {
        if(isAes && aesGson != null){
            return aesGson;

        }
        if(!isAes && notAesGson != null){
            return notAesGson;
        }

        BooleanSerializer booleanSerializer = new BooleanSerializer();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Boolean.class, booleanSerializer)
                .registerTypeAdapter(boolean.class, booleanSerializer)
                .registerTypeAdapter(ResponseContent.class, new ResponseSerializer(isAes))
                .create();

        if(isAes){
            aesGson = gson;
        }else{
            notAesGson = gson;
        }
        return gson;
    }




}


