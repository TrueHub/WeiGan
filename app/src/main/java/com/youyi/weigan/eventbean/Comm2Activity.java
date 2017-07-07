package com.youyi.weigan.eventbean;

/**
 * Created by user on 2017/7/7.
 */

public class Comm2Activity {
    private boolean inScanning;

    public boolean isInScanning() {
        return inScanning;
    }

    public void setInScanning(boolean inScanning) {
        this.inScanning = inScanning;
    }

    public Comm2Activity(boolean inScanning) {
        this.inScanning = inScanning;
    }
}
