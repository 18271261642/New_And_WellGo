package com.truescend.gofit.pagers.device.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.sn.utils.DateUtil;
import com.truescend.gofit.R;
import com.sn.app.db.data.clock.AlarmClockBean;
import com.truescend.gofit.pagers.base.adapter.BaseListViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;

import java.text.ParseException;

/**
 * 功能：闹钟适配器
 * Author:Created by 泽鑫 on 2017/12/16 18:21.
 */

public class AlarmItemAdapter extends BaseListViewAdapter<AlarmClockBean> implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    private CheckChangeListener checkChangeListener;

    public AlarmItemAdapter(Context context, CheckChangeListener checkChangeListener) {
        super(context);
        this.mContext = context;
        this.checkChangeListener = checkChangeListener;
    }

    @Override
    public void onItemInflate(int position, AlarmClockBean item, ViewHolder.BaseViewHolder viewHolder, View rootView) {
        try {
            viewHolder.setTextView(R.id.tvClockScheduleTime, DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM, DateUtil.HH_MM, item.getDate()));
            viewHolder.setTextView(R.id.tvClockScheduleDay, repeatCycle(item));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        CheckBox cItem = viewHolder.getView(R.id.cbClockScheduleSwitch);
        cItem.setTag(item.getId());
        cItem.setOnCheckedChangeListener(null);
        cItem.setChecked(item.isSwitchStatues());
        cItem.setOnCheckedChangeListener(this);
    }

    @Override
    public int initLayout() {
        return R.layout.list_alarm_item;
    }


    /**
     * 显示重复日期
     *
     * @param clockBean 闹钟对象
     * @return 文字
     */
    private String repeatCycle(AlarmClockBean clockBean) {
        boolean[] week = clockBean.getWeek();
        StringBuilder str= new StringBuilder();
        String[] weeks = mContext.getResources().getStringArray(R.array.week_day);
        int num = 0;
        for (int i = 0; i < week.length; i++) {
            if (week[i]) {
                str.append(weeks[i]).append(",");
                num++;
            }
        }
        int length = str.length();

        if (num == week.length) {
            return mContext.getString(R.string.content_every_day);
        } else if (num == 0) {
            try {
                int which = DateUtil.whichDay(clockBean.getDate());
                switch (which) {
                    case DateUtil.OTHER_DAY:
                        return DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM, DateUtil.YYYY_MM_DD, clockBean.getDate());
                    case DateUtil.TODAY:
                        return mContext.getString(R.string.content_today);
                    case DateUtil.TOMORROW:
                        return  mContext.getString(R.string.content_tomorrow);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            str = str.delete(length - 1, length) ;
        }
        return str.toString();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (checkChangeListener != null) {
            checkChangeListener.onCheckChangedListener(buttonView, isChecked);
        }
    }

    public interface CheckChangeListener {
        void onCheckChangedListener(View view, boolean statues);
    }
}
