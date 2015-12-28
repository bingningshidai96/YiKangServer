package com.yikang.app.yikangserver.adapter;

import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.bean.CrossWirses;

public class CrossWirsesItemAdapter extends CommonAdapter<CrossWirses> {
	/**
	 * 这个代表了已经提交成功的item位置
	 */
	private HashSet<Integer> submitedSet;
	private int colorUndone ;
	private int colorDone;

	public CrossWirsesItemAdapter(Context context, List<CrossWirses> datas) {
		super(context, datas, R.layout.item_colum_textview);
		Resources res = context.getResources();
		colorUndone = res.getColor(R.color.crosswire_item_bg_undone);
		colorDone = res.getColor(R.color.crosswire_item_bg_done);
	}

	@Override
	protected void convert(ViewHolder holder, CrossWirses item) {
		int position = holder.getPosition();
		CrossWirses crossWirses = datas.get(position);
		String name = crossWirses.getQuestionCrosswiseName(); // 行的名字
		int id = crossWirses.getQuestionCrosswiseId(); // 行的id

		TextView tvItem = holder.getView(R.id.tv_crosswire_item);
		tvItem.setText(name);
		// 如果已经提交过了，就显示不同的颜色
		if (submitedSet != null && submitedSet.contains(id)) {
			tvItem.setBackgroundColor(colorDone);
		} else {
			tvItem.setBackgroundColor(colorUndone);
		}
	} 

	/**
	 * 一次添加一个集合，包含已经提交的所有位置
	 * 
	 * @param set
	 */
	public void setSubmitedSet(HashSet<Integer> set) {
		submitedSet = set;
	}

	/**
	 * 添加一个新提交的位置
	 * 
	 * @param position
	 */
	public void addSubmitPosition(int position) {
		submitedSet.add(position);
	}

}