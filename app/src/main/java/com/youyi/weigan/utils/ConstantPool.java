package com.youyi.weigan.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.youyi.weigan.R;

/**
 * Created by user on 2017/4/6.
 * 有关设备的常量池
 */

public class ConstantPool {

    //BluetoothProfile
    public static final  java.util.UUID UUID_WRITE  = java.util.UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");//weigan的
    public static final  java.util.UUID UUID_NOTIFY  = java.util.UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");//weigan的

    //instruct
    public static final byte HEAD = (byte) 0xAA;
    public static final byte END = (byte)0x55;
    public static final byte INSTRUCT_SEARCH_TIME = (byte)0x00;
    public static final byte INSTRUCT_SET_TIME = (byte)0x01;
    public static final byte INSTRUCT_SEARCH_PULSE = (byte)0x02;
    public static final byte INSTRUCT_HIS = (byte)0x03;
    public static final byte INSTRUCT_SEARCH_AOG_HIS = (byte)0x04;
    public static final byte INSTRUCT_SEARCH_PALSTANCE = (byte)0x05;
    public static final byte INSTRUCT_SEARCH_MAGNETISM = (byte)0x06;
    public static final byte INSTRUCT_SEARCH_PRESSURE = (byte)0x07;
    public static final byte INSTRUCT_DELETE_FLASH = (byte)0xFE;

    public static final byte[] SEARCH_DEVICE_TIME = new byte[]
            {HEAD,(byte)0x02, INSTRUCT_SEARCH_TIME, END};//查询设备时间

    public static final byte[] PULSE_UP_ON = new byte[]
        {HEAD,(byte)0x03, INSTRUCT_SEARCH_PULSE, (byte) 0x01,END};//实时心率上传开关 PULSE_UP_ON[3]：0x01:on || 0x00:off

    public static final byte[] PULSE_UP_OFF = new byte[]
            {HEAD,(byte)0x03, INSTRUCT_SEARCH_PULSE,(byte) 0x00, END};//实时心率上传开关 PULSE_UP_ON[2]：0x01:on || 0x00:off

    public static final byte[] SEARCH_HIS = new byte[]
            {HEAD,(byte)0x02, INSTRUCT_HIS, END};//查询历史数据

    public static final byte[] DELETE_FLASH = new byte[]
            {HEAD,(byte)0x02,INSTRUCT_DELETE_FLASH,END};//清缓存

    public static final String DEVICEID_1 = "Nordic-9FBEE5315";
    public static final String DEVICEID_2 = "Nordic-98FD564AC";
    public static final String DEVICEID_3 = "Nordic-FC0942090";
    public static final String DEVICEID_4 = "Nordic-BE766AA9B";

    public static final String debugWifiName = "linux.utang.cn-puppet";
    public static final String debugWifiMac = "04:bd:70:da:1b:c0";
    public static final String debugWifiMac2 = "04:bd:70:da:1b:d0";

    public static final String URL_DEBUG_LAN = "http://192.168.0.141:8080/";
    public static final String URL_DEBUG_WLAN = "http://116.236.215.26:8000/";


}
