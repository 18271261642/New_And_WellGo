package com.truescend.gofit.pagers.ranking.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sn.app.net.data.app.bean.FriendListBean;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseRecycleViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;
import com.truescend.gofit.utils.ResUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 作者:东芝(2019/01/11).
 * 功能:亲友运动排行
 */
public class FriendsRankingAdapter extends BaseRecycleViewAdapter<FriendListBean.DataBean> {


    public FriendsRankingAdapter(Context context) {
        super(context);
    }


    @Override
    public void onItemInflate(int position, FriendListBean.DataBean item, ViewHolder.BaseViewHolder viewHolder, View rootView) {
        String title = item.getRemark();
        if (TextUtils.isEmpty(title)) {
            title = item.getFriend().getNickname();
        }
        String sign = item.getFriend().getSign();
        if (TextUtils.isEmpty(sign)) {
            sign = ResUtil.getString(R.string.content_sign_null);
        }
        viewHolder.setTextView(R.id.tvNickname, ResUtil.getString(R.string.content_nickname).concat(item.getFriend().getNickname()));
        viewHolder.setTextView(R.id.tvContent, sign);
        viewHolder.setTextView(R.id.tvStep, String.valueOf(item.getFriend().getSport().getStep()));
        ImageView ivLevelIcon = viewHolder.getImageView(R.id.ivLevelIcon);
        TextView tvLevelText = viewHolder.getTextView(R.id.tvLevelText);
        switch (position) {
            case 0:
                ivLevelIcon.setVisibility(View.VISIBLE);
                tvLevelText.setVisibility(View.INVISIBLE);
                ivLevelIcon.setImageResource( R.mipmap.icon_ranking_level_1);

                break;
            case 1:
                ivLevelIcon.setVisibility(View.VISIBLE);
                tvLevelText.setVisibility(View.INVISIBLE);
                ivLevelIcon.setImageResource( R.mipmap.icon_ranking_level_2);
                break;
            case 2:
                ivLevelIcon.setVisibility(View.VISIBLE);
                tvLevelText.setVisibility(View.INVISIBLE);
                ivLevelIcon.setImageResource( R.mipmap.icon_ranking_level_3);
                break;
            default:
                ivLevelIcon.setVisibility(View.INVISIBLE);
                tvLevelText.setVisibility(View.VISIBLE);
                tvLevelText.setText(String.valueOf(position+1));
                break;
        }

        TextView tvTitle = viewHolder.getTextView(R.id.tvTitle);
        tvTitle.setText(title);

        CircleImageView civHead = viewHolder.getView(R.id.civHead);


        Glide.with(civHead)
                .load(item.getFriend().getPortrait())
                .apply(RequestOptions.errorOf(R.mipmap.img_test_picture))
                .apply(RequestOptions.placeholderOf(R.mipmap.img_test_picture))
                .into(civHead);

    }

    @Override
    public int initLayout(int viewType) {
        return R.layout.item_friends_ranking;
    }



}
