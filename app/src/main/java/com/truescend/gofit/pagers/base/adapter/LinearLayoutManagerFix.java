package com.truescend.gofit.pagers.base.adapter;

import android.content.Context;

import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者:东芝(2017/10/16).
 * 功能:修复RecycleView在OPPO VIVO等国产手机上出现的 异常
 * java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder{239c3bd3 position=4 id=-1, oldPos=-1, pLpos:-1 no parent}
 */
public class LinearLayoutManagerFix extends LinearLayoutManager {
    public LinearLayoutManagerFix(Context context) {
        super(context);
    }

    public LinearLayoutManagerFix(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutManagerFix(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}