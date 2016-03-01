package com.yikang.app.yikangserver.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.ViewHolder;
import com.yikang.app.yikangserver.api.ApiClient;
import com.yikang.app.yikangserver.api.RequestParam;
import com.yikang.app.yikangserver.api.ResponseContent;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.Appointment;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.utils.DeviceUtils;
import com.yikang.app.yikangserver.utils.LOG;

import java.util.List;


/**
 * Created by liu on 16/2/1.
 */
public class AppointmentListFragment extends BaseListFragment<Appointment> {

    private static final String TAG = "AppointmentListFragment";


    @Override
    protected int getItemLayoutId() {
        return R.layout.item_appointment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ColorDrawable divider = new ColorDrawable(Color.WHITE);
        mListView.setDivider(divider);
        mListView.setDividerHeight((int) (DeviceUtils.getDensity() * 10));
        return view;
    }

    @Override
    protected void convert(ViewHolder holder, Appointment item) {

        TextView appointmentId = holder.getView(R.id.tv_appointment_id);
        TextView comment = (TextView) holder.getView(R.id.tv_appointment_comment);
        TextView time = holder.getView(R.id.tv_appointment_time);
        TextView money = (TextView) holder.getView(R.id.tv_appointment_money);
        TextView serviceName = (TextView) holder.getView(R.id.tv_appointment_service_name);
        TextView userName = (TextView) holder.getView(R.id.tv_appointment_username);


        appointmentId.setText(item.appointmentId);
        comment.setText(item.comment);
        money.setText("￥" + item.servicePrice);
        serviceName.setText(item.serviceName);
        userName.setText(item.userName);
        time.setText("" + item.createTime);
    }

    @Override
    protected void sendRequestData(final RequestType requestType) {
//        String url = UrlConstants.URl_APPOINTMENT_LIST;
//        RequestParam param = new RequestParam();
//
//        ApiClient.postAsyn(url, param, new ApiClient.ResponceCallBack() {
//            @Override
//            public void onSuccess(ResponseContent content) {
//                String json = content.getData();
//                List<Appointment> list = JSON.parseArray(json, Appointment.class);
//                mData.clear();
//                mData.addAll(list);
//                mAdapter.notifyDataSetChanged();
//                LOG.i(TAG, "[sendRequestData]" + json);
//                onLoadResult(requestType, true);
//            }
//
//            @Override
//            public void onFailure(String status, String message) {
//                AppContext.showToast(message);
//                onLoadResult(requestType, false);
//            }
//        });
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
}
