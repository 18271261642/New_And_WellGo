package com.truescend.gofit.pagers.device.schedule;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.sn.app.db.data.schedule.ScheduleBean;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.device.adapter.ScheduleItemAdapter;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.UnitConversion;
import com.truescend.gofit.views.TitleLayout;

import java.util.List;

import butterknife.BindView;

/**
 * 功能：日程管理页面
 * Author:Created by 泽鑫 on 2018/2/1 18:06.
 */

public class ScheduleActivity extends BaseActivity<SchedulePresenterImpl, IScheduleContract.IView> implements IScheduleContract.IView {
    public static final String SCHEDULE_TYPE = "scheduleId";
    public static final int SCHEDULE_ADD = -1;

    @BindView(R.id.llHistoryScheduleItem)
    LinearLayout llHistoryScheduleItem;
    @BindView(R.id.lvScheduleList)
    SwipeMenuListView lvScheduleList;


    private ScheduleItemAdapter adapter;

    @Override
    protected SchedulePresenterImpl initPresenter() {
        return new SchedulePresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_schedule;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
       initItem();
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        titleLayout.setTitle(getString(R.string.title_schedule_manager));
        titleLayout.setLeftIconFinishActivity(this);
        titleLayout.addRightItem(TitleLayout.ItemBuilder.Builder().setIcon(R.mipmap.icon_add).setIconSizeRatio(45).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getCount() == 5) {
                    return;
                }
                PageJumpUtil.startAddScheduleActivity(ScheduleActivity.this, SCHEDULE_ADD);
            }
        }));
    }
    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().requestScheduleItemList();
    }

    @Override
    public void updateScheduleItemList(List<ScheduleBean> list) {
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    private void initItem(){
        adapter = new ScheduleItemAdapter(ScheduleActivity.this);
        lvScheduleList.setAdapter(adapter);
        lvScheduleList.setMenuCreator(creator);
        lvScheduleList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                int id = adapter.getItem(position).getId();
                getPresenter().requestRemoveScheduleItem(id);
                adapter.removeItem(position);
                return false;
            }
        });
        lvScheduleList.setOnItemClickListener(new SwipeMenuListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScheduleBean scheduleBean = adapter.getItem(position);
                PageJumpUtil.startAddScheduleActivity(ScheduleActivity.this, scheduleBean.getId());
            }
        });

        llHistoryScheduleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PageJumpUtil.startHistoryScheduleActivity(ScheduleActivity.this);
            }
        });
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            deleteItem.setBackground(R.color.black);
            deleteItem.setWidth(UnitConversion.dipToPx(ScheduleActivity.this, 90));
            deleteItem.setTitle(getString(R.string.delete));
            deleteItem.setTitleSize(15);
            deleteItem.setTitleColor(Color.WHITE);
            menu.addMenuItem(deleteItem);
        }
    };



}
