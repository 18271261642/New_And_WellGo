package com.truescend.gofit.pagers.start.login;

import android.text.TextUtils;

import com.sn.app.BuildConfig;
import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.AppApiService;
import com.sn.app.net.data.app.bean.SignBean;
import com.sn.app.net.data.app.bean.UserDeviceBean;
import com.sn.app.net.data.app.bean.UserMessageBean;
import com.sn.app.net.data.base.DefResponseBean;
import com.sn.app.storage.UserStorage;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.storage.DeviceStorage;
import com.sn.utils.RegexUtil;
import com.sn.utils.SystemUtil;
import com.sn.utils.math.MD5Util;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;

import retrofit2.Call;

/**
 * 作者:东芝(2017/11/16).
 * 功能:登录
 */
public class LoginPresenterImpl extends BasePresenter<ILoginContract.IView> implements ILoginContract.IPresenter {


    private final AppApiService service;
    private ILoginContract.IView view;
    private Call<SignBean> login = null;
    private boolean isNewUser;

    public LoginPresenterImpl(ILoginContract.IView view) {
        this.view = view;
        service = AppNetReq.getApi();
    }

    @Override
    public void requestLoadUserMessage() {

        service.queryUser(SystemUtil.getUniqueId(ResUtil.getContext())).enqueue(new OnResponseListener<UserMessageBean>() {
            @Override
            public void onResponse(UserMessageBean body) throws Throwable {
                //用户数据初始化
                final UserMessageBean.DataBean data = body.getData();
                if (data.getApp_id()==0) {
                    AppNetReq.getApi().setAppId().enqueue(new OnResponseListener<DefResponseBean>() {
                        @Override
                        public void onResponse(DefResponseBean body) throws Throwable {
                            if (body.isSuccessful()) {
                                data.setApp_id(BuildConfig.APP_ID);
                            }
                            next(data);
                        }

                        @Override
                        public void onFailure(int ret, String msg) {
                            next(data);
                        }
                    });
                }else{
                    next(data);
                }



            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onLoginFailed(msg);
            }
        });
    }

    private void next(UserMessageBean.DataBean data)  {
        try {
            AppUserUtil.initialize(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserDeviceBean.DataBean device = data.getLast_device();
        if(device !=null&& !TextUtils.isEmpty(device.getMac())) {
            DeviceStorage.setDeviceMac(device.getMac());
            DeviceStorage.setDeviceName(device.getDevice_name());
            DeviceStorage.setDeviceAdvId(Integer.parseInt(device.getAdv_id()));
            //这里无法拿到,因为服务器没存这个,不过app会在同步数据时 会取得CustomerId设置进去这里,所以不用担心
//                    DeviceStorage.setDeviceCustomerI( );
            //取消 [开发主动断开]  否则不会重连!
            SNBLEHelper.setIsUserDisconnected(false);
            //启用[自动重连]
            SNBLEHelper.setAutoReConnect(true);
        }
        view.onLoginSuccess(isNewUser);
    }

    @Override
    public void requestLogin(final String account, final String password) {

        //把密码md5化 再进行请求服务器, IOS也是如此
        String md5Password = MD5Util.md5(password);

        if (RegexUtil.isEmail(account)) {//如果是邮箱
            login = service.loginFromEmail(account, md5Password);
        } else if (RegexUtil.isPhoneNumber(account)) {//如果是手机号
            //TODO 这里是写死+86 在国外可能无法注册 到时候问问服务器
            login = service.loginFromPhoneNumber("86", account, md5Password);
        }
        //保存账号下次打开登录界面 自动输入
        UserStorage.setAccount(account);

        if (login != null) {
            login.enqueue(new OnResponseListener<SignBean>() {
                @Override
                public void onResponse(SignBean body) {
                    SignBean.DataBean data = body.getData();
                    //存储token
                    UserStorage.setAccessToken(data.getAccess_token());
                    //保存密码 下次打开登录界面 自动输入
                    UserStorage.setPassword(password);
                    //是否新用户
                    isNewUser = data.isFirst();
                    UserStorage.setIsFirst(isNewUser);

                    UserStorage.setTmpUserId(data.getId());
                    //加载用户信息
                    requestLoadUserMessage();

                }

                @Override
                public void onFailure(int ret, String msg) {
                    view.onLoginFailed(msg);
                }
            });
        }

    }

    @Override
    public void requestLoginOther(String openId, String openTypeName) {
        login = service.loginFromOther(openId, openTypeName);
        if (login != null) {
            login.enqueue(new OnResponseListener<SignBean>() {
                @Override
                public void onResponse(SignBean body) {
                    SignBean.DataBean data = body.getData();
                    //存储token
                    UserStorage.setAccessToken(data.getAccess_token());
                    //是否新用户
                    isNewUser = data.isFirst();
                    UserStorage.setIsFirst(isNewUser);
                    //加载用户信息
                    requestLoadUserMessage();
                }

                @Override
                public void onFailure(int ret, String msg) {
                    view.onLoginFailed(msg);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (login != null) {
            login.cancel();
        }
    }
}
