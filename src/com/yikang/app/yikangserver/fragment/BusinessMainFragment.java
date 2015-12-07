package com.yikang.app.yikangserver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.ui.InviteCustomerListActivity;
import com.yikang.app.yikangserver.ui.SeniorListActivity;
import com.yikang.app.yikangserver.ui.ServiceCalendarActivity;

public class BusinessMainFragment extends BaseFragment implements
		OnClickListener {
	protected static final String TAG = "BusinessFragment";

	private boolean hasDataChanged = false;

	// private NotifyEntity notifyEntity;

	private User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = AppContext.getAppContext().getUser();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_buisness,
				container, false);
		LinearLayout lyCustomerList = (LinearLayout) view
				.findViewById(R.id.ly_buisness_item_customerlist);
		LinearLayout lyCalendar = (LinearLayout) view
				.findViewById(R.id.ly_buisness_service_calendar);

		LinearLayout lyNursedList = (LinearLayout) view
				.findViewById(R.id.ly_buisness_item_nursedlist);
		// TODO 记得取消注释
		 if(user.profession == MyData.DOCTOR){
			 lyCalendar.setVisibility(View.GONE);
			 lyNursedList.setVisibility(View.GONE);
		 }

		lyCustomerList.setOnClickListener(this);
		lyCalendar.setOnClickListener(this);
		lyNursedList.setOnClickListener(this);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		checkRefresh();
	}

	/**
	 * 检查是否有更新
	 */
	private void checkRefresh() {
		if (hasDataChanged) {
			getData();
		}
	}

	protected void getData() {
	}

	// private class DataChageReciver extends BroadcastReceiver{
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// final String action = intent.getAction();
	// if(notifyEntity == null){
	// notifyEntity = new NotifyEntity();
	// }
	// if(ConstantData.ACTION_BROADCAST_ADD_SENOIR.equals(action)){
	// final int newSenor = intent.getIntExtra("newSeniorCount", 0);
	// notifyEntity.newSenior += newSenor;
	//
	// }else if(ConstantData.ACTION_BROADCAST_NEW_SERVICE_TIEM.equals(action)){
	// final int newService = intent.getIntExtra("newServiceCount", 0);
	// notifyEntity.newSerivce += newService;
	// }
	// hasDataChanged = true;
	// LOG.i(TAG, "接受到广播：数据改变");
	// }
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ly_buisness_item_customerlist:
			toCustomerListPage();
			break;
		case R.id.ly_buisness_service_calendar:
			toServiceCalendarPage();
			break;
		case R.id.ly_buisness_item_nursedlist:
			toNursedListPage();
			break;
		default:
			break;
		}

	}

	private void toNursedListPage() {
		Intent intent = new Intent(getActivity(), SeniorListActivity.class);
		startActivity(intent);
	}

	private void toServiceCalendarPage() {
		Intent intent = new Intent(getActivity(), ServiceCalendarActivity.class);
		startActivity(intent);
	}

	private void toCustomerListPage() {
		Intent intent = new Intent(getActivity(),
				InviteCustomerListActivity.class);
		startActivity(intent);
	}
}
