package com.yikang.app.yikangserver.fragment;

import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.fragment.SystemSelectPhotoFragment.OnResultListener;
import com.yikang.app.yikangserver.ui.AddrSarchActivity;
import com.yikang.app.yikangserver.utils.BitmapUtils;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.view.TextSpinner;
import com.yikang.app.yikangserver.view.TextSpinner.OnDropDownItemClickListener;
import com.yikang.app.yikangserver.view.adapter.PopListAdapter;

public class EditUserInfoFragemt extends BaseFragment implements OnClickListener, OnResultListener {
	public static final String TAG = "RegisterStepTwoFragemt";
	public static final String EXTRA_USER = "user";
	private static final int REQUEST_ADDR = 100;
	private EditText edtName;
	private TextSpinner tspProfession;
	private Button btRegist;
	private ImageView ivAvatar;
	private View rootView;
	private Bitmap avatar; // 填充头像的图片

	private PopListAdapter proLeverAdapter;

	/** 头像相关 */
	private String selectAvatarPath; // 临时文件，用于指向拍摄的照片
	private SystemSelectPhotoFragment selePhotofragment;
	private String avatarUrl;

	/** 各个职业各自的ui */
	private DoctorUI doctorUI;
	private NursingUI nursingUI;
	private TherapistUI therapistUI;
	/** 地址相关的参数 */
	private String title;
	private String detail;
	private String adCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		proLeverAdapter = new PopListAdapter(getActivity(),MyData.getItems(MyData.profeLeversMap));
		proLeverAdapter.setCurrentSelected(-1);
		AppContext.showToast(R.string.mine_enter_tips);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_fill_user_info,container, false);
		edtName = (EditText) rootView.findViewById(R.id.edt_register_name);
		tspProfession = (TextSpinner) rootView.findViewById(R.id.tsp_register_profession);
		btRegist = (Button) rootView.findViewById(R.id.bt_regist_regist);
		ivAvatar = (ImageView) rootView.findViewById(R.id.iv_register_avatar);
		ivAvatar.setOnClickListener(this);
		btRegist.setOnClickListener(this);
		tspProfession.setAdapter(new PopListAdapter(getActivity(), MyData.getItems(MyData.professionMap)));
		tspProfession.setOnDropDownItemClickListener(new OnDropDownItemClickListener() {
				@Override
				public void onItemClickListern(TextSpinner spinner,int position) {
					coloseOptionUI(); // 将其他的UI关闭
					getProfessionUI().initUI();
				}
		});
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle arguments = getArguments();
		User user = null;
		if (arguments != null) {
			user = (User) arguments.getSerializable(EXTRA_USER);
		}
		initViews(user);
		
	}

	private void initViews(User user) {
		// 初始化各个职业的ui可选的UI
		doctorUI = new DoctorUI(user);
		nursingUI = new NursingUI(user);
		therapistUI = new TherapistUI(user);
		if (user != null) {
			if (!TextUtils.isEmpty(user.avatarImg)) {
				ImageLoader.getInstance()
						.displayImage(user.avatarImg, ivAvatar);
			}
			edtName.setText(user.name);
			tspProfession.setCurrentSelection(user.profession);
			getProfessionUI().initUI();
			
			detail = user.addressDetail;
			title = user.mapPositionAddress;
			adCode = user.districtCode;
			avatarUrl = user.avatarImg;
		}
	}

	/**
	 * 关闭所有可选的页面
	 */
	private void coloseOptionUI() {
		doctorUI.colseUI();
		nursingUI.colseUI();
		therapistUI.colseUI();
	}

	private ProfessionUI getProfessionUI() {
		ProfessionUI professionUI = null;
		int currentSelction = tspProfession.getCurrentSelction();
		if (currentSelction == MyData.DOCTOR) {
			professionUI = doctorUI;
		} else if (currentSelction == MyData.NURSING) {
			professionUI = nursingUI;
		} else if (currentSelction == MyData.THERAPIST) {
			professionUI = therapistUI;
		}
		return professionUI;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ADDR && data != null) {
			adCode = data.getStringExtra(AddrSarchActivity.EXTRA_ADCODE);
			title = data.getStringExtra(AddrSarchActivity.EXTRA_ADDR_TITLE);
			detail = data.getStringExtra(AddrSarchActivity.EXTRA_ADDR_DETIAL);
			String district = data
					.getStringExtra(AddrSarchActivity.EXTRA_ADDR_DISTRICT);
			if (tspProfession.getCurrentSelction() == 1) {
				nursingUI.tvAddr.setText(district + title + detail);
			} else if (tspProfession.getCurrentSelction() == 2) {
				therapistUI.tvAddr.setText(district + title + detail);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.bt_regist_regist:
			complete(); // 注册
			break;
		case R.id.iv_register_avatar:
			getAvatar(); // 点击了头像，获取头像
			break;
		default:
			break;
		}

	}

	/**
	 * 获取头像
	 */
	private void getAvatar() {
		showSelePhotoUI();
	}

	/**
	 * 显示选择图片的fragment
	 */
	private void showSelePhotoUI() {
		if (selePhotofragment == null) {
			selePhotofragment = new SystemSelectPhotoFragment();
			selePhotofragment.setOnResultListener(this);
		}

		if (selePhotofragment == getFragmentManager().findFragmentById(
				R.id.fl_register_fragment_two_container)) {
			return;
		}
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fl_register_fragment_two_container,
				selePhotofragment).commit();
	}

	/**
	 * 选择照片完成
	 */
	@Override
	public void onComplete(String path) {
		dismissSelectPhotoUI();
		selectAvatarPath = path;
		LOG.i(TAG, "[onComplete]" + path);
		if (avatar != null) { // 将之前的roudPic回收
			avatar.recycle();
		}
		avatar = BitmapUtils.getBitmap(getActivity(), path,ivAvatar.getWidth(), ivAvatar.getHeight());
		if (avatar != null) {
			ivAvatar.setImageBitmap(avatar);
		} else {
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

	/**
	 * 隐藏fragment
	 */
	private void dismissSelectPhotoUI() {
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.remove(selePhotofragment).commit();
		LOG.i(TAG, "dismissSelectPhotoUI" + "");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnCompleteListener) {
			this.listener = (OnCompleteListener) activity;
		} else {
			throw new IllegalArgumentException(
					"your activity must implement OnCompleteListener");
		}
	}

	private OnCompleteListener listener;

	public interface OnCompleteListener {
		void onComplete(Map<String, Object> map);
	}

	/**
	 * 响应注册点击事件
	 */
	private void complete() {
		ProfessionUI professionUI = getProfessionUI();
		if (!generalcheck() || professionUI == null || !professionUI.check()) { // 检查参数
			AppContext.showToastLong(getActivity(),
					getString(R.string.regist_null_tip));
			return;
		}
		HashMap<String, Object> collectInfo = collectInfo();
		LOG.e(TAG, collectInfo.toString());
		listener.onComplete(collectInfo);

	}

	/**
	 * 检查姓名和职业
	 */
	private boolean generalcheck() {
		String name = edtName.getText().toString();
		int selection = tspProfession.getCurrentSelction();
		LOG.e(TAG,"[generalcheck]"+selection+">>>>>>"+tspProfession.getCurrentSelction());
		return !TextUtils.isEmpty(name) && selection != -1;
	}

	/**
	 * 收集用户填写的信息
	 */
	private HashMap<String, Object> collectInfo() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", edtName.getText().toString());
		map.put("userPosition", tspProfession.getCurrentSelction());
		map.put("photoUrl", avatarUrl);
		
		ProfessionUI professionUI = getProfessionUI();

		Map<String, Object> inputData = professionUI.getInputData();
		if (inputData == null) { // 说明有空的参数
			return null;
		}
		map.putAll(inputData);
		map.put("filePath", selectAvatarPath);
		return map;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dismissWatingDailog();
		if (avatar != null) {
			avatar.recycle();
			avatar = null;
		}
	}

	/**
	 * 获取地址
	 */
	private void getDetailAddr() {
		Intent intent = new Intent(getActivity(), AddrSarchActivity.class);
		startActivityForResult(intent, REQUEST_ADDR);
	}

	
	
	
	private abstract class ProfessionUI {
		protected View privateUI;
		protected User user;

		public ProfessionUI(User user) {
			this.user = user;
		}

		public abstract void initUI();

		public void colseUI() {
			if (privateUI != null) {
				privateUI.setVisibility(View.GONE);
			}
		}

		public abstract boolean check();

		public abstract Map<String, Object> getInputData();
	}

	
	private class DoctorUI extends ProfessionUI {
		public EditText edtHospital;
		public EditText edtDepartment;

		public DoctorUI(User user) {
			super(user);
			privateUI = rootView.findViewById(R.id.ly_register_doctor_private);
		}

		public void initUI() {
			if (rootView == null) return;
			privateUI.setVisibility(View.VISIBLE);
			edtHospital = (EditText) privateUI.findViewById(R.id.edt_register_doctor_hospital);
			edtDepartment = (EditText) privateUI.findViewById(R.id.edt_register_doctor_department);

			if (user != null) {
				edtHospital.setText(user.hosital);
				edtDepartment.setText(user.deparment);
				user = null;
			}
		}

		@Override
		public Map<String, Object> getInputData() {
			String hospital = edtHospital.getText().toString();
			String department = edtDepartment.getText().toString();
//			int currentSelction = edtDepartment.getCurrentSelction();
//			if (TextUtils.isEmpty(hospital) || currentSelction == -1) {
//				return null;
//			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("hospital", hospital);
			map.put("offices", department);
			return map;
		}

		@Override
		public boolean check() {
			String hospital = edtHospital.getText().toString();
			String department =  edtDepartment.getText().toString();
			return (!TextUtils.isEmpty(hospital) && !TextUtils.isEmpty(department));
		}
	}

	private class TherapistUI extends ProfessionUI implements OnClickListener {
		public TextSpinner tspWorkType;
		public TextView tvAddr;
		public EditText edtSpeciality;

		public TherapistUI(User user) {
			super(user);
			privateUI = rootView
					.findViewById(R.id.ly_register_therapists_private);
		}

		@Override
		public void initUI() {
			if (privateUI == null)
				return;
			privateUI.setVisibility(View.VISIBLE);
			tspWorkType = (TextSpinner) privateUI
					.findViewById(R.id.tsp_register_therapists_profession_lever);
			tspWorkType.setAdapter(proLeverAdapter);
			tvAddr = (TextView) privateUI.findViewById(R.id.tv_register_therapists_addr);
			tvAddr.setOnClickListener(this);
			edtSpeciality = (EditText) privateUI.findViewById(R.id.edt_register_therapists_speciality);
			if (user != null) {
				if(user.jobType>=0){
					tspWorkType.setCurrentSelection(user.jobType);// TODO
				}
				if(user.addressDetail!=null||user.addressDetail!=null){
					tvAddr.setText(user.mapPositionAddress + user.addressDetail);
					LOG.i(TAG, user.mapPositionAddress + user.addressDetail);
				}
				edtSpeciality.setText(user.special);
				user = null;
			}
		}

		@Override
		public Map<String, Object> getInputData() {
			String speciality = edtSpeciality.getText().toString();
			int proLever = tspWorkType.getCurrentSelction();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("adept", speciality);
			map.put("jobCategory", proLever);
			map.put("districtCode", adCode);
			map.put("addressDetail", detail);
			map.put("mapPositionAddress", title);

			return map;
		}

		@Override
		public void onClick(View v) {
			LOG.i(TAG, "[TherapistUI.onClick]");
			getDetailAddr();
		}

		@Override
		public boolean check() {
			String addr = (String) tvAddr.getText();
			String speciality = edtSpeciality.getText().toString();
			int proLever = tspWorkType.getCurrentSelction();
			return !(TextUtils.isEmpty(addr) || TextUtils.isEmpty(speciality) || proLever == -1);
		}
	}

	private class NursingUI extends ProfessionUI implements OnClickListener {
		public TextSpinner tspWorkType;
		public TextView tvAddr;
		public EditText edtHospital;
		public EditText edtSpeciality;

		public NursingUI(User user) {
			super(user);
			privateUI = rootView.findViewById(R.id.ly_register_nurse_private);
		}

		@Override
		public void initUI() {
			if (privateUI == null) return;
			privateUI.setVisibility(View.VISIBLE);

			tspWorkType = (TextSpinner) privateUI.findViewById(R.id.tsp_register_nurse_profession_lever);
			tspWorkType.setAdapter(proLeverAdapter);

			tvAddr = (TextView) privateUI.findViewById(R.id.tv_register_nurse_addr);
			tvAddr.setOnClickListener(this);

			edtHospital = (EditText) privateUI.findViewById(R.id.edt_register_nurse_hospital);
			edtSpeciality = (EditText) privateUI.findViewById(R.id.edt_register_nurse_speciality);

			if (user != null) {
				if(user.jobType>=0){
					tspWorkType.setCurrentSelection(user.jobType);// TODO
				}
				if(user.addressDetail!=null||user.addressDetail!=null){
					tvAddr.setText(user.mapPositionAddress + user.addressDetail);
				}
				edtHospital.setText(user.hosital);
				edtSpeciality.setText(user.special);
				user = null;
			}
		}

		@Override
		public Map<String, Object> getInputData() {
			String hospital = edtHospital.getText().toString();
			String speciality = edtSpeciality.getText().toString();
			int workType = tspWorkType.getCurrentSelction();

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("hospital", hospital);
			map.put("adept", speciality);
			map.put("jobCategory", workType);

			map.put("districtCode", adCode);
			map.put("addressDetail", detail);
			map.put("mapPositionAddress", title);
			return map;
		}

		@Override
		public void onClick(View v) {
			LOG.i(TAG, "[NursingUI.onClick]");
			getDetailAddr();
		}

		@Override
		public boolean check() {
			String addr = (String) tvAddr.getText();
			String hospital = edtHospital.getText().toString();
			String speciality = edtSpeciality.getText().toString();
			int proLever = tspWorkType.getCurrentSelction();

			return (!TextUtils.isEmpty(addr) && !TextUtils.isEmpty(hospital)
					&& !TextUtils.isEmpty(speciality) && proLever != -1);
		}
	}
}
