package com.truescend.gofit.pagers.user.setting;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.ImageBean;
import com.sn.app.net.data.app.bean.UserMessageBean;
import com.sn.app.storage.UserStorage;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.sport.SportDao;
import com.sn.net.comm.builder.MultipartBuilder;
import com.sn.utils.DateUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.ble.CMDCompat;
import com.truescend.gofit.pagers.base.BasePresenter;

import java.io.File;

import retrofit2.Call;

/**
 * 作者:东芝(2018/2/23).
 * 功能:用户信息设置
 */
public class UserSettingPresenterImpl extends BasePresenter<IUserSettingContract.IView> implements IUserSettingContract.IPresenter {
    private IUserSettingContract.IView view;

    public UserSettingPresenterImpl(IUserSettingContract.IView view) {
        this.view = view;
    }

    @Override
    public void requestUpdateUserData(String mUserLocalImagePath, final String mUserNickname, final String mUserSign, final int mUserGender, final String mUserBirthDate, final float mUserHeight, final float mUserWeight, final String lastWeightTime, final int mUserTarget) {
        if (mUserLocalImagePath == null) {
            //没有修改头像
            updateUserData(null, mUserNickname, mUserSign, mUserBirthDate, (int) mUserHeight, mUserWeight, lastWeightTime, mUserGender, mUserTarget);
        } else {
            AppNetReq.getApi().uploadImage(MultipartBuilder.createPart("image", new File(mUserLocalImagePath))).enqueue(new OnResponseListener<ImageBean>() {
                @Override
                public void onResponse(ImageBean body) throws Throwable {
                    String mUserHeadUrl = body.getData().getUrls().get(0);
                    updateUserData(mUserHeadUrl, mUserNickname, mUserSign, mUserBirthDate, (int) mUserHeight, mUserWeight, lastWeightTime, mUserGender, mUserTarget);
                }

                @Override
                public void onFailure(int ret, String msg) {
                    view.onUpdateUserDataFailed(msg);
                }
            });
        }
    }

    private void updateUserData(String mUserHeadUrl, String mUserNickname, String mUserSign, String mUserBirthDate, int mUserHeight, float mUserWeight, String lastWeightTime, int mUserGender, final int mUserTarget) {
        Call<UserMessageBean> updateUser;
        if (mUserHeadUrl == null) {
            //不修改头像
            updateUser = AppNetReq.getApi().updateUser(mUserNickname,mUserSign,mUserBirthDate, mUserHeight, mUserWeight, lastWeightTime, mUserGender, mUserTarget);
        } else {
            //全部都修改
            updateUser = AppNetReq.getApi().updateUser(mUserNickname,mUserSign,mUserBirthDate, mUserHeight, mUserWeight, lastWeightTime, mUserGender, mUserHeadUrl, mUserTarget);
        }

        updateUser.enqueue(new OnResponseListener<UserMessageBean>() {
            @Override
            public void onResponse(UserMessageBean body) throws Throwable {
                final UserMessageBean.DataBean data = body.getData();
                SNAsyncTask.execute(new SNVTaskCallBack() {
                    @Override
                    public void run() throws Throwable {
                        //重刷新程序层的用户数据信息
                        AppUserUtil.initialize(data);
                        UserStorage.setIsFirst(false);
                        //更新当天目标
                        SportDao sportDao = SNBLEDao.get(SportDao.class);
                        sportDao.updateStepTarget(AppUserUtil.getUser().getUser_id(), DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD), mUserTarget);
                    }

                    @Override
                    public void done() {
                        super.done();
                        CMDCompat.get().setUserInfo();
                        view.onUpdateUserDataSuccess();
                    }

                    @Override
                    public void error(Throwable e) {
                        super.error(e);
                        view.onUpdateUserDataFailed(e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onUpdateUserDataFailed(msg);
            }
        });
    }
}
