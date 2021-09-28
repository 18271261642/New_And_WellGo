package com.truescend.gofit.wifi;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.truescend.gofit.R;
import java.io.File;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


/**
 * Created by Admin
 * Date 2021/9/27
 */
public class LocalWifiPlayerActivity extends GSYBaseActivityDetail<StandardGSYVideoPlayer> {

    private static final String TAG = "LocalWifiPlayerActivity";

    private StandardGSYVideoPlayer videoPlayer;

    private ImageView wifiTitleBackImg;
    private TextView itemTitleTv;

    private String videoUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_wifi_player_layout);

        initViews();

        initData();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            return;
        videoUrl = bundle.getString("local_video");
        Log.e(TAG,"---videoUrl="+videoUrl);
        if(videoUrl == null){
            Toast.makeText(this,"视频为空!",Toast.LENGTH_SHORT).show();
            return;
        }
        java.io.File file = new File(videoUrl);

        Log.e(TAG,"---filePath="+file.getName()+"\n"+file.getAbsolutePath());
        String tmpPaht = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/"+file.getName();
        String tmpPaht2 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/"+file.getName()+".avi";
        Log.e(TAG,"-----tmpPaht="+tmpPaht+" "+tmpPaht2);
       // videoPlayer.setUp(videoUrl,true,"title");

        initVideoBuilderMode();
    }

    private void initViews() {
        videoPlayer = findViewById(R.id.videoPlayer);
        wifiTitleBackImg = findViewById(R.id.wifiTitleBackImg);
        itemTitleTv = findViewById(R.id.itemTitleTv);

        itemTitleTv.setText("视频播放");
        wifiTitleBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},0x00);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(videoPlayer != null)
            videoPlayer.onVideoResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(videoPlayer != null)
            videoPlayer.onVideoPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    public StandardGSYVideoPlayer getGSYVideoPlayer() {
        return videoPlayer;
    }

    @Override
    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        return new GSYVideoOptionBuilder()
                .setUrl(videoUrl)
                .setCacheWithPlay(true)
                .setVideoTitle("title")
                .setIsTouchWiget(true)
                //.setAutoFullWithSize(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)//打开动画
                .setNeedLockFull(true)
                .setSeekRatio(1);
    }

    @Override
    public void clickForFullScreen() {

    }

    @Override
    public boolean getDetailOrientationRotateAuto() {
        return true;
    }
}
