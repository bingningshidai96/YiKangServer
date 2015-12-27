package com.yikang.app.yikangserver.ui;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppConfig;
import com.yikang.app.yikangserver.application.AppContext;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 *第一次开机的引导页
 */
public class GuideActivity extends BaseActivity{
	private ViewPager guidePager;	
	private int[] imgIds = new int[] { R.drawable.guide1, R.drawable.guide2,
			R.drawable.guide3, R.drawable.guide4 };
	private ImageView[] imgViews;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
	}
	
	@TargetApi(19)
	protected void initContent() {
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			initStatusBar(getResources().getColor(R.color.transparent));
		}
		setContentView();
		findViews();
		getData();
		initViewConent();
	}

	@Override
	protected void initTitleBar(String title) {
		guidePager = (ViewPager) findViewById(R.id.vp_guide_pager);
	}
	
	@Override
	protected void findViews() {
		guidePager = (ViewPager) findViewById(R.id.vp_guide_pager);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_guide);
	}

	@Override
	protected void getData() {}

	@Override
	protected void initViewConent() {
		imgViews = new ImageView[imgIds.length];
		for (int i=0;i<imgIds.length;i++) {
			imgViews[i] = new ImageView(this);
			imgViews[i].setScaleType(ScaleType.FIT_XY);
			imgViews[i].setImageResource(imgIds[i]);
		}
		imgViews[imgViews.length-1].setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AppContext.set(AppConfig.PRE_APP_FIRST_RUN, false);
				startNextPage();
				finish();
			}
		});
		GuideAdapter adapter = new GuideAdapter();
		guidePager.setAdapter(adapter);
		
	}
	
	private void startNextPage() {
	   if (AppContext.getAppContext().getAccessTicket() == null) {
			Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
			startActivity(intent);
		}else {
			Intent intent = new Intent(GuideActivity.this, MainActivity.class);
			startActivity(intent);
		}
	}
	
	
	class GuideAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return imgViews.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view==object;
		}
		
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(imgViews[position]);
			return imgViews[position];
		}
		
		
	}
	
}
