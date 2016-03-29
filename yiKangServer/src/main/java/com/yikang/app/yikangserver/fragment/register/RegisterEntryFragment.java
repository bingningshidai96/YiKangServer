package com.yikang.app.yikangserver.fragment.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.fragment.BaseFragment;
import com.yikang.app.yikangserver.ui.WebActivity;


/**
 *点击注册进入的第一个页面
 * Created by liu on 16/3/11.
 */
public class RegisterEntryFragment extends BaseFragment implements View.OnClickListener{
    private EditText edtPhone;
    private Button btComplete;
    private Button btLogin;


    private OnDone callback;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnDone){
            this.callback = (OnDone) activity;
        }else{
            throw new IllegalArgumentException("wrong argument,context should be instance of RegisterEntryFragment.OnDone");
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_entry, container, false);
        edtPhone = (EditText) view.findViewById(R.id.edt_register_entry_phoneNumber);
        btComplete = ((Button) view.findViewById(R.id.bt_register_entry_complete));
        btLogin = ((Button) view.findViewById(R.id.bt_register_entry_login));
        view.findViewById(R.id.tv_register_entry_agreement).setOnClickListener(this);

        btComplete.setOnClickListener(this);
        btLogin.setOnClickListener(this);
        return view;
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_register_entry_complete:
                String phoneNumber = edtPhone.getText().toString();
                if(phoneNumber.length() ==11){
                    callback.afterGetPhone(phoneNumber);
                }else{
                    AppContext.showToast("请输入正确的手机号");
                }
                break;
            case R.id.bt_register_entry_login:
                getActivity().finish();
                break;
            case R.id.tv_register_entry_agreement:
                String title = getString(R.string.web_agreement_title);
                String url ="www.baidu.com";
                startAgreementPage(title,url);
                break;
        }
    }


    private void startAgreementPage(String title,String url){
        Intent intent = new Intent(getActivity(), WebActivity.class);
        intent.putExtra(WebActivity.EXTRA_URL,url);
        intent.putExtra(WebActivity.EXTRA_TITLE,title);
        startActivity(intent);
    }


    /**
     * 完成接口
     */
    public interface OnDone{
       void afterGetPhone(String phone);
    }


}
