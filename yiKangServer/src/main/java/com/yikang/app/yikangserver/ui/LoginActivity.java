package com.yikang.app.yikangserver.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppConfig;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.dailog.DialogFactory;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.utils.BuisNetUtils;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.utils.UIHelper;

public class LoginActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "LoginActivity";
	private EditText edtUserId, edtPassw;
	private Button btnLogin;

	private TextView tvRegist,tvFindPassw;
	private String accessTicket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar(getString(R.string.login_title));
	}

	@Override
	protected void findViews() {
		edtUserId = (EditText) findViewById(R.id.edt_login_userId);
		edtPassw = (EditText) findViewById(R.id.edt_login_passw);
		btnLogin = (Button) findViewById(R.id.bt_login_login);
		tvRegist = (TextView) findViewById(R.id.tv_login_regist);
		tvFindPassw = (TextView) findViewById(R.id.tv_login_forget);
		
		edtUserId.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				if(TextUtils.isEmpty(s.toString())){
					edtPassw.setText("");
				}
			}
		});
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_login);
	}

	@Override
	protected void getData() {}

	@Override
	protected void initViewConent() {
		btnLogin.setOnClickListener(this);
		tvRegist.setOnClickListener(this);
		tvFindPassw.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		AppConfig config = AppConfig.getAppConfig(this);
		String userName = config.getProperty("login.userName");
		String passw = config.getProperty("login.password");
		boolean autoLogin = AppContext.get("autoLogin", false);
		if(!TextUtils.isEmpty(userName) && !userName.equals(edtUserId.getText())){
			edtPassw.setText(passw);
			edtUserId.setText(userName);
		}
		if (autoLogin) {
			login(userName, passw);
		}
	}
	
	
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.bt_login_login:
			String userId = edtUserId.getText().toString();
			String passw = edtPassw.getText().toString();
			if (TextUtils.isEmpty(userId) || userId.length() != 11
					|| TextUtils.isEmpty(passw)) {
				AppContext.showToast(getString(R.string.login_passw_error_tip));
				return;
			}
			login(userId, passw);
			break;
		case R.id.tv_login_regist:
			toRegiste();// 注册完直接登录，不会回到这个页面
			break;
		case R.id.tv_login_forget:
			findPassw();
		default:
			break;
		}
	}
	
	/**
	 * 找回密码
	 */
	private void findPassw() {
        UIHelper.showFindPasswordPage(this);



//		String userId = edtUserId.getText().toString();
//		if(TextUtils.isEmpty(userId)||userId.length()!=11){
//			AppContext.showToast(R.string.login_find_passw_input_tips);
//			edtUserId.requestFocus();
//			return ;
//		}
//		showWatingDailog();
//		String url = UrlConstants.URL_FIND_PASSW;
//		RequestParam param = new RequestParam();
//		param.add("loginName", userId);
//		BuisNetUtils.requestStr(url, param, new BuisNetUtils.ResponceCallBack() {
//			@Override
//			public void onSuccess(ResponseContent content) {
//				dismissWatingDailog();
//				AppContext.showToast(R.string.login_find_passw_find_seucess);
//			}
//
//			@Override
//			public void onFialure(String status, String message) {
//				dismissWatingDailog();
//				String msg = getString(R.string.login_find_passw_find_fail);
//				AppContext.showToast(msg+":"+message);
//			}
//		});
	}

	/**
	 * 跳到注册页面
	 */
	private void toRegiste() {
		Intent intent = new Intent(this, RegistActivtiy.class);
		startActivity(intent);
	}

	private void toMainPage() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 登录
	 */
	private void login(final String userName, final String passw) {
		showSubmitDialog();
		String url = UrlConstants.URL_LOGIN_LOGIN;
		RequestParam param = new RequestParam("appid", "accessticket");
		param.add("loginName", userName);
		param.add("passWord", passw);
		param.add("machineCode", AppContext.getAppContext().getDeviceID());
		BuisNetUtils.requestStr(url, param,new BuisNetUtils.ResponceCallBack() {
			@Override
			public void onSuccess(ResponseContent content) {
				dismissSubmitDialog();
				accessTicket = content.getData();
				AppContext.getAppContext().updateAccessTicket(accessTicket);
				AppConfig appConfig = AppConfig.getAppConfig(getApplicationContext());
				appConfig.setProperty("login.userName", userName);
				appConfig.setProperty("login.password", passw);
				toMainPage();
			}

			@Override
			public void onFialure(String status, String message) {
				dismissSubmitDialog();
				LOG.d(TAG, "[login]" + message);
				AppContext.showToast(LoginActivity.this, message);
			}
		});
	}


	private ProgressDialog proDialog;

	private void showSubmitDialog() {
		if (proDialog == null) {
			proDialog = DialogFactory.getProgressDailog(
					DialogFactory.TYPE_SUBMIT_DATA, this);
		}
		proDialog.show();
	}

	private void dismissSubmitDialog() {
		if (proDialog != null && proDialog.isShowing()) {
			proDialog.dismiss();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		dismissSubmitDialog();
		proDialog = null;

	}

}
