package com.zegocloud.uikit.plugin.signaling;

import android.app.Application;
import android.util.Log;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.plugin.common.IZegoUIKitPlugin;
import com.zegocloud.uikit.plugin.common.PluginCallbackListener;
import com.zegocloud.uikit.plugin.common.PluginEventListener;
import com.zegocloud.uikit.plugin.common.ZegoUIKitPluginType;
import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMEventHandler;
import im.zego.zim.callback.ZIMLoggedInCallback;
import im.zego.zim.entity.ZIMAppConfig;
import im.zego.zim.entity.ZIMCallInvitationAcceptedInfo;
import im.zego.zim.entity.ZIMCallInvitationCancelledInfo;
import im.zego.zim.entity.ZIMCallInvitationReceivedInfo;
import im.zego.zim.entity.ZIMCallInvitationRejectedInfo;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMRoomAttributesBatchOperationConfig;
import im.zego.zim.entity.ZIMRoomAttributesDeleteConfig;
import im.zego.zim.entity.ZIMRoomAttributesSetConfig;
import im.zego.zim.entity.ZIMRoomAttributesUpdateInfo;
import im.zego.zim.entity.ZIMRoomMemberAttributesUpdateInfo;
import im.zego.zim.entity.ZIMRoomOperatedInfo;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMErrorCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class ZegoSignalingPlugin implements IZegoUIKitPlugin {

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

    private static final String TAG = "ZegoSignalingPlugin";

    private ZegoPluginInvitationService invitationService = new ZegoPluginInvitationService();
    private ZegoUserInRoomAttributesPluginService userInRoomAttributesPluginService = new ZegoUserInRoomAttributesPluginService();
    private ZegoRoomPropertiesPluginService roomPropertiesPluginService = new ZegoRoomPropertiesPluginService();
    private ZIMConnectionState zimConnectionState;
    private ZIMUserInfo currentZIMUserInfo;
    private boolean isInLoginProcess;

    private void init(Application application, Long appID, String appSign) {
        ZIMAppConfig zimAppConfig = new ZIMAppConfig();
        zimAppConfig.appID = appID;
        zimAppConfig.appSign = appSign;
        ZIM.create(zimAppConfig, application);
        ZIM.getInstance().setEventHandler(new ZIMEventHandler() {

            @Override
            public void onRoomMemberLeft(ZIM zim, ArrayList<ZIMUserInfo> memberList, String roomID) {
                super.onRoomMemberLeft(zim, memberList, roomID);
                userInRoomAttributesPluginService.onRoomMemberLeft(zim, memberList, roomID);
            }

            @Override
            public void onConnectionStateChanged(ZIM zim, ZIMConnectionState state, ZIMConnectionEvent event,
                JSONObject extendedData) {
                super.onConnectionStateChanged(zim, state, event, extendedData);
                zimConnectionState = state;
                Log.d(TAG,
                    "onConnectionStateChanged() called with: zim = [" + zim + "], state = [" + state + "], event = ["
                        + event + "], extendedData = [" + extendedData + "]");
            }

            @Override
            public void onError(ZIM zim, ZIMError errorInfo) {
                super.onError(zim, errorInfo);
            }

            /**
             * invitee received a call from inviter.
             * @param zim
             * @param info
             * @param callID
             */
            @Override
            public void onCallInvitationReceived(ZIM zim, ZIMCallInvitationReceivedInfo info, String callID) {
                super.onCallInvitationReceived(zim, info, callID);
                invitationService.onCallInvitationReceived(zim, info, callID);
            }

            /**
             * invitee received when inviter cancelled call.
             * @param zim
             * @param info
             * @param callID
             */
            @Override
            public void onCallInvitationCancelled(ZIM zim, ZIMCallInvitationCancelledInfo info, String callID) {
                super.onCallInvitationCancelled(zim, info, callID);
                invitationService.onCallInvitationCancelled(zim, info, callID);
            }

            /**
             * inviter received invitees accept.
             * @param zim
             * @param info
             * @param callID
             */
            @Override
            public void onCallInvitationAccepted(ZIM zim, ZIMCallInvitationAcceptedInfo info, String callID) {
                super.onCallInvitationAccepted(zim, info, callID);
                invitationService.onCallInvitationAccepted(zim, info, callID);
            }

            /**
             * inviter received invitees reject.
             * @param zim
             * @param info
             * @param callID
             */
            @Override
            public void onCallInvitationRejected(ZIM zim, ZIMCallInvitationRejectedInfo info, String callID) {
                super.onCallInvitationRejected(zim, info, callID);
                invitationService.onCallInvitationRejected(zim, info, callID);
            }

            /**
             * invitee doesn't respond to call,missed
             * @param zim
             * @param callID
             */
            @Override
            public void onCallInvitationTimeout(ZIM zim, String callID) {
                super.onCallInvitationTimeout(zim, callID);
                invitationService.onCallInvitationTimeout(zim, callID);
            }

            /**
             * inviter received invitees no respond,they missed.
             * @param zim
             * @param invitees
             * @param callID
             */
            @Override
            public void onCallInviteesAnsweredTimeout(ZIM zim, ArrayList<String> invitees, String callID) {
                super.onCallInviteesAnsweredTimeout(zim, invitees, callID);
                invitationService.onCallInviteesAnsweredTimeout(zim, invitees, callID);
            }

            @Override
            public void onRoomMemberAttributesUpdated(ZIM zim, ArrayList<ZIMRoomMemberAttributesUpdateInfo> infos,
                ZIMRoomOperatedInfo operatedInfo, String roomID) {
                super.onRoomMemberAttributesUpdated(zim, infos, operatedInfo, roomID);
                userInRoomAttributesPluginService.notifyOnRoomMemberAttributesUpdated(infos, operatedInfo, roomID);
            }

            @Override
            public void onRoomAttributesUpdated(ZIM zim, ZIMRoomAttributesUpdateInfo info, String roomID) {
                super.onRoomAttributesUpdated(zim, info, roomID);
                roomPropertiesPluginService.notifyOnRoomPropertiesUpdated(info);
            }

            @Override
            public void onRoomAttributesBatchUpdated(ZIM zim, ArrayList<ZIMRoomAttributesUpdateInfo> infos,
                String roomID) {
                super.onRoomAttributesBatchUpdated(zim, infos, roomID);
                for (ZIMRoomAttributesUpdateInfo info : infos) {
                    roomPropertiesPluginService.notifyOnRoomPropertiesUpdated(info);
                }
            }
        });
    }

    private void login(String userID, String userName, PluginCallbackListener listener) {
        currentZIMUserInfo = new ZIMUserInfo();
        currentZIMUserInfo.userID = userID;
        currentZIMUserInfo.userName = userName;
        loginZIM(currentZIMUserInfo, listener);
    }

    private void logout() {
        currentZIMUserInfo = null;
        if (ZIM.getInstance() != null) {
            ZIM.getInstance().logout();
        }
    }

    private void onActivityStarted() {
        if (ZIM.getInstance() != null) {
            boolean isDisconnected = zimConnectionState == ZIMConnectionState.DISCONNECTED;
            if (currentZIMUserInfo != null && isDisconnected && !isInLoginProcess) {
                loginZIM(currentZIMUserInfo, null);
            }
        }
    }

    private void loginZIM(ZIMUserInfo zimUserInfo, PluginCallbackListener listener) {
        isInLoginProcess = true;
        ZIM.getInstance().login(zimUserInfo, null, new ZIMLoggedInCallback() {
            @Override
            public void onLoggedIn(ZIMError errorInfo) {
                isInLoginProcess = false;
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    currentZIMUserInfo = null;
                }
                if (listener != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", errorInfo.code.value());
                    map.put("message", errorInfo.message);
                    listener.callback(map);
                }
            }
        });
    }

    static JSONObject getJsonObjectFromString(String data) {
        try {
            return new JSONObject(data);
        } catch (JSONException e) {
            Log.w(ZegoUIKit.TAG, "data is empty");
        }
        return null;
    }

    static String getStringFromJson(JSONObject jsonObject, String key) {
        try {
            if (jsonObject.has(key)) {
                return jsonObject.getString(key);
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ZegoUIKitPluginType getPluginType() {
        return ZegoUIKitPluginType.SIGNALING;
    }

    @Override
    public String getVersion() {
        return "1.2.0";
    }

    @Override
    public void invoke(String method, Map<String, Object> params, PluginCallbackListener listener) {
        Log.d(TAG, "invoke() called with: method = [" + method + "], params = [" + params + "], listener = [" + listener
            + "]");
        switch (method) {
            case "init": {
                Long appID = (Long) params.get("appID");
                String appSign = (String) params.get("appSign");
                Application application = (Application) params.get("application");
                init(application, appID, appSign);
            }
            break;
            case "login": {
                String userID = (String) params.get("userID");
                String userName = (String) params.get("userName");
                login(userID, userName, listener);
            }
            break;
            case "logout": {
                logout();
            }
            break;
            case "onActivityStarted": {
                onActivityStarted();
            }
            break;
            case "sendInvitation": {
                List<String> invitees = (List<String>) params.get("invitees");
                int timeout = (int) params.get("timeout");
                int type = (int) params.get("type");
                String data = (String) params.get("data");
                invitationService.sendInvitation(invitees, timeout, type, data, listener);
            }
            break;
            case "cancelInvitation": {
                List<String> invitees = (List<String>) params.get("invitees");
                String data = (String) params.get("data");
                invitationService.cancelInvitation(invitees, data, listener);
            }
            break;
            case "refuseInvitation": {
                String inviterID = (String) params.get("inviterID");
                String data = (String) params.get("data");
                invitationService.refuseInvitation(inviterID, data, listener);
            }
            break;
            case "acceptInvitation": {
                String inviterID = (String) params.get("inviterID");
                String data = (String) params.get("data");
                invitationService.acceptInvitation(inviterID, data, listener);
            }
            break;
            case "setUsersInRoomAttributes": {
                String key = (String) params.get("key");
                String value = (String) params.get("value");
                List<String> userIDs = (List<String>) params.get("userIDs");
                userInRoomAttributesPluginService.setUsersInRoomAttributes(key, value, userIDs, listener);
            }
            break;
            case "queryUsersInRoomAttributes": {
                String nextFlag = (String) params.get("nextFlag");
                int count = (int) params.get("count");
                userInRoomAttributesPluginService.queryUsersInRoomAttributesList(nextFlag, count, listener);
            }
            break;
            case "joinRoom": {
                String roomID = (String) params.get("roomID");
                userInRoomAttributesPluginService.joinRoom(roomID, listener);
            }
            break;
            case "leaveRoom": {
                userInRoomAttributesPluginService.leaveRoom(listener);
            }
            break;
            case "updateRoomProperty": {
                String key = (String) params.get("key");
                String value = (String) params.get("value");
                boolean isDeleteAfterOwnerLeft = (boolean) params.get("isDeleteAfterOwnerLeft");
                boolean isForce = (boolean) params.get("isForce");
                boolean isUpdateOwner = (boolean) params.get("isUpdateOwner");

                HashMap<String, String> roomAttributes = new HashMap<>();
                roomAttributes.put(key, value);
                ZIMRoomAttributesSetConfig config = new ZIMRoomAttributesSetConfig();
                config.isDeleteAfterOwnerLeft = isDeleteAfterOwnerLeft;
                config.isForce = isForce;
                config.isUpdateOwner = isUpdateOwner;
                roomPropertiesPluginService.updateRoomProperty(roomAttributes, config, listener);
            }
            break;
            case "deleteRoomProperties": {
                List<String> keys = (List<String>) params.get("keys");
                boolean isForce = (boolean) params.get("isForce");
                ZIMRoomAttributesDeleteConfig config = new ZIMRoomAttributesDeleteConfig();
                config.isForce = isForce;
                roomPropertiesPluginService.deleteRoomProperties(keys, config, listener);
            }
            break;
            case "beginRoomPropertiesBatchOperation": {
                boolean isDeleteAfterOwnerLeft = (boolean) params.get("isDeleteAfterOwnerLeft");
                boolean isForce = (boolean) params.get("isForce");
                boolean isUpdateOwner = (boolean) params.get("isUpdateOwner");
                ZIMRoomAttributesBatchOperationConfig config = new ZIMRoomAttributesBatchOperationConfig();
                config.isDeleteAfterOwnerLeft = isDeleteAfterOwnerLeft;
                config.isForce = isForce;
                config.isUpdateOwner = isUpdateOwner;
                roomPropertiesPluginService.beginRoomPropertiesBatchOperation(config, listener);
            }
            break;
            case "endRoomPropertiesBatchOperation": {
                roomPropertiesPluginService.endRoomPropertiesBatchOperation(listener);
            }
            break;
            case "queryRoomProperties": {
                roomPropertiesPluginService.queryRoomProperties(listener);
            }
            break;
        }
    }

    @Override
    public void registerPluginEventHandler(PluginEventListener listener) {
        invitationService.setPluginEventListener(listener);
        userInRoomAttributesPluginService.setPluginEventListener(listener);
        roomPropertiesPluginService.setPluginEventListener(listener);
    }

    ZIMUserInfo getZimUserInfo() {
        return currentZIMUserInfo;
    }
}
