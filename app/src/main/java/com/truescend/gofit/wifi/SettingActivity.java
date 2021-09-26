package com.truescend.gofit.wifi;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.truescend.gofit.R;

import java.io.File;
import java.util.ArrayList;

import generalplus.com.GPCamLib.CamWrapper;
import generalplus.com.GPCamLib.GPXMLParse;

/**
 * WiFi设置页面
 */
public class SettingActivity extends PreferenceActivity {

    private static final String TAG = "SettingActivity";
    GPXMLParse m_GPXMLParse;
    private static ArrayList<GPXMLParse.GPXMLCategory> m_xmlGategory;
    private static Thread ParseXMLThread = null;
    private ProgressDialog m_Dialog;
    private Handler m_handler;
    private boolean m_bDismiss = false;
    private Handler m_dismissHandler = null;

    public final int Reflash_All_Setting_ID = 0x00000208;
    public final int ClearBuff_Setting_ID = 0x00000206;

    public final int SettingType_RadioButton = 0x00;
    public final int SettingType_ConfirmAction = 0x01;
    public final int SettingType_EditText = 0x02;
    public final int SettingType_DisplayOnly = 0x03;
    public final int SettingType_ConfirmActionForDevice = 0x04;
    private boolean m_bGoFWUpgrade = false;
    private String m_strFilePath = "";


    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.e(TAG,"-msgWhat="+msg.what);
            if(msg.what == 0x00){
                if(m_Dialog != null)
                    m_Dialog.dismiss();
            }

            if(msg.what == 0x01){
                clearApplicationData();
                if (m_xmlGategory != null && m_xmlGategory.size() > 0)
                    UpdateSettingListView();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate ...");

        super.onCreate(savedInstanceState);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        m_GPXMLParse = new GPXMLParse();
        m_handler = new Handler();


      //  ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x00);


        CamWrapper.getComWrapperInstance().GPCamSendSetMode(CamWrapper.GPDEVICEMODE_Menu);
//		m_strFilePath = String.format(Environment
//				.getExternalStorageDirectory().getPath() + "/Download/" + CamWrapper.CamDefaulFolderName + "/" + CamWrapper.ParameterFileName);


        m_strFilePath = String.format(getExternalFilesDir(null) + "/" + CamWrapper.CamDefaulFolderName + "/" + CamWrapper.ParameterFileName);


        String tmpPath = getExternalFilesDir(null).getPath();

        Log.e(TAG, "---11-m_strFilePath=" + m_strFilePath + " " + tmpPath);


        if (CamWrapper.bIsDefault) {
            setTitle(getString(R.string.app_name) + "*");
//			m_strFilePath = String.format(Environment
//					.getExternalStorageDirectory().getPath() + "/Download/" + CamWrapper.CamDefaulFolderName + "/" + CamWrapper.DefaultParameterFileName);

            m_strFilePath = String.format(getExternalFilesDir(null).getPath() + "/" + CamWrapper.CamDefaulFolderName + "/" + CamWrapper.DefaultParameterFileName);

            Log.e(TAG, "--22--m_strFilePath=" + m_strFilePath);
        }
            if (ParseXMLThread == null) {
                if (m_Dialog == null) {
                    m_Dialog = new ProgressDialog(this);
                    m_Dialog.setMessage(getResources().getString(R.string.Getting_menu));
                    m_Dialog.setCanceledOnTouchOutside(false);
                    m_Dialog.setCancelable(false);
                }
                m_Dialog.show();

                Log.e(TAG,"------dialog-------");


                m_dismissHandler = new Handler();
                handler.sendEmptyMessageDelayed(0x00,10 * 1000);
    //            m_dismissHandler.postDelayed(new Runnable() {
    //
    //                @Override
    //                public void run() {
    //                    if (m_bDismiss) {
    //                        return;
    //                    }
    //                    runOnUiThread(new Runnable() {
    //                        public void run() {
    //                            if (m_Dialog != null) {
    //                                if (m_Dialog.isShowing()) {
    //                                    m_Dialog.setCancelable(true);
    //                                }
    //                            }
    //                        }
    //                    });
    //                }
    //            }, 10000);

                ParseXMLThread = new Thread(new ParseXMLRunnable(m_strFilePath));
                ParseXMLThread.start();


            }
    }




    public static ArrayList<GPXMLParse.GPXMLCategory> getXMLCategory() {
        return m_xmlGategory;
    }

    public void setCategorySettingDefautValue(int CategoryIndex,
                                              int SettingIndex, String DefaultValue) {
        m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings.get(SettingIndex).strXMLSettingDefaultValue = DefaultValue;
    }

    public void UpdateSettingListView() {
        Log.e(TAG,"------2222--------1111----");
        PreferenceScreen screen = getPreferenceManager()
                .createPreferenceScreen(this);
        Log.e(TAG,"------screen="+(screen == null));
        for (int i32CategoryIndex = 0; i32CategoryIndex < m_xmlGategory.size(); i32CategoryIndex++) {
            PreferenceCategory category = new PreferenceCategory(this);
            category.setTitle(m_xmlGategory.get(i32CategoryIndex).strXMLCategoryName);
            screen.addPreference(category);

            for (int i32SettingIndex = 0; i32SettingIndex < m_xmlGategory
                    .get(i32CategoryIndex).aryListGPXMLSettings.size(); i32SettingIndex++) {

                long tmpLong = Long.parseLong(  m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                        .get(i32SettingIndex).strXMLSettingType
                        .substring(2).toString(), 16);

                Log.e(TAG,"-----tmpLong="+tmpLong+" "+((int)tmpLong));

                switch ((int) Long
                        .parseLong(
                                m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                                        .get(i32SettingIndex).strXMLSettingType
                                        .substring(2).toString(), 16)) {
                    case SettingType_RadioButton:  //0x00
                        category.addPreference(getListPreference(i32CategoryIndex,
                                i32SettingIndex));
                        break;
                    case SettingType_ConfirmAction:  //0x01
                        category.addPreference(getPreference(i32CategoryIndex,
                                i32SettingIndex, true));
                        break;
                    case SettingType_EditText:  //0x02
                        category.addPreference(getEditTextPreference(
                                i32CategoryIndex, i32SettingIndex));
                        break;
                    case SettingType_DisplayOnly:  //0x03
                        category.addPreference(getPreference(i32CategoryIndex,
                                i32SettingIndex, false));
                        break;
                    case SettingType_ConfirmActionForDevice:  //0x04
                        category.addPreference(getPreference(i32CategoryIndex,
                                i32SettingIndex, true));
                        break;
                    default:
                        category.addPreference(getPreference(i32CategoryIndex,
                                i32SettingIndex, false));
                        break;
                }

            }
        }

        super.setPreferenceScreen(screen);
    }

    private EditTextPreference getEditTextPreference(int CategoryIndex,
                                                     int SettingIndex) {

        Log.e(TAG,"---getEditTextPreference="+CategoryIndex+" "+SettingIndex);

        EditTextPreference editTextPreference = new EditTextPreference(this);
        editTextPreference.getEditText().setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }

            @Override
            protected char[] getAcceptedChars() {
                char[] data = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
                return data;
            }
        });
        editTextPreference.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});

        String titStr = m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                .get(SettingIndex).strXMLSettingName.toString();
        Log.e(TAG,"------titStr="+titStr);
        editTextPreference
                .setTitle(titStr);

        CharSequence summAry = (m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                .get(SettingIndex).strXMLSettingDefaultValue.toString());

        Log.e(TAG,"-----summAry="+summAry);

        editTextPreference
                .setSummary(summAry);
        String txt = m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                .get(SettingIndex).strXMLSettingDefaultValue.toString();
        Log.e(TAG,"-----txt="+txt);
        editTextPreference
                .setText(txt);
        String setKey = String.valueOf(m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                .get(SettingIndex).i32TreeLevel);
        Log.e(TAG,"-----setKey="+setKey);
        editTextPreference
                .setKey(setKey);

        Object deftValue = m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                .get(SettingIndex).strXMLSettingDefaultValue.toString();
      Log.e(TAG,"-----deftValue="+deftValue);
        editTextPreference
                .setDefaultValue(deftValue);

        editTextPreference
                .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference,
                                                      Object newValue) {
                        preference.setSummary((CharSequence) newValue);
                        preference.setDefaultValue((CharSequence) newValue);

                        int i32KeyValue = Integer.valueOf(preference.getKey().toString());
                        int i32CategoryIndex = i32KeyValue / (0x01 << m_GPXMLParse.CategoryLevel);
                        int i32SettingIndex = (i32KeyValue - (i32CategoryIndex * (0x01 << m_GPXMLParse.CategoryLevel))) / (0x01 << m_GPXMLParse.SettingLevel);
                        int i32SettingID = Integer.valueOf(m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                                .get(i32SettingIndex).strXMLSettingID.substring(2).toString(), 16);

                        int i32Len = ((String) newValue).length();
                        byte[] byStringData = new byte[i32Len];
                        ((String) newValue).getBytes(0, i32Len, byStringData, 0);
                        SendSetParameter(i32SettingID, byStringData.length, byStringData);

                        byStringData = null;
                        return true;
                    }

                });

        return editTextPreference;
    }

    private Preference getPreference(int CategoryIndex, int SettingIndex,
                                     boolean bHasClickEvent) {
        Log.e(TAG,"------getPreference="+CategoryIndex+" "+SettingIndex);

        Preference preference = new Preference(this);
        String titleStr = m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                .get(SettingIndex).strXMLSettingName.toString();

        Log.e(TAG,"-----titleStr="+titleStr);

        preference.setTitle(titleStr);

        String strCurrent = m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings.get(SettingIndex).strXMLSettingCurrent;
        if (null != strCurrent) {
            preference.setSummary(strCurrent);
        } else {
            if (m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                    .get(SettingIndex).aryListGPXMLValues.size() > 0) {
                preference.setSummary(m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                        .get(SettingIndex).aryListGPXMLValues.get(0).strXMLValueName.toString());
            }
        }

        preference.setKey(String.valueOf(m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings.get(SettingIndex).i32TreeLevel).toString());

        if (bHasClickEvent) {
            preference
                    .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {

                            ParseCategoriesData(Integer.valueOf(preference.getKey().toString()));

                            return true;
                        }

                    });
        }

        return preference;
    }

    private static int i32IndexValue = 0;

    private ListPreference getListPreference(int CategoryIndex, int SettingIndex) {
        ListPreference listPreference = new ListPreference(this);

        if (m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                .get(SettingIndex).aryListGPXMLValues.size() > 0) {
            listPreference.setTitle(m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings.get(SettingIndex).strXMLSettingName.toString());
            listPreference.setDialogTitle(m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings.get(SettingIndex).strXMLSettingName.toString());
            CharSequence charSequenceValues[] = new CharSequence[m_xmlGategory
                    .get(CategoryIndex).aryListGPXMLSettings.get(SettingIndex).aryListGPXMLValues
                    .size()];
            for (int i32ValueIndex = 0; i32ValueIndex < m_xmlGategory
                    .get(CategoryIndex).aryListGPXMLSettings.get(SettingIndex).aryListGPXMLValues
                    .size(); i32ValueIndex++) {
                charSequenceValues[i32ValueIndex] = m_xmlGategory
                        .get(CategoryIndex).aryListGPXMLSettings
                        .get(SettingIndex).aryListGPXMLValues
                        .get(i32ValueIndex).strXMLValueName.toString();

                if ((int) Long.parseLong(
                        m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                                .get(SettingIndex).strXMLSettingDefaultValue
                                .substring(2).toString(), 16) == i32ValueIndex) {
                    String strSettingName = m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                            .get(SettingIndex).strXMLSettingName;
                    String strSettingDefault = m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                            .get(SettingIndex).strXMLSettingDefaultValue;

                    listPreference.setDefaultValue(m_xmlGategory
                            .get(CategoryIndex).aryListGPXMLSettings
                            .get(SettingIndex).aryListGPXMLValues
                            .get(i32ValueIndex).strXMLValueName.toString());

                    listPreference
                            .setSummary(m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                                    .get(SettingIndex).aryListGPXMLValues
                                    .get(i32ValueIndex).strXMLValueName
                                    .toString());
                }

            }
            listPreference.setEntries(charSequenceValues);
            listPreference.setEntryValues(charSequenceValues);
            listPreference
                    .setKey(String.valueOf(m_xmlGategory.get(CategoryIndex).aryListGPXMLSettings
                            .get(SettingIndex).i32TreeLevel));
            listPreference
                    .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(
                                Preference preference, Object newValue) {
                            preference.setSummary((CharSequence) newValue);
                            preference.setDefaultValue((CharSequence) newValue);
                            int i32KeyValue = Integer.valueOf(preference
                                    .getKey().toString());
                            int i32CategoryIndex = i32KeyValue
                                    / (0x01 << m_GPXMLParse.CategoryLevel);
                            int i32SettingIndex = (i32KeyValue - (i32CategoryIndex * (0x01 << m_GPXMLParse.CategoryLevel)))
                                    / (0x01 << m_GPXMLParse.SettingLevel);

                            int i32Value = 0x00;
                            if (m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                                    .get(i32SettingIndex).aryListGPXMLValues
                                    .size() > 0) {
                                for (int i32ValueIndex = 0; i32ValueIndex < m_xmlGategory
                                        .get(i32CategoryIndex).aryListGPXMLSettings
                                        .get(i32SettingIndex).aryListGPXMLValues
                                        .size(); i32ValueIndex++) {
                                    if ((String) newValue == m_xmlGategory
                                            .get(i32CategoryIndex).aryListGPXMLSettings
                                            .get(i32SettingIndex).aryListGPXMLValues
                                            .get(i32ValueIndex).strXMLValueName
                                            .toString()) {
                                        i32Value = Integer.valueOf(
                                                m_xmlGategory
                                                        .get(i32CategoryIndex).aryListGPXMLSettings
                                                        .get(i32SettingIndex).aryListGPXMLValues
                                                        .get(i32ValueIndex).strXMLValueID
                                                        .substring(2)
                                                        .toString(), 16);
                                        break;
                                    }
                                }
                            }

                            int i32SettingID = Integer.valueOf(m_xmlGategory
                                    .get(i32CategoryIndex).aryListGPXMLSettings
                                    .get(i32SettingIndex).strXMLSettingID
                                    .substring(2).toString(), 16);
                            byte[] byData = {(byte) i32Value};
                            SendSetParameter(i32SettingID, 1, byData);
                            m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                                    .get(i32SettingIndex).strXMLSettingDefaultValue = String
                                    .format("0x%02X ", i32Value);
                            byData = null;
                            return true;
                        }

                    });

        } else
            listPreference.setTitle(getResources().getString(R.string.Unknown));
        return listPreference;
    }

    private static int i32SelectedCategoryIndex = 0,
            i32SelectedSettingIndex = 0;

    private void ParseCategoriesData(int KeyValue) {
        int i32CategoryIndex = KeyValue / (0x01 << m_GPXMLParse.CategoryLevel);
        int i32SettingIndex = (KeyValue - (i32CategoryIndex * (0x01 << m_GPXMLParse.CategoryLevel)))
                / (0x01 << m_GPXMLParse.SettingLevel);
        int i32SelectSettingType = Integer.valueOf(m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                .get(i32SettingIndex).strXMLSettingType.substring(2).toString(), 16);

        if (i32SelectSettingType == SettingType_ConfirmAction
                || i32SelectSettingType == SettingType_ConfirmActionForDevice) {

            i32SelectedCategoryIndex = i32CategoryIndex;
            i32SelectedSettingIndex = i32SettingIndex;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            TextView tv = new TextView(this);
            tv.setText(getResources().getString(R.string.Are_you_sure_to_do) + "\n\""
                    + m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings
                    .get(i32SettingIndex).strXMLSettingName + "\""
                    + "?");
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setCancelable(false);
            builder.setView(tv);
            builder.setPositiveButton(getResources().getString(R.string.Confirm),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            int i32SettingID = Integer.valueOf(
                                    m_xmlGategory.get(i32SelectedCategoryIndex).aryListGPXMLSettings
                                            .get(i32SelectedSettingIndex).strXMLSettingID
                                            .substring(2).toString(), 16);

                            byte[] byData = {0x00};
                            SendSetParameter(i32SettingID, 0, byData);
                            byData = null;

                            if (Reflash_All_Setting_ID == i32SettingID) {
                                if (ParseXMLThread == null) {
                                    ParseXMLThread = new Thread(new ParseXMLRunnable(m_strFilePath));
                                    ParseXMLThread.start();
                                }
                            } else if (ClearBuff_Setting_ID == i32SettingID) {
                                clearApplicationData();
                            }
                        }
                    });

            builder.setNegativeButton(getResources().getString(R.string.Cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    class ParseXMLRunnable implements Runnable {
        private String m_strXMLFilePath;

        ParseXMLRunnable(String XMLFilePath) {
            this.m_strXMLFilePath = XMLFilePath;
            m_Dialog.show();
        }

        @Override
        public void run() {
            if (m_xmlGategory == null || m_xmlGategory.size() == 0){
                m_xmlGategory = m_GPXMLParse.GetGPXMLInfo(m_strXMLFilePath);
//                m_xmlGategory.clear();
//                ArrayList<GPXMLParse.GPXMLCategory> tmplist = m_GPXMLParse.GetGPXMLInfo(m_strXMLFilePath);
//                m_xmlGategory.addAll(tmplist);
            }


            Log.e(TAG,"-----m_xmlGategory.size="+m_xmlGategory.size() +" "+new Gson().toJson(m_xmlGategory));

            for (int i32CategoryIndex = 0; i32CategoryIndex < m_xmlGategory
                    .size(); i32CategoryIndex++) {
                for (int i32SettingIndex = 0; i32SettingIndex < m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings.size(); i32SettingIndex++) {
                    CamWrapper.getComWrapperInstance().GPCamSendGetParameter(
                            (int) Long.parseLong(m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings.get(i32SettingIndex).strXMLSettingID.substring(2).toString(), 16));
                }
            }

//            try {
//                Thread.sleep(2 * 1000);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }

//            Log.e(TAG,"------1--------1111----");
//            m_handler.post(new Runnable() {
//                public void run() {
//                    clearApplicationData();
//                    if (m_xmlGategory != null && m_xmlGategory.size() > 0)
//                        UpdateSettingListView();
//                }
//            });


            handler.sendEmptyMessageDelayed(0x01,1000);


//            ParseXMLThread.interrupt();
//            ParseXMLThread = null;
            if (m_Dialog != null) {
                if (m_Dialog.isShowing()) {
                    m_Dialog.dismiss();
                    m_Dialog = null;
                    m_bDismiss = true;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"----onResume----");
        CamWrapper.getComWrapperInstance().SetViewHandler(m_FromWrapperHandler, CamWrapper.GPVIEW_MENU);
        m_bGoFWUpgrade = false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"---onPause----");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"---onStop----");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG, "onBackPressed GPCamClearCommandQueue");
        CamWrapper.getComWrapperInstance().GPCamClearCommandQueue();
    }

    @Override
    protected void onDestroy() {

        Log.e(TAG,"----onDestroy----");
        if (m_Dialog != null) {
            if (m_Dialog.isShowing()) {
                m_Dialog.dismiss();
                m_Dialog = null;
            }
        }
        m_xmlGategory.clear();
        m_xmlGategory = null;
        clearApplicationData();
        if (null != m_dismissHandler) {
            m_dismissHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    protected void onUserLeaveHint() {
        if (!m_bGoFWUpgrade) {
            CamWrapper.getComWrapperInstance().GPCamSendSetMode(CamWrapper.GPDEVICEMODE_Record);
            finish();
        }

        super.onUserLeaveHint();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    public void clearApplicationData() {

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

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

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
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
                    if (i32CmdID == CamWrapper.GPSOCK_Menu_CMD_GetParameter) {
                        int i32ID = (pbyData[0] & 0xFF)
                                + ((pbyData[1] & 0xFF) << 8)
                                + ((pbyData[2] & 0xFF) << 16)
                                + ((pbyData[3] & 0xFF) << 24);

                        if (null == m_xmlGategory) {
                            Log.e(TAG, "CamWrapper.GPSOCK_Menu_CMD_GetParameter, null == m_xmlGategory");
                            return;
                        }

                        for (int i32CategoryIndex = 0; i32CategoryIndex < m_xmlGategory.size(); i32CategoryIndex++) {
                            for (int i32SettingIndex = 0; i32SettingIndex < m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings.size(); i32SettingIndex++) {
                                int i32SettingID = (int) Long.parseLong(m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings.get(i32SettingIndex).strXMLSettingID.substring(2).toString(), 16);
                                if (i32SettingID == i32ID) {
                                    if (i32DataSize == 5)    // SettingType_RadioButton
                                        m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings.get(i32SettingIndex).strXMLSettingDefaultValue = String.format("0x%02X", pbyData[4] & 0xFF);
                                    else                    // SettingType_EditText
                                    {
                                        int i32Len = i32DataSize
                                                - (int) (Math.log10(i32ID) + 1) - 1;
                                        char[] StringValus = new char[i32Len];
                                        for (int i = 0; i < i32Len; i++)
                                            StringValus[i] = (char) (pbyData[i + 4] & 0xFF);
                                        m_xmlGategory.get(i32CategoryIndex).aryListGPXMLSettings.get(i32SettingIndex).strXMLSettingDefaultValue = String.valueOf(StringValus);
                                        StringValus = null;
                                    }
                                    break;
                                }
                            }
                        }
                    } else if (i32CmdID == CamWrapper.GPSOCK_Menu_CMD_SetParameter) {
                        if (m_Dialog != null) {
                            if (m_Dialog.isShowing()) {
                                m_Dialog.dismiss();
                                m_Dialog = null;
                            }
                        }
                    }
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
            if (m_Dialog != null) {
                if (m_Dialog.isShowing()) {
                    m_Dialog.dismiss();
                    m_Dialog = null;
                }
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (MainViewController.m_bRtsp) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.upgrade, menu);
            return super.onCreateOptionsMenu(menu);
        }

        if (CamWrapper.getComWrapperInstance().getIsNewFile()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.upgrade, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_fwupgrade:
                m_bGoFWUpgrade = true;
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, FWUpgradeActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void FinishToMainController() {
        Log.e(TAG, "Finish ...");
        CamWrapper.getComWrapperInstance().GPCamDisconnect();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private int SendSetParameter(int ID, int Size, byte[] Data) {
        Log.e(TAG, "GPCamSendSetParameter ... ");
        if (m_Dialog == null) {
            m_Dialog = new ProgressDialog(this);
            m_Dialog.setMessage(getResources().getString(R.string.Setting));
            m_Dialog.setCanceledOnTouchOutside(false);
        }
        m_Dialog.show();
        return CamWrapper.getComWrapperInstance().GPCamSendSetParameter(ID, Size, Data);
    }
}
