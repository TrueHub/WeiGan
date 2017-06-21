package com.youyi.weigan.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.youyi.weigan.R;
import com.youyi.weigan.adapter.DeviceListAdapter;
import com.youyi.weigan.beans.DeviceStatusBean;
import com.youyi.weigan.beans.SensorFreq;
import com.youyi.weigan.beans.UserBean;
import com.youyi.weigan.eventbean.Comm2Frags;
import com.youyi.weigan.eventbean.Comm2GATT;
import com.youyi.weigan.eventbean.Comm2WriteService;
import com.youyi.weigan.eventbean.EventNotification;
import com.youyi.weigan.eventbean.Event_BleDevice;
import com.youyi.weigan.moudul.ControlDeviceImp;
import com.youyi.weigan.service.GATTService;
import com.youyi.weigan.service.WriteService;
import com.youyi.weigan.utils.DateUtils;
import com.youyi.weigan.utils.EventUtil;
import com.youyi.weigan.utils.FileUtils;
import com.youyi.weigan.utils.RequestPermissionUtils;
import com.youyi.weigan.utils.ScreenUtil;
import com.youyi.weigan.utils.ServiceUtils;
import com.youyi.weigan.utils.SystemBarTintManager;
import com.youyi.weigan.utils.WIFIUtils;
import com.youyi.weigan.view.BattaryView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.youyi.weigan.eventbean.Comm2GATT.TYPE.REAL_DATA_OFF;
import static com.youyi.weigan.eventbean.Comm2GATT.TYPE.REAL_DATA_ON;
import static com.youyi.weigan.eventbean.Comm2GATT.TYPE.REAL_PULSE_OFF;
import static com.youyi.weigan.eventbean.Comm2GATT.TYPE.REAL_PULSE_ON;
import static com.youyi.weigan.eventbean.Comm2GATT.TYPE.SEARCH_DEVICE_STATUE;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private FrameLayout frame_content;
    private CoordinatorLayout main_content;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private AppBarLayout appbar;
    private DrawerLayout drawerlayout;
    private TextView tv_deviceId;
    private TextView tv_battery;
    private BattaryView ic_battery;
    private TextView info_tv_chk_time;
    private TextView info_tv_sensor_freq;
    private TextView info_tv_battery;
    private TextView info_tv_vib_low;
    private TextView info_tv_vib_high;
    private LinearLayout device_info;

    //侧滑菜单中的头部
    private TextView nvg_header_deviceId;
    //侧滑菜单中的各个按钮
    private MenuItem nvg_conn;
    private MenuItem nvg_chk_status;
    private MenuItem nvg_collect_data;
    private MenuItem nvg_upload_data;
    private MenuItem nvg_clear_flash;
    private MenuItem nvg_del_cache;
    private MenuItem nvg_gps_location;
    private MenuItem nvg_net_location;
    //侧滑菜单中的real data 组中的Switch
    private Switch sw_heartRate_real;
    private Switch sw_data_real;
    //侧滑菜单中的location组中的Switch
    private Switch sw_location;
    private Switch sw_network_loca;
    private Switch sw_gps_loca;
    //侧滑菜单中的sensor setting中的SeekBar
    private SeekBar sb_grav;
    private SeekBar sb_ang;
    private SeekBar sb_mag;
    private SeekBar sb_pressure;

    private ControlDeviceImp controlDeviceImp;

    private Toast toast;
    private boolean isStarted;
    private Intent gattService;
    private UserBean userBean;
    private Intent writeServiceIntent;
    private static boolean isWifiState;
    private SensorFreq sensorFreq;//带有传感器频率设置的bean类
    private DeviceListAdapter adapter;
    private AlertDialog devicelistDialog;
    private RecyclerView recyclerView;
    private int[] freqValues = new int[4];
    private boolean getDataEnd;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            if (aMapLocation == null) {
                Log.e("MSL", "onLocationChanged:  + aMapLocation is null");
                return;
            }
            if (aMapLocation.getErrorCode() != 0) {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                return;
            }
            String str = aMapLocation.getCity() + " - "
                    + aMapLocation.getDistrict() + " - "
                    + aMapLocation.getStreet() + " - "
                    + aMapLocation.getStreetNum() + " , "
                    + aMapLocation.getBuildingId() + " , "
                    + aMapLocation.getFloor();
            controlDeviceImp.showToast(str);
            Log.i("MSL", "onLocationChanged: " + str);
/**
 * amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
 * amapLocation.getLatitude();//获取纬度
 * amapLocation.getLongitude();//获取经度
 * amapLocation.getAccuracy();//获取精度信息
 * amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
 * amapLocation.getCountry();//国家信息
 * amapLocation.getProvince();//省信息
 * amapLocation.getCity();//城市信息
 * amapLocation.getDistrict();//城区信息
 * amapLocation.getStreet();//街道信息
 * amapLocation.getStreetNum();//街道门牌号信息
 * amapLocation.getCityCode();//城市编码
 * amapLocation.getAdCode();//地区编码
 * amapLocation.getAoiName();//获取当前定位点的AOI信息
 * amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
 * amapLocation.getFloor();//获取当前室内定位的楼层
 * amapLocation.getGpsStatus();//获取GPS的当前状态
 //获取定位时间
 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 Date date = new Date(amapLocation.getTime());
 df.format(date);
 */

        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setTintColor(getResources().getColor(R.color.colorPrimary));

            device_info.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        }

        initNavigationItem();

        requestMyPermissions();

        writeServiceIntent = new Intent(this, WriteService.class);

        if (userBean == null) {
            userBean = UserBean.getInstence();
        }

        toast = Toast.makeText(this.getApplicationContext(), "再次点击退出程序", Toast.LENGTH_SHORT);

        controlDeviceImp = new ControlDeviceImp(this);

        if (WIFIUtils.isWifiType(this)) {
            isWifiState = true;
            controlDeviceImp.showToast("当前连接：" + WIFIUtils.getWifiId((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)));
        }

        setActionBar();

        setNavigationItemClickListener();

        initFragments();

    }

    /**
     * 初始navigation中的各个可点击的item，层级比较多
     */
    private void initNavigationItem() {

        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            MenuItem root = navigationView.getMenu().getItem(i);//第一层级的item
            switch (root.getItemId()) {//
                //conn按钮
                case R.id.navigation_conn:
                    nvg_conn = root;
                    break;
                //获取设备状态
                case R.id.navigation_check_status:
                    nvg_chk_status = root;
                    break;
                //收集数据
                case R.id.navigation_collect_data:
                    nvg_collect_data = root;
                    getDataEnd = false;
                    break;
                //上传本地数据
                case R.id.navigation_upload_data:
                    nvg_upload_data = root;
                    break;
                //清除外接设备的缓存数据
                case R.id.navigation_clear_flash:
                    nvg_clear_flash = root;
                    break;
                //删除本地缓存数据
                case R.id.navigation_clear_data:
                    nvg_del_cache = root;
                    break;

                //real这一组
                case R.id.navigation_real_group:
                    //遍历real group 的item下的menu item。。。
                    for (int j = 0; j < root.getSubMenu().size(); j++) {
                        MenuItem v = root.getSubMenu().getItem(j);
                        //还是根据id判断
                        switch (v.getItemId()) {
                            case R.id.navigation_realTime_heartRate:
                                sw_heartRate_real = (Switch) v.getActionView().findViewById(R.id._switch);
                                sw_heartRate_real.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        Log.i("MSL", "onCheckedChanged: " + isChecked);
                                        if (isChecked) {
                                            EventUtil.post(REAL_PULSE_ON);
                                            EventUtil.post(new Comm2Frags("PULSE_UP_ON", Comm2Frags.Type.FromActivity));
                                        } else {
                                            EventUtil.post(REAL_PULSE_OFF);
                                            EventUtil.post(new Comm2Frags("PULSE_UP_OFF", Comm2Frags.Type.FromActivity));
                                        }
                                    }
                                });
                                break;
                            case R.id.navigation_realData:
                                sw_data_real = (Switch) v.getActionView().findViewById(R.id._switch);
                                sw_data_real.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            EventUtil.post(REAL_DATA_ON);
                                            EventUtil.post(new Comm2Frags("DATA_UP_ON", Comm2Frags.Type.FromActivity));

                                        } else {
                                            EventUtil.post(REAL_DATA_OFF);
                                            EventUtil.post(new Comm2Frags("DATA_UP_OFF", Comm2Frags.Type.FromActivity));
                                        }
                                    }
                                });
                                break;
                        }
                    }
                    break;
                //传感器设置一组
                case R.id.navigation_sensor_setting:
                    for (int j = 0; j < root.getSubMenu().size(); j++) {
                        MenuItem v = root.getSubMenu().getItem(j);
                        switch (v.getItemId()) {
                            case R.id.navigation_sensor_freq:

                                break;
                            case R.id.navigation_vib_low:

                                break;
                            case R.id.navigation_vib_up:

                                break;
                        }
                    }
                    break;
                case R.id.navigation_aMap:

                    for (int j = 0; j < root.getSubMenu().size(); j++) {
                        MenuItem v = root.getSubMenu().getItem(j);

                        switch (v.getItemId()) {
                            case R.id.navigation_location_switch:
                                sw_location = (Switch) v.getActionView().findViewById(R.id._switch);
                                sw_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            nvg_gps_location.setVisible(true);
                                            nvg_net_location.setVisible(true);

                                            initLocationClientOption();

                                            //启动定位
                                            mLocationClient.startLocation();

                                        } else {
                                            mLocationClient.stopLocation();
                                            mLocationClient.onDestroy();
                                            nvg_gps_location.setVisible(false);
                                            nvg_net_location.setVisible(false);
                                        }
                                    }
                                });
                                break;

                            case R.id.navigation_gps_location:
                                nvg_gps_location = v;
                                sw_gps_loca = (Switch) v.getActionView().findViewById(R.id._switch);

                                sw_gps_loca.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        Log.i("MSL", "onCheckedChanged: " + isChecked);
                                        if (isChecked) {
                                            //设置定位模式为AMapLocationMode.Device_Sensors，仅设备模式。
                                            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);

                                            //给定位客户端对象设置定位参数
                                            mLocationClient.setLocationOption(mLocationOption);

                                            mLocationClient.startLocation();
                                        } else {

                                        }
                                    }
                                });
                                break;
                            case R.id.navigation_net_location:
                                nvg_net_location = v;
                                sw_network_loca = (Switch) v.getActionView().findViewById(R.id._switch);
                                sw_network_loca.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        Log.i("MSL", "onCheckedChanged: " + isChecked);
                                        if (isChecked) {
                                            //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
                                            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

                                            //给定位客户端对象设置定位参数
                                            mLocationClient.setLocationOption(mLocationOption);

                                            mLocationClient.startLocation();
                                        } else {

                                        }
                                    }
                                });
                                break;
                        }
                    }


                    break;

            }
        }
    }

    //侧滑菜单item点击事件
    private void setNavigationItemClickListener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_conn:

                        String title = (String) (item.getTitle());
                        Log.d("MSL", "onNavigationItemSelected: conn");
                        requestMyPermissions();

                        if (title.equals("连接")) {
                            if (ServiceUtils.isServiceWork(MainActivity.this, "com.youyi.weigan.service.GATTService")) {
                                EventUtil.post(Comm2GATT.TYPE.START_SCAN);
                                Log.e("MSL", "onClick: gatt service is running");
                            } else {
                                gattService = new Intent(MainActivity.this, GATTService.class);
                                startService(gattService);
                                Log.e("MSL", "onClick: gatt is not running");
                            }
                            startWriteService();

                        } else if (title.equals("断开")) {
                            EventUtil.post(Comm2GATT.TYPE.STOP_GATT_SERVICE);
                        }
                        break;
                    case R.id.navigation_check_status:
                        EventUtil.post(SEARCH_DEVICE_STATUE);
                        break;
                    case R.id.navigation_collect_data:

                        startWriteService();

                        EventUtil.post(new Comm2Frags("GET_DATA_START", Comm2Frags.Type.FromActivity));

                        EventUtil.post(Comm2GATT.TYPE.SEARCH_HIS);
                        break;
                    case R.id.navigation_upload_data:
                        String size = FileUtils.getFileSize("weigan");
                        showMessageOKCancel(MainActivity.this, "未上传至服务器的缓共有？" + size + " ,是否现在上传？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EventUtil.post(Comm2WriteService.UpLoadCache);
                            }
                        });
                        break;
                    case R.id.navigation_clear_flash:
                        showMessageOKCancel(MainActivity.this, "确定要清空蓝牙设备里量测好的数据吗？（会丢失部分数据） 请确认已经接受完所有数据！！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EventUtil.post(Comm2GATT.TYPE.CLEAR_FLASH);
                            }
                        });
                        break;
                    case R.id.navigation_clear_data:
                        String cacheSize = FileUtils.getCacheSize("weigan");
                        showMessageOKCancel(MainActivity.this, "sd卡内存储的缓存文件共有 " + cacheSize + " , 是否删除？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean d = FileUtils.deleteCache("weigan");
                                if (d) controlDeviceImp.showToast("已删");

                            }
                        });
                        break;
                    case R.id.navigation_realTime_heartRate:
                        sw_heartRate_real.setChecked(!sw_heartRate_real.isChecked());

                        break;
                    case R.id.navigation_realData:
                        sw_data_real.setChecked(!sw_data_real.isChecked());
                        break;
                    case R.id.navigation_sensor_freq:
                        Log.d("MSL", "onNavigationItemSelected: sensor");
                        showDialogForSeekBar(MainActivity.this);
                        break;
                    case R.id.navigation_vib_low:

                        break;
                    case R.id.navigation_vib_up:

                        break;
                    case R.id.navigation_location_switch:
                        sw_location.setChecked(!sw_location.isChecked());
                        break;
                    case R.id.navigation_gps_location:
                        sw_gps_loca.setChecked(!sw_gps_loca.isChecked());
                        break;
                    case R.id.navigation_net_location:
                        sw_network_loca.setChecked(!sw_network_loca.isChecked());
                        break;

                }
                return false;
            }
        });
    }

    private void initLocationClientOption() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000);

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiActiveScan(true);

        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);

        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);

        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

    }

    private void initFragments() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        ContentFragment contentFragment = new ContentFragment();

        transaction.add(R.id.frame_content, contentFragment).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventUtil.register(this);
    }

    private void setActionBar() {
        if (toolbar == null) return;
        showToolbar(true);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar == null) return;
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onBackPressed() {
        if (toast.getView().getParent() == null)
            toast.show();
        else {
            toast.cancel();
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        EventUtil.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isStarted) {//停止Service
            stopService(gattService);
            isStarted = false;
        }
        EventUtil.unregister(this);//注销eventbus
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isStarted) {//停止Service
            stopService(writeServiceIntent);
            stopService(gattService);
            isStarted = false;
        }
        EventUtil.unregister(this);//注销eventbus
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
                    drawerlayout.closeDrawers();
                } else {
                    drawerlayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.appbar_screenshot:

                EventUtil.post(new Comm2Frags("ScreenShot", Comm2Frags.Type.FromActivity));

                break;
            case R.id.appbar_share:
                Log.i("MSL", "onOptionsItemSelected: share");
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        frame_content = (FrameLayout) findViewById(R.id.frame_content);
        main_content = (CoordinatorLayout) findViewById(R.id.main_content);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        navigationView.setItemIconTintList(null);
        //取消竖直方向的scrollbar
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }

        appbar = (AppBarLayout) findViewById(R.id.appbar);
        drawerlayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        tv_battery = (TextView) toolbar.findViewById(R.id.tv_battery_toolbar);
        ic_battery = (BattaryView) toolbar.findViewById(R.id.ic_battery_toolbar);
        tv_deviceId = (TextView) toolbar.findViewById(R.id.tv_deviceId_toolbar);

        device_info = (LinearLayout) findViewById(R.id.device_info);
        info_tv_chk_time = (TextView) device_info.findViewById(R.id.tv_last_check_time_info);
        info_tv_battery = (TextView) device_info.findViewById(R.id.tv_battery_info);
        info_tv_vib_low = (TextView) device_info.findViewById(R.id.tv_vib_low_info);
        info_tv_vib_high = (TextView) device_info.findViewById(R.id.tv_vib_high_info);
        info_tv_sensor_freq = (TextView) device_info.findViewById(R.id.sensor_freq_info);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
    }

    /**
     * ___________________________________________↓↓↓↓__EventBus__↓↓↓↓_____________________________________________________________
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getGATTCallback(String str) {
        switch (str) {
            case "上传成功":

                break;
            default:
                controlDeviceImp.showToast(str);
                break;
        }
    }

    private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<>();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBtDevice(Event_BleDevice event_bleDevice) {
        if (event_bleDevice.getFrom() == Event_BleDevice.From.Activity) {
            devicelistDialog.dismiss();
            devicelistDialog = null;
            adapter = null;
            recyclerView = null;
        } else if (event_bleDevice.getFrom() == Event_BleDevice.From.Gatt) {
            BluetoothDevice bluetoothDevice = event_bleDevice.getDevice();
            if (btDeviceList.contains(bluetoothDevice)) return;
            if (bluetoothDevice.getName() == null) return;
            btDeviceList.add(bluetoothDevice);

            if (btDeviceList.size() > 0) {
                if (adapter == null) adapter = new DeviceListAdapter(btDeviceList, this);

                recyclerView = new RecyclerView(this);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.addItemDecoration(new DividerItemDecoration(
                        this, DividerItemDecoration.HORIZONTAL));
                recyclerView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
                showDeviceChooseDialog(MainActivity.this);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void getGATTCallback(DeviceStatusBean deviceStatusBean) {
        //设备返回状态值
        final int[] time = {deviceStatusBean.getTime()};
//        Log.i("MSL", "getGATTCallback: " + (long) time[0] * 1000 + "," + time[0]);
        int pulseAbnomal_min = deviceStatusBean.getPulseAbnomal_min();
        int pulseAbnomal_max = deviceStatusBean.getPulseAbnomal_max();
        int simplingFreq = deviceStatusBean.getSimplingFreq();
        final int deviceElec = deviceStatusBean.getDeviceElec();

        //toolbar上初始化
        tv_battery.setVisibility(View.VISIBLE);
        ic_battery.setVisibility(View.VISIBLE);

        tv_battery.setText(deviceElec + "%");
        ic_battery.setBattaryPercent(deviceElec);

        //下拉显示的info
        device_info.setVisibility(View.VISIBLE);
        collapsingToolbarLayout.setScrimsShown(true);
        info_tv_battery.setText(deviceElec + "%");
        info_tv_sensor_freq.setText(String.valueOf(simplingFreq));
        info_tv_vib_low.setText(String.valueOf(pulseAbnomal_min));
        info_tv_vib_high.setText(String.valueOf(pulseAbnomal_max));
        info_tv_chk_time.setText(DateUtils.getDateToString((long) time[0] * 100));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDataOver(EventNotification eventNotification) {
        switch (eventNotification.getType()) {
            case "HIS_DATA":
                getDataEnd = true;
                EventUtil.post(new Comm2Frags("GET_DATA_END", Comm2Frags.Type.FromActivity));
                break;

            case "NET STATE":
                startWriteService();
                break;
            default:
                if (eventNotification.isGetOver()) {
                    controlDeviceImp.showToast("连接" + eventNotification.getType() + "成功");
                    tv_deviceId.setText(eventNotification.getType());

                    //侧滑菜单的header中
                    RelativeLayout relativeLayout = (RelativeLayout) navigationView.getHeaderView(0);
                    nvg_header_deviceId = (TextView) relativeLayout.findViewById(R.id.header_connect_status);
                    nvg_header_deviceId.setText(eventNotification.getType());
                    nvg_conn.setTitle(R.string.disConn);

                    EventUtil.post(SEARCH_DEVICE_STATUE);

                    if (sw_heartRate_real.isChecked()) {
                        EventUtil.post(REAL_PULSE_ON);
                    } else {
                        EventUtil.post(REAL_PULSE_OFF);
                    }

                } else {
                    initAll();
                }
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sensorSetting(SensorFreq sensorFreq) {
        if (sensorFreq.getType() == SensorFreq.Type.comm2Activity) {
            freqValues[0] = sensorFreq.getGravFreq();
            freqValues[1] = sensorFreq.getAngFreq();
            freqValues[2] = sensorFreq.getMagFreq();
            freqValues[3] = sensorFreq.getPressureFreq();
        }
    }

/**_______________________________________↑↑↑↑__EventBus__↑↑↑↑__________________________________________________________*/

    /**
     * 显示Toolbar
     *
     * @param show true:显示,false:隐藏
     */
    public void showToolbar(boolean show) {
        if (toolbar == null) {
            Log.e("MSL", "Toolbar is null.");
        } else {
            int paddingTop = toolbar.getPaddingTop();
            int paddingBottom = toolbar.getPaddingBottom();
            int paddingLeft = toolbar.getPaddingLeft();
            int paddingRight = toolbar.getPaddingRight();
            int statusHeight = ScreenUtil.getStatusHeight(this);
            ViewGroup.LayoutParams params = toolbar.getLayoutParams();
            int height = params.height;
            /**
             * 利用状态栏的高度，4.4及以上版本给Toolbar设置一个paddingTop值为status_bar的高度，
             * Toolbar延伸到status_bar顶部
             **/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(show);
                if (show) {
                    paddingTop += statusHeight;
                    height += statusHeight;
                } else {
                    paddingTop -= statusHeight;
                    height -= statusHeight;
                }
            }
            params.height = height;
            toolbar.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            toolbar.setVisibility(show ? View.VISIBLE : GONE);
        }
    }

    /**
     * 设置透明状态栏
     * 对4.4及以上版本有效
     *
     * @param on
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initAll() {
        tv_battery.setVisibility(GONE);
        ic_battery.setVisibility(GONE);
        tv_deviceId.setText(getResources().getString(R.string.device_not_connected));
        device_info.setVisibility(GONE);
        nvg_conn.setTitle(R.string.connect);
        nvg_header_deviceId.setText(R.string.device_not_connected);

    }

    private void requestMyPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
            };
            RequestPermissionUtils.requestPermission(this, permissions, "BLE设备连接蓝牙还需要获取以下权限");
        }
    }

    private void startWriteService() {
        if (isWifiState) {
            writeServiceIntent.putExtra("net", "wifi");
            writeServiceIntent.putExtra("wifiName", WIFIUtils.getWifiId((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)));
            writeServiceIntent.putExtra("wifiMac", WIFIUtils.getWifiMAC((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)));
        } else {
            writeServiceIntent.putExtra("net", "mobile or null");
        }
        startService(writeServiceIntent);
    }

    /****************************seekBar的listener事件************************************************ */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.ss_seek_grav:
                if (!fromUser) break;
                if (sensorFreq == null) sensorFreq = new SensorFreq();
                sensorFreq.setGravFreq(progress + 1);
                break;
            case R.id.ss_seek_ang:
                if (!fromUser) break;
                if (sensorFreq == null) sensorFreq = new SensorFreq();
                sensorFreq.setAngFreq(progress + 1);
                break;
            case R.id.ss_seek_mag:
                if (!fromUser) break;
                if (sensorFreq == null) sensorFreq = new SensorFreq();
                sensorFreq.setMagFreq(progress + 1);
                break;
            case R.id.ss_seek_pressure:
                if (!fromUser) break;
                if (sensorFreq == null) sensorFreq = new SensorFreq();
                sensorFreq.setPressureFreq(progress + 1);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    /******************************seekBar的listener事件******************************************** */

    public static class NetWorkStateChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // an Intent broadcast.
            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int wifiState = intent.getIntExtra(WifiManager.WIFI_STATE_CHANGED_ACTION, 0);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_DISABLED:
                            Log.i("MSL", "onReceive: WIFI_STATE_DISABLED");
                            break;
                        case WifiManager.WIFI_STATE_DISABLING:
                            Log.i("MSL", "onReceive: WIFI_STATE_DISABLING");
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            Log.i("MSL", "onReceive: WIFI_STATE_ENABLED");
                            break;
                    }
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    Parcelable intentParcelabe = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (intentParcelabe == null) break;

                    NetworkInfo.State state = ((NetworkInfo) intentParcelabe).getState();

                    if (state == NetworkInfo.State.CONNECTED) {
                        Log.i("MSL", "onReceive: √√ wifi可用");
                        isWifiState = true;
                    } else {
                        Log.i("MSL", "onReceive: !! wifi不可用");
                        isWifiState = false;
                        EventUtil.post(new EventNotification("NET STATE", false));
                    }
                    break;
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                    if (networkInfo == null) break;
                    if (networkInfo.getState() != NetworkInfo.State.CONNECTED || !networkInfo.isAvailable())
                        break;
                    switch (networkInfo.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            Log.i("MSL", "onReceive: 连接上wifi");
                            isWifiState = true;
                            EventUtil.post(new EventNotification("NET STATE", true));
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            Log.i("MSL", "onReceive: 当前使用移动数据");
                            EventUtil.post(new EventNotification("NET STATE", true));
                            isWifiState = false;
                            break;
                        default:
                            isWifiState = false;
                            break;
                    }
                    break;
            }
        }
    }

    private void showMessageOKCancel(Activity activity, String message, DialogInterface.OnClickListener okListener) {
        AlertDialog dlgShowBack = new AlertDialog.Builder(activity, R.style.MyDialogStyle)
                .setMessage(message)
                .setPositiveButton("ok", okListener)
                .setNegativeButton("cancel", null)
                .create();
        dlgShowBack.show();
        Button btnPositive = dlgShowBack.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = dlgShowBack.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

        btnNegative.setTextColor(getResources().getColor(R.color.textColorPrimary));
//        btnNegative.setBackground(getResources().getDrawable(R.drawable.btn_selector_dialog));

        btnPositive.setTextColor(getResources().getColor(R.color.textColorPrimary_alpha));
//        btnPositive.setBackground(getResources().getDrawable(R.drawable.btn_selector_dialog));

        TextView tvMsg = (TextView) dlgShowBack.findViewById(android.R.id.message);
        tvMsg.setTextColor(getResources().getColor(R.color.textColorPrimary));
//        tvMsg.setTextAppearance(this,R.style.text_msg);
//        tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP , 10);
    }

    //Navigation Menu中 Sensor Setting 点击后的dialog界面
    private void showDialogForSeekBar(Activity activity) {
        sensorFreq = new SensorFreq();
        sensorFreq.setType(SensorFreq.Type.comm2Gatt);
        LinearLayout ll = (LinearLayout) getLayoutInflater().inflate
                (R.layout.sensor_setting, (ViewGroup) findViewById(R.id.sensor_setting_ll));
        sb_grav = (SeekBar) ll.findViewById(R.id.ss_seek_grav);
        sb_ang = (SeekBar) ll.findViewById(R.id.ss_seek_ang);
        sb_mag = (SeekBar) ll.findViewById(R.id.ss_seek_mag);
        sb_pressure = (SeekBar) ll.findViewById(R.id.ss_seek_pressure);

        sb_grav.setProgress(freqValues[0]);
        sb_ang.setProgress(freqValues[1]);
        sb_mag.setProgress(freqValues[2]);
        sb_pressure.setProgress(freqValues[3]);

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("设定采样频率")
                .setMessage("可选范围： 1HZ ~ 0.1HZ")
                .setIcon(R.drawable.ic_settings_sensor_24dp)
                .setView(ll)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (sensorFreq != null) {
                            if (sensorFreq.getGravFreq() == 0)
                                sensorFreq.setGravFreq(freqValues[0]);
                            if (sensorFreq.getAngFreq() == 0)
                                sensorFreq.setAngFreq(freqValues[1]);
                            if (sensorFreq.getMagFreq() == 0)
                                sensorFreq.setMagFreq(freqValues[2]);
                            if (sensorFreq.getPressureFreq() == 0)
                                sensorFreq.setPressureFreq(freqValues[3]);
                            EventUtil.post(sensorFreq);
                            Log.i("MSL", "onClick: \n" + sensorFreq.getGravFreq() + "\n" + sensorFreq.getAngFreq()
                                    + "\n" + sensorFreq.getMagFreq() + "\n" + sensorFreq.getPressureFreq());
                            dialog = null;
                        }
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        dialog.show();
        sb_grav.setOnSeekBarChangeListener(this);
        sb_ang.setOnSeekBarChangeListener(this);
        sb_mag.setOnSeekBarChangeListener(this);
        sb_pressure.setOnSeekBarChangeListener(this);

    }

    private void showDeviceChooseDialog(Activity activity) {
        if (devicelistDialog != null) return;
        devicelistDialog = new AlertDialog.Builder(activity)
                .setTitle("扫描到的蓝牙设备")
                .setPositiveButton("停止", null)
                .setNegativeButton("cancel", null)
                .setView(recyclerView)
                .create();
        devicelistDialog.show();

        final Button btnPositive = devicelistDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnPositive.getText().equals("扫描")) {
                    btnPositive.setText("停止");
                    EventUtil.post(Comm2GATT.TYPE.START_SCAN);

                } else if (btnPositive.getText().equals("停止")) {
                    btnPositive.setText("扫描");
                    EventUtil.post(Comm2GATT.TYPE.STOP_SCAN);
                }
            }
        });
    }
}
