package com.yikang.app.yikangserver.ui;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.RecordAdapter;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.EvalutionRecord;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.dailog.DialogFactory;
import com.yikang.app.yikangserver.data.BusinessState.SenoirState;
import com.yikang.app.yikangserver.data.BusinessState.SenoirState.EvalutionState;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.utils.BuisNetUtils;
import com.yikang.app.yikangserver.utils.HttpUtils;
import com.yikang.app.yikangserver.utils.LOG;

public class EvaluationRecordActivity extends BaseActivity implements
		OnItemClickListener {
	protected static final String TAG = "EvaluationRecordActivity";
	public static final String EXTRA_SENIORID = "seniorId";

	private List<EvalutionRecord> datas;

	private GridView griView;
	private RecordAdapter adapter;

	private ProgressDialog loadingDialog;

	// 是否新建了病历夹,新建了之后就要刷新数据
	private boolean hasNewRecord = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String seniorId = getIntent().getStringExtra(EXTRA_SENIORID);
		SenoirState.currSeniorId = seniorId;
		initContent();
		initTitleBar(getString(R.string.evaluation_record_title));
	}

	@Override
	protected void findViews() {
		griView = (GridView) findViewById(R.id.gv_eval_records);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_evaluation_record);
	}

	@Override
	protected void getData() {
		datas = new ArrayList<EvalutionRecord>();
	}

	@Override
	protected void initViewConent() {
		adapter = new RecordAdapter(datas, this);
		griView.setAdapter(adapter);
		griView.setOnItemClickListener(this);
	}

	private void showDialog() {
		if (loadingDialog == null) {
			loadingDialog = DialogFactory.getProgressDailog(
					DialogFactory.TYPE_LOADING_DATA, this);
		}
		loadingDialog.show();
	}

	private void dismissDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (hasNewRecord) {
			laodDataFromNet();
		}

	}

	// 从网络上获取数据
	private void laodDataFromNet() {
		showDialog();
		String url = UrlConstants.URL_GET_EVALUATION_RECORD;
		RequestParam param = new RequestParam();
		param.add("seniorId", SenoirState.currSeniorId);
		BuisNetUtils.requestStr(url, param,
				new BuisNetUtils.ResponceCallBack() {
					@Override
					public void onSuccess(ResponseContent content) {
						dismissDialog();
						String json = content.getData();
						datas.clear();
						hasNewRecord = false;
						LOG.d(TAG, "====" + json);
						if (!TextUtils.isEmpty(json)) {
							List<EvalutionRecord> list = JSON.parseArray(json,
									EvalutionRecord.class);
							datas.addAll(list);
							int profession = AppContext.getAppContext()
									.getUser().profession;
							if (profession != MyData.DOCTOR) {
								adapter.setAddEnable(true);
							}
							adapter.notifyDataSetChanged();
						}
					}

					@Override
					public void onFialure(String status, String message) {
						dismissDialog();
						AppContext.showToast(EvaluationRecordActivity.this,
								message);
					}
				});
	}

	private void newEvaluationRecord() {
		showDialog();
		RequestParam param = new RequestParam();
		param.add("seniorId", SenoirState.currSeniorId);
		HttpUtils.requestPost(UrlConstants.URL_ADD_EVALUATION_BAG,
				param.toParams(), new HttpUtils.ResultCallBack() {
					@Override
					public void postResult(String result) {
						try {
							dismissDialog();
							ResponseContent reContent = ResponseContent
									.toResposeContent(result);
							String status = reContent.getStatus();
							if (ResponseContent.STATUS_OK.equals(status)) {
								AppContext.showToast(AppContext
										.getStrRes(R.string.sucess_create_record));
								int assessmentId = new JSONObject(reContent
										.getData()).getInt("assessmentId");
								LOG.w(TAG, "[newEvaluationRecord]"
										+ assessmentId);
								toEvaluationPage(assessmentId, false);
								hasNewRecord = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 跳转到评估页面
	 * 
	 * @param assessmentId
	 *            评估记录的id
	 * @param isRecord
	 *            是否是从评估记录页面中跳转过去的 如果ture,则评估页面只做展示，不做相关的交互功能
	 */
	private void toEvaluationPage(int assessmentId, boolean isRecord) {
		Intent intent = new Intent(EvaluationRecordActivity.this,
				EvaluationActivity.class);
		intent.putExtra("assessmentId", assessmentId);
		intent.putExtra("isRecord", isRecord);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EvalutionState.isRecord = false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		LOG.i(TAG, "" + position);
		if (position == datas.size()) {
			newEvaluationRecord(); // 新建一个评估袋，并跳转到评估页面
		} else {
			int assessmentId = datas.get(position).getAssessmentId();
			toEvaluationPage(assessmentId, true);
		}
	}

}