package com.yikang.app.yikangserver.utils;

import android.util.Log;

/**
 * 日志工具类，可以控制全局的日志等级输出
 * 
 * @author LGhui
 * 
 */
public class LOG {
	public static int current_lever = 3;

	public static final int LEVER_VERBOSE = 5;
	public static final int LEVER_DEBUG = 4;
	public static final int LEVER_INFO = 3;
	public static final int LEVER_WARN = 2;
	public static final int LEVER_ERROR = 1;

	public static void v(String tag, String msg) {
		if (current_lever >= LEVER_VERBOSE)
			;
		Log.v(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (current_lever >= LEVER_DEBUG)
			;
		Log.d(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (current_lever >= LEVER_INFO)
			;
		Log.i(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (current_lever >= LEVER_WARN)
			;
		Log.w(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (current_lever >= LEVER_ERROR)
			;
		Log.e(tag, msg);
	}
}
