package com.yikang.app.yikangserver.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liu on 16/2/1.
 */
public class Appointment {
   @SerializedName("appointmentUserId")
   public String appointmentId;  //订单编号

   @SerializedName("serviceTitle")
    public String serviceName; //服务名

   @SerializedName("serviceId")
    public String serviceId; // 该项服务的Id

   @SerializedName("createTime")
    public String createTime; //订单时间

   @SerializedName("remarks")
    public String comment; //备注

   @SerializedName("mobileNumber")
    public String userName; //用户名

   @SerializedName("servicePrice")
    public String servicePrice; //支付价格

    public String userId;
}
