package com.yikang.app.yikangserver.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.dialog.DialogFactory;
import com.yikang.app.yikangserver.utils.LOG;

import java.lang.ref.WeakReference;


public class ResetPasswordFragment extends BaseFragment implements View.OnClickListener {
    protected static final String TAG = null;
    private static final long VERIFY_INTERVAL = 60 * 1000;//两次获取时间间隔

    private EditText edtUserId, edtPassw, edtVerifyCode, edtPasswAgain;
    private TextView tvGetVerifyCode;
    private Button btNext;
    /**
     * 计时器的数字
     */
    private CountDownHandler countDownHandler;
    private long lastTimeMillis; //上一下获取的时间


    /**
     * 获取验证码网络请求处理
     **/
    private ResponseCallback<Void> getVerifyHandler = new ResponseCallback<Void>() {
        @Override
        public void onSuccess(Void data) {
            hideWaitingUI();
            AppContext.showToast("短信已发送");
            lastTimeMillis = System.currentTimeMillis();
            startCountDown();
        }

        @Override
        public void onFailure(String status, String message) {
            hideWaitingUI();
            AppContext.showToast(message);
        }
    };


    /**
     * 验证手机号网络处理
     **/
    private ResponseCallback<Void> verifyHandler = new ResponseCallback<Void>() {
        @Override
        public void onSuccess(Void data) {
            hideWaitingUI();
            AppContext.showToast(R.string.verify_success);
            resetPassword();
        }

        @Override
        public void onFailure(String status, String message) {
            hideWaitingUI();
            String note = getString(R.string.verify_fail);
            AppContext.showToast(note + ":" + message);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_passw, container, false);
        tvGetVerifyCode = (TextView) view.findViewById(R.id.tv_register_get_verifyCode);
        edtVerifyCode = (EditText) view.findViewById(R.id.edt_register_verify_code);
        edtUserId = (EditText) view.findViewById(R.id.edt_register_phoneNumber);
        edtPassw = (EditText) view.findViewById(R.id.edt_register_passw);
        edtPasswAgain = (EditText) view.findViewById(R.id.edt_register_passw_again);
        btNext = (Button) view.findViewById(R.id.bt_register_next);
        tvGetVerifyCode.setOnClickListener(this);
        btNext.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_register_get_verifyCode:
                getVerify(edtUserId.getText().toString());
                break;
            case R.id.bt_register_next:
                verifyPhone();
                break;
            default:
                break;
        }
    }


    /**
     * 开始计时
     */
    private void startCountDown() {
        if (countDownHandler == null)
            countDownHandler = new CountDownHandler(this);
        countDownHandler.start();
    }


    /**
     * 获取验证码
     *
     * @param phone
     */
    private void getVerify(String phone) {
        String phoneNumber = edtUserId.getText().toString();
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 11) {
            AppContext.showToast(R.string.regist_phoneNumber_error_hint);
            return;
        }
        if (System.currentTimeMillis() - lastTimeMillis > VERIFY_INTERVAL) {
            showWaitingUI();
            Api.getVerifyCode(phone, getVerifyHandler);
            LOG.i(TAG,"[getVerify]invoke this method");
        }
    }

    /**
     * 更新倒计时时间
     *
     * @param count
     */
    private void updateCountDown(int count) {
        if (count == 0) {
            tvGetVerifyCode.setEnabled(true);// 重新启用
            tvGetVerifyCode.setText(R.string.register_get_verify_code);
        } else {
            tvGetVerifyCode.setEnabled(false);
            tvGetVerifyCode.setText(count + "s秒后再次获取");
        }
    }

    /**
     * 验证手机号，验证结果的处理在handler中
     */
    private void verifyPhone() {
        String verifyCode = edtVerifyCode.getText().toString();
        String phoneNumber = edtUserId.getText().toString();
        String password = edtPassw.getText().toString();
        String passwordAgain = edtPasswAgain.getText().toString();

        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 11) {
            AppContext.showToast(R.string.regist_phoneNumber_error_hint);
            return;
        }
        if (TextUtils.isEmpty(verifyCode)) {
            AppContext.showToast(R.string.register_verify_code_hint);
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6
                || password.length() > 16) {
            AppContext.showToast(R.string.set_password_hint);
            return;
        }

        if (!password.equals(passwordAgain)) {
            AppContext.showToast(R.string.regist_passw_error_hint);
            return;
        }

        showWaitingUI();

        // 提交验证码
        Api.verifyPhone(phoneNumber, verifyCode, verifyHandler);

    }

    /**
     * 跳到下一步
     */
    private void resetPassword() {
        showWaitingUI();
        String phoneNumber = edtUserId.getText().toString();
        String passWord = edtPassw.getText().toString();

        Api.resetPassword(phoneNumber, passWord, new ResponseCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                hideWaitingUI();
                AppContext.showToast("重置密码成功");
                getActivity().finish();
            }

            @Override
            public void onFailure(String status, String message) {
                hideWaitingUI();
                Dialog dialog = DialogFactory.getCommonAlertDialog(getActivity(), message);
                dialog.show();
            }
        });
    }



    /**
     * 计时器
     */
    private static class CountDownHandler extends Handler {

        public WeakReference<ResetPasswordFragment> refVerifyFragment;
        private volatile boolean isStart;

        public CountDownHandler(ResetPasswordFragment fragment) {
            refVerifyFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isStart && msg.what == 0) {
                ResetPasswordFragment fragment = refVerifyFragment.get();
                if(fragment != null){
                    long count = (fragment.VERIFY_INTERVAL -
                            (System.currentTimeMillis() - fragment.lastTimeMillis)) / 1000;
                    fragment.updateCountDown((int) count); //更新时间
                    if (count > 0) {
                        sendEmptyMessageDelayed(0, 1000);
                    }
                }
            }
        }

        /**
         * 停止计时器
         */
        public void stop() {
            isStart = false;
            refVerifyFragment.get().updateCountDown(0);
        }


        public void start() {
            isStart = true;
            sendEmptyMessage(0);//开始计时
        }


    }


}
