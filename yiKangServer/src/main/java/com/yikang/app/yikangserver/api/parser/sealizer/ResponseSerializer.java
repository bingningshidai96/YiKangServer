package com.yikang.app.yikangserver.api.parser.sealizer;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.yikang.app.yikangserver.api.client.ResponseContent;
import com.yikang.app.yikangserver.api.parser.GsonFactory;
import com.yikang.app.yikangserver.utils.AES;
import com.yikang.app.yikangserver.utils.LOG;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
/**
 * 数据回应的解析器
 * Created by liu on 16/3/10.
 * @author 刘光辉
 *
 */
public class ResponseSerializer implements JsonDeserializer<ResponseContent>,JsonSerializer<ResponseContent> {
    public static final String TAG = "ResponseSerializer";
    private boolean isAes;

    public ResponseSerializer(boolean isAes){
        this.isAes = isAes;
    }


    @Override
    public ResponseContent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = GsonFactory.newInstance(new GsonFactory.EncryptedProvider());



        JsonObject jsonObject = json.getAsJsonObject();
        String status = jsonObject.get("status").getAsString();
        String message = jsonObject.get("message").getAsString();


        ResponseContent responseContent = new ResponseContent();
        responseContent.setStatus(status);
        responseContent.setMessage(message);

        if(jsonObject.get("data")!=null && !jsonObject.get("data").isJsonNull()){
            String data = null;
            if(isAes){
                String srcData = jsonObject.get("data").getAsString();
                data = AES.decrypt(srcData, AES.getKey());
            }else{
                data = jsonObject.get("data").toString();
            }

            LOG.i(TAG,"[deserialize]>>>>>>>>>>>>>>>>"+data);
            Type type = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
            responseContent.setData(gson.fromJson(data, type));
        }

        return responseContent;
    }

    @Override
    public JsonElement serialize(ResponseContent src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}

