package com.sn.blesdk.db.data.health.heart_rate;

import com.dz.blesdk.utils.BLELog;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.net.HealthHeartRateDataNetworkSyncHelper;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.SNLog;
import com.sn.utils.eventbus.SNEventBus;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 作者:东芝(2017/12/21).
 * 功能:心率数据存储助手类
 */

public class HeartRateStorageHelper {

    //TODO 这个应该让界面层 保存, 因为是实时传输的数据 需要界面显示完后 点结束 或倒计时结束 才保存平均值 这里使用的是 假保存 后面界面出来 这里要改!!!

    /**
     * 实时数据保存
     *
     * @param deviceMacAddress
     * @param mHeartRate
     */
    public static void asyncSaveRealTimeData(final String deviceMacAddress, final int mHeartRate) {
        final Calendar currentCalendar = DateUtil.getCurrentCalendar();
        final int timeIndex = DateUtil.convertTimeToIndex(currentCalendar, 1);
        //心率
        if (mHeartRate != 0) {
            SNAsyncTask.execute(new SNVTaskCallBack() {
                @Override
                public void run() throws Throwable {
                    BLELog.d("心率实时数据:本地保存开始");
                    //保存一条数据
                    ArrayList<HeartRateBean.HeartRateDetailsBean> mNewHeartRateDetails = new ArrayList<>();
                    mNewHeartRateDetails.add(new HeartRateBean.HeartRateDetailsBean(timeIndex, DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, currentCalendar), mHeartRate, 0/*(默认0=手动检测,1=自动检测)*/));
                    save(deviceMacAddress, mNewHeartRateDetails);
                }

                @Override
                public void done() {
                    BLELog.d("心率实时数据:本地保存完成");
                    SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_HEALTH_CHECK_HEART_RATE, mHeartRate);
                    SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_HEART_RATE_DATA);
                    try {
                        List<HeartRateBean> beans = HeartRateDao.get(HeartRateDao.class).queryNotUpload(AppUserUtil.getUser().getUser_id());
                        if (!IF.isEmpty(beans)) {
                            HealthHeartRateDataNetworkSyncHelper.uploadToServer(beans);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void error(Throwable e) {
                    BLELog.d("心率实时数据:本地保存失败" + e);
                }
            });
        }

    }

    /**
     * 保存心率大数据数据
     */
    public static void asyncSaveData(final String deviceMacAddress, final ArrayList<HeartRateBean.HeartRateDetailsBean> heartRates) {
        //非空判断
        if (IF.isEmpty(heartRates)) {
            BLELog.w("心率大数据:心率大数据有丢失,不保存 数据数量:" + heartRates.size());
            return;
        }
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                BLELog.w("心率大数据:本地保存开始");
                //把解析出来的数据 按天分割 分别装
                List<ArrayList<HeartRateBean.HeartRateDetailsBean>> heartRateDetailSet = getList(heartRates);
                //数据量(天数)
                int size = heartRateDetailSet.size();
                //保存两天的数据(如果有的画)
                for (int i = 0; i < size; i++) {
                    save(deviceMacAddress, heartRateDetailSet.get(i));
                }
            }

            @Override
            public void done() {
                BLELog.w("心率大数据:本地保存完成");
                SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_HEART_RATE_DATA);
                try {
                    List<HeartRateBean> beans = HeartRateDao.get(HeartRateDao.class).queryNotUpload(AppUserUtil.getUser().getUser_id());
                    if (!IF.isEmpty(beans)) {
                        HealthHeartRateDataNetworkSyncHelper.uploadToServer(beans);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(Throwable e) {
                SNLog.i("心率大数据:本地保存失败:" + e);
            }
        });


    }

    /**
     * 保存心率数据
     *
     * @param mac
     * @param mNewHeartRateDetails 新数据(实时数据/新历史数据)
     * @return
     * @throws ParseException
     * @throws java.sql.SQLException
     */
    private static boolean save(String mac, ArrayList<HeartRateBean.HeartRateDetailsBean> mNewHeartRateDetails) throws ParseException, java.sql.SQLException {
        HeartRateDao heartRateDao = SNBLEDao.get(HeartRateDao.class);
        String date = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.YYYY_MM_DD, mNewHeartRateDetails.get(0).getDateTime());
        //查询这天的心率历史对象
        List<HeartRateBean> mHistoryHeartRateBeans = heartRateDao.queryForDay(AppUserUtil.getUser().getUser_id(), date);
        if (!IF.isEmpty(mHistoryHeartRateBeans)) {
            //取第0条  只有一条 所以不会超出数组
            HeartRateBean heartRateBean = mHistoryHeartRateBeans.get(0);
            //取出心率详情历史
            ArrayList<HeartRateBean.HeartRateDetailsBean> mHistoryHeartRateDetails = heartRateBean.getHeartRateDetails();
            if (!IF.isEmpty(mHistoryHeartRateBeans)) {
                boolean isSameDevice = mac.equalsIgnoreCase(heartRateBean.getMac());
                boolean isToday = DateUtil.equalsToday(date);
                //不同手环,昨天的数据不做保存处理. 对用户来说 昨天的数据不能再变,除非是同一个手环
                if(!isSameDevice&&!isToday){
                    return true;
                }
                merge(isSameDevice,isToday, mHistoryHeartRateDetails, mNewHeartRateDetails);
            }
        }

        HeartRateBean heartRateBean = new HeartRateBean();
        heartRateBean.setUser_id(AppUserUtil.getUser().getUser_id());
        heartRateBean.setHeartRateDetails(mNewHeartRateDetails);
        heartRateBean.setMac(mac);
        int[] minAvgMaxTotal = getMinAvgMaxTotal(mNewHeartRateDetails);
        heartRateBean.setMin(minAvgMaxTotal[0]);
        heartRateBean.setAvg(minAvgMaxTotal[1]);
        heartRateBean.setMax(minAvgMaxTotal[2]);
        heartRateBean.setDate(date);

        boolean success = heartRateDao.insertOrUpdate(AppUserUtil.getUser().getUser_id(), heartRateBean);
        return success;
    }


    /**
     * 合并
     * 从mFromLocalData 合并到mToDeviceData, 最后请使用mToDeviceData作为存储
     *
     * @param isSameDevice
     * @param isToday
     * @param mFromLocalData
     * @param mToDeviceData
     */
    private static void merge(boolean isSameDevice,boolean isToday, List<HeartRateBean.HeartRateDetailsBean> mFromLocalData, List<HeartRateBean.HeartRateDetailsBean> mToDeviceData) {

        //合并集合
        List<HeartRateBean.HeartRateDetailsBean> mMergeData = new ArrayList<>();
        //覆盖集合
        List<HeartRateBean.HeartRateDetailsBean> mCoverData = new ArrayList<>();

        int curTimeIndex = isToday?DateUtil.convertTimeToIndex(DateUtil.getCurrentCalendar(), 1)+1:1440;
        //把所有自动检测的数据 重新填充成1440个分钟的数据,不然合并很麻烦
        for (int index = 0; index < curTimeIndex; index++) {
            HeartRateBean.HeartRateDetailsBean mAutoLocalBeanWithIndex = findBeanWithIndex(mFromLocalData, index);
            HeartRateBean.HeartRateDetailsBean mAutoDeviceBeanWithIndex = findBeanWithIndex(mToDeviceData, index);

            //没数据则跳过
            if(mAutoLocalBeanWithIndex==null&&mAutoDeviceBeanWithIndex==null){
                continue;
            }
            HeartRateBean.HeartRateDetailsBean mTemp = null;
            boolean hasLocal = mAutoLocalBeanWithIndex != null && mAutoLocalBeanWithIndex.getValue() > 0;
            boolean hasDevice = mAutoDeviceBeanWithIndex != null && mAutoDeviceBeanWithIndex.getValue() > 0;
            //本地有,设备无
            if(hasLocal &&!hasDevice){
                //按本地
                mMergeData.add(mTemp=mAutoLocalBeanWithIndex);
            }
            //本地无,设备有
            if(hasDevice &&!hasLocal){
                //按设备
                mMergeData.add(mTemp=mAutoDeviceBeanWithIndex);
                mCoverData.add(mAutoDeviceBeanWithIndex);
            }
            //本地有,设备有
            if(hasLocal && hasDevice){
                //按设备的来,因为优先要和设备的数据同步
                mMergeData.add(mTemp=mAutoDeviceBeanWithIndex);
                mCoverData.add(mAutoDeviceBeanWithIndex);
            }
            //取出手动检测数据
            if (mTemp!=null&&mTemp.getValue()>0&&mTemp.getType() == 0)//默认0=手动检测,1=自动检测
            {
                mCoverData.add(mTemp);
            }
        }
        //同一手环
        if(isSameDevice){
            //到此 已经把历史心率和设备心率合并起来了  然后可能是乱序的,  于是按index 排一下序
            Collections.sort(mMergeData, new Comparator<HeartRateBean.HeartRateDetailsBean>() {
                @Override
                public int compare(HeartRateBean.HeartRateDetailsBean o1, HeartRateBean.HeartRateDetailsBean o2) {
                    return o1.getIndex() - o2.getIndex();
                }
            });
            mToDeviceData.clear();
            mToDeviceData.addAll(mMergeData);
        }else{//不同手环
            //今天覆盖(但保留手动检测)
            if(isToday)
            {
                //到此 已经把设备心率和本地手动检测数据合并起来了  然后可能是乱序的,  于是按index 排一下序
                Collections.sort(mCoverData, new Comparator<HeartRateBean.HeartRateDetailsBean>() {
                    @Override
                    public int compare(HeartRateBean.HeartRateDetailsBean o1, HeartRateBean.HeartRateDetailsBean o2) {
                        return o1.getIndex() - o2.getIndex();
                    }
                });
                mToDeviceData.clear();
                mToDeviceData.addAll(mCoverData);
            }else{//昨天的不处理
                //不处理(不处理在方法外面已经判断了 这里不用写了)
            }
        }

        SNLog.i("心率合并测试:");

    }

    private static HeartRateBean.HeartRateDetailsBean findBeanWithIndex(List<HeartRateBean.HeartRateDetailsBean> mSources, int index) {
        for (HeartRateBean.HeartRateDetailsBean detailsBean : mSources) {
            if (index == detailsBean.getIndex()) {
                return detailsBean;
            }
        }
        return null;
    }


    /**
     * 列表对象中是否含有这个对象
     *
     * @param list
     * @param obj
     * @return
     */
    private static boolean contains(ArrayList<HeartRateBean.HeartRateDetailsBean> list, HeartRateBean.HeartRateDetailsBean obj) {
        if (list == null) return false;
        for (HeartRateBean.HeartRateDetailsBean bean : list) {
            if (bean.getIndex() == obj.getIndex()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 把n个数据转成n个96的集合
     *
     * @param list
     * @param <T>
     * @return
     */
    private static <T> List<ArrayList<T>> getList(List<T> list) {
        List<ArrayList<T>> beans = new ArrayList<>();
        for (int i = 0; i < list.size(); ) {
            beans.add(new ArrayList<>(list.subList(i, i += 96)));
        }
        return beans;
    }


    /**
     * 取得大中小
     *
     * @param list
     * @return
     */
    public static int[] getMinAvgMaxTotal(List<? extends HeartRateBean.HeartRateDetailsBean> list) {
        int[] min_avg_max = new int[3];
        int total = 0;
        int total_count = 0;
        min_avg_max[0] = Integer.MAX_VALUE;
        min_avg_max[2] = -Integer.MAX_VALUE;
        for (HeartRateBean.HeartRateDetailsBean bean : list) {
            int value = bean.getValue();
            if (value == 0) {
                continue;
            }
            min_avg_max[0] = Math.min(min_avg_max[0], value);
            min_avg_max[2] = Math.max(min_avg_max[2], value);
            total += value;
            total_count++;
        }
        if (total > 0) {
            min_avg_max[1] = Math.round(total / total_count);
        } else {
            min_avg_max[0] = 0;
            min_avg_max[1] = 0;
            min_avg_max[2] = 0;
        }
        return min_avg_max;
    }

}
