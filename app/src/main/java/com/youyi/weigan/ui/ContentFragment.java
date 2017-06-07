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
import android.widget.ListView;
import android.widget.TextView;

import com.youyi.weigan.R;
import com.youyi.weigan.adapter.MyAngVDataAdapter;
import com.youyi.weigan.adapter.MyGravADataAdapter;
import com.youyi.weigan.adapter.MyMagDataAdapter;
import com.youyi.weigan.adapter.MyPressureDataAdapter;
import com.youyi.weigan.adapter.MyPulseDataAdapter;
import com.youyi.weigan.beans.AngV;
import com.youyi.weigan.beans.GravA;
import com.youyi.weigan.beans.Mag;
import com.youyi.weigan.beans.Pressure;
import com.youyi.weigan.beans.Pulse;
import com.youyi.weigan.beans.PulseBean;
import com.youyi.weigan.beans.UserBean;
import com.youyi.weigan.eventbean.Comm2Frags;
import com.youyi.weigan.utils.EventUtil;
import com.youyi.weigan.utils.StatusUtils;
import com.youyi.weigan.view.ImgWheelView;
import com.youyi.weigan.view.LineView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.youyi.weigan.utils.DateUtils.current_100_TimeMillis;

public class ContentFragment extends Fragment {

    private TextView tv_heartRate;
    private LineView lineView_heartRate;
    private CardView card_heartRate;
    private ImageView iv_instructLevel;
    private ImgWheelView imgWheelView;
    private TextView tv_status;
    private CardView card_status;
    private ArrayList<AngV> latestAngV = new ArrayList<>();
    private ListView lv_gravA;
    private ListView lv_ang;
    private ListView lv_mag;
    private ListView lv_pressure;
    private MyGravADataAdapter adapterGravA;
    private MyMagDataAdapter adapterMag;
    private MyAngVDataAdapter adapterAng;
    private MyPressureDataAdapter adapterPressure;
    private MyPulseDataAdapter adapterPulse;

    ArrayList<Pulse> pulseArrayList = new ArrayList<>();
    ArrayList<Mag> magArrayList = new ArrayList<>();
    ArrayList<Pressure> pressureArrayList = new ArrayList<>();
    ArrayList<GravA> gravAArrayList = new ArrayList<>();
    ArrayList<AngV> angVList = new ArrayList<>();
    ArrayList<Pulse> pulseList = new ArrayList<>();
    ArrayList<Mag> magList = new ArrayList<>();
    ArrayList<Pressure> pressureList = new ArrayList<>();
    ArrayList<GravA> gravAList = new ArrayList<>();
    ArrayList<AngV> angVArrayList = new ArrayList<>();
    private final int LIST_SIZE = 1000;
    private final int SIZE_2 = 5;
    private boolean getDataEnd;//接收完数据，将小于100的list也存储和上传
    private UserBean userBean;

    private final int WALK = 0;
    private final int RUN = 1;
    private final int BIKE = 2;
    private final int SIT = 3;
    private final int Lie = 4;
    private final int Stairs = 5;
    private final int Elevator = 6;


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
        if (userBean == null) {
            userBean = UserBean.getInstence();
        }
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
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_stand),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_sleep),
                BitmapFactory.decodeResource(getResources(), R.drawable.stairs),
                BitmapFactory.decodeResource(getResources(), R.drawable.elevator)

        };
        Bitmap centerBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stand);
        imgWheelView.setImgs(imgs);
        imgWheelView.setCenterImg(centerBmp);
        lv_gravA = (ListView) view.findViewById(R.id.lv_gravA);
        lv_ang = (ListView) view.findViewById(R.id.lv_ang);
        lv_mag = (ListView) view.findViewById(R.id.lv_mag);
        lv_pressure = (ListView) view.findViewById(R.id.lv_pressure);
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
    public synchronized void getGATTCallback(Pulse pulse) {
        //心率历史
        pulseArrayList.add(pulse);
        //添加到另一个list，用于传到searchResultActivity中本地显示最近的数据
        pulseList.add(pulse);
        if (pulseList.size() > SIZE_2) {
            pulseList.remove(0);
        }

        if (pulseArrayList.size() == LIST_SIZE || getDataEnd) {

            ArrayList<Pulse> list = new ArrayList<>();
            list.addAll(pulseArrayList);
            userBean.getPulseArrayList().addAll(list);
            pulseArrayList.clear();
//            Log.i("MSL", "getGATTCallback: " + userBean.getPulseArrayList().size() + "," + pulseArrayList.size() + "," +list.size());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void getGATTCallback(GravA gravA) {
        if (gravA.getTime() != 0) {
            //心率历史
            gravAArrayList.add(gravA);

            if (gravAArrayList.size() == LIST_SIZE || getDataEnd) {
                ArrayList<GravA> list = new ArrayList<>();
                list.addAll(gravAArrayList);
                userBean.getGravAArrayList().addAll(list);
                gravAArrayList.clear();
//            Log.i("MSL", "getGATTCallback: " + userBean.getGravAArrayList().size() + "," + gravAArrayList.size() + "," +list.size());
            }
        } else {
            gravA.setTime(current_100_TimeMillis());
            gravAList.add(gravA);
            if (gravAList.size() > SIZE_2) {
                gravAList.remove(0);
            }
            if (adapterGravA == null)
                adapterGravA = new MyGravADataAdapter(gravAList, this.getContext().getApplicationContext());
            lv_gravA.setAdapter(adapterGravA);
            adapterGravA.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void getGATTCallback(AngV angV) {
        if (angV.getTime() != 0) {
            //心率历史
            angVArrayList.add(angV);
            if (angVArrayList.size() == LIST_SIZE || getDataEnd) {
                ArrayList<AngV> list = new ArrayList<>();
                list.addAll(angVArrayList);
                userBean.getAngVArrayList().addAll(list);
                angVArrayList.clear();
//            Log.i("MSL", "getGATTCallback: " + userBean.getAngVArrayList().size() + "," + angVArrayList.size() + "," +list.size());
            }
        } else {
            angV.setTime(current_100_TimeMillis());
            angVList.add(angV);
            if (angVList.size() > SIZE_2) {
                angVList.remove(0);
            }
            if (adapterAng == null)
                adapterAng = new MyAngVDataAdapter(angVList, this.getContext().getApplicationContext());
            lv_ang.setAdapter(adapterAng);
            adapterAng.notifyDataSetChanged();

        }
    }

    private void stateByAng() {
        if (angVList.size() < SIZE_2) return;

        StatusUtils.Status state = StatusUtils.getMasterStateByAng(angVList);
        Log.e("MSL", "getGATTCallback: " + state);
        switch (state) {
            case Sit:
                imgWheelView.setChecked(SIT);
                tv_status.setText("静止");
                break;
            case Walk:
                imgWheelView.setChecked(WALK);
                tv_status.setText("步行");
                break;
            case Run:
                imgWheelView.setChecked(RUN);
                tv_status.setText("跑步");
                break;
            case Bike:
                imgWheelView.setChecked(BIKE);
                tv_status.setText("自行车");
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void getGATTCallback(Mag mag) {
        if (mag.getTime() != 0) {
            //心率历史
            magArrayList.add(mag);

            if (magArrayList.size() == LIST_SIZE || getDataEnd) {
                ArrayList<Mag> list = new ArrayList<>();
                list.addAll(magArrayList);
                userBean.getMagArrayList().addAll(list);
                magArrayList.clear();
//            Log.i("MSL", "getBluetoothCallback: " + userBean.getPulseArrayList().size() + "," + magArrayList.size() + "," +list.size());
            }
        } else {
            mag.setTime(current_100_TimeMillis());
            magList.add(mag);
            if (magList.size() > SIZE_2) {
                magList.remove(0);
            }
            if (adapterMag == null)
                adapterMag = new MyMagDataAdapter(magList, this.getContext().getApplicationContext());
            lv_mag.setAdapter(adapterMag);
            adapterMag.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void getGATTCallback(Pressure pressure) {
        if (pressure.getTime() != 0) {
            //心率历史
            pressureArrayList.add(pressure);

            if (pressureArrayList.size() == LIST_SIZE || getDataEnd) {
                ArrayList<Pressure> list = new ArrayList<>();
                list.addAll(pressureArrayList);
                userBean.getPressureArrayList().addAll(list);
                pressureArrayList.clear();
//            Log.i("MSL", "getGATTCallback: " + userBean.getPulseArrayList().size() + "," + pressureArrayList.size() + "," +list.size());
            }
        } else {
            pressure.setTime(current_100_TimeMillis());
            pressureList.add(pressure);
            if (pressureList.size() > SIZE_2) {
                pressureList.remove(0);
            }
            if (adapterPressure == null)
                adapterPressure = new MyPressureDataAdapter(pressureList, getContext().getApplicationContext());
            lv_pressure.setAdapter(adapterPressure);
            adapterPressure.notifyDataSetChanged();

            stateByPressure();
        }
    }

    private void stateByPressure() {
        if (pressureList.size() < SIZE_2) return;
        StatusUtils.Status state = StatusUtils.getMasterStateByPressure(pressureList,angVList);
        switch (state) {
            case Normal:
                stateByAng();
                break;
            case Elevator:
                imgWheelView.setChecked(Elevator);
                tv_status.setText("电梯");
                break;
            case Stairs:
                imgWheelView.setChecked(Stairs);
                tv_status.setText("楼梯");
                break;
        }
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
            case "GET_DATA_END":
                getDataEnd = true;
                break;
            case "GET_DATA_START":
                getDataEnd = false;
                break;
        }
    }

    /**
     * _______________________________________↑↑↑↑__EventBus__↑↑↑↑______________________________________________
     */

}
