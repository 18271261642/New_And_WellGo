package com.truescend.gofit.utils;


import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

/**
 * 作者:东芝(2018/2/2).
 * 描述:官方design库的 TabLayout 工具类
 */
public class TabLayoutUtils {

    /**
     * 设置Tab可见
     * @param tabLayout
     * @param enable
     */
    public static void setEnable(TabLayout tabLayout, boolean enable) {
        ViewGroup viewGroup = getTabViewGroup(tabLayout);
        if (viewGroup != null)
            for (int childIndex = 0; childIndex < viewGroup.getChildCount(); childIndex++) {
                View tabView = viewGroup.getChildAt(childIndex);
                if (tabView != null)
                    tabView.setEnabled(enable);
            }
    }

    public static View getTabView(TabLayout tabLayout, int position) {
        View tabView = null;
        ViewGroup viewGroup = getTabViewGroup(tabLayout);
        if (viewGroup != null && viewGroup.getChildCount() > position)
            tabView = viewGroup.getChildAt(position);

        return tabView;
    }


    public static void setOnTabTouchListener(TabLayout tabLayout,View.OnTouchListener mOnTouchListener){
        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            View childAt = tabStrip.getChildAt(i);
            childAt.setTag(i);
            childAt.setOnTouchListener(mOnTouchListener);
        }
    }


    private static ViewGroup getTabViewGroup(TabLayout tabLayout) {
        ViewGroup viewGroup = null;

        if (tabLayout != null && tabLayout.getChildCount() > 0) {
            View view = tabLayout.getChildAt(0);
            if (view != null && view instanceof ViewGroup)
                viewGroup = (ViewGroup) view;
        }
        return viewGroup;
    }
}
