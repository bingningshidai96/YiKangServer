package com.yikang.app.yikangserver.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.yikang.app.yikangserver.application.AppContext;

/**
 * Created by liu on 15/12/21.
 */
public class DeviceUtils {


    /**
     * 将dp值转化成pix的值
     * @param dp 像素值
     * @return pix
     */
    public static int dpToPix(int dp){
        AppContext appContext = AppContext.getAppContext();
        float density = appContext.getResources().getDisplayMetrics().density;
        return (int) (dp*density);
    }


    /**
     * 将pix转化成dp的值
     */
    public static int pixToDp(int pix){
        AppContext appContext = AppContext.getAppContext();
        float density = appContext.getResources().getDisplayMetrics().density;
        return (int) (pix/density);
    }



    /**
     * 判断网络是否已经连接
     */
    public static boolean checkNetWorkIsOk(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnected();
        }
        return false;

    }

    /**
     * 获取当前网络类型，跟ConnectivityManager中定义的一样
     */
    public static int getNetWorkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return -1;
        }
        return networkInfo.getType();
    }

}
