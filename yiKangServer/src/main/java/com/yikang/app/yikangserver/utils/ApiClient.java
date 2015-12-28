package com.yikang.app.yikangserver.utils;

import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;
import java.util.Set;

/**
 * 业务网络请求工具类
 * 
 */
public class ApiClient {
	private static final String TAG ="ApiClient";
	private static OkHttpClient client;
	private static Handler mDelivery;

	static {
		client = new OkHttpClient();
		client.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
		mDelivery = new Handler(Looper.getMainLooper());
	}

	public interface ResponceCallBack {
		String STATUS_OK = "000000";
		String STATUS_NET_ERORR = "99999";
		String STATUS_DATA_ERORR = "99998";
		void onSuccess(ResponseContent content);
		void onFialure(String status, String message);
	}

	public static void requestStr(String url, RequestParam param,
			final ResponceCallBack callBack) {
		requestStr(url, param, callBack, true);
	}

	public static void requestStr(String url, RequestParam param, final ResponceCallBack callBack, final boolean isReSultEncrypt) {

		Request request = new Request.Builder()
				.url(url)
				.post(buildBody(param))
				.build();


		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				//处理网络错误
				mDelivery.post(new Runnable() {
					@Override
					public void run() {
						callBack.onFialure(ResponceCallBack.STATUS_NET_ERORR,"加载失败,请检查网络");
					}
				});
			}

			@Override
			public void onResponse(Response response) throws IOException {

				String result = response.body().string();

				try {
					final ResponseContent content = ResponseContent.toResposeContent(result, isReSultEncrypt);
					mDelivery.post(new Runnable() {
						@Override
						public void run() {
							if (ResponseContent.STATUS_OK.equals(content.getStatus())) {
								callBack.onSuccess(content);
							} else {
								callBack.onFialure(content.getStatus(), content.getMessage());
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * 传入一个RequestParam，构建RequestBody
	 *
	 * @param param 需要构建的RequestParam
	 * @return 一个构建好的RequestParam
	 */
	private static RequestBody buildBody(RequestParam param) {
		FormEncodingBuilder builder = new FormEncodingBuilder();

		if (param.getAppId() != null) {
			LOG.i(TAG,"appid");
			builder.add(RequestParam.KEY_APPID, param.getAppId());
		}
		if (param.getAccessTicket() != null) {
			LOG.i(TAG,"AccessTicket");
			builder.add(RequestParam.KEY_ACCESS_TICKET, param.getAccessTicket());
		}
		if (param.getMachineCode() != null) {
			LOG.i(TAG,"machineCode");
			builder.add(RequestParam.KEY_MACHINECODE, param.getMachineCode());
		}
		if (!param.isParamEmpty()) {
			builder.add(RequestParam.KEY_PARAM_DATA, encript(param.getParamJson()));
			LOG.i(TAG,"paramData"+param.getParamJson());
		}

		return builder.build();
	}


	private static String encript(String json) {
		try {
			return AES.encrypt(json, AES.getKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 上传文件
	 * 
	 * @param url
	 * @param paramMap
	 * @param callBack
	 */
	public static void UploadSingleFile(String url,Map<String, Object> paramMap, final ResponceCallBack callBack) {

		if (!DeviceUtils.checkNetWorkIsOk(AppContext.getAppContext())) {
			callBack.onFialure(ResponceCallBack.STATUS_NET_ERORR,"抱歉网络错误");
			return;
		}
		if (paramMap == null || paramMap.isEmpty()) {
			throw new IllegalArgumentException("传入param的不能为null，或者empty");
		}
		Set<String> keySet = paramMap.keySet();
		MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
		for (String key : keySet) {
			Object object = paramMap.get(key);
			if (object instanceof File) {
				File file = (File) object;
				MediaType DEFAULT_BINARY = MediaType.parse("application/octet-stream");
				builder.addFormDataPart(key,file.getName(),RequestBody.create(DEFAULT_BINARY,file));
			} else if (object instanceof String) {
				builder.addFormDataPart(key,(String)object);
			} else {
				throw new IllegalArgumentException(
						"传入的map参数中只能包含File或者String对象");
			}
		}
		final Request request = new Request.Builder().url(url).post(builder.build()).build();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				callBack.onFialure(ResponceCallBack.STATUS_NET_ERORR,"加载失败,请检查网络");
			}

			@Override
			public void onResponse(Response response) throws IOException {
				if(response.code() !=200){
					callBack.onFialure(ResponceCallBack.STATUS_NET_ERORR,"加载失败,请检查网络");
					return;
				}
				String result = response.body().string();
				try{
					ResponseContent content = ResponseContent.toResposeContent(result,false);
					if(content.isStautsOk()){
						callBack.onSuccess(content);
					}else{
						callBack.onFialure(content.getStatus(),
								content.getMessage());
					}
				}catch(Exception e){
					callBack.onFialure(ResponceCallBack.STATUS_DATA_ERORR,
							"本地数据解析错误");
				}
			}
		});

	}

}
