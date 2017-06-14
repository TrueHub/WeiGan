package com.youyi.weigan.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by user on 2017/6/14.
 */

public class FileUtils {

    public static String getFileSize(String str) {
        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();//SD卡根目录
        fileDir = fileDir + "/" + str;
        File dir = new File(fileDir);

        File[] files = dir.listFiles();

        if (files == null) return null;

        long size = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) continue;//如果是文件夹，忽略它
            File file = files[i];
            size += file.length();
        }

        return convertSize(size);

    }

    public static String getCacheSize(String str) {
        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();//SD卡根目录
        fileDir = fileDir + "/" + str;
        File dir = new File(fileDir);

        File[] files = dir.listFiles();

        if (files == null) return null;

        long size = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                for (File file :
                        files[i].listFiles()) {
                    size += file.length();
                }
            } else {
                File file = files[i];
                size += file.length();
            }
        }

        return convertSize(size);
    }

    public static boolean deleteCache(String str) {
        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();//SD卡根目录
        fileDir = fileDir + "/" + str;
        File dir = new File(fileDir);

        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                for (File file :
                        files[i].listFiles()) {
                    file.delete();
                }
            } else {
                File file = files[i];
                file.delete();
            }
        }

        return true;
    }

    private static String convertSize(long size) {
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }
}
