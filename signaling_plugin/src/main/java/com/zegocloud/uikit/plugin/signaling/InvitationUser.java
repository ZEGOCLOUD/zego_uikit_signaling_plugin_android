package com.zegocloud.uikit.plugin.signaling;

import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

public class InvitationUser {
    ZegoUIKitUser user;
    InvitationState state;

    public InvitationUser(ZegoUIKitUser user, InvitationState state) {
        this.user = user;
        this.state = state;
    }
}
