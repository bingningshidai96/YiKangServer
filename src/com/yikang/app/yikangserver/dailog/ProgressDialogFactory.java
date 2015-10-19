package com.yikang.app.yikangserver.dailog;

import com.yikang.app.yikangserver.view.CustomWatingDialog;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogFactory {
	public static final int TYPE_LOADING_DATA = 1;
	public static final int TYPE_SUBMIT_DATA = 2;
	
	public static final ProgressDialog getProgressDailog(int type,Context context){
		ProgressDialog dialog = null;
		switch (type) {
		case TYPE_LOADING_DATA:
			dialog = new CustomWatingDialog(context, "正在加载数据，请稍等");
			return dialog;
		case TYPE_SUBMIT_DATA:
			dialog = new CustomWatingDialog(context, "正在提交数据，请稍等");
			return dialog;
		default:
			throw new IllegalArgumentException("please give the right type which is one of the given static-final-integer in this class");
		}
	}
	
}
