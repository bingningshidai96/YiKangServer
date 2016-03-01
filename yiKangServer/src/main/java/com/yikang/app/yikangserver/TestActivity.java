package com.yikang.app.yikangserver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yikang.app.yikangserver.api.ApiClient;
import com.yikang.app.yikangserver.api.ResponseContent;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.service.UpdateService;
import com.yikang.app.yikangserver.ui.BaseActivity;
import com.yikang.app.yikangserver.utils.AES;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.utils.UpdateManger;

import java.io.File;

public class TestActivity extends BaseActivity {
    private static final String TAG = "TestActivity";
    private int progress;
    private Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initContent();
    }

    @Override
    protected void findViews() {}

    @Override
    protected void setContentView() {
        setContentView(R.layout.test);
    }

    @Override
    protected void getData() {}

    protected void initViewContent() {}

    public void onStartService(View v) {
        Intent intent =new Intent(this, UpdateService.class);
        startService(intent);
    }

    public void onStopService(View v){
        stopService(new Intent(this, UpdateService.class));
    }


    public void checkUpdate(View v){
        UpdateManger manger = new UpdateManger(this);
        manger.checkUpate();
    }


}
