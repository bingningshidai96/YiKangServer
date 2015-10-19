package com.yikang.app.yikangserver.bean;

import java.io.Serializable;
import java.util.List;

public class CrossWirses implements Serializable{
	private static final long serialVersionUID = 1L;
	private String questionCrosswiseName;
	private int questionCrosswiseId;
	private String tableName;
	
	private List<QuestionPortrait> questions;

	public String getQuestionCrosswiseName() {
		return questionCrosswiseName;
	}

	public void setQuestionCrosswiseName(String questionCrosswiseName) {
		this.questionCrosswiseName = questionCrosswiseName;
	}

	public int getQuestionCrosswiseId() {
		return questionCrosswiseId;
	}

	public void setQuestionCrosswiseId(int questionCrosswiseId) {
		this.questionCrosswiseId = questionCrosswiseId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public List<QuestionPortrait> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionPortrait> questions) {
		this.questions = questions;
	}

	@Override
	public String toString() {
		return "CrossWirses [questionCrosswiseName=" + questionCrosswiseName
				+ ", questionCrosswiseId=" + questionCrosswiseId
				+ ", tableName=" + tableName + ", questionPortraits="
				+ questions + "]";
	}
	
}
