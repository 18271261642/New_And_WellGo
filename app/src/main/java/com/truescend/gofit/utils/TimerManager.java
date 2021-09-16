//package com.truescend.gofit.utils;
//
//import android.app.AlarmManager;
//import android.app.Application;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.support.annotation.IntDef;
//import android.support.annotation.NonNull;
//import android.support.v4.app.AlarmManagerCompat;
//
//import com.evernote.android.job.Job;
//import com.evernote.android.job.JobCreator;
//import com.evernote.android.job.JobManager;
//import com.evernote.android.job.JobRequest;
//import com.truescend.gofit.receive.TimerReceiver;
//
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//
///**
// * 作者:东芝(2018/3/26).
// * 功能:闹钟/计时器
// */
//
//public class TimerManager {
//    public final static String KEY_FLAG = "flag";
//    /**
//     * 为了防止ID重复 ,所以起的这样子的值
//     */
//    private static final int FLAG_DEF_INDEX = 0x50001000;
//    /**
//     * 天气
//     */
//    public static final int FLAG_WEATHER = FLAG_DEF_INDEX + 1;
//    /**
//     * 日程
//     */
//    public static final int FLAG_SCHEDULE = FLAG_DEF_INDEX + 2;
//
//    @IntDef(flag = true, value = {
//            FLAG_WEATHER,
//            FLAG_SCHEDULE
//    })
//    @Retention(RetentionPolicy.SOURCE)
//    @interface Flag {
//    }
//
//
//    public static void init(Application application) {
//        JobManager.create(application).addJobCreator(new DemoJobCreator());
//    }
//
//
//    static class DemoJobCreator implements JobCreator {
//        @Override
//        public Job create(@NonNull String tag) {
//            switch (tag) {
//                case DemoSyncJob.TAG:
//                    return new DemoSyncJob();
//                default:
//                    return null;
//            }
//        }
//    }
//
//    static class DemoSyncJob extends Job {
//        static final String TAG = "job_demo_tag";
//
//        @Override
//        @NonNull
//        protected Result onRunJob(Params params) {
//            // run your job here
//            return Result.SUCCESS;
//            //出问题了重新计划
////          return Result.RESCHEDULE;
//        }
//
//        public static void scheduleJob() {
//            new JobRequest.Builder(DemoSyncJob.TAG)
//                    .setExecutionWindow(30_000L, 40_000L)
//                    //是否在充电时执行
//                    .setRequiresCharging(true)
//                    //是否在空闲时执行
//                    .setRequiresDeviceIdle(true)
//                    //设置需要的网络条件
//                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
//                    //携带的数据
////                    .setExtras(extras)
//                    .build()
//                    .schedule();
//        }
//
//    }
//
//    private void cancelJob(int jobId) {
//        JobManager.instance().cancel(jobId);
//    }
//
//    /**
//     * 设置一个计时器 传入触发时间, 达到触发时间后在TimerReceiver中回调
//     *
//     * @param context         上下文
//     * @param flag            标记, 用来区分计时器类型
//     * @param triggerAtMillis 触发时间
//     */
//
//    public static void setExactAndAllowWhileIdle(Context context, @Flag int flag, long triggerAtMillis) {
//        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        if (am != null) {
//            Intent intent = new Intent(TimerReceiver.ACTION_ALARM).putExtra(KEY_FLAG, flag);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, flag, intent, 0);
//            AlarmManagerCompat.setExactAndAllowWhileIdle(am, AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
//        }
//    }
//
//    /**
//     * 取消计时器
//     *
//     * @param context 上下文
//     * @param flag    标记, 用来区分计时器类型
//     */
//    public static void cancel(Context context, @Flag int flag) {
//        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        if (am != null) {
//            Intent intent = new Intent(TimerReceiver.ACTION_ALARM).putExtra(KEY_FLAG, flag);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, flag, intent, 0);
//            am.cancel(pendingIntent);
//        }
//    }
//
//}
