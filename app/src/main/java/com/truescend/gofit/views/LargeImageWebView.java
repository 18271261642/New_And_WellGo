package com.truescend.gofit.views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * 作者:东芝(2019/4/4).
 * 功能:
 */

public class LargeImageWebView extends WebView {
    public LargeImageWebView(Context context) {
        super(context);
        init();
    }

    public LargeImageWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LargeImageWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LargeImageWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public LargeImageWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init();
    }



    private void init() {
        setScrollbarFadingEnabled(true);
        setVerticalScrollBarEnabled(true);
        setHorizontalScrollBarEnabled(false);

        WebSettings settings = getSettings();
        settings.setSupportZoom(false);//缩放
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);//不显示控制器
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");



    }



}
