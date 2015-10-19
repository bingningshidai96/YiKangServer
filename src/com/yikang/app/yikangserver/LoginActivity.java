package com.yikang.app.yikangserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.yikang.app.yikangserver.application.AppConfig;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.dailog.ProgressDialogFactory;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.utils.BuisNetUtils;
import com.yikang.app.yikangserver.utils.LOG;

public class LoginActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "LoginActivity";
	private EditText edtUserId,edtPassw;
	private CheckBox chkAutoLogin;
	private Button btnLogin;
	
	private TextView tvRegist;
	private String accessTicket;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar(getString(R.string.login_title));
	}
	
	
	@Override
	protected void findViews() {
		// TODO Auto-generated method stub
		edtUserId = (EditText) findViewById(R.id.edt_login_userId);
		edtPassw = (EditText) findViewById(R.id.edt_login_passw);
		chkAutoLogin = (CheckBox) findViewById(R.id.chk_login_autoLogin);
		btnLogin = (Button) findViewById(R.id.bt_login_login);
		tvRegist = (TextView) findViewById(R.id.tv_login_regist);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_login);
	}

	@Override
	protected void getData() {
		AppConfig config = AppConfig.getAppConfig(this);
		String userName = config.getProperty("user.userName");
		String passw = config.getProperty("user.password");
		boolean autoLogin = AppContext.get("autoLogin", false);
		if(autoLogin){
			login(userName,passw);
		}
		edtPassw.setText(passw);
		edtUserId.setText(userName);
	}

	@Override
	protected void initViewConent() {
		boolean autoLogin = AppContext.get("autoLogin", false);
		chkAutoLogin.setChecked(autoLogin);
		
		btnLogin.setOnClickListener(this);
		tvRegist.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		int id  = v.getId();
		switch (id) {
		case R.id.bt_login_login:
			String userId = edtUserId.getText().toString();
			String passw = edtPassw.getText().toString();
			if(TextUtils.isEmpty(userId)||userId.length()!=11
				||TextUtils.isEmpty(passw)){
					AppContext.showToast(getString(R.string.login_passw_error_tip));
					return ;
			}
			login(userId,passw);
			break;
		case R.id.tv_login_regist:
			toRegiste();//注册完直接登录，不会回到这个页面
		default:
			break;
		}
	}
	
	
	/**
	 * 跳到注册页面
	 */
	private void toRegiste(){
		Intent intent = new Intent(this, RegistActivtiy.class);
		startActivity(intent);
	}
	private void toMainPage(){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	
	
	/**
	 * 登录
	 */
	private void login(final String userName,final String passw){
		showSubmitDialog();
		String url = UrlConstants.URL_LOGIN_LOGIN;
		RequestParam param = new RequestParam("11", "111");
		param.add("loginName", userName);
		param.add("passWord", passw);
		param.add("machineCode", AppContext.getAppContext().getDeviceID());
		BuisNetUtils.requestStr(url, param, new BuisNetUtils.ResponceCallBack() {
			
			@Override
			public void onSuccess(ResponseContent content) {
				dismissSubmitDialog();
				accessTicket = content.getData();
				AppContext.getAppContext().updateAccessTicket(accessTicket);
				if(chkAutoLogin.isChecked()){
					AppContext.set("autoLogin", true);
				}
				AppConfig appConfig = AppConfig.getAppConfig(getApplicationContext());
				appConfig.setProperty("user.userName", userName);
				appConfig.setProperty("user.password", passw);
				toMainPage();
			}
			@Override
			public void onFialure(String status, String message) {
				dismissSubmitDialog();
				LOG.d(TAG, "[login]"+message);
				AppContext.showToast(LoginActivity.this, message);
			}
		});
	}

	private ProgressDialog proDialog;
	
	private void showSubmitDialog(){
		if(proDialog==null){
			proDialog = ProgressDialogFactory.getProgressDailog(
					ProgressDialogFactory.TYPE_SUBMIT_DATA, this);
		}
		proDialog.show();
	}
	
	
	private void dismissSubmitDialog(){
		if(proDialog!=null&&proDialog.isShowing()){
			proDialog.dismiss();
		}
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		dismissSubmitDialog();
		proDialog = null;
		
	}

}
