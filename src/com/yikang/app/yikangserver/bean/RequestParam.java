package com.yikang.app.yikangserver.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.utils.AES;
import com.yikang.app.yikangserver.utils.LOG;

/**
 * 一次网络请求所需要包含的参数
 * 
 * @author LGhui
 * 
 */
public class RequestParam {
	private static final String TAG = "RequsetParam";
	private static final String ENCRYPT_KEY = AES.getKey();

	public static final String KEY_APPID = "appId";
	public static final String KEY_ACCESS_TICKET = "accessTicket";
	public static final String KEY_MACHINECODE = "machineCode";
	public static final String KEY_PARAM_DATA = "paramData";

	private String appId;
	private String accessTicket;
	private String machineCode;
	private Map<String, Object> paramData;

	public RequestParam() {
		this(null, null);
		AppContext context = AppContext.getAppContext();
		this.appId = context.getAppId();
		this.accessTicket = context.getAccessTicket();
		this.machineCode = AppContext.getAppContext().getDeviceID();
	}

	public RequestParam(String appId, String acessTicket) {
		this(appId, acessTicket, null, null);
		this.machineCode = AppContext.getAppContext().getDeviceID();
	}

	public RequestParam(String appId, String acessTicket, String machineCode,
			Map<String, Object> paramData) {
		this.accessTicket = acessTicket;
		this.appId = appId;
		this.paramData = paramData;
		this.machineCode = machineCode;
	}

	public void add(String key, Object value) {
		if (paramData == null) {
			paramData = new HashMap<String, Object>();
		}
		paramData.put(key, value);
	}

	public void addAll(Map<String, Object> map) {
		if (paramData == null) {
			paramData = new HashMap<String, Object>();
		}
		paramData.putAll(map);
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAccessTicket() {
		return accessTicket;
	}

	public void setAccessTicket(String accessTicket) {
		this.accessTicket = accessTicket;
	}

	public Map<String, Object> getParamData() {
		return paramData;
	}

	/**
	 * 如果之前设置了参数，这个将会调用覆盖之前设置的所有paramData参数 如果不是必要，请使用{@link #addAll() }方法，那个更加安全
	 * 
	 * @param paramData
	 */
	public void setParamData(Map<String, Object> paramData) {
		this.paramData = paramData;
	}

	/**
	 * 将本对象转换成参数列表
	 */
	public List<NameValuePair> toParams() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (appId != null) {
			params.add(new BasicNameValuePair(KEY_APPID, appId));
		}
		if (accessTicket != null) {
			params.add(new BasicNameValuePair(KEY_ACCESS_TICKET, accessTicket));
		}

		if (machineCode != null) {
			params.add(new BasicNameValuePair(KEY_MACHINECODE, machineCode));
		}
		// LOG.d(TAG, "[toParams]"+KEY_APPID+"="+appId);
		// LOG.d(TAG, "[toParams]"+KEY_ACCESS_TICKET+"="+accessTicket);
		LOG.d(TAG, "[toParams]" + KEY_MACHINECODE + "=" + machineCode);
		if (paramData != null) {
			String json = toJson(paramData);
			LOG.d(TAG, "[toParams]" + KEY_PARAM_DATA + "=" + json);
			params.add(new BasicNameValuePair(KEY_PARAM_DATA, Encrypt(json,
					ENCRYPT_KEY)));
		}
		return params;
	}

	/**
	 * 将本对象转换成用于get请求后面的链接参数字符串
	 * 
	 * @return
	 */
	public String toUrlParam() {
		StringBuilder builder = new StringBuilder();
		builder.append(KEY_APPID + "=" + appId);
		builder.append("&");
		builder.append(KEY_ACCESS_TICKET + "=" + accessTicket);
		if (paramData != null && paramData.size() > 0) {
			String json = toJson(paramData);
			builder.append("&");
			builder.append(KEY_PARAM_DATA + "=" + Encrypt(json, ENCRYPT_KEY));
		}
		LOG.d(TAG, builder.toString());
		return builder.toString();
	}

	/**
	 * 将一个对象转换成一个json数据
	 * 
	 * @param object
	 * @return
	 */
	private static String toJson(Object object) {
		return JSON.toJSONString(object);
	}

	/**
	 * 给字符串加密
	 * 
	 * @param sSrc
	 * @param sKey
	 * @return
	 */
	public static String Encrypt(String sSrc, String sKey) {
		String encryptedStr = null;
		try {
			encryptedStr = AES.encrypt(sSrc, sKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptedStr;
	}
}
