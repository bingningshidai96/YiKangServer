package com.yikang.app.yikangserver;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.yikang.app.yikangserver.fragment.RegisterStepOneFragment;
import com.yikang.app.yikangserver.fragment.RegisterStepOneFragment.OnNextListener;
import com.yikang.app.yikangserver.fragment.RegisterStepTwoFragemt;
import com.yikang.app.yikangserver.utils.LOG;

public class RegistActivtiy extends BaseActivity implements OnNextListener{
	private static final String TAG = "RegistActivtiy";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar("注册");
	}
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_register);
	}

	@Override
	protected void findViews() {}

	@Override
	protected void getData() {}

	@Override
	protected void initViewConent() {
		Fragment fragment = new RegisterStepOneFragment();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fl_register_fragment_container, 
				fragment).commit();
		//next("17801092532","123456");
	}
	
	@Override
	public void next(String userName, String passw) {
		LOG.i(TAG, "[next]======");
		Fragment fragment = new RegisterStepTwoFragemt();
		Bundle bundle = new Bundle(); 
		bundle.putString("userName", userName);
		bundle.putString("passw", passw);
		fragment.setArguments(bundle);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fl_register_fragment_container, 
				fragment);
		ft.addToBackStack("toTepTwo");
		ft.commit();
	}
	
	

}
