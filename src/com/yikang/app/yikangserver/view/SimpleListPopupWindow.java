package com.yikang.app.yikangserver.view;

import com.yikang.app.yikangserver.utils.LOG;

import android.content.Context;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

/**
 * 一个带有LisView的弹出窗
 * @author LGhui
 *
 */
public class SimpleListPopupWindow extends PopupWindow{
	private ListView listView; 
	private int maxShowCount=4; //设置最大的显示数量
	private static final String TAG = "SimpleListPopupWindow";
	public SimpleListPopupWindow(Context context,int width,int height) {
		super(width, height);
		listView = new ListView(context);
		setContentView(listView);//设置内容
		
	}
	
	/**
	 * 当设置适配器时，高度可能会发生改变
	 * @param adapter
	 */
	public void setAdapter(ListAdapter adapter){
		listView.setAdapter(adapter);
		setHeightBaseItems();
	}
	
	/**
	 * 创建一个初始宽和高为0的listWindow
	 * @param context
	 * @param datas
	 */
	public SimpleListPopupWindow(Context context,boolean focusable){
		this(context, 0, 0);
		if(isFocusable()){
			setFocusable(focusable);
		}
	}
	
	/**
	 * 获得弹出窗内部的ListView
	 * @return
	 */
	public ListView getListView(){
		return listView;
	}
	
	
	
	
	/**
	 * 根据Item的条数来设置高度，最大显示
	 */
	public void setHeightBaseItems(){
		if(listView.getAdapter()==null){
			return;
		}
		int totalHeight = 0;
		
		for(int i=0;i<listView.getAdapter().getCount()&&i<maxShowCount;i++){
			View view = listView.getAdapter().getView(i, null, listView);
			view.measure(0, 0);
			totalHeight+=view.getMeasuredHeight();
		}
		LOG.d(TAG, "==="+listView.getAdapter().getCount());
		setHeight(totalHeight);
		
	}
	
	/**
	 * 最大的显示数量
	 * @param maxShowCount
	 */
	public void setMaxShowCount(int maxShowCount){
		this.maxShowCount = maxShowCount;
	}
	
}
