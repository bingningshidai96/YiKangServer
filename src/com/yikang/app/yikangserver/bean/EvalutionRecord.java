package com.yikang.app.yikangserver.bean;

public class EvalutionRecord {
	private int appraisersId; //评估服务人员的id
	private String appraisersName;  //评估人员的名字
	private int assessmentId; //评估袋的id
	private String assessmentName; //评估袋的名字
	private String assessmentNumber;
	private boolean commonProblem;
	private long createTime;
	private	boolean dailyState;
	private boolean dementia;
	private boolean depression;
	private boolean fallRisk;
	private boolean isDelete;
	private boolean lifeNursings;
	private boolean mentalityState;
	private boolean onlineAssess; //是否在线评估
	private boolean pathologicalSigns; //
	private boolean perceptionConmunication;
	private boolean ppAssessment; //???????
	private int seniorId;
	private boolean socialParticipation;
	private long updateTime;
	
	public int getAppraisersId() {
		return appraisersId;
	}
	public void setAppraisersId(int appraisersId) {
		this.appraisersId = appraisersId;
	}
	public String getAppraisersName() {
		return appraisersName;
	}
	public void setAppraisersName(String appraisersName) {
		this.appraisersName = appraisersName;
	}
	public int getAssessmentId() {
		return assessmentId;
	}
	public void setAssessmentId(int assessmentId) {
		this.assessmentId = assessmentId;
	}
	public String getAssessmentName() {
		return assessmentName;
	}
	public void setAssessmentName(String assessmentName) {
		this.assessmentName = assessmentName;
	}
	public String getAssessmentNumber() {
		return assessmentNumber;
	}
	public void setAssessmentNumber(String assessmentNumber) {
		this.assessmentNumber = assessmentNumber;
	}
	public boolean isCommonProblem() {
		return commonProblem;
	}
	public void setCommonProblem(boolean commonProblem) {
		this.commonProblem = commonProblem;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public boolean isDailyState() {
		return dailyState;
	}
	public void setDailyState(boolean dailyState) {
		this.dailyState = dailyState;
	}
	public boolean isDementia() {
		return dementia;
	}
	public void setDementia(boolean dementia) {
		this.dementia = dementia;
	}
	public boolean isDepression() {
		return depression;
	}
	public void setDepression(boolean depression) {
		this.depression = depression;
	}
	public boolean isFallRisk() {
		return fallRisk;
	}
	public void setFallRisk(boolean fallRisk) {
		this.fallRisk = fallRisk;
	}
	public boolean isDelete() {
		return isDelete;
	}
	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	public boolean isLifeNursings() {
		return lifeNursings;
	}
	public void setLifeNursings(boolean lifeNursings) {
		this.lifeNursings = lifeNursings;
	}
	public boolean isMentalityState() {
		return mentalityState;
	}
	public void setMentalityState(boolean mentalityState) {
		this.mentalityState = mentalityState;
	}
	public boolean isOnlineAssess() {
		return onlineAssess;
	}
	public void setOnlineAssess(boolean onlineAssess) {
		this.onlineAssess = onlineAssess;
	}
	public boolean isPathologicalSigns() {
		return pathologicalSigns;
	}
	public void setPathologicalSigns(boolean pathologicalSigns) {
		this.pathologicalSigns = pathologicalSigns;
	}
	public boolean isPerceptionConmunication() {
		return perceptionConmunication;
	}
	public void setPerceptionConmunication(boolean perceptionConmunication) {
		this.perceptionConmunication = perceptionConmunication;
	}
	public boolean isPpAssessment() {
		return ppAssessment;
	}
	public void setPpAssessment(boolean ppAssessment) {
		this.ppAssessment = ppAssessment;
	}
	public int getSeniorId() {
		return seniorId;
	}
	public void setSeniorId(int seniorId) {
		this.seniorId = seniorId;
	}
	public boolean isSocialParticipation() {
		return socialParticipation;
	}
	public void setSocialParticipation(boolean socialParticipation) {
		this.socialParticipation = socialParticipation;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
}
