package com.youyi.weigan.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 2017/4/11.
 * 单例user类
 */

public class UserJsonBean implements Parcelable , Serializable{
    private ArrayList<GravA> gravAArrayList = new ArrayList<>();
    private ArrayList<Mag> magArrayList = new ArrayList<>();
    private ArrayList<AngV> angVArrayList = new ArrayList<>();
    private ArrayList<Pressure> pressureArrayList = new ArrayList<>();
    private ArrayList<Pulse> pulseArrayList = new ArrayList<>();
    private String deviceId ;

    public UserJsonBean(String deviceId) {
        this.deviceId = deviceId;
    }

    private UserJsonBean(Parcel in) {
        gravAArrayList = in.createTypedArrayList(GravA.CREATOR);
        magArrayList = in.createTypedArrayList(Mag.CREATOR);
        angVArrayList = in.createTypedArrayList(AngV.CREATOR);
        pressureArrayList = in.createTypedArrayList(Pressure.CREATOR);
        pulseArrayList = in.createTypedArrayList(Pulse.CREATOR);
        deviceId = in.readString();
    }

    public UserJsonBean() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(gravAArrayList);
        dest.writeTypedList(magArrayList);
        dest.writeTypedList(angVArrayList);
        dest.writeTypedList(pressureArrayList);
        dest.writeTypedList(pulseArrayList);
        dest.writeString(deviceId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserJsonBean> CREATOR = new Creator<UserJsonBean>() {
        @Override
        public UserJsonBean createFromParcel(Parcel in) {
            return new UserJsonBean(in);
        }

        @Override
        public UserJsonBean[] newArray(int size) {
            return new UserJsonBean[size];
        }
    };

    public ArrayList<GravA> getGravAArrayList() {
        return gravAArrayList;
    }

    public void setGravAArrayList(ArrayList<GravA> gravAArrayList) {
        this.gravAArrayList = gravAArrayList;
    }

    public ArrayList<Mag> getMagArrayList() {
        return magArrayList;
    }

    public void setMagArrayList(ArrayList<Mag> magArrayList) {
        this.magArrayList = magArrayList;
    }

    public ArrayList<AngV> getAngVArrayList() {
        return angVArrayList;
    }

    public void setAngVArrayList(ArrayList<AngV> angVArrayList) {
        this.angVArrayList = angVArrayList;
    }

    public ArrayList<Pressure> getPressureArrayList() {
        return pressureArrayList;
    }

    public void setPressureArrayList(ArrayList<Pressure> pressureArrayList) {
        this.pressureArrayList = pressureArrayList;
    }

    public ArrayList<Pulse> getPulseArrayList() {
        return pulseArrayList;
    }

    public void setPulseArrayList(ArrayList<Pulse> pulseArrayList) {
        this.pulseArrayList = pulseArrayList;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
