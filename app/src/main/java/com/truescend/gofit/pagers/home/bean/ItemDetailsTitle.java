package com.truescend.gofit.pagers.home.bean;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.common.bean.ItemBase;

import butterknife.BindView;

/**
 * 功能：详情界面标题布局
 * 适用于查询某些历史记录的布局页面
 * layout_details_title.xml，一个ViewHolder
 * Author:Created by 泽鑫 on 2017/12/5 18:27.
 */

public class ItemDetailsTitle extends ItemBase{

    @BindView(R.id.ivDetailsTitleLeftIcon)
    ImageView ivDetailsTitleLeftIcon;
    @BindView(R.id.rbDetailsTitleFirst)
    RadioButton rbDetailsTitleFirst;
    @BindView(R.id.rbDetailsTitleSecond)
    RadioButton rbDetailsTitleSecond;
    @BindView(R.id.rbDetailsTitleThird)
    RadioButton rbDetailsTitleThird;
    @BindView(R.id.ivDetailsTitleRightIcon)
    ImageView ivDetailsTitleRightIcon;

    public ItemDetailsTitle(View view) {
        super(view);
    }

    public void setLeftIcon(int resId){
        ivDetailsTitleLeftIcon.setImageResource(resId);
    }

    public void setLeftIcon(Drawable drawable){
        ivDetailsTitleLeftIcon.setImageDrawable(drawable);
    }

    public void setLeftIconBackground(int resId){
        ivDetailsTitleLeftIcon.setBackgroundResource(resId);
    }

    public void setLeftIconBackground(Drawable drawable){
        ivDetailsTitleLeftIcon.setBackground(drawable);
    }

    public void setLeftIconOnClickListener(View.OnClickListener onClickListener){
        ivDetailsTitleLeftIcon.setOnClickListener(onClickListener);
    }

    public void setRightIcon(int resId){
        ivDetailsTitleRightIcon.setImageResource(resId);
    }

    public void setRightIcon(Drawable drawable){
        ivDetailsTitleRightIcon.setImageDrawable(drawable);
    }

    public void setRightIconBackground(int resId){
        ivDetailsTitleRightIcon.setBackgroundResource(resId);
    }

    public void setRightIconBackground(Drawable drawable){
        ivDetailsTitleRightIcon.setBackground(drawable);
    }

    public void setRightIconOnClickListener(View.OnClickListener onClickListener){
        ivDetailsTitleRightIcon.setOnClickListener(onClickListener);
    }

    public void setRightIconVisibility(int visibility){
        ivDetailsTitleRightIcon.setVisibility(visibility);
    }

    public void setFirstText(int resId){
        rbDetailsTitleFirst.setText(resId);
    }

    public void setFirstText(String text){
        rbDetailsTitleFirst.setText(text);
    }

    public void setFirstOnClickListener(View.OnClickListener onClickListener){
        rbDetailsTitleFirst.setOnClickListener(onClickListener);
    }

    public void setSecondText(int resId){
        rbDetailsTitleSecond.setText(resId);
    }

    public void setSecondText(String text){
        rbDetailsTitleSecond.setText(text);
    }

    public void setSecondOnClickListener(View.OnClickListener onClickListener){
        rbDetailsTitleSecond.setOnClickListener(onClickListener);
    }

    public void setThirdText(int resId){
        rbDetailsTitleThird.setText(resId);
    }

    public void setThirdText(String text){
        rbDetailsTitleThird.setText(text);
    }

    public void setThirdOnClickListener(View.OnClickListener onClickListener){
        rbDetailsTitleThird.setOnClickListener(onClickListener);
    }
}
