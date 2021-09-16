package com.truescend.gofit.pagers.device.bean;

import androidx.appcompat.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.common.bean.ItemBase;

import butterknife.BindView;

/**
 * 功能：单位切换布局
 * 适用于需要切换单位的布局
 * 适配item_unit.xml布局，一个ViewHolder
 * Author:Created by 泽鑫 on 2017/12/18 19:28.
 */

public class ItemUnit extends ItemBase implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.tvUnitTitle)
    TextView tvUnitTitle;
    @BindView(R.id.rbUnitLeft)
    AppCompatRadioButton rbUnitLeft;
    @BindView(R.id.rbUnitRight)
    AppCompatRadioButton rbUnitRight;

    @BindView(R.id.rgUnitGroup)
    RadioGroup rgUnitGroup;
    private OnCheckedChangeListener listener;
    private View view;

    public ItemUnit(View view) {
        super(view);
        this.view = view;
    }


    public void setVisibility( int visibility){
        view.setVisibility(visibility);
    }

    public void setTitle(int resId){
        tvUnitTitle.setText(resId);
    }

    public void setTitle(String title){
        tvUnitTitle.setText(title);
    }

    public void setLeftText(int resId){
        rbUnitLeft.setText(resId);
    }

    public void setLeftText(String text){
        rbUnitLeft.setText(text);
    }

    public void setRightText(int resId){
        rbUnitRight.setText(resId);
    }

    public void setRightText(String text){
        rbUnitRight.setText(text);
    }


    public void setLeftChecked(boolean checked){
        rbUnitLeft.setChecked(checked);
    }

    public void setRightChecked(boolean checked){
        rbUnitRight.setChecked(checked);
    }



    public void setOnCheckedChangeListener(OnCheckedChangeListener listener){
        this.listener = listener;
        rgUnitGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(listener==null)return;
        switch (checkedId) {
            case R.id.rbUnitLeft:
                listener.onCheckedChanged(this,0);
                break;
            case R.id.rbUnitRight:
                listener.onCheckedChanged(this,1);
                break;
        }
    }

    public interface  OnCheckedChangeListener{
        void onCheckedChanged(ItemUnit item,int position);
    }
}
