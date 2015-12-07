package com.yikang.app.yikangserver.adapter;

import java.util.List;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.bean.Question;
import com.yikang.app.yikangserver.utils.LOG;

public class SeniorCommonQuestionAdapter extends SingleChoiseAdapter {
	private static final String TAG = "SeniorCommonQuestionAdapter";

	public SeniorCommonQuestionAdapter(Context context, List<Question> datas,
			int answerItemId) {
		super(context, datas, R.layout.item_question_senior_common_question,
				answerItemId);
	}

	@Override
	protected void fillContent(ViewHolder holder, Question question) {
		int position = holder.getPosition();
		TextView tvTopic = holder.getView(R.id.tv_item_question_topic);
		TextView tvDecreption = holder
				.getView(R.id.tv_item_question_topic_detial);
		tvTopic.setText(position + ". " + question.getQuestionText());
		if (question.getDescription() != null) {
			tvDecreption.setText(question.getDescription());
		}

		RadioGroup rgAnswers = (RadioGroup) getAnswersContainer(holder);
		// 将各个答案的文字填上
		for (int index = 0; index < rgAnswers.getChildCount(); index++) {
			RadioButton answer = (RadioButton) rgAnswers.getChildAt(index);
			answer.setText(question.getAnswers().get(index).getAnswerText());
			if (answerMap.get(position) != null
					&& answerMap.get(position) == index) {
				answer.setChecked(true);
			}

		}
	}

	@Override
	protected ViewGroup getAnswersContainer(ViewHolder holder) {
		return holder.getView(R.id.rg_item_question_answers);
	}

	@Override
	void onAnswerChanged(RadioGroup group, int checkedId) {
		int index = getChechedIndex(checkedId);
		if (index != -1) {
			Integer position = (Integer) group.getTag();
			answerMap.put(position, index);
			LOG.d(TAG, answerMap.toString());
		}
	}

}
