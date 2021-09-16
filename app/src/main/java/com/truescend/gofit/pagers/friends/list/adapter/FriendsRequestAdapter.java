package com.truescend.gofit.pagers.friends.list.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sn.app.net.data.app.bean.FriendInvitesBean;
import com.sn.utils.DateUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseListViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;
import com.truescend.gofit.utils.ResUtil;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 作者:东芝(2018/8/9).
 * 功能:好友请求
 */

public class FriendsRequestAdapter extends BaseListViewAdapter<FriendInvitesBean.DataBean> {

    public FriendsRequestAdapter(Context context) {
        super(context);
    }

    private OnButtonClickListener listener;

    @Override
    public void onItemInflate(int position, FriendInvitesBean.DataBean item, ViewHolder.BaseViewHolder viewHolder, View rootView) {
        viewHolder.setTextView(R.id.tvTitle, item.getInviter().getNickname());
        viewHolder.setTextView(R.id.tvContent, ResUtil.getString(R.string.content_add_agree_friends));
        viewHolder.setTextView(R.id.tvTime, getTime(item.getInvite_time() * 1000L));
        TextView tvStatusText = viewHolder.getTextView(R.id.tvStatusText);
        TextView tvRefused = viewHolder.getTextView(R.id.tvRefused);
        TextView tvAgreed = viewHolder.getTextView(R.id.tvAgreed);
        final CircleImageView civHead = viewHolder.getView(R.id.civHead);
        //状态，1：未同意，2：已同意，3：已拒绝
        switch (item.getStatus()) {
            case 1:
                tvRefused.setVisibility(View.VISIBLE);
                tvAgreed.setVisibility(View.VISIBLE);
                tvStatusText.setVisibility(View.GONE);
                break;
            case 2:
                tvRefused.setVisibility(View.GONE);
                tvAgreed.setVisibility(View.GONE);
                tvStatusText.setVisibility(View.VISIBLE);
                tvStatusText.setText(ResUtil.getString(R.string.content_already_friends));
                break;
            case 3:
                tvRefused.setVisibility(View.GONE);
                tvAgreed.setVisibility(View.GONE);
                tvStatusText.setVisibility(View.VISIBLE);
                tvStatusText.setText(ResUtil.getString(R.string.content_already_refused));
                break;
        }
        Glide.with(civHead)
                .load(item.getInviter().getPortrait())
                .apply(RequestOptions.errorOf(R.mipmap.img_test_picture))
                .apply(RequestOptions.placeholderOf(R.mipmap.img_test_picture))
                .into(civHead);
        setOnClickListener(position, tvRefused, tvAgreed);
    }

    private String getTime(long timeMillis) {

        // 注意,好友请求列表里面的时间戳是正常时间,(但服务器文档写的是0时区,其实不是),
        // 所以不需要转换直接显示时间即可
        // 所以避免以后回来一看时防止被坑到.故备注一下

        //得出实际日期时间
        String date;
        if (DateUtil.equalsToday(timeMillis)) {
            date = DateUtil.getDate("HH:mm", timeMillis);
        } else if (DateUtil.getYear(timeMillis) == DateUtil.getYear(new Date())) {
            date = DateUtil.getDate("MM/dd", timeMillis);
        } else {
            date = DateUtil.getDate("yyyy/MM/dd", timeMillis);
        }

        return date;
    }

    private void setOnClickListener(int position, TextView tvRefused, TextView tvAgreed) {
        tvRefused.setTag(position);
        tvRefused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (v.getTag() instanceof Integer) {
                        listener.onClickRefused(((Integer) v.getTag()));
                    }
                }
            }
        });
        tvAgreed.setTag(position);
        tvAgreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (v.getTag() instanceof Integer) {
                        listener.onClickAgreed(((Integer) v.getTag()));
                    }
                }
            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.item_friends_request;
    }


    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;
    }

    public interface OnButtonClickListener {
        void onClickAgreed(int position);

        void onClickRefused(int position);
    }
}
