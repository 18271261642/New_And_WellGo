package com.sn.app.net.callback;

import com.sn.app.AppSDK;
import com.sn.app.R;
import com.sn.app.storage.UserStorage;
import com.sn.net.callback.OnBaseResponseListener;
import com.sn.utils.eventbus.SNEventBus;

import java.lang.reflect.Field;

/**
 * 作者:东芝(2017/7/31).
 * 功能:网络响应
 */

public abstract class OnResponseListener<T> extends OnBaseResponseListener<T> {

    private static final int ERROR_PHONE_REGISTERED = 10101;
    private static final int ERROR_USER_NOT_EXISTS = 10102;
    private static final int ERROR_PASSWORD_ERROR = 10103;
    private static final int ERROR_PHONE_INVALID = 10104;
    private static final int ERROR_SMS_TOO_FREQUENTLY = 10105;
    private static final int ERROR_CAPTCHA_EXPIRED = 10106;
    private static final int ERROR_CAPTCHA_WRONG = 10107;
    private static final int ERROR_CAPTCHA_FAILURE = 10108;
    private static final int ERROR_EMAIL_REGISTERED = 10109;
    private static final int ERROR_NOT_LOGIN = 401;
    public static final int EVENT_ERROR_NOT_LOGIN = 0x1000_401;

    public abstract void onResponse(T body) throws Throwable;

    public abstract void onFailure(int ret, String msg);


    @Override
    protected void onBaseResponse(T body) throws Throwable {

        if (body instanceof String) {
            try {
                onResponse(body);
            } catch (Throwable throwable) {
                onFailure(-3, insertErrorMessage(throwable.toString()));
            }
            return;
        }
        try {
            Field retField = body.getClass().getDeclaredField("ret");
            retField.setAccessible(true);

            Field messageField = body.getClass().getDeclaredField("message");
            messageField.setAccessible(true);

            int ret = retField.getInt(body);
            if (ret != 0) {
                String message = (String) messageField.get(body);
                int resId = 0;
                switch (ret) {
                    case ERROR_PHONE_REGISTERED:
                        resId = R.string.error_phone_registered;
                        break;
                    case ERROR_USER_NOT_EXISTS:
                        resId = R.string.error_user_not_exists;
                        break;
                    case ERROR_PASSWORD_ERROR:
                        resId = R.string.error_password_error;
                        break;
                    case ERROR_PHONE_INVALID:
                        resId = R.string.error_phone_invalid;
                        break;
                    case ERROR_SMS_TOO_FREQUENTLY:
                        resId = R.string.error_sms_too_frequently;
                        break;
                    case ERROR_CAPTCHA_EXPIRED:
                        resId = R.string.error_captcha_expired;
                        break;
                    case ERROR_CAPTCHA_WRONG:
                        resId = R.string.error_captcha_wrong;
                        break;
                    case ERROR_CAPTCHA_FAILURE:
                        resId = R.string.error_captcha_failure;
                        break;
                    case ERROR_EMAIL_REGISTERED:
                        resId = R.string.error_email_registered;
                        break;
                }
                if (resId != 0) {
                    message = AppSDK.getContext().getString(resId);
                }
                onFailure(ret, message);
//                if (ret == ERROR_NOT_LOGIN) {
//                    UserStorage.setAccessToken(null);
//                    //发送退出登录 广播
//                    SNEventBus.sendEvent(EVENT_ERROR_NOT_LOGIN);
//                }
            } else {

                try {
                    onResponse(body);
                } catch (Throwable throwable) {
                    onFailure(-3, insertErrorMessage(throwable.toString()));
                }
            }
        } catch (Exception e) {
            try {
                onResponse(body);
            } catch (Throwable throwable) {
                onFailure(-3, insertErrorMessage(throwable.toString()));
            }

        }
    }

    @Override
    protected void onBaseFailure(int ret, String msg) {
        onFailure(ret,msg);
    }

    @Override
    protected String onBaseNetworkError(String msg) {
       return AppSDK.getContext().getString(R.string.network_err);
    }



}
