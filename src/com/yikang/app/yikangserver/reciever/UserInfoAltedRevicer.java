package com.yikang.app.yikangserver.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UserInfoAltedRevicer extends BroadcastReceiver{
	public static final String ACTION_USER_INFO_ALTED ="ACTION_USER_INFO_ALTED";
	private boolean isAlted = false;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction() == ACTION_USER_INFO_ALTED){
			isAlted = true;
		}
	}
	
	/**
	 * 获取并消费这一次获取的事件。
	 * @return 如果有收到广播，返回true,但获取一次之后会被消费并重置，再次获取时为false
	 */
	public boolean getAndConsume(){
		boolean currentState = isAlted;
		isAlted = false;
		return currentState;
	}
	
}
