package com.yikang.app.yikangserver;

import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.adapter.CommonAdapter;
import com.yikang.app.yikangserver.adapter.ViewHolder;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.utils.BuisNetUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ServiceCalendarActivity extends BaseActivity implements OnItemClickListener{
	private static final String TAG = "ServiceCalendarActivity";
	private ListView lvServiceCalendar;
	private List<Item> data = new ArrayList<Item>();
	private CommonAdapter<Item> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar("你的服务日程");
	}

	@Override
	protected void findViews() {
		lvServiceCalendar = (ListView) findViewById(R.id.lv_service_calendar_list);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activty_service_calendar);
		
	}

	@Override
	protected void getData() {
		showWatingDailog();
		String url = "";
		RequestParam param = new RequestParam();
		BuisNetUtils.requestStr(url, param, new BuisNetUtils.ResponceCallBack() {
			@Override
			public void onSuccess(ResponseContent content) {
				dismissWatingDailog();
				String result = content.getData();
				Log.i(TAG, "[getData]"+result);
				List<Item> list = JSON.parseArray(result, Item.class);
				data.addAll(list);
				adapter.notifyDataSetChanged();
			}
			@Override
			public void onFialure(String status, String message) {
				dismissWatingDailog();
				AppContext.showToast(message);
			}
		});
	}

	@Override
	protected void initViewConent() {
		adapter = new CommonAdapter<Item>(this,data,R.layout.item_service_calendar){
			@Override
			protected void convert(ViewHolder holder, Item item) {
				TextView tvName = holder.getView(R.id.tv_service_carlendar_name);
				TextView tvAddr = holder.getView(R.id.tv_service_carlendar_addr);
				TextView tvDate = holder.getView(R.id.tv_service_carlendar_date);
				tvAddr.setText(item.addr);
				tvDate.setText(item.date);
				tvName.setText(item.name);
			}
		};
		lvServiceCalendar.setAdapter(adapter);
		lvServiceCalendar.setOnItemClickListener(this);
		
		for(int i=0;i<10;i++){
			Item item = new Item();
			item.id = "id"+i;
			item.addr ="北京"+i;
			item.name = "汪九千岁"+i;
			item.date = "2015/09/0"+i;
			data.add(item);
		}
		adapter.notifyDataSetChanged();
		
	}
    
	private static class Item{
		public String id;
		public String name;
		public String date;
		public String addr;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View parent, int position,
			long id) {
		final String itemId = data.get(position).id;
		Intent intent = new Intent(this, ScheduleItemActivty.class);
		intent.putExtra(ScheduleItemActivty.EXTRA_ITEM_ID, itemId);
		startActivity(intent);
	}

}
