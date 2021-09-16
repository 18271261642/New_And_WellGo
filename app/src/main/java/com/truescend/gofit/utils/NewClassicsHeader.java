package com.truescend.gofit.utils;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.truescend.gofit.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Author:Created by 泽鑫 on 2018/4/7 10:23.
 */

public class NewClassicsHeader extends ClassicsHeader {


    public NewClassicsHeader(Context context) {
        super(context);
        init();

    }

    public NewClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewClassicsHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        REFRESH_HEADER_PULLDOWN = getContext().getString(R.string.content_pull_down_to_refresh);
        REFRESH_HEADER_REFRESHING = getContext().getString(R.string.content_refreshing);
        REFRESH_HEADER_LOADING = getContext().getString(R.string.content_is_loading);
        REFRESH_HEADER_RELEASE = getContext().getString(R.string.content_release_immediately_refresh);
        REFRESH_HEADER_FINISH = getContext().getString(R.string.content_refresh_completed);
        REFRESH_HEADER_FAILED = getContext().getString(R.string.content_refresh_failed);
        REFRESH_HEADER_LASTTIME = getContext().getString(R.string.content_last_update);
        mFormat = new SimpleDateFormat(REFRESH_HEADER_LASTTIME, Locale.ENGLISH);
        setTimeFormat(mFormat);
    }

    @Override
    public ClassicsHeader setLastUpdateTime(Date time) {
        return super.setLastUpdateTime(time);
    }

    @Override
    public ClassicsHeader setTimeFormat(DateFormat format) {
        return super.setTimeFormat(format);
    }
}
