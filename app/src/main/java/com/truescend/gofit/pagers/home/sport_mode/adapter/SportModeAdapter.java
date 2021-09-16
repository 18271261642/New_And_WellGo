package com.truescend.gofit.pagers.home.sport_mode.adapter;

import android.content.Context;
import android.view.View;

import com.sn.app.db.data.config.bean.UnitConfig;
import com.sn.app.utils.AppUnitUtil;
import com.sn.blesdk.db.data.sport_mode.SportModeBean;
import com.sn.utils.DateUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseRecycleViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;
import com.truescend.gofit.pagers.home.sport_mode.bean.ItemSportModeIconUtil;
import com.truescend.gofit.pagers.home.sport_mode.bean.SportModeDetailItem;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.SportModeTypeUtil;
import com.truescend.gofit.utils.UnitConversion;

import java.util.List;

/**
 * 作者:东芝(2019/6/3).
 * 功能:
 */
public class SportModeAdapter extends BaseRecycleViewAdapter<SportModeDetailItem> {
    private final String[] week;
    private String unit_h;
    private String unit_m;
    private String unit_distance;
    private String unit_heart;
    private String unit_cal;
    private String sport_mode_count;


    public SportModeAdapter(Context context, List<SportModeDetailItem> items) {
        super(context, items);
        unit_h = ResUtil.getString(R.string.unit_hours);
        unit_m = ResUtil.getString(R.string.unit_min);
        unit_distance = ResUtil.getString(R.string.unit_km);
        unit_heart = ResUtil.getString(R.string.unit_heart);
        unit_cal = ResUtil.getString(R.string.unit_cal);
        week = context.getResources().getStringArray(R.array.week_day);
        sport_mode_count = ResUtil.getString(R.string.content_sport_mode_count);

    }

    @Override
    public void onItemInflate(int position, SportModeDetailItem item, ViewHolder.BaseViewHolder viewHolder, View rootView) {
        int modeType = item.getModeType();
        viewHolder.setTextView(R.id.tvTitle, SportModeTypeUtil.getLabelResForSportModeType(modeType));
        boolean isWeekMode = item.getStatisticalType() == SportModeDetailItem.STATISTICAL_TYPE_WEEK;
        String subTitle;
        if (isWeekMode) {
            subTitle = getWeekTime(item);
        } else {
            subTitle = ResUtil.format(sport_mode_count, item.getCount());
        }
        viewHolder.setTextView(R.id.tvSubTitle,subTitle);
        viewHolder.setImageView(R.id.ivItemIcon,SportModeTypeUtil.getIconResForSportModeType(modeType));

        //三个图标栏
        View icon1 = viewHolder.getView(R.id.layoutIconTextItem1);
        View icon2 = viewHolder.getView(R.id.layoutIconTextItem2);
        View icon3 = viewHolder.getView(R.id.layoutIconTextItem3);


        //A组 运动开始时间+运动时长+卡路里消耗总结+距离： 健步 跑步  跑步机 登山 足球
        //B组 运动开始时间+运动时长+平均心率+最大心率：骑行 乒乓球 篮球 羽毛球  网球
        //C组 运动开始时间+运动时长+疲劳度 :力量训练 瑜伽     （暂时不做）

        String formatHM = "<strong>%d</strong><small>%s</small><strong>%d</strong><small>%s</small>";
        String formatHM_Min = "<strong>%d</strong><small>%s</small>";
        long h = item.getTakeMinutes() / 60;
        long m = item.getTakeMinutes() % 60;
        CharSequence time = h==0?ResUtil.formatHtml(formatHM_Min, m, unit_m):ResUtil.formatHtml(formatHM, h, unit_h, m, unit_m);
        switch (modeType) {
            //A组
            case SportModeBean.MODE_TYPE_WALKING:
            case SportModeBean.MODE_TYPE_RUNNING:
            case SportModeBean.MODE_TYPE_TREADMILL:
            case SportModeBean.MODE_TYPE_MOUNTAINEERING:
            case SportModeBean.MODE_TYPE_FOOTBALL: {
                //时长
                ItemSportModeIconUtil.getIconImageView(icon1).setImageResource(R.mipmap.icon_details_use_time);
                ItemSportModeIconUtil.getDescriptionTextView(icon1).setText(R.string.content_duration);
                ItemSportModeIconUtil.getContentTextView(icon1).setText(time);
                //卡路里
                ItemSportModeIconUtil.getIconImageView(icon2).setImageResource(R.mipmap.icon_details_calorie);

                ItemSportModeIconUtil.getDescriptionTextView(icon2).setText(R.string.content_finish_calorie);
                ItemSportModeIconUtil.getContentTextView(icon2).setText(ResUtil.formatHtml("<strong>%.2f</strong><small>%s</small>", item.getCalorie() / 1000d, unit_cal));
                //距离
                ItemSportModeIconUtil.getIconImageView(icon3).setImageResource(R.mipmap.icon_details_distance);
                ItemSportModeIconUtil.getDescriptionTextView(icon3).setText(R.string.content_finish_distance);
                float tempDistanceTotal = (item.getDistance() / 1000.0f);
                //如果单位是英里,则需要转一下
                UnitConfig unitConfig = AppUnitUtil.getUnitConfig();
                if (unitConfig.distanceUnit == UnitConfig.DISTANCE_MILES) {
                    unit_distance = ResUtil.getString(R.string.unit_mile);
                    tempDistanceTotal = UnitConversion.kmToMiles(tempDistanceTotal);
                }
                ItemSportModeIconUtil.getContentTextView(icon3).setText(ResUtil.formatHtml("<strong>%.2f</strong><small>%s</small>", tempDistanceTotal, unit_distance));
            }
            break;
            //B组
            case SportModeBean.MODE_TYPE_CYCLING:
            case SportModeBean.MODE_TYPE_TABLE_TENNIS:
            case SportModeBean.MODE_TYPE_BASKETBALL:
            case SportModeBean.MODE_TYPE_BADMINTON:
            case SportModeBean.MODE_TYPE_TENNIS:
            case SportModeBean.MODE_TYPE_SWIMMING: {
                //时长
                ItemSportModeIconUtil.getIconImageView(icon1).setImageResource(R.mipmap.icon_details_use_time);
                ItemSportModeIconUtil.getDescriptionTextView(icon1).setText(R.string.content_duration);
                ItemSportModeIconUtil.getContentTextView(icon1).setText(time);
                //平均心率
                ItemSportModeIconUtil.getIconImageView(icon2).setImageResource(R.mipmap.icon_details_heart_rate_avg);
                ItemSportModeIconUtil.getDescriptionTextView(icon2).setText(R.string.content_average_heart);
                ItemSportModeIconUtil.getContentTextView(icon2).setText(ResUtil.formatHtml("<strong>%.0f</strong><small>%s</small>", item.getHrAvg(), unit_heart));
                //最大心率
                ItemSportModeIconUtil.getIconImageView(icon3).setImageResource(R.mipmap.icon_details_heart_rate_max);
                ItemSportModeIconUtil.getDescriptionTextView(icon3).setText(R.string.content_highest_heart);
                ItemSportModeIconUtil.getContentTextView(icon3).setText(ResUtil.formatHtml("<strong>%.0f</strong><small>%s</small>", item.getHrMax(), unit_heart));
            }
            break;
        }

    }

    private String getWeekTime(SportModeDetailItem item)   {
        try {
            return  DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.YYYY_MM_DD, item.getBeginDateTime())
                    + " " +
                    week[DateUtil.getWeekIndex(DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, item.getDate()))]
                    + " " +
                    DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM, item.getBeginDateTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item.getBeginDateTime();
    }

    @Override
    public int initLayout(int viewType) {
        return R.layout.list_sport_mode_item;
    }
}
