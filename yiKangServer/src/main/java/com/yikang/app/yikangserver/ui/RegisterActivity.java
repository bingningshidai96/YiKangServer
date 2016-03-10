package com.yikang.app.yikangserver.ui;

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
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.application.AppConfig;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.dialog.DialogFactory;
import com.yikang.app.yikangserver.fragment.EditUserInfoFragemt;
import com.yikang.app.yikangserver.fragment.EditUserInfoFragemt.OnCompleteListener;
import com.yikang.app.yikangserver.fragment.RegisterAccountFragment;
import com.yikang.app.yikangserver.fragment.RegisterAccountFragment.OnNextListener;
import com.yikang.app.yikangserver.service.UpLoadService;
import com.yikang.app.yikangserver.utils.LOG;

import java.util.HashMap;
import java.util.Map;
/**
 *注册activity
 */
public class RegisterActivity extends BaseActivity implements OnNextListener,
		OnCompleteListener {
	private static final String TAG = "RegisterActivity";
	private final String PARAM_LOGIN_NAME = "loginName";
	private final String PARAM_LOGIN_PASSW = "passWord";
	private final String PARAM_LOGIN_PHOTO_URL = "photoUrl";

	private BroadcastReceiver receiver;
	private Map<String, Object> paramMap;


	/**
	 * 注册回调
	 */
	private ResponseCallback<Void> registerHandler = new ResponseCallback<Void>() {
		@Override
		public void onSuccess(Void data) {
			hideWaitingUI();
			onRegisterSuccess();// 注册成功
		}

		@Override
		public void onFailure(String status, String message) {
			hideWaitingUI();
			onRegisterFailure(message);// 注册失败
		}
	};


	/**
	 * 登录回调
	 */
	private class LoginHandler implements ResponseCallback<String> {
		private String userName;
		private String password;

		private LoginHandler(String userName,String password){
			this.userName = userName;
			this.password = password;
		}

		@Override
		public void onSuccess(String ticket) {
			hideWaitingUI();
			AppContext.getAppContext().updateAccessTicket(ticket);
			AppConfig appConfig = AppConfig.getAppConfig(getApplicationContext());
			appConfig.setProperty("login.userName", userName);
			appConfig.setProperty("login.password", password);
			onLoginSuccess();
		}

		@Override
		public void onFailure(String status, String message) {
			hideWaitingUI();
			LOG.d(TAG, "[login]" + message);
			AppContext.showToast(RegisterActivity.this, message);
		}
	}

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
	protected void initViewContent() {
		 Fragment fragment = new RegisterAccountFragment();
		 FragmentTransaction ft = getFragmentManager().beginTransaction();
		 ft.replace(R.id.fl_register_fragment_container,
		 fragment).commit();
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
			registerWithAvatar(selectAvatarPath);
			return;
		}
		requestRegister(); // 直接注册
	}

	/**
	 * 上传头像后注册
	 */
	private void registerWithAvatar(String selectAvatarPath) {
		if (TextUtils.isEmpty(selectAvatarPath)) {
			return;
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		showWaitingUI();
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				hideWaitingUI();
				printUploadResult(intent);
				String avatarUrl = intent.getStringExtra(UpLoadService.EXTRA_DATA);
				if (!TextUtils.isEmpty(avatarUrl)) {
					paramMap.put(PARAM_LOGIN_PHOTO_URL, avatarUrl);
				}
				requestRegister();
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
				UpLoadService.EXTRA_IS_SUCCESS, false);
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
	private void onLoginSuccess() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}



	/**
	 * 想服务器提交数据注册
	 */
	private void requestRegister() {
		showWaitingUI();
		Api.register(paramMap, registerHandler);
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
	private void onRegisterSuccess() {
		AppContext.showToast("注册成功");
		login((String) paramMap.get(PARAM_LOGIN_NAME),
				(String) paramMap.get(PARAM_LOGIN_PASSW));
	}

	/**
	 * 注册失败时执行的动作
	 */
	private void onRegisterFailure(String msg) {
		AppContext.showToast("注册失败:" + msg);
		Dialog dialog = DialogFactory.getCommonAlertDialog(this,
				getString(R.string.alert), "注册失败:" + msg);
		dialog.show();
	}

	/**
	 * 登录
	 */
	private void login(final String userName, final String password) {
		showWaitingUI();
		Api.login(userName,password,new LoginHandler(userName,password));
	}
}
