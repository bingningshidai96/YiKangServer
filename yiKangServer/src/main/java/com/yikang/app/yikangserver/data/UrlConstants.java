package com.yikang.app.yikangserver.data;

import com.yikang.app.yikangserver.data.EvaluationLocalData.TableType;

public class UrlConstants {
	/** 中国主机 */
	private static final String SERVER_HOST = "http://54.223.35.197";
	private static final String IMAGE_SERVER_HOST = "http://54.223.35.197";

	/** 测试主机 */
	private static final String SERVER_TEST = "http://192.168.8.127";
	private static final String IMAGE_SERVER_HOST_TEST = "http://54.223.35.197";

	/************* 下面才是基地址 ***********/
	/** 接口开发的地址 */
	public static final String URL_BASE_SERVICE_URL = SERVER_TEST
			+ ":8088/yikangservice/service";
	/** 文件上传服务器的地址 */
	private static final String URL_BASE_FILE_SERVER = IMAGE_SERVER_HOST
			+ ":8088/yikangFileManage";
	public static final String URL_SENOIR_INFO_SUBMIT = URL_BASE_SERVICE_URL
			+ "/00-01-01";
	/** 获取一个用户所有的老人 */
	public static final String URL_GET_SENIOR_LIST = URL_BASE_SERVICE_URL
			+ "/00-01-03";
	public static final String URL_EVALUTION_COMMON_CROSSWIRSES_DATA = URL_BASE_SERVICE_URL
			+ "/00-03-01";
	public static final String URL_EVALUTION_COMMON_PORTAITS_DATA = URL_BASE_SERVICE_URL
			+ "/00-03-02";
	public static final String URL_EVALUTION_DISEASE_CROSSWIRSES_DATA = URL_BASE_SERVICE_URL
			+ "/00-06-01";
	public static final String URL_LOGIN_LOGIN = URL_BASE_SERVICE_URL
			+ "/login";
	public static final String URL_REGISTER = URL_BASE_SERVICE_URL
			+ "/registerUserAndSaveServiceInfo";

	public static final String URL_ADD_EVALUATION_BAG = URL_BASE_SERVICE_URL
			+ "/00-15-01";
	public static final String URL_REGISTER_DEVICE = URL_BASE_SERVICE_URL
			+ "/00-18-01";
	public static final String URL_GET_JPUSH_ALIAS = URL_BASE_SERVICE_URL
			+ "/00-18-02";
	/** 获取所有评估记录 */
	public static final String URL_GET_EVALUATION_RECORD = URL_BASE_SERVICE_URL
			+ "/00-15-02";

	
	public static final String URL_GET_TABLE_LIST = URL_BASE_SERVICE_URL
			+ "/00-16-01";
	public static final String URL_GET_TABLE_ANSWER = URL_BASE_SERVICE_URL
			+ "/00-16-02";
	public static final String URL_GET_CROSS_ANSWER = URL_BASE_SERVICE_URL
			+ "/00-16-03";
	public static final String URL_GET_FALLRISK_ANSWER = URL_BASE_SERVICE_URL
			+ "/00-16-04";
	
	/** 获取一个服务人员的信息 */
	public static final String URL_GET_USER_INFO = URL_BASE_SERVICE_URL
			+ "/00-17-04";
	public static final String URL_EDIT_USER_INFO = URL_BASE_SERVICE_URL
			+"/00-17-05";
	public static final String URL_FIND_PASSW = URL_BASE_SERVICE_URL
			+"/00-17-06";
	
	/** 获取日程列表 URL_GET_FREE_TIM */
	public static final String URL_GET_WORK_DAYS = URL_BASE_SERVICE_URL
			+ "/00-19-01";
	/** 获取某一天的详细时间 */
	public static final String URL_GET_WORK_TIME = URL_BASE_SERVICE_URL
			+ "/00-19-02";
	/** 保存选择的时间 */
	public static final String URL_SAVE_FREE_TIME = URL_BASE_SERVICE_URL
			+ "/00-19-03";
	
	public static final String URL_ORDER_LIST = URL_BASE_SERVICE_URL
			+ "/00-21-04";
	public static final String URL_SERVICE_ORDER_DETAIL = URL_BASE_SERVICE_URL
			+ "/00-21-05";
	public static final String URL_SERVICE_ORDER_SUBMIT_FEEDBACK = URL_BASE_SERVICE_URL
			+ "/00-21-06";
	public static final String URL_INVITE_LIST = URL_BASE_SERVICE_URL
			+ "/00-01-04";
	public static final String URL_RESET_PASSW = URL_BASE_SERVICE_URL
			+"/00-17-08";


	/** 上传文件 */
	public static final String URL_UPLOAD_IMAGE = URL_BASE_FILE_SERVER
			+ "/fileUpload/imageFileUpload";


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
		case fall_risk:
			return URL_BASE_SERVICE_URL + "/00-04-02";

		case disease:
		case daily_nursing:
			return URL_BASE_SERVICE_URL + "/00-04-01";

		default:
			return null;
		}

	}
}
