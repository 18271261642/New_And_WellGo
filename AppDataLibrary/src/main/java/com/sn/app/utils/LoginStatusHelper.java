package com.sn.app.utils;

import com.sn.app.net.callback.OnResponseListener;
import com.sn.utils.eventbus.SNEvent;
import com.sn.utils.eventbus.SNEventBus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 作者:东芝(2018/3/29).
 * 功能:登录状态监听器
 */

public class LoginStatusHelper {

    private LoginStatusListener listener;

    public LoginStatusHelper(LoginStatusListener listener) {
        this.listener = listener;
    }

    public void register() {
        SNEventBus.register(this);
    }

    public void unregist() {
        SNEventBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(SNEvent event) {
        if (event == null || listener == null) return;
        switch (event.getCode()) {
            case OnResponseListener.EVENT_ERROR_NOT_LOGIN://未登录
                listener.onLogout();
                break;
        }

    }

    public interface LoginStatusListener {
        void onLogout();
    }
}
