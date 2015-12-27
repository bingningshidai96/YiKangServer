package com.yikang.app.yikangserver.utils;

import android.content.Context;
import android.content.Intent;

import com.yikang.app.yikangserver.ui.SimpleActivity;

public class UIHelper {
	/**
	 * 服务日程
	 */
	public static void toServiceCalendar(Context context) {

	}

	public static void showFindPasswordPage(Context context){
		SimpleActivity.SimplePage page = SimpleActivity.SimplePage.findPassw;
		Intent intent = new Intent(context,SimpleActivity.class);
		intent.putExtra(SimpleActivity.EXRRA_PAGE,page);
		context.startActivity(intent);
	}
	
}
