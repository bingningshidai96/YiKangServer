package com.yikang.app.yikangserver.test;

import com.yikang.app.yikangserver.adapter.ViewHolder;
import com.yikang.app.yikangserver.bean.Answer;
import com.yikang.app.yikangserver.fragment.BaseListFragment;

public class TestFragment extends BaseListFragment<Answer> {

	@Override
	protected int getItemLayoutId() {
		return 0;
	}

	@Override
	protected void convert(ViewHolder holder, Answer item) {

	}

	@Override
	protected void sendRequestData(RequestType requestType) {

	}

}
