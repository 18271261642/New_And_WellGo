package com.truescend.gofit.pagers.friends.list.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sn.app.net.data.app.bean.FriendListBean;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseListViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;
import com.truescend.gofit.utils.ResUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 作者:东芝(2018/8/9).
 * 功能:朋友列表
 */

public class FriendsListAdapter extends BaseListViewAdapter<FriendListBean.DataBean> {


    private OnEditRemarkClickListener btnEditRemarkClickListener;

    public FriendsListAdapter(Context context) {
        super(context);
    }


    @Override
    public void onItemInflate(int position, FriendListBean.DataBean item, ViewHolder.BaseViewHolder viewHolder, View rootView) {
        String title = item.getRemark();
        if (TextUtils.isEmpty(title)) {
            title = ResUtil.getString(R.string.content_remark_null);
        }
        String sign = item.getFriend().getSign();
        if (TextUtils.isEmpty(sign)) {
            sign = ResUtil.getString(R.string.content_sign_null);
        }
        viewHolder.setTextView(R.id.tvNickname, ResUtil.getString(R.string.content_nickname).concat(item.getFriend().getNickname()));
        viewHolder.setTextView(R.id.tvContent, sign);
        viewHolder.setTextView(R.id.tvStep, String.valueOf(item.getFriend().getSport().getStep()));

        TextView tvTitle = viewHolder.getTextView(R.id.tvTitle);
        tvTitle.setText(title);

        CircleImageView civHead = viewHolder.getView(R.id.civHead);


        Glide.with(civHead)
                .load(item.getFriend().getPortrait())
                .apply(RequestOptions.errorOf(R.mipmap.img_test_picture))
                .apply(RequestOptions.placeholderOf(R.mipmap.img_test_picture))
                .into(civHead);

        View v_remark = viewHolder.getView(R.id.ivEditRemark);

        tvTitle.setTag(position);

        tvTitle.setOnClickListener(onClickListener);
        v_remark.setTag(position);
        v_remark.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (btnEditRemarkClickListener == null) {
                return;
            }
            if (v.getTag() == null) {
                return;
            }
            if (v.getTag() instanceof Integer) {
                btnEditRemarkClickListener.onEditRemarkClick(v, (Integer) v.getTag());
            }
        }
    };

    /**
     * 修改备注
     *
     * @param onEditRemarkClickListener
     */
    public void setOnEditRemarkClickListener(OnEditRemarkClickListener onEditRemarkClickListener) {
        this.btnEditRemarkClickListener = onEditRemarkClickListener;
    }

    public interface OnEditRemarkClickListener {
        void onEditRemarkClick(View v, int position);
    }

    @Override
    public int initLayout() {
        return R.layout.item_friends_list;
    }


}
