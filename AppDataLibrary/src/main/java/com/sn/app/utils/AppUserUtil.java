package com.sn.app.utils;

import com.sn.app.db.data.base.AppDao;
import com.sn.app.db.data.config.DeviceConfigBean;
import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.db.data.user.UserDao;
import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.UserDeviceBean;
import com.sn.app.net.data.app.bean.UserMessageBean;
import com.sn.app.net.data.base.DefResponseBean;
import com.sn.app.storage.UserStorage;
import com.sn.utils.IF;

import java.sql.SQLException;
import java.util.List;

/**
 * app全局用户 工具
 */
public class AppUserUtil {

    private static UserBean bean;

    /**
     * 刷新或保存User数据
     *
     * @param user
     * @return
     * @throws SQLException
     */
    public static boolean setUser(UserBean user) {
        try {
            return AppDao.get(UserDao.class).insertOrUpdate(user, user.getUser_id());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    /**
     * 取得当前User数据
     *
     * @return
     */
    public static synchronized UserBean getUser() {
        UserDao userDao = AppDao.get(UserDao.class);//这get() 是单例的 不消耗内存 可以放心多次调用getUser
        if (bean == null) {
            List<UserBean> beans = userDao.queryForEq(UserBean.COLUMN_USER_ID, UserStorage.getUserId());
            if (!IF.isEmpty(beans)) {
                bean = beans.get(0);
                return bean;
            }
        }
        if (bean == null) {
            bean = setTmpUser();
           // throw new NullPointerException("空UserBean 错误!你在没有用户之前事先调用了getUser(), 因为此时还没有用户,(如果你更改了表结构,请修改数据库版本号)");
        }
        // 如果UserBean 在其他地方做了表数据修改,则刷新UserBean
        try {
            //TODO 不知道损耗的性能有多少 有时间看看这段的执行性能
            userDao.getDao().refresh(bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bean;
    }

    //TODO  这整个需要加入线程 才能进行操作 否则UI会卡 现在先等功能写完了 再抽成线程操作
    public static void initialize(UserMessageBean.DataBean data) throws SQLException {
        int userId = data.getId();
        UserStorage.setUserId(userId);

        initializeUser(data);
//        initializeAlarm(userId);
        initializeDeviceConfig(userId);


    }


    private static void initializeUser(UserMessageBean.DataBean data) throws SQLException {
        try {
            //取得已存在的用户
            bean = AppDao.get(UserDao.class).queryForOneEq(UserBean.COLUMN_USER_ID, data.getId());
        } catch (Exception ignored) {
            bean = null;
        } finally {
            //如果数据库里没有,则创建新User
            if (bean == null) {
                bean = new UserBean();
            }
        }

        bean.setUser_id(data.getId());
        bean.setEmail(data.getEmail());
        bean.setNickname(data.getNickname());
        bean.setAddress(data.getAddress());
        bean.setSign(data.getSign());
        bean.setBirthday(data.getBirthday());
        bean.setJob(data.getJob());
        bean.setHeight(data.getHeight());
        bean.setWeight(data.getWeight());
        bean.setGender(data.getGender());
        bean.setPortrait(data.getPortrait());
        bean.setWallpaper(data.getWallpaper());
        bean.setTarget_sleep(data.getTarget_sleep());
        bean.setTarget_step(data.getTarget_step());
        bean.setMax_step(data.getMax_step());
        bean.setMax_reach_times(data.getMax_reach_times());
        bean.setMax_reach_date(data.getMax_reach_date());
        bean.setWeight_measure_date(data.getLast_weight_time());
        bean.setUnique_id(data.getPhone_uuid());
        bean.setTarget_calory(data.getGoal_calory());
        bean.setTarget_weight(data.getGoal_weight());
        bean.setFirst_meal_date(data.getFirst_meal_date());
        bean.setTotal_meal_day(data.getTotal_meal_day());
        bean.setSport_days(data.getSport_days());

        UserDeviceBean.DataBean last_device = data.getLast_device();
        if (last_device != null) {
            String mac = last_device.getMac();
            if (!IF.isEmpty(mac)) {
                bean.setMac(mac);
                bean.setDevice_name(last_device.getDevice_name());
                bean.setAdv_id(last_device.getAdv_id());
            }
        }
        boolean user = setUser(bean);
        if (!user) {
            throw new SQLException("无法创建/更新UserBean表");
        }
    }

    private static void initializeDeviceConfig(final int userId) {

        DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
        //如果该用户没有初始化 则初始化一条  数据,并定义为该userId用户的app设备配置,如果已经有的话  不需要初始化
        if (deviceConfigDao.queryCount(userId) == 0) {
            DeviceConfigBean data = new DeviceConfigBean();
            data.setUser_id(userId);
            deviceConfigDao.insert(data);
        }


    }


    /**
     * 初始化五个闹钟
     *
     * @param userId
     * @throws SQLException
     */
//    private static void initializeAlarm(int userId) throws SQLException {
//        AlarmClockDao alarmClockDao = AlarmClockDao.get(AlarmClockDao.class);
//        long l = alarmClockDao.getDao().queryBuilder().where().eq(AlarmClockBean.COLUMN_USER_ID, userId).countOf();
//        int alarmCount = 5;
//        if (l != alarmCount) {
//            for (int i = 0; i < alarmCount; i++) {
//                AlarmClockBean alarmClockBean = new AlarmClockBean();
//                alarmClockBean.setAlarmId(i);
//                alarmClockBean.setDate(DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD_HH_MM));
//                alarmClockBean.setDelete(true);
//                alarmClockBean.setUser_Id(userId);
//                alarmClockDao.insert(alarmClockBean);
//            }
//        }
//    }

    /**
     * 上传用户上次连接的设备
     *
     * @param mac
     * @param function
     * @param device_name
     * @param adv_id
     */
    public static void uploadUserDevice(String mac, String device_name, int function, int adv_id, final OnOperationListener listener) {
        AppNetReq.getApi().uploadDevice(mac, device_name, function, adv_id, "", "").enqueue(new OnResponseListener<UserDeviceBean>() {
            @Override
            public void onResponse(UserDeviceBean body) throws Throwable {
                UserDeviceBean.DataBean data = body.getData();
                if (data != null) {
                    bean = getUser();
                    String dataMac = data.getMac();
                    if (!IF.isEmpty(dataMac)) {
                        bean.setAdv_id(data.getAdv_id());
                        bean.setDevice_name(data.getDevice_name());
                        bean.setMac(dataMac);
                        boolean user = setUser(bean);
                        if (!user) {
                            throw new SQLException("无法创建/更新UserBean表");
                        } else {
                            if (listener != null) {
                                listener.success();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(int ret, String msg) {
                if (listener != null) {
                    listener.failed(msg);
                }
            }
        });
    }


    /**
     * 上传用户上次连接的设备
     *
     * @param mac
     * @param function
     * @param device_name
     * @param adv_id
     */
    public static void uploadUserDevice(String mac, String device_name, int function, int adv_id, int customid, int upgradeid, final OnOperationListener listener) {
        AppNetReq.getApi().uploadDeviceAllMessage(mac, device_name, function, adv_id, customid, upgradeid, "", "").enqueue(new OnResponseListener<UserDeviceBean>() {
            @Override
            public void onResponse(UserDeviceBean body) throws Throwable {
                UserDeviceBean.DataBean data = body.getData();
                if (data != null) {
                    bean = getUser();
                    String dataMac = data.getMac();
                    if (!IF.isEmpty(dataMac)) {
                        bean.setAdv_id(data.getAdv_id());
                        bean.setDevice_name(data.getDevice_name());
                        bean.setMac(dataMac);
                        boolean user = setUser(bean);
                        if (!user) {
                            throw new SQLException("无法创建/更新UserBean表");
                        } else {
                            if (listener != null) {
                                listener.success();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(int ret, String msg) {
                if (listener != null) {
                    listener.failed(msg);
                }
            }
        });
    }

    /**
     * 清空用户设备数据
     */
    public static void clearUserDevice(final OnOperationListener listener) {
        AppNetReq.getApi().uploadDevice("", "", 0, 0, "", "").enqueue(new OnResponseListener<UserDeviceBean>() {
            @Override
            public void onResponse(UserDeviceBean body) throws Throwable {
                UserDeviceBean.DataBean data = body.getData();
                if (data != null) {
                    bean = getUser();
                    bean.setAdv_id(data.getAdv_id());
                    bean.setDevice_name(data.getDevice_name());
                    bean.setMac(data.getMac());
                    boolean user = setUser(bean);
                    if (!user) {
                        throw new SQLException("无法创建/更新UserBean表");
                    }
                }
                if (listener != null) {
                    listener.success();
                }
            }

            @Override
            public void onFailure(int ret, String msg) {
                if (listener != null) {
                    listener.failed(msg);
                }
            }
        });
    }


    public static void uploadUserDeviceOriginMac(String mac, String origin_mac, final OnOperationListener listener) {
        AppNetReq.getApi().uploadDeviceOriginMac(mac, origin_mac).enqueue(new OnResponseListener<DefResponseBean>() {
            @Override
            public void onResponse(DefResponseBean body) throws Throwable {
                if (listener != null) {
                    listener.success();
                }
            }

            @Override
            public void onFailure(int ret, String msg) {
                if (listener != null) {
                    listener.failed(msg);
                }
            }
        });
    }

    public interface OnOperationListener {
        void success();

        void failed(String msg);
    }

    /**
     * id : 17941
     * country_code :
     * phone :
     * email : dn0520@163.com
     * nickname :
     * address :
     * sign :
     * birthday : 0000-00-00
     * job :
     * height : 0.0
     * weight : 0.0
     * gender : 0
     * portrait :
     * wallpaper :
     * target_sleep : 0
     * target_step : 0
     * max_step : 0
     * max_reach_times : 2
     * max_reach_date : 1519862400
     * create_time : 2018-02-28 07:45:53
     * phone_uuid :
     * last_device : null
     * last_weight_time : 0000-00-00
     */

    private static UserBean setTmpUser(){
        UserBean ub = new UserBean();
        ub.setUser_id(UserStorage.getTmpUserId());
        ub.setEmail("dn0520@163.com");
        ub.setNickname("User");
        ub.setAddress("");
        ub.setSign("");
        ub.setBirthday("0000-00-00");
        ub.setJob("");
        ub.setHeight(175f);
        ub.setWeight(60f);
        ub.setGender(0);
        ub.setPortrait("");
        ub.setWallpaper("");
        ub.setTarget_sleep(0);
        ub.setTarget_step(0);
        ub.setMax_step(0);
        ub.setMax_reach_times(1519862400);
        ub.setMax_reach_date(1519862400);
        ub.setWeight_measure_date("");
        ub.setUnique_id("11");
        ub.setTarget_calory(0f);
        ub.setTarget_weight(60f);
        ub.setFirst_meal_date("0000-00-00");
        ub.setTotal_meal_day(0);
        ub.setSport_days(0);
        return ub;
    }
}
