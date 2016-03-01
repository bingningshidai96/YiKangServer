package com.yikang.app.yikangserver.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.yikang.app.yikangserver.bean.SimpleAddr;
import com.yikang.app.yikangserver.utils.LOG;

/**
 * 地址的搜索提示业务
 * 
 * 
 */
public class AddressInputTipsModel implements InputtipsListener {
	private static final String TAG = "AddressInputTipsModel";
	private String keyWord;
	private String cityCode;
	private Inputtips inputTips;

	public AddressInputTipsModel(Context context, String cityCode) {
		this.cityCode = cityCode;
		inputTips = new Inputtips(context, this);
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getKeyWord() {
		return keyWord;
	}

	/**
	 * 开始异步搜索
	 */
	public void requestInputtips() {
		LOG.d(TAG, "requestInputtips");
		if (TextUtils.isEmpty(keyWord)) {
			Log.d(TAG, "[requestInputtips] keyword is empty");
			return;
		}
		try {
			inputTips.requestInputtips(keyWord, cityCode);
		} catch (AMapException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGetInputtips(List<Tip> tipList, int rcode) {
		LOG.d(TAG, "====rcode " + rcode);
		if (rcode == 0) { // 0表示返回成功
			if (tipList == null) {
				LOG.e(TAG, "[onGetInputtips]there is no tip return from net");
				return;
			}
			LOG.i(TAG, "[onGetInputtips]tip's length:" + tipList.size());
			ArrayList<SimpleAddr> list = new ArrayList<SimpleAddr>();
			for (Tip tip : tipList) { // 将数据组织进一个list回调传给调用者
				SimpleAddr addr = new SimpleAddr();
				addr.title = tip.getName();
				addr.district = tip.getDistrict();
				addr.adCode = tip.getAdcode();
				list.add(addr);
			}
			listener.onTipsResult(list, true);

		} else {
			listener.onTipsResult(null, false);
			Log.e(TAG, "sorry,search fails!!!");
		}
	}

	/**
	 * 
	 */
	private OnTipsResultListener listener;

	public void setOnSearchResultListener(OnTipsResultListener listener) {
		this.listener = listener;
	}

	/**
	 * 搜索结果的监听器，
	 * 
	 * @author LGhui
	 * 
	 */
	public interface OnTipsResultListener {
		/**
		 * 回调方法，返回搜索结果
		 * 
		 * @param addrList
		 *            地址的List
		 * @param keyWord
		 *            addrList对应的搜索关键字
		 * @param isRusultOK
		 *            是否是请求成功的结果
		 */
		void onTipsResult(List<SimpleAddr> addrList, boolean isRusultOK);

	}

}
