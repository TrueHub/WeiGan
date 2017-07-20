package com.youyi.weigan.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    public static int getWeight(ArrayList<Integer> list) {
        int index = -1;
        int max = 0;

        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i : list) {
            if (map.get(i) != null) {
                map.put(i, map.get(i) + 1);
            } else
                map.put(i, 1);
        }
        Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            max = Math.max(entry.getValue(), max);
        }
        if (max == 1)
            return -1;
        list.clear();
        iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            if (entry.getValue() == max) {
                index = entry.getKey();
                list.add(index);
            }
        }
        if (list.size() == 1)
            return index;
        else
            return -1;
    }

    public static void main(String[] args) {

        ArrayList<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(3);
        list.add(2);
        list.add(1);
        list.add(5);
        list.add(5);

        System.out.println(getWeight(list));
    }

}
