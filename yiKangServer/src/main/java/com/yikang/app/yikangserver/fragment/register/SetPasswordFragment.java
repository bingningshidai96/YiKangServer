package com.yikang.app.yikangserver.fragment.register;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.fragment.BaseFragment;

/**
 * 设置密码fragment
 */
public class SetPasswordFragment extends BaseFragment implements View.OnClickListener{

    private EditText edtPassword;
    private EditText edtPasswordAgain;


    private OnDone callback;
    private Button btComplete;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnDone){
            this.callback = (OnDone) activity;
        }else{
            throw new IllegalArgumentException("wrong argument,context should be instance of SetPasswordFragment.OnDone");
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_password, container, false);
        edtPassword = ((EditText) view.findViewById(R.id.edt_set_password_password));
        edtPasswordAgain = ((EditText) view.findViewById(R.id.edt_set_password_password_again));
        btComplete = ((Button) view.findViewById(R.id.bt_set_password_next));
        btComplete.setOnClickListener(this);
        return view;
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_set_password_next:
                next();
                break;
        }
    }

    /**
     * 设置完成
     */
    private void next(){
        String password = edtPassword.getText().toString();
        String passwordAgain = edtPasswordAgain.getText().toString();

        if (TextUtils.isEmpty(password) || password.length() < 6
                || password.length() > 16) {
            AppContext.showToast(R.string.set_password_hint);
            return;
        }

        if (!password.equals(passwordAgain)) {
            AppContext.showToast(R.string.regist_passw_error_hint);
            return;
        }

        callback.afterSetPassword(password);

    }


    public interface OnDone{
       void  afterSetPassword(String password);
    }
}
