package com.truescend.gofit.wifi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.generalplus.ffmpegLib.ffmpegWrapper;
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYStateUiListener;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.truescend.gofit.R;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import generalplus.com.GPCamLib.CamWrapper;

/**
 * 视频播放页面，在线
 * Created by Admin
 * Date 2021/10/11
 */
public class OnLineWifiPlayerActivity extends GSYBaseActivityDetail<StandardGSYVideoPlayer> {

    private static final String TAG = "OnLineWifiPlayerActivit";

    private StandardGSYVideoPlayer videoPlayer;

    private String _urlToStream;
    private int _FileFlag;
    private int _FileIndex;

    private int _i32CommandIndex = 0;

    private static boolean _bRunVLC = false;
    private static boolean _bIsPause = false;

    private long mLastClickTime;

    private ImageView imgView;
    private ImageView wifiTitleBackImg;
    private TextView itemTitleTv;

    private final Handler m_FromWrapperHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CamWrapper.GPCALLBACKTYPE_CAMSTATUS:
                    Bundle data = msg.getData();
                    ParseGPCamStatus(data);
                    msg = null;
                    break;
                case CamWrapper.GPCALLBACKTYPE_CAMDATA:
                    break;
            }
        }

    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_wifi_player_layout);


        initViews();
    }

    private void initViews() {
        videoPlayer = findViewById(R.id.onLineVideoPlayer);
        imgView = findViewById(R.id.imgView);
        itemTitleTv = findViewById(R.id.itemTitleTv);
        itemTitleTv.setText("视频播放");

        wifiTitleBackImg = findViewById(R.id.wifiTitleBackImg);
        wifiTitleBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CamWrapper.getComWrapperInstance().GPCamSendStopPlayback();
                finish();
            }
        });

        initVideoBuilderMode();

    }


    @Override
    protected void onResume() {
        super.onResume();
        initPlayStatus();
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
                //.setUrl(videoUrl)
                .setCacheWithPlay(true)
                .setVideoTitle("")
                .setIsTouchWiget(false)
                //.setAutoFullWithSize(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setShowFullAnimation(false)//打开动画
                .setNeedLockFull(true)
                .setGSYStateUiListener(gsyStateUiListener)
                .setGSYVideoProgressListener(gsyVideoProgressListener)
                .setSeekRatio(1);
    }

    @Override
    public void clickForFullScreen() {

    }

    @Override
    public boolean getDetailOrientationRotateAuto() {
        return false;
    }


    private void initPlayStatus(){
        Bundle b = getIntent().getExtras();
        _urlToStream = b.getString(CamWrapper.GPFILECALLBACKTYPE_FILEURL, null);
        _FileFlag = b.getInt(CamWrapper.GPFILECALLBACKTYPE_FILEFLAG, 0);
        _FileIndex = b.getInt(CamWrapper.GPFILECALLBACKTYPE_FILEINDEX, 0);


        Log.e(TAG,"---_urlToStream="+_urlToStream+"\n"+_FileFlag+"\n"+_FileIndex);

//        setupControls();
//        initStreaming();

      //  imgbtn_playpause.setVisibility(View.VISIBLE);
        if (_urlToStream.isEmpty())    // Streaming
        {
            CamWrapper.getComWrapperInstance().SetViewHandler(m_FromWrapperHandler, CamWrapper.GPVIEW_STREAMING);

//            ffmpegWrapper.getInstance().naSetStreaming(true);
//            ffmpegWrapper.getInstance().naSetBufferingTime(3 * 1000);

            if (_FileFlag == CamWrapper.GPFILEFLAG_JPGSTREAMING) {
                playPictureStreaming();
//                imgbtn_playpause.setVisibility(View.INVISIBLE);
            } else
                playVideoStreaming();
        } else                        // Local File
        {
            videoPlayer.setUp(_urlToStream,true,"");
            videoPlayer.startPlayLogic();
//            playLocalFile();
        }
       // mSurfaceView.onResume();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
      //  ffmpegWrapper.getInstance().naSetBufferingTime(0);
        if (isFastClick()) {
            return;
        }
        super.onBackPressed();
        CamWrapper.getComWrapperInstance().GPCamClearCommandQueue();
        if (_urlToStream.isEmpty()) {
            Log.e(TAG, "GPCamSendStopPlayback ...");
            CamWrapper.getComWrapperInstance().GPCamSendStopPlayback();
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        finish();
    }


    //停止播放
    private void stopStreaming() {
        if (_FileFlag == CamWrapper.GPFILEFLAG_AVISTREAMING && _urlToStream.isEmpty())            // Play Streaming
        {
            if (ffmpegWrapper.getInstance().naStatus() == ffmpegWrapper.ePlayerStatus.E_PlayerStatus_Playing.ordinal()) {
                CamWrapper.getComWrapperInstance().GPCamSendPausePlayback();
            } else {
                playVideoStreaming();
            }
        } else if (_FileFlag == CamWrapper.GPFILEFLAG_AVISTREAMING && !_urlToStream.isEmpty())        // Play Local File
        {
            if (!_bIsPause) {
                ffmpegWrapper.getInstance().naPause();
            } else {
                ffmpegWrapper.getInstance().naResume();
            }

            _bIsPause = !_bIsPause;
        }
    }


    //读取视频流
    private void playVideoStreaming() {

        String urlToStream = "";
        if (MainViewController.m_bRtsp) {
            urlToStream = String.format(CamWrapper.RTSP_STREAMING_URL, CamWrapper.COMMAND_URL);
        } else {
            urlToStream = String.format(CamWrapper.STREAMING_URL, CamWrapper.COMMAND_URL);
        }
        // ffmpegWrapper.getInstance().naStop();
        CamWrapper.getComWrapperInstance().GPCamSendRestartStreaming();
        CamWrapper.getComWrapperInstance().GPCamSendStartPlayback(_FileIndex);

        Log.e(TAG,"------在线播放流="+urlToStream);

        videoPlayer.setUp(urlToStream,false,"");
        videoPlayer.startPlayLogic();






//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String urlToStream = "";
//                if (MainViewController.m_bRtsp) {
//                    urlToStream = String.format(CamWrapper.RTSP_STREAMING_URL, CamWrapper.COMMAND_URL);
//                } else {
//                    urlToStream = String.format(CamWrapper.STREAMING_URL, CamWrapper.COMMAND_URL);
//                }
//               // ffmpegWrapper.getInstance().naStop();
//                CamWrapper.getComWrapperInstance().GPCamSendRestartStreaming();
//                CamWrapper.getComWrapperInstance().GPCamSendStartPlayback(_FileIndex);
//
//                Log.e(TAG,"------在线播放流="+urlToStream);
//
//                videoPlayer.setUp(urlToStream,false,"");
//                videoPlayer.startPlayLogic();
//
//               // ffmpegWrapper.getInstance().naInitAndPlay(urlToStream, "timeout=-1;stimeout=-1");
//
//    	    	/*if(PlayVLCThread == null)
//    	        {
//    	        	PlayVLCThread = new Thread(new PlayVLCRunnable());
//    	    		PlayVLCThread.start();
//    	        }*/
//            }
//        }).start();
    }


    //读取图片流
    private void playPictureStreaming() {

        String urlToStream = "";
        if (MainViewController.m_bRtsp) {
            urlToStream = String.format(CamWrapper.RTSP_STREAMING_URL, CamWrapper.COMMAND_URL);
        } else {
            urlToStream = String.format(CamWrapper.STREAMING_URL, CamWrapper.COMMAND_URL);
        }
        // ffmpegWrapper.getInstance().naStop();
        CamWrapper.getComWrapperInstance().GPCamClearCommandQueue();
        CamWrapper.getComWrapperInstance().GPCamSendRestartStreaming();
        Log.e(TAG,"-------图片流="+urlToStream);
        Glide.with(OnLineWifiPlayerActivity.this)
                .load(urlToStream)
                .into(imgView);

        videoPlayer.setUp(urlToStream,false,"");
        // ffmpegWrapper.getInstance().naInitAndPlay(urlToStream, "");
    	    	/*if(PlayVLCThread == null)
    	        {
    	        	PlayVLCThread = new Thread(new PlayVLCRunnable());
    	    		PlayVLCThread.start();
    	        }*/
        for (int i = 0; i < 4; i++)
            CamWrapper.getComWrapperInstance().GPCamSendStartPlayback(_FileIndex);






//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String urlToStream = "";
//                if (MainViewController.m_bRtsp) {
//                    urlToStream = String.format(CamWrapper.RTSP_STREAMING_URL, CamWrapper.COMMAND_URL);
//                } else {
//                    urlToStream = String.format(CamWrapper.STREAMING_URL, CamWrapper.COMMAND_URL);
//                }
//               // ffmpegWrapper.getInstance().naStop();
//                CamWrapper.getComWrapperInstance().GPCamClearCommandQueue();
//                CamWrapper.getComWrapperInstance().GPCamSendRestartStreaming();
//                Log.e(TAG,"-------图片流="+urlToStream);
//               // ffmpegWrapper.getInstance().naInitAndPlay(urlToStream, "");
//    	    	/*if(PlayVLCThread == null)
//    	        {
//    	        	PlayVLCThread = new Thread(new PlayVLCRunnable());
//    	    		PlayVLCThread.start();
//    	        }*/
//                for (int i = 0; i < 4; i++)
//                    CamWrapper.getComWrapperInstance().GPCamSendStartPlayback(_FileIndex);
//            }
//        }).start();
    }


    private void ParseGPCamStatus(Bundle StatusBundle) {
        int i32CmdIndex = StatusBundle.getInt(CamWrapper.GPCALLBACKSTATUSTYPE_CMDINDEX);
        int i32CmdType = StatusBundle.getInt(CamWrapper.GPCALLBACKSTATUSTYPE_CMDTYPE);
        int i32Mode = StatusBundle.getInt(CamWrapper.GPCALLBACKSTATUSTYPE_CMDMODE);
        int i32CmdID = StatusBundle.getInt(CamWrapper.GPCALLBACKSTATUSTYPE_CMDID);
        int i32DataSize = StatusBundle.getInt(CamWrapper.GPCALLBACKSTATUSTYPE_DATASIZE);
        byte[] pbyData = StatusBundle.getByteArray(CamWrapper.GPCALLBACKSTATUSTYPE_DATA);
        //Log.e(TAG, "i32CMDIndex = " + i32CmdIndex + ", i32Type = " + i32CmdType + ", i32Mode = " + i32Mode + ", i32CMDID = " + i32CmdID + ", i32DataSize = " + i32DataSize);

        Log.e(TAG,"-----pbyData="+ Arrays.toString(pbyData));

        if (i32CmdType == CamWrapper.GP_SOCK_TYPE_ACK) {
            switch (i32Mode) {
                case CamWrapper.GPSOCK_MODE_General:
                    switch (i32CmdID) {
                        case CamWrapper.GPSOCK_General_CMD_SetMode:
                            break;
                        case CamWrapper.GPSOCK_General_CMD_GetDeviceStatus:
                            break;
                        case CamWrapper.GPSOCK_General_CMD_GetParameterFile:
                            break;
                        case CamWrapper.GPSOCK_General_CMD_RestartStreaming:
                            break;
                    }
                    break;
                case CamWrapper.GPSOCK_MODE_Record:
                    Log.e(TAG, "GPSOCK_MODE_Record ... ");
                    break;
                case CamWrapper.GPSOCK_MODE_CapturePicture:
                    Log.e(TAG, "GPSOCK_MODE_CapturePicture ... ");
                    break;
                case CamWrapper.GPSOCK_MODE_Playback:
                    Log.e(TAG, "GPSOCK_MODE_Playback ... ");
                    switch (i32CmdID){
                        case CamWrapper.GPSOCK_Playback_CMD_GetNameList:
                        case CamWrapper.GPSOCK_Playback_CMD_GetThumbnail:
                        case CamWrapper.GPSOCK_Playback_CMD_GetRawData:
                            _i32CommandIndex = i32CmdIndex;
                            break;
                    }
                    break;
                case CamWrapper.GPSOCK_MODE_Menu:
                    Log.e(TAG, "GPSOCK_MODE_Menu ... ");
                    break;
                case CamWrapper.GPSOCK_MODE_Vendor:
                    Log.e(TAG, "GPSOCK_MODE_Vendor ... ");
                    break;
            }
        } else if (i32CmdType == CamWrapper.GP_SOCK_TYPE_NAK) {
            int i32ErrorCode = (pbyData[0] & 0xFF) + ((pbyData[1] & 0xFF) << 8);

            switch (i32ErrorCode) {
                case CamWrapper.Error_ServerIsBusy:
                    Log.e(TAG, "Error_ServerIsBusy ... ");
                    break;
                case CamWrapper.Error_InvalidCommand:
                    Log.e(TAG, "Error_InvalidCommand ... ");
                    break;
                case CamWrapper.Error_RequestTimeOut:
                    Log.e(TAG, "Error_RequestTimeOut ... ");
                    break;
                case CamWrapper.Error_ModeError:
                    Log.e(TAG, "Error_ModeError ... ");
                    break;
                case CamWrapper.Error_NoStorage:
                    Log.e(TAG, "Error_NoStorage ... ");
                    break;
                case CamWrapper.Error_WriteFail:
                    Log.e(TAG, "Error_WriteFail ... ");
                    break;
                case CamWrapper.Error_GetFileListFail:
                    Log.e(TAG, "Error_GetFileListFail ... ");
                    break;
                case CamWrapper.Error_GetThumbnailFail:
                    Log.e(TAG, "Error_GetThumbnailFail ... ");
                    break;
                case CamWrapper.Error_FullStorage:
                    Log.e(TAG, "Error_FullStorage ... ");
                    break;
                case CamWrapper.Error_SocketClosed:
                    Log.e(TAG, "Error_SocketClosed ... ");
                    FinishToMainController();
                    break;
                case CamWrapper.Error_LostConnection:
                    Log.e(TAG, "Error_LostConnection ...");
                    FinishToMainController();
                    break;
            }
        }
    }

    private void FinishToMainController() {
        Log.e(TAG, "Finish ...");
        CamWrapper.getComWrapperInstance().GPCamDisconnect();
       // ffmpegWrapper.getInstance().naStop();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private boolean isFastClick() {
        long currentTime = System.currentTimeMillis();

        long time = currentTime - mLastClickTime;
        if (0 < time && time < 5000) {
            return true;
        }

        mLastClickTime = currentTime;
        return false;
    }

    private final GSYStateUiListener gsyStateUiListener = new GSYStateUiListener() {
        @Override
        public void onStateChanged(int state) {  //5暂停，2播放
            Log.e(TAG,"-----state="+state);
//            if(state == 3){
//                if(videoPlayer != null)
//                    videoPlayer.onVideoPause();
//                //CamWrapper.getComWrapperInstance().GPCamSendPausePlayback();
//            }if(state == 2){
//                playVideoStreaming();
//            }

            if(state == 5){
                CamWrapper.getComWrapperInstance().GPCamSendPausePlayback();
            }

            if(state == 3){ //播放完了
                CamWrapper.getComWrapperInstance().GPCamSendPausePlayback();
                videoPlayer.onVideoReset();
                GSYVideoManager.instance().stop();
            }

//            if(state == 2){
//                playVideoStreaming();
//            }
        }
    };

    private final GSYVideoProgressListener gsyVideoProgressListener = new GSYVideoProgressListener() {
        @Override
        public void onProgress(int progress, int secProgress, int currentPosition, int duration) {
            Log.e(TAG,"-----progress="+progress+"\n"+secProgress+"\n"+currentPosition+"\n"+duration);
        }
    };
}
