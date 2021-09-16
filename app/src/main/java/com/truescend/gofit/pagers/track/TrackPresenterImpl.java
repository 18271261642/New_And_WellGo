package com.truescend.gofit.pagers.track;

import com.sn.app.db.data.config.bean.UnitConfig;
import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.SportTrackBean;
import com.sn.app.utils.AppUnitUtil;
import com.sn.utils.DateUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.pagers.track.bean.PathMapItem;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.RunTrackUtil;
import com.truescend.gofit.utils.RunningSportDataUtil;
import com.truescend.gofit.utils.StravaTool;
import com.truescend.gofit.utils.UnitConversion;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2018/3/1).
 * 功能:运动轨迹
 */

public class TrackPresenterImpl extends BasePresenter<ITrackContract.IView> implements ITrackContract.IPresenter {
    private ITrackContract.IView view;

    public TrackPresenterImpl(ITrackContract.IView view) {
        this.view = view;
    }


    @Override
    public void requestLoadTrackItemData() {
        AppNetReq.getApi().downloadTrack().enqueue(new OnResponseListener<SportTrackBean>() {
            @Override
            public void onResponse(SportTrackBean body) throws Throwable {
                List<PathMapItem> mapItems = new ArrayList<>();
                List<SportTrackBean.DataBean> data = body.getData();
                for (SportTrackBean.DataBean bean : data) {
                    RunningSportDataUtil.SportData sportData = RunningSportDataUtil.calculateSportData(new RunningSportDataUtil.BaseSportData(bean.getDistance(), bean.getAverage_speed(), bean.getFast_speed()));

                    PathMapItem item = new PathMapItem();

                    UnitConfig unitConfig = AppUnitUtil.getUnitConfig();
                    //如果单位是英里,则需要转一下
                    if (unitConfig.distanceUnit == UnitConfig.DISTANCE_MILES) {
                        item.setDistanceTotal(ResUtil.format( "%.2f %s", UnitConversion.kmToMiles((float) sportData.distances),ResUtil.getString(R.string.unit_mile)));
                        item.setSpeedAvgTotal(ResUtil.format( "%.2f %s",  UnitConversion.kmToMiles((float) sportData.hourSpeed),ResUtil.getString(R.string.unit_mile_h)));
                        item.setSpeedMaxTotal(ResUtil.format("%.2f %s", UnitConversion.kmToMiles((float) sportData.speedMax),ResUtil.getString(R.string.unit_mile_h)));
                    }else{
                        item.setDistanceTotal(ResUtil.format( "%.2f %s", sportData.distances,ResUtil.getString(R.string.unit_km)));
                        item.setSpeedAvgTotal(ResUtil.format( "%.2f %s", sportData.hourSpeed,ResUtil.getString(R.string.unit_km_h)));
                        item.setSpeedMaxTotal(ResUtil.format("%.2f %s", sportData.speedMax,ResUtil.getString(R.string.unit_km_h)));
                    }


//                  item.setDateTime(DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS,"MM-dd HH:mm:ss",bean.getDate()));
                    String date = bean.getDate();
                    item.setDateTime(date);
                    item.setScreenshotUrl(bean.getTrack_image());
                    item.setAddress(bean.getLocation());

                    DateUtil.HMS hms = DateUtil.getHMSFromSeconds(bean.getDuration() );
                    item.setSpendTimeTotal(ResUtil.format("%02d:%02d:%02d", hms.getHour(), hms.getMinute(), hms.getSecond()));

                    item.setSpeedAvgPaceTotal(ResUtil.format("%02d'%02d\"", sportData.speed_minutes, sportData.speed_seconds));

                    item.setCalories(ResUtil.format("%.2f kcal", sportData.calories));


                    mapItems.add(item);
                    //轨迹生成 gpx
                    if (!StravaTool.checkGpxFileExists(date)) {
                        StravaTool.saveToGpxFile(RunTrackUtil.convertToGPXs(bean.getData()), date);
                    }
                }

                view.onUpdateTrackItemData(mapItems);
            }

            @Override
            public void onFailure(int ret, String msg) {
                
            }
        });

    }
}
