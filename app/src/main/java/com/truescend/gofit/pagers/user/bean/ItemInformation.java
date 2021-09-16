package com.truescend.gofit.pagers.user.bean;

import android.view.View;
import android.widget.TextView;

import com.truescend.gofit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 功能：信息布局
 * 适配item_information.xml
 * Author:Created by 泽鑫 on 2018/1/25 15:53.
 */

public class ItemInformation {
    @BindView(R.id.tvInformationTitle)
    TextView tvInformationTitle;
    @BindView(R.id.tvInformationContent)
    TextView tvInformationContent;
    @BindView(R.id.tvInformationNote)
    TextView tvInformationNote;

    public ItemInformation(View view) {
        ButterKnife.bind(this, view);
    }

    public void setTitle(int resId){
        tvInformationTitle.setText(resId);
    }

    public void setTitle(String title){
        tvInformationTitle.setText(title);
    }

    public void setTitleColor(int resId){
        tvInformationTitle.setTextColor(resId);
    }

    public void setContent(int resId){
        tvInformationContent.setText(resId);
    }

    public void setContent(String content){
        tvInformationContent.setText(content);
    }

    public void setContentVisibility(int visibility){
        tvInformationContent.setVisibility(visibility);
    }

    public void setNote(int resId){
        tvInformationNote.setText(resId);
    }

    public void setNote(String note){
        tvInformationNote.setText(note);
    }

    public void setNoteVisibility(int visibility){
        tvInformationNote.setVisibility(visibility);
    }
}
