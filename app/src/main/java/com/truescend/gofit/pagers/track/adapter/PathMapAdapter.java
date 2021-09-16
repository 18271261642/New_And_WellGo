package com.truescend.gofit.pagers.track.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseRecycleViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;
import com.truescend.gofit.pagers.track.bean.PathMapItem;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 功能：RecycleView适配器
 * Author:Created by 泽鑫 on 2017/12/13 14:46.
 */

public class PathMapAdapter extends BaseRecycleViewAdapter<PathMapItem> {


    private final RequestManager requestManager;
    private final RoundedCornersTransformation transformation;

    public PathMapAdapter(Context context) {
        super(context);
        requestManager = Glide.with(context);
        transformation = new RoundedCornersTransformation(30, 0, RoundedCornersTransformation.CornerType.ALL);

    }

    @Override
    public void onItemInflate(int position, PathMapItem item, ViewHolder.BaseViewHolder viewHolder, View rootView) {

        ImageView imageView = viewHolder.getImageView(R.id.ivPathMapItemIcon);
        requestManager
                .load(item.getScreenshotUrl())
                .apply(RequestOptions.bitmapTransform( transformation))
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(imageView);

        viewHolder.setTextView(R.id.tvPathMapItemAddress, item.getAddress());
        viewHolder.setTextView(R.id.tvPathMapItemDate, item.getDateTime());
        viewHolder.setTextView(R.id.tvPathMapItemTotalMileage, item.getDistanceTotal());
        viewHolder.setTextView(R.id.tvPathMapItemCalories, item.getCalories());
        viewHolder.setTextView(R.id.tvPathMapItemTotalTime, item.getSpendTimeTotal());
    }

    @Override
    public int initLayout(int viewType) {
        return R.layout.list_path_map_item;
    }
}
