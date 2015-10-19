package com.yikang.app.yikangserver;

import com.yikang.app.yikangserver.view.CustomWatingDialog;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 基础的Activity,其他activity继承它
 * 
 * @author LGhui
 * 
 */
public abstract class BaseActivity extends Activity {

	@TargetApi(19)
	protected void initContent() {
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			initStatusBar(getResources().getColor(R.color.statue_bar_color));
		}
		setContentView();
		findViews();
		getData();
		initViewConent();
	}
	
	
	@TargetApi(19)
	protected void initStatusBar(int color) {
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			SystemBarTintManager mTintManager = new SystemBarTintManager(this);
			mTintManager.setStatusBarTintEnabled(true);
			//mTintManager.setTintColor(getResources().getColor(R.color.statue_bar_color));
//			int color1 = getResources().getColor(R.color.line_blue);
//			int color2 = Color.parseColor("#2d86ff");
//			Log.i("baseActivity","a1"+Color.alpha(color1));
//			Log.i("baseActivity","a2"+Color.alpha(color2));
//			Log.i("baseActivity","red1"+Color.red(color1));
//			Log.i("baseActivity","red2"+Color.red(color2));
//			Log.i("baseActivity","blue1"+Color.blue(color1));
//			Log.i("baseActivity","blue2"+Color.blue(color2));
//			Log.i("baseActivity","blue3"+Color.green(color1));
//			Log.i("baseActivity","blue3"+Color.green(color2));
			mTintManager.setTintColor(color);
			
		}
	}

	/**
	 * 初始化Views
	 */
	protected abstract void findViews();

	/**
	 * 初始化contentView
	 */
	protected abstract void setContentView();

	/**
	 * 获取数据，
	 */
	protected abstract void getData();

	/**
	 * 展示内容
	 */
	protected abstract void initViewConent();

	/**
	 * 初始化titlebar，BaseActivity中此方法做了两件事 1.将左上角返回按钮设置点击后返回 2.设置标题为参数title
	 * 
	 * 如果子类有不同的布局，或者要做更多的事情，请重写这个方法
	 * 
	 * @param title
	 *            要设置的标题
	 */
	protected void initTitleBar(String title) {
		TextView tvTitle = (TextView) findViewById(R.id.tv_title_text);
		if (tvTitle != null) // 由于设计上的问题，并不是所有的Activity布局中都有统一的titleBar
			tvTitle.setText(title);

		ImageButton btBack = (ImageButton) findViewById(R.id.ibtn_title_back);
		if (btBack != null) // 设置返回监听
			btBack.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
	}

	private Dialog watingDaiglog;
	
	protected void showWatingDailog() {
		showWatingDailog("正在加载，请稍候...");
	}
	
	/**
	 * 显示等待的diaglog
	 */
	protected void showWatingDailog(String message) {
		if (watingDaiglog == null) {
			watingDaiglog = createDialog(message);
		}
		if (!watingDaiglog.isShowing())
			watingDaiglog.show();
	}

	/**
	 * dismiss 等待的diaglog
	 */
	protected void dismissWatingDailog() {
		if (watingDaiglog != null && watingDaiglog.isShowing()) {
			watingDaiglog.dismiss();
		}
	}

	/**
	 * 创建一个dialog，同过复写这个方法可以创建其他样式的dialog
	 */
	protected Dialog createDialog(String message) {
		return new CustomWatingDialog(this, message);
	}

	@Override
	protected void onStop() {
		super.onStop();
		dismissWatingDailog();
	}
}
