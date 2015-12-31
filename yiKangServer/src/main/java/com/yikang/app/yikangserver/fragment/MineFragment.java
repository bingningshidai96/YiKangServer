package com.yikang.app.yikangserver.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.dailog.QrCodeDialog;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.ui.EditMineInfoActivity;
import com.yikang.app.yikangserver.ui.FreeDayCalendarActivty;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.utils.QRImageCreateUtils;

/**
 * 主页面的我的fragment
 */
public class MineFragment extends BaseFragment implements OnClickListener {
    protected static final String TAG = "MineFragment";
    private TextView tvName, tvProfession, tvHospital, tvSpecial, tvDepartment,
            tvInviteCode;
    private ImageView ivAvatar, ivQRCode;
    private ImageButton ibtEdit;
    private LinearLayout lyFreeTime, lyDistinct; // 兼职人员时间
    private TextView tvDistinct, tvCustomerNum;
    private View rootView;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View view) {
        ivAvatar = (ImageView) view.findViewById(R.id.iv_mine_avatar);
        ivQRCode = (ImageView) view.findViewById(R.id.iv_mine_qr_code);

        ibtEdit = (ImageButton) view.findViewById(R.id.ibtn_mine_edit);
        tvName = (TextView) view.findViewById(R.id.tv_mine_name);
        tvProfession = (TextView) view.findViewById(R.id.tv_mine_profession);
        tvHospital = (TextView) view.findViewById(R.id.tv_mine_hospital);
        tvSpecial = (TextView) view.findViewById(R.id.tv_mine_special);
        tvDepartment = (TextView) view.findViewById(R.id.tv_mine_department);
        tvInviteCode = (TextView) view.findViewById(R.id.tv_mine_invite_code);

        tvCustomerNum = (TextView) view.findViewById(R.id.tv_mine_customer_number);
        tvDistinct = (TextView) view.findViewById(R.id.tv_mine_distinct);

        lyDistinct = (LinearLayout) view.findViewById(R.id.ly_mine_distinct_area);
        lyFreeTime = (LinearLayout) view.findViewById(R.id.ly_mine_free_time);

        ibtEdit.setOnClickListener(this);
        lyFreeTime.setOnClickListener(this);
        ivQRCode.setOnClickListener(this);
        fillToViews();
    }

    /**
     * 将map中的信息填写到Views中
     */
    private void fillToViews() {
        user = AppContext.getAppContext().getUser();
        LOG.d(TAG, "[fillToViews]:" + user);

        tvName.setText(user.name);//名字
        tvInviteCode.setText(user.inviteCode); //邀请码

        if (user.profession >= 0) { //职业
            String profession = MyData.professionMap.valueAt(user.profession);
            tvProfession.setText(profession);
        }

        if (!TextUtils.isEmpty(user.avatarImg)) { // 显示头像
            ImageLoader.getInstance().displayImage(user.avatarImg, ivAvatar);
        }

        if (!TextUtils.isEmpty(user.invitationUrl)) { // 显示二维码
            QRImageCreateUtils utils = new QRImageCreateUtils(200, 200);
            ivQRCode.setImageBitmap(utils.createQRImage(user.invitationUrl));
        }

        //患者人数
        final String customNumStr = String.format(
                getString(R.string.mine_format_customer_nums), user.paintsNums);
        tvCustomerNum.setText(customNumStr);

        //职业相关的
        String addressDetail = user.mapPositionAddress + user.addressDetail;
        if (user.profession == MyData.DOCTOR) {
            rootView.findViewById(R.id.mine_container_hospital).setVisibility(
                    View.VISIBLE);
            rootView.findViewById(R.id.mine_container_department)
                    .setVisibility(View.VISIBLE);
            tvDepartment.setText(user.deparment);
            tvHospital.setText(user.hosital);
        } else if (user.profession == MyData.NURSING) {
            rootView.findViewById(R.id.mine_container_special).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.mine_container_hospital).setVisibility(View.VISIBLE);
            tvSpecial.setText(user.special);
            tvHospital.setText(user.hosital);

            lyDistinct.setVisibility(View.VISIBLE);
            if (user.jobType == MyData.PAER_TIME) {
                lyFreeTime.setVisibility(View.VISIBLE);
            }
            tvDistinct.setText(addressDetail);
        } else {
            rootView.findViewById(R.id.mine_container_special).setVisibility(View.VISIBLE);
            tvSpecial.setText(user.special);

            lyDistinct.setVisibility(View.VISIBLE);
            if (user.jobType == MyData.PAER_TIME) {
                lyFreeTime.setVisibility(View.VISIBLE);
            }
            tvDistinct.setText(addressDetail);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_mine_free_time:
                Intent intent = new Intent(getActivity(), FreeDayCalendarActivty.class);
                startActivity(intent);
                break;
            case R.id.ibtn_mine_edit:
                showEditPage();
                break;
            case R.id.iv_mine_qr_code:
                showQrCodeDialog();
                break;

            default:
                break;
        }
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
        Intent intent = new Intent(getActivity(), EditMineInfoActivity.class);
        startActivity(intent);
    }
}
