package com.truescend.gofit.pagers.device.setting.update;

import android.content.Context;
import android.text.TextUtils;

import com.dz.bleota.base.OTA;
import com.dz.bleota.dialog.Dialog;
import com.dz.bleota.nrf.nRF;
import com.dz.bleota.phy.Phy;
import com.dz.bleota.syd.SYD;
import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.HWVersionBean;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.blesdk.storage.DeviceStorage;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.net.comm.builder.FIleDownloadBuilder;
import com.sn.utils.IF;
import com.sn.utils.SNLog;
import com.sn.utils.eventbus.SNEvent;
import com.sn.utils.eventbus.SNEventBus;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.Constant;
import com.truescend.gofit.utils.ResUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;

/**
 * 作者:东芝(2018/3/7).
 * 功能:设备版本获取/空中升级
 */

public class BandUpdatePresenterImpl extends BasePresenter<IBandUpdateContract.IView> implements IBandUpdateContract.IPresenter, OTA.OnOTAUpdateStatusChangeListener {
    private String deviceMac;
    private boolean isFixBand;
    private IBandUpdateContract.IView view;
    private int newVersion = -1;
    private int localVersion = -1;
    private OTA ota;
    private DeviceInfo currentDeviceInfo;
    private String download_url;
    private File mOTAFile;
    private WeakReference<Context> contextWeakReference;
    private int upgradeid = -1;//升级ID  仅来自设备
    private String originMac;
    private boolean isAcquireMac;
    private int lastStatus = OTA.STATUS_PREPARE;

    public BandUpdatePresenterImpl(IBandUpdateContract.IView view) {
        this.view = view;

    }


    @Override
    public void requestCheckVersionAndUpdate(Context context, DeviceInfo into, final String mac) {
        contextWeakReference = new WeakReference<>(context.getApplicationContext());
        this.currentDeviceInfo = into;
        this.deviceMac = mac;
        originMac = null;
        isAcquireMac = false;
        if (currentDeviceInfo != null) {
            if (currentDeviceInfo.isnRF()) {
                ota = new nRF();
            } else if (currentDeviceInfo.isSYD8801()) {
                ota = new SYD();
            } else if (currentDeviceInfo.isDialog()) {
                ota = new Dialog();
            } else if (currentDeviceInfo.isPhy()) {
                ota = new Phy();
            }
        }
        if (ota == null || currentDeviceInfo == null) {
            view.onFailed(true, ResUtil.getString(R.string.content_upgrade_no_plan));
            return;
        }
        if (upgradeid == -1) {
            upgradeid = DeviceStorage.getDeviceUpgradeId();
        }
        if (isFixBand) {
            //救砖模式,不需要已连接 不需要获取本地版本
            view.onDialogLoading(ResUtil.getString(R.string.content_getting_information));
            requestNetworkDeviceVersion(currentDeviceInfo.getAdv_id(), currentDeviceInfo.getCustomid(), upgradeid);

        } else {
            if (SNBLEHelper.isDisconnected()) {
                view.onFailed(true, ResUtil.getString(R.string.content_disconnect_band));
                return;
            }

            view.onDialogLoading(ResUtil.getString(R.string.content_getting_information));
            requestLocalDeviceVersion();
            //注意这里有连续三次的网络请求... 董睿说没法做到app一次上传, 原因不知道,所以只能嵌套上传
            //再次上传全部手环数据,确保手环如果成砖后有数据可查询并救砖
            AppUserUtil.uploadUserDevice(mac, into.getDevice_name(), into.getFunction(), into.getAdv_id(), into.getCustomid(), upgradeid, new AppUserUtil.OnOperationListener() {
                @Override
                public void success() {
                    if(!TextUtils.isEmpty(originMac))
                    {
                        //上传原生mac 到救砖重要数据中去
                        AppUserUtil.uploadUserDeviceOriginMac(mac, originMac, new AppUserUtil.OnOperationListener() {
                            @Override
                            public void success() {
                                //查询版本并准备ota
                                requestNetworkDeviceVersion(currentDeviceInfo.getAdv_id(), currentDeviceInfo.getCustomid(), upgradeid);
                                //记录连接历史 方便救砖用
                                DeviceStorage.addDeviceInfoConnectHistory(DeviceStorage.getDeviceName(), mac, upgradeid, currentDeviceInfo);
                            }
                            @Override
                            public void failed(String msg) {
                                view.onFailed(true, msg);
                                view.onDialogDismiss();
                            }
                        });
                    }else{//大部分手环没有原生mac
                        //查询版本并准备ota
                        requestNetworkDeviceVersion(currentDeviceInfo.getAdv_id(), currentDeviceInfo.getCustomid(), upgradeid);
                    }

                }

                @Override
                public void failed(String msg) {
                    view.onFailed(true, msg);
                    view.onDialogDismiss();
                }
            });


        }
    }

    @Override
    public void requestFixDeviceUpdate(Context context, DeviceInfo into, String mac, int upgradeid) {
        this.upgradeid = upgradeid;
        this.isFixBand = true;
        requestCheckVersionAndUpdate(context, into, mac);
    }

    @Override
    public void requestStartOTA() {

        if (download_url == null || ota == null || currentDeviceInfo == null || IF.isEmpty(download_url)) {
            view.onFailed(true, ResUtil.getString(R.string.content_upgrade_no_plan));
            return;
        }
        String fileSuffix = download_url.substring(download_url.lastIndexOf(".") + 1, download_url.length());
        String fileName = ResUtil.format("version_%s(%04X)_cid%d_upid%d_v%d_file.%s", currentDeviceInfo.getDevice_name(), currentDeviceInfo.getAdv_id(), currentDeviceInfo.getCustomid(), upgradeid, newVersion, fileSuffix);
        mOTAFile = new File(Constant.Path.CACHE_OTA_DOWNLOAD, fileName);

        if (checkOTAFile()) {
            step1ToSendOTACommand();
        } else {
            requestDownloadOTAFile(download_url);
        }
    }

    /**
     * 校验OTA文件合法性
     *
     * @return
     */
    private boolean checkOTAFile() {
        //文件存在&&文件大小正确&&可读
        return mOTAFile.exists() && mOTAFile.length() > 0 && mOTAFile.canRead();
    }

    /**
     * 第一步: 发送OTA命令
     */
    private void step1ToSendOTACommand() {

        //下面涉及到一些批量的线程睡眠 于是使用SNAsyncTask来执行以下操作
        SNAsyncTask.execute(new SNVTaskCallBack() {

            private final int WHAT_SEND_CMD_ERR = 1100;
            private final int WHAT_START_OTA = 1101;

            @Override
            public void prepare() {
                onStatusChanged(OTA.STATUS_PREPARE);
                //关闭本框架的自动重连,让OTA框架接管
                SNBLEHelper.setAutoReConnect(false);
            }

            @Override
            public void run() throws Throwable {
                String otaMac = deviceMac;
                if(!TextUtils.isEmpty(originMac)){
                    otaMac = originMac;
                }


                //如果是nRF蓝牙模块 则需要通过命令来启动dfu模式
                if (ota instanceof nRF) {

                    if (!isFixBand/*不是DFU模式则需要通过命令启动DFU模式*/) {
                        SNLog.i("发送升级命令");
                        boolean sendDFUCommand = SNBLEHelper.sendCMD(SNCMD.get().setDeviceAirUpdate());
                        if (!sendDFUCommand) {//如果发送命令失败了
                            publishToMainThread(WHAT_SEND_CMD_ERR);
                            return;
                        } else {
                            SNLog.i("发送升级命令成功!");
                            SNLog.i("休息1.5秒 让设备重启到DFU升级模式!");
                            //休息1.5秒 让设备重启到DFU升级模式
                            Thread.sleep(1500);
                            SNLog.i("断开当前连接的设备,关闭本框架的自动重连,让OTA框架接管蓝牙部分");
                            //断开当前连接的设备,关闭本框架的自动重连,让OTA框架接管蓝牙部分
                            SNBLEHelper.disconnect();
                            SNLog.i("为了兼容各种手环和手机,休息2-3秒 让手环在断开后再广播");
                            //为了兼容各种手环和手机,休息2-3秒 让手环在断开后再广播
                            Thread.sleep(2000);
                            SNLog.i("转换mac地址为nRF芯片按规律生成的新地址,然后才能进行OTA升级");
                        }
                    }
                    //转换mac地址为nRF芯片按规律生成的新地址,然后才能进行OTA升级
                    String nRFDeviceMac = nRF.convertToDFUMacAddress(otaMac);
                    publishToMainThread(WHAT_START_OTA, nRFDeviceMac);
                } else if (isFixBand && ota instanceof Phy) {
                    //奉加微在救砖模式也需要增加mac地址最后一位
                    String phyDeviceMac = Phy.convertToDFUMacAddress(otaMac);
                    publishToMainThread(WHAT_START_OTA, phyDeviceMac);

                }
                //如果是其他蓝牙模块 目前没有其他特殊要求 直接使用OTA工具进行升级即可
                else {
                    //断开当前连接的设备,关闭本框架的自动重连,让OTA框架接管蓝牙部分
                    SNLog.i("断开当前连接的设备");
                    SNBLEHelper.disconnect();
                    SNLog.i("为了兼容各种手环和手机,休息2-3秒 让手环在断开后再广播");
                    //为了兼容各种手环和手机,休息2-3秒 让手环在断开后再广播
                    Thread.sleep(2000);
                    SNLog.i("publishToMainThread");
                    //开始OTA
                    publishToMainThread(WHAT_START_OTA, otaMac);
                }
            }

            @Override
            public void main(int what, Object... obj) {
                switch (what) {
                    case WHAT_START_OTA:
                        //开始OTA
                        step2ToStartOTA((String) obj[0]);
                        break;
                    case WHAT_SEND_CMD_ERR:
                        onStatusChanged(OTA.STATUS_PREPARE_TIME_OUT);
                        break;
                }
            }

            @Override
            public void done() {

            }

            @Override
            public void error(Throwable e) {
                onStatusChanged(OTA.STATUS_FAILED);
                SNLog.i("升级过程发生异常" + e);
            }
        });


    }

    /**
     * 第二步: 开始OTA
     *
     * @param mDeviceMac
     */
    private void step2ToStartOTA(String mDeviceMac) {
        ota.startOTA(contextWeakReference.get(), true, mDeviceMac, mOTAFile.getAbsolutePath(), this);
    }

    private void requestDownloadOTAFile(String download_url) {
        if (download_url.startsWith("http")) {
            //后台 的坑!!!  把https转http
            if (download_url.startsWith("https")) {
                download_url = download_url.replace("https", "http");
            }
            view.onDialogLoading(ResUtil.getString(R.string.content_downloading));

            //下载固件包
            AppNetReq.getApi().downloadFile(download_url).enqueue(new OnResponseListener<ResponseBody>() {
                @Override
                public void onResponse(final ResponseBody body) throws Throwable {
                    //开启任务,在子线程中处理
                    SNAsyncTask.execute(new SNVTaskCallBack() {
                        @Override
                        public void run() throws Throwable {
                            FIleDownloadBuilder.createFile(body, mOTAFile.getAbsolutePath());
                        }

                        @Override
                        public void done() {
                            view.onDialogDismiss();
                            if (checkOTAFile()) {
                                //开始OTA
                                step1ToSendOTACommand();
                            } else {
                                view.onFailed(false, ResUtil.getString(R.string.content_download_failed));
                            }
                        }
                    });
                }

                @Override
                public void onFailure(int ret, String msg) {
                    view.onFailed(false, ResUtil.getString(R.string.content_download_failed));
                    view.onDialogDismiss();
                }
            });
        }
    }

    @Override
    public void requestAbortOTA() {
        //终止升级
        if (ota != null) {
            ota.abortOTA();
        }
    }

    /**
     * 根据广播id和客户id获取 固件版本
     *
     * @param adv_id
     * @param custom_id
     * @param upgradeid
     */
    private void requestNetworkDeviceVersion(int adv_id, int custom_id, int upgradeid) {



        AppNetReq.getApi().checkHWVersion2(adv_id, custom_id, upgradeid).enqueue(new OnResponseListener<HWVersionBean>() {
            @Override
            public void onResponse(HWVersionBean body) throws Throwable {
                HWVersionBean.DataBean data = body.getData();
                newVersion = data.getVersion();
                download_url = data.getDownload_url();

                //救砖模式 直接升级
                if (isFixBand) {
                    requestStartOTA();
                    view.onDialogDismiss();
                } else {
                    //取得版本号和固件下载链接后, 回调到界面
                    onCallBackDeviceVersion();
                }

            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onFailed(true, msg);
                view.onDialogDismiss();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(SNEvent event) {
        switch (event.getCode()) {
            //监听设备信息
            case SNBLEEvent.EVENT_DEVICE_INFO_0:
                Object dataInfo = event.getData();
                if (dataInfo instanceof int[]) {
                    upgradeid = ((int[]) dataInfo)[0];
                    localVersion = ((int[]) dataInfo)[1];
                    onCallBackDeviceVersion();
                }
                break;

            case SNBLEEvent.EVENT_DEVICE_INFO_MAC:
                Object dataInfo2 = event.getData();
                if (dataInfo2 instanceof String[]) {
                    String[] info2 = (String[]) dataInfo2;
                    if (info2.length > 0) {
                        isAcquireMac = true;
                        String mac = info2[0];
                        if (deviceMac != null && deviceMac.equalsIgnoreCase(mac)) {
                            originMac = info2[1];
                        }
                        onCallBackDeviceVersion();
                    }
                }
                break;
            case SNBLEEvent.EVENT_BLE_STATUS_DISCONNECTED:
                if (lastStatus == OTA.STATUS_UPDATING) {
                    view.onOTAFailed(ResUtil.getString(R.string.content_upgrade_failed));
                }

                break;

            default:
                return;
        }

    }

    @Override
    protected void onCreate() {
        super.onCreate();
        SNEventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        view.onDialogDismiss();
        super.onDestroy();

        SNEventBus.unregister(this);
        if (contextWeakReference != null) {
            contextWeakReference.clear();
        }
        //注销固件升级工具
        if (ota != null) {
            ota.destroy();
        }
    }

    /**
     * 请求获取设备版本,将会通过onEventReceived接收结果
     */
    private void requestLocalDeviceVersion() {
        SNBLEHelper.sendCMD(SNCMD.get().getDeviceInfoCmd0());
        SNBLEHelper.sendCMD(SNCMD.get().getDeviceMacInfo());
    }


    private void onCallBackDeviceVersion() {

        //需要两个都成功获取 再回调到UI
        if (newVersion != -1 && localVersion != -1&&isAcquireMac) {
            view.onDeviceVersion(localVersion, newVersion);
            view.onDialogDismiss();

        }

    }

    @Override
    public void onStatusChanged(int status) {

        switch (status) {
            case OTA.STATUS_PREPARE://准备中
                view.onOTAStarted();
                view.onOTAProcessing(ResUtil.getString(R.string.content_preparing));
                break;
            case OTA.STATUS_PREPARE_TIME_OUT://准备超时
                view.onOTAFailed(ResUtil.getString(R.string.content_prepare_timeout));
                break;
            case OTA.STATUS_UPDATING://升级中
                view.onOTAProcessing(ResUtil.getString(R.string.content_during_upgrade));
                lastStatus = OTA.STATUS_UPDATING;
                break;
            case OTA.STATUS_SUCCESSFUL://升级成功
                view.onOTASuccessful();
                break;
            case OTA.STATUS_FAILED://升级失败
                lastStatus = OTA.STATUS_FAILED;
                view.onOTAFailed(ResUtil.getString(R.string.content_upgrade_failed));
                break;
            case OTA.STATUS_CANCEL://升级已取消
                view.onOTAFailed(ResUtil.getString(R.string.content_upgrade_cancel));
                break;
        }
    }

    @Override
    public void onProgressChanged(int cur, int total) {
        view.onOTAProgressChanged(cur, total);
    }
}
