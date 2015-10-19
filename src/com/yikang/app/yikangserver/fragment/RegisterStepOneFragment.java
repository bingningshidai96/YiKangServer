package com.yikang.app.yikangserver.fragment;

import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.dailog.ProgressDialogFactory;
import com.yikang.app.yikangserver.utils.LOG;

public class RegisterStepOneFragment extends Fragment implements OnClickListener{
	protected static final String TAG = null;
	// 填写从短信SDK应用后台注册得到的APPKEY 
	private static String APPKEY = "a11dd7ffad70";//463db7238681  27fe7909f8e8
	// 填写从短信SDK应用后台注册得到的APPSECRET
	private static String APPSECRET = "8893f3bd45db487a6291460f3904ca46";//
	
	// 默认使用中国区号
	private static final String CHINA_CODE="86";
	
	private EditText edtUserId,edtPassw,edtVerlifiCode,edtPasswAgain;
	
	private TextView tvGetVerlifiCode;
	
	private Button btNext;
	
	
	/**
	 * 计时器的数字
	 */
	private  int timerCount=60;
	

	private Handler eventHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			dismissDaiglog();
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			
			if (result == SMSSDK.RESULT_COMPLETE) {
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
					AppContext.showToast("验证码校验成功");
					toNextStepPage();
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					AppContext.showToast("短信已经发送,请稍候");
				}else if(event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE){
					AppContext.showToast("请注意接听电话~");
				}
			} else {
				((Throwable) data).printStackTrace(); //答应异常
				// 根据服务器返回的网络错误，给toast提示
				if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					AppContext.showToast("获取验证码失败,请检查网络情况");
					handler.sendEmptyMessage(101);//提示获取验证码失败，将获取验证码的按钮恢复
					return ;
				}else if(event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE){
					AppContext.showToast("获取验证码失败,请检查网络情况");
					handler.sendEmptyMessage(101);//提示获取验证码失败，将获取验证码的按钮恢复
					return ;
				}else if(event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
					AppContext.showToast("验证失败,请检查网络情况");
					return ;
				}
				try {
					Throwable throwable = (Throwable) data;
					JSONObject object = new JSONObject(
							throwable.getMessage());
					String des = object.optString("detail");
					if (!TextUtils.isEmpty(des)) {
						AppContext.showToast(des);
						return;
					}
				} catch (Exception e) {
					LOG.e(TAG, e.toString());
				}
				AppContext.showToast("网络发生异常,请稍候重试");
			}
		}
	};
	
	private OnNextListener listener;
	
	
	
	
	
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(!(activity instanceof OnNextListener)){
			throw new IllegalArgumentException("your activity must implement OnNextListener");
		}
		this.listener = (OnNextListener) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SMSSDK.initSDK(getActivity(),APPKEY,APPSECRET);
		
		eh = new EventHandler(){
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				eventHandler.sendMessage(msg);
			}
		};
		SMSSDK.registerEventHandler(eh);
	}
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_register_step_one, container,false);
		
		tvGetVerlifiCode = (TextView) view.findViewById(R.id.tv_register_get_verlifiCode);
		edtVerlifiCode = (EditText) view.findViewById(R.id.edt_register_verlifi_code);
		edtUserId = (EditText) view.findViewById(R.id.edt_register_phoneNumber);
		edtPassw =(EditText) view.findViewById(R.id.edt_register_passw);
		edtPasswAgain = (EditText) view.findViewById(R.id.edt_register_passw_again);
		btNext = (Button) view.findViewById(R.id.bt_register_next);
		tvGetVoice = (TextView) view.findViewById(R.id.tv_regist_get_voice_code);
		layout = (LinearLayout) view.findViewById(R.id.ly_register_voice_code_container);
		
		tvGetVerlifiCode.setOnClickListener(this);
		btNext.setOnClickListener(this);
		tvGetVoice.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterEventHandler(eh);
		timerCount=0;
	}
	
	
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tv_register_get_verlifiCode:
			getverlifiCode();
			break;
		case R.id.bt_register_next:
			verlifiPhone();
			break;
		case R.id.tv_regist_get_voice_code:
			getVoiceCode();
		default:
			break;
		}
	}

	/**
	 * 获取验证码
	 */
	private void getverlifiCode(){
		String phoneNumber = edtUserId.getText().toString();
		if(TextUtils.isEmpty(phoneNumber)||phoneNumber.length()!=11){
			AppContext.showToast("请输入正确的手机号码");
			return ;
		}
		layout.setVisibility(View.VISIBLE);
		SMSSDK.getVerificationCode(CHINA_CODE, phoneNumber);
		tvGetVerlifiCode.setEnabled(false);
		resetTimer();
		
	}
	/**
	 * 获取语音验证短信
	 */
	private void getVoiceCode() {
		String phoneNumber = edtUserId.getText().toString();
		if(TextUtils.isEmpty(phoneNumber)||phoneNumber.length()!=11){
			AppContext.showToast("请输入正确的手机号码");
			return ;
		}
		SMSSDK.getVoiceVerifyCode(phoneNumber, CHINA_CODE);
		resetTimer();
		
	}
	
	
	/**
	 * 用来计时的
	 */
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==100){
				int count = msg.arg1;
				tvGetVerlifiCode.setText(count+"");
				if(count<=0){
					tvGetVerlifiCode.setEnabled(true);//重新启用
					tvGetVerlifiCode.setText("获取验证码");
				}
			}else if(msg.what==101){
				tvGetVerlifiCode.setEnabled(true);//重新启用
				tvGetVerlifiCode.setText("获取验证码");
				timerCount=0;//取消timer
			}
		}
	};
	
	
	/**
	 * 重置计时器
	 */
	public void resetTimer(){
	   
		if(timer==null){
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Message msg = Message.obtain();
					if(timerCount>0){
						msg.what=100;
						msg.arg1 = timerCount;
						handler.sendMessage(msg);
						timerCount--;
					}else{
						timer.cancel();
						msg.what=101;
						handler.sendMessage(msg);
						LOG.i(TAG, "[getverlifiCode]计时器timer取消");
					}
				}
			}, 0, 1000);
		}
		timerCount = 60;
	}
	
	
	
	/**
	 * 验证手机号，验证结果的处理在handler中
	 */
	private void verlifiPhone(){
		LOG.i(TAG, "[verlifiPhone]====");
		String verlifiCode = edtVerlifiCode.getText().toString();
		String phoneNumber = edtUserId.getText().toString();
		String passw = edtPassw.getText().toString();
		String passwAgain = edtPasswAgain.getText().toString();
		
		if(TextUtils.isEmpty(phoneNumber)||phoneNumber.length()!=11){
			AppContext.showToast("请输入正确的手机号码");
			return ;
		}
		if(TextUtils.isEmpty(verlifiCode)){
			AppContext.showToast("请输入验证码");
		}
		if(TextUtils.isEmpty(passw)||passw.length()<6||passw.length()>16){
			AppContext.showToast("请输入6d到16位长度的密码");
			return ;
		}
		
		if(!passw.equals(passwAgain)){
			AppContext.showToast("两次输入的密码不一致");
			return ;
		}
		
		showDailog();
		//提交验证码
		SMSSDK.submitVerificationCode(CHINA_CODE,phoneNumber,verlifiCode);
	}
	
	
	/**
	 * 跳到下一步
	 */
	private void toNextStepPage(){
		String phoneNumber = edtUserId.getText().toString();
		String passw = edtPassw.getText().toString();
		listener.next(phoneNumber, passw);
	}
	
	
	private ProgressDialog waitingDialog;
	private EventHandler eh;
	private Timer timer;
	private TextView tvGetVoice;
	private LinearLayout layout;
	private void showDailog(){
		if(waitingDialog==null){
			waitingDialog = ProgressDialogFactory.getProgressDailog(
					ProgressDialogFactory.TYPE_SUBMIT_DATA, getActivity());
		}
		waitingDialog.show();
	}
	
	private void dismissDaiglog(){
		if(waitingDialog!=null&&waitingDialog.isShowing()){
			waitingDialog.dismiss();
		}
	}
	
	
	public interface OnNextListener{
		public void next(String userName,String passw);
	}
}
