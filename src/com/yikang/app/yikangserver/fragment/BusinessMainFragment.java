package com.yikang.app.yikangserver.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.SeniorListActivity;
import com.yikang.app.yikangserver.ServiceCalendarActivity;
import com.yikang.app.yikangserver.bean.NotifyEntity;
import com.yikang.app.yikangserver.data.ConstantData;
import com.yikang.app.yikangserver.utils.LOG;

public class BusinessMainFragment extends BaseFragment implements OnClickListener{
	protected static final String TAG = "BusinessFragment";
	private boolean hasDataChanged = false;
	private DataChageReciver receiver;
	
	private NotifyEntity notifyEntity;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(ConstantData.ACTION_BROADCAST_NEW_SENIOR);
		filter.addAction(ConstantData.ACTION_BROADCAST_NEW_SENIOR);
		receiver = new DataChageReciver();
		getActivity().registerReceiver(receiver, filter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_buisness, container,false);
		LinearLayout lyBuisness = 
				(LinearLayout) view.findViewById(R.id.ly_buisness_item_senorlist);
		LinearLayout lyCalendar = 
				(LinearLayout) view.findViewById(R.id.ly_buisness_service_calendar);
		
		lyBuisness.setOnClickListener(this);
		lyCalendar.setOnClickListener(this);
		initTitleBar("业务流程", view);
		return view;
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		checkRefresh();
	}
	
	/**
	 * 检查是否有更新
	 */
	private void checkRefresh(){
		if(hasDataChanged){
			getData();
		}
	}
	 

	
	protected void initTitleBar(String title,View contentView) {
		TextView tvTitle = (TextView) contentView.findViewById(R.id.tv_title_text);
		LOG.i(TAG, ""+(tvTitle==null));
		tvTitle.setText(title);
		contentView.findViewById(R.id.ibtn_title_back).setVisibility(View.GONE);
	}
	
	protected void getData() {}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
	}
	
	private class DataChageReciver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if(notifyEntity == null){
				notifyEntity = new NotifyEntity();
			}
			if(ConstantData.ACTION_BROADCAST_ADD_SENOIR.equals(action)){
				final int newSenor = intent.getIntExtra("newSeniorCount", 0);
				notifyEntity.newSenior += newSenor;
				
			}else if(ConstantData.ACTION_BROADCAST_NEW_SERVICE_TIEM.equals(action)){
				final int newService = intent.getIntExtra("newServiceCount", 0);
				notifyEntity.newSerivce += newService;
			}
			hasDataChanged = true;
			LOG.i(TAG, "接受到广播：数据改变");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ly_buisness_item_senorlist:
			toSeniorListPage();
			break;
		case R.id.ly_buisness_service_calendar:
			toServiceCalendarPage();
			break;
		default:
			break;
		}
		
	}
	
	private void toServiceCalendarPage() {
		Intent intent = new Intent(getActivity(), ServiceCalendarActivity.class);
		startActivity(intent);
	}

	private void toSeniorListPage(){
		Intent intent = new Intent(getActivity(), SeniorListActivity.class);
		startActivity(intent);
	}
}
