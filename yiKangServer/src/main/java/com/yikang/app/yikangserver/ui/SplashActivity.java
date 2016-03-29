package com.yikang.app.yikangserver.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tencent.bugly.crashreport.CrashReport;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppConfig;
import com.yikang.app.yikangserver.application.AppContext;

import java.io.File;

public class SplashActivity extends BaseActivity {
	private Handler mhHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		correctPreference();
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
		initViewContent();
	}


	/**
	 *如果版本更新，修正数据
	 */
	private void correctPreference(){
		int currentVersion = AppContext.getAppContext().getVersionCode();
		int cacheVersionCode = AppContext.get(AppConfig.PRE_VERSION_CODE, 0);
		if(currentVersion>cacheVersionCode){
			AppContext.set(AppConfig.PRE_VERSION_CODE,currentVersion);
			AppContext.set(AppConfig.PRE_APP_FIRST_RUN,true);

			/**因为升级之后，和之前的数据已经不一致*/
			if(cacheVersionCode<=6){
				AppContext.getAppContext().logout();
				File configDir = getDir("config", Context.MODE_PRIVATE);
				File configFile = new File(configDir, "config");
				if (!configFile.exists()) {
					configFile.delete();
				}
			}
		}
	}

	private void startNextPage() {
		if (AppContext.getAppContext().getAccessTicket() == null) {
			Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
			startActivity(intent);
		}else {
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
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
	protected void initViewContent() {}
}
