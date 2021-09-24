package com.truescend.gofit.wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.truescend.gofit.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import generalplus.com.GPCamLib.CamWrapper;
import generalplus.com.GPCamLib.GPXMLParse;
import com.generalplus.ffmpegLib.ffmpegWrapper;

/**
 * 在线视频播放页面
 */
public class MainViewController extends Activity implements SurfaceHolder.Callback {

    private static String TAG = "MainViewController";
    private int FW_Old_Number = 0x01000000;   // V1.0.0.0

    private LinearLayout vlcContainer;
    // display surface
    private GLSurfaceView mSurfaceView;

    // media player
    private int mVideoWidth;
    private int mVideoHeight;
    private final static int VideoSizeChanged = -1;

    private FrameLayout vlcOverlay;
    private ImageButton imgbtn_file;
    private ImageButton imgbtn_audio;
    private ImageButton imgbtn_menu;
    private ImageButton imgbtn_disconnect;
    private ImageButton imgbtn_recordorcapture;
    private ImageButton imgbtn_mode;
    private ImageView imgview_battery_status;
    private ImageView imgview_multi_shot_status;
    private ImageView imgview_record_status;
    private ImageView imgview_audio_status;

    private TextView tv_res_status;
    private TextView tv_time_remain;
    //private TextView 			tv_status;

    private Handler handlerOverlay;
    private Runnable runnableOverlay;
    private Handler handlerSeekbar;
    private Runnable runnableSeekbar;

    private GPXMLParse m_GPXMLParse;
    private Context m_Context;
    private ProgressDialog m_Dialog = null;
    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_BEST_FIT;

    private static ArrayList<GPXMLParse.GPXMLCategory> m_xmlGategory = null;
    private static int mMainVideoWidth = 0;
    private static int mMainVideoVisibleWidth = 0;
    private static int mMainVideoHeight = 0;
    private static int mMainVideoVisibleHeight = 0;
    private static int mMainSarNum = 0;
    private static int mMainSarDen = 0;
    private String urlToStream = null;

    private final long timeToDisappear = 10 * 1000;
    private static boolean _bRunVLC = false;
    private static boolean _bSetModeDone = false;
    private static boolean _bSetRestartStreamingDone = false;
    private static int _CurrentMode = CamWrapper.GPDEVICEMODE_Record;
    private static boolean _ChangeToAnotherModeDone = true;
    private static boolean _bCurrentAudio = true;
    private static boolean _Recording = false;
    private static boolean _Capturing = false;
    private long mLastClickTime;
    private long mLastCapClickTime;
    private boolean m_bDelay = false;
    public static boolean m_bRtsp = false;
    private static ArrayList<BatteryRes> m_BatteryList;
    private boolean m_bVendorID = true;

    static private class BatteryRes {
        int BatteryIndex;
        int BatterResIndex;
    }

    private static ArrayList<RecordRes> m_RecordList;
    private static boolean isAnimating = false;

    static private class RecordRes {
        int RecordResIndex;
    }

    private boolean ReadDefaultMenu() {
       // String strXMLFilePath = String.format(Environment.getExternalStorageDirectory().getPath() + "/Download/" + CamWrapper.CamDefaulFolderName + "/" + CamWrapper.ParameterFileName);


        String strXMLFilePath = this.getExternalFilesDir(null).getPath()+"/" + CamWrapper.CamDefaulFolderName + "/" + CamWrapper.ParameterFileName;

        String tmpPath = this.getExternalFilesDir(null).getPath();

        Log.e(TAG,"-----strXMLFilePath="+strXMLFilePath+" "+tmpPath);


        if (m_GPXMLParse == null)
            m_GPXMLParse = new GPXMLParse();

        m_GPXMLParse.GetCategories();
        if (m_xmlGategory == null) {
            ArrayList<GPXMLParse.GPXMLCategory> xmlTempGategory = m_GPXMLParse
                    .GetGPXMLInfo(strXMLFilePath);

            if (xmlTempGategory.size() > 0) {
                CamWrapper.bIsDefault = false;
                ReadXml(strXMLFilePath);
                return true;
            }
        }

        copyDefaultXml();
       // ReadXml(String.format(Environment.getExternalStorageDirectory().getPath() + "/Download/" + CamWrapper.CamDefaulFolderName + "/" + CamWrapper.DefaultParameterFileName));

        ReadXml(String.format(getExternalFilesDir(null).getPath()+"/" + CamWrapper.CamDefaulFolderName + "/" + CamWrapper.DefaultParameterFileName));

        return false;
    }

    private void copyDefaultXml() {
        CamWrapper.bIsDefault = true;
        //File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/" + CamWrapper.CamDefaulFolderName + "/", CamWrapper.DefaultParameterFileName);

        File file = new File(getExternalFilesDir(null).getPath() + "/" + CamWrapper.CamDefaulFolderName + "/", CamWrapper.DefaultParameterFileName);

        if (!file.exists()) {
            AssetManager assetManager = getAssets();
            InputStream fIn = null;
            try {
                fIn = assetManager.open("Default_Menu.xml");

                OutputStream os = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                while (true) {
                    int bytesRead = fIn.read(buffer);
                    if (bytesRead == -1) break;
                    os.write(buffer, 0, bytesRead);
                }

                fIn.close();
                os.close();
            } catch (IOException e) {

                Log.e("tag", e.getMessage());
            }
        }
    }

    private void ReadXml(String strXMLFilePath) {
        if (m_GPXMLParse == null)
            m_GPXMLParse = new GPXMLParse();
        if (m_xmlGategory == null) {
            ArrayList<GPXMLParse.GPXMLCategory> xmlTempGategory = m_GPXMLParse
                    .GetGPXMLInfo(strXMLFilePath);
            m_xmlGategory = new ArrayList<GPXMLParse.GPXMLCategory>();
            m_xmlGategory.addAll(xmlTempGategory);

            String strFirmwareVersion = getFirmwareVersion();
            if (0 == strFirmwareVersion.length()) {
                CamWrapper.getComWrapperInstance().setIsNewFile(true);
            } else {
                String[] array = strFirmwareVersion.split("V");
                if (2 == array.length) {
                    try {

                        String[] dotArray = array[1].split("\\.");

                        int _FWVersion = 0;
                        int i32Shift = 24;
                        for (int i = 0; i < dotArray.length; i++) {
                            _FWVersion |= (Integer.valueOf(dotArray[i]) << i32Shift);
                            i32Shift -= 8;
                            if (i32Shift < 0)
                                break;
                        }

                        if (_FWVersion <= FW_Old_Number) {
                            CamWrapper.getComWrapperInstance().setIsNewFile(false);
                        } else {
                            CamWrapper.getComWrapperInstance().setIsNewFile(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate ...");

        setContentView(R.layout.activity_main_vlc_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        m_Context = MainViewController.this;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle b = getIntent().getExtras();
//        urlToStream = b.getString("url", null);

        int iSetTime = b.getInt("SetTime", 0);
        if (1 == iSetTime) {
            setVendorTime();
        }
//		setVendorID();
        CamWrapper.getComWrapperInstance().SetViewHandler(m_FromWrapperHandler, CamWrapper.GPVIEW_STREAMING);
        CamWrapper.getComWrapperInstance().GPCamSendGetStatus();
        CamWrapper.getComWrapperInstance().SetGPCamSendGetParameterFile(CamWrapper.ParameterFileName);

        vlcContainer = (LinearLayout) findViewById(R.id.vlc_container);
        mSurfaceView = (GLSurfaceView) findViewById(R.id.vlc_surface);

        vlcOverlay = (FrameLayout) findViewById(R.id.vlc_overlay);

        tv_res_status = (TextView) findViewById(R.id.tv_res_status);
        tv_time_remain = (TextView) findViewById(R.id.tv_time_remain);

        imgbtn_file = (ImageButton) findViewById(R.id.imgbtn_file);
        imgbtn_audio = (ImageButton) findViewById(R.id.imgbtn_audio);
        imgbtn_menu = (ImageButton) findViewById(R.id.imgbtn_menu);
        imgbtn_disconnect = (ImageButton) findViewById(R.id.imgbtn_disconnect);
        imgbtn_recordorcapture = (ImageButton) findViewById(R.id.imgbtn_recordorcapture);
        imgbtn_mode = (ImageButton) findViewById(R.id.imgbtn_mode);

        imgview_battery_status = (ImageView) findViewById(R.id.imgview_battery_status);
        imgview_multi_shot_status = (ImageView) findViewById(R.id.imgview_multi_shot_status);
        imgview_record_status = (ImageView) findViewById(R.id.imgview_record_status);
        imgview_audio_status = (ImageView) findViewById(R.id.imgview_audio_status);

        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setRenderer(ffmpegWrapper.getInstance());
        mSurfaceView.setKeepScreenOn(true);
    }

    private void setVendorID() {
        byte[] byStringData = new byte[8];
        byStringData[0] = 0x56;
        byStringData[1] = 0x45;
        byStringData[2] = 0x4E;
        byStringData[3] = 0x44;
        byStringData[4] = 0x4F;
        byStringData[5] = 0x52;
        byStringData[6] = 0x49;
        byStringData[7] = 0x44;
        CamWrapper.getComWrapperInstance().GPCamSendVendorCmd(byStringData, 8);
    }

    private void setVendorTime() {
        byte[] byStringData = new byte[17];
        byStringData[0] = 0x47;
        byStringData[1] = 0x50;
        byStringData[2] = 0x56;
        byStringData[3] = 0x45;
        byStringData[4] = 0x4E;
        byStringData[5] = 0x44;
        byStringData[6] = 0x4F;
        byStringData[7] = 0x52;
        byStringData[8] = 0x0;
        byStringData[9] = 0x0;

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        byStringData[10] = (byte) today.year;
        byStringData[11] = (byte) (today.year >>> 8);
        byStringData[12] = (byte) (today.month + 1);
        byStringData[13] = (byte) today.monthDay;
        byStringData[14] = (byte) today.hour;
        byStringData[15] = (byte) today.minute;
        byStringData[16] = (byte) today.second;
        CamWrapper.getComWrapperInstance().GPCamSendVendorCmd(byStringData, 17);
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
                if (m_bDelay) {
                    if (false == _bSetModeDone || false == _bSetRestartStreamingDone) {
                        Log.e(TAG, "return vlcContainer setOnClickListener _bSetModeDone = " + String.valueOf(_bSetModeDone)
                                + " _bSetRestartStreamingDone = " + String.valueOf(_bSetRestartStreamingDone));
                        return;
                    }
                }
                if (vlcOverlay.getVisibility() == View.VISIBLE)
                    vlcOverlay.setVisibility(View.GONE);
                else
                    vlcOverlay.setVisibility(View.VISIBLE);
                Log.e(TAG, "vlcContainer setOnClickListener");
                handlerOverlay.removeCallbacks(runnableOverlay);
                handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
            }
        });

        imgbtn_file.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFastClick()) {
                    return;
                }
                stopStreaming();
                Intent intent = new Intent();
                intent.setClass(MainViewController.this, FilesActivity.class);
                startActivity(intent);
            }

        });

        imgbtn_audio.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                handlerOverlay.removeCallbacks(runnableOverlay);
                handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
                CamWrapper.getComWrapperInstance().GPCamSendAudioOnOff(_bCurrentAudio);
                _bCurrentAudio = !_bCurrentAudio;
            }

        });

        imgbtn_menu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!_ChangeToAnotherModeDone)
                    return;
                if (isFastClick()) {
                    return;
                }
                copyDefaultXml();
                stopStreaming();
                Intent intent = new Intent();
                intent.setClass(MainViewController.this, SettingActivity.class);
                startActivity(intent);
            }

        });

        imgbtn_disconnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                stopStreaming();
                Finish();
            }

        });

        imgbtn_recordorcapture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                handlerOverlay.removeCallbacks(runnableOverlay);
                handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
                if (isRecordoCcaptureClick())
                    return;
                if (isFastClick()) {
                }
                if (_CurrentMode == CamWrapper.GPDEVICEMODE_Record) {
                    _ChangeToAnotherModeDone = false;
                    _Recording = true;
                    _Capturing = false;
                    CamWrapper.getComWrapperInstance().GPCamSendRecordCmd();
                } else if (_CurrentMode == CamWrapper.GPDEVICEMODE_Capture) {
                    _ChangeToAnotherModeDone = false;
                    _Recording = false;
                    _Capturing = true;
                    if (m_Dialog == null) {
                        m_Dialog = new ProgressDialog(m_Context);
                        m_Dialog.setCanceledOnTouchOutside(false);
                        m_Dialog.setMessage(getResources().getString(R.string.Capture));
                    }
                    m_Dialog.show();
                    CamWrapper.getComWrapperInstance().GPCamSendCapturePicture();
                }
            }

        });

        imgbtn_mode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                handlerOverlay.removeCallbacks(runnableOverlay);
                handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
                CharSequence[] mode = {getResources().getString(R.string.Record), getResources().getString(R.string.Capture)};

                AlertDialog.Builder builder = new AlertDialog.Builder(m_Context);
                builder.setTitle(getResources().getString(R.string.Mode));

                builder.setSingleChoiceItems(mode, _CurrentMode,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: // Record
                                        if (_Capturing)
                                            CamWrapper.getComWrapperInstance().GPCamSendCapturePicture();
                                        _bSetModeDone = false;
                                        CamWrapper.getComWrapperInstance().GPCamSendSetMode(CamWrapper.GPDEVICEMODE_Record);
                                        break;
                                    case 1: // Capture
                                        if (_Recording)
                                            CamWrapper.getComWrapperInstance().GPCamSendRecordCmd();
                                        _bSetModeDone = false;
                                        CamWrapper.getComWrapperInstance().GPCamSendSetMode(CamWrapper.GPDEVICEMODE_Capture);
                                        break;
                                }
                                _CurrentMode = which;
                                dialog.dismiss();
                            }

                        });
                builder.show();
            }

        });
    }

    private final Handler m_StatusHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == ffmpegWrapper.FFMPEG_STATUS_PLAYING) {

            } else if (msg.what == ffmpegWrapper.FFMPEG_STATUS_STOPPED) {

            }
        }

    };


    private boolean isRecordoCcaptureClick() {
        long currentTime = System.currentTimeMillis();

        long time = currentTime - mLastCapClickTime;
        if (0 < time && time < 500) {
            return true;
        }

        mLastCapClickTime = currentTime;
        return false;
    }

    private boolean isFastClick() {
        long currentTime = System.currentTimeMillis();

        long time = currentTime - mLastClickTime;
        if (0 < time && time < 1500) {
            return true;
        }

        mLastClickTime = currentTime;
        return false;
    }

    public void initStreaming() {
        if (ffmpegWrapper.getInstance().naStatus() == ffmpegWrapper.ePlayerStatus.E_PlayerStatus_Playing.ordinal())
            return;

        mSurfaceView.onPause();

        ffmpegWrapper.getInstance().naSetStreaming(true);
        ffmpegWrapper.getInstance().naInitAndPlay(urlToStream, "");

        mSurfaceView.onResume();
    }

    private void playStreaming() {
        if (ffmpegWrapper.getInstance().naStatus() == ffmpegWrapper.ePlayerStatus.E_PlayerStatus_Playing.ordinal())
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {

                int i32RetryCount = 50;

                _bSetModeDone = false;
                _bSetRestartStreamingDone = false;

                Log.e(TAG, "playStreaming SendSetMode = " + _CurrentMode);
                CamWrapper.getComWrapperInstance().GPCamSendSetMode(_CurrentMode);
                while (!_bSetModeDone && i32RetryCount > 0) {
                    try {
                        Thread.sleep(100);
                        i32RetryCount--;
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (_bSetModeDone) {
                    //ffmpegWrapper.getInstance().naPlay();
                    _bSetRestartStreamingDone = false;
                    CamWrapper.getComWrapperInstance().GPCamSendRestartStreaming();
                    playVLC();
                }
            }
        }).start();
    }

    private void stopStreaming() {
        Log.e(TAG, "stopStreaming");
        if (_Capturing)
            CamWrapper.getComWrapperInstance().GPCamSendCapturePicture();
        if (_Recording)
            CamWrapper.getComWrapperInstance().GPCamSendRecordCmd();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        _bRunVLC = false;
        ffmpegWrapper.getInstance().naStop();
        Log.e(TAG, "stopStreaming mLibVLC.stop();");
    }

    private void playVLC() {
        if (PlayVLCThread == null) {
            PlayVLCThread = new Thread(new PlayVLCRunnable());
            PlayVLCThread.start();
        }
    }

    private static Thread PlayVLCThread = null;

    class PlayVLCRunnable implements Runnable {

        private int i32RepeatCount = 3; // 3s

        @Override
        public void run() {

            _bRunVLC = true;
            if (GetGPCamStatusThread == null) {
                GetGPCamStatusThread = new Thread(new GetGPCamStatusRunnable());
                GetGPCamStatusThread.start();
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            while (_bRunVLC && i32RepeatCount >= 0) {

                if (ffmpegWrapper.getInstance().naStatus() != ffmpegWrapper.ePlayerStatus.E_PlayerStatus_Playing.ordinal()) {
                    initStreaming();
                    //ffmpegWrapper.getInstance().naPlay();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                i32RepeatCount--;
            }
            if (null != PlayVLCThread) {
                PlayVLCThread.interrupt();
                PlayVLCThread = null;
            }
        }
    }

    private static Thread GetGPCamStatusThread = null;

    class GetGPCamStatusRunnable implements Runnable {

        private int i32DelayTime = 500;            // in ms

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (_bRunVLC) {
                CamWrapper.getComWrapperInstance().GPCamSendGetStatus();
                try {
                    Thread.sleep(i32DelayTime);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            GetGPCamStatusThread.interrupt();
            GetGPCamStatusThread = null;
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

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format,
                               int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {

    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume ...");
        super.onResume();
        setupControls();
        if (null != urlToStream) {
            playStreaming();
        }
        CamWrapper.getComWrapperInstance().SetViewHandler(m_FromWrapperHandler, CamWrapper.GPVIEW_STREAMING);

        m_bDelay = true;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                m_bDelay = false;
            }
        }, 10000);
        mSurfaceView.onResume();
    }

    @Override
    public void onBackPressed() {
        if (m_bDelay) {
            if (false == _bSetModeDone || false == _bSetRestartStreamingDone) {
                return;
            }
        }
        stopStreaming();
        Finish();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause ...");
        super.onPause();
        CamWrapper.getComWrapperInstance().SetViewHandler(null, CamWrapper.GPVIEW_STREAMING);
        mSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy ...");
        if (null != m_xmlGategory) {
            m_xmlGategory.clear();
            m_xmlGategory = null;
        }
        dismissProgressDialog();
        super.onDestroy();
        Finish();
    }

    private void Finish() {
        CamWrapper.getComWrapperInstance().GPCamDisconnect();
        ffmpegWrapper.getInstance().naStop();
        finish();
    }

    /*************
     * Events
     *************/

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

        if (i32CmdType == CamWrapper.GP_SOCK_TYPE_ACK) {
            switch (i32Mode) {
                case CamWrapper.GPSOCK_MODE_General:
                    switch (i32CmdID) {
                        case CamWrapper.GPSOCK_General_CMD_SetMode:
                            _bSetModeDone = true;
                            Log.e(TAG, "_bSetModeDone = true");
                            break;
                        case CamWrapper.GPSOCK_General_CMD_GetDeviceStatus:
                            boolean bParseSendSetMode = false;
                            switch ((pbyData[0] & 0xFF)) {
                                case CamWrapper.GPDEVICEMODE_Record:
                                    _CurrentMode = CamWrapper.GPDEVICEMODE_Record;
                                    if ((pbyData[1] & 0x01) == 0x00) {
                                        AnimateRecord(false);
                                        _Recording = false;
                                    } else {
                                        AnimateRecord(true);
                                        _Recording = true;
                                    }
                                    AnimateAudio(((((pbyData[1] & 0xFF) >> 1) & 0x01) == 0x00) ? true
                                            : false);
                                    ShowMultiShot(false);
                                    ShowResource(pbyData[4] & 0xFF, true);
                                    ShowTime((pbyData[5] & 0xFF)
                                            + ((pbyData[6] & 0xFF) << 8)
                                            + ((pbyData[7] & 0xFF) << 16)
                                            + ((pbyData[8] & 0xFF) << 24), true);
                                    ShowModePIC(true);
                                    break;
                                case CamWrapper.GPDEVICEMODE_Capture:
                                    _CurrentMode = CamWrapper.GPDEVICEMODE_Capture;
                                    AnimateRecord(false);
                                    if ((pbyData[1] & 0x01) == 0x00) {
                                        _Capturing = false;
                                        ShowMultiShot(false);
                                    } else {
                                        _Capturing = true;
                                        ShowMultiShot(true);
                                    }
                                    AnimateAudio(false);
                                    ShowResource(pbyData[9] & 0xFF, false);
                                    ShowTime((pbyData[10] & 0xFF)
                                            + ((pbyData[11] & 0xFF) << 8)
                                            + ((pbyData[12] & 0xFF) << 16)
                                            + ((pbyData[13] & 0xFF) << 24), false);
                                    ShowModePIC(false);
                                    break;
                                default:
                                    if (_CurrentMode != (pbyData[0] & 0xFF)) {
                                        _bSetModeDone = false;
                                        Log.e(TAG, "GetDeviceStatus SendSetMode = " + _CurrentMode);
                                        bParseSendSetMode = true;
                                        CamWrapper.getComWrapperInstance().GPCamSendSetMode(_CurrentMode);
                                    }
                                    AnimateRecord(false);
                                    AnimateAudio(false);
                                    ShowMultiShot(false);
                                    ShowModePIC(false);
                                    break;
                            }
                            if ((((pbyData[1] & 0xFF) >> 7) & 0x01) == 0x01) {
                                _bRunVLC = false;
                                try {    //It's waiting for recording ready.
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                if (PlayVLCThread == null) {
                                    _bRunVLC = true;
                                    PlayVLCThread = new Thread(new PlayVLCRunnable());
                                    PlayVLCThread.start();
                                    Log.e(TAG, "Reset VLC.");
                                }

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(m_Context, getResources().getString(R.string.Reset_VLC),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            if (urlToStream == null) {
//						if (m_bVendorID) {
                                if (1 == (pbyData[2] & 0xFF) >> 7) {
                                    urlToStream = String.format(CamWrapper.RTSP_STREAMING_URL, CamWrapper.COMMAND_URL);
                                    CamWrapper.getComWrapperInstance().GPCamSetFileNameMapping(CamWrapper.GP22_DEFAULT_MAPPING_STR);
                                    m_bRtsp = true;
                                    CamWrapper.getComWrapperInstance().GPCamCheckFileMapping();
                                } else {
                                    urlToStream = String.format(CamWrapper.STREAMING_URL, CamWrapper.COMMAND_URL);
                                    CamWrapper.getComWrapperInstance().GPCamSetFileNameMapping(CamWrapper.DEFAULT_MAPPING_STR);
                                    m_bRtsp = false;
                                }

                                playStreaming();
//						}
                            }

                            int byLevel = (pbyData[2] & 0xFF) - ((pbyData[2] & 0xFF) >> 7) * 128;
                            ShowBattery((byte) byLevel, ((pbyData[3] & 0x01) == 0x00) ? false : true);
                            if (false == bParseSendSetMode) {
                                _CurrentMode = (pbyData[0] & 0xFF);
                            }

                            if (1 == (pbyData[3] & 0xFF) >> 7) {
                                FilesActivity.m_bCanDeleteSDFile = true;
                            } else {
                                FilesActivity.m_bCanDeleteSDFile = false;
                            }
                            break;
                        case CamWrapper.GPSOCK_General_CMD_GetParameterFile:
                            ReadDefaultMenu();
                            break;
                        case CamWrapper.GPSOCK_General_CMD_RestartStreaming:
                            _bSetRestartStreamingDone = true;
                            break;
                        case CamWrapper.GPSOCK_General_CMD_CheckMapping:
                            CamWrapper.getComWrapperInstance().GPCamSetFileNameMapping(CamWrapper.DEFAULT_MAPPING_STR);
                            Log.e(TAG, "Ack CheckMapping");
                            break;
                    }
                    break;
                case CamWrapper.GPSOCK_MODE_Record:
                    Log.e(TAG, "GPSOCK_MODE_Record ... ");
                    try {    //It's waiting for recording ready.
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    _ChangeToAnotherModeDone = true;
                    break;
                case CamWrapper.GPSOCK_MODE_CapturePicture:
                    Log.e(TAG, "GPSOCK_MODE_CapturePicture ... ");
                    try {        //It's waiting for capturing picture ready.
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    _ChangeToAnotherModeDone = true;
                    dismissProgressDialog();
                    break;
                case CamWrapper.GPSOCK_MODE_Playback:
                    Log.e(TAG, "GPSOCK_MODE_Playback ... ");
                    break;
                case CamWrapper.GPSOCK_MODE_Menu:
                    Log.e(TAG, "GPSOCK_MODE_Menu ... ");
                    break;
                case CamWrapper.GPSOCK_MODE_Vendor:
                    Log.e(TAG, "GPSOCK_MODE_Vendor ... ");
//				try {
//					final String str = new String(pbyData, "UTF-8");
//					if (null != str) {
//						if (str.equalsIgnoreCase("VENDORID1")) {
//							m_bVendorID = true;
//						}
//						else if (str.equalsIgnoreCase("VENDORID0") || str.equalsIgnoreCase("VENDORID")) {
//							finish();
//						}
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
                    break;
            }
        } else if (i32CmdType == CamWrapper.GP_SOCK_TYPE_NAK) {
            int i32ErrorCode = (pbyData[0] & 0xFF) + ((pbyData[1] & 0xFF) << 8);

            if (_Recording || _Capturing)
                _ChangeToAnotherModeDone = true;

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
                    if (_Recording || _Capturing) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(m_Context, getResources().getString(R.string.No_Storage),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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
                    Finish();
                    break;
                case CamWrapper.Error_LostConnection:
                    Log.e(TAG, "Error_LostConnection ...");
                    Finish();
                    break;
            }
            dismissProgressDialog();
        }
    }

    private void ShowBattery(byte byLevel, boolean bCharge) {

        if (m_BatteryList == null) {
            m_BatteryList = new ArrayList<BatteryRes>();
            BatteryRes battery0 = new BatteryRes();
            battery0.BatterResIndex = CamWrapper.GPBATTERTY_LEVEL0;
            battery0.BatterResIndex = R.drawable.battery_level0;
            m_BatteryList.add(battery0);

            BatteryRes battery1 = new BatteryRes();
            battery1.BatterResIndex = CamWrapper.GPBATTERTY_LEVEL1;
            battery1.BatterResIndex = R.drawable.battery_level1;
            m_BatteryList.add(battery1);

            BatteryRes battery2 = new BatteryRes();
            battery2.BatterResIndex = CamWrapper.GPBATTERTY_LEVEL2;
            battery2.BatterResIndex = R.drawable.battery_level2;
            m_BatteryList.add(battery2);

            BatteryRes battery3 = new BatteryRes();
            battery3.BatterResIndex = CamWrapper.GPBATTERTY_LEVEL3;
            battery3.BatterResIndex = R.drawable.battery_level3;
            m_BatteryList.add(battery3);

            BatteryRes battery4 = new BatteryRes();
            battery4.BatterResIndex = CamWrapper.GPBATTERTY_LEVEL4;
            battery4.BatterResIndex = R.drawable.battery_level4;
            m_BatteryList.add(battery4);

            BatteryRes battery5 = new BatteryRes();
            battery5.BatterResIndex = CamWrapper.GPBATTERTY_GHARGE;
            battery5.BatterResIndex = R.drawable.battery_charge;
            m_BatteryList.add(battery5);
        }

        int i32Idx = byLevel;

        if (bCharge)
            i32Idx = CamWrapper.GPBATTERTY_GHARGE;

        if (i32Idx > CamWrapper.GPBATTERTY_GHARGE || i32Idx < 0)
            i32Idx = CamWrapper.GPBATTERTY_GHARGE;

        final BatteryRes battery = m_BatteryList.get(i32Idx);

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                imgview_battery_status.setImageResource(battery.BatterResIndex);
            }

        });
    }

	/*private void ShowStatus(byte[] pbyData) {
		final StringBuilder sb = new StringBuilder();
		for (byte b : pbyData) {
			sb.append(String.format("%02X ", b));
		}

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				tv_status.setText(sb.toString());
			}

		});
	}*/

    private void ShowMultiShot(boolean bEnable) {
        final boolean bMultiShot = bEnable;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (bMultiShot)
                    imgview_multi_shot_status.setVisibility(View.VISIBLE);
                else
                    imgview_multi_shot_status.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void ShowResource(int ValuesIndex, boolean RecordMode) {
        if (m_GPXMLParse == null) {
            return;
        }
        if (m_xmlGategory == null || m_xmlGategory.size() == 0) {
           // String strXMLFilePath = String.format(Environment.getExternalStorageDirectory().getPath() + "/" + CamWrapper.CamDefaulFolderName + "/" + CamWrapper.ParameterFileName);

            String strXMLFilePath = String.format(getExternalFilesDir(null).getPath() + "/" + CamWrapper.CamDefaulFolderName + "/" + CamWrapper.ParameterFileName);

            ArrayList<GPXMLParse.GPXMLCategory> xmlTempGategory = m_GPXMLParse
                    .GetGPXMLInfo(strXMLFilePath);
            if (m_xmlGategory == null)
                m_xmlGategory = new ArrayList<GPXMLParse.GPXMLCategory>();
            m_xmlGategory.clear();
            m_xmlGategory.addAll(xmlTempGategory);
        }

        String strValueName = "";
        if (m_xmlGategory == null) {
            strValueName = "XML NULL";
        } else {
            if (m_xmlGategory.size() <= 0) {
                strValueName = "XML length 0";
            }
        }

        if (m_xmlGategory != null && m_xmlGategory.size() > 0) {


            for (int i32CategoryIndex = 0; i32CategoryIndex < m_xmlGategory
                    .size(); i32CategoryIndex++) {
                for (int i32SettingIndex = 0; i32SettingIndex < m_xmlGategory
                        .get(i32CategoryIndex).aryListGPXMLSettings.size(); i32SettingIndex++) {

                    if (RecordMode) {
                        if (Long.decode(m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                                .get(i32SettingIndex).strXMLSettingID) == GPXMLParse.RecordResolution_Setting_ID) {
                            if (ValuesIndex >= m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                                    .get(i32SettingIndex).aryListGPXMLValues.size()) {
                                strValueName = getResources().getString(R.string.Record_resolution_unknown);
                            } else {
                                strValueName = m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                                        .get(i32SettingIndex).aryListGPXMLValues.get(ValuesIndex).strXMLValueName;
                            }
                        }
                    } else {
                        if (Long.decode(m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                                .get(i32SettingIndex).strXMLSettingID) == GPXMLParse.CaptureResolution_Setting_ID) {
                            if (ValuesIndex >= m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                                    .get(i32SettingIndex).aryListGPXMLValues.size()) {
                                strValueName = getResources().getString(R.string.Capture_resolution_unknown);
                            } else {
                                strValueName = m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                                        .get(i32SettingIndex).aryListGPXMLValues.get(ValuesIndex).strXMLValueName;
                            }
                        }
                    }
                }
            }

            final String strName = strValueName;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tv_res_status.setText(strName);
                }
            });
        } else {
            tv_res_status.setVisibility(View.INVISIBLE);
        }
    }

    private void ShowTime(int SecRemain, boolean RecordMode) {
        String strTempSecRemain = "";
        if (RecordMode) {
            int i32SecLeft = SecRemain % 60;
            int i32MinLeft = (SecRemain / 60) % 60;
            int i32HourLeft = SecRemain / 3600;
            strTempSecRemain = String.format("%02d:%02d:%02d", i32HourLeft,
                    i32MinLeft, i32SecLeft);
        } else
            strTempSecRemain = String.format("%d", SecRemain);

        final String strSecRemain = strTempSecRemain;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                tv_time_remain.setText(strSecRemain);
            }
        });
    }

    private void ShowModePIC(boolean RecordMode) {
        final boolean bRecordMode = RecordMode;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (bRecordMode) {
                    imgbtn_mode.setImageResource(R.drawable.mode_dv);
                    imgbtn_audio.setVisibility(View.VISIBLE);
                } else {
                    imgbtn_mode.setImageResource(R.drawable.mode_dc);
                    imgbtn_audio.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void AnimateAudio(boolean bEnable) {
        final boolean bEnableAudio = bEnable;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (bEnableAudio)
                    imgview_audio_status.setVisibility(View.VISIBLE);
                else
                    imgview_audio_status.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void AnimateRecord(boolean bEnable) {
        if (m_RecordList == null) {
            m_RecordList = new ArrayList<RecordRes>();
            RecordRes record0 = new RecordRes();
            record0.RecordResIndex = R.drawable.record_1;
            m_RecordList.add(record0);

            RecordRes record1 = new RecordRes();
            record1.RecordResIndex = R.drawable.record_2;
            m_RecordList.add(record1);
        }

        final boolean bEnableRecord = bEnable;

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (bEnableRecord) {
                    if (isAnimating)
                        imgview_record_status.setImageResource(m_RecordList
                                .get(1).RecordResIndex);
                    else
                        imgview_record_status.setImageResource(m_RecordList
                                .get(0).RecordResIndex);
                    isAnimating = !isAnimating;

                } else {
                    imgview_record_status
                            .setImageResource(m_RecordList.get(0).RecordResIndex);
                    isAnimating = false;
                }
            }
        });
    }

    private String getFirmwareVersion() {
        String strFirmwareVersion = "";
        if (m_xmlGategory != null && m_xmlGategory.size() > 0) {

            for (int i32CategoryIndex = 0; i32CategoryIndex < m_xmlGategory
                    .size(); i32CategoryIndex++) {
                for (int i32SettingIndex = 0; i32SettingIndex < m_xmlGategory
                        .get(i32CategoryIndex).aryListGPXMLSettings.size(); i32SettingIndex++) {

                    if (Long.decode(m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                            .get(i32SettingIndex).strXMLSettingID) == GPXMLParse.Version_Setting_ID) {
                        strFirmwareVersion = m_xmlGategory
                                .get(i32CategoryIndex).aryListGPXMLSettings
                                .get(i32SettingIndex).aryListGPXMLValues
                                .get(GPXMLParse.Version_Value_Index).strXMLValueName
                                .toString();
                    }
                }
            }
        }
        return strFirmwareVersion;
    }

    private void dismissProgressDialog() {
        if (m_Dialog != null) {
            if (m_Dialog.isShowing()) {
                Log.e(TAG, "m_Dialog.dismiss();");
                m_Dialog.dismiss();
                m_Dialog = null;
            }
        }
    }
}
