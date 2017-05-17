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

        /** 实时重力加速度开 */
        REAL_GRAV_ON,

        /** 实时重力加速度关*/
        REAL_GRAV_OFF,

        /** 实时角速度开 */
        REAL_ANG_ON,

        /** 实时角速度关*/
        REAL_ANG_OFF,

        /** 实时地磁开 */
        REAL_MAG_ON,

        /** 实时地磁关*/
        REAL_MAG_OFF,

        /** 实时气压强度开 */
        REAL_PRESSURE_ON,

        /** 实时气压强度关*/
        REAL_PRESSURE_OFF,

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
