package com.yikang.app.yikangserver.adapter;

import java.util.List;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.bean.EvalutionRecord;

public class RecordAdapter extends BaseAdapter{
	private List<EvalutionRecord> data;
	private LayoutInflater inflater;
	
	public RecordAdapter(List<EvalutionRecord> data, Activity context) {
		super();
		this.data = data;
		inflater = LayoutInflater.from(context);
			
	}

	@Override
	public int getCount() {
		return data==null?0:data.size()+1;
	}

	@Override
	public Object getItem(int position) {
		return position!=data.size()?data.get(position):null;
	}

	@Override
	public long getItemId(int position) {
		if(data==null||data.size()==position){
			return 0;
		}
		return data.get(position).getAssessmentId();
	}

	@Override
	public int getItemViewType(int position) {
		if(position == data.size()){
			return 1;
		}
		return 0;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		if(convertView==null){
			convertView = createView(type,parent);
		}
		fillView(convertView, position, type);
		return convertView;
	}
	
	private View createView(int type,ViewGroup parent){
		View v;
		if(type==0){
			v = inflater.inflate(R.layout.item_eval_record, parent,false);
		}else{
			v = inflater.inflate(R.layout.item_eval_record_new, parent,false);
		}
		return v;
		
	}
	
	private void fillView(View v,int position,int type){
		if(type==0){
			TextView tvTime = (TextView) v.findViewById(R.id.tv_item_record_time);
			//由于服务器暂时返回的时间是错误的时间，故截取一段
			String name = data.get(position).getAssessmentName();
			if(!TextUtils.isEmpty(name)){
				tvTime.setText(data.get(position).getAssessmentName().substring(0,10));
			}
			
			TextView tvCount = (TextView) v.findViewById(R.id.tv_item_record_count);
			tvCount.setText("第"+(data.size()-position)+"次评估");
		}else{
//			ImageView ibtnNew = (ImageView) v.findViewById(R.id.ibtn_item_record_new);
//			ibtnNew.setOnClickListener(listener);
		}
	}

}
