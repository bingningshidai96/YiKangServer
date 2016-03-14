package com.yikang.app.yikangserver.fragment;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.MuiltyChoiseAdapter;
import com.yikang.app.yikangserver.api.parser.GsonFactory;
import com.yikang.app.yikangserver.bean.CrossWirses;
import com.yikang.app.yikangserver.bean.QuestionPortrait;
import com.yikang.app.yikangserver.api.client.RequestParam;
import com.yikang.app.yikangserver.data.BusinessState.SenoirState.EvalutionState;
import com.yikang.app.yikangserver.data.EvaluationLocalData;
import com.yikang.app.yikangserver.data.EvaluationLocalData.TableType;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.interf.EvaInterActctionListnter;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.view.NoReactListView;

public class DetailQuestionFragment extends BaseFragment implements OnClickListener {
    private static final String TAG = "DetailQuestionFragment";
    private static final String DATA = "data";
    private Button btSubmit;
    private ListView lvQuestions;

    private CrossWirses cWirses;

    /**
     * 要展示的数据源
     */
    private List<QuestionPortrait> questions;
    private MuiltyChoiseAdapter adapter;
    /**
     * 与activity交互的接口
     */
    public EvaInterActctionListnter listener;

    private boolean isRecord = EvalutionState.isRecord; // 答案是否可以被点击
    private TableType tableType; //

    /**
     * 获得一个EvaluationMainFragment
     *
     * @param tableType
     * @return
     */
    public static DetailQuestionFragment newInstance(CrossWirses cWirses,
                                                     TableType tableType) {
        DetailQuestionFragment fragment = new DetailQuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable(DATA, (Serializable) cWirses);
        args.putSerializable("tableType", tableType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof EvaInterActctionListnter) {
            this.listener = (EvaInterActctionListnter) activity;
        } else {
            throw new IllegalArgumentException(
                    "activity must implemnt interface EvaInterActctionListnter");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cWirses = (CrossWirses) getArguments().getSerializable(DATA);
        tableType = (TableType) getArguments().getSerializable("tableType");
        getData();
        initAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final boolean isAnswerEnable = !isRecord;
        View footView = inflater.inflate(R.layout.foot_view_button_orange,
                lvQuestions, false);
        btSubmit = (Button) footView.findViewById(R.id.bt_questions_submit);
        btSubmit.setOnClickListener(this);

        View view = null;
        if (isAnswerEnable) {
            view = inflater.inflate(R.layout.fragment_ecalution_questionlist,
                    container, false);
            lvQuestions = (ListView) view.findViewById(R.id.lv_questions);
        } else {
            LOG.i(TAG, "[onCreateView]创建不能交互的的ListView");
            view = inflater.inflate(
                    R.layout.fragment_ecalution_questionlist_no_interacton,
                    container, false);
            lvQuestions = (ListView) view.findViewById(R.id.lv_questions);
            ((NoReactListView) lvQuestions)
                    .setItemsEnable(isAnswerEnable);
            btSubmit.setVisibility(View.GONE); // 将提交按钮隐藏
        }
        lvQuestions.addFooterView(footView);
        lvQuestions = (ListView) view.findViewById(R.id.lv_questions);
        lvQuestions.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_questions_submit) {
            Map<String, Object> map = adapter.toAnwerMap();
            LOG.d(TAG, "[onClick]数据提交参数：" + map.toString());
            // 将选好的数据传编程一个hashMap,递到activity中提交，
            submit(map);
        }
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        adapter = new MuiltyChoiseAdapter(getActivity(), questions);
    }

    /**
     * 加载问题，文字等数据
     */
    protected void getData() {
        questions = new ArrayList<QuestionPortrait>();
        showWaitingUI();
        String json = EvaluationLocalData.readFileFromAssert(getActivity(),
                "daily_nursing_detials.txt");
        String data = null;
        try {
            JSONArray jarray = new JSONArray(json);
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                if (jo.getInt("questionCrosswiseId") == cWirses
                        .getQuestionCrosswiseId()) {
                    data = jo.getJSONArray("questions").toString();
                    break;
                }
            }
            Gson gson = GsonFactory.newInstance();
            Type type = new TypeToken<List<QuestionPortrait>>() {}.getType();
            List<QuestionPortrait> portraits= gson.fromJson(data, type);


            questions.addAll(portraits);
            cWirses.setQuestions(questions);
            hideWaitingUI();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 判断是否加载答案
        @SuppressWarnings("unchecked")
        HashSet<Integer> submitSet = (HashSet<Integer>) EvalutionState.stateMap
                .get(EvalutionState.Keys.TABLE_SUBMIT_LIST);

        String suffix = EvalutionState.Keys.ID_SUFF_CROSS_SUBMIT_SET;
        String key = tableType.getTableName() + suffix;
        // 表示哪些item被提交过了
        @SuppressWarnings("unchecked")
        HashSet<Integer> crossSet = (HashSet<Integer>) EvalutionState.stateMap
                .get(key);

        if (isRecord
                || (submitSet.contains(EvalutionState.currTableId) && crossSet
                .contains(cWirses.getQuestionCrosswiseId()))) {
            LOG.d(TAG, "[getData]加载答案");
            loadRecordAnswers();
        }
    }

    /**
     * 加载答案的数据
     */
    private void loadRecordAnswers() {
        hideWaitingUI();
        String url = UrlConstants.URL_GET_CROSS_ANSWER;
        RequestParam params = new RequestParam();
        params.add("assessmentId", EvalutionState.currAssementId);
        params.add("surveyTableId", EvalutionState.currTableId);
        params.add("questionCrosswiseId", cWirses.getQuestionCrosswiseId());
        //TODO: 网络请求变更

//        ApiClient.execute(url, params, new ApiClient.ResponseCallback() {
//
//            @Override
//            public void onSuccess(ResponseContent content) {
//                hideWaitingUI();
//                String json = content.getData();
//                LOG.i(TAG, json);
//                try {
//                    JSONObject jo = new JSONObject(json);
//                    String data = jo.getJSONArray("questions").toString();
//                    List<QuestionPortrait> portraits = JSON.parseArray(data,
//                            QuestionPortrait.class);
//                    adapter.restoreAnswer(portraits);
//                    adapter.notifyDataSetChanged();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    LOG.e(TAG, "数据解析出错" + e.getMessage());
//                    AppContext.showToast("");
//                }
//            }
//
//            @Override
//            public void onFailure(String status, String message) {
//                hideWaitingUI();
//                AppContext.showToast(message);
//
//            }
//        });



    }

    /**
     * 提交数据
     *
     * @param paramData
     */
    private void submit(Map<String, Object> paramData) {
        showWaitingUI();
        // 开始提交数据
        RequestParam param = new RequestParam();
        param.add("surveyTableId", EvalutionState.currTableId);
        param.add("assessmentId", EvalutionState.currAssementId);
        param.add("questionCrosswiseId", cWirses.getQuestionCrosswiseId());
        param.addAll(paramData);

        String url = UrlConstants.getQustrionSubmitUrl(tableType);

        //TODO: 忘了请求变更
//        ApiClient.execute(url, param, new ApiClient.ResponseCallback() {
//
//            @Override
//            public void onSuccess(ResponseContent content) {
//                hideWaitingUI();
//                AppContext.showToast(R.string.submit_succeed_tip);
//                listener.onInterAction(
//                        EvaInterActctionListnter.WHAT_SUBMIT,
//                        cWirses.getQuestionCrosswiseId());
//            }
//
//            @Override
//            public void onFailure(String status, String message) {
//                hideWaitingUI();
//                AppContext.showToast(message);
//            }
//        });
   }
}
