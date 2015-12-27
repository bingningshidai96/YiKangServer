package com.yikang.app.yikangserver.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.bean.Answer;
import com.yikang.app.yikangserver.bean.CrossWirses;
import com.yikang.app.yikangserver.bean.Question;
import com.yikang.app.yikangserver.bean.QuestionPortrait;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.view.TextSpinner;
import com.yikang.app.yikangserver.view.TextSpinner.OnDropDownItemClickListener;
import com.yikang.app.yikangserver.view.adapter.PopListAdapter;

/**
 * 老人跌倒风险评估的适配器
 * 
 */
public class FallRiskAdapter extends CommonChoiseAdapter<CrossWirses> implements
		OnDropDownItemClickListener {

	private static final String TAG = "FallRiskAdapter";

	private Drawable icArrow = null;

	private float totalPoint;

	private Drawable icArrowSelected;
	private Drawable icArrowUnSelected;
	/**
	 * 存放答案的地方
	 */
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, HashMap<Integer, Integer>> answerMap = new HashMap<Integer, HashMap<Integer, Integer>>();

	public FallRiskAdapter(Context context, List<CrossWirses> datas) {
		super(context, datas, R.layout.item_crosswire_fall_risk,
				R.layout.item_portail_fall_risk);
	}

	@Override
	protected int getAnwerCount(int position) {
		return datas.get(position).getQuestions().size();
	}

	@Override
	protected void fillContent(ViewHolder holder, CrossWirses item) {
		int position = holder.getPosition();
		TextView crossWireText = holder.getView(R.id.tv_item_crosswire_topic);
		crossWireText.setText(datas.get(position).getQuestionCrosswiseName());

		LinearLayout lyContainer = (LinearLayout) getAnswersContainer(holder);
		List<QuestionPortrait> portraits = datas.get(position).getQuestions();

		if (portraits != null) { // 这个是下面的多个问题
			// 将数据找到
			for (int i = 0; i < lyContainer.getChildCount(); i++) {
				LinearLayout lyPortait = holder.getView(buffIds[i]);
				QuestionPortrait portrait = portraits.get(i);
				setPotrait(lyPortait, portrait, position, i);
			}
		}
	}

	/**
	 * 设置对应的一个问题
	 * 
	 * @param lyPortait
	 * @param portrait
	 */
	private void setPotrait(ViewGroup lyPortait, QuestionPortrait portrait,
			int crossPosition, int portraPosition) {
		PortailHodler portailHodler = (PortailHodler) lyPortait.getTag();
		if (portailHodler == null) {
			portailHodler = new PortailHodler();
			portailHodler.tvPortailName = (TextView) lyPortait
					.findViewById(R.id.tv_item_quesion_topic);
			portailHodler.tvWeight = (TextView) lyPortait
					.findViewById(R.id.tv_item_question_weight);
			portailHodler.edpPoint = (TextSpinner) lyPortait
					.findViewById(R.id.edp_item_question_answers);
			lyPortait.setTag(portailHodler);
		}

		// 将数据放到相应的空间中去,设置问题和权重
		portailHodler.tvPortailName.setText(portrait.getQuestionPortraitName());
		portailHodler.tvWeight.setText(portrait.getWeightVal() + "");

		// 设置样式
		setEdpStyle(portailHodler.edpPoint, crossPosition, portraPosition);
		// 将所有的答案添加到适配器中
		List<Answer> answers = portrait.getAnswers();
		List<String> list = new ArrayList<String>();
		for (Answer answer : answers) {
			list.add(answer.getAnswerText());
		}
		PopListAdapter adapter = new PopListAdapter(context, list);
		portailHodler.edpPoint.setAdapter(adapter);
		portailHodler.edpPoint.setOnDropDownItemClickListener(this);
		int[] tag = new int[] { crossPosition, portraPosition };// 两个数组分别代表在哪个组，以及那个组中的位置
		portailHodler.edpPoint.setTag(tag);

		if (getCheckAnserValue(crossPosition, portraPosition) != -1) {
			float value = getCheckAnserValue(crossPosition, portraPosition);
			portailHodler.edpPoint.setText(String.valueOf(value));
		} else {
			portailHodler.edpPoint.setText("");
		}

	}

	private void setEdpStyle(TextSpinner edpAnswers, int crossPosition,
			int porPosition) {
		if (isAnswerSelected(crossPosition, porPosition)) { // 设置样式
			setEdpSelectedStyle(edpAnswers);
		} else {
			setEdpUnselectedStyle(edpAnswers);
		}
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

	class PortailHodler {
		public TextView tvPortailName;
		public TextView tvWeight;
		public TextSpinner edpPoint;
	}

	@Override
	protected void clearCheck(ViewHolder holder) {
	}

	@Override
	protected ViewGroup getAnswersContainer(ViewHolder holder) {
		return holder.getView(R.id.item_crosswire_answer_container);
	}

	@Override
	public Map<String, Object> toAnwerMap() {
		Set<Integer> crossKeys = answerMap.keySet();
		LOG.d(TAG, answerMap.toString());
		List<Question> qPramList = new ArrayList<Question>();
		for (Integer ckey : crossKeys) {
			HashMap<Integer, Integer> crossAnswerMap = answerMap.get(ckey);
			Set<Integer> portraitKeys = crossAnswerMap.keySet();

			// 将每一个问题的
			List<QuestionPortrait> portraits = datas.get(ckey).getQuestions();
			for (Integer pkey : portraitKeys) {
				// 将一个问题下所选的答案添加到list里面
				QuestionPortrait portrait = portraits.get(pkey);

				int akey = crossAnswerMap.get(pkey); // 获得选中的答案
				Answer answer = portraits.get(pkey).getAnswers().get(akey);
				List<Answer> aParamList = Arrays.asList(answer);

				Question question = new Question();
				question.setAnswers(aParamList);
				question.setQuestionId(portrait.getQuestionPortraitId());
				qPramList.add(question);
			}

		}
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("questions", qPramList);
		return map;
	}

	/**
	 * 分数改变时的监听器
	 */
	private TotalPointChangedListener listener;

	public void setTotalPointChangedListener(TotalPointChangedListener listener) {
		this.listener = listener;
	}

	@Override
	public void onItemClickListern(TextSpinner spinner, int position) {
		spinner.setBackgroundResource(R.drawable.item_bg_pull_down_done);
		if (icArrow == null) {
			icArrow = context.getResources().getDrawable(
					R.drawable.ic_arrow_pull_down_black);
			icArrow.setBounds(new Rect(0, 0, icArrow.getIntrinsicWidth(),
					icArrow.getIntrinsicHeight()));
		}
		spinner.setCompoundDrawables(null, null, icArrow, null);

		// 计算增量

		// 1.获取之前的值
		int[] tag = (int[]) spinner.getTag();
		int crossPosition = tag[0];
		int porPosition = tag[1];

		float oldValue = getCheckAnserValue(crossPosition, porPosition);
		float newValue = getAnserValue(crossPosition, porPosition, position);
		putAnswer(crossPosition, porPosition, position);
		float increament = (oldValue == -1) ? newValue : newValue - oldValue;

		int weightVal = datas.get(crossPosition).getQuestions()
				.get(porPosition).getWeightVal();
		totalPoint += increament * weightVal;
		// 2.现在的值减去之前的值
		if (listener != null) {
			listener.onTotalPointChange(totalPoint, increament);
		}

	}

	@SuppressLint("UseSparseArrays")
	private void putAnswer(int crossPosition, int porPosition,
			int answerPosition) {
		HashMap<Integer, Integer> crossAnswerMap = answerMap.get(crossPosition);
		if (crossAnswerMap == null) {
			crossAnswerMap = new HashMap<Integer, Integer>();
			answerMap.put(crossPosition, crossAnswerMap);
		}
		crossAnswerMap.put(porPosition, answerPosition);
	}

	/**
	 * 获得所某个问题当前所选的值
	 * 
	 * @param crossPosition
	 * @param porPosition
	 * @param answerPosition
	 * @return 如果没有选择，返回最小值
	 */
	private float getCheckAnserValue(int crossPosition, int porPosition) {
		HashMap<Integer, Integer> crossAnswerMap = answerMap.get(crossPosition);
		if (crossAnswerMap == null) {
			return -1;
		}
		Integer answerPosition = crossAnswerMap.get(porPosition);// 获得答案的位置
		if (answerPosition == null) {
			return -1;
		}
		// 在这个问题中，下表和值是一样的，所以在去data去查询了
		return getAnserValue(crossPosition, porPosition, answerPosition);
	}

	/**
	 * 获取一个问题的答案
	 * 
	 * @param crossPosition
	 * @param porPosition
	 * @param answerPosition
	 * @return
	 */
	private float getAnserValue(int crossPosition, int porPosition,
			int answerPosition) {
		return datas.get(crossPosition).getQuestions().get(porPosition)
				.getAnswers().get(answerPosition).getAnswerVal();
	}

	/**
	 * 计算答案有没有被选择过
	 * 
	 * @param position
	 * @return
	 */
	private boolean isAnswerSelected(int crossPosition, int porPosition) {

		HashMap<Integer, Integer> crossAnswer = answerMap.get(crossPosition);
		if (crossAnswer == null) {
			return false;
		}
		Integer porAnswer = crossAnswer.get(porPosition);
		return porAnswer != null;
	}

	@SuppressLint("UseSparseArrays")
	@Override
	public void restoreAnswer(List<CrossWirses> crossWirses) {
		// 将数据解析到里面
		if (crossWirses == null)
			return;
		for (CrossWirses crossWirse : crossWirses) {
			HashMap<Integer, Integer> cMap = new HashMap<Integer, Integer>();
			// 1.确定每一行的位置,根据id确定
			int cPosition = getCPosition(crossWirse);
			if (cPosition == -1) {
				LOG.e(TAG, "服务器返回了不属于这这个表的行");
				continue;
			}
			// 2.获得每一个问题的位置
			List<QuestionPortrait> queList = crossWirse.getQuestions();
			for (QuestionPortrait questionPortrait : queList) {
				int qPosition = getQPosition(questionPortrait,
						datas.get(cPosition));
				if (qPosition == -1) {
					LOG.e(TAG, "服务器返回了不属于这一行的问题");
					continue;
				}

				int weight = datas.get(cPosition).getQuestions().get(qPosition)
						.getWeightVal();
				// 找出每一个答案的位置
				if (questionPortrait.getAnswers().size() > 1) {
					LOG.w(TAG, "此问题是单选，但服务器返回了多余一个的答案");
					continue;
				}
				Answer answer = questionPortrait.getAnswers().get(0);
				int aPosition = getAPosition(answer, datas.get(cPosition)
						.getQuestions().get(qPosition));

				if (aPosition != -1) {
					cMap.put(qPosition, aPosition);
					totalPoint += answer.getAnswerVal() * weight;
					LOG.d(TAG, answer.toString() + "==" + aPosition);
				}
			}
			queList = null;
			crossWirse = null;
			if (cMap.size() != 0) {
				answerMap.put(cPosition, cMap);
			}
		}
		crossWirses = null;
		listener.onTotalPointChange(totalPoint, totalPoint);
		System.gc();
	}

	private int getAPosition(Answer answer, QuestionPortrait questionPortrait) {
		int aPosition = -1;
		int aId = answer.getAnswerId();
		List<Answer> answers = questionPortrait.getAnswers();
		for (int a = 0; a < answers.size(); a++) {
			if (aId == answers.get(a).getAnswerId()) {
				aPosition = a;
				break;
			}
		}
		return aPosition;
	}

	private int getQPosition(QuestionPortrait questionPortrait,
			CrossWirses crossWirse) {
		int qPosition = -1;
		int qId = questionPortrait.getQuestionPortraitId();
		List<QuestionPortrait> questions = crossWirse.getQuestions();
		for (int q = 0; q < questions.size(); q++) {
			if (qId == questions.get(q).getQuestionPortraitId()) {
				qPosition = q;
				break;
			}
		}
		return qPosition;
	}

	private int getCPosition(CrossWirses crossWirse) {
		int cPosition = -1;
		int cId = crossWirse.getQuestionCrosswiseId();
		for (int c = 0; c < datas.size(); c++) {
			if (cId == datas.get(c).getQuestionCrosswiseId()) {
				cPosition = c;
				break;
			}
		}
		return cPosition;
	}

}
