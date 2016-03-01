package com.yikang.app.yikangserver.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.utils.QRImageCreateUtils;


/**
 * 用于展示个人的二维码
 * Created by liu on 15/12/21.
 */
public class QrCodeDialog extends Dialog{
    private Bitmap bitmap;
    private OnCancelListener listener;
    private static final int QR_SIZE = 400;


    public QrCodeDialog(Context context,Bitmap bitmap){
        super(context, R.style.custom_dialog);
        this.bitmap = bitmap;
        init();

    }


    public QrCodeDialog(Context context,String url){
        super(context, R.style.custom_dialog);
        QRImageCreateUtils utils = new QRImageCreateUtils(QR_SIZE,QR_SIZE);
        this.bitmap = utils.createQRImage(url);
        init();

    }

    /**
     * 初始化dialog的属性
     */
    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_qrcode, null, false);
        this.setContentView(view);

        ImageView imgView = (ImageView) view.findViewById(R.id.iv_qr_dialog_qr_code);
        imgView.setImageBitmap(bitmap);
        this.setCancelable(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (listener != null) {
                    listener.onCancel(dialog);

                }
                if(bitmap!=null && !bitmap.isRecycled()){
                   bitmap.recycle();
                }
            }
        });
    }


    @Override
    public void setOnCancelListener(OnCancelListener listener) {
        this.listener = listener;
    }


}
