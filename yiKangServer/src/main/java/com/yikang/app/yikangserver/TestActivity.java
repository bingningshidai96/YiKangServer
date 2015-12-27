package com.yikang.app.yikangserver;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yikang.app.yikangserver.ui.BaseActivity;
import com.yikang.app.yikangserver.utils.AES;

public class TestActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initContent();
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.test);
    }

    @Override
    protected void getData() {

    }

    @Override
    protected void initViewConent() {

    }

    public void getkey(View v) {
        Toast.makeText(this, AES.getKey(), 0).show();
    }


}
