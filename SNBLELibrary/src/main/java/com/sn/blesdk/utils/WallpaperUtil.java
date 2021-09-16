package com.sn.blesdk.utils;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 作者:东芝(2019/3/18).
 * 功能:
 */

public class WallpaperUtil {
    private static int RGB565_RED = 0xf800;
    private static int RGB565_GREEN = 0x07e0;
    private static int RGB565_BLUE = 0x001f;
    private static int RGB888_RED = 0x00ff0000;
    private static int RGB888_GREEN = 0x0000ff00;
    private static int RGB888_BLUE = 0x000000ff;

    public static byte[] BMP2RGB565bytes(Bitmap src) {
        //转成RGB565 格式的图片
        Bitmap bmpRGB565 = src.copy(Bitmap.Config.RGB_565, false);
        //缩放到新分辨率 240x240
        Bitmap bmpScaledRGB565 = Bitmap.createScaledBitmap(bmpRGB565,240, 240, false);
        //申请 ByteBuffer 装 RGB565 数据
        ByteBuffer buf = ByteBuffer.allocate(bmpScaledRGB565.getWidth() * bmpScaledRGB565.getHeight() * 2);
        buf.order(ByteOrder.BIG_ENDIAN);
        //RGB565 格式的图片数据导出到 ByteBuffer
        bmpScaledRGB565.copyPixelsToBuffer(buf);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        //ByteBuffer 导出为字节数组
        return converting(buf.array());
    }

    private static byte[] converting(byte[] in) {
        int length = in.length;
        byte[] out = new byte[length];
        for (int i = 0; i < length; i += 2) {
            out[i] = in[i + 1];
            out[i + 1] = in[i];
        }
        return out;
    }

   public static int RGB888ToRGB565(int n888Color) {
        // 获取RGB单色，并截取高位
        int cRed = (n888Color & RGB888_RED) >> 19;
        int cGreen = (n888Color & RGB888_GREEN) >> 10;
        int cBlue = (n888Color & RGB888_BLUE) >> 3;
        // 连接
        return (cRed << 11) + (cGreen << 5) + (cBlue << 0);
    }

    public static int RGB565ToRGB888(int n565Color) {
        // 获取RGB单色，并填充低位
        int cRed = (n565Color & RGB565_RED) >> 8;
        int cGreen = (n565Color & RGB565_GREEN) >> 3;
        int cBlue = (n565Color & RGB565_BLUE) << 3;
        // 连接
        return (cRed << 16) + (cGreen << 8) + (cBlue << 0);
    }

}
