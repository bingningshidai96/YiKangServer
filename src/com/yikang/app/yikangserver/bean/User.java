package com.yikang.app.yikangserver.bean;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int STATUS_REGISTER = 0;
	public static final int STATUS_CONSUMED = 1;

	@JSONField(name = "userName")
	public String name;
	@JSONField(name = "userId")
	public String userId;
	@JSONField(name = "userPosition")
	public int profession = -1; // 职业

	@JSONField(name = "jobCategory")
	public int jobType = -1; // 职业类型 全职和兼职

	@JSONField(name = "nums")
	public int paintsNums = 0;// 病患个数

	@JSONField(name = "hospital")
	public String hosital;

	//TODO @JSONField(name = "offices")
	public int deparment;

	@JSONField(name = "adept")
	public String special;

	@JSONField(name = "invitationCode")
	public String inviteCode;

	@JSONField(name = "photoUrl")
	public String avatarImg;

	// TODO
	@JSONField(name = "photoUrl")
	public int status;

	@JSONField(name = "mapPositionAddress")
	public String mapPositionAddress;

	@JSONField(name = "addressDetail")
	public String addressDetail;

	@JSONField(name = "districtCode")
	public String districtCode;

	@JSONField(name = "photoUrl")
	public String consumerTime;

	@Override
	public String toString() {
		return "User [name=" + name + ", userId=" + userId + ", profession="
				+ profession + ", jobType=" + jobType + ", paintsNums="
				+ paintsNums + ", hosital=" + hosital + ", deparment="
				+ deparment + ", special=" + special + ", inviteCode="
				+ inviteCode + ", avatarImg=" + avatarImg + ", status="
				+ status + ", mapPositionAddress=" + mapPositionAddress
				+ ", addressDetail=" + addressDetail + ", districtCode="
				+ districtCode + ", consumerTime=" + consumerTime + "]";
	}

}
