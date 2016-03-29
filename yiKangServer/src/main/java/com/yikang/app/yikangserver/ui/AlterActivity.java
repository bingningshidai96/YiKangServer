package com.yikang.app.yikangserver.ui;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.fragment.alter.AlterExpertFragment;
import com.yikang.app.yikangserver.fragment.alter.AlterHospitalFragment;
import com.yikang.app.yikangserver.fragment.alter.AlterNameFragment;
import com.yikang.app.yikangserver.fragment.alter.AlterProfessionFragment;
import com.yikang.app.yikangserver.interf.CanSubmit;


/**
 * 修改activity
 */
public class AlterActivity extends BaseActivity implements View.OnClickListener{
    public static final String EXTRA_FRAGMENT_PAGE = "simpleAlterPage";
    public static final String EXTRA_ARGS = "args";
    public SimpleAlterPage page;
    private Bundle fragmentArgs;
    private Fragment fragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = (SimpleAlterPage) getIntent().getSerializableExtra(EXTRA_FRAGMENT_PAGE);
        if(page == null){
            throw new IllegalStateException("必须要传进来一个SimpleAlterPage");
        }
        fragmentArgs = getIntent().getBundleExtra(EXTRA_ARGS);
        initContent();
        initTitleBar(getString(page.getTitleId()));
    }




    @Override
    protected void initTitleBar(String title) {
        super.initTitleBar(title);
        TextView tvRight = (TextView) findViewById(R.id.tv_title_right_text);
        tvRight.setText(getString(R.string.simple_alter_save));
        tvRight.setOnClickListener(this);
    }




    @Override
    protected void findViews() {}




    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_simple_alter);
    }




    @Override
    protected void getData() {}




    @Override
    protected void initViewContent() {
        try {
            fragment = page.getClz().newInstance();
            fragment.setArguments(fragmentArgs);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.fl_simple_alter_container, fragment).commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title_right_text:
                ((CanSubmit) fragment).submit();
                break;
        }
    }


    /**
     * 简单的修改界面
     *
     */
    public enum SimpleAlterPage{
        alterName(0, R.string.title_alter_info_name,AlterNameFragment.class),
        alterHospital(1, R.string.title_alter_info_hospital,AlterHospitalFragment.class),
        alterExpert(2, R.string.title_alter_info_experts,AlterExpertFragment.class),
        alterProfession(3,R.string.title_alter_info_profession,AlterProfessionFragment.class);


        private final int id;
        private final int titleId;
        private final Class<? extends Fragment> clz;
        SimpleAlterPage(int id,int titleId,Class<? extends Fragment> clz){
            this.id = id;
            this.clz= clz;
            this.titleId = titleId;
        }

        public int getId() {
            return id;
        }



        public Class<? extends Fragment> getClz() {
            return clz;
        }


        public int getTitleId() {
            return titleId;
        }

    }
}
