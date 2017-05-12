package com.youyi.weigan.utils;


/**
 * Created by user on 2017/5/2.
 */

public class MathUtils {

    public static double getVarianceUtil(double[] nums) {
        double avg = 0 ;//平均值
        for (double n :nums) {
            avg += n;
        }
        avg /= nums.length ;
        double vari = 0 ;
        for (double n :nums) {
            vari += Math.pow((n - avg),2 );
        }
        return Math.sqrt(vari / nums.length);
    }

    public static void main (String[] args) {
        System.out.println("方差为：" +getVarianceUtil(new double[]{50,100,100,50,60}));
        System.out.printf("方差为：" +getVarianceUtil(new double[]{73,72,75,70,70}));
    }

}
