package com.yikang.app.yikangserver.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.application.AppConfig;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.bean.User;
import com.yikang.app.yikangserver.fragment.alter.AlterUserInfoFragment;
import com.yikang.app.yikangserver.fragment.alter.InitProfessionFragment;
import com.yikang.app.yikangserver.fragment.alter.InitUserInfoFragment;
import com.yikang.app.yikangserver.utils.LOG;

/**
 * 个人信息页面
 */
public class MineInfoActivity extends BaseActivity implements InitProfessionFragment.OnDone{
    private static final String TAG = "MineInfoActivity";
    public static String EXTRA_USER = "user";

    private User user;


    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra(EXTRA_USER);

        if(user == null){
            throw new IllegalArgumentException("必须要传入一个User对象");
        }
        initContent();
        initTitleBar(null);

    }


    /**
     * 设置标题
     * @param title
     */
    private void setPageTitle(String title){
        if(tvTitle!=null){
            tvTitle.setText(title);
        }
    }



    @Override
    protected void findViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title_text);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_mine_info);
    }

    @Override
    protected void getData() {

    }



    @Override
    protected void initViewContent() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(user.professionCheckStatus == User.CHECK_STATUS_UNCHECKED &&
                user.infoStatus == User.INFO_STATUS_INCOMPLETE){ //未审核，且信息不完整
            ft.add(R.id.fl_mine_info_container, new InitProfessionFragment());
            setPageTitle(getString(R.string.init_profession_title));
            setBackVisible(false);
        }else if(user.professionCheckStatus == User.CHECK_STATUS_PASSED &&
                user.infoStatus == User.INFO_STATUS_INCOMPLETE){ //审核通过，且信息不完整
            ft.add(R.id.fl_mine_info_container,InitUserInfoFragment.newInstance(user));
            setPageTitle(getString(R.string.init_user_info_title));
            setBackVisible(false);
        }else if(user.infoStatus == User.INFO_STATUS_COMPLETE){ //未审核，信息完整
            setPageTitle(getString(R.string.alter_user_info_title));
            ft.add(R.id.fl_mine_info_container, AlterUserInfoFragment.newInstance(user));
        }

        ft.commit();
    }




    @Override
    public void afterChooseProfession(int profession) {
        user.profession = profession;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.fl_mine_info_container, InitUserInfoFragment.newInstance(user));
        ft.addToBackStack(null).commit();
        setPageTitle(getString(R.string.init_user_info_title));
        setBackVisible(true);
    }


    private void setBackVisible(boolean visible){
        View back = findViewById(R.id.ibtn_title_back);
        back.setVisibility(visible ? View.VISIBLE : View.GONE);
    }



    @Override
    protected void initTitleBar(String title) {
        tvTitle = (TextView) findViewById(R.id.tv_title_text);
        findViewById(R.id.ibtn_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });

    }


    /**
     * 接收到back事件
     */
    private void onBack(){
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fl_mine_info_container);
        if (fragment instanceof InitUserInfoFragment) {
            if( getFragmentManager().getBackStackEntryCount() >=0){
                LOG.i(TAG,"[initTitleBar]"+getFragmentManager().getBackStackEntryCount());
                getFragmentManager().popBackStack();
                setBackVisible(false);
                setTitle(R.string.init_profession_title);
            }else{
                showExitDialog();
            }
        } else if (fragment instanceof AlterUserInfoFragment) {
            finish();
        } else {
            showExitDialog();
        }
    }



    /**
     显示退出对话框
     */
    private void showExitDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_prompt)
                .setMessage("需要离开吗？")
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
                        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
                        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivity(mHomeIntent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否退出应用
           onBack();
        }
        return true;
    }




}
