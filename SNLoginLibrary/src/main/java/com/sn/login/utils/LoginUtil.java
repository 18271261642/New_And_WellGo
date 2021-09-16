package com.sn.login.utils;

//import android.os.Handler;
//import android.os.Message;
//
//import com.mob.tools.utils.UIHandler;
//
//import java.util.HashMap;

//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.tencent.qq.QQ;
//import cn.sharesdk.twitter.Twitter;
//import cn.sharesdk.wechat.friends.Wechat;

/**
 * 第三方登录工具
 * Author:Created by 泽鑫 on 2018/4/6 18:21.
 */

public class LoginUtil {
//    private static final int MSG_USERID_FOUND = 1;
//    private static final int MSG_LOGIN = 2;
//    private static final int MSG_AUTH_CANCEL = 3;
//    private static final int MSG_AUTH_ERROR = 4;
//    private static final int MSG_AUTH_COMPLETE = 5;
//    public static final String PLATFORM_QQ = QQ.NAME;
//    public static final String PLATFORM_WECHAT = Wechat.NAME;
//    public static final String PLATFORM_TWITTER = Twitter.NAME;
////    public static final String PLATFORM_INSTAGRAM = Instagram.NAME;
//
//    private static RequestOtherSignInCallback loginThirdCallback;
//
//    public static void authorize(String name, RequestOtherSignInCallback loginCallback) {
//        loginThirdCallback = loginCallback;
//        Platform plat = ShareSDK.getPlatform(name);
//        if (plat == null) {
//            return;
//        }
//        if (plat.isAuthValid()) {
//            plat.removeAccount(true);
//        }
//        //判断指定平台是否已经完成授权
//        if (plat.isAuthValid()) {
//            String userId = plat.getDb().getUserId();
//            if (userId != null) {
//                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, callback);
////                plat.getDb().get("openid");
//                login(plat.getName(), userId, null);
//                return;
//            }
//        }
//
//        plat.setPlatformActionListener(mPlatformActionListener);
//        // true不使用SSO授权，false使用SSO授权
////        通俗点讲SSO就是调用微信客户端进行登录授权（前提是：手机端必须安装微信客户端）
////        非SSO就是通过网页的方式请求授权（可以不用安装微信客户端哦）
//        plat.SSOSetting(false);
//        //获取用户资料
//        plat.showUser(null);
//    }
//
//    private static void login(String plat, String userId, HashMap<String, Object> userInfo) {
//        Message msg = new Message();
//        msg.what = MSG_LOGIN;
//        msg.obj = plat;
//        UIHandler.sendMessage(msg, callback);
//    }
//
//    private static Handler.Callback callback = new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_USERID_FOUND: {
//                    System.out.println("--------MSG_USERID_FOUND-------");
//                }
//                break;
//                case MSG_LOGIN: {
//                    System.out.println("--------MSG_LOGIN-------");
//                }
//                break;
//                case MSG_AUTH_CANCEL: {
//                    System.out.println("-------MSG_AUTH_CANCEL--------");
//                }
//                break;
//                case MSG_AUTH_ERROR: {
//                    System.out.println("-------MSG_AUTH_ERROR--------");
//                }
//                break;
//                case MSG_AUTH_COMPLETE: {
//                    System.out.println("--------MSG_AUTH_COMPLETE-------");
//                }
//                break;
//
//            }
//            return false;
//        }
//    };
//
//
//    private static PlatformActionListener mPlatformActionListener = new PlatformActionListener() {
//        public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
//            if (action == Platform.ACTION_USER_INFOR) {
//                UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, callback);
//                login(platform.getName(), platform.getDb().getUserId(), res);
//                if (loginThirdCallback != null) {
//                    loginThirdCallback.authorizedSuccess(platform.getName(), platform.getDb().getUserId());
//                }
//            }
//
//        }
//
//
//        public void onError(Platform platform, int action, Throwable t) {
//            if (action == Platform.ACTION_USER_INFOR) {
//                UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, callback);
//            }
//            t.printStackTrace();
//            if (loginThirdCallback != null) {
//                loginThirdCallback.authorizedFailed(t.getMessage());
//            }
//        }
//
//        public void onCancel(Platform platform, int action) {
//            if (action == Platform.ACTION_USER_INFOR) {
//                UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, callback);
//            }
//            if (loginThirdCallback != null) {
//                loginThirdCallback.authorizedFailed("");
//            }
//        }
//
//    };
//
//
//    public interface RequestOtherSignInCallback {
//        void authorizedSuccess(String platform, String userId);
//
//        void authorizedFailed(String errorMsg);
//    }

}
