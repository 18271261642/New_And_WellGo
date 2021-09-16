package com.truescend.gofit.pagers.start.register;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.SignBean;
import com.sn.app.storage.UserStorage;
import com.sn.utils.math.MD5Util;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者:东芝(2018/06/13).
 * 功能:新注册界面
 */
public class RegisterPresenterImpl extends BasePresenter<IRegisterContract.IView> implements IRegisterContract.IPresenter {
    private IRegisterContract.IView view;


    public RegisterPresenterImpl(IRegisterContract.IView view) {
        this.view = view;
    }

    @Override
    public void requestRegister(final String email, String psw, String answer1, String answer2, int indexFromQuestion1, int indexFromQuestion2) {
        JSONArray array = new JSONArray();
        try {
            //问题1
            JSONObject question1 = new JSONObject();
            question1.put("question", String.valueOf(indexFromQuestion1));
            question1.put("answer", answer1);

            //问题2
            JSONObject question2 = new JSONObject();
            question2.put("question", String.valueOf(indexFromQuestion2));
            question2.put("answer", answer2);

            array.put(question1);
            array.put(question2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        view.onShowLoading(true);

        //把密码md5化 再进行请求服务器, IOS也是如此
        String md5Password = MD5Util.md5(psw);

        AppNetReq.getApi().signup2(email, md5Password, array.toString()).enqueue(new OnResponseListener<SignBean>() {
            @Override
            public void onResponse(SignBean body) throws Throwable {
                if(!isUIEnable())return;
                view.onShowLoading(false);
                SignBean.DataBean data = body.getData();
                UserStorage.setAccessToken(data.getAccess_token());
                UserStorage.setAccount(email);
                view.updateRegisterStatue(true, ResUtil.getString(R.string.content_register_success));
            }

            @Override
            public void onFailure(int ret, String msg) {
                if(!isUIEnable())return;
                view.onShowLoading(false);
                view.updateRegisterStatue(false,msg);
            }
        });
    }

}
