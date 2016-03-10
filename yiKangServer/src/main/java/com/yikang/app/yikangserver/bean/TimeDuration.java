package com.yikang.app.yikangserver.bean;

import com.google.gson.annotations.SerializedName;

public class TimeDuration {
	@SerializedName("timeQuantumId")
	public int serviceId;
	@SerializedName("startTime")
	public int startTime;
	@SerializedName("endTime")
	public int endTime;
	//@SerializedName("isChecked")
	public boolean isChecked;

	@Override
	public String toString() {
		return "TimeDuration{" +
				"serviceId=" + serviceId +
				", startTime=" + startTime +
				", endTime=" + endTime +
				", isChecked=" + isChecked +
				'}';
	}
}
