package com.truescend.gofit.pagers.device.wallpaper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.DrawableRes;

import com.dz.blesdk.ble.BaseUUID;
import com.dz.blesdk.ble.BleHelper;
import com.dz.blesdk.interfaces.NotifyReceiverListener;
import com.sn.app.storage.WallpaperInfoStorage;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.entity.WallpaperPackage;
import com.sn.blesdk.interfaces.OnWallpaperUploadListener;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.eventbus.SNEvent;
import com.sn.utils.eventbus.SNEventBus;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.WallpaperPickerUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 作者:东芝(2019/4/2).
 * 功能:手环壁纸功能
 */

public class WallpaperPresenterImpl extends BasePresenter<IWallpaperContract.IView> implements IWallpaperContract.IPresenter, OnWallpaperUploadListener {
    private IWallpaperContract.IView view;
    private Bitmap wallpaper;
    private Bitmap wallpaperSrc;
    private boolean isWallpaperEnable;
    private boolean isWallpaperModified;
    private boolean isTimeEnable;
    private boolean isStepEnable;
    private PointF timeLocation;
    private PointF stepLocation;
    private Point timeFontSize;
    private Point stepFontSize;
    private int fontColor;
    private String mImagePath;
    private String mac;

    public WallpaperPresenterImpl(IWallpaperContract.IView view) {
        this.view = view;
    }


    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void updateWallpaperData(Bitmap wallpaperSrc, final Bitmap wallpaper, boolean isWallpaperEnable, boolean isWallpaperModified, final boolean isTimeEnable, final boolean isStepEnable, final PointF timeLocation, final PointF stepLocation, final Point timeFontSize, final Point stepFontSize, final int fontColor) {
        this.wallpaperSrc = wallpaperSrc;
        this.wallpaper = wallpaper;
        this.isWallpaperEnable = isWallpaperEnable;
        this.isWallpaperModified = isWallpaperModified;
        this.isTimeEnable = isTimeEnable;
        this.isStepEnable = isStepEnable;
        this.timeLocation = timeLocation;
        this.stepLocation = stepLocation;
        this.timeFontSize = timeFontSize;
        this.stepFontSize = stepFontSize;
        this.fontColor = fontColor;
        init();
        if (isUIEnable()) {
            getView().onDialogLoading(ResUtil.getString(R.string.loading));
        }
        updateWallpaperEnable();
    }

    private void init() {
        this.mac = DeviceType.getDeviceMac();
        this.mImagePath = WallpaperPickerUtil.getLastUserImagePath(mac);
    }

    private void updateWallpaperImage(final Bitmap wallpaper) {
        //设置BLE通讯进入高速传输模式
        //设置手环进入高速传输模式
        SNBLEHelper.addCommunicationListener(new NotifyReceiverListener() {
            @Override
            public void onReceive(byte[] buffer) {
                if (SNBLEHelper.startWith(buffer, "05021101")) {
                    SNBLEHelper.removeCommunicationListener(this);
                    onDialogDismiss();
                    SNAsyncTask.execute(new SNVTaskCallBack() {
                        @Override
                        public void run() throws Throwable {
                            List<WallpaperPackage> wallpaperPackage = SNCMD.get().createWallpaperPackage(wallpaper);
                            SNBLEHelper.sendWallpaperCMD(wallpaperPackage, WallpaperPresenterImpl.this);
                        }

                        @Override
                        public void error(Throwable e) {
                            super.error(e);
                            onDialogDismiss();
                            onFailed(new Exception(e));
                            SNBLEHelper.abortSendWallpaper();
                        }
                    });
                } else if (SNBLEHelper.startWith(buffer, "05021100")) {
                    SNBLEHelper.removeCommunicationListener(this);
                    onDialogDismiss();
                    onFailed(new Exception());
                    SNBLEHelper.abortSendWallpaper();
                }
            }
        });
        SNBLEHelper.sendCMD(SNCMD.get().setHighSpeedTransportStatus(true));
    }


    private void updateWallpaperEnable() {
        SNBLEHelper.addCommunicationListener(new NotifyReceiverListener() {
            @Override
            public void onReceive(byte[] buffer) {
                if (SNBLEHelper.startWith(buffer, "05021201")) {//开启
                    SNBLEHelper.removeCommunicationListener(this);
                    updateWallpaperTimeInfo();
                } else if (SNBLEHelper.startWith(buffer, "05021202")) {//关闭
                    SNBLEHelper.removeCommunicationListener(this);
                    if (isUIEnable()) {
                        onDialogDismiss();
                        onUpdateWallpaperSuccessful();
                    }
                }
            }
        });
        SNBLEHelper.sendCMD(SNCMD.get().setWallpaperEnable(isWallpaperEnable));
    }

    private void updateWallpaperTimeInfo() {
        SNBLEHelper.addCommunicationListener(new NotifyReceiverListener() {
            @Override
            public void onReceive(byte[] buffer) {
                if (SNBLEHelper.startWith(buffer, "05021301")) {//开启
                    SNBLEHelper.removeCommunicationListener(this);
                    updateWallpaperStepInfo();
                } else if (SNBLEHelper.startWith(buffer, "050213")) {
                    SNBLEHelper.removeCommunicationListener(this);
                    int status = buffer[3] & 0xFF;
                    //Ox00:保留
                    //0x01:设置成功
                    //0x02:显示方式不支持
                    //0x03:需要显示的字体分辨率不支持
                    //0x04:需要显示的字体颜色不支持
                    //0x05:X 轴，Y 轴起始坐标点异常
                    //0x06:X 轴，Y 轴起始坐标点正常，根据计算，已越界
                    //0x07:与其他屏保效果块重叠
                    onFailed(new Exception("error code = " + status));
                    onDialogDismiss();
                }
            }

        });
        SNBLEHelper.sendCMD(SNCMD.get().setWallpaperTimeInfo(isTimeEnable, true, timeFontSize.x, timeFontSize.y, fontColor, Math.round(timeLocation.x), Math.round(timeLocation.y)));
    }

    private void updateWallpaperStepInfo() {
        SNBLEHelper.addCommunicationListener(new NotifyReceiverListener() {
            @Override
            public void onReceive(byte[] buffer) {
                if (SNBLEHelper.startWith(buffer, "05021401")) {//开启
                    SNBLEHelper.removeCommunicationListener(this);

                    WallpaperInfoStorage.setFontColor(mac, fontColor);
                    WallpaperInfoStorage.setTimeLocation(mac, timeLocation);
                    //至此屏幕信息已发送完成
                    //如果修改了壁纸 就传输壁纸,如果没有 就直接成功了
                    if (isWallpaperModified) {
                        updateWallpaperImage(wallpaper);
                    } else {
                        if (isUIEnable()) {
                            onDialogDismiss();
                            onUpdateWallpaperSuccessful();
                        }
                    }

                } else if (SNBLEHelper.startWith(buffer, "050214")) {
                    SNBLEHelper.removeCommunicationListener(this);
                    int status = buffer[3] & 0xFF;
                    //Ox00:保留
                    //0x01:设置成功
                    //0x02:显示方式不支持
                    //0x03:需要显示的字体分辨率不支持
                    //0x04:需要显示的字体颜色不支持
                    //0x05:X 轴，Y 轴起始坐标点异常
                    //0x06:X 轴，Y 轴起始坐标点正常，根据计算，已越界
                    //0x07:与其他屏保效果块重叠
                    onFailed(new Exception("error code = " + status));
                    onDialogDismiss();
                }
            }
        });

        SNBLEHelper.sendCMD(SNCMD.get().setWallpaperStepInfo(isStepEnable, true, stepFontSize.x, stepFontSize.y, fontColor, Math.round(stepLocation.x), Math.round(stepLocation.y)));
    }


    @Override
    public void loadWallpaperInfo() {
        if (isUIEnable()) {
            getView().onDialogLoading(ResUtil.getString(R.string.loading));
        }
        init();

        SNAsyncTask.execute(new SNVTaskCallBack() {
            boolean errorExit;

            @Override
            public void run() throws Throwable {
                int count = 0;
                while (isUIEnable()) {
                    if (BleHelper.getInstance().setNotifyEnable(BaseUUID.SERVICE, BaseUUID.NOTIFY_WALLPAPER, BaseUUID.DESC, true)) {
                        break;
                    }
                    if (count >= 10) {
                        errorExit = true;
                        break;
                    }
                    Thread.sleep(1000);
                    count++;
                }
            }

            @Override
            public void done() {
                super.done();
                if (isUIEnable()) {
                    if (errorExit) {
                        onDialogDismiss();
                        getView().onLoadWallpaperFailed();
                    } else {
                        updateWallpaperInfo();
                    }
                }
            }


            @Override
            public void error(Throwable e) {
                super.error(e);
                if (isUIEnable()) {
                    onDialogDismiss();
                    getView().onLoadWallpaperFailed();
                }
            }
        });
    }

    private void updateWallpaperInfo() {
        SNBLEHelper.addCommunicationListener(new NotifyReceiverListener() {
            @Override
            public void onReceive(byte[] buffer) {
                if (SNBLEHelper.startWith(buffer, "050114")) {
                    SNBLEHelper.removeCommunicationListener(this);
                    final int screenWidth = SNBLEHelper.subBytesToInt(buffer, 2, 3, 4);
                    final int screenHeight = SNBLEHelper.subBytesToInt(buffer, 2, 5, 6);
                    final boolean isSupport = (buffer[7] & 0xFF) == 0x01;
                    final boolean isEnable = (buffer[8] & 0xFF) == 0x01;
                    final boolean isTimeEnable = (buffer[9] & 0xFF) == 0x01;
                    final boolean isStepEnable = (buffer[10] & 0xFF) == 0x01;
                    final boolean[] isDefaultWallpaper = {true};
                    SNBLEHelper.addCommunicationListener(new NotifyReceiverListener() {
                        @Override
                        public void onReceive(final byte[] buffer) {
                            if (SNBLEHelper.startWith(buffer, "050115")) {
                                SNBLEHelper.removeCommunicationListener(this);
                                SNAsyncTask.execute(new SNVTaskCallBack() {
                                    private Bitmap wallpaperSrc;
                                    private final List<Point> fontList = new ArrayList<>();
                                    private PointF timeLocation;
                                    private int fontColor;
                                    private int screenType;

                                    @Override
                                    public void run() throws Throwable {
                                        fontList.clear();
                                        int fontCount = buffer[3] & 0xFF;

                                        for (int i = 4; i < (buffer.length - 4) / fontCount; i += 2) {
                                            int x = buffer[i] & 0xFF;
                                            int y = buffer[i + 1] & 0xFF;
                                            fontList.add(new Point(x, y));
                                        }
                                        try {
                                            wallpaperSrc = BitmapFactory.decodeFile(mImagePath);
                                            isDefaultWallpaper[0] = false;
                                        } catch (Exception ignored) {
                                        }

                                        DeviceInfo deviceInfo = DeviceType.getCurrentDeviceInfo();

                                        @DrawableRes int defaultWallpaper = R.mipmap.icon_wallpaper_screen;
                                        if (deviceInfo != null) {
                                            screenType = deviceInfo.getScreenType();
                                            //TODO 目前这里先写死,不做服务器存储,问过王腾了
                                            switch (deviceInfo.getAdv_id()) {
                                                case 0xE91D://IT106S 银河
                                                case 0xE11D://IT106S 银河
                                                    defaultWallpaper = R.mipmap.icon_wallpaper_screen_it106s_e11d;
                                                    break;
                                                case 0xB21D://IT118 花瓣
                                                case 0xEC1D://IT118 花瓣
                                                    defaultWallpaper = R.mipmap.icon_wallpaper_screen_it118_b21d;
                                                    break;
                                            }
                                        }

                                        if (wallpaperSrc == null || wallpaperSrc.isRecycled() || (wallpaperSrc.getWidth() + wallpaperSrc.getHeight() == 0)) {
                                            wallpaperSrc = BitmapFactory.decodeResource(ResUtil.getResources(), defaultWallpaper);
                                            isDefaultWallpaper[0] = true;
                                        }
                                        timeLocation = WallpaperInfoStorage.getTimeLocation(DeviceType.getDeviceMac());
                                        fontColor = WallpaperInfoStorage.getFontColor(DeviceType.getDeviceMac());


                                    }

                                    @Override
                                    public void done() {
                                        super.done();
                                        if (isUIEnable()) {
                                            onDialogDismiss();
                                            getView().onLoadWallpaperInfo(isSupport, isEnable, screenWidth, screenHeight, screenType, isTimeEnable, isStepEnable, fontList, wallpaperSrc, isDefaultWallpaper[0], timeLocation, fontColor);
                                            if (!isSupport) {
                                                getView().onLoadWallpaperFailed();
                                            }
                                        }
                                    }

                                    @Override

                                    public void error(Throwable e) {
                                        super.error(e);
                                        e.printStackTrace();
                                        if (isUIEnable()) {
                                            onDialogDismiss();
                                            getView().onLoadWallpaperFailed();
                                        }
                                    }
                                });
                            }
                        }
                    });
                    SNBLEHelper.sendCMD(SNCMD.get().getWallpaperFontInfo());
                }
            }
        });
        SNBLEHelper.sendCMD(SNCMD.get().getWallpaperScreenInfo());
    }

    private void onDialogDismiss() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                getView().onDialogDismiss();
            }
        });
    }

    @Override
    public void onProgress(final int max, final int progress, final int total, final int current) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isUIEnable()) {
                    getView().onUpdateWallpaperProgressChanged(current, total);
                }
            }
        });
    }

    @Override
    public void onSuccess(long spendTime) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isUIEnable()) {
                    getView().onDialogLoading(ResUtil.getString(R.string.loading));
                }
                SNBLEHelper.addCommunicationListener(new NotifyReceiverListener() {
                    @Override
                    public void onReceive(byte[] buffer) {
                        if (SNBLEHelper.startWith(buffer, "05021101")) {
                            SNBLEHelper.removeCommunicationListener(this);
                            SNAsyncTask.execute(new SNVTaskCallBack() {
                                @Override
                                public void run() throws Throwable {
                                    saveWallpaperSrc();
                                }

                                @Override
                                public void error(Throwable e) {
                                    super.error(e);
                                    onDialogDismiss();
                                    onUpdateWallpaperSuccessful();
                                }

                                @Override
                                public void done() {
                                    super.done();
                                    onDialogDismiss();
                                    onUpdateWallpaperSuccessful();
                                }
                            });

                        } else if (SNBLEHelper.startWith(buffer, "05021100")) {
                            SNBLEHelper.removeCommunicationListener(this);
                            onDialogDismiss();
                            onUpdateWallpaperSuccessful();
                        }
                    }
                });
                SNBLEHelper.sendCMD(SNCMD.get().setHighSpeedTransportStatus(false));

            }
        });

    }

    private void saveWallpaperSrc() throws FileNotFoundException {
        if (wallpaperSrc != null && !wallpaperSrc.isRecycled()) {
            Bitmap copy = wallpaperSrc.copy(Bitmap.Config.ARGB_8888, false);
            copy.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(mImagePath));
        }
    }

    @Override
    public void onFailed(final Exception e) {
        SNBLEHelper.abortSendWallpaper();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isUIEnable()) {
                    getView().onUpdateWallpaperFailed(e.getMessage());
                }
            }
        });
    }

    private void onUpdateWallpaperSuccessful() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isUIEnable()) {
                    getView().onUpdateWallpaperSuccessful();
                }
            }
        });
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        SNEventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SNEventBus.unregister(this);
        SNBLEHelper.abortSendWallpaper();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(SNEvent event) {
        if (!isUIEnable()) {
            return;
        }
        switch (event.getCode()) {
            case SNBLEEvent.EVENT_BLE_STATUS_BLUETOOTH_OFF:
            case SNBLEEvent.EVENT_BLE_STATUS_DISCONNECTED:
            case SNBLEEvent.EVENT_BLE_STATUS_CONNECT_FAILED:
                getView().onUpdateBleDisconnect();
                break;
        }
    }

}
