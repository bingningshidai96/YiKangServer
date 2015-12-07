package com.yikang.app.yikangserver.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.yikang.app.yikangserver.application.AppContext;

public class HttpUtils {
	private static final String TAG = "HttpUtils";
	private static final HttpClient client;
	static {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setTimeout(params, 2000);// 从连接管理器获得连接的超时时间
		HttpConnectionParams.setConnectionTimeout(params, 10000);// 链接超时时间
		HttpConnectionParams.setSoTimeout(params, 30000);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);

		client = new DefaultHttpClient(cm, params);
	}

	public interface ResultCallBack {
		void postResult(String result);
	}

	/**
	 * 发送get 请求
	 */
	public static void requestGet(String url, ResultCallBack callBack) {
		LOG.d(TAG, url);
		if (!checkNetWorkIsOk(AppContext.getAppContext())) {
			callBack.postResult(null);
			return;
		}
		HttpStrAsycTask task = new HttpStrAsycTask(callBack);
		HttpGet get = new HttpGet(url);
		task.execute(get);
	}

	/**
	 * 发送post 请求
	 */
	public static void requestPost(String url, List<NameValuePair> params,ResultCallBack callBack) {
		if (!checkNetWorkIsOk(AppContext.getAppContext())) {
			callBack.postResult(null);
			return;
		}

		HttpStrAsycTask task = new HttpStrAsycTask(callBack);
		HttpPost post = new HttpPost(url);
		try {
			if (params != null) {
				post.setEntity((new UrlEncodedFormEntity(params, "UTF-8")));
			}
			task.execute(post);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断网络是否已经连接
	 */
	public static boolean checkNetWorkIsOk(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;

	}

	/**
	 * 获取当前网络类型，跟ConnectivityManager中定义的一样
	 */
	public static int getNetWorkType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return -1;
		}
		return networkInfo.getType();
	}

	/**
	 * 上传数据
	 */
	public static void uploadDate(String url, List<Object> params,
			ResultCallBack callBack) {
	}

	public static void uploadFile(String url, Map<String, Object> param,
			ResultCallBack callBack) {
		if (!checkNetWorkIsOk(AppContext.getAppContext())) {
			callBack.postResult(null);
			return;
		}
		if (param == null || param.isEmpty()) {
			throw new IllegalArgumentException("传入param的不能为null，或者empty");
		}
		Set<String> keySet = param.keySet();
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		for (String key : keySet) {
			Object object = param.get(key);
			if (object instanceof File) {
				File file = (File) object;
				FileBody fileBody = new FileBody(file);
				builder.addPart(key, fileBody);
			} else if (object instanceof String) {
				StringBody stringBody = new StringBody((String) object,
						ContentType.TEXT_PLAIN);
				builder.addPart(key, stringBody);
			} else {
				throw new IllegalArgumentException(
						"传入的map参数中只能包含File或者String对象");
			}
		}
		HttpStrAsycTask task = new HttpStrAsycTask(callBack);
		HttpPost post = new HttpPost(url);
		post.setEntity(builder.build());
		task.execute(post);
		LOG.i(TAG, "[uploadFile]" + "现在开始获取文件");
	}

	/**
	 * 利用异步任务
	 * 
	 */
	private static class HttpStrAsycTask extends
			AsyncTask<HttpUriRequest, Integer, String> {
		private static final String TAG = "HttpUtils$HttpStrAsycTask";
		private ResultCallBack callback;
		private long start_time;

		public HttpStrAsycTask(ResultCallBack callback) {
			this.callback = callback;
		}

		@Override
		protected String doInBackground(HttpUriRequest... params) {
			start_time = System.currentTimeMillis();
			LOG.i(TAG, "[doInBackground]" + "start_time");
			HttpUriRequest request = params[0];
			String result = null;
			HttpResponse response = null;

			try {
				response = client.execute((HttpUriRequest) request);
				int statusCode = response.getStatusLine().getStatusCode();
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					result = EntityUtils.toString(entity);
					long endtime = System.currentTimeMillis();
					LOG.d(TAG, "[doInBackground]请求成功   " + request.getURI()+ "\n耗时" + (endtime - start_time) + "毫秒");
				} else {
					LOG.d(TAG, "[doInBackground]请求失败：statusCode=" + statusCode);
				}
			} catch (Exception e) {
				e.printStackTrace();
				request.abort();
			} finally {
				if (response != null && response.getEntity() != null) {
					try {
						response.getEntity().consumeContent();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			callback.postResult(result);
		}

	}
}
