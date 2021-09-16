package com.truescend.gofit.receive;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.db.data.config.bean.RemindConfig;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.utils.SNLog;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.R;
import com.truescend.gofit.utils.ContactsUtil;

import java.util.List;

/**
 * 功能：来电提醒广播接收器
 * 接收系统广播，获取来电手机号码
 * Author:Created by 泽鑫 on 2018/2/23 18:31.
 */

public class CallReminderReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String contactName;
        String action = intent.getAction();
        if (action != null && !action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                int state = telephonyManager.getCallState();
                String callPhoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if (callPhoneNumber != null){
                    contactName = ContactsUtil.lookForContacts(context, callPhoneNumber);
                }else {
                    contactName = context.getString(R.string.content_unknown_number);
                }
              SNLog.i("查询联系人:" + contactName);
                if (!BuildConfig.isGooglePlayVersion&&TextUtils.isEmpty(callPhoneNumber)&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    Android P 之后, 读取来电号码需要请求READ_CALL_LOG权限,同时当前的onReceive会接收两次(详情看官网文档)
//                    第一次是null,第二次才会拿到电话号码, 于是凡是9.0+null 都return掉 等待第二次调用.
                    return;
//                    谷歌新规要求去掉READ_CALL_LOG 权限
//                  contactName = context.getString(R.string.content_call);
                }
                int user_id =0;
                try {
                    user_id = AppUserUtil.getUser().getUser_id();
                } catch (Exception e) {
                    return;
                }
                DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);

                RemindConfig remindConfig = deviceConfigDao.queryRemindConfig(user_id);
                if(remindConfig!=null) {
                    switch (state) {
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            if (remindConfig.isRemindCall()) {
                                //接电话
                                SNBLEHelper.sendCMD(SNCMD.get().setCallStatus(1));
                            }
                            break;
                        case TelephonyManager.CALL_STATE_RINGING:
                            if (remindConfig.isRemindCall()) {
                                //TODO 2018 10 09 问过郑工  说可以去掉标题
                                //String name = context.getString(R.string.content_call);
                                String name = " ";

                                //来电提醒 ,发送电话号码...
                                List<byte[]> list1 = SNCMD.get().setCallReminderMessage(name, contactName);
                                for (byte[] bytes : list1) {
                                    SNBLEHelper.sendCMD(bytes);
                                }
                            }
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            if (remindConfig.isRemindCall()) {
                                //挂电话
                                SNBLEHelper.sendCMD(SNCMD.get().setCallStatus(2));
                                ContactsUtil.modifyingVolume(context, false);
                            }
                            break;
                    }
                }
            }

        }
    }

}
