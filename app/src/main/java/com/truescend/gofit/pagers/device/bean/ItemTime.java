package com.truescend.gofit.pagers.device.bean;

import androidx.appcompat.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.RadioGroup;

import com.truescend.gofit.R;

/**
 * 功能：时间间隔布局
 * 适用于选择多长时间提醒的页面
 * Author:Created by 泽鑫 on 2018/3/17 10:42.
 */

public class ItemTime implements RadioGroup.OnCheckedChangeListener{
    private RadioGroup rgTimeGroup;
    private AppCompatRadioButton rbTimeHalfHour;
    private AppCompatRadioButton rbTimeOneHour;
    private AppCompatRadioButton rbTimeTwoHour;
    private AppCompatRadioButton rbTimeThreeHour;

    private OnCheckedChangeListener listener;

    public ItemTime(View view) {
        rgTimeGroup = (RadioGroup) view.findViewById(R.id.rgTimeGroup);
        rbTimeHalfHour = (AppCompatRadioButton) view.findViewById(R.id.rbTimeHalfHour);
        rbTimeOneHour = (AppCompatRadioButton) view.findViewById(R.id.rbTimeOneHour);
        rbTimeTwoHour = (AppCompatRadioButton) view.findViewById(R.id.rbTimeTwoHour);
        rbTimeThreeHour = (AppCompatRadioButton) view.findViewById(R.id.rbTimeThreeHour);
    }

    public void setHalfHourChecked(boolean checked){
        rbTimeHalfHour.setChecked(checked);
    }
    public void setOneHourChecked(boolean checked){
        rbTimeOneHour.setChecked(checked);
    }
    public void setTwoHourChecked(boolean checked){
        rbTimeTwoHour.setChecked(checked);
    }
    public void setThreeHourChecked(boolean checked){
        rbTimeThreeHour.setChecked(checked);
    }

    public void setHalfHourEnabled(boolean enabled){
        rbTimeHalfHour.setEnabled(enabled);
    }
    public void setOneHourEnabled(boolean enabled){
        rbTimeOneHour.setEnabled(enabled);
    }
    public void setTwoHourEnabled(boolean enabled){
        rbTimeTwoHour.setEnabled(enabled);
    }
    public void setThreeHourEnabled(boolean enabled){
        rbTimeThreeHour.setEnabled(enabled);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener){
        this.listener = listener;
        rgTimeGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (listener == null)return;
        switch (i){
            case R.id.rbTimeHalfHour:
                listener.onCheckedChanged(this, 30);
                break;
            case R.id.rbTimeOneHour:
                listener.onCheckedChanged(this, 60);
                break;
            case R.id.rbTimeTwoHour:
                listener.onCheckedChanged(this, 120);
                break;
            case R.id.rbTimeThreeHour:
                listener.onCheckedChanged(this, 180);
                break;
        }
    }


    public interface  OnCheckedChangeListener{
        void onCheckedChanged(ItemTime item, int position);
    }
}
