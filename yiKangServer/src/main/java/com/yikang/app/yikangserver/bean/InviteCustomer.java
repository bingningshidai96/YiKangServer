package com.yikang.app.yikangserver.bean;

import com.google.gson.annotations.SerializedName;

public class InviteCustomer {
	public static final int STATUS_REGISTER = 0;
	public static final int STATUS_CONSUMED = 1;
	@SerializedName("name")
	public String name;

	@SerializedName("userId")
	public String userId;

	@SerializedName("userStatus")
	public int status;

	public String consumeDate;

	@SerializedName("photoUrl")
	public String imgUrl;

	@SerializedName("sex")
	public int sex;


	@SerializedName("loginName")
	public String phone;


	@SerializedName("createTimeStr")
	public String registerTime;


	public String serviceTime;
}
