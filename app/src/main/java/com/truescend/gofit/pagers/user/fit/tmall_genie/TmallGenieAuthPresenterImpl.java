package com.truescend.gofit.pagers.user.fit.tmall_genie;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.TmallGenieAuthCodeBean;
import com.truescend.gofit.pagers.base.BasePresenter;

/**
 * 作者:东芝(2019/6/18).
 * 功能:天猫精灵授权
 */
public class TmallGenieAuthPresenterImpl extends BasePresenter<ITmallGenieAuthContract.IView> implements ITmallGenieAuthContract.IPresenter {
    private ITmallGenieAuthContract.IView view;


    public TmallGenieAuthPresenterImpl(ITmallGenieAuthContract.IView view) {
        this.view = view;
    }


    @Override
    public void requestGetUserCode() {
        AppNetReq.getApi().getTmallGenieAuthCode().enqueue(new OnResponseListener<TmallGenieAuthCodeBean>() {
            @Override
            public void onResponse(TmallGenieAuthCodeBean body) throws Throwable {
                TmallGenieAuthCodeBean.DataBean data = body.getData();
                if (data !=null&&data.getAuth_code()!=null) {
                    String auth_code = data.getAuth_code();
                    if(isUIEnable()) {
                        getView().onUpdateUserCode(auth_code);
                    }
                }else{
                    if(isUIEnable()) {
                        getView().onUpdateUserCode("Error: auth_code=null");
                    }
                }
            }

            @Override
            public void onFailure(int ret, String msg) {
                if(isUIEnable()) {
                    getView().onUpdateUserCode(msg);
                }
            }
        });
    }
}
