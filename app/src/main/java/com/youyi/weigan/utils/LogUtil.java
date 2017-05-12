package com.youyi.weigan.utils;

import android.util.Log;

/**
 * Created by user on 2017/5/3.
 */

public class LogUtil {

    //可以全局控制是否打印log日志
    private static boolean isPrintLog = true;

    private static int LOG_MAXLENGTH = 2000;

    public static void LogMSL(String msg) {
        if (isPrintLog) {

            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < msg.length()/LOG_MAXLENGTH + 1; i++) {
                if (strLength > end) {
                    Log.d("MSL___" + i, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.d("MSL___" + i, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void LogMSL(String type, String msg) {

        if (isPrintLog) {

            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < msg.length()/LOG_MAXLENGTH + 1; i++) {
                if (strLength > end) {
                    Log.d(type + "___" + i, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.d(type + "___" + i, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

}