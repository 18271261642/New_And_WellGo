package com.truescend.gofit.utils;

import com.sn.blesdk.db.data.sleep.SleepBean;
import com.truescend.gofit.R;

/**
 * 作者:东芝(2017/12/16).
 * 功能:睡眠UI数据相关工具类
 */

public class SleepDataUtil {
    /**
     * 睡眠质量
     *
     * @param sleepBean return
     */
    public static String getSleepQuality(SleepBean sleepBean) {
        //睡眠质量
        float sleepQuality = getSleepQualityFloat(sleepBean);
        return getSleepQualityStr(sleepQuality);
    }

    public static String getSleepQualityStr(float sleepQuality) {
        String quality = ResUtil.getString(R.string.content_no_time);
        if (sleepQuality == 4) {
            quality = ResUtil.getString(R.string.content_good);
        } else if (sleepQuality == 3) {
            quality = ResUtil.getString(R.string.content_well);
        } else if (sleepQuality == 2) {
            quality = ResUtil.getString(R.string.content_normal);
        } else if (sleepQuality == 1) {
            quality = ResUtil.getString(R.string.content_poor);
        }
        return quality;
    }

    /**
     * 睡眠质量 值
     *
     * @param sleepBean return
     */
    public static int getSleepQualityInt(SleepBean sleepBean) {
        //睡眠质量
        float sleepQuality = getSleepQualityFloat(sleepBean);
        return (int) sleepQuality;
    }

    /**
     * 睡眠质量 值
     *
     * @param sleepBean return
     */
    public static float getSleepQualityFloat(SleepBean sleepBean) {
        return getSleepQualityFloat(sleepBean.getDeepTotal(), sleepBean.getLightTotal(), sleepBean.getSoberTotal());
    }

    /**
     * 睡眠质量 值
     * return
     */
    public static float getSleepQualityFloat(int deepTotal, int lightTotal, int soberTotal) {
        //总睡眠
        int sleepTimeTotal = getSleepTotal(deepTotal, lightTotal, soberTotal);
        //睡眠占比
        float percent = ((deepTotal + lightTotal) * 1.0f / sleepTimeTotal)*100;


        int score;//分数,  4321 分别为: 优秀 良好 一般 较差
        if (sleepTimeTotal >= 12 * 60) {//睡超过12小时,不管深睡睡占比多少,一律视为 一般,  即使睡24小时 也不会评较差
            score = 2;
        } else if (sleepTimeTotal >= 10 * 60) {//睡10-11小时,不管深睡睡占比多少,一律视为良好
            score = 3;
        } else if (sleepTimeTotal >= 7 * 60) { //睡7-9小时 深睡占比30%以上为优秀
            if (percent > 30) {
                score = 4;
            } else {
                score = 3;
            }
        } else if (sleepTimeTotal >= 5 * 60) {//睡5-6小时 为良好
            score = 3;
        } else if (sleepTimeTotal >= 3 * 60) { //睡3-4小时 深睡占比低于50%以上为差,否则为一般
            if (percent > 50) {
                score = 2;
            } else {
                score = 1;
            }
        } else { //睡眠低于3小时  不管深睡睡占比多少, 一律视为较差
            score = 1;
        }
        return score;
    }

    /**
     * 睡眠总时长
     * return
     */
    public static int getSleepTotal(int deepTotal, int lightTotal, int soberTotal) {
        //总睡眠
        return deepTotal + lightTotal + soberTotal;
    }

    /**
     * 睡眠有效时长
     * return
     */
    public static int getSleepValidTotal(int deepTotal, int lightTotal) {
        return deepTotal + lightTotal;
    }

}
