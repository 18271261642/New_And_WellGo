package com.truescend.gofit.pagers.friends.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sn.app.net.data.app.bean.FriendInfoBean;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.ResUtil;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 作者:东芝(2018/08/15).
 * 功能:用户主页信息
 */
public class FriendsInfoActivity extends BaseActivity<FriendsInfoPresenterImpl, IFriendsInfoContract.IView> implements IFriendsInfoContract.IView {

    @BindView(R.id.civHead)
    CircleImageView civHead;
    @BindView(R.id.ivZan)
    ImageView ivZan;
    @BindView(R.id.ivEncourage)
    ImageView ivEncourage;
    @BindView(R.id.tvRequestAddFriends)
    TextView tvRequestAddFriends;
    @BindView(R.id.tvNickname)
    TextView tvNickname;
    @BindView(R.id.ivGender)
    ImageView ivGender;
    @BindView(R.id.tvIdName)
    TextView tvIdName;
    private int user_id;
    private boolean isMyFriends;


    public static void startActivity(Context context, int user_id) {
        context.startActivity(new Intent(context, FriendsInfoActivity.class).putExtra("user_id", user_id));
    }

    @Override
    protected FriendsInfoPresenterImpl initPresenter() {
        return new FriendsInfoPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_friends_info;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setTitle(R.string.content_user_info);
        user_id = getIntent().getIntExtra("user_id", -1);
        if (user_id == -1) {
            finish();
            return;
        }
        getPresenter().getFriendsInfoResults(user_id);
    }

    @Override
    public void onLoading() {
        LoadingDialog.loading(this);
    }

    @Override
    public void onLoadingDone() {
        LoadingDialog.dismiss();
    }

    @Override
    public void onShowTips(String str) {
        SNToast.toast(str);
    }

    @Override
    public void onFriendsInfoResults(FriendInfoBean.DataBean data) {
        isMyFriends = data.getIs_friend() == 1;
        if (isMyFriends) {
            tvRequestAddFriends.setText(R.string.content_user_info);
        } else {
            tvRequestAddFriends.setText(R.string.content_add_friends);
        }

        Glide.with(civHead)
                .load(data.getPortrait())
                .apply(RequestOptions.errorOf(R.mipmap.img_test_picture))
                .apply(RequestOptions.placeholderOf(R.mipmap.img_test_picture))
                .into(civHead);

        tvIdName.setText(ResUtil.format("ID:%d", data.getId()));
        tvNickname.setText(data.getNickname());
        ivGender.setSelected(data.getGender() == 1);//1：男，2：女
        ivZan.setSelected(data.getIs_thumb() == 1);
        ivEncourage.setSelected(data.getIs_encourage() == 1);
    }

    @Override
    public void onFriendsThumbAction(int type) {
        switch (type) {
            case FriendsInfoPresenterImpl.THUMBACTION_TYPE_ZAN:
                ivZan.setSelected(true);
                break;
            case FriendsInfoPresenterImpl.THUMBACTION_TYPE_ENCOURAGE:
                ivEncourage.setSelected(true);
                break;
        }
    }

    @Override
    public void onRequestAddFriendsSuccess() {

    }

    @Override
    public void onRequestAddFriendsFailed() {

    }

    @OnClick({R.id.tvRequestAddFriends, R.id.ivZan, R.id.ivEncourage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvRequestAddFriends:
                if (isMyFriends) {
                    PageJumpUtil.startFriendsDataActivity(this, user_id);
                } else {
                    getPresenter().requestAddFriends(user_id);
                }
                break;
            case R.id.ivZan:
                getPresenter().setFriendsThumbAction(user_id, FriendsInfoPresenterImpl.THUMBACTION_TYPE_ZAN);
                break;
            case R.id.ivEncourage:
                getPresenter().setFriendsThumbAction(user_id, FriendsInfoPresenterImpl.THUMBACTION_TYPE_ENCOURAGE);
                break;
        }
    }
}
