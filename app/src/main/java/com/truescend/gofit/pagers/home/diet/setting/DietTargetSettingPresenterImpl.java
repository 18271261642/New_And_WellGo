package com.truescend.gofit.pagers.home.diet.setting;

import com.sn.app.db.data.config.DeviceConfigBean;
import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.UserMessageBean;
import com.sn.app.storage.UserStorage;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.sport.SportDao;
import com.sn.utils.DateUtil;
import com.sn.utils.SNLog;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.ble.CMDCompat;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;

/**
 * 作者:东芝(2018/11/21).
 * 功能:用户饮食减肥计划目标参数设置
 */

public class DietTargetSettingPresenterImpl extends BasePresenter<IDietTargetSettingContract.IView> implements IDietTargetSettingContract.IPresenter {
    private IDietTargetSettingContract.IView view;


    public DietTargetSettingPresenterImpl(IDietTargetSettingContract.IView view) {
        this.view = view;
    }


    @Override
    public void requestUpdateDietTarget(int targetStep, float curWeight,float targetWeight, float targetCalory) {
        view.onDialogLoading(ResUtil.getString(R.string.loading));
        AppNetReq.getApi()
                .updateUser(targetStep,Math.round(curWeight),Math.round(targetWeight),Math.round(targetCalory))
                .enqueue(new OnResponseListener<UserMessageBean>() {
            @Override
            public void onResponse(UserMessageBean body) throws Throwable {
                final UserMessageBean.DataBean data = body.getData();
                SNAsyncTask.execute(new SNVTaskCallBack() {
                    @Override
                    public void run() throws Throwable {
                        //重刷新程序层的用户数据信息
                        AppUserUtil.initialize(data);
                        UserStorage.setIsFirst(false);
                        //更新当天目标
                        SportDao sportDao = SNBLEDao.get(SportDao.class);
                        sportDao.updateStepTarget(AppUserUtil.getUser().getUser_id(), DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD), data.getTarget_step());


                    }

                    @Override
                    public void done() {
                        super.done();
                        CMDCompat.get().setUserInfo();
                        if(!isUIEnable())return;
                        view.onUpdateUserDataSuccess();
                        view.onDialogDismiss();
                    }

                    @Override
                    public void error(Throwable e) {
                        super.error(e);
                        if(!isUIEnable())return;
                        view.onUpdateUserDataFailed(e.getMessage());
                        view.onDialogDismiss();
                    }
                });
            }

            @Override
            public void onFailure(int ret, String msg) {
                if(!isUIEnable())return;
                view.onUpdateUserDataFailed(msg);
                view.onDialogDismiss();
            }
        });
    }

    @Override
    public void requestDeviceConfig()
    {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            private DeviceConfigBean deviceConfigBean;
            @Override
            public void run() throws Throwable {
                DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
                deviceConfigBean = deviceConfigDao.queryForUser(AppUserUtil.getUser().getUser_id());
            }
            @Override
            public void done() {
                if(!isUIEnable())return;
                view.onUpdateDeviceConfig(deviceConfigBean);
            }
        });
    }
    @Override
    public void requestChangeDeviceConfigData(final DeviceConfigBean bean) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
                boolean update = deviceConfigDao.update(bean);
                SNLog.i("设置数据变化存储:" + update);
            }

            @Override
            public void done() {
                super.done();
                if(!isUIEnable())return;
                //重新刷新界面
                view.onUpdateDeviceConfig(bean);
            }

            @Override
            public void error(Throwable e) {
                super.error(e);
                if(!isUIEnable())return;
                SNLog.i("设置数据变化存储失败:" + e);
            }
        });
    }

}
