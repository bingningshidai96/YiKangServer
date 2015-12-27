package com.yikang.app.yikangserver.utils;

import com.yikang.app.yikangserver.application.AppContext;

/**
 * Created by liu on 15/12/21.
 */
public class DeviceUtils {


    /**
     * 将dp值转化成pix的值
     * @param dp 像素值
     * @return
     */
    public static int dpToPix(int dp){
        AppContext appContext = AppContext.getAppContext();
        float density = appContext.getResources().getDisplayMetrics().density;
        return (int) (dp*density);
    }


    /**
     * 将pix转化成dp的值
     * @param pix
     * @return
     */
    public static int pixToDp(int pix){
        AppContext appContext = AppContext.getAppContext();
        float density = appContext.getResources().getDisplayMetrics().density;
        return (int) (pix/density);
    }
}
