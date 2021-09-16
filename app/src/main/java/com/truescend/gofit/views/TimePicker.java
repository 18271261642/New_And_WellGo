package com.truescend.gofit.views;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sn.utils.DateUtil;
import com.truescend.gofit.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


/**
 * 作者:东芝(2018/1/5).
 * 功能:时间选择器
 */

public class TimePicker extends LinearLayout implements NumberPicker.OnValueChangeListener {

    private NumberPicker hourView;
    private NumberPicker minuteView;
    private int selectedHourIndex;
    private int selectedMinuteIndex;

    public TimePicker(Context context) {
        super(context);
        init();
    }

    public TimePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }



    private void init() {
        inflate(getContext(), R.layout.layout_timepicker, this);
        initView();
        //默认选择当前时间
        selectedHourIndex = DateUtil.getCurrentCalendar().get(Calendar.HOUR_OF_DAY);
        selectedMinuteIndex = DateUtil.getCurrentCalendar().get(Calendar.MINUTE);

        setHour(selectedHourIndex);
        setMinute(selectedMinuteIndex);
        hourView.setOnValueChangedListener(this);
        minuteView.setOnValueChangedListener(this);
    }

    public void setTime(String date){
        Date date1 = new Date();
        try {
            date1 = DateUtil.convertStringToDate(DateUtil.HH_MM, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int hour = DateUtil.getHour(date1);
        int minute = DateUtil.getMinute(date1);

        setTime(hour, minute);
    }

    public void setTime(int hour, int minute){
        this.selectedHourIndex = hour;
        this.selectedMinuteIndex = minute;

        hourView.setValue(hour);
        minuteView.setValue(minute);
    }

    public void setHour(int hour) {
        this.selectedHourIndex = hour;
        hourView.setValue(hour);
    }

    public void setMaxHour(int hour){
        hourView.setMaxValue(hour);
    }

    public void setMinHour(int hour){
        hourView.setMinValue(hour);
    }


    public void setMinute(int minute) {
        this.selectedMinuteIndex = minute;
        minuteView.setValue(minute);
    }

    public void setMaxMinute(int minute){
        minuteView.setMaxValue(minute);
    }

    public void setMinMinute(int minute){
        minuteView.setMinValue(minute);
    }

    public int getHour(){
        return hourView.getValue();
    }

    public int getMinute(){
        return minuteView.getValue();
    }

    private void initView() {
        hourView = (NumberPicker) findViewById(R.id.hourView);
        minuteView = (NumberPicker) findViewById(R.id.minuteView);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if(hourView==null||minuteView==null||listener==null)return;

        int hour = hourView.getValue();
        int minute = minuteView.getValue();
        if (listener != null) {
            listener.onValueChange(this, hour, minute);
        }
    }

    private OnTimePickerListener listener;


    public void setOnTimePickerListener(OnTimePickerListener listener) {
        this.listener = listener;
    }

    public interface OnTimePickerListener{
       void onValueChange(TimePicker picker, int hour, int minute);
    }
}
