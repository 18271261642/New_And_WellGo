package com.sn.blesdk.db.data.health.blood_pressure;

import com.dz.blesdk.utils.BLELog;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.net.HealthBloodPressureDataNetworkSyncHelper;
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
 * 功能:血压数据存储助手类
 */

public class BloodPressureStorageHelper {
    private static int lastTimeIndex=-1;


    //TODO 这个应该让界面层 保存, 因为是实时传输的数据 需要界面显示完后 点结束 或倒计时结束 才保存平均值 这里使用的是 假保存 后面界面出来 这里要改!!!

    /**
     * 实时数据保存
     *
     * @param deviceMacAddress
     * @param mBloodDiastolic
     */
    public static void asyncSaveRealTimeData(final String deviceMacAddress, final int mBloodDiastolic,final int mBloodSystolic) {
        final Calendar currentCalendar = DateUtil.getCurrentCalendar();
        final int timeIndex = DateUtil.convertTimeToIndex(currentCalendar, 1);
        //血压有此特性 数据只保存一次, 可能手环会一直推送过来  但只保存一次
        if(lastTimeIndex!=-1&&lastTimeIndex==timeIndex){
            return;
        }
        lastTimeIndex = timeIndex;
        //血压
        if (mBloodDiastolic != 0&&mBloodSystolic!=0) {
            SNAsyncTask.execute(new SNVTaskCallBack() {
                @Override
                public void run() throws Throwable {
                    BLELog.d("血压实时数据:本地保存开始");

                    ArrayList<BloodPressureBean.BloodPressureDetailsBean> bloodPressureDetailsBeans = new ArrayList<>();
                    bloodPressureDetailsBeans.add(new BloodPressureBean.BloodPressureDetailsBean(timeIndex, DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, currentCalendar), mBloodDiastolic, mBloodSystolic, 0/*(默认0=手动检测,1=自动检测)*/));
                    //保存一条数据
                    save(deviceMacAddress,bloodPressureDetailsBeans);
                }

                @Override
                public void done() {
                    BLELog.d("血压实时数据:本地保存完成");
                    SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_HEALTH_CHECK_BLOOD_PRESSURE, new int[]{mBloodDiastolic, mBloodSystolic});

                    SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_BLOOD_PRESSURE_DATA);
                    try {
                        List<BloodPressureBean> beans = BloodPressureDao.get(BloodPressureDao.class).queryNotUpload(AppUserUtil.getUser().getUser_id());
                        if (!IF.isEmpty(beans)) {
                            HealthBloodPressureDataNetworkSyncHelper.uploadToServer(beans);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void error(Throwable e) {
                    BLELog.d("血压实时数据:本地保存失败"+e);
                }
            });
        }

    }

    /**
     * 保存血压大数据数据
     */
    public static void asyncSaveData(final String deviceMacAddress, final ArrayList<BloodPressureBean.BloodPressureDetailsBean> bloodPressures) {
        //非空判断
        if (IF.isEmpty(bloodPressures)) {
            SNLog.e("血压数据有丢失,不保存 数据数量:"+ bloodPressures.size());
            return;
        }
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {

                //把解析出来的数据 按天分割 分别装
                List<ArrayList<BloodPressureBean.BloodPressureDetailsBean>> bloodPressureDetailSet = getList(bloodPressures);
                //数据量(天数)
                int size = bloodPressureDetailSet.size();
                //保存两天的数据(如果有的画)
                for (int i = 0; i < size; i++) {
                    save(deviceMacAddress,  bloodPressureDetailSet.get(i));
                }
            }

            @Override
            public void done() {
                SNLog.i("血压数据保存完成");
                SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_BLOOD_PRESSURE_DATA);
            }

            @Override
            public void error(Throwable e) {
                SNLog.i("血压数据保存失败" + e);
            }
        });


    }

    /**
     * 保存血压数据
     *
     * @param mac
     * @param mBloodPressureDetails
     * @return
     * @throws ParseException
     * @throws java.sql.SQLException
     */
    private static boolean save(String mac,   ArrayList<BloodPressureBean.BloodPressureDetailsBean> mBloodPressureDetails) throws ParseException, java.sql.SQLException {
        BloodPressureDao bloodPressureDao = SNBLEDao.get(BloodPressureDao.class);
        String date = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.YYYY_MM_DD, mBloodPressureDetails.get(0).getDateTime());
        //查询血压历史对象
        List<BloodPressureBean> mHistoryBloodPressureBeans = bloodPressureDao.queryForDay(AppUserUtil.getUser().getUser_id(),date);
        if (!IF.isEmpty(mHistoryBloodPressureBeans)) {
            //取第0条  只有一条 所以不会超出数组
            BloodPressureBean bloodPressureBean = mHistoryBloodPressureBeans.get(0);
            //是同一个设备才共存数据,否则break掉 重新存新数据
//            if (bloodPressureBean.getMac().equalsIgnoreCase(mac)) {
                //取出血压详情历史
                ArrayList<BloodPressureBean.BloodPressureDetailsBean> mHistoryBloodPressureDetails = bloodPressureBean.getBloodPressureDetails();
                if (!IF.isEmpty(mHistoryBloodPressureBeans)) {   //遍历 并合并数据
                    for (BloodPressureBean.BloodPressureDetailsBean detail : mHistoryBloodPressureDetails) {
                        //TODO 这里可能出现数据错乱 可能要判断 第一条新数据 和历史的最后一条对比 是否新数据更旧...  则视为设备被[清除数据]等情况 需要清空数据库 但这里暂时没处理  以后有问题再改
                        //如果数据中没有  则合并 这里的合并 可能包括 实时血压数据
                        if (!contains(mBloodPressureDetails, detail)) {
                            mBloodPressureDetails.add(detail);
                        }
                    }
//                }
            }
        }
        //到此 已经把历史血压和实时血压合并起来了  然后可能是乱序的,  于是按index 排一下序
        Collections.sort(mBloodPressureDetails, new Comparator<BloodPressureBean.BloodPressureDetailsBean>() {
            @Override
            public int compare(BloodPressureBean.BloodPressureDetailsBean o1, BloodPressureBean.BloodPressureDetailsBean o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        BloodPressureBean bloodPressureBean = new BloodPressureBean();
        bloodPressureBean.setUser_id(AppUserUtil.getUser().getUser_id());
        bloodPressureBean.setBloodPressureDetails(mBloodPressureDetails);
        bloodPressureBean.setMac(mac);

        int[] bloodDiastolicMinAvgMaxTotal = getBloodDiastolicMinAvgMaxTotal(mBloodPressureDetails);
        int[] bloodSystolicMinAvgMaxTotal = getBloodSystolicMinAvgMaxTotal(mBloodPressureDetails);
        //TODO 这里好像只要平均值? 先这样写
        bloodPressureBean.setBloodDiastolic(bloodDiastolicMinAvgMaxTotal[1]);
        bloodPressureBean.setBloodSystolic(bloodSystolicMinAvgMaxTotal[1]);

        bloodPressureBean.setDate(date);

        boolean success = bloodPressureDao.insertOrUpdate(AppUserUtil.getUser().getUser_id(), bloodPressureBean);
        SNLog.i("血压数据保存=" + success);
        return success;
    }


    /**
     * 列表对象中是否含有这个对象
     *
     * @param list
     * @param obj
     * @return
     */
    private static boolean contains(ArrayList<BloodPressureBean.BloodPressureDetailsBean> list, BloodPressureBean.BloodPressureDetailsBean obj) {
        if (list == null) return false;
        for (BloodPressureBean.BloodPressureDetailsBean bean : list) {
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
     * 取得舒张压 大中小
     *
     * @param list
     * @return
     */
    public static int[] getBloodDiastolicMinAvgMaxTotal(List<? extends BloodPressureBean.BloodPressureDetailsBean> list) {
        int[] min_avg_max = new int[3];
        int total = 0;
        int total_count = 0;
        min_avg_max[0] = Integer.MAX_VALUE;
        min_avg_max[2] = -Integer.MAX_VALUE;
        for (BloodPressureBean.BloodPressureDetailsBean bean : list) {
            int value = bean.getBloodDiastolic();
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

    /**
     * 取得收缩压 大中小
     *
     * @param list
     * @return
     */
    public static int[] getBloodSystolicMinAvgMaxTotal(List<? extends BloodPressureBean.BloodPressureDetailsBean> list) {
        int[] min_avg_max = new int[3];
        int total = 0;
        int total_count = 0;
        min_avg_max[0] = Integer.MAX_VALUE;
        min_avg_max[2] = -Integer.MAX_VALUE;
        for (BloodPressureBean.BloodPressureDetailsBean bean : list) {
            int value = bean.getBloodSystolic();
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
