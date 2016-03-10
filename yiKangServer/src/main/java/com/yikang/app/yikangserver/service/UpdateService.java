package com.yikang.app.yikangserver.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.api.callback.DownloadCallback;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.utils.UpdateManger;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 更新服务
 */
public class UpdateService extends Service {
    private static final String TAG = "UpdateService";
    public static final String KEY_UPDATE_INFO = "key_update";

    private int progress; //进度
    private Notification notification; //构建的通知
    private String savePath;
    private NotificationManager manager;
    private UpdateManger.AndroidUpdate mUpdate;

    private DownloadCallback downloadHandler = new DownloadCallback() {
        @Override
        public void onProgress(long total, long current) {
            int currProgress = (int) (current * 100 / total);
            if (currProgress > progress) {
                progress = currProgress;
                sendProgressNotification(progress);
            }
        }

        @Override
        public void onSuccess(Void data) {
            //安装
            stopSelf();
            if(!TextUtils.isEmpty(savePath)){
                installApk(savePath);
                LOG.i(TAG,"开始安装。。。。。");
            }
        }

        @Override
        public void onFailure(String status, String message) {
            stopSelf();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        manager =((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        buildNotification();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mUpdate = (UpdateManger.AndroidUpdate) intent.getSerializableExtra(KEY_UPDATE_INFO);
        downLoadNewApk();
        return START_REDELIVER_INTENT;
    }


    /**
     * 初始化一个Notification通知
     */
    private void buildNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("正在下载")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("正在下载护理家V1.0.4")
                .setProgress(100, progress, false);
        notification = builder.build();
    }



    /**
     *下载新的apk
     */
    protected void downLoadNewApk() {
        savePath = AppContext.CACHE_IMAGE_PATH+ File.separator+"hulijia_"+mUpdate.versionName+".apk";
        Api.downloadNewApk(mUpdate.downloadUrl, savePath, downloadHandler);

    }

    /**
     * android 安装apk文件
     * @param filePath apk文件的路径
     */
    private void installApk(String filePath){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + filePath),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }


    /**
     * 发送进度通知
     */
    private void sendProgressNotification(int progress) {
        notification.contentView.setProgressBar(android.R.id.progress, 100, progress, false);
        manager.notify(100, notification);
        if(progress==100){
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    manager.cancel(100);
                }
            },500);
        }

    }

}
