package com.truescend.gofit.pagers.device.bean;

import androidx.appcompat.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.RadioGroup;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.common.bean.ItemBase;

import butterknife.BindView;

/**
 * 功能：时间间隔布局
 * 适用于选择多长时间提醒的页面
 * Author:Created by 泽鑫 on 2018/1/6 11:08.
 */

public class ItemTimeInterval extends ItemBase{
    @BindView(R.id.rgTimeIntervalGroup)
    RadioGroup rgTimeIntervalGroup;
    @BindView(R.id.rbTimeIntervalHalfHour)
    AppCompatRadioButton rbTimeIntervalHalfHour;
    @BindView(R.id.rbTimeIntervalOneHour)
    AppCompatRadioButton rbTimeIntervalOneHour;
    @BindView(R.id.rbTimeIntervalTwoHour)
    AppCompatRadioButton rbTimeIntervalTwoHour;
    @BindView(R.id.rbTimeIntervalThreeHour)
    AppCompatRadioButton rbTimeIntervalThreeHour;

    public ItemTimeInterval(View view) {
        super(view);
    }
    
    public void setHalfHourChecked(boolean checked){
        rbTimeIntervalHalfHour.setChecked(checked);
    }
    public void setOneHourChecked(boolean checked){
        rbTimeIntervalOneHour.setChecked(checked);
    }
    public void setTwoHourChecked(boolean checked){
        rbTimeIntervalTwoHour.setChecked(checked);
    }
    public void setThreeHourChecked(boolean checked){
        rbTimeIntervalThreeHour.setChecked(checked);
    }

    public void setHalfHourEnabled(boolean enabled){
        rbTimeIntervalHalfHour.setEnabled(enabled);
    }
    public void setOneHourEnabled(boolean enabled){
        rbTimeIntervalOneHour.setEnabled(enabled);
    }
    public void setTwoHourEnabled(boolean enabled){
        rbTimeIntervalTwoHour.setEnabled(enabled);
    }
    public void setThreeHourEnabled(boolean enabled){
        rbTimeIntervalThreeHour.setEnabled(enabled);
    }

    public void setHalfHourOnClickListener(View.OnClickListener listener){
        rbTimeIntervalHalfHour.setOnClickListener(listener);
    }

    public void setOneHourOnClickListener(View.OnClickListener listener){
        rbTimeIntervalOneHour.setOnClickListener(listener);
    }

    public void setTwoHourOnClickListener(View.OnClickListener listener){
        rbTimeIntervalTwoHour.setOnClickListener(listener);
    }

    public void setThreeHourOnClickListener(View.OnClickListener listener){
        rbTimeIntervalThreeHour.setOnClickListener(listener);
    }
}
