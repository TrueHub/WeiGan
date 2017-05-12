package com.youyi.weigan.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by user on 2017/4/7.
 * 历史心率的bean对象
 */
public class Pulse implements Parcelable,Serializable {
    public Pulse() {
    }

    private long time;
    private int pulse;
    private int trustLevel;

    protected Pulse(Parcel in) {
        time = in.readLong();
        pulse = in.readInt();
        trustLevel = in.readInt();
    }

    public static final Creator<Pulse> CREATOR = new Creator<Pulse>() {
        @Override
        public Pulse createFromParcel(Parcel in) {
            return new Pulse(in);
        }

        @Override
        public Pulse[] newArray(int size) {
            return new Pulse[size];
        }
    };

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public int getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(int trustLevel) {
        this.trustLevel = trustLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeInt(pulse);
        dest.writeInt(trustLevel);
    }
}

