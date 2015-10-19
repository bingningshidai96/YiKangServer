package com.yikang.app.yikangserver.bean;

import java.util.List;

public class Table {
	private int surveyTableId;
	private String scaleName;
	private List<Question> questions;
	
	public String getScaleName() {
		return scaleName;
	}
	public void setScaleName(String scaleName) {
		this.scaleName = scaleName;
	}
	public List<Question> getQuestions() {
		return questions;
	}
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
	
	public int getSurveyTableId() {
		return surveyTableId;
	}
	public void setSurveyTableId(int surveyTaleId) {
		this.surveyTableId = surveyTaleId;
	}

}
