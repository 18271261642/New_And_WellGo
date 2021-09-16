package com.sn.blesdk.control;

import com.dz.blesdk.utils.BLELog;
import com.j256.ormlite.stmt.Where;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.sport_mode.SportModeBean;
import com.sn.blesdk.db.data.sport_mode.SportModeDao;
import com.sn.blesdk.interfaces.IDataDecodeHelper;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.eventbus.SNEventBus;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 作者:东芝(2019/6/3).
 * 功能:运动模式解码
 */

public class SportModeDecodeHelper implements IDataDecodeHelper {

    private ArrayList<SportModeBean> sportModeBeans = new ArrayList<>();

    @Override
    public void decode(byte[] buffer) {
        if (SNBLEHelper.startWith(buffer, "05070B")) {

            int index = buffer[3] & 0xff;
            if(index==0){
                sportModeBeans.clear();
            }

            int type = buffer[4] & 0xff;
            //从2000-01-01 00:00:00至今的秒数
            int startTimeSeconds = SNBLEHelper.subBytesToInt(buffer, 4, 5, 8);

            Calendar instance = Calendar.getInstance();
            instance.set(2000,0,1,0,0,0);
            instance.add(Calendar.SECOND,startTimeSeconds);

            String beginDateTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, instance);
            String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, instance);
            int takeMinutes = SNBLEHelper.subBytesToInt(buffer, 2, 9, 10);
            instance.add(Calendar.MINUTE,takeMinutes);
            String endDateTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, instance);

            int step = SNBLEHelper.subBytesToInt(buffer, 2, 11, 12);
            int distance = SNBLEHelper.subBytesToInt(buffer, 2, 13, 14);
            int calories = SNBLEHelper.subBytesToInt(buffer, 2, 15, 16);
            int rateMaxHeart = buffer[17] & 0xFF;
            int rateMinHeart = buffer[18] & 0xFF;
            int rateAvgHeart = buffer[19] & 0xFF;

            BLELog.d("运动模式数据:类型:%d,开始时间:%s,结束时间:%s,持续时间:%d分钟", type, beginDateTime,endDateTime, takeMinutes);
            BLELog.d("运动模式数据:步数:%d,距离:%d,卡路里:%d,心率最大:%d,最小:%d,平均:%d", step, distance, calories, rateMaxHeart, rateMinHeart, rateAvgHeart);

            SportModeBean sportModeBean = new SportModeBean();
            sportModeBean.setModeType(type);
            sportModeBean.setBeginDateTime(beginDateTime);
            sportModeBean.setEndDateTime(endDateTime);
            sportModeBean.setTakeMinutes(takeMinutes);
            sportModeBean.setStep(step);
            sportModeBean.setDistance(distance);
            sportModeBean.setCalorie(calories);
            sportModeBean.setHeartRateMax(rateMaxHeart);
            sportModeBean.setHeartRateMin(rateMinHeart);
            sportModeBean.setHeartRateAvg(rateAvgHeart);
            sportModeBean.setDate(date);
            sportModeBeans.add(sportModeBean);

        }
        //运动模式接收完毕
        if (SNBLEHelper.startWith(buffer, "0507F901")){
            BLELog.w("运动模式数据:解析完成");
            asyncSaveData();
        }
    }




    /**
     * 存储实时数据到历史步数大数据里
     */
    private void asyncSaveData() {
        if(IF.isEmpty(sportModeBeans)){
            BLELog.w("运动模式数据:解析有丢失或无数据,不保存");
            return;
        }
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                BLELog.w("运动模式数据:本地保存开始");
                SportModeDao sportModeDao = SNBLEDao.get(SportModeDao.class);
                String mac = SNBLEHelper.getDeviceMacAddress();
                int user_id = AppUserUtil.getUser().getUser_id();

                for (SportModeBean sportModeBean : sportModeBeans) {
                    sportModeBean.setUser_id(user_id);
                    sportModeBean.setMac(mac);
                    //以开始时间作为唯一
                    Where<SportModeBean, Integer> where = sportModeDao.getDao().queryBuilder().where();
                    where.eq(SportModeBean.COLUMN_BEGIN_DATE_TIME, sportModeBean.getBeginDateTime()).and().eq(SportModeBean.COLUMN_USER_ID, user_id);
                    sportModeDao.insertOrUpdate(sportModeBean,where);
                }

                //TODO 上传数据到服务器, 目前不需要写
                //SportModeDataNetworkSyncHelper.uploadToServer(uploadList);
            }

            @Override
            public void done() {
                super.done();
                BLELog.w("运动模式数据:本地保存完成");
                SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_SPORT_MODE_DATA);
            }

            @Override
            public void error(Throwable e) {
                super.error(e);
                BLELog.w("运动模式数据:本地保存失败:" + e);
            }
        });
    }
}
