package com.youyi.weigan.eventbean;

/**
 * Created by user on 2017/5/17.
 */

public class Comm2GATT {

    public enum TYPE{
        /** 查询设备状态 */
        SEARCH_DEVICE_STATUE,

        /** 实时心率开 */
        REAL_PULSE_ON,

        /** 实时心率开 */
        REAL_PULSE_OFF,

        /** 实时实时数据开 开 */
        REAL_DATA_ON,

        /** 实时数据关*/
        REAL_DATA_OFF,

        /** 收取设备里的缓存数据*/
        SEARCH_HIS,

        /** 连接*/
        START_CONNECT,

        /** 断开连接*/
        STOP_GATT_SERVICE,

        /** 清除设备里的缓存*/
        CLEAR_FLASH,

    }
}
