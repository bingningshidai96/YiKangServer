package com.yikang.app.yikangserver.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.api.ApiTest;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.application.AppConfig;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.dialog.DialogFactory;
import com.yikang.app.yikangserver.fragment.register.RegisterEntryFragment;
import com.yikang.app.yikangserver.fragment.register.SetPasswordFragment;
import com.yikang.app.yikangserver.fragment.register.VerifyPhoneFragment;
import com.yikang.app.yikangserver.utils.LOG;

/**
 * 注册activity
 */
public class RegisterActivity extends BaseActivity implements
        RegisterEntryFragment.OnDone,
        VerifyPhoneFragment.OnDone,
        SetPasswordFragment.OnDone{

    private static final String TAG = "RegisterActivity";


    private String phoneNumber;
    private String passWord;


    /**注册回调*/
    private ResponseCallback<Void> registerHandler = new ResponseCallback<Void>() {
        @Override
        public void onSuccess(Void data) {
            hideWaitingUI();
            AppContext.showToast(R.string.register_success);
            login(phoneNumber, passWord);
        }

        @Override
        public void onFailure(String status, String message) {
            hideWaitingUI();
            AppContext.showToast("注册失败:" + message);
            Dialog dialog = DialogFactory.getCommonAlertDialog(RegisterActivity.this,
                    getString(R.string.alert), "注册失败:" + message);
            dialog.show();
        }
    };


    /**登录回调*/
    private class LoginHandler implements ResponseCallback<String> {
        private String userName;
        private String password;

        private LoginHandler(String userName, String password) {
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

            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
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
    protected void findViews() {
    }

    @Override
    protected void getData() {
    }


    @Override
    protected void initTitleBar(String title) {
        super.initTitleBar(title);
        findViewById(R.id.ibtn_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getFragmentManager().findFragmentById(R.id.fl_register_fragment_container);
                if(fragment instanceof RegisterEntryFragment){
                    finish();
                }else{
                    getFragmentManager().popBackStack();
                }
            }
        });
    }

    @Override
    protected void initViewContent() {
        Fragment fragment = new RegisterEntryFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fl_register_fragment_container,
                fragment).commit();
    }




    /**
     * 填写完手机号
     * @param phone 手机号
     */
    @Override
    public void afterGetPhone(String phone) {
        this.phoneNumber = phone;

        Fragment fragment = VerifyPhoneFragment.newInstance(phone);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fl_register_fragment_container, fragment);
        ft.addToBackStack("toVerify").commit();
    }




    /**
     * 验证完手机号码
     * @param phone 手机号
     */
    @Override
    public void onAfterVerified(String phone) {
        Fragment fragment = new SetPasswordFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fl_register_fragment_container, fragment);
        ft.addToBackStack("setPassword").commit();
    }




    /**
     * 设置完密码
     * @param password 密码
     */
    @Override
    public void afterSetPassword(String password) {
        this.passWord = password;
        register(phoneNumber, password);
    }




    /**
     * 注册
     */
    private void register(String phone,String passWord) {
        showWaitingUI();
        LOG.i(TAG,"[register]phone:"+phone+"-----password:"+passWord);
        ApiTest.registerNew(phone, passWord, registerHandler);
    }



    /**
     * 登录
     */
    private void login(final String userName, final String password) {
        showWaitingUI();
        Api.login(userName, password, new LoginHandler(userName, password));
        finish();
    }






}
