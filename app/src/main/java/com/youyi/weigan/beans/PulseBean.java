package com.youyi.weigan.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 2017/4/7.
 * 实时心率的bean对象
 */
public class PulseBean implements Parcelable {
    private int pulse;
    private int trustLevel;

    public PulseBean(int pulse, int trustLevel) {
        this.pulse = pulse;
        this.trustLevel = trustLevel;
    }

    protected PulseBean(Parcel in) {
        pulse = in.readInt();
        trustLevel = in.readInt();
    }

    public static final Creator<PulseBean> CREATOR = new Creator<PulseBean>() {
        @Override
        public PulseBean createFromParcel(Parcel in) {
            return new PulseBean(in);
        }

        @Override
        public PulseBean[] newArray(int size) {
            return new PulseBean[size];
        }
    };

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
        dest.writeInt(pulse);
        dest.writeInt(trustLevel);
    }
}
