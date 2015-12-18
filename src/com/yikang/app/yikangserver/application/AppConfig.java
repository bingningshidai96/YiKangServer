package com.yikang.app.yikangserver.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import com.yikang.app.yikangserver.utils.LOG;
import android.content.Context;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 * 
 * @author LGhui
 * 
 */

public class AppConfig {
	/** 保存在属性文件中 */
	public static final String KEY_DOUBLE_CLICK_EXIT = "KEY_DOUBLE_CLICK_EXIT";
	public static final String CONF_DEVICE_ID = "DEVICE_ID";
	public static final String CONF_DEVICE_ID_TYPE = "DEVICE_ID_TYPE";
	public static final String CONF_IS_DEVICE_REGISTED = "IS_DEVICE_REGISTED";
	
	public static final String PRE_APP_FIRST_RUN = "app.isFirstRun";
	
	
	static final String CONF_APPID = "APPID";
	static final String CONF_APP_USER_ID = "APP_USER_ID";
	static final String CON_APP_ACCESS_TICKET = "ACCESS_TICKET";
	
	

	public static final String APP_CONFIG = "config";
	private static final String TAG = "AppConfig";

	private static AppConfig appConfig;
	private Context mContext;

	private AppConfig(Context context) {
		this.mContext = context;
	}

	/**
	 * 获取一个单例的mContext
	 * @param context 上下文参数。如果之前调用过此方法，可以为null，因为默认的最早调用的是appContext
	 */
	public static AppConfig getAppConfig(Context context) {
		if (appConfig == null) {
			appConfig = new AppConfig(context);
		}
		return appConfig;
	}

	/**
	 * 根据键找到配置的值，如果配置文件不存在或者键不存在则返回null
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		if (key == null) {
			return null;
		}
		Properties props = getProperties();
		return (props != null) ? props.getProperty(key) : null;
	}

	/**
	 * 获取所有的配置属性
	 * 
	 * @return
	 */
	public Properties getProperties() {
		FileInputStream fileInput = null;
		Properties prop = new Properties();
		try {
			File configDir = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			File configFile = new File(configDir, APP_CONFIG);
			if (!configFile.exists()) {
				boolean isCreate = configFile.createNewFile();
				LOG.i(TAG, "" + isCreate);

			}
			fileInput = new FileInputStream(configFile);
			prop.load(fileInput);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return prop;
	}

	/**
	 * 保存所有的配置属性
	 * 
	 * @param pros
	 */
	public void saveProperties(Properties pros) {
		FileOutputStream fileOutput = null;
		File configDir = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
		try {
			fileOutput = new FileOutputStream(configDir.getAbsolutePath()
					+ File.separator + APP_CONFIG);
			pros.store(fileOutput, null);
			fileOutput.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setProperty(String key, String value) {
		if (key == null || value == null) {
			LOG.w(TAG, "[setProperty] key or value is null");
			return;
		}
		Properties properties = getProperties();
		properties.setProperty(key, value);
		saveProperties(properties);
	}

	public void remove(String... key) {
		Properties props = getProperties();
		for (String k : key)
			props.remove(k);
		saveProperties(props);
	}

}
