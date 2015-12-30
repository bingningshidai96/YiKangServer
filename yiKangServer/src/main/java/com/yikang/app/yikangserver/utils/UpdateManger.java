package com.yikang.app.yikangserver.utils;

import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.api.ApiClient;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.api.RequestParam;
import com.yikang.app.yikangserver.api.ResponseContent;
import com.yikang.app.yikangserver.api.ApiClient.ResponceCallBack;
import com.yikang.app.yikangserver.view.CustomWatingDialog;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

/**
 *更新管理
 */
public class UpdateManger {
	
	private AndroidUpdate mUpdate;
	
	private boolean isShow;//是否显示更新界面
	
	private Context mContext;

	private Dialog watingDaiglog;
	
	
	
	
	
	
	
	public UpdateManger(Context context) {
		this(context,true);
	}


	public UpdateManger(Context context,boolean isShow) {
		this.isShow = isShow;
		this.mContext = context;
	}
	
	
	private ResponceCallBack mCallBack = new ResponceCallBack() {
		@Override
		public void onSuccess(ResponseContent content) {
			if(isShow) hideWatingDailog();
			
			mUpdate = JSON.parseObject(content.getData(),
					AndroidUpdate.class);
			
			if(hasNew()){
				showUpdateDialog();
			}else{
				showLeastVersionDialog();
			}
			
		}
		
		@Override
		public void onFialure(String status, String message) {
			if(isShow) hideWatingDailog();
			
			AppContext.showToast(message);
		}
	};

	public void checkUpate(){
		if(!isShow){
			showCheckDialog("正在获取版本信息，请稍候...");
		}
		final String url = "";
		RequestParam param = new RequestParam();
		
		ApiClient.postAsyn(url, param, mCallBack);
	}
	
	
	
	private boolean hasNew(){
		if(mUpdate == null)
			return false;
		int curVersion = AppContext.getAppContext().getVersionCode();
		return mUpdate.versionCode > curVersion;
	}
	

	private void showCheckDialog(String message) {
		if (watingDaiglog == null) {
			watingDaiglog = new CustomWatingDialog((Activity) mContext, message);
		}
		if (!watingDaiglog.isShowing())
			watingDaiglog.show();
	}
	

	private void hideWatingDailog() {
		if (watingDaiglog != null && watingDaiglog.isShowing()) {
			watingDaiglog.dismiss();
		}
	}
	
	
	
	
	private void showUpdateDialog(){
		
	}
	
	private void showLeastVersionDialog(){
		
	}
	
	
	
	public static class AndroidUpdate{
		public int versionCode; 
		public String versionName;
		public String updateLog;
		public String downloadUrl;
	}
}
