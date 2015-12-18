package com.yikang.app.yikangserver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.ui.EditMineInfoActivity;
import com.yikang.app.yikangserver.ui.FreeDayCalendarActivty;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.utils.QRImageCreateUtils;

public class MineFragment extends BaseFragment implements OnClickListener {
	protected static final String TAG = "MineFragment";
	private TextView tvName, tvProfession, tvHospital, tvSpecial, tvDepartment,
			tvInviteCode;
	private ImageView ivAvatar, ivQRCode;
	private ImageButton ibtEdit;
	private LinearLayout lyFreeTime, lyDistinct; // 兼职人员时间
	private TextView tvDistinct, tvCustomerNum;
	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_mine, container, false);
		findViews(rootView);
		return rootView;
	}

	private void findViews(View view) {
		ivAvatar = (ImageView) view.findViewById(R.id.iv_mine_avatar);
		ivQRCode = (ImageView) view.findViewById(R.id.iv_mine_qr_code);

		ibtEdit = (ImageButton) view.findViewById(R.id.ibtn_mine_edit);
		tvName = (TextView) view.findViewById(R.id.tv_mine_name);
		tvProfession = (TextView) view.findViewById(R.id.tv_mine_profession);
		tvHospital = (TextView) view.findViewById(R.id.tv_mine_hospital);
		tvSpecial = (TextView) view.findViewById(R.id.tv_mine_special);
		tvDepartment = (TextView) view.findViewById(R.id.tv_mine_department);
		tvInviteCode = (TextView) view.findViewById(R.id.tv_mine_invite_code);

		tvCustomerNum = (TextView) view.findViewById(R.id.tv_mine_customer_number);
		tvDistinct = (TextView) view.findViewById(R.id.tv_mine_distinct);

		lyDistinct = (LinearLayout) view.findViewById(R.id.ly_mine_distinct_area);
		lyFreeTime = (LinearLayout) view.findViewById(R.id.ly_mine_free_time);

		ibtEdit.setOnClickListener(this);
		lyFreeTime.setOnClickListener(this);
		fillToViews();
	}

	/**
	 * 将map中的信息填写到Views中
	 */
	private void fillToViews() {
		User user = AppContext.getAppContext().getUser();
		LOG.d(TAG, "[fillToViews]:" + user);

		tvName.setText(user.name);//名字
		tvInviteCode.setText(user.inviteCode); //邀请码

		if (user.profession >= 0) { //职业
			String profession = MyData.professionMap.valueAt(user.profession);
			tvProfession.setText(profession);
		}

		if (!TextUtils.isEmpty(user.avatarImg)) { // 显示头像
			ImageLoader.getInstance().displayImage(user.avatarImg, ivAvatar);
		}

		if (!TextUtils.isEmpty(user.invitationUrl)) { // 显示二维码
			QRImageCreateUtils utils = new QRImageCreateUtils(ivQRCode);
			utils.createQRImage(user.invitationUrl);
		}
		
		//患者人数
		final String customNumStr = String.format(
				getString(R.string.mine_format_customer_nums), user.paintsNums);
		tvCustomerNum.setText(customNumStr);
		
		//职业相关的
		String addressDetail = user.mapPositionAddress + user.addressDetail;
		if (user.profession == MyData.DOCTOR) {
			rootView.findViewById(R.id.mine_container_hospital).setVisibility(
					View.VISIBLE);
			rootView.findViewById(R.id.mine_container_department)
					.setVisibility(View.VISIBLE);
			tvDepartment.setText(user.deparment);
			tvHospital.setText(user.hosital);
		} else if (user.profession == MyData.NURSING) {
			rootView.findViewById(R.id.mine_container_special).setVisibility(View.VISIBLE);
			rootView.findViewById(R.id.mine_container_hospital).setVisibility(View.VISIBLE);
			tvSpecial.setText(user.special);
			tvHospital.setText(user.hosital);
			
			lyDistinct.setVisibility(View.VISIBLE);
			if(user.jobType == MyData.PAER_TIME){
				lyFreeTime.setVisibility(View.VISIBLE);
			}
			tvDistinct.setText(addressDetail);
		} else {
			rootView.findViewById(R.id.mine_container_special).setVisibility(View.VISIBLE);
			tvSpecial.setText(user.special);

			lyDistinct.setVisibility(View.VISIBLE);
			if(user.jobType == MyData.FULL_TIME){
				lyFreeTime.setVisibility(View.VISIBLE);
			}
			tvDistinct.setText(addressDetail);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ly_mine_free_time:
			Intent intent = new Intent(getActivity(),FreeDayCalendarActivty.class);
			startActivity(intent);
			break;
		case R.id.ibtn_mine_edit:
			showEditPage();
		default:
			break;
		}
	}

	private void showEditPage() {
		Intent intent = new Intent(getActivity(), EditMineInfoActivity.class);
		startActivity(intent);
	}

	
	// private DataChageReciver receiver; //当用户资料改变时，触发广播
	// private boolean isNeedReload = true;; //是否有新的数据
	// private boolean isLoading;

	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// IntentFilter filter = new
	// IntentFilter(ConstantData.ACTION_BROADCAST_ADD_SENOIR);
	// receiver = new DataChageReciver();
	// getActivity().registerReceiver(receiver, filter);
	// //loadData();
	// }
	//

	//
	// @Override
	// public void onHiddenChanged(boolean hidden) {
	// if(hidden){
	// dismissWatingDailog();
	// }else{ //如果是显示则注意刷新数据
	// checkRefresh();
	// }
	// }
	//
	// @Override
	// public void onStart() {
	// super.onStart();
	// checkRefresh(); //从其他页面跳转回来检查数据
	// }

	// /**
	// * 检查是否有更新
	// */
	// private void checkRefresh(){
	// if(isNeedReload&&!isLoading){
	// loadData();
	// }
	// }

	// /**
	// *获取数据
	// */
	// private void loadData(){
	// isLoading = true;
	// showWatingDailog();
	// String url = UrlConstants.URL_GET_USER_INFO;
	// RequestParam param = new RequestParam();
	//
	// BuisNetUtils.requestStr(url, param, new BuisNetUtils.ResponceCallBack() {
	// @SuppressWarnings("unchecked")
	// @Override
	// public void onSuccess(ResponseContent content) {
	// isLoading = false;
	// isNeedReload = false;
	// dismissWatingDailog();
	// LOG.d(TAG, "[loadData-->requestStr]:"+content.getData());
	// User user = JSON.parseObject(content.getData(), User.class);
	// LOG.d(TAG, "[loadData-->requestStr]:"+user);
	// AppContext.getAppContext().login(user);
	// fillToViews();
	// }
	// @Override
	// public void onFialure(String status, String message) {
	// isLoading = false;
	// dismissWatingDailog();
	// AppContext.showToast(message);
	// }
	// });
	// }

	//
	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// getActivity().unregisterReceiver(receiver);
	// }

	// private class DataChageReciver extends BroadcastReceiver{
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// isNeedReload = true;
	// }
	// }
	//

	// private SystemSelectPhotoFragment selePhotofragment; //选择照片的fragment

	// private Map<String,Object> map = new HashMap<String, Object>();
	// private Bitmap roundAvatar; //圆形图片

	// private User user;

	// @Override
	// public void onComplete(String path) {
	// dismessEditUI();
	// LOG.i(TAG, "[onComplete]"+path);
	// if(roundAvatar!=null){ //将之前的roudPic回收
	// roundAvatar.recycle();
	// }
	// Bitmap bitmap = BitmapUtils.getBitmap(getActivity(),
	// path, ivAvatar.getWidth(), ivAvatar.getHeight());
	// if(bitmap!=null){
	// roundAvatar = BitmapUtils.toRoundPic(bitmap);
	// bitmap.recycle();
	// bitmap = null;
	// ivAvatar.setImageBitmap(roundAvatar);
	// }else{
	// LOG.e(TAG, "系统出错，没有获取到图片");
	// }
	// }
	//
	//
	//
	// @Override
	// public void onCancel() {
	// dismessEditUI();
	// }

	// /**
	// * 显示选择图片的fragment
	// */
	// private void showSelePhotoUI(){
	// getView().findViewById(R.id.fl_mine_photo_fragment_container)
	// .setVisibility(View.VISIBLE);
	//
	// if(selePhotofragment==null){
	// selePhotofragment = new SystemSelectPhotoFragment();
	// selePhotofragment.setOnResultListener(this);
	// }
	//
	// if(selePhotofragment ==
	// getFragmentManager().
	// findFragmentById(R.id.fl_mine_photo_fragment_container)){
	// return ;
	// }
	// FragmentTransaction transaction =
	// getFragmentManager().beginTransaction();
	// transaction.replace(R.id.fl_mine_photo_fragment_container,
	// selePhotofragment).commit();
	// }

	// /**
	// * 隐藏fragment
	// */
	// private void dismissSelectPhotoUI(){
	// FragmentTransaction transaction =
	// getFragmentManager().beginTransaction();
	// transaction.remove(selePhotofragment).commit();
	// getView().findViewById(R.id.fl_mine_photo_fragment_container)
	// .setVisibility(View.GONE);
	// LOG.i(TAG, "dismissSelectPhotoUI"+"");
	// }
}
