package com.youyi.weigan.dbUtils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by user on 2017/4/12.
 */

public class DataBaseContext extends ContextWrapper {
    public DataBaseContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath(String name) {

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//如果不存在SD卡,

            String dbPath = getApplicationContext().getDatabasePath("..").getAbsolutePath() + "/" + name;
            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡" + dbPath);

            File dbFile = new File(dbPath);

            if (!dbFile.exists()) {
                try {
                    dbFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return dbFile;
        }

        String dbDir = Environment.getExternalStorageDirectory().getAbsolutePath();//SD卡根目录
        dbDir += "/vervel/database";//数据库所在目录

        Log.i("MSL", "getDatabasePath: " + dbDir);

        String dbPath = dbDir + "/" + name;
        File dirFile = new File(dbDir);
        if (!dirFile.exists())
            dirFile.mkdirs();

        //数据库文件是否创建成功
        boolean isFileCreateSuccess = false;
        //判断文件是否存在，不存在则创建该文件
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            try {
                isFileCreateSuccess = dbFile.createNewFile();//创建文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else isFileCreateSuccess = true;

        //返回数据库文件对象
        if (isFileCreateSuccess) return dbFile;
        else return null;
    }

    /**
     * 2.3以下 版本会调用此方法，打开sd卡上的数据库
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }

    /**
     * 4.0调用此方法，获取数据库
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }
}
