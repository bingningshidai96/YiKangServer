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
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.EvaInterActctionListnter;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.FallRiskAdapter;
import com.yikang.app.yikangserver.adapter.TotalPointChangedListener;
import com.yikang.app.yikangserver.bean.CrossWirses;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.bean.TableCoross;
import com.yikang.app.yikangserver.dailog.ProgressDialogFactory;
import com.yikang.app.yikangserver.data.BusinessState.SenoirState.EvalutionState;
import com.yikang.app.yikangserver.data.QuestionData;
import com.yikang.app.yikangserver.data.QuestionData.TableType;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.utils.HttpUtils;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.utils.HttpUtils.ResultCallBack;
import com.yikang.app.yikangserver.view.NoInterActionListView;

public class FallRiskFragment extends Fragment implements TotalPointChangedListener,OnClickListener{
	private static final String TAG ="FallRiskFragment";
    private static final String TABLE_TYPR = "table_type";
	private final boolean isRecord = EvalutionState.isRecord;
    private Button btSubmit;
    private TextView tvTotalPoint;
    private View totalPointLayout;
    
    private TableType tableType;
    private ListView lvQuestions;
    private List<CrossWirses> crossWirses;
	private FallRiskAdapter adapter;
	private ProgressDialog loadingDialog;
	
	public EvaInterActctionListnter listener;
	private int surveyTableId;
	private ProgressDialog submitDialog;
    
    /**
     * 获得一个EvaluationMainFragment
     * @param type
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
    	if(activity instanceof EvaInterActctionListnter){
    		this.listener = (EvaInterActctionListnter) activity;
    	}else{
    		throw new IllegalArgumentException("activity must implemnt interface FragmentInterActionListener");
    	}
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crossWirses = new ArrayList<CrossWirses>();
        if (getArguments() != null) {
        	tableType = (TableType) getArguments().getSerializable(TABLE_TYPR);
        	getData();
        }
        initAdapter(tableType);
    }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	//表示答案是否可以交互
    	final boolean isAnswerEnable = !isRecord; 
    	View view = null;
    	
    	View footView = inflater.inflate(R.layout.foot_view_button_orange, lvQuestions,false);
		btSubmit = (Button) footView.findViewById(R.id.bt_questions_submit); //提交按钮
		totalPointLayout = footView.findViewById(R.id.ly_total_point_layout); //显示份数
		tvTotalPoint = (TextView) footView.findViewById(R.id.tv_qustions_total_point);
		btSubmit.setOnClickListener(this);
    	
		
		if(isAnswerEnable){ //不是记录，则初始化可以交互的ListView
			view = inflater.inflate(R.layout.fragment_ecalution_questionlist, container, false);
			lvQuestions = (ListView) view.findViewById(R.id.lv_questions);
		}else{//如果是记录，则整个页面应该不可交互
			view = inflater.inflate(R.layout.fragment_ecalution_questionlist_no_interacton, 
					container, false);
			lvQuestions = (ListView) view.findViewById(R.id.lv_questions);
			((NoInterActionListView)lvQuestions).setItemsEnable(isAnswerEnable);
			btSubmit.setVisibility(View.GONE); //将提交按钮隐藏
		}
    	
    	lvQuestions.addFooterView(footView);
    	lvQuestions.setAdapter(adapter);
    	return view;
    }

    @Override
    public void onClick(View v) {
    	// TODO Auto-generated method stub
    	if(v.getId()==R.id.bt_questions_submit){
    		submit();
    	}
    }
    
    @Override
    public void onTotalPointChange(float totalPoint, float increment) {
    	if(totalPointLayout.getVisibility() == View.GONE){
    		totalPointLayout.setVisibility(View.VISIBLE);
    	}
    	tvTotalPoint.setText(""+totalPoint);
    	listener.onInterAction(EvaInterActctionListnter.WHAT_POINT, totalPoint);
    }
    
    /**
     * 初始化adapter
     * @param type
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
    public void getData(){
    	//获取字符串
    	String json = QuestionData.getData(getActivity(), tableType);
    	
    	//解析数据
    	TableCoross tableCoross = JSON.parseObject(json, TableCoross.class);
    	surveyTableId = tableCoross.getSurveyTableId();
    	crossWirses.addAll(tableCoross.getQuestionGrooup());
    	tableCoross = null;
    	
    	@SuppressWarnings("unchecked")
		HashSet<Integer> submitSet = (HashSet<Integer>) EvalutionState.stateMap.get(
    			EvalutionState.Keys.TABLE_SUBMIT_LIST);
    	
    	if(isRecord||submitSet.contains(tableType.getTableId())){
    		LOG.d(TAG, "加载答案");
    		loadRecordAnswers();
    	}
    }

    /**
     * 从服务器端加载已经提交的数据
     */
    private void loadRecordAnswers(){
    	showDialog();
    	String url = UrlConstants.URL_GET_FALLRISK_ANSWER;
    	RequestParam param =new RequestParam();
    	param.add("surveyTableId", EvalutionState.currTableId);
    	param.add("assessmentId", EvalutionState.currAssementId);
    	LOG.i(TAG, "[loadRecordAnswers]"+EvalutionState.currTableId+"==="+EvalutionState.currAssementId);
    	HttpUtils.requestPost(url, param.toParams(), new ResultCallBack() {
			@Override
			public void postResult(String result) {
				dismissDialog();
				try {
					ResponseContent content = ResponseContent.toResposeContent(result);
					String json = content.getData();
					LOG.i(TAG, json);
					String data = new JSONObject(json).getJSONArray("questionGroups").toString();
					List<CrossWirses> cWireses = JSON.parseArray(data, CrossWirses.class);
					adapter.restoreAnswer(cWireses);
    				adapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
					LOG.e(TAG, "[loadRecordAnswers]数据解析异常");
				}
			}
    	});
    }
    
    
	private void showDialog(){
		if(loadingDialog == null){
			loadingDialog = ProgressDialogFactory.getProgressDailog(
					ProgressDialogFactory.TYPE_LOADING_DATA, getActivity());
		}
		loadingDialog.show();
	}
	
	private void dismissDialog(){
		if(loadingDialog!=null&&loadingDialog.isShowing()){
			loadingDialog.dismiss();
		}
	}
    
	private void showSubmitDialog(){
		if(submitDialog==null){
			submitDialog = ProgressDialogFactory.getProgressDailog(
					ProgressDialogFactory.TYPE_SUBMIT_DATA, getActivity());
		}
		submitDialog.show();
	}
	
	private void dismissSubmitDialog(){
		if(submitDialog.isShowing()){
			submitDialog.dismiss();
		}
	}

	/**
	 * 提交数据
	 */
	private void submit() {
		showSubmitDialog();
		RequestParam param = new RequestParam();
		Map<String, Object> map = adapter.toAnwerMap();
		map.put("surveyTableId", surveyTableId);
		map.put("assessmentId", EvalutionState.currAssementId);
		param.setParamData(map);
		
		LOG.i(TAG, "[submit]"+UrlConstants.getQustrionSubmitUrl(tableType));
		HttpUtils.requestPost(UrlConstants.getQustrionSubmitUrl(tableType), param.toParams(), new HttpUtils.ResultCallBack() {
			@Override
			public void postResult(String result) {
				try {
					ResponseContent resposeContent = ResponseContent.toResposeContent(result);
					if(resposeContent.isStautsOk()){
						Toast.makeText(getActivity(), "提交成功", Toast.LENGTH_LONG).show();
						listener.onInterAction(EvaInterActctionListnter.WHAT_SUBMIT, surveyTableId);
					}else{
						Toast.makeText(getActivity(), "提交失败", Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					dismissSubmitDialog();
				}
			}
		});
	}
	
	
}
