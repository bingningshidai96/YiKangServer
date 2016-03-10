package com.yikang.app.yikangserver.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.CommonAdapter;
import com.yikang.app.yikangserver.adapter.ViewHolder;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.ServiceScheduleData;

import java.util.ArrayList;
import java.util.List;

public class FreeDayCalendarActivity extends BaseActivity implements
		OnItemClickListener {
	private static final String TAG = "FreeDayCalendarActivity";
	private static final int REQUEST_CODE_EDIT_TIME = 0x101;
	private GridView gvCalendar;


	private ArrayList<ServiceScheduleData> data = new ArrayList<>();
	private int updatePosition;
	private CommonAdapter<ServiceScheduleData> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar(getString(R.string.freeTime_title));
	}

	@Override
	protected void findViews() {
		gvCalendar = (GridView) findViewById(R.id.gv_freeTimeCarlendar_carlendar);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_free_time_calendar);
	}

	@Override
	protected void getData() {
		showWaitingUI();
		Api.getFreeDays(new ResponseCallback<List<ServiceScheduleData>>() {
			@Override
			public void onSuccess(List<ServiceScheduleData> list) {
				hideWaitingUI();
				data.clear();
				data.addAll(list);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(String status, String message) {
				hideWaitingUI();
				AppContext.showToast(message);
			}
		});

	}

	@Override
	protected void initViewContent() {
		gvCalendar.setOnItemClickListener(this);
		adapter = new CommonAdapter<ServiceScheduleData>(this, data,
				R.layout.item_servier_data_textview) {
			@Override
			protected void convert(ViewHolder holder, ServiceScheduleData item) {
				TextView tvTitle = (TextView) holder.getConvertView();
				String format = "%02d.%02d";
				tvTitle.setText(String.format(format, item.serviceMonth,item.serviceDay));
				boolean isEditable = item.scheduleStatus == ServiceScheduleData.status_editable;
				// 如果不能编辑
				tvTitle.setEnabled(isEditable);
			}
		};
		gvCalendar.setAdapter(adapter);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ServiceScheduleData schedule = data.get(position);
		updatePosition = position;
		Intent intent = new Intent(this, FreeTimeActivity.class);
		intent.putExtra(FreeTimeActivity.EXTRA_SERVICE_DATE,
				schedule.serviceDate);
		intent.putExtra(FreeTimeActivity.EXTRA_SERVICE_DATE_STR,
				schedule.serviceDayStr);
		startActivityForResult(intent, REQUEST_CODE_EDIT_TIME);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == REQUEST_CODE_EDIT_TIME && intent != null) {
			boolean isSuccess = intent.getBooleanExtra(
					FreeTimeActivity.EXTRA_IS_EDIT_SUCCESS, false);
			if (isSuccess) {
				data.get(updatePosition).scheduleStatus = ServiceScheduleData.status_selected;
				adapter.notifyDataSetChanged();
			}
		}
	}

}
