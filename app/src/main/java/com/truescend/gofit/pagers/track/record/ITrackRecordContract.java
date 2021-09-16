package com.truescend.gofit.pagers.track.record;

import com.truescend.gofit.pagers.track.bean.PathMapItem;

/**
 * 作者:东芝(20178/01/06).
 * 功能:
 */

public class ITrackRecordContract {

    interface IView {
        /**
         * @param distanceTotal     总距离
         * @param spendTimeTotal    总耗时
         * @param speedAvgTotal     总平均时速
         * @param speedMaxTotal     最大速度
         * @param speedAvgPaceTotal 平均配速
         * @param calories          卡路里
         * @param screenshotUrl    截图数据
         */
        void onUpdateTrackRecord(String distanceTotal, String spendTimeTotal, String speedAvgTotal, String speedMaxTotal, String speedAvgPaceTotal, String calories, String screenshotUrl);
        void onShowUploadToStravaItem(boolean isUploaded);
        void onUpdateUploadToStravaUnAuthorizedFailed();
        void onUpdateUploadToStravaUpdating();
        void onUpdateUploadToStravaSuccess();
        void onUpdateUploadToStravaFailed(String msg);
    }

    interface IPresenter {

        void requestLoadTrackRecord(PathMapItem data);
        void requestUpLoadToStrava();
    }
}
