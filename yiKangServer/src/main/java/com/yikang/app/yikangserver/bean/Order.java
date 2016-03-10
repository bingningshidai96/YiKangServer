package com.yikang.app.yikangserver.bean;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 服务人员见到的订单。在“服务日程”中有用到
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

   @SerializedName("orderNumber")
    public String orderId; //订单号

   @SerializedName("myPhoneNumber")
    public String userName; //用户名

   @SerializedName("serviceTitle")
    public String serviceName; //服务名


   @SerializedName("createTimeStr")
    public String orderTime; //订单时间

   @SerializedName("servicePrice")
    public int paidCount; //支付金额

   @SerializedName("orderStatus")
    public int orderStatus;//订单状态


   @SerializedName("startTime")
    public String startTime; //预约时间：小时

   @SerializedName("appointmentDate")
    public String appointmentDate;//预约时间：日期

   @SerializedName("remarks")
    public String comment; //备注


   @SerializedName("createUserId")
    public String createUserId; //下单用户

   @SerializedName("seniorId")
    public String seniorId;

   @SerializedName("mapPostionAddress")
    public String mapPosition;

   @SerializedName("detailAddress")
    public String addressDetail;

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userName='" + userName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", paidCount=" + paidCount +
                ", orderStatus=" + orderStatus +
                ", startTime='" + startTime + '\'' +
                ", appointmentDate='" + appointmentDate + '\'' +
                ", comment='" + comment + '\'' +
                ", createUserId='" + createUserId + '\'' +
                ", seniorId='" + seniorId + '\'' +
                ", mapPosition='" + mapPosition + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                '}';
    }
}
