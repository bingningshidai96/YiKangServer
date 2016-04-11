package com.yikang.app.yikangserver.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liu on 16/3/22.
 */
public class PaintsData {
    @SerializedName("nums")
    public int sum;
    @SerializedName("regiestUserNums")
    public int registerNum;
    @SerializedName("serviceingUserNums")
    public int inServiceNum;

    @SerializedName("servicedUserNums")
    public int completeNum;

    @SerializedName("invitionUsers")
    public List<InviteCustomer> list;
}
