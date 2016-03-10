package com.yikang.app.yikangserver.api.parse.sealizer;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.yikang.app.yikangserver.api.client.ResponseContent;
import com.yikang.app.yikangserver.api.parse.GsonFatory;
import com.yikang.app.yikangserver.utils.AES;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
/**
 * 数据回应的解析器
 * Created by liu on 16/3/10.
 * @author 刘光辉
 *
 */
public class ResponseSerializer implements JsonDeserializer<ResponseContent>,JsonSerializer<ResponseContent> {
    private boolean isAes;

    public ResponseSerializer(boolean isAes){
        this.isAes = isAes;
    }


    @Override
    public ResponseContent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = GsonFatory.getCommonGsonInstance(isAes);
        if(!isAes)
            return gson.fromJson(json.getAsString(),typeOfT);

        JsonObject jsonObject = json.getAsJsonObject();
        String status = jsonObject.get("status").getAsString();
        String message = jsonObject.get("message").getAsString();




        ResponseContent responseContent = new ResponseContent();
        responseContent.setStatus(status);
        responseContent.setMessage(message);

        if(jsonObject.get("data")!=null){
            String srcData = jsonObject.get("data").getAsString();
            String data = AES.decrypt(srcData, AES.getKey());
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

