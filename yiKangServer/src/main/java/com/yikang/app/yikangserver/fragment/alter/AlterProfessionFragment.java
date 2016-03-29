package com.yikang.app.yikangserver.fragment.alter;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.api.ApiTest;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.view.TextSpinner;
import com.yikang.app.yikangserver.view.adapter.PopListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlterProfessionFragment extends BaseAlterFragment implements View.OnClickListener {

    public static final String TAG ="AlterProfessionFragment";
    private TextSpinner tspProfession;
    private int oldValue =-1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCanSubmit = true;

        if (getArguments() != null) {
            oldValue = getArguments().getInt(ARG_OLD_VALUE);
            LOG.i(TAG, "" + mCanSubmit + ">>" + oldValue);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alter_profession, container, false);
        tspProfession = (TextSpinner) view.findViewById(R.id.tsp_alter_profession);

        view.findViewById(R.id.ly_alter_profession).setOnClickListener(this);
        view.findViewById(R.id.bt_confirm).setOnClickListener(this);

        PopListAdapter proLeverAdapter = new PopListAdapter(getActivity(),
                MyData.getItems(MyData.professionMap));

        proLeverAdapter.setCurrentSelected(oldValue);
        tspProfession.setAdapter(proLeverAdapter);
        return view;
    }

    @Override
    public void submit() {
        if(!check()){
            AppContext.showToast("请选择职位");
            return;
        }
        showWaitingUI();
        int newValue = tspProfession.getCurrentSelection();
        ApiTest.applyChangeProfession(newValue, alterHandler); //网络提交，数据返回
    }




    private boolean check() {
        if(tspProfession.getCurrentSelection() <0){
            return false;
        }
        return true;
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
        intent.putExtra(RESULT_EXTRA_RESULT, tspProfession.getCurrentSelection());
        getActivity().setResult(0, intent);
        getActivity().finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_confirm:
                submit();
                break;
            case R.id.ly_alter_profession:
                tspProfession.performClick();
        }

    }
}
