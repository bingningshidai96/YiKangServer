package com.yikang.app.yikangserver.api;

import com.yikang.app.yikangserver.application.AppContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by liu on 15/12/28.
 * 文件服务的请求参数
 */
public class FileRequestParam {
    static final String KEY_FILEGROUP = "fileGroup";
    static final String KEY_FILES = "files";
    static final String KEY_APPID = "appId";
    static final String KEY_ACCESSTICKET = "accessTicket";
    static final String KEY_MACHINECODE = "machineCode";


    private ArrayList<File> files;
    private String fileGroup;

    String appId;
    String accessTicket;
    String mochineCode;


    {
        AppContext appContext = AppContext.getAppContext();
        appId = appContext.getAppId();
        accessTicket = appContext.getAccessTicket();
        mochineCode = appContext.getDeviceID();
    }



    public FileRequestParam(String fileGroup) {
        this(fileGroup,null);
    }

    public FileRequestParam(String fileGroup,ArrayList<File> files) {
        this.fileGroup = fileGroup;
        this.files = new ArrayList<>();
        if(files!=null){
            Collections.copy(this.files,files);
        }
    }


    public void addFile(File file){
        files.add(file);
    }

    public void remove(File file){
        files.remove(file);
    }

    ArrayList<File> getFiles(){
        return files;
    }

    public ArrayList<File> getFilesCopy(){
        ArrayList<File> newList = new ArrayList<>();
        Collections.copy(newList,files);
        return newList;
    }

    public String getFileGroup() {
        return fileGroup;
    }
}
