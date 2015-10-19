package com.yikang.app.yikangserver.fragment;

import com.yikang.app.yikangserver.view.CustomWatingDialog;

import android.app.Dialog;
import android.app.Fragment;

public class BaseFragment extends Fragment {

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
		return new CustomWatingDialog(getActivity(), message);
	}

	@Override
	public void onStop() {
		super.onStop();
		dismissWatingDailog();
	}

}
