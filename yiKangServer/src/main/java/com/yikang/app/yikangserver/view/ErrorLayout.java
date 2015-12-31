package com.yikang.app.yikangserver.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yikang.app.yikangserver.R;

/**
 * Created by liu on 15/12/31.
 */
public class ErrorLayout extends FrameLayout implements View.OnClickListener{
    private Context context;
    private ErrorType errorType;
    private TextView tvExtra;

    private OnClickListener listener;

    private ImageView ivError;

    private TextView tvTips;

    private boolean clickEnable = false;

    public enum ErrorType{
        net_fail,no_data;
    }
    public ErrorLayout(Context context) {
        super(context, null);
    }

    public ErrorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ErrorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @Override
    public void onClick(View v) {
        if(listener!=null && clickEnable){
            listener.onClick(v);
        }
    }

    public void setOnLayoutClickListener(OnClickListener listener){
        this.listener = listener;
    }


    public void setTips(int res){
        tvTips.setText(res);
    }

    public void setTips(String msg){
        tvTips.setText(msg);
    }

    public void setImg(int resId){
        ivError.setImageResource(resId);
    }

    public void setImg(Bitmap bitmap){
        ivError.setImageBitmap(bitmap);
    }

    public void setExtra(int strId){
        tvExtra.setText(strId);
    }

    public void setExtra(String msg){
        tvExtra.setText(msg);
    }

    /**
     * 初始化
     */
    private void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.error_layout, this, true);

        tvTips = (TextView) view.findViewById(R.id.tv_error_tips);
        ivError = (ImageView) view.findViewById(R.id.iv_error_img);
        tvExtra = ((TextView) view.findViewById(R.id.tv_data_fail_extra));

        view.setOnClickListener(this);

    }

    public void setErrorType(ErrorType errorType){
        this.errorType = errorType;
        switch (errorType){
            case net_fail:
                ivError.setImageResource(R.drawable.img_net_fail);
                tvTips.setText(R.string.default_network_data_fail_describe);
                break;
            case no_data:
                ivError.setImageResource(R.drawable.img_no_data);
                tvTips.setText(R.string.default_content_data_fail_describe);
                break;
        }
    }



}
