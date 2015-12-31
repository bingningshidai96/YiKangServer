package com.yikang.app.yikangserver.ui;

import java.util.ArrayList;
import java.util.List;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.SimpleFragmentPagerAdapter;
import com.yikang.app.yikangserver.fragment.ServiceTaskListFragment;
import com.yikang.app.yikangserver.fragment.ServiceTaskListFragment.Type;

public class ServiceCalendarActivity extends BaseActivity implements
		OnCheckedChangeListener {
	protected static final String TAG = "SeniorListActivity";
	private ViewPager viewpager;
	private View indicator;
	private RadioGroup rgTabs;
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	private int[] tabIds = new int[] { R.id.rb_service_calendar_tab_compelete,
			R.id.rb_service_calendar_tab_uncomplete };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar(getString(R.string.service_carlendar_title));
	}

	@Override
	protected void findViews() {
		viewpager = (ViewPager) findViewById(R.id.vp_service_calendar_pager);
		indicator = findViewById(R.id.view_service_calendar_indicator);
		rgTabs = (RadioGroup) findViewById(R.id.rg_service_calendar_tabs);
		rgTabs.setOnCheckedChangeListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_service_carlendar);
	}

	@Override
	protected void getData() {
	}

	@Override
	protected void initViewConent() {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicator
				.getLayoutParams();
		params.width = getResources().getDisplayMetrics().widthPixels
				/ tabIds.length;
		indicator.setLayoutParams(params);

		ServiceTaskListFragment incompleteFragment = ServiceTaskListFragment
				.getInstance(Type.Incomplete);
		ServiceTaskListFragment completeFragment = ServiceTaskListFragment
				.getInstance(Type.Complete);
		fragmentList.add(incompleteFragment);
		fragmentList.add(completeFragment);
		viewpager.setAdapter(new SimpleFragmentPagerAdapter(
				getFragmentManager(), fragmentList));
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				rgTabs.check(tabIds[position]);
			}

			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				moveIndicator(positionOffset + position);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	/**
	 * 移动指示器的位置
	 */
	private void moveIndicator(float positon) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicator
				.getLayoutParams();
		int width = rgTabs.getWidth() / tabIds.length;
		params.leftMargin = (int) (positon * width);
		indicator.setLayoutParams(params);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int index = getIdIndex(checkedId);
		viewpager.setCurrentItem(index);
	}

	private int getIdIndex(int checkedId) {
		switch (checkedId) {
		case R.id.rb_service_calendar_tab_compelete:
			return 0;
		case R.id.rb_service_calendar_tab_uncomplete:
			return 1;
		default:
			return 0;
		}
	}
}
