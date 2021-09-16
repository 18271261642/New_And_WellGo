package com.truescend.gofit.pagers.device.wallpaper;


import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.List;

/**
 * 作者:东芝(2019/4/2).
 * 功能:手环壁纸功能
 */

public class IWallpaperContract {

    interface IView {
        void onDialogLoading(String msg);

        void onDialogDismiss();

        void onUpdateWallpaperProgressChanged(int cur, int total);

        void onUpdateWallpaperFailed(String msg);

        void onUpdateWallpaperSuccessful();
        void onLoadWallpaperInfo(boolean isSupport, boolean isEnable, int screenWidth, int screenHeight, int screenType, boolean isTimeEnable, boolean isStepEnable, List<Point> fontList, Bitmap wallpaperSrc, boolean isDefaultWallpaper, PointF firstLocation, int fontColor);
        void onLoadWallpaperFailed();

        void onUpdateBleDisconnect();
    }

    interface IPresenter {
        void updateWallpaperData(Bitmap wallpaperSrc, Bitmap wallpaper, boolean isWallpaperEnable, boolean isWallpaperModified, boolean isTimeEnable, boolean isStepEnable, PointF timeLocation, PointF stepLocation, Point timeFontSize, Point stepFontSize, int fontColor);

        void loadWallpaperInfo();

    }
}
