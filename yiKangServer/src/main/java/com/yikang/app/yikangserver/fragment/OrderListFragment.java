package com.yikang.app.yikangserver.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.ViewHolder;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.Order;
import com.yikang.app.yikangserver.utils.DeviceUtils;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * 订单list的fragment
 */
public class OrderListFragment extends BaseListFragment<Order> {
    public static final String EXTRA_USERID ="userid";

    private static final String TAG = "OrderListFragment";
    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getString(EXTRA_USERID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ColorDrawable divider = new ColorDrawable(getResources().getColor(R.color.divider_gray));
        mListView.setDivider(divider);
        mListView.setDividerHeight((int) (DeviceUtils.getDensity() * 10));
        int backgroundColor = getResources().getColor(R.color.divider_gray);
        mRefreshLayout.setBackgroundColor(backgroundColor);
        return view;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_order;
    }

    @Override
    protected void convert(ViewHolder holder, Order item) {

        TextView orderId = holder.getView(R.id.tv_order_id);
        TextView appointTime = (TextView) holder.getView(R.id.tv_order_appointment_time);
        TextView comment = (TextView) holder.getView(R.id.tv_order_comment);
        TextView money = (TextView) holder.getView(R.id.tv_order_money);
        TextView paidStatus = (TextView) holder.getView(R.id.tv_order_paid_status);
        TextView serviceName = (TextView) holder.getView(R.id.tv_order_service_name);
        TextView orderTime = (TextView) holder.getView(R.id.tv_order_time);
        TextView userName = (TextView) holder.getView(R.id.tv_order_username);
        TextView address = holder.getView(R.id.tv_order_address);

        orderId.setText(item.orderId);
        appointTime.setText(item.appointmentDate + " " + item.startTime);
        comment.setText(item.comment);
        Currency currency = Currency.getInstance(Locale.CHINA);
        money.setText(currency.getSymbol() +item.paidCount);
        paidStatus.setText(item.orderStatus > 0 ? "已支付" : "未支付");
        serviceName.setText(item.serviceName);
        orderTime.setText(item.orderTime);
        userName.setText(item.userName);
        address.setText(item.mapPosition + item.addressDetail);

    }


    @Override
    protected void sendRequestData(final RequestType requestType) {
        Api.getOrderList(userId, new ResponseCallback<List<Order>>() {
            @Override
            public void onSuccess(List<Order> list) {
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

}
