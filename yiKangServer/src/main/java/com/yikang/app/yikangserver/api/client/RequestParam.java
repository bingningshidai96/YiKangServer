package com.yikang.app.yikangserver.api.client;
import com.yikang.app.yikangserver.api.parser.GsonFactory;
import com.yikang.app.yikangserver.application.AppContext;
import java.util.HashMap;
import java.util.Map;

/**
 * 一次网络请求所需要包含的参数
 */
public class RequestParam {
	private static final String TAG = "RequsetParam";

	public static final String KEY_APP_ID = "appId";
	public static final String KEY_ACCESS_TICKET = "accessTicket";
	public static final String KEY_MACHINE_CODE = "machineCode";
	public static final String KEY_PARAM_DATA = "paramData";

	private String appId;
	private String accessTicket;
	private String machineCode;
	private Map<String, Object> paramData;

	public RequestParam() {
		this(AppContext.getAppContext().getAppId(),
				AppContext.getAppContext().getAccessTicket());
	}

	public RequestParam(String appId, String accessTicket) {
		this(appId, accessTicket, AppContext.getAppContext().getDeviceID());
	}


	public RequestParam(String appId, String accessTicket, String machineCode) {
		this.accessTicket = accessTicket;
		this.appId = appId;
		this.machineCode = machineCode;
		this.paramData = new HashMap<>();
	}



	public String getAppId() {
		return appId;
	}


	public String getAccessTicket() {
		return accessTicket;
	}


	public String getMachineCode() {
		return machineCode;
	}




	public void add(String key, Object value) {
		paramData.put(key, value);
	}

	public void addAll(Map<String, Object> map) {
		paramData.putAll(map);
	}

	public Object remove(String key){
		return paramData.remove(key);
	}

	public boolean isParamEmpty(){
		return paramData.isEmpty();
	}

	/**
	 * 获得json化的paramData
	 */
	public String getParamJson(){
		return toJson(paramData);
	}


	/**
	 * 将一个对象转换成一个json数据
	 */
	private static String toJson(Object object) {
		if(object == null){
			return null;
		}
		return GsonFactory.newInstance(new GsonFactory.EncryptedProvider()).toJson(object);
	}

}
