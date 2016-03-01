package com.yikang.app.yikangserver.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.fragment.OrderListFragment;

/**
 * Created by liu on 16/1/29.
 */
public class OrdersManageActivity extends BaseActivity{
    public static final String EXTRA_USER_ID = "userId";

    private String userId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getStringExtra(EXTRA_USER_ID);
        initContent();
        initTitleBar(getString(R.string.order_manger_title));
    }


    @Override
    protected void findViews() {
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_order_manager);
    }

    @Override
    protected void getData() {}

    protected void initViewContent() {
        OrderListFragment orderListFragment = new OrderListFragment();
        Bundle arguments = new Bundle();
        arguments.putString(OrderListFragment.EXTRA_USERID,userId);
        orderListFragment.setArguments(arguments);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fl_order_manager_container,orderListFragment).commit();
    }




}
