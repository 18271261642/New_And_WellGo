package generalplus.com.GPCamLib;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CamWrapper {

	private final static String TAG = "CamWrapper";	
	private static String	m_ParameterFilePath;
	private static String 	m_ParameterFileName;
	private static Handler	m_NowViewHandler;
	private static int		m_NowViewIndex;
	private static CamWrapper m_ComWrapperInstance;	
	private static boolean m_bNewFile = false;

	public final static String DEFAULT_MAPPING_STR = "A=MOVI,avi;J=PICT,jpg;L=LOCK,avi;S=SOSO,avi";
	public final static String GP22_DEFAULT_MAPPING_STR = "A=MOVI,mov;J=PICT,jpg;L=LOCK,mov;S=SOSO,mov;V=MOVI,avi;K=LOCK,avi;O=SOSO,avi";
	public final static String STREAMING_URL = "http://%s:8080/?action=stream";
	public final static String RTSP_STREAMING_URL = "rtsp://%s:8080/?action=stream";
	public static String COMMAND_URL = "192.168.25.1";
	public final static String CamDefaulFolderName = "GoPlusCam";
	public final static String SaveFileToDevicePath = "/DCIM/Camera/";
	public final static String SaveLogFileName = "GoPlusCamCmdLog";
	public final static String ConfigFileName = "GoPlusCamConf.ini";
	public final static String ParameterFileName = "Menu.xml";
    public final static String DefaultParameterFileName = "Default_Menu.xml";
	public static boolean bIsDefault = false;
	public final static String EventMessgae_SMS = "android.provider.Telephony.SMS_RECEIVED";
	public final static int		SupportMaxLogLength = 65536;
	public final static int		SupportMaxShowLogLength = 200;	
	
	public final static int	Error_ServerIsBusy			= 0xFFFF;
	public final static int	Error_InvalidCommand		= 0xFFFE;
	public final static int	Error_RequestTimeOut		= 0xFFFD;
	public final static int	Error_ModeError				= 0xFFFC;
	public final static int	Error_NoStorage				= 0xFFFB;
	public final static int	Error_WriteFail				= 0xFFFA;
	public final static int	Error_GetFileListFail		= 0xFFF9;
	public final static int	Error_GetThumbnailFail		= 0xFFF8;
	public final static int	Error_FullStorage			= 0xFFF7;
	public final static int	Error_NoFile				= 0xFFF3;
	
	public final static int	Error_SocketClosed			= 0xFFC1;
	public final static int	Error_LostConnection		= 0xFFC0;
	
	public final static int STREAMING_PORT						= 8080;
	public final static int COMMAN_PORT 						= 8081;	
	
	//GP_SOCK_TYPE 2Byte
	public final static int GP_SOCK_TYPE_CMD					= 0x0001;
	public final static int GP_SOCK_TYPE_ACK					= 0x0002;
	public final static int GP_SOCK_TYPE_NAK					= 0x0003;

	//GP_SOCK_MODE_ID 1Byte
	public final static int GPSOCK_MODE_General					= 0x00;
	public final static int GPSOCK_MODE_Record					= 0x01;
	public final static int GPSOCK_MODE_CapturePicture			= 0x02;
	public final static int GPSOCK_MODE_Playback				= 0x03;
	public final static int GPSOCK_MODE_Menu					= 0x04;
	public final static int GPSOCK_MODE_Firmware                = 0x05;
	public final static int GPSOCK_MODE_Firmware_CV             = 0x06;
	public final static int GPSOCK_MODE_Vendor					= 0xFF;

	//GP_SOCK_CMD_ID 1Byte
	public final static int GPSOCK_General_CMD_SetMode			= 0x00;
	public final static int GPSOCK_General_CMD_GetDeviceStatus	= 0x01;
	public final static int GPSOCK_General_CMD_GetParameterFile	= 0x02;
	public final static int GPSOCK_General_CMD_Poweroff			= 0x03;
	public final static int GPSOCK_General_CMD_RestartStreaming	= 0x04;
	public final static int GPSOCK_General_CMD_AuthDevice		= 0x05;
	public final static int GPSOCK_General_CMD_CheckMapping		= 0x07;
	
	public final static int GPSOCK_Record_CMD_Start				= 0x00;
	public final static int GPSOCK_Record_CMD_Audio				= 0x01;
	
	public final static int GPSOCK_CapturePicture_CMD_Capture	= 0x00;
	
	public final static int GPSOCK_Playback_CMD_Start			= 0x00;
	public final static int GPSOCK_Playback_CMD_Pause			= 0x01;
	public final static int GPSOCK_Playback_CMD_GetFileCount	= 0x02;
	public final static int GPSOCK_Playback_CMD_GetNameList		= 0x03;
	public final static int GPSOCK_Playback_CMD_GetThumbnail	= 0x04;
	public final static int GPSOCK_Playback_CMD_GetRawData		= 0x05;
	public final static int GPSOCK_Playback_CMD_Stop			= 0x06;
	public final static int GPSOCK_Playback_CMD_GetSpecficName	= 0x07;
	public final static int GPSOCK_Playback_CMD_DeleteFile		= 0x08;
	public final static int GPSOCK_Playback_CMD_ERROR			= 0xFF;
	
	public final static int GPSOCK_Menu_CMD_GetParameter		= 0x00;
	public final static int GPSOCK_Menu_CMD_SetParameter		= 0x01;

	public final static int GPSOCK_Vendor_CMD_Vendor			= 0x00;
	
	public final static int GPTYPE_ConnectionStatus_Idle		= 0x00;
	public final static int GPTYPE_ConnectionStatus_Connecting	= 0x01;
	public final static int GPTYPE_ConnectionStatus_Connected	= 0x02;
	public final static int GPTYPE_ConnectionStatus_DisConnected= 0x03;
	public final static int GPTYPE_ConnectionStatus_SocketClosed= 0x0A;
	
	public final static int GPDEVICEMODE_Record					= 0x00;
	public final static int GPDEVICEMODE_Capture				= 0x01;
	public final static int GPDEVICEMODE_Playback				= 0x02;
	public final static int GPDEVICEMODE_Menu					= 0x03;
	public final static int GPDEVICEMODE_USB					= 0x04;
	
	public final static int GPBATTERTY_LEVEL0					= 0x00;
	public final static int GPBATTERTY_LEVEL1					= 0x01;
	public final static int GPBATTERTY_LEVEL2					= 0x02;
	public final static int GPBATTERTY_LEVEL3					= 0x03;
	public final static int GPBATTERTY_LEVEL4					= 0x04;
	public final static int GPBATTERTY_GHARGE					= 0x05;	
	
	public final static int GPVIEW_STREAMING					= 0x00;
	public final static int GPVIEW_MENU							= 0x01;
	public final static int GPVIEW_FILELIST						= 0x02;
	
	public final static int GPCALLBACKTYPE_CAMSTATUS			= 0x00;
	public final static int GPCALLBACKTYPE_CAMDATA				= 0x01;
	
	public static final int GPFILEFLAG_AVISTREAMING 			= 0x01;
	public static final int GPFILEFLAG_JPGSTREAMING 			= 0x02;
	
	public static final int GPSOCK_Firmware_CMD_Download        = 0x00;
	public static final int GPSOCK_Firmware_CMD_SendRawData     = 0x01;
	public static final int GPSOCK_Firmware_CMD_Upgrade         = 0x02;
	
	public final static String GPFILECALLBACKTYPE_FILEURL		= "FileURL";
	public final static String GPFILECALLBACKTYPE_FILEINDEX		= "FileIndex";
	public final static String GPFILECALLBACKTYPE_FILEFLAG		= "FileFlag";
	
	public final static String GPCALLBACKSTATUSTYPE_CMDINDEX	= "CmdIndex";
	public final static String GPCALLBACKSTATUSTYPE_CMDTYPE		= "CmdType";
	public final static String GPCALLBACKSTATUSTYPE_CMDMODE		= "CmdMode";
	public final static String GPCALLBACKSTATUSTYPE_CMDID		= "CmdID";
	public final static String GPCALLBACKSTATUSTYPE_DATASIZE	= "DataSize";
	public final static String GPCALLBACKSTATUSTYPE_DATA		= "Data";

	static {
		try {
			Log.i(TAG, "Trying to load GPCam.so ...");
			System.loadLibrary("GPCam");
		} catch (UnsatisfiedLinkError Ule) {
			Log.e(TAG, "Cannot load GPCam.so ...");
			Ule.printStackTrace();
		} finally {
		}
	}
	
	public CamWrapper()
	{
		m_ComWrapperInstance = this;
	}
	
	public void SetViewHandler(Handler ViewHandler, int ViewIndex)
	{
		m_NowViewHandler = ViewHandler;
		m_NowViewIndex = ViewIndex;
	}
	
	public static CamWrapper getComWrapperInstance()
	{
		return m_ComWrapperInstance;
	}
	
	void GPCamDataCallBack(boolean bIsWrite, int i32DataSize, byte[] pbyData)
	{

	}

	void GPCamStatusCallBack(int i32CMDIndex, int i32Type, int i32Mode,
			int i32CMDID, int i32DataSize, byte[] pbyData) {
			
		if(m_NowViewHandler != null)
		{
			Message msg = new Message();
			msg.what = GPCALLBACKTYPE_CAMSTATUS;
			Bundle bundle = new Bundle();
			bundle.putInt(GPCALLBACKSTATUSTYPE_CMDINDEX, i32CMDIndex);
			bundle.putInt(GPCALLBACKSTATUSTYPE_CMDTYPE, i32Type);
			bundle.putInt(GPCALLBACKSTATUSTYPE_CMDMODE, i32Mode);
			bundle.putInt(GPCALLBACKSTATUSTYPE_CMDID, i32CMDID);
			bundle.putInt(GPCALLBACKSTATUSTYPE_DATASIZE, i32DataSize);
			bundle.putByteArray(GPCALLBACKSTATUSTYPE_DATA, pbyData);
			msg.setData(bundle);
			m_NowViewHandler.sendMessage(msg);
		}
	
		/*if(displayValuesHelper != null)
			displayValuesHelper.getGPCamStatus(i32CMDIndex, i32Type, i32Mode, i32CMDID, i32DataSize, pbyData);	*/	
	}

	public native int GPCamConnectToDevice(String IPAddress, int Port);

	public native void GPCamDisconnect();

	private native void GPCamSetDownloadPath(String Path);

	public native int GPCamAbort(int Index);

	public native int GPCamSendSetMode(int Mode);

	public native int GPCamSendGetStatus();

	private native int GPCamSendGetParameterFile(String FileName);

	public native int GPCamSendPowerOff();

	public native int GPCamSendRestartStreaming();

	public native int GPCamSendRecordCmd();

	public native int GPCamSendAudioOnOff(boolean IsOn);

	public native int GPCamSendCapturePicture();

	public native int GPCamSendStartPlayback(int Index);

	public native int GPCamSendPausePlayback();

	public native int GPCamSendGetFullFileList();

	public native int GPCamSendGetFileThumbnail(int Index);

	public native int GPCamSendGetFileRawdata(int Index);

	public native int GPCamSendStopPlayback();
	
	public native int GPCamSetNextPlaybackFileListIndex(int Index);

	public native int GPCamSendDeleteFile(int Index);

	public native int GPCamSendGetParameter(int ID);

	public native int GPCamSendSetParameter(int ID, int Size, byte[] Data);
	
	public native int GPCamSendFirmwareDownload(long FileSize, long Checksum);
	
	public native int GPCamSendFirmwareRawData(long Size, byte[] Data);
	
	public native int GPCamSendFirmwareUpgrade();
	
	public native int GPCamSendCVFirmwareDownload(long FileSize, long Checksum);
	
	public native int GPCamSendCVFirmwareRawData(long Size, byte[] Data);
	
	public native int GPCamSendCVFirmwareUpgrade(long Area);

	public native int GPCamSendVendorCmd(byte[] Data, int Size);

	public native int GPCamGetStatus();

	public native String GPCamGetFileName(int Index);

	public native boolean GPCamGetFileTime(int Index, byte[] Time);

	public native int GPCamGetFileIndex(int Index);

	public native int GPCamGetFileSize(int Index);

	public native byte GPCamGetFileExt(int Index);

	public native byte[] GPCamGetFileExtraInfo(int Index);
	
	public native void GPCamClearCommandQueue();

	public native boolean GPCamSetFileNameMapping(String FileName);
	
	public native void GPCamCheckFileMapping();

	public void SetGPCamSetDownloadPath(String FilePath)
	{
		m_ParameterFilePath = FilePath;
		GPCamSetDownloadPath(m_ParameterFilePath);
	}
	
	public String GetGPCamSetDownloadPath()
	{
		return m_ParameterFilePath;
	}
	
	public void SetGPCamSendGetParameterFile(String FileName)
	{
		m_ParameterFileName = FileName;
		GPCamSendGetParameterFile(m_ParameterFileName);
	}
	
	public String GetGPCamSendGetParameterFile()
	{
		return m_ParameterFileName;
	}
	
	public void setIsNewFile(boolean bNewFile)
	{
		m_bNewFile = bNewFile;
	}
	
	public boolean getIsNewFile() {
		return m_bNewFile;
	}
	
}
