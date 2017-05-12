package com.youyi.weigan.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by user on 2017/4/7.
 */
public class GravA implements Parcelable ,Serializable {
    public GravA() {
    }

    private long time;
    private int velX;
    private int velY;
    private int velZ;

    protected GravA(Parcel in) {
        time = in.readLong();
        velX = in.readInt();
        velY = in.readInt();
        velZ = in.readInt();
    }

    public static final Creator<GravA> CREATOR = new Creator<GravA>() {
        @Override
        public GravA createFromParcel(Parcel in) {
            return new GravA(in);
        }

        @Override
        public GravA[] newArray(int size) {
            return new GravA[size];
        }
    };

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
}

