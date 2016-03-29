import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.api.client.ResponseContent;
import com.yikang.app.yikangserver.api.parser.GsonFactory;
import com.yikang.app.yikangserver.api.parser.sealizer.BooleanSerializer;
import com.yikang.app.yikangserver.api.parser.sealizer.ResponseSerializer;
import com.yikang.app.yikangserver.bean.Expert;
import com.yikang.app.yikangserver.bean.FileResponse;
import com.yikang.app.yikangserver.utils.AES;

import org.junit.Test;

import java.lang.Object;
import java.lang.System;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * Created by liu on 16/3/10.
 */
public class JavaTest {

    private static final int CODE_NAME = 1<<0;
    private static final int CODE_PROFESSION = 1<<1;
    private static final int CODE_ADDRESS= 1<<2;
    private static final int CODE_HOSPITAL = 1<<3;
    private static final int CODE_AVATAR = 1<<4;
    private static final int CODE_SPECIAL = 1<<5;
    private static final int CODE_OFFICE = 1<<6;
    private static final int CODE_WORK_TYPE = 1<<7;

    private static final int HOSPITAL_MASK = CODE_NAME|CODE_HOSPITAL|CODE_PROFESSION|CODE_OFFICE ;
    private static final int NURSING_MASK = CODE_NAME|CODE_PROFESSION|CODE_ADDRESS|CODE_WORK_TYPE|CODE_SPECIAL|CODE_HOSPITAL;
    private static final int THERAPIST_MASK = CODE_NAME|CODE_PROFESSION|CODE_ADDRESS|CODE_WORK_TYPE|CODE_SPECIAL;


    @Test
    public void javaTest(){
        String src ="d96405c795fc40d79bcceab2b14e603dbed7a5fbf4641ada125da9a7f4698d3770c4fff5e643aa385509f2b53b59fe57e0de97eb6557c9c7402f5e8e67d29412c594321fa2140d37925d31682800a015aa24fb5a13cb6cdd6dba41645c685eae524f336806dabddd1ab89580872c533f";
        String src1 = "2d60a8be89496c02073337f78927bafab74aa8b11b2abd910eee5bedb506e6ce";
        System.out.println(AES.decrypt(src,AES.getKey()));

        String ss ="{\"status\":\"000000\",\"message\":\"您的验证码，已经发送！请注意，手机提醒！\"}";

        int aa =CODE_NAME|CODE_HOSPITAL|CODE_PROFESSION|CODE_OFFICE;
     //   assert(((aa & HOSPITAL_MASK)^HOSPITAL_MASK) ==0);


//        String sss1 ="{\"data\":{\"fileName\":\"794de693-d1f9-42e7-912f-1e35435d1a15.jpg\",\"fileUrl\":\"https://biophoto.s3.cn-north-1.amazonaws.com.cn/794de693-d1f9-42e7-912f-1e35435d1a15.jpg\",\"oldFileName\":\"temp.jpg\"},\"message\":\"操作成功！\",\"status\":\"000000\"}";
//
 //         Gson gson = GsonFactory.newInstance(new GsonFactory.NonEncryptedProvider());
//        Type type = new TypeToken<ResponseContent<FileResponse>>() {}.getType();
//        Object file = gson.fromJson(sss1, type);
//        System.out.println(file.toString());

//
        Long number = new Long(2);

        assert (number.equals(2));












    }
}
