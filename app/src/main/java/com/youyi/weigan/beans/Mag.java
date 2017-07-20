package com.youyi.weigan.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import java.io.Serializable;

/**
 * Created by user on 2017/4/7.
 */
public class Mag implements Parcelable , Serializable{
    public Mag() {
    }

    private long time;
    private int strengthX;
    private int strengthY;
    private int strengthZ;

    protected Mag(Parcel in) {
        time = in.readLong();
        strengthX = in.readInt();
        strengthY = in.readInt();
        strengthZ = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeInt(strengthX);
        dest.writeInt(strengthY);
        dest.writeInt(strengthZ);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Mag> CREATOR = new Creator<Mag>() {
        @Override
        public Mag createFromParcel(Parcel in) {
            return new Mag(in);
        }

        @Override
        public Mag[] newArray(int size) {
            return new Mag[size];
        }
    };

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getStrengthX() {
        return strengthX;
    }
    public void setStrengthX(int strengthX) {
        this.strengthX = strengthX;
    }

    public int getStrengthY() {
        return strengthY;
    }

    public void setStrengthY(int strengthY) {
        this.strengthY = strengthY;
    }

    public int getStrengthZ() {
        return strengthZ;
    }

    public void setStrengthZ(int strengthZ) {
        this.strengthZ = strengthZ;
    }
}
