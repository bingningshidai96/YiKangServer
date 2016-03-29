package com.yikang.app.yikangserver.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liu on 16/3/23.
 */
public class Department implements Serializable{
    @SerializedName("officeId")
    public String departmentId;
    @SerializedName("officeName")
    public String departmentName;

}
