package com.yikang.app.yikangserver.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by liu on 16/2/1.
 */
public class Appointment {
   @JSONField(name ="appointmentUserId")
    public String appointmentId;  //订单编号

   @JSONField(name ="serviceTitle")
    public String serviceName; //服务名

   @JSONField(name ="serviceId")
    public String serviceId; // 该项服务的Id

   @JSONField(name ="createTime")
    public String createTime; //订单时间

   @JSONField(name ="remarks")
    public String comment; //备注

   @JSONField(name ="mobileNumber")
    public String userName; //用户名

   @JSONField(name ="servicePrice")
    public String servicePrice; //支付价格

    public String userId;
}
