package com.youyi.weigan.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by user on 2017/4/7.
 */
public class AngV implements Parcelable ,Serializable{
    private long time;
    private int velX;
    private int velY;
    private int velZ;

    public AngV() {
    }

    protected AngV(Parcel in) {
        time = in.readLong();
        velX = in.readInt();
        velY = in.readInt();
        velZ = in.readInt();
    }

    public static final Creator<AngV> CREATOR = new Creator<AngV>() {
        @Override
        public AngV createFromParcel(Parcel in) {
            return new AngV(in);
        }

        @Override
        public AngV[] newArray(int size) {
            return new AngV[size];
        }
    };

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getVelX() {
        return velX;
    }

    public void setVelX(int velX) {
        this.velX = velX;
    }

    public int getVelY() {
        return velY;
    }

    public void setVelY(int velY) {
        this.velY = velY;
    }

    public int getVelZ() {
        return velZ;
    }

    public void setVelZ(int velZ) {
        this.velZ = velZ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeInt(velX);
        dest.writeInt(velY);
        dest.writeInt(velZ);
    }
}

