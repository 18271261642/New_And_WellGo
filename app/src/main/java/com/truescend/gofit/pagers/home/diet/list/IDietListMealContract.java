package com.truescend.gofit.pagers.home.diet.list;


import com.sn.app.net.data.app.bean.MealBean;
import com.truescend.gofit.views.RegionalProgressBar;

import java.util.List;

/**
 * 作者:东芝(2018/11/22).
 * 功能:餐列表
 */

public class IDietListMealContract {

    interface IView {
        void onDialogLoading(String msg);

        void onDialogDismiss();

        void onLoadMealList(List<MealBean> data);

        void onMealCalorieTotal(CharSequence totalCalorie);
        void onMealCalorieRange(CharSequence rangeCalorie);

        void onMealDelete(int position);

        void onMealRegionalProgressBar(List<RegionalProgressBar.OccupiesValue> values, boolean isOutOfSize);

        void onFinishRefresh();
    }


    interface IPresenter {

        void loadMealList();

        void deleteMeal(int position, int id);
    }
}
