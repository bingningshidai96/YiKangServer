package com.yikang.app.yikangserver.fragment;

import java.util.HashMap;
import java.util.List;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yikang.app.yikangserver.AddrSarchActivity;
import com.yikang.app.yikangserver.MainActivity;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppConfig;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.data.MyData.City;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.fragment.SystemSelectPhotoFragment.OnResultListener;
import com.yikang.app.yikangserver.service.UpLoadService;
import com.yikang.app.yikangserver.utils.BitmapUtils;
import com.yikang.app.yikangserver.utils.BuisNetUtils;
import com.yikang.app.yikangserver.utils.HttpUtils;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.view.TextSpinner;
import com.yikang.app.yikangserver.view.adapter.PopListAdapter;

public class RegisterStepTwoFragemt extends BaseFragment 
		implements OnClickListener,OnResultListener{
	public static final String TAG = "RegisterStepTwoFragemt";
	private static final int REQUEST_ADDR =100;
	private EditText edtName;
	private TextSpinner tspProfession,tspProfeLever,tspDistrict;
	private TextView tvDetialAddr;
	private Button btRegist;
	private ImageView ivAvatar;
	
	/**
	 * 从上一个fragment传过来的用户名
	 */
	private String userName;
	/**
	 * 从上一个fragment传过来的密码
	 */
	private String passw;
	
	private String selectAvatarPath; //临时文件，用于指向拍摄的照片
	private Bitmap roundAvatar; //填充头像的图片
	private SystemSelectPhotoFragment selePhotofragment;
	private BroadcastReceiver receiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		userName = bundle.getString("userName");
		passw = bundle.getString("passw");
		LOG.d(TAG, "[onCreate]userName:"+userName+">>>>passw"+passw);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_register_step_two, container,false);
		edtName = (EditText) view.findViewById(R.id.edt_register_name);
		tvDetialAddr = (TextView) view.findViewById(R.id.tv_register_detial_addr);
		tspDistrict = (TextSpinner) view.findViewById(R.id.tsp_register_district);
		tspProfeLever = (TextSpinner) view.findViewById(R.id.tsp_register_profession_lever);
		tspProfession = (TextSpinner) view.findViewById(R.id.tsp_register_profession);
		btRegist =  (Button) view.findViewById(R.id.bt_regist_regist);
		ivAvatar =  (ImageView) view.findViewById(R.id.iv_register_avatar);
		
		ivAvatar.setOnClickListener(this);
		tvDetialAddr.setOnClickListener(this);
		btRegist.setOnClickListener(this);
		
		final int beijingCode = 2;
		City beijing = MyData.cityMap.get(beijingCode);
		tspDistrict.setAdapter(new PopListAdapter(getActivity(), beijing.getListArea()));
		
		List<String> proLeverNames = MyData.getItems(MyData.profeLeversMap);
		tspProfeLever.setAdapter(new PopListAdapter(getActivity(), proLeverNames));
		
		List<String> proNames = MyData.getItems(MyData.professionMap);
		tspProfession.setAdapter(new PopListAdapter(getActivity(), proNames));
		
		return view;
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_ADDR&&data!=null){
			String addr = data.getStringExtra("addr");
			tvDetialAddr.setText(addr);
		}
		
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.bt_regist_regist:
			register(); //注册
			break;
		case R.id.iv_register_avatar:
			getAvatar(); //点击了头像，获取头像
			break;
		case R.id.tv_register_detial_addr:  
			getDetailAddr(); //获取详细地址
			break;
		default:
			break;
		}
		
	}
	
	/**
	 * 显示选择图片的fragment
	 */
	private void showSelePhotoUI(){
		if(selePhotofragment==null){
			selePhotofragment = new SystemSelectPhotoFragment();
			selePhotofragment.setOnResultListener(this);
		}
		
		if(selePhotofragment == 
				getFragmentManager().
				findFragmentById(R.id.fl_mine_photo_fragment_container)){
					return ;
		}
//		new SelePicManager(getView(), getFragmentManager());
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fl_mine_photo_fragment_container, selePhotofragment).commit();
	}
	
	
	/**
	 * 隐藏fragment
	 */
	private void dismissSelectPhotoUI(){
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.remove(selePhotofragment).commit();
		LOG.i(TAG, "dismissSelectPhotoUI"+"");
	}
	
	
	/**
	 * 获取地址
	 */
	private void getDetailAddr() {
		Intent intent = new Intent(getActivity(), AddrSarchActivity.class);
		startActivityForResult(intent, REQUEST_ADDR);
	}

	/**
	 * 获取头像
	 */
	private void getAvatar() {
		showSelePhotoUI();
	}

	/**
	 * 收集用户填写的信息
	 */
    private HashMap<String, Object> collectInfo(){
    	HashMap<String,Object> map = new HashMap<String, Object>();
    	map.put("userName", edtName.getText().toString());
		map.put("loginName", userName);
		map.put("passWord", passw);
		int p = tspProfeLever.getCurrentSelction();
		map.put("userPosition", MyData.profeLeversMap.keyAt(p));
		
		p = tspProfession.getCurrentSelction();
		map.put("jobCategory", MyData.professionMap.keyAt(p));
		map.put("cityCode", 2);
		
		p = tspDistrict.getCurrentSelction();
		map.put("districtCode",  MyData.cityMap.get(2).getSparseArea().keyAt(p));
		map.put("addressDetail", tvDetialAddr.getText().toString());
		map.put("photoUrl", "");
		return map;
    }
    
    
    /**
     * 上传图片
     */
    private void upLoadAvatar(){
    	if(selectAvatarPath!=null){
			Intent intent = new Intent(getActivity(), UpLoadService.class);
			intent.putExtra("filePath", selectAvatarPath);
			getActivity().startService(intent);
    	}
    }
    
    /**
     * 响应注册点击事件
     */
    private void register(){
    	if(!check()){
    		AppContext.showToastLong(getActivity(),"请填写全部的必要资料(除头像外都是必要的)");
    		return ;
    	}
//    	if(!TextUtils.isEmpty(selectAvatarPath)){
//    		if(receiver!=null){
//    			receiver = new BroadcastReceiver() {
//    				@Override
//    				public void onReceive(Context context, Intent intent) {
//    					boolean isUploadSucess = 
//    							intent.getBooleanExtra(UpLoadService.EXTRA_IS_SUCESS, false);
//    					String avatarUrl = intent.getStringExtra(UpLoadService.EXTRA_DATA);
//    					if(isUploadSucess&&!TextUtils.isEmpty(avatarUrl)){
//    						LOG.i(TAG, "[register]头像上传成功"+avatarUrl);
//    						requestRegist(avatarUrl);
//    					}else{
//    						String message = intent.getStringExtra(UpLoadService.EXTRA_MESSAGE);
//    						AppContext.showToast("抱歉，头像上传失败");
//    						LOG.e(TAG, "[register]上传头像失败:"+message);
//    					}
//    				}
//    			};
//    		}
//    		IntentFilter filter = new IntentFilter(UpLoadService.ACTION_UPLOAD_COMPLETE);
//			getActivity().registerReceiver(receiver, filter);
//    		upLoadAvatar();
//    		return ;
//    	}
    	requestRegist(null);
    }
    
	/**
	 * 想服务器提交数据注册
	 */
	private void requestRegist(String avatarUrl){
		LOG.i(TAG, "[requestRegist]"+avatarUrl);
		showWatingDailog();
		String url = UrlConstants.URL_REGISTER;
		RequestParam param = new RequestParam("appid","accessTicket");
		param.setParamData(collectInfo());
		param.toParams();
		HttpUtils.requestPost(url, param.toParams(), new HttpUtils.ResultCallBack() {
			@Override
			public void postResult(String result) {
				dismissWatingDailog();
				if(TextUtils.isEmpty(result)){
					onRegistFailure("网络异常");
					return;
				}
				try {
					ResponseContent content = ResponseContent.toResposeContent(result);
					if(ResponseContent.STATUS_OK.equals(content.getStatus())){
						//注册成功
						onRegistSuccess();
					}else{
						//注册失败
						onRegistFailure(content.getMessage());
					}
				} catch (Exception e) {
					e.printStackTrace();
					//注册异常
				}
			}
		});
	}
	
	/**
	 * 检查合法性
	 */
	private boolean check(){
		if(tspDistrict.getCurrentSelction()==-1
				||tspProfeLever.getCurrentSelction()==-1
				||tspProfession.getCurrentSelction()==-1
				||TextUtils.isEmpty(tvDetialAddr.getText())){
			return false;
		}
		return true;
	}
	
	/**
	 * 注册成功时执行的动作
	 */
	private void onRegistSuccess(){
		AppContext.showToast("注册成功");
		login(userName,passw);
	}
	
	/**
	 * 注册失败时执行的动作
	 */
	private void onRegistFailure(String msg){
		AppContext.showToast("注册失败:"+msg);
	}
	
	
	/**
	 * 登录
	 */
	private void login(final String userName,final String passw){
		showWatingDailog();
		LOG.i(TAG, "[login]"+userName+"==="+passw);
		String url = UrlConstants.URL_LOGIN_LOGIN;
		RequestParam param = new RequestParam("appid","accessTicket");
		param.add("loginName", userName);
		param.add("passWord", passw);
		param.add("machineCode", AppContext.getAppContext().getDeviceID());
		BuisNetUtils.requestStr(url, param, new BuisNetUtils.ResponceCallBack() {
			
			@Override
			public void onSuccess(ResponseContent content) {
				dismissWatingDailog();
				LOG.i(TAG, "[login]"+content.getData());
				String ticket = content.getData();
				AppContext.getAppContext().updateAccessTicket(ticket);
				//将用户名和密码存起来
				AppConfig appConfig = AppConfig.getAppConfig(getActivity());
				appConfig.setProperty("user.userName", userName);
				appConfig.setProperty("user.password", passw);
				onLoginSucess();
			}
			@Override
			public void onFialure(String status, String message) {
				dismissWatingDailog();
				AppContext.showToast(message);
			}
		});
	}
	/**
	 * 登录成功
	 */
	private void onLoginSucess(){
		Intent intent = new Intent(getActivity(), MainActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if(receiver!=null){
			getActivity().unregisterReceiver(receiver);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		dismissWatingDailog();
		if(roundAvatar!=null){
			roundAvatar.recycle();
			roundAvatar = null;
		}
	}
	

	/**
	 * 选择照片完成
	 */
	@Override
	public void onComplete(String path) {
		dismissSelectPhotoUI();
		selectAvatarPath = path;
		LOG.i(TAG, "[onComplete]"+path);
		if(roundAvatar!=null){ //将之前的roudPic回收
			roundAvatar.recycle();
		}
		Bitmap bitmap = BitmapUtils.getBitmap(getActivity(), 
				path, ivAvatar.getWidth(), ivAvatar.getHeight());
		if(bitmap!=null){
			roundAvatar = BitmapUtils.toRoundPic(bitmap);
			bitmap.recycle();
			bitmap = null;
			ivAvatar.setImageBitmap(roundAvatar);
		}else{
			LOG.e(TAG, "系统出错，没有获取到图片");
		}
	}
	
	/**
	 * 选择照片取消
	 */
	@Override
	public void onCancel() {
		dismissSelectPhotoUI();
	}
	
}

