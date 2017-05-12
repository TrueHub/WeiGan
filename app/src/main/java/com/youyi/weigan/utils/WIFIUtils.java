package com.youyi.weigan.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by user on 2017/4/26.
 */

public class WIFIUtils {

    public static String getWifiId(WifiManager wifiMgr) {
        WifiInfo info = wifiMgr.getConnectionInfo();
        return info != null ? info.getSSID() : null; //wifi名
    }

    public static String getWifiMAC(WifiManager wifiMgr) {
        WifiInfo info = wifiMgr.getConnectionInfo();
        return info.getBSSID();//wifi mac地址
    }

    public static boolean isWifiType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
