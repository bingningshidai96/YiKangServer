package com.yikang.app.yikangserver.fragment.alter;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.Expert;
import com.yikang.app.yikangserver.view.tag.FlowLayout;
import com.yikang.app.yikangserver.view.tag.TagAdapter;
import com.yikang.app.yikangserver.view.tag.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * 修改擅长时的fragment
 */
public class AlterExpertFragment extends BaseAlterFragment{
    private static final String TAG = "AlterExpertFragment";
    public static final String ARG_PROFESSION = "profession";

    private List<Expert> mData = new ArrayList<>();
    private TagFlowLayout mTflTags;

    private int profession;


    private TagAdapter<Expert>  mAdapter = new TagAdapter<Expert>(mData) {
        @Override
        public View getView(FlowLayout parent, int position, Expert expert) {
            LayoutInflater tagInflater = LayoutInflater.from(getActivity());
            TextView tag = ((TextView) tagInflater.inflate(R.layout.item_chk_tag, parent, false));
            tag.setText(mData.get(position).name);
            return tag;
        }
    };

  //  private List<Expert> checkedList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(!getArguments().containsKey(ARG_NEED_SUBMIT)||
                    !getArguments().containsKey(ARG_PROFESSION)){
                throw new IllegalArgumentException("必须传入两个参数"+ARG_NEED_SUBMIT+" 和" +ARG_PROFESSION);
            }
            mCanSubmit = getArguments().getBoolean(ARG_NEED_SUBMIT);
            profession = getArguments().getInt(ARG_PROFESSION);

        }
        loadTags();
    }


    private void loadTags(){
        showWaitingUI();
        Api.getExperts(profession, new ResponseCallback<List<Expert>>() {
            @Override
            public void onSuccess(List<Expert> data) {
                hideWaitingUI();
                mData.clear();
                mData.addAll(data);
                mAdapter.notifyDataChanged();
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).isChecked) {
                        mAdapter.setSelectedList(i);
                    }
                }
            }

            @Override
            public void onFailure(String status, String message) {
                hideWaitingUI();
                AppContext.showToast(message);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alter_expert, container, false);
        mTflTags = (TagFlowLayout) view.findViewById(R.id.id_tfl_expert);
        mTflTags.setMaxSelectCount(4);
        mTflTags.setAdapter(mAdapter);
        return view;
    }




    @Override
    public void submit() {

        if(!check()){return;}

        if(mCanSubmit){ //网络提交数据
            showWaitingUI();
            ArrayList<Expert> checkList = new ArrayList<>();
            for (int index:  mTflTags.getSelectedList()) {
                checkList.add(mData.get(index));
            }
            Api.alterExpert(checkList, alterHandler); //网络提交，数据返回
        }else {
            finishWithResult();
        }
    }

    private boolean check() {
        Set<Integer> selectedList = mTflTags.getSelectedList();
        if(selectedList.isEmpty()){
            AppContext.showToast("请至少选择一个");
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
        ArrayList<Expert> checkList = new ArrayList<>();
        for (int index:  mTflTags.getSelectedList()) {
            checkList.add(mData.get(index));
        }

        Intent intent = new Intent();
        intent.putExtra(RESULT_EXTRA_RESULT, checkList);
        getActivity().setResult(0, intent);
        getActivity().finish();
    }

}
