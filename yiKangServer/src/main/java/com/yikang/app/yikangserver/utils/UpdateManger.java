package com.yikang.app.yikangserver.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.dialog.DialogFactory;
import com.yikang.app.yikangserver.interf.UINetwork;
import com.yikang.app.yikangserver.service.UpdateService;

import java.io.Serializable;

/**
 * 更新管理
 */
public class UpdateManger {
    private static final String TAG ="UpdateManger";

    private AndroidUpdate mUpdate;

    private boolean isShow;//是否显示更新界面

    private Context mContext;

    private UINetwork lisenter;

    /**
     * 请求更新的回调
     */
    private ResponseCallback<AndroidUpdate> checkUpateHandler = new ResponseCallback<AndroidUpdate>() {
        @Override
        public void onSuccess(AndroidUpdate data) {

            if (isShow) dismissWaitingDialog();
//            mUpdate = new AndroidUpdate();
//            mUpdate.versionCode =5;
//            mUpdate.downloadUrl ="http://src.pre.im/d/app/03948e7b2b9e6ba6c03b1d04acc3b0a0.apk?attname=护理家-医护端_1.0.2_4.apk";
//            mUpdate.updateLog ="1.临近上线版本。\n" +
//                    "2.修改了界面整体风格。\n" +
//                    "3.增加了推荐患者列表二维码等业务。";

            if (hasNew()) {
                showUpdateDialog(needForceUpdate());
                LOG.i(TAG,"显示更新的信息");
            } else {
                if (isShow) {
                    showLeastVersionDialog();
                }
            }
        }

        @Override
        public void onFailure(String status, String message) {
            if (isShow) dismissWaitingDialog();
            AppContext.showToast(message);
        }
    };


    public UpdateManger(Context context) {
        this(context, true);
    }


    public UpdateManger(Context context, boolean isShow) {
        this.isShow = isShow;
        this.mContext = context;
        if (mContext instanceof UINetwork) {
            this.lisenter = (UINetwork) context;
        }
    }

    /**
     * 检查更新
     */
    public void checkUpate() {
        if (!isShow) {
            showWaitingDialog(mContext.getString(R.string.update_get_version_info));
        }
        Api.checkUpdate(checkUpateHandler);
    }


    /**
     * 判断是否有更新的操作
     * @return true 有更新； false没有更新
     */
    private boolean hasNew() {
        if (mUpdate == null)
            return false;
        int curVersion = AppContext.getAppContext().getVersionCode();
        return mUpdate.versionCode > curVersion;
    }

    /**
     *判断是否需要强制更新
     * @return
     */
    private boolean needForceUpdate(){
        return  true;
    }


    private void showWaitingDialog(String message) {
        if (lisenter != null) {
            lisenter.showWaitingUI(message);
        }
    }


    private void dismissWaitingDialog() {
        if (lisenter != null) {
            lisenter.hideWaitingUI();
        }
    }


    /**
     * 显示更新的信息
     * @param isForceUpdate 是否需要强制更新
     */
    private void showUpdateDialog(final boolean isForceUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.update_new_version_dialog_title).setCancelable(false);
        if(isForceUpdate){
            builder.setMessage(R.string.update_force_update_message);
        }else if (!TextUtils.isEmpty(mUpdate.updateLog)) {
            builder.setMessage(mUpdate.updateLog);
        }

        builder.setNegativeButton(R.string.update_later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isForceUpdate){
                    dialog.cancel();
                    if(mContext instanceof Activity){
                        ((Activity) mContext).finish();
                    }
                }
            }
        });
        builder.setPositiveButton(R.string.update_right_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //具体的安装
                Intent intent = new Intent(mContext, UpdateService.class);
                intent.putExtra(UpdateService.KEY_UPDATE_INFO, mUpdate);
                mContext.startService(intent);
            }
        });
        builder.create().show();

    }




    /**
     * 显示已经是最新版本
     */
    private void showLeastVersionDialog() {
        Dialog dialog = DialogFactory.getCommonAlertDialog(mContext,
                mContext.getString(R.string.update_least_hint));
        dialog.show();
    }


    /**
     * android apk更新累
     */
    public static class AndroidUpdate implements Serializable{
        public int versionCode;  //代码版本
        public String versionName; //版本名字
        public String updateLog; //更新日志
        public String downloadUrl; //下载URL
    }
}
