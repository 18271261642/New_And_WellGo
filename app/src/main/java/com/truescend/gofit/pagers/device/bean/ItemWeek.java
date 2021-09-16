package com.truescend.gofit.pagers.device.bean;

import androidx.appcompat.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.CompoundButton;

import com.truescend.gofit.R;

/**
 * 功能：星期多选按钮布局
 * Author:Created by 泽鑫 on 2018/3/17 10:42.
 */

public class ItemWeek implements CompoundButton.OnCheckedChangeListener {
    private AppCompatCheckBox cbWeekSunday;
    private AppCompatCheckBox cbWeekMonday;
    private AppCompatCheckBox cbWeekTuesday;
    private AppCompatCheckBox cbWeekWednesday;
    private AppCompatCheckBox cbWeekThursday;
    private AppCompatCheckBox cbWeekFriday;
    private AppCompatCheckBox cbWeekSaturday;

    private OnChooseListener listener;

    public ItemWeek(View view) {
        cbWeekSunday = (AppCompatCheckBox) view.findViewById(R.id.cbWeekSunday);
        cbWeekMonday = (AppCompatCheckBox) view.findViewById(R.id.cbWeekMonday);
        cbWeekTuesday = (AppCompatCheckBox) view.findViewById(R.id.cbWeekTuesday);
        cbWeekWednesday = (AppCompatCheckBox) view.findViewById(R.id.cbWeekWednesday);
        cbWeekThursday = (AppCompatCheckBox) view.findViewById(R.id.cbWeekThursday);
        cbWeekFriday = (AppCompatCheckBox) view.findViewById(R.id.cbWeekFriday);
        cbWeekSaturday = (AppCompatCheckBox) view.findViewById(R.id.cbWeekSaturday);

        cbWeekSunday.setOnCheckedChangeListener(this);
        cbWeekMonday.setOnCheckedChangeListener(this);
        cbWeekTuesday.setOnCheckedChangeListener(this);
        cbWeekWednesday.setOnCheckedChangeListener(this);
        cbWeekThursday.setOnCheckedChangeListener(this);
        cbWeekFriday.setOnCheckedChangeListener(this);
        cbWeekSaturday.setOnCheckedChangeListener(this);
    }

    public void setSundayCheck(boolean checked){
        cbWeekSunday.setChecked(checked);
    }
    public void setMondayCheck(boolean checked){
        cbWeekMonday.setChecked(checked);
    }
    public void setTuesdayCheck(boolean checked){
        cbWeekTuesday.setChecked(checked);
    }
    public void setWednesdayCheck(boolean checked){
        cbWeekWednesday.setChecked(checked);
    }
    public void setThursdayCheck(boolean checked){
        cbWeekThursday.setChecked(checked);
    }
    public void setFridayCheck(boolean checked){
        cbWeekFriday.setChecked(checked);
    }
    public void setSaturdayCheck(boolean checked){
        cbWeekSaturday.setChecked(checked);
    }

    public void setOnChooseListener(OnChooseListener listener){
        this.listener = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (listener == null)return;
        switch (compoundButton.getId()){
            case R.id.cbWeekSunday:
                listener.onChoose(this,0, b);
                break;
            case R.id.cbWeekMonday:
                listener.onChoose(this,1, b);
                break;
            case R.id.cbWeekTuesday:
                listener.onChoose(this,2, b);
                break;
            case R.id.cbWeekWednesday:
                listener.onChoose(this,3, b);
                break;
            case R.id.cbWeekThursday:
                listener.onChoose(this,4, b);
                break;
            case R.id.cbWeekFriday:
                listener.onChoose(this,5, b);
                break;
            case R.id.cbWeekSaturday:
                listener.onChoose(this,6, b);
                break;
        }
    }

    public interface OnChooseListener{
        void onChoose(ItemWeek itemWeek, int day, boolean isChecked);
    }
}
