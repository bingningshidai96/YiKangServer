package com.yikang.app.yikangserver.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.application.AppConfig;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.fragment.BusinessMainFragment;
import com.yikang.app.yikangserver.fragment.MineFragment;
import com.yikang.app.yikangserver.reciever.UserInfoAlteredReceiver;
import com.yikang.app.yikangserver.utils.DeviceUtils;
import com.yikang.app.yikangserver.utils.DoubleClickExitHelper;
import com.yikang.app.yikangserver.utils.LOG;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class MainActivity extends BaseActivity implements OnCheckedChangeListener{
	protected static final String TAG = "MainActivity";
	private DoubleClickExitHelper mExitHelper; // 双击退出
	private RadioGroup rgTabs;//
	private UserInfoAlteredReceiver receiver;

	private int currentCheck; // 当前选中的tab
	private ArrayList<Fragment> fragList = new ArrayList<Fragment>();

	/**
	 * 注册设备handler
	 */
	private ResponseCallback<Void> registerDeviceHandler=  new ResponseCallback<Void>() {
		@Override
		public void onSuccess(Void data) {
			AppConfig appConfig = AppConfig.getAppConfig(getApplicationContext());
			appConfig.setProperty(AppConfig.CONF_IS_DEVICE_REGISTED, "" + 1); // 将设备设置为注册成功
			if (AppContext.DEBUG) {
				String log = appConfig.getProperty(AppConfig.CONF_DEVICE_ID_TYPE) + "******"
						+ appConfig.getProperty(AppConfig.CONF_DEVICE_ID);
				LOG.e(TAG, "[registerDeviceHandler]" + log);
			}
			LOG.e(TAG, "[registerDeviceHandler]device register success!!");
		}

		@Override
		public void onFailure(String status, String message) {
			LOG.d(TAG, "[registerDeviceHandler]sorry,device register fail.error message:" + message);
		}
	};

	/**
	 * 加载用户信信息回调
	 */
	private ResponseCallback<User> loadUserInfoHandler = new ResponseCallback<User>(){
		@Override
		public void onSuccess(User user) {
			findViewById(R.id.main_load_error).setVisibility(View.GONE);
			hideWaitingUI();
			if (user == null || user.userId == null) {
				logout();
			}
			AppContext.getAppContext().login(user);
			hideLoadError();
			initViewsAfterGetInfo();
		}

		@Override
		public void onFailure(String status, String message) {
			LOG.i(TAG,"[loadUserInfo]加载失败"+message);
			hideWaitingUI();
			AppContext.showToast(message);
			showLoadError();
		}
	};


	/**
	 * 获取推送别名的回调
	 */
	private ResponseCallback<Map<String,String>> getPushAliasHandler = new ResponseCallback<Map<String,String>>() {
		@Override
		public void onSuccess(Map<String,String> map) {
			LOG.w(TAG, "[requestAndSetJPushAlias]获取别名成功" + map);
			String alias = map.get("alias");
			if (!TextUtils.isEmpty(alias)) {
				setJPushAlias(alias);
			}
		}

		@Override
		public void onFailure(String status, String message) {
			LOG.w(TAG, "[requestAndSetJPushAlias]sorry! get alias fail.error message:" + message);
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(UserInfoAlteredReceiver.ACTION_USER_INFO_ALTERED);
		receiver = new UserInfoAlteredReceiver();
		registerReceiver(receiver, filter);
		initContent();
		initTitleBar(getString(R.string.buisness_titile));
		mExitHelper = new DoubleClickExitHelper(this);
		if(!AppContext.getAppContext().isDeviceRegisted()){
			registerDevice(); // 注册设备
		}
		requestAndSetJPushAlias(); // 设置极光推送的别名,别名是跟用户绑定在一起的
	}
	

	@Override
	protected void findViews() {
		rgTabs = (RadioGroup) findViewById(R.id.rg_main_tabs);
		rgTabs.setOnCheckedChangeListener(this);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void getData() {}

	@Override
	protected void initViewContent() {
		refreshFragments();
		// 手动调用选中第一个
		RadioButton rb = (RadioButton) findViewById(R.id.rb_main_business);
		rb.setChecked(true);
	}
	
	/**
	 * 加载用户信息刷新界面
	 */
	private void refreshFragments(){
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		for (Fragment fragment : fragList) {
			ft.remove(fragment);
		}
		ft.commit();
		fragList.clear();
		loadUserInfo();
	}

	@Override
	protected void onStart() {
		super.onStart();
		//当用户信息发生改变时，重新加载页面
		if(receiver.getAndConsume()){
			refreshFragments(); 
		}
	}

	
	
	/**
	 * 向服务器请求注册设备
	 */
	private void registerDevice() {
		int codeType = AppContext.getAppContext().getDeviceIdType();
		String deviceCode = AppContext.getAppContext().getDeviceID();
		Api.registerDevice(codeType, deviceCode,registerDeviceHandler);
	}


	
	/**
	 * 获取数据
	 */
	private void loadUserInfo() {
		showWaitingUI();
		Api.getUserInfo(loadUserInfoHandler);
	}


	private void hideLoadError(){
		findViewById(R.id.main_load_error).setVisibility(View.GONE);
	}

	private void showLoadError(){
		findViewById(R.id.main_load_error).setVisibility(View.VISIBLE);
		TextView tvTips = ((TextView) findViewById(R.id.tv_error_tips));
		tvTips.setText(R.string.default_network_data_fail_describe);
	}


	/**
	 * init views after loading data;
	 */
	private void initViewsAfterGetInfo() {
		// 将fragment添加到MainActivity中
		Fragment busiFragment = new BusinessMainFragment();
		Fragment mineFragment = new MineFragment();

		addTabsFragment(busiFragment, String.valueOf(R.id.rb_main_business));
		addTabsFragment(mineFragment, String.valueOf(R.id.rb_main_mine));
	}



	/**
	 * 将fragment天骄到主页面中，tag设置为id
	 */
	private void addTabsFragment(Fragment fragment, String tag) {
		fragList.add(fragment);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.fl_main_container, fragment, tag);
		if (!String.valueOf(currentCheck).equals(tag)) {
			ft.hide(fragment);
		}
		ft.commit();
	}



	/**
	 * 点击之后，更改当前显示的fragment
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		final String lastTag = String.valueOf(currentCheck);
		final String currentTag = String.valueOf(checkedId);
		currentCheck = checkedId;
		changeTitleBar();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		for (Fragment fragment : fragList) {
			if (lastTag.equals(fragment.getTag())) {
				ft.hide(fragment);
			} else if (currentTag.equals(fragment.getTag())) {
				ft.show(fragment);
			}
		}
		ft.commit();
	}



	/**
	 * 在fragment切换时需要改变标题栏
	 */
	protected void changeTitleBar() {
		if (currentCheck == R.id.rb_main_business) {
			findViewById(R.id.ibtn_title_back).setVisibility(View.GONE);
			TextView tvRight = (TextView) findViewById(R.id.tv_title_right_text);
			tvRight.setText("");
			super.initTitleBar(getString(R.string.buisness_titile));
		} else if (currentCheck == R.id.rb_main_mine) {
			findViewById(R.id.ibtn_title_back).setVisibility(View.GONE);
			TextView tvTitle = (TextView) findViewById(R.id.tv_title_text);
			tvTitle.setText(R.string.mine_title);
			TextView tvRight = (TextView) findViewById(R.id.tv_title_right_text);
			tvRight.setText(R.string.mine_right_logout);
			tvRight.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					logoutDialog();
				}
			});
		}
	}

	
	
	/**
	 * 登出
	 */
	private void logoutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_title_prompt)
				.setMessage(R.string.logout_dialog_message)
				.setNegativeButton(R.string.cancel, null)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								logout();
							}
						}).create().show();
	}



	/**
	 * 注销用户
	 */
	private void logout(){
		AppContext.getAppContext().logout();
		Intent intent = new Intent(MainActivity.this,LoginActivity.class);
		startActivity(intent);
		finish();
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 是否退出应用
			if (AppContext.get(AppConfig.KEY_DOUBLE_CLICK_EXIT, true)) {
				return mExitHelper.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}



	@Override
	protected void onStop() {
		super.onStop();
		if (mHandler.hasMessages(MSG_SET_ALIAS)) {
			mHandler.removeMessages(MSG_SET_ALIAS);
		}
		if (mHandler.hasMessages(MSG_SET_TAGS)) {
			mHandler.removeMessages(MSG_SET_TAGS);
		}

	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
		unregisterReceiver(receiver);
	}





	/*******以下是从服务器获取标识，病设置给极光推送别名的代码******/

	/**
	 * 获取别名,获取成功后设置别名
	 */
	private void requestAndSetJPushAlias() {
		Api.getPushAlias(getPushAliasHandler);
	}



	private void setJPushAlias(String alias) {
		if (TextUtils.isEmpty(alias)) {
			AppContext.showToast(R.string.error_alias_empty);
			LOG.w(TAG, "[setJPushAlias]alias parament is empty");
			return;
		}
		if (!isValidTagAndAlias(alias)) {
			AppContext.showToast(R.string.error_tag_gs_empty);
			LOG.w(TAG, "[setJPushAlias]alias parament is invalid");
			return;
		}
		// 调用JPush API设置Alias
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
	}



	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg){
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SET_ALIAS:
				LOG.d(TAG, "[mHandler.handleMessage]Set alias in handler."
						+ msg.obj);
				JPushInterface.setAliasAndTags(getApplicationContext(),
						(String) msg.obj, null, mAliasCallback);
				break;

			case MSG_SET_TAGS:
				LOG.d(TAG, "[mHandler.handleMessage]Set tags in handler.");
				JPushInterface.setAliasAndTags(getApplicationContext(), null,
						(Set<String>) msg.obj, mTagsCallback);
				break;
			default:
				LOG.i(TAG, "[mHandler.handleMessage]Unhandled msg - "
						+ msg.what);
			}
		}
	};


	/**
	 * 设置别名的回调
	 */
	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
		int count = 0;

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				count = 0;
				logs = "Set tag and alias success";
				LOG.i(TAG, "[mAliasCallback.gotResult]:" + logs);
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				LOG.i(TAG, "[mAliasCallback.gotResult]:" + logs);
				count++;
				if (DeviceUtils.checkNetWorkIsOk()
						&& count < 10) {
					mHandler.sendMessageDelayed(
							mHandler.obtainMessage(MSG_SET_ALIAS, alias),
							1000 * 10);
				} else {
					LOG.i(TAG,
							"[mAliasCallback.gotResult]:No network.set alias fails");
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Log.e(TAG, "[mAliasCallback.gotResult]:" + logs);
			}
		}
	};


	/**
	 * 设置标签的回调
	 */
	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				LOG.i(TAG, "[mTagsCallback.gotResult]" + logs);
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				LOG.i(TAG, "[mTagsCallback.gotResult]" + logs);
				if (DeviceUtils.checkNetWorkIsOk()) {
					mHandler.sendMessageDelayed(
							mHandler.obtainMessage(MSG_SET_TAGS, tags),
							1000 * 60);
				} else {
					LOG.i(TAG, "[mTagsCallback.gotResult]:No network");
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				LOG.w(TAG, "[mTagsCallback.gotResult]" + logs);
			}
			AppContext.showToast(logs);
		}

	};



	/**
	 * 校验Tag Alias 只能是数字,英文字母和中文
	 */
	public boolean isValidTagAndAlias(String s) {
		Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
		Matcher m = p.matcher(s);
		return m.matches();
	}



	private void setTag(ArrayList<String> tags) {
		// ","隔开的多个 转换成 Set
		Set<String> tagSet = new LinkedHashSet<String>();
		tagSet.addAll(tags);
		for (String tag : tags) {
			if (!isValidTagAndAlias(tag)) {
				AppContext.showToast(R.string.error_tag_gs_empty);
				return;
			}
			tagSet.add(tag);
		}

		// 调用JPush API设置Tag
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));

	}

}
