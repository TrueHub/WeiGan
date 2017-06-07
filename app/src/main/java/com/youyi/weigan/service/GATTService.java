package com.youyi.weigan.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.youyi.weigan.beans.AngV;
import com.youyi.weigan.beans.DeviceStatusBean;
import com.youyi.weigan.beans.GravA;
import com.youyi.weigan.beans.Mag;
import com.youyi.weigan.beans.Pressure;
import com.youyi.weigan.beans.Pulse;
import com.youyi.weigan.beans.SensorFreq;
import com.youyi.weigan.eventbean.Comm2GATT;
import com.youyi.weigan.eventbean.EventNotification;
import com.youyi.weigan.eventbean.Event_BleDevice;
import com.youyi.weigan.thread.CommandPool;
import com.youyi.weigan.utils.ConstantPool;
import com.youyi.weigan.utils.DataUtils;
import com.youyi.weigan.utils.EventUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import static com.youyi.weigan.utils.ConstantPool.INSTRUCT_HEART_RATE_HIS;
import static com.youyi.weigan.utils.ConstantPool.INSTRUCT_SEARCH_ANGV;
import static com.youyi.weigan.utils.ConstantPool.INSTRUCT_SEARCH_GRAV_HIS;
import static com.youyi.weigan.utils.ConstantPool.INSTRUCT_SEARCH_MAG;
import static com.youyi.weigan.utils.ConstantPool.INSTRUCT_SEARCH_PRESSURE;
import static com.youyi.weigan.utils.ConstantPool.INSTRUCT_SEARCH_PULSE;
import static com.youyi.weigan.utils.ConstantPool.INSTRUCT_SEARCH_TIME;
import static com.youyi.weigan.utils.ConstantPool.INSTRUCT_SET_SENSOR_FREQ;
import static com.youyi.weigan.utils.ConstantPool.INSTRUCT_SET_TIME;
import static com.youyi.weigan.utils.DateUtils.currentTimeSec;

/**
 * Created by Dell on 2017-4-16.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class GATTService extends Service {

    public static String DEVICE_ID = ConstantPool.DEVICEID_4;
    private BluetoothAdapter mBluetoothAdapter;
    private LeScanCallback_LOLLIPOP mScanCallBack_lollipop;//5.0以上
    private LeScanCallback_JELLY_BEAN mScanCallBack_jelly;//4.3以上
    private BluetoothLeScanner mBluetoothScanner;
    private BluetoothDevice mTarget;
    private CommandPool commandPool;
    private BLEGATTCallBack mGattCallback;
    private Handler handler;
    private boolean mScanning;
    private BluetoothGattCharacteristic vibrationChar;
    private boolean isConnected = false;
    private BluetoothGatt mGatt;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventUtil.unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        EventUtil.register(this);
        handler = new Handler();
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        searchDevice();
    }

    private void searchDevice() {
        Log.i("MSL", "searchDevice: method running");

        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            return;
        }
        if (mGattCallback == null) mGattCallback = new BLEGATTCallBack();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上
//            if (mScanCallBack_lollipop == null)
            mScanCallBack_lollipop = new LeScanCallback_LOLLIPOP();
            mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mBluetoothScanner.startScan(mScanCallBack_lollipop);
        } else {//4.3 ~ 5.0
//            if (mScanCallBack_lollipop == null)
            mScanCallBack_jelly = new LeScanCallback_JELLY_BEAN();
            mBluetoothAdapter.startLeScan(mScanCallBack_jelly);

        }

        mScanning = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mScanning) {
                    Log.d("MSL", "Stop Scan， Time Out");
                    mScanning = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mBluetoothScanner.stopScan(mScanCallBack_lollipop);
                    } else {
                        mBluetoothAdapter.stopLeScan(mScanCallBack_jelly);
                    }
                }
            }
        }, 1000 * 10);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class LeScanCallback_LOLLIPOP extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result == null) {
                return;
            }
            Log.i("MSL", "onScanResult: 扫描到设备：" + result.getDevice().getName() + "\n" + result.getDevice().getAddress());

            EventUtil.post(new Event_BleDevice(result.getDevice(), Event_BleDevice.From.Gatt));

        }
    }

    private class LeScanCallback_JELLY_BEAN implements BluetoothAdapter.LeScanCallback {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.i("MSL", "onScanResult: JELLY_BEAN 扫描到设备：" + device.getName() + "\n" + device.getAddress());

            EventUtil.post(new Event_BleDevice(device, Event_BleDevice.From.Gatt));

        }
    }

    private void stopScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothScanner.stopScan(mScanCallBack_lollipop);
        } else
            mBluetoothAdapter.stopLeScan(mScanCallBack_jelly);
    }

    private void connect() {
        if (!isConnected) {
            mTarget.connectGatt(GATTService.this, false, mGattCallback);
            isConnected = true;
        }
        if (mBluetoothAdapter.getBondedDevices().contains(mTarget)) {
            EventUtil.post("目标设备已配对");
        }
    }

    private class BLEGATTCallBack extends BluetoothGattCallback {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            mGatt = gatt;
            if (status == 0) {
                mGatt.discoverServices();
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //连上了新设备
                commandPool = new CommandPool(GATTService.this, gatt);
                new Thread(commandPool).start();
                Log.i("MSL", "Connected to GATT server 连接成功");
                stopScan();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //设备断开
                EventUtil.post(new EventNotification(DEVICE_ID, false));
                Log.i("MSL", "Disconnected from GATT server");
                mGatt.close();
                stopSelf();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> serviceList;
            //发现新的设备
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("MSL", "onServicesDiscovered: 发现新的设备");
            }
            serviceList = gatt.getServices();
            if (serviceList != null) {
                Log.i("MSL", "onServicesDiscovered: " + serviceList);
                Log.i("MSL", "serviceList NUM ： " + serviceList.size());
                for (BluetoothGattService bleService : serviceList) {
                    List<BluetoothGattCharacteristic> characteristicList = bleService.getCharacteristics();
                    Log.i("MSL", "扫描到Service: " + bleService.getUuid());
                    for (BluetoothGattCharacteristic characteristic :
                            characteristicList) {
                        Log.i("MSL", "characteristic: " + characteristic.getUuid() + "\n" + characteristic.getProperties());
                        if (characteristic.getUuid().equals(ConstantPool.UUID_NOTIFY)) {
                            Log.i("MSL", "onServicesDiscovered: " + characteristic.getUuid());
                            gatt.setCharacteristicNotification(characteristic, true);
                            List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
                            for (BluetoothGattDescriptor descriptor :
                                    descriptorList) {
                                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                gatt.writeDescriptor(descriptor);
                            }
//                            descriptorList.get(0).setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                            gatt.writeDescriptor(descriptorList.get(0));
                        }
                        if (characteristic.getUuid().equals(ConstantPool.UUID_WRITE)) {
                            vibrationChar = characteristic;
                            EventUtil.post(new EventNotification(DEVICE_ID, true));
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            commandPool.onCommandCallbackComplete();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            Log.d("MSL", "onCharacteristicWrite: " + status);
            commandPool.onCommandCallbackComplete();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (characteristic.getUuid().equals(ConstantPool.UUID_NOTIFY)) {
                commandPool.onCommandCallbackComplete();
                byte[] data = characteristic.getValue();
                Log.d("MSL", "onCharacteristicChanged: " + DataUtils.bytes2hex(data));
                readData(data);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

            Log.d("MSL", "onDescriptorRead: " + DataUtils.bytes2hex(descriptor.getValue()));
            commandPool.onCommandCallbackComplete();
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d("MSL", "onDescriptorWrite: ");
            commandPool.onCommandCallbackComplete();
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            commandPool.onCommandCallbackComplete();
            Log.d("MSL", "onReliableWriteCompleted: ");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            commandPool.onCommandCallbackComplete();
            Log.d("MSL", "onReadRemoteRssi: ");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            commandPool.onCommandCallbackComplete();
            Log.d("MSL", "onMtuChanged: ");
        }
    }

    /**
     * ________↓↓_______MainActivity的btn控制这里_____________↓↓↓↓_eventBus_↓↓↓↓↓___________________________________________________
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void btnClick(Comm2GATT.TYPE type) {
        if (vibrationChar == null) return;
        switch (type) {
            case SEARCH_DEVICE_STATUE:
                commandPool.addCommand(CommandPool.Type.write, ConstantPool.SEARCH_DEVICE_STATUES, vibrationChar);
                break;
            case REAL_DATA_ON:
                commandPool.addCommand(CommandPool.Type.write, ConstantPool.REAL_SENSOR_DATA_ON, vibrationChar);
                break;
            case REAL_DATA_OFF:
                commandPool.addCommand(CommandPool.Type.write, ConstantPool.REAL_SENSOR_DATA_OFF, vibrationChar);
                break;
            case REAL_PULSE_ON:
                commandPool.addCommand(CommandPool.Type.write, ConstantPool.PULSE_UP_ON, vibrationChar);
                break;
            case REAL_PULSE_OFF:
                commandPool.addCommand(CommandPool.Type.write, ConstantPool.PULSE_UP_OFF, vibrationChar);
                break;
            case SEARCH_HIS:
                commandPool.addCommand(CommandPool.Type.write, ConstantPool.SEARCH_HIS, vibrationChar);
                break;
            case START_SCAN:
                Log.i("MSL", "gatt btnClick: start scan");
                searchDevice();
                break;
            case STOP_SCAN:
                Log.i("MSL", "gatt btnClick: stop scan");
                stopScan();
                break;
            case STOP_GATT_SERVICE:
                EventUtil.post("断开GATT连接");
                EventUtil.post(new EventNotification(DEVICE_ID, false));
//                Log.i("MSL", "Disconnected from GATT server");
                mGatt.disconnect();
                break;
            case CLEAR_FLASH:
                Log.i("MSL", "指令：清除设备的flash缓存");
                commandPool.addCommand(CommandPool.Type.write, ConstantPool.DELETE_FLASH, vibrationChar);
                EventUtil.post(new EventNotification("HIS_DATA", true));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void connectedDevice(Event_BleDevice event_bleDevice) {
        if (event_bleDevice.getFrom() == Event_BleDevice.From.Gatt) return;
        mTarget = event_bleDevice.getDevice();
        DEVICE_ID = mTarget.getName();
        connect();
    }

    /**
     * 设置传感器的频率
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sensorSetting(SensorFreq sensorFreq) {
        if (sensorFreq.getType() == SensorFreq.Type.comm2Gatt) {
            byte[] freq = ConstantPool.SET_SENSOR_FREQ;
            freq[3] = DataUtils.int2OneByte(sensorFreq.getGravFreq());
            freq[4] = DataUtils.int2OneByte(sensorFreq.getAngFreq());
            freq[5] = DataUtils.int2OneByte(sensorFreq.getMagFreq());
            freq[6] = DataUtils.int2OneByte(sensorFreq.getPressureFreq());
            commandPool.addCommand(CommandPool.Type.write, freq, vibrationChar);
        }
    }

    /**
     * _________________________________________________________________________________________________________
     */

    public void readData(byte[] data) throws NullPointerException {
        int length = DataUtils.byte2Int(data[1]);//设备返回的数据长度
        if (data[2] == INSTRUCT_SET_TIME) {//返回：设定时间成功
            if (data[3] == (byte) 0x01) {
                EventUtil.post("SET_TIME_SUCCESS!");
//                commandPool.addCommand(CommandPool.Type.write, ConstantPool.SEARCH_DEVICE_STATUES, vibrationChar);
            }
        } else if (data[2] == INSTRUCT_SET_SENSOR_FREQ && data[1] == (byte) 0x06) {
            //返回各传感器的采样频率
            SensorFreq sensorFreq = new SensorFreq();
            sensorFreq.setGravFreq(DataUtils.Byte2Int(data[3]));
            sensorFreq.setAngFreq(DataUtils.Byte2Int(data[4]));
            sensorFreq.setMagFreq(DataUtils.Byte2Int(data[5]));
            sensorFreq.setPressureFreq(DataUtils.Byte2Int(data[6]));
            sensorFreq.setType(SensorFreq.Type.comm2Activity);
            EventUtil.post(sensorFreq);

        } else if (data[2] == INSTRUCT_SEARCH_PULSE && data[1] == (byte) 0x02) {
            EventUtil.post("开关实时心率成功");

        } else if (data[2] == INSTRUCT_SEARCH_TIME) {
            int timeInt = getTimeInt(data);
            byte[] datas = new byte[length - 6];
            System.arraycopy(data, 7, datas, 0, datas.length);

            DeviceStatusBean deviceStatusBean = new DeviceStatusBean();
            deviceStatusBean.setTime(timeInt);
            deviceStatusBean.setDeviceElec(datas[0]);
            EventUtil.post(deviceStatusBean);
            if (!needSetTime(timeInt)) {
                EventUtil.post("设备与本地时间无误差");
            } else {
                Log.i("MSL", "readData: set time");
                writeTime();
            }
        } else if (data[2] == INSTRUCT_HEART_RATE_HIS) {
            int timeInt = getTimeInt(data);
            byte[] datas = new byte[length - 6];
            System.arraycopy(data, 7, datas, 0, datas.length);

            Pulse pulse = new Pulse();
            pulse.setTime(timeInt);
            pulse.setPulse(DataUtils.byte2Int(datas[0]));
            pulse.setTrustLevel(DataUtils.byte2Int(datas[1]));
            EventUtil.post(pulse);
        } else if (data[2] == INSTRUCT_SEARCH_GRAV_HIS) {
            GravA mGravA = new GravA();
            if (data[1] == 0x0C) {

                int timeInt = getTimeInt(data);
                byte[] datas = new byte[length - 6];
                System.arraycopy(data, 7, datas, 0, datas.length);
                mGravA.setTime(timeInt);
                mGravA.setVelX(DataUtils.bytes2IntSigned(new byte[]{datas[0], datas[1]}));
                mGravA.setVelY(DataUtils.bytes2IntSigned(new byte[]{datas[2], datas[3]}));
                mGravA.setVelZ(DataUtils.bytes2IntSigned(new byte[]{datas[4], datas[5]}));
            } else if (data[1] == 0x08) {
                byte[] datas = new byte[length - 2];
                System.arraycopy(data, 3, datas, 0, datas.length);
                mGravA.setVelX(DataUtils.bytes2IntSigned(new byte[]{datas[0], datas[1]}));
                mGravA.setVelY(DataUtils.bytes2IntSigned(new byte[]{datas[2], datas[3]}));
                mGravA.setVelZ(DataUtils.bytes2IntSigned(new byte[]{datas[4], datas[5]}));
            }

            EventUtil.post(mGravA);
        } else if (data[2] == INSTRUCT_SEARCH_ANGV) {
            AngV angV = new AngV();
            byte[] datas;
            if (data[1] == 0x0C) {

                int timeInt = getTimeInt(data);
                datas = new byte[length - 6];
                System.arraycopy(data, 7, datas, 0, datas.length);
                angV.setTime(timeInt);
            } else {
                datas = new byte[length - 2];
                System.arraycopy(data, 3, datas, 0, datas.length);
            }
            angV.setVelX(DataUtils.bytes2IntSigned(new byte[]{datas[0], datas[1]}));
            angV.setVelY(DataUtils.bytes2IntSigned(new byte[]{datas[2], datas[3]}));
            angV.setVelZ(DataUtils.bytes2IntSigned(new byte[]{datas[4], datas[5]}));
            EventUtil.post(angV);
        } else if (data[2] == INSTRUCT_SEARCH_MAG) {
            Mag mag = new Mag();
            byte[] datas;
            if (data[1] == 0x0C) {

                int timeInt = getTimeInt(data);
                datas = new byte[length - 6];
                System.arraycopy(data, 7, datas, 0, datas.length);
                mag.setTime(timeInt);
            } else {
                datas = new byte[length - 2];
                System.arraycopy(data, 3, datas, 0, datas.length);
            }
            mag.setStrengthX(DataUtils.bytes2IntSigned(new byte[]{datas[0], datas[1]}));
            mag.setStrengthY(DataUtils.bytes2IntSigned(new byte[]{datas[2], datas[3]}));
            mag.setStrengthZ(DataUtils.bytes2IntSigned(new byte[]{datas[4], datas[5]}));
            EventUtil.post(mag);
            Log.i("MSL", "readData: mag" + mag.getTime());
        } else if (data[2] == INSTRUCT_SEARCH_PRESSURE) {
            Pressure pressure = new Pressure();
            byte[] datas;
            if (data[1] == 0x0A) {
                int timeInt = getTimeInt(data);
                datas = new byte[length - 6];
                System.arraycopy(data, 7, datas, 0, datas.length);
                pressure.setTime(timeInt);
            }else {
                datas = new byte[length - 2];
                System.arraycopy(data, 3, datas, 0, datas.length);
            }
            pressure.setIntensityOfPressure(DataUtils.bytes2Long(datas));
            EventUtil.post(pressure);
        }
    }

    private int getTimeInt(byte[] data) {
        byte[] timeBytes = new byte[4];//时间数组
        System.arraycopy(data, 3, timeBytes, 0, timeBytes.length);
        return DataUtils.bytes2IntUnsigned(timeBytes);//这里的timeInt是100ms级别的
    }

    private void writeTime() {
        byte[] currentTimeBytes = DataUtils.int2Bytes(currentTimeSec());//需要发送的时间戳的长度
//        Log.d("MSL", "writeTime: " + currentTimeBytes.length + "," + currentTimeBytes[0] + "," + currentTimeBytes[1] + "," + currentTimeBytes[2] + "," + currentTimeBytes[3]);
        byte[] setTimeBytes = new byte[8];//整条指令的长度
        setTimeBytes[0] = ConstantPool.HEAD;
        setTimeBytes[1] = (byte) 0x06;
        setTimeBytes[2] = (byte) 0x01;
        System.arraycopy(currentTimeBytes, 0, setTimeBytes, 3, currentTimeBytes.length);
        setTimeBytes[setTimeBytes.length - 1] = ConstantPool.END;
        commandPool.addCommand(CommandPool.Type.write, setTimeBytes, vibrationChar);
    }

    private boolean needSetTime(int time) {
        int current = (int) (System.currentTimeMillis() / 10);
//        Log.d("MSL", "needSetTime: " + current + "," + time);
        return Math.abs(time - current) >= 1;
    }

}
