package com.truescend.gofit.pagers.home.diet.list;

import com.sn.app.db.data.diet.DietBean;
import com.sn.app.db.data.diet.DietDao;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.MealBean;
import com.sn.app.net.data.app.bean.MealListBean;
import com.sn.app.net.data.app.bean.MealSingleBean;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.DateUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.views.RegionalProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2018/11/22).
 * 功能:餐列表
 */
public class DietListMealPresenterImpl extends BasePresenter<IDietListMealContract.IView> implements IDietListMealContract.IPresenter {
    private IDietListMealContract.IView view;


    public DietListMealPresenterImpl(IDietListMealContract.IView view) {
        this.view = view;
    }


    /**
     * 加载进餐记录,分为网络和本地加载
     */
    @Override
    public void loadMealList() {
        final String currentDate = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
        AppNetReq.getApi().loadMealList(currentDate)
                .enqueue(new OnResponseListener<MealListBean>() {
                    @Override
                    public void onResponse(MealListBean body) throws Throwable {
                        if (!isUIEnable()) return;
                        final MealListBean.DataBean data = body.getData();
                        List<MealBean> meals = null;
                        float targetCalory = 0;
                        float totalCalory = 0;
                        if (data != null && data.getMeals() != null) {
                            meals = data.getMeals();
                            targetCalory = data.getGoal_calory();
                            totalCalory = data.getCalory();

                        } else {
                            targetCalory = AppUserUtil.getUser().getTarget_calory();
                        }
                        view.onLoadMealList(meals);
                        onMealCalorieRange(targetCalory);
                        onMealRegionalProgressBar(meals, targetCalory, totalCalory);
                        onMealCalorieTotal(data==null?0.0f:data.getCalory());

                        final float finalTargetCalory = targetCalory;
                        final float finalTotalCalory = totalCalory;
                        SNAsyncTask.execute(new SNVTaskCallBack() {
                            @Override
                            public void run() throws Throwable {
                                UserBean user = AppUserUtil.getUser();
                                DietDao dietDao = DietDao.get(DietDao.class);
                                DietBean dietBean = dietDao.queryForDate(user.getUser_id(), currentDate);
                                if (data != null) {
                                    dietBean.setDate(data.getDate());
                                    dietBean.setWeight(data.getWeight());
                                    dietBean.setTargetWeight(data.getGoal_weight());
                                    dietBean.setTargetCalory(finalTargetCalory);
                                    dietBean.setTotalCalory(finalTotalCalory);
                                    dietBean.setBasicMetabolismCalorie(26 * data.getWeight());
                                    dietBean.setUser_id(data.getUser_id());
                                    dietBean.setMeals(new ArrayList<>(data.getMeals()));
                                    dietDao.insertOrUpdate(data.getUser_id(), dietBean);
                                } else {
                                    dietDao.delete(user.getUser_id(), currentDate);
                                }
                            }
                        });

                        view.onFinishRefresh();
                    }

                    @Override
                    public void onFailure(int ret, String msg) {
                        if (!isUIEnable()) return;
                        view.onFinishRefresh();


                        SNAsyncTask.execute(new SNVTaskCallBack() {
                            float totalCalory;
                            float targetCalory;
                            ArrayList<MealBean> meals;

                            @Override
                            public void run() throws Throwable {
                                UserBean user = AppUserUtil.getUser();
                                DietDao dietDao = DietDao.get(DietDao.class);
                                DietBean dietBean = dietDao.queryForDate(user.getUser_id(), currentDate);
                                if (dietBean != null) {
                                    meals = dietBean.getMeals();
                                    targetCalory = dietBean.getTargetCalory();
                                    totalCalory = dietBean.getTotalCalory();
                                }else{
                                    targetCalory = AppUserUtil.getUser().getTarget_calory();
                                }
                            }

                            @Override
                            public void done() {
                                super.done();
                                if (!isUIEnable()) return;
                                view.onLoadMealList(meals);
                                onMealRegionalProgressBar(meals, targetCalory, totalCalory);
                                onMealCalorieRange(targetCalory);
                                onMealCalorieTotal(totalCalory);
                            }
                        });
                    }
                });


    }

    private void onMealCalorieRange(float targetCalory) {
        String strHead = ResUtil.getString(R.string.content_intake_tips_head);
        String strfoot = ResUtil.getString(R.string.content_intake_tips_foot);
        String formatHtml = strHead + " <strong><font color=#000000>%.0f-%.0f</font></strong> Kcal " + strfoot;
        view.onMealCalorieRange(ResUtil.formatHtml(formatHtml, targetCalory * 1.05f, targetCalory * 1.1f));
    }

    private void onMealCalorieTotal(float totalCalory) {
        view.onMealCalorieTotal(ResUtil.format("%.0f", totalCalory ));
    }

    private void onMealRegionalProgressBar(final List<MealBean> meals, final float targetCalory, final float totalCalory) {

        SNAsyncTask.execute(new SNVTaskCallBack() {
            boolean isOutOfSize;
            List<RegionalProgressBar.OccupiesValue> chartValues = new ArrayList<>();

            @Override
            public void run() throws Throwable {

                if (meals != null) {
                    int size = meals.size();
                    float goal_calory = (targetCalory * 1.1f);

                    float increment = 0;
                    if (goal_calory <= totalCalory) {
                        float outOfSize = totalCalory - goal_calory;
                        increment = outOfSize / size;
                    }

                    for (int i = 0; i < size; i++) {
                        MealBean mealBean = meals.get(i);
                        chartValues.add(new RegionalProgressBar.OccupiesValue(RegionalProgressBar.COLOR_HIGHLIGHT, mealBean.getCalory() + increment, mealBean.getCalory()));
                    }
                    if (goal_calory > totalCalory) {
                        float abs = Math.abs(goal_calory - totalCalory);
                        chartValues.add(new RegionalProgressBar.OccupiesValue(RegionalProgressBar.COLOR_NONE, abs, abs));
                    }

                    isOutOfSize = increment > 0;

                } else {
                    chartValues.add(new RegionalProgressBar.OccupiesValue(RegionalProgressBar.COLOR_NONE, 1, 0));
                    isOutOfSize = false;
                }
            }

            @Override
            public void done() {
                super.done();
                if (!isUIEnable()) return;
                view.onMealRegionalProgressBar(chartValues, isOutOfSize);
            }
        });
    }

    @Override
    public void deleteMeal(final int position, int id) {
        view.onDialogLoading(ResUtil.getString(R.string.loading));
        AppNetReq.getApi().deleteMeal(id).enqueue(new OnResponseListener<MealSingleBean>() {
            @Override
            public void onResponse(MealSingleBean body) throws Throwable {
                if (!isUIEnable()) return;
                view.onMealDelete(position);
                view.onDialogDismiss();
                loadMealList();
            }

            @Override
            public void onFailure(int ret, String msg) {
                if (!isUIEnable()) return;
                view.onDialogDismiss();
            }
        });
    }
}
