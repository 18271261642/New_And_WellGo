package com.truescend.gofit.pagers.device.setting.feedback;

import android.content.Context;
import android.os.Build;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.storage.UserStorage;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.blesdk.storage.DeviceStorage;
import com.sn.utils.IF;
import com.sn.utils.RegexUtil;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.LogRecorder;
import com.truescend.gofit.utils.ResUtil;


/**
 * 作者:东芝(2018/2/3).
 * 描述:反馈
 */
public class FeedbackPresenter extends BasePresenter<IFeedbackContract.IView> implements IFeedbackContract.IPresenter {
    private IFeedbackContract.IView view;

    private int deviceVersion = -1;
    private int adv_id = -1;
    private int c_id = -1;

    public FeedbackPresenter(IFeedbackContract.IView view) {
        this.view = view;
        view.setPhoneName(Build.BRAND);

        DeviceInfo currentDeviceInfo = DeviceType.getCurrentDeviceInfo();
        deviceVersion = DeviceStorage.getDeviceVersion();
        String account = UserStorage.getAccount();
        String deviceName;
        if (currentDeviceInfo != null) {
            deviceName = currentDeviceInfo.getDevice_name();
            adv_id = currentDeviceInfo.getAdv_id();
            c_id = currentDeviceInfo.getCustomid();
        } else {
            deviceName = DeviceType.getDeviceName();
            adv_id = DeviceType.getDeviceAdvId();
            c_id = DeviceType.getDeviceCustomerId();
        }
        if (RegexUtil.isEmail(account)) {
            view.setInputEmail(account);
        }
        if (!IF.isEmpty(deviceName)) {
            view.setInputDeviceName(deviceName);
        }

    }

    @Override
    public void requestSendFeedbackMessage(final Context context, String email, String deviceName, String msg) {

        if (!RegexUtil.isEmail(email)) {
            view.onInputEmailError(ResUtil.getString(R.string.content_input_email));
            return;
        }
        if (IF.isEmpty(deviceName)) {
            view.onInputDeviceNameError(ResUtil.getString(R.string.content_input_band));
            return;
        }
        if (IF.isEmpty(msg)) {
            view.onInputContentError(ResUtil.getString(R.string.content_feedback_content));
            return;
        }
        //反馈
        int userId = UserStorage.getUserId();
        String id = "(ID:" + userId + ")";
        String contact = email + id;
        String builder = "【"+contact+"】【" +
                deviceName + "|" +
                deviceVersion +   (BuildConfig.isGooglePlayVersion?"(play)":"") + "|" +
                adv_id + "|" +
                c_id +
                "】" +
                "【" +
                BuildConfig.VERSION_NAME + "|" +
                Build.MODEL + "|" +
                Build.VERSION.RELEASE +
                "】" +
                msg;




        view.onShowLoading(true);
//        AppNetReq.getApi().feedback(builder).enqueue(new OnResponseListener<String>() {
//            @Override
//            public void onResponse(String body) throws Throwable {
//                //反馈成功后上传日志
//                LogRecorder.uploadLog(context, UserStorage.getUserId(), 0/*今天的日志*/, new LogRecorder.OnLogcatUploadStatusListener() {
//                    @Override
//                    public void onSuccess() {
//                        view.onShowLoading(false);
//                        //上传成功
//                        view.onSendFeedbackMessageSuccess();
//                    }
//
//                    @Override
//                    public void onFailed() {
//                        view.onShowLoading(false);
//                        //上传失败 也让成功,不要让用户知道我在上传日志
//                        view.onSendFeedbackMessageSuccess();
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(int ret, String msg) {
//                view.onSendFeedbackMessageError(msg);
//                view.onShowLoading(false);
//            }
//        });


    }

}
