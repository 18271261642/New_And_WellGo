package com.truescend.gofit.pagers.common.dialog;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.sn.app.db.data.config.bean.HealthReminderConfig;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;
import com.truescend.gofit.pagers.common.bean.ItemWeekCycle;
import com.truescend.gofit.pagers.device.bean.ItemTimeInterval;
import com.truescend.gofit.views.TimePicker;

/**
 * 功能：健康提醒弹框
 * Author:Created by 泽鑫 on 2018/1/5 18:37.
 */
@Deprecated
public class HealthReminderDialog {
    private final boolean[] week;
    private HealthReminderConfig.HealthReminder healthReminder;
    private BaseDialog dialog;

    private ItemTimeInterval timeIntervalItem;
    private ItemWeekCycle weekCycleItem;

    private TimePicker startTime;
    private TimePicker endTime;

    public HealthReminderDialog(Context context,HealthReminderConfig.HealthReminder healthReminder) {
        this.healthReminder = healthReminder;
        week = healthReminder.getWeek();
        dialog = new BaseDialog.Builder(context)
                .setContentView(R.layout.dialog_health_reminder)
                .fullWidth()
                .fromBottom(true)
                .create();
        initItem();
        initData();
    }


    /**
     * 获取控件并开启监听
     */
    private void initItem() {
        TextView tvHealthReminderExit = dialog.findViewById(R.id.tvHealthReminderExit);
        TextView tvHealthReminder = dialog.findViewById(R.id.tvHealthReminderDone);

        startTime = dialog.findViewById(R.id.tpHealthReminderStartTime);
        endTime = dialog.findViewById(R.id.tpHealthReminderEndTime);

        View ilHealthReminderTimeInterval = dialog.findViewById(R.id.ilHealthReminderTimeInterval);
        View ilHealthReminderRepeatCycle = dialog.findViewById(R.id.ilHealthReminderRepeatCycle);

        timeIntervalItem = new ItemTimeInterval(ilHealthReminderTimeInterval);
        weekCycleItem = new ItemWeekCycle(ilHealthReminderRepeatCycle);

        if (tvHealthReminderExit != null) {
            tvHealthReminderExit.setOnClickListener(clickListener);
        }
        if (tvHealthReminder != null) {
            tvHealthReminder.setOnClickListener(clickListener);
        }

        startTime.setOnTimePickerListener(timePickerListener);
        endTime.setOnTimePickerListener(timePickerListener);

        timeIntervalItem.setHalfHourOnClickListener(clickListener);
        timeIntervalItem.setOneHourOnClickListener(clickListener);
        timeIntervalItem.setTwoHourOnClickListener(clickListener);
        timeIntervalItem.setThreeHourOnClickListener(clickListener);

        weekCycleItem.setSundayOnCheckedChangeListener(checkChangeListener);
        weekCycleItem.setMondayOnCheckedChangeListener(checkChangeListener);
        weekCycleItem.setTuesdayOnCheckedChangeListener(checkChangeListener);
        weekCycleItem.setWednesdayOnCheckedChangeListener(checkChangeListener);
        weekCycleItem.setThursdayOnCheckedChangeListener(checkChangeListener);
        weekCycleItem.setFridayOnCheckedChangeListener(checkChangeListener);
        weekCycleItem.setSaturdayOnCheckedChangeListener(checkChangeListener);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        startTime.setHour(healthReminder.getItemStartTimeHour());
        startTime.setMinute(healthReminder.getItemStartTimeMinute());
        endTime.setHour(healthReminder.getItemEndTimeHour());
        endTime.setMinute(healthReminder.getItemEndTimeMinute());

//        switch (healthReminder.getItemIntervalTime()) {
//            case 30:
//                timeIntervalItem.setHalfHourChecked(true);
//                break;
//            case 60:
//                timeIntervalItem.setOneHourChecked(true);
//                break;
//            case 120:
//                timeIntervalItem.setTwoHourChecked(true);
//                break;
//            case 180:
//                timeIntervalItem.setThreeHourChecked(true);
//                break;
//        }
        switch (healthReminder.getItemIntervalTime()) {
            case 1:
                timeIntervalItem.setHalfHourChecked(true);
                break;
            case 2:
                timeIntervalItem.setOneHourChecked(true);
                break;
            case 3:
                timeIntervalItem.setTwoHourChecked(true);
                break;
            case 4:
                timeIntervalItem.setThreeHourChecked(true);
                break;
        }

        hideOfShow(startTime.getHour(), startTime.getMinute(), endTime.getHour(), endTime.getMinute());


        weekCycleItem.setSundayCheck(week[0]);
        weekCycleItem.setMondayCheck(week[1]);
        weekCycleItem.setTuesdayCheck(week[2]);
        weekCycleItem.setWednesdayCheck(week[3]);
        weekCycleItem.setThursdayCheck(week[4]);
        weekCycleItem.setFridayCheck(week[5]);
        weekCycleItem.setSaturdayCheck(week[6]);
    }


    public void show() {
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.rbTimeIntervalHalfHour:
//                    healthReminder.setItemIntervalTime(30);
//                    break;
//                case R.id.rbTimeIntervalOneHour:
//                    healthReminder.setItemIntervalTime(60);
//                    break;
//                case R.id.rbTimeIntervalTwoHour:
//                    healthReminder.setItemIntervalTime(120);
//                    break;
//                case R.id.rbTimeIntervalThreeHour:
//                    healthReminder.setItemIntervalTime(180);
//                    break;
                case R.id.rbTimeIntervalHalfHour:
                    healthReminder.setItemIntervalTime(1);
                    break;
                case R.id.rbTimeIntervalOneHour:
                    healthReminder.setItemIntervalTime(2);
                    break;
                case R.id.rbTimeIntervalTwoHour:
                    healthReminder.setItemIntervalTime(3);
                    break;
                case R.id.rbTimeIntervalThreeHour:
                    healthReminder.setItemIntervalTime(4);
                    break;
                case R.id.tvHealthReminderExit:
                    dismiss();
                    break;
                case R.id.tvHealthReminderDone:
                    saveAndSendHealthReminder();
                    if (onHealthReminderSettingListener != null) {
                        onHealthReminderSettingListener.onDataChanged(healthReminder);
                    }
                    dismiss();
                    break;
            }
        }
    };

    private TimePicker.OnTimePickerListener timePickerListener = new TimePicker.OnTimePickerListener() {
        @Override
        public void onValueChange(TimePicker picker, int hour, int minute) {
            correctErrorTime(startTime.getHour(), startTime.getMinute(), endTime.getHour(), endTime.getMinute());
            hideOfShow(startTime.getHour(), startTime.getMinute(), endTime.getHour(), endTime.getMinute());
        }
    };

    private CompoundButton.OnCheckedChangeListener checkChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.cbWeekCycleSunday:
                    week[0] = isChecked;
                    break;
                case R.id.cbWeekCycleMonday:
                    week[1] = isChecked;
                    break;
                case R.id.cbWeekCycleTuesday:
                    week[2] = isChecked;
                    break;
                case R.id.cbWeekCycleWednesday:
                    week[3] = isChecked;
                    break;
                case R.id.cbWeekCycleThursday:
                    week[4] = isChecked;
                    break;
                case R.id.cbWeekCycleFriday:
                    week[5] = isChecked;
                    break;
                case R.id.cbWeekCycleSaturday:
                    week[6] = isChecked;
                    break;
            }
        }
    };

    /**
     * 显示或隐藏时间间隔
     *
     * @param sHour   开始小时
     * @param sMinute 开始分钟
     * @param eHour   结束小时
     * @param eMinute 结束分钟
     */
    private void hideOfShow(int sHour, int sMinute, int eHour, int eMinute) {
        int time = eHour * 60 + eMinute - sHour * 60 - sMinute;
        if (time < 30) {
            timeIntervalItem.setHalfHourEnabled(false);
            timeIntervalItem.setOneHourEnabled(false);
            timeIntervalItem.setTwoHourEnabled(false);
            timeIntervalItem.setThreeHourEnabled(false);

        } else if (time >= 30 && time < 60) {
            timeIntervalItem.setHalfHourEnabled(true);
            timeIntervalItem.setOneHourEnabled(false);
            timeIntervalItem.setTwoHourEnabled(false);
            timeIntervalItem.setThreeHourEnabled(false);

        } else if (time >= 60 && time < 120) {
            timeIntervalItem.setHalfHourEnabled(true);
            timeIntervalItem.setOneHourEnabled(true);
            timeIntervalItem.setTwoHourEnabled(false);
            timeIntervalItem.setThreeHourEnabled(false);
        } else if (time >= 120 && time < 180) {
            timeIntervalItem.setHalfHourEnabled(true);
            timeIntervalItem.setOneHourEnabled(true);
            timeIntervalItem.setTwoHourEnabled(true);
            timeIntervalItem.setThreeHourEnabled(false);
        } else if (time >= 180) {
            timeIntervalItem.setHalfHourEnabled(true);
            timeIntervalItem.setOneHourEnabled(true);
            timeIntervalItem.setTwoHourEnabled(true);
            timeIntervalItem.setThreeHourEnabled(true);
        }
    }

    /**
     * 强制选择的时间结束时间不能小于开始时间
     *
     * @param sHour   开始小时
     * @param sMinute 开始分钟
     * @param eHour   结束小时
     * @param eMinute 结束分钟
     */
    private void correctErrorTime(int sHour, int sMinute, int eHour, int eMinute) {
        if (eHour < sHour) {
            endTime.setHour(sHour);
        }
        if ((eHour == sHour) && (eMinute < sMinute)) {
            endTime.setMinute(sMinute);
        }
    }

//
//    private boolean checkParameters() {
//        int interval = healthReminder.getItemIntervalTime();
//        switch (interval) {
//            case 30:
//                if (timeIntervalItem.rbTimeIntervalHalfHour.isEnabled())
//                    return false;
//            case 60:
//                if (timeIntervalItem.rbTimeIntervalOneHour.isEnabled())
//                    return false;
//            case 120:
//                if (timeIntervalItem.rbTimeIntervalTwoHour.isEnabled())
//                    return false;
//            case 180:
//                if (timeIntervalItem.rbTimeIntervalThreeHour.isEnabled())
//                    return false;
//        }
//        return true;
//    }

    /**
     * 保存状态并发送
     */
    private void saveAndSendHealthReminder() {

        healthReminder.setItemStartTimeHour(startTime.getHour());
        healthReminder.setItemStartTimeMinute(startTime.getMinute());
        healthReminder.setItemEndTimeHour(endTime.getHour());
        healthReminder.setItemEndTimeMinute(endTime.getMinute());
        healthReminder.setWeek(week);


    }
    private OnHealthReminderSettingListener onHealthReminderSettingListener;

    public void setOnHealthReminderSettingListener(OnHealthReminderSettingListener onHealthReminderSettingListener) {
        this.onHealthReminderSettingListener = onHealthReminderSettingListener;
    }

    public interface OnHealthReminderSettingListener{
        void onDataChanged(HealthReminderConfig.HealthReminder healthReminder);
    }
}
