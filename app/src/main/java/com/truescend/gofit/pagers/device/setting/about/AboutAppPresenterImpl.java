package com.truescend.gofit.pagers.device.setting.about;

import com.sn.app.db.data.config.DeviceConfigBean;
import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.SNLog;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.ble.CMDCompat;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.AppVersionUpdateHelper;
import com.truescend.gofit.utils.ResUtil;

import java.io.File;

/**
 * Author:Created by 泽鑫 on 2017/12/18 18:13.
 * 泽鑫:app更新功能
 * 东芝:修改app更新刷新方式+单位设置功能
 */

public class AboutAppPresenterImpl extends BasePresenter<IAboutAppContract.IView> implements IAboutAppContract.IPresenter, AppVersionUpdateHelper.OnUpdateStatusChangeListener {
    private IAboutAppContract.IView view;
    private AppVersionUpdateHelper mUpdateHelper = new AppVersionUpdateHelper(this);
    public AboutAppPresenterImpl(IAboutAppContract.IView view) {
        this.view = view;
    }

    @Override
    public void requestCheckVersion() {
        mUpdateHelper.checkUpdate();
    }


    @Override
    public void requestStartUpdateApp() {
        mUpdateHelper.startUpdate();
    }


    @Override
    public void onUpdateStatusChange(boolean isNeedUpdate, boolean isNecessary, String newVersion, String localVersion, String description) {
        if(!isUIEnable())return;
        view.onUpdateStatusChange(isNeedUpdate,newVersion,localVersion, description);
    }

    @Override
    public void onUpdateProgressChange(int progress) {
        if(!isUIEnable())return;
        view.onUpdateProgress(progress);
    }

    @Override
    public void onUpdateDone(File apkFile) {
        if(!isUIEnable())return;
        view.onUpdateSuccessful(apkFile);
    }

    @Override
    public void onUpdateFailed(String msg) {
        if(!isUIEnable())return;
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
        view.onDialogLoading(ResUtil.getString(R.string.loading));
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
                boolean update = deviceConfigDao.update(bean);
                SNLog.i("设置数据变化存储:" + update);
                if (update) {
                    sendConfigToDevice();
                }
                sleep(1500);
            }

            @Override
            public void done() {
                super.done();
                if(!isUIEnable())return;
                view.onDialogDismiss();
                //重新刷新界面
                view.onUpdateDeviceConfig(bean);
            }

            @Override
            public void error(Throwable e) {
                super.error(e);
                if(!isUIEnable())return;
                view.onDialogDismiss();
                SNLog.i("设置数据变化存储失败:" + e);
            }
        });
    }

    private void sendConfigToDevice() {
        CMDCompat.get().setUserInfo();
    }

}
