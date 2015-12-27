package com.yikang.app.yikangserver.bean;

import java.util.List;

public class QuestionPortrait {
	private String questionPortraitName;
	private int questionPortraitId;
	private int answerType;
	private List<Answer> answers;
	private int weightVal;

	public int getWeightVal() {
		return weightVal;
	}

	public void setWeightVal(int weightVal) {
		this.weightVal = weightVal;
	}

	public String getQuestionPortraitName() {
		return questionPortraitName;
	}

	public void setQuestionPortraitName(String questionPortraitName) {
		this.questionPortraitName = questionPortraitName;
	}

	public int getAnswerType() {
		return answerType;
	}

	public void setAnswerType(int answerType) {
		this.answerType = answerType;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public int getQuestionPortraitId() {
		return questionPortraitId;
	}

	public void setQuestionPortraitId(int questionPortraitId) {
		this.questionPortraitId = questionPortraitId;
	}

	@Override
	public String toString() {
		return "QuestionPortrait [questionPortraitName=" + questionPortraitName
				+ ", questionPortraitId=" + questionPortraitId
				+ ", answerType=" + answerType + ", answers=" + answers + "]";
	}

}
