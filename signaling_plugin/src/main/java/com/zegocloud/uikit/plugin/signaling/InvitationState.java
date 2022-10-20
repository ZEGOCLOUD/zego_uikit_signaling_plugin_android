package com.zegocloud.uikit.plugin.signaling;

public enum InvitationState {
    ERROR, WAITING, ACCEPT, REFUSE, CANCEL,
    TIMEOUT, // 好像没有用，如果timeout了，整个invitation生命周期结束，直接向上回调就好了
}
