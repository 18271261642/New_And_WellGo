package com.truescend.gofit.pagers.home.pressure.adapter;

import android.content.Context;
import android.view.View;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseRecycleViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;
import com.truescend.gofit.pagers.home.pressure.bean.BloodPressureDetailItem;
import com.truescend.gofit.utils.ResUtil;

/**
 * 作者:东芝(2017/12/11).
 * 功能:血压详情
 */
public class BloodPressureDetailAdapter extends BaseRecycleViewAdapter<BloodPressureDetailItem> {

    private Context context;

    public BloodPressureDetailAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onItemInflate(int position, BloodPressureDetailItem item, ViewHolder.BaseViewHolder viewHolder, View rootView) {
        viewHolder.setTextView(R.id.tvTime, item.getTime());
        viewHolder.setTextView(R.id.tvSystolic, ResUtil.format( "%d mmHg", item.getSystolic()))
                .setTextColor(context.getResources().getColor(getTypeColor(item.getSystolic_type())));
        viewHolder.setTextView(R.id.tvDiastolic, ResUtil.format("%d mmHg", item.getDiastolic()))
                .setTextColor(context.getResources().getColor(getTypeColor(item.getDiastolic_type())));
    }

    private int getTypeColor(int type) {
        switch (type) {
            case BloodPressureDetailItem.TYPE_DIASTOLIC_ABNORMAL:
                return R.color.red;
            case BloodPressureDetailItem.TYPE_SYSTOLIC_ABNORMAL:
                return R.color.orange;
            case BloodPressureDetailItem.TYPE_NORMAL:
            default:
                return R.color.black;
        }
    }
    @Override
    public int initLayout(int viewType) {
        return R.layout.item_details_list2;
    }
}
