package com.yikang.app.yikangserver.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * 用于获取所有问题相关的数据
 */
public class QuestionData {

	/**
	 * 评估表的本地记录
	 */
	public static enum TableType {
		sensation("questions_sensor.txt", "感知觉与沟通评估", 5), mental_state(
				"questions_mental_state.txt", "精神状态评估", 7), daily_life(
				"questions_daily_life.txt", "日常生活", 8), social_parti(
				"questions_social_participation.txt", "社会参与", 6), depression_self(
				"depression_self_test.txt", "抑郁自测", 9), depression_other(
				"depression_other_test.txt", "抑郁评估", 10), daily_nursing(
				"daily_nursing.txt", "生活护理", 1), disease(
				"desease_evalution.txt", "疾病的评估", 3), senior_common_question(
				"senior_common_question.txt", "老年人常见问题", 11), chang_gu_chuang(
				"questions_chang_gu_chuang.txt", "长谷川痴呆量表", 4), fall_risk(
				"fall_risk.txt", "老年人跌倒风险评估", 2);
		private final String fileName;
		private final String tableName;
		private final int tableId;

		TableType(String fileName, String tableName, int tableId) {
			this.fileName = fileName;
			this.tableName = tableName;
			this.tableId = tableId;
		}

		public String getFileName() {
			return fileName;
		}

		public String getTableName() {
			return tableName;
		}

		public int getTableId() {
			return tableId;
		}

		/**
		 * 获取请求数据的链接
		 * 
		 * @param type
		 * @return
		 */
		public static final String getDataUrl(TableType type) {
			switch (type) {
			case daily_nursing:
				return UrlConstants.URL_EVALUTION_COMMON_CROSSWIRSES_DATA;
			case disease:
				return UrlConstants.URL_EVALUTION_DISEASE_CROSSWIRSES_DATA;
			default:
				return null;
			}
		}

		public static final String getSeconsDataUrl(TableType type) {
			switch (type) {
			case daily_nursing:
			case disease:
				return UrlConstants.URL_EVALUTION_COMMON_PORTAITS_DATA;

			default:
				return null;
			}
		}

	}

	/**
	 * 获得表格json数据
	 * 
	 * @param context
	 * @param type
	 * @return
	 */
	public static String getData(Context context, TableType type) {
		return readFileFromAssert(context, type.getFileName());
	}

	/**
	 * 从资产目录中读取数据
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String readFileFromAssert(Context context, String fileName) {
		AssetManager manager = context.getAssets();
		InputStream inputStream = null;
		try {
			inputStream = manager.open(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			StringBuffer buffer = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			return buffer.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;

	}

}