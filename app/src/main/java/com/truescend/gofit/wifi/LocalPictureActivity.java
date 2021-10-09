package com.truescend.gofit.wifi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.truescend.gofit.R;
import com.truescend.gofit.utils.PermissionUtils;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * 本地图片查看
 * Created by Admin
 * Date 2021/9/28
 */
public class LocalPictureActivity extends AppCompatActivity {

    private static final String TAG = "LocalPictureActivity";

    private PhotoView wifiPhotoView;

    private String pictureUrl;

    private ImageView wifiTitleBackImg;
    private TextView itemTitleTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_wifi_picture_layout);


        initViews();

        pictureUrl = getIntent().getStringExtra("local_picture");

        Log.e(TAG,"---pictureUrl="+pictureUrl);

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            java.io.File file = new File(pictureUrl);
            Uri uri = Uri.fromFile(file);
            wifiPhotoView.setImageURI(uri);
        }

    }

    private void initViews() {
        wifiTitleBackImg = findViewById(R.id.wifiTitleBackImg);
        itemTitleTv = findViewById(R.id.itemTitleTv);

        itemTitleTv.setText("图片查看");
        wifiTitleBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        wifiPhotoView = findViewById(R.id.wifiPhotoView);

        PermissionUtils.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionUtils.OnPermissionGrantedListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied() {

            }
        });
    }
}
