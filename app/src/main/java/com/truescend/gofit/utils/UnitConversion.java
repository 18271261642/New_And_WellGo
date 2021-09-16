package com.truescend.gofit.utils;

import android.content.Context;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * 功能:单位转换工具类
 * Modify by 泽鑫 2017/11/28 10:14
 */

public class UnitConversion {


    /**
     * 华氏温度->摄氏温度
     * @param c
     * @return
     */
    public static float FToC(float c) {
        return (c - 32) * 1.0f / 1.8f;
    }

    /**
     * 摄氏温度->华氏温度
     * @param c
     * @return
     */
    public static float CToF(float c) {
        return c * 1.8f + 32;
    }

    /**
     * 千克 转 磅
     * 1(千克) = 2.2046226(磅)
     *
     * @param kg 千克
     * @return 磅
     */
    public static double kgTolb(double kg) {
        return 2.2/*046226*/ * kg;
    }

    /**
     * 磅 转 千克
     * 1(千克) = 2.2046226(磅)
     *
     * @param lb 磅
     * @return 千克
     */
    public static double lbTokg(double lb) {
        return lb / 2.2/*046226*/;
    }




    /**
     * 公里(千米) 转 英里
     * 1(公里/千米) = 0.6213712(英里)
     *
     * @param kl 公里
     * @return 英里
     */
    public static float kmToMiles(float kl) {
        return (float) (kl * 0.6213712);
    }

    /**
     * 英里 转 公里(千米)
     * 1(公里/千米) = 0.6213712(英里)
     *
     * @param mile 英里
     * @return 公里
     */
    public static float milesToKm(float mile) {
        return (float) (mile * 1.609344);
    }

    /**
     * 米 转 英尺
     * 1(米) = 3.2808399(英尺)
     *
     * @param m 米
     * @return 英尺
     */
    public static float mToft(float m) {
        return (float) (m * 3.2808399);
    }

    /**
     * 英尺 转 米
     * 1(米) = 3.2808399(英尺)
     *
     * @param ft 英尺
     * @return 米
     */
    public static float ftTom(float ft) {

        return (float) (ft / 3.2808399);
    }


    /**
     * x 转 手环所显示的公里(千米)
     * x 转 手环所显示的英里
     */
    public static float toBandK(float x) {
        return ((int) ((x * 1.0f / 1000) * 100)) * 1.0f / 100;
    }



    private static DecimalFormat df = new DecimalFormat("#0.##");
    private static DecimalFormatSymbols symbols = new DecimalFormatSymbols(); //德语等其他语言把","转成"."

    /**
     * 俄语、德语等语言小数点显示的是逗号，因此需要转为小数点
     *
     * @param num 数值
     * @return 转换的值
     */
    public static float getIgnoreRound(float num) {
        symbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(symbols);
        df.setRoundingMode(RoundingMode.FLOOR);//不舍舍五入
        String formatString = df.format(num / 1000.0d);
        return Float.valueOf(formatString);
    }

    /**
     * dp转px(像素)
     *
     * @param context 上下文参数
     * @param dpValue dp值
     * @return px值
     */
    public static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px(像素)转dp
     *
     * @param context 上下文参数
     * @param pxValue px值
     * @return dp值
     */
    public static int pxToDip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


}
