package com.zegocloud.uikit.plugin.signaling.room;

import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMRoomAllLeftCallback;
import im.zego.zim.callback.ZIMRoomCreatedCallback;
import im.zego.zim.callback.ZIMRoomEnteredCallback;
import im.zego.zim.callback.ZIMRoomJoinedCallback;
import im.zego.zim.callback.ZIMRoomLeftCallback;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMRoomAdvancedConfig;
import im.zego.zim.entity.ZIMRoomFullInfo;
import im.zego.zim.entity.ZIMRoomInfo;
import im.zego.zim.enums.ZIMErrorCode;
import im.zego.zim.enums.ZIMRoomEvent;
import im.zego.zim.enums.ZIMRoomState;
import java.util.ArrayList;
import java.util.Objects;
import org.json.JSONObject;

public class ZIMRoomRepository {

    private ZIMRoomFullInfo roomFullInfo;
    private ZIMRoomInfo pendingEnterRoomInfo;
    private boolean isEnterIngRoom;
    private ZIMRoomState roomState;
    private ZIMRoomEvent roomEvent;

    public void createRoom(ZIMRoomInfo roomInfo, ZIMRoomCreatedCallback callback) {

    }

    public void createRoom(ZIMRoomInfo roomInfo, ZIMRoomAdvancedConfig config, ZIMRoomCreatedCallback callback) {

    }

    public void joinRoom(String roomID, ZIMRoomJoinedCallback callback) {

    }

    public void enterRoom(ZIMRoomInfo roomInfo, ZIMRoomAdvancedConfig config, ZIMRoomEnteredCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onRoomEntered(null, errorInfo);
            }
        }
        if (!isEnterIngRoom) {
            pendingEnterRoomInfo = roomInfo;
            isEnterIngRoom = true;
            ZIM.getInstance().enterRoom(roomInfo, config, new ZIMRoomEnteredCallback() {
                @Override
                public void onRoomEntered(ZIMRoomFullInfo roomInfo, ZIMError errorInfo) {
                    pendingEnterRoomInfo = null;
                    isEnterIngRoom = false;
                    roomFullInfo = roomInfo;
                    if (callback != null) {
                        callback.onRoomEntered(roomInfo, errorInfo);
                    }
                }
            });
        }
    }

    public String getEnteringRoomID() {
        if (roomFullInfo != null) {
            return roomFullInfo.baseInfo.roomID;
        }
        if (pendingEnterRoomInfo != null) {
            return pendingEnterRoomInfo.roomID;
        }
        return null;
    }

    public String getRoomID() {
        if (roomFullInfo != null) {
            return roomFullInfo.baseInfo.roomID;
        }
        return null;
    }

    public void leaveRoom(String roomID, ZIMRoomLeftCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onRoomLeft(null, errorInfo);
            }
        }
        ZIM.getInstance().leaveRoom(roomID, new ZIMRoomLeftCallback() {

            public void onRoomLeft(String roomID, ZIMError errorInfo) {
                if (roomFullInfo != null && Objects.equals(roomFullInfo.baseInfo.roomID, roomID)) {
                    roomFullInfo = null;
                }
                if (pendingEnterRoomInfo != null && Objects.equals(pendingEnterRoomInfo.roomID, roomID)) {
                    pendingEnterRoomInfo = null;
                }
                if (callback != null) {
                    callback.onRoomLeft(roomID, errorInfo);
                }
            }
        });
    }

    public void leaveAllRoom(ZIMRoomAllLeftCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onRoomAllLeft(null, errorInfo);
            }
        }
        ZIM.getInstance().leaveAllRoom(new ZIMRoomAllLeftCallback() {
            @Override
            public void onRoomAllLeft(ArrayList<String> roomIDs, ZIMError errorInfo) {
                if (callback != null) {
                    callback.onRoomAllLeft(roomIDs, errorInfo);
                }
            }
        });
    }

    public void onRoomStateChanged(ZIM zim, ZIMRoomState state, ZIMRoomEvent event, JSONObject extendedData,
        String roomID) {
        this.roomState = state;
        this.roomEvent = event;
    }
}
