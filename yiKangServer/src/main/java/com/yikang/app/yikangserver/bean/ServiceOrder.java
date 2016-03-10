package com.yikang.app.yikangserver.bean;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

/**
 * 服务人员见到的订单。在“服务日程”中有用到
 */
public class ServiceOrder implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int STAUS_COMPLETE = 5;

	@SerializedName( "orderServiceDetailId")
	public String id;

	@SerializedName( "serviceDate")
	public String date;

	@SerializedName( "address")
	public String patientAddr;

	@SerializedName( "name")
	public String patientName;

	@SerializedName( "isRead")
	public boolean isRead;

	@SerializedName( "startTime")
	public String startTime;

	@SerializedName( "endTime")
	public String endTime;

	@SerializedName( "timeQuantumId")
	public int timeQuantumId;

	@SerializedName( "sex")
	public int patientSex;

	@SerializedName( "birthYear")
	public int patientBirthYear;

	public boolean onLine;

	@SerializedName( "myPhoneNumber")
	public String userPhone;

	@SerializedName( "phoneNumber")
	public String patientPhone;

	@SerializedName( "serviceDetailStatus")
	public int orderStatus;
	/** 1.已经分配服务人员 2.去服务的路上 3.到达服务地点进行服务 4.服务结束 5.服务记录填写结束 */

	@SerializedName( "feedback")
	public String feedBack;

	@SerializedName( "conclusion")
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
