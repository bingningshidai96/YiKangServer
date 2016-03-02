package com.yikang.app.yikangserver.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class InviteCustomer {
	public static final int STATUS_REGISTER = 0;
	public static final int STATUS_CONSUMED = 1;
	@JSONField(name = "name")
	public String name;

	@JSONField(name = "userId")
	public String userId;

	@JSONField(name = "userStatus")
	public int status;

	@JSONField(name = "createTimeStr")
	public String consumeDate;

	@JSONField(name = "photoUrl")
	public String imgUrl;

	@JSONField(name = "sex")
	public int sex;

}
