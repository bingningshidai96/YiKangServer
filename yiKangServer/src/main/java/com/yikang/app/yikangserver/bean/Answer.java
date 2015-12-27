package com.yikang.app.yikangserver.bean;

/**
 * 一个答案
 * 
 * 
 */
public class Answer {
	private String answerText;
	private float answerVal;
	private int answerId;

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	public float getAnswerVal() {
		return answerVal;
	}

	public void setAnswerVal(float answerVal) {
		this.answerVal = answerVal;
	}

	public int getAnswerId() {
		return answerId;
	}

	public void setAnswerId(int answerId) {
		this.answerId = answerId;
	}

	@Override
	public String toString() {
		return "Answer [answerText=" + answerText + ", answerVal=" + answerVal
				+ ", answerId=" + answerId + "]";
	}

}
