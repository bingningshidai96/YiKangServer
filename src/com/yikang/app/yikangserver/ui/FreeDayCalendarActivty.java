package com.yikang.app.yikangserver.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.CommonAdapter;
import com.yikang.app.yikangserver.adapter.ViewHolder;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.bean.ServiceScheduleData;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.utils.BuisNetUtils;
import com.yikang.app.yikangserver.utils.LOG;

public class FreeDayCalendarActivty extends BaseActivity implements
		OnItemClickListener {
	private static final int REQUEST_CODE_EDITTIME = 0x101;
	private static final String TAG = "FreeTimeCalendarActivty";
	/**
	 * 
	 */
	private GridView gvCarlendar;

	private ArrayList<ServiceScheduleData> data = new ArrayList<ServiceScheduleData>();
	private int updataPosition;
	private CommonAdapter<ServiceScheduleData> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar(getString(R.string.freeTime_title));
	}

	@Override
	protected void findViews() {
		gvCarlendar = (GridView) findViewById(R.id.gv_freeTimeCarlendar_carlendar);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_free_time_calendar);
	}

	@Override
	protected void getData() {
		showWatingDailog();
		final String url = UrlConstants.URL_GET_WORK_DAYS;
		RequestParam param = new RequestParam();
		Calendar calendar = Calendar.getInstance();
		param.add("year", calendar.get(Calendar.YEAR));
		param.add("month", calendar.get(Calendar.MONTH) + 1);
		BuisNetUtils.requestStr(url, param,
				new BuisNetUtils.ResponceCallBack() {

					@Override
					public void onSuccess(ResponseContent content) {
						dismissWatingDailog();
						String result = content.getData();
						LOG.i(TAG, "[getData]" + result);
						List<ServiceScheduleData> dataList = JSON.parseArray(
								result, ServiceScheduleData.class);
						data.addAll(dataList);
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
		gvCarlendar.setOnItemClickListener(this);
		adapter = new CommonAdapter<ServiceScheduleData>(this, data,
				R.layout.item_servier_data_textview) {
			@Override
			protected void convert(ViewHolder holder, ServiceScheduleData item) {
				TextView tvTitle = (TextView) holder.getConvertView();
				String format = "%02d/%02d";
				tvTitle.setText(String.format(format, item.serviceDay,item.serviceMonth));
				boolean isEditable = item.scheduleStatus == ServiceScheduleData.status_editable;
				// 如果不能编辑
				tvTitle.setEnabled(isEditable);
			}
		};
		gvCarlendar.setAdapter(adapter);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ServiceScheduleData schedule = data.get(position);
		updataPosition = position;
		Intent intent = new Intent(this, FreeTimeActivity.class);
		intent.putExtra(FreeTimeActivity.EXTRA_SERVICE_DATE,
				schedule.serviceDate);
		intent.putExtra(FreeTimeActivity.EXTRA_SERVICE_DATE_STR,
				schedule.serviceDayStr);
		startActivityForResult(intent, REQUEST_CODE_EDITTIME);
		// if(position==2){ /
		// ServiceScheduleData schedule = data.get(position);
		// updataPosition = position;
		// Intent intent = new Intent(this, EditTimeActivity.class);
		// intent.putExtra(EditTimeActivity.EXTRA_SERVICE_DATA,
		// schedule.serviceDate);
		// intent.putExtra(EditTimeActivity.EXTRA_IS_EDITEABLE, true);
		// startActivityForResult(intent, REQUEST_CODE_EDITTIME);
		// LOG.i(TAG, "[onItemClick]"+schedule.serviceDate);
		// }
		// LOG.i(TAG, "[onItemClick]"+position);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == REQUEST_CODE_EDITTIME && intent != null) {
			boolean isSuccess = intent.getBooleanExtra(
					FreeTimeActivity.EXTRA_IS_EDITE_SUCCESS, false);
			if (isSuccess) {
				data.get(updataPosition).scheduleStatus = ServiceScheduleData.status_selected;
				adapter.notifyDataSetChanged();
			}
		}
	}

}
