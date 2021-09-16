package com.truescend.gofit.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import androidx.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;

import com.sn.map.interfaces.OnMapScreenShotListener;
import com.sn.map.view.SNMapHelper;
import com.sn.utils.net.ShareUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Author:Created by 泽鑫 on 2018/3/10 10:22.
 * 东芝:修改添加loading 加载中弹窗 , 增加弱引用防止内存泄露 ,并且兼容Android N(7.0) 的文件uri 新特性
 */

public class AppShareUtil extends ShareUtil {

    /**
     * 地图分享
     * @param mapHelper
     * @param other
     */
    public static void shareCaptureMap(final Activity activity, SNMapHelper mapHelper, final View other) {
        if(mapHelper==null){
            return;
        }
        mapHelper.screenCapture(false, new OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(final Bitmap bitmap) {
              SNAsyncTask.execute(new SNVTaskCallBack(activity) {
                  File file;
                  @Override
                  public void prepare() {
                      super.prepare();
                      Activity target = (Activity) getTarget();
                      if(target==null||target.isFinishing()||target.isDestroyed())return;
                      LoadingDialog.show(target, R.string.dialog_waiting);
                  }
                  @Override
                  public void run() throws Throwable {
                      try {
                          file = new File(Constant.Path.CACHE_SHARE_IMAGE, "share_image.jpg");
                          other.setDrawingCacheEnabled(true);
                          Bitmap viewBM = other.getDrawingCache().copy(Bitmap.Config.ARGB_8888,true);
                          Bitmap mapBM = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                          Canvas canvas = new Canvas(mapBM);
                          canvas.drawBitmap(viewBM,new Matrix(),null);
                          mapBM .compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                          other.setDrawingCacheEnabled(false);
                          try {
                              if (!bitmap.isRecycled()) {
                                  bitmap.recycle();
                              }
                          } catch (Throwable ignored) {
                          }
                          try {
                              if (!mapBM.isRecycled()) {
                                  mapBM.recycle();
                              }
                          } catch (Throwable ignored) {
                          }
                          try {
                              if (!viewBM.isRecycled()) {
                                  viewBM.recycle();
                              }
                          } catch (Throwable ignored) {
                          }
                      } catch (Throwable e) {
                          e.printStackTrace();
                      }
                  }

                  @Override
                  public void done() {
                      Activity target = getTarget();
                      if(target==null||target.isFinishing()||target.isDestroyed())return;
                      LoadingDialog.dismiss();
                      if(file==null)return;
                      shareImage(target, target.getString(R.string.app_name), "", "", file);
                  }
                  @Override
                  public void error(Throwable e) {
                      LoadingDialog.dismiss();
                  }
              });
            }
        } );

    }
    public static void shareCapture(final Activity activity) {
        shareCapture(activity,-1);
    }
    public static void shareCapture(final Activity activity, @IdRes final int scrollviewId) {


        SNAsyncTask.execute(new SNVTaskCallBack(activity) {
            File file;

            @Override
            public void prepare() {
                super.prepare();
                Activity target = (Activity) getTarget();
                if(target==null||target.isFinishing()||target.isDestroyed())return;
                LoadingDialog.show(target, R.string.dialog_waiting);
            }

            @Override
            public void run() throws Throwable {
                Activity target = (Activity) getTarget();
                if(target==null||target.isFinishing()||target.isDestroyed())return;
                Bitmap bitmap = scrollviewId==-1?capture(target):capture(target, (ViewGroup) target.findViewById(scrollviewId));
                file = new File(Constant.Path.CACHE_SHARE_IMAGE, "share_image.jpg");
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
            }

            @Override
            public void done() {
                Activity target = getTarget();
                if(target==null||target.isFinishing()||target.isDestroyed())return;
                LoadingDialog.dismiss();
                if(file==null)return;
                shareImage(target, target.getString(R.string.app_name), "", "", file);
            }

            @Override
            public void error(Throwable e) {
                Activity target = getTarget();
                if(target==null||target.isFinishing()||target.isDestroyed())return;
                LoadingDialog.dismiss();
            }
        });
    }


}
