package com.yikang.app.yikangserver.application;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.utils.LOG;

public class AppContext extends Application {
	private static final String TAG = "AppContext";
	//缓存路径
	private static final String CACHE_PATH =
			Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"hulijia";
	//图片缓存路径
	public static final String CACHE_IMAGE_PATH = CACHE_PATH + File.separator+ "images";
	public static final int CACHE_DISK_SIZE = 100*1<<20;

	private static AppContext appContext;
	private static final String PREF_NAME = "app_pref";

	private String accessTicket;
	private int diviceIdType = -1;
	private String diviceId;

	public static final boolean DEBUG = false;

	@Override
	public void onCreate() {
		super.onCreate();
		appContext = this;
		CrashReport.initCrashReport(appContext, "900015194", DEBUG);
		AppConfig.getAppConfig(this);
		JPushInterface.setDebugMode(DEBUG); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
		initCachePath();
		ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(
				this);
		configBuilder.tasksProcessingOrder(QueueProcessingType.LIFO)
		.diskCacheSize(CACHE_DISK_SIZE);

		// DisplayImageOptions.Builder displayOptionBuidler =
		// new DisplayImageOptions.Builder();
		//
		// displayOptionBuidler.showImageOnLoading(R.drawable.img_service_defualt)
		// .showImageForEmptyUri(R.drawable.img_service_defualt)
		// .showImageOnFail(R.drawable.image_default);
		// configBuilder.defaultDisplayImageOptions(displayOptionBuidler.build());
		ImageLoader.getInstance().init(configBuilder.build());
	}

	private void initCachePath() {
		File file = new File(CACHE_IMAGE_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static AppContext getAppContext() {
		return appContext;
	}

	public void login(final User user) {
		Properties properties = new Properties() {
			{
				setStringProperty("user.name", user.name);
				setStringProperty("user.userId", user.userId);
				setIntProperty("user.profession", user.profession);

				setIntProperty("user.jobType", user.jobType);
				setIntProperty("user.paintsNums", user.paintsNums);
				setStringProperty("user.hosital", user.hosital);
				setStringProperty("user.deparment", user.deparment);
				setStringProperty("user.special", user.special);
				setStringProperty("user.inviteCode", user.inviteCode);
				setStringProperty("user.avatarImg", user.avatarImg);
				setIntProperty("user.status", user.status);
				setStringProperty("user.mapPositionAddress",user.mapPositionAddress);
				setStringProperty("user.addressDetail", user.addressDetail);
				setStringProperty("user.districtCode", user.districtCode);
				setStringProperty("user.consumerTime", user.consumerTime);
				setStringProperty("user.invitationUrl", user.invitationUrl);
			}

			private void setIntProperty(String key, int value) {
				setProperty(key, String.valueOf(value));

			}

			private void setStringProperty(String key, String value) {
				if (value != null) {
					setProperty(key, value);
					LOG.i(TAG, "[setStringProperty]" + value);
				}
			}

		};

		Properties propertiesAll = AppConfig.getAppConfig(appContext)
				.getProperties();
		Enumeration<Object> keys = propertiesAll.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith("user")) {
				propertiesAll.remove(key);
			}
		}

		Enumeration<Object> keys2 = properties.keys();
		while (keys2.hasMoreElements()) {
			String key = (String) keys2.nextElement();
			propertiesAll.setProperty(key, properties.getProperty(key));
		}

		setProperties(propertiesAll);

	}
	
	/**
	 * 登出
	 */
	public void logout() {
		AppConfig appConfig = AppConfig.getAppConfig(appContext);
		appConfig.remove("user.name", "user.jobType", "user.profession",
				"user.userId", "user.paintsNums");
		accessTicket = null;
		removeProperty(AppConfig.CON_APP_ACCESS_TICKET);
		removeProperty(AppConfig.CONF_IS_DEVICE_REGISTED);//取消用户注册
	}
	
	/**
	 * 获取用户的信息
	 */
	public User getUser() {
		User user = new User();
		user.name = getProperty("user.name");
		user.userId = getProperty("user.userId");
		user.profession = getIntProperty("user.profession");
		user.jobType = getIntProperty("user.jobType");
		user.paintsNums = getIntProperty("user.paintsNums");
		user.hosital = getProperty("user.hosital");
		user.deparment = getProperty("user.deparment");
		user.special = getProperty("user.special");
		user.inviteCode = getProperty("user.inviteCode");
		user.avatarImg = getProperty("user.avatarImg");
		user.inviteCode = getProperty("user.inviteCode");
		user.status = getIntProperty("user.status");
		user.mapPositionAddress = getProperty("user.mapPositionAddress");
		user.addressDetail = getProperty("user.addressDetail");
		user.districtCode = getProperty("user.districtCode");
		user.consumerTime = getProperty("user.consumerTime");
		user.invitationUrl = getProperty("user.invitationUrl");
		return user;
	}

	private final int getIntProperty(String key) {
		String number = getProperty(key);
		return TextUtils.isEmpty(number) ? 0 : Integer.parseInt(number);

	}

	/**
	 * 获取DeviceID
	 * 
	 * @return
	 */
	public String getDeviceID() {

		if (TextUtils.isEmpty(diviceId)) {
			// 从本地文件中获取
			diviceId = getProperty(AppConfig.CONF_DEVICE_ID);
			// 获取设备imei
			if (TextUtils.isEmpty(diviceId)) {
				TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				diviceId = manager.getDeviceId();
				diviceIdType = 1;// imei
				LOG.i(TAG, "imei");
			}

			// 获取mac
			if (TextUtils.isEmpty(diviceId)) {
				WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = wifi.getConnectionInfo();
				diviceId = info.getMacAddress();
				diviceIdType = 2; // mac
				LOG.i(TAG, "mac");
			}

			// 用uuid生成
			if (TextUtils.isEmpty(diviceId)) {
				diviceId = UUID.randomUUID().toString();
				diviceIdType = 4; // uuid
				LOG.i(TAG, "uuid");
			}
			// 保存起来
			AppConfig.getAppConfig(this).setProperty(AppConfig.CONF_DEVICE_ID,
					diviceId);
			AppConfig.getAppConfig(this)
					.setProperty(AppConfig.CONF_DEVICE_ID_TYPE,
							String.valueOf(diviceIdType));
		}
		return diviceId;
	}

	/**
	 * 获取deviceId的类型
	 */
	public int getDeviceIdType() {
		if (diviceIdType == -1) { // 充文件中获取
			String type = getProperty(AppConfig.CONF_DEVICE_ID_TYPE);
			if (!TextUtils.isEmpty(type)) {
				diviceIdType = Integer.parseInt(type);
			}
		}
		if (diviceIdType == -1) {
			getDeviceID(); // 文件中不存在，则重新读。可能出现在第一次使用时
		}

		if (diviceIdType == -1) { // 可能出现错误了。将之前的获取记录删除，重新获取
			LOG.e(TAG,
					"error! 'deviceID' is in property file while 'diviceIdType' not");
			diviceId = null;
			removeProperty(AppConfig.CONF_DEVICE_ID);
			getDeviceID();
		}
		return diviceIdType;
	}

	/**
	 * 判断设备是否已经注册
	 */
	public boolean isDeviceRegisted() {
		String property = getProperty(AppConfig.CONF_IS_DEVICE_REGISTED);
		if (!TextUtils.isEmpty(property) && property.equals("1")) {
			return true;
		}
		return false;
	}

	/**
	 * 获得一个accessTicket
	 */
	public String getAccessTicket() {
		if (accessTicket == null) {
			accessTicket = getProperty(AppConfig.CON_APP_ACCESS_TICKET);
		}
		return accessTicket;
	}

	/**
	 * 更新accessTicket
	 */
	public void updateAccessTicket(String ticket) {
		if (ticket == null)
			throw new IllegalArgumentException("erorr! the argument 'ticket' is null");
		if (ticket.equals(accessTicket)) {
			return;
		}
		String key = AppConfig.CON_APP_ACCESS_TICKET;
		AppConfig.getAppConfig(this).setProperty(key, ticket);
		accessTicket = ticket;
	}
	
	
	public int getVersionCode(){
		int versionCode = 0;
		try {
			versionCode = appContext.getPackageManager()
			.getPackageInfo(appContext.getPackageName(), 
					0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}
	
	
	/**
	 * 判断sd卡是否挂载
	 */
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).saveProperties(ps);
	}
	
	

	/**
	 * 获取cookie时传AppConfig.CONF_COOKIE, 如果不存在会返回null
	 */
	public String getProperty(String key) {
		String res = AppConfig.getAppConfig(this).getProperty(key);
		return res;
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}

	public static SharedPreferences getPreferences() {
		return appContext.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
	}

	public static int get(String key, int defaultVal) {
		return getPreferences().getInt(key, defaultVal);
	}

	public static String get(String key, String defaultVal) {
		return getPreferences().getString(key, defaultVal);

	}

	public static boolean get(String key, boolean defaultVal) {
		return getPreferences().getBoolean(key, defaultVal);
	}

	public static boolean contains(String key){
		return getPreferences().contains(key);
	}



	public static void set(String key, boolean value) {
		Editor editor = getPreferences().edit();
		editor.putBoolean(key, value).commit();
	}

	public static void set(String key, String value) {
		Editor editor = getPreferences().edit();
		editor.putString(key, value).commit();
	}


	public static void set(String key, int value) {
		Editor editor = getPreferences().edit();
		editor.putInt(key, value).commit();
	}


	public static void showToast(String message) {
		Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(int message) {
		Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, int message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void showToastLong(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static String getStrRes(int resId) {
		return appContext.getResources().getString(resId);
	}

	public static Drawable getDrawRes(int resId) {
		return appContext.getResources().getDrawable(resId);
	}

	public String getAppId() {
		return "appId";
	}

}
