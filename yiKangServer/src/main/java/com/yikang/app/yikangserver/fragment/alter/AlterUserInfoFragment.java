package com.yikang.app.yikangserver.fragment.alter;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.api.Api;
import com.yikang.app.yikangserver.api.callback.ResponseCallback;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.Department;
import com.yikang.app.yikangserver.bean.Expert;
import com.yikang.app.yikangserver.bean.FileResponse;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.dialog.DialogFactory;
import com.yikang.app.yikangserver.fragment.BaseFragment;
import com.yikang.app.yikangserver.fragment.SystemSelectPhotoFragment;
import com.yikang.app.yikangserver.reciever.UserInfoAlteredReceiver;
import com.yikang.app.yikangserver.ui.AddressSearchActivity;
import com.yikang.app.yikangserver.ui.AlterActivity;
import com.yikang.app.yikangserver.utils.BitmapUtils;
import com.yikang.app.yikangserver.utils.LOG;
import com.yikang.app.yikangserver.view.TextSpinner;
import com.yikang.app.yikangserver.view.adapter.PopListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 *修改个人信息fragment
 */
public class AlterUserInfoFragment extends BaseFragment
        implements View.OnClickListener, SystemSelectPhotoFragment.OnResultListener {
    private static String TAG = "AlterUserInfoFragment";

    /**传入的参数名*/
    private static final String EXTRA_USER = "user";


    /** request code */
    private static final int CODE_NAME = 1<<0;
    private static final int CODE_PROFESSION = 1<<1;
    private static final int CODE_ADDRESS= 1<<2;
    private static final int CODE_HOSPITAL = 1<<3;
    private static final int CODE_AVATAR = 1<<4;
    private static final int CODE_SPECIAL = 1<<5;
    private static final int CODE_OFFICE = 1<<6;
    private static final int CODE_WORK_TYPE = 1<<7;



    /** 不同的UI显示不同的item */
    private static int[] doctorUI = new int[]{R.id.ly_info_hospital, R.id.ly_info_office};
    private static int[] therapistUI = new int[]{R.id.ly_info_work_type, R.id.ly_info_spacial,R.id.ly_info_address};
    private static int[] nursingUI = new int[]{R.id.ly_info_hospital, R.id.ly_info_work_type, R.id.ly_info_spacial,R.id.ly_info_address};

    /** 默认头像 */
    private static SparseIntArray defaultAvatar = new SparseIntArray();
    static {
        defaultAvatar.put(MyData.DOCTOR,R.drawable.doctor_default_avatar);
        defaultAvatar.put(MyData.NURSING,R.drawable.nurse_default_avatar);
        defaultAvatar.put(MyData.THERAPIST,R.drawable.therapists_default_avatar);
    }




    /**展示选择头像的UI*/
    private SystemSelectPhotoFragment alterAvatarFragment;
    private String avatarLocalPath;
    private Bitmap avatar;



    private User user;

    private ArrayList<Department> departmentData = new ArrayList<>();
    private PopListAdapter departmentAdapter;

    private View rootView;
    private ImageView ivAvatar;
    private TextView tvAddress;
    private TextView tvHospital;
    private TextView tvName;
    private TextSpinner tspOffice;
    private TextView tvProfession;
    private TextSpinner tspWorkType;
    private TextView tvSpecial;
    private Button btComplete;

    private boolean isProfessionInCheck;


    private ResponseCallback<FileResponse> uploadAvatarHandler = new ResponseCallback<FileResponse>() {
        @Override
        public void onSuccess(FileResponse data) {
            hideWaitingUI();
            LOG.i(TAG, "[uploadAvatarHandler]上传头像成功");
            alterAvatarInfo(data.fileUrl);
            notifyUserInfoChanged();
        }

        @Override
        public void onFailure(String status, String message) {
            hideWaitingUI();
            LOG.i(TAG, "[uploadAvatarHandler]上传头像失败");
            AppContext.showToast("上传头像失败："+message);
        }
    };


    private ResponseCallback<Void>  alterWorkTypeHandler = new ResponseCallback<Void>() {
        @Override
        public void onSuccess(Void data) {
            hideWaitingUI();
            AppContext.showToast(R.string.alter_suceess);
            notifyUserInfoChanged();
            tvAddress.setText(user.mapPositionAddress+user.addressDetail);
        }

        @Override
        public void onFailure(String status, String message) {
            hideWaitingUI();
            AppContext.showToast("修改失败" + message);

        }
    };


    private ResponseCallback<Void> alterOfficeHandler = new ResponseCallback<Void>() {
        @Override
        public void onSuccess(Void data) {
            hideWaitingUI();
            AppContext.showToast(R.string.alter_suceess);
            notifyUserInfoChanged();
        }

        @Override
        public void onFailure(String status, String message) {
            hideWaitingUI();
            AppContext.showToast("修改失败" + message);
        }
    };



    /**
     * 修改上传头像
     * @param url
     */
    private void alterAvatarInfo(String url) {
        showWaitingUI();
        Api.alterAvatar(url, new ResponseCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                hideWaitingUI();
                LOG.i(TAG, "[alterAvatarInfo]修改头像成功");
            }

            @Override
            public void onFailure(String status, String message) {
                hideWaitingUI();
                LOG.i(TAG, "[alterAvatarInfo]修改头像失败");
                AppContext.showToast("修改头像失败：" + message);
            }
        });
    }


    public static AlterUserInfoFragment newInstance(User user) {
        AlterUserInfoFragment fragment = new AlterUserInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER, user);
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(EXTRA_USER);
            isProfessionInCheck = (user.professionCheckStatus == User.CHECK_STATUS_CHECKING);
            if(user.profession == MyData.DOCTOR){
                loadDepartment();

            }else if(user.profession == MyData.NURSING){


            }else if(user.profession == MyData.THERAPIST){

            }
        }
    }



    /**
     * 获取科室列表
     */
    private void loadDepartment(){
        Api.getDepartment(new ResponseCallback<List<Department>>() {
            @Override
            public void onSuccess(List<Department> data) {
                departmentData.clear();
                departmentData.addAll(data);
                List<String> strData = new ArrayList<>();

                for (Department department : data) {
                    strData.add(department.departmentName);
                }
                departmentAdapter = new PopListAdapter(getActivity(), strData);
                tspOffice.setAdapter(departmentAdapter);
            }

            @Override
            public void onFailure(String status, String message) {
                AppContext.showToast(message);
            }
        });
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alter_user_info, container, false);
        rootView.findViewById(R.id.ly_info_address).setOnClickListener(this);
        rootView.findViewById(R.id.ly_mine_basic_info).setOnClickListener(this);
        rootView.findViewById(R.id.ly_info_hospital).setOnClickListener(this);
        rootView.findViewById(R.id.ly_info_name).setOnClickListener(this);
        rootView.findViewById(R.id.ly_info_office).setOnClickListener(this);
        rootView.findViewById(R.id.ly_info_profession).setOnClickListener(this);
        rootView.findViewById(R.id.ly_info_work_type).setOnClickListener(this);
        rootView.findViewById(R.id.ly_info_spacial).setOnClickListener(this);

        ivAvatar = ((ImageView) rootView.findViewById(R.id.iv_alter_info_avatar));
        tvAddress = (TextView) rootView.findViewById(R.id.tv_alter_info_address);
        tvHospital = (TextView) rootView.findViewById(R.id.tv_alter_info_hospital);
        tvName = (TextView) rootView.findViewById(R.id.tv_alter_info_name);
        tspOffice = (TextSpinner) rootView.findViewById(R.id.tv_alter_info_office);
        tvProfession = (TextView) rootView.findViewById(R.id.tv_alter_info_profession);
        tvSpecial = (TextView) rootView.findViewById(R.id.tv_alter_info_special);
        tspWorkType = (TextSpinner) rootView.findViewById(R.id.tv_alter_info_work_type);


        tvProfession.setText(MyData.professionMap.get(user.profession));

        PopListAdapter proLeverAdapter = new PopListAdapter(getActivity(),MyData.getItems(MyData.profeLeversMap));
        proLeverAdapter.setCurrentSelected(-1);
        tspWorkType.setAdapter(proLeverAdapter);
        tspWorkType.setOnDropDownItemClickListener(new TextSpinner.OnItemSelectedListener() {
            @Override
            public void onItemClickListener(TextSpinner spinner, int position) {
                showWaitingUI();
                Api.alterWorkType(position, alterWorkTypeHandler);
            }
        });


        tspOffice.setOnDropDownItemClickListener(new TextSpinner.OnItemSelectedListener() {
            @Override
            public void onItemClickListener(TextSpinner spinner, int position) {
                showWaitingUI();
                Api.alterOffices(departmentData.get(position).departmentId, alterOfficeHandler);
            }
        });

        resetUIVisibility();
        showUserInfo(user);
        return rootView;
    }



    private void notifyUserInfoChanged(){
        getActivity().sendBroadcast(new Intent(UserInfoAlteredReceiver.ACTION_USER_INFO_ALTERED));
    }


    public void submit() {

    }




    /**
     * 显示用户信息
     * @param user
     */
    private void showUserInfo(User user){

        tvName.setText(user.name);

        if(!TextUtils.isEmpty(user.avatarImg)){
            ImageLoader.getInstance().displayImage(user.avatarImg,ivAvatar);
        }else {
            ivAvatar.setImageResource(defaultAvatar.get(user.profession));
        }

        if(user.profession >= 0){
            tvProfession.setText(MyData.professionMap.get(user.profession));
        }

        if(user.profession == MyData.DOCTOR){ //医生
            if(user.department!=null){
                tspOffice.setText(user.department.departmentName);//设置科室
            }
            tvHospital.setText(user.hospital);//设置医院

        }else{ //护士或者康复师
            tspWorkType.setCurrentSelection(user.jobType);//设置工作类型

            StringBuilder builder = new StringBuilder();
            for(Expert e :user.special){
                builder.append(e.name+" ");
            }
            tvSpecial.setText(builder.toString()); //设置擅长

            String address = user.mapPositionAddress + user.addressDetail;
            if(!TextUtils.isEmpty(address))
                tvAddress.setText(address);//设置地址

            if(user.profession == MyData.NURSING){
               tvHospital.setText(user.hospital);//设置医院
            }
        }
    }



    /**
     * 根据职业的不同设置不同item的可见性
     */
    private void resetUIVisibility(){
        LOG.i(TAG, "[resetUIVisibility]");
        if(user.profession == MyData.DOCTOR){
            hideItems(therapistUI);
            hideItems(nursingUI);

            showItems(doctorUI);
        }else if(user.profession == MyData.THERAPIST){
            hideItems(doctorUI);
            hideItems(nursingUI);

            showItems(therapistUI);
        }else if(user.profession == MyData.NURSING){
            hideItems(therapistUI);
            hideItems(doctorUI);

            showItems(nursingUI);
        }
    }




    private void showItems(int... ids){
        for (int id : ids) {
            rootView.findViewById(id).setVisibility(View.VISIBLE);
        }
    }

    private void hideItems(int ...ids){
        for (int id : ids) {
            rootView.findViewById(id).setVisibility(View.GONE);
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            return;
        }
        switch (requestCode){
            case CODE_ADDRESS:
                user.districtCode = data.getStringExtra(AddressSearchActivity.EXTRA_ADCODE);
                user.addressDetail = data.getStringExtra(AddressSearchActivity.EXTRA_ADDR_DETIAL);
                user.mapPositionAddress = data.getStringExtra(AddressSearchActivity.EXTRA_ADDR_TITLE);
                String district = data.getStringExtra(AddressSearchActivity.EXTRA_ADDR_DISTRICT);
                Api.alterAddr(user.mapPositionAddress,user.districtCode,user.addressDetail,alterWorkTypeHandler);
                break;
            case CODE_NAME:
                user.name = data.getStringExtra(BaseAlterFragment.RESULT_EXTRA_RESULT);
                tvName.setText(user.name);
                break;
            case CODE_PROFESSION:
                int profession = data.getIntExtra(BaseAlterFragment.RESULT_EXTRA_RESULT, 0);
                if(user.profession != profession){
                    isProfessionInCheck = true;
                    user.professionCheckStatus = User.CHECK_STATUS_CHECKING;
                    user.profession = profession;
                    tvProfession.setText(MyData.professionMap.get(profession));
                }
                break;
            case CODE_HOSPITAL:
                user.hospital = data.getStringExtra(BaseAlterFragment.RESULT_EXTRA_RESULT);
                tvHospital.setText(user.hospital);
                break;
            case CODE_SPECIAL:
                user.special = (ArrayList<Expert>) data.getSerializableExtra(BaseAlterFragment.RESULT_EXTRA_RESULT);
                StringBuilder builder = new StringBuilder();
                for(Expert e :user.special){
                    builder.append(e.name+" ");
                }
                tvSpecial.setText(builder.toString());
                break;
        }
    }




    @Override
    public void onClick(View v) {
        if(isProfessionInCheck){
            DialogFactory.getCommonAlertDialog(getActivity(),"职位信息正在审核，不能修改信息").show();
            return;
        }

        switch (v.getId()){
            case R.id.ly_info_address:
                alterAddress();
                break;
            case R.id.ly_mine_basic_info:
                alterAvatar();
                break;
            case R.id.ly_info_hospital:
                alterHospital();
                break;
            case R.id.ly_info_name:
                alterName(user.name);
                break;
            case R.id.ly_info_office:
                alterOffice();
                break;
            case R.id.ly_info_profession:
                alterProfession();
                break;
            case R.id.ly_info_spacial:
                alterExpert();
                break;
            case R.id.ly_info_work_type:
                alterWorkType();
                break;
            default:
                break;
        }

    }



    /**
     * 修改科室
     */
    private void alterOffice() {
        LOG.i(TAG, "[alterOffice]");
        tspOffice.performClick();
    }




    /**
     * 修改职业
     */
    private void alterProfession() {
        LOG.i(TAG,"[applyChangeProfession]");

        AlterActivity.SimpleAlterPage page = AlterActivity.SimpleAlterPage.alterProfession;

        Bundle fragmentBundle = new Bundle(); //传到fragment中的参数
        fragmentBundle.putBoolean(BaseAlterFragment.ARG_NEED_SUBMIT, true);
        fragmentBundle.putInt(BaseAlterFragment.ARG_OLD_VALUE, user.profession);


        Intent intent = new Intent(getActivity(),AlterActivity.class);
        intent.putExtra(AlterActivity.EXTRA_FRAGMENT_PAGE, page);
        intent.putExtra(AlterActivity.EXTRA_ARGS,fragmentBundle);

        startActivityForResult(intent, CODE_PROFESSION);
    }

    /**
     * 修改工作类型
     */
    private void alterWorkType() {
        LOG.i(TAG,"[alterWorkType]");
        tspWorkType.performClick();

    }


    /**
     * 修改地址
     */
    private void alterAddress(){
        LOG.i(TAG, "[alterAddress]");
        Intent intent = new Intent(getActivity(),AddressSearchActivity.class);
        startActivityForResult(intent,CODE_ADDRESS);

    }




    /**
     * 修改照片
     */
    private void alterAvatar(){
        showSelectPhotoUI();
    }



    /**
     * 修改名称
     * @param oldValue 之前的值
     */
    private void alterName(String oldValue){

        AlterActivity.SimpleAlterPage page = AlterActivity.SimpleAlterPage.alterName;

        Bundle fragmentBundle = new Bundle(); //传到fragment中的参数
        fragmentBundle.putBoolean(BaseAlterFragment.ARG_NEED_SUBMIT, true);
        fragmentBundle.putString(BaseAlterFragment.ARG_OLD_VALUE, oldValue);


        Intent intent = new Intent(getActivity(),AlterActivity.class);
        intent.putExtra(AlterActivity.EXTRA_FRAGMENT_PAGE, page);
        intent.putExtra(AlterActivity.EXTRA_ARGS,fragmentBundle);

        startActivityForResult(intent, CODE_NAME);
    }




    /**
     * 修改医院
     */
    private void alterHospital(){

        AlterActivity.SimpleAlterPage page = AlterActivity.SimpleAlterPage.alterHospital;

        Bundle fragmentBundle = new Bundle(); //传到fragment中的参数
        fragmentBundle.putBoolean(BaseAlterFragment.ARG_NEED_SUBMIT, true);
        fragmentBundle.putString(BaseAlterFragment.ARG_OLD_VALUE, user.hospital);


        Intent intent = new Intent(getActivity(),AlterActivity.class);
        intent.putExtra(AlterActivity.EXTRA_FRAGMENT_PAGE, page);
        intent.putExtra(AlterActivity.EXTRA_ARGS,fragmentBundle);

        startActivityForResult(intent, CODE_HOSPITAL);
    }




    /**
     * 修改擅长
     */
    private void alterExpert(){

        AlterActivity.SimpleAlterPage page = AlterActivity.SimpleAlterPage.alterExpert;

        Bundle fragmentBundle = new Bundle(); //传到fragment中的参数
        fragmentBundle.putBoolean(BaseAlterFragment.ARG_NEED_SUBMIT, true);
        fragmentBundle.putSerializable(BaseAlterFragment.ARG_OLD_VALUE, user.special);
        fragmentBundle.putSerializable(AlterExpertFragment.ARG_PROFESSION,user.profession);


        Intent intent = new Intent(getActivity(),AlterActivity.class);
        intent.putExtra(AlterActivity.EXTRA_FRAGMENT_PAGE, page);
        intent.putExtra(AlterActivity.EXTRA_ARGS,fragmentBundle);

        startActivityForResult(intent, CODE_SPECIAL);
    }





    /**
     * 显示选择图片的fragment
     */
    private void showSelectPhotoUI() {
        if (alterAvatarFragment == null) {
            alterAvatarFragment = new SystemSelectPhotoFragment();
            alterAvatarFragment.setOnResultListener(this);
        }

        if (alterAvatarFragment == getFragmentManager().findFragmentById(
                R.id.fl_alter_info_photo_container)) {
            return;
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_alter_info_photo_container,
                alterAvatarFragment).commit();
    }




    /**
     * 选择照片完成
     */
    @Override
    public void onComplete(String path) {
        dismissSelectPhotoUI();
        avatarLocalPath = path;
        LOG.i(TAG, "[onComplete]" + path);
        if (avatar != null) { // 将之前的roudPic回收
            avatar.recycle();
        }
        avatar = BitmapUtils.getBitmap(getActivity(), path, ivAvatar.getWidth(), ivAvatar.getHeight());
        if (avatar != null) {
            ivAvatar.setImageBitmap(avatar);
            LOG.i(TAG, "[submit]开始长传头像");
            showWaitingUI();
            Api.uploadFile(new File(avatarLocalPath), uploadAvatarHandler);
        } else {
            LOG.e(TAG, "系统出错，没有获取到图片");
        }


    }





    /**
     * 选择照片取消
     */
    @Override
    public void onCancel() {
        dismissSelectPhotoUI();
    }




    /**
     * 隐藏fragment
     */
    private void dismissSelectPhotoUI() {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.remove(alterAvatarFragment).commit();
    }

}


