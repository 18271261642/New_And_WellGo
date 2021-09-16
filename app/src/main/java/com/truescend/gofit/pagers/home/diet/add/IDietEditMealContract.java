package com.truescend.gofit.pagers.home.diet.add;


import com.sn.app.net.data.app.bean.MealBean;
import com.sn.app.net.data.app.bean.SearchFoodResultBean;

import java.util.List;

/**
 * 作者:东芝(2018/11/22).
 * 加餐/更新餐
 */

public class IDietEditMealContract {

    interface IView {
        void onDialogLoading(String msg);

        void onDialogDismiss();

        void onSearchFoodResult(List<SearchFoodResultBean.DataBean> foodResults);

        void onAddMeal(boolean success, String error);

        void onUpdateMeal(boolean success, String error);
    }

    interface IPresenter {
        void searchFoodKeywords(String keyword);

        void addMeal(MealBean mealBean);

        void updateMeal(MealBean mealBean);
    }
}
