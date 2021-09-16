package com.truescend.gofit.wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.truescend.gofit.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import generalplus.com.GPCamLib.CamWrapper;


public class FWUpgradeActivity extends Activity {


    private static String TAG = "FWUpgradeActivity";
    private static String DEVICE_FW_HEADER = "SPII";
    private static String DEVICE_FW_HEADER2 = "PGps";
//	private static String DEVICE_FW_HEADER3 = "GPNV";　//航拍用

    private static final int REQUEST_CHOOSE_FILE = 2;

    private String strFilePath = "";
    private byte[] m_byAryBinData = null;

    private int m_i32Index = 0;
    private int m_i32Left = 0;
    private int m_i32Total = 0;
    private boolean _bIsFinish = false;
    private boolean m_bExit = false;
    private static ProgressDialog m_DownloadDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fwgrade);
        selectFile(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume ...");
        super.onResume();
        CamWrapper.getComWrapperInstance().SetViewHandler(m_FromWrapperHandler, CamWrapper.GPVIEW_STREAMING);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy ...");
        if (m_DownloadDialog != null) {
            if (m_DownloadDialog.isShowing()) {
                m_DownloadDialog.dismiss();
                m_DownloadDialog = null;
            }
        }
        CamWrapper.getComWrapperInstance().GPCamClearCommandQueue();

        super.onDestroy();
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

    private void SendRawData() {
        if (m_DownloadDialog != null) {
            m_DownloadDialog.setProgress((int) (m_i32Index * 100 / m_i32Total));
        } else {
            return;
        }

        int i32SendSize = 0;
        int i32Size = 0x0400;

        byte[] byData = new byte[i32Size];
        if (m_i32Left > 0) {
            if (m_i32Left > i32Size) {
                i32SendSize = i32Size;
                byData = Arrays.copyOfRange(m_byAryBinData, m_i32Index, m_i32Index + i32Size);
            } else {
                i32SendSize = m_i32Left;
                byData = Arrays.copyOfRange(m_byAryBinData, m_i32Index, m_i32Index + m_i32Left);
            }

            m_i32Index += i32SendSize;
            m_i32Left -= i32SendSize;

            CamWrapper.getComWrapperInstance().GPCamSendFirmwareRawData(i32SendSize, byData);

        } else {
            if (!_bIsFinish) {
                CamWrapper.getComWrapperInstance().GPCamSendFirmwareRawData(0, new byte[0]);
                _bIsFinish = true;
            } else {
                CamWrapper.getComWrapperInstance().GPCamSendFirmwareUpgrade();
            }
        }
    }

    private void DownloadComplete() {
        SendRawData();
    }

    private void RawDataComplete() {
        SendRawData();
    }

    private void UpgradeComplete() {
        if (m_DownloadDialog != null) {
            if (m_DownloadDialog.isShowing()) {
                m_DownloadDialog.dismiss();
                m_DownloadDialog = null;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.Upgrade_Firmware_successfully));
        builder.setMessage(getResources().getString(R.string.Please_reboot));
        builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                m_bExit = true;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);

                finish();
            }
        });
        builder.show();
    }

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
                case CamWrapper.GPSOCK_MODE_Firmware:
                    if (i32CmdID == CamWrapper.GPSOCK_Firmware_CMD_Download) {
                        DownloadComplete();
                    } else if (i32CmdID == CamWrapper.GPSOCK_Firmware_CMD_SendRawData) {
                        RawDataComplete();

                        return; //Not complete do not nofity ack
                    } else if (i32CmdID == CamWrapper.GPSOCK_Firmware_CMD_Upgrade) {
                        UpgradeComplete();
                    }
                    break;
            }
        } else if (i32CmdType == CamWrapper.GP_SOCK_TYPE_NAK) {
            if (m_bExit) {
                return;
            }
            int i32ErrorCode = (pbyData[0] & 0xFF) + ((pbyData[1] & 0xFF) << 8);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.Upgrade_Firmware_failed));
            builder.setMessage(getResources().getString(R.string.Error_Code) + i32ErrorCode);
            builder.setPositiveButton(getResources().getString(R.string.OK), null);
            builder.show();

            if (m_DownloadDialog != null) {
                if (m_DownloadDialog.isShowing()) {
                    m_DownloadDialog.dismiss();
                    m_DownloadDialog = null;
                }
            }

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
                    return;
                case CamWrapper.Error_LostConnection:
                    Log.e(TAG, "Error_LostConnection ...");
                    FinishToMainController();
                    return;
            }

        }
    }

    private void FinishToMainController() {
        Log.e(TAG, "Finish ...");
        CamWrapper.getComWrapperInstance().GPCamDisconnect();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Choose File
        if (requestCode == REQUEST_CHOOSE_FILE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                strFilePath = FileHelper.getPath(this, uri);
                if (null == strFilePath) {
                    Log.e(TAG, "strFilePath = null");
                    Toast.makeText(this, "Path = null", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, strFilePath);
                    showCheckAlert();
                }
            } else {
                Log.e(TAG, "UnValid File Path");
                Toast.makeText(this, getResources().getString(R.string.UnValid_File_Path), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Cancel Choose File");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showCheckAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.Upgrade_Firmware));
        builder.setMessage(String.format(getResources().getString(R.string.Upgrade_Firmware_File_Path), strFilePath));
        builder.setPositiveButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                StartUpgradeFW();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.Cancel), null);
        builder.show();
    }

    public void StartUpgradeFW() {
        m_i32Total = 0;
        m_i32Index = 0;
        _bIsFinish = false;

        File file = new File(strFilePath);
        int i32fileLen = (int) file.length();

        if (i32fileLen > 10000000) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.Upgrade_Firmware_failed));
            builder.setMessage(String.format(getResources().getString(R.string.incorrect_firmware_file), strFilePath));
            builder.setPositiveButton(getResources().getString(R.string.OK), null);
            builder.show();

            return;
        }
        DataInputStream inputStream = null;
        try {
            m_byAryBinData = new byte[i32fileLen];
            inputStream = new DataInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        if (inputStream != null) {
            //WifiAudioLibWrapper.getInstance().WASendDataTransStart(0,0);

            try {
                inputStream.readFully(m_byAryBinData);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }

            if (m_byAryBinData.length <= 0) {
                Toast.makeText(this, getResources().getString(R.string.Load_File_Fail), Toast.LENGTH_SHORT).show();
            }

            byte[] byData = new byte[4];
            byData = Arrays.copyOfRange(m_byAryBinData, 0, 4);
            try {
                String strTemp = new String(byData, "UTF-8");
                if (!strTemp.equalsIgnoreCase(DEVICE_FW_HEADER) && !strTemp.equalsIgnoreCase(DEVICE_FW_HEADER2)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.Upgrade_Firmware_failed));
                    builder.setMessage(String.format(getResources().getString(R.string.incorrect_firmware_file), strFilePath));
                    builder.setPositiveButton(getResources().getString(R.string.OK), null);
                    builder.show();
                    return;
                }
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if (_bIsFinish) {

            }

            long ui32Checksum = 0;
            for (int i = 0; i < m_byAryBinData.length; i++) {
                ui32Checksum += m_byAryBinData[i] & 0xFF;
            }

            m_i32Left = m_byAryBinData.length;
            m_i32Total = m_byAryBinData.length;
            showDownloadDialog();
            CamWrapper.getComWrapperInstance().GPCamSendFirmwareDownload(i32fileLen, ui32Checksum);

            try {
                inputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }

        } else {
            Toast.makeText(this, getResources().getString(R.string.Load_File_Fail), Toast.LENGTH_SHORT).show();
        }

    }

    public void selectFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        sIntent.putExtra("CONTENT_TYPE", "*/*");
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (getPackageManager().resolveActivity(sIntent, 0) != null) {
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(sIntent, getResources().getString(R.string.Select_file_to_upgrade));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
        } else {
            chooserIntent = Intent.createChooser(intent, getResources().getString(R.string.Select_file_to_upgrade));
        }

        try {
            startActivityForResult(chooserIntent, REQUEST_CHOOSE_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.No_suitable_File_Manager), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDownloadDialog() {
        if (m_DownloadDialog == null) {
            m_DownloadDialog = new ProgressDialog(this);
            m_DownloadDialog.setMessage(getResources().getString(R.string.Downloading));
            m_DownloadDialog.setCanceledOnTouchOutside(false);
            m_DownloadDialog.setMax(100);
            m_DownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			m_DownloadDialog
//					.setButton(
//							DialogInterface.BUTTON_NEGATIVE,
//							"Abort",
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,int which) {
//									
//									
//									m_DownloadDialog = null;
//								}
//							});
            m_DownloadDialog.show();
        }
    }
}
