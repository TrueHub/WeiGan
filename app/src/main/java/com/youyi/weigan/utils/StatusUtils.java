package com.youyi.weigan.utils;

import com.youyi.weigan.beans.AngV;
import com.youyi.weigan.beans.GravA;

import java.util.ArrayList;
import java.util.List;

import static com.youyi.weigan.utils.MathUtils.getAVG;
import static com.youyi.weigan.utils.MathUtils.getVariance;
import static com.youyi.weigan.utils.StatusUtils.Status.Bike;
import static com.youyi.weigan.utils.StatusUtils.Status.DownStairs;
import static com.youyi.weigan.utils.StatusUtils.Status.Run;
import static com.youyi.weigan.utils.StatusUtils.Status.Static;
import static com.youyi.weigan.utils.StatusUtils.Status.UpStairs;
import static com.youyi.weigan.utils.StatusUtils.Status.Walk;

/**
 * Created by user on 2017/6/2.
 */

public class StatusUtils {
    public enum Status {
        Walk,       //步行
        Run,        //跑步
        Bike,       //自行车
        UpStairs,   //上楼梯
        DownStairs, //下楼梯
        Static      //静止
    }

    public static Status getStatus(List<GravA> gravAList, List<AngV> angVList) {
        double[] angV_norm;   //  AngV的模
        double[] grav_norm;   //  GraV的模

        double angv_stdnorm;  //  AngV的模的方差
        double angv_meannorm; //  AngV的模的平均值
        double graV_meanY;    //  GraV_y轴的平均值
        double grav_stdY;     //  GraV的y轴的方差
        double grav_stdnorm;  //  GraV的模的方差

        Integer[] grav_yNums = new Integer[angVList.size()];
        ArrayList<ArrayList<Integer>> g0 = new ArrayList<>();
        for (int i = 0; i < gravAList.size(); i++) {
            ArrayList<Integer> g1 = new ArrayList<>();
            GravA gravA = gravAList.get(i);
            g1.add(gravA.getVelX());
            g1.add(gravA.getVelY());
            g1.add(gravA.getVelZ());
            g0.add(g1);
            grav_yNums[i] = gravA.getVelY();
        }

        Integer[] angV_yNums = new Integer[angVList.size()];
        ArrayList<ArrayList<Integer>> a0 = new ArrayList<>();
        for (int i = 0; i < angVList.size(); i++) {
            AngV angV = angVList.get(i);
            ArrayList<Integer> a1 = new ArrayList<>();
            a1.add(angV.getVelX());
            a1.add(angV.getVelY());
            a1.add(angV.getVelZ());
            a0.add(a1);
            angV_yNums[i] = angV.getVelY();
        }

        angV_norm = MathUtils.getNorm(a0);
        angv_stdnorm = getVariance(angV_norm);

        //第一次判断 ， AngV的每组数据的模的方差

        if (angv_stdnorm >= 735) {
            graV_meanY = getAVG(grav_yNums);
            if (graV_meanY >= 1025) {
                grav_stdY = MathUtils.getVariance(grav_yNums);
                if (grav_stdY < 653) {
                    angv_meannorm = MathUtils.getAVG(angV_norm);
                    if (angv_meannorm >= 1622) {
                        if (angv_stdnorm < 2168) {
                            return Walk;
                        } else {
                            if (angv_meannorm < 3048) {
                                return Walk;
                            } else {
                                return Run;
                            }
                        }
                    } else {
                        return UpStairs;
                    }
                } else {
                    return Run;
                }
            } else {
                angv_meannorm = getAVG(angV_norm);
                if (angv_meannorm >= 1962) {
                    if (graV_meanY < 613) {
                        return Run;
                    } else {
                        grav_stdY = MathUtils.getVariance(grav_yNums);
                        if (grav_stdY >= 615) {
                            return Run;
                        } else {
                            grav_norm = MathUtils.getNorm(g0);
                            grav_stdnorm = MathUtils.getVariance(grav_norm);
                            if (grav_stdnorm >= 589) {
                                return Walk;
                            } else {
                                return DownStairs;
                            }
                        }
                    }
                } else {
                    return UpStairs;
                }
            }
        } else {
            angv_meannorm = getAVG(angV_norm);
            if (angv_meannorm < 212)
                return Static;
            else
                return Bike;
        }
    }

/*
    */
/**
     * 判断骑车、走路、跑步
     *//*

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
            return Static;
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
        long abs = Math.abs(end - start);

        long endTime = (pressureList.get(pressureList.size() - 1).getTime()) / 10;
        long startTime = (pressureList.get(0).getTime()) / 10;

        long deftime = endTime - startTime;

        long ff = abs / deftime;

        if (ff <= 4)
            return Normal;
        else if (ff > 4 && ff <= 20 && a >= 700) {
            Log.i("MSL", "getMasterStateByPressure: Ang_max:" + a + ", endValue:" + end
                    + ",startValue:" + start + "\n endTime:" + endTime + ",startTime:" + startTime);
            return Stairs;
        } else if (ff > 20)
            return Elevator;
        return null;
    }
*/


}