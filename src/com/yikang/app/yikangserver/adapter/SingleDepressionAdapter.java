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

public class SingleDepressionAdapter extends SingleChoiseAdapter{

	private static final String TAG = "SingleDepressionAdapter";
	
	public SingleDepressionAdapter(Context context, List<Question> datas) {
		super(context, datas, R.layout.item_question_two_choice_horiztontal,R.layout.item_answer_radiobutton);
	}

	
	@Override
	protected void fillContent(ViewHolder holder, Question item) {
		int position = holder.getPosition();
		TextView tvTopic = holder.getView(R.id.tv_item_question_h_topic);
		tvTopic.setText(datas.get(position).getQuestionText());
		
		RadioGroup rgAnswers = (RadioGroup) getAnswersContainer(holder);
		for(int i = 0;i<rgAnswers.getChildCount();i++){
			RadioButton rbAnswer = (RadioButton) rgAnswers.getChildAt(i);
			if(answerMap.get(position)!=null&&answerMap.get(position)==i){
				rbAnswer.setChecked(true);
			}
		}
	}

	@Override
	protected int getChechedIndex(int checkedId) {
		switch (checkedId) {
		case R.id.rb_item_question_h_yes:
			return 0;
		case R.id.rb_item_question_h_no:
			return 1;
		default:
			return -1;
		}
	}
	
	@Override
	void onAnswerChanged(RadioGroup group, int checkedId) {
		int index = getChechedIndex(checkedId);
		if(index != -1){
			answerMap.put((Integer) group.getTag(), index);
		}
		LOG.d(TAG, answerMap.toString());
	}


	@Override
	protected ViewGroup getAnswersContainer(ViewHolder holder) {
		return holder.getView(R.id.rg_item_question_h_answers);
	}

}
