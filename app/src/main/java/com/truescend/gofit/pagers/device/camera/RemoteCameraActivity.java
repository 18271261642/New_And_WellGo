package com.truescend.gofit.pagers.device.camera;


import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.eventbus.SNEvent;
import com.sn.utils.eventbus.SNEventBus;
import com.sn.utils.music.SoundUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.utils.CameraFixUtil;
import com.truescend.gofit.utils.Constant;
import com.truescend.gofit.utils.PermissionUtils;
import com.truescend.gofit.views.CameraPreview;
import com.truescend.gofit.views.TitleLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 功能：远程拍照页面
 * Author:Created by 泽鑫 on 2017/12/15 14:47.
 */

public class RemoteCameraActivity extends BaseActivity<RemoteCameraPresenterImpl, IRemoteCameraContract.IView> implements IRemoteCameraContract.IView {
    @BindView(R.id.flCameraPreview)
    FrameLayout flCameraPreview;
    @BindView(R.id.ivCameraTakePhoto)
    ImageView ivCameraTakePhoto;
    @BindView(R.id.ivRemoteCameraBack)
    ImageView ivRemoteCameraBack;

    /**
     * 相机预览图
     **/
    private CameraPreview preview;

    private boolean safeToTakePicture;
    private SoundUtil soundUtil;

    @Override
    protected RemoteCameraPresenterImpl initPresenter() {
        return new RemoteCameraPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_remote_camera;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        soundUtil = new SoundUtil(this);
        SNEventBus.register(this);
        PermissionUtils.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, new PermissionUtils.OnPermissionGrantedListener() {
            @Override
            public void onGranted() {
                preview = new CameraPreview(RemoteCameraActivity.this);
                flCameraPreview.addView(preview);
            }
            @Override
            public void onDenied() {
                finish();
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SNEventBus.unregister(this);
        soundUtil.destory();
        getPresenter().requestExitTakePhoto();

    }



    @OnClick({R.id.ivRemoteCameraBack,R.id.ivCameraTakePhoto})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivRemoteCameraBack:
                onBackPressed();
                break;
            case R.id.ivCameraTakePhoto:
                playSong();
                CameraFixUtil.takePicture(preview.getCamera(), null, null, mPicture);
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
        if (!safeToTakePicture) {
            safeToTakePicture = true;
            playSong();
            CameraFixUtil.takePicture(preview.getCamera(), null, null, mPicture);
        }
    }

    @Override
    public void exitRemoteCamera()
    {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(SNEvent event) {
        switch (event.getCode()) {
            case SNBLEEvent.EVENT_BLE_STATUS_BLUETOOTH_OFF:
            case SNBLEEvent.EVENT_BLE_STATUS_DISCONNECTED:
            case SNBLEEvent.EVENT_BLE_STATUS_CONNECT_FAILED:
            case SNBLEEvent.EVENT_BLE_STATUS_CONNECTED:
                finish();
                break;
        }
    }
    /**
     * 处理图片
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.preRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                safeToTakePicture = false;
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                bitmap.recycle();
                Toast.makeText(RemoteCameraActivity.this, getString(R.string.content_save_picture_to) + pictureFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                safeToTakePicture = false;
                CameraFixUtil.notifyImage(RemoteCameraActivity.this, pictureFile);
            } catch (Exception e) {
                e.printStackTrace();
                safeToTakePicture = false;
            }
            camera.startPreview();
        }
    };




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

        /**
         * 相片命名格式不能变，要不然无法保存
         */
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }
}
