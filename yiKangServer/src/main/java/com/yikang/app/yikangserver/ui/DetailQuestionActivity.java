package com.yikang.app.yikangserver.ui;

import java.util.HashSet;
import java.util.List;
import android.os.Bundle;
import android.widget.TextView;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.CrossWirses;
import com.yikang.app.yikangserver.data.BusinessState.SenoirState.EvalutionState;
import com.yikang.app.yikangserver.data.EvaluationLocalData.TableType;
import com.yikang.app.yikangserver.fragment.CrossWiresFragment;
import com.yikang.app.yikangserver.fragment.DetailQuestionFragment;
import com.yikang.app.yikangserver.interf.EvaInterActctionListnter;
import com.yikang.app.yikangserver.utils.LOG;

/**
 * 从生活护理等表中点击一个cell，变跳到这个页面 需要传入的参数有 这个activity中的数据不需要要从网络上加载，加载数据是fragment自己的事情
 */
public class DetailQuestionActivity extends BaseActivity implements
		EvaInterActctionListnter {
	protected static final String TAG = "DetailQuestionActivity";
	// 这是在CrossWiresFragment存放的数据，用于自动跳转到下一页
	private List<CrossWirses> crossList;
	private int currentPosition; // 记录当前是哪一个crossWire

	private TableType tableType; // 表的类型

	private TextView tvTitile;

	private final int surveyTableId = EvalutionState.currTableId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取数据
		currentPosition = getIntent().getIntExtra("position", 0);
		tableType = (TableType) getIntent().getSerializableExtra("table_type");
		initContent();
		initTitleBar(crossList.get(currentPosition).getQuestionCrosswiseName());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		String key = tableType.getTableName()
				+ CrossWiresFragment.STATE_SUFF_CROSS_LIST;
		EvalutionState.stateMap.remove(key);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_evaluation);
	}

	@Override
	protected void findViews() {
	}

	/**
	 * 加载数据
	 */

	@SuppressWarnings("unchecked")
	@Override
	protected void getData() {
		// 这是在CrossWiresFragment存放的数据，用于自动跳转到下一页
		String key = tableType.getTableName()
				+ CrossWiresFragment.STATE_SUFF_CROSS_LIST;
		crossList = (List<CrossWirses>) EvalutionState.stateMap.get(key);
	}

	@Override
	protected void initViewContent() {
		initFragmentToShow(); // 初始化一个fragment
	}

	@Override
	protected void initTitleBar(String title) {
		super.initTitleBar(title);
		tvTitile = (TextView) findViewById(R.id.tv_title_text);
	}

	@Override
	public void onInterAction(int what, Object paramData) {
		if (what == EvaInterActctionListnter.WHAT_SUBMIT) {
			setSumitSuccess();
		}
	}

	/**
	 * 初始化一个fragment来显示数据
	 */
	private void initFragmentToShow() {
		DetailQuestionFragment fragment = DetailQuestionFragment.newInstance(
				crossList.get(currentPosition), tableType);

		getFragmentManager().beginTransaction()
				.replace(R.id.fl_evalution_main_container, fragment).commit();

	}

	/**
	 * 设置提交成功，做3件事 1.在记录已提交状态的表的 集合中加上这张表 2.在记录已提交的cross 的集合中添加此cross 3.跳到下一行
	 */
	private void setSumitSuccess() {
		String suffix = EvalutionState.Keys.ID_SUFF_CROSS_SUBMIT_SET;
		String key = tableType.getTableName() + suffix;
		// 表示哪些item被提交过了
		@SuppressWarnings("unchecked")
		HashSet<Integer> crossSet = (HashSet<Integer>) EvalutionState.stateMap
				.get(key);
		crossSet.add(crossList.get(currentPosition).getQuestionCrosswiseId());

		// 表示这个表已经被提交过了。我们的策略是，只要有一个item被提交过了，这个表就算是提交了
		@SuppressWarnings("unchecked")
		HashSet<Integer> tableSet = (HashSet<Integer>) EvalutionState.stateMap
				.get(EvalutionState.Keys.TABLE_SUBMIT_LIST);
		tableSet.add(surveyTableId);
		// 跳到下一个cross
		nextCross();
	}

	/**
	 * 跳到下一个字item
	 */
	private void nextCross() {
		LOG.d(TAG, "[nextCross]" + currentPosition);
		if (currentPosition >= crossList.size() - 1) {
			// 提示
			AppContext.showToast("此评估表已经全部评估完成");
			return;
		}
		currentPosition++;
		initFragmentToShow();
		tvTitile.setText(crossList.get(currentPosition)
				.getQuestionCrosswiseName());
	}

	/**
	 * 获取请求数据的链接
	 */
	public String getBaseDataUrl(TableType type) {
		return TableType.getSeconsDataUrl(tableType);
	}

}

