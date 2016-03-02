package com.yikang.app.yikangserver.api;

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
import com.yikang.app.yikangserver.utils.AES;
import com.yikang.app.yikangserver.utils.DeviceUtils;
import com.yikang.app.yikangserver.utils.FileUtlis;
import com.yikang.app.yikangserver.utils.LOG;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

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
		void onFailure(String status, String message);
	}

	public interface DownloadCallBack extends ResponceCallBack{
		String STATUS_DOWNLOAD_FAIL = "99997";
		void onProgress(long total,long current);
	}


	/**
	 * @see #postAsyn(String,RequestParam,ResponceCallBack,boolean)
	 */
	public static void postAsyn(String url, RequestParam param,
								final ResponceCallBack callBack) {
		postAsyn(url, param, callBack, true);
	}


	/**
	 * 异步post请求
	 * @param param 请求参数
	 * @param callBack 请求结果的回调接口
	 * @param isReSultEncrypt 结果是否加密
	 */
	public static void postAsyn(String url, RequestParam param, final ResponceCallBack callBack,
								final boolean isReSultEncrypt) {
		Request request = new Request.Builder().url(url)
				.post(buildBody(param)).build();
		executeAsyn(request, callBack, isReSultEncrypt);
	}


	/**
	 * 上传文件
	 */
	public static void postFilesAsyn(String url, FileRequestParam param,
									 final ResponceCallBack callBack){
		if(!DeviceUtils.checkNetWorkIsOk()){
			callBack.onFailure(ResponceCallBack.STATUS_NET_ERORR, "抱歉，当前没有网络");
			return;
		}
		ArrayList<File> files = param.getFiles();
		if(files.isEmpty()){
			callBack.onFailure(ResponceCallBack.STATUS_DATA_ERORR, "没有上传的文件");
		}

		final Request request = new Request.Builder().url(url).post(buildBody(param)).build();

		executeAsyn(request,callBack,false);
	}



	public static void downloadFile(String url,String fileName,final DownloadCallBack callBack){
		if(!DeviceUtils.checkNetWorkIsOk()){
			callBack.onFailure(ResponceCallBack.STATUS_NET_ERORR, "抱歉，当前没有网络");
			return;
		}
		Request request = new Request.Builder().url(url).get().build();
		executeDownloadAsyn(request,fileName,callBack);
	}


	/**
	 * 传入一个RequestParam，构建RequestBody
	 * @param param 需要构建的RequestParam
	 * @return 一个构建好的RequestParam
	 */
	private static RequestBody buildBody(RequestParam param) {
		FormEncodingBuilder builder = new FormEncodingBuilder();

		if (param.getAppId() != null) {
			builder.add(RequestParam.KEY_APPID, param.getAppId());
		}
		if (param.getAccessTicket() != null) {
			builder.add(RequestParam.KEY_ACCESS_TICKET, param.getAccessTicket());
		}
		if (param.getMachineCode() != null) {
			builder.add(RequestParam.KEY_MACHINECODE, param.getMachineCode());
		}
		if (!param.isParamEmpty()) {
			builder.add(RequestParam.KEY_PARAM_DATA, encript(param.getParamJson()));
		}
		return builder.build();
	}


	/**
	 * 构建文件请求的requestBody
	 * @param param 文件请求参数
	 * @return form 请求提
	 */
	private static RequestBody buildBody(FileRequestParam param){
		ArrayList<File> files = param.getFiles();
		MediaType DEFAULT_BINARY = MediaType.parse("application/octet-stream");
		MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
		if (param.appId != null) {
			builder.addFormDataPart(FileRequestParam.KEY_APPID, param.appId);
		}
		if (param.accessTicket != null) {
			builder.addFormDataPart(FileRequestParam.KEY_ACCESSTICKET, param.accessTicket);
		}
		if (param.mochineCode != null) {
			builder.addFormDataPart(FileRequestParam.KEY_MACHINECODE, param.mochineCode);
		}
		builder.addFormDataPart(FileRequestParam.KEY_FILEGROUP,param.getFileGroup());
		for (File file:files) {
			builder.addFormDataPart(FileRequestParam.KEY_FILES,file.getName(),
					RequestBody.create(DEFAULT_BINARY,file));
		}
		LOG.i(TAG,param.appId+"=="+param.accessTicket+"==="+param.mochineCode+"==="+param.getFileGroup());
		return builder.build();
	}


	/**
	 * 加密字符串
	 * @param json 需要加密的json
	 * @return 返回加密后的json
	 */
	private static String encript(String json) {
		try {
			return AES.encrypt(json, AES.getKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * 执行异步请求
	 * @param request 要执行的请求
	 * @param callBack 回调接口
	 * @param isReSultEncrypt 请求结果是否加密
	 */
	private static void executeAsyn(Request request,final ResponceCallBack callBack,
									final boolean isReSultEncrypt ){
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				mDelivery.post(new Runnable() {
					@Override
					public void run() {
						callBack.onFailure(ResponceCallBack.STATUS_NET_ERORR, "加载失败,请检查网络");
					}
				});
			}

			@Override
			public void onResponse(Response response) throws IOException {
				final String result = response.body().string();
				mDelivery.post(new Runnable() {
					@Override
					public void run() {
						try {
							final ResponseContent content = ResponseContent
									.toResposeContent(result, isReSultEncrypt);

							if (content.isStautsOk()) {
								callBack.onSuccess(content);
							} else {
								callBack.onFailure(content.getStatus(), content.getMessage());
							}
						} catch (Exception e) {
							e.printStackTrace();

							callBack.onFailure(ResponceCallBack.STATUS_DATA_ERORR, "本地数据解析错误");
						}
					}
				});
			}
		});
	}


	/**
	 * 执行下载任务
	 * @param request 请求
	 * @param fileName 存储文件名
	 * @param callBack  回调接口
	 */
	private static void executeDownloadAsyn(Request request, final String fileName,
											final DownloadCallBack callBack){
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				mDelivery.post(new Runnable() {
					@Override
					public void run() {
						callBack.onFailure(ResponceCallBack.STATUS_NET_ERORR, "加载失败,请检查网络");
					}
				});
			}

			@Override
			public void onResponse(Response response) throws IOException {
				boolean successful = response.isSuccessful();
				if (!successful) {
					callBack.onFailure(String.valueOf(response.code()), "抱歉，下载失败");
					return;
				}
				long size = response.body().contentLength();
				InputStream inputStream = response.body().byteStream();
				saveWithProgress(inputStream, size, fileName, callBack);
			}

		});
	}


	private static void saveWithProgress(InputStream inputStream,long total,
										 String filepath,DownloadCallBack callBack){
		//检查父级目录是否存在
		File parent = new File(FileUtlis.getParentPath(filepath));
		if(!parent.exists())  parent.mkdirs();

		//开始写文件
		File saveFile = new File(parent,FileUtlis.getFileName(filepath));
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(saveFile);
			byte[] buffer = new byte[512];
			long current = 0; //当前已经下载的

			int count;
			while((count=inputStream.read(buffer))!=-1){
				fos.write(buffer,0,count);
				current+=count;
				callBack.onProgress(total, current);
			}
			fos.flush();
			callBack.onSuccess(null);
		} catch (IOException e) {
			e.printStackTrace();
			callBack.onFailure(DownloadCallBack.STATUS_DOWNLOAD_FAIL, "下载出现异常");
		} finally {
			try{
				if(inputStream!=null){
					inputStream.close();
				}
				if(fos!=null){
					fos.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}

}
