package com.yikang.app.yikangserver;

import com.yikang.app.yikangserver.fragment.SystemSelectPhotoFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SelePicManager {
	private Context context;
	private FrameLayout contianer;

	public SelePicManager(View rootView,FragmentManager manager) {
		final View view = rootView;
		context = rootView.getContext();
		
		 
		FrameLayout framLayout = new FrameLayout(rootView.getContext());
		framLayout.setLayoutParams(view.getLayoutParams());
		
		ViewGroup parent = (ViewGroup)view.getParent();
		final int indexOfChild = parent.indexOfChild(view);
		parent.removeView(view);
		framLayout.addView(view);
		parent.addView(framLayout,indexOfChild);
		
		int id = 0x7f0c01f7;
		while(rootView.findViewById(id)!=null){
			id = (int) (Math.random()*id); 
		}
		contianer = new FrameLayout(context);
		contianer.setId(id);
		contianer.setLayoutParams(view.getLayoutParams());
		contianer.setVisibility(View.GONE);
		framLayout.addView(contianer);
		
		
		SystemSelectPhotoFragment fragment = new SystemSelectPhotoFragment();
		
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(id, fragment).commit();
	}
	
}
