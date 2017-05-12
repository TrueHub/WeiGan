package com.youyi.weigan.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by user on 2017/4/7.
 */
public class Pressure implements Parcelable , Serializable{
    private long time;
    private long intensityOfPressure;

    public Pressure() {
    }

    protected Pressure(Parcel in) {
        time = in.readLong();
        intensityOfPressure = in.readLong();
    }

    public static final Creator<Pressure> CREATOR = new Creator<Pressure>() {
        @Override
        public Pressure createFromParcel(Parcel in) {
            return new Pressure(in);
        }

        @Override
        public Pressure[] newArray(int size) {
            return new Pressure[size];
        }
    };

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getIntensityOfPressure() {
        return intensityOfPressure;
    }

    public void setIntensityOfPressure(long intensityOfPressure) {
        this.intensityOfPressure = intensityOfPressure;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeLong(intensityOfPressure);
    }
}

