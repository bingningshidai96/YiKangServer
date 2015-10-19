package com.yikang.app.yikangserver;

import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.adapter.CommonAdapter;
import com.yikang.app.yikangserver.adapter.ViewHolder;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.bean.SeniorInfo;
import com.yikang.app.yikangserver.data.ConstantData;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.utils.HttpUtils;
import com.yikang.app.yikangserver.utils.LOG;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SeniorListActivity extends BaseActivity implements OnItemClickListener{
	protected static final String TAG = "SeniorListActivity";
	private ListView lvNames;
	private ArrayList<SeniorInfo> seniorList = new ArrayList<SeniorInfo>();;
	private MyAdapter adapter;
	private TextView tvTips;
	private boolean hasDataChanged = false;
	private DataChageReciver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(ConstantData.ACTION_BROADCAST_ADD_SENOIR);
		receiver = new DataChageReciver();
		registerReceiver(receiver, filter);
		adapter = new MyAdapter(this,seniorList);
		initContent();
		initTitleBar(getString(R.string.senior_list_title_text));
	}
		
	
	protected void initTitleBar(String title) {
		View view = findViewById(R.id.titlebar);
		view.findViewById(R.id.ibtn_title_back).setVisibility(View.GONE);
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_text);
		tvTitle.setText(title);
		
		TextView tvRight =(TextView) findViewById(R.id.tv_title_right_text);
		tvRight.setText(getResources().getText(R.string.title_bar_add));
		tvRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SeniorListActivity.this, SeniorInfoActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void findViews() {
		tvTips = (TextView) findViewById(R.id.tv_fragment_buisness_no_content_tip);
		lvNames = (ListView)findViewById(R.id.lv_senior_list);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_senior_list);
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, EvaluationRecordActivity.class);
		Log.d(TAG, "[onItemClick]"+seniorList.get(position).getSeniorId());
		intent.putExtra("seniorId", seniorList.get(position).getSeniorId());
		startActivity(intent);
	}
	
	@Override
	protected void getData() {
		showWatingDailog();
		RequestParam params = new RequestParam();
		HttpUtils.requestPost(UrlConstants.URL_GET_SENIOR_LIST, params.toParams(), new HttpUtils.ResultCallBack() {
			@Override 
			public void postResult(String result) {
				try {
					dismissWatingDailog();
					ResponseContent content = ResponseContent.toResposeContent(result);
					if(ResponseContent.STATUS_OK.equals(content.getStatus())){
						hasDataChanged = false;
						String json = content.getData();
						if(!TextUtils.isEmpty(json)){
							tvTips.setVisibility(View.GONE);
							seniorList.clear();
							List<SeniorInfo> list = JSON.parseArray(json, SeniorInfo.class);
							if(list==null||list.isEmpty()){
								tvTips.setVisibility(View.VISIBLE);
							}
							seniorList.addAll(list);
							adapter.notifyDataSetChanged();
						}else{
							tvTips.setVisibility(View.VISIBLE);
							LOG.d(TAG, "[getData]什么都没有");
						}
					}
				} catch (Exception e) {e.printStackTrace();}
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		checkRefresh();
	}
	
	
	@Override
	protected void initViewConent() {
		lvNames.setAdapter(adapter);
		lvNames.setOnItemClickListener(this);
	}
	/**
	 * 检查是否有更新
	 */
	private void checkRefresh(){
		if(hasDataChanged){
			getData();
		}
	}
	
	
	class MyAdapter extends CommonAdapter<SeniorInfo>{

		public MyAdapter(Context context, List<SeniorInfo> datas) {
			super(context, datas, R.layout.item_senoir);
		}

		@Override
		protected void convert(ViewHolder holder, SeniorInfo item) {
			TextView tvName = holder.getView(R.id.tv_item_senior_name);
			tvName.setText(item.getName());
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	private class DataChageReciver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			hasDataChanged = true;
			LOG.i(TAG, "接受到广播：数据改变");
		}
	}
}
