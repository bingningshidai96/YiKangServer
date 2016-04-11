package com.yikang.app.yikangserver.application;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by liu on 16/3/29.
 */
public class AppConfigInfo {
    private static final String PRE_CONFIG_NAME = "user";

    public static final String DEVICE_ID ="device.deviceId";
    public static final String DEVICE_ID_TYPE ="device.idType";


    public static final String APP_NEW_VERSION ="app.newVersion";
    public static final String APP_Cache_version_code ="app.cache.versionCode";
    public static final String APP_FIRST_RUN = "app.firstRun";//第一次使用

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
