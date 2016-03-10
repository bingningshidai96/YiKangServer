package com.yikang.app.yikangserver.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int STATUS_REGISTER = 0;
	public static final int STATUS_CONSUMED = 1;
	@SerializedName( "userName")
	public String name;
	@SerializedName( "userId")
	public String userId;
	@SerializedName( "userPosition")
	public int profession = -1; // 职业

	@SerializedName( "jobCategory")
	public int jobType = -1; // 职业类型 全职和兼职

	@SerializedName( "nums")
	public int paintsNums = 0;// 病患个数

	@SerializedName( "hospital")
	public String hospital;

	@SerializedName( "offices")
	public String department;

	@SerializedName( "adept")
	public String special;

	@SerializedName( "invitationCode")
	public String inviteCode;

	@SerializedName( "photoUrl")
	public String avatarImg;

	public int status;

	@SerializedName( "mapPositionAddress")
	public String mapPositionAddress;

	@SerializedName( "addressDetail")
	public String addressDetail;

	@SerializedName( "districtCode")
	public String districtCode;

	public String consumerTime;
	
	@SerializedName("invitationUrl")
	public String invitationUrl;

	@Override
	public String toString() {
		return "User [name=" + name + ", userId=" + userId + ", profession="
				+ profession + ", jobType=" + jobType + ", paintsNums="
				+ paintsNums + ", hospital=" + hospital + ", department="
				+ department + ", special=" + special + ", inviteCode="
				+ inviteCode + ", avatarImg=" + avatarImg + ", status="
				+ status + ", mapPositionAddress=" + mapPositionAddress
				+ ", addressDetail=" + addressDetail + ", districtCode="
				+ districtCode + ", consumerTime=" + consumerTime
				+ ", invitationUrl=" + invitationUrl + "]";
	}

	
}
