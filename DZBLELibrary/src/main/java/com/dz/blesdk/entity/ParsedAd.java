package com.dz.blesdk.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 作者:东芝(2017/11/20).
 * 功能:广播数据
 */
public  class ParsedAd{


    public byte flags;
    public List<UUID> uuids = new CopyOnWriteArrayList<>();
    public String localName;
    public List<Integer> manufacturers = new LinkedList<>();
}
