package com.yikang.app.yikangserver.utils;

import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtlis {



    /**
     * 保存stream到指定路径
     */
    public static File save(InputStream inputStream,String filepath){
        //检查父级目录是否存在
        File parent = new File(getParentPath(filepath));
        if(!parent.exists())  parent.mkdirs();

        //开始写文件
        File saveFile = new File(parent,getFileName(filepath));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(saveFile);
            byte[] buffer = new byte[512];
            int count = 0;
            while((count=inputStream.read(buffer))!=-1){
                fos.write(buffer,0,count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            try{
                if(inputStream!=null){
                    inputStream.close();
                }
                if(fos!=null){
                    fos.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }
        return saveFile;
    }

    /**
     * SD卡是否挂载
     */
    public static boolean isSDMounted(){
        String state = Environment.getExternalStorageState();
        return state == Environment.MEDIA_MOUNTED;
    }


    /**
     * 获取文件名
     */
    public static String getFileName(String path){
        int lastIndexOf = path.lastIndexOf(File.separator + "");
        if(lastIndexOf ==-1){
            return path;
        }
        if(path.length()<=1)
            return null;
        return path.substring(lastIndexOf + 1, path.length());
    }

    /**
     * 获取父级目录
     */
    public static String getParentPath(String path){
        int lastIndexOf = path.lastIndexOf(File.separator + "");

        if(lastIndexOf==-1){
            return null;
        }
        return path.substring(0,lastIndexOf);
    }




}
