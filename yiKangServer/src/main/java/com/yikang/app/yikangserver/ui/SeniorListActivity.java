package com.yikang.app.yikangserver.ui;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.fragment.SeniorListFragment;
import com.yikang.app.yikangserver.reciever.UserInfoAltedRevicer;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
/**
 * 服务对象列表
 *
 */
public class SeniorListActivity extends BaseActivity {
	protected static final String TAG = "SeniorListActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar(getString(R.string.senior_list_title_text));
	}

	@Override
	protected void findViews() {
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_senior_list);
	}

	@Override
	protected void getData() {
	}

	@Override
	protected void initViewConent() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fl_senoirList_container, new SeniorListFragment());
		ft.commit();
	}

	
	
	@Override
	protected void initTitleBar(String title) {
		View view = findViewById(R.id.titlebar);
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_text);
		tvTitle.setText(title);
		TextView tvRight = (TextView) findViewById(R.id.tv_title_right_text);
		tvRight.setText(getResources().getText(R.string.title_bar_add));
		tvRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SeniorListActivity.this,
						SeniorInfoActivity.class);
				startActivity(intent);
			}
		});

		ImageButton btBack = (ImageButton) findViewById(R.id.ibtn_title_back);
		if (btBack != null) // 设置返回监听
			btBack.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
	}

}
