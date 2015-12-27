package com.yikang.app.yikangserver.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class TimeDuration {
	@JSONField(name = "timeQuantumId")
	public int serviceId;
	@JSONField(name = "startTime")
	public int startTime;
	@JSONField(name = "endTime")
	public int endTime;
	@JSONField(name = "isChecked")
	public boolean isChecked;
}
