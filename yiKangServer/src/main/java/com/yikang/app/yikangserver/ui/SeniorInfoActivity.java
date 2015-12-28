package com.yikang.app.yikangserver.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.RequestParam;
import com.yikang.app.yikangserver.bean.ResponseContent;
import com.yikang.app.yikangserver.data.BusinessState.SenoirState;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.data.MyData.City;
import com.yikang.app.yikangserver.data.UrlConstants;
import com.yikang.app.yikangserver.reciever.UserInfoAltedRevicer;
import com.yikang.app.yikangserver.utils.ApiClient;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.view.TextSpinner;
import com.yikang.app.yikangserver.view.TextSpinner.OnDropDownItemClickListener;
import com.yikang.app.yikangserver.view.adapter.PopListAdapter;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SeniorInfoActivity extends BaseActivity implements
        OnClickListener, AMapLocationListener, OnDropDownItemClickListener {
    public static final int MAX_LOCATTIMES = 3; // 最大定位次数，如果还不能获得结果，就认为定位失败
    private static final String TAG = "SeniorInfoActivity";
    private Button btSubmit;
    private TextSpinner edpSex, edpIdentType, edpRace, edpFaith, edpLiveWith,
            edpPayType, edpIncomeSour, edpRoomOrientation, edpOutWindow,
            edpArea;
    private EditText edtName, edtProfess, edtTel, edtIdNum, edtSocialSecurity,
            edtFloor, edtResidential;
    private TextView tvBirth;
    private TableLayout formTable;
    private Calendar calendar = Calendar.getInstance();// 记录用户选择的日期
    private List<String> areas; // 地区
    private LocationManagerProxy mLocationManagerProxy;
    private double longitude = 0;// 经度
    private double latitude = 0;// 纬度
    private int loacatTimes = 0; // 经纬度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContent();
        initTitleBar(getResources().getString(R.string.senior_info_title_text));
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_senior_info);
    }

    @Override
    protected void getData() {
    }

    /**
     * 初始化view的内容以及行为
     */
    protected void initViewConent() {
        btSubmit.setOnClickListener(this);
        initEditors();

    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        endLocate();
    }

    /**
     * 初始化定位
     */
    private void startLocate() {
        if (mLocationManagerProxy == null) {
            mLocationManagerProxy = LocationManagerProxy.getInstance(this);

            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用destroy()方法
            // 其中如果间隔时间为-1，则定位只定一次
            mLocationManagerProxy.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);

        }
    }

    /**
     * 销毁定位
     */

    private void endLocate() {
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destroy();
            mLocationManagerProxy = null;
        }
    }

    /**
     * find各个view
     */
    protected void findViews() {
        formTable = (TableLayout) findViewById(R.id.ty_senior_info_table);
        btSubmit = (Button) findViewById(R.id.bt_senior_info_submit);
        edtName = (EditText) findViewById(R.id.edt_seniorInfo_name);
        edpSex = (TextSpinner) findViewById(R.id.edt_seniorInfo_sex);
        edtProfess = (EditText) findViewById(R.id.edt_seniorInfo_profession);
        edtTel = (EditText) findViewById(R.id.edt_seniorInfo_tel);
        tvBirth = (TextView) findViewById(R.id.tv_seniorInfo_birthday);
        edpIdentType = (TextSpinner) findViewById(R.id.edt_seniorInfo_ident_type);
        edtIdNum = (EditText) findViewById(R.id.edt_seniorInfo_ident_num);
        edtSocialSecurity = (EditText) findViewById(R.id.edt_seniorInfo_social_security);
        edpRace = (TextSpinner) findViewById(R.id.edt_seniorInfo_race);
        edpFaith = (TextSpinner) findViewById(R.id.edt_seniorInfo_faith);
        edpLiveWith = (TextSpinner) findViewById(R.id.edt_seniorInfo_living_condition);
        edpPayType = (TextSpinner) findViewById(R.id.edt_seniorInfo_payment_type);
        edpIncomeSour = (TextSpinner) findViewById(R.id.edt_seniorInfo_income_sources);
        edpRoomOrientation = (TextSpinner) findViewById(R.id.edt_seniorInfo_room_orientation);
        edpOutWindow = (TextSpinner) findViewById(R.id.edt_seniorInfo_out_window);

        edpArea = (TextSpinner) findViewById(R.id.edt_seniorInfo_area);

        edtFloor = (EditText) findViewById(R.id.edt_seniorInfo_floor);
        edtResidential = (EditText) findViewById(R.id.edt_seniorInfo_residential_quarter);

    }

    private void addEvaluationBag(final String seniorId) {
        RequestParam param = new RequestParam();
        param.add("seniorId", seniorId);
        String url =UrlConstants.URL_ADD_EVALUATION_BAG;
        ApiClient.requestStr(url, param, new ApiClient.ResponceCallBack() {
            @Override
            public void onSuccess(ResponseContent content) {
                dismissWatingDailog();
                try {
                    JSONObject jo = new JSONObject(content.getData());
                    int assessmentId = jo.getInt("assessmentId");
                    LOG.i(TAG, "获得assessmentId" + assessmentId);
                    SenoirState.currSeniorId = seniorId;
                    Intent intent = new Intent(SeniorInfoActivity.this, EvaluationActivity.class);
                    intent.putExtra("assessmentId", assessmentId);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFialure(String status, String message) {
                dismissWatingDailog();
                AppContext.showToast(message);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt_senior_info_submit) submitInfo();

    }

    /**
     * 向服务器提交
     */
    private void submitInfo() {
        showWatingDailog();
        RequestParam param = new RequestParam();
        param.addAll(genrateMapFromInput());
        String url = UrlConstants.URL_SENOIR_INFO_SUBMIT;

        ApiClient.requestStr(url, param, new ApiClient.ResponceCallBack() {
            @Override
            public void onSuccess(ResponseContent content) {
                dismissWatingDailog();
                try {
                    AppContext.showToast(R.string.submit_succeed_tip);
                    JSONObject jo = new JSONObject(content.getData());
                    String seniorId = jo.getString("seniorId");
                    LOG.d(TAG, "获得seniorId:" + seniorId);
                    notifyAddSenior();
                    addEvaluationBag(seniorId);
                } catch (Exception e) {
                    e.printStackTrace();
                    AppContext.showToast(R.string.submit_fail_tip);
                }
            }

            @Override
            public void onFialure(String status, String message) {
                dismissWatingDailog();
                AppContext.showToast(R.string.submit_fail_redo);
            }
        });
    }

    /**
     * 用广播通知数据已经发生改变
     */
    private void notifyAddSenior() {
        sendBroadcast(new Intent(UserInfoAltedRevicer.ACTION_USER_INFO_ALTED));
    }

    /**
     * 初始化各种输入框
     */
    private void initEditors() {
        edpSex.setAdapter(new PopListAdapter(this, MyData
                .getItems(MyData.sexMap)));
        edpIdentType.setAdapter(new PopListAdapter(this, MyData
                .getItems(MyData.identTypeMap)));
        edpRace.setAdapter(new PopListAdapter(this, MyData
                .getItems(MyData.raceMap)));
        edpLiveWith.setAdapter(new PopListAdapter(this, MyData
                .getItems(MyData.livWithMap)));
        edpFaith.setAdapter(new PopListAdapter(this, MyData
                .getItems(MyData.faithMap)));
        edpIncomeSour.setAdapter(new PopListAdapter(this, MyData
                .getItems(MyData.inCometSourceMap)));
        edpPayType.setAdapter(new PopListAdapter(this, MyData
                .getItems(MyData.payMentMap)));
        edpRoomOrientation.setAdapter(new PopListAdapter(this, MyData
                .getItems(MyData.roomOritentationMap)));
        edpOutWindow.setAdapter(new PopListAdapter(this, MyData
                .getItems(MyData.outWindowMap)));
        setDropDownListeners();// 设置监听器，主要作用是让其他editText失去焦点。避免下拉选择后，屏幕会移动到焦点的位置
        List<City> items = MyData.getItems(MyData.cityMap);
//		List<String> cityNames = new ArrayList<String>();
//		for (City city : items) {
//			cityNames.add(city.getName());
//		}
//		edpCity.setAdapter(new PopListAdapter(this, cityNames));
        areas = items.get(0).getListArea();
        edpArea.setAdapter(new PopListAdapter(this, areas));
        tvBirth.setOnClickListener(new OnClickListener() {
            DatePickerFragment dataPicker;

            @Override
            public void onClick(View v) {
                if (dataPicker == null) {
                    dataPicker = new DatePickerFragment();
                }
                dataPicker.show(getFragmentManager(), "datePicker");
            }
        });
    }

    /**
     *
     */
    private void setDropDownListeners() {
        edpArea.setOnDropDownItemClickListener(this);
        edpFaith.setOnDropDownItemClickListener(this);
        edpIdentType.setOnDropDownItemClickListener(this);
        edpIncomeSour.setOnDropDownItemClickListener(this);
        edpLiveWith.setOnDropDownItemClickListener(this);
        edpOutWindow.setOnDropDownItemClickListener(this);
        edpPayType.setOnDropDownItemClickListener(this);
        edpRace.setOnDropDownItemClickListener(this);
        edpRoomOrientation.setOnDropDownItemClickListener(this);
        edpSex.setOnDropDownItemClickListener(this);
    }


    /**
     * 根据用户输入的生成map
     */
    private HashMap<String, Object> genrateMapFromInput() {
        String faith = edpFaith.getText().toString();
        String identType = edpIdentType.getText().toString();
        String incomeSour = edpIncomeSour.getText().toString();
        String liveCondi = edpLiveWith.getText().toString();
        String outWindow = edpOutWindow.getText().toString();
        String payTyp = edpPayType.getText().toString();
        String race = edpRace.getText().toString();
        String roomOrientation = edpRoomOrientation.getText().toString();
        String sex = edpSex.getText().toString();
        String birth = tvBirth.getText().toString();
        String idNum = edtIdNum.getText().toString();
        String name = edtName.getText().toString();
        String socialSecurity = edtSocialSecurity.getText().toString();
        String tel = edtTel.getText().toString();
        String profession = edtProfess.getText().toString();
        String area = edpArea.getText().toString();
        String floor = edtFloor.getText().toString();
        String residential_quarter = edtResidential.getText().toString();

        boolean isNoEmpty = checkEmpty(sex, name);
        if (!isNoEmpty) { // 如果包含空的数据
            Toast.makeText(this, "抱歉，您还有数据为空,请检查输入的数据",
                    Toast.LENGTH_SHORT).show();
            return null;
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        addToParamMap("name", name, map);
        addToParamMap("race", race, MyData.raceMap, map);
        addToParamMap("sex", sex, MyData.sexMap, map);
        addToParamMap("faith", faith, MyData.faithMap, map);
        addToParamMap("cardType", identType, MyData.identTypeMap, map);
        addToParamMap("liveWith", liveCondi, MyData.livWithMap, map);
        addToParamMap("paymentType", payTyp, MyData.payMentMap, map);
        addToParamMap("incomeSources", incomeSour, MyData.inCometSourceMap, map);
        addToParamMap("roomOrientation", roomOrientation, MyData.roomOritentationMap, map);
        addToParamMap("outWindow", outWindow, MyData.outWindowMap, map);
        addToParamMap("socialSecurity", socialSecurity, map);
        addToParamMap("cardNumber", idNum, map);
        addToParamMap("phoneNo", tel, map);
        addToParamMap("profession", profession, map);
        addToParamMap("birthday", birth, map);

        //final String cityStr = "北京";
        // 获取用户所选城市的的编码
        final String cityCode = "110000";
        addToParamMap("city", cityCode, map);
//		for (int i = 0; i < MyData.cityMap.size(); i++) {
//			City cityObject = MyData.cityMap.valueAt(i);
//			if (cityObject.getName().equals(cityStr)) {
//				cityCode = cityObject.getCityCode();
//				addToParamMap("city", cityCode, map);//
//				break;
//			}
//		}
        // 获取地区的编码
        if (!TextUtils.isEmpty(cityCode) && !TextUtils.isEmpty(area)) {
            City city = MyData.cityMap.get(0);
            int areaKey = city.getAreaIntkey(area);
            final String areaCode = city.getAreaCodeValue(areaKey);
            addToParamMap("district", areaCode, map);
        }

        addToParamMap("residentialQuarter", residential_quarter, map);
        addToParamMap("floor",
                !TextUtils.isEmpty(floor) ? Integer.parseInt(floor) : null, map);
        addToParamMap("longitude", longitude, map);
        addToParamMap("latitude", latitude, map);
        addToParamMap("unit", 0, map);
        LOG.i(TAG, map.toString());
        return map;

    }

    /**
     * 传入的所有参数都不为空，则返回true，否则返回false
     *
     * @param strs
     * @return
     */
    private boolean checkEmpty(String... strs) {
        for (String string : strs) {
            if (TextUtils.isEmpty(string)) {
                return false;
            }
        }
        return true;
    }

    public void addToParamMap(String key, String srcValue,
                              SparseArray<String> srcMap, HashMap<String, Object> desmap) {
        if (TextUtils.isEmpty(srcValue) || srcMap == null) {
            LOG.w(TAG, "addToMap 方法传入参数有为空的，直接return");
            return;
        }
        int code = MyData.getIntKey(srcValue, srcMap);
        if (code == -100) { // -100代表找不到
            LOG.w(TAG, "addToMap 方法传入参数srcValue+'" + srcValue
                    + "'在对应的spaseArray中找不到数据");
            return;
        }
        addToParamMap(key, code, desmap);
    }

    public void addToParamMap(String key, Object value,
                              HashMap<String, Object> desmap) {
        if (TextUtils.isEmpty(key) || value == null || desmap == null) {
            LOG.i(TAG, "addToParamMap 方法传入参数有为空的，直接return");
            return;
        }
        if (value instanceof String && ((String) value).isEmpty()) {
            LOG.i(TAG, "addToParamMap 方法传入参数有为空串的，直接return");
            return;
        }
        desmap.put(key, value);
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        loacatTimes++; // 定位次数加1
        if (location != null && location.getAMapException().getErrorCode() == 0) {
            double lat = location.getLatitude();
            double lngt = location.getLongitude();
            // Log.i(TAG,"===="+latitude +"*******"+longitude);
            longitude = lngt;
            latitude = lat;
        }
        // 定位超过十次还没有成功，表示定位失败
        if (loacatTimes > MAX_LOCATTIMES) {
            if (latitude == -1 && longitude == -1) {
                AppContext.showToast(R.string.senior_info_location_fail);
            }
            endLocate();// 结束定位
        }
    }

    @Override
    public void onItemClickListern(TextSpinner spinner, int position) {
//		int id = spinner.getId();
//		if (R.id.edt_seniorInfo_city == id) {
//			City city = MyData.cityMap.valueAt(position);
//			List<String> area = city.getListArea();
//			areas.clear();
//			areas.addAll(area);
//			edpArea.setText("");
//			((PopListAdapter) edpArea.getAdapter()).notifyDataSetChanged();
//		}
        formTable.requestFocus();
    }

    /**
     * 时间选择器
     *
     * @author LGhui
     */
    private class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year = 1960;
            int month = 1;
            int day = 1;
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String month = monthOfYear < 10 ? "0" + (monthOfYear + 1) : String
                    .valueOf(monthOfYear + 1);
            String day = dayOfMonth < 10 ? "0" + dayOfMonth : String
                    .valueOf(dayOfMonth);
            tvBirth.setText(year + "-" + month + "-" + day);
            calendar.set(year, monthOfYear, dayOfMonth);
        }
    }

}
