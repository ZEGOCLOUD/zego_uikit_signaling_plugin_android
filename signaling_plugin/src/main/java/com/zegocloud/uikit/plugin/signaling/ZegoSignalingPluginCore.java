package com.zegocloud.uikit.plugin.signaling;

import android.app.Application;
import android.util.Log;
import com.zegocloud.uikit.ZegoUIKit;
import com.zegocloud.uikit.plugin.common.IZegoUIKitPlugin;
import com.zegocloud.uikit.plugin.common.PluginCallbackListener;
import com.zegocloud.uikit.plugin.common.PluginEventListener;
import com.zegocloud.uikit.plugin.common.ZegoUIKitPluginType;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.utils.GenericUtils;
import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMCallAcceptanceSentCallback;
import im.zego.zim.callback.ZIMCallCancelSentCallback;
import im.zego.zim.callback.ZIMCallInvitationSentCallback;
import im.zego.zim.callback.ZIMCallRejectionSentCallback;
import im.zego.zim.callback.ZIMEventHandler;
import im.zego.zim.callback.ZIMLoggedInCallback;
import im.zego.zim.entity.ZIMAppConfig;
import im.zego.zim.entity.ZIMCallAcceptConfig;
import im.zego.zim.entity.ZIMCallCancelConfig;
import im.zego.zim.entity.ZIMCallInvitationAcceptedInfo;
import im.zego.zim.entity.ZIMCallInvitationCancelledInfo;
import im.zego.zim.entity.ZIMCallInvitationReceivedInfo;
import im.zego.zim.entity.ZIMCallInvitationRejectedInfo;
import im.zego.zim.entity.ZIMCallInvitationSentInfo;
import im.zego.zim.entity.ZIMCallInviteConfig;
import im.zego.zim.entity.ZIMCallRejectConfig;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMErrorCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ZegoSignalingPluginCore implements IZegoUIKitPlugin {

    private static ZegoSignalingPluginCore sInstance;
    private ZIMUserInfo zimUserInfo;

    private ZegoSignalingPluginCore() {
    }

    public static ZegoSignalingPluginCore getInstance() {
        synchronized (ZegoSignalingPluginCore.class) {
            if (sInstance == null) {
                sInstance = new ZegoSignalingPluginCore();
            }
            return sInstance;
        }
    }

    /**
     * key: callID received by zim, there is no callID when start invite no return
     */
    private Map<String, InvitationData> invitationMap = new HashMap<>();

    private ZegoPluginInvitationService invitationService = new ZegoPluginInvitationService();
    private ZIMConnectionState zimConnectionState;

    public void init(Application application, Long appID, String appSign) {
        ZIMAppConfig zimAppConfig = new ZIMAppConfig();
        zimAppConfig.appID = appID;
        zimAppConfig.appSign = appSign;
        ZIM.create(zimAppConfig, application);
        ZIM.getInstance().setEventHandler(new ZIMEventHandler() {

            @Override
            public void onConnectionStateChanged(ZIM zim, ZIMConnectionState state, ZIMConnectionEvent event,
                JSONObject extendedData) {
                super.onConnectionStateChanged(zim, state, event, extendedData);
                zimConnectionState = state;
                Log.d(ZegoUIKit.TAG,
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
                try {
                    JSONObject jsonObject = new JSONObject(info.extendedData);
                    int type = jsonObject.getInt("type");
                    String inviterName = getStringFromJson(jsonObject, "inviter_name");
                    String data = getStringFromJson(jsonObject, "data");
                    JSONObject dataJson = new JSONObject(data);
                    JSONArray invitees = dataJson.getJSONArray("invitees");
                    List<ZegoUIKitUser> list = new ArrayList<>();
                    for (int i = 0; i < invitees.length(); i++) {
                        JSONObject invitee = invitees.getJSONObject(i);
                        String user_id = getStringFromJson(invitee, "user_id");
                        String user_name = getStringFromJson(invitee, "user_name");
                        list.add(new ZegoUIKitUser(user_id, user_name));
                    }
                    List<InvitationUser> invitationUsers = GenericUtils.map(list,
                        uiKitUser -> new InvitationUser(uiKitUser, InvitationState.WAITING));
                    ZegoUIKitUser inviteUser = new ZegoUIKitUser(info.inviter, inviterName);
                    InvitationData invitationData = new InvitationData(callID, inviteUser, invitationUsers, type);
                    addInvitationData(invitationData);

                    dataJson.put("invitationID", callID);
                    invitationService.notifyUIKitInvitationReceived(inviteUser, type, dataJson.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                InvitationData invitationData = removeInvitationData(callID);
                if (invitationData != null) {
                    invitationService.notifyUIKitInvitationCancelled(invitationData.inviter, info.extendedData);
                }
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
                InvitationUser invitationUser = getInvitee(callID, info.invitee);
                if (invitationUser != null) {
                    invitationUser.state = InvitationState.ACCEPT;
                    removeInvitationData(callID);
                    invitationService.notifyUIKitInvitationAccepted(invitationUser.user, info.extendedData);
                }
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
                InvitationUser invitationUser = getInvitee(callID, info.invitee);
                if (invitationUser != null) {
                    invitationUser.state = InvitationState.REFUSE;
                    removeIfAllChecked(callID);
                    invitationService.notifyUIKitInvitationRejected(invitationUser.user, info.extendedData);
                }
            }

            /**
             * invitee doesn't respond to call,missed
             * @param zim
             * @param callID
             */
            @Override
            public void onCallInvitationTimeout(ZIM zim, String callID) {
                super.onCallInvitationTimeout(zim, callID);
                InvitationData invitationData = removeInvitationData(callID);
                if (invitationData != null) {
                    invitationService.notifyUIKitInvitationTimeout(invitationData.inviter, "");
                }
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
                InvitationData invitationData = getInvitationByCallID(callID);
                if (invitationData == null) {
                    return;
                }
                List<InvitationUser> timeoutUsers = GenericUtils.filter(invitationData.invitees,
                    uiKitUser -> invitees.contains(uiKitUser.user.userID));
                for (InvitationUser timeoutUser : timeoutUsers) {
                    timeoutUser.state = InvitationState.TIMEOUT;
                }
                removeIfAllChecked(callID);
                List<ZegoUIKitUser> timeoutInvitees = GenericUtils.map(timeoutUsers,
                    invitationUser -> invitationUser.user);
                invitationService.notifyUIKitInvitationAnsweredTimeout(timeoutInvitees, "");
            }
        });
    }

    private void removeIfAllChecked(String callID) {
        InvitationData invitationData = getInvitationByCallID(callID);
        if (invitationData == null) {
            return;
        }
        boolean allChecked = true;
        for (InvitationUser invitee : invitationData.invitees) {
            if (invitee.state == InvitationState.WAITING) {
                allChecked = false;
                break;
            }
        }
        if (allChecked) {
            removeInvitationData(callID);
        }
    }

    public void login(String userID, String userName) {
        zimUserInfo = new ZIMUserInfo();
        zimUserInfo.userID = userID;
        zimUserInfo.userName = userName;
        loginZIM(zimUserInfo);
    }

    public void logout() {
        zimUserInfo = null;
        if (ZIM.getInstance() != null) {
            ZIM.getInstance().logout();
        }
    }


    private void onBackground() {
    }

    private void onForeground() {
        if (zimConnectionState == ZIMConnectionState.DISCONNECTED) {
            if (zimUserInfo != null && ZIM.getInstance() != null) {
                loginZIM(zimUserInfo);
            }
        }
    }

    private void loginZIM(ZIMUserInfo zimUserInfo) {
        Log.d(ZegoUIKit.TAG, "loginSignal() called with: userID = [" + zimUserInfo.userID + "]");
        ZIM.getInstance().login(zimUserInfo, null, new ZIMLoggedInCallback() {
            @Override
            public void onLoggedIn(ZIMError errorInfo) {
            }
        });
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

    private InvitationUser getInvitee(String callID, String userID) {
        InvitationData invitationData = getInvitationByCallID(callID);
        if (invitationData == null) {
            return null;
        }
        InvitationUser invitationUser = null;
        for (InvitationUser invitee : invitationData.invitees) {
            if (Objects.equals(invitee.user.userID, userID)) {
                invitationUser = invitee;
                break;
            }
        }
        return invitationUser;
    }

    void addInvitationData(InvitationData invitationData) {
        invitationMap.put(invitationData.id, invitationData);
    }

    InvitationData getInvitationByCallID(String callID) {
        return invitationMap.get(callID);
    }

    InvitationData removeInvitationData(String callID) {
        return invitationMap.remove(callID);
    }

    public void sendInvitation(List<String> invitees, int timeout, int type, String data,
        PluginCallbackListener listener) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", type);
            jsonObject.put("inviter_name", zimUserInfo.userName);
            jsonObject.put("data", data);

            JSONObject dataJson = new JSONObject(data);
            String roomID = dataJson.getString("call_id");
            JSONArray inviteesJson = dataJson.getJSONArray("invitees");
            List<ZegoUIKitUser> list = new ArrayList<>();
            for (int i = 0; i < inviteesJson.length(); i++) {
                JSONObject invitee = inviteesJson.getJSONObject(i);
                String user_id = ZegoSignalingPluginCore.getStringFromJson(invitee, "user_id");
                String user_name = ZegoSignalingPluginCore.getStringFromJson(invitee, "user_name");
                list.add(new ZegoUIKitUser(user_id, user_name));
            }
            ZIMCallInviteConfig config = new ZIMCallInviteConfig();
            config.timeout = timeout;
            config.extendedData = jsonObject.toString();

            ZIM.getInstance().callInvite(invitees, config, new ZIMCallInvitationSentCallback() {
                @Override
                public void onCallInvitationSent(String callID, ZIMCallInvitationSentInfo info, ZIMError errorInfo) {
                    if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                        ZegoUIKitUser inviter = new ZegoUIKitUser(zimUserInfo.userID, zimUserInfo.userName);
                        List<InvitationUser> invitationUsers = GenericUtils.map(list,
                            uiKitUser -> new InvitationUser(uiKitUser, InvitationState.WAITING));
                        InvitationData invitationData = new InvitationData(callID, inviter, invitationUsers, type);
                        addInvitationData(invitationData);

                        List<String> errorUserIDs = GenericUtils.map(info.errorInvitees, userInfo -> userInfo.userID);
                        List<ZegoUIKitUser> errorUsers = new ArrayList<>();
                        for (InvitationUser invitee : invitationData.invitees) {
                            if (errorUserIDs.contains(invitee.user.userID)) {
                                invitee.state = InvitationState.ERROR;
                                errorUsers.add(invitee.user);
                            }
                        }
                        removeIfAllChecked(callID);

                        if (listener != null) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("code", errorInfo.code.value());
                            map.put("message", errorInfo.message);
                            map.put("callID", callID);
                            map.put("errorInvitees", errorUsers);
                            listener.callback(map);
                        }
                    } else {
                        if (listener != null) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("code", errorInfo.code.value());
                            map.put("message", errorInfo.message);
                            map.put("callID", callID);
                            listener.callback(map);
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void cancelInvitation(List<String> invitees, String data, PluginCallbackListener listener) {
        ZIMCallCancelConfig cancelConfig = new ZIMCallCancelConfig();
        cancelConfig.extendedData = data;

        String callID = null;
        for (InvitationData invitationData : invitationMap.values()) {
            if (invitees.isEmpty()) {
                break;
            }
            List<String> inviteUserIDs = GenericUtils.map(invitationData.invitees,
                invitationUser -> invitationUser.user.userID);
            for (String invitee : invitees) {
                if (inviteUserIDs.contains(invitee)) {
                    callID = invitationData.id;
                    break;
                }
            }
            if (callID != null) {
                break;
            }
        }
        if (callID == null) {
            return;
        }
        ZIM.getInstance().callCancel(invitees, callID, cancelConfig, new ZIMCallCancelSentCallback() {
            @Override
            public void onCallCancelSent(String callID, ArrayList<String> errorInvitees, ZIMError errorInfo) {
                InvitationData invitationData = getInvitationByCallID(callID);
                List<ZegoUIKitUser> errorCancelUsers = new ArrayList<>();
                if (invitationData != null) {
                    for (InvitationUser invitationUser : invitationData.invitees) {
                        boolean cancelUser = invitees.contains(invitationUser.user.userID);
                        boolean cancelError = errorInvitees.contains(invitationUser.user.userID);
                        if (cancelUser && !cancelError) {
                            invitationUser.state = InvitationState.CANCEL;
                        } else {
                            invitationUser.state = InvitationState.ERROR;
                        }
                        if (cancelError) {
                            errorCancelUsers.add(invitationUser.user);
                        }
                    }
                }
                removeIfAllChecked(callID);
                if (listener != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", errorInfo.code.value());
                    map.put("message", errorInfo.message);
                    map.put("errorInvitees", errorCancelUsers);
                    listener.callback(map);
                }
            }
        });
    }

    public void refuseInvitation(String inviterID, String data, PluginCallbackListener listener) {
        ZIMCallRejectConfig config = new ZIMCallRejectConfig();
        config.extendedData = data;

        String invitationID = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            invitationID = getStringFromJson(jsonObject, "invitationID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (invitationID == null) {
            for (InvitationData value : invitationMap.values()) {
                if (Objects.equals(value.inviter.userID, inviterID)) {
                    invitationID = value.id;
                    break;
                }
            }
        }
        if (invitationID == null) {
            return;
        }

        ZIM.getInstance().callReject(invitationID, config, new ZIMCallRejectionSentCallback() {
            @Override
            public void onCallRejectionSent(String callID, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    removeInvitationData(callID);
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

    public void acceptInvitation(String inviterID, String data, PluginCallbackListener listener) {
        ZIMCallAcceptConfig config = new ZIMCallAcceptConfig();
        config.extendedData = data;
        InvitationData result = null;
        for (InvitationData value : invitationMap.values()) {
            if (Objects.equals(value.inviter.userID, inviterID)) {
                result = value;
                break;
            }
        }
        if (result == null) {
            return;
        }
        ZIM.getInstance().callAccept(result.id, config, new ZIMCallAcceptanceSentCallback() {
            @Override
            public void onCallAcceptanceSent(String callID, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    removeInvitationData(callID);
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

    @Override
    public ZegoUIKitPluginType getPluginType() {
        return ZegoUIKitPluginType.CALL_INVITATION;
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public void invoke(String method, Map<String, Object> params, PluginCallbackListener listener) {
        //        Log.d(ZegoUIKit.TAG,
        //            "invoke() called with: method = [" + method + "], params = [" + params + "], listener = [" + listener
        //                + "]");
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
                login(userID, userName);
            }
            break;
            case "logout": {
                logout();
            }
            break;
            case "onForeground": {
                onForeground();
            }
            break;
            case "onBackground": {
                onBackground();
            }
            break;
            case "sendInvitation": {
                List<String> invitees = (List<String>) params.get("invitees");
                int timeout = (int) params.get("timeout");
                int type = (int) params.get("type");
                String data = (String) params.get("data");
                sendInvitation(invitees, timeout, type, data, listener);
            }
            break;
            case "cancelInvitation": {
                List<String> invitees = (List<String>) params.get("invitees");
                String data = (String) params.get("data");
                cancelInvitation(invitees, data, listener);
            }
            break;
            case "refuseInvitation": {
                String inviterID = (String) params.get("inviterID");
                String data = (String) params.get("data");
                refuseInvitation(inviterID, data, listener);
            }
            break;
            case "acceptInvitation": {
                String inviterID = (String) params.get("inviterID");
                String data = (String) params.get("data");
                acceptInvitation(inviterID, data, listener);
            }
            break;
        }
    }

    @Override
    public void registerPluginEventHandler(PluginEventListener listener) {
        invitationService.setPluginEventListener(listener);
    }
}
