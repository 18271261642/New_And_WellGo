//package com.truescend.gofit.receive;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import com.sn.utils.SNLog;
//import com.truescend.gofit.BuildConfig;
//import com.truescend.gofit.utils.TimerManager;
//
///**
// * 作者:东芝(2018/3/26).
// * 功能:定时认为监听
// */
//
//public class TimerReceiver extends BroadcastReceiver {
//    public final static String ACTION_ALARM = BuildConfig.APPLICATION_ID + ".ACTION_ALARM";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (ACTION_ALARM.equals(intent.getAction())) {
//            int flag = intent.getIntExtra(TimerManager.KEY_FLAG, -1);
//            switch (flag) {
//                //日程
//                case TimerManager.FLAG_SCHEDULE:
//                    SNLog.d("日程查询...");
//                    break;
//                //天气刷新
//                case TimerManager.FLAG_WEATHER:
//                    SNLog.d("天气查询...");
//                    break;
//            }
//        }
//    }
//}
