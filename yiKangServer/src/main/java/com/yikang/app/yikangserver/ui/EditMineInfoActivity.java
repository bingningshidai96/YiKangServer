package com.yikang.app.yikangserver.ui;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.fragment.EditUserInfoFragemt;
import com.yikang.app.yikangserver.fragment.EditUserInfoFragemt.OnCompleteListener;
import com.yikang.app.yikangserver.reciever.UserInfoAlteredReceiver;
import com.yikang.app.yikangserver.service.UpLoadService;
import com.yikang.app.yikangserver.utils.LOG;

import java.util.HashMap;
import java.util.Map;

public class EditMineInfoActivity extends BaseActivity implements
		OnCompleteListener {

	private static final String TAG = "EditMineInfoActivity";
	
	protected static final String PARAM_LOGIN_PHOTO_URL = "photoUrl";
	private BroadcastReceiver receiver;

	private Map<String, Object> paramMap = new HashMap<String, Object>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar(getString(R.string.edit_mine_info));
	}
	@Override
	protected void findViews() {}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_simple);
	}

	@Override
	protected void getData() {
	}

	@Override
	protected void initViewContent() {
		EditUserInfoFragemt fragment = new EditUserInfoFragemt();
		Bundle args = new Bundle();
		args.putSerializable(EditUserInfoFragemt.EXTRA_USER, AppContext
				.getAppContext().getUser());
		fragment.setArguments(args);

		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fy_simple_container, fragment);
		transaction.commit();
	}

	@Override
	public void onComplete(Map<String, Object> map) {
		/**
		 * jobCategory (是否兼职) [可选] districtCode (区县编码) mapPositionAddress
		 * (地图定位地址) [可选 与districtCode 并列，需同时存在] addressDetail （详细 地址）[可选]
		 * photoUrl （头像地址）[可选] userPosition （用户职位）[可选] userName （用户名称）[可选]
		 * hospital （医院）[可选] offices （科室）[可选] adpt
		 */
		paramMap.putAll(map);
		String filePath = (String) paramMap.get("filePath");
		paramMap.remove("filePath");
		if (!TextUtils.isEmpty(filePath)) {
			updateWithAvatar(filePath);
			return;
		}
		updateInfo(); // 直接注册
	}

	/**
	 * 上传头像后注册
	 */
	private void updateWithAvatar(String selectAvatarPath) {
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		showWaitingUI();
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				hideWaitingUI();
				printUploadResult(intent);
				String avatarUrl = intent.getStringExtra(UpLoadService.EXTRA_DATA);
				if (!TextUtils.isEmpty(avatarUrl)) {
					paramMap.put(PARAM_LOGIN_PHOTO_URL, avatarUrl);
				}
				updateInfo();
			}
		};
		IntentFilter filter = new IntentFilter(UpLoadService.ACTION_UPLOAD_COMPLETE);
		registerReceiver(receiver, filter);

		Intent intent = new Intent(this, UpLoadService.class);
		intent.putExtra("filePath", selectAvatarPath);
		startService(intent);
		return;
	} 

	/** 上传结果 */
	private void printUploadResult(Intent intent) {
		boolean isUploadSucess = intent.getBooleanExtra(UpLoadService.EXTRA_IS_SUCCESS, false);
		String avatarUrl = intent.getStringExtra(UpLoadService.EXTRA_DATA);

		if (isUploadSucess && !TextUtils.isEmpty(avatarUrl)) {
			AppContext.showToast("头像上传成功");
			LOG.i(TAG, "[register]头像上传成功" + avatarUrl);
		} else {
			String message = intent.getStringExtra(UpLoadService.EXTRA_MESSAGE);
			AppContext.showToast("抱歉，头像上传失败");
			LOG.e(TAG, "[register]上传头像失败:" + message);
		}
	}

	/**
	 * 想服务器提交数据注册
	 */
	private void updateInfo() {
		showWaitingUI();
		Api.alterUserInfo(paramMap, new ResponseCallback<Void>() {
			@Override
			public void onSuccess(Void data) {
				hideWaitingUI();
				AppContext.showToast("修改成功");
				sendBroadcast(new Intent(UserInfoAlteredReceiver.ACTION_USER_INFO_ALTED));
				finish();
			}

			@Override
			public void onFailure(String status, String message) {
				hideWaitingUI();
				AppContext.showToast("抱歉，修改失败." + message);
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(receiver!=null){
			unregisterReceiver(receiver);
		}
	}

}
