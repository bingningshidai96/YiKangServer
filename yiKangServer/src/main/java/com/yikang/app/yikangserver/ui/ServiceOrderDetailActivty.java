package com.yikang.app.yikangserver.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.api.RequestParam;
import com.yikang.app.yikangserver.api.ResponseContent;
import com.yikang.app.yikangserver.bean.ServiceOrder;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.api.ApiClient;
import com.yikang.app.yikangserver.utils.LOG;

import java.util.Date;

/**
 * 服务详情页面
 */
public class ServiceOrderDetailActivty extends BaseActivity implements
		OnClickListener {
	private static final String TAG = "ScheduleItemActivty";
	public static final String EXTRA_ITEM_ORDER = "EXTRA_ITEM_ORDER";
	/** 时间、地址、性别、年龄、评估情况、客户电话、服务对象电话 */
	private TextView tvTime, tvAddr, tvSex, tvAge, tvSituation, tvCustomerPho,
			tvPientPho;
	/** 客户电话、服务对象电话 */
	private LinearLayout lyCustomerPhone, lyPatientPhone;
	/** 反馈 */
	private EditText edtFeedBack;
	private Button btConfirm;
	private ServiceOrder order;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar(getString(R.string.schedule_item_title));
	}

	@Override
	protected void findViews() {
		tvAddr = (TextView) findViewById(R.id.tv_service_detail_addr);
		tvAge = (TextView) findViewById(R.id.tv_service_detail_age);
		tvSex = (TextView) findViewById(R.id.tv_service_detail_sex);
		tvTime = (TextView) findViewById(R.id.tv_service_detail_date);
		tvSituation = (TextView) findViewById(R.id.tv_service_detail_situation);
		lyCustomerPhone = (LinearLayout) findViewById(R.id.ly_service_detail_children_phone);
		lyPatientPhone = (LinearLayout) findViewById(R.id.ly_service_detail_parent_phone);
		btConfirm = (Button) findViewById(R.id.bt_service_detail_confirm);
		edtFeedBack = (EditText) findViewById(R.id.edt_service_detail_service_feedback);

		tvCustomerPho = (TextView) findViewById(R.id.tv_service_detail_childPhone);
		tvPientPho = (TextView) findViewById(R.id.tv_service_detail_parenPhone);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_service_detail);

	}

	@Override
	protected void getData() {
		order = (ServiceOrder) getIntent().getSerializableExtra(
				EXTRA_ITEM_ORDER);
		if (order == null) {
			throw new IllegalStateException(
					"must pass an ServiceOrder object which name is EXTRA_ITEM_ORDER");
		}
		showWatingDailog();
		final String url = UrlConstants.URL_SERVICE_ORDER_DETAIL;
		RequestParam param = new RequestParam();
		param.add("orderServiceDetailId", order.id);
		ApiClient.postAsyn(url, param,
                new ApiClient.ResponceCallBack() {
                    @Override
                    public void onSuccess(ResponseContent content) {
                        dismissWatingDailog();
                        String reuslt = content.getData();
                        LOG.i(TAG, "[getData]" + reuslt);
                        order = JSON.parseObject(reuslt, ServiceOrder.class);
                        fillData();
                    }

                    @Override
                    public void onFialure(String status, String message) {
                        dismissWatingDailog();
                        AppContext.showToast(message);
                    }
                });

	}

	private void fillData() {
		tvAddr.setText(order.patientAddr);
		if (order.patientBirthYear > 0) {
			tvAge.setText(new Date().getYear() - order.patientBirthYear);
		}

		if (order.patientSex >= 0) {
			tvSex.setText(MyData.sexMap.get(order.patientSex));
		}
		tvTime.setText(order.date);
		tvPientPho.setText(order.patientPhone);
		tvCustomerPho.setText(order.userPhone);
		tvSituation.setText(order.patientEvaluationResult);
		edtFeedBack.setText(order.feedBack);

		boolean empty = TextUtils.isEmpty(order.userPhone);
		lyCustomerPhone.setVisibility(empty ? View.GONE : View.VISIBLE);
		empty = TextUtils.isEmpty(order.patientPhone);
		lyPatientPhone.setVisibility(empty ? View.GONE : View.VISIBLE);

		btConfirm.setEnabled(order.orderStatus < ServiceOrder.STAUS_COMPLETE);
	}

	@Override
	protected void initViewConent() {
		fillData();
		btConfirm.setEnabled(false);
		lyCustomerPhone.setOnClickListener(this);
		lyPatientPhone.setOnClickListener(this);
		btConfirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ly_service_detail_children_phone:
			callPhone(tvCustomerPho.getText().toString());
			break;
		case R.id.ly_service_detail_parent_phone:
			callPhone(tvPientPho.getText().toString());
			break;
		case R.id.bt_service_detail_confirm:
			submitFeedback();
			break;

		default:
			break;
		}
	}

	/**
	 * 提交反馈
	 */
	private void submitFeedback() {
		showWatingDailog();
		final String url = UrlConstants.URL_SERVICE_ORDER_SUBMIT_FEEDBACK;
		final String feedback = edtFeedBack.getText().toString();
		RequestParam param = new RequestParam();
		param.add("orderServiceDetailId", order.id);
		param.add("feedback", feedback);
		ApiClient.postAsyn(url, param,
                new ApiClient.ResponceCallBack() {
                    @Override
                    public void onSuccess(ResponseContent content) {
                        dismissWatingDailog();
                        order.feedBack = feedback;
                        btConfirm.setEnabled(false);
                        AppContext
                                .showToast(R.string.service_order_feedback_success);
                    }

                    @Override
                    public void onFialure(String status, String message) {
                        dismissWatingDailog();
                        AppContext.showToast(message);
                    }
                });
	}

	/**
	 * 呼叫电话
	 */
	private void callPhone(final String phone) {
		String message = String.format(getString(R.string.schedule_item_call_phone_promt),phone);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(true)
		.setTitle(R.string.dialog_title_prompt)
		.setMessage(message)
		.setPositiveButton(R.string.confirm, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        })
		.create().show();

	}

}
