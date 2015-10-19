package com.yikang.app.yikangserver.view;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;

public class CustomWatingDialog  extends ProgressDialog{
		private Context context;
		private String message;
		public CustomWatingDialog(Context context, String message) {
			super(context, R.style.CustomProgressDialog);
			this.context = context;
			this.message = message;
			
		}

		public CustomWatingDialog(Context context) {
			this(context, null);
		}
		
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_waiting);
			if(message!=null){
				TextView tvMsg = (TextView) findViewById(R.id.tv_loadingText);
				tvMsg.setText(message);
			}
		}
		
		
		
		
		@Override
		public void onWindowFocusChanged(boolean hasFocus) {
			super.onWindowFocusChanged(hasFocus);
			ImageView img = (ImageView) findViewById(R.id.iv_loadingImageView);
			if(!hasFocus){
				dismiss();
				return ;
			}
			Animation animation = AnimationUtils.loadAnimation(context, R.anim.waiting_routate);
			img.startAnimation(animation);
		}
		
	}