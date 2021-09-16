package com.truescend.gofit.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.AppVersionNetBean;
import com.sn.net.utils.FileDownload;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.BuildConfig;

import java.io.File;
import java.util.List;

/**
 * 作者:东芝(2018/4/23).
 * 功能:检测更新
 */

public class AppVersionUpdateHelper {
    private boolean needUpdate;
    private String downloadUrl;
    private OnUpdateStatusChangeListener listener;

    public AppVersionUpdateHelper(OnUpdateStatusChangeListener listener) {
        this.listener = listener;
    }

    public boolean isNeedUpdate() {
        return needUpdate;
    }

    public void checkUpdate() {
        if (listener == null) return;
        AppNetReq.getApi().checkAppNewVersion().enqueue(new OnResponseListener<AppVersionNetBean>() {
            @Override
            public void onResponse(AppVersionNetBean body) throws Throwable {
                List<AppVersionNetBean.DataBean> data1 = body.getData();
                AppVersionNetBean.DataBean data = null;
                for (AppVersionNetBean.DataBean dataBean : data1) {
                    if (dataBean.getApp_name().equalsIgnoreCase(BuildConfig.APP_UPDATE_APPNAME)) {
                        data = dataBean;
                    }
                }
                if (data != null) {
                    downloadUrl = data.getDownload_url();
                    String description = data.getDescription();
                    if (data.getVersion_code() > BuildConfig.VERSION_CODE) {
                        needUpdate = true;
                        listener.onUpdateStatusChange(true, data.isNecessary(), data.getVersion_name(), BuildConfig.VERSION_NAME, description);
                    } else {
                        needUpdate = false;
                        listener.onUpdateStatusChange(false, data.isNecessary(), data.getVersion_name(), BuildConfig.VERSION_NAME, description);
                    }
                }
            }

            @Override
            public void onFailure(int ret, String msg) {
                listener.onUpdateFailed(msg);
            }
        });
    }

    public void startUpdate() {
        if (listener == null) return;
        SNAsyncTask.execute(new SNVTaskCallBack() {
            private File mApkFile;

            @Override
            public void run() throws Throwable {
                mApkFile = new File(Constant.Path.CACHE_APP_DOWNLOAD, "app.apk");
                FileDownload.dowanload(downloadUrl, mApkFile.getPath(), new FileDownload.OnProgressChangeListener() {
                    @Override
                    public void onProgress(int progress) {
                        publishToMainThread(1, progress);
                    }
                });
            }

            @Override
            public void main(int what, Object... obj) {
                if (what == 1) {
                    listener.onUpdateProgressChange( (Integer) obj[0]);
                }
            }

            @Override
            public void done() {
                listener.onUpdateDone(mApkFile);
            }
        });
    }

    public static void startInstall(Context context,File file){
        if(file==null)return;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(CompatUtil.getUriForFile(context, file), "application/vnd.android.package-archive");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public interface OnUpdateStatusChangeListener {
        void onUpdateStatusChange(boolean isNeedUpdate, boolean isNecessary, String newVersion, String localVersion, String description);
        void onUpdateProgressChange( int progress);
        void onUpdateDone(File apkFile);
        void onUpdateFailed(String msg);
    }
}
