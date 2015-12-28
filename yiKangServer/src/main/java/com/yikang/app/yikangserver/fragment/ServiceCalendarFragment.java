package com.yikang.app.yikangserver.fragment;

import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.ViewHolder;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.bean.ServiceOrder;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.ui.ServiceOrderDetailActivty;
import com.yikang.app.yikangserver.utils.ApiClient;

/**
 * 被服务对象列表
 */
public class ServiceCalendarFragment extends BaseListFragment<ServiceOrder> {
	private static final String TAG = "PaintListFragment";
	private static final String ARG_TYPE = "type";
	private Type type;

	public static enum Type {
		Complete(1), Incomplete(0);
		private final int code;

		private Type(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}

	private ServiceCalendarFragment() {
		super();
	}

	public static ServiceCalendarFragment getInstance(Type type) {
		ServiceCalendarFragment fragment = new ServiceCalendarFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_TYPE, type);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = (Type) getArguments().getSerializable(ARG_TYPE);
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_service_calendar;
	}

	@Override
	protected void convert(ViewHolder holder, ServiceOrder item) {
		TextView tvName = holder.getView(R.id.tv_service_carlendar_name);
		TextView tvAddr = holder.getView(R.id.tv_service_carlendar_addr);
		TextView tvDate = holder.getView(R.id.tv_service_carlendar_date);
		View badge = holder.getView(R.id.view_service_carlendar_badge);
		tvAddr.setText(item.patientAddr);
		tvDate.setText(item.date);
		tvName.setText(item.patientName);
		if (item.isRead) {
			badge.setVisibility(View.GONE);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setRefreshEnable(true);
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
	protected void sendRequestData(final RequestType requestType) {
		// TODO 在这个地方应该根据type来使用不用的url
		final String url = UrlConstants.URL_ORDER_LIST;
		RequestParam param = new RequestParam();
		param.add("serviceDetailStatus", type.getCode());
		ApiClient.requestStr(url, param,
				new ApiClient.ResponceCallBack() {
					@Override
					public void onSuccess(ResponseContent content) {
						String result = content.getData();
						Log.i(TAG, "[getData]" + result);
						List<ServiceOrder> list = JSON.parseArray(result,
								ServiceOrder.class);
						mData.clear();
						mData.addAll(list);
						mAdapter.notifyDataSetChanged();
						onLoadResult(requestType, true);
					}

					@Override
					public void onFialure(String status, String message) {
						AppContext.showToast(message);
						onLoadResult(requestType, false);
					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final String itemId = mData.get(position).id;
		setRead(itemId);
		Intent intent = new Intent(getActivity(),
				ServiceOrderDetailActivty.class);
		intent.putExtra(ServiceOrderDetailActivty.EXTRA_ITEM_ORDER,
				mData.get(position));
		startActivity(intent);
	}

	/** 告诉服务器本条消息已经读取 */
	private void setRead(String itemId) {

	}

}
