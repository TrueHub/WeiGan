package com.youyi.weigan.utils;

/**
 * Created by user on 2017/4/6.
 * 有关设备的常量池
 */

public class ConstantPool {

    /**BluetoothProfile*/
    public static final  java.util.UUID UUID_WRITE  = java.util.UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");//weigan的
    public static final  java.util.UUID UUID_NOTIFY  = java.util.UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");//weigan的

    /**区分走路跑步标准差阈值*/
    public static final double ANG_X = 3356.797 ;
    public static final double ANG_Y = 1439.458 ;
    public static final double ANG_Z = 1149.736 ;

    /**instruct*/
    public static final byte HEAD = (byte) 0xAA;
    public static final byte END = (byte)0x55;
    public static final byte INSTRUCT_SEARCH_TIME = (byte)0x00;
    public static final byte INSTRUCT_SET_TIME = (byte)0x01;
    public static final byte INSTRUCT_REAL_SENSOR_DATA = (byte)0x02;
    public static final byte INSTRUCT_SEARCH_PULSE = (byte)0x05;
    public static final byte INSTRUCT_HEART_RATE_HIS = (byte)0x03;
    public static final byte INSTRUCT_SEARCH_GRAV_HIS = (byte)0x04;
    public static final byte INSTRUCT_SET_SENSOR_FREQ = (byte)0x04;
    public static final byte INSTRUCT_SEARCH_ANGV = (byte)0x05;
    public static final byte INSTRUCT_SEARCH_MAG = (byte)0x06;
    public static final byte INSTRUCT_SEARCH_PRESSURE = (byte)0x07;
    public static final byte INSTRUCT_DELETE_FLASH = (byte)0xFE;
    public static final byte INSTRUCT_SEARCH_SENSOR_FREQ = (byte)0xFF;

    /** 查询设备时间 */
    public static final byte[] SEARCH_DEVICE_STATUES = new byte[]
            {HEAD,(byte)0x02, INSTRUCT_SEARCH_TIME, END};//

    /** 实时数据上传   开*/
    public static final byte[] REAL_SENSOR_DATA_ON = new byte[]{HEAD,
            (byte)0x03,INSTRUCT_REAL_SENSOR_DATA ,(byte) 0x01,END};//
    /** 实时数据上传   关*/
    public static final byte[] REAL_SENSOR_DATA_OFF = new byte[]{HEAD,
            (byte)0x03,INSTRUCT_REAL_SENSOR_DATA,(byte)0x00,END};//关闭实时数据上传

    /** 实时心率上传   开*/
    public static final byte[] PULSE_UP_ON = new byte[]
        {HEAD,(byte)0x03, INSTRUCT_SEARCH_PULSE, (byte) 0x01,END};// PULSE_UP_ON[3]：0x01:on || 0x00:off

    /** 实时心率上传   关*/
    public static final byte[] PULSE_UP_OFF = new byte[]
            {HEAD,(byte)0x03, INSTRUCT_SEARCH_PULSE,(byte) 0x00, END};// PULSE_UP_ON[2]：0x01:on || 0x00:off

    /** 接收历史数据   增量 */
    public static final byte[] SEARCH_HIS = new byte[]
            {HEAD,(byte)0x03, INSTRUCT_HEART_RATE_HIS,(byte)0x01 , END};

    /** 接收历史数据   全部 */
    public static final byte[] SEARCH_HIS_ALL = new byte[]
            {HEAD,(byte)0x03, INSTRUCT_HEART_RATE_HIS,(byte)0x00 , END};

    /** 设定采样频率 */
    public static final byte[] SET_SENSOR_FREQ = new byte[]{
            HEAD,(byte)0x06, INSTRUCT_SET_SENSOR_FREQ,
            /** 重力加速度 */    INSTRUCT_SEARCH_SENSOR_FREQ ,
            /** 角速度 */        INSTRUCT_SEARCH_SENSOR_FREQ ,
            /** 磁场强度 */      INSTRUCT_SEARCH_SENSOR_FREQ ,
            /** 气压强 */        INSTRUCT_SEARCH_SENSOR_FREQ ,
            END
    };

    /** 查询采样频率 */
    public static final byte[] SEARCH_SENSOR_FREQ = new byte[]{
            HEAD,(byte)0x06, INSTRUCT_SET_SENSOR_FREQ,
            /** 重力加速度 */    INSTRUCT_SEARCH_SENSOR_FREQ ,
            /** 角速度 */        INSTRUCT_SEARCH_SENSOR_FREQ ,
            /** 磁场强度 */      INSTRUCT_SEARCH_SENSOR_FREQ ,
            /** 气压强 */        INSTRUCT_SEARCH_SENSOR_FREQ ,
            END
    };

    /** 清闪存 */
    public static final byte[] DELETE_FLASH = new byte[]
            {HEAD,(byte)0x02,INSTRUCT_DELETE_FLASH,END};//

    public static final String DEVICEID_1 = "Nordic-9FBEE5315";
    public static final String DEVICEID_2 = "Nordic-98FD564AC";
    public static final String DEVICEID_3 = "Nordic-FC0942090";
    public static final String DEVICEID_4 = "Nordic-BE766AA9B";

    public static final String debugWifiName = "linux.utang.cn-puppet";
    public static final String debugWifiMac = "04:bd:70:da:1b:c0";
    public static final String debugWifiMac2 = "04:bd:70:da:1b:d0";

    public static final String URL_DEBUG_LAN = "http://192.168.0.169:8080/";
    public static final String URL_DEBUG_WLAN = "http://116.236.215.26:8000/";


}
