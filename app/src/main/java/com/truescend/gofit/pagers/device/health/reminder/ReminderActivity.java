package com.truescend.gofit.pagers.device.health.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sn.app.db.data.config.bean.HealthReminderConfig;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.device.bean.ItemTime;
import com.truescend.gofit.pagers.device.bean.ItemWeek;
import com.truescend.gofit.pagers.device.health.HealthReminderActivity;
import com.truescend.gofit.views.TimePicker;
import com.truescend.gofit.views.TitleLayout;

import butterknife.BindView;

/**
 * 功能：健康提醒子页面
 * Author:Created by 泽鑫 on 2018/3/17 10:39.
 */

public class ReminderActivity extends BaseActivity<ReminderPresenterImpl, ReminderContract.IView> implements ReminderContract.IView {
    @BindView(R.id.tpReminderStartTime)
    TimePicker tpReminderStartTime;
    @BindView(R.id.tpReminderEndTime)
    TimePicker tpReminderEndTime;
    @BindView(R.id.ilReminderTimeInterval)
    View ilReminderTimeInterval;
    @BindView(R.id.ilReminderWeek)
    View ilReminderWeek;

    private ItemTime intervalItem;
    private ItemWeek weekItem;

    private HealthReminderConfig.HealthReminder healthReminder;
    private boolean[] week;
    private int type;

    @Override
    protected ReminderPresenterImpl initPresenter() {
        return new ReminderPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_reminder;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initItem();
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        Intent intent = getIntent();
        type = intent.getIntExtra(HealthReminderActivity.DRINK_OR_SCHEDULE, HealthReminderConfig.HealthReminder.TYPE_SEDENTARY);
        switch (type){
            case HealthReminderConfig.HealthReminder.TYPE_SEDENTARY:
                titleLayout.setTitle(getString(R.string.content_remind_sedentary));
                break;
            case HealthReminderConfig.HealthReminder.TYPE_DRINK:
                titleLayout.setTitle(getString(R.string.content_reminder_drink));
                break;
        }
        titleLayout.setLeftIconFinishActivity(this);
        titleLayout.addRightItem(TitleLayout.ItemBuilder.Builder()
                .setText(R.string.content_save)
                .setTextColor(getResources().getColor(R.color.black))
                .setTextSize(16)
                .setTextStyle(true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkDayAllFalse()){
                            SNToast.toast(getString(R.string.content_choose_one_day_reminder));
                            return;
                        }
                        save();
                        finish();
                    }
                })
        );
    }


    private void initItem() {
        tpReminderStartTime.setOnTimePickerListener(timePickerListener);
        tpReminderEndTime.setOnTimePickerListener(timePickerListener);

        intervalItem = new ItemTime(ilReminderTimeInterval);
        intervalItem.setOnCheckedChangeListener(checkedChangeListener);

        weekItem = new ItemWeek(ilReminderWeek);
        weekItem.setOnChooseListener(onChooseListener);

        getPresenter().requestShowReminderData(type);

    }

    @Override
    public void onShowReminderData(HealthReminderConfig.HealthReminder healthReminder) {
        this.healthReminder = healthReminder;
        week = healthReminder.getWeek();

        tpReminderStartTime.setHour(healthReminder.getItemStartTimeHour());
        tpReminderStartTime.setMinute(healthReminder.getItemStartTimeMinute());

        tpReminderEndTime.setHour(healthReminder.getItemEndTimeHour());
        tpReminderEndTime.setMinute(healthReminder.getItemEndTimeMinute());

        switch (healthReminder.getItemIntervalTime()) {
            case 30:
                intervalItem.setHalfHourChecked(true);
                break;
            case 60:
                intervalItem.setOneHourChecked(true);
                break;
            case 120:
                intervalItem.setTwoHourChecked(true);
                break;
            case 180:
                intervalItem.setThreeHourChecked(true);
                break;
        }
        correctErrorTime(
                tpReminderStartTime.getHour(),
                tpReminderStartTime.getMinute(),
                tpReminderEndTime.getHour(),
                tpReminderEndTime.getMinute());

        hideOrShow(
                tpReminderStartTime.getHour(),
                tpReminderStartTime.getMinute(),
                tpReminderEndTime.getHour(),
                tpReminderEndTime.getMinute());


        weekItem.setSundayCheck(week[0]);
        weekItem.setMondayCheck(week[1]);
        weekItem.setTuesdayCheck(week[2]);
        weekItem.setWednesdayCheck(week[3]);
        weekItem.setThursdayCheck(week[4]);
        weekItem.setFridayCheck(week[5]);
        weekItem.setSaturdayCheck(week[6]);
    }


    private TimePicker.OnTimePickerListener timePickerListener = new TimePicker.OnTimePickerListener() {
        @Override
        public void onValueChange(TimePicker picker, int hour, int minute) {
            correctErrorTime(
                    tpReminderStartTime.getHour(),
                    tpReminderStartTime.getMinute(),
                    tpReminderEndTime.getHour(),
                    tpReminderEndTime.getMinute());
            hideOrShow(
                    tpReminderStartTime.getHour(),
                    tpReminderStartTime.getMinute(),
                    tpReminderEndTime.getHour(),
                    tpReminderEndTime.getMinute());
        }
    };


    private ItemTime.OnCheckedChangeListener checkedChangeListener = new ItemTime.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(ItemTime item, int time) {
            switch (time) {
                case 30:
                    healthReminder.setItemIntervalTime(30);
                    break;
                case 60:
                    healthReminder.setItemIntervalTime(60);
                    break;
                case 120:
                    healthReminder.setItemIntervalTime(120);
                    break;
                case 180:
                    healthReminder.setItemIntervalTime(180);
                    break;
            }
        }
    };

    private ItemWeek.OnChooseListener onChooseListener = new ItemWeek.OnChooseListener() {
        @Override
        public void onChoose(ItemWeek itemWeek, int day, boolean isChecked) {
            switch (day) {
                case 0:
                    week[0] = isChecked;
                    break;
                case 1:
                    week[1] = isChecked;
                    break;
                case 2:
                    week[2] = isChecked;
                    break;
                case 3:
                    week[3] = isChecked;
                    break;
                case 4:
                    week[4] = isChecked;
                    break;
                case 5:
                    week[5] = isChecked;
                    break;
                case 6:
                    week[6] = isChecked;
                    break;
            }
        }
    };

    /**
     * 显示或隐藏
     *
     * @param sHour   开始小时
     * @param sMinute 开始分钟
     * @param eHour   结束小时
     * @param eMinute 结束分钟
     */
    private void hideOrShow(int sHour, int sMinute, int eHour, int eMinute) {
        int time = eHour * 60 + eMinute - sHour * 60 - sMinute;
        int selectTime = healthReminder.getItemIntervalTime();
        if (time < 30) {
            intervalItem.setHalfHourEnabled(false);
            intervalItem.setOneHourEnabled(false);
            intervalItem.setTwoHourEnabled(false);
            intervalItem.setThreeHourEnabled(false);

        } else if (time < 60) {
            intervalItem.setHalfHourEnabled(true);
            intervalItem.setOneHourEnabled(false);
            intervalItem.setTwoHourEnabled(false);
            intervalItem.setThreeHourEnabled(false);

            if(selectTime != 30){
                intervalItem.setHalfHourChecked(true);
            }

        } else if (time < 120) {
            intervalItem.setHalfHourEnabled(true);
            intervalItem.setOneHourEnabled(true);
            intervalItem.setTwoHourEnabled(false);
            intervalItem.setThreeHourEnabled(false);

            if(selectTime != 30 && selectTime != 60){
                intervalItem.setOneHourChecked(true);
            }
        } else if (time < 180) {
            intervalItem.setHalfHourEnabled(true);
            intervalItem.setOneHourEnabled(true);
            intervalItem.setTwoHourEnabled(true);
            intervalItem.setThreeHourEnabled(false);

            if (selectTime != 30 && selectTime != 60 && selectTime != 120){
                intervalItem.setTwoHourChecked(true);
            }
        } else {
            intervalItem.setHalfHourEnabled(true);
            intervalItem.setOneHourEnabled(true);
            intervalItem.setTwoHourEnabled(true);
            intervalItem.setThreeHourEnabled(true);
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
        int startTime = sHour * 60 + sMinute;
        int endTime = eHour * 60 + eMinute;
        int finalMinute = sMinute + 30;

        if (sHour == 23){
            tpReminderEndTime.setHour(sHour);
            if (sMinute > 29){
                tpReminderStartTime.setMinute(29);
                tpReminderEndTime.setMinute(59);
            }else if (finalMinute > eMinute){
                tpReminderEndTime.setMinute(finalMinute);
            }
        }else if (sHour < eHour){
            if ((startTime + 30) > endTime){
                tpReminderEndTime.setMinute(finalMinute);
            }
        }else {
            if (finalMinute > eMinute && sMinute > 29){
                tpReminderEndTime.setHour(sHour + 1);
                tpReminderEndTime.setMinute(finalMinute);
            }else {
                tpReminderEndTime.setHour(sHour);
                tpReminderEndTime.setMinute(finalMinute);
            }
        }
    }

    /**
     * 保存数据
     */
    private void save() {
        healthReminder.setItemStartTimeHour(tpReminderStartTime.getHour());
        healthReminder.setItemStartTimeMinute(tpReminderStartTime.getMinute());
        healthReminder.setItemEndTimeHour(tpReminderEndTime.getHour());
        healthReminder.setItemEndTimeMinute(tpReminderEndTime.getMinute());
        healthReminder.setWeek(week);
        getPresenter().requestUpdateReminderData(healthReminder, type);
    }

    private boolean checkDayAllFalse(){
        for (boolean day : week){
            if (day){
                return false;
            }
        }
        return true;
    }
}
