package com.yikang.app.yikangserver.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.yikang.app.yikangserver.bean.Answer;
import com.yikang.app.yikangserver.bean.Question;
import com.yikang.app.yikangserver.utils.LOG;
import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public abstract class SingleChoiseAdapter extends CommonChoiseAdapter<Question>
		implements OnCheckedChangeListener {
	private static final String TAG = "SigleChoiseAdapter";

	/**
	 * 所有选择的答案都会存放到这个map集合中 key：为某个位置 value:为某个 答案的位置
	 */
	@SuppressLint("UseSparseArrays")
	protected HashMap<Integer, Integer> answerMap = new HashMap<Integer, Integer>();

	public SingleChoiseAdapter(Context context, List<Question> datas,
			int itemLayoutId, int answerItemId) {
		super(context, datas, itemLayoutId, answerItemId);
	}

	/**
	 * 根据所选择的id获得选择的位置
	 * 
	 * @param checkedId
	 * @return
	 */
	protected int getChechedIndex(int checkedId) {
		for (int i = 0; i < buffIds.length; i++) {
			if (buffIds[i] == checkedId)
				return i;
		}
		return -1;
	}

	@Override
	protected void clearCheck(ViewHolder holder) {
		RadioGroup rgAnswers = (RadioGroup) getAnswersContainer(holder);
		rgAnswers.clearCheck();
	}

	@Override
	protected void convert(ViewHolder holder, Question item) {
		super.convert(holder, item);
		RadioGroup rgAnswers = (RadioGroup) getAnswersContainer(holder);
		rgAnswers.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		onAnswerChanged(group, checkedId);
	}

	/**
	 * 当点击事件改变时，即用户选择的答案发生发生的情况
	 * 
	 * @param group
	 * @param checkedId
	 */
	abstract void onAnswerChanged(RadioGroup group, int checkedId);

	@Override
	protected int getAnwerCount(int position) {
		return datas.get(position).getAnswers().size();
	}

	/**
	 * 将答案按照网络接口的要求转换成一个map
	 */
	@Override
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
	public void restoreAnswer(List<Question> questions) {
		for (Question question : questions) {
			int qPosition = -1; // 问题的位置
			int aPosition = -1; // 答案的位置
			final int qId = question.getQuestionId();
			if (question.getAnswers().size() == 0
					|| question.getAnswers().size() > 1) {

				LOG.w(TAG, "这个问题的答案个数不正确");
				continue;// 如果是空的
			}

			for (int q = 0; q < datas.size(); q++) { // 确定问题的位置
				if (datas.get(q).getQuestionId() == qId) {
					qPosition = q;
					break;
				}
			}
			if (qPosition == -1) {
				LOG.e(TAG, "服务器返回了不属于这个表的问题");
				continue;
			}

			List<Answer> ans = datas.get(qPosition).getAnswers();
			if (ans == null)
				continue;
			Answer answer = question.getAnswers().get(0);// 取出答案并确定答案的位置
			for (int a = 0; a < ans.size(); a++) {
				if (ans.get(a).getAnswerId() == answer.getAnswerId()) {
					aPosition = a;
					break;
				}
			}
			if (aPosition == -1)
				continue; // 未找到答案
			answerMap.put(qPosition, aPosition);
		}

	}
}
