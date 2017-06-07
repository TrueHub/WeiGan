package com.youyi.weigan.utils;

import android.util.Log;

import com.youyi.weigan.beans.AngV;
import com.youyi.weigan.beans.GravA;
import com.youyi.weigan.beans.Mag;
import com.youyi.weigan.beans.Pressure;

import java.util.ArrayList;
import java.util.List;

import static com.youyi.weigan.R.string.angV;
import static com.youyi.weigan.utils.StatusUtils.Status.Bike;
import static com.youyi.weigan.utils.StatusUtils.Status.Elevator;
import static com.youyi.weigan.utils.StatusUtils.Status.Escalator;
import static com.youyi.weigan.utils.StatusUtils.Status.Normal;
import static com.youyi.weigan.utils.StatusUtils.Status.Run;
import static com.youyi.weigan.utils.StatusUtils.Status.Sit;
import static com.youyi.weigan.utils.StatusUtils.Status.Stairs;
import static com.youyi.weigan.utils.StatusUtils.Status.Walk;

/**
 * Created by user on 2017/6/2.
 */

public class StatusUtils {
    public enum Status {
        Normal,     //..
        Walk,       //步行
        Run,        //跑步
        Bike,       //自行车
        Escalator,  //自动扶梯
        Elevator,  //电梯
        Stairs,     //楼梯
        Sit,        //静止
        Lie,        //躺下
        Leg_Crossed //跷腿
    }

    public static Status getStatus(List<GravA> gravAList, List<AngV> angVList, List<Mag> magList, List<Pressure> pressures) {
        Status status = null;

        //九轴各数据的方差
        //重力加速度
        double gX = -1;
        double gY = -1;
        double gZ = -1;
        //陀螺仪
        double aX = -1;
        double aY = -1;
        double aZ = -1;
        //地磁
        double mX = -1;
        double mY = -1;
        double mZ = -1;

        Integer[] gXs = new Integer[gravAList.size()];
        Integer[] gYs = new Integer[gravAList.size()];
        Integer[] gZs = new Integer[gravAList.size()];
        for (int i = 0; i < gravAList.size(); i++) {
            GravA gravA = gravAList.get(i);
            gXs[i] = gravA.getVelX();
            gYs[i] = gravA.getVelY();
            gZs[i] = gravA.getVelZ();
        }

        Integer[] aXs = new Integer[angVList.size()];
        Integer[] aYs = new Integer[angVList.size()];
        Integer[] aZs = new Integer[angVList.size()];
        for (int i = 0; i < angVList.size(); i++) {
            AngV angV = angVList.get(i);
            aXs[i] = angV.getVelX();
            aYs[i] = angV.getVelY();
            aZs[i] = angV.getVelZ();
        }

        Integer[] mXs = new Integer[magList.size()];
        Integer[] mYs = new Integer[magList.size()];
        Integer[] mZs = new Integer[magList.size()];
        for (int i = 0; i < magList.size(); i++) {
            Mag mag = magList.get(i);
            mXs[i] = mag.getStrengthX();
            mYs[i] = mag.getStrengthY();
            mZs[i] = mag.getStrengthZ();
        }


        gX = MathUtils.getVarianceUtil(gXs);
        gY = MathUtils.getVarianceUtil(gYs);
        gZ = MathUtils.getVarianceUtil(gZs);
        aX = MathUtils.getVarianceUtil(aXs);
        aY = MathUtils.getVarianceUtil(aYs);
        aZ = MathUtils.getVarianceUtil(aZs);
        mX = MathUtils.getVarianceUtil(mXs);
        mY = MathUtils.getVarianceUtil(mYs);
        mZ = MathUtils.getVarianceUtil(mZs);

        return status;
    }


    /**
     * 判断骑车、走路、跑步
     */
    public static Status getMasterStateByAng(ArrayList<AngV> angVList) {
        //只考虑x轴的情况
        int a, b;
        a = b = angVList.get(0).getVelX();
        for (int i = 0; i < angVList.size(); i++) {
            if (angVList.get(i).getVelX() > a) a = angVList.get(i).getVelX();
            if (angVList.get(i).getVelX() < b) b = angVList.get(i).getVelX();
        }
        if (a >= 3000) {//bike or run
            int aa = Math.abs(a);
            int bb = Math.abs(b);
            int ab = Math.abs(aa - bb);
            if (ab < 800) {
                return Bike;
            } else {
                return Run;
            }
        } else if (a >= 700 && a < 3000) { // bike or walk
            int aa = Math.abs(a);
            int bb = Math.abs(b);
            int ab = Math.abs(aa - bb);
            if (ab < 200)
                return Bike;
            else
                return Walk;
        } else {
            return Sit;
        }
    }

    public static Status getMasterStateByPressure(ArrayList<Pressure> pressureList, ArrayList<AngV> angVList) {

        int a;
        a = angVList.get(0).getVelX();
        for (int i = 0; i < angVList.size(); i++) {
            if (angVList.get(i).getVelX() > a) a = angVList.get(i).getVelX();
        }
        long start = pressureList.get(0).getIntensityOfPressure();
        long end = pressureList.get(pressureList.size() - 1).getIntensityOfPressure();
        long abs = Math.abs( end - start);

        long endTime = (pressureList.get(pressureList.size() - 1).getTime()) / 10;
        long startTime = (pressureList.get(0).getTime()) / 10;

        long deftime = endTime - startTime;

        long ff = abs / deftime;

        if (ff <= 4)
            return Normal;
        else if (ff > 4 && ff <= 20 && a >= 700) {
            Log.i("MSL", "getMasterStateByPressure: Ang_max:" + a + ", endValue:" + end
                    +",startValue:" + start +"\n endTime:"+endTime +",startTime:" + startTime);
            return Stairs;
        } else if (ff > 20)
            return Elevator;
        return null;
    }
}