package com.sn.net.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 作者:东芝(2018/3/5).
 * 功能:文件下载
 */

public class FileDownload {
    public static boolean dowanload(String url, String path ,OnProgressChangeListener listener) throws IOException {
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10 * 1000);
            File file = new File(path);
            //文件夹是否存在
            if (!file.getParentFile().exists())
                file.getParentFile().mkdir();
            if (file.exists())
                file.delete();
            file.createNewFile();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                inputStream = urlConnection.getInputStream();
                int len = 0;
                byte[] data = new byte[4096];
                //用于保存当前进度（具体进度）
                int progres = 0;
                //获取文件长度
                int maxProgres = urlConnection.getContentLength();
                randomAccessFile = new RandomAccessFile(file, "rwd");
                //设置文件大小
                randomAccessFile.setLength(maxProgres);
                //将文件大小分成100分，每一分的大小为unit
                int unit = maxProgres / 100;
                //用于保存当前进度(1~100%)
                int unitProgress = 0;
                while (-1 != (len = inputStream.read(data))) {
                    randomAccessFile.write(data, 0, len);
                    progres += len;//保存当前具体进度
                    int temp = progres / unit; //计算当前百分比进度
                    if (temp >= 1 && temp > unitProgress) {//如果下载过程出现百分比变化
                        unitProgress = temp;//保存当前百分比
                        if (listener != null) {
                            listener.onProgress(unitProgress);
                        }
                    }
                }
                inputStream.close();
                return true;
            } else {
                return false;
            }
        } finally {
            if (null != inputStream) {
                inputStream.close();
            }
            if (null != randomAccessFile) {
                randomAccessFile.close();
            }
        }

    }

    public interface OnProgressChangeListener{
        void onProgress(int progress);
    }

}
