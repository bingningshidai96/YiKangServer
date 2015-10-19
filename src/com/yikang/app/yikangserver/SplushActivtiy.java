package com.yikang.app.yikangserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;

public class SplushActivtiy extends Activity{
	private Handler mhHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
				finish();
				Intent intent = new Intent(SplushActivtiy.this, LoginActivity.class);
				startActivity(intent);
			}
		}, 2000);
		
	}
}
