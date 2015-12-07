package com.yikang.app.yikangserver.ui;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;

public class SplashActivtiy extends Activity {
	private Handler mhHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		ImageView img = new ImageView(this);
		img.setImageResource(R.drawable.start_background);
		img.setScaleType(ImageView.ScaleType.CENTER_CROP);
		setContentView(img);

		mhHandler = new Handler();
		mhHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				startNextPage();
				finish();
			}
		}, 3000);

	}

	private void startNextPage() {
		if (AppContext.getAppContext().getAccessTicket() == null) {
			Intent intent = new Intent(SplashActivtiy.this, LoginActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(SplashActivtiy.this, MainActivity.class);
			startActivity(intent);
		}
	}
}
