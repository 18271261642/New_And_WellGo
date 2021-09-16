package com.truescend.gofit.pagers.device.push;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.sn.app.db.data.config.DeviceConfigBean;
import com.sn.app.db.data.config.DeviceConfigDao;
import com.sn.app.db.data.config.bean.RemindConfig;
import com.sn.app.storage.AppPushStorage;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.SNLog;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.App;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.ble.CMDCompat;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.pagers.device.bean.ItemApps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * 作者:东芝(2018/2/5).
 * 功能:app推送
 */
public class PushPresenterImpl extends BasePresenter<IPushContract.IView> implements IPushContract.IPresenter {
    private IPushContract.IView view;

    public PushPresenterImpl(IPushContract.IView view) {
        this.view = view;
    }

    @Override
    public void requestDeviceConfig() {
        SNAsyncTask.execute(new SNVTaskCallBack() {

            private DeviceConfigBean deviceConfigBean;

            @Override
            public void run() throws Throwable {
                DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
                deviceConfigBean = deviceConfigDao.queryForUser(AppUserUtil.getUser().getUser_id());
            }

            @Override
            public void done() {
                view.onUpdateDeviceConfig(deviceConfigBean);
            }
        });
    }

    @Override
    public void requestLoadAllApps() {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            List<ItemApps> itemApps = new ArrayList<>();

            @Override
            public void run() throws Throwable {
                DeviceConfigDao deviceConfigDao = DeviceConfigDao.get(DeviceConfigDao.class);
                DeviceConfigBean deviceConfigBean = deviceConfigDao.queryForUser(AppUserUtil.getUser().getUser_id());
                RemindConfig remindConfig = deviceConfigBean.getRemindConfig();


                final PackageManager pm = App.getContext().getPackageManager();
                List<ApplicationInfo> installedApplications = pm.getInstalledApplications(0);
                for (ApplicationInfo appInfo : installedApplications) {
                    if (!appInfo.enabled/*被冻结的*/ || pm.getLaunchIntentForPackage(appInfo.packageName) == null/*不可启动的*/) {
                        continue;
                    }
                    if(appInfo.packageName.equals(BuildConfig.APPLICATION_ID)){
                        continue;
                    }
                    if (remindConfig.findRemindAppPush(appInfo.packageName) == null) {
                        itemApps.add(new ItemApps(appInfo,pm.getApplicationLabel(appInfo).toString(), AppPushStorage.isAppPushCheck(appInfo.packageName)));
                    }
                }

                Collections.sort(itemApps, new Comparator<ItemApps>() {
                    @Override
                    public int compare(ItemApps o1, ItemApps o2) {
                        return o1.getAppInfo().loadLabel(pm).toString().compareTo(o2.getAppInfo().loadLabel(pm).toString());
                    }
                });

                Collections.sort(itemApps, new Comparator<ItemApps>() {
                    @Override
                    public int compare(ItemApps o1, ItemApps o2) {
                        //两个值进行位运算,值不同为1,为true,参与运算
                        //值相同为0,为false,不参与运算
                        if (o1.isChecked() ^ o2.isChecked()) {
                            return o1.isChecked() ? -1 : 1;
                        } else {
                            return 0;
                        }
                    }
                });
            }

            @Override
            public void done() {
                if (!isUIEnable()) return;
                view.onUpdateAllApps(itemApps);
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
                if (update) {
                    sendConfigToDevice();
                }
            }

            @Override
            public void done() {
                super.done();
                if (!isUIEnable()) return;
                //重新刷新界面
                view.onUpdateDeviceConfig(bean);
            }

            @Override
            public void error(Throwable e) {
                super.error(e);
                SNLog.i("设置数据变化存储失败:" + e);
            }
        });
    }

    private void sendConfigToDevice() {
        CMDCompat.get().setDeviceNightModeInfo();
    }


}
