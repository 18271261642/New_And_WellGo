package com.truescend.gofit.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.sn.app.net.data.app.bean.GetQuestionsBean;
import com.sn.app.net.data.app.bean.MealBean;
import com.sn.app.storage.UserStorage;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.utils.DateUtil;
import com.truescend.gofit.App;
import com.truescend.gofit.pagers.device.camera.RemoteCameraProActivity;
import com.truescend.gofit.pagers.device.camera.viewer.ImageViewerActivity;
import com.truescend.gofit.pagers.device.clock.AlarmClockActivity;
import com.truescend.gofit.pagers.device.clock.add.AddAlarmClockActivity;
import com.truescend.gofit.pagers.device.health.HealthReminderActivity;
import com.truescend.gofit.pagers.device.health.reminder.ReminderActivity;
import com.truescend.gofit.pagers.device.push.PushActivity;
import com.truescend.gofit.pagers.device.schedule.ScheduleActivity;
import com.truescend.gofit.pagers.device.schedule.add.AddScheduleActivity;
import com.truescend.gofit.pagers.device.schedule.history.HistoryScheduleActivity;
import com.truescend.gofit.pagers.device.setting.DeviceSettingActivity;
import com.truescend.gofit.pagers.device.setting.about.AboutAppActivity;
import com.truescend.gofit.pagers.device.setting.feedback.FeedbackActivity;
import com.truescend.gofit.pagers.device.setting.update.BandUpdateActivity;
import com.truescend.gofit.pagers.device.wallpaper.WallpaperActivity;
import com.truescend.gofit.pagers.friends.data.FriendsDataActivity;
import com.truescend.gofit.pagers.friends.info.FriendsInfoActivity;
import com.truescend.gofit.pagers.friends.invitation.MyQRCActivity;
import com.truescend.gofit.pagers.friends.list.FriendsListActivity;
import com.truescend.gofit.pagers.friends.search.FriendsSearchActivity;
import com.truescend.gofit.pagers.health.HealthCheckActivity;
import com.truescend.gofit.pagers.help.HelpActivity;
import com.truescend.gofit.pagers.home.HomeFragment;
import com.truescend.gofit.pagers.home.diet.add.DietEditMealActivity;
import com.truescend.gofit.pagers.home.diet.list.DietListMealActivity;
import com.truescend.gofit.pagers.home.diet.setting.DietTargetSettingActivity;
import com.truescend.gofit.pagers.home.diet.statistics.DietStatisticsActivity;
import com.truescend.gofit.pagers.home.heart.HeartActivity;
import com.truescend.gofit.pagers.home.oxygen.BloodOxygenActivity;
import com.truescend.gofit.pagers.home.pressure.BloodPressureActivity;
import com.truescend.gofit.pagers.home.sleep.SleepActivity;
import com.truescend.gofit.pagers.home.sleep.details.SleepDetailsActivity;
import com.truescend.gofit.pagers.home.sport.SportActivity;
import com.truescend.gofit.pagers.home.sport_mode.SportModeActivity;
import com.truescend.gofit.pagers.main.MainActivity;
import com.truescend.gofit.pagers.ranking.RankingActivity;
import com.truescend.gofit.pagers.scan.ScanningAndBindActivity;
import com.truescend.gofit.pagers.start.login.LoginActivity;
import com.truescend.gofit.pagers.start.register.RegisterActivity;
import com.truescend.gofit.pagers.start.resetpsw.ResetPswActivity;
import com.truescend.gofit.pagers.start.resetpsw.checker.ResetPswCheckerActivity;
import com.truescend.gofit.pagers.track.TrackFragment;
import com.truescend.gofit.pagers.track.bean.PathMapItem;
import com.truescend.gofit.pagers.track.record.TrackRecordActivity;
import com.truescend.gofit.pagers.track.run.RunningActivity;
import com.truescend.gofit.pagers.track.setting.RunSettingActivity;
import com.truescend.gofit.pagers.user.UserFragment;
import com.truescend.gofit.pagers.user.fit.google_fit.ConnectGoogleFitActivity;
import com.truescend.gofit.pagers.user.fit.strava.ConnectStravaActivity;
import com.truescend.gofit.pagers.user.fit.tmall_genie.TmallGenieAuthActivity;
import com.truescend.gofit.pagers.user.setting.UserSettingActivity;

import androidx.fragment.app.Fragment;

/**
 * 类型：页面跳转工具类
 * Author:Created by 泽鑫 on 2017/12/14 17:14.
 */

public class PageJumpUtil {

    /**
     * 共用 返回(请求码)
     */
    public static final int REQUEST_CODE_RESULT = 0x545;

    /**
     * 通过密保找回密码
     *
     * @param context
     */
    public static void startResetPswFromQuestionActivity(Activity context, String email, boolean isHasSetQuestion, GetQuestionsBean.DataBean bean) {
        context.startActivityForResult(new Intent(context, ResetPswActivity.class)
                        .putExtra("data", bean)
                        .putExtra("isHasSetQuestion", isHasSetQuestion)
                        .putExtra("email", email),
                REQUEST_CODE_RESULT
        );
    }


    /**
     * 跳转到注册
     *
     * @param context
     */
    public static void startRegisterActivity(Context context) {
        RegisterActivity.startActivity(context);
    }

    /**
     * 跳转到找回密码 前校验界面
     *
     * @param context
     */
    public static void startResetPswCheckerActivity(Context context) {
        ResetPswCheckerActivity.startActivity(context);
    }

    /**
     * 跳转主界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startMainActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    /**
     * 跳转到运动详情界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startSportActivity(Context context) {
        context.startActivity(new Intent(context, SportActivity.class));
    }

    /**
     * 跳转到蓝牙扫描绑定界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startScanningAndBindActivity(Context context) {
        context.startActivity(new Intent(context, ScanningAndBindActivity.class));
    }

    /**
     * 跳转到睡眠（当天）详情界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startSleepActivity(Context context) {
        context.startActivity(new Intent(context, SleepActivity.class));
    }

    /**
     * 跳转到睡眠（周月年）详情界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startSleepDetailsActivity(Context context) {
        context.startActivity(new Intent(context, SleepDetailsActivity.class));
    }

    /**
     * 跳转到心率界面
     *
     * @param context 从那个页面跳转
     */
    public static void startHeartActivity(Context context) {
        context.startActivity(new Intent(context, HeartActivity.class));
    }

    /**
     * 跳转到血压界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startPressureActivity(Context context) {
        context.startActivity(new Intent(context, BloodPressureActivity.class));
    }

    /**
     * 跳转到血氧界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startOxygenActivity(Context context) {
        context.startActivity(new Intent(context, BloodOxygenActivity.class));
    }

    /**
     * 跳转到跑步（轨迹）界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startRunningActivity(TrackFragment context, MapType.TYPE mapType) {
        context.startActivityForResult(new Intent(context.getActivity(), RunningActivity.class).putExtra(RunningActivity.KEY_MAP_TYPE, mapType), REQUEST_CODE_RESULT);
    }


    /**
     * 跳转到轨迹历史记录 界面
     *
     * @param context
     * @param item
     */
    public static void startTrackRecordActivity(Context context, PathMapItem item) {
        context.startActivity(new Intent(context, TrackRecordActivity.class).putExtra(TrackRecordActivity.KEY_PATH_MAP_ITEM, item));
    }

    /**
     * 跳转到跑步设置界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startRunSettingActivity(Context context) {
        context.startActivity(new Intent(context, RunSettingActivity.class));
    }

    /**
     * 跳转到设备设置界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startDeviceSettingActivity(Context context) {
        context.startActivity(new Intent(context, DeviceSettingActivity.class));
    }


    /**
     * 跳转到手环固件更新界面
     *
     * @param context
     * @param info
     * @param mac
     * @param isFixBand 是否救砖
     */
    public static void startBandUpdateActivity(Context context, DeviceInfo info, String mac, boolean isFixBand) {
        context.startActivity(new Intent(context, BandUpdateActivity.class)
                .putExtra(BandUpdateActivity.KEY_INFO, info)
                .putExtra(BandUpdateActivity.KEY_MAC, mac)
                .putExtra(BandUpdateActivity.KEY_IS_FIX_BAND, isFixBand)
        );
    }

    /**
     * 跳转到手环固件更新界面
     *
     * @param context
     * @param info
     * @param mac
     * @param upgradeid
     */
    public static void startBandUpdateFixBandActivityForResult(Activity context, DeviceInfo info, String mac, int upgradeid) {
        context.startActivityForResult(new Intent(context, BandUpdateActivity.class)
                        .putExtra(BandUpdateActivity.KEY_INFO, info)
                        .putExtra(BandUpdateActivity.KEY_MAC, mac)
                        .putExtra(BandUpdateActivity.KEY_IS_FIX_BAND, true)
                        .putExtra(BandUpdateActivity.KEY_UPGRADEID, upgradeid)
                , REQUEST_CODE_RESULT
        );
    }

    /**
     * 跳转到关于APP界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startAboutAppActivity(Context context) {
        context.startActivity(new Intent(context, AboutAppActivity.class));
    }

    /**
     * 跳转到意见反馈界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startFeedbackActivity(Context context) {
        context.startActivity(new Intent(context, FeedbackActivity.class));
    }

    /**
     * 跳转到远程相机界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startRemoteCameraActivity(Context context) {
        context.startActivity(new Intent(context, RemoteCameraProActivity.class));
    }

    /**
     * 跳转到图片预览
     * @param context
     */
    public static void startImageViewerActivity(Context context,String path) {
        ImageViewerActivity.startActivity(context,path);
    }

    /**
     * 跳转到健康提醒下子页面
     *
     * @param context
     * @param type
     */
    public static void startReminderActivity(Context context, int type) {
        Intent intent = new Intent();
        intent.putExtra(HealthReminderActivity.DRINK_OR_SCHEDULE, type);
        intent.setClass(context, ReminderActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到健康提醒界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startHealthReminderActivity(Context context) {
        context.startActivity(new Intent(context, HealthReminderActivity.class));
    }

    /**
     * 跳转到日程提醒界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startScheduleActivity(Context context) {
        context.startActivity(new Intent(context, ScheduleActivity.class));
    }

    /**
     * 跳转到添加日程提醒
     *
     * @param context 从哪个页面跳转
     */
    public static void startAddScheduleActivity(Context context, int id) {
        Intent intent = new Intent();
        intent.putExtra(ScheduleActivity.SCHEDULE_TYPE, id);
        intent.setClass(context, AddScheduleActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到历史日程提醒
     *
     * @param context 从哪个页面跳转
     */
    public static void startHistoryScheduleActivity(Context context) {
        context.startActivity(new Intent(context, HistoryScheduleActivity.class));
    }

    /**
     * 跳转到闹钟界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startAlarmClockActivity(Context context) {
        context.startActivity(new Intent(context, AlarmClockActivity.class));
    }

    /**
     * 跳转到添加闹钟页面
     *
     * @param context 从哪个页面跳转
     */
    public static void startAddAlarmClockActivity(Context context, int id) {
        Intent intent = new Intent();
        intent.putExtra(AlarmClockActivity.ALARM_CLOCK_TYPE, id);
        intent.setClass(context, AddAlarmClockActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到推送界面
     *
     * @param context 从哪个页面跳转
     */
    public static void startPushActivity(Context context) {
        context.startActivity(new Intent(context, PushActivity.class));
    }

    /**
     * 跳转到个人设置页面
     *
     * @param context 从哪个页面跳转
     */
    public static void startUserSettingActivity(UserFragment context) {
        context.startActivityForResult(new Intent(context.getActivity(), UserSettingActivity.class), REQUEST_CODE_RESULT);
    }

    /**
     * 跳转到个人设置页面
     *
     * @param context 从哪个页面跳转
     */
    public static void startUserSettingActivity(Activity context, @UserSettingActivity.NextType int type) {
        context.startActivity(new Intent(context, UserSettingActivity.class).putExtra(UserSettingActivity.KEY_NEXT_TYPE, type));
    }

    public static void startConnectGoogleFitActivity(Context context) {
        context.startActivity(new Intent(context, ConnectGoogleFitActivity.class));
    }

    public static void startConnectStravaActivity(Context context) {
        context.startActivity(new Intent(context, ConnectStravaActivity.class));
    }


    /**
     * 跳转到登录界面
     *
     * @param context    从哪个页面跳转
     * @param isLoginOut 退出登录吗? 还是只是跳转 但不退出?
     */
    public static void startLoginActivity(Context context, boolean isLoginOut) {
        if (isLoginOut) {
            //轻量存储全部清空 只保留用户账号
            UserStorage.setPassword("");
            UserStorage.setAccessToken("");
            UserStorage.setUserId(-1);
            //APP日期选择的时间重置为今天
            App.setSelectedCalendar(DateUtil.getCurrentCalendar());
            //必须断开蓝牙 不然旧设备会默认给新用户
            SNBLEHelper.disconnect();
            //跳转到登录界面 (同时清除栈顶)
            context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }

    /**
     * 跳转到我的体检页面
     *
     * @param context 从哪个页面跳转
     */
    public static void startHealthCheckActivity(Context context) {
        context.startActivity(new Intent(context, HealthCheckActivity.class));
    }

    /**
     * 跳转到帮助页&隐私政策
     *
     * @param context
     */
    public static void startHelpActivity(Context context) {
        HelpActivity.startActivity(context);
//        try {
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(Constant.URL.HELP));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        } catch (Exception ignored) {
//        }
    }

    /**
     * 跳转到添加朋友
     *
     * @param context 从哪个页面跳转
     */
    public static void startFriendsSearchActivity(Context context) {
        FriendsSearchActivity.startActivity(context);
    }

    /**
     * 跳转到朋友列表
     *
     * @param context 从哪个页面跳转
     */
    public static void startFriendsListActivity(Context context) {
        FriendsListActivity.startActivity(context);
    }

    /**
     * 跳转到朋友数据查看
     *
     * @param context 从哪个页面跳转
     */
    public static void startFriendsDataActivity(Context context, int user_id) {
        FriendsDataActivity.startActivity(context, user_id);
    }

    /**
     * 跳转到二维码分享
     *
     * @param context 从哪个页面跳转
     */
    public static void startMyQRCActivity(Context context) {
        MyQRCActivity.startActivity(context);
    }

    /**
     * 跳转到朋友主页
     *
     * @param context 从哪个页面跳转
     * @param user_id
     */
    public static void startFriendsInfoActivity(Context context, int user_id) {
        FriendsInfoActivity.startActivity(context, user_id);
    }

    /**
     * 用户饮食减肥计划目标参数设置
     *
     * @param fragment
     */
    public static void startDietTargetSettingActivity(HomeFragment fragment) {
        fragment.startActivityForResult(new Intent(fragment.getActivity(), DietTargetSettingActivity.class), REQUEST_CODE_RESULT);
    }

    /**
     * 用户饮食减肥进餐列表
     *
     * @param context
     */
    public static void startDietListMealActivity(Context context) {
        DietListMealActivity.startActivity(context);
    }

    /**
     * 用户饮食减肥进餐列表
     *
     * @param fragment
     */
    public static void startDietListMealActivity(HomeFragment fragment) {
        fragment.startActivityForResult(new Intent(fragment.getActivity(), DietListMealActivity.class), REQUEST_CODE_RESULT);
    }

    /**
     * 用户饮食减肥添加一餐
     */
    public static void startDietAddMealActivity(Activity activity) {
        DietEditMealActivity.startDietAddMealActivity(activity, REQUEST_CODE_RESULT);
    }

    /**
     * 用户饮食减肥更新一餐
     */
    public static void startDietUpdateMealActivity(Activity activity, MealBean item) {
        DietEditMealActivity.startDietUpdateMealActivity(activity, REQUEST_CODE_RESULT, item);
    }

    /**
     * 用户饮食减肥  图表/分析
     *
     * @param context
     */
    public static void startDietStatisticsActivity(Context context) {
        DietStatisticsActivity.startActivity(context);
    }

    /**
     * 世界排行
     *
     * @param context
     */
    public static void startRankingActivity(Context context) {
        RankingActivity.startActivity(context);
    }

    /**
     * 更换壁纸
     *
     * @param context
     */
    public static void startDeviceWallpaperActivity(Context context) {
        WallpaperActivity.startActivity(context);
    }


    /**
     * 运动模式
     *
     * @param context
     */
    public static void startSportModeActivity(Context context) {
        SportModeActivity.startActivity(context);
    }

    /**
     * 关联天猫精灵
     *
     * @param context
     */
    public static void startTmallGenieAuthActivity(Context context) {
        TmallGenieAuthActivity.startActivity(context);
    }

    /**
     * 跳转到应用市场
     *
     * @param context
     * @param packageName
     * @throws ActivityNotFoundException
     */
    public static void startToMarket(Context context, String packageName) throws ActivityNotFoundException {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            //如果手机没有安装应用市场
            throw e;
        }
    }

    public static void startToApp(Context context, String packageName) throws ActivityNotFoundException {
        try {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(packageName));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            //如果手机没有安装这个app
            throw e;
        }
    }


}
