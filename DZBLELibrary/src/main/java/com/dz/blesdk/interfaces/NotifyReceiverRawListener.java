package com.dz.blesdk.interfaces;

import java.util.UUID;


public interface NotifyReceiverRawListener extends CommunicationListener {
    void onReceive(UUID uuid, byte[] buffer);
}
