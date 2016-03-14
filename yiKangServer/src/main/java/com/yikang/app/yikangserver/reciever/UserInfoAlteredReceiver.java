package com.yikang.app.yikangserver.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UserInfoAlteredReceiver extends BroadcastReceiver{
	public static final String ACTION_USER_INFO_ALTERED ="ACTION_USER_INFO_ALTERED";
	private boolean isAltered = false;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction() == ACTION_USER_INFO_ALTERED){
			isAltered = true;
		}
	}
	
	/**
	 * 获取并消费这一次获取的事件。
	 * @return 如果有收到广播，返回true,但获取一次之后会被消费并重置，再次获取时为false
	 */
	public boolean getAndConsume(){
		boolean currentState = isAltered;
		isAltered = false;
		return currentState;
	}
	
}
