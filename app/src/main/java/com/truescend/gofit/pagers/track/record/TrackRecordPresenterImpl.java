package com.truescend.gofit.pagers.track.record;


import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.sweetzpot.stravazpot.common.api.exception.StravaAPIException;
import com.sweetzpot.stravazpot.common.api.exception.StravaUnauthorizedException;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.pagers.track.bean.PathMapItem;
import com.truescend.gofit.utils.StravaTool;

/**
 * 作者:东芝(20178/01/06).
 * 功能: 运动轨迹记录查看
 */
public class TrackRecordPresenterImpl extends BasePresenter<ITrackRecordContract.IView> implements ITrackRecordContract.IPresenter {
    private ITrackRecordContract.IView view;
    private String dateTime;
    private String address;


    public TrackRecordPresenterImpl(ITrackRecordContract.IView view) {
        this.view = view;
    }


    @Override
    public void requestLoadTrackRecord(PathMapItem data) {
        view.onUpdateTrackRecord(
                data.getDistanceTotal(),
                data.getSpendTimeTotal(),
                data.getSpeedAvgTotal(),
                data.getSpeedMaxTotal(),
                data.getSpeedAvgPaceTotal(),
                data.getCalories(),
                data.getScreenshotUrl()
        );
        address = data.getAddress();
        dateTime = data.getDateTime();
         if(BuildConfig.isSupportStrava) {
             if (StravaTool.checkGpxFileExists(dateTime)) {
                 view.onShowUploadToStravaItem(false);
             } else if (StravaTool.isGpxFileUploaded(dateTime)) {
                 view.onShowUploadToStravaItem(true);
             }
         }
    }

    @Override
    public void requestUpLoadToStrava() {
        if (!StravaTool.isAuthorized()) {
            view.onUpdateUploadToStravaUnAuthorizedFailed();
            return;
        }
        if (StravaTool.isGpxFileUploaded(dateTime)) {
            view.onUpdateUploadToStravaSuccess();
            return;
        }
        SNAsyncTask.execute(new SNVTaskCallBack() {

            @Override
            public void prepare() {
                view.onUpdateUploadToStravaUpdating();
            }

            @Override
            public void run() throws Throwable {
                   StravaTool.uploadGpxFile(StravaTool.getGpxFile(dateTime), address);
            }

            @Override
            public void error(Throwable e) {
                super.error(e);
                if(e instanceof StravaUnauthorizedException|| e instanceof StravaAPIException){
                    view.onUpdateUploadToStravaFailed(e.getMessage());
                    view.onUpdateUploadToStravaUnAuthorizedFailed();
                }else {
                    view.onUpdateUploadToStravaFailed(e.getMessage());
                }
            }

            @Override
            public void done() {
                super.done();
                view.onUpdateUploadToStravaSuccess();
            }
        });
    }
}
