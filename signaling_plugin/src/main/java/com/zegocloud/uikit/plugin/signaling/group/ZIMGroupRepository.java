package com.zegocloud.uikit.plugin.signaling.group;

import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMGroupMemberInfoQueriedCallback;
import im.zego.zim.callback.ZIMGroupMemberListQueriedCallback;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMGroupMemberInfo;
import im.zego.zim.entity.ZIMGroupMemberQueryConfig;
import im.zego.zim.enums.ZIMErrorCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ZIMGroupRepository {

    // key :groupID ,value: group users
    private Map<String, List<ZIMGroupMemberInfo>> groupUserMap;
    // key : userID , value : any group users
    private Map<String, ZIMGroupMemberInfo> allGroupUserMap;

    public ZIMGroupRepository() {
        groupUserMap = new HashMap<>();
        allGroupUserMap = new HashMap<>();
    }

    public void queryGroupMemberInfo(String userID, String groupID, ZIMGroupMemberInfoQueriedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onGroupMemberInfoQueried(groupID, null, errorInfo);
            }
            return;
        }
        ZIM.getInstance().queryGroupMemberInfo(userID, groupID, new ZIMGroupMemberInfoQueriedCallback() {
            @Override
            public void onGroupMemberInfoQueried(String groupID, ZIMGroupMemberInfo userInfo, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    allGroupUserMap.put(userInfo.userID, userInfo);

                    List<ZIMGroupMemberInfo> zimGroupMemberInfos;
                    if (groupUserMap.containsKey(groupID)) {
                        zimGroupMemberInfos = groupUserMap.get(groupID);
                    } else {
                        zimGroupMemberInfos = new ArrayList<>();
                        groupUserMap.put(groupID, zimGroupMemberInfos);
                    }
                    Optional<ZIMGroupMemberInfo> any = zimGroupMemberInfos.stream()
                        .filter(memberInfo -> Objects.equals(userInfo.userID, memberInfo.userID)).findAny();
                    any.ifPresent(zimGroupMemberInfos::remove);
                    zimGroupMemberInfos.add(userInfo);
                }
                if (callback != null) {
                    callback.onGroupMemberInfoQueried(groupID, userInfo, errorInfo);
                }
            }
        });
    }

    public void queryGroupMemberList(String groupID, ZIMGroupMemberQueryConfig config,
        ZIMGroupMemberListQueriedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onGroupMemberListQueried(groupID, null, 0, errorInfo);
            }
            return;
        }
        ZIM.getInstance().queryGroupMemberList(groupID, config, new ZIMGroupMemberListQueriedCallback() {
            @Override
            public void onGroupMemberListQueried(String groupID, ArrayList<ZIMGroupMemberInfo> userList, int nextFlag,
                ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    userList.forEach(memberInfo -> {
                        allGroupUserMap.put(memberInfo.userID, memberInfo);
                    });
                    List<ZIMGroupMemberInfo> zimGroupMemberInfos;
                    if (groupUserMap.containsKey(groupID)) {
                        zimGroupMemberInfos = groupUserMap.get(groupID);
                    } else {
                        zimGroupMemberInfos = new ArrayList<>();
                        groupUserMap.put(groupID, zimGroupMemberInfos);
                    }

                    for (ZIMGroupMemberInfo groupMemberInfo : userList) {
                        Optional<ZIMGroupMemberInfo> any = zimGroupMemberInfos.stream()
                            .filter(memberInfo -> Objects.equals(groupMemberInfo.userID, memberInfo.userID)).findAny();
                        any.ifPresent(zimGroupMemberInfos::remove);
                        zimGroupMemberInfos.add(groupMemberInfo);
                    }
                }
                callback.onGroupMemberListQueried(groupID, userList, nextFlag, errorInfo);
            }
        });
    }

    public ZIMGroupMemberInfo getGroupMemberInfo(String userID) {
        return allGroupUserMap.get(userID);
    }
}
