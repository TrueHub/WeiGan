package com.youyi.weigan.utils;

import java.util.ArrayList;

/**
 * Created by user on 2017/5/2.
 */

public class MathUtils {

    /**
     * 求整体方差
     *
     * @param nums
     * @return
     */
    public static double getVariance(Integer[] nums) {
        double avg = 0;//平均值
        for (double n : nums) {
            avg += n;
        }
        avg /= nums.length;
        double vari = 0;
        for (double n : nums) {
            vari += Math.pow((n - avg), 2);
        }
        return Math.sqrt(vari / nums.length);
    }

    /**
     * 求样本方差
     *
     * @param nums
     * @return
     */
    public static double getVariance(double[] nums) {
        double avg = getAVG(nums);
        double vari = 0;
        for (double n : nums) {
            vari += Math.pow((n - avg), 2);
        }
        return Math.sqrt(vari / (nums.length - 1));
    }

    /**
     * 求模
     *
     * @param list
     * @return
     */
    public static double[] getNorm(ArrayList<ArrayList<Integer>> list) {

        double[] result = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            double n = 0;
            for (int j = 0; j < 3; j++) {
                n += Math.pow(list.get(i).get(j), 2);
            }
            result[i] = Math.sqrt(n);
        }
        return result;
    }

    /**
     * 求平均值
     *
     * @param nums
     * @return
     */
    public static double getAVG(double[] nums) {
        double avg = 0;//平均值
        for (double n : nums) {
            avg += n;
        }
        avg /= nums.length;
        return avg;
    }

    public static double getAVG(Integer[] nums) {
        double avg = 0;//平均值
        for (double n : nums) {
            avg += n;
        }
        avg /= nums.length;
        return avg;
    }

    public static void main(String[] args) {
        System.out.println("方差为：" + getVariance(new double[]{50, 100, 100, 50, 60}));
        System.out.println("方差为：" + getVariance(new double[]{73, 72, 75, 70, 70}));
    }

}
