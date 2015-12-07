package com.yikang.app.yikangserver.fragment;

import java.io.File;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;

public class SystemSelectPhotoFragment extends Fragment implements
		OnClickListener {
	private static final int REQUEST_PIC_PICK = 101;
	private static final int REQUEST_PHOTO_CUT = 102;
	private static final int REQUEST_CAMERA = 103;

	private static final String PHOTO_FILE_NAME = "temp.jpg";
	private static final String CARAMER_PHTO_NAME = "photo.jpg";
	private static final String TAG = "SystemSelectPhotoFragment";

	private Uri originUri; // 表示选择照片后传回来文件的uri
	private File completeFile; // 临时文件，用于指向裁剪完成后的照片
	private PopupWindow pWindow;

	public void setOnResultListener(OnResultListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		framContainer = new FrameLayout(getActivity());
		ViewGroup.LayoutParams layoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		framContainer.setLayoutParams(layoutParams);
		return framContainer;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initPopUpWindow();
	}

	private void initPopUpWindow() {
		if (pWindow != null && !pWindow.isShowing()) {
			pWindow.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
			return;
		}
		// 初始化弹出框的控件
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.ppw_register_get_pic,
				(ViewGroup) getView(), false);
		View btCancel = view.findViewById(R.id.bt_register_ppw_cancel);
		View btCapture = view.findViewById(R.id.bt_register_ppw_capture);
		View btChoose = view.findViewById(R.id.bt_register_ppw_choose);
		btCancel.setOnClickListener(this);
		btCapture.setOnClickListener(this);
		btChoose.setOnClickListener(this);
		// 新建弹出框
		pWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		pWindow.setAnimationStyle(R.style.style_window_slow_up_down);
		// ColorDrawable drawable = new
		// ColorDrawable(Color.parseColor("#aad9d6d8"));
		// pWindow.setBackgroundDrawable(drawable);
		pWindow.setFocusable(true);
		pWindow.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
	}

	private OnResultListener listener;
	private FrameLayout framContainer;

	/**
	 * 当照片选择完成之后，调用这个接口，将结果传给对应的activity
	 * 
	 */
	public interface OnResultListener {
		void onComplete(String url);

		void onCancel();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.bt_register_ppw_capture:
			camera(); // 在弹出框选择了拍摄照片
			break;
		case R.id.bt_register_ppw_choose:
			choosePic(); // 在弹出框选择了选择相片
			break;
		case R.id.bt_register_ppw_cancel:
			if (listener != null)
				listener.onCancel();
			break;
		default:
			break;
		}
	}

	/**
	 * 裁剪图片
	 * 
	 * @param dataUri
	 */
	private void crop(Uri dataUri, Uri outputUri, int outputX, int outputY) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(dataUri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
		intent.putExtra("return-data", false);// 若为false则表示不返回数据
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", false);
		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
		startActivityForResult(intent, REQUEST_PHOTO_CUT);
	}

	/**
	 * 选择图片
	 */
	private void choosePic() {
		Intent intent = new Intent();
		/* 开启Pictures画面Type设定为image */
		intent.setType("image/**");
		/* 使用Intent.ACTION_GET_CONTENT这个Action */
		intent.setAction(Intent.ACTION_PICK);
		/* 取得相片后返回本画面 */
		startActivityForResult(intent, REQUEST_PIC_PICK);
	}

	/*
	 * 从相机获取
	 */
	public void camera() {
		// 激活相机
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

		boolean hasSdcard = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
		// 判断存储卡是否可以用，可用进行存储
		if (hasSdcard) {
			File cameraFile = new File(AppContext.CACHE_IMAGE_PATH,
					CARAMER_PHTO_NAME);
			// 从文件中创建uri
			originUri = Uri.fromFile(cameraFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, originUri);
		}
		// 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
		startActivityForResult(intent, REQUEST_CAMERA);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		pWindow.dismiss();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (completeFile == null) {
			completeFile = new File(AppContext.CACHE_IMAGE_PATH,
					PHOTO_FILE_NAME);
		}
		if (requestCode == REQUEST_PIC_PICK && resultCode == Activity.RESULT_OK) {
			originUri = data.getData();
			crop(originUri, Uri.fromFile(completeFile), 300, 300);
		} else if (requestCode == REQUEST_CAMERA) {
			crop(originUri, Uri.fromFile(completeFile), 300, 300);
		} else if (requestCode == REQUEST_PHOTO_CUT
				&& resultCode == Activity.RESULT_OK) {
			if (listener != null) {
				listener.onComplete(completeFile.getAbsolutePath());
			}
		}
	}

}
