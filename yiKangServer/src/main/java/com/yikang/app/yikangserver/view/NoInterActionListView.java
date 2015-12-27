package com.yikang.app.yikangserver.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class NoInterActionListView extends ListView {
	private boolean isItemsEnable;

	public NoInterActionListView(Context context) {
		this(context, null);
	}

	public NoInterActionListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NoInterActionListView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isItemsEnable) {
			return super.onTouchEvent(ev);
		} else {
			super.onTouchEvent(ev);
			return true;
		}
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return !isItemsEnable;
	}

	public void setItemsEnable(boolean isItemsEnable) {
		this.isItemsEnable = isItemsEnable;
	}

}
