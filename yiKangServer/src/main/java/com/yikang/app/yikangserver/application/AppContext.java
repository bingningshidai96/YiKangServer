package com.yikang.app.yikangserver.application;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.api.parser.GsonFactory;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.utils.AES;
import com.yikang.app.yikangserver.utils.LOG;

public class AppContext extends Application {
	private static final String TAG = "AppContext";
	//缓存路径
	private static final String CACHE_PATH =
			Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"jiajia";
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

		ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(this);
		File cacheDir = new File(CACHE_IMAGE_PATH);
        DisplayImageOptions.Builder builder =new DisplayImageOptions.Builder();
        builder.cacheInMemory(true)
                .cacheOnDisk(true);
		configBuilder.tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(CACHE_DISK_SIZE)
				.diskCacheFileCount(200)
				.diskCache(new UnlimitedDiskCache(cacheDir))
				.tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(builder.build())
                .writeDebugLogs();

		ImageLoader.getInstance().init(configBuilder.build());
        correctPreference();
	}



    /**
     *如果版本更新，修正数据
     */
    private void correctPreference(){
        int currentVersion = AppContext.getAppContext().getVersionCode();
        int cacheVersionCode = AppContext.get(AppConfig.PRE_VERSION_CODE, 0);

        LOG.i(TAG,"currentVersion"+currentVersion+"cacheVersion"+cacheVersionCode);
        if(currentVersion>cacheVersionCode){
            AppContext.set(AppConfig.PRE_VERSION_CODE,currentVersion);
            AppContext.set(AppConfig.PRE_APP_FIRST_RUN,true);

            /**因为升级之后，和之前的数据已经不一致*/
            if(cacheVersionCode<=6){
                AppContext.getAppContext().logout();
//				File configDir = getDir("config", Context.MODE_PRIVATE);
//				File configFile = new File(configDir, "config");
//				if (!configFile.exists()) {
//					configFile.delete();
//				}
            }
        }
    }

	/**
	 * 初始化缓存路径
	 */
	private void initCachePath() {
		File file = new File(CACHE_IMAGE_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
	}



	public static AppContext getAppContext() {
		return appContext;
	}


	/**
	 * 登录
	 * @param user
	 */
	public void login(final User user) {
		Gson gson = GsonFactory.newInstance(new GsonFactory.NonEncryptedProvider());
		try {
			String encrypt = AES.encrypt(gson.toJson(user), AES.getKey());
			Properties propertiesAll = AppConfig.getAppConfig(appContext).getProperties();
			Enumeration<Object> keys = propertiesAll.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				if (key.startsWith("user")) {
					propertiesAll.remove(key);
				}
			}
			propertiesAll.setProperty("user.info",encrypt);
			setProperties(propertiesAll);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 登出
	 */
	public void logout() {
		Properties propertiesAll = AppConfig.getAppConfig(appContext).getProperties();
		Enumeration<Object> keys = propertiesAll.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith("user")) {
				propertiesAll.remove(key);
			}
		}
		accessTicket = null;
		removeProperty(AppConfig.CON_APP_ACCESS_TICKET);
		removeProperty(AppConfig.CONF_IS_DEVICE_REGISTED);//取消用户注册
	}
	
	/**
	 * 获取用户的信息
	 */
	public User getUser() {
		User user = null;
		try {
			String encryptedJson = getProperty("user.info");
			if(encryptedJson!=null){
				String json = AES.decrypt(encryptedJson, AES.getKey());
				Gson gson = GsonFactory.newInstance(new GsonFactory.NonEncryptedProvider());
				user = gson.fromJson(json, User.class);
			}
		}catch (Exception e){ //因为可能出现产品更新，数据不一致问题
			e.printStackTrace();
			logout(); //如果解析错误，则将用户数据清空
		}
		return user;
	}


	/**
	 * 获取DeviceID
	 *  IMEI
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
	 * IMEI 1
	 * mac 2
	 * UUID 4
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


	public String getVersionName(){
		String versionName = null;
		try {
			versionName = appContext.getPackageManager()
                    .getPackageInfo(appContext.getPackageName(),
							0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
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
