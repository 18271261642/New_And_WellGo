package com.truescend.gofit.utils;

import com.sn.app.db.data.user.UserBean;
import com.sn.utils.DateUtil;
import com.sn.utils.tuple.Tuple;
import com.sn.utils.tuple.TupleTwo;

import java.text.ParseException;

/**
 * 作者:东芝(2018/12/3).
 * 功能:基础新陈代谢
 */

public class BMRUtil {

    public static TupleTwo<Integer,Integer> getBMRRange(float mCurHeight,float mCurWeight,UserBean user){

        int gender = user.getGender();
        int age = 0;
        try {
            age = DateUtil.getYear(System.currentTimeMillis()) - DateUtil.getYear(DateUtil.convertStringToDate(DateUtil.YYYY_MM_DD, user.getBirthday()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        float minBMR, maxBMR;
        if (gender == 1) {//男
            minBMR = (10 * mCurWeight) + (6.25f * mCurHeight) - (5 * age) + 5;
        } else {
            minBMR = (10 * mCurWeight) + (6.25f * mCurHeight) - (5 * age) - 161;
        }
        maxBMR = minBMR*1.5f;


        return Tuple.create(upwardFix(minBMR),upwardFix(maxBMR));
    }

    private static int upwardFix(float bmr) {
        return ((int) (bmr / 100)+1) * 100;
    }

}
