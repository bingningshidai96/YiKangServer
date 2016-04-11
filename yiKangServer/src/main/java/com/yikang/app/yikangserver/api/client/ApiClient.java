package com.yikang.app.yikangserver.api.client;

import android.os.Handler;
import android.os.Looper;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.yikang.app.yikangserver.api.callback.DownloadCallback;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.api.parser.GsonFactory;
import com.yikang.app.yikangserver.utils.AES;
import com.yikang.app.yikangserver.utils.DeviceUtils;
import com.yikang.app.yikangserver.utils.FileUtlis;
import com.yikang.app.yikangserver.utils.LOG;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
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




	/**
	 * @see #execute(String,RequestParam, ResponseCallback,Type,boolean)
	 */
	public static void execute(String url, RequestParam param,
							   final ResponseCallback callBack,
							   final Type resultType) {

		execute(url, param, callBack, resultType, true);
	}


	/**
	 * 异步post请求
	 * @param param 请求参数
	 * @param callBack 请求结果的回调接口
	 * @param isResultEncrypt 结果是否加密
	 */
	public static void execute(String url, RequestParam param,
							   final ResponseCallback callBack,
							   final Type resultType,
							   final boolean isResultEncrypt) {
		Request.Builder builder = new Request.Builder().url(url);
		if(param !=null){
			builder.post(buildBody(param));
		}else {
			builder.get();
		}
		Request request = builder.build();
		executeAsyn(request, callBack,isResultEncrypt,resultType);
	}


	/**
	 * 上传文件
	 */
	public static void postFilesAsyn(String url,
									 FileRequestParam param,
									 final ResponseCallback callBack,Type type){

		if(!DeviceUtils.checkNetWorkIsOk()){
			callBack.onFailure(ResponseCallback.STATUS_NET_ERROR, "抱歉，当前没有网络");
			return;
		}
		ArrayList<File> files = param.getFiles();
		if(files.isEmpty()){
			callBack.onFailure(ResponseCallback.STATUS_DATA_ERROR, "没有上传的文件");
		}

		final Request request = new Request.Builder().url(url).post(buildBody(param)).build();

		executeAsyn(request,callBack,false,type);
	}



	public static void downloadFile(String url,String fileName,final DownloadCallback callBack){
		if(!DeviceUtils.checkNetWorkIsOk()){
			callBack.onFailure(ResponseCallback.STATUS_NET_ERROR, "抱歉，当前没有网络");
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
			builder.add(RequestParam.KEY_APP_ID, param.getAppId());
		}
		if (param.getAccessTicket() != null) {
			builder.add(RequestParam.KEY_ACCESS_TICKET, param.getAccessTicket());
		}
		if (param.getMachineCode() != null) {
			builder.add(RequestParam.KEY_MACHINE_CODE, param.getMachineCode());
		}
		if (!param.isParamEmpty()) {
			try {
				LOG.i(TAG,param.getParamJson());
				String encryptJson = AES.encrypt(param.getParamJson(), AES.getKey());
				builder.add(RequestParam.KEY_PARAM_DATA, encryptJson);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	 * 执行异步请求
	 * @param request 要执行的请求
	 * @param callBack 回调接口
	 * @param isEncrypt 请求结果是否加密
	 */
	private static <T> void executeAsyn(Request request,final ResponseCallback<T> callBack,
									final boolean isEncrypt,final Type typeOfT){
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				mDelivery.post(new OnFailureRunnable(callBack,"加载失败,请检查网络",
						ResponseCallback.STATUS_NET_ERROR));
                e.printStackTrace();
			}

			@Override
			public void onResponse(Response response) throws IOException {
				final String result = response.body().string();
				GsonFactory.GsonProvider provider = isEncrypt ? new GsonFactory.EncryptedProvider()
						: new GsonFactory.NonEncryptedProvider();
                Gson gson = GsonFactory.newInstance(provider);
                Runnable runnable;
                try{

                    LOG.i(TAG,result);
                    ResponseContent<T> content= gson.fromJson(result, typeOfT);
                    if(content.isStautsOk()){
                        runnable = new OnSuccessRunnable<>(callBack, content.getData());

                    }else {
                        runnable = new OnFailureRunnable(callBack,content.getMessage(),
                                content.getStatus());
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    runnable =new OnFailureRunnable(callBack,"本地数据解析错误",
                            ResponseCallback.STATUS_DATA_ERROR);
                }
				mDelivery.post(runnable);

			}
		});
	}


	private static class OnSuccessRunnable<T> implements Runnable{
		private ResponseCallback callback;
		private T data;

		OnSuccessRunnable(ResponseCallback<T> callback,T data){
			this.callback = callback;
			this.data = data;
		}

		@Override
		public void run() {
			callback.onSuccess(data);
		}
	}


	private static class OnFailureRunnable implements Runnable{
		private ResponseCallback callback;
		private String message;
		private String statusCode;

		OnFailureRunnable(ResponseCallback callback,String msg,String statusCode){
			this.callback = callback;
			this.message =msg;
			this.statusCode = statusCode;
		}

		@Override
		public void run() {
			callback.onFailure(statusCode, message);
		}
	}



	/**
	 * 执行下载任务
	 * @param request 请求
	 * @param fileName 存储文件名
	 * @param callBack  回调接口
	 */
	private static void executeDownloadAsyn(Request request, final String fileName,
											final DownloadCallback callBack){
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				mDelivery.post(new Runnable() {
					@Override
					public void run() {
						callBack.onFailure(ResponseCallback.STATUS_NET_ERROR, "加载失败,请检查网络");
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
										 String filepath,DownloadCallback callBack){
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
			callBack.onFailure(DownloadCallback.STATUS_DOWNLOAD_FAIL, "下载出现异常");
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
