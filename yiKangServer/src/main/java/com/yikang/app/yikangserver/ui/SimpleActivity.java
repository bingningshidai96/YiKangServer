package com.yikang.app.yikangserver.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.fragment.ResetPasswFragment;

/**
 * Created by liu on 15/12/20.
 */
public class SimpleActivity extends  BaseActivity{
    public static final String EXRRA_PAGE ="PAGE";
    private static final  String TAG = "SimpleActivity" ;
    private SimplePage page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page =  ((SimplePage) getIntent().getSerializableExtra(EXRRA_PAGE));
        initContent();
        initTitleBar(getString(page.getTitleId()));
    }

    @Override
    protected void findViews() {}

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_simple);
    }

    @Override
    protected void getData() {}

    @Override
    protected void initViewContent() {


        try {

            Fragment fragment = page.getClz().newInstance();
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            ft.replace(R.id.fy_simple_container,fragment).commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


    public enum SimplePage{
        findPassw(1,R.string.reset_passw_title, ResetPasswFragment.class,null);


        //freeTime(2,"空闲时间",)


        private final int id;
        private final int titleId;
        private final Class<? extends  Fragment> clz;
        private final Bundle args;
        SimplePage(int id,int titleId,Class<? extends Fragment> clz,Bundle args){
            this.id = id;
            this.clz= clz;
            this.titleId = titleId;
            this.args = args;
        }

        public int getId() {
            return id;
        }


        public Bundle getArgs() {
            return args;
        }


        public Class<? extends Fragment> getClz() {
            return clz;
        }


        public int getTitleId() {
            return titleId;
        }

    }
}
