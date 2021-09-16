package com.truescend.gofit.views;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.truescend.gofit.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2018/4/11).
 * 功能:复合勾选 组,支持单/多 选择  改布局可包含 CheckBox 或 RadioButton
 */
public class CompoundGroup extends LinearLayout implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, View.OnTouchListener {

    private List<CompoundButton> checkBoxList = new ArrayList<>();
    private int position = -1;
    private boolean isMultiSelected = false;


    public CompoundGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CompoundGroup);
        position = attributes.getInt(R.styleable.CompoundGroup_checkedPosition, -1);
        isMultiSelected =  attributes.getInt(R.styleable.CompoundGroup_checkedType, 0)==1;
        attributes.recycle();
        init();
    }


    private void init() {

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < checkBoxList.size(); i++) {
            CompoundButton checkBox = checkBoxList.get(i);
            if (checkBox == null) continue;
            boolean checked = false;

            if (position == -1) {
                if(!isMultiSelected) {
                    if (i == 0) {
                        checked = true;
                    }
                }
            } else if (i == position) {
                checked = true;
            }
            checkBox.setChecked(checked);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        CompoundButton checkBox = null;
        if (child instanceof CompoundButton) {
            checkBox = (CompoundButton) child;
        } else if (child instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) child;
            int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = group.getChildAt(i);
                if (childAt instanceof CompoundButton) {
                    checkBox = (CompoundButton) childAt;
                    break;
                }
            }
            group.setOnClickListener(this);
        }
        if (checkBox != null) {
            checkBox.setOnCheckedChangeListener(this);
            checkBox.setOnTouchListener(this);
            checkBox.setTag(checkBoxList.size());
            checkBoxList.add(checkBox);
        }
        super.addView(child, index, params);
    }

    public void setChecked(int position, boolean checked) {
        this.position = position;
        if (checkBoxList.isEmpty()) {
            return;
        }
        CompoundButton checkBox = checkBoxList.get(position);
        if (checked) {
            if (!checkBox.isChecked()) {
                checkBox.setChecked(true);
            }
        } else {
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
            }
        }
    }




    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (checkBoxList == null || checkBoxList.isEmpty()) return;
        Object tag = buttonView.getTag();
        if (tag == null || !(tag instanceof Integer)) return;
        this.position = (int) tag;

        if (!isMultiSelected) {
            for (int i = 0; i < checkBoxList.size(); i++) {

                CompoundButton checkBox = checkBoxList.get(i);
                if (checkBox == null) continue;
                setBroadcasting(checkBox,true);
                checkBox.setChecked(false);
                setBroadcasting(checkBox,false);
            }
            setBroadcasting(buttonView,true);
            buttonView.setChecked(isChecked);
            setBroadcasting(buttonView,false);
        }
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, position);
        }
    }

    private void setBroadcasting(CompoundButton  button,boolean mBroadcasting){
        try {
            Field field = CompoundButton.class.getDeclaredField("mBroadcasting");
            field.setAccessible(true);
            field.set(button,mBroadcasting);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        //事件仅有ViewGroup
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = group.getChildAt(i);
                if (childAt instanceof CompoundButton) {
                    CompoundButton checkBox = (CompoundButton) childAt;
                    if (isMultiSelected) {
                        checkBox.setChecked(!checkBox.isChecked());
                    } else if (!checkBox.isChecked()) {
                        checkBox.setChecked(true);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isMultiSelected) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //事件仅有CompoundButton
                if (v instanceof CompoundButton) {
                    CompoundButton checkBox = (CompoundButton) v;
                    //已经勾选则不取消勾选 这是单选 如果以后要实现多选 可以用if判断 整个touch去掉
                    if (checkBox.isChecked()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private OnCheckedChangeListener onCheckedChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CompoundGroup group, int checkedPosition);
    }

}