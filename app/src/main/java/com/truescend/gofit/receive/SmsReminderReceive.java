package com.truescend.gofit.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.db.data.config.bean.RemindConfig;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.truescend.gofit.utils.ContactsUtil;

import java.util.List;

/**
 * 功能：短信接收广播监听器
 * 监听收到短信的
 * Author:Created by 泽鑫 on 2018/2/23 18:24.
 */

public class SmsReminderReceive extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage message = null;
        if (null != bundle){
            Object[] smsObject = (Object[])bundle.get("pdus");
            if (smsObject == null){
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (Object object : smsObject) {
                message = SmsMessage.createFromPdu((byte[]) object);
                if (message != null) {
                    sb.append(message.getMessageBody());
                }
            }

            String number = message==null?" ":message.getOriginatingAddress();
            String content = sb.toString();
            String contactName = ContactsUtil.lookForContacts(context, number);
            if (TextUtils.isEmpty(contactName)){
                contactName = number;
            }
            pushSMS(contactName, content);
        }
    }

    /**
     * 推送短信
     * @param name 姓名
     * @param content 内容
     */
    private void pushSMS(String name, String content){
        DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
        int user_id = 0;
        try {
            user_id = AppUserUtil.getUser().getUser_id();
        } catch (Exception e) {
            return;
        }
        RemindConfig remindConfig = deviceConfigDao.queryRemindConfig(user_id);
        if (remindConfig != null && remindConfig.isRemindSMS()){
            List<byte[]> list = SNCMD.get().setSMSMessage(name, content);
            for (byte[] bytes : list) {
                SNBLEHelper.sendCMD(bytes);
            }
        }
    }
}
