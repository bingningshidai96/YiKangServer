package com.yikang.app.yikangserver.adapter;

import java.util.List;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.bean.Answer;
import com.yikang.app.yikangserver.bean.Question;
import com.yikang.app.yikangserver.utils.LOG;
/**
 * 选项为abcd的适配器
 *
 */
public class SingleABCDAdapter extends SingleChoiseAdapter{
	
	private static final String TAG ="SingleABCDAdapter";
	
	public SingleABCDAdapter(Context context, List<Question> datas) {
		super(context, datas, R.layout.item_question_common_single,R.layout.item_answer_radiobutton);
	}

	

	@Override
	protected void fillContent(ViewHolder holder, Question item) {
		int position = holder.getPosition();
		TextView tvTopic = holder.getView(R.id.tv_item_question_topic);
		tvTopic.setText(position+1+"."+datas.get(position).getQuestionText());
		
		RadioGroup rgAnswers = (RadioGroup) getAnswersContainer(holder);
		List<Answer> answerList = datas.get(position).getAnswers();
		for(int i = 0;i<rgAnswers.getChildCount();i++){
			RadioButton rbAnswer = (RadioButton) rgAnswers.getChildAt(i);
			char answerChar = (char)('A'+i);
			rbAnswer.setText(answerChar+". "+answerList.get(i).getAnswerText());
			if(answerMap.get(position)!=null&&answerMap.get(position)==i){
				rbAnswer.setChecked(true);
			}
		}
	}

	@Override
	void onAnswerChanged(RadioGroup group, int checkedId) {
		int index = getChechedIndex(checkedId);
		if(index!=-1){
			Integer position = (Integer) group.getTag();
			answerMap.put(position, index);
			LOG.d(TAG, answerMap.toString());
		}
	}


	@Override
	protected ViewGroup getAnswersContainer(ViewHolder holder) {
		return holder.getView(R.id.rg_item_question_answers);
	}


}
