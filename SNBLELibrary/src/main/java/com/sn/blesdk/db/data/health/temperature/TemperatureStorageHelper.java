package com.sn.blesdk.db.data.health.temperature;

import com.dz.blesdk.utils.BLELog;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.net.HealthTemperatureDataNetworkSyncHelper;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 作者:东芝(2017/12/21).
 * 功能:体温数据存储助手类
 */

public class TemperatureStorageHelper {

    //TODO 这个应该让界面层 保存, 因为是实时传输的数据 需要界面显示完后 点结束 或倒计时结束 才保存平均值 这里使用的是 假保存 后面界面出来 这里要改!!!

//    /**
//     * 实时数据保存
//     *
//     * @param deviceMacAddress
//     * @param value
//     */
//    public static void asyncSaveRealTimeData(final String deviceMacAddress, final int value) {
//        final Calendar currentCalendar = DateUtil.getCurrentCalendar();
//        final int timeIndex = DateUtil.convertTimeToIndex(currentCalendar, 1);
//        //体温
//        if (value != 0) {
//            SNAsyncTask.execute(new SNVTaskCallBack() {
//                @Override
//                public void run() throws Throwable {
//                    BLELog.d("体温实时数据:本地保存开始");
//                    //保存一条数据
//                    ArrayList<TemperatureBean.TemperatureDetailsBean> mNewHeartRateDetails = new ArrayList<>();
//                    mNewHeartRateDetails.add(new TemperatureBean.TemperatureDetailsBean(timeIndex, DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, currentCalendar), value, 0/*(默认0=手动检测,1=自动检测)*/));
//                    save(deviceMacAddress, mNewHeartRateDetails);
//                }
//
//                @Override
//                public void done() {
//                    BLELog.d("体温实时数据:本地保存完成");
//                    //SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_HEALTH_CHECK_HEART_RATE, value);
//                    SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_TEMPERATURE_DATA);
//                    try {
//                        List<TemperatureBean> beans = TemperatureDao.get(TemperatureDao.class).queryNotUpload(AppUserUtil.getUser().getUser_id());
//                        if (!IF.isEmpty(beans)) {
//                            HealthTemperatureDataNetworkSyncHelper.uploadToServer(beans);
//                        }
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                @Override
//                public void error(Throwable e) {
//                    BLELog.d("体温实时数据:本地保存失败" + e);
//                }
//            });
//        }
//
//    }

    /**
     * 保存体温大数据数据
     */
    public static void asyncSaveData(final String deviceMacAddress, final ArrayList<TemperatureBean.TemperatureDetailsBean> temperatureDetailsBeans) {
        //非空判断
        if (IF.isEmpty(temperatureDetailsBeans)) {
            BLELog.w("体温大数据:体温大数据有丢失,不保存 数据数量:" + temperatureDetailsBeans.size());
            return;
        }
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                BLELog.w("体温大数据:本地保存开始");
                //把解析出来的数据 按天分割 分别装
                List<ArrayList<TemperatureBean.TemperatureDetailsBean>> list = getList(temperatureDetailsBeans);
                //数据量(天数)
                int size = list.size();
                //保存两天的数据(如果有的画)
                for (int i = 0; i < size; i++) {
                    save(deviceMacAddress, list.get(i));
                }
            }

            @Override
            public void done() {
                BLELog.w("体温大数据:本地保存完成");
                SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_TEMPERATURE_DATA);
                try {
                    List<TemperatureBean> beans = TemperatureDao.get(TemperatureDao.class).queryNotUpload(AppUserUtil.getUser().getUser_id());
                    if (!IF.isEmpty(beans)) {
                        HealthTemperatureDataNetworkSyncHelper.uploadToServer(beans);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void error(Throwable e) {
                SNLog.i("体温大数据:本地保存失败:" + e);
            }
        });


    }

    /**
     * 保存体温数据
     *
     * @param mac
     * @param mNewHeartRateDetails 新数据(实时数据/新历史数据)
     * @return
     * @throws ParseException
     * @throws SQLException
     */
    private static boolean save(String mac, ArrayList<TemperatureBean.TemperatureDetailsBean> mNewHeartRateDetails) throws ParseException, SQLException {
        TemperatureDao temperatureDao = SNBLEDao.get(TemperatureDao.class);
        String date = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.YYYY_MM_DD, mNewHeartRateDetails.get(0).getDateTime());
        //查询这天的体温历史对象
        List<TemperatureBean> mHistoryTemperatureBeans = temperatureDao.queryForDay(AppUserUtil.getUser().getUser_id(), date);
        if (!IF.isEmpty(mHistoryTemperatureBeans)) {
            //取第0条  只有一条 所以不会超出数组
            TemperatureBean heartRateBean = mHistoryTemperatureBeans.get(0);
            //取出体温详情历史
            ArrayList<TemperatureBean.TemperatureDetailsBean> mHistoryHeartRateDetails = heartRateBean.getTemperatureDetailsBeans();
            if (!IF.isEmpty(mHistoryTemperatureBeans)) {
                boolean isSameDevice = mac.equalsIgnoreCase(heartRateBean.getMac());
                boolean isToday = DateUtil.equalsToday(date);
                //不同手环,昨天的数据不做保存处理. 对用户来说 昨天的数据不能再变,除非是同一个手环
                if(!isSameDevice&&!isToday){
                    return true;
                }
                merge(isSameDevice,isToday, mHistoryHeartRateDetails, mNewHeartRateDetails);
            }
        }

        TemperatureBean heartRateBean = new TemperatureBean();
        heartRateBean.setUser_id(AppUserUtil.getUser().getUser_id());
        heartRateBean.setTemperatureDetailsBeans(mNewHeartRateDetails);
        heartRateBean.setMac(mac);
        int[] minAvgMaxTotal = getMinAvgMaxTotal(mNewHeartRateDetails);
        heartRateBean.setMin(minAvgMaxTotal[0]);
        heartRateBean.setAvg(minAvgMaxTotal[1]);
        heartRateBean.setMax(minAvgMaxTotal[2]);
        heartRateBean.setDate(date);

        boolean success = temperatureDao.insertOrUpdate(AppUserUtil.getUser().getUser_id(), heartRateBean);
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
    private static void merge(boolean isSameDevice, boolean isToday, List<TemperatureBean.TemperatureDetailsBean> mFromLocalData, List<TemperatureBean.TemperatureDetailsBean> mToDeviceData) {

        //合并集合
        List<TemperatureBean.TemperatureDetailsBean> mMergeData = new ArrayList<>();
        //覆盖集合
        List<TemperatureBean.TemperatureDetailsBean> mCoverData = new ArrayList<>();

        int curTimeIndex = isToday?DateUtil.convertTimeToIndex(DateUtil.getCurrentCalendar(), 1)+1:1440;
        //把所有自动检测的数据 重新填充成1440个分钟的数据,不然合并很麻烦
        for (int index = 0; index < curTimeIndex; index++) {
            TemperatureBean.TemperatureDetailsBean mAutoLocalBeanWithIndex = findBeanWithIndex(mFromLocalData, index);
            TemperatureBean.TemperatureDetailsBean mAutoDeviceBeanWithIndex = findBeanWithIndex(mToDeviceData, index);

            //没数据则跳过
            if(mAutoLocalBeanWithIndex==null&&mAutoDeviceBeanWithIndex==null){
                continue;
            }
            TemperatureBean.TemperatureDetailsBean mTemp = null;
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
            //到此 已经把历史体温和设备体温合并起来了  然后可能是乱序的,  于是按index 排一下序
            Collections.sort(mMergeData, new Comparator<TemperatureBean.TemperatureDetailsBean>() {
                @Override
                public int compare(TemperatureBean.TemperatureDetailsBean o1, TemperatureBean.TemperatureDetailsBean o2) {
                    return o1.getIndex() - o2.getIndex();
                }
            });
            mToDeviceData.clear();
            mToDeviceData.addAll(mMergeData);
        }else{//不同手环
            //今天覆盖(但保留手动检测)
            if(isToday)
            {
                //到此 已经把设备体温和本地手动检测数据合并起来了  然后可能是乱序的,  于是按index 排一下序
                Collections.sort(mCoverData, new Comparator<TemperatureBean.TemperatureDetailsBean>() {
                    @Override
                    public int compare(TemperatureBean.TemperatureDetailsBean o1, TemperatureBean.TemperatureDetailsBean o2) {
                        return o1.getIndex() - o2.getIndex();
                    }
                });
                mToDeviceData.clear();
                mToDeviceData.addAll(mCoverData);
            }else{//昨天的不处理
                //不处理(不处理在方法外面已经判断了 这里不用写了)
            }
        }

        SNLog.i("体温合并测试:");

    }

    private static TemperatureBean.TemperatureDetailsBean findBeanWithIndex(List<TemperatureBean.TemperatureDetailsBean> mSources, int index) {
        for (TemperatureBean.TemperatureDetailsBean detailsBean : mSources) {
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
    private static boolean contains(ArrayList<TemperatureBean.TemperatureDetailsBean> list, TemperatureBean.TemperatureDetailsBean obj) {
        if (list == null) return false;
        for (TemperatureBean.TemperatureDetailsBean bean : list) {
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
            beans.add(new ArrayList<>(list.subList(i, i += 48)));
        }
        return beans;
    }


    /**
     * 取得大中小
     *
     * @param list
     * @return
     */
    public static int[] getMinAvgMaxTotal(List<? extends TemperatureBean.TemperatureDetailsBean> list) {
        int[] min_avg_max = new int[3];
        int total = 0;
        int total_count = 0;
        min_avg_max[0] = Integer.MAX_VALUE;
        min_avg_max[2] = -Integer.MAX_VALUE;
        for (TemperatureBean.TemperatureDetailsBean bean : list) {
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
