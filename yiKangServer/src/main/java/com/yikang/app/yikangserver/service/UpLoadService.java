package com.yikang.app.yikangserver.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.api.FileRequestParam;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.api.ResponseContent;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.api.ApiClient;
import com.yikang.app.yikangserver.utils.LOG;

/**
 * 上传服务
 * 
 */
public class UpLoadService extends IntentService {
	private static final String TAG = "UpLoadService";
	public static final String EXTRA_IS_SUCESS = "isSucess";
	public static final String EXTRA_MESSAGE = "message";
	public static final String EXTRA_DATA = "data";

	public static final String ACTION_UPLOAD_COMPLETE = "com.yikang.action.upLoadComplete";

	public UpLoadService() {
		super("UpLoadService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String path = intent.getStringExtra("filePath");
		LOG.i(TAG, "[onHandleIntent]path：" + path);
		if (TextUtils.isEmpty(path)) {
			upLoadFail("传入参数不正确");
			return;
		}
		File file = new File(path);
		if (!file.exists() || !file.isFile()) {
			upLoadFail("传入参数不正确");
			return;
		}
		upLoadSingleFile(file, UrlConstants.URL_UPLOAD_IMAGE);
	}

	/**
	 * 上传单个文件
	 */
	private void upLoadSingleFile(File file, String url) {
		LOG.i(TAG, "[upLoadSingleFile]上传文件开始...");
		FileRequestParam param = new FileRequestParam("headImage");
		param.addFile(file);

		ApiClient.upLoadFiles(url, param, new ApiClient.ResponceCallBack() {
			@Override
			public void onSuccess(ResponseContent content) {
				String data = content.getData();
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) JSON
						.parse(data);
				LOG.i(TAG, "[upLoadSingleFile-->onSuccess]" + map.toString());
				String fileUrl = (String) map.get("fileUrl");
				upLoadSuccess(fileUrl);
			}

			@Override
			public void onFialure(String status, String message) {
				LOG.i(TAG, "[upLoadSingleFile-->onFialure]" + message);
				upLoadFail(message);
			}
		});

//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("fileGroup", "headImage");
//		map.put("files", file);
//		//map.put("appId", "appId");
//		//map.put("accessTicket", "aaa");
//		map.put("machineCode", AppContext.getAppContext().getDeviceID());
//		LOG.i(TAG, "[upLoadSingleFile]" + map.toString());
//		LOG.i(TAG, "[upLoadSingleFile]" + file.isFile() + file.exists());
//		ApiClient.UploadSingleFile(url, map, new ApiClient.ResponceCallBack() {
//			@Override
//			public void onSuccess(ResponseContent content) {
//				String data = content.getData();
//				@SuppressWarnings("unchecked")
//				Map<String, Object> map = (Map<String, Object>) JSON
//						.parse(data);
//				LOG.i(TAG, "[upLoadSingleFile-->onSuccess]" + map.toString());
//				String fileUrl = (String) map.get("fileUrl");
//				upLoadSuccess(fileUrl);
//			}
//
//			@Override
//			public void onFialure(String status, String message) {
//				LOG.i(TAG, "[upLoadSingleFile-->onFialure]" + message);
//				upLoadFail(message);
//			}
//		});
	}

	private void upLoadFail(String message) {
		LOG.i(TAG, "[upLoadFail]上传失败" + message);
		Intent intent = new Intent("com.yikang.action.upLoadComplete");
		intent.putExtra(EXTRA_IS_SUCESS, false);
		intent.putExtra(EXTRA_MESSAGE, message);
		sendBroadcast(intent);
	}

	private void upLoadSuccess(String url) {
		LOG.i(TAG, "上传成功" + url);
		Intent intent = new Intent("com.yikang.action.upLoadComplete");
		intent.putExtra(EXTRA_IS_SUCESS, true);
		intent.putExtra(EXTRA_DATA, url);
		sendBroadcast(intent);
	}

	// /**
	// * 上传多个文件
	// */
	// private void upLoadFiles(Files files,String url){
	// String[] paths = intent.getStringArrayExtra("paths");
	// if(paths!=null){
	// ArrayList<File> fileList = new ArrayList<File>();
	// for (String path : paths) {
	// fileList.add(new File(path));
	// }
	// String url;
	// HttpUtils.uploadFile(url, param, callBack)
	// }
	// }

}
