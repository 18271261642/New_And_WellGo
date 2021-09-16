package com.truescend.gofit.pagers.device.adapter;

import android.content.Context;
import android.view.View;

import com.sn.app.db.data.schedule.ScheduleBean;
import com.sn.utils.DateUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseListViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;

import java.text.ParseException;

/**
 * 功能：日程适配器
 * Author:Created by 泽鑫 on 2018/2/2 15:29.
 */

public class ScheduleItemAdapter extends BaseListViewAdapter<ScheduleBean> {
    private Context mContext;

    public ScheduleItemAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public void onItemInflate(int position, ScheduleBean item, ViewHolder.BaseViewHolder viewHolder, View rootView) {
        String date = calcDay(item.getDate());
        String time = null;
        try {
            time = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM, DateUtil.HH_MM, item.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.setTextView(R.id.tvScheduleTitle, item.getContent());
        viewHolder.setTextView(R.id.tvScheduleTime, time);
        viewHolder.setTextView(R.id.tvScheduleDate, date);
    }

    @Override
    public int initLayout() {
        return R.layout.list_schedule;
    }

    private String calcDay(String date){
        String day = null;
        try {
            int which = DateUtil.whichDay(date);
            switch (which) {
                case DateUtil.OTHER_DAY:
                    day =  DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM, DateUtil.YYYY_MM_DD, date);
                    break;
                case DateUtil.TODAY:
                    day = mContext.getString(R.string.content_today);
                    break;
                case DateUtil.TOMORROW:
                    day = mContext.getString(R.string.content_tomorrow);
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }
}
