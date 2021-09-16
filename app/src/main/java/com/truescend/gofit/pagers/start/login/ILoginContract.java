package com.truescend.gofit.pagers.start.login;

/**
 * 作者:东芝(2018/1/17).
 * 功能:登录
 */

public class ILoginContract {

    public interface IView {
        /**
         * 登陆失败
         * @param errMsg 错误信息
         */
        void onLoginFailed(String errMsg);

        /**
         * 登录成功
         * @param isNewUser 是否新用户(用于决定要不要跳转到用户信息填写界面)
         */
        void onLoginSuccess(boolean isNewUser);
    }

    interface IPresenter
    {
        /**
         * 读取用户信息
         */
        void requestLoadUserMessage();

        /**
         * 登录(手机号/邮箱)
         * @param account
         * @param password
         */
        void requestLogin(String account, String password);

        /**
         * 第三方登录(包括游客登录)
         * @param openId
         * @param openTypeName
         */
        void requestLoginOther(String openId, String openTypeName);
    }
}
