package com.youyi.weigan.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.youyi.weigan.beans.Pressure;
import com.youyi.weigan.beans.PulseBean;
import com.youyi.weigan.eventbean.Comm2Frags;
import com.youyi.weigan.utils.EventUtil;
import com.youyi.weigan.view.ImgWheelView;
import com.youyi.weigan.view.LineView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ContentFragment extends Fragment {

    private TextView tv_heartRate;
    private LineView lineView_heartRate;
    private CardView card_heartRate;
    private ImageView iv_instructLevel;
    private ImgWheelView imgWheelView;
    private TextView tv_status;
    private CardView card_status;

    public ContentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        EventUtil.register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventUtil.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView(View view) {
        tv_heartRate = (TextView) view.findViewById(R.id.tv_heartRate);
        lineView_heartRate = (LineView) view.findViewById(R.id.lineView_heartRate);
        iv_instructLevel = (ImageView) view.findViewById(R.id.iv_instructLevel);
        imgWheelView = (ImgWheelView) view.findViewById(R.id.imgWheelView);
        tv_status = (TextView) view.findViewById(R.id.tv_status);
        card_status = (CardView) view.findViewById(R.id.card_status);
        card_heartRate = (CardView) view.findViewById(R.id.card_heartRate);

        imgWheelView.setChecked(0);
        final Bitmap[] imgs = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_walk),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_run),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_bike),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_sit),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_sleep)
        };
        Bitmap centerBmp = BitmapFactory.decodeResource(getResources(),R.drawable.ic_state);
        imgWheelView.setImgs(imgs);
        imgWheelView.setCenterImg(centerBmp);
    }

    /**___________________________________________↓↓↓↓__EventBus__↓↓↓↓_______________________________________*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getGATTCallback(PulseBean pulseBean) {//实时心率
        card_heartRate.setVisibility(View.VISIBLE);
        tv_heartRate.setText(String.valueOf(pulseBean.getPulse()));
        lineView_heartRate.setCurrentValue(pulseBean.getPulse());
        switch (pulseBean.getTrustLevel()) {
            case 0:
                iv_instructLevel.setBackground(getResources().getDrawable(R.drawable.ic_trust_0));
                break;
            case 1:
                iv_instructLevel.setBackground(getResources().getDrawable(R.drawable.ic_trust_1));
                break;
            case 2:
                iv_instructLevel.setBackground(getResources().getDrawable(R.drawable.ic_trust_2));
                break;
            case 3:
                iv_instructLevel.setBackground(getResources().getDrawable(R.drawable.ic_trust_3));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void getGATTCallback(Pressure pressure) {

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getInstruction(Comm2Frags comm2Frags) {
        if (comm2Frags.getType() != Comm2Frags.Type.FromActivity) return;
        switch (comm2Frags.getInstruct()) {
            case "PULSE_UP_OFF":
                Log.d("MSL", "getInstruction: " + comm2Frags.getInstruct());
                iv_instructLevel.setBackground(getResources().getDrawable(R.drawable.ic_trust_0));
                tv_heartRate.setText("--");
                lineView_heartRate.setCurrentValue(0);
                card_heartRate.setVisibility(View.GONE);
                break;
            case "PULSE_UP_ON":
                card_heartRate.setVisibility(View.VISIBLE);
                break;
            case "DATA_UP_ON":
                card_status.setVisibility(View.VISIBLE);
                break;
            case "DATA_UP_OFF":
                tv_status.setText("--");
                imgWheelView.setChecked(0);
                card_status.setVisibility(View.GONE);
                break;
        }
    }
/**_______________________________________↑↑↑↑__EventBus__↑↑↑↑______________________________________________*/

}
