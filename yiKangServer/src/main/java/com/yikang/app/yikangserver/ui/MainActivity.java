package com.yikang.app.yikangserver.ui;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppConfig;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.api.RequestParam;
import com.yikang.app.yikangserver.api.ResponseContent;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.fragment.BusinessMainFragment;
import com.yikang.app.yikangserver.fragment.MineFragment;
import com.yikang.app.yikangserver.reciever.UserInfoAltedRevicer;
import com.yikang.app.yikangserver.api.ApiClient;
import com.yikang.app.yikangserver.api.ApiClient.ResponceCallBack;
import com.yikang.app.yikangserver.utils.DeviceUtils;
import com.yikang.app.yikangserver.utils.DoubleClickExitHelper;
import com.yikang.app.yikangserver.utils.LOG;

public class MainActivity extends BaseActivity implements OnCheckedChangeListener{
	protected static final String TAG = "MainActivity";
	private DoubleClickExitHelper mExitHelper; // 双击退出
	private RadioGroup rgTabs;//
	private int currentCheck; // 当前选中的tab
	private ArrayList<Fragment> fragList = new ArrayList<Fragment>();

	private final boolean DEBUG = true;
	private UserInfoAltedRevicer receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(UserInfoAltedRevicer.ACTION_USER_INFO_ALTED);
		receiver = new UserInfoAltedRevicer();
		registerReceiver(receiver, filter);
		initContent();
		initTitleBar(getResources().getString(R.string.buisness_titile));
		mExitHelper = new DoubleClickExitHelper(this);
		if(!AppContext.getAppContext().isDeviceRegisted()){
			registDevice(); // 注册设备
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
	protected void initViewConent() {
		refreshFragments();
		// 手动调用选中第一个
		RadioButton rb = (RadioButton) findViewById(R.id.rb_main_business);
		rb.setChecked(true);
	}
	
	/**
	 * 加载用户信息刷新界面
	 */
	private void refreshFragments(){
		FragmentTransaction ft = getFragmentManager().beginTransaction();;
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
	private void registDevice() {
		final String url = UrlConstants.URL_REGISTER_DEVICE;
		RequestParam param = new RequestParam();
		param.add("deviceType", 0);
		param.add("codeType", AppContext.getAppContext().getDeviceIdType());
		param.add("deviceCode", AppContext.getAppContext().getDeviceID());
		ApiClient.postAsyn(url, param, new ApiClient.ResponceCallBack() {
			@Override
			public void onSuccess(ResponseContent content) {
				AppConfig appConfig = AppConfig.getAppConfig(getApplicationContext());
				appConfig.setProperty(AppConfig.CONF_IS_DEVICE_REGISTED, "" + 1); // 将设备设置为注册成功
				if (DEBUG) {
					String log = appConfig.getProperty(AppConfig.CONF_DEVICE_ID_TYPE) + "******"
							+ appConfig.getProperty(AppConfig.CONF_DEVICE_ID);
					LOG.e(TAG, "[registDevice]" + log);
				}
				LOG.e(TAG, "[registDevice]device register success!!");
			}

			@Override
			public void onFialure(String status, String message) {
				LOG.d(TAG, "[registDevice]sorry,device register fail.error message:" + message);
			}
		});
	}
	
	
	

	
	/**
	 * 获取数据
	 */
	private void loadUserInfo() {
		showWatingDailog();
		String url = UrlConstants.URL_GET_USER_INFO;
		RequestParam param = new RequestParam();
		ApiClient.postAsyn(url, param, new ApiClient.ResponceCallBack() {
			@Override
			public void onSuccess(ResponseContent content) {
				dismissWatingDailog();
				LOG.d(TAG, "[loadData-->postAsyn]:" + content.getData());
				User user = JSON.parseObject(content.getData(), User.class);
				if (user == null || user.userId == null) {
					logout();
				}
				LOG.d(TAG, "[loadData-->postAsyn]:" + user);
				AppContext.getAppContext().login(user);
				initViewsAfterGetInfo();
			}

			@Override
			public void onFialure(String status, String message) {
				dismissWatingDailog();
				AppContext.showToast(message);
			}
		});
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
	private void addTabsFragment(Fragment framgent, String tag) {
		fragList.add(framgent);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.fl_main_container, framgent, tag);
		if (!String.valueOf(currentCheck).equals(tag)) {
			ft.hide(framgent);
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
		final String url = UrlConstants.URL_GET_JPUSH_ALIAS;
		ApiClient.postAsyn(url, new RequestParam(), new ResponceCallBack() {
			@Override
			public void onSuccess(ResponseContent content) {
				LOG.i(TAG, "[requestAndSetJPushAlias] get alias success! ");
				JSONObject object = JSON.parseObject(content.getData());
				String alias = object.getString("alias");
				if (!TextUtils.isEmpty(alias)) {
					LOG.i(TAG, "[requestAndSetJPushAlias] correct alias has been parsed from joson");
					setJPushAlias(alias);
				}
			}

			@Override
			public void onFialure(String status, String message) {
				LOG.w(TAG, "[requestAndSetJPushAlias]sorry! get alias fail.error message:" + message);
			}
		});
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
		public void handleMessage(android.os.Message msg) {
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
