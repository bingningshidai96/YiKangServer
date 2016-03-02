package com.yikang.app.yikangserver.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.FallRiskAdapter;
import com.yikang.app.yikangserver.adapter.TotalPointChangedListener;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.CrossWirses;
import com.yikang.app.yikangserver.api.RequestParam;
import com.yikang.app.yikangserver.api.ResponseContent;
import com.yikang.app.yikangserver.bean.TableCoross;
import com.yikang.app.yikangserver.data.BusinessState.SenoirState.EvalutionState;
import com.yikang.app.yikangserver.data.EvaluationLocalData;
import com.yikang.app.yikangserver.data.EvaluationLocalData.TableType;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.interf.EvaInterActctionListnter;
import com.yikang.app.yikangserver.api.ApiClient;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.view.NoReactListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FallRiskFragment extends BaseFragment implements
        TotalPointChangedListener, OnClickListener {
    private static final String TAG = "FallRiskFragment";
    private static final String TABLE_TYPR = "table_type";
    private final boolean isRecord = EvalutionState.isRecord;
    public EvaInterActctionListnter listener;
    private Button btSubmit;
    private TextView tvTotalPoint;
    private View totalPointLayout;
    private TableType tableType;
    private ListView lvQuestions;
    private List<CrossWirses> crossWirses;
    private FallRiskAdapter adapter;
    private int surveyTableId;

    /**
     * 获得一个EvaluationMainFragment
     *
     * @return
     */
    public static FallRiskFragment newInstance(TableType type) {
        FallRiskFragment fragment = new FallRiskFragment();
        Bundle args = new Bundle();
        args.putSerializable(TABLE_TYPR, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        if (activity instanceof EvaInterActctionListnter) {
            this.listener = (EvaInterActctionListnter) activity;
        } else {
            throw new IllegalArgumentException(
                    "activity must implemnt interface FragmentInterActionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crossWirses = new ArrayList<>();
        if (getArguments() != null) {
            tableType = (TableType) getArguments().getSerializable(TABLE_TYPR);
            getData();
        }
        initAdapter(tableType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 表示答案是否可以交互
        final boolean isAnswerEnable = !isRecord;

        View footView = inflater.inflate(R.layout.foot_view_button_orange,
                lvQuestions, false);
        btSubmit = (Button) footView.findViewById(R.id.bt_questions_submit); // 提交按钮
        totalPointLayout = footView.findViewById(R.id.ly_total_point_layout); // 显示份数
        tvTotalPoint = (TextView) footView
                .findViewById(R.id.tv_qustions_total_point);
        btSubmit.setOnClickListener(this);

        View view;
        if (isAnswerEnable) { // 不是记录，则初始化可以交互的ListView
            view = inflater.inflate(R.layout.fragment_ecalution_questionlist,
                    container, false);
            lvQuestions = (ListView) view.findViewById(R.id.lv_questions);
        } else {// 如果是记录，则整个页面应该不可交互
            view = inflater.inflate(
                    R.layout.fragment_ecalution_questionlist_no_interacton,
                    container, false);
            lvQuestions = (ListView) view.findViewById(R.id.lv_questions);
            ((NoReactListView) lvQuestions)
                    .setItemsEnable(isAnswerEnable);
            btSubmit.setVisibility(View.GONE); // 将提交按钮隐藏
        }

        lvQuestions.addFooterView(footView);
        lvQuestions.setAdapter(adapter);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_questions_submit) {
            submit();
        }
    }

    @Override
    public void onTotalPointChange(float totalPoint, float increment) {
        if (totalPointLayout.getVisibility() == View.GONE) {
            totalPointLayout.setVisibility(View.VISIBLE);
        }
        tvTotalPoint.setText("" + totalPoint);
        listener.onInterAction(EvaInterActctionListnter.WHAT_POINT, totalPoint);
    }

    /**
     * 初始化adapter
     */
    private void initAdapter(TableType type) {
        switch (type) {
            case fall_risk:
                adapter = new FallRiskAdapter(getActivity(), crossWirses);
                adapter.setTotalPointChangedListener(this);
                break;
            default:
                throw new IllegalStateException("这种类型的表不应该传入此fragment");
        }
    }

    /**
     * 获得数据
     */
    public void getData() {
        // 获取字符串
        String json = EvaluationLocalData.getData(getActivity(), tableType);

        // 解析数据
        TableCoross tableCoross = JSON.parseObject(json, TableCoross.class);
        surveyTableId = tableCoross.getSurveyTableId();
        crossWirses.addAll(tableCoross.getQuestionGrooup());

        @SuppressWarnings("unchecked")
        HashSet<Integer> submitSet = (HashSet<Integer>) EvalutionState.stateMap
                .get(EvalutionState.Keys.TABLE_SUBMIT_LIST);

        if (isRecord || submitSet.contains(tableType.getTableId())) {
            LOG.d(TAG, "加载答案");
            loadRecordAnswers();
        }
    }

    /**
     * 从服务器端加载已经提交的数据
     */
    private void loadRecordAnswers() {
        showWaitingUI();
        String url = UrlConstants.URL_GET_FALLRISK_ANSWER;
        RequestParam param = new RequestParam();
        param.addAll(adapter.toAnwerMap());
        param.add("surveyTableId", EvalutionState.currTableId);
        param.add("assessmentId", EvalutionState.currAssementId);
        LOG.i(TAG, "[loadRecordAnswers]" + EvalutionState.currTableId + "==="
                + EvalutionState.currAssementId);

        ApiClient.postAsyn(url, param, new ApiClient.ResponceCallBack() {

            @Override
            public void onSuccess(ResponseContent content) {
                hideWaitingUI();
                String json = content.getData();
                try {
                    String data = new JSONObject(json).getJSONArray("questionGroups").toString();
                    List<CrossWirses> cWireses = JSON.parseArray(data,
                            CrossWirses.class);
                    adapter.restoreAnswer(cWireses);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.e(TAG, "[loadRecordAnswers]数据解析异常");
                }
            }

            @Override
            public void onFailure(String status, String message) {
                AppContext.showToast(message);
                hideWaitingUI();
            }
        });

    }


    /**
     * 提交数据
     */
    private void submit() {
        showWaitingUI();
        RequestParam param = new RequestParam();
        param.add("surveyTableId", surveyTableId);
        param.add("assessmentId", EvalutionState.currAssementId);
        param.addAll(adapter.toAnwerMap());
        LOG.i(TAG, "[submit]" + UrlConstants.getQustrionSubmitUrl(tableType));


        final String url = UrlConstants.getQustrionSubmitUrl(tableType);
        ApiClient.postAsyn(url, param, new ApiClient.ResponceCallBack() {

            @Override
            public void onSuccess(ResponseContent content) {
                hideWaitingUI();
                AppContext.showToast(R.string.submit_succeed_tip);
                listener.onInterAction(EvaInterActctionListnter.WHAT_SUBMIT,
                        surveyTableId);
            }

            @Override
            public void onFailure(String status, String message) {
                hideWaitingUI();
                AppContext.showToast(message);
            }
        });
    }

}
