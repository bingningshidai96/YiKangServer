package com.yikang.app.yikangserver.fragment.alter;

import android.content.Intent;
import android.os.Bundle;

import com.yikang.app.yikangserver.fragment.BaseFragment;
import com.yikang.app.yikangserver.interf.CanSubmit;
import com.yikang.app.yikangserver.reciever.UserInfoAlteredReceiver;

/**
 * 点击某一项的修改之后跳到的页面
 */
public abstract class BaseAlterFragment extends BaseFragment implements CanSubmit{
    public static final String ARG_NEED_SUBMIT = "need_submit";
    public static final String ARG_OLD_VALUE = "oldValue";

    protected static final String RESULT_EXTRA_RESULT = "result";
    protected  boolean mCanSubmit;

    protected void notifyUserInfoChanged(){
        getActivity().sendBroadcast(new Intent(UserInfoAlteredReceiver.ACTION_USER_INFO_ALTERED));
    }

}
