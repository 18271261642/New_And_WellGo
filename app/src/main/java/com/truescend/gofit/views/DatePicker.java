package com.truescend.gofit.views;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sn.utils.DateUtil;
import com.truescend.gofit.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * 作者:东芝(2018/1/5).
 * 功能:日期选择器
 */

public class DatePicker extends LinearLayout {

    private NumberPicker monthView;
    private NumberPicker dayView;
    private NumberPicker yearView;
    private TextView unitYear;
    private int selectedDayIndex;
    private int selectedMonthIndex;
    private int selectedYearIndex;

    public DatePicker(Context context) {
        super(context);
        init();
    }

    public DatePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DatePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_datepicker, this);
        initView();
        Calendar instance = Calendar.getInstance(Locale.ENGLISH);
        //默认选择当前时间
        selectedYearIndex = instance.get(Calendar.YEAR);
        selectedDayIndex = instance.get(Calendar.DAY_OF_MONTH);
        selectedMonthIndex = instance.get(Calendar.MONTH) + 1;

        setDate(selectedYearIndex, selectedMonthIndex, selectedDayIndex);
        updateDay(selectedYearIndex, selectedMonthIndex);
        dayView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int year = yearView.getValue();
                int day = dayView.getValue();
                int month = monthView.getValue();
                if (listener == null) return;
                listener.onValueChange(DatePicker.this, year, month, day);
            }
        });

        monthView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //月改变时 更新这个月的天数
                int year = yearView.getValue();
                int day = dayView.getValue();
                int month = monthView.getValue();
                updateDay(year, month);
                if (listener == null) return;
                listener.onValueChange(DatePicker.this, year, month, day);
            }

        });


        yearView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //月改变时 更新这个月的天数
                int year = yearView.getValue();
                int day = dayView.getValue();
                int month = monthView.getValue();
                updateDay(year, month);
                if (listener == null) return;
                listener.onValueChange(DatePicker.this, year, month, day);
            }

        });
    }

    public void setYearViewVisibility(int visibility) {
        yearView.setVisibility(visibility);
        unitYear.setVisibility(visibility);

    }

    public void setDate(String date) {
        Date date1 = new Date();
        try {
            date1 = DateUtil.convertStringToDate(DateUtil.YYYY_MM_DD, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int year = DateUtil.getYear(date1);
        int month = DateUtil.getMonth(date1);
        int day = DateUtil.getDay(date1);

        setDate(year, month, day);
    }

    public void setDate(int year, int month, int day) {
        this.selectedYearIndex = year;
        this.selectedMonthIndex = month;
        this.selectedDayIndex = day;

        yearView.setValue(year);
        monthView.setValue(month);
        dayView.setValue(day);
    }

    public String getDate() {
        StringBuilder builder = new StringBuilder();
        builder.append(yearView.getValue());
        builder.append("-");
        builder.append(monthView.getValue());
        builder.append("-");
        builder.append(dayView.getValue());
        return builder.toString();
    }

    public void setMaxYear(int year) {
        yearView.setMaxValue(year);
        yearView.setWrapSelectorWheel(false);
    }

    public int getMaxYear() {
        return yearView.getMaxValue();
    }

    public void setMinYear(int year) {
        yearView.setMinValue(year);
    }

    public int getMinYear() {
        return yearView.getMinValue();
    }


    public void setYear(int year) {
        this.selectedYearIndex = year;
        yearView.setValue(year);
    }

    public int getYear() {
        return yearView.getValue();
    }

    public void setDay(int day) {
        this.selectedDayIndex = day;
        dayView.setValue(day);
    }

    public void setMonth(int month) {
        this.selectedMonthIndex = month;
        monthView.setValue(month);
    }

    public void setMaxMonth(int month) {
        monthView.setMaxValue(month);
    }

    public void setMinMonth(int month) {
        monthView.setMinValue(month);
    }


    public int getMaxMonth() {
        return monthView.getMaxValue();
    }

    public int getMinMonth() {
        return monthView.getMinValue();
    }


    public int getMonth() {
        return monthView.getValue();
    }

    public void setMaxDay(int day) {
        dayView.setMaxValue(day);
    }

    public void setMinDay(int day) {
        dayView.setMinValue(day);
    }

    public int getDay() {
        return dayView.getValue();
    }

    private void updateDay(int year, int month) {
        System.out.println("year==" + year);
        Calendar c = Calendar.getInstance(Locale.ENGLISH);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, 0);//设置为1号,当前日期既为本月第一天
        int maxDay = c.getActualMaximum(Calendar.DATE);
        dayView.setMinValue(1);
        dayView.setMaxValue(maxDay);
    }

    private void initView() {
        dayView = findViewById(R.id.dayView);
        monthView = findViewById(R.id.monthView);
        yearView = findViewById(R.id.yearView);
        unitYear = findViewById(R.id.unit_year);
    }


    private OnDatePickerListener listener;


    public void setOnDatePickerListener(OnDatePickerListener listener) {
        this.listener = listener;
    }

    public interface OnDatePickerListener {
        void onValueChange(DatePicker picker, int year, int month, int day);
    }
}
