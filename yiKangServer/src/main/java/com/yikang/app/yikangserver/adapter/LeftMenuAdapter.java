package com.yikang.app.yikangserver.adapter;

import java.util.HashSet;
import java.util.List;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.data.EvaluationLocalData.TableType;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class LeftMenuAdapter extends CommonAdapter<TableType> {
	private Drawable rightDrawable;
	private HashSet<Integer> submitTabs;
	private int currentTabId;

	private int hlBgColor;
	private int commonBgColor;

	public LeftMenuAdapter(Context context, List<TableType> datas) {
		super(context, datas, R.layout.item_drawer_table_name);
		Resources res = context.getResources();
		hlBgColor = res.getColor(R.color.evalutation_left_item_hl_bg);
		rightDrawable = res.getDrawable(R.drawable.evalu_menu_submited_flag);
		rightDrawable.setBounds(new Rect(0, 0, rightDrawable
				.getIntrinsicWidth(), rightDrawable.getIntrinsicHeight()));
	}

	@Override
	protected void convert(ViewHolder holder, TableType item) {
		TextView tv = holder.getView(R.id.tv_item_table_name);
		setItemStyle(tv, item);
		tv.setText(item.getTableName());

	}

	private void setItemStyle(TextView tv, TableType item) {
		if (item.getTableId() == currentTabId) {
			tv.setBackgroundColor(hlBgColor);
		} else {
			tv.setBackgroundColor(commonBgColor);
		}

		if (submitTabs != null && submitTabs.contains(item.getTableId())) {
			tv.setCompoundDrawables(null, null, rightDrawable, null);
		} else {
			tv.setCompoundDrawables(null, null, null, null);
		}
	}

	public void setSubmitTabs(HashSet<Integer> submitTabs) {
		this.submitTabs = submitTabs;
	}

	public int getCurrentTabId() {
		return currentTabId;
	}

	public void setCurrentTab(int currentTab) {
		this.currentTabId = currentTab;
	}
}