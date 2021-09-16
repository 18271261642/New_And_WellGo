package com.truescend.gofit.pagers.device.setting.feedback;

import android.content.Context;

/**
 * 作者:东芝(2018/2/3).
 * 描述:反馈
 */
public class IFeedbackContract {
    interface IView {
        void onSendFeedbackMessageError(String msg);

        void onSendFeedbackMessageSuccess();
        void onInputEmailError(String msg);
        void onInputContentError(String msg);
        void onInputDeviceNameError(String msg);

        void setInputEmail(String value);
        void setInputDeviceName(String value);
        void setPhoneName(String value);

        void onShowLoading(boolean isShow);

    }

    interface IPresenter {
        void requestSendFeedbackMessage(Context context, String email, String deviceName, String msg);

    }
}
