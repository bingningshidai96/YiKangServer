package com.yikang.app.yikangserver.fragment;

import java.util.HashMap;
import java.util.Map;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.FreeTimeCalendarActivty;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.data.ConstantData;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.fragment.SystemSelectPhotoFragment.OnResultListener;
import com.yikang.app.yikangserver.utils.BitmapUtils;
import com.yikang.app.yikangserver.utils.BuisNetUtils;
import com.yikang.app.yikangserver.utils.LOG;

public class MineFragment extends BaseFragment implements OnClickListener,OnResultListener{
	protected static final String TAG = "MineFragment";
	private TextView tvName,tvEmloyeeId,tvProfession,
					tvProLever,tvDistinct,tvCustomerNum;
	private ImageView ivAvatar;
	private LinearLayout lvFreeTime; //兼职人员时间
	private SystemSelectPhotoFragment selePhotofragment; //选择照片的fragment
	
	private Map<String,Object> map = new HashMap<String, Object>();
	
	private boolean hasDataChanged; //是否有新的数据
	private DataChageReciver receiver; //当用户资料改变时，触发广播
	private Bitmap roundAvatar; //圆形图片
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(ConstantData.ACTION_BROADCAST_ADD_SENOIR);
		receiver = new DataChageReciver();
		getActivity().registerReceiver(receiver, filter);
		loadData();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mine, container,false);
		initTitleBar(getActivity().getString(R.string.mine_title), view);
		findViews(view);
		return view;
	}
	
	protected void initTitleBar(String title,View view) {
		view.findViewById(R.id.ibtn_title_back).setVisibility(View.GONE);
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_text);
		tvTitle.setText(title);
	}
	
	private void findViews(View view) {
		tvName = (TextView) view.findViewById(R.id.tv_mine_name);
		tvEmloyeeId = (TextView) view.findViewById(R.id.tv_mine_employee_id);
		tvCustomerNum = (TextView) view.findViewById(R.id.tv_mine_customer_number);
		tvProfession = (TextView) view.findViewById(R.id.tv_mine_profession);
		tvDistinct = (TextView) view.findViewById(R.id.tv_mine_distinct);
		tvProLever = (TextView) view.findViewById(R.id.tv_mine_profession_lever);
		lvFreeTime = (LinearLayout) view.findViewById(R.id.ly_mine_free_time);
		ivAvatar = (ImageView) view.findViewById(R.id.iv_mine_avatar);
		ivAvatar.setOnClickListener(this);
		lvFreeTime.setOnClickListener(this);
	}
	
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		if(hidden){
			dismissWatingDailog();
		}else{ //如果是显示则注意刷新数据
			checkRefresh();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		checkRefresh(); //从其他页面跳转回来检查数据
	}
	
	/**
	 * 检查是否有更新
	 */
	private void checkRefresh(){
		if(hasDataChanged){
			loadData();
		}
	}
	
	/**
	 *获取数据 
	 */
	private void loadData(){
		showWatingDailog();
		String url = UrlConstants.URL_GET_USER_INFO;
		RequestParam param = new RequestParam();
		
		BuisNetUtils.requestStr(url, param, new BuisNetUtils.ResponceCallBack() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseContent content) {
				dismissWatingDailog();
				map = (Map<String, Object>) JSON.parse(content.getData());
				fillToViews(map);
				hasDataChanged = false;
			}
			@Override
			public void onFialure(String status, String message) {
				dismissWatingDailog();
				AppContext.showToast(message);
			}
		});
	}
	/**
	 *将map中的信息填写到Views中 
	 */
	private void fillToViews(Map<String,Object> map){
		String userName = (String) map.get("userName");
		//int districtCode = (Integer) map.get("districtCode");
		String addressDetail = (String) map.get("addressDetail");
		int userId = (Integer) map.get("userId");
		int userPosition = (Integer) map.get("userPosition");
		int jobCategory = (Integer) map.get("jobCategory");
		int num = (Integer) map.get("nums");
		
		tvDistinct.setText(addressDetail);
		tvProfession.setText(MyData.professionMap.valueAt(userPosition));
		tvProLever.setText(MyData.profeLeversMap.valueAt(jobCategory));
		tvName.setText(userName);
		tvEmloyeeId.setText(""+userId);
		tvCustomerNum.setText(num+"人");
		
		if(jobCategory ==1 ){ //如果是兼职，就显示空闲时间
			lvFreeTime.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ly_mine_free_time:
			Intent intent =new Intent(getActivity(), FreeTimeCalendarActivty.class);
			startActivity(intent);
			break;
		case R.id.iv_mine_avatar:
			showSelePhotoUI();
		default:
			break;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
	}
	
	
	private class DataChageReciver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			hasDataChanged = true;
		}
	}

	@Override
	public void onComplete(String path) {
		dismissSelectPhotoUI();
		LOG.i(TAG, "[onComplete]"+path);
		//String imagePath = BitmapUtils.getImagePathFromURI(getActivity(), uri);
		//LOG.i(TAG, "[onComplete]"+imagePath+"uri"+uri);
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

	@Override
	public void onCancel() {
		dismissSelectPhotoUI();
	}
	
	/**
	 * 显示选择图片的fragment
	 */
	private void showSelePhotoUI(){
		getView().findViewById(R.id.fl_mine_photo_fragment_container)
			.setVisibility(View.VISIBLE);
		
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
		getView().findViewById(R.id.fl_mine_photo_fragment_container)
		.setVisibility(View.GONE);
		LOG.i(TAG, "dismissSelectPhotoUI"+"");
	}
}
