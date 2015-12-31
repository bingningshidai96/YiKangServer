package com.yikang.app.yikangserver.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.view.ErrorLayout;

/**
 * Created by liu on 15/12/31.
 */
public abstract class BaseNetFragment extends BaseFragment{

    private FrameLayout mainContainer;
    private ErrorLayout errorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basenet,container,false);
        mainContainer = (FrameLayout) view.findViewById(R.id.fl_basenet_contianer);
        errorLayout = (ErrorLayout) view.findViewById(R.id.error_layout);

        View mainView = inflater.inflate(getLayoutId(), mainContainer, false);
        mainContainer.addView(mainView);

        return view;
    }

    
    protected abstract int getLayoutId();



    public void showErrorLayout(){
        errorLayout.setVisibility(View.VISIBLE);
    }

    public void hideErrorLayout(){
        errorLayout.setVisibility(View.GONE);
    }



}
