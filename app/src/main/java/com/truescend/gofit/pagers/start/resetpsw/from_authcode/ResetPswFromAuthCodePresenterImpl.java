//package com.truescend.gofit.pagers.start.resetpsw.from_authcode;
//
//import com.sn.app.net.AppNetReq;
//import com.sn.app.net.callback.OnResponseListener;
//import com.sn.app.net.data.app.AppApiService;
//import com.sn.app.net.data.base.DefResponseBean;
//import com.sn.app.storage.UserStorage;
//import com.sn.utils.LanguageUtil;
//import com.truescend.gofit.pagers.base.BasePresenter;
//
//import retrofit2.Call;
//
///**
// * 遗弃
// */
//@Deprecated
//public class ResetPswFromAuthCodePresenterImpl extends BasePresenter<IResetPswFromAuthCodeContract.IView> implements IResetPswFromAuthCodeContract.IPresenter {
//    private IResetPswFromAuthCodeContract.IView view;
//    private final AppApiService service;
//
//    public ResetPswFromAuthCodePresenterImpl(IResetPswFromAuthCodeContract.IView view) {
//        this.view = view;
//        service = AppNetReq.getApi();
//    }
//
//    @Override
//    public void requestVerificationCode(String email) {
//        String language = LanguageUtil.isZH() ? "cmn-Hans" : "en";
//        Call<DefResponseBean> captch  = service.captcha(email, 2, language);
//
////        switch (type) {
////            case ResetPswFromAuthCodeActivity.TYPE_REGISTER:
////                captch = service.captcha(email, 1, language);
////                break;
////            case ResetPswFromAuthCodeActivity.TYPE_RESET:
////                captch = service.captcha(email, 2, language);
////                break;
////        }
//
//        if (captch != null) {
//            view.onShowLoading(true);
//            captch.enqueue(new OnResponseListener<DefResponseBean>() {
//                @Override
//                public void onResponse(DefResponseBean body) {
//                    view.onShowLoading(false);
//                    view.updateObtainVerification(true, body.getMessage());
//                }
//
//                @Override
//                public void onFailure(int ret, String msg) {
//                    view.onShowLoading(false);
//                    view.updateObtainVerification(false, msg);
//                }
//            });
//        }
//    }
//
//
//    @Override
//    public void requestReset(String email, String code, String password) {
//        Call<DefResponseBean>   reset = service.resetpwd(email, code, password);
//        if (reset != null) {
//            view.onShowLoading(true);
//            reset.enqueue(new OnResponseListener<DefResponseBean>() {
//                @Override
//                public void onResponse(DefResponseBean body) throws Throwable {
//                    view.onShowLoading(false);
//                    UserStorage.setAccessToken("");
//                    view.updateResetStatue(true, body.getMessage());
//                }
//
//                @Override
//                public void onFailure(int ret, String msg) {
//                    view.onShowLoading(false);
//                    view.updateResetStatue(false, msg);
//                }
//            });
//        }
//    }
//
//
//}
