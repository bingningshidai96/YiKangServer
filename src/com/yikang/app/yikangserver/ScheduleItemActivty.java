package com.yikang.app.yikangserver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.utils.BuisNetUtils;
import com.yikang.app.yikangserver.utils.LOG;

public class ScheduleItemActivty extends BaseActivity implements OnClickListener{
	private static final String TAG = "ScheduleItemActivty";
	public static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";
	
	private TextView tvTime,tvAddr,tvSex,tvAge,tvSituation,tvChildePho,tvParentPho;
	private LinearLayout lyChildrenPhone,lyParentPhone;
	private EditText edtFeedBack;
	private Button btConfirm;
	
	private String itemId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		itemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
		initContent();
		initTitleBar("服务详情");
	}
	
	@Override
	protected void findViews() {
		tvAddr = (TextView) findViewById(R.id.tv_service_detail_addr);
		tvAge = (TextView) findViewById(R.id.tv_service_detail_age);
		tvSex = (TextView) findViewById(R.id.tv_service_detail_sex);
		tvTime = (TextView) findViewById(R.id.tv_service_detail_date);
		tvSituation = (TextView) findViewById(R.id.tv_service_detail_situation);
		lyChildrenPhone = (LinearLayout) findViewById(R.id.ly_service_detail_children_phone);
		lyParentPhone = (LinearLayout) findViewById(R.id.ly_service_detail_parent_phone);
		btConfirm  = (Button) findViewById(R.id.bt_service_detail_confirm);
		edtFeedBack = (EditText) findViewById(R.id.edt_service_detail_service_feedback);
		
		tvChildePho = (TextView) findViewById(R.id.tv_service_detail_childPhone);
		tvParentPho = (TextView) findViewById(R.id.tv_service_detail_parenPhone);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_service_detail);
		
	}

	@Override
	protected void getData() {
		showWatingDailog();
		String url = "";
		RequestParam param = new RequestParam();
		BuisNetUtils.requestStr(url, param, new BuisNetUtils.ResponceCallBack() {
			@Override
			public void onSuccess(ResponseContent content) {
				dismissWatingDailog();
				String reuslt = content.getData();
				LOG.i(TAG, reuslt);
				
			}
			
			@Override
			public void onFialure(String status, String message) {
				dismissWatingDailog();
				AppContext.showToast(message);
			}
		});
		
	}

	@Override
	protected void initViewConent() {
		lyChildrenPhone.setOnClickListener(this);
		lyParentPhone.setOnClickListener(this);
		btConfirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ly_service_detail_children_phone:
			 callPhone(tvChildePho.getText().toString());
			break;
		case R.id.ly_service_detail_parent_phone:
			callPhone(tvParentPho.getText().toString());
			break;
			
		case R.id.bt_service_detail_confirm:
			break;

		default:
			break;
		}
	}
	
	
	
	private void callPhone(final String phone){
		AlertDialog.Builder builder =new AlertDialog.Builder(this);
		builder.setTitle("提示").setMessage("将给"+phone+"拨打电话")
		.setCancelable(true)
		.setPositiveButton("确定", new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+phone));
				startActivity(intent);
			}
		}).create().show();
	}
	
	public static class Item{
		public String dateStr;
		public String addr;
		public String sex;
		public int age;
		public String situation;
	}

}
