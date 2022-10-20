package com.zegocloud.uikit.plugin.signaling;

import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import java.util.List;

public class InvitationData {

    String id; // invitation ID
    ZegoUIKitUser inviter;
    List<InvitationUser> invitees;
    int type;

    public InvitationData(String id, ZegoUIKitUser inviter, List<InvitationUser> invitees, int type) {
        this.id = id;
        this.inviter = inviter;
        this.invitees = invitees;
        this.type = type;
    }
}
