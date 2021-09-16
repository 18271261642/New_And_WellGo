package com.generalplus.ffmpegLib;

public class ffDecodeFrame {

    public final static int FFDECODE_FORMAT_YUV420P                 = 0;
    public final static int FFDECODE_FORMAT_YUV422P                 = 1;
    public final static int FFDECODE_FORMAT_YUV444P                 = 2;
    public final static int FFDECODE_FORMAT_YUVJ420P                = 3;
    public final static int FFDECODE_FORMAT_YUVJ422P                = 4;
    public final static int FFDECODE_FORMAT_YUVJ444P                = 5;

    public byte data[][];
    public int  linesize[];
    public int  width;
    public int  height;
    public int  format;

    public ffDecodeFrame(byte data[][],int linesize[],int  width, int  height,int  format)
    {
        this.data=data;
        this.linesize=linesize;
        this.width=width;
        this.height=height;
        this.format=format;
    }

}
