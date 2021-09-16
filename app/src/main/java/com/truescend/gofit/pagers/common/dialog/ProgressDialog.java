package com.truescend.gofit.pagers.common.dialog;

import android.content.Context;
import androidx.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;
import com.truescend.gofit.utils.ResUtil;

/**
 * 功能：更新进度条弹框
 * 适用于某些耗时下载，例如：更新新版本App时
 * Author:Created by 泽鑫 on 2018/3/1 18:05.
 * 东芝:改-去掉文件大小显示 只保留百分比好了 并改名为ProgressDialog 供全局进度条弹窗用
 */

public class ProgressDialog {
    private BaseDialog dialog;
    private ProgressBar progressBar;
    private TextView tvUpdateTitle;
    private TextView tvProgressRate;
    private TextView tvMessage;

    public ProgressDialog(Context context) {
        dialog = new BaseDialog.Builder(context)
                .setContentView(R.layout.dialog_progress)
                .setCanceledOnTouchOutside(false)
                .create();
        progressBar = dialog.findViewById(R.id.pbProgressBar);
        tvUpdateTitle = dialog.findViewById(R.id.tvTitle);
        tvProgressRate = dialog.findViewById(R.id.tvProgressRate);
        tvMessage = dialog.findViewById(R.id.tvMessage);
        if (tvMessage != null) {
            tvMessage.setVisibility(View.GONE);
        }
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void setMax(int max) {
        if (progressBar != null) {
            progressBar.setMax(max);
        }
    }

    public void setTitle(@StringRes int id) {
        if (tvUpdateTitle != null) {
            tvUpdateTitle.setText(id);
        }
    }

    public void setTitle(CharSequence title) {
        if (tvUpdateTitle != null) {
            tvUpdateTitle.setText(title);
        }
    }

    public void setMessage(CharSequence message) {
        if (tvMessage != null) {
            tvMessage.setText(message);
            tvMessage.setVisibility(TextUtils.isEmpty(message) ? View.GONE : View.VISIBLE);
        }
    }

    public void setProgress(int progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
            if (tvProgressRate != null) {
                tvProgressRate.setText(calcProgressBarRate(progress, progressBar.getMax()));
            }
        }
    }

    /**
     * 计算进度条百分比
     *
     * @param progress 当前进度
     * @param max      最大进度
     * @return 百分之几
     */
    private String calcProgressBarRate(int progress, int max) {
        float rate = progress * 1.0f / max * 100;
        return ResUtil.format("%.2f%%", rate);
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }
}
