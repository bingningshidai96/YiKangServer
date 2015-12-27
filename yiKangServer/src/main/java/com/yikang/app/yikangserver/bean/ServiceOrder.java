package com.yikang.app.yikangserver.bean;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 服务人员见到的订单。在“服务日程”中有用到
 */
public class ServiceOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int STAUS_COMPLETE = 5;

	@JSONField(name = "orderServiceDetailId")
	public String id;

	@JSONField(name = "serviceDate")
	public String date;

	@JSONField(name = "address")
	public String patientAddr;

	@JSONField(name = "name")
	public String patientName;

	@JSONField(name = "isRead")
	public boolean isRead;

	@JSONField(name = "startTime")
	public String startTime;

	@JSONField(name = "endTime")
	public String endTime;

	@JSONField(name = "timeQuantumId")
	public int timeQuantumId;

	@JSONField(name = "sex")
	public int patientSex;

	@JSONField(name = "birthYear")
	public int patientBirthYear;

	public boolean onLine;

	@JSONField(name = "myPhoneNumber")
	public String userPhone;

	@JSONField(name = "phoneNumber")
	public String patientPhone;

	@JSONField(name = "serviceDetailStatus")
	public int orderStatus;
	/** 1.已经分配服务人员 2.去服务的路上 3.到达服务地点进行服务 4.服务结束 5.服务记录填写结束 */

	@JSONField(name = "feedback")
	public String feedBack;

	@JSONField(name = "conclusion")
	public String patientEvaluationResult;

	@Override
	public String toString() {
		return "ServiceOrder [id=" + id + ", date=" + date + ", patientAddr="
				+ patientAddr + ", patientName=" + patientName + ", isRead="
				+ isRead + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", timeQuantumId=" + timeQuantumId + ", patientSex="
				+ patientSex + ", patientBirthYear=" + patientBirthYear
				+ ", patientEvaluationResult=" + patientEvaluationResult
				+ ", onLine=" + onLine + ", userPhone=" + userPhone
				+ ", patientPhone=" + patientPhone + ", orderStatus="
				+ orderStatus + "]";
	}

}
