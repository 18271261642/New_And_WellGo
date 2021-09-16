package com.truescend.gofit.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * 作者:东芝(2017/12/30).
 * 功能:位图工具
 */

public class BitmapUtil {

    /**
     * 获取位图的中间范围
     *
     * @param src
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapCenterRange(Bitmap src, int width, int height) {
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        int bw = src.getWidth();//720
        int bh = src.getHeight();

        int top = (bh / 2) - (bw / 2);
        int bottom = (bh / 2) + (bw / 2);

        c.drawBitmap(src, new Rect(0, top, src.getWidth(), bottom), new RectF(0, 0, width, height), null);

        return bm;
    }

    /**
     * Bitmap转base64
     * @param src
     * @return
     */
    public static String convertToBase64(Bitmap src) {
        return Base64.encodeToString(convertToByteArrays(src), Base64.DEFAULT);
    }

    /**
     * base64转Bitmap
     * @param base64String
     * @return
     */
    public static Bitmap convertToBitmap(String base64String) {
        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
        return convertToBitmap(bytes);
    }

    public static Bitmap convertToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    public static byte[] convertToByteArrays(Bitmap src) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
