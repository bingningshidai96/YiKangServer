package com.yikang.app.yikangserver.ui;

import java.util.ArrayList;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.SimpleFragmentPagerAdapter;
import com.yikang.app.yikangserver.bean.InviteCustomer;
import com.yikang.app.yikangserver.bean.PaintsData;
import com.yikang.app.yikangserver.fragment.InviteCustomerFragment;
import com.yikang.app.yikangserver.fragment.InviteCustomerFragment.Type;
import com.yikang.app.yikangserver.utils.DeviceUtils;

/**
 * 病患列表： 这个页面用来展示该用户推荐的客户列表。
 */
public class InviteCustomerListActivity extends BaseActivity implements
		OnCheckedChangeListener {
	public static final String TAG = "InviteCustomerListActivity";
	private ViewPager vpPager;
	private RadioGroup rgTabs;
	private int[] tabIds = new int[] {R.id.rb_inviteCustomer_item_tab_registed,
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

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) indicator
                .getLayoutParams();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        params.leftMargin = (int) (0.5*(screenWidth/tabIds.length)-(0.5*params.width));
        indicator.setLayoutParams(params);

		initFragment();
		SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(
				getFragmentManager(), fragmentList);
		rgTabs.setOnCheckedChangeListener(this);
		vpPager.setAdapter(adapter);
		//vpPager.setOffscreenPageLimit(2);

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
	private void moveIndicator(float position) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) indicator
                .getLayoutParams();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        params.leftMargin = (int) ((position+0.5)*(screenWidth/tabIds.length)-(0.5*params.width));

		indicator.setLayoutParams(params);
	}

	private void initFragment() {
		fragmentList = new ArrayList<>();
		fragmentList.add(InviteCustomerFragment.getInstance(Type.registered));
		fragmentList.add(InviteCustomerFragment.getInstance(Type.consumed));
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int index = getIdIndex(checkedId);
		vpPager.setCurrentItem(index);
	}

	/**
	 * 获取选中的id在RadioGroup中的的index
	 * @param checkedId
	 * @return
	 */
	private int getIdIndex(int checkedId) {
		for(int i =0 ;i<tabIds.length;i++){
			if(tabIds[i] == checkedId){
				return i;
			}
		}
		return  -1;
	}



	public void initCustomerNums(PaintsData data){
		((TextView) findViewById(R.id.tv_invite_sum)).setText(""+data.sum);
		((TextView) findViewById(R.id.tv_invite_register_num)).setText(""+data.registerNum);
		((TextView) findViewById(R.id.tv_invite_service_num)).setText(""+data.inServiceNum);
	}
}
