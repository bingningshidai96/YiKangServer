package com.yikang.app.yikangserver.fragment;

import java.util.List;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
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
import com.yikang.app.yikangserver.bean.SeniorInfo;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.reciever.UserInfoAltedRevicer;
import com.yikang.app.yikangserver.ui.EvaluationRecordActivity;
import com.yikang.app.yikangserver.utils.ApiClient;

public class SeniorListFragment extends BaseListFragment<SeniorInfo> {
	private static final String TAG = "SeniorListFragment";
	private UserInfoAltedRevicer refreshReceiver = new UserInfoAltedRevicer();
	
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String action = UserInfoAltedRevicer.ACTION_USER_INFO_ALTED; 
		IntentFilter filter = new IntentFilter(action);
		getActivity().registerReceiver(refreshReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(refreshReceiver);
	}
	
	@Override
	protected int getItemLayoutId() {
		return R.layout.item_senoir;
	}

	@Override
	protected void convert(ViewHolder holder, SeniorInfo item) {
		TextView tvName = holder.getView(R.id.tv_item_senior_name);
		tvName.setText(item.getName());
	}
	
	
	@Override
	protected void sendRequestData(final RequestType requestType) {
		RequestParam params = new RequestParam();
		ApiClient.requestStr(UrlConstants.URL_GET_SENIOR_LIST, params, new ApiClient.ResponceCallBack() {
			@Override
			public void onSuccess(ResponseContent content) {
				String json = content.getData();
				if (!TextUtils.isEmpty(json)) {
					mData.clear();
					List<SeniorInfo> list = JSON.parseArray(json, SeniorInfo.class);
					if (list != null && !list.isEmpty()) {
						mData.addAll(list);
					}
					mAdapter.notifyDataSetChanged();
				}
				onLoadResult(requestType, true);
			}

			@Override
			public void onFialure(String status, String message) {
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
	public void onStart() {
		super.onStart();
		checkRefresh();
	}

	/**
	 * 检查是否有更新
	 */
	private void checkRefresh() {
		if (refreshReceiver.getAndConsume()) {
			onRefresh();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(),
				EvaluationRecordActivity.class);
		Log.d(TAG, "[onItemClick]" + mData.get(position).getSeniorId());
		intent.putExtra("seniorId", mData.get(position).getSeniorId());
		startActivity(intent);
	}

}
