package com.yikang.app.yikangserver.bean;


import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;

/**
 * 服务人员见到的订单。在“服务日程”中有用到
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

   @JSONField(name ="orderNumber")
    public String orderId; //订单号

   @JSONField(name ="myPhoneNumber")
    public String userName; //用户名

   @JSONField(name ="serviceTitle")
    public String serviceName; //服务名


   @JSONField(name ="createTimeStr")
    public String orderTime; //订单时间

   @JSONField(name ="servicePrice")
    public int paidCount; //支付金额

   @JSONField(name ="orderStatus")
    public int orderStatus;//订单状态


   @JSONField(name ="startTime")
    public String startTime; //预约时间：小时

   @JSONField(name ="appointmentDate")
    public String appointmentDate;//预约时间：日期

   @JSONField(name ="remarks")
    public String comment; //备注


   @JSONField(name ="createUserId")
    public String createUserId; //下单用户

   @JSONField(name ="seniorId")
    public String seniorId;

   @JSONField(name ="mapPostionAddress")
    public String mapPosition;

   @JSONField(name ="detailAddress")
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
