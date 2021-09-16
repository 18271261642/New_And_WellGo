package com.truescend.gofit.pagers.device.schedule.history;

import android.graphics.Color;
import android.os.Bundle;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.sn.app.db.data.schedule.ScheduleBean;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.device.adapter.ScheduleItemAdapter;
import com.truescend.gofit.utils.UnitConversion;
import com.truescend.gofit.views.TitleLayout;

import java.util.List;

import butterknife.BindView;

/**
 * 功能：历史日程页面
 * 已经提醒过的日程显示的页面，只能查看或删除，无法编辑重用
 * Author:Created by 泽鑫 on 2018/2/6 14:37.
 */

public class HistoryScheduleActivity extends BaseActivity<HistorySchedulePresenterImpl, IHistoryScheduleContract.IView> implements IHistoryScheduleContract.IView {
    @BindView(R.id.lvHistoryScheduleList)
    SwipeMenuListView lvHistoryScheduleList;

    private ScheduleItemAdapter adapter;

    @Override
    protected HistorySchedulePresenterImpl initPresenter() {
        return new HistorySchedulePresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_history_schedule;
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        super.onCreateTitle(titleLayout);
        titleLayout.setTitle(getString(R.string.title_history_schedule_reminder));
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        adapter = new ScheduleItemAdapter(HistoryScheduleActivity.this);
        lvHistoryScheduleList.setAdapter(adapter);
        lvHistoryScheduleList.setMenuCreator(creator);
        lvHistoryScheduleList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                int id = adapter.getItem(position).getId();
                getPresenter().requestRemoveHistoryScheduleItem(id);
                adapter.removeItem(position);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().requestHistoryScheduleItemList();
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            deleteItem.setBackground(R.color.black);
            deleteItem.setWidth(UnitConversion.dipToPx(HistoryScheduleActivity.this, 90));
            deleteItem.setTitle(getString(R.string.delete));
            deleteItem.setTitleSize(15);
            deleteItem.setTitleColor(Color.WHITE);
            menu.addMenuItem(deleteItem);
        }
    };

    @Override
    public void updateHistoryScheduleItemList(List<ScheduleBean> list) {
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }
}
