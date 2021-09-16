package com.truescend.gofit.pagers.friends.search;

import android.text.TextUtils;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.FriendInfoBean;
import com.sn.app.net.data.app.bean.SearchFriendsResultBean;
import com.sn.app.net.data.base.DefResponseBean;
import com.sn.utils.IF;
import com.sn.utils.RegexUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;

/**
 * 作者:东芝(2018/08/08).
 * 功能:添加好友
 */
public class FriendsSearchPresenterImpl extends BasePresenter<IFriendsSearchContract.IView> implements IFriendsSearchContract.IPresenter {
    private IFriendsSearchContract.IView view;
    private int user_id;
    public static final int THUMBACTION_TYPE_ZAN = 1;//点赞
    public static final int THUMBACTION_TYPE_ENCOURAGE = 2;//鼓励

    public FriendsSearchPresenterImpl(IFriendsSearchContract.IView view) {
        this.view = view;
    }


    @Override
    public void searchFriends(String searchContent) {
        searchContent = searchContent.replaceAll(" ","");
        if (TextUtils.isEmpty(searchContent)) {
            return;
        }
        if(!searchContent.contains("@")&&!RegexUtil.isNumber(searchContent)){
            view.onShowTips(ResUtil.getString(R.string.content_search_friends_input_err));
            return;
        }

        view.onLoading();
        AppNetReq.getApi().userSearch(searchContent).enqueue(new OnResponseListener<SearchFriendsResultBean>() {
            @Override
            public void onResponse(SearchFriendsResultBean body) throws Throwable {
                view.onLoadingDone();
                if (IF.isEmpty(body.getData())) {
                    view.onSearchNothing();
                    return;
                }
                SearchFriendsResultBean.DataBean dataBean = body.getData().get(0);

                user_id = dataBean.getUser_id();
                view.onSearchResults(dataBean);
                getFriendsInfoResults(user_id);
            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onLoadingDone();
                view.onSearchNothing();
            }
        });
    }

    @Override
    public void requestAddFriends() {
        if (user_id<=0) {
            return;
        }
        view.onLoading();
        AppNetReq.getApi().friendInvite(user_id).enqueue(new OnResponseListener<DefResponseBean>() {
            @Override
            public void onResponse(DefResponseBean body) throws Throwable {
                view.onLoadingDone();
                view.onRequestAddFriendsSuccess();
                view.onShowTips(ResUtil.getString(R.string.toast_tips_req_add_friends));
            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onLoadingDone();
                view.onRequestAddFriendsFailed();
                view.onShowTips(msg);
            }
        });
    }
    @Override
    public void getFriendsInfoResults(int user_id) {
        view.onLoading();
        AppNetReq.getApi().friendHomepage(user_id).enqueue(new OnResponseListener<FriendInfoBean>() {
            @Override
            public void onResponse(FriendInfoBean body) throws Throwable {
                view.onLoadingDone();
                view.onFriendsInfoResults( body.getData());

            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onLoadingDone();
                view.onShowTips(msg);
            }
        });
    }

    @Override
    public void setFriendsThumbAction(final int type) {
        AppNetReq.getApi().friendThumbAction(user_id,type).enqueue(new OnResponseListener<String>() {
            @Override
            public void onResponse(String body) throws Throwable {
                view.onFriendsThumbAction(type);
            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onShowTips(msg);
            }
        });
    }
}
