package com.sn.blesdk.control;

import com.dz.blesdk.utils.BLELog;
import com.sn.app.db.data.config.bean.UnitConfig;
import com.sn.app.utils.AppUnitUtil;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.sport.SportBean;
import com.sn.blesdk.db.data.sport.SportDao;
import com.sn.blesdk.interfaces.IDataDecodeHelper;
import com.sn.blesdk.net.SportDataNetworkSyncHelper;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.SNLog;
import com.sn.utils.eventbus.SNEventBus;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 作者:东芝(2017/11/24).
 * 功能:运动数据解码 实时步数/历史步数/历史距离/历史卡路里
 */
public class SportDataDecodeHelper implements IDataDecodeHelper {
    /**
     * 时间间隔
     */
    public static final int EVERY_MINUTES = 30;
    /**
     * 一天的运动数据最大长度
     */
    public static final int DAY_SPORT_LENGTH = 48;
    private Calendar mDistanceDataCalendar;
    private Calendar mStepDataCalendar;
    private Calendar mCaloriesDataCalendar;

    private ArrayList<SportBean.CalorieBean> calories = new ArrayList<>();
    private ArrayList<SportBean.DistanceBean> distances = new ArrayList<>();
    private ArrayList<SportBean.StepBean> steps = new ArrayList<>();


    @Override
    public void decode(byte[] buffer) {
//
        //解析实时运动数据
        if (SNBLEHelper.startWith(buffer, "050701")) {
            BLELog.w("实时运动数据:解析开始");
            int real_time_step = SNBLEHelper.subBytesToInt(buffer, 4, 3, 6);
            int real_time_distance = SNBLEHelper.subBytesToInt(buffer, 4, 7, 10);
            int real_time_calorie = SNBLEHelper.subBytesToInt(buffer, 4, 11, 14);
//            int time = SNBLEHelper.subBytesToInt(buffer, 4, 15, 16);//该值是错的 永远是65536
            boolean case1 = real_time_step == 0 && (real_time_distance > 0 || real_time_calorie > 0);//无步数数据 但有距离或卡路里数据!
            boolean case2 = real_time_calorie > 10000;//一万千卡! 错误的数据
            boolean case3 = real_time_step > 99999;
            if (case1 || case2 || case3) {
                BLELog.w("实时运动数据:数据异常 步数:%d,距离:%d,卡路里:%d", real_time_step, real_time_distance, real_time_calorie);
                return;
            }
            BLELog.w("实时运动数据:步数:%d,距离:%d,卡路里:%d", real_time_step, real_time_distance, real_time_calorie);
            BLELog.w("实时运动数据:解析结束");
            asyncSaveRealTimeData(real_time_step, real_time_distance, real_time_calorie);
        }
        //解析历史步数数据
        if (SNBLEHelper.startWith(buffer, "050703")) {
            //序号
            int index = buffer[3] & 0xff;
            if (index > 11) {//防止返回超过2天的数据
                return;
            }
            if (index == 0)//开始
            {
                BLELog.w("运动大数据:解析开始");
                mStepDataCalendar = DateUtil.getCurrentCalendarBegin();
                steps.clear();//初始化 开始装
            }
            if (mStepDataCalendar == null) return;//错误 无包头
            if (index != 0 && index % 6 == 0) {//时间移动到前一天

                mStepDataCalendar.add(Calendar.DAY_OF_MONTH, -2);
            }

            for (int i = 4; i < 20; i += 2) {
                int val = SNBLEHelper.subBytesToInt(buffer, 2, i, i + 1);
                int timeIndex = DateUtil.convertTimeToIndex(mStepDataCalendar, EVERY_MINUTES);
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, mStepDataCalendar);
                if (val != 0) {
                    BLELog.w("计步大数据:%d,index:%d,时间:%s", val, timeIndex, date);
                }
                if(val>10000){
                    val = 0;
                }
                steps.add(new SportBean.StepBean(timeIndex, date, val));
                //++
                mStepDataCalendar.add(Calendar.MINUTE, +EVERY_MINUTES/*min*/);
            }

        }
        //解析历史距离数据
        if (SNBLEHelper.startWith(buffer, "050705")) {
            //序号
            int index = buffer[3] & 0xff;
            if (index > 11) {//防止返回超过2天的数据
                return;
            }
            if (index == 0)//开始
            {
                mDistanceDataCalendar = DateUtil.getCurrentCalendarBegin();
                distances.clear();
            }
            if (mDistanceDataCalendar == null) return;//错误 无包头
            if (index != 0 && index % 6 == 0) {//时间移动到前一天
                mDistanceDataCalendar.add(Calendar.DAY_OF_MONTH, -2);
            }

            for (int i = 4; i < 20; i += 2) {
                int val = SNBLEHelper.subBytesToInt(buffer, 2, i, i + 1);
                int timeIndex = DateUtil.convertTimeToIndex(mDistanceDataCalendar, EVERY_MINUTES);
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, mDistanceDataCalendar);
                if (val != 0) {
                    BLELog.w("距离大数据:%d,index:%d,时间:%s", val, timeIndex, date);
                }
                distances.add(new SportBean.DistanceBean(timeIndex, date, val));
                mDistanceDataCalendar.add(Calendar.MINUTE, +EVERY_MINUTES/*min*/);
            }

        }
        //解析卡路里距离数据
        if (SNBLEHelper.startWith(buffer, "050706")) {
            //序号
            int index = buffer[3] & 0xff;
            if (index > 11) {//防止返回超过2天的数据
                return;
            }
            if (index == 0)//开始
            {
                mCaloriesDataCalendar = DateUtil.getCurrentCalendarBegin();
                calories.clear();
            }
            if (mCaloriesDataCalendar == null) return;//错误 无包头
            if (index != 0 && index % 6 == 0) {//时间移动到前一天
                mCaloriesDataCalendar.add(Calendar.DAY_OF_MONTH, -2);
            }

            for (int i = 4; i < 20; i += 2) {
                int val = SNBLEHelper.subBytesToInt(buffer, 2, i, i + 1);
                int timeIndex = DateUtil.convertTimeToIndex(mCaloriesDataCalendar, EVERY_MINUTES);
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, mCaloriesDataCalendar);
                if (val != 0) {
                    BLELog.w("卡路里大数据:%d,index:%d,时间:%s", val, timeIndex, date);
                    if (val > 10000) {
                        val = 0;
                        BLELog.w("卡路里大数据:数据异常  卡路里:%d", val);
                    }
                }
                calories.add(new SportBean.CalorieBean(timeIndex, date, val));
                mCaloriesDataCalendar.add(Calendar.MINUTE, +EVERY_MINUTES/*min*/);
            }


        }
        //运动大数据同步成功
        if (SNBLEHelper.startWith(buffer, "0507FF")) {
            BLELog.w("运动大数据:解析完成");
            asyncSaveData();
        }


    }

    /**
     * 存储实时数据到历史步数大数据里
     *
     * @param real_time_step
     * @param real_time_distance
     * @param real_time_calorie
     */
    private void asyncSaveRealTimeData(final int real_time_step, final int real_time_distance, final int real_time_calorie) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                BLELog.w("实时运动数据:本地保存开始");
                SportDao sportDao = SNBLEDao.get(SportDao.class);
                UnitConfig unitConfig = AppUnitUtil.getUnitConfig();
                int temp_distance;
                //TODO 注意 这里因为手环设置了英制  所以实时数据返回了 英制的数据(注意,此时大数据的单位还是米,切记勿混淆)  所以合并到大数据时 需要转成米
                //如果单位是英里,则需要转一下
                if (unitConfig.distanceUnit == UnitConfig.DISTANCE_MILES) {
                    //手环数据转英里
                    double miles = real_time_distance * 1.0d / 1000d;
                    //英里转千米
                    double km = miles * 1.609344d;
                    //千米转化成米 ,并四舍五入
                    temp_distance = (int) Math.round(km * 1000d);
                } else {
                    temp_distance = real_time_distance;
                }
                sportDao.insertOrUpdate(
                        AppUserUtil.getUser().getUser_id(),
                        AppUserUtil.getUser().getTarget_step(),
                        SNBLEHelper.getDeviceMacAddress(),
                        real_time_step,
                        temp_distance,
                        real_time_calorie
                );
            }

            @Override
            public void done() {
                super.done();
                BLELog.w("实时运动数据:本地保存完成");
                SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_REAL_TIME_SPORT_DATA, real_time_step);
            }

            @Override
            public void error(Throwable e) {
                super.error(e);
                BLELog.w("实时运动数据:本地保存失败:" + e);
            }
        });
    }


    /**
     * 异步保存数据
     */
    private void asyncSaveData() {

        //他们的长度都全等于吗
        if (!IF.isEquals(steps.size(), distances.size(), calories.size())) {
            BLELog.w("运动大数据:解析有丢失,不保存 步数数据数量:%d,距离数据数量:%d,卡路里数据数量:%d", steps.size(), distances.size(), calories.size());
            return;
        }
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                BLELog.w("运动大数据:本地保存开始");

                String deviceMacAddress = SNBLEHelper.getDeviceMacAddress();
                //把96个数据转成2个48的集合
                List<ArrayList<SportBean.StepBean>> stepSet = getList(steps);
                List<ArrayList<SportBean.DistanceBean>> distanceSer = getList(distances);
                List<ArrayList<SportBean.CalorieBean>> calorieSet = getList(calories);

                int size = stepSet.size();
                //需要上传的运动数据集
                List<SportBean> uploadList = new ArrayList<>();

                //取出数据库操作类
                SportDao sportDao = SNBLEDao.get(SportDao.class);
                //倒过来遍历的原因是 为了先保存昨天的 再保存今天的 按顺序存 这样数据库的id 不会乱
                for (int i = size - 1; i >= 0; i--) {
                    //创建一条一天的对象
                    ArrayList<SportBean.StepBean> steps = stepSet.get(i);
                    ArrayList<SportBean.CalorieBean> calories = calorieSet.get(i);
                    ArrayList<SportBean.DistanceBean> distances = distanceSer.get(i);
                    String date = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.YYYY_MM_DD, steps.get(0).getDateTime());
                    //查询这天的数据
                    List<SportBean> sportBeans = sportDao.queryForDay(AppUserUtil.getUser().getUser_id(), date);
                    SportBean sportBean;
                    if (IF.isEmpty(sportBeans)) {
                        //如果这天没有数据 则创建一条
                        sportBean = new SportBean();
                        sportBean.setUser_id(AppUserUtil.getUser().getUser_id());
                        sportBean.setStepTarget(AppUserUtil.getUser().getTarget_step());

                    } else {
                        //取出这天的数据
                        sportBean = sportBeans.get(0);
                        //如果mac地址一致 说明是同一个设备,此时需要合并数据,因为手环有复位/断电丢失数据 的可能性,
                        //导致数据丢失,我们这里的处理的目的是为了让用户无法感知数据丢失了,增强用户体验
                        boolean isSameDevice = deviceMacAddress.equals(sportBean.getMac());
                        boolean isToday = DateUtil.equalsToday(date);
                        //同一手环
                        if (isSameDevice) {
                            //今天的覆盖,昨天的合并
                            if (isToday) {
                                //无代码 (默认就是覆盖)
                            } else {
                                //合并
                                mergeStep(sportBean.getSteps(), steps);
                                mergeCalories(sportBean.getCalories(), calories);
                                mergeDistances(sportBean.getDistances(), distances);
                            }
                        } else {//不同手环
                            //今天的覆盖,昨天的不处理
                            if (isToday) {
                                //无代码 (默认就是覆盖)
                            } else {
                                //不处理
                                continue;
                            }
                        }
                    }
                    sportBean.setMac(deviceMacAddress);
                    sportBean.setSteps(steps);
                    sportBean.setCalories(calories);
                    sportBean.setDistances(distances);

                    //设置总数数据 供快速查询
                    sportBean.setStepTotal(getTotal(steps));
                    sportBean.setCalorieTotal(getTotal(calories));
                    sportBean.setDistanceTotal(getTotal(distances));
                    //把该天的起始位置 的时间去掉时分秒 只要日期,然后昨晚该天的数据  这个SportBean.COLUMN_DATE 是唯一的
                    sportBean.setDate(date);////第0条数据的日期作为当天
                    uploadList.add(sportBean);

                    //更新或插入
                    sportDao.insertOrUpdate(AppUserUtil.getUser().getUser_id(), sportBean);
                }

                //上传数据到服务器
                SportDataNetworkSyncHelper.uploadToServer(uploadList);
            }

            @Override
            public void done() {
                BLELog.w("运动大数据:本地保存完成");
                SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_SPORT_DATA);
            }

            @Override
            public void error(Throwable e) {
                e.printStackTrace();
                BLELog.w("运动大数据:本地保存失败" + e);
            }
        });
    }

    /**
     * 把n个数据转成n个48的集合
     *
     * @param list
     * @param <T>
     * @return
     */
    private <T> List<ArrayList<T>> getList(List<T> list) {
        List<ArrayList<T>> beans = new ArrayList<>();
        for (int i = 0; i < list.size(); ) {
            beans.add(new ArrayList<>(list.subList(i, i += DAY_SPORT_LENGTH)));
        }
        return beans;
    }


    /**
     * 取得总数
     *
     * @param list
     * @return
     */
    public static int getTotal(List<? extends SportBean.AbsSportBean> list) {
        int total = 0;
        if (list == null) return total;
        for (SportBean.AbsSportBean bean : list) {
            total += bean.getValue();
        }
        return total;
    }


    /**
     * 合并 (需要保证MAC地址一致,否则勿合并,没意义)
     *
     * @param localData
     * @param deviceData
     * @return
     */
    private void mergeStep(List<SportBean.StepBean> localData, List<SportBean.StepBean> deviceData) {
        if (IF.isEmpty(localData)) {
            SNLog.i("本地无数据,不需要处理");
            return;
        }
        int size = localData.size();
        if (size != deviceData.size()) {
            SNLog.i("数据长度异常,不处理");
            return;
        }
        try {
            int mergeCount = 0;
            List<SportBean.StepBean> tempData = new ArrayList<>();
            for (int i = 0; i < size; i++) {

                SportBean.StepBean mLocalBean = localData.get(i);
                SportBean.StepBean mDeviceBean = deviceData.get(i);

                if (mLocalBean.getValue() > 0) {
                    //本地有值 优先使用本地的
                    tempData.add(mLocalBean);
                    if (mDeviceBean.getValue() > 0) {
                        mergeCount++;
                    }
                } else {
                    //本地没值 则使用手环值
                    tempData.add(mDeviceBean);
                }
            }
            if (tempData.size() == size) {
                deviceData.clear();
                deviceData.addAll(tempData);
                SNLog.i("步数合并测试:" + mergeCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 合并 (需要保证MAC地址一致,否则勿合并,没意义)
     *
     * @param localData
     * @param deviceData
     * @return
     */
    private void mergeCalories(List<SportBean.CalorieBean> localData, List<SportBean.CalorieBean> deviceData) {
        if (IF.isEmpty(localData)) {
            SNLog.i("本地无数据,不需要合并");
            return;
        }
        int size = localData.size();
        if (size != deviceData.size()) {
            SNLog.i("数据长度异常,不能合并");
            return;
        }
        List<SportBean.CalorieBean> tempData = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SportBean.CalorieBean mLocalBean = localData.get(i);
            SportBean.CalorieBean mDeviceBean = deviceData.get(i);
            if (mLocalBean.getValue() == 0) {
                //本地没值 则使用手环值
                tempData.add(mDeviceBean);
            } else {
                //本地有值 优先使用本地的
                tempData.add(mLocalBean);
            }
        }
        deviceData.clear();
        deviceData.addAll(tempData);

    }

    /**
     * 合并 (需要保证MAC地址一致,否则勿合并,没意义)
     *
     * @param localData
     * @param deviceData
     * @return
     */
    private void mergeDistances(List<SportBean.DistanceBean> localData, List<SportBean.DistanceBean> deviceData) {
        if (IF.isEmpty(localData)) {
            SNLog.i("本地无数据,不需要合并");
            return;
        }
        int size = localData.size();
        if (size != deviceData.size()) {
            SNLog.i("数据长度异常,不能合并");
            return;
        }
        List<SportBean.DistanceBean> tempData = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SportBean.DistanceBean mLocalBean = localData.get(i);
            SportBean.DistanceBean mDeviceBean = deviceData.get(i);
            if (mLocalBean.getValue() == 0) {
                //本地没值 则使用手环值
                tempData.add(mDeviceBean);
            } else {
                //本地有值 优先使用本地的
                tempData.add(mLocalBean);
            }
        }
        deviceData.clear();
        deviceData.addAll(tempData);
    }
}
