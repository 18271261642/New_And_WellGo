package com.sn.blesdk.db.data.health.blood_oxygen;

import com.dz.blesdk.utils.BLELog;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.net.HealthBloodOxygenDataNetworkSyncHelper;
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
 * 功能:血氧数据存储助手类
 */

public class BloodOxygenStorageHelper {

    //TODO 这个应该让界面层 保存, 因为是实时传输的数据 需要界面显示完后 点结束 或倒计时结束 才保存平均值 这里使用的是 假保存 后面界面出来 这里要改!!!

    /**
     * 实时数据保存
     *
     * @param deviceMacAddress
     * @param mBloodOxygen
     */
    public static void asyncSaveRealTimeData(final String deviceMacAddress, final int mBloodOxygen) {
        final Calendar currentCalendar = DateUtil.getCurrentCalendar();
        final int timeIndex = DateUtil.convertTimeToIndex(currentCalendar, 1);

        //血氧气
        if (mBloodOxygen != 0) {
            SNAsyncTask.execute(new SNVTaskCallBack() {
                @Override
                public void run() throws Throwable {
                    BLELog.d("血氧实时数据:本地保存开始");
                    ArrayList<BloodOxygenBean.BloodOxygenDetailsBean> bloodOxygenDetailsBeans = new ArrayList<>();
                    bloodOxygenDetailsBeans.add(new BloodOxygenBean.BloodOxygenDetailsBean(timeIndex, DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, currentCalendar), mBloodOxygen, 0/*(默认0=手动检测,1=自动检测)*/));
                    //保存一条数据
                    save(deviceMacAddress, bloodOxygenDetailsBeans);
                }

                @Override
                public void done() {
                    BLELog.d("血氧实时数据:本地保存完成");
                    SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_HEALTH_CHECK_BLOOD_OXYGEN, mBloodOxygen);
                    SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_BLOOD_OXYGEN_DATA);
                    try {
                        List<BloodOxygenBean> beans = BloodOxygenDao.get(BloodOxygenDao.class).queryNotUpload(AppUserUtil.getUser().getUser_id());
                        if (!IF.isEmpty(beans)) {
                            HealthBloodOxygenDataNetworkSyncHelper.uploadToServer(beans);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void error(Throwable e) {
                    BLELog.d("血氧实时数据:本地保存失败" + e);
                }
            });
        }

    }

    /**
     * 保存血氧大数据数据
     */
    public static void asyncSaveData(final String deviceMacAddress, final ArrayList<BloodOxygenBean.BloodOxygenDetailsBean> heartRates) {
        //非空判断
        if (IF.isEmpty(heartRates)) {
            SNLog.e("血氧数据有丢失,不保存 数据数量:"+ heartRates.size());
            return;
        }
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                //把解析出来的数据 按天分割 分别装
                List<ArrayList<BloodOxygenBean.BloodOxygenDetailsBean>> heartRateDetailSet = getList(heartRates);
                //数据量(天数)
                int size = heartRateDetailSet.size();
                //保存两天的数据(如果有的画)
                for (int i = 0; i < size; i++) {
                    save(deviceMacAddress,  heartRateDetailSet.get(i));
                }
            }

            @Override
            public void done() {
                SNLog.i("血氧数据保存完成");
                SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_BLOOD_OXYGEN_DATA);
            }

            @Override
            public void error(Throwable e) {
                SNLog.i("血氧数据保存失败" + e);
            }
        });


    }

    /**
     * 保存大数据
     *
     * @param mac
     * @param mNewBloodOxygenDetails
     * @return
     * @throws ParseException
     * @throws java.sql.SQLException
     */
    private static boolean save(String mac,   ArrayList<BloodOxygenBean.BloodOxygenDetailsBean> mNewBloodOxygenDetails) throws ParseException, java.sql.SQLException {
        BloodOxygenDao bloodOxygenDao = SNBLEDao.get(BloodOxygenDao.class);
        String date = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.YYYY_MM_DD, mNewBloodOxygenDetails.get(0).getDateTime());
        //查询血氧历史对象
        List<BloodOxygenBean> mHistoryBloodOxygenBeans = bloodOxygenDao.queryForDay(AppUserUtil.getUser().getUser_id(),date);
        if (!IF.isEmpty(mHistoryBloodOxygenBeans)) {
            //取第0条  只有一条 所以不会超出数组
            BloodOxygenBean bloodOxygenBean = mHistoryBloodOxygenBeans.get(0);
            //是同一个设备才共存数据,否则break掉 重新存新数据
//            if (bloodOxygenBean.getMac().equalsIgnoreCase(mac)) {
                //取出血氧详情历史
                ArrayList<BloodOxygenBean.BloodOxygenDetailsBean> mHistoryBloodOxygenDetails = bloodOxygenBean.getBloodOxygenDetails();
                if (!IF.isEmpty(mHistoryBloodOxygenBeans)) {   //遍历 并合并数据
                    for (BloodOxygenBean.BloodOxygenDetailsBean detail : mHistoryBloodOxygenDetails) {
                        //TODO 这里可能出现数据错乱 可能要判断 第一条新数据 和历史的最后一条对比 是否新数据更旧...  则视为设备被[清除数据]等情况 需要清空数据库 但这里暂时没处理  以后有问题再改
                        //如果数据中没有  则合并 这里的合并 可能包括 实时血氧数据
                        if (!contains(mNewBloodOxygenDetails, detail)) {
                            mNewBloodOxygenDetails.add(detail);
                        }
                    }
//                }
            }
        }
        //到此 已经把历史血氧和实时血氧合并起来了  然后可能是乱序的,  于是按index 排一下序
        Collections.sort(mNewBloodOxygenDetails, new Comparator<BloodOxygenBean.BloodOxygenDetailsBean>() {
            @Override
            public int compare(BloodOxygenBean.BloodOxygenDetailsBean o1, BloodOxygenBean.BloodOxygenDetailsBean o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        BloodOxygenBean bloodOxygenBean = new BloodOxygenBean();
        bloodOxygenBean.setUser_id(AppUserUtil.getUser().getUser_id());
        bloodOxygenBean.setBloodOxygenDetails(mNewBloodOxygenDetails);
        bloodOxygenBean.setMac(mac);
        int[] minAvgMaxTotal = getMinAvgMaxTotal(mNewBloodOxygenDetails);
        bloodOxygenBean.setMin(minAvgMaxTotal[0]);
        bloodOxygenBean.setAvg(minAvgMaxTotal[1]);
        bloodOxygenBean.setMax(minAvgMaxTotal[2]);
        bloodOxygenBean.setDate(date);

        boolean success = bloodOxygenDao.insertOrUpdate(AppUserUtil.getUser().getUser_id(), bloodOxygenBean);
        SNLog.i("血氧数据保存=" + success);
        return success;
    }


    /**
     * 列表对象中是否含有这个对象
     *
     * @param list
     * @param obj
     * @return
     */
    private static boolean contains(ArrayList<BloodOxygenBean.BloodOxygenDetailsBean> list, BloodOxygenBean.BloodOxygenDetailsBean obj) {
        if (list == null) return false;
        for (BloodOxygenBean.BloodOxygenDetailsBean bean : list) {
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
    public static int[] getMinAvgMaxTotal(List<? extends BloodOxygenBean.BloodOxygenDetailsBean> list) {
        int[] min_avg_max = new int[3];
        int total = 0;
        int total_count = 0;
        min_avg_max[0] = Integer.MAX_VALUE;
        min_avg_max[2] = -Integer.MAX_VALUE;
        for (BloodOxygenBean.BloodOxygenDetailsBean bean : list) {
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
