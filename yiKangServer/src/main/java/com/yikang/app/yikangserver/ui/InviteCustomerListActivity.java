package com.yikang.app.yikangserver.ui;

import java.util.ArrayList;
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
import com.yikang.app.yikangserver.fragment.InviteCustomerFragment;
import com.yikang.app.yikangserver.fragment.InviteCustomerFragment.Type;

/**
 * 病患列表： 这个页面用来展示该用户推荐的客户列表。
 */
public class InviteCustomerListActivity extends BaseActivity implements
		OnCheckedChangeListener {
	public static final String TAG = "InviteCustomerListActivity";
	private ViewPager vpPager;
	private RadioGroup rgTabs;
	private int[] tabIds = new int[] { R.id.rb_inviteCustomer_item_tab_all,
			R.id.rb_inviteCustomer_item_tab_registed,
			R.id.rb_inviteCustomer_item_tab_consumed };
	private ArrayList<Fragment> fragmentList;
	private View indicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar(getString(R.string.customerList_title));
	}

	@Override
	protected void findViews() {
		vpPager = (ViewPager) findViewById(R.id.vp_inviteCustomer_item_pager);
		rgTabs = (RadioGroup) findViewById(R.id.rg_inviteCustomer_tabs);
		indicator = findViewById(R.id.view_inviteCustomer_item_indicator);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_invite_customer);
	}

	@Override
	protected void getData() {}

	@Override
	protected void initViewContent() {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) indicator
				.getLayoutParams();
		params.width = getResources().getDisplayMetrics().widthPixels
				/ tabIds.length;
		indicator.setLayoutParams(params);
		initFragment();
		SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(
				getFragmentManager(), fragmentList);
		rgTabs.setOnCheckedChangeListener(this);
		vpPager.setAdapter(adapter);
		vpPager.setOffscreenPageLimit(2);


		vpPager.setOnPageChangeListener(new OnPageChangeListener() {
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

	private void initFragment() {
		fragmentList = new ArrayList<>();
		fragmentList.add(InviteCustomerFragment.getInstance(Type.all));
		fragmentList.add(InviteCustomerFragment.getInstance(Type.registed));
		fragmentList.add(InviteCustomerFragment.getInstance(Type.consumed));
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int index = getIdIndex(checkedId);
		vpPager.setCurrentItem(index);
	}

	private int getIdIndex(int checkedId) {
		switch (checkedId) {
		case R.id.rb_inviteCustomer_item_tab_all:
			return 0;
		case R.id.rb_inviteCustomer_item_tab_registed:
			return 1;
		case R.id.rb_inviteCustomer_item_tab_consumed:
			return 2;
		default:
			return 0;
		}
	}

}
