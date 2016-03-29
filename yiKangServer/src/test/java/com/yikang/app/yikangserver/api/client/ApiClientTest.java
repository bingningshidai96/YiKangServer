package com.yikang.app.yikangserver.api.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.yikang.app.yikangserver.api.parser.sealizer.BooleanSerializer;
import com.yikang.app.yikangserver.bean.*;

import org.junit.Test;

import java.lang.reflect.Type;

/**
 * Created by liu on 16/3/10.
 */
public class ApiClientTest {

    @Test
    public void testExecute() throws Exception {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Double.class, new JsonSerializer<Double>() {

            public JsonElement serialize(Double src, Type typeOfSrc,
                                         JsonSerializationContext context) {

                Integer value = (int) Math.round(src);
                return new JsonPrimitive(value);
            }
        });

        Gson gson = gsonBuilder.create();



//        String json ="{\"addressDetail\":\"zoom\",\"photoUrl\":\"https://biophoto.s3.cn-north-1.amazonaws.com.cn/6c088be2-c3ac-4ecb-917c-b24e56393a69.jpg\",\"offices\":\"\",\"districtCode\":\"110108\",\"hospital\":\"\",\"invitationUrl\":\"http://hulingjia.com/invite/inviteRegister?invitationCode\\u003dnull\",\"mapPositionAddress\":\"摸错门牛肚火锅(双安店)\",\"userName\":\"默默\",\"adept\":[{\"adeptId\":\"2\",\"adeptName\":\"腿部按摩\",\"isCheck\":0},{\"adeptId\":\"4\",\"adeptName\":\"颈椎矫正\",\"isCheck\":0}],\"userId\":\"32\",\"infoWrite\":0,\"jobCategory\":1,\"nums\":0,\"userPosition\":2,\"positionAuditStatus\":0}";

        String json = "{\"adeptId\":\"2\",\"adeptName\":\"腿部按摩\",\"isCheck\":true,\"number\":1}";

        Type type = new TypeToken<Expert>() {}.getType();
        Expert expert = gson.fromJson(json, type);

        System.out.println(expert);

        System.out.println(gson.toJson(expert));
    }



    public static class Base {
        @Expose
        @SerializedName("class")
        protected String clazz = getClass().getSimpleName();
        protected String control = "ctrl";
    }

    public class Child extends Base {
        protected String text = "This is text";
        protected boolean boolTest = false;
    }

    /**
     * @param args
     */
    public  void aaa(String[] args) {
        GsonBuilder b = new GsonBuilder();
        BooleanSerializer serializer = new BooleanSerializer();
        b.registerTypeAdapter(Boolean.class, serializer);
        b.registerTypeAdapter(boolean.class, serializer);
        Gson gson = b.create();

        Child c = new Child();
        System.out.println(gson.toJson(c));
        String testStr = "{\"text\":\"This is text\",\"boolTest\":1,\"class\":\"Child\",\"control\":\"ctrl\"}";
        Child cc = gson.fromJson(testStr, Child.class);
        System.out.println(gson.toJson(cc));



        testStr ="{\"timeQuantumId\":1,\"startTime\":8,\"endTime\":10,\"isChecked\":1}";
        Type type = new TypeToken<TimeDuration>() {
        }.getType();
        TimeDuration timeDuration = gson.fromJson(testStr, type);
        System.out.println(timeDuration);
    }
}