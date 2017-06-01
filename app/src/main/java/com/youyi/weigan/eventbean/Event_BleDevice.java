package com.youyi.weigan.eventbean;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2017/6/1.
 */

public class Event_BleDevice implements Parcelable{
    private BluetoothDevice device;
    private From from;

    public Event_BleDevice(BluetoothDevice device, From from) {
        this.device = device;
        this.from = from;
    }

    protected Event_BleDevice(Parcel in) {
        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
    }

    public static final Creator<Event_BleDevice> CREATOR = new Creator<Event_BleDevice>() {
        @Override
        public Event_BleDevice createFromParcel(Parcel in) {
            return new Event_BleDevice(in);
        }

        @Override
        public Event_BleDevice[] newArray(int size) {
            return new Event_BleDevice[size];
        }
    };

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public From getFrom() {
        return from;
    }

    public void setFrom(From from) {
        this.from = from;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
    }

    public enum From {
        Gatt,Activity
    }
}
