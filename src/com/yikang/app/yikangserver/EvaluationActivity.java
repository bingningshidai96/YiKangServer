package com.yikang.app.yikangserver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.yikang.app.yikangserver.adapter.LeftMenuAdapter;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.data.BusinessState.SenoirState.EvalutionState;
import com.yikang.app.yikangserver.data.QuestionData.TableType;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.fragment.CrossWiresFragment;
import com.yikang.app.yikangserver.fragment.EvaluationMainFragment;
import com.yikang.app.yikangserver.fragment.FallRiskFragment;
import com.yikang.app.yikangserver.utils.HttpUtils;
import com.yikang.app.yikangserver.utils.HttpUtils.ResultCallBack;
import com.yikang.app.yikangserver.utils.LOG;
/**
 * 评估的主页面
 */
public class EvaluationActivity extends BaseActivity implements
		OnItemClickListener,EvaInterActctionListnter{
	private static final String TAG = "EvaluationActivity";
	/**
	 * 左侧侧滑导航栏的ListView
	 */
	private ListView lvNavPapers;
	private LeftMenuAdapter adapter;
	private List<TableType> data;
	private SlidingMenu menu;//侧滑导航栏菜单
	private TextView tvTotalPoint;
	private TextView tvTitle;
	private ImageButton ibtnBack;
	private AlertDialog backDialog;
	
	private static final String STATE_TABLE_LIST = "table_list";
	private HashSet<Integer> submitTableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		EvalutionState.currAssementId = getIntent().getIntExtra("assessmentId", 0);
		EvalutionState.isRecord = getIntent().getBooleanExtra("isRecord", false);
		LOG.d(TAG, "[onCreate]assessmentId"+EvalutionState.isRecord);
		LOG.d(TAG, "[onCreate]isRecord"+EvalutionState.currAssementId);
		submitTableIds = new HashSet<Integer>();
		EvalutionState.stateMap.put(STATE_TABLE_LIST, submitTableIds);
		initContent();
	}

	@Override
	protected void initTitleBar(String title) {
		
		tvTitle = (TextView) findViewById(R.id.tv_title_text);
		if (tvTitle != null) //由于设计上的问题，并不是所有的Activity布局中都有统一的titleBar
			tvTitle.setText(title);

		ImageButton btBack = (ImageButton) findViewById(R.id.ibtn_title_back);
		btBack.setImageResource(R.drawable.icon_drawer_toggle);
		if (btBack != null) //设置返回监听
			btBack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					menu.toggle();//侧滑栏开关
				}
			});
		
		tvTotalPoint = (TextView) findViewById(R.id.tv_title_right_text);
		tvTotalPoint.setGravity(Gravity.CENTER);
	}
	
	
	@Override
	protected void findViews() {}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_evaluation);
	}
	
	
	@Override
	protected void initViewConent() {
		initTitleBar(data.get(0).getTableName()); 
		initSlidingMenu();
		// 将fragment添加到页面中
		initFragmentToShow(data.get(0));
	}

	/**
	 * 获取数据
	 */
	@Override
	protected void getData() {
		data = Arrays.asList(TableType.values());
		if(EvalutionState.isRecord){ //请求接口数据
			loadRecordTabState();
		}
	}
	
	
	/**
	 * 从网络上加载各个表的提交状态
	 */
	private void loadRecordTabState() {
		String url = UrlConstants.URL_GET_TABLE_LIST;
		RequestParam param = new RequestParam();
		param.add("assessmentId", EvalutionState.currAssementId);
		HttpUtils.requestPost(url, param.toParams(), new ResultCallBack() {
			@Override
			public void postResult(String result) {
				try {
					ResponseContent content = ResponseContent.toResposeContent(result);
					String json = content.getData();
					LOG.d(TAG, "[loadRecordTabState]"+json);
					parseJson(json);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			private void parseJson(String json) throws Exception{
				JSONArray array = new JSONArray(json);
				for(int i = 0;i < array.length(); i++){
					JSONObject object = array.getJSONObject(i);
					int isTest = object.getInt("isTest");
					int tableId = object.getInt("surveyTableId");
					if(isTest==1)//1表示提交了
						submitTableIds.add(tableId);
				}
			}
		});
	}
	

	/**
	 * 初始化侧滑菜单
	 */
	private void initSlidingMenu() {
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setBehindOffset((int) (getResources().getDisplayMetrics().density * 150));
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu_drawer_nav);
		adapter = new LeftMenuAdapter(this, data);
		adapter.setSubmitTabs(submitTableIds);
		lvNavPapers = (ListView) menu
				.findViewById(R.id.lv_papers_drawNav_paperList);
		lvNavPapers.setAdapter(adapter);
		lvNavPapers.setOnItemClickListener(this);
		
		menu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
			@Override
			public void onOpen() {
				LOG.d(TAG, "[initSlidingMenu]当前评估表id"+adapter.getCurrentTabId());
				adapter.notifyDataSetChanged();
			}
		});
		ibtnBack = (ImageButton) findViewById(R.id.ibtn_evaluation_back);
		ibtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!EvalutionState.isRecord){
					if(backDialog==null){
						backDialog = createBackDailog();
					}
					backDialog.show();
				}else
					finish();
			}
		});
	}
	
	
	
	/**
	 * 当点击退出时，创建一个dialog提示用户
	 */
	private AlertDialog createBackDailog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage(AppContext.getStrRes(R.string.evaluaton_back_hint))
		.setPositiveButton("确定", new  DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(EvaluationActivity.this,MainActivity.class);
				startActivity(intent);
			}
		})
		.setNegativeButton("继续评估",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(!menu.isMenuShowing())
				 menu.toggle();
			}
		});
		return builder.create();
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		menu.toggle();
		TableType chooseType = data.get(position) ;
		initFragmentToShow(chooseType);
	}

	/**
	 * 根据表的类型初始化一个fragment来展示
	 * @param tableType
	 * @param isEnable 初始化的fragment是否可以交互
	 */
	private void initFragmentToShow(TableType tableType){
		EvalutionState.currTableId = tableType.getTableId(); //设置当前的currentTable
		Fragment fragment = null;
		switch (tableType) {
		case daily_nursing: //生活护理表
		case disease: //疾病的评估
			fragment = CrossWiresFragment.newInstance(tableType);
			break;
		case fall_risk://老人跌倒风险评估
			fragment = FallRiskFragment.newInstance(tableType);
			break;
		default:
			fragment = EvaluationMainFragment.newInstance(tableType);
			break;
		}
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);//增加一个动画
		ft.replace(R.id.fl_evalution_main_container,
				fragment).commit();
		tvTitle.setText(tableType.getTableName());
		tvTotalPoint.setText("");
		//给左边侧滑栏设置当前选中的table
		adapter.setCurrentTab(tableType.getTableId());
	}
	
	/**
	 * 来自于问卷fragment中，用户选择的总分发生改变
	 * @param what
	 * @param param
	 */
	@Override
	public void onInterAction(int what, Object param) {
		if(what == EvaInterActctionListnter.WHAT_POINT){
			float totalPoint = (Float) param;
			String strimPoint = strimPoint(totalPoint);
			tvTotalPoint.setText("总分\n"+(strimPoint.length()>1?" "+strimPoint:strimPoint));
		}else if(what == EvaInterActctionListnter.WHAT_SUBMIT){
			int tableId =(Integer)param;
			submitTableIds.add(tableId);
		}
	}
	
	/**
	 * 如果分数是整数，就不要保留小数部分
	 * @param totalPoint
	 */
	private String strimPoint(float totalPoint){
		if(totalPoint%1==0){
			return  String.valueOf((int)totalPoint);
		}else{
			return String.valueOf(totalPoint);
		}
		
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && !EvalutionState.isRecord){
			if(backDialog == null){
				backDialog = createBackDailog();
			}
			backDialog.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	} 
	 
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EvalutionState.clearAllState();
		LOG.d(TAG, EvalutionState.currAssementId+" "+" "+EvalutionState.stateMap);
	}
	
}
