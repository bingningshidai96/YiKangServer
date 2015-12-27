package com.yikang.app.yikangserver.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

public class BitmapUtils {
	private static final String TAG = "BitmapUtils";

	/**
	 * 将bitmap转换成圆形
	 */
	public static Bitmap toRoundPic(Bitmap src_bitmap) {
		int src_width = src_bitmap.getWidth();
		int src_height = src_bitmap.getHeight();

		Bitmap desbitmap = Bitmap.createBitmap(src_width, src_height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(desbitmap);

		canvas.drawColor(Color.TRANSPARENT);// 将整个画布置为白色

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);

		Rect src_rect = new Rect(0, 0, src_width, src_height);
		Rect des_rect = new Rect(src_rect);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);

		float roundPx = src_width / 2;
		canvas.drawCircle(roundPx, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

		canvas.drawBitmap(src_bitmap, src_rect, des_rect, paint);

		return desbitmap;
	}

	/**
	 * 将图片uri转换成路径
	 */
	public static String getImagePathFromURI(Context context, Uri contentUri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(contentUri, proj,
				null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
			cursor.close();
		}
		return res;
	}

	/**
	 * 采用二次采样，获得图偏
	 */
	public static Bitmap getBitmap(Context context, Uri uri, int weidth,
			int height) {
		String imgPath = getImagePathFromURI(context, uri);
		if (TextUtils.isEmpty(imgPath)) {
			LOG.w(TAG, "[getBitmap]" + "通过uri找不到图片");
			return null;
		}
		return getBitmap(context, imgPath, weidth, height);
	}

	public static Bitmap getBitmap(Context context, String imgPath, int weidth,
			int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgPath);
		int simleZise = Math.min(options.outHeight / weidth, options.outHeight/ height);
		options.inJustDecodeBounds = false;
		options.inSampleSize = simleZise;
		return BitmapFactory.decodeFile(imgPath);
	}

}
