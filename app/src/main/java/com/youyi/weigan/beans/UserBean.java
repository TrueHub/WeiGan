package com.youyi.weigan.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by user on 2017/4/11.
 * 单例user类
 */

public class UserBean implements Parcelable {
    private ArrayList<GravA> gravAArrayList = new ArrayList<>();
    private ArrayList<Mag> magArrayList = new ArrayList<>();
    private ArrayList<AngV> angVArrayList = new ArrayList<>();
    private ArrayList<Pressure> pressureArrayList = new ArrayList<>();
    private ArrayList<Pulse> pulseArrayList = new ArrayList<>();

    private UserBean() {
    }

    private static class UserHolder {
        private static final UserBean userBean = new UserBean();
    }

    public static UserBean getInstence() {
        return UserHolder.userBean;
    }

    protected UserBean(Parcel in) {
        gravAArrayList = in.createTypedArrayList(GravA.CREATOR);
        magArrayList = in.createTypedArrayList(Mag.CREATOR);
        angVArrayList = in.createTypedArrayList(AngV.CREATOR);
        pressureArrayList = in.createTypedArrayList(Pressure.CREATOR);
        pulseArrayList = in.createTypedArrayList(Pulse.CREATOR);
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(gravAArrayList);
        dest.writeTypedList(magArrayList);
        dest.writeTypedList(angVArrayList);
        dest.writeTypedList(pressureArrayList);
        dest.writeTypedList(pulseArrayList);
    }

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

    public static Creator<UserBean> getCREATOR() {
        return CREATOR;
    }
}
