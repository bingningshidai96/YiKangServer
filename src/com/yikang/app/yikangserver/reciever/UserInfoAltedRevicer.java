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
	
	public boolean getAndConsume(){
		boolean currentState = isAlted;
		isAlted = false;
		return currentState;
	}
	
}
