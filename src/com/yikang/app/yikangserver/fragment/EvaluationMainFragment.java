package com.yikang.app.yikangserver.fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
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
import com.yikang.app.yikangserver.adapter.ChangGuChuangAdapter;
import com.yikang.app.yikangserver.adapter.CommonChoiseAdapter;
import com.yikang.app.yikangserver.adapter.SeniorCommonQuestionAdapter;
import com.yikang.app.yikangserver.adapter.SingleABCDAdapter;
import com.yikang.app.yikangserver.adapter.SingleDepressionAdapter;
import com.yikang.app.yikangserver.adapter.SinglePointChoiseAdapter;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.Question;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.bean.Table;
import com.yikang.app.yikangserver.dailog.DialogFactory;
import com.yikang.app.yikangserver.data.BusinessState.SenoirState.EvalutionState;
import com.yikang.app.yikangserver.data.EvaluationLocalData;
import com.yikang.app.yikangserver.data.EvaluationLocalData.TableType;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.interf.EvaInterActctionListnter;
import com.yikang.app.yikangserver.utils.BuisNetUtils;
import com.yikang.app.yikangserver.utils.HttpUtils;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.view.NoInterActionListView;

public class EvaluationMainFragment extends Fragment implements
		com.yikang.app.yikangserver.adapter.TotalPointChangedListener,
		OnClickListener {
	private static final String TAG = "EvaluationMainFragment";
	private static final String EXTRA_TABLE_TYPR = "table_type";

	private Button btSubmit;
	private TextView tvTotalPoint;
	private View totalPointLayout;

	private TableType tableType;
	private ListView lvQuestions;
	private List<Question> questions;
	private CommonChoiseAdapter<Question> adapter;

	private final int surveyId = EvalutionState.currTableId;
	private final boolean isRecord = EvalutionState.isRecord;

	/**
	 * 获得一个EvaluationMainFragment
	 * 
	 * @param type
	 * @return
	 */
	public static EvaluationMainFragment newInstance(TableType type) {
		EvaluationMainFragment fragment = new EvaluationMainFragment();
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TABLE_TYPR, type);
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
					"activity must implemnt interface FragmentInterActionListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		questions = new ArrayList<Question>();
		if (getArguments() != null) {
			tableType = (TableType) getArguments().getSerializable(
					EXTRA_TABLE_TYPR);
			getData();// 加载需要显示数据，和当数据
		}
		initAdapter(tableType);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 表示答案是否可以交互
		final boolean isAnswerEnable = !isRecord;

		View view = null;
		// 初始化fooetView
		View footView = inflater.inflate(R.layout.foot_view_button_orange,
				lvQuestions, false);
		btSubmit = (Button) footView.findViewById(R.id.bt_questions_submit); // 提交按钮
		totalPointLayout = footView.findViewById(R.id.ly_total_point_layout); // 显示份数
		tvTotalPoint = (TextView) footView
				.findViewById(R.id.tv_qustions_total_point);
		btSubmit.setOnClickListener(this);

		if (isAnswerEnable) { // 不是记录，则初始化可以交互的ListView
			view = inflater.inflate(R.layout.fragment_ecalution_questionlist,
					container, false);
			lvQuestions = (ListView) view.findViewById(R.id.lv_questions);
		} else {// 如果是记录，则整个页面应该不可交互
			view = inflater.inflate(
					R.layout.fragment_ecalution_questionlist_no_interacton,
					container, false);
			lvQuestions = (ListView) view.findViewById(R.id.lv_questions);
			((NoInterActionListView) lvQuestions)
					.setItemsEnable(isAnswerEnable);
			btSubmit.setVisibility(View.GONE); // 将提交按钮隐藏
		}
		lvQuestions.addFooterView(footView);
		lvQuestions.setAdapter(adapter);
		return view;
	}

	@Override
	public void onStop() {
		super.onStop();
		dismissDialog();
		proDialog = null;

	}

	/**
	 * 获得数据
	 */
	private void getData() {
		// 获取字符串
		String json = EvaluationLocalData.getData(getActivity(), tableType);
		// 解析数据
		Table table = JSON.parseObject(json, Table.class);
		questions.addAll(table.getQuestions());
		table = null;

		@SuppressWarnings("unchecked")
		HashSet<Integer> submitSet = (HashSet<Integer>) EvalutionState.stateMap
				.get(EvalutionState.Keys.TABLE_SUBMIT_LIST);

		if (isRecord || submitSet.contains(tableType.getTableId())) {
			LOG.d(TAG, "[getData]加载答案");
			loadRecordAnswers();
		}
	}

	/**
	 * 初始化adapter
	 * 
	 * @param type
	 */
	private void initAdapter(TableType type) {
		switch (type) {
		case daily_life: // 日常生活评估
		case mental_state: // 精神状态评估
		case sensation: // 感知觉评估
		case social_parti: // 社会参与评估
			adapter = new SinglePointChoiseAdapter(getActivity(), questions);
			((SinglePointChoiseAdapter) adapter)
					.setTotalPointChangedListener(this);
			break;
		case depression_self: // 抑郁症自测
			adapter = new SingleABCDAdapter(getActivity(), questions);
			break;
		case depression_other: // 抑郁症评估
			adapter = new SingleDepressionAdapter(getActivity(), questions);
			break;
		case senior_common_question: // 病人常见问题
			adapter = new SeniorCommonQuestionAdapter(getActivity(), questions,
					R.layout.item_answer_radiobutton);
			break;
		case chang_gu_chuang:
			adapter = new ChangGuChuangAdapter(getActivity(), questions);
			((ChangGuChuangAdapter) adapter).setTotalPointChangedListener(this);
			break;
		default:
			throw new IllegalStateException("这种类型的表不应该传入此fragment");
		}
	}

	@Override
	public void onTotalPointChange(float totalPoint, float increment) {
		if (totalPointLayout.getVisibility() == View.GONE) {
			totalPointLayout.setVisibility(View.VISIBLE);
		}
		LOG.d(TAG, "[onTotalPointChange]increment=" + increment);
		tvTotalPoint.setText("" + totalPoint);
		listener.onInterAction(EvaInterActctionListnter.WHAT_POINT, totalPoint);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bt_questions_submit) {
			submit();
		}
	}

	public EvaInterActctionListnter listener;

	/**
	 * 从服务器端加载已经提交的数据
	 */
	private void loadRecordAnswers() {
		showDialog();
		String url = UrlConstants.URL_GET_TABLE_ANSWER;
		RequestParam param = new RequestParam();
		param.add("surveyTableId", surveyId);
		param.add("assessmentId", EvalutionState.currAssementId);

		BuisNetUtils.requestStr(url, param,
				new BuisNetUtils.ResponceCallBack() {

					@Override
					public void onSuccess(ResponseContent content) {
						// TODO Auto-generated method stub
						dismissDialog();
						String json = content.getData();
						LOG.d(TAG, "[loadRecordAnswers]" + json);
						try {
							JSONObject jo = new JSONObject(json);
							String data = jo.getJSONArray("questions")
									.toString();
							List<Question> list = JSON.parseArray(data,
									Question.class);
							adapter.restoreAnswer(list);
							adapter.notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFialure(String status, String message) {
						dismissDialog();
						AppContext.showToast(message);
					}
				});
	}

	/**
	 * 提交数据
	 */
	private void submit() {
		showSubmitDialog();
		RequestParam param = new RequestParam();
		Map<String, Object> map = adapter.toAnwerMap();
		map.put("surveyTableId", surveyId);
		map.put("assessmentId", EvalutionState.currAssementId);
		param.setParamData(map);
		LOG.d(TAG, "[submit]" + UrlConstants.getQustrionSubmitUrl(tableType));

		HttpUtils.requestPost(UrlConstants.getQustrionSubmitUrl(tableType),
				param.toParams(), new HttpUtils.ResultCallBack() {
					@Override
					public void postResult(String result) {
						dismissSubmitDialog();
						try {
							ResponseContent resposeContent = ResponseContent
									.toResposeContent(result);
							if (ResponseContent.STATUS_OK.equals(resposeContent
									.getStatus())) {
								AppContext.showToast("提交成功");
								// 通知主fragment提交成功
								listener.onInterAction(
										EvaInterActctionListnter.WHAT_SUBMIT,
										surveyId);
							} else {
								String failTip = AppContext
										.getStrRes(R.string.submit_fail_redo);
								AppContext.showToast(failTip);
							}
						} catch (Exception e) {
							e.printStackTrace();
							String failTip = AppContext
									.getStrRes(R.string.submit_fail_redo);
							AppContext.showToast(failTip);
						}
					}
				});
	}

	private ProgressDialog proDialog;
	private ProgressDialog loadingDialog;

	private void showSubmitDialog() {
		if (proDialog == null) {
			proDialog = DialogFactory.getProgressDailog(
					DialogFactory.TYPE_SUBMIT_DATA, getActivity());
		}
		proDialog.show();
	}

	private void dismissSubmitDialog() {
		if (proDialog.isShowing()) {
			proDialog.dismiss();
		}
	}

	private void showDialog() {
		if (loadingDialog == null) {
			loadingDialog = DialogFactory.getProgressDailog(
					DialogFactory.TYPE_LOADING_DATA, getActivity());
		}
		loadingDialog.show();
	}

	private void dismissDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}
}