package com.yikang.app.yikangserver.dailog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.view.CustomWatingDialog;

public class DialogFactory {
	public static final int TYPE_LOADING_DATA = 1;
	public static final int TYPE_SUBMIT_DATA = 2;

	public static final ProgressDialog getProgressDailog(int type,
			Context context) {
		ProgressDialog dialog = null;
		switch (type) {
		case TYPE_LOADING_DATA:
			dialog = new CustomWatingDialog(context, "正在加载数据，请稍等");
			return dialog;
		case TYPE_SUBMIT_DATA:
			dialog = new CustomWatingDialog(context, "正在提交数据，请稍等");
			return dialog;
		default:
			throw new IllegalArgumentException(
					"please give the right type which is one of the given static-final-integer in this class");
		}
	}
	
	
	/**
	 * 获得一个普通的提示对话框
	 */
	public static final Dialog getCommerAlertDiaglog(Context context,String title,String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		return builder.setTitle(title)
		.setMessage(message)
		.setPositiveButton(R.string.confirm, null).create();
	}


	public static final Dialog getCommerAlertDiaglog(Context context,String message){
		return getCommerAlertDiaglog(context,context.getString(R.string.dialog_title_prompt),message);

	}


	

}
