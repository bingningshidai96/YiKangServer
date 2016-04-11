package com.yikang.app.yikangserver.fragment.register;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.yikang.app.yikangserver.fragment.BaseFragment;

import java.lang.ref.WeakReference;

/**
 * 填写验证码fragment
 * Created by liu on 16/3/11.
 */
public class VerifyPhoneFragment extends BaseFragment implements View.OnClickListener{
    private static final String ARG_PHONE = "phone";

    private String mPhone;
    private EditText edtVerifyCode;
    private Button btComplete;
    private TextView tvIntervalCount;
    private Button btGetAgain;


    private CountDownHandler countDownHandler;

    private static final long VERIFY_INTERVAL = 60*1000;//两次获取时间间隔

    private long lastTimeMillis; //上一下获取的时间

    private OnDone callback;
    
    
    /** 获取验证码网络请求处理 **/
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


    /**验证手机号网络处理 **/
    private ResponseCallback<Void> verifyHandler = new ResponseCallback<Void>() {
        @Override
        public void onSuccess(Void data) {
            hideWaitingUI();
            AppContext.showToast(R.string.verify_success);
            callback.onAfterVerified(mPhone);
        }

        @Override
        public void onFailure(String status, String message) {
            hideWaitingUI();
            String note = getString(R.string.verify_fail);
            AppContext.showToast(note+":"+message);
        }
    };





    public VerifyPhoneFragment() {
        // Required empty public constructor
    }





    /**
     * 创建一个VerifyPhoneFragment实力
     * @param phone 手机号码.
     * @return A new instance of fragment SetPasswordFragment.
     */
    public static VerifyPhoneFragment newInstance(String phone) {
        VerifyPhoneFragment fragment = new VerifyPhoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHONE, phone);
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnDone){
            this.callback = (OnDone) activity;
        }else{
            throw new IllegalArgumentException("wrong argument,context should be instance of VerifyPhoneFragment.OnDone");
        }
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPhone = getArguments().getString(ARG_PHONE);
        }
        getVerify(mPhone);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_phone, container, false);
        edtVerifyCode = (EditText) view.findViewById(R.id.edt_verify_verify_code);
        btComplete = ((Button) view.findViewById(R.id.bt_verify_complete));
        tvIntervalCount = (TextView) view.findViewById(R.id.tv_verify_interval_count);
        btGetAgain = ((Button) view.findViewById(R.id.bt_verify_get_again));

        btGetAgain.setOnClickListener(this);
        btComplete.setOnClickListener(this);
        return view;
    }




    /**
     * 获取验证码
     * @param phone
     */
    private void getVerify(String phone) {
        if(System.currentTimeMillis()-lastTimeMillis > VERIFY_INTERVAL){
            showWaitingUI();
            Api.getVerifyCode(phone, getVerifyHandler);
        }
    }




    /**
     * 验证手机号
     * @param phone 手机号
     * @param verifyCode 验证码
     */
    private void verifyPhone(String phone,String verifyCode) {
        showWaitingUI();
        Api.verifyPhone(phone, verifyCode, verifyHandler);
    }




    /**
     * 开始计时
     */
    private void startCountDown() {
        if(countDownHandler == null)
            countDownHandler = new CountDownHandler(this);
        countDownHandler.start();
    }




    /**
     * 更新倒计时时间
     * @param count
     */
    private void updateCountDown(int count){
        if(count == 0){
            tvIntervalCount.setVisibility(View.GONE);
            tvIntervalCount.setText("");
            btGetAgain.setVisibility(View.VISIBLE);
        }else{
            String note = getString(R.string.verify_interval_note, count);
            tvIntervalCount.setText(note);
            tvIntervalCount.setVisibility(View.VISIBLE);
            btGetAgain.setVisibility(View.GONE);
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        if(countDownHandler != null){
            countDownHandler.stop();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_verify_get_again:
                getVerify(mPhone);
                break;
            case R.id.bt_verify_complete:
                String verifyCode = edtVerifyCode.getText().toString();
                if(verifyCode.isEmpty()){
                    AppContext.showToast("请输入验证码");
                }else{
                    verifyPhone(mPhone, verifyCode);
                }
                break;
        }
    }




    /**
     * 计时器
     */
    private static class CountDownHandler extends Handler{

        public WeakReference<VerifyPhoneFragment> refVerifyFragment;
        private volatile boolean isStart;

        public CountDownHandler(VerifyPhoneFragment fragment){
            refVerifyFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(isStart && msg.what == 0){
                VerifyPhoneFragment fragment = refVerifyFragment.get();
                if(fragment!=null){
                    long count = (fragment.VERIFY_INTERVAL-
                            (System.currentTimeMillis() - fragment.lastTimeMillis))/ 1000;
                    fragment.updateCountDown((int) count); //更新时间
                    if(count>0){
                        sendEmptyMessageDelayed(0,1000);
                    }
                }
            }
        }

        /**
         * 停止计时器
         */
        public void stop(){
            isStart = false;
            refVerifyFragment.get().updateCountDown(0);
        }


        public void start(){
            isStart = true;
            sendEmptyMessage(0);//开始计时
        }

    }


    public interface OnDone{
        void onAfterVerified(String phone);
    }


}
