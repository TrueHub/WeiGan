package com.youyi.weigan.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.youyi.weigan.R;
import com.youyi.weigan.beans.PulseBean;
import com.youyi.weigan.utils.EventUtil;
import com.youyi.weigan.view.LineView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ContentFragment extends Fragment {

    private Context context;
    private TextView tv_heartRate;
    private LineView lineView_heartRate;
    private CardView card_heartRate;
    private ImageView iv_instructLevel;

    public ContentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        EventUtil.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventUtil.unregister(context);
    }

    private void initView(View view) {
        tv_heartRate = (TextView) view.findViewById(R.id.tv_heartRate);
        lineView_heartRate = (LineView) view.findViewById(R.id.lineView_heartRate);
        iv_instructLevel = (ImageView) view.findViewById(R.id.iv_instructLevel);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getGATTCallback(PulseBean pulseBean) {//实时心率
        tv_heartRate.setText(String.valueOf(pulseBean.getPulse()));
        lineView_heartRate.setCurrentValue(pulseBean.getPulse());
        switch (pulseBean.getTrustLevel()) {
            case 0:
                iv_instructLevel.setBackground(getResources().getDrawable(R.drawable.ic_instruct_0_24dp));
                break;
            case 1:
                iv_instructLevel.setBackground(getResources().getDrawable(R.drawable.ic_instruct_1_24dp));
                break;
            case 2:
                iv_instructLevel.setBackground(getResources().getDrawable(R.drawable.ic_instruct_2_24dp));
                break;
            case 3:
                iv_instructLevel.setBackground(getResources().getDrawable(R.drawable.ic_instruct_3_24dp));
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getInstruction(String str){
        switch (str) {
            case "PULSE_UP_OFF":
                Log.d("MSL", "getInstruction: " + str);
                iv_instructLevel.setBackground(getResources().getDrawable(R.drawable.ic_instruct_0_24dp));
                tv_heartRate.setText("--");
                lineView_heartRate.setCurrentValue(0);
                card_heartRate.setVisibility(View.GONE);
                break;
            case "PULSE_UP_ON":
                card_heartRate.setVisibility(View.VISIBLE);
                break;
        }
    }

}
