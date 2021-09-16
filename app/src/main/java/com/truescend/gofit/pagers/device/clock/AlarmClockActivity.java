package com.truescend.gofit.pagers.device.clock;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.sn.app.db.data.clock.AlarmClockBean;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.device.adapter.AlarmItemAdapter;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.UnitConversion;
import com.truescend.gofit.views.TitleLayout;

import java.util.List;

import butterknife.BindView;

/**
 * 功能:闹钟页面
 * Author:Created by 泽鑫 on 2017/11/21 16:30.
 */
public class AlarmClockActivity extends BaseActivity<AlarmClockPresenterImpl, IAlarmClockContract.IView> implements IAlarmClockContract.IView, AlarmItemAdapter.CheckChangeListener {
    public final static String ALARM_CLOCK_TYPE = "alarmClockId";
    public final static int ALARM_CLOCK_ADD = -1;
    @BindView(R.id.smAlarmClock)
    SwipeMenuListView smAlarmClock;

    AlarmItemAdapter adapter;

    @Override
    protected AlarmClockPresenterImpl initPresenter() {
        return new AlarmClockPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_alarm_clock;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        adapter = new AlarmItemAdapter(AlarmClockActivity.this, this);
        smAlarmClock.setAdapter(adapter);
        smAlarmClock.setMenuCreator(creator);
        smAlarmClock.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                int id = adapter.getItem(position).getId();
                getPresenter().requestRemoveAlarmClockItem(id);
                adapter.removeItem(position);
                return false;
            }
        });
        smAlarmClock.setOnItemClickListener(new SwipeMenuListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlarmClockBean alarmClockBean = adapter.getItem(position);
                PageJumpUtil.startAddAlarmClockActivity(AlarmClockActivity.this, alarmClockBean.getId());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().requestAlarmClockItemList();
    }

    @Override
    protected void onCreateTitle(final TitleLayout titleLayout) {
        titleLayout.setTitle(getString(R.string.title_alarm));
        titleLayout.setLeftIconFinishActivity(this);

        titleLayout.addRightItem(
                TitleLayout.ItemBuilder.Builder()
                        .setIcon(R.mipmap.icon_add)
                        .setIconSizeRatio(45)
                        .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getCount() == 5){
                    return;
                }
                PageJumpUtil.startAddAlarmClockActivity(AlarmClockActivity.this, ALARM_CLOCK_ADD);
            }
        }));
    }


    SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            deleteItem.setBackground(R.color.black);
            deleteItem.setWidth(UnitConversion.dipToPx(AlarmClockActivity.this, 90));
            deleteItem.setTitle(getString(R.string.delete));
            deleteItem.setTitleSize(15);
            deleteItem.setTitleColor(Color.WHITE);
            menu.addMenuItem(deleteItem);
        }
    };

    @Override
    public void updateAlarmClockItemList(List<AlarmClockBean> list) {
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckChangedListener(View view, boolean statues) {
        getPresenter().requestChangeAlarmClockStatues((int)view.getTag(),statues);
    }
}
