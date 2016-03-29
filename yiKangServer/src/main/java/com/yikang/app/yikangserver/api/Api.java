package com.yikang.app.yikangserver.api;

import android.text.TextUtils;
import com.google.gson.reflect.TypeToken;
import com.yikang.app.yikangserver.api.callback.DownloadCallback;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.api.client.ApiClient;
import com.yikang.app.yikangserver.api.client.FileRequestParam;
import com.yikang.app.yikangserver.api.client.RequestParam;
import com.yikang.app.yikangserver.api.client.ResponseContent;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.FileResponse;
import com.yikang.app.yikangserver.bean.InviteCustomer;
import com.yikang.app.yikangserver.bean.Order;
import com.yikang.app.yikangserver.bean.ServiceOrder;
import com.yikang.app.yikangserver.bean.ServiceScheduleData;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.ui.FreeTimeActivity;
import com.yikang.app.yikangserver.utils.UpdateManger.AndroidUpdate;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 本应用的所有api
 * @author 刘光辉 2016-03-01
 */
public class Api {
    /** 中国主机*/
    private static final String SERVER_HOST = "http://54.223.35.197:8088";

//    /** 测试主机 */
//    private static final String SERVER_TEST = "http://54.223.53.20";
    private static final String SERVER_LOCAL_TEST = "http://192.168.1.28:8088";



    /************* 下面才是基地址 ***********/
    /**接口开发的地址*/
    public static final String BASE_URL = SERVER_LOCAL_TEST +"/yikangservice/service";
    private static final String IMAGE_SERVER_HOST = "http://54.223.35.197";
    /*** 文件上传服务器的地址*/
    private static final String BASE_FILE_URL = "http://54.223.35.197:8088/yikangFileManage";


    /**
     * 获取我的患者
     *
     * @param userStatus
     * @param callback
     */
    public static void getMyPaintList(int userStatus, ResponseCallback<List<InviteCustomer>> callback) {
        String url = BASE_URL + "/00-17-09";
        RequestParam param = new RequestParam();
        param.add("userStatus", userStatus);
        Type type = new TypeToken<ResponseContent<List<InviteCustomer>>>(){}.getType();
        ApiClient.execute(url, param, callback, type);
    }


    /**
     * 获取订单列表
     *
     * @param userId
     * @param callback
     */
    public static void getOrderList(String userId, ResponseCallback<List<Order>> callback) {
        String url = BASE_URL + "/00-21-07";
        RequestParam param = new RequestParam();
        if (!TextUtils.isEmpty(userId)) {
            param.add("paramUserId", userId);
        }
        Type type = new TypeToken<ResponseContent<List<Order>>>(){}.getType();
        ApiClient.execute(url, param, callback,type);
    }

    /**
     * 重置密码
     *
     * @param userName
     * @param password
     * @param callback
     */
    public static void resetPassword(String userName, String password, ResponseCallback<Void> callback) {
        String url = BASE_URL + "/00-17-08";
        RequestParam param = new RequestParam();
        param.add("loginName", userName);
        param.add("password", password);
        Type type = new TypeToken<ResponseContent<Void>>(){}.getType();
        ApiClient.execute(url, param, callback,type);
    }

    /**
     * 获取服务日程列表
     *
     * @param orderStatus
     * @param callback
     */
    public static void getServiceTaskList(int orderStatus, ResponseCallback<List<ServiceOrder>> callback) {
        String url = BASE_URL + "/00-21-04";
        RequestParam param = new RequestParam();
        param.add("serviceDetailStatus", orderStatus);
        Type type = new TypeToken<ResponseContent<List<ServiceOrder>>>(){}.getType();
        ApiClient.execute(url, param, callback,type);
    }


    /**
     * 修改用户信息
     *
     * @param paramMap
     * @param callback
     */
    public static void alterUserInfo(Map<String, Object> paramMap, ResponseCallback<Void> callback) {
        String url = BASE_URL
                + "/00-17-05";
        RequestParam param = new RequestParam();
        param.addAll(paramMap);
        Type type = new TypeToken<ResponseContent<Void>>(){}.getType();
        ApiClient.execute(url, param, callback,type);
    }


    /**
     * 获取空闲服务时间
     * @param callback
     */
    public static void getFreeDays(ResponseCallback<List<ServiceScheduleData>> callback) {
        String url = BASE_URL + "/00-19-01";
        RequestParam param = new RequestParam();
        Calendar calendar = Calendar.getInstance();
        param.add("year", calendar.get(Calendar.YEAR));
        param.add("month", calendar.get(Calendar.MONTH) + 1);
        Type type = new TypeToken<ResponseContent<List<ServiceScheduleData>>>(){}.getType();
        ApiClient.execute(url, param, callback,type);
    }


    /**
     * 登录
     * @param userName
     * @param password
     * @param callback
     */
    public static void login(String userName,String password,ResponseCallback<String> callback){
        String url = BASE_URL + "/login";
        RequestParam param = new RequestParam("", "");
        param.add("loginName", userName);
        param.add("passWord", password);
        param.add("machineCode", AppContext.getAppContext().getDeviceID());
        Type type = new TypeToken<ResponseContent<String>>(){}.getType();
        ApiClient.execute(url, param, callback,type);
    }

    /**
     * 注册设备
     * @param codeType 设备编码类型
     * @param deviceCode 设备码
     * @param callback
     */
    public static void registerDevice(int codeType,String deviceCode,ResponseCallback<Void> callback){
        String url = BASE_URL + "/00-18-01";
        RequestParam param = new RequestParam();
        param.add("deviceType", 0);
        param.add("codeType", codeType);
        param.add("deviceCode", deviceCode);
        Type type = new TypeToken<ResponseContent<Void>>(){}.getType();
        ApiClient.execute(url, param, callback,type);
    }

    /**
     * 获取用户信息
     * @param callback
     */
    public static void getUserInfo(ResponseCallback<User> callback){
        String url =  BASE_URL + "/00-17-04";
        RequestParam param = new RequestParam();
        Type type = new TypeToken<ResponseContent<User>>(){}.getType();
        ApiClient.execute(url, param, callback,type);
    }


    /**
     * 获取极光推送的别名
     * @param callback
     */
    public static void getPushAlias(ResponseCallback<Map<String,String>> callback){
        String url = BASE_URL + "/00-18-02";
        Type type = new TypeToken<ResponseContent<Map<String,String>>>(){}.getType();
        ApiClient.execute(url, new RequestParam(), callback,type);

    }

    /**
     * 注册
     * @param paramMap
     * @param callback
     */
    public static void register(Map<String, Object> paramMap, ResponseCallback<Void> callback){
        String url = BASE_URL + "/registerUserAndSaveServiceInfo";
        RequestParam param = new RequestParam("", "");
        param.addAll(paramMap);
        Type type = new TypeToken<Void>(){}.getType();
        ApiClient.execute(url, param, callback,type);
    }




    /**
     * 获取订单详情
     * @param orderId
     * @param callback
     */
    public static void getOrderDetail(String orderId,ResponseCallback<ServiceOrder> callback){
        String url = BASE_URL + "/00-21-05";
        RequestParam param = new RequestParam();
        param.add("orderServiceDetailId", orderId);
        Type type = new TypeToken<ResponseContent<ServiceOrder>>(){}.getType();
        ApiClient.execute(url, param, callback,type);
    }

    /**
     * 提交反馈
     * @param orderId
     * @param feedback
     * @param callback
     */
    public static void submitFeedBack(String orderId,String feedback,ResponseCallback<Void> callback){
        String url = BASE_URL + "/00-21-06";
        RequestParam param = new RequestParam();
        param.add("orderServiceDetailId", orderId);
        param.add("feedback", feedback);

        Type type = new TypeToken<ResponseContent<Void>>(){}.getType();
        ApiClient.execute(url, param, callback,type);
    }


    /**
     * 下载新版apk
     * @param callback
     */
    public static void downloadNewApk(String downloadUrl,String savePath, DownloadCallback callback){
        ApiClient.downloadFile(downloadUrl, savePath, callback);
    }

    /**
     * 检查更新
     * @param callback
     * TODO url
     */
    public static void checkUpdate(ResponseCallback<AndroidUpdate> callback){
        String url ="";
        RequestParam param = new RequestParam();
        Type type = new TypeToken<ResponseContent<AndroidUpdate>>(){}.getType();
        ApiClient.execute(url, param, callback, type);

    }


    /**
     * 上传文件
     * @param file
     * @param callback
     */
    public static void uploadFile(File file,ResponseCallback<FileResponse> callback){
        String url = BASE_FILE_URL + "/fileUpload/imageFileUpload";
        FileRequestParam param = new FileRequestParam("headImage");
        param.addFile(file);
        Type type = new TypeToken<ResponseContent<FileResponse>>(){}.getType();
        ApiClient.postFilesAsyn(url, param, callback, type);

    }

    /**
     * 获取一天的空闲时间段
     * @param serviceDate
     * @param callback
     */
    public static void getEditTime(String serviceDate,ResponseCallback<FreeTimeActivity.FreeTimeData> callback){
        String url = BASE_URL + "/00-19-02";
        RequestParam param = new RequestParam();
        param.add("serviceDate", serviceDate);
        Type type = new TypeToken<ResponseContent<FreeTimeActivity.FreeTimeData>>(){}.getType();
        ApiClient.execute(url, param, callback, type);

    }

    /**
     * 提交选择的时间
     * @param serviceDate
     * @param list
     */
    public static void submitTimes(String serviceDate,List<Integer> list,ResponseCallback<Void> callback){
        String url = BASE_URL + "/00-19-03";
        RequestParam param = new RequestParam();
        param.add("serviceDate", serviceDate);
        param.add("timeQuantumIds", list);

        Type type = new TypeToken<ResponseContent<Void>>(){}.getType();
        ApiClient.execute(url, param, callback, type);

    }



}
