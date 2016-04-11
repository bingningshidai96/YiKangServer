package com.yikang.app.yikangserver.application;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.yikang.app.yikangserver.api.parser.GsonFactory;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.utils.AES;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by liu on 16/3/29.
 */
public class UserCofig {
    private static final String PRE_CONFIG_NAME = "user";

    public static final String USER_INFO ="user.info";
    public static final String USER_ACCESS_TICKET ="user.accessTicket";
    public static final String USER_IS_DEVICE_REGISTERED ="user.isDeviceRegistered";


    //加密
    public static final String LOGIN_NAME ="login.name";
    public static final String LOGIN_PASWORD ="login.password";


    /**
     * 获取用户的信息
     */
    public User getUser() {
        User user = null;
        try {
            String encryptedJson = get(USER_INFO,null);
            if(encryptedJson!=null){
                String json = AES.decrypt(encryptedJson, AES.getKey());
                Gson gson = GsonFactory.newInstance(new GsonFactory.NonEncryptedProvider());
                user = gson.fromJson(json, User.class);
            }
        }catch (Exception e){ //因为可能出现产品更新，数据不一致问题
            e.printStackTrace();
            logout(); //如果解析错误，则将用户数据清空
        }
        return user;
    }



    /**
     * 登出
     */
    public void logout() {
        remove(USER_ACCESS_TICKET,USER_IS_DEVICE_REGISTERED,USER_INFO);
    }


    /**
     * 登录
     */
    public void login(User user,String accessTicket){
        Gson gson = GsonFactory.newInstance(new GsonFactory.NonEncryptedProvider());
        try {
            String encrypt = AES.encrypt(gson.toJson(user), AES.getKey());
            set(USER_ACCESS_TICKET,accessTicket);
            set(USER_INFO,encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    public static SharedPreferences getPreferences() {
        return AppContext.getAppContext().getSharedPreferences(PRE_CONFIG_NAME, Context.MODE_PRIVATE);
    }

    public static int get(String key, int defaultVal) {
        return getPreferences().getInt(key, defaultVal);
    }

    public static String get(String key, String defaultVal) {
        return getPreferences().getString(key, defaultVal);

    }

    public static boolean get(String key, boolean defaultVal) {
        return getPreferences().getBoolean(key, defaultVal);
    }


    public static boolean contains(String key){
        return getPreferences().contains(key);
    }



    public static void set(String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value).apply();
    }

    public static void set(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value).apply();
    }


    public static void set(String key, int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value).apply();
    }


    public static void remove(String ... keys){
        SharedPreferences.Editor editor = getPreferences().edit();
        for(String key : keys){
            editor.remove(key);
        }
        editor.apply();
    }

}
