package com.youyi.weigan.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
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
import com.youyi.weigan.utils.DateUtils;
import com.youyi.weigan.utils.EventUtil;
import com.youyi.weigan.utils.ScreenShot;
import com.youyi.weigan.utils.StatusUtils;
import com.youyi.weigan.view.ImgWheelView;
import com.youyi.weigan.view.LineView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.youyi.weigan.utils.DateUtils.current_100_TimeMillis;

public class ContentFragment extends Fragment implements LocationSource,
        AMapLocationListener, AMap.OnMyLocationChangeListener, GeocodeSearch.OnGeocodeSearchListener {

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
    private final int LIST_SIZE = 200;
    private final int SIZE_2 = 15;
    private boolean getDataEnd;//接收完数据，将小于100的list也存储和上传
    private UserBean userBean;

    private final int STATIC = 0;
    private final int WALK = 1;
    private final int RUN = 2;
    private final int BIKE = 3;
    private final int UpStairs = 4;
    private final int DownStairs = 5;
    private final int Elevator = 6;
    private TextView tv_grav;
    private TextView tv_angV;
    private TextView tv_mag;
    private TextView tv_pressure;
    private ScrollView scroll;

    private TextureMapView map_view;
    private AMap aMap = null;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private GeocodeSearch geocoderSearch;
    private int lastState;

    final String[] stateStr = new String[]{"静止","步行","跑步","自行车","上楼梯","下楼梯"};


    public ContentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        EventUtil.register(this);

        map_view.onCreate(savedInstanceState);
        initLocation();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        map_view.onResume();
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }

    @Override
    public void onPause() {
        super.onPause();
        map_view.onPause();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//ֹͣ��λ
        }
        deactivate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map_view.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventUtil.unregister(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        map_view.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        Bitmap centerBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stand);
        final Bitmap[] imgs = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_stand),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_walk),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_run),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_bike),
                BitmapFactory.decodeResource(getResources(), R.drawable.up_stairs),
                BitmapFactory.decodeResource(getResources(), R.drawable.down_stairs),
//                BitmapFactory.decodeResource(getResources(), R.drawable.elevator)
        };
        imgWheelView.setImgs(imgs);
        imgWheelView.setCenterImg(centerBmp);
        lv_gravA = (ListView) view.findViewById(R.id.lv_gravA);
        lv_ang = (ListView) view.findViewById(R.id.lv_ang);
        lv_mag = (ListView) view.findViewById(R.id.lv_mag);
        lv_pressure = (ListView) view.findViewById(R.id.lv_pressure);
        tv_grav = (TextView) view.findViewById(R.id.tv_grav);
        tv_angV = (TextView) view.findViewById(R.id.tv_angV);
        tv_mag = (TextView) view.findViewById(R.id.tv_mag);
        tv_pressure = (TextView) view.findViewById(R.id.tv_pressure);
        scroll = (ScrollView) view.findViewById(R.id.scroll);
        map_view = (TextureMapView) view.findViewById(R.id.map_view);
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
            tv_grav.setText("重力加速度:" + DateUtils.getDateToString(gravA.getTime() * 100));
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
                adapterGravA = new MyGravADataAdapter(gravAList, this.getActivity().getApplicationContext());
            lv_gravA.setAdapter(adapterGravA);
            adapterGravA.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void getGATTCallback(AngV angV) {
        if (angV.getTime() != 0) {
            //心率历史
            tv_angV.setText("角速度:" + DateUtils.getDateToString(angV.getTime() * 100));
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
                getStatus();
            }
            if (adapterAng == null)
                adapterAng = new MyAngVDataAdapter(angVList, this.getActivity().getApplicationContext());
            lv_ang.setAdapter(adapterAng);
            adapterAng.notifyDataSetChanged();
        }
    }

    private void getStatus() {
        StatusUtils.Status status = StatusUtils.getStatus(gravAList, angVList);
        int index = -1;
        switch (status) {
            case Static:
                index = STATIC;
                break;
            case Walk:
                index = WALK;
                break;
            case Run:
                index = RUN;
                break;
            case Bike:
                index = BIKE;
                break;
            case UpStairs:
                index = UpStairs;
                break;
            case DownStairs:
                index = DownStairs;
                break;
        }
        if (index == lastState){
            imgWheelView.setChecked(index);
            tv_status.setText(stateStr[index]);
        }
        lastState = index;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void getGATTCallback(Mag mag) {
        if (mag.getTime() != 0) {
            //地磁历史
            tv_mag.setText("地磁强度:" + DateUtils.getDateToString(mag.getTime() * 100));
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
                adapterMag = new MyMagDataAdapter(magList, this.getActivity().getApplicationContext());
            lv_mag.setAdapter(adapterMag);
            adapterMag.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void getGATTCallback(Pressure pressure) {
        if (pressure.getTime() != 0) {
            //心率历史
            tv_pressure.setText("气压强度:" + DateUtils.getDateToString(pressure.getTime() * 100));
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
                adapterPressure = new MyPressureDataAdapter(pressureList, this.getActivity().getApplicationContext());
            lv_pressure.setAdapter(adapterPressure);
            adapterPressure.notifyDataSetChanged();

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
            case "ScreenShot":
                String fileName = this.getActivity().getCacheDir().toString() + "/screenshot" + System.currentTimeMillis() + ".PNG";
                Log.i("MSL", "getInstruction:  screenshot" + fileName);
                ScreenShot.shootScrollView(scroll, fileName);
                break;
        }
    }

    /**
     * _______________________________________↑↑↑↑__EventBus__↑↑↑↑______________________________________________
     */

    private void initLocation() {
        if (aMap == null) {
            aMap = map_view.getMap();
        }
        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        geocoderSearch = new GeocodeSearch(this.getActivity().getApplicationContext());


    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("MSL", errText);
            }
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
        LatLonPoint latLonPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());

//        Log.i("MSL", "onMyLocationChange: " + latLonPoint.getLatitude() + " , " + latLonPoint.getLongitude());

        geocoderSearch = new GeocodeSearch(this.getActivity().getApplicationContext());
        geocoderSearch.setOnGeocodeSearchListener(this);

        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);

        geocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = new AMapLocationClient(this.getActivity().getApplicationContext());
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();//启动定位
            //显示室内地图
            aMap.showIndoorMap(true);

            aMap.setOnMyLocationChangeListener(this);

            aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
            //隐藏logo
            aMap.getUiSettings().setLogoBottomMargin(-50);
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress addr = regeocodeResult.getRegeocodeAddress();
        String str = addr.getCity() + " - " //市
                + addr.getDistrict() + " - " //城区
                + addr.getFormatAddress() + " , "//详细地址
                + addr.getAdCode();//邮编
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
