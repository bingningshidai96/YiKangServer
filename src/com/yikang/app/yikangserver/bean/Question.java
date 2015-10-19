package com.yikang.app.yikangserver.bean;

import java.util.List;

public class Question {
	
	private int questionId;
	private String questionText;
	private List<Answer> answers;
	private String questionName;
	private List<String> descriptions;
	private String description;
	
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public List<Answer> getAnswers() {
		return answers;
	}
	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
	
	public String getQuestionName() {
		return questionName;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}
	
	public List<String> getDescriptions() {
		return descriptions;
	}
	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "Question [questionId=" + questionId + ", questionText="
				+ questionText + ", answers=" + answers + ", questionName="
				+ questionName + "]";
	}
	
	
	
}
