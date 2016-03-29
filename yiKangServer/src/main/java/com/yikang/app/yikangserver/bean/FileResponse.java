package com.yikang.app.yikangserver.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liu on 16/3/24.
 */
public class FileResponse {
    @SerializedName("fileUrl")
    public String fileUrl;
    @SerializedName("fileName")
    public String fileName;

    @SerializedName("oldFileName")
    public String oldFileName;

    @Override
    public String toString() {
        return "FileResponse{" +
                "fileUrl='" + fileUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                ", oldFileName='" + oldFileName + '\'' +
                '}';
    }


}
