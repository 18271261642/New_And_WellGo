package com.truescend.gofit.pagers.friends.list.adapter;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sn.app.net.data.app.bean.SystemMessageBean;
import com.sn.utils.DateUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseListViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;
import com.truescend.gofit.utils.ResUtil;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 作者:东芝(2018/8/9).
 * 功能:消息列表
 */

public class MessageListAdapter extends BaseListViewAdapter<SystemMessageBean.DataBean.ItemsBean> {


    public MessageListAdapter(Context context) {
        super(context);

    }


    @Override
    public void onItemInflate(int position, SystemMessageBean.DataBean.ItemsBean item, ViewHolder.BaseViewHolder viewHolder, View rootView) {

        String content = item.getContent();

        CircleImageView civHead = viewHolder.getView(R.id.civHead);

        int img_type = R.mipmap.icon_zan_on;
        switch (item.getType()) {
            case "encourage":
                img_type = R.mipmap.icon_encourage_on;
                try {
                    content = item.getSender().getNickname()+" "+ResUtil.getString(R.string.content_encourage_you);
                } catch (Exception ignored) {}
                break;
            case "thumb":
                img_type = R.mipmap.icon_zan_on;
                try {
                    content = item.getSender().getNickname()+" "+ResUtil.getString(R.string.content_zan_you);
                } catch (Exception ignored) {
                }
                break;
        }
        viewHolder.setTextView(R.id.tvTitle, content);
        Glide.with(civHead)
                .load(img_type)
                .apply(RequestOptions.errorOf(R.mipmap.img_test_picture))
                .apply(RequestOptions.placeholderOf(R.mipmap.img_test_picture))
                .into(civHead);

        viewHolder.setTextView(R.id.tvContent,getTime(item));

    }

    private String getTime(SystemMessageBean.DataBean.ItemsBean item)  {
        try {
            return getTime(DateUtil.convertStringToLong(DateUtil.YYYY_MM_DD_HH_MM_SS,item.getSend_time()));
        } catch ( Exception ignored) {
        }
        return "";
    }

    private String getTime(long timeMillis_0_time_zone) {
        //系统消息的发送时间是0时区的字符串,需要转成当前时区的时间,  注意,好友请求列表里面的那个时间戳是正常时间,(但服务器文档写的是0时区,其实不是),
        // 所以避免以后回来一看时防止被坑到.故备注一下
        //0时区转当前时区
        long timeMillis = DateUtil.convertLongToCurTimeZoneLong(timeMillis_0_time_zone);
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
    @Override
    public int initLayout() {
        return R.layout.item_message_list;
    }


}
