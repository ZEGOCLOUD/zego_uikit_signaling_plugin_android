package com.zegocloud.uikit.plugin.signaling;

import com.zegocloud.uikit.plugin.common.PluginCallbackListener;
import com.zegocloud.uikit.plugin.common.PluginEventListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.utils.GenericUtils;
import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMCallAcceptanceSentCallback;
import im.zego.zim.callback.ZIMCallCancelSentCallback;
import im.zego.zim.callback.ZIMCallInvitationSentCallback;
import im.zego.zim.callback.ZIMCallRejectionSentCallback;
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
import im.zego.zim.enums.ZIMErrorCode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

public class ZegoPluginInvitationService {

    /**
     * key: callID received by zim, there is no callID when start invite no return
     */
    private Map<String, InvitationData> invitationMap = new HashMap<>();

    private PluginEventListener pluginEventListener;

    public void setPluginEventListener(PluginEventListener pluginEventListener) {
        this.pluginEventListener = pluginEventListener;
    }

    private InvitationUser getInvitee(String callID, String userID) {
        InvitationData invitationData = getInvitationByCallID(callID);
        if (invitationData == null) {
            return null;
        }
        InvitationUser invitationUser = null;
        for (InvitationUser invitee : invitationData.invitees) {
            if (Objects.equals(invitee.getUserID(), userID)) {
                invitationUser = invitee;
                break;
            }
        }
        return invitationUser;
    }

    public void onCallInvitationReceived(ZIM zim, ZIMCallInvitationReceivedInfo info, String callID) {
        try {
            JSONObject jsonObject = ZegoSignalingPlugin.getJsonObjectFromString(info.extendedData);
            if (jsonObject != null) {
                int type = jsonObject.getInt("type");
                String inviterName = ZegoSignalingPlugin.getStringFromJson(jsonObject, "inviter_name");
                String data = ZegoSignalingPlugin.getStringFromJson(jsonObject, "data");
                JSONObject dataJson = ZegoSignalingPlugin.getJsonObjectFromString(data);
                ZegoUIKitUser inviter = new ZegoUIKitUser(info.inviter, inviterName);
                ZIMUserInfo zimUserInfo = ZegoSignalingPlugin.getInstance().getZimUserInfo();
                InvitationUser invitee = new InvitationUser(new ZegoUIKitUser(zimUserInfo.userID, zimUserInfo.userName),
                    InvitationState.WAITING);
                InvitationData invitationData = new InvitationData(callID, inviter, Collections.singletonList(invitee),
                    type);
                addInvitationData(invitationData);

                if (dataJson == null) {
                    dataJson = new JSONObject();
                }
                dataJson.put("invitationID", callID);
                notifyUIKitInvitationReceived(inviter, type, dataJson.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        ZIMUserInfo zimUserInfo = ZegoSignalingPlugin.getInstance().getZimUserInfo();
        if (zimUserInfo == null) {
            if (listener != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("code", ZIMErrorCode.USER_IS_NOT_LOGGED.value());
                map.put("message", "USER_IS_NOT_LOGGED");
                map.put("callID", "");
                listener.callback(map);
            }
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("inviter_name", zimUserInfo.userName);
            jsonObject.put("data", data);

            ZIMCallInviteConfig config = new ZIMCallInviteConfig();
            config.timeout = timeout;
            config.extendedData = jsonObject.toString();

            ZIM.getInstance().callInvite(invitees, config, new ZIMCallInvitationSentCallback() {
                @Override
                public void onCallInvitationSent(String callID, ZIMCallInvitationSentInfo info, ZIMError errorInfo) {
                    if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                        ZegoUIKitUser inviter = new ZegoUIKitUser(zimUserInfo.userID, zimUserInfo.userName);
                        List<InvitationUser> invitationUsers = GenericUtils.map(invitees,
                            userID -> new InvitationUser(new ZegoUIKitUser(userID), InvitationState.WAITING));
                        InvitationData invitationData = new InvitationData(callID, inviter, invitationUsers, type);
                        addInvitationData(invitationData);

                        List<String> errorUserIDs = GenericUtils.map(info.errorInvitees, userInfo -> userInfo.userID);
                        List<ZegoUIKitUser> errorUsers = new ArrayList<>();
                        for (InvitationUser invitee : invitationData.invitees) {
                            if (errorUserIDs.contains(invitee.getUserID())) {
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
                invitationUser -> invitationUser.getUserID());
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
            if (listener != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("code", 0);
                listener.callback(map);
            }
            return;
        }
        ZIM.getInstance().callCancel(invitees, callID, cancelConfig, new ZIMCallCancelSentCallback() {
            @Override
            public void onCallCancelSent(String callID, ArrayList<String> errorInvitees, ZIMError errorInfo) {
                InvitationData invitationData = getInvitationByCallID(callID);
                List<ZegoUIKitUser> errorCancelUsers = new ArrayList<>();
                if (invitationData != null) {
                    for (InvitationUser invitationUser : invitationData.invitees) {
                        boolean cancelUser = invitees.contains(invitationUser.getUserID());
                        boolean cancelError = errorInvitees.contains(invitationUser.getUserID());
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
        JSONObject jsonObject = ZegoSignalingPlugin.getJsonObjectFromString(data);
        if (jsonObject != null) {
            invitationID = ZegoSignalingPlugin.getStringFromJson(jsonObject, "invitationID");
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
            if (listener != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("code", 0);
                listener.callback(map);
            }
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

    void removeIfAllChecked(String callID) {
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

    public void notifyUIKitInvitationReceived(ZegoUIKitUser inviteUser, int type, String data) {
        Map<String, Object> map = new HashMap<>();
        map.put("inviter", inviteUser);
        map.put("type", type);
        map.put("data", data);
        pluginEventListener.onPluginEvent("onCallInvitationReceived", map);
    }

    public void onCallInvitationCancelled(ZIM zim, ZIMCallInvitationCancelledInfo info, String callID) {
        InvitationData invitationData = removeInvitationData(callID);
        if (invitationData != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("inviter", invitationData.inviter);
            map.put("data", info.extendedData);
            pluginEventListener.onPluginEvent("onCallInvitationCancelled", map);
        }
    }

    public void onCallInvitationAccepted(ZIM zim, ZIMCallInvitationAcceptedInfo info, String callID) {
        InvitationUser invitationUser = getInvitee(callID, info.invitee);
        if (invitationUser != null) {
            invitationUser.state = InvitationState.ACCEPT;
            removeInvitationData(callID);
            Map<String, Object> map = new HashMap<>();
            map.put("invitee", invitationUser.user);
            map.put("data", info.extendedData);
            pluginEventListener.onPluginEvent("onCallInvitationAccepted", map);
        }
    }

    public void onCallInvitationRejected(ZIM zim, ZIMCallInvitationRejectedInfo info, String callID) {
        InvitationUser invitationUser = getInvitee(callID, info.invitee);
        if (invitationUser != null) {
            invitationUser.state = InvitationState.REFUSE;
            removeIfAllChecked(callID);
            Map<String, Object> map = new HashMap<>();
            map.put("invitee", invitationUser.user);
            map.put("data", info.extendedData);
            pluginEventListener.onPluginEvent("onCallInvitationRejected", map);
        }
    }

    public void onCallInvitationTimeout(ZIM zim, String callID) {
        InvitationData invitationData = removeInvitationData(callID);
        if (invitationData != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("inviter", invitationData.inviter);
            map.put("data", "");
            pluginEventListener.onPluginEvent("onCallInvitationTimeout", map);
        }
    }

    public void onCallInviteesAnsweredTimeout(ZIM zim, ArrayList<String> invitees, String callID) {
        InvitationData invitationData = getInvitationByCallID(callID);
        if (invitationData == null) {
            return;
        }
        List<InvitationUser> timeoutUsers = GenericUtils.filter(invitationData.invitees,
            uiKitUser -> invitees.contains(uiKitUser.getUserID()));
        for (InvitationUser timeoutUser : timeoutUsers) {
            timeoutUser.state = InvitationState.TIMEOUT;
        }
        removeIfAllChecked(callID);
        List<ZegoUIKitUser> timeoutInvitees = GenericUtils.map(timeoutUsers, invitationUser -> invitationUser.user);
        Map<String, Object> map = new HashMap<>();
        map.put("invitees", timeoutInvitees);
        map.put("data", "");
        pluginEventListener.onPluginEvent("onCallInviteesAnsweredTimeout", map);
    }
}
