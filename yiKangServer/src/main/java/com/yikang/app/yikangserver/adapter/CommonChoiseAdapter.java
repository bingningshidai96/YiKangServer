package com.yikang.app.yikangserver.adapter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.utils.LOG;

/**
 * 这是选择问题适配器的基类
 * 
 * @param <T>
 */
public abstract class CommonChoiseAdapter<T> extends CommonAdapter<T> {

	private static final String TAG = "CommonChoiseAdapter";
	private int answerlayId;

	public CommonChoiseAdapter(Context context, List<T> datas,
			int itemLayoutId, int answerItemId) {
		super(context, datas, itemLayoutId);
		this.answerlayId = answerItemId;
	}

	/**
	 * 这是所有的为radioButton所声明的id 这意味着，一个问题最多只能有23个答案
	 */
	protected int[] buffIds = { R.id.choice_item0, R.id.choice_item1,
			R.id.choice_item2, R.id.choice_item3, R.id.choice_item5,
			R.id.choice_item6, R.id.choice_item7, R.id.choice_item8,
			R.id.choice_item9, R.id.choice_item10, R.id.choice_item11,
			R.id.choice_item12, R.id.choice_item13, R.id.choice_item14,
			R.id.choice_item15, R.id.choice_item16, R.id.choice_item17,
			R.id.choice_item18, R.id.choice_item19, R.id.choice_item20,
			R.id.choice_item21, R.id.choice_item22, R.id.choice_item23 };

	/**
	 * 用这个来缓存创建的radioButton,避重复地创建与销毁
	 */
	private Queue<View> buffRdBtns = new LinkedList<View>();

	/**
	 * 为了平衡性能和内存消耗，最大只能缓存的RadioButton的个数
	 */
	private static final int MAX_BURFFED_COUNT = 10;

	@Override
	protected void convert(ViewHolder holder, T item) {
		int position = holder.getPosition();
		clearCheck(holder); // 清除选择
		ViewGroup vgAnswers = getAnswersContainer(holder);
		vgAnswers.setTag(position); // 设置标签
		ajustAnswers(vgAnswers, holder.getPosition(), getAnwerCount(position));// 调整radioButton的个数
		fillContent(holder, item); // 填充数据
	}

	abstract protected int getAnwerCount(int position);

	abstract protected void fillContent(ViewHolder holder, T item);

	abstract protected void clearCheck(ViewHolder holder);

	/**
	 * 获取代表所有答案的ViewGoup
	 * 
	 * @param holder
	 * @return
	 */
	abstract protected ViewGroup getAnswersContainer(ViewHolder holder);

	/**
	 * 将ViewGoup的child个数调整到答案的个数
	 * 
	 * @param rgAnswers
	 * @param answerCounts
	 */
	private void ajustAnswers(ViewGroup answerContainer, int position,
			int answerCounts) {
		int count = answerContainer.getChildCount();
		if (count > answerCounts) {// 多余就移除
			for (int index = count - 1; index >= answerCounts; index--) {
				View answer = answerContainer.getChildAt(index);
				if (buffRdBtns.size() <= MAX_BURFFED_COUNT) { // 超出10个就不缓存了
					buffRdBtns.offer(answer);
				}
				answerContainer.removeView(answer);
			}
		}
		if (count < answerCounts) {// 不够就增加
			for (int index = count; index < answerCounts; index++) {
				View answer = buffRdBtns.poll();
				LOG.i(TAG, answer != null ? "从缓存中获取radioButton" : "");
				if (answer == null) { // 如果缓存为空，就新创建
					answer = inflater.inflate(answerlayId, answerContainer,
							false);
				}
				answer.setId(buffIds[index]);// 将各个id给填上
				answerContainer.addView(answer);
			}
		}
	}

	/**
	 * 将选中的的答案map转换成网络请求参数的
	 * 
	 * @return
	 */
	abstract public Map<String, Object> toAnwerMap();

	/**
	 * 根据从网络上请求的当数据恢复答案
	 * 
	 * @param questions
	 */
	abstract public void restoreAnswer(List<T> questions);

}
