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
import com.youyi.weigan.beans.AngV;
import com.youyi.weigan.beans.Pressure;
import com.youyi.weigan.beans.PulseBean;
import com.youyi.weigan.eventbean.Comm2Frags;
import com.youyi.weigan.utils.ConstantPool;
import com.youyi.weigan.utils.EventUtil;
import com.youyi.weigan.utils.MathUtils;
import com.youyi.weigan.view.ImgWheelView;
import com.youyi.weigan.view.LineView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;

import static com.youyi.weigan.utils.MathUtils.getVarianceUtil;

public class ContentFragment extends Fragment {

    private TextView tv_heartRate;
    private LineView lineView_heartRate;
    private CardView card_heartRate;
    private ImageView iv_instructLevel;
    private ImgWheelView imgWheelView;
    private TextView tv_status;
    private CardView card_status;
    private ArrayList<AngV> latestAngV = new ArrayList<>();

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
        Bitmap centerBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_state);
        imgWheelView.setImgs(imgs);
        imgWheelView.setCenterImg(centerBmp);
    }

    /**
     * ___________________________________________↓↓↓↓__EventBus__↓↓↓↓_______________________________________
     */
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
    public synchronized void getGATTCallback(AngV angV) {
        latestAngV.add(angV);

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
                latestAngV.clear();
                break;
        }
    }

    /**
     * _______________________________________↑↑↑↑__EventBus__↑↑↑↑______________________________________________
     */

    /** 当前 是 跑步 状态 否 走路*/
    private boolean AngVCompute() {
        if (latestAngV.size() < 5) return false;
        long current = System.currentTimeMillis();
        ArrayList<Integer> listX = new ArrayList<>();
        ArrayList<Integer> listY = new ArrayList<>();
        ArrayList<Integer> listZ = new ArrayList<>();
        int a = -1;
        for (int i = 0; i < latestAngV.size(); i++) {
            AngV angV = latestAngV.get(i);
            if (angV.getTime() * 10 < current - 1000 * 5) {
                a = i ;
            }
            listX.add(angV.getVelX());
            listY.add(angV.getVelY());
            listZ.add(angV.getVelZ());
        }
        if (a != -1) {
            for (int i = 0; i <= a; i++) {
                latestAngV.remove(0);
                listX.remove(0);
                listY.remove(0);
                listZ.remove(0);
            }
        }

        Integer[] xarray = listX.toArray(new Integer[listX.size()]);
        Integer[] yarray = listX.toArray(new Integer[listY.size()]);
        Integer[] zarray = listX.toArray(new Integer[listZ.size()]);

        double xVariance = MathUtils.getVarianceUtil(xarray);
        double yVariance = MathUtils.getVarianceUtil(yarray);
        double zVariance = MathUtils.getVarianceUtil(zarray);

        return xVariance >= ConstantPool.ANG_X && yVariance >= ConstantPool.ANG_Y && zVariance >= ConstantPool.ANG_Z;
    }

}
