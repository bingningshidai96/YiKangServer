package com.yikang.app.yikangserver.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int INFO_STATUS_COMPLETE =1;
	public static final int INFO_STATUS_INCOMPLETE =0;


	public static final int CHECK_STATUS_UNCHECKED =0;
	public static final int  CHECK_STATUS_CHECKING =1;
	public static final int CHECK_STATUS_PASSED =2;
	public static final int  CHECK_STATUS_REJECT =3;

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

	@SerializedName( "officeObj")
	public Department department;


	@SerializedName( "invitationCode")
	public String inviteCode;

	@SerializedName( "photoUrl")
	public String avatarImg;

	@SerializedName( "mapPositionAddress")
	public String mapPositionAddress;

	@SerializedName( "addressDetail")
	public String addressDetail;

	@SerializedName( "districtCode")
	public String districtCode;

	public String consumerTime;
	
	@SerializedName("invitationUrl")
	public String invitationUrl;

	@SerializedName("infoWrite")
	public int infoStatus = INFO_STATUS_INCOMPLETE;

	@SerializedName("positionAuditStatus")
	public int  professionCheckStatus= CHECK_STATUS_UNCHECKED;

	@SerializedName("adepts")
    public ArrayList<Expert> special;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", profession=" + profession +
                ", jobType=" + jobType +
                ", paintsNums=" + paintsNums +
                ", hospital='" + hospital + '\'' +
                ", department='" + department + '\'' +
                ", inviteCode='" + inviteCode + '\'' +
                ", avatarImg='" + avatarImg + '\'' +
                ", mapPositionAddress='" + mapPositionAddress + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", districtCode='" + districtCode + '\'' +
                ", consumerTime='" + consumerTime + '\'' +
                ", invitationUrl='" + invitationUrl + '\'' +
                ", infoStatus=" + infoStatus +
                ", professionCheckStatus=" + professionCheckStatus +
                ", special=" + special +
                '}';
    }
}
