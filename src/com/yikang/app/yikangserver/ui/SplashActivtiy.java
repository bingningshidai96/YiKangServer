package com.yikang.app.yikangserver.ui;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppConfig;
import com.yikang.app.yikangserver.application.AppContext;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashActivtiy extends BaseActivity {
	private Handler mhHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
		mhHandler = new Handler();
		mhHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				startNextPage();
				finish();
			}
		}, 3000);

	}
	

	@TargetApi(19)
	protected void initContent() {
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			initStatusBar(getResources().getColor(R.color.transparent));
		}
		setContentView();
		findViews();
		getData();
		initViewConent();
	}
	
	private void startNextPage() {
		if(AppContext.get(AppConfig.PRE_APP_FIRST_RUN, true)){
			Intent intent = new Intent(this, GuideActivity.class);
			startActivity(intent);
			finish();
		}else if (AppContext.getAppContext().getAccessTicket() == null) {
			Intent intent = new Intent(SplashActivtiy.this, LoginActivity.class);
			startActivity(intent);
		}else {
			Intent intent = new Intent(SplashActivtiy.this, MainActivity.class);
			startActivity(intent);
		}
	}


	@Override
	protected void findViews() {
		
	}


	@Override
	protected void setContentView() {
		ImageView img = new ImageView(this);
		img.setImageResource(R.drawable.start_background);
		img.setScaleType(ImageView.ScaleType.CENTER_CROP);
		setContentView(img);
	}

	@Override
	protected void getData() {}
	@Override
	protected void initViewConent() {}
}
