package com.yikang.app.yikangserver.application;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liu on 16/3/29.
 */
public class UserCofig {
    private static final String PRE_CONFIG_NAME = "user";

    public static final String USER_INFO ="user.info";
    public static final String USER_ACCESSTICKET ="user.accessTicket";
    public static final String USER_IS_DEVICE_REGIST ="user.isDeviceRegistered";



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

    public static void remove(String ... keys){
        SharedPreferences.Editor editor = getPreferences().edit();
        for(String key : keys){
            editor.remove(key);
        }
        editor.apply();
    }

}
