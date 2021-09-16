package com.truescend.gofit.pagers.home.diet.add;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.MealBean;
import com.sn.app.net.data.app.bean.MealSingleBean;
import com.sn.app.net.data.app.bean.SearchFoodResultBean;
import com.sn.net.utils.JsonUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;

import java.util.List;

/**
 * 作者:东芝(2018/11/22).
 * 加餐/更新餐
 */
public class DietEditMealPresenterImpl extends BasePresenter<IDietEditMealContract.IView> implements IDietEditMealContract.IPresenter {
    private IDietEditMealContract.IView view;


    public DietEditMealPresenterImpl(IDietEditMealContract.IView view) {
        this.view = view;
    }


    @Override
    public void searchFoodKeywords(String keyword) {
        AppNetReq.getApi().searchFoods(keyword).enqueue(new OnResponseListener<SearchFoodResultBean>() {
            @Override
            public void onResponse(SearchFoodResultBean body) throws Throwable {
                if(!isUIEnable())return;
                List<SearchFoodResultBean.DataBean> foodResults = body.getData();
                view.onSearchFoodResult( foodResults );
            }

            @Override
            public void onFailure(int ret, String msg) {

            }
        });
    }

    @Override
    public void addMeal(MealBean mealBean) {
        view.onDialogLoading(ResUtil.getString(R.string.loading));
        AppNetReq.getApi().addMeal(
                mealBean.getDate(),
                mealBean.getCalory(),
                JsonUtil.toJson(mealBean.getFoods())
        ).enqueue(new OnResponseListener<MealSingleBean>() {
            @Override
            public void onResponse(MealSingleBean body) throws Throwable {
                if(!isUIEnable())return;
                view.onAddMeal(true,null);
                view.onDialogDismiss();
            }

            @Override
            public void onFailure(int ret, String msg) {
                if(!isUIEnable())return;
                view.onAddMeal(false,msg);
                view.onDialogDismiss();
            }
        });
    }

    @Override
    public void updateMeal(MealBean mealBean) {
        view.onDialogLoading(ResUtil.getString(R.string.loading));
        AppNetReq.getApi().updateMeal(
                mealBean.getId(),
                mealBean.getCalory(),
                JsonUtil.toJson(mealBean.getFoods())
        ).enqueue(new OnResponseListener<MealSingleBean>() {
            @Override
            public void onResponse(MealSingleBean body) throws Throwable {
                if(!isUIEnable())return;
                view.onUpdateMeal(true,null);
                view.onDialogDismiss();
            }

            @Override
            public void onFailure(int ret, String msg) {
                if(!isUIEnable())return;
                view.onUpdateMeal(false,msg);
                view.onDialogDismiss();
            }
        });
    }

}
