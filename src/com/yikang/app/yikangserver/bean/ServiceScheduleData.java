package com.yikang.app.yikangserver.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class ServiceScheduleData {
	public static final int status_editable = 0;
	public static final int status_selected = 1;
	public static final int status_invalid = 2;
	
	@JSONField(name="serviceDate")
	public long serviceDate;
	@JSONField(name="serviceScheduleId")
	public int serviceScheduleId;
	@JSONField(name="isCanSelected")
	public int scheduleStatus; 
	@JSONField(name="serviceMonth")
	public int serviceMonth;
	@JSONField(name="servcieDay")
	public int serviceDay;
	@JSONField(name="serviceDateStr")
	public String serviceDayStr;

}
