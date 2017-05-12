package com.youyi.weigan.dbUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.youyi.weigan.beans.AngV;
import com.youyi.weigan.beans.GravA;
import com.youyi.weigan.beans.Mag;
import com.youyi.weigan.beans.Pressure;
import com.youyi.weigan.beans.Pulse;
import com.youyi.weigan.beans.UserBean;
import com.youyi.weigan.utils.DateUtils;

import java.util.ArrayList;

public class SqliteHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private String createTabSql1, createTabSql2, createTabSql3, createTabSql4, createTabSql5;
    private UserBean userBean = UserBean.getInstence();

    public SqliteHelper(Context context) {
        super(context, "health.db", null, 1);

        createTabSql1 = "CREATE TABLE if not exists GravA(_id integer primary key autoincrement, timeLong integer,timeStr text, x integer,y integer,z integer)";
        createTabSql2 = "CREATE TABLE if not exists Mag(_id integer primary key autoincrement, timeLong integer, timeStr text, x integer,y integer,z integer)";
        createTabSql3 = "CREATE TABLE if not exists AngV(_id integer primary key autoincrement, timeLong integer, timeStr text, x integer,y integer,z integer)";
        createTabSql4 = "CREATE TABLE if not exists Pressure(_id integer primary key autoincrement, timeLong integer, timeStr text, intensity integer)";
        createTabSql5 = "CREATE TABLE if not exists Pulse(_id integer primary key autoincrement, timeLong integer, timeStr text, pulse integer,trustLevel integer)";
        db = getReadableDatabase();

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("MSL", "onCreateSQL: ");
        db.execSQL(createTabSql1);
        db.execSQL(createTabSql2);
        db.execSQL(createTabSql3);
        db.execSQL(createTabSql4);
        db.execSQL(createTabSql5);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertDataBySw() {
        if (userBean.getGravAArrayList().size() != 0) {
            addGravABySw(userBean.getGravAArrayList());
            userBean.getGravAArrayList().clear();
        }
        if (userBean.getPulseArrayList().size() != 0) {
            addPulseBySw(userBean.getPulseArrayList());
            userBean.getPulseArrayList().clear();
        }
        if (userBean.getAngVArrayList().size() != 0) {
            addAngVBySw(userBean.getAngVArrayList());
            userBean.getAngVArrayList().clear();
        }
        if (userBean.getMagArrayList().size() != 0) {
            addMagBySw(userBean.getMagArrayList());
            userBean.getMagArrayList().clear();
        }
        if (userBean.getPressureArrayList().size() != 0) {
            addPressureBySw(userBean.getPressureArrayList());
            userBean.getPressureArrayList().clear();
        }
    }

    private void addGravABySw(ArrayList<GravA> list) {
        db.beginTransaction();
        try {
            String time;
            int x, y, z;
            for (int i = 0; i < list.size(); i++) {
                time = DateUtils.getDateToString(list.get(i).getTime() * 1000);
                x = list.get(i).getVelX();
                y = list.get(i).getVelY();
                z = list.get(i).getVelZ();
                ContentValues values = new ContentValues();
                values.put("timeLong", list.get(i).getTime());
                values.put("timeStr", time);
                values.put("x", x);
                values.put("y", y);
                values.put("z", z);
                db.insert("GravA", "_id", values);

            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.endTransaction();
    }

    private void addMagBySw(ArrayList<Mag> list) {
        db.beginTransaction();
        try {
            String time;
            int x, y, z;
            for (int i = 0; i < list.size(); i++) {
                time = DateUtils.getDateToString(list.get(i).getTime() * 1000);
                x = list.get(i).getStrengthX();
                y = list.get(i).getStrengthY();
                z = list.get(i).getStrengthZ();
                ContentValues values = new ContentValues();
                values.put("timeLong", list.get(i).getTime());
                values.put("timeStr", time);
                values.put("x", x);
                values.put("y", y);
                values.put("z", z);
                db.insert("Mag", "_id", values);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.endTransaction();
    }

    private void addAngVBySw(ArrayList<AngV> list) {
        db.beginTransaction();
        try {
            String time;
            int x, y, z;
            for (int i = 0; i < list.size(); i++) {
                time = DateUtils.getDateToString(list.get(i).getTime() * 1000);
                x = list.get(i).getVelX();
                y = list.get(i).getVelY();
                z = list.get(i).getVelZ();
                ContentValues values = new ContentValues();
                values.put("timeLong", list.get(i).getTime());
                values.put("timeStr", time);
                values.put("x", x);
                values.put("y", y);
                values.put("z", z);
                db.insert("AngV", "_id", values);

            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.endTransaction();
    }

    private void addPressureBySw(ArrayList<Pressure> list) {
        db.beginTransaction();  //开启事务
        try {
            String time;
            long intensity;
            for (int i = 0; i < list.size(); i++) {
                time = DateUtils.getDateToString(list.get(i).getTime() * 1000);
                intensity = list.get(i).getIntensityOfPressure();
                ContentValues values = new ContentValues();
                values.put("timeLong", list.get(i).getTime());
                values.put("timeStr", time);
                values.put("intensity", intensity);
                db.insert("Pressure", "_id", values);

            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.endTransaction();
    }

    private void addPulseBySw(ArrayList<Pulse> list) {
        Log.i("MSL", "addPulseBySw: " + list.size());
        db.beginTransaction();  //开启事务
        try {
            String time;
            int pulse;
            int trustLevel;
            for (int i = 0; i < list.size(); i++) {
                time = DateUtils.getDateToString(list.get(i).getTime() * 1000);
                pulse = list.get(i).getPulse();
                trustLevel = list.get(i).getTrustLevel();
                ContentValues values = new ContentValues();
                values.put("timeLong", list.get(i).getTime());
                values.put("timeStr", time);
                values.put("pulse", pulse);
                values.put("trustLevel", trustLevel);
                db.insert("Pulse", "_id", values);
            }
            Log.i("MSL", "addPulseBySw: OK");
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.endTransaction();
    }

}
