package com.truescend.gofit.pagers.common.dialog;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.cmd.SNCMD;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;


/**
 * 功能：寻找手环弹框
 * Author:Created by 泽鑫 on 2017/12/15 21:56.
 */

public class SearchBandDialog {
    private Context mContext;
    private BaseDialog dialog;

    public SearchBandDialog(Context context) {
        this.mContext = context;
    }

    public void show() {
        int width = Math.round(mContext.getResources().getDisplayMetrics().widthPixels * 0.8f);
        dialog = new BaseDialog.Builder(mContext)
                .setWidth(width)
                .setContentView(R.layout.dialog_search_band)
                .setCanceledOnTouchOutside(false)
                .show();
        initAnim();
        initClick();
        SNBLEHelper.sendCMD(SNCMD.get().setFindDeviceStatus(true));
    }

    private void initAnim() {
        ImageView ivSearchBandDialogIcon = dialog.findViewById(R.id.ivSearchBandDialogIcon);
        TranslateAnimation translateAnimation = new TranslateAnimation(0,20, 0,0);
        translateAnimation.setDuration(1000);
        translateAnimation.setFillAfter(true);
        translateAnimation.setInterpolator(new CycleInterpolator(6));
        translateAnimation.setRepeatMode(Animation.RESTART);
        translateAnimation.setAnimationListener(new TranslateAnimation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                animation.setRepeatCount(-1);
                animation.setStartOffset(500);
                animation.startNow();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (ivSearchBandDialogIcon != null) {
            ivSearchBandDialogIcon.startAnimation(translateAnimation);
        }
        translateAnimation.startNow();
    }

    private void initClick() {
        TextView tvSearchBandDialogConfirm = dialog.findViewById(R.id.tvSearchBandDialogConfirm);

        if (tvSearchBandDialogConfirm != null) {
            tvSearchBandDialogConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SNBLEHelper.sendCMD(SNCMD.get().setFindDeviceStatus(false));
                    onDismiss();
                }
            });
        }
    }

    private void onDismiss(){
        if (dialog != null){
            dialog.dismiss();
        }
    }

}
