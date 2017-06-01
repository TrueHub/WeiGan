package com.youyi.weigan.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2017/5/25.
 */

public class SensorFreq implements Parcelable{

    protected SensorFreq(Parcel in) {
        gravFreq = in.readInt();
        angFreq = in.readInt();
        magFreq = in.readInt();
        pressureFreq = in.readInt();
    }

    public static final Creator<SensorFreq> CREATOR = new Creator<SensorFreq>() {
        @Override
        public SensorFreq createFromParcel(Parcel in) {
            return new SensorFreq(in);
        }

        @Override
        public SensorFreq[] newArray(int size) {
            return new SensorFreq[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(gravFreq);
        dest.writeInt(angFreq);
        dest.writeInt(magFreq);
        dest.writeInt(pressureFreq);
    }

    public enum Type{
        comm2Gatt,comm2Activity
    }
    private int gravFreq;
    private int angFreq;
    private int magFreq;
    private int pressureFreq;
    private Type type;

    public SensorFreq() {
    }

    public int getGravFreq() {
        return gravFreq;
    }

    public void setGravFreq(int gravFreq) {
        this.gravFreq = gravFreq;
    }

    public int getAngFreq() {
        return angFreq;
    }

    public void setAngFreq(int angFreq) {
        this.angFreq = angFreq;
    }

    public int getMagFreq() {
        return magFreq;
    }

    public void setMagFreq(int magFreq) {
        this.magFreq = magFreq;
    }

    public int getPressureFreq() {
        return pressureFreq;
    }

    public void setPressureFreq(int pressureFreq) {
        this.pressureFreq = pressureFreq;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
