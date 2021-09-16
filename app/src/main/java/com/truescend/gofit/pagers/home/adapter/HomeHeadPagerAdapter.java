package com.truescend.gofit.pagers.home.adapter;

import android.content.Context;
import androidx.annotation.NonNull;

import com.truescend.gofit.pagers.base.adapter.BasePagerAdapter;
import com.truescend.gofit.views.SportChartViewSwitcher;

/**
 * 作者:东芝(2017/12/11).
 * 功能:Home 头PagerAdapter
 */
public class HomeHeadPagerAdapter extends BasePagerAdapter<SportChartViewSwitcher> implements SportChartViewSwitcher.OnSwitchTypeChangeListener {

    public HomeHeadPagerAdapter(Context context) {
        lists.add(initView(context));
        lists.add(initView(context));
        lists.add(initView(context));
        lists.add(initView(context));
    }

    @NonNull
    private SportChartViewSwitcher initView(Context context) {
        SportChartViewSwitcher switcher = new SportChartViewSwitcher(context);
        switcher.setSwitchTypeChangeListener(this);
        switcher.setSwitchType(SportChartViewSwitcher.TYPE_CIRCLE_PROGRESS);

        return switcher;
    }

    @Override
    public void onSwitchTypeChange(int type) {
        if (lists != null) {
            for (SportChartViewSwitcher switcher : lists) {
                switcher.setSwitchType(type);
            }
        }
    }
}
