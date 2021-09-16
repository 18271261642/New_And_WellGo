package com.truescend.gofit.pagers.device.camera;


import android.Manifest;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.cameraview.CameraView;
import com.sn.utils.SNToast;
import com.sn.utils.music.SoundUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.utils.CameraFixUtil;
import com.truescend.gofit.utils.Constant;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.PermissionUtils;
import com.truescend.gofit.utils.StatusBarUtil;
import com.truescend.gofit.views.TitleLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2019/8/13).
 * 功能:远程相机pro
 */

public class RemoteCameraProActivity extends BaseActivity<RemoteCameraPresenterImpl, IRemoteCameraContract.IView> implements IRemoteCameraContract.IView {
    @BindView(R.id.camera)
    CameraView camera;
    @BindView(R.id.clpCameraTakePhoto)
    ContentLoadingProgressBar clpCameraTakePhoto;
    @BindView(R.id.ivRemoteCameraPhotoPreview)
    ImageView ivRemoteCameraPhotoPreview;
    @BindView(R.id.ivRemoteCameraFlash)
    ImageView ivRemoteCameraFlash;


    private SoundUtil soundUtil;
    private String lastTakePhotoPath;
    private boolean isSaving;
    private boolean isCameraGranted;

    @Override
    protected void onStart() {
        super.onStart();
        if (isCameraGranted) {
            camera.start();

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundUtil.destory();
        getPresenter().requestExitTakePhoto();
    }

    @Override
    protected RemoteCameraPresenterImpl initPresenter() {
        return new RemoteCameraPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_remote_camera_pro;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        StatusBarUtil.setStatusBarDarkTheme(this, false);
        StatusBarUtil.setStatusBarColor(this, 0xFF010101);
        soundUtil = new SoundUtil(this);
        if (!PermissionUtils.hasPermission(this, Manifest.permission.CAMERA)) {
            PermissionUtils.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, new PermissionUtils.OnPermissionGrantedListener() {
                @Override
                public void onGranted() {
                    isCameraGranted = true;
                    if (camera != null) {
                        camera.start();
                    }
                }

                @Override
                public void onDenied() {
                    finish();
                }
            });
        }else{
            isCameraGranted = true;
        }

        camera.addCallback(imageCallback);
        clpCameraTakePhoto.hide();
        getPresenter().requestStartTakePhoto();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        super.onCreateTitle(titleLayout);
        titleLayout.setTitleShow(false);
    }


    @OnClick({R.id.ivRemoteCameraBack, R.id.ivCameraTakePhoto, R.id.ivRemoteCameraFlash, R.id.ivRemoteCameraTransfer, R.id.ivRemoteCameraPhotoPreview})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivRemoteCameraBack:
                onBackPressed();
                break;
            case R.id.ivCameraTakePhoto:
                updateTakePhoto();
                break;
            case R.id.ivRemoteCameraFlash:
                toggleFlashStatus();
                refreshFlashIconStatus();
                break;
            case R.id.ivRemoteCameraTransfer:
                toggleFacingStatus();
                refreshFlashIconStatus();
                break;
            case R.id.ivRemoteCameraPhotoPreview:
                if (lastTakePhotoPath == null) {
                    return;
                }

                File file = new File(lastTakePhotoPath);
                if (file.exists()) {
                    PageJumpUtil.startImageViewerActivity(this, file.getAbsolutePath());

                }
                break;
        }
    }

    private void toggleFacingStatus() {
        switch (camera.getFacing()) {
            case CameraView.FACING_BACK:
                camera.setFacing(CameraView.FACING_FRONT);
                break;
            case CameraView.FACING_FRONT:
                camera.setFacing(CameraView.FACING_BACK);
                break;
        }

    }

    private void toggleFlashStatus() {
        switch (camera.getFlash()) {
            case CameraView.FLASH_AUTO:
                camera.setFlash(CameraView.FLASH_ON);
                break;
            case CameraView.FLASH_ON:
                camera.setFlash(CameraView.FLASH_OFF);
                break;
            case CameraView.FLASH_OFF:
                camera.setFlash(CameraView.FLASH_AUTO);
                break;
            case CameraView.FLASH_TORCH:
                //没有处理
                break;
        }
    }

    private void refreshFlashIconStatus() {

        if (camera.getFacing() == CameraView.FACING_FRONT) {
            ivRemoteCameraFlash.setVisibility(View.INVISIBLE);
        } else {
            ivRemoteCameraFlash.setVisibility(View.VISIBLE);
        }

        switch (camera.getFlash()) {
            case CameraView.FLASH_AUTO:
                ivRemoteCameraFlash.setImageResource(R.mipmap.icon_camera_flash_auto);
                break;
            case CameraView.FLASH_ON:
                ivRemoteCameraFlash.setImageResource(R.mipmap.icon_camera_flash_open);
                break;
            case CameraView.FLASH_OFF:
                ivRemoteCameraFlash.setImageResource(R.mipmap.icon_camera_flash_close);
                break;
            case CameraView.FLASH_RED_EYE:
                //无处理
                break;
            case CameraView.FLASH_TORCH:
                //无处理
                break;
        }

    }


    private void playSong() {
        if (soundUtil != null) {
            soundUtil.speak();
        }
    }

    @Override
    public void updateTakePhoto() {
        if (!isSaving) {
            isSaving = true;
            playSong();
            clpCameraTakePhoto.show();
            //拍照(包括本地点击和远程拍照)
            camera.takePicture();
        }


    }

    @Override
    public void exitRemoteCamera() {
        finish();
    }

    private CameraView.Callback imageCallback = new CameraView.Callback() {
        @Override
        public void onCameraOpened(CameraView cameraView) {
            super.onCameraOpened(cameraView);
//            LinkedList<AspectRatio> aspectRatios = new LinkedList<>(cameraView.getSupportedAspectRatios());
//            for (int i = aspectRatios.size() - 1; i >= 0; i--) {
//                AspectRatio aspectRatio = aspectRatios.get(i);
//                switch (aspectRatio.toString()) {
//                    case "13:9":
//                    case "4:3":
//                        try {
//                            cameraView.setAspectRatio(aspectRatio);
//                        } catch (Exception ignored) {
//                        }
//                        return;
//                }
//            }
//            if (aspectRatios.size() > 3) {
//                cameraView.setAspectRatio(aspectRatios.get(2));
//            } else if (!aspectRatios.isEmpty()) {
//                cameraView.setAspectRatio(aspectRatios.getFirst());
//            }

        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            SNAsyncTask.execute(new SNVTaskCallBack() {

                @Override
                public void run() {
                    File pictureFile = getOutputMediaFile();
                    OutputStream os = null;
                    try {
                        if (pictureFile != null) {
                            os = new FileOutputStream(pictureFile);
                            os.write(data);
                            os.close();
                            CameraFixUtil.notifyImage(RemoteCameraProActivity.this, pictureFile);
                            lastTakePhotoPath = pictureFile.getAbsolutePath();
                        }
                    } catch (IOException ignored) {

                    } finally {
                        isSaving = false;
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }


                }

                @Override
                public void error(Throwable e) {
                    isSaving = false;
                    if (!isFinished()) {
                        clpCameraTakePhoto.hide();
                    }
                }

                @Override
                public void done() {
                    isSaving = false;
                    if (!isFinished()) {
                        clpCameraTakePhoto.hide();
                    }
                    SNToast.toast(getString(R.string.content_save_picture_to) + lastTakePhotoPath);
                    showPhotoPreview(lastTakePhotoPath);
                }
            });
        }
    };

    private void showPhotoPreview(String path) {
        if (isFinished()) return;
        Glide.with(getApplicationContext())
                .load(path)
                .apply(RequestOptions.errorOf(R.mipmap.icon_camera_photo))
                .apply(RequestOptions.centerCropTransform())
                .into(ivRemoteCameraPhotoPreview);
    }

    /**
     * 输出图片格式
     */
    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Constant.Path.CAMERA, "Camera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }



    @Override
    protected void onStop() {
        if (isCameraGranted) {
            camera.stop();
        }
        super.onStop();
        finish();
    }


}
