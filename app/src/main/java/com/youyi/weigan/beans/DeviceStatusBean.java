package com.youyi.weigan.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2017/4/14.
 */

public class DeviceStatusBean implements Parcelable{
    private int time ;
    private int pulseAbnomal_min;
    private int pulseAbnomal_max;
    private int simplingFreq;
    private int deviceElec;


    public DeviceStatusBean(Parcel in) {
        time = in.readInt();
        pulseAbnomal_min = in.readInt();
        pulseAbnomal_max = in.readInt();
        simplingFreq = in.readInt();
        deviceElec = in.readInt();
    }

    public DeviceStatusBean() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(time);
        dest.writeInt(pulseAbnomal_min);
        dest.writeInt(pulseAbnomal_max);
        dest.writeInt(simplingFreq);
        dest.writeInt(deviceElec);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeviceStatusBean> CREATOR = new Creator<DeviceStatusBean>() {
        @Override
        public DeviceStatusBean createFromParcel(Parcel in) {
            return new DeviceStatusBean(in);
        }

        @Override
        public DeviceStatusBean[] newArray(int size) {
            return new DeviceStatusBean[size];
        }
    };

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getPulseAbnomal_min() {
        return pulseAbnomal_min;
    }

    public void setPulseAbnomal_min(int pulseAbnomal_min) {
        this.pulseAbnomal_min = pulseAbnomal_min;
    }

    public int getPulseAbnomal_max() {
        return pulseAbnomal_max;
    }

    public void setPulseAbnomal_max(int pulseAbnomal_max) {
        this.pulseAbnomal_max = pulseAbnomal_max;
    }

    public int getSimplingFreq() {
        return simplingFreq;
    }

    public void setSimplingFreq(int simplingFreq) {
        this.simplingFreq = simplingFreq;
    }

    public int getDeviceElec() {
        return deviceElec;
    }

    public void setDeviceElec(int deviceElec) {
        this.deviceElec = deviceElec;
    }

    public static Creator<DeviceStatusBean> getCREATOR() {
        return CREATOR;
    }
}
