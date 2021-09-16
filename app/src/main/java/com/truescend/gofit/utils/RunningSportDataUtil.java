package com.truescend.gofit.utils;

import com.sn.app.utils.AppUserUtil;
import com.sn.map.bean.SNLocation;
import com.sn.map.view.SNMapHelper;
import com.sn.utils.DateUtil;

import java.util.List;

/**
 * 作者:东芝(2018/2/27).
 * 功能:运动数据计算工具
 */

public class RunningSportDataUtil {


    public static BaseSportData calculateBaseSportData(SNMapHelper mMapHelper, List<SNLocation> locations) {
        double distanceTotal = 0;
        double speedAvg = 0;
        double speedMax = 0;
        int size = locations.size();
        if (size >= 2) {
            for (int i = 0; i < size; i++) {
                SNLocation locationA = locations.get(i);
                i++;
                if (i >= size) break;
                SNLocation locationB = locations.get(i);
                distanceTotal += mMapHelper.calculateLineDistance(locationA, locationB);
                i--;//a-b,b-c,c-d计算距离
            }
            int num = 0;
            float speedTotal = 0;
            for (int i = 0; i < size; i++) {
                SNLocation location = locations.get(i);
                float speed = location.getSpeed();
                if (speed <= 0) {//过滤掉无意义的速度数据
                    continue;
                }
                //累计
                speedTotal += speed;
                num++;
                speedMax = Math.max(speedMax, speed);
            }
            if (speedTotal > 0) {
                speedAvg = speedTotal / num;
            }
        }

        return new BaseSportData(distanceTotal, speedAvg, speedMax);

    }

    public static SportData calculateSportData(BaseSportData baseSportData) {
        // m/s转km/h
        double hourSpeed = 0;
        if (baseSportData.speedAvg > 0) {
            hourSpeed = 3.6d * baseSportData.speedAvg;
        }

        double speedMax = 0;
        if (baseSportData.speedMax > 0) {
            speedMax = 3.6d * baseSportData.speedMax;
        }


        // m 转 km
        double distances = 0;
        if (baseSportData.distanceTotal > 0) {
            distances = baseSportData.distanceTotal / 1000.0d;
        }

        float weight = AppUserUtil.getUser().getWeight();
        //跑步热量（kcal）＝体重（kg）×距离（公里）×1.036 (百度的 具体公式还没定)
        double calories = weight * distances * 1.036d;

        //通过时速转成配速
        int speed_minutes = 0;
        int speed_seconds = 0;
        if (hourSpeed > 0) {
            DateUtil.HMS hms = DateUtil.getHMSFromMinutes((int) Math.round((60 / hourSpeed) * 60));
            speed_minutes = hms.getHour() * 60 + hms.getMinute();
            speed_seconds = hms.getSecond();
        }



        return new SportData(distances, calories, speed_minutes, speed_seconds, hourSpeed, speedMax);
    }

    public static class SportData {
        public double distances;
        public double calories;
        public int speed_minutes;
        public int speed_seconds;
        public double hourSpeed;
        public double speedMax;

        public SportData(double distances, double calories, int speed_minutes, int speed_seconds, double hourSpeed, double speedMax) {
            this.distances = distances;
            this.calories = calories;
            this.speed_minutes = speed_minutes;
            this.speed_seconds = speed_seconds;
            this.hourSpeed = hourSpeed;
            this.speedMax = speedMax;
        }
    }

    public static class BaseSportData {
        /**
         * 总距离
         */
        public double distanceTotal = 0;
        /**
         * 平均速度
         */
        public double speedAvg = 0;
        /**
         * 最大速度
         */
        public double speedMax = 0;

        public BaseSportData(double distanceTotal, double speedAvg, double speedMax) {
            this.distanceTotal = distanceTotal;
            this.speedAvg = speedAvg;
            this.speedMax = speedMax;
        }
    }
}
