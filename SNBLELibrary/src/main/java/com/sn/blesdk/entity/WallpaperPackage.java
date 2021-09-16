package com.sn.blesdk.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2019/3/19).
 * 功能:
 */


public class WallpaperPackage {
    private List<byte[]> bytes20 = new ArrayList<>();

    public List<byte[]> getBytes20() {
        return bytes20;
    }

    public WallpaperPackage(List<byte[]> bytes20) {
        this.bytes20 = bytes20;
    }

}
