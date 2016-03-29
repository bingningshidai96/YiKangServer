package com.yikang.app.yikangserver.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.dialog.QrCodeDialog;
import com.yikang.app.yikangserver.ui.InviteCustomerListActivity;
import com.yikang.app.yikangserver.ui.MineInfoActivity;

/**
 * 主页面的我的fragment
 */
public class MineFragment extends BaseFragment implements OnClickListener {
    protected static final String TAG = "MineFragment";
    private static SparseIntArray defaultAvatar = new SparseIntArray();

    static {
        defaultAvatar.put(MyData.DOCTOR,R.drawable.doctor_default_avatar);
        defaultAvatar.put(MyData.NURSING,R.drawable.nurse_default_avatar);
        defaultAvatar.put(MyData.THERAPIST,R.drawable.therapists_default_avatar);
    }
    private TextView tvName, tvProfession, tvInviteCode,tvCustomerNum;
    private ImageView ivAvatar;

    private User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = AppContext.getAppContext().getUser();

        if(user.infoStatus != User.INFO_STATUS_COMPLETE){
            showEditPage();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View view) {
        ivAvatar = (ImageView) view.findViewById(R.id.iv_mine_avatar);
        tvName = (TextView) view.findViewById(R.id.tv_mine_name);
        tvProfession = (TextView) view.findViewById(R.id.tv_mine_profession);
        tvInviteCode = (TextView) view.findViewById(R.id.tv_mine_invite_code);
        tvCustomerNum = (TextView) view.findViewById(R.id.tv_mine_customer_number);

        LinearLayout lyBasicInfo = ((LinearLayout) view.findViewById(R.id.ly_mine_basic_info));
        LinearLayout lyQrCode = (LinearLayout) view.findViewById(R.id.ly_mine_qr_code);
        View lyCustomNumber = view.findViewById(R.id.ly_mine_customer_number);

        lyQrCode.setOnClickListener(this);
        lyBasicInfo.setOnClickListener(this);
        lyCustomNumber.setOnClickListener(this);


        fillToViews();
    }




    /**
     * 将map中的信息填写到Views中
     */
    private void fillToViews() {
        tvName.setText(user.name);//名字
        tvInviteCode.setText(user.inviteCode); //邀请码

        if (user.profession >= 0) { //职业
            String profession = MyData.professionMap.valueAt(user.profession);
            tvProfession.setText(profession);
        }

        if (!TextUtils.isEmpty(user.avatarImg)) { // 显示头像
            ImageLoader.getInstance().displayImage(user.avatarImg, ivAvatar);
        }else{
            ivAvatar.setImageResource(defaultAvatar.get(user.profession));
        }

        //患者人数
        final String customNumStr = String.format(
                getString(R.string.mine_format_customer_nums), user.paintsNums);
        tvCustomerNum.setText(customNumStr);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_mine_basic_info:
                showEditPage();
                break;
            case R.id.ly_mine_qr_code:
                showQrCodeDialog();
                break;
            case R.id.ly_mine_customer_number:
                toCustomerListPage();
                break;
            default:
                break;
        }
    }

    /**
     * 跳转到患者列表页面
     */
    private void toCustomerListPage() {
        Intent intent = new Intent(getActivity(),
                InviteCustomerListActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out);
    }




    /**
     * 显示大的二维码dialog
     */
    private void showQrCodeDialog() {
        Dialog qrCodeDialog = new QrCodeDialog(getActivity(), user.invitationUrl);
        qrCodeDialog.show();
    }



    /**
     * 显示编辑page
     */
    private void showEditPage() {
       // user.professionCheckStatus = User.CHECK_STATUS_UNCHECKED;
        //user.infoStatus = User.INFO_STATUS_INCOMPLETE;

        Intent intent = new Intent(getActivity(), MineInfoActivity.class);
        intent.putExtra(MineInfoActivity.EXTRA_USER,user);

        startActivity(intent);
    }


}
