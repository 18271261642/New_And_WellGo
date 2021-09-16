package com.truescend.gofit.pagers.start.resetpsw.checker;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.GetQuestionsBean;
import com.truescend.gofit.pagers.base.BasePresenter;

public class ResetPswCheckerPresenterImpl extends BasePresenter<IResetPswCheckerContract.IView> implements IResetPswCheckerContract.IPresenter {
    private IResetPswCheckerContract.IView view;


    public ResetPswCheckerPresenterImpl(IResetPswCheckerContract.IView view) {
        this.view = view;
    }


    @Override
    public void requestHasSetQuestionsStatus(final String email) {
        view.onShowLoading(true);
        AppNetReq.getApi().getKeyQuestions(email).enqueue(new OnResponseListener<GetQuestionsBean>() {
            @Override
            public void onResponse(GetQuestionsBean body) throws Throwable {
                view.onShowLoading(false);
                view.onShowMessage(body.getMessage());
                GetQuestionsBean.DataBean data = body.getData();
                view.onHasSetQuestionsStatus(data.getIs_set()==1,email, data);
            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onShowLoading(false);
                view.onShowMessage(msg);
            }
        });
    }
}
