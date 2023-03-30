package com.zegocloud.uikit.plugin.signaling;

import android.content.Context;
import im.zego.zpns.ZPNsMessageReceiver;
import im.zego.zpns.entity.ZPNsMessage;
import im.zego.zpns.entity.ZPNsRegisterMessage;

public class ZegoZPNsReceiver extends ZPNsMessageReceiver {

    @Override
    protected void onThroughMessageReceived(Context context, ZPNsMessage message) {

    }

    @Override
    protected void onNotificationClicked(Context context, ZPNsMessage message) {

    }

    @Override
    protected void onNotificationArrived(Context context, ZPNsMessage message) {

    }

    @Override
    protected void onRegistered(Context context, ZPNsRegisterMessage message) {

    }
}
