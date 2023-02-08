package com.zegocloud.uikit.plugin.signaling;

import android.app.Application;
import com.zegocloud.uikit.plugin.adapter.plugins.CancelInvitationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.ConnectUserCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.EndRoomBatchOperationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.InvitationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.QueryRoomPropertyCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.QueryUsersInRoomAttributesCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.RenewTokenCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.ResponseInvitationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.RoomCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.RoomPropertyOperationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.SendRoomMessageCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.SetUsersInRoomAttributesCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.ZegoPluginType;
import com.zegocloud.uikit.plugin.adapter.plugins.ZegoSignalingPluginEventHandler;
import com.zegocloud.uikit.plugin.adapter.plugins.ZegoSignalingPluginNotificationConfig;
import com.zegocloud.uikit.plugin.adapter.plugins.ZegoSignalingPluginProtocol;

import im.zego.zim.callback.ZIMEventHandler;

import java.util.HashMap;
import java.util.List;

public class ZegoSignalingPlugin implements ZegoSignalingPluginProtocol {

    private static ZegoSignalingPlugin sInstance;

    private ZegoSignalingPlugin() {
    }

    public static ZegoSignalingPlugin getInstance() {
        synchronized (ZegoSignalingPlugin.class) {
            if (sInstance == null) {
                sInstance = new ZegoSignalingPlugin();
            }
            return sInstance;
        }
    }

    private ZegoSignalingPluginService service = new ZegoSignalingPluginService();

    @Override
    public void init(Application application, Long appID, String appSign) {
        service.init(application, appID, appSign);
    }

    @Override
    public void connectUser(String userID, String userName, ConnectUserCallback callback) {
        service.connectUser(userID, userName, callback);
    }

    @Override
    public void connectUser(String userID, String userName, String token, ConnectUserCallback callback) {
        service.connectUser(userID, userName, token, callback);
    }

    @Override
    public void disconnectUser() {
        service.disconnectUser();
    }

    @Override
    public void renewToken(String token, RenewTokenCallback callback) {
        service.renewToken(token, callback);
    }

    @Override
    public void sendInvitation(List<String> invitees, int timeout, String data, ZegoSignalingPluginNotificationConfig notificationConfig, InvitationCallback callback) {
        service.sendInvitation(invitees, timeout, data, notificationConfig, callback);
    }

    @Override
    public void cancelInvitation(List<String> invitees, String invitationID, String data,
                                 CancelInvitationCallback callback) {
        service.cancelInvitation(invitees, invitationID, data, callback);
    }

    @Override
    public void refuseInvitation(String invitationID, String data, ResponseInvitationCallback callback) {
        service.refuseInvitation(invitationID, data, callback);
    }

    @Override
    public void acceptInvitation(String invitationID, String data, ResponseInvitationCallback callback) {
        service.acceptInvitation(invitationID, data, callback);
    }

    @Override
    public void joinRoom(String roomID, RoomCallback callback) {
        service.joinRoom(roomID, callback);
    }

    @Override
    public void joinRoom(String roomID, String roomName, RoomCallback callback) {
        service.joinRoom(roomID, roomName, callback);
    }

    @Override
    public void leaveRoom(String roomID, RoomCallback callback) {
        service.leaveRoom(roomID, callback);
    }

    @Override
    public void setUsersInRoomAttributes(HashMap<String, String> attributes, List<String> userIDs, String roomID,
                                         SetUsersInRoomAttributesCallback callback) {
        service.setUsersInRoomAttributes(attributes, userIDs, roomID, callback);
    }

    @Override
    public void queryUsersInRoomAttributes(String roomID, int count, String nextFlag,
                                           QueryUsersInRoomAttributesCallback callback) {
        service.queryUsersInRoomAttributes(roomID, count, nextFlag, callback);
    }

    @Override
    public void updateRoomProperty(HashMap<String, String> attributes, String roomID, boolean isForce,
                                   boolean isDeleteAfterOwnerLeft, boolean isUpdateOwner, RoomPropertyOperationCallback callback) {
        service.updateRoomProperty(attributes, roomID, isForce, isDeleteAfterOwnerLeft, isUpdateOwner, callback);
    }

    @Override
    public void deleteRoomProperties(List<String> keys, String roomID, boolean isForce,
                                     RoomPropertyOperationCallback callback) {
        service.deleteRoomProperties(keys, roomID, isForce, callback);
    }

    @Override
    public void queryRoomProperties(String roomID, QueryRoomPropertyCallback callback) {
        service.queryRoomProperties(roomID, callback);
    }

    @Override
    public void beginRoomPropertiesBatchOperation(String roomID, boolean isDeleteAfterOwnerLeft, boolean isForce,
                                                  boolean isUpdateOwner) {
        service.beginRoomPropertiesBatchOperation(roomID, isDeleteAfterOwnerLeft, isForce, isUpdateOwner);
    }

    @Override
    public void endRoomPropertiesBatchOperation(String roomID, EndRoomBatchOperationCallback callback) {
        service.endRoomPropertiesBatchOperation(roomID, callback);
    }

    @Override
    public void sendRoomMessage(String text, String roomID, SendRoomMessageCallback callback) {
        service.sendRoomMessage(text, roomID, callback);
    }

    @Override
    public ZegoPluginType getPluginType() {
        return ZegoPluginType.SIGNALING;
    }

    @Override
    public String getVersion() {
        return "1.3.0";
    }

    @Override
    public void registerPluginEventHandler(ZegoSignalingPluginEventHandler handler) {
        service.registerPluginEventHandler(handler);
    }


    public void registerZIMEventHandler(ZIMEventHandler handler) {
        service.registerZIMEventHandler(handler);
    }

    @Override
    public void enableNotifyWhenAppRunningInBackgroundOrQuit(boolean enable) {
        service.enableNotifyWhenAppRunningInBackgroundOrQuit(enable);
    }

}
