package com.yikang.app.yikangserver.fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.DetailQuestionActivity;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.CrossWirsesItemAdapter;
import com.yikang.app.yikangserver.bean.CrossWirses;
import com.yikang.app.yikangserver.dailog.ProgressDialogFactory;
import com.yikang.app.yikangserver.data.BusinessState.SenoirState.EvalutionState;
import com.yikang.app.yikangserver.data.QuestionData;
import com.yikang.app.yikangserver.data.QuestionData.TableType;
import com.yikang.app.yikangserver.utils.LOG;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class CrossWiresFragment extends Fragment implements OnItemClickListener{
	private static final String TAG = "CrossWiresFragment";
	private static final String TABLE_TYPR = "table_type";
	public static final String STATE_SUFF_CROSS_LIST = "_cross_list";
	
	
	private ListView lvQuestions;
	private CrossWirsesItemAdapter mAdapter;
	
	private TableType tableType;
	private List<CrossWirses> data = new ArrayList<CrossWirses>();
	private ProgressDialog loadingDialog;
	/**
     * 获得一个EvaluationMainFragment
     * @param type
     * @return
     */
    public static CrossWiresFragment newInstance(TableType type) {
    	CrossWiresFragment fragment = new CrossWiresFragment();
        Bundle args = new Bundle();
        args.putSerializable(TABLE_TYPR, type);
        fragment.setArguments(args);
        return fragment;
    }
	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        	tableType = (TableType) getArguments().getSerializable(TABLE_TYPR);
        	getData();
        }else{
        	throw new IllegalArgumentException("传入的参数不能为null");
        }
        initAdapter(tableType);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_evaluation_columlist, container, false);
		//listView展示各行的数据
		lvQuestions = (ListView) view.findViewById(R.id.lv_questions);
		lvQuestions.setAdapter(mAdapter);
		lvQuestions.setOnItemClickListener(this);
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		LOG.d(TAG, "onStart");
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if(loadingDialog!=null&&loadingDialog.isShowing()){
			loadingDialog.dismiss();
		}
		loadingDialog = null;
	}
	
	 /**
     * 初始化adapter
     * @param type
     */
    private void initAdapter(TableType type) {
    	mAdapter = new CrossWirsesItemAdapter(getActivity(), data);
    	setSubmitedSet();
	}
    /**
     * 为adapter设置哪些item已经提交成功，哪些没有提交
     */
    private void setSubmitedSet(){
    	
    	String suffix = EvalutionState.Keys.ID_SUFF_CROSS_SUBMIT_SET;
    	@SuppressWarnings("unchecked")
		HashSet<Integer> sbumitSet = (HashSet<Integer>) EvalutionState.stateMap.get(
				tableType.getTableName()+suffix);
    	if(sbumitSet == null){
    		sbumitSet = new HashSet<Integer>();
    		EvalutionState.stateMap.put(tableType.getTableName()+suffix, sbumitSet);
    	}
    	mAdapter.setSubmitedSet(sbumitSet);
    }
    
    
    /**
     * 获得数据
     */
    public void getData(){
    	loadDataFromLoacl();
    }
    
    private void showLoadingDialog(){
    	if(loadingDialog==null){
    		loadingDialog = ProgressDialogFactory.getProgressDailog(
    				ProgressDialogFactory.TYPE_LOADING_DATA, getActivity());
    	}
    	loadingDialog.show();
    }
    
    private void dimissLoadingDialog(){
    	if(loadingDialog!=null){
    		loadingDialog.dismiss();
    	}
    }
    
    
    private void loadDataFromLoacl(){
    	showLoadingDialog();
    	String json = QuestionData.getData(getActivity(), tableType);
		try {
			JSONObject object = new JSONArray(json).getJSONObject(0);
			//解析问题
			String dataJson = object.getJSONArray("questions").toString();
			List<CrossWirses> list = JSON.parseArray(dataJson, CrossWirses.class);
			//LOG.d(TAG, "[loadDataFromLoacl]解析的数据"+list.toString());
			data.addAll(list);	
			
		} catch (JSONException e) {
			e.printStackTrace();
			LOG.e(TAG, "loadDataFromLoacl"+"解析本地数据异常>>>"+tableType);
		}
		dimissLoadingDialog();
    }
    
//    private void loadDataFromNet(){
//    	showLoadingDialog();
//    	String appId = AppContext.getAppContext().getAppId();
//    	String accessTicket = AppContext.getAppContext().getAccessTicket();
//    	RequestParam param = new RequestParam(appId,accessTicket);
//    	HttpUtils.requestGet(TableType.getDataUrl(tableType)+"?"+param.toUrlParam(),
//    			new HttpUtils.ResultCallBack() {
//					@Override
//					public void postResult(String result) {
//						try {
//							ResponseContent response = ResponseContent.toResposeContent(result);
//							String json = response.getData();
//							LOG.d(TAG, "json=="+json);
//							
//							JSONObject object = new JSONArray(json).getJSONObject(0);
//							//解析问题
//							String dataJson = object.getJSONArray("questions").toString();
//							List<CrossWirses> list = JSON.parseArray(dataJson, CrossWirses.class);
//							LOG.i(TAG, "===="+list.toString());
//							data.clear();
//							data.addAll(list);	
//						} catch (Exception e) {
//							e.printStackTrace();
//						}finally{
//							dimissLoadingDialog();
//						}
//					}
//		     });
//    } 

    /**
	 * 获取请求数据的链接
	 */
	public String getBaseDataUrl(TableType type){
		return TableType.getSeconsDataUrl(type);
	}
    
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//点击后表示已经
		EvalutionState.stateMap.put(tableType.getTableName()+STATE_SUFF_CROSS_LIST, data);
		Intent intent = new Intent(getActivity(), DetailQuestionActivity.class);
		intent.putExtra("position", position);
		intent.putExtra("table_type",tableType);
		startActivity(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//String suffix = EvalutionState.Keys.ID_SUFF_CROSS_SUBMIT_SET;
		EvalutionState.stateMap.remove(tableType.getTableName()+STATE_SUFF_CROSS_LIST);
		//EvalutionState.stateMap.remove(tableType.getTableName()+suffix);
	}
	

}
