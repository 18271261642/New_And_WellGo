package com.truescend.gofit.net;

import com.sn.app.db.data.diet.DietBean;
import com.sn.app.db.data.diet.DietDao;
import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.MeaHistoryBean;
import com.sn.app.net.data.app.bean.MealListBean;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.SNLog;
import com.sn.utils.eventbus.SNEventBus;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.utils.AppEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2018/11/28).
 * 功能:饮食数据同步器
 */

public class DietDataNetworkSyncHelper {
    public static void downloadFromServer(final int user_id, String beginDate, final String endDate) {
        if(IF.isEmpty(beginDate)){
            beginDate = endDate;
        }
        try {
            //处理: 获取数据库最后一条, 如果能拿到说明以前拉过, 就取最后一次的日期为beginDate,  endDate默认是今天
            //如果beginDate==endDate则只拉今天
            DietBean dietBean = DietDao.get(DietDao.class).queryForLast(user_id);
            String date = dietBean.getDate();
            if (!date.equalsIgnoreCase(beginDate)) {
                beginDate = date;
            }

        } catch (Exception ignored) {
        }
        final boolean sameToDay = DateUtil.equalsDate(beginDate, endDate) && DateUtil.equalsToday(beginDate);
        AppNetReq.getApi().loadMealList(beginDate, endDate).enqueue(new OnResponseListener<MeaHistoryBean>() {
            @Override
            public void onResponse(MeaHistoryBean body) throws Throwable {
                final List<MealListBean.DataBean> data = body.getData();
                SNAsyncTask.execute(new SNVTaskCallBack() {
                    @Override
                    public void run() throws Throwable {


                        DietDao dietDao = DietDao.get(DietDao.class);
                        if (IF.isEmpty(data)) {
                            if (sameToDay) {//服务器可能删了今天的数据
                                dietDao.delete(user_id,endDate);
                            }
                        } else {
                            for (MealListBean.DataBean datum : data) {
                                DietBean dietBean = new DietBean();
                                dietBean.setUser_id(datum.getUser_id());
                                dietBean.setDate(datum.getDate());
                                dietBean.setWeight(datum.getWeight());
                                dietBean.setTargetWeight(datum.getGoal_weight());
                                dietBean.setTargetCalory(datum.getGoal_calory());
                                dietBean.setTotalCalory(datum.getCalory());
                                dietBean.setBasicMetabolismCalorie(26 * datum.getWeight());
                                dietBean.setMeals(new ArrayList<>(datum.getMeals()));
                                dietDao.insertOrUpdate(datum.getUser_id(), dietBean);
                            }
                        }
                        SNLog.i("饮食数据拉取完成");
                    }

                    @Override
                    public void done() {
                        super.done();
                        SNEventBus.sendEvent(AppEvent.EVENT_SYNC_DIET_STATISTICS_DATA_SUCCESS);
                    }
                });
            }

            @Override
            public void onFailure(int ret, String msg) {

            }
        });
    }
}
