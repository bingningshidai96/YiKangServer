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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.api.RequestParam;
import com.yikang.app.yikangserver.api.ResponseContent;
import com.yikang.app.yikangserver.dailog.DialogFactory;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.api.ApiClient;
import com.yikang.app.yikangserver.utils.LOG;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class ResetPasswFragment extends BaseFragment implements View.OnClickListener {
    protected static final String TAG = null;
    // 填写从短信SDK应用后台注册得到的APPKEY
    private static String APPKEY = "a11dd7ffad70";// 463db7238681 27fe7909f8e8

    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = "8893f3bd45db487a6291460f3904ca46";//

    // 默认使用中国区号
    private static final String CHINA_CODE = "86";

    private EditText edtUserId, edtPassw, edtVerlifiCode, edtPasswAgain;

    private TextView tvGetVerlifiCode;

    private Button btNext;


    /**
     * 计时器的数字
     */
    private int timerCount = 60;


    private EventHandler smsSDKHanlder = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            smsResultHandler.sendMessage(msg);
        }
    };

    /**
     * 处理短信SDK的Handler
     */
    private Handler smsResultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dismissWatingDailog();
            final int event = msg.arg1;
            final int result = msg.arg2;
            final Object data = msg.obj;

            if (result == SMSSDK.RESULT_COMPLETE) { //成功
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    LOG.i(TAG,"[smsResultHandler.handleMessage]"+data);
                    AppContext.showToast(R.string.regist_verlify_succeed);
                    toNextStepPage();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    AppContext.showToast(R.string.regist_send_sms_succees);
                } else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
                    AppContext.showToast(R.string.regist_send_voice_succees);
                }
            } else {
                ((Throwable) data).printStackTrace(); // 答应异常
                String des = null;
                try {
                    Throwable throwable = (Throwable) data;
                    JSONObject object = new JSONObject(throwable.getMessage());
                    des = object.optString("detail") != null ? "\n" + object.optString("detail") : "";
                } catch (Exception e) {
                    LOG.e(TAG, e.toString());
                }

                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    AppContext.showToast(getString(R.string.regist_obtiain_sms_fail) + des);
                    handler.sendEmptyMessage(101);// 提示获取验证码失败，将获取验证码的按钮恢复
                } else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
                    AppContext.showToast(getString(R.string.regist_obtiain_voice_fail) + des);
                    handler.sendEmptyMessage(101);// 提示获取验证码失败，将获取验证码的按钮恢复
                } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    AppContext.showToast(getString(R.string.regist_verlifu_fail) + des);
                } else {
                    AppContext.showToast(R.string.regist_sms_sdk_exception);
                }
            }
        }

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SMSSDK.initSDK(getActivity(), APPKEY, APPSECRET);
        SMSSDK.registerEventHandler(smsSDKHanlder);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_passw, container, false);
        tvGetVerlifiCode = (TextView) view.findViewById(R.id.tv_register_get_verlifiCode);
        edtVerlifiCode = (EditText) view.findViewById(R.id.edt_register_verlifi_code);
        edtUserId = (EditText) view.findViewById(R.id.edt_register_phoneNumber);
        edtPassw = (EditText) view.findViewById(R.id.edt_register_passw);
        edtPasswAgain = (EditText) view.findViewById(R.id.edt_register_passw_again);
        btNext = (Button) view.findViewById(R.id.bt_register_next);
        tvGetVoice = (TextView) view.findViewById(R.id.tv_regist_get_voice_code);
        layout = (LinearLayout) view.findViewById(R.id.ly_register_voice_code_container);
        tvGetVerlifiCode.setOnClickListener(this);
        btNext.setOnClickListener(this);
        tvGetVoice.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(smsSDKHanlder);
        timerCount = 0;
        handler.removeCallbacksAndMessages(null);
        smsResultHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_register_get_verlifiCode:
                getverlifiCode();
                break;
            case R.id.bt_register_next:
                verlifiPhone();
                break;
            case R.id.tv_regist_get_voice_code:
                getVoiceCode();
            default:
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getverlifiCode() {
        String phoneNumber = edtUserId.getText().toString();
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 11) {
            AppContext.showToast(R.string.regist_phoneNumber_error_hint);
            return;
        }
        layout.setVisibility(View.VISIBLE);
        SMSSDK.getVerificationCode(CHINA_CODE, phoneNumber);
        tvGetVerlifiCode.setEnabled(false);
        resetTimer();
    }

    /**
     * 获取语音验证短信
     */
    private void getVoiceCode() {
        String phoneNumber = edtUserId.getText().toString();
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 11) {
            AppContext.showToast(R.string.regist_phoneNumber_error_hint);
            return;
        }
        SMSSDK.getVoiceVerifyCode(phoneNumber, CHINA_CODE);
        resetTimer();
    }


    /**
     * 用来计时的
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
                int count = msg.arg1;
                tvGetVerlifiCode.setText(count + "s秒后再次获取");
                if (count <= 0) {
                    tvGetVerlifiCode.setEnabled(true);// 重新启用
                    tvGetVerlifiCode.setText(R.string.regist_get_verlify_code);
                }
            } else if (msg.what == 101) {
                tvGetVerlifiCode.setEnabled(true);// 重新启用
                tvGetVerlifiCode.setText(R.string.regist_get_verlify_code);
                timerCount = 0;// 取消timer
            }
        }
    };

    /**
     * 重置计时器
     */
    public void resetTimer() {
        timerCount = 60;
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = Message.obtain();
                    if (timerCount > 0) { //handler发送倒数数字
                        msg.what = 100;
                        msg.arg1 = timerCount;
                        handler.sendMessage(msg);
                        timerCount--;
                    } else { //handler发送恢复消息
                        msg.what = 101;
                        handler.sendMessage(msg);
                        timer.cancel();
                        LOG.i(TAG, "[getverlifiCode]计时器timer取消");
                    }
                }
            }, 0, 1000);
        }
    }

    /**
     * 验证手机号，验证结果的处理在handler中
     */
    private void verlifiPhone() {
        LOG.i(TAG, "[verlifiPhone]====");
        String verlifiCode = edtVerlifiCode.getText().toString();
        String phoneNumber = edtUserId.getText().toString();
        String passw = edtPassw.getText().toString();
        String passwAgain = edtPasswAgain.getText().toString();

        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 11) {
            AppContext.showToast(R.string.regist_phoneNumber_error_hint);
            return;
        }
        if (TextUtils.isEmpty(verlifiCode)) {
            AppContext.showToast(R.string.regist_verlify_code_hint);
            return;
        }
        if (TextUtils.isEmpty(passw) || passw.length() < 6
                || passw.length() > 16) {
            AppContext.showToast(R.string.regist_pass_hint);
            return;
        }

        if (!passw.equals(passwAgain)) {
            AppContext.showToast(R.string.regist_passw_error_hint);
            return;
        }

        showWatingDailog();

        // 提交验证码
        SMSSDK.submitVerificationCode(CHINA_CODE, phoneNumber, verlifiCode);

    }

    /**
     * 跳到下一步
     */
    private void toNextStepPage() {
        showWatingDailog();
        String phoneNumber = edtUserId.getText().toString();
        String passWord = edtPassw.getText().toString();

        final String url = UrlConstants.URL_RESET_PASSW;
        RequestParam param = new RequestParam();
        param.add("loginName", phoneNumber);
        param.add("password", passWord);
        ApiClient.postAsyn(url, param, new ApiClient.ResponceCallBack() {

            @Override
            public void onSuccess(ResponseContent content) {
                dismissWatingDailog();
                AppContext.showToast("重置密码成功");
                getActivity().finish();
            }

            @Override
            public void onFialure(String status, String message) {
                dismissWatingDailog();
                Dialog dialog = DialogFactory.getCommerAlertDiaglog(getActivity(), message);
                dialog.show();
            }
        });

    }

    private Timer timer;
    private TextView tvGetVoice;
    private LinearLayout layout;



}
