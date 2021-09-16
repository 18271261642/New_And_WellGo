package com.truescend.gofit.pagers.home.heart.adapter;

import android.content.Context;
import android.view.View;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseRecycleViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;
import com.truescend.gofit.pagers.home.heart.bean.HeartRateDetailItem;
import com.truescend.gofit.utils.ResUtil;

/**
 * 作者:东芝(2017/12/11).
 * 功能:心率详情
 */
public class HeartRateDetailAdapter extends BaseRecycleViewAdapter<HeartRateDetailItem> {

    private Context context;

    public HeartRateDetailAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onItemInflate(int position, HeartRateDetailItem item, ViewHolder.BaseViewHolder viewHolder, View rootView) {
        viewHolder.setTextView(R.id.tvTime, item.getTime());
        viewHolder.setTextView(R.id.tvValue, ResUtil.format("%d bpm", item.getValue()))
                .setTextColor(context.getResources().getColor(getTypeColor(item.getType())));
    }

    private int getTypeColor(int type) {
        switch (type) {
            case HeartRateDetailItem.TYPE_TOO_HIGH:
                return R.color.red;
            case HeartRateDetailItem.TYPE_TOO_LOW:
                return R.color.orange;
            case HeartRateDetailItem.TYPE_NORMAL:
            default:
                return R.color.black;
        }
    }

    @Override
    public int initLayout(int viewType) {
        return R.layout.item_details_list;
    }
}
