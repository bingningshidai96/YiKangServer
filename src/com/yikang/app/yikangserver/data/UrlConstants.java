package com.yikang.app.yikangserver.data;

import com.yikang.app.yikangserver.data.QuestionData.TableType;

 
public class UrlConstants {
	private static final String SERVER_HOST = "http://192.168.8.104";
	
	//private static final String URL_BASE_SERVICE_URL=SERVER_HOST+":8088/yikangservice/service";
	/** 
	 * 测试
	 */
	public static final String URL_BASE_SERVICE_URL="http://192.168.8.116:8088/yikangservice/service";
	/**
	 * 中国    
	 */
	//public static final String URL_BASE_SERVICE_URL = "http://54.223.229.10:8088/yikangservice/service";
	/**
	 * 新加坡
	 */
	//public static final String URL_BASE_SERVICE_URL="http://52.74.59.190:8088/yikangservice/service";
	public static final String URL_SENOIR_INFO_SUBMIT =URL_BASE_SERVICE_URL+"/00-01-01";
	public static final String URL_EVALUTION_COMMON_CROSSWIRSES_DATA =URL_BASE_SERVICE_URL+"/00-03-01";
	public static final String URL_EVALUTION_COMMON_PORTAITS_DATA =URL_BASE_SERVICE_URL+"/00-03-02";
	public static final String URL_EVALUTION_DISEASE_CROSSWIRSES_DATA =URL_BASE_SERVICE_URL+"/00-06-01";
	public static final String URL_ADD_EVALUATION_BAG=URL_BASE_SERVICE_URL+"/00-15-01";
	
	public static final String URL_LOGIN_LOGIN = URL_BASE_SERVICE_URL+"/login";
	public static final String URL_REGISTER = URL_BASE_SERVICE_URL+"/registerUserAndSaveServiceInfo";
	
	public static final String URL_REGISTER_DEVICE = URL_BASE_SERVICE_URL+"/00-18-01";
	public static final String URL_GET_JPUSH_ALIAS = URL_BASE_SERVICE_URL+"/00-18-02";
	/**
	 * 获取所有评估记录
	 */
	public static final String URL_GET_EVALUATION_RECORD = URL_BASE_SERVICE_URL+"/00-15-02";
	
	/**
	 * 获取一个用户所有的老人
	 */
	public static final String URL_GET_SENIOR_LIST = URL_BASE_SERVICE_URL+"/00-01-03";
	public static final String URL_GET_TABLE_LIST = URL_BASE_SERVICE_URL +"/00-16-01";
	public static final String URL_GET_TABLE_ANSWER = URL_BASE_SERVICE_URL +"/00-16-02";
	public static final String URL_GET_CROSS_ANSWER = URL_BASE_SERVICE_URL +"/00-16-03";
	public static final String URL_GET_FALLRISK_ANSWER = URL_BASE_SERVICE_URL +"/00-16-04";
	
	/**
	 * 文件上传服务器的地址
	 */
	private static final String URL_FILE_UPLOAD= SERVER_HOST+":8081/yikangeFileManage";
	/**
	 * 上传文件
	 */
	public static final String URL_UPLOAD_IMAGE=URL_FILE_UPLOAD+"/fileUpload/imageFileUpload";
	
	
	/**
	 * 获取日程列表 URL_GET_FREE_TIME
	 */
	public static final String URL_GET_WORK_DAYS = URL_BASE_SERVICE_URL +"/00-19-01";
	
	/**
	 * 获取某一天的详细时间
	 */
	public static final String URL_GET_WORK_TIME = URL_BASE_SERVICE_URL +"/00-19-02";
	
	/**
	 * 保存选择的时间
	 */
	public static final String URL_SAVE_FREE_TIME = URL_BASE_SERVICE_URL +"/00-19-03";
	
	
	/**
	 * 获取一个服务人员的信息
	 */
	public static final String URL_GET_USER_INFO = URL_BASE_SERVICE_URL +"/00-17-04";
	public static final String getQustrionSubmitUrl(TableType type) {
		switch (type) {
		case daily_life:
		case mental_state:
		case sensation:
		case social_parti:
		case chang_gu_chuang:
		case depression_self:
		case depression_other:
		case senior_common_question:
		case fall_risk :
			return URL_BASE_SERVICE_URL+"/00-04-02";
			
		case disease:
		case daily_nursing:
			return URL_BASE_SERVICE_URL+"/00-04-01";
		
		default:
			return null;
		}

	}
}
