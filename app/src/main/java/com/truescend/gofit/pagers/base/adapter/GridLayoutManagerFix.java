package com.truescend.gofit.pagers.base.adapter;

import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者:东芝(2017/10/16).
 * 功能:修复RecycleView在OPPO VIVO等国产手机上出现的 异常
 * java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder{239c3bd3 position=4 id=-1, oldPos=-1, pLpos:-1 no parent}
 */
public class GridLayoutManagerFix extends GridLayoutManager {

    public GridLayoutManagerFix(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GridLayoutManagerFix(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GridLayoutManagerFix(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Throwable e) {
            Log.e("probe", "meet a IOOBE in RecyclerView");
        }
    }
}