package com.truescend.gofit.wifi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.truescend.gofit.R;

import generalplus.com.GPCamLib.CamWrapper;
import com.generalplus.ffmpegLib.ffmpegWrapper;

import java.util.Arrays;

/**
 * 播放视频页面
 */
public class FileViewController extends Activity implements SurfaceHolder.Callback {

    private static String TAG = "FileViewController";

    private LinearLayout vlcContainer;
    private GLSurfaceView mSurfaceView;

    private FrameLayout vlcOverlay;

    private ImageButton imgbtn_playpause;

    private Handler handlerOverlay;
    private Runnable runnableOverlay;

    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_BEST_FIT;

    // media player
    private static int mVideoWidth;
    private static int mVideoHeight;
    private static int mVideoVisibleWidth;
    private static int mVideoVisibleHeight;
    private static int mSarNum;
    private static int mSarDen;
    private String _urlToStream;
    private int _FileFlag;
    private int _FileIndex;
    private final static int VideoSizeChanged = -1;
    private final long timeToDisappear = 10 * 1000;
    private static boolean _bRunVLC = false;
    private static boolean _bIsPause = false;
    private long mLastClickTime;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_files_vlc_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        vlcContainer = (LinearLayout) findViewById(R.id.vlc_container);
        mSurfaceView = (GLSurfaceView) findViewById(R.id.vlc_surface);

        vlcOverlay = (FrameLayout) findViewById(R.id.vlc_overlay);
        imgbtn_playpause = (ImageButton) findViewById(R.id.imgbtn_playpause);

        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setRenderer(ffmpegWrapper.getInstance());
        mSurfaceView.setKeepScreenOn(true);
        mContext = FileViewController.this;
        ffmpegWrapper.getInstance().SetViewHandler(m_StatusHandler);

    }

    private void setupControls() {
       // getActionBar().hide();

        vlcContainer.setVisibility(View.VISIBLE);

        // OVERLAY
        handlerOverlay = new Handler();
        runnableOverlay = new Runnable() {
            @Override
            public void run() {
                vlcOverlay.setVisibility(View.GONE);
            }
        };

        handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
        vlcOverlay.setVisibility(View.GONE);
        toggleFullscreen(true);
        vlcContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vlcOverlay.getVisibility() == View.VISIBLE)
                    vlcOverlay.setVisibility(View.GONE);
                else
                    vlcOverlay.setVisibility(View.VISIBLE);

                handlerOverlay.removeCallbacks(runnableOverlay);
                handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
            }
        });

        imgbtn_playpause.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                stopStreaming();
            }

        });
    }

    private void playLocalFile() {
        ffmpegWrapper.getInstance().naSetStreaming(false);
        ffmpegWrapper.getInstance().naInitAndPlay(_urlToStream, "");
    }

    private void playVideoStreaming() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlToStream = "";
                if (MainViewController.m_bRtsp) {
                    urlToStream = String.format(CamWrapper.RTSP_STREAMING_URL, CamWrapper.COMMAND_URL);
                } else {
                    urlToStream = String.format(CamWrapper.STREAMING_URL, CamWrapper.COMMAND_URL);
                }
                ffmpegWrapper.getInstance().naStop();
                CamWrapper.getComWrapperInstance().GPCamSendRestartStreaming();
                CamWrapper.getComWrapperInstance().GPCamSendStartPlayback(_FileIndex);

                Log.e(TAG,"------在线播放流="+urlToStream);

                ffmpegWrapper.getInstance().naInitAndPlay(urlToStream, "timeout=-1;stimeout=-1");

    	    	/*if(PlayVLCThread == null)
    	        {
    	        	PlayVLCThread = new Thread(new PlayVLCRunnable());
    	    		PlayVLCThread.start();
    	        }*/
            }
        }).start();
    }

    //读取图片流
    private void playPictureStreaming() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlToStream = "";
                if (MainViewController.m_bRtsp) {
                    urlToStream = String.format(CamWrapper.RTSP_STREAMING_URL, CamWrapper.COMMAND_URL);
                } else {
                    urlToStream = String.format(CamWrapper.STREAMING_URL, CamWrapper.COMMAND_URL);
                }
                ffmpegWrapper.getInstance().naStop();
                CamWrapper.getComWrapperInstance().GPCamClearCommandQueue();
                CamWrapper.getComWrapperInstance().GPCamSendRestartStreaming();
                Log.e(TAG,"-------图片流="+urlToStream);
                ffmpegWrapper.getInstance().naInitAndPlay(urlToStream, "");
    	    	/*if(PlayVLCThread == null)
    	        {
    	        	PlayVLCThread = new Thread(new PlayVLCRunnable());
    	    		PlayVLCThread.start();
    	        }*/
                for (int i = 0; i < 4; i++)
                    CamWrapper.getComWrapperInstance().GPCamSendStartPlayback(_FileIndex);
            }
        }).start();
    }

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

    private static Thread PlayVLCThread = null;

    class PlayVLCRunnable implements Runnable {

        private int i32RepeatCount = 30; // 3s

        @Override
        public void run() {

            _bRunVLC = true;

            if (_FileFlag == CamWrapper.GPFILEFLAG_AVISTREAMING) {
                try {
                    ffmpegWrapper.getInstance().naStop();
                    Thread.sleep(50);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            while (_bRunVLC && i32RepeatCount >= 0) {

                if (ffmpegWrapper.getInstance().naStatus() != ffmpegWrapper.ePlayerStatus.E_PlayerStatus_Playing.ordinal())
                    ffmpegWrapper.getInstance().naPlay();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                i32RepeatCount--;
            }
            PlayVLCThread = null;
        }
    }

    private void toggleFullscreen(boolean fullscreen) {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (fullscreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//            vlcContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        getWindow().setAttributes(attrs);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e(TAG, "onConfigurationChanged ...");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();


        Bundle b = getIntent().getExtras();
        _urlToStream = b.getString(CamWrapper.GPFILECALLBACKTYPE_FILEURL, null);
        _FileFlag = b.getInt(CamWrapper.GPFILECALLBACKTYPE_FILEFLAG, 0);
        _FileIndex = b.getInt(CamWrapper.GPFILECALLBACKTYPE_FILEINDEX, 0);


        Log.e(TAG,"---_urlToStream="+_urlToStream+"\n"+_FileFlag+"\n"+_FileIndex);

        setupControls();
        initStreaming();

        imgbtn_playpause.setVisibility(View.VISIBLE);
        if (_urlToStream.isEmpty())    // Streaming
        {
            CamWrapper.getComWrapperInstance().SetViewHandler(m_FromWrapperHandler, CamWrapper.GPVIEW_STREAMING);

            ffmpegWrapper.getInstance().naSetStreaming(true);
            ffmpegWrapper.getInstance().naSetBufferingTime(3 * 1000);

            if (_FileFlag == CamWrapper.GPFILEFLAG_JPGSTREAMING) {
                playPictureStreaming();
                imgbtn_playpause.setVisibility(View.INVISIBLE);
            } else
                playVideoStreaming();
        } else                        // Local File
        {
            playLocalFile();
        }
        mSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause ...");
        super.onPause();
        mSurfaceView.onPause();
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

    private ProgressDialog m_Dialog;
    private final Handler m_StatusHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == ffmpegWrapper.FFMPEG_STATUS_PLAYING) {
                if (m_Dialog != null) {
                    if (m_Dialog.isShowing()) {
                        m_Dialog.dismiss();
                        m_Dialog = null;
                    }
                }
            } else if (msg.what == ffmpegWrapper.FFMPEG_STATUS_BUFFERING) {
                try {
                    if (m_Dialog == null) {
                        m_Dialog = new ProgressDialog(mContext);
                        m_Dialog.setCanceledOnTouchOutside(false);
                        m_Dialog.setMessage(getResources().getString(R.string.Buffering));
                    }
                    if(m_Dialog != null && m_Dialog.isShowing())
                        m_Dialog.show();
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }

    };


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        ffmpegWrapper.getInstance().naSetBufferingTime(0);
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
        Finish();
    }

    private void Finish() {
        Log.e(TAG, "Finish ...");
        ffmpegWrapper.getInstance().naStop();
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format,
                               int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
    }

    public void initStreaming() {
        if (ffmpegWrapper.getInstance().naStatus() == ffmpegWrapper.ePlayerStatus.E_PlayerStatus_Playing.ordinal())
            return;
    }

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
        ffmpegWrapper.getInstance().naStop();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
