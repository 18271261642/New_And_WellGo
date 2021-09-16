package com.truescend.gofit.utils;


import com.truescend.gofit.pagers.base.adapter.DividerItemDecoration;
import com.truescend.gofit.pagers.base.adapter.GridLayoutManagerFix;
import com.truescend.gofit.pagers.base.adapter.LinearLayoutManagerFix;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者:东芝(2017/11/17).
 * 功能:RecycleView adapter效果设置
 */

public class RecycleViewUtil {
    public static void setAdapter(RecyclerView rv, RecyclerView.Adapter adapter){
        rv.setAdapter(adapter);
        LinearLayoutManagerFix managerFix = new LinearLayoutManagerFix(rv.getContext());
        managerFix.setSmoothScrollbarEnabled(true);
        managerFix.setAutoMeasureEnabled(true);
        rv.setLayoutManager(managerFix);

        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL_LIST));
        //下拉回弹
//       OverScrollDecoratorHelper.setUpOverScroll(, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

    }

    public static void setGridLayoutAdapter(RecyclerView rv, RecyclerView.LayoutManager layoutManager, RecyclerView.Adapter adapter){
        rv.setAdapter(adapter);
        rv.setLayoutManager(new GridLayoutManagerFix(rv.getContext(), 3));
        rv.setItemAnimator(new DefaultItemAnimator());
    }
}
