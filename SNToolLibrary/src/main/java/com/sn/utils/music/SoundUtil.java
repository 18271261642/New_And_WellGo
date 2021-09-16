package com.sn.utils.music;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.sn.utils.R;

/**
 * 作者:东芝(2019/7/5).
 * 功能:
 */
public class SoundUtil {
    private SoundPool soundPool;
    private boolean initComplete = false;
    private int soundID;

    public SoundUtil(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频的数量
            builder.setMaxStreams(3);
            //AudioAttributes是一个封装音频各种属性的类
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            //第一个参数是可以支持的声音数量，第二个是声音类型，第三个是声音品质
            soundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 5);
        }
        initBeep(context);
    }
    //初始化资源文件
    private void initBeep(Context context) {
        //第一个参数Context,第二个参数资源Id，第三个参数优先级
        soundID = soundPool.load(context, R.raw.camera, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                initComplete = true;
            }
        });
    }
    //播放
    public void speak() {
        if (initComplete) {
            soundPool.play(soundID, 1, 1, 0, 0, 1);
        }
    }

    //销毁
    public void destory() {
        if (null != soundPool) {
            soundPool.release();
        }
    }
} 