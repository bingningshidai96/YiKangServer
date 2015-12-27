package com.yikang.app.yikangserver.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 用与
 * Created by liu on 15/12/24.
 */
public class FractionXLayout extends AnimationLayout{

    private int screenWidth;

    public FractionXLayout(Context context) {
        super(context);
    }

    public FractionXLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FractionXLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void applyAnimValue() {
        setX(screenWidth>0? (screenWidth*animValue) : 0);
        if(animValue == 1 && animValue==-1){
            setAlpha(0);
        }else if(animValue<1 && animValue >-1){
            setAlpha(1);
        }

        if(mlistener != null){
            mlistener.onAnimExecute(this,animValue);
        }

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
    }


}
