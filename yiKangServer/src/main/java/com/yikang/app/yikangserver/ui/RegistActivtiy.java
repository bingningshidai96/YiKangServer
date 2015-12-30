package com.yikang.app.yikangserver.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppConfig;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.api.RequestParam;
import com.yikang.app.yikangserver.api.ResponseContent;
import com.yikang.app.yikangserver.dailog.DialogFactory;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.fragment.RegisterAccountFragment;
import com.yikang.app.yikangserver.fragment.RegisterAccountFragment.OnNextListener;
import com.yikang.app.yikangserver.fragment.EditUserInfoFragemt;
import com.yikang.app.yikangserver.fragment.EditUserInfoFragemt.OnCompleteListener;
import com.yikang.app.yikangserver.service.UpLoadService;
import com.yikang.app.yikangserver.api.ApiClient;
import com.yikang.app.yikangserver.utils.LOG;
/**
 *注册activity
 */
public class RegistActivtiy extends BaseActivity implements OnNextListener,
		OnCompleteListener {
	private static final String TAG = "RegistActivtiy";
	private final String PARAM_LOGIN_NAME = "loginName";
	private final String PARAM_LOGIN_PASSW = "passWord";
	private final String PARAM_LOGIN_PHOTO_URL = "photoUrl";

	private BroadcastReceiver receiver;
	private Map<String, Object> paramMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar(getString(R.string.regist_title));
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
		 Fragment fragment = new RegisterAccountFragment();
		 FragmentTransaction ft = getFragmentManager().beginTransaction();
		 ft.replace(R.id.fl_register_fragment_container,
		 fragment).commit();
		//next("17822222222", "123456");
		// next("15836270024","111111");
	}

	@Override
	public void next(String userName, String passw) {
		paramMap = new HashMap<String, Object>();
		paramMap.put(PARAM_LOGIN_NAME, userName);
		paramMap.put(PARAM_LOGIN_PASSW, passw);
		LOG.i(TAG, "[next]" + userName + ">>" + passw);
		Fragment fragment = new EditUserInfoFragemt();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fl_register_fragment_container, fragment);
		ft.addToBackStack("toTepTwo").commit();
	}

	@Override
	public void onComplete(Map<String, Object> params) {
		this.paramMap.putAll(params);
		register();
	}

	/**
	 * 上传图片
	 */
	private void register() {
		String selectAvatarPath = (String) paramMap.get("filePath");
		LOG.i(TAG, "[register]选择的头像路劲是" + selectAvatarPath);
		paramMap.remove("filePath");
		if (!TextUtils.isEmpty(selectAvatarPath)) {
			regiserWithAvatar(selectAvatarPath);
			return;
		}
		requestRegist(); // 直接注册
	}

	/**
	 * 上传头像后注册
	 */
	private void regiserWithAvatar(String selectAvatarPath) {
		if (TextUtils.isEmpty(selectAvatarPath)) {
			return;
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		showWatingDailog();
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				dismissWatingDailog();
				printUploadResult(intent);
				String avatarUrl = intent
						.getStringExtra(UpLoadService.EXTRA_DATA);
				if (!TextUtils.isEmpty(avatarUrl)) {
					paramMap.put(PARAM_LOGIN_PHOTO_URL, avatarUrl);
				}
				requestRegist();
			}
		};
		IntentFilter filter = new IntentFilter(
				UpLoadService.ACTION_UPLOAD_COMPLETE);
		registerReceiver(receiver, filter);

		Intent intent = new Intent(this, UpLoadService.class);
		intent.putExtra("filePath", selectAvatarPath);
		startService(intent);
		return;
	}

	/** 上传结果 */
	private void printUploadResult(Intent intent) {
		boolean isUploadSucess = intent.getBooleanExtra(
				UpLoadService.EXTRA_IS_SUCESS, false);
		String avatarUrl = intent.getStringExtra(UpLoadService.EXTRA_DATA);

		if (isUploadSucess && !TextUtils.isEmpty(avatarUrl)) {
			AppContext.showToast("头像上传成功");
			LOG.i(TAG, "[register]头像上传成功" + avatarUrl);
		} else {
			String message = intent.getStringExtra(UpLoadService.EXTRA_MESSAGE);
			AppContext.showToast("抱歉，头像上传失败");
			LOG.e(TAG, "[register]上传头像失败:" + message);
		}
	}

	/**
	 * 登录成功
	 */
	private void onLoginSucess() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 想服务器提交数据注册
	 */
	private void requestRegist() {
		showWatingDailog();
		String url = UrlConstants.URL_REGISTER;
		RequestParam param = new RequestParam("appid", "accessTicket");
		param.addAll(paramMap);
		ApiClient.postAsyn(url, param,
				new ApiClient.ResponceCallBack() {
					@Override
					public void onSuccess(ResponseContent content) {
						dismissWatingDailog();
						onRegistSuccess();// 注册成功
					}

					@Override
					public void onFialure(String status, String message) {
						dismissWatingDailog();
						onRegistFailure(message);// 注册失败
					}
				});
	}

	@Override
	public void onStop() {
		super.onStop();
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
	}

	/**
	 * 注册成功时执行的动作
	 */
	private void onRegistSuccess() {
		AppContext.showToast("注册成功");
		login((String) paramMap.get(PARAM_LOGIN_NAME),
				(String) paramMap.get(PARAM_LOGIN_PASSW));
	}

	/**
	 * 注册失败时执行的动作
	 */
	private void onRegistFailure(String msg) {
		AppContext.showToast("注册失败:" + msg);
		Dialog dialog = DialogFactory.getCommerAlertDiaglog(this,
				getString(R.string.alert), "注册失败:"+msg);
		dialog.show();
	}

	/**
	 * 登录
	 */
	private void login(final String userName, final String passw) {
		showWatingDailog();
		String url = UrlConstants.URL_LOGIN_LOGIN;
		RequestParam param = new RequestParam("appid", "accessTicket");
		param.add("loginName", userName);
		param.add("passWord", passw);
		param.add("machineCode", AppContext.getAppContext().getDeviceID());
		ApiClient.postAsyn(url, param,
				new ApiClient.ResponceCallBack() {
					@Override
					public void onSuccess(ResponseContent content) {
						dismissWatingDailog();
						String ticket = content.getData();
						AppContext.getAppContext().updateAccessTicket(ticket);
						AppConfig appConfig = AppConfig.getAppConfig(getApplicationContext());
						appConfig.setProperty("login.userName", userName);
						appConfig.setProperty("login.password", passw);
						onLoginSucess();
					}

					@Override
					public void onFialure(String status, String message) {
						dismissWatingDailog();
						LOG.d(TAG, "[login]" + message);
						AppContext.showToast(RegistActivtiy.this, message);
					}
				});
	}
}
