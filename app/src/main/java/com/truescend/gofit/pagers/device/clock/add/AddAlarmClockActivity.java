package com.truescend.gofit.pagers.device.clock.add;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sn.app.db.data.clock.AlarmClockBean;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.DateUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.device.bean.ItemWeek;
import com.truescend.gofit.pagers.device.clock.AlarmClockActivity;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.views.TimePicker;
import com.truescend.gofit.views.TitleLayout;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

/**
 * 功能：添加闹钟或编辑页面
 * Author:Created by 泽鑫 on 2018/1/18 14:27.
 */

public class AddAlarmClockActivity extends BaseActivity<AddAlarmClockPresenterImpl, IAddAlarmClockContract.IView> implements IAddAlarmClockContract.IView {
    @BindView(R.id.tpAddAlarmClockTime)
    TimePicker tpAddAlarmClockTime;
    @BindView(R.id.tvAddAlarmClockRepetitionPeriod)
    TextView tvAddAlarmClockRepetitionPeriod;
    @BindView(R.id.ilAddAlarmClockWeekCycle)
    View ilAddAlarmClockWeekCycle;

    private ItemWeek weekItem;
    private AlarmClockBean alarmClockBean = new AlarmClockBean();
    private boolean[] week;

    @Override
    protected AddAlarmClockPresenterImpl initPresenter() {
        return new AddAlarmClockPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_add_alarm_clock;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initItem();
        initData();
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        titleLayout.setTitle(getString(R.string.title_alarm));
        titleLayout.setLeftIconFinishActivity(this);
        titleLayout.addRightItem(TitleLayout.ItemBuilder.Builder()
                .setText(R.string.content_save)
                .setTextColor(getResources().getColor(R.color.black))
                .setTextSize(16)
                .setTextStyle(true)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveDataToDataBase();
                        alarmClockBean.setUser_id(AppUserUtil.getUser().getUser_id());
                        alarmClockBean.setWeek(week);
                        alarmClockBean.setSwitchStatues(true);
                        getPresenter().requestUpdateAlarmClock(alarmClockBean);
                        finish();
                    }
                })
        );
    }

    private void initItem() {
        weekItem = new ItemWeek(ilAddAlarmClockWeekCycle);
        weekItem.setOnChooseListener(checkChangeListener);
    }


    private void initData() {
        int hour = DateUtil.getHour(new Date());
        int minute = DateUtil.getMinute(new Date());
        Intent intent = getIntent();
        week = alarmClockBean.getWeek();
        int id = intent.getIntExtra(AlarmClockActivity.ALARM_CLOCK_TYPE, AlarmClockActivity.ALARM_CLOCK_ADD);
        if (id != AlarmClockActivity.ALARM_CLOCK_ADD) {//修改闹钟，则执行这里
            getPresenter().requestEditAlarmClock(id);
            String date = alarmClockBean.getDate();
            try {
                Calendar calendar = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD_HH_MM, date);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        tpAddAlarmClockTime.setHour(hour);
        tpAddAlarmClockTime.setMinute(minute);

        weekItem.setSundayCheck(week[0]);
        weekItem.setMondayCheck(week[1]);
        weekItem.setTuesdayCheck(week[2]);
        weekItem.setWednesdayCheck(week[3]);
        weekItem.setThursdayCheck(week[4]);
        weekItem.setFridayCheck(week[5]);
        weekItem.setSaturdayCheck(week[6]);

        tvAddAlarmClockRepetitionPeriod.setText(calcRepeatStr());
    }

    @Override
    public void updateAlarmClockBean(AlarmClockBean alarmClockBean) {
        this.alarmClockBean = alarmClockBean;
        week = alarmClockBean.getWeek();
    }

    ItemWeek.OnChooseListener checkChangeListener = new ItemWeek.OnChooseListener() {
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
            tvAddAlarmClockRepetitionPeriod.setText(calcRepeatStr());
        }
    };


    /**
     * 将选择的时间格式化并保存数据库
     *
     */
    private void saveDataToDataBase() {
        int hour = tpAddAlarmClockTime.getHour();
        int minute = tpAddAlarmClockTime.getMinute();
        String yMD = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
        int current = hour *60 + minute;
        int sys = DateUtil.getHour(new Date()) * 60 + DateUtil.getMinute(new Date());
       if (current <= sys){
           yMD = DateUtil.getWhichDate(DateUtil.YYYY_MM_DD, 1);
       }
        String date = ResUtil.format("%s %02d:%02d", yMD, hour, minute);
        alarmClockBean.setDate(date);
    }

    private String calcRepeatStr(){
        int num = 0;
        StringBuilder str = new StringBuilder();
        String[] weeks = getResources().getStringArray(R.array.week_day);
        for (int i = 0; i < week.length; i++) {
            if (week[i]) {
                str.append(weeks[i]).append(",");
                num++;
            }
        }
        int length = str.length();

        if (num == week.length) {
            return getString(R.string.content_every_day);
        } else if (num == 0) {
            return getString(R.string.content_single);
        }else {
            str = str.delete(length - 1, length) ;
            return str.toString();
        }
    }

}
