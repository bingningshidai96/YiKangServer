package com.yikang.app.yikangserver.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.view.adapter.PopListAdapter;

public class TextSpinner extends TextView {
	public static final String TAG = "EdtSpinner";
	private SimpleListPopupWindow listWindow;// 点击之后要弹出的窗体
	private OnDropDownItemClickListener listener;
	private PopListAdapter adapter;

	private int currentSelect = -1;

	public TextSpinner(Context context) {
		this(context, null);
	}

	public TextSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);

		setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listWindow == null) {
					creatSimpleListPopWindow();
				}
				if (listWindow.isShowing()) {
					listWindow.dismiss();
				} else {
					listWindow.showAsDropDown(TextSpinner.this);
				}
			}
		});
	}

	/**
	 * 设置下拉的适配器
	 * 
	 * @param adapter
	 */
	public void setAdapter(PopListAdapter adapter) {
		this.adapter = adapter;
		if (listWindow != null) {
			listWindow.getListView().setAdapter(adapter);
		}
	}

	public PopListAdapter getAdapter() {
		return adapter;
	}

	/**
	 * 设置下拉列表的item点击事件监听器,只能在创建window之前设置。
	 * 
	 * @param listener
	 */
	public void setOnDropDownItemClickListener(
			OnDropDownItemClickListener listener) {
		this.listener = listener;
	}

	public interface OnDropDownItemClickListener {
		void onItemClickListern(TextSpinner spinner, int position);
	}

	/**
	 * 创建一个SimpleListPopupWindow
	 * 
	 * @return
	 */
	private void creatSimpleListPopWindow() {

		listWindow = new SimpleListPopupWindow(this.getContext(), true);
		listWindow.setFocusable(true);
		listWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		// 设置PopupWindow内部的显示内容的listView
		ListView popListView = listWindow.getListView();
		// popListView.setVerticalScrollBarEnabled(false);//隐藏滚动条
		popListView.setDivider(getResources().getDrawable(
				R.drawable.shape_theme_color_line));// 这只蓝色分隔条


		popListView
				.setDividerHeight((int) (getResources().getDisplayMetrics().density*2));// 设置分割线的高度
		popListView.setBackgroundResource(R.drawable.shape_theme_color_reactangle);

		popListView.setPadding(0, 0, 0, 0); // 设置padding,一定要在分隔条之后设置
		listWindow.setWidth(this.getWidth()); // 设置popupwindow的宽度跟上控一样宽
		listWindow.setHeightBaseItems();// 根据item的数量设置高度

		popListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				listWindow.dismiss();
				PopListAdapter adapter = (PopListAdapter) parent.getAdapter();
				adapter.setCurrentSelected(position);
				String str = (String) parent.getAdapter().getItem(position);
				TextSpinner.this.setText(str);
				currentSelect = position;
				LOG.i(TAG,">>>>>>>>>>>>>"+position);
				if (listener != null) {
					listener.onItemClickListern(TextSpinner.this, position);
				}
			}
		});

		if (adapter != null) {
			listWindow.setAdapter(adapter);
		}
	}

	/**
	 * 获得当前的选中的位置，如果没有选中，则返回-1
	 * 
	 * @return
	 */
	public int getCurrentSelction() {
		return currentSelect;
	}

	public void setCurrentSelection(int currentSelection) {
		if (adapter == null) {
			LOG.e(TAG, "adapter还没有设置");
		}
		if (currentSelection < 0 || currentSelection > adapter.getCount()) {
			LOG.e(TAG, "tsp中设置额currentSelctsion不合法");
			return;
		}
		this.currentSelect = currentSelection;
		adapter.setCurrentSelected(currentSelection);
		setText((CharSequence) adapter.getItem(currentSelection));
	}

}
