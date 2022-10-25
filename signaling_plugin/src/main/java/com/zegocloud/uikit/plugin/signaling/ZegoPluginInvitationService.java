package com.zegocloud.uikit.plugin.signaling;

import com.zegocloud.uikit.plugin.common.PluginEventListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZegoPluginInvitationService {

    private PluginEventListener pluginEventListener;

    public void setPluginEventListener(PluginEventListener pluginEventListener) {
        this.pluginEventListener = pluginEventListener;
    }

    public void notifyUIKitInvitationReceived(ZegoUIKitUser inviteUser, int type, String data) {
        Map<String, Object> map = new HashMap<>();
        map.put("inviter", inviteUser);
        map.put("type", type);
        map.put("data", data);
        pluginEventListener.onPluginEvent("onCallInvitationReceived", map);
    }

    public void notifyUIKitInvitationCancelled(ZegoUIKitUser inviteUser, String data) {
        Map<String, Object> map = new HashMap<>();
        map.put("inviter", inviteUser);
        map.put("data", data);
        pluginEventListener.onPluginEvent("onCallInvitationCancelled", map);
    }

    public void notifyUIKitInvitationAccepted(ZegoUIKitUser invitee, String data) {
        Map<String, Object> map = new HashMap<>();
        map.put("invitee", invitee);
        map.put("data", data);
        pluginEventListener.onPluginEvent("onCallInvitationAccepted", map);
    }

    public void notifyUIKitInvitationRejected(ZegoUIKitUser invitee, String data) {
        Map<String, Object> map = new HashMap<>();
        map.put("invitee", invitee);
        map.put("data", data);
        pluginEventListener.onPluginEvent("onCallInvitationRejected", map);
    }

    public void notifyUIKitInvitationTimeout(ZegoUIKitUser inviter, String data) {
        Map<String, Object> map = new HashMap<>();
        map.put("inviter", inviter);
        map.put("data", data);
        pluginEventListener.onPluginEvent("onCallInvitationTimeout", map);
    }

    public void notifyUIKitInvitationAnsweredTimeout(List<ZegoUIKitUser> users, String data) {
        Map<String, Object> map = new HashMap<>();
        map.put("invitees", users);
        map.put("data", data);
        pluginEventListener.onPluginEvent("onCallInviteesAnsweredTimeout", map);
    }
}
