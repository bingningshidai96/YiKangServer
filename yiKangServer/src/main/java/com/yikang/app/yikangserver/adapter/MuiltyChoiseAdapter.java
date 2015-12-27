package com.yikang.app.yikangserver.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.bean.Answer;
import com.yikang.app.yikangserver.bean.Question;
import com.yikang.app.yikangserver.bean.QuestionPortrait;
import com.yikang.app.yikangserver.utils.LOG;

public class MuiltyChoiseAdapter extends CommonChoiseAdapter<QuestionPortrait>
		implements OnCheckedChangeListener {
	private static final String TAG = "MuiltyChoiseAdapter";
	// 当滑动中为false（复用时将之前的item状态清除，会触发事件，但这不是用户自己的选择）
	private Boolean isListenerEnable = true;
	// 用这个来存储答案，每一道题有多个答案，但是不能重复，所以用set
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, HashSet<Integer>> answerMap = new HashMap<Integer, HashSet<Integer>>();

	public MuiltyChoiseAdapter(Context context, List<QuestionPortrait> mDatas) {
		super(context, mDatas, R.layout.item_question_common_multi,
				R.layout.item_answer_checkbox);

	}

	@Override
	protected void fillContent(ViewHolder holder, QuestionPortrait item) {
		isListenerEnable = false; // 让lisnter的onCheckedChanged方法不做操作
		ViewGroup contianer = getAnswersContainer(holder);
		int position = holder.getPosition();
		List<Answer> answerList = datas.get(position).getAnswers();
		TextView tvQuestionText = holder.getView(R.id.tv_item_question_topic);
		tvQuestionText.setText((position + 1) + ". "
				+ datas.get(position).getQuestionPortraitName());
		HashSet<Integer> anwsers = answerMap.get(position);
		for (int i = 0; i < contianer.getChildCount(); i++) {
			CheckBox chkAnswer = (CheckBox) contianer.getChildAt(i);
			int[] tag = { position, i }; // 将位置作为tag
			chkAnswer.setTag(tag);
			chkAnswer.setOnCheckedChangeListener(this); // 设置监听器
			// 填充文字
			chkAnswer.setText(i + 1 + "." + answerList.get(i).getAnswerText());
			if (anwsers != null && anwsers.contains(i)) {
				chkAnswer.setChecked(true);
			}
		}
		isListenerEnable = true; // 重新激活监听
	}

	@Override
	protected void clearCheck(ViewHolder holder) {
		isListenerEnable = false; // 让lisnter的onCheckedChanged方法不做操作
		ViewGroup viewGroup = getAnswersContainer(holder);
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			CheckBox chkAnswer = (CheckBox) viewGroup.getChildAt(i);
			chkAnswer.setChecked(false);
		}
		isListenerEnable = true; // 重新激活监听
	}

	@Override
	protected ViewGroup getAnswersContainer(ViewHolder holder) {
		return holder.getView(R.id.ly_item_question_answers);
	}

	@Override
	public Map<String, Object> toAnwerMap() {
		List<Question> questions = new ArrayList<Question>();
		Set<Integer> keySet = answerMap.keySet();
		for (int key : keySet) {
			int qPostion = key;
			Question question = new Question();
			question.setQuestionId(datas.get(qPostion).getQuestionPortraitId());// 设置id

			// 从所有答案中 挑选选择的答案 添加到参数列表中
			List<Answer> aPramList = new ArrayList<Answer>(); // 用来装所选择的答案
			List<Answer> answers = datas.get(qPostion).getAnswers();
			for (Integer aPosition : answerMap.get(qPostion)) {
				aPramList.add(answers.get(aPosition));
			}
			question.setAnswers(aPramList); // 设置答案
			questions.add(question);// 添加到集合中
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("questions", questions);
		return map;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (!isListenerEnable) { // 这个时候可能是来自于 clearCheck
			return;
		}
		int[] tag = (int[]) buttonView.getTag();
		int questionPosition = tag[0];
		int anwserPosition = tag[1];
		HashSet<Integer> answers = answerMap.get(questionPosition);
		if (isChecked) {
			// 将答案添加到答案结合中
			if (answers == null) {
				answers = new HashSet<Integer>();
				answerMap.put(questionPosition, answers);
			}
			answers.add(anwserPosition);
		} else {
			// 将答案从集合中移除
			if (answers != null) {
				answers.remove(anwserPosition);
				if (answers.size() <= 0) {
					answerMap.remove(questionPosition);
				}
			}
		}

		LOG.i(TAG, answerMap.toString());
	}

	@Override
	protected int getAnwerCount(int position) {
		return datas.get(position).getAnswers().size();
	}

	@Override
	public void restoreAnswer(List<QuestionPortrait> questions) {
		for (QuestionPortrait question : questions) {
			int qPosition = 0;
			List<Answer> ans = null;
			for (int q = 0; q < datas.size(); q++) {
				if (datas.get(q).getQuestionPortraitId() == question
						.getQuestionPortraitId()) {
					qPosition = q;
					ans = datas.get(q).getAnswers();
					break;
				}
			}
			if (ans == null || ans.size() == 0)
				continue;

			List<Answer> answers = question.getAnswers();
			HashSet<Integer> set = new HashSet<Integer>();
			for (Answer answer : answers) {
				for (int a = 0; a < ans.size(); a++) {
					if (ans.get(a).getAnswerId() == answer.getAnswerId()) {
						set.add(a);
						break;
					}
				}
			}
			if (set.size() == 0)
				continue;
			answerMap.put(qPosition, set);
		}
	}

}
