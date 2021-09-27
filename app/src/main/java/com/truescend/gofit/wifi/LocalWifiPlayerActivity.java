package com.truescend.gofit.wifi;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.truescend.gofit.R;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import ijk.JZMediaIjk;

/**
 * Created by Admin
 * Date 2021/9/27
 */
public class LocalWifiPlayerActivity extends AppCompatActivity implements ScreenRotateUtils.OrientationChangeListener{

    private static final String TAG = "LocalWifiPlayerActivity";
    
    private JzvdStd mJzvdStd;

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
        mJzvdStd.setUp(videoUrl, "");
    }

    private void initViews() {
        mJzvdStd = findViewById(R.id.localJZVideo);
        wifiTitleBackImg = findViewById(R.id.wifiTitleBackImg);
        itemTitleTv = findViewById(R.id.itemTitleTv);

        itemTitleTv.setText("视频播放");
        wifiTitleBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    public void orientationChange(int orientation) {
        if (Jzvd.CURRENT_JZVD != null
                && (mJzvdStd.state == Jzvd.STATE_PLAYING || mJzvdStd.state == Jzvd.STATE_PAUSE)
                && mJzvdStd.screen != Jzvd.SCREEN_TINY) {
            if (orientation >= 45 && orientation <= 315 && mJzvdStd.screen == Jzvd.SCREEN_NORMAL) {
                changeScreenFullLandscape(ScreenRotateUtils.orientationDirection);
            } else if (((orientation >= 0 && orientation < 45) || orientation > 315) && mJzvdStd.screen == Jzvd.SCREEN_FULLSCREEN) {
                changeScrenNormal();
            }
        }
    }

    /**
     * 竖屏并退出全屏
     */
    private void changeScrenNormal() {
        if (mJzvdStd != null && mJzvdStd.screen == Jzvd.SCREEN_FULLSCREEN) {
            mJzvdStd.autoQuitFullscreen();
        }
    }
    /**
     * 横屏
     */
    private void changeScreenFullLandscape(float x) {
        //从竖屏状态进入横屏
        if (mJzvdStd != null && mJzvdStd.screen != Jzvd.SCREEN_FULLSCREEN) {
            if ((System.currentTimeMillis() - Jzvd.lastAutoFullscreenTime) > 2000) {
                mJzvdStd.autoFullscreen(x);
                Jzvd.lastAutoFullscreenTime = System.currentTimeMillis();
            }
        }
    }
}
