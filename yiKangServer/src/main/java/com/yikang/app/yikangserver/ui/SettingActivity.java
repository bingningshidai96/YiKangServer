package com.yikang.app.yikangserver.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;


public class SettingActivity extends BaseActivity implements View.OnClickListener {
    public static final int RESULT_LOGOUT = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContent();
        initTitleBar(getString(R.string.setting_title));
    }
    @Override
    protected void findViews() {
        findViewById(R.id.ly_setting_logout).setOnClickListener(this);
        findViewById(R.id.ly_setting_mark).setOnClickListener(this);
        findViewById(R.id.ly_setting_version).setOnClickListener(this);
        TextView tvVersion = (TextView) findViewById(R.id.tv_setting_version);
        tvVersion.setText(AppContext.getAppContext().getVersionName());

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void getData() {

    }

    @Override
    protected void initViewContent() {

    }



    /**
     * 登出
     */
    private void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_prompt)
                .setMessage(R.string.logout_dialog_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                logout();

                            }
                        }).create().show();
    }



    /**
     * 注销用户
     */
    private void logout(){
       setResult(RESULT_LOGOUT);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ly_setting_version:
                break;
            case R.id.ly_setting_mark:
                markApp();
                break;
            case R.id.ly_setting_logout:
                logoutDialog();
                break;
        }
    }

    /**
     * 该app打分
     */
    private void markApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
