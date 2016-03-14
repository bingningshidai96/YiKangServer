package com.yikang.app.yikangserver.api.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.yikang.app.yikangserver.api.parser.sealizer.BooleanSerializer;
import com.yikang.app.yikangserver.bean.TimeDuration;

import org.junit.Test;

import java.lang.reflect.Type;

/**
 * Created by liu on 16/3/10.
 */
public class ApiClientTest {

    @Test
    public void testExecute() throws Exception {
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(ResponseContent2.class,new Response2Serializer(true))
//                .create();
//
//        String json ="{\"data\":\"c002651e86472930648fd1e47339e5e04af650d9c1986097643962efa5425dce7c172c368c3af8a7c15ad72b80424184160baeea8179bdb6284a558c3aaa544bd1ed5ae2051f4d8c8d6b108bb7d735fc5b6fa4a91f8ddb2e143fe43f4ffe0d3db2d7a0cda2b8f0752bc4d68f24827ac59adb658d54d8059cace6cc26baa366c3080732ffc40a54f898ebeb57624ea82fece5b231e0918b2a5f2828eb261e26682a47b5fb35a2a14f3c6187e38da04a5b796cd3c07c2da878e56c247e8680b48ba471ebfa87d6c48bd8e4f415a48bf2f97b81db271f231e50827fb7d962886ff4c7c62db3f99089b9515f1fedba73a1676250e025ab87afc5a42d7a59fb3c71da36faf3b75d669e8d987cd6d1bda889fa7afbf396cd8c9e8231411857a5c28f6a429d51498eb1702101b488e53fce5081aa18fe5b2949a8780c9a0f35fb654f3038eb51f7b9d953309510a301cd2daec70a1d5501efd32f800429dbd4e856a25294d1c6555c83278fa046318982c5986564759f3aacb41e9af9352c80e2f7ef73f004305026ce1cc3a0d9288fb23f2a031ffcfab161849b16ec84f864c7fb68abff2b289efa68584fb62f3025eb1262e7b336b63235fbd96e85098401f1ba4e182269d5ea6bb482af010085f7bae74581365f5d18d90b071bfae8cdb077dfd9debcf2cb716a73e39de0a79f6688ae06e059888502414d2f2b0a478fcfbb1f55ce\",\"message\":\"操作成功！\",\"status\":\"000000\"}";
//
//        ResponseContent2<User> res = gson.fromJson(json, new TypeToken<ResponseContent2<User>>() {
//        }.getType());
//        System.out.println(res.toString());
        aaa(null);
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