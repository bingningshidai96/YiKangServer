package com.yikang.app.yikangserver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.ViewHolder;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.InviteCustomer;
import com.yikang.app.yikangserver.ui.OrdersManageActivity;
import com.yikang.app.yikangserver.view.CircleImageView;

import java.util.List;

/**
 * 推荐病患列表的fragment
 */
public class InviteCustomerFragment extends BaseListFragment<InviteCustomer> {
	public static final String TAG = "InviteCustomerFragment";
	public static final String ARG_TYPE = "type";

	private Type type;

	public enum Type {
		all(-1), registered(0), consumed(1);
		private int code;

		Type(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		type = (Type) getArguments().getSerializable(ARG_TYPE);
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	protected View createDefaultContentTips() {
		View view = super.createDefaultContentTips();
		TextView tvTips = (TextView) view.findViewById(R.id.tv_data_fail_describe);
		int resId = R.string.customerList_no_customer_register_tip;
		if (type == Type.consumed) {
			resId = R.string.customerList_no_customer_consume_tip;
		}
		tvTips.setText(resId);
		return view;
	}

	public static InviteCustomerFragment getInstance(Type type) {
		InviteCustomerFragment fragment = new InviteCustomerFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(ARG_TYPE, type);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_customer;
	}

	@Override
	protected void convert(ViewHolder holder, InviteCustomer item) {
		CircleImageView img = holder.getView(R.id.iv_customer_item_img);
		TextView tvName = holder.getView(R.id.tv_customer_item_name);
		TextView tvStatus = holder.getView(R.id.tv_customer_item_status);
		if (!TextUtils.isEmpty(item.imgUrl)) {
			ImageLoader.getInstance().displayImage(item.imgUrl, img);
		}
		tvName.setText(item.name);
		if (item.status == InviteCustomer.STATUS_REGISTER) {
			tvStatus.setTextColor(getResources().getColor(
					R.color.patientList_stauts_registed));
			tvStatus.setText(getString(R.string.customerList_status_registed));
			holder.getView(R.id.ly_customer_item_time_container).setVisibility(
					View.GONE);
		} else if (item.status == InviteCustomer.STATUS_CONSUMED) {
			tvStatus.setTextColor(getResources().getColor(
					R.color.patientList_stauts_consumed));
			tvStatus.setText(getString(R.string.customerList_status_consumed));
			TextView tvTime = holder
					.getView(R.id.tv_customer_item_service_time);
			holder.getView(R.id.ly_customer_item_time_container).setVisibility(
					View.VISIBLE);
			tvTime.setText(item.consumeDate);
		}
	}

	@Override
	protected void sendRequestData(final RequestType requestType) {
		Api.getMyPaintList(type.getCode(), new ResponseCallback<List<InviteCustomer>>() {
			@Override
			public void onSuccess(List<InviteCustomer> list) {
				mData.clear();
				mData.addAll(list);
				mAdapter.notifyDataSetChanged();
				onLoadResult(requestType, true);
			}

			@Override
			public void onFailure(String status, String message) {
				AppContext.showToast(message);
				onLoadResult(requestType, false);
			}
		});
	}
	
	/**
	 * 处理加载结果
	 * <ul>
	 * 	<li>首先有由加载状态恢复到正常状态
	 * 	<li>如果数据不为空，则不显示相关失败页面
	 *  <li>否则，如果网络成功，显示内容相关提示，否则显示网络失败
	 * </ul>
	 */
	private void onLoadResult(RequestType type,boolean netWorkSuceed){
		if(type == RequestType.refresh){
			onRefreshFinish();
		}else{
			onLoadFinish();
		}
		if(mData.isEmpty()){
			setTipsVisible(true);
			if(netWorkSuceed){
				setTips(createDefaultContentTips());
			}else{
				setTips(createDefaultNetWorkTips());
			}
		}else{
			setTipsVisible(false);
		}
		
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(mData.get(position).status == InviteCustomer.STATUS_CONSUMED){
			//显示订单列表
			Intent intent = new Intent(getActivity(),
					OrdersManageActivity.class);
			Log.d(TAG, "[onItemClick]" + mData.get(position).userId);
			intent.putExtra(OrdersManageActivity.EXTRA_USER_ID,mData.get(position).userId);
			startActivity(intent);
		}

	}

}
