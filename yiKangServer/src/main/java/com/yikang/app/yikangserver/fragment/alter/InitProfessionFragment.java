package com.yikang.app.yikangserver.fragment.alter;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.data.MyData;
import com.yikang.app.yikangserver.fragment.BaseFragment;

/**
 * 选择职位
 */
public class InitProfessionFragment extends BaseFragment
        implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private RadioButton rbDoctor, rbTherapists, rbNurse;
    private Button btDone;
    private RadioGroup rgProfessions;

    private int profession; //选中的职业

    private OnDone callback;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnDone){
            this.callback = (OnDone) activity;
        }else{
            throw new IllegalArgumentException("wrong argument,context should be instance of InitProfessionFragment.OnDone");
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_init_profession, container, false);

        rbDoctor = (RadioButton) view.findViewById(R.id.rb_profession_doctor);
        rbTherapists = (RadioButton) view.findViewById(R.id.rb_profession_therapists);
        rbNurse = (RadioButton) view.findViewById(R.id.rb_profession_nurse);
        rgProfessions = ((RadioGroup) view.findViewById(R.id.rg_profession_choices));
        btDone = ((Button) view.findViewById(R.id.bt_profession_done));

        rgProfessions.check(R.id.rb_profession_doctor);//默认选中
        btDone.setOnClickListener(this);

        rgProfessions.setOnCheckedChangeListener(this);

        return view;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_profession_done:
                callback.afterChooseProfession(profession);
                break;
        }
    }



    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_profession_doctor:
                profession = MyData.DOCTOR;
                break;
            case R.id.rb_profession_nurse:
                profession = MyData.NURSING;
                break;
            case R.id.rb_profession_therapists:
                profession = MyData.THERAPIST;
                break;
            default:
                break;
        }
    }



    public interface OnDone{
        /**
         * 回调选中的职业
         * @param profession 详细请看{@link  MyData};
         */
        void afterChooseProfession(int profession);
    }

}
