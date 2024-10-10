package com.zegocloud.uikit.plugin.signaling;

import android.app.Application;
import com.zegocloud.uikit.plugin.adapter.plugins.ZegoPluginType;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.CancelInvitationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ConnectUserCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.EndRoomBatchOperationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.InvitationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.PluginZIMUser;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.QueryRoomPropertyCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.QueryUsersInRoomAttributesCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.RenewTokenCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ResponseInvitationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.RoomCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.RoomPropertyOperationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.SendRoomMessageCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.SetUsersInRoomAttributesCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginEventHandler;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginNotificationConfig;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginProtocol;
import im.zego.zim.callback.ZIMCallAcceptanceSentCallback;
import im.zego.zim.callback.ZIMCallCancelSentCallback;
import im.zego.zim.callback.ZIMCallEndSentCallback;
import im.zego.zim.callback.ZIMCallInvitationSentCallback;
import im.zego.zim.callback.ZIMCallJoinSentCallback;
import im.zego.zim.callback.ZIMCallQuitSentCallback;
import im.zego.zim.callback.ZIMCallRejectionSentCallback;
import im.zego.zim.callback.ZIMCallingInvitationSentCallback;
import im.zego.zim.callback.ZIMEventHandler;
import im.zego.zim.callback.ZIMUsersInfoQueriedCallback;
import im.zego.zim.entity.ZIMCallAcceptConfig;
import im.zego.zim.entity.ZIMCallCancelConfig;
import im.zego.zim.entity.ZIMCallEndConfig;
import im.zego.zim.entity.ZIMCallInfo;
import im.zego.zim.entity.ZIMCallInviteConfig;
import im.zego.zim.entity.ZIMCallJoinConfig;
import im.zego.zim.entity.ZIMCallQuitConfig;
import im.zego.zim.entity.ZIMCallRejectConfig;
import im.zego.zim.entity.ZIMCallingInviteConfig;
import im.zego.zim.entity.ZIMUserFullInfo;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.entity.ZIMUsersInfoQueryConfig;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
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
    public PluginZIMUser getCurrentUser() {
        ZIMUserInfo userInfo = service.getUserInfo();
        if (userInfo == null) {
            return null;
        }
        PluginZIMUser pluginZIMUser = new PluginZIMUser();
        pluginZIMUser.userID = userInfo.userID;
        pluginZIMUser.userName = userInfo.userName;
        pluginZIMUser.userAvatarUrl = userInfo.userAvatarUrl;
        return pluginZIMUser;
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
    public void destroy() {
        service.destroy();
    }

    @Override
    public void renewToken(String token) {
        service.renewToken(token, null);
    }

    @Override
    public void sendInvitation(List<String> invitees, int timeout, String data,
        ZegoSignalingPluginNotificationConfig notificationConfig, InvitationCallback callback) {
        service.sendInvitation(invitees, timeout, data, notificationConfig, callback);
    }

    @Override
    public void cancelInvitation(List<String> invitees, String invitationID, String data,
        ZegoSignalingPluginNotificationConfig pushConfig, CancelInvitationCallback callback) {
        service.cancelInvitation(invitees, invitationID, data, pushConfig, callback);
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
    public void sendInRoomCommandMessage(String command, String roomID, SendRoomMessageCallback callback) {
        service.sendInRoomCommandMessage(command, roomID, callback);
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

    public void unregisterZIMEventHandler(ZIMEventHandler handler) {
        service.unregisterZIMEventHandler(handler);
    }

    public void registerZIMEventHandler(ZIMEventHandler handler) {
        service.registerZIMEventHandler(handler);
    }

    @Override
    public void enableNotifyWhenAppRunningInBackgroundOrQuit(boolean enable) {
        service.enableNotifyWhenAppRunningInBackgroundOrQuit(enable);
    }

    @Override
    public void enableFCMPush() {
        service.enableFCMPush();
    }

    @Override
    public void disableFCMPush() {
        service.disableFCMPush();
    }

    @Override
    public void enableHWPush(String hwAppID) {
        service.enableHWPush(hwAppID);
    }

    @Override
    public void enableMiPush(String miAppID, String miAppKey) {
        service.enableMiPush(miAppID, miAppKey);
    }

    @Override
    public void enableVivoPush(String vivoAppID, String vivoAppKey) {
        service.enableVivoPush(vivoAppID, vivoAppKey);
    }

    @Override
    public void enableOppoPush(String oppoAppID, String oppoAppKey, String oppoAppSecret) {
        service.enableOppoPush(oppoAppID, oppoAppKey, oppoAppSecret);
    }

    @Override
    public void setAppType(int appType) {
        service.setAppType(appType);
    }

    @Override
    public void registerPush() {
        service.registerPush();
    }

    @Override
    public void unregisterPush() {
        service.unregisterPush();
    }

    @Override
    public boolean isOtherPushEnabled() {
        return service.isOtherPushEnabled();
    }

    @Override
    public boolean isFCMPushEnabled() {
        return service.isFCMPushEnabled();
    }

    public ZIMCallInfo getZIMCallInfo(String callID) {
        return service.getZIMCallInfo(callID);
    }

    public ZIMUserInfo getUserInfo() {
        return service.getUserInfo();
    }

    public ZIMUserFullInfo getMemoryUserInfo(String userID) {
        return service.getMemoryUserInfo(userID);
    }

    /**
     * use the data in memory first,if not all cached in memory,then call zim.query().
     *
     * @param userIDList
     * @param config
     * @param callback
     */
    public void queryUserInfo(List<String> userIDList, ZIMUsersInfoQueryConfig config,
        ZIMUsersInfoQueriedCallback callback) {
        service.queryUserInfo(userIDList, config, callback);
    }

    public void callJoin(String callID, ZIMCallJoinConfig config, ZIMCallJoinSentCallback callback) {
        service.callJoin(callID, config, callback);
    }

    public void callQuit(String callID, ZIMCallQuitConfig config, ZIMCallQuitSentCallback callback) {
        service.callQuit(callID, config, callback);
    }

    public void callEnd(String callID, ZIMCallEndConfig config, ZIMCallEndSentCallback callback) {
        service.callEnd(callID, config, callback);
    }

    public void callCancel(List<String> invitees, String callID, ZIMCallCancelConfig config,
        ZIMCallCancelSentCallback callback) {
        service.callCancel(invitees, callID, config, callback);
    }

    public void callingInvite(List<String> invitees, String callID, ZIMCallingInviteConfig config,
        ZIMCallingInvitationSentCallback callback) {
        service.callingInvite(invitees, callID, config, callback);
    }

    public void callReject(String callID, ZIMCallRejectConfig config, ZIMCallRejectionSentCallback callback) {
        service.callReject(callID, config, callback);
    }

    public void callAccept(String callID, ZIMCallAcceptConfig config, ZIMCallAcceptanceSentCallback callback) {
        service.callAccept(callID, config, callback);
    }

    public void callInvite(List<String> invitees, ZIMCallInviteConfig config, ZIMCallInvitationSentCallback callback) {
        service.callInvite(invitees, config, callback);
    }

    public ZIMConnectionEvent getConnectionEvent() {
        return service.getConnectionEvent();
    }

    public ZIMConnectionState getConnectionState() {
        return service.getConnectionState();
    }
}
