package com.truescend.gofit.pagers.friends.invitation;

import android.app.Activity;
import android.graphics.Bitmap;

import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.AppShareUtil;
import com.truescend.gofit.utils.CameraFixUtil;
import com.truescend.gofit.utils.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 作者:东芝(2018/08/08).
 * 功能:添加好友
 */
public class MyQRCPresenterImpl extends BasePresenter<IMyQRCContract.IView> implements IMyQRCContract.IPresenter {
    private IMyQRCContract.IView view;


    public MyQRCPresenterImpl(IMyQRCContract.IView view) {
        this.view = view;
    }


    @Override
    public void saveScreenshots() {
        SNAsyncTask.execute(new SNVTaskCallBack(view) {
            File file;

            @Override
            public void run() throws Throwable {
                Object target = getTarget();
                if (target != null && target instanceof Activity) {
                    Activity act = (Activity) target;
                    if (act.isFinishing() || act.isDestroyed()) {
                        return;
                    }
                    Bitmap capture = AppShareUtil.capture(act);
                      file = getOutputMediaFile();
                    if (file != null) {
                        capture.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                    }
                    if (!capture.isRecycled()) {
                        capture.recycle();
                    }

                }
            }

            @Override
            public void done() {
                super.done();
                if(!isUIEnable())return;
                Object target = getTarget();
                if (file!=null&&file.exists()&&file.canRead()&&target != null && target instanceof Activity) {
                    Activity act = (Activity) target;
                    if (act.isFinishing() || act.isDestroyed()) {
                        return;
                    }
                    CameraFixUtil.notifyImage(act, file);
                    view.onSaveScreenshots(file.getAbsolutePath());
                }
            }

            /**
             * 输出图片格式
             */
            private File getOutputMediaFile() {
                File mediaStorageDir = new File(Constant.Path.CAMERA, "Camera");
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        return null;
                    }
                }
                /**
                 * 相片命名格式不能变，要不然无法保存
                 */
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
                return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            }
        });
    }
}
