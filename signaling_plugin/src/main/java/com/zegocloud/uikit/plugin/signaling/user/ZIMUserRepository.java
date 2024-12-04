package com.zegocloud.uikit.plugin.signaling.user;

import com.zegocloud.uikit.plugin.adapter.plugins.signaling.RenewTokenCallback;
import com.zegocloud.uikit.plugin.signaling.group.ZIMGroupRepository;
import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMLoggedInCallback;
import im.zego.zim.callback.ZIMTokenRenewedCallback;
import im.zego.zim.callback.ZIMUserAvatarUrlUpdatedCallback;
import im.zego.zim.callback.ZIMUsersInfoQueriedCallback;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMErrorUserInfo;
import im.zego.zim.entity.ZIMGroupMemberInfo;
import im.zego.zim.entity.ZIMUserFullInfo;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.entity.ZIMUserRule;
import im.zego.zim.entity.ZIMUsersInfoQueryConfig;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMErrorCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.json.JSONObject;

public class ZIMUserRepository {

    private ZIMUserInfo zimUserInfo;
    private boolean isLoginIng;
    private ZIMConnectionState connectionState;
    private ZIMConnectionEvent connectionEvent;
    private Map<String, ZIMUserFullInfo> userFullInfoMap;
    private ZIMGroupRepository groupRepository;

    public ZIMUserRepository(ZIMGroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void login(ZIMUserInfo userInfo, String token, ZIMLoggedInCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onLoggedIn(errorInfo);
            }
            return;
        }
        if (!isLoginIng) {
            isLoginIng = true;
            if (token == null) {
                token = "";
            }
            zimUserInfo = userInfo;
            ZIM.getInstance().login(userInfo, token, new ZIMLoggedInCallback() {
                @Override
                public void onLoggedIn(ZIMError errorInfo) {
                    isLoginIng = false;
                    if (errorInfo.code != ZIMErrorCode.SUCCESS) {
                        clearLoginData();
                    }
                    if (callback != null) {
                        callback.onLoggedIn(errorInfo);
                    }
                }
            });
        }
    }

    public void clearLoginData() {
        zimUserInfo = null;
        isLoginIng = false;
        connectionState = ZIMConnectionState.DISCONNECTED;
        connectionEvent = ZIMConnectionEvent.UNKNOWN;
        if (userFullInfoMap != null) {
            userFullInfoMap.clear();
        }
    }

    public void logout() {
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().logout();
        clearLoginData();
    }

    public void renewToken(String token, RenewTokenCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onResult(errorInfo.code.value(), errorInfo.message);
            }
            return;
        }
        ZIM.getInstance().renewToken(token, new ZIMTokenRenewedCallback() {

            public void onTokenRenewed(String token, ZIMError errorInfo) {
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message);
                }
            }
        });
    }

    public ZIMUserInfo getUserInfo() {
        return zimUserInfo;
    }

    public boolean hasLoggedIn() {
        return zimUserInfo == null;
    }

    public void onConnectionStateChanged(ZIM zim, ZIMConnectionState state, ZIMConnectionEvent event,
        JSONObject extendedData) {
        this.connectionState = state;
        this.connectionEvent = event;
    }

    public ZIMConnectionEvent getConnectionEvent() {
        return connectionEvent;
    }

    public ZIMConnectionState getConnectionState() {
        return connectionState;
    }

    public ZIMUserFullInfo getMemoryUserInfo(String userID) {
        // is self
        if (zimUserInfo != null && Objects.equals(zimUserInfo.userID, userID)) {
            ZIMUserFullInfo zimUserFullInfo = new ZIMUserFullInfo();
            zimUserFullInfo.baseInfo = zimUserInfo;
            return zimUserFullInfo;
        }
        // no  user cache
        if (userFullInfoMap == null || userFullInfoMap.isEmpty()) {
            // group user cache
            ZIMGroupMemberInfo groupMemberInfo = groupRepository.getGroupMemberInfo(userID);
            if (groupMemberInfo != null) {
                ZIMUserFullInfo userFullInfo = new ZIMUserFullInfo();
                userFullInfo.baseInfo = groupMemberInfo;
                return userFullInfo;
            }
            return null;
        } else {
            ZIMUserFullInfo zimUserFullInfo = userFullInfoMap.get(userID);
            if (zimUserFullInfo != null) {
                return zimUserFullInfo;
            }
            return null;
        }
    }

    public void queryUserInfo(List<String> userIDList, ZIMUsersInfoQueryConfig config,
        ZIMUsersInfoQueriedCallback callback) {

        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onUsersInfoQueried(null, null, errorInfo);
            }
            return;
        }

        if (config != null && config.isQueryFromServer) {
            ZIM.getInstance().queryUsersInfo(userIDList, config, new ZIMUsersInfoQueriedCallback() {
                @Override
                public void onUsersInfoQueried(ArrayList<ZIMUserFullInfo> userList,
                    ArrayList<ZIMErrorUserInfo> errorUserList, ZIMError errorInfo) {
                    if (userFullInfoMap == null) {
                        userFullInfoMap = new HashMap<>();
                    }
                    for (ZIMUserFullInfo zimUserFullInfo : userList) {
                        userFullInfoMap.put(zimUserFullInfo.baseInfo.userID, zimUserFullInfo);
                    }

                    ArrayList<ZIMUserFullInfo> result = new ArrayList<>();
                    for (String userID : userIDList) {
                        ZIMUserFullInfo zimUserFullInfo = userFullInfoMap.get(userID);
                        if (zimUserFullInfo != null) {
                            result.add(zimUserFullInfo);
                        }
                    }
                    if (callback != null) {
                        callback.onUsersInfoQueried(result, errorUserList, errorInfo);
                    }
                }
            });
        } else {
            List<String> needQueryUserIDList = new ArrayList<>();
            ArrayList<ZIMUserFullInfo> resultUserList = new ArrayList<>();

            for (String userID : userIDList) {
                ZIMUserFullInfo memoryUserInfo = getMemoryUserInfo(userID);
                if (memoryUserInfo == null) {
                    needQueryUserIDList.add(userID);
                } else {
                    resultUserList.add(memoryUserInfo);
                }
            }

            if (needQueryUserIDList.isEmpty()) {
                ZIMError zimError = new ZIMError();
                zimError.code = ZIMErrorCode.SUCCESS;
                zimError.message = ZIMErrorCode.SUCCESS.toString();
                if (callback != null) {
                    callback.onUsersInfoQueried(resultUserList, new ArrayList<>(), zimError);
                }
            } else {
                ZIM.getInstance().queryUsersInfo(needQueryUserIDList, config, new ZIMUsersInfoQueriedCallback() {
                    @Override
                    public void onUsersInfoQueried(ArrayList<ZIMUserFullInfo> userList,
                        ArrayList<ZIMErrorUserInfo> errorUserList, ZIMError errorInfo) {
                        if (userFullInfoMap == null) {
                            userFullInfoMap = new HashMap<>();
                        }
                        for (ZIMUserFullInfo zimUserFullInfo : userList) {
                            userFullInfoMap.put(zimUserFullInfo.baseInfo.userID, zimUserFullInfo);
                        }

                        ArrayList<ZIMUserFullInfo> result = new ArrayList<>();
                        for (String userID : userIDList) {
                            ZIMUserFullInfo zimUserFullInfo = userFullInfoMap.get(userID);
                            if (zimUserFullInfo != null) {
                                result.add(zimUserFullInfo);
                            }
                        }
                        if (callback != null) {
                            callback.onUsersInfoQueried(result, errorUserList, errorInfo);
                        }
                    }
                });
            }
        }

    }

    /**
     * 多端登录场景时，用户在 A 设备修改自己的信息后，其他在线的多端设备会收到此回调。对于离线设备，用户上线后，需要调用 [queryUsersInfo] 接口，主动查询用户信息。
     *
     * @param zim
     * @param info
     */
    public void onUserInfoUpdated(ZIM zim, ZIMUserFullInfo info) {

    }

    public void onUserRuleUpdated(ZIM zim, ZIMUserRule rule) {

    }

    public void updateUserAvatarUrl(String userAvatarUrl, ZIMUserAvatarUrlUpdatedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onUserAvatarUrlUpdated(userAvatarUrl, errorInfo);
            }
            return;
        }
        ZIM.getInstance().updateUserAvatarUrl(userAvatarUrl, new ZIMUserAvatarUrlUpdatedCallback() {
            @Override
            public void onUserAvatarUrlUpdated(String userAvatarUrl, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    zimUserInfo.userAvatarUrl = userAvatarUrl;
                }
                if (callback != null) {
                    callback.onUserAvatarUrlUpdated(userAvatarUrl, errorInfo);
                }
            }
        });
    }

    public void onTokenWillExpire(ZIM zim, int second) {

    }
}
