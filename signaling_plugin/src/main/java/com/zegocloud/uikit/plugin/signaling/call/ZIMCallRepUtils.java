package com.zegocloud.uikit.plugin.signaling.call;

import androidx.annotation.NonNull;
import im.zego.zim.entity.ZIMCallInfo;
import im.zego.zim.entity.ZIMCallInvitationCreatedInfo;
import im.zego.zim.entity.ZIMCallInvitationReceivedInfo;

public class ZIMCallRepUtils {

    static @NonNull ZIMCallInfo convertToZIMCallInfo(ZIMCallInvitationCreatedInfo info, String callID) {
        ZIMCallInfo callInfo = new ZIMCallInfo();
        callInfo.callID = callID;
        callInfo.caller = info.caller;
        callInfo.callUserList = info.callUserList;
        callInfo.createTime = info.createTime;
        callInfo.mode = info.mode;
        callInfo.extendedData = info.extendedData;
        return callInfo;
    }

    static @NonNull ZIMCallInfo convertToZIMCallInfo(ZIMCallInvitationReceivedInfo info, String callID) {
        ZIMCallInfo callInfo = new ZIMCallInfo();
        callInfo.callID = callID;
        callInfo.caller = info.caller;
        callInfo.callUserList = info.callUserList;
        callInfo.createTime = info.createTime;
        callInfo.mode = info.mode;
        callInfo.extendedData = info.extendedData;
        callInfo.inviter = info.inviter;
        return callInfo;
    }
}
