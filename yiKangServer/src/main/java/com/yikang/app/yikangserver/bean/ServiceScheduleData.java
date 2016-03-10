package com.yikang.app.yikangserver.bean;

import com.google.gson.annotations.SerializedName;

public class ServiceScheduleData {
	public static final int status_editable = 0;
	public static final int status_selected = 1;
	public static final int status_invalid = 2;

	@SerializedName("serviceDate")
	public long serviceDate;
	@SerializedName("serviceScheduleId")
	public int serviceScheduleId;
	@SerializedName("isCanSelected")
	public int scheduleStatus;
	@SerializedName("serviceMonth")
	public int serviceMonth;
	@SerializedName("servcieDay")
	public int serviceDay;
	@SerializedName("serviceDateStr")
	public String serviceDayStr;

}
