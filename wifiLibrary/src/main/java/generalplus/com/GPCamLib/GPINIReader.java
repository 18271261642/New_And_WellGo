package generalplus.com.GPCamLib;

import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class GPINIReader {
	private static String TAG = "GPINIReader";
	private Properties configuration;
	private String configurationFile = "";
	private static GPINIReader m_Instance = null;
	private static boolean _EnableShowLog = false;
	private static boolean _EnableSaveLog = false;

	private final static String INIReader_ShowLog = "ShowLog";
	private final static String INIReader_SaveLog = "SaveLog";

	public GPINIReader() {
		m_Instance = this;
		configuration = new Properties();


//		configurationFile = Environment.getExternalStorageDirectory().getPath()
//				+ "/Download/" + CamWrapper.CamDefaulFolderName + "/"
//				+ CamWrapper.ConfigFileName;

		configurationFile = "/storage/emulated/0/Android/data/com.truescend.gofit/files/"+ CamWrapper.CamDefaulFolderName + "/"
				+ CamWrapper.ConfigFileName;


		File f = new File(configurationFile);
		if (!f.exists() && !f.isDirectory()) {
			FileWriter fw;
			try {
				fw = new FileWriter(configurationFile);
				String strTemp = "";
				strTemp += String.format("%s = false\n", INIReader_SaveLog);
				strTemp += String.format("%s = false\n", INIReader_ShowLog);
				fw.write(strTemp);
				fw.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (load()) {
			String strProperty = get(INIReader_SaveLog);
			if (strProperty != null
					&& strProperty.toUpperCase().equalsIgnoreCase("TRUE"))
				_EnableSaveLog = true;
			strProperty = get(INIReader_ShowLog);
			if (strProperty != null
					&& get(INIReader_ShowLog).toUpperCase().equalsIgnoreCase(
							"TRUE"))
				_EnableShowLog = true;
		}
	}

	public static GPINIReader getInstance() {
		return m_Instance;
	}

	public boolean IsEnableShowLog() {
		return _EnableShowLog;
	}

	public boolean IsEnableSaveLog() {
		return _EnableSaveLog;
	}

	public boolean load() {
		boolean retval = false;

		try {
			configuration.load(new FileInputStream(this.configurationFile));
			retval = true;
		} catch (IOException e) {
			System.out.println("Configuration error: " + e.getMessage());
		}
		return retval;
	}

	public String get(String key) {
		return configuration.getProperty(key);
	}
}
