package com.truescend.gofit.pagers.device.schedule.add;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sn.app.db.data.schedule.ScheduleBean;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.device.schedule.ScheduleActivity;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.views.DatePicker;
import com.truescend.gofit.views.NumberPicker;
import com.truescend.gofit.views.TimePicker;
import com.truescend.gofit.views.TitleLayout;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

/**
 * 功能：日程管理页面
 * 添加或者编辑日程（日程未提醒过）
 * Author:Created by 泽鑫 on 2018/2/1 18:06.
 */

public class AddScheduleActivity extends BaseActivity<AddSchedulePresenterImpl, IAddScheduleContract.IView> implements IAddScheduleContract.IView {

    @BindView(R.id.etScheduleContent)
    EditText etScheduleContent;
    @BindView(R.id.tvAddScheduleDate)
    TextView tvAddScheduleDate;
    @BindView(R.id.tvAddScheduleTime)
    TextView tvAddScheduleTime;
    @BindView(R.id.dpAddScheduleDate)
    DatePicker dpAddScheduleDate;
    @BindView(R.id.npAddScheduleWeek)
    NumberPicker npAddScheduleWeek;
    @BindView(R.id.tpAddScheduleTime)
    TimePicker tpAddScheduleTime;

    private ScheduleBean scheduleBean = new ScheduleBean();

    @Override
    protected AddSchedulePresenterImpl initPresenter() {
        return new AddSchedulePresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_add_schedule;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initItem();
        initDate();
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        titleLayout.setTitle(getString(R.string.title_edit_schedule));
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
                        scheduleBean.setUser_id(AppUserUtil.getUser().getUser_id());
                        String content = etScheduleContent.getText().toString().trim();
                        if (IF.isEmpty(content)) {
                            etScheduleContent.setFocusable(true);
                            etScheduleContent.setFocusableInTouchMode(true);
                            etScheduleContent.requestFocus();
                            etScheduleContent.setError(getString(R.string.content_can_not_be_null));
                            return;
                        }
                        scheduleBean.setContent(content);
                        scheduleBean.setRead(false);
                        getPresenter().requestUpdateSchedule(scheduleBean);
                        finish();
                    }
                })
        );
    }

    private void initItem() {
        dpAddScheduleDate.setYearViewVisibility(View.GONE);
        dpAddScheduleDate.setOnDatePickerListener(onDateChangeListener);
        tpAddScheduleTime.setOnTimePickerListener(onTimeChangeListener);
    }

    private void initDate() {
        int week = DateUtil.getWeekIndex(DateUtil.getCurrentCalendar());
        int hour = DateUtil.getHour(new Date());
        int year = DateUtil.getYear(new Date());
        int minute = DateUtil.getMinute(new Date());
        String date = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
        String time = DateUtil.getCurrentDate(DateUtil.HH_MM);
        Intent intent = getIntent();
        int id = intent.getIntExtra(ScheduleActivity.SCHEDULE_TYPE, ScheduleActivity.SCHEDULE_ADD);
        if (id != ScheduleActivity.SCHEDULE_ADD) {//修改日程
            getPresenter().requestEditSchedule(id);
            String content = scheduleBean.getContent();
            etScheduleContent.setText(content);
            etScheduleContent.setSelection(content.length());
            String date1 = scheduleBean.getDate();
            try {
                Calendar calendar = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD_HH_MM, date1);
                year = calendar.get(Calendar.YEAR);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
                week = DateUtil.getWeekIndex(calendar);
                date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);
                time = DateUtil.getDate(DateUtil.HH_MM, calendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        tvAddScheduleDate.setText(date);
        tvAddScheduleTime.setText(time);

        dpAddScheduleDate.setDate(date);
        dpAddScheduleDate.setMaxYear(2100);
        dpAddScheduleDate.setMinYear(year);
        tpAddScheduleTime.setTime(hour, minute);

        resetPicker(0);

        npAddScheduleWeek.setDisplayedValues(getResources().getStringArray(R.array.week_day));
        npAddScheduleWeek.setMinValue(0);
        npAddScheduleWeek.setMaxValue(6);
        npAddScheduleWeek.setValue(0);
        npAddScheduleWeek.setEnabled(false);
        npAddScheduleWeek.setValue(week);
    }

    @Override
    public void updateScheduleBean(ScheduleBean bean) {
        this.scheduleBean = bean;
    }

    DatePicker.OnDatePickerListener onDateChangeListener = new DatePicker.OnDatePickerListener() {
        int lastMonth = -1;

        @Override
        public void onValueChange(DatePicker picker, int year, int month, int day) {
            if (lastMonth == 12 && month == 1) {
                year = dpAddScheduleDate.getYear() + 1;

                if(year>dpAddScheduleDate.getMaxYear()){
                    year = dpAddScheduleDate.getMaxYear();
                }
                dpAddScheduleDate.setYear(year);
            }  else if (lastMonth == 1 && month == 12) {
                year = dpAddScheduleDate.getYear() - 1;
                if(year<dpAddScheduleDate.getMinYear()){
                    year = dpAddScheduleDate.getMinYear();
                }
                dpAddScheduleDate.setYear(year);
            }
            lastMonth = month;

            String date = ResUtil.format("%04d-%02d-%02d", year, month, day);
            try {
                long currentDate = DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD,  DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD));
                long selectDate = DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD,date);
                if(selectDate<currentDate){
                    dpAddScheduleDate.setDate(DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD));
                    year=dpAddScheduleDate.getYear();
                    month=dpAddScheduleDate.getMonth();
                    day=dpAddScheduleDate.getDay();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            resetPicker(0);
            int index = DateUtil.getWeekIndex(year, month, day);
            tvAddScheduleDate.setText(date);
            npAddScheduleWeek.setValue(index);


        }
    };

    TimePicker.OnTimePickerListener onTimeChangeListener = new TimePicker.OnTimePickerListener() {
        @Override
        public void onValueChange(TimePicker picker, int hour, int minute) {
            resetPicker(1);
            String time = ResUtil.format("%02d:%02d", hour, minute);
            tvAddScheduleTime.setText(time);
        }
    };

    /**
     * 重置日期和时间
     *
     * @param type 类型
     */
    private void resetPicker(int type) {
        int currentMonth = DateUtil.getMonth(new Date());
        int currentDay = DateUtil.getDay(new Date());
        int currentHour = DateUtil.getHour(new Date());
        int currentMinute = DateUtil.getMinute(new Date());

        int selectMonth = dpAddScheduleDate.getMonth();
        int selectDay = dpAddScheduleDate.getDay();
        int selectHour = tpAddScheduleTime.getHour();
        switch (type) {
            case 0://重置日期和时间
//                dpAddScheduleDate.setMinMonth(currentMonth);
                if (selectMonth == currentMonth) {
//                    dpAddScheduleDate.setMinDay(currentDay);
                    if (selectDay == currentDay) {
                        tpAddScheduleTime.setMinHour(currentHour);
                        if (selectHour == currentHour) {
                            tpAddScheduleTime.setMinMinute(currentMinute);
                        } else {
                            tpAddScheduleTime.setMinMinute(0);
                        }
                    } else {
                        tpAddScheduleTime.setMinHour(0);
                        tpAddScheduleTime.setMinMinute(0);
                    }
                } else {

                    dpAddScheduleDate.setMinDay(1);
                    tpAddScheduleTime.setMinHour(0);
                    tpAddScheduleTime.setMinMinute(0);
                }
                break;
            case 1://重置时间
                if (selectMonth == currentMonth && selectDay == currentDay) {
                    tpAddScheduleTime.setMinHour(currentHour);
                    if (selectHour == currentHour) {
                        tpAddScheduleTime.setMinMinute(currentMinute);
                    } else {
                        tpAddScheduleTime.setMinMinute(0);
                    }
                } else {
                    tpAddScheduleTime.setMinHour(0);
                    tpAddScheduleTime.setMinMinute(0);
                }
                break;
        }
    }


    /**
     * 将选择的时间格式化并保存数据库
     */
    private void saveDataToDataBase() {
        int month = dpAddScheduleDate.getMonth();
        int day = dpAddScheduleDate.getDay();
        int hour = tpAddScheduleTime.getHour();
        int minute = tpAddScheduleTime.getMinute();
        String year = DateUtil.getCurrentDate(DateUtil.YYYY);
        String date = ResUtil.format("%s-%02d-%02d %02d:%02d", year, month, day, hour, minute);
        scheduleBean.setDate(date);
    }
}
