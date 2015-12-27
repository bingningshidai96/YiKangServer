package com.yikang.app.yikangserver.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by liu on 15/12/24.
 */
public abstract class AnimationLayout extends FrameLayout{

    protected OnAnimExecuteListener mlistener;
    protected float animValue;

    public AnimationLayout(Context context) {
        super(context);
    }

    public AnimationLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAnimValue(float value){
        this.animValue = value;
        applyAnimValue();
    }

    protected abstract void applyAnimValue();

    public interface OnAnimExecuteListener{
        void onAnimExecute(AnimationLayout view,float value);
    }






}
