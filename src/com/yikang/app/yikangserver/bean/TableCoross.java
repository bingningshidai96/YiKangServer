package com.yikang.app.yikangserver.bean;

import java.util.List;

public class TableCoross {
	private List<CrossWirses> questionGrooup;
	private int surveyTableId;
	private String tableName;

	public List<CrossWirses> getQuestionGrooup() {
		return questionGrooup;
	}

	public void setQuestionGrooup(List<CrossWirses> questionGrooup) {
		this.questionGrooup = questionGrooup;
	}

	public int getSurveyTableId() {
		return surveyTableId;
	}

	public void setSurveyTableId(int surveyTableId) {
		this.surveyTableId = surveyTableId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public String toString() {
		return "TableCoross [questionGrooup=" + questionGrooup
				+ ", surveyTableId=" + surveyTableId + ", tableName="
				+ tableName + "]";
	}

}
