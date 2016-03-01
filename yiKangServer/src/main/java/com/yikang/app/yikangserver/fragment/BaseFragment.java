package com.yikang.app.yikangserver.fragment;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.interf.NetworkErrorHandler;
import com.yikang.app.yikangserver.interf.UINetwork;
import com.yikang.app.yikangserver.view.CustomWatingDialog;
import android.app.Dialog;
import android.app.Fragment;

public class BaseFragment extends Fragment implements UINetwork,NetworkErrorHandler{

	private Dialog waitingDialog;

	protected void showWaitingUI() {
		showWaitingUI(getString(R.string.waiting_loading));
	}

	public void showWaitingUI(String message) {
		if(getActivity() instanceof UINetwork){
			((UINetwork) getActivity()).showWaitingUI(message);
			return;
		}

		if (waitingDialog == null) {
			waitingDialog = createDialog(message);
		}
		if (!waitingDialog.isShowing())
			waitingDialog.show();
	}
	

	public void hideWaitingUI() {
		if(getActivity() instanceof UINetwork){
			((UINetwork) getActivity()).hideWaitingUI();
			return;
		}

		if (waitingDialog != null && waitingDialog.isShowing()) {
			waitingDialog.dismiss();
		}
	}

	@Override
	public void showErrorUI() {

	}

	@Override
	public void hideErrorUI() {

	}

	@Override
	public void reload() {

	}

	/**
	 * 创建一个dialog，同过复写这个方法可以创建其他样式的dialog
	 */
	protected Dialog createDialog(String message) {
		return new CustomWatingDialog(getActivity(), message);
	}

	@Override
	public void onStop() {
		super.onStop();
		hideWaitingUI();
	}

}
