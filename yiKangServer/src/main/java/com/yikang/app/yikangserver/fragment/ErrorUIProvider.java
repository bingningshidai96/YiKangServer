package com.yikang.app.yikangserver.fragment;

import android.view.View;

import com.yikang.app.yikangserver.view.ErrorLayout;


public interface ErrorUIProvider {
    enum ErrorType{
        netError,noData;
    }
    View getErrorView(ErrorLayout.ErrorType errorType,String message);
}
