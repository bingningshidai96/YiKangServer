package com.yikang.app.yikangserver.view;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.ui.SimpleActivity;
import com.yikang.app.yikangserver.utils.LOG;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 一个带有LisView的弹出窗
 */
public class SimpleListPopupWindow extends PopupWindow implements
		AdapterView.OnItemClickListener,PopupWindow.OnDismissListener{
	private static final String TAG = "SimpleListPopupWindow";

	protected ListView listView;
	private int maxShowCount = 6; // 设置最大的显示数量

	private TextView tvTitle;
	private OnShowListener onShowListener;
	private OnDismissListener onDismissListener;

	private Context context;

	private AdapterView.OnItemClickListener onItemClickListener;

	/**当弹出来时，是否背景变暗*/
	private boolean isWindowDark = true;

	public SimpleListPopupWindow(Context context) {
		super(context);
		this.context = context;
		init();
	}


	private void init(){
		LayoutInflater inflater = LayoutInflater.from(context);
		View contentView = inflater.inflate(R.layout.ppw_simple_list, null, false);
		setContentView(contentView);// 设置内容


		listView = ((ListView) contentView.findViewById(R.id.lv_simple_list_list));
		listView.setOnItemClickListener(this);

		tvTitle = ((TextView) contentView.findViewById(R.id.tv_simple_list_head));

		setAnimationStyle(R.style.style_simple_popWindow_anim);
		setOnDismissListener(this);
	}

	/**
	 * 设置当弹出时，背景是否变暗
	 * @param isWindowDark
	 */
	public void setIsWindowDark(boolean isWindowDark){
		this.isWindowDark = isWindowDark;
	}



	@Override
	public void onDismiss() {
		if(isWindowDark){ //恢复背景
			Window window = ((Activity) context).getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.alpha = 1f;
			window.setAttributes(lp);
		}

		if(onDismissListener !=null){
			onDismissListener.onDismiss();
		}
	}

	/**
	 *当这个popupWindow弹出时，会回调该监听器的监听方法
	 */
	public interface OnShowListener{
		void onShow();
	}

	/**
	 * 设置show这个行为的监听器
	 * @param listener
	 */
	public void setOnShowListener(OnShowListener listener){
		this.onShowListener = onShowListener;
	}

	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
		super.showAsDropDown(anchor, xoff, yoff, gravity);
		onShow();
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
		onShow();
	}

	/**
	 * 当show出来的时候，会调用这个方法
	 */
	private void onShow(){
		if(isWindowDark){ ///设置背景变暗
			Window window = ((Activity) context).getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.alpha = 0.6f;
			window.setAttributes(lp);
		}

		if(onShowListener !=null){
			onShowListener.onShow();
		}
	}

	/**
	 * 当设置适配器时，高度可能会发生改变
	 */
	public void setAdapter(ListAdapter adapter) {
		listView.setAdapter(adapter);
		setHeightBaseItems();
	}


	public void setTitle(String msg){
		tvTitle.setText(msg);
	}



//	/**
//	 * 获得弹出窗内部的ListView
//	 */
//	public ListView getListView() {
//		return listView;
//	}



	/**
	 * 根据Item的条数来设置高度，最大显示
	 */
	public void setHeightBaseItems() {
		if (listView.getAdapter() == null) {
			return;
		}
		int totalHeight = 0;

		for (int i = 0; i < listView.getAdapter().getCount()
				&& i < maxShowCount; i++) {
			View view = listView.getAdapter().getView(i, null, listView);
			view.measure(0, 0);
			totalHeight += view.getMeasuredHeight();
		}

		listView.measure(0, 0);
		getContentView().measure(0,0);
		setHeight(totalHeight + getContentView().getMeasuredHeight() - listView.getMeasuredHeight());

	}

	/**
	 * 最大的显示数量
	 */
	public void setMaxShowCount(int maxShowCount) {
		this.maxShowCount = maxShowCount;
	}


	public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
		this.onItemClickListener = listener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		onItemClickListener.onItemClick(parent,view,position,id);
	}


}
