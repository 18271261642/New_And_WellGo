package com.truescend.gofit.pagers.start.resetpsw;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.ResetPswQuestionStatusBean;
import com.sn.app.net.data.base.DefResponseBean;
import com.sn.app.storage.UserStorage;
import com.sn.utils.LanguageUtil;
import com.sn.utils.math.MD5Util;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;


public class ResetPswPresenterImpl extends BasePresenter<IResetPswContract.IView> implements IResetPswContract.IPresenter {

    @Override
    public void requestReset(final String email, String answer1, String answer2, int indexFromQuestion1, int indexFromQuestion2, String password) {

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

        getView().onShowLoading(true);
        //把密码md5化 再进行请求服务器, IOS也是如此
        String md5Password = MD5Util.md5(password);
        AppNetReq.getApi().resetpwdByKeys(email, array.toString(), md5Password).enqueue(new OnResponseListener<ResetPswQuestionStatusBean>() {
            @Override
            public void onResponse(ResetPswQuestionStatusBean body) throws Throwable {
                if (body.getData().getIs_success() == 1) {
                    UserStorage.setAccessToken("");
                    UserStorage.setAccount(email);
                    getView().onShowLoading(false);
                    getView().onShowMessage(ResUtil.getString(R.string.content_resetpsw_success));
                    getView().updateResetStatue(true);
                } else {
                    getView().onShowLoading(false);
                    getView().onShowMessage(ResUtil.getString(R.string.content_resetpsw_wrong_answer));
                    getView().updateResetStatue(false);
                }
            }

            @Override
            public void onFailure(int ret, String msg) {
                getView().onShowLoading(false);
                getView().onShowMessage(msg);
                getView().updateResetStatue(false);
            }
        });

    }

    @Override
    public void requestReset(final String email, String authCode, String password) {
        getView().onShowLoading(true);
        //把密码md5化 再进行请求服务器, IOS也是如此
        String md5Password = MD5Util.md5(password);
        AppNetReq.getApi().resetpwd(email, authCode, md5Password).enqueue(new OnResponseListener<DefResponseBean>() {
            @Override
            public void onResponse(DefResponseBean body) throws Throwable {
                UserStorage.setAccessToken("");
                UserStorage.setAccount(email);
                getView().onShowLoading(false);
                getView().onShowMessage(ResUtil.getString(R.string.content_resetpsw_success));
                getView().updateResetStatue(true);
            }

            @Override
            public void onFailure(int ret, String msg) {
                getView().onShowLoading(false);
                getView().onShowMessage(msg);
                getView().updateResetStatue(false);
            }
        });
    }

    @Override
    public void requestAuthCode(String email) {
        String language = LanguageUtil.isZH() ? "cmn-Hans" : "en";
        Call<DefResponseBean> captcha = AppNetReq.getApi().captcha(email, 2, language);
        getView().onShowLoading(true);
        captcha.enqueue(new OnResponseListener<DefResponseBean>() {
            @Override
            public void onResponse(DefResponseBean body) {
                getView().onShowLoading(false);
                getView().updateGetAuthCodeStatue(true);
            }

            @Override
            public void onFailure(int ret, String msg) {
                getView().onShowLoading(false);
                getView().updateGetAuthCodeStatue(false);
            }
        });
    }

}
