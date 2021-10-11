package com.truescend.gofit.wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.j256.ormlite.stmt.query.In;
import com.truescend.gofit.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import generalplus.com.GPCamLib.CamWrapper;

/**
 * 文件列表
 */
public class FilesActivity extends Activity {


    private static final String TAG = "FilesActivity";
    private static FilesActivity m_FilesActivityInstance;
    private Context m_Context;
    private Handler m_handler = null;
    private boolean _bUserLeaveHint = true;

    private final String MAPKEY_ThumbnailFilePath = "ThumbnailFilePath";
    private final String MAPKEY_FileName = "FileName";
    private final String MAPKEY_FileSize = "FileSize";
    private final String MAPKEY_FileTime = "FileTime";
    private final String MAPKEY_FileStatus = "FileStatus";

    public static final int FileTag_FileCount = 0x00;
    public static final int FileTag_FileName = 0x01;

    public static final int FileFlag_Unknown = 0x00;
    public static final int FileFlag_AVIStreaming = 0x01;
    public static final int FileFlag_JPGStreaming = 0x02;
    public static final int FileFlag_LocalFile = 0x03;

    public static final int DownloadFlag_Init = 0x00;
    public static final int DownloadFlag_Downloading = 0x01;
    public static final int DownloadFlag_Stopping = 0x02;
    public static final int DownloadFlag_Completed = 0x03;

    private static final int FileTag_FileDeviceInit = 0xA0;
    private static final int FileTag_FileDeviceReady = 0xA1;
    private static final int FileTag_FileGettingThumbnail = 0xA2;
    private static final int FileTag_FileGotThumbnail = 0xA3;
    private static final int FileTag_FileDownload = 0xA4;
    private static final int FileTag_FilePalying = 0xA5;
    private static final int FileTag_FileBroken = 0xA6;
    private static final int FileTag_FileGotThumbnailEnd = 0xA7;
    private static ProgressDialog m_DownloadDialog = null;

    private static boolean bSaveImageItem = false;
    private GridView m_Gridview;
    private static ArrayList<HashMap<String, Object>> listImageItem = null;

    private static Thread m_UpdateThumbnailThread = null;
    private static Thread m_UpdateGridVierThread = null;
    private static boolean m_bRunCreateGridViewDone = false;
    private static boolean m_bPendingGetThumbnail = false;
    //private static boolean bIsStopDownload = false;
    private static int m_i32DownlaodStatus = DownloadFlag_Completed;
    private static boolean bIsStopUpdateThumbnail = false;
    private boolean bIsCopingFile = false;
    private int i32GetThumbnailCount = 0;
    private String strDevicePICLocation = "";
    private int _i32CommandIndex = 0;
    private int _i32ErrorCount = 0;
    private int _i32WaitGettingThumbnailCount = 0;
    private static int _i32GettingThumbnailFileIndex = -1;
    private int _i32GotThumbnailFileIndex = -1;
    private int _i32SelectedFirstItem = -1;
    private int _firstVisibleItem = 0;
    private int _scrollState = 0;
    private CharSequence[] CharSequenceItemsDefault = new String[3];
    private CharSequence[] CharSequenceItemsDelete = new String[3];
    private CharSequence[] CharSequenceItemsSDdelete = new String[4];
    private SimpleAdapter m_saImageItems;
    private static boolean _bSetModeDone = false;
    private long mLastClickTime;
    public static boolean m_bCanDeleteSDFile = false;
    private boolean[] m_bCheckboxArray = {false, true};
    private ProgressDialog m_Dialog = null;
    private int m_iDeleteSDposition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        CharSequenceItemsDefault[0] = getResources().getString(R.string.Play);
        CharSequenceItemsDefault[1] = getResources().getString(R.string.Download);
        CharSequenceItemsDefault[2] = getResources().getString(R.string.Info);

        CharSequenceItemsDelete[0] = getResources().getString(R.string.Play);
        CharSequenceItemsDelete[1] = getResources().getString(R.string.Delete);
        CharSequenceItemsDelete[2] = getResources().getString(R.string.Info);

        CharSequenceItemsSDdelete[0] = getResources().getString(R.string.Play);
        CharSequenceItemsSDdelete[1] = getResources().getString(R.string.Download);
        CharSequenceItemsSDdelete[2] = getResources().getString(R.string.Delete);
        CharSequenceItemsSDdelete[3] = getResources().getString(R.string.Info);

        setContentView(R.layout.activity_files);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        m_Context = FilesActivity.this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        strDevicePICLocation = Environment.getExternalStorageDirectory()
                .getPath() + CamWrapper.SaveFileToDevicePath;

        Log.e(TAG,"---------strDevicePICLocation="+strDevicePICLocation);

        if (m_handler == null)
            m_handler = new Handler();

        m_Gridview = (GridView) findViewById(R.id.gridView1);
        m_Gridview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                _scrollState = scrollState;
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (_i32SelectedFirstItem == _firstVisibleItem) {
                        return;
                    }
                    _i32SelectedFirstItem = _firstVisibleItem;
                    m_Gridview.setSelection(_i32SelectedFirstItem);
                    m_bRunCreateGridViewDone = false;
                    if (m_UpdateThumbnailThread != null) {
                        m_UpdateThumbnailThread.interrupt();
                        m_UpdateThumbnailThread = null;
                    }
                    m_UpdateThumbnailThread = new Thread(
                            new UpdateThumbnailRunnable());
                    m_UpdateThumbnailThread.start();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                _firstVisibleItem = firstVisibleItem;

                Log.e("tag", "onScroll = " + firstVisibleItem);
            }
        });
        m_Gridview.setOnItemClickListener(new OnItemClickListener() {

            private AdapterView m_Paramet;
            private String strStreamFilePath = "";
            private long m_i64ID;
            private CharSequence[] SetCharSequenceItems = null;

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                m_Paramet = parent;
                m_i64ID = id;

                if (position >= listImageItem.size()) {
                    Log.d(TAG, "position >= listImageItem.size()");
                    return;
                }

//				byte[] FileExtra = CamWrapper.getComWrapperInstance().GPCamGetFileExtraInfo(position);
//				if (null != FileExtra) {
//					for (int i = 0; i < FileExtra.length; i++) {
//						Log.e(TAG,"FileExtra = " + FileExtra[i]);
//					}
//				}
                final HashMap<String, Object> map = listImageItem.get(position);
                int iFileStatus = (int) map.get(MAPKEY_FileStatus);
                if (iFileStatus != FileTag_FileGotThumbnail && iFileStatus != FileTag_FileGotThumbnailEnd)
                    return;

                if (false == m_bCanDeleteSDFile) {
                    SetCharSequenceItems = CharSequenceItemsDefault;
                } else {
                    SetCharSequenceItems = CharSequenceItemsSDdelete;
                }

                strStreamFilePath = "";

                File dir = new File(strDevicePICLocation + map.get(MAPKEY_FileName));

                boolean bFileExist = false;
                Log.e(TAG, "dir.exists() = " + dir.exists() + ",dir.length() = " + dir.length() + ", dir.length() / 1024 = " + dir.length() / 1024 + ", MAPKEY_FileSize = " + map.get(MAPKEY_FileSize));
                if (dir.exists() && ((dir.length() / 1024) == (int) map.get(MAPKEY_FileSize))) {
                    SetCharSequenceItems = CharSequenceItemsDelete;
                    strStreamFilePath = strDevicePICLocation + map.get(MAPKEY_FileName);
                    bFileExist = true;
                }
                final boolean fbFileExist = bFileExist;

                Builder ChoseAlertDialog = new Builder(m_Context);
                ChoseAlertDialog.setNegativeButton(getResources().getString(R.string.Cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                ChoseAlertDialog.setItems(SetCharSequenceItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String, Object> item = (HashMap<String, Object>) m_Paramet.getItemAtPosition(position);
                                if (SetCharSequenceItems[which].toString().contentEquals(getResources().getString(R.string.Play))) {
                                    if (IsDownloading()) {
                                        return;
                                    }
                                    _bUserLeaveHint = false;
                                    bIsStopUpdateThumbnail = true;
                                    bSaveImageItem = true;

                                    if (map.get(MAPKEY_FileName).toString().contains(".jpg")) {
                                        if (!strStreamFilePath.isEmpty()) {   //本地图片

                                            Intent pictureIntent = new Intent(FilesActivity.this,LocalPictureActivity.class);
                                            pictureIntent.putExtra("local_picture",strStreamFilePath);
                                            startActivity(pictureIntent);

//                                            ImageView iv = new ImageView(m_Context);
//                                            try {
//                                                iv.setImageBitmap(
//                                                        decodeSampledBitmapFromResource(strStreamFilePath, 720, 480));
//                                                //iv.setImageURI(Uri.parse(strStreamFilePath));
//                                            } catch (Exception e) {
//                                                // TODO Auto-generated catch block
//                                                e.printStackTrace();
//                                            } catch (OutOfMemoryError e) {
//                                                e.printStackTrace();
//                                            }
//
//                                            Builder ShowImgAlertDialog = new Builder(m_Context);
//                                            ShowImgAlertDialog.setView(iv);
//                                            ShowImgAlertDialog.setNegativeButton(getResources().getString(R.string.Cancel),
//                                                    new DialogInterface.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            bIsStopUpdateThumbnail = false;
//
//                                                        }
//                                                    });
//                                            ShowImgAlertDialog.show();
                                        } else {
                                            Intent toVlcPlayer = new Intent(FilesActivity.this, FileViewController.class);
//                                            Intent toVlcPlayer = new Intent(FilesActivity.this, OnLineWifiPlayerActivity.class);
                                            Bundle b = new Bundle();
                                            b.putString(CamWrapper.GPFILECALLBACKTYPE_FILEURL, strStreamFilePath);
                                            b.putInt(CamWrapper.GPFILECALLBACKTYPE_FILEFLAG, CamWrapper.GPFILEFLAG_JPGSTREAMING);
                                            b.putInt(CamWrapper.GPFILECALLBACKTYPE_FILEINDEX, position);
                                            toVlcPlayer.putExtras(b);
                                            startActivity(toVlcPlayer);
                                        }
                                    } else {
                                        if(dir.exists()){  //本地视频
                                            Intent intent = new Intent(FilesActivity.this,LocalWifiPlayerActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("local_video",strStreamFilePath);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            return;
                                        }
//                                        Intent toVlcPlayer = new Intent(FilesActivity.this, FileViewController.class);
                                        Intent toVlcPlayer = new Intent(FilesActivity.this, OnLineWifiPlayerActivity.class);

                                        Bundle b = new Bundle();
                                        b.putString(CamWrapper.GPFILECALLBACKTYPE_FILEURL, strStreamFilePath);
                                        b.putInt(CamWrapper.GPFILECALLBACKTYPE_FILEFLAG, CamWrapper.GPFILEFLAG_AVISTREAMING);
                                        b.putInt(CamWrapper.GPFILECALLBACKTYPE_FILEINDEX, position);
                                        toVlcPlayer.putExtras(b);
                                        startActivity(toVlcPlayer);
                                    }
                                } else if (SetCharSequenceItems[which].toString().contentEquals(getResources().getString(R.string.Download))) {
                                    if (IsDownloading()) {
                                        return;
                                    }
                                    //bIsStopUpdateThumbnail = true;
                                    m_bPendingGetThumbnail = true;
                                    if (m_DownloadDialog == null) {
                                        m_DownloadDialog = new ProgressDialog(m_Context);
                                        m_DownloadDialog.setMessage(getResources().getString(R.string.Downloading));
                                        m_DownloadDialog.setCanceledOnTouchOutside(false);
                                        m_DownloadDialog.setMax(100);
                                        m_DownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        m_DownloadDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.Abort),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (!bIsCopingFile) {
                                                            final File deviceDirDelete = new File(
                                                                    strDevicePICLocation + map.get(MAPKEY_FileName));
                                                            deviceDirDelete.delete();
                                                        }

                                                        m_DownloadDialog = null;
                                                        CamWrapper.getComWrapperInstance().GPCamClearCommandQueue();
                                                        CamWrapper.getComWrapperInstance().GPCamAbort(_i32CommandIndex);
                                                        CamWrapper.getComWrapperInstance().GPCamSendGetStatus();
                                                    }
                                                });
                                        m_DownloadDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                if (!bIsCopingFile) {
                                                    final File deviceDirDelete = new File(
                                                            strDevicePICLocation + map.get(MAPKEY_FileName));
                                                    deviceDirDelete.delete();
                                                }

                                                m_DownloadDialog = null;
                                                CamWrapper.getComWrapperInstance().GPCamClearCommandQueue();
                                                CamWrapper.getComWrapperInstance().GPCamAbort(_i32CommandIndex);
                                                CamWrapper.getComWrapperInstance().GPCamSendGetStatus();
                                            }
                                        });
                                        m_DownloadDialog.show();
                                    }

                                    m_i32DownlaodStatus = DownloadFlag_Init;
                                    CamWrapper.getComWrapperInstance().GPCamClearCommandQueue();
                                    CamWrapper.getComWrapperInstance().GPCamSendGetFileRawdata(position);
                                } else if (SetCharSequenceItems[which].toString().contentEquals(getResources().getString(R.string.Info))) {
                                    if (IsDownloading()) {
                                        return;
                                    }
                                    String strFileTime = (String) map.get(MAPKEY_FileTime);
                                    Builder InfoAlertDialog = new Builder(
                                            m_Context);
                                    String strInfoMsg = "", strTime = "";
                                    strTime = "20" + strFileTime.substring(0, 2)
                                            + "/" + strFileTime.substring(2, 4)
                                            + "/" + strFileTime.substring(4, 6)
                                            + " " + strFileTime.substring(6, 8)
                                            + ":" + strFileTime.substring(8, 10)
                                            + ":" + strFileTime.substring(10, 12);
                                    strInfoMsg = "Name: " + map.get(MAPKEY_FileName)
                                            + "\nTime: " + strTime
                                            + "\nSize: " + String.valueOf(map.get(MAPKEY_FileSize));
                                    InfoAlertDialog.setTitle(getResources().getString(R.string.Info));
                                    InfoAlertDialog.setMessage(strInfoMsg);
                                    InfoAlertDialog.setCancelable(true);
                                    InfoAlertDialog.show();
                                } else if (SetCharSequenceItems[which].toString().contentEquals(getResources().getString(R.string.Delete))) {
                                    if (false == m_bCanDeleteSDFile) {
                                        File deviceDirDelete = new File(strDevicePICLocation + map.get(MAPKEY_FileName));
                                        deviceDirDelete.delete();
                                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + deviceDirDelete.getAbsolutePath())));
                                    } else {
                                        Builder builder = new Builder(m_Context);
                                        String[] week = null;
                                        if (fbFileExist) {
                                            week = new String[]{getResources().getString(R.string.Delete_SD_File), getResources().getString(R.string.Delete_Phone_File)};
                                            builder.setMultiChoiceItems(week, m_bCheckboxArray, new DialogInterface.OnMultiChoiceClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                                    if (1 == which) {
                                                        ((AlertDialog) dialog).getListView().setItemChecked(which, true);
                                                        m_bCheckboxArray[which] = true;
                                                    }
                                                }
                                            });
                                        } else {
                                            m_bCheckboxArray[0] = true;
                                            week = new String[]{getResources().getString(R.string.Delete_SD_File)};
                                            builder.setMultiChoiceItems(week, new boolean[]{true}, new DialogInterface.OnMultiChoiceClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                                    if (0 == which) {
                                                        ((AlertDialog) dialog).getListView().setItemChecked(which, true);
                                                    }
                                                }
                                            });
                                        }

                                        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Log.e(TAG, "1= " + m_bCheckboxArray[0] + ", 2= " + m_bCheckboxArray[1]);

                                                if (fbFileExist) {
                                                    File deviceDirDelete = new File(strDevicePICLocation + map.get(MAPKEY_FileName));
                                                    deviceDirDelete.delete();
                                                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + deviceDirDelete.getAbsolutePath())));
                                                }
                                                if (m_bCheckboxArray[0]) {
                                                    if (m_Dialog != null) {
                                                        if (m_Dialog.isShowing()) {
                                                            m_Dialog.dismiss();
                                                            m_Dialog = null;
                                                        }
                                                    }
                                                    m_Dialog = new ProgressDialog(m_Context);
                                                    m_Dialog.setMessage(getResources().getString(R.string.Delete_SD_File));
                                                    m_Dialog.setCanceledOnTouchOutside(false);
                                                    m_Dialog.show();

                                                    m_iDeleteSDposition = position;
                                                    m_bPendingGetThumbnail = true;
                                                    CamWrapper.getComWrapperInstance().GPCamClearCommandQueue();
                                                    CamWrapper.getComWrapperInstance().GPCamSendDeleteFile(position);
                                                }
                                            }
                                        }).setNegativeButton(R.string.No, null);
                                        builder.create().show();
                                    }

                                }
                            }

                        });

                ChoseAlertDialog.setCancelable(false);
                ChoseAlertDialog.show();

            }

        });

        if (!bSaveImageItem) {

            if (listImageItem == null)
                listImageItem = new ArrayList<HashMap<String, Object>>();
            listImageItem.clear();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            _bSetModeDone = false;
            CamWrapper.getComWrapperInstance().GPCamSendSetMode(
                    CamWrapper.GPDEVICEMODE_Playback);

            CamWrapper.getComWrapperInstance().GPCamSendGetFullFileList();


        } else {
            _bSetModeDone = false;
            CamWrapper.getComWrapperInstance().GPCamSendSetMode(
                    CamWrapper.GPDEVICEMODE_Playback);

            CamWrapper.getComWrapperInstance().GPCamSendGetFullFileList();

            m_bRunCreateGridViewDone = false;
            if (m_UpdateThumbnailThread == null) {
                m_UpdateThumbnailThread = new Thread(
                        new UpdateThumbnailRunnable());
                m_UpdateThumbnailThread.start();
            }
        }

    }

    static public FilesActivity getInstance() {
        return m_FilesActivityInstance;
    }

    private void UpdateGridView() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (null == m_saImageItems) {
                    if (null == listImageItem) {
                        return;
                    }
                    m_saImageItems = new SimpleAdapter(m_Context,
                            listImageItem, R.layout.files_program_list, new String[]{
                            MAPKEY_ThumbnailFilePath, MAPKEY_FileName},
                            new int[]{R.id.imageView1, R.id.textView1});

                    m_Gridview.setAdapter(m_saImageItems);
                }
                m_saImageItems.notifyDataSetChanged();
            }

        });
    }

    class UpdateGridViewRunnable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.e(TAG, "UpdateGridViewRunnable ...");
            while (m_UpdateThumbnailThread != null) {

                m_handler.post(new Runnable() {
                    public void run() {
                        if (listImageItem.size() >= 0)
                            UpdateGridView();
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            m_handler.post(new Runnable() {
                public void run() {
                    if (listImageItem.size() >= 0)
                        UpdateGridView();
                }
            });
            Log.e(TAG, "UpdateGridViewRunnable ... Done");
            m_UpdateGridVierThread = null;
        }

    }

    class UpdateThumbnailRunnable implements Runnable {

        UpdateThumbnailRunnable() {

            Log.e(TAG, "Create UpdateThumbnailRunnable ... ");
            if (m_UpdateGridVierThread == null) {
                m_UpdateGridVierThread = new Thread(
                        new UpdateGridViewRunnable());
                m_UpdateGridVierThread.start();
            }
            bIsStopUpdateThumbnail = false;
            m_bPendingGetThumbnail = false;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub

            while (!m_bRunCreateGridViewDone && !bIsStopUpdateThumbnail) {
                if (m_bPendingGetThumbnail)
                    continue;
                else {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                synchronized (listImageItem) {
                    m_bRunCreateGridViewDone = false;
                    int index = 0;
                    if (CamWrapper.getComWrapperInstance().getIsNewFile()) {
                        if (-1 != _i32SelectedFirstItem) {
                            index = _i32SelectedFirstItem;
                        }
                    }
                    for (int i = index; i < listImageItem.size(); i++) {
                        HashMap<String, Object> map = listImageItem.get(i);
                        if (null == map.get(MAPKEY_FileStatus)) {
                            continue;
                        }
                        if ((int) map.get(MAPKEY_FileStatus) == FileTag_FileDeviceReady || (int) map.get(MAPKEY_FileStatus) == FileTag_FileDeviceInit) {
                            if (CamWrapper.getComWrapperInstance().getIsNewFile()) {
                                if (-1 == CamWrapper.getComWrapperInstance().GPCamGetFileIndex(i)) {
                                    CamWrapper.getComWrapperInstance().GPCamSetNextPlaybackFileListIndex(i);
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }

                            CamWrapper.getComWrapperInstance()
                                    .GPCamSendGetFileThumbnail(i);

                            _i32GettingThumbnailFileIndex = i;
                            map.put(MAPKEY_FileStatus, FileTag_FileGettingThumbnail);
                            listImageItem.set(i, map);
                            Log.e(TAG, "i = " + i + ", FileTag_FileGettingThumbnail...");
                            break;
                        } else if ((int) map.get(MAPKEY_FileStatus) == FileTag_FileGettingThumbnail) {
                            _i32WaitGettingThumbnailCount++;
                            if (_i32WaitGettingThumbnailCount > 100) {
                                _i32WaitGettingThumbnailCount = 0;
                                if (CamWrapper.getComWrapperInstance().getIsNewFile()) {
                                    if (-1 == CamWrapper.getComWrapperInstance().GPCamGetFileIndex(i)) {
                                        CamWrapper.getComWrapperInstance().GPCamSetNextPlaybackFileListIndex(i);
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                CamWrapper.getComWrapperInstance().GPCamSendGetFileThumbnail(i);

                                map.put(MAPKEY_FileStatus, FileTag_FileGettingThumbnail);
                                listImageItem.set(i, map);
                            }
                            _i32GettingThumbnailFileIndex = i;
                            break;
                        } else if ((int) map.get(MAPKEY_FileStatus) == FileTag_FileGotThumbnail) {
                            _i32WaitGettingThumbnailCount = 0;

                            map.put(MAPKEY_FileStatus, FileTag_FileGotThumbnailEnd);

                            listImageItem.set(i, map);
                            _i32GotThumbnailFileIndex = i;
                            m_bRunCreateGridViewDone = false;
                            if (i == listImageItem.size() - 1)
                                m_bRunCreateGridViewDone = true;
                        } else if ((int) map.get(MAPKEY_FileStatus) == FileTag_FileBroken) {
                            map.put(MAPKEY_ThumbnailFilePath, R.drawable.broken);
                            listImageItem.set(i, map);

                            _i32GotThumbnailFileIndex = i;
                            m_bRunCreateGridViewDone = false;
                            if (i == listImageItem.size() - 1)
                                m_bRunCreateGridViewDone = true;
                        }
                    }
                }
            }
            Log.e(TAG, "m_UpdateThumbnailThread = null");
            UpdateGridView();
            m_UpdateThumbnailThread = null;
        }
    }

    ;

    private boolean isFastClick() {
        long currentTime = System.currentTimeMillis();

        long time = currentTime - mLastClickTime;
        if (0 < time && time < 1500) {
            return true;
        }

        mLastClickTime = currentTime;
        return false;
    }

    private boolean IsDownloading() {
        if (m_i32DownlaodStatus == DownloadFlag_Downloading || m_i32DownlaodStatus == DownloadFlag_Stopping) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(m_Context, getResources().getString(R.string.Download_wait), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed ...");
        if (false == _bSetModeDone) {
            return;
        }
        if (isFastClick()) {
            return;
        }
        if (IsDownloading()) {
            return;
        }
        exitFileActivity();
        super.onBackPressed();
    }

    private void exitFileActivity() {
        if (m_UpdateThumbnailThread != null || m_UpdateGridVierThread != null) {
            bIsStopUpdateThumbnail = true;
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        m_UpdateThumbnailThread = null;
        m_UpdateGridVierThread = null;

        bSaveImageItem = false;
        CamWrapper.getComWrapperInstance().GPCamClearCommandQueue();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume ...");
        super.onResume();
        bIsStopUpdateThumbnail = false;
        CamWrapper.getComWrapperInstance().SetViewHandler(m_FromWrapperHandler, CamWrapper.GPVIEW_FILELIST);

        if (null != listImageItem) {
            if (m_UpdateThumbnailThread == null) {
                m_UpdateThumbnailThread = new Thread(
                        new UpdateThumbnailRunnable());
                m_UpdateThumbnailThread.start();
            }
        }
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
        if (!bSaveImageItem) {
            File appGoPlusCamDir = new File(Environment
                    .getExternalStorageDirectory().getPath()
                    + "/"
                    + CamWrapper.CamDefaulFolderName);
            deleteDir(appGoPlusCamDir);
            m_i32DownlaodStatus = DownloadFlag_Completed;
        }

        super.onDestroy();
    }

    @Override
    protected void onUserLeaveHint() {
        Log.e(TAG, "onUserLeaveHint ...");
//		m_i32DownlaodStatus = DownloadFlag_Completed;
//		if(_bUserLeaveHint)
//		{
//			CamWrapper.getComWrapperInstance().GPCamSendSetMode(
//					CamWrapper.GPDEVICEMODE_Record);
//			finish();
//		}
        _bUserLeaveHint = true;
        super.onUserLeaveHint();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    private void copyFile(String inputPath, String OutputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {
            File dir = new File(OutputPath);
            if (dir.exists() && dir.isFile())
                dir.delete();

            in = new FileInputStream(inputPath);
            out = new FileOutputStream(OutputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            out.flush();
            out.close();
            out = null;

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + dir.getAbsolutePath())));

        } catch (FileNotFoundException fnfe1) {
            Log.e(TAG, fnfe1.getMessage());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void clearApplicationData() {

//		SharedPreferences preferences = PreferenceManager
//				.getDefaultSharedPreferences(this);
//		SharedPreferences.Editor editor = preferences.edit();
//		editor.clear();
//		editor.commit();

        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib") && !s.equals("shared_prefs")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children.length > 0) {
                for (int i = children.length - 1; i >= 0; i--) {
                    if (!children[i].toString().contentEquals("Menu.xml")
                            && !children[i].toString().contains("Crash")
                            && !children[i].toString().contains("Logcat")
                            && !children[i].toString().contains(CamWrapper.SaveLogFileName)
                            && !children[i].toString().contentEquals(CamWrapper.ConfigFileName)) {
                        boolean success = deleteDir(new File(dir, children[i]));
                        if (!success) {
                            return false;
                        }
                    }
                }
            }
        }
        return dir.delete();
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

        Log.e(TAG,"------pbData="+ Arrays.toString(pbyData));


        if (i32CmdType == CamWrapper.GP_SOCK_TYPE_ACK) {
            switch (i32Mode) {
                case CamWrapper.GPSOCK_MODE_General:
                    switch (i32CmdID) {
                        case CamWrapper.GPSOCK_General_CMD_SetMode:
                            _bSetModeDone = true;
                            Log.e(TAG, "_bSetModeDone = true");
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
                    switch (i32CmdID) {
                        case CamWrapper.GPSOCK_Playback_CMD_GetFileCount: {
                            if (bSaveImageItem)
                                return;

                            int i32FileCount = (pbyData[0] & 0xFF) | ((pbyData[1] & 0xFF) << 8);
                            if (i32FileCount <= 0)
                                break;

                            _i32GettingThumbnailFileIndex = -1;
                            _i32GotThumbnailFileIndex = -1;
                            i32GetThumbnailCount = 0;
                            if (listImageItem != null)
                                listImageItem.clear();

                            m_bRunCreateGridViewDone = false;
                            m_i32DownlaodStatus = DownloadFlag_Completed;

                            for (int i = 0; i < i32FileCount; i++) {
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put(MAPKEY_ThumbnailFilePath, R.drawable.loading);
                                map.put(MAPKEY_FileName, "Unknown");
                                map.put(MAPKEY_FileSize, "0");
                                map.put(MAPKEY_FileTime, "0");
                                map.put(MAPKEY_FileStatus, FileTag_FileDeviceInit);
                                listImageItem.add(map);
                            }

                            UpdateGridView();
                            break;
                        }
                        case CamWrapper.GPSOCK_Playback_CMD_GetNameList: {
                            if (listImageItem == null) {
                                m_bRunCreateGridViewDone = true;
                                break;
                            }

                            _i32CommandIndex = i32CmdIndex;

                            int i32FileIndex = (pbyData[0] & 0xFF) + ((pbyData[1] & 0xFF) << 8);
                            int i32FileCount = pbyData[2] & 0xFF;

                            if (bIsStopUpdateThumbnail) {
                                CamWrapper.getComWrapperInstance().GPCamAbort(_i32CommandIndex);
                                CamWrapper.getComWrapperInstance().GPCamSendGetStatus();
                                break;
                            }

                            if (i32FileIndex + i32FileCount > listImageItem.size())
                                i32FileCount = listImageItem.size() - i32FileIndex;

                            synchronized (listImageItem) {
                                for (int i = i32FileIndex; i < i32FileCount + i32FileIndex; i++) {
                                    byte[] byTimeData = new byte[6];

                                    String strName = CamWrapper.getComWrapperInstance().GPCamGetFileName(i);
                                    if (null == strName) {
                                        strName = "";
                                    }
                                    HashMap<String, Object> map = listImageItem.get(i);

                                    map.put(MAPKEY_FileName, strName);
                                    map.put(MAPKEY_FileSize, CamWrapper.getComWrapperInstance().GPCamGetFileSize(i));
                                    CamWrapper.getComWrapperInstance().GPCamGetFileTime(i, byTimeData);

                                    StringBuilder sb = new StringBuilder();
                                    for (byte b : byTimeData) {
                                        sb.append(String.format("%02d", b));
                                    }
                                    map.put(MAPKEY_FileTime, sb.toString());
                                    if ((int) map.get(MAPKEY_FileStatus) == FileTag_FileDeviceInit)
                                        map.put(MAPKEY_FileStatus, FileTag_FileDeviceReady);
                                    byTimeData = null;

                                    map.put(MAPKEY_ThumbnailFilePath, R.drawable.loading);
                                    listImageItem.set(i, map);
                                }
                            }

                            m_bRunCreateGridViewDone = false;

                            if (m_UpdateThumbnailThread == null) {
                                m_UpdateThumbnailThread = new Thread(
                                        new UpdateThumbnailRunnable());
                                m_UpdateThumbnailThread.start();
                            }
                            break;
                        }
                        case CamWrapper.GPSOCK_Playback_CMD_GetThumbnail: {
                            if (listImageItem == null) {
                                m_bRunCreateGridViewDone = true;
                                break;
                            }

                            int i32FileIndex = (pbyData[0] & 0xFF)
                                    + ((pbyData[1] & 0xFF) << 8);
                            int i32Len = (pbyData[2] & 0xFF)
                                    + ((pbyData[3] & 0xFF) << 8);
                            char[] StringValus = new char[i32Len];

                            StringValus[0] = 0;
                            for (int i = 0; i < i32Len; i++)
                                StringValus[i] = (char) (pbyData[i + 4] & 0xFF);

                            _i32GotThumbnailFileIndex = i32FileIndex;
                            _i32CommandIndex = i32CmdIndex;

                            if (bIsStopUpdateThumbnail) {
                                CamWrapper.getComWrapperInstance().GPCamAbort(_i32CommandIndex);
                                CamWrapper.getComWrapperInstance().GPCamSendGetStatus();
                                break;
                            }

                            synchronized (listImageItem) {
                                if (_i32GotThumbnailFileIndex < listImageItem.size()) {
                                    HashMap<String, Object> map = listImageItem.get(_i32GotThumbnailFileIndex);
                                    if ((int) map.get(MAPKEY_FileStatus) == FileTag_FileGettingThumbnail) {
                                        i32GetThumbnailCount++;
                                    }
                                    map.put(MAPKEY_ThumbnailFilePath, String.valueOf(StringValus));
                                    map.put(MAPKEY_FileStatus, FileTag_FileGotThumbnail);
                                    listImageItem.set(_i32GotThumbnailFileIndex, map);
                                }
                            }
                            StringValus = null;
                            break;
                        }
                        case CamWrapper.GPSOCK_Playback_CMD_GetRawData: {
                            _i32CommandIndex = i32CmdIndex;

//					if (m_i32DownlaodStatus == DownloadFlag_Stopping || m_i32DownlaodStatus == DownloadFlag_Completed) {
//						CamWrapper.getComWrapperInstance().GPCamClearCommandQueue();
//						CamWrapper.getComWrapperInstance().GPCamAbort(_i32CommandIndex);
//						CamWrapper.getComWrapperInstance().GPCamSendGetStatus();
//						break;
//					}

                            Log.e(TAG, "GetRawData ... 0 = " + (pbyData[0] & 0xFF) + "   1 = " + (pbyData[1] & 0xFF));
                            int i32Finish = pbyData[0] & 0xFF;
                            if (i32Finish == 0x01) // Finish
                            {
                                final int i32FileIndex = (pbyData[1] & 0xFF) + ((pbyData[2] & 0xFF) << 8);
                                int i32Len = (pbyData[3] & 0xFF) + ((pbyData[4] & 0xFF) << 8);
                                char[] StringValus = new char[i32Len];
                                StringValus[0] = 0;
                                for (int i = 0; i < i32Len; i++)
                                    StringValus[i] = (char) (pbyData[i + 5] & 0xFF);
                                final String strFilePath = String.valueOf(StringValus);

                                if (m_DownloadDialog != null)
                                    m_DownloadDialog.setMessage(getResources().getString(R.string.Copy_file));
                                bIsCopingFile = true;

                                if (i32FileIndex < listImageItem.size()) {
                                    HashMap<String, Object> map = listImageItem.get(i32FileIndex);
                                    final String strName = (String) map.get(MAPKEY_FileName);
                                    new Thread(new Runnable() {
                                        public void run() {
                                            copyFile(strFilePath, strDevicePICLocation + strName);
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    if (m_DownloadDialog != null) {
                                                        if (m_DownloadDialog.isShowing()) {
                                                            m_DownloadDialog.dismiss();
                                                            m_DownloadDialog = null;
                                                        }
                                                    }
                                                    m_i32DownlaodStatus = DownloadFlag_Completed;
                                                    m_bPendingGetThumbnail = false;
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            } else {
                                bIsCopingFile = false;
                                float fPercent = (pbyData[1] & 0xFF);
                                if (m_DownloadDialog != null) {
                                    m_DownloadDialog.setMessage(getResources().getString(R.string.Downloading));
                                    m_DownloadDialog.setProgress((int) fPercent);
                                }
                                m_i32DownlaodStatus = DownloadFlag_Downloading;
                            }
                            break;
                        }
                        case CamWrapper.GPSOCK_Playback_CMD_DeleteFile: {
                            if (m_UpdateThumbnailThread != null) {
                                m_UpdateThumbnailThread.interrupt();
                                m_UpdateThumbnailThread = null;
                            }
                            listImageItem.remove(m_iDeleteSDposition);

                            m_UpdateThumbnailThread = new Thread(new UpdateThumbnailRunnable());
                            m_UpdateThumbnailThread.start();
                            if (m_Dialog != null) {
                                if (m_Dialog.isShowing()) {
                                    m_Dialog.dismiss();
                                    m_Dialog = null;
                                }
                            }
                            break;
                        }
                        case CamWrapper.GPSOCK_Playback_CMD_Stop:
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
            switch (i32Mode) {
                case CamWrapper.GPSOCK_MODE_Playback:
                    if (i32CmdID == CamWrapper.GPSOCK_Playback_CMD_DeleteFile) {
                        m_bPendingGetThumbnail = false;
                        if (m_Dialog != null) {
                            if (m_Dialog.isShowing()) {
                                m_Dialog.dismiss();
                                m_Dialog = null;
                            }
                        }
                    }
                    break;
            }
            int i32ErrorCode = (pbyData[0] & 0xFF) + ((pbyData[1] & 0xFF) << 8);

            switch (i32ErrorCode) {
                case CamWrapper.Error_ServerIsBusy:
                    Log.e(TAG, "Error_ServerIsBusy ... ");
                    _i32ErrorCount++;

                    if (_i32ErrorCount > 20) {
                        _i32ErrorCount = 0;
                        m_i32DownlaodStatus = DownloadFlag_Completed;
                        CamWrapper.getComWrapperInstance().GPCamAbort(_i32CommandIndex);
                        CamWrapper.getComWrapperInstance().GPCamSendGetStatus();
                        Toast.makeText(m_Context, getResources().getString(R.string.Get_Thumbnail_Failed),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CamWrapper.Error_InvalidCommand:
                    Log.e(TAG, "Error_InvalidCommand ... ");
                    m_bPendingGetThumbnail = false;
                    synchronized (listImageItem) {
                        if (_i32GotThumbnailFileIndex + 1 < listImageItem.size() && m_i32DownlaodStatus == DownloadFlag_Completed) {
                            HashMap<String, Object> map = listImageItem.get(_i32GotThumbnailFileIndex + 1);
                            map.put(MAPKEY_FileStatus, FileTag_FileBroken);
                            listImageItem.set(_i32GotThumbnailFileIndex + 1, map);
                        }
                    }
                    m_i32DownlaodStatus = DownloadFlag_Completed;
                    break;
                case CamWrapper.Error_RequestTimeOut:
                    Log.e(TAG, "Error_RequestTimeOut ... ");
                    m_i32DownlaodStatus = DownloadFlag_Completed;
                    break;
                case CamWrapper.Error_ModeError:
                    Log.e(TAG, "Error_ModeError ... ");
                    break;
                case CamWrapper.Error_NoStorage:
                    Log.e(TAG, "Error_NoStorage ... ");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(m_Context, getResources().getString(R.string.No_Storage), Toast.LENGTH_SHORT).show();
                        }
                    });
                    m_i32DownlaodStatus = DownloadFlag_Completed;
                    break;
                case CamWrapper.Error_WriteFail:
                    Log.e(TAG, "Error_WriteFail ... ");
                    m_i32DownlaodStatus = DownloadFlag_Completed;
                    break;
                case CamWrapper.Error_GetFileListFail:
                    Log.e(TAG, "Error_GetFileListFail ... ");
                    break;
                case CamWrapper.Error_GetThumbnailFail:
                    Log.e(TAG, "Error_GetThumbnailFail ... ");
                    synchronized (listImageItem) {
                        if (_i32GotThumbnailFileIndex + 1 < listImageItem.size() && m_i32DownlaodStatus == DownloadFlag_Completed) {
                            HashMap<String, Object> map = listImageItem.get(_i32GotThumbnailFileIndex + 1);
                            map.put(MAPKEY_FileStatus, FileTag_FileBroken);
                            listImageItem.set(_i32GotThumbnailFileIndex + 1, map);
                        }
                    }
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
                case CamWrapper.Error_NoFile:
                    Log.e(TAG, "Error_NoFile ...");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(m_Context, getResources().getString(R.string.No_File), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
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

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize += 1;
            }
        }

        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(String pathName,
                                                  int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }
}
