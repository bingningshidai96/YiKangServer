package com.yikang.app.yikangserver.view.adapter;

import java.util.List;

import com.yikang.app.yikangserver.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PopListAdapter extends BaseAdapter {
	private Context context;
	private List<String> datas;
	private LayoutInflater inflater;
	private int currnetSlected;

	public PopListAdapter(Context context, List<String> datas) {
		this.context = context;
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas == null ? null : datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setCurrentSelected(int selected) {
		this.currnetSlected = selected;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.ppw_array_item, parent, false);
		TextView tv = (TextView) view.findViewById(R.id.tv_ppw_item_text);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_ppw_item_check);

		tv.setText(datas.get(position));
		if (currnetSlected == position) {// 当先显示的状态要勾选出来
			iv.setVisibility(View.VISIBLE);
		} else {
			iv.setVisibility(View.GONE);
		}
		return view;
	}

	class ViewHolder {

	}

}
