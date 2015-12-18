package com.yikang.app.yikangserver.data;

import java.util.HashMap;

/**
 * 此类用于存放全局的业务逻辑变量 例如：登录后的职业，用户登录后对评估表的操作
 */
public class BusinessState {
	/**
	 * 这个一个病人的状态
	 * 
	 */
	public static class SenoirState {
		/**
		 * 是否添加过评估记录，如果添加了，将记录下载，下次回到页面时刷新
		 */
		public static boolean hasNewRecord;

		public static String currSeniorId; // 当前病人

		/**
		 * 一次评估的评估状态， 这个类的存在周期应该只在EvaluationActivity的生命周期之内，
		 * 当EvaluationActivity销毁时，为防止内存泄漏，会调用{@link #clearAllState()}
		 * 此类中的状态会全部销毁
		 */
		public static class EvalutionState {
			public static int currAssementId = -1; // 当前评估袋
			public static int currTableId = -1; // 当前表的id，存id的好处就是，如果将来以后扩展表，需要改动，好改
			public static boolean isRecord = false;

			/**
			 * 用于添加其他数据，尤其注意用完后remove掉，避免内存泄漏。 禁止向着这里添加View相关的东西，因为容易造成内存泄漏。
			 */
			public static HashMap<String, Object> stateMap = new HashMap<String, Object>();

			/**
			 * 这个是存放着各种状态的key 在这里面包含其他的存储
			 */
			public static class Keys {
				public static final String TABLE_SUBMIT_LIST = "table_list";
				public static final String ID_SUFF_CROSS_SUBMIT_SET = "_cross_submit_set";

			}

			public static final void clearAllState() {
				isRecord = false;
				currAssementId = -1;
				currTableId = -1;
				stateMap.clear();
			}
		}

	}

}
