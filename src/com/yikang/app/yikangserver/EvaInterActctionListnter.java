package com.yikang.app.yikangserver;

/**
 * 此接口专门用于评估页面中 fragment和activity之间的交互
 * @author LGhui
 *
 */
public interface EvaInterActctionListnter {
	public static final int WHAT_POINT = 100; // 在表示消息类型是分数改变
	public static final int WHAT_SUBMIT = 101; // 在表示消息类型是分数改变
	
	public static final int WHAT_NEXT_CROSSWIRE=102;
	void onInterAction(int what, Object param);
}
