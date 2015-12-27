package com.yikang.app.yikangserver.adapter;

/**
 * 当用户的某道题选择发生变化时，会触发总体的分数的改变，
 */
public interface TotalPointChangedListener {
	/**
	 * the total point has changed
	 * 
	 * @param totalPoint
	 *            sum of all question's point
	 * @param increment
	 *            the change-num of total point,mabey negative
	 */
	void onTotalPointChange(float totalPoint, float increment);
}
