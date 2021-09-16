package com.truescend.gofit.pagers.home.oxygen.adapter;

import android.content.Context;
import android.view.View;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseRecycleViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;
import com.truescend.gofit.pagers.home.oxygen.bean.BloodOxygenDetailItem;
import com.truescend.gofit.utils.ResUtil;

/**
 * 作者:东芝(2017/12/11).
 * 功能:血氧详情
 */
public class BloodOxygenDetailAdapter extends BaseRecycleViewAdapter<BloodOxygenDetailItem> {

    private Context context;

    public BloodOxygenDetailAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onItemInflate(int position, BloodOxygenDetailItem item, ViewHolder.BaseViewHolder viewHolder, View rootView) {
        viewHolder.setTextView(R.id.tvTime, item.getTime());
        viewHolder.setTextView(R.id.tvValue, ResUtil.format("%d%%", item.getValue()));
    }

    @Override
    public int initLayout(int viewType) {
        return R.layout.item_details_list;
    }
}
