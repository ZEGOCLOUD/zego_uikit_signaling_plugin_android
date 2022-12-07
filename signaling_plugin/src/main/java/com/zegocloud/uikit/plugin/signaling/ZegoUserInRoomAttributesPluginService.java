package com.zegocloud.uikit.plugin.signaling;

import com.zegocloud.uikit.plugin.common.PluginCallbackListener;
import com.zegocloud.uikit.plugin.common.PluginEventListener;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;
import com.zegocloud.uikit.service.defines.ZegoUserInRoomAttributesInfo;
import com.zegocloud.uikit.service.internal.UIKitCore;
import com.zegocloud.uikit.utils.GenericUtils;
import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMRoomEnteredCallback;
import im.zego.zim.callback.ZIMRoomLeftCallback;
import im.zego.zim.callback.ZIMRoomMemberAttributesListQueriedCallback;
import im.zego.zim.callback.ZIMRoomMembersAttributesOperatedCallback;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMRoomAdvancedConfig;
import im.zego.zim.entity.ZIMRoomFullInfo;
import im.zego.zim.entity.ZIMRoomInfo;
import im.zego.zim.entity.ZIMRoomMemberAttributesInfo;
import im.zego.zim.entity.ZIMRoomMemberAttributesOperatedInfo;
import im.zego.zim.entity.ZIMRoomMemberAttributesQueryConfig;
import im.zego.zim.entity.ZIMRoomMemberAttributesSetConfig;
import im.zego.zim.entity.ZIMRoomMemberAttributesUpdateInfo;
import im.zego.zim.entity.ZIMRoomOperatedInfo;
import im.zego.zim.entity.ZIMUserInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Member Room Properties
 */
public class ZegoUserInRoomAttributesPluginService {

    private PluginEventListener pluginEventListener;

    public void setPluginEventListener(PluginEventListener pluginEventListener) {
        this.pluginEventListener = pluginEventListener;
    }

    /**
     * Enter the room
     *
     * @param roomID
     * @param listener
     */
    public void joinRoom(String roomID, PluginCallbackListener listener) {
        ZIMRoomInfo roomInfo = new ZIMRoomInfo();
        roomInfo.roomID = roomID;
        ZIM.getInstance().enterRoom(roomInfo, new ZIMRoomAdvancedConfig(), new ZIMRoomEnteredCallback() {
            @Override
            public void onRoomEntered(ZIMRoomFullInfo roomInfo, ZIMError errorInfo) {
                if (listener != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", errorInfo.code.value());
                    map.put("message", errorInfo.message);
                    listener.callback(map);
                }
            }
        });
    }

    public void leaveRoom(PluginCallbackListener listener) {
        String roomID = UIKitCore.getInstance().getRoom().roomID;
        ZIM.getInstance().leaveRoom(roomID, new ZIMRoomLeftCallback() {
            @Override
            public void onRoomLeft(String roomID, ZIMError errorInfo) {
                if (listener != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", errorInfo.code.value());
                    map.put("message", errorInfo.message);
                    listener.callback(map);
                }
            }
        });
    }

    /**
     * setRoomMembersAttributes
     *
     * @param key
     * @param value
     * @param userIDs
     * @param listener
     */
    public void setUsersInRoomAttributes(String key, String value, List<String> userIDs, PluginCallbackListener listener) {

        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(key, value);

        String roomID = UIKitCore.getInstance().getRoom().roomID;
        ZIM.getInstance().setRoomMembersAttributes(attributes, userIDs, roomID, new ZIMRoomMemberAttributesSetConfig(), new ZIMRoomMembersAttributesOperatedCallback() {
            @Override
            public void onRoomMembersAttributesOperated(String roomID, ArrayList<ZIMRoomMemberAttributesOperatedInfo> infos, ArrayList<String> errorUserList, ZIMError errorInfo) {
                if (listener != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", errorInfo.code.value());
                    map.put("message", errorInfo.message);
                    List<ZegoUserInRoomAttributesInfo> inRoomAttributesInfoList = new ArrayList<>();
                    for (ZIMRoomMemberAttributesOperatedInfo info : infos) {
                        ZegoUserInRoomAttributesInfo attributesInfo = new ZegoUserInRoomAttributesInfo(info.attributesInfo.userID, info.attributesInfo.attributes);
                        inRoomAttributesInfoList.add(attributesInfo);
                    }
                    map.put("infos", inRoomAttributesInfoList);
                    listener.callback(map);
                }
            }
        });
    }

    /**
     * Queries the properties of the members in the room. The query returns a list of all the properties set in the current room.
     *
     * @param nextFlag
     * @param count
     * @param listener
     */
    public void queryUsersInRoomAttributesList(String nextFlag, int count, PluginCallbackListener listener) {
        String roomID = UIKitCore.getInstance().getRoom().roomID;
        ZIMRoomMemberAttributesQueryConfig config = new ZIMRoomMemberAttributesQueryConfig();
        config.count = count;
        config.nextFlag = nextFlag;
        ZIM.getInstance().queryRoomMemberAttributesList(roomID, config, new ZIMRoomMemberAttributesListQueriedCallback() {
            @Override
            public void onRoomMemberAttributesListQueried(String roomID, ArrayList<ZIMRoomMemberAttributesInfo> infos, String nextFlag, ZIMError errorInfo) {
                if (listener != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", errorInfo.code.value());
                    map.put("message", errorInfo.message);
                    map.put("nextFlag", nextFlag);
                    List<ZegoUserInRoomAttributesInfo> inRoomAttributesInfoList = new ArrayList<>();
                    for (ZIMRoomMemberAttributesInfo info : infos) {
                        ZegoUserInRoomAttributesInfo attributesInfo = new ZegoUserInRoomAttributesInfo(info.userID, info.attributes);
                        inRoomAttributesInfoList.add(attributesInfo);
                    }
                    map.put("infos", inRoomAttributesInfoList);
                    listener.callback(map);
                }
            }
        });
    }

    /**
     * Notification of user attributes in the room
     *
     * @param infos
     * @param operatedInfo
     * @param roomID
     */
    public void notifyOnRoomMemberAttributesUpdated(ArrayList<ZIMRoomMemberAttributesUpdateInfo> infos, ZIMRoomOperatedInfo operatedInfo, String roomID) {
        Map<String, Object> map = new HashMap<>();
        List<ZegoUserInRoomAttributesInfo> inRoomAttributesInfoList = new ArrayList<>();
        for (ZIMRoomMemberAttributesUpdateInfo info : infos) {
            ZegoUserInRoomAttributesInfo attributesInfo = new ZegoUserInRoomAttributesInfo(info.attributesInfo.userID, info.attributesInfo.attributes);
            inRoomAttributesInfoList.add(attributesInfo);
        }
        map.put("infos", inRoomAttributesInfoList);
        map.put("editor", operatedInfo.userID);
        pluginEventListener.onPluginEvent("onUsersInRoomAttributesUpdated", map);
    }

    public void onRoomMemberLeft(ZIM zim, ArrayList<ZIMUserInfo> memberList, String roomID) {
        Map<String, Object> map = new HashMap<>();
        List<ZegoUIKitUser> userList = GenericUtils.map(memberList,
            zimUserInfo -> new ZegoUIKitUser(zimUserInfo.userID, zimUserInfo.userName));
        map.put("userList", userList);
        map.put("roomID", roomID);
        pluginEventListener.onPluginEvent("onRoomMemberLeft", map);
    }
}
