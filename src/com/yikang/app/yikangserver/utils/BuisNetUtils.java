package com.yikang.app.yikangserver.utils;

import java.util.Map;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;

/**
 * 业务网络请求工具类
 * 
 * @author LGhui
 * 
 */
public class BuisNetUtils {

	public interface ResponceCallBack {
		public static String STATUS_OK = "000000";
		public static String STATUS_NET_ERORR = "99999";
		public static String STATUS_DATA_ERORR = "99998";

		public void onSuccess(ResponseContent content);

		public void onFialure(String status, String message);
	}

	public static void requestStr(String url, RequestParam param,
			final ResponceCallBack callBack) {
		requestStr(url, param, callBack, true);
	}

	public static void requestStr(String url, RequestParam param,
			final ResponceCallBack callBack, final boolean isReSultEncrypt) {
		HttpUtils.requestPost(url, param.toParams(),new HttpUtils.ResultCallBack() {
			@Override
			public void postResult(String result) {
				if (TextUtils.isEmpty(result)) {
					callBack.onFialure(ResponceCallBack.STATUS_NET_ERORR,"加载失败,请检查网络");
					return;
				}
				try {
					ResponseContent content = ResponseContent.toResposeContent(result, isReSultEncrypt);
					if (ResponseContent.STATUS_OK.equals(content.getStatus())) {
						callBack.onSuccess(content);
					} else {
						callBack.onFialure(content.getStatus(),content.getMessage());
					}
				} catch (Exception e) {
					e.printStackTrace();
					callBack.onFialure(ResponceCallBack.STATUS_DATA_ERORR, "数据错误");
				}
			}
		});
	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 * @param paramMap
	 * @param callBack
	 */
	public static void UploadSingleFile(String url,Map<String, Object> paramMap, final ResponceCallBack callBack) {
		HttpUtils.uploadFile(url, paramMap, new HttpUtils.ResultCallBack() {
			@Override
			public void postResult(String result) {
				if (TextUtils.isEmpty(result)) {
					callBack.onFialure(ResponceCallBack.STATUS_NET_ERORR,"加载失败,请检查网络");
					return;
				}
				try {
					ResponseContent content = ResponseContent.toResposeContent(
							result, false);
					if (content.isStautsOk()) {
						String data = content.getData();
						Map<String, String> map = (Map<String, String>) JSON.parse(data);
						callBack.onSuccess(content);
					} else {
						callBack.onFialure(content.getStatus(),
								content.getMessage());
					}
				} catch (Exception e) {
					e.printStackTrace();
					callBack.onFialure(ResponceCallBack.STATUS_DATA_ERORR,
							"本地数据解析错误");
				}
			}
		});
	}

}
