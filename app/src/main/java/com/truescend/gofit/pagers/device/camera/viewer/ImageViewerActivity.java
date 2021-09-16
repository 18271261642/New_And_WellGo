package com.truescend.gofit.pagers.device.camera.viewer;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.utils.StatusBarUtil;
import com.truescend.gofit.views.TouchImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2019/8/13).
 * 功能:远程相机pro
 */

public class ImageViewerActivity extends BaseActivity<ImageViewerPresenterImpl, IImageViewerContract.IView> implements IImageViewerContract.IView {

    @BindView(R.id.ivImageViewer)
    TouchImageView ivImageViewer;
    @BindView(R.id.clpLoading)
    ContentLoadingProgressBar clpLoading;
    @BindView(R.id.ivImageViewerGoToGallery)
    ImageView ivImageViewerGoToGallery;
    private File imageFile;
    private Intent intentGalleryApp;


    public static void startActivity(Context context, String path) {
        context.startActivity(new Intent(context, ImageViewerActivity.class).putExtra("path", path));
    }

    @Override
    protected ImageViewerPresenterImpl initPresenter() {
        return new ImageViewerPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_imageviewer;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        getTitleLayout().setTitleShow(false);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        StatusBarUtil.setStatusBarDarkTheme(this, false);
        StatusBarUtil.setStatusBarColor(this, 0xFF010101);

        String path = getIntent().getStringExtra("path");
        if (TextUtils.isEmpty(path)) {
            finish();
            return;
        }
        imageFile = new File(path);
        if (!imageFile.exists() || !imageFile.canRead() || imageFile.length() == 0) {
            finish();
            return;
        }
        clpLoading.show();
        Glide.with(this)
                .asBitmap()
                .load(imageFile)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        if (!isFinishing()) {
                            ivImageViewer.setImageBitmap(resource);
                            clpLoading.hide();
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        if (!isFinishing()) {
                            clpLoading.hide();
                        }
                        finish();
                    }
                });
        refreshGallerySupportStatus();
    }

    private void refreshGallerySupportStatus() {
        intentGalleryApp = new Intent(Intent.ACTION_VIEW, Uri.parse(
                "content://media/internal/images/media"));
        intentGalleryApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager manager = getPackageManager();
        ComponentName componentName = intentGalleryApp.resolveActivity(manager);
        if (componentName != null) {
            String packageName = componentName.getPackageName();
            Intent intentGallery = manager.getLaunchIntentForPackage(packageName);
            if (intentGallery != null && intentGallery.resolveActivity(manager) != null) {
                intentGalleryApp = intentGallery;
                ivImageViewerGoToGallery.setVisibility(View.VISIBLE);
            }else{
                ivImageViewerGoToGallery.setVisibility(View.INVISIBLE);
            }
        } else {
            ivImageViewerGoToGallery.setVisibility(View.INVISIBLE);
        }
    }


    @OnClick({R.id.ivImageViewerBack, R.id.ivImageViewerGoToGallery})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivImageViewerBack:
                onBackPressed();
                break;
            case R.id.ivImageViewerGoToGallery:
                if (intentGalleryApp != null && imageFile != null && imageFile.exists()) {
                    try {
                        startActivity(intentGalleryApp);
                    } catch (Exception ignored) {
                    }
                }
                break;
        }
    }


}
