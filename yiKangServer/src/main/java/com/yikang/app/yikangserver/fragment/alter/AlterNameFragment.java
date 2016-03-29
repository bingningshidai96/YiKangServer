package com.yikang.app.yikangserver.fragment.alter;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.api.ApiTest;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.fragment.BaseFragment;
import com.yikang.app.yikangserver.interf.CanSubmit;
import com.yikang.app.yikangserver.reciever.UserInfoAlteredReceiver;
import com.yikang.app.yikangserver.utils.LOG;

import java.io.Serializable;

/**
 */
public class AlterNameFragment extends BaseAlterFragment implements CanSubmit{
    private static final String TAG = "AlterNameFragment";

    private EditText edtAlter;
    private String oldValue;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCanSubmit = getArguments().getBoolean(ARG_NEED_SUBMIT);
            oldValue = getArguments().getString(ARG_OLD_VALUE);
            if(!getArguments().containsKey(ARG_NEED_SUBMIT)){
                throw new IllegalArgumentException("必须传入一个参数"+ARG_NEED_SUBMIT);
            }
            LOG.i(TAG,""+mCanSubmit+">>"+oldValue);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alter_write, container, false);
        edtAlter = ((EditText) view.findViewById(R.id.edt_alter_item));
        edtAlter.setHint("请输入姓名");
        if(oldValue != null){
            edtAlter.setText(oldValue);
        }
        return view;
    }






    @Override
    public void submit() {
        if(!check()){
            return;
        }
        if(mCanSubmit){
            showWaitingUI();
            String newValue = edtAlter.getText().toString();
            ApiTest.alterName(newValue,alterHandler); //网络提交，数据返回
        }else {
            finishWithResult(); //数据返回
        }

    }

    private ResponseCallback<Void> alterHandler = new ResponseCallback<Void>() {
        @Override
        public void onSuccess(Void data) {
            AppContext.showToast("修改成功");
            hideWaitingUI();
            finishWithResult();
            //发送已经修改广播
            notifyUserInfoChanged();
        }

        @Override
        public void onFailure(String status, String message) {
            hideWaitingUI();
            AppContext.showToast(message);
        }
    };


    /**
     * 带着信息返回上一页
     */
    private void finishWithResult(){
        Intent intent = new Intent();
        intent.putExtra(RESULT_EXTRA_RESULT, edtAlter.getText().toString());
        getActivity().setResult(0, intent);
        getActivity().finish();
    }


    /**
     * 检查数据
     * @return
     */
    private boolean check() {
        String result = edtAlter.getText().toString();
        if(TextUtils.isEmpty(result)){
            AppContext.showToast("填写不能为空");
            return false;
        }
        return true;
    }



}
