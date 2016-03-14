package com.yikang.app.yikangserver.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.api.parser.GsonFactory;
import com.yikang.app.yikangserver.utils.LOG;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 上传服务
 * 
 */
public class UpLoadService extends IntentService {
	private static final String TAG = "UpLoadService";
	public static final String EXTRA_IS_SUCCESS = "isSuccess";
	public static final String EXTRA_MESSAGE = "message";
	public static final String EXTRA_DATA = "data";

	public static final String ACTION_UPLOAD_COMPLETE = "com.yikang.action.upLoadComplete";

	private ResponseCallback<String> uploadFileHandler = new ResponseCallback<String>() {
		@Override
		public void onSuccess(String data) {
			// 解析数据
			Gson gson = GsonFactory.newInstance();
			Type type = new TypeToken<Map<String, Object>>() {}.getType();
			Map<String, Object> map= gson.fromJson(data, type);
			String fileUrl = (String) map.get("fileUrl");
			upLoadSuccess(fileUrl);
		}

		@Override
		public void onFailure(String status, String message) {
			upLoadFail(message);
		}
	};

	public UpLoadService() {
		super("UpLoadService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String path = intent.getStringExtra("filePath");
		if (TextUtils.isEmpty(path)) {
			upLoadFail("传入参数不正确");
			return;
		}
		File file = new File(path);
		if (!file.exists() || !file.isFile()) {
			upLoadFail("传入参数不正确");
			return;
		}
		Api.uploadFile(file, uploadFileHandler);
	}




	private void upLoadFail(String message) {
		LOG.i(TAG, "[upLoadFail]上传失败" + message);
		Intent intent = new Intent(ACTION_UPLOAD_COMPLETE);
		intent.putExtra(EXTRA_IS_SUCCESS, false);
		intent.putExtra(EXTRA_MESSAGE, message);
		sendBroadcast(intent);
	}

	private void upLoadSuccess(String url) {
		LOG.i(TAG, "上传成功" + url);
		Intent intent = new Intent(ACTION_UPLOAD_COMPLETE);
		intent.putExtra(EXTRA_IS_SUCCESS, true);
		intent.putExtra(EXTRA_DATA, url);
		sendBroadcast(intent);
	}

}
