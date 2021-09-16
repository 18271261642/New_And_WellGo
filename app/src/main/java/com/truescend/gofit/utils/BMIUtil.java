package com.truescend.gofit.utils;

import com.truescend.gofit.App;
import com.truescend.gofit.R;

/**
 * 类型：计算BMI工具类
 * Author:Created by 泽鑫 on 2018/1/31 18:34.
 * 东芝:修改方法名,重新规定该类的作用
 */

public class BMIUtil {

    /**
     * 计算BMI指数
     *
     * @param height 身高
     * @param weight 体重
     * @return BMI
     */
    public static String getUserWeightBMI(float height, float weight) {

        float BMI = getUserWeightBMIValue(height, weight);
        if (BMI == -1) return "";
        return ResUtil.format("%.1f", BMI);
    }

    public static float getUserWeightBMIValue(float height, float weight) {
        if ((int) height == 0 || (int) weight == 0) {
            return -1;
        }
        return weight / (float) Math.pow(height / 100, 2);
    }


    /**
     * 根据BMI指数判断体重类型
     *
     * @param height 身高
     * @param weight 体重
     * @return 体重类型
     */
    public static String getUserWeightType(float height, float weight) {
        float BMI = getUserWeightBMIValue(height, weight);
        if (BMI == -1) return "";

        if (BMI < 18.5) {
            return App.getContext().getString(R.string.bmi_too_light);
        } else if (BMI <= 23.9) {
            return App.getContext().getString(R.string.bmi_normal);
        } else if (BMI <= 27) {
            return App.getContext().getString(R.string.bmi_too_heavy);
        } else if (BMI <= 32) {
            return App.getContext().getString(R.string.bmi_obesity);
        } else {
            return App.getContext().getString(R.string.bmi_very_fat);
        }
    }

}
