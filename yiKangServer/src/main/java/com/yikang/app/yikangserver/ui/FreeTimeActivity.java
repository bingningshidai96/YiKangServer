package com.yikang.app.yikangserver.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.CommonAdapter;
import com.yikang.app.yikangserver.adapter.ViewHolder;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.TimeDuration;
import com.yikang.app.yikangserver.utils.LOG;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择自己的空闲时间页面
 */
public class FreeTimeActivity extends BaseActivity implements OnClickListener,
        OnCheckedChangeListener {
    public static final String EXTRA_IS_EDIT_SUCCESS = "isEditeSuccess";
    public static final String EXTRA_SERVICE_DATE = "serviceDate";
    public static final String EXTRA_SERVICE_DATE_STR = "serviceDateStr";
    public static final int RESULT_CODE_SUCCESS = 0x01;
    private static final String TAG = "EditTimeActivity";
    private Button btConfirm;
    private GridView gvTimes;
    private List<TimeDuration> data = new ArrayList<TimeDuration>();

    // 需要传给服务器的参数
    private String serviceDate;
    private ArrayList<Integer> selectedList = new ArrayList<Integer>();
    private CommonAdapter<TimeDuration> adapter;
    private boolean submitEnable;
    private TextView tvTitleRight;
    //TODO
    private ResponseCallback<FreeTimeData> TimeDurationListHandler
            = new ResponseCallback<FreeTimeData>() {
        @Override
        public void onSuccess(FreeTimeData data) {
            hideWaitingUI();
            final Integer submitEnable = data.isCanSubmit;
            setEditable(submitEnable == 0);
            setTimeDurationData(data.data);
        }

        @Override
        public void onFailure(String status, String message) {
            hideWaitingUI();
            AppContext.showToast(message);
        }
    };
    private ResponseCallback<Void> submitDateHandler = new ResponseCallback<Void>() {
        @Override
        public void onSuccess(Void data) {
            hideWaitingUI();
            AppContext.showToast(getString(R.string.submit_succeed_tip));
            Intent intent = new Intent();
            intent.putExtra(FreeTimeActivity.EXTRA_IS_EDIT_SUCCESS, true);
            setResult(RESULT_CODE_SUCCESS, intent);
            finish();
        }

        @Override
        public void onFailure(String status, String message) {
            hideWaitingUI();
            AppContext.showToast(getString(R.string.submit_fail_tip));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContent();
        initTitleBar(serviceDate);
        final boolean initEditable = false;
        setEditable(initEditable);
    }

    @Override
    protected void initTitleBar(String title) {
        super.initTitleBar(title);
        tvTitleRight = (TextView) findViewById(R.id.tv_title_right_text);
        tvTitleRight.setText(getString(R.string.select_all));
        tvTitleRight.setOnClickListener(this);
    }

    @Override
    protected void findViews() {
        btConfirm = (Button) findViewById(R.id.bt_detial_time_confirm);
        gvTimes = (GridView) findViewById(R.id.gv_detial_time_times);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_edite_free_time);
    }

    @Override
    protected void getData() {
        serviceDate = getIntent().getStringExtra(EXTRA_SERVICE_DATE_STR);
        if (!TextUtils.isEmpty(serviceDate)) {
            getEditableTime();
            LOG.i(TAG, "[getData]" + serviceDate);
        } else {
            LOG.e(TAG, "[getData] the extra " + EXTRA_SERVICE_DATE_STR
                    + " didnt been pass to this activity");
        }
    }

    @Override
    protected void initViewContent() {
        btConfirm.setOnClickListener(this);
        LOG.i(TAG, "[initViewContent]" + submitEnable);
        adapter = new CommonAdapter<TimeDuration>(this, data,
                R.layout.item_blue_gray_background_check) {
            @Override
            protected void convert(ViewHolder holder, TimeDuration item) {
                int position = holder.getPosition();
                String format = "%02d:00 - %02d:00";
                CheckBox chk = holder.getView(R.id.chk_item_checkbox);
                chk.setText(String.format(format, item.startTime, item.endTime));
                chk.setTag(position);
                chk.setChecked(selectedList.contains(item.serviceId));
                chk.setEnabled(submitEnable);
            }

            @Override
            protected void initCreatedHolder(ViewHolder holder) {
                super.initCreatedHolder(holder);
                CheckBox chk = holder.getView(R.id.chk_item_checkbox);
                chk.setOnCheckedChangeListener(FreeTimeActivity.this);
            }
        };
        gvTimes.setAdapter(adapter);
    }



    /**
     * 获取可编辑的时间
     */
    private void getEditableTime() {
        showWaitingUI();
        Api.getEditTime(serviceDate, TimeDurationListHandler);
    }



    /**
     * 设置是否是可编辑的
     */
    private void setEditable(boolean editable) {
        this.submitEnable = editable;
        btConfirm.setVisibility(submitEnable ? View.VISIBLE : View.GONE);
        tvTitleRight.setEnabled(editable);
        int colorDisable = getResources().getColor(R.color.text_color_disable);
        int colorEnable = getResources().getColor(R.color.white);
        tvTitleRight.setTextColor(editable ? colorEnable : colorDisable);
    }



    /**
     * 设置时间端的数据源
     **/
    private void setTimeDurationData(List<TimeDuration> list) {
        data.addAll(list);
        for (TimeDuration timeDuration : data) {
            if (timeDuration.isChecked) {
                selectedList.add(timeDuration.serviceId);
            }
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * 提交数据
     */
    private void submitData() {
        if (selectedList.isEmpty()) {
            AppContext.showToast(R.string.edtTime_choose_contraint_tip);
            return;
        }
        showWaitingUI();
        Api.submitTimes(serviceDate, selectedList, submitDateHandler);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_detial_time_confirm:
                submitData();
                break;
            case R.id.tv_title_right_text:
                selectAll();
            default:
                break;
        }
    }

    /**
     * 选择全部
     */
    private void selectAll() {
        if (!data.isEmpty()) { // 必须在数据刷新下来之后
            selectedList.clear();
            for (TimeDuration timeDuration : data) {
                selectedList.add(timeDuration.serviceId);
            }
            adapter.notifyDataSetChanged();
        }
    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Integer position = (Integer) buttonView.getTag();
        if (isChecked) {
            if (!selectedList.contains(data.get(position).serviceId)) {
                selectedList.add(data.get(position).serviceId);
            }
            LOG.i(TAG, "[onCheckedChanged]选中了" + position);
        } else {
            selectedList.remove(Integer.valueOf((data.get(position).serviceId)));
            LOG.i(TAG, "[onCheckedChanged]取消选中了" + position);
        }
        LOG.i(TAG, selectedList.toString());
    }


    public static class FreeTimeData {
        @SerializedName("isCanSubmit")
        public int isCanSubmit;
        @SerializedName("data")
        public List<TimeDuration> data;
    }
}
