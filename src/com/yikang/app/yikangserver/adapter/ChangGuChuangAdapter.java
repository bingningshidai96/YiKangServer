package com.yikang.app.yikangserver.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.bean.Answer;
import com.yikang.app.yikangserver.bean.Question;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.view.TextSpinner;
import com.yikang.app.yikangserver.view.TextSpinner.OnDropDownItemClickListener;
import com.yikang.app.yikangserver.view.adapter.PopListAdapter;

/**
 * 长谷川评估表的适配器
 */
public class ChangGuChuangAdapter extends CommonChoiseAdapter<Question>
		implements OnDropDownItemClickListener {
	private static final String TAG = "QuestionPullAdapter";
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Integer> answerMap = new HashMap<Integer, Integer>();
	private Drawable icArrowSelected;
	private Drawable icArrowUnSelected;

	public ChangGuChuangAdapter(Context context, List<Question> mDatas) {
		super(context, mDatas, R.layout.item_question_chang_pulldown, 0);
	}

	@Override
	protected void convert(ViewHolder holder, Question question) {
		clearCheck(holder);
		fillContent(holder, question);
	}

	@Override
	protected void fillContent(ViewHolder holder, Question question) {
		int position = holder.getPosition();
		TextView tvTopic = holder.getView(R.id.tv_item_quesion_topic);
		TextSpinner edpAnswers = holder.getView(R.id.edp_item_question_answers);
		tvTopic.setText(position + " " + question.getQuestionText());

		// 创建并设置答案下拉框的适配器
		List<String> answerTextList = new ArrayList<String>();
		for (Answer answer : question.getAnswers()) {
			answerTextList.add(answer.getAnswerText());
		}
		PopListAdapter adapter = new PopListAdapter(context, answerTextList);
		edpAnswers.setAdapter(adapter);

		if (isAnswerSelected(position)) { // 设置样式
			setEdpSelectedStyle(edpAnswers);
		} else {
			setEdpUnselectedStyle(edpAnswers);
		}
		edpAnswers.setOnDropDownItemClickListener(this);
		edpAnswers.setTag(position);
		if (answerMap.containsKey(position)) {
			Integer selectAnswer = answerMap.get(position);
			edpAnswers.setText(String.valueOf(getAnswerValue(position,
					selectAnswer)));
		}
	}

	@Override
	protected void clearCheck(ViewHolder holder) {
		TextSpinner edpAnswers = holder.getView(R.id.edp_item_question_answers);
		edpAnswers.setText("");
	}

	/**
	 * 分数改变时的监听器
	 */
	private TotalPointChangedListener listener;
	private float totalPoint;

	public void setTotalPointChangedListener(TotalPointChangedListener listener) {
		this.listener = listener;
	}

	@Override
	public void onItemClickListern(TextSpinner edtSpinner, int position) {
		int quesPosition = (Integer) edtSpinner.getTag();
		float oldValue = getCheckAnserValue(quesPosition);
		float newValue = getAnswerValue(quesPosition, position);
		float increament = oldValue == -1 ? newValue : newValue - oldValue;
		totalPoint += increament;
		listener.onTotalPointChange(totalPoint, increament);
		if (!isAnswerSelected(quesPosition)) {
			setEdpSelectedStyle(edtSpinner);// 没有选择过就选择
		}
		answerMap.put(quesPosition, position);
		LOG.d(TAG, answerMap.toString());

	}

	/**
	 * 设置控件选中后的样子
	 * 
	 * @param edtSpinner
	 */
	private void setEdpSelectedStyle(TextSpinner edtSpinner) {
		edtSpinner.setBackgroundResource(R.drawable.item_bg_pull_down_done);
		if (icArrowSelected == null) {
			icArrowSelected = context.getResources().getDrawable(
					R.drawable.ic_arrow_pull_down_black);
			icArrowSelected
					.setBounds(new Rect(0, 0, icArrowSelected
							.getIntrinsicWidth(), icArrowSelected
							.getIntrinsicHeight()));
		}

		edtSpinner.setCompoundDrawables(null, null, icArrowSelected, null);
	}

	/**
	 * 设置控件没有选中的样子
	 */
	private void setEdpUnselectedStyle(TextSpinner edtSpinner) {
		edtSpinner.setBackgroundResource(R.drawable.item_bg_pull_down);
		if (icArrowUnSelected == null) {
			icArrowUnSelected = context.getResources().getDrawable(
					R.drawable.ic_arrow_pull_down_white);
			icArrowUnSelected.setBounds(new Rect(0, 0, icArrowUnSelected
					.getIntrinsicWidth(), icArrowUnSelected
					.getIntrinsicHeight()));
		}

		edtSpinner.setCompoundDrawables(null, null, icArrowUnSelected, null);
	}

	/**
	 * 将选中的的答案map转换成网络请求参数的
	 * 
	 * @return
	 */
	public Map<String, Object> toAnwerMap() {
		List<Question> questions = new ArrayList<Question>();
		Set<Integer> keySet = answerMap.keySet();
		for (Integer key : keySet) {
			Question desQustion = new Question();
			Question srcQustion = datas.get(key);
			// 设置id
			desQustion.setQuestionId(srcQustion.getQuestionId());
			// 设置答案
			List<Answer> answers = new ArrayList<Answer>();
			int aPosition = answerMap.get(key);
			answers.add(srcQustion.getAnswers().get(aPosition));
			desQustion.setAnswers(answers);
			// 将包装好的问题放到列表里面
			questions.add(desQustion);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("questions", questions);
		return map;

	}

	@Override
	protected ViewGroup getAnswersContainer(ViewHolder holder) {
		return null;
	}

	@Override
	protected int getAnwerCount(int position) {
		return datas.get(position).getAnswers().size();
	}

	/**
	 * 获得所某个问题当前所选的值
	 * 
	 * @param quesPosition
	 * @param answerPosition
	 * @return 如果没有选择，返回最小值
	 */
	private float getCheckAnserValue(int quesPosition) {
		Integer answerPosition = answerMap.get(quesPosition);
		if (answerPosition == null) {
			return -1;
		}
		return getAnswerValue(quesPosition, answerPosition);
	}

	/**
	 * 获取一个问题的答案
	 * 
	 * @param quesPosition
	 * @param answerPosition
	 * @return
	 */
	private float getAnswerValue(int quesPosition, int answerPosition) {
		List<Answer> answers = datas.get(quesPosition).getAnswers();
		return answers.get(answerPosition).getAnswerVal();
	}

	/**
	 * 计算答案有没有被选择过
	 * 
	 * @param position
	 * @return
	 */
	private boolean isAnswerSelected(int position) {
		Integer integer = answerMap.get(position);
		return integer != null;
	}

	@Override
	public void restoreAnswer(List<Question> questions) {
		for (Question question : questions) {
			int qPosition = -1;
			int aPosition = -1;
			List<Answer> ans = null;
			for (int q = 0; q < datas.size(); q++) {
				if (datas.get(q).getQuestionId() == question.getQuestionId()) {
					qPosition = q;
					ans = datas.get(q).getAnswers();
					break;
				}
			}
			if (qPosition == -1 || ans == null || ans.size() == 0)
				continue;

			Answer answer = question.getAnswers().get(0);
			for (int a = 0; a < ans.size(); a++) {
				if (ans.get(a).getAnswerId() == answer.getAnswerId()) {
					aPosition = a;
					break;
				}
			}
			if (aPosition == -1)
				continue;
			answerMap.put(qPosition, aPosition);
		}
		restoreTotalPosint();
	}

	/**
	 * 通过网络上恢复的当恢复总分
	 */
	private void restoreTotalPosint() {
		totalPoint = 0;
		// 计算总分
		Set<Integer> set = answerMap.keySet();
		for (int qPosition : set) {
			int aPosition = answerMap.get(qPosition);
			Answer answer = datas.get(qPosition).getAnswers().get(aPosition);
			totalPoint += answer.getAnswerVal();
		}
		listener.onTotalPointChange(totalPoint, totalPoint); // 通知改变界面
	}

}
