package com.generalplus.ffmpegLib;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ffmpegWrapper implements GLSurfaceView.Renderer {

    private final static String TAG = "ffmpegWrapper";
    private static ffmpegWrapper m_Instance;
    private static Handler	m_NowViewHandler;

    public final static String LOW_LOADING_TRANSCODE_OPTIONS = "qmin=15;qmax=35;b=400000;g=15;bf=0;refs=2;weightp=simple;level=2.2;" +
                                                               "x264-params=lookahead-threads=3:subme=4:chroma_qp_offset=0";

    public final static int FFMPEG_STATUS_PLAYING			            = 0x00;
    public final static int FFMPEG_STATUS_STOPPED			            = 0x01;
    public final static int FFMPEG_STATUS_SAVESNAPSHOTCOMPLETE			= 0x02;
    public final static int FFMPEG_STATUS_SAVEVIDEOCOMPLETE			    = 0x03;
    public final static int FFMPEG_STATUS_BUFFERING			            = 0x04;

    public final static int EXTRACTOR_OK                            = 0;
    public final static int EXTRACTOR_BUSY                          = 1;
    public final static int EXTRACTOR_READFILEFAILED                = 2;
    public final static int EXTRACTOR_DECODEFAILED                  = 3;
    public final static int EXTRACTOR_NOSUCHFRAME                   = 4;

    public final static int CODEC_ID_NONE                          = 0;
    public final static int CODEC_ID_MJPEG                         = 8;
    public final static int CODEC_ID_H264                          = 28;

    public enum eFFMPEG_ERRCODE
    {
        FFMPEGPLAYER_NOERROR,				    //0
        FFMPEGPLAYER_INITMEDIAFAILED,	        //1
        FFMPEGPLAYER_MEDIAISPLAYING,	        //2
        FFMPEGPLAYER_CREATESAVESTREAMFAILED,	//3
        FFMPEGPLAYER_SAVESNAPSHOTFAILED,        //4
        FFMPEGPLAYER_SAVEVIDEOFAILED,	        //5

    };

    public enum ePlayerStatus
    {
        E_PlayerStatus_Stoped,
        E_PlayerStatus_Playing,
        E_PlayerStatus_Stoping,

    };

    public enum eDisplayScale
    {
        E_DisplayScale_Fit,
        E_DisplayScale_Fill,
        E_DisplayScale_Stretch,

    };

    public enum eEncodeContainer
    {
        E_EncodeContainer_MP4 ,
        E_EncodeContainer_AVI ,

    };

    //----------------------------------------------------------------------
    static {
        try {
            Log.i(TAG, "Trying to load ffmpeg.so ...");

            System.loadLibrary("ffmpeg");
        } catch (UnsatisfiedLinkError Ule) {
            Log.e(TAG, "Cannot load ffmpeg.so ...");
            Ule.printStackTrace();
        } finally {
        }
    }

    public ffmpegWrapper()
    {
        m_Instance = this;
    }

    public static ffmpegWrapper getInstance()
    {
        return m_Instance;
    }


    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        Log.e(TAG, "onSurfaceCreated ... ");
        naInitDrawFrame();
    }

    public void onDrawFrame(GL10 unused) {

        naDrawFrame();

    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {

        Log.e(TAG, "onSurfaceChanged ... ");
        naSetup(width, height);
    }

    /**
     * \brief
     * 	Set The player status change notification handler.
     *
     * \param[in] ViewHandler
     *	The handler.
     *
     */
    public void SetViewHandler(Handler ViewHandler)
    {
        m_NowViewHandler = ViewHandler;
    }


    /**
     * \brief
     * 	Set The player status change notification.
     *
     * \param[in] i32Status
     *	The status. FFMPEG_STATUS_PLAYING => Player is playing , FFMPEG_STATUS_STOPPED => Player is stop play.
     *              FFMPEG_STATUS_SAVESNAPSHOTCOMPLETE is saving snapshot complete  , FFMPEG_STATUS_SAVEVIDEOCOMPLETE is ssaving video complete .
     *
     */
    void StatusChange(int i32Status)
    {

        if(m_NowViewHandler != null)
        {
            Message msg = new Message();
            msg.what = i32Status;
            m_NowViewHandler.sendMessage(msg);
        }

    }

    /**
     * \brief
     * 	Set the streaming path and play the streaming.
     *
     * \param[in] pFileName
     *	The streaming path.
     * \param[in] pOptions
     *	The option for streaming.The option string format is "option1=argument1;option2=argument2;...".
     *  Ex: RTSP streaming over TCP "rtsp_transport=tcp".
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naInitAndPlay(String pFileName,String pOptions);

    /**
     * \brief
     * 	Get the resolution of streaming .
     *
     * \return
     *	The resolution array.
     */
    public static native int[] naGetVideoRes();

    /**
     * \brief
     * 	Set the surface width and height.
     *
     * \param[in] pWidth
     *	The Surface width.
     * \param[in] pHeight
     *	The Surface height.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSetup(int pWidth, int pHeight);

    /**
     * \brief
     * 	Play the streaming.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     *
     */
    public static native int naPlay();

    /**
     * \brief
     * 	Stop the streaming.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     *
     */
    public static native int naStop();

    /**
     * \brief
     * 	Pause the streaming.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     *
     */
    public static native int naPause();

    /**
     * \brief
     * 	Resume the streaming.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     *
     */
    public static native int naResume();

    /**
     * \brief
     * 	Seek the streaming to postion.
     *
     * \details
     *  Only for playing local file.
     *
     * \param[in] position
     *	The file position (microsecond).
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSeek(long lPos);

    /**
     * \brief
     * 	Set the streaming is from network or not.
     *
     * \details
     *  Enable streaming mode will playing the streaming in low lantency.
     *
     * \param[in] bEnable
     *	Enable/Disable streaming mode.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSetStreaming(boolean bEnable);


    /**
     * \brief
     * 	Set encoding the stream by using local timestamp which is the time shows on the screen.
     *
     * \details
     *  Enable this will ignoring timestamp from the stream and MJPEG streaming will increase file size.
     *
     * \param[in] bEnable
     *	Enable/Disable encoding the stream by using local timestamp. Default is disable.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSetEncodeByLocalTime(boolean bEnable);


    /**
     * \brief
     *  Set display debug message on screen. (Mjpeg only).
     *
     * \param[in] bEnable
     *  Enable/Disable display debug message on screen.
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSetDebugMessage(boolean bRepeat);


    /**
     * \brief
     * 	Set play the streaming repeatedly.
     *
     * \details
     *  Only for playing local file.
     *
     * \param[in] bRepeat
     *	Enable/Disable repeat.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSetRepeat(boolean bRepeat);


    /**
     * \brief
     *     Enable force to transcode during saving stream.
     *
     * \details
     *  Saving streaming to file directly now by default. Enable this option
     *  will do transcode frames to H264 before saving to file.
     *  This option is only for mjpeg streaming.
     *  Android is using x264 library(software) to transcode streaming,and iOS is using videotoobox(hardware).
     *
     * \param[in] bEnable
     *    Enable/Disable transcode.
     */

    public static native int naSetForceToTranscode(boolean bEnable);

    /**
     * \brief
     * 	Get the streaming duration.
     *
     * \details
     *  Only for playing local file.
     *
     * \return
     *	The streaming duration (microsecond).
     */
    public static native long naGetDuration();

    /**
     * \brief
     * 	Get the current streaming position.
     *
     * \details
     *  Only for playing local file.
     *
     * \return
     *	The file position (microsecond).
     */
    public static native long naGetPosition();

    /**
     * \brief
     * 	Init the player.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naInitDrawFrame();

    /**
     * \brief
     * 	Draw current video frame.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naDrawFrame();

    /**
     * \brief
     * 	The the player status.
     *
     * \return
     *	Return ePlayerStatus.
     */
    public static native int naStatus();

    /**
     * \brief
     * 	Get the received packet size.
     *
     * \return
     *	Return the packet size.
     */
    public static native long naGetRevSizeCnt();

    /**
     * \brief
     * 	Get the frames count.
     *
     * \return
     *	Return the frames count.
     */
    public static native long naGetFrameCnt();


    /**
     * \brief
     *  Get the current streaming codec ID.
     *
     * \return
     *  The codec ID.
     */
    public static native int naGetStreamCodecID();

    /**
     * \brief
     * 	Save the streaming snapshot to file.
     *
     * \param[in] pFileName
     *	The file path.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSaveSnapshot(String pFileName);

    /**
     * \brief
     * 	Save the streaming to file.
     *
     * \details
     *  The file path will automatically appends the correct file extension for the stream.
     *  Ex: H264 streaming => appends .mp4 , MJPEG streaming => appends .avi
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSaveVideo(String pFileName);

    /**
     * \brief
     * 	Stop save streaming.
     *
     * \return
     *	The file extension is .mp4 return E_EncodeContainer_MP4 , The file extension is .avi return E_EncodeContainer_AVI.
     */
    public static native int naStopSaveVideo();


    /**
     * \brief
     *     Extract frame from video file and save the frame to file.
     *
     * \param[in] VideoPath
     *    The video path.
     * \param[in] SavePath
     *    The save path.
     * \param[in] frameIdx
     *    The frame index.
     *
     * \return
     *    The extract result.
     *    EXTRACTOR_OK = Extract frame Extract successfully.
     *    EXTRACTOR_BUSY = Extractor is busy.
     *    EXTRACTOR_READFILEFAILED = Failed to read video file.
     *    EXTRACTOR_DECODEFAILED = Failed to decode frame.
     *    EXTRACTOR_NOSUCHFRAME = No such "frameIdx" in video file.
     */
    public static native int naExtractFrame(String VideoPath,String SavePath,long frameIdx);


    /**
     * \brief
     * 	Set the option for encode streaming.(Deprecated. No longer do transcode video, saving streaming to file directly now.)
     *
     * \details
     *  The option string format is "option1=argument1;option2=argument2;...".
     *
     * \param[in] pOption
     *	The options.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSetTransCodeOptions(String pOption);

    /**
     * \brief
     * 	Set the option for decode streaming.
     *
     * \details
     *  The option string format is "option1=argument1;option2=argument2;...".
     *  Ex: Decoding H264 with low lantency "flags=low_delay".
     *
     * \param[in] pOption
     *	The options.
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSetDecodeOptions(String pOption);

    /**
     * \brief
     * 	Set the display scale mode.
     *
     * \param[in] i32Mode
     *	The display scale mode.
     *  DISPLAY_SCALE_FIT = Fit the screen
     *  DISPLAY_SCALE_FILL = Fill the screen
     *  DISPLAY_SCALE_STRETCH = Stretch the screen
     *
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSetScaleMode(int i32Mode);

    /**
     * \brief
     *     Set the format for coverting decode frame.
     *
     * \param[in] i32format
     *    The format ID which been defined in FFDecodeFrame class.
     *    Assign -1 to disable conversion.
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSetCovertDecodeFrameFormat(int i32Mode);


    /**
     * \brief
     *     Set the buffering time when display queue is emtpy during streaming.
     *
     * \param[in] bufferTime
     *    The buffering time in millisecond. Value must be greater than 0
     */
    public static native int naSetBufferingTime(long bufferTime);


    /**
     * \brief
     * 	Get the decoded frame.
     *
     * \return
     *	Return the decoded frame.
     */
    public static native ffDecodeFrame naGetDecodeFrame();

    /**
     * \brief
     *     Set the display zoom in ratio
     *
     * \param[in] fRatio
     *    The display scale ratio. Value must be greater than 0
     * \return
     *	Return 0 if this function succeeded. Otherwise, other value returned.
     */
    public static native int naSetZoomInRatio(float fRatio);
}
