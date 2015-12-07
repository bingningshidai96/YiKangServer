package com.yikang.app.yikangserver;

import com.yikang.app.yikangserver.ui.BaseActivity;

import android.app.Activity;
import android.os.Bundle;

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
		setContentView(R.layout.activity_service_detail);
	}

	@Override
	protected void getData() {
		
	}

	@Override
	protected void initViewConent() {
		showWatingDailog();
		
	}
	
	
	
}
