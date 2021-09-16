package com.truescend.gofit.utils;

import com.sn.app.db.data.user.UserBean;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.health.blood_oxygen.BloodOxygenBean;
import com.sn.blesdk.db.data.health.blood_oxygen.BloodOxygenDao;
import com.sn.blesdk.db.data.health.blood_pressure.BloodPressureBean;
import com.sn.blesdk.db.data.health.blood_pressure.BloodPressureDao;
import com.sn.blesdk.db.data.health.heart_rate.HeartRateBean;
import com.sn.blesdk.db.data.health.heart_rate.HeartRateDao;
import com.sn.blesdk.db.data.sleep.SleepBean;
import com.sn.blesdk.db.data.sleep.SleepDao;
import com.sn.blesdk.db.data.sport.SportBean;
import com.sn.blesdk.db.data.sport.SportDao;
import com.sn.blesdk.net.HealthBloodOxygenDataNetworkSyncHelper;
import com.sn.blesdk.net.HealthBloodPressureDataNetworkSyncHelper;
import com.sn.blesdk.net.HealthHeartRateDataNetworkSyncHelper;
import com.sn.blesdk.net.SleepDataNetworkSyncHelper;
import com.sn.blesdk.net.SportDataNetworkSyncHelper;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.net.DietDataNetworkSyncHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * 作者:东芝(2018/4/4).
 * 功能:网络数据同步
 */

public class NetworkSyncHelper {

    public static void startAutoSync() {
        String beginDate = "2017-01-01";
        //今天
        String endDate = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);

        UserBean user = AppUserUtil.getUser();
        if (user == null) return;

        final int user_id = user.getUser_id();

        long network_sport_days = user.getSport_days();
        long local_sport_days = getLocalSportDays(user_id);

        //网络上的运动总数<本地已存在的运动总数
        if (network_sport_days < local_sport_days) {
            //说明本地有数据还没上传上去
            upLoadData(user_id);
        } else {
            if (local_sport_days != 0 && network_sport_days == local_sport_days) {
                //说明本地数据和网络数据一致,只拉今天
                //把开始时间设置为今天,结束时间也是今天
                beginDate = endDate;
            }
            //下载
            SportDataNetworkSyncHelper.downloadFromServer(beginDate, endDate);
            SleepDataNetworkSyncHelper.downloadFromServer(beginDate, endDate);
            HealthHeartRateDataNetworkSyncHelper.downloadFromServer(beginDate, endDate);
            HealthBloodOxygenDataNetworkSyncHelper.downloadFromServer(beginDate, endDate);
            HealthBloodPressureDataNetworkSyncHelper.downloadFromServer(beginDate, endDate);
        }
        if (BuildConfig.isSupportDiet) {
            if (user.getTotal_meal_day() >= 0) {
                beginDate = user.getFirst_meal_date();
                DietDataNetworkSyncHelper.downloadFromServer(user_id, beginDate, endDate);
            }
        }
    }

    private static void upLoadData(final int user_id) {
        //上传...
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                try {
                    List<SportBean> beans = SportDao.get(SportDao.class).queryNotUpload(user_id);
                    if (!IF.isEmpty(beans)) {
                        SportDataNetworkSyncHelper.uploadToServer(beans);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    List<SleepBean> beans = SleepDao.get(SleepDao.class).queryNotUpload(user_id);
                    if (!IF.isEmpty(beans)) {
                        SleepDataNetworkSyncHelper.uploadToServer(beans);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    List<HeartRateBean> beans = HeartRateDao.get(HeartRateDao.class).queryNotUpload(user_id);
                    if (!IF.isEmpty(beans)) {
                        HealthHeartRateDataNetworkSyncHelper.uploadToServer(beans);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    List<BloodOxygenBean> beans = BloodOxygenDao.get(BloodOxygenDao.class).queryNotUpload(user_id);
                    if (!IF.isEmpty(beans)) {
                        HealthBloodOxygenDataNetworkSyncHelper.uploadToServer(beans);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    List<BloodPressureBean> beans = BloodPressureDao.get(BloodPressureDao.class).queryNotUpload(user_id);
                    if (!IF.isEmpty(beans)) {
                        HealthBloodPressureDataNetworkSyncHelper.uploadToServer(beans);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static long getLocalSportDays(int user_id) {
        long local_sport_days = 0;
        try {
            local_sport_days =
                    SportDao.get(SportDao.class)
                            .getDao()
                            .queryBuilder()
                            .where()
                            .eq(SportBean.COLUMN_USER_ID, user_id)
                            .and()
                            .gt(SportBean.COLUMN_STEP_TOTAL, 0)
                            .countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return local_sport_days;
    }

}
