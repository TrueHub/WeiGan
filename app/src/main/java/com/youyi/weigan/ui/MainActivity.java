package com.youyi.weigan.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.youyi.weigan.R;
import com.youyi.weigan.beans.AngV;
import com.youyi.weigan.beans.DeviceStatusBean;
import com.youyi.weigan.beans.GravA;
import com.youyi.weigan.beans.Mag;
import com.youyi.weigan.beans.Pressure;
import com.youyi.weigan.beans.Pulse;
import com.youyi.weigan.beans.UserBean;
import com.youyi.weigan.eventbean.Comm2Frags;
import com.youyi.weigan.eventbean.Comm2GATT;
import com.youyi.weigan.eventbean.EventNotification;
import com.youyi.weigan.moudul.ControlDeviceImp;
import com.youyi.weigan.service.GATTService;
import com.youyi.weigan.service.WriteService;
import com.youyi.weigan.utils.DateUtils;
import com.youyi.weigan.utils.EventUtil;
import com.youyi.weigan.utils.RequestPermissionUtils;
import com.youyi.weigan.utils.ScreenUtil;
import com.youyi.weigan.utils.ServiceUtils;
import com.youyi.weigan.utils.WIFIUtils;
import com.youyi.weigan.view.BattaryView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static android.view.View.GONE;
import static com.youyi.weigan.eventbean.Comm2GATT.TYPE.REAL_PULSE_OFF;
import static com.youyi.weigan.eventbean.Comm2GATT.TYPE.REAL_PULSE_ON;
import static com.youyi.weigan.eventbean.Comm2GATT.TYPE.SEARCH_DEVICE_STATUE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FrameLayout frame_content;
    private CoordinatorLayout main_content;
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
    //侧滑菜单中的real data 组中的switch
    private Switch sw_heartRate_real;

    private ControlDeviceImp controlDeviceImp;

    private Toast toast;
    private boolean isStarted;
    private Intent gattService;
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
    private UserBean userBean;
    private Intent writeServiceIntent;
    private int LIST_SIZE = 1000;
    private boolean getDataEnd;//接收完数据，将小于100的list也存储和上传
    private static boolean isWifiState;
    private CollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initNavigationItem();

        requestMyPermissions();

        EventUtil.register(this);
        writeServiceIntent = new Intent(this, WriteService.class);

        if (userBean == null) {
            userBean = UserBean.getInstence();
        }

        toast = Toast.makeText(this, "再次点击退出程序", Toast.LENGTH_SHORT);

        controlDeviceImp = new ControlDeviceImp(this);

        if (WIFIUtils.isWifiType(this)) {
            isWifiState = true;
            controlDeviceImp.showToast("当前连接：" + WIFIUtils.getWifiId((WifiManager) getSystemService(Context.WIFI_SERVICE)));
        }

        setActionBar();


        setNavigationItemClickListener();

        initFragments();


    }

    /**
     * 初始画navigation中的各个可点击的item，层级比较多
     */
    private void initNavigationItem() {

        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            MenuItem root1 = navigationView.getMenu().getItem(i);//第一层级的item
            switch (root1.getItemId()) {//
                //conn按钮
                case R.id.navigation_conn:
                    nvg_conn = root1;
                    break;
                //获取设备状态
                case R.id.navigation_check_status:
                    nvg_chk_status = root1;
                    break;
                //收集数据
                case R.id.navigation_collect_data:
                    nvg_collect_data = root1;
                    break;
                //上传本地数据
                case R.id.navigation_upload_data:
                    nvg_upload_data = root1;
                    break;
                //清除外接设备的缓存数据
                case R.id.navigation_clear_flash:
                    nvg_clear_flash = root1;
                    break;
                //删除本地缓存数据
                case R.id.navigation_clear_data:
                    nvg_del_cache = root1;
                    break;

                //real这一组
                case R.id.navigation_real_group:
                    Log.i("MSL", "initNavigationItem: " + root1.getSubMenu().size());
                    //遍历real group 的item下的menu item。。。
                    for (int j = 0; j < root1.getSubMenu().size(); j++) {
                        MenuItem v = root1.getSubMenu().getItem(j);
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
                            case R.id.navigation_realTime_pressure:

                                break;
                            case R.id.navigation_realTime_gravA:

                                break;
                            case R.id.navigation_realTime_angV:

                                break;
                            case R.id.navigation_realTime_mag:

                                break;
                        }
                    }
                    break;
                //传感器设置一组
                case R.id.navigation_sensor_setting:
                    for (int j = 0; j < root1.getSubMenu().size(); j++) {
                        MenuItem v = root1.getSubMenu().getItem(j);
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
            }

        }


    }

    private void initFragments() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        ContentFragment contentFragment = new ContentFragment();

        transaction.add(R.id.frame_content, contentFragment).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();

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
        if (toast.getView().getParent() == null) {
            toast.show();

        } else {
            toast.cancel();
            super.onBackPressed();
        }
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
                Log.i("MSL", "onOptionsItemSelected: screenshot ");
                break;
            case R.id.appbar_share:
                Log.i("MSL", "onOptionsItemSelected: share");
                break;
        }

        return super.onOptionsItemSelected(item);
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
                                EventUtil.post(Comm2GATT.TYPE.START_CONNECT);
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
                        Log.d("MSL", "onClick: 网络是否wifi状态：" + isWifiState);

                        startWriteService();

                        getDataEnd = false;

                        EventUtil.post(Comm2GATT.TYPE.SEARCH_HIS);
                        break;
                    case R.id.navigation_upload_data:

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

                        break;
                    case R.id.navigation_realTime_heartRate:
                        sw_heartRate_real.setChecked(!sw_heartRate_real.isChecked());

                        break;
                    case R.id.navigation_realTime_pressure:

                        break;
                    case R.id.navigation_realTime_gravA:

                        break;
                    case R.id.navigation_realTime_angV:

                        break;
                    case R.id.navigation_realTime_mag:

                        break;
                    case R.id.navigation_sensor_setting:
                        Log.d("MSL", "onNavigationItemSelected: sensor");
                        break;
                    case R.id.navigation_vib_low:

                        break;
                    case R.id.navigation_vib_up:

                        break;

                }
                return false;
            }
        });
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

        ic_battery.setOnClickListener(this);
        device_info = (LinearLayout) findViewById(R.id.device_info);
        info_tv_chk_time = (TextView) device_info.findViewById(R.id.tv_last_check_time_info);
        info_tv_battery = (TextView) device_info.findViewById(R.id.tv_battery_info);
        info_tv_vib_low = (TextView) device_info.findViewById(R.id.tv_vib_low_info);
        info_tv_vib_high = (TextView) device_info.findViewById(R.id.tv_vib_high_info);
        info_tv_sensor_freq = (TextView) device_info.findViewById(R.id.sensor_freq_info);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    /**
     * _______________________________________↓↓↓↓EventBus↓↓↓↓_________________________________________________________
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void getGATTCallback(DeviceStatusBean deviceStatusBean) {
        //设备返回状态值
        final int[] time = {deviceStatusBean.getTime()};
        Log.i("MSL", "getBluetoothHelathCallback: " + (long) time[0] * 1000 + "," + time[0]);
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
        info_tv_chk_time.setText(DateUtils.getDateToString((long) time[0] * 1000));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDataOver(EventNotification eventNotification) {
        switch (eventNotification.getType()) {
            case "HIS_DATA":
                getDataEnd = eventNotification.isGetOver();
                break;
            case GATTService.DEVICE_ID:
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
            case "NET STATE":
                startWriteService();
                break;
        }

    }

/**_______________________________________↑↑↑↑EventBus↑↑↑↑_________________________________________________________*/

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
            String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            RequestPermissionUtils.requestPermission(this, permissions, "BLE设备连接蓝牙还需要获取以下权限");
        }
    }

    private void startWriteService() {
        if (isWifiState) {
            writeServiceIntent.putExtra("net", "wifi");
            writeServiceIntent.putExtra("wifiName", WIFIUtils.getWifiId((WifiManager) getSystemService(Context.WIFI_SERVICE)));
            writeServiceIntent.putExtra("wifiMac", WIFIUtils.getWifiMAC((WifiManager) getSystemService(Context.WIFI_SERVICE)));
        } else {
            writeServiceIntent.putExtra("net", "mobile or null");
        }
        startService(writeServiceIntent);
    }

    private class NetWorkStateChangedReceiver extends BroadcastReceiver {

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
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("已经接收完了", okListener)
                .setNegativeButton("暂时不删", null)
                .create()
                .show();
    }
}
