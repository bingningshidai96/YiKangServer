package com.yikang.app.yikangserver.adapter;

import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.bean.Answer;
import com.yikang.app.yikangserver.bean.Question;

public class SinglePointChoiseAdapter extends SingleChoiseAdapter {
	private static final String TAG = "SinglePointChoiseAdapter";


	/**
	 * 总分
	 */
	float totalPoint = 0;

	public SinglePointChoiseAdapter(Context context, List<Question> mDatas) {
		super(context, mDatas, R.layout.item_question_common_single,R.layout.item_answer_radiobutton);
	}

	@Override
	protected ViewGroup getAnswersContainer(ViewHolder holder) {
		return holder.getView(R.id.rg_item_question_answers);
	}

	@Override
	protected void fillContent(ViewHolder holder, Question question) {

		RadioGroup rgAnswers = (RadioGroup) getAnswersContainer(holder);
		TextView tvTopic = holder.getView(R.id.tv_item_question_topic);
		LinearLayout lyDcrpts = holder
				.getView(R.id.ly_item_question_answer_decreption);
		setDesciptions(lyDcrpts, question); //设置问题的描述
		
		int position = holder.getPosition();
		// 问题的文字
		tvTopic.setText(position + 1 + ". " + question.getQuestionText());
		
		int checedPosition = getChecedAnswerPosition(position);
		// 将各个答案的文字填上
		for (int index = 0; index < rgAnswers.getChildCount(); index++) {
			RadioButton answer = (RadioButton) rgAnswers.getChildAt(index);
			answer.setText((int)question.getAnswers().get(index).getAnswerVal() + "分  "
					+ question.getAnswers().get(index).getAnswerText());
			if (index == checedPosition) {
				answer.setChecked(true);
			}
		}
		
	}
	
	/**
	 * 返回在position位置上选中的答案位置。没有选择则返回-1
	 * 
	 * @param position
	 * @return
	 */
	private int getChecedAnswerPosition(int position) {
		Integer checkPosition = answerMap.get(position);
		return checkPosition == null? -1 : checkPosition;
	}

	
	/**
	 * 设置问题的描述，问题的描述一般的问题都没有
	 */
	private void setDesciptions(LinearLayout lyDescps, Question question) {
		// 问题的答案描述
		if (question.getDescriptions() != null) {
			List<String> dscrps = question.getDescriptions();
			int count = lyDescps.getChildCount();
			if (count > dscrps.size()) {// 多余就移除
				for (int index = count - 1; index >= dscrps.size(); index--) {
					lyDescps.removeViewAt(index);
				}
			}
			if (count < dscrps.size()) {// 不够就增加
				for (int index = count; index < dscrps.size(); index++) {
					TextView tvDescp = (TextView) inflater.inflate(
							R.layout.item_answer_discription,
							lyDescps, false);
					lyDescps.addView(tvDescp);
				}
			}
			for(int index = 0; index<lyDescps.getChildCount();index++){
				TextView tvDedcrps = (TextView) lyDescps.getChildAt(index);
				tvDedcrps.setText(dscrps.get(index));
			}
			lyDescps.setVisibility(View.VISIBLE);
		} else {
			lyDescps.setVisibility(View.GONE);
		}
	}

	@Override
	void onAnswerChanged(RadioGroup group, int checkedId) {
		if (checkedId == -1) // -1代表没有选中值
			return;

		final int positon = (Integer) group.getTag();
		// 获得这个问题之前这个问题对应的值
		float formerValue = 0f; // 代表上一次选中的值
		
		Integer answerIndex = answerMap.get(positon);
		if(answerIndex!=null){
			formerValue = datas.get(positon).getAnswers().get(answerIndex).getAnswerVal();
		}
		
		// 获得本次所选答案代表的值
		float checkValue = 0;
		for (int i = 0; i < buffIds.length; i++) {
			if (buffIds[i] == checkedId) {
				// 根据所选的id获得这个答案对应的值
				answerMap.put(positon, i);// 将本次选中的值添加到选择的答案集中
				checkValue = datas.get(positon).getAnswers().get(i).getAnswerVal();
				break;
			}
		}
		
		float increment = checkValue - formerValue; // 获得增量
		totalPoint += increment;
		listener.onTotalPointChange(totalPoint, increment);
		Log.i(TAG, "the increment is " + increment);
	}

	/**
	 * 重写父类的方法，增加了计算总分，并通知改变的功能
	 */
	@Override
	public void restoreAnswer(List<Question> questions) {
		super.restoreAnswer(questions); //super值做了答案的回显
		//计算总分
		Set<Integer> set = answerMap.keySet();
		for (int qPosition : set) {
			int aPosition = answerMap.get(qPosition);
			Answer answer = datas.get(qPosition).getAnswers().get(aPosition);
			totalPoint +=answer.getAnswerVal();
		}
		listener.onTotalPointChange(totalPoint, totalPoint); //通知改变界面
	}
	
	/**
	 * 分数改变时的监听器
	 */
	private TotalPointChangedListener listener;

	public void setTotalPointChangedListener(TotalPointChangedListener listener) {
		this.listener = listener;
	}

}
