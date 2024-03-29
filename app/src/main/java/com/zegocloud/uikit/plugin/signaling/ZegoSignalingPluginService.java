package com.zegocloud.uikit.plugin.signaling;

import android.app.Application;
import android.util.Log;

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
import com.zegocloud.uikit.plugin.adapter.plugins.ZegoSignalingInRoomTextMessage;
import com.zegocloud.uikit.plugin.adapter.plugins.ZegoSignalingPluginConnectionState;
import com.zegocloud.uikit.plugin.adapter.plugins.ZegoSignalingPluginEventHandler;
import com.zegocloud.uikit.plugin.adapter.plugins.ZegoSignalingPluginNotificationConfig;
import com.zegocloud.uikit.plugin.adapter.utils.GenericUtils;
import com.zegocloud.uikit.plugin.adapter.utils.NotifyList;

import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMCallAcceptanceSentCallback;
import im.zego.zim.callback.ZIMCallCancelSentCallback;
import im.zego.zim.callback.ZIMCallInvitationSentCallback;
import im.zego.zim.callback.ZIMCallRejectionSentCallback;
import im.zego.zim.callback.ZIMEventHandler;
import im.zego.zim.callback.ZIMLoggedInCallback;
import im.zego.zim.callback.ZIMMessageSentCallback;
import im.zego.zim.callback.ZIMRoomAttributesBatchOperatedCallback;
import im.zego.zim.callback.ZIMRoomAttributesOperatedCallback;
import im.zego.zim.callback.ZIMRoomAttributesQueriedCallback;
import im.zego.zim.callback.ZIMRoomEnteredCallback;
import im.zego.zim.callback.ZIMRoomLeftCallback;
import im.zego.zim.callback.ZIMRoomMemberAttributesListQueriedCallback;
import im.zego.zim.callback.ZIMRoomMembersAttributesOperatedCallback;
import im.zego.zim.callback.ZIMTokenRenewedCallback;
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
import im.zego.zim.entity.ZIMConversationChangeInfo;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMGroupAttributesUpdateInfo;
import im.zego.zim.entity.ZIMGroupFullInfo;
import im.zego.zim.entity.ZIMGroupMemberInfo;
import im.zego.zim.entity.ZIMGroupOperatedInfo;
import im.zego.zim.entity.ZIMMessage;
import im.zego.zim.entity.ZIMMessageSendConfig;
import im.zego.zim.entity.ZIMPushConfig;
import im.zego.zim.entity.ZIMRoomAdvancedConfig;
import im.zego.zim.entity.ZIMRoomAttributesBatchOperationConfig;
import im.zego.zim.entity.ZIMRoomAttributesDeleteConfig;
import im.zego.zim.entity.ZIMRoomAttributesSetConfig;
import im.zego.zim.entity.ZIMRoomAttributesUpdateInfo;
import im.zego.zim.entity.ZIMRoomFullInfo;
import im.zego.zim.entity.ZIMRoomInfo;
import im.zego.zim.entity.ZIMRoomMemberAttributesInfo;
import im.zego.zim.entity.ZIMRoomMemberAttributesOperatedInfo;
import im.zego.zim.entity.ZIMRoomMemberAttributesQueryConfig;
import im.zego.zim.entity.ZIMRoomMemberAttributesSetConfig;
import im.zego.zim.entity.ZIMRoomMemberAttributesUpdateInfo;
import im.zego.zim.entity.ZIMRoomOperatedInfo;
import im.zego.zim.entity.ZIMTextMessage;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMConversationType;
import im.zego.zim.enums.ZIMErrorCode;
import im.zego.zim.enums.ZIMGroupEvent;
import im.zego.zim.enums.ZIMGroupMemberEvent;
import im.zego.zim.enums.ZIMGroupMemberState;
import im.zego.zim.enums.ZIMGroupState;
import im.zego.zim.enums.ZIMRoomAttributesUpdateAction;
import im.zego.zim.enums.ZIMRoomEvent;
import im.zego.zim.enums.ZIMRoomState;
import im.zego.zpns.ZPNsManager;
import im.zego.zpns.util.ZPNsConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;

public class ZegoSignalingPluginService {

    private Application application;
    private boolean notifyWhenAppRunningInBackgroundOrQuit;
    private NotifyList<ZegoSignalingPluginEventHandler> signalingPluginEventHandlerNotifyList = new NotifyList<>();
    private NotifyList<ZIMEventHandler> zimEventHandlerNotifyList = new NotifyList<>();
    private final AtomicBoolean isZIMInited = new AtomicBoolean();

    public void init(Application application, Long appID, String appSign) {
        this.application = application;
        boolean result = isZIMInited.compareAndSet(false, true);
        if (!result) {
            return;
        }
        ZIMAppConfig zimAppConfig = new ZIMAppConfig();
        zimAppConfig.appID = appID;
        zimAppConfig.appSign = appSign;
        ZIM.create(zimAppConfig, application);
        ZIM.getInstance().setEventHandler(new ZIMEventHandler() {
            @Override
            public void onRoomMemberJoined(ZIM zim, ArrayList<ZIMUserInfo> memberList, String roomID) {
                super.onRoomMemberJoined(zim, memberList, roomID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onRoomMemberJoined(zim, memberList, roomID);
                });

                List<String> userIDList = GenericUtils.map(memberList, zimUserInfo -> zimUserInfo.userID);
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onRoomMemberJoined(userIDList, roomID);
                });
            }

            public void onRoomMemberLeft(ZIM zim, ArrayList<ZIMUserInfo> memberList, String roomID) {
                super.onRoomMemberLeft(zim, memberList, roomID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onRoomMemberLeft(zim, memberList, roomID);
                });

                List<String> userIDList = GenericUtils.map(memberList, zimUserInfo -> zimUserInfo.userID);
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onRoomMemberLeft(userIDList, roomID);
                });
            }


            public void onConnectionStateChanged(ZIM zim, ZIMConnectionState state, ZIMConnectionEvent event,
                                                 JSONObject extendedData) {
                super.onConnectionStateChanged(zim, state, event, extendedData);
                //                zimConnectionState = state;
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onConnectionStateChanged(zim, state, event, extendedData);
                });
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    ZegoSignalingPluginConnectionState connectionState = ZegoSignalingPluginConnectionState.getConnectionState(
                            state.value());
                    handler.onConnectionStateChanged(connectionState);
                });
            }


            public void onError(ZIM zim, ZIMError errorInfo) {
                super.onError(zim, errorInfo);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onError(zim, errorInfo);
                });
            }

            /**
             * invitee received a call from inviter.
             * @param zim
             * @param info
             * @param callID
             */

            public void onCallInvitationReceived(ZIM zim, ZIMCallInvitationReceivedInfo info, String callID) {
                super.onCallInvitationReceived(zim, info, callID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationReceived(zim, info, callID);
                });
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationReceived(callID, info.inviter, info.extendedData);
                });
            }

            /**
             * invitee received when inviter cancelled call.
             * @param zim
             * @param info
             * @param callID
             */

            public void onCallInvitationCancelled(ZIM zim, ZIMCallInvitationCancelledInfo info, String callID) {
                super.onCallInvitationCancelled(zim, info, callID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationCancelled(zim, info, callID);
                });
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationCancelled(callID, info.inviter, info.extendedData);
                });
            }

            /**
             * inviter received invitees accept.
             * @param zim
             * @param info
             * @param callID
             */

            public void onCallInvitationAccepted(ZIM zim, ZIMCallInvitationAcceptedInfo info, String callID) {
                super.onCallInvitationAccepted(zim, info, callID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationAccepted(zim, info, callID);
                });
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationAccepted(callID, info.invitee, info.extendedData);
                });
            }

            /**
             * inviter received invitees reject.
             * @param zim
             * @param info
             * @param callID
             */

            public void onCallInvitationRejected(ZIM zim, ZIMCallInvitationRejectedInfo info, String callID) {
                super.onCallInvitationRejected(zim, info, callID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationRejected(zim, info, callID);
                });
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationRejected(callID, info.invitee, info.extendedData);
                });
            }

            /**
             * invitee doesn't respond to call,missed
             * @param zim
             * @param callID
             */

            public void onCallInvitationTimeout(ZIM zim, String callID) {
                super.onCallInvitationTimeout(zim, callID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationTimeout(zim, callID);
                });
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationTimeout(callID);
                });
            }

            /**
             * inviter received invitees no respond,they missed.
             * @param zim
             * @param invitees
             * @param callID
             */

            public void onCallInviteesAnsweredTimeout(ZIM zim, ArrayList<String> invitees, String callID) {
                super.onCallInviteesAnsweredTimeout(zim, invitees, callID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInviteesAnsweredTimeout(zim, invitees, callID);
                });
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInviteesAnsweredTimeout(callID, invitees);
                });
            }

            @Override
            public void onTokenWillExpire(ZIM zim, int second) {
                super.onTokenWillExpire(zim, second);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onTokenWillExpire(zim, second);
                });
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onTokenWillExpire(second);
                });
            }

            public void onRoomMemberAttributesUpdated(ZIM zim, ArrayList<ZIMRoomMemberAttributesUpdateInfo> infos,
                                                      ZIMRoomOperatedInfo operatedInfo, String roomID) {
                super.onRoomMemberAttributesUpdated(zim, infos, operatedInfo, roomID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onRoomMemberAttributesUpdated(zim, infos, operatedInfo, roomID);
                });
                Map<String, HashMap<String, String>> map = new HashMap<>();
                for (ZIMRoomMemberAttributesUpdateInfo info : infos) {
                    map.put(info.attributesInfo.userID, info.attributesInfo.attributes);
                }
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onUsersInRoomAttributesUpdated(map, operatedInfo.userID, roomID);
                });
            }


            public void onRoomAttributesUpdated(ZIM zim, ZIMRoomAttributesUpdateInfo info, String roomID) {
                super.onRoomAttributesUpdated(zim, info, roomID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onRoomAttributesUpdated(zim, info, roomID);
                });
                HashMap<String, String> roomAttributes = info.roomAttributes;
                List<Map<String, String>> setProperties;
                if (info.action == ZIMRoomAttributesUpdateAction.SET) {
                    setProperties = Collections.singletonList(roomAttributes);
                } else {
                    setProperties = new ArrayList<>();
                }
                List<Map<String, String>> deleteProperties;
                if (info.action == ZIMRoomAttributesUpdateAction.DELETE) {
                    deleteProperties = Collections.singletonList(roomAttributes);
                } else {
                    deleteProperties = new ArrayList<>();
                }
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onRoomPropertiesUpdated(setProperties, deleteProperties, roomID);
                });
            }

            public void onRoomAttributesBatchUpdated(ZIM zim, ArrayList<ZIMRoomAttributesUpdateInfo> infos,
                                                     String roomID) {
                super.onRoomAttributesBatchUpdated(zim, infos, roomID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onRoomAttributesBatchUpdated(zim, infos, roomID);
                });

                List<Map<String, String>> setProperties = new ArrayList<>();
                List<Map<String, String>> deleteProperties = new ArrayList<>();
                for (ZIMRoomAttributesUpdateInfo info : infos) {
                    if (info.action == ZIMRoomAttributesUpdateAction.SET) {
                        setProperties.add(info.roomAttributes);
                    } else if (info.action == ZIMRoomAttributesUpdateAction.DELETE) {
                        deleteProperties.add(info.roomAttributes);
                    }
                }
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onRoomPropertiesUpdated(setProperties, deleteProperties, roomID);
                });
            }

            @Override
            public void onReceiveRoomMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromRoomID) {
                super.onReceiveRoomMessage(zim, messageList, fromRoomID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onReceiveRoomMessage(zim, messageList, fromRoomID);
                });
                Collections.sort(messageList, new Comparator<ZIMMessage>() {
                    @Override
                    public int compare(ZIMMessage o1, ZIMMessage o2) {
                        return (int) (o1.getTimestamp() - o2.getTimestamp());
                    }
                });

                List<ZegoSignalingInRoomTextMessage> signalMessageList = GenericUtils.map(messageList, zimMessage -> {
                    if (zimMessage instanceof ZIMTextMessage) {
                        ZIMTextMessage textMessage = (ZIMTextMessage) zimMessage;
                        ZegoSignalingInRoomTextMessage message = new ZegoSignalingInRoomTextMessage();
                        message.messageID = textMessage.getMessageID();
                        message.timestamp = textMessage.getMessageID();
                        message.orderKey = textMessage.getOrderKey();
                        message.text = textMessage.message;
                        message.senderUserID = textMessage.getSenderUserID();
                        return message;
                    } else {
                        return null;
                    }
                });
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onInRoomTextMessageReceived(signalMessageList, fromRoomID);
                });
            }

            @Override
            public void onRoomStateChanged(ZIM zim, ZIMRoomState state, ZIMRoomEvent event, JSONObject extendedData,
                                           String roomID) {
                super.onRoomStateChanged(zim, state, event, extendedData, roomID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onRoomStateChanged(zim, state, event, extendedData, roomID);
                });
            }

            @Override
            public void onConversationChanged(ZIM zim,
                                              ArrayList<ZIMConversationChangeInfo> conversationChangeInfoList) {
                super.onConversationChanged(zim, conversationChangeInfoList);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onConversationChanged(zim, conversationChangeInfoList);
                });
            }

            @Override
            public void onConversationTotalUnreadMessageCountUpdated(ZIM zim, int totalUnreadMessageCount) {
                super.onConversationTotalUnreadMessageCountUpdated(zim, totalUnreadMessageCount);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onConversationTotalUnreadMessageCountUpdated(zim, totalUnreadMessageCount);
                });
            }

            @Override
            public void onGroupAttributesUpdated(ZIM zim, ArrayList<ZIMGroupAttributesUpdateInfo> infos,
                                                 ZIMGroupOperatedInfo operatedInfo, String groupID) {
                super.onGroupAttributesUpdated(zim, infos, operatedInfo, groupID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onGroupAttributesUpdated(zim, infos, operatedInfo, groupID);
                });
            }

            @Override
            public void onGroupAvatarUrlUpdated(ZIM zim, String groupAvatarUrl, ZIMGroupOperatedInfo operatedInfo,
                                                String groupID) {
                super.onGroupAvatarUrlUpdated(zim, groupAvatarUrl, operatedInfo, groupID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onGroupAvatarUrlUpdated(zim, groupAvatarUrl, operatedInfo, groupID);
                });
            }

            @Override
            public void onGroupMemberInfoUpdated(ZIM zim, ArrayList<ZIMGroupMemberInfo> userList,
                                                 ZIMGroupOperatedInfo operatedInfo, String groupID) {
                super.onGroupMemberInfoUpdated(zim, userList, operatedInfo, groupID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onGroupMemberInfoUpdated(zim, userList, operatedInfo, groupID);
                });
            }

            @Override
            public void onGroupMemberStateChanged(ZIM zim, ZIMGroupMemberState state, ZIMGroupMemberEvent event,
                                                  ArrayList<ZIMGroupMemberInfo> userList, ZIMGroupOperatedInfo operatedInfo, String groupID) {
                super.onGroupMemberStateChanged(zim, state, event, userList, operatedInfo, groupID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onGroupMemberStateChanged(zim, state, event, userList, operatedInfo, groupID);
                });
            }

            @Override
            public void onGroupNameUpdated(ZIM zim, String groupName, ZIMGroupOperatedInfo operatedInfo,
                                           String groupID) {
                super.onGroupNameUpdated(zim, groupName, operatedInfo, groupID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onGroupNameUpdated(zim, groupName, operatedInfo, groupID);
                });
            }

            @Override
            public void onGroupNoticeUpdated(ZIM zim, String groupNotice, ZIMGroupOperatedInfo operatedInfo,
                                             String groupID) {
                super.onGroupNoticeUpdated(zim, groupNotice, operatedInfo, groupID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onGroupNoticeUpdated(zim, groupNotice, operatedInfo, groupID);
                });
            }

            @Override
            public void onGroupStateChanged(ZIM zim, ZIMGroupState state, ZIMGroupEvent event,
                                            ZIMGroupOperatedInfo operatedInfo, ZIMGroupFullInfo groupInfo) {
                super.onGroupStateChanged(zim, state, event, operatedInfo, groupInfo);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onGroupStateChanged(zim, state, event, operatedInfo, groupInfo);
                });
            }

            @Override
            public void onReceiveGroupMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromGroupID) {
                super.onReceiveGroupMessage(zim, messageList, fromGroupID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onReceiveGroupMessage(zim, messageList, fromGroupID);
                });
            }

            @Override
            public void onReceivePeerMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromUserID) {
                super.onReceivePeerMessage(zim, messageList, fromUserID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onReceivePeerMessage(zim, messageList, fromUserID);
                });
            }
        });
    }


    public void connectUser(String userID, String userName, ConnectUserCallback callback) {
        connectUser(userID, userName, null, callback);
    }


    public void connectUser(String userID, String userName, String token, ConnectUserCallback callback) {
        ZIMUserInfo zimUserInfo = new ZIMUserInfo();
        zimUserInfo.userID = userID;
        zimUserInfo.userName = userName;
        if (token == null) {
            token = "";
        }
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().login(zimUserInfo, token, new ZIMLoggedInCallback() {

            public void onLoggedIn(ZIMError errorInfo) {
                if (callback != null) {
                    int code = errorInfo.code == ZIMErrorCode.USER_HAS_ALREADY_LOGGED ? ZIMErrorCode.SUCCESS.value() : errorInfo.code.value();
                    callback.onResult(code, errorInfo.message);
                }
            }
        });
    }


    public void disconnectUser() {
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().logout();
    }


    public void renewToken(String token, RenewTokenCallback callback) {
        if (ZIM.getInstance() == null) {
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


    public void sendInvitation(List<String> invitees, int timeout, String data,
                               ZegoSignalingPluginNotificationConfig notificationConfig, InvitationCallback callback) {
        ZIMCallInviteConfig config = new ZIMCallInviteConfig();
        config.timeout = timeout;
        config.extendedData = data;
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIMPushConfig pushConfig = new ZIMPushConfig();
        if (notifyWhenAppRunningInBackgroundOrQuit && notificationConfig != null) {
            pushConfig.payload = data;
            pushConfig.title = notificationConfig.getTitle();
            pushConfig.content = notificationConfig.getMessage();
            pushConfig.resourcesID = notificationConfig.getResourceID();
            config.pushConfig = pushConfig;
        }
        ZIM.getInstance().callInvite(invitees, config, new ZIMCallInvitationSentCallback() {

            public void onCallInvitationSent(String callID, ZIMCallInvitationSentInfo info, ZIMError errorInfo) {
                if (callback != null) {
                    List<String> stringList = GenericUtils.map(info.errorInvitees,
                            zimCallUserInfo -> zimCallUserInfo.userID);
                    callback.onResult(errorInfo.code.value(), errorInfo.message, callID, stringList);
                }
            }
        });
    }


    public void cancelInvitation(List<String> invitees, String invitationID, String data,
                                 CancelInvitationCallback callback) {
        ZIMCallCancelConfig config = new ZIMCallCancelConfig();
        config.extendedData = data;
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().callCancel(invitees, invitationID, config, new ZIMCallCancelSentCallback() {

            public void onCallCancelSent(String callID, ArrayList<String> errorInvitees, ZIMError errorInfo) {
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message, errorInvitees);
                }
            }
        });
    }


    public void refuseInvitation(String invitationID, String data, ResponseInvitationCallback callback) {
        ZIMCallRejectConfig config = new ZIMCallRejectConfig();
        config.extendedData = data;
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().callReject(invitationID, config, new ZIMCallRejectionSentCallback() {

            public void onCallRejectionSent(String callID, ZIMError errorInfo) {
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message);
                }
            }
        });
    }


    public void acceptInvitation(String invitationID, String data, ResponseInvitationCallback callback) {
        ZIMCallAcceptConfig config = new ZIMCallAcceptConfig();
        config.extendedData = data;
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().callAccept(invitationID, config, new ZIMCallAcceptanceSentCallback() {

            public void onCallAcceptanceSent(String callID, ZIMError errorInfo) {
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message);
                }
            }
        });
    }


    public void joinRoom(String roomID, RoomCallback callback) {
        joinRoom(roomID, "", callback);
    }


    public void joinRoom(String roomID, String roomName, RoomCallback callback) {
        ZIMRoomInfo zimRoomInfo = new ZIMRoomInfo();
        zimRoomInfo.roomID = roomID;
        zimRoomInfo.roomName = roomName;
        ZIMRoomAdvancedConfig config = new ZIMRoomAdvancedConfig();
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().enterRoom(zimRoomInfo, config, new ZIMRoomEnteredCallback() {

            public void onRoomEntered(ZIMRoomFullInfo roomInfo, ZIMError errorInfo) {
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message);
                }
            }
        });
    }


    public void leaveRoom(String roomID, RoomCallback callback) {
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().leaveRoom(roomID, new ZIMRoomLeftCallback() {

            public void onRoomLeft(String roomID, ZIMError errorInfo) {
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message);
                }
            }
        });
    }


    public void setUsersInRoomAttributes(HashMap<String, String> attributes, List<String> userIDs, String roomID,
                                         SetUsersInRoomAttributesCallback callback) {
        ZIMRoomMemberAttributesSetConfig config = new ZIMRoomMemberAttributesSetConfig();
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().setRoomMembersAttributes(attributes, userIDs, roomID, config,
                new ZIMRoomMembersAttributesOperatedCallback() {

                    public void onRoomMembersAttributesOperated(String roomID,
                                                                ArrayList<ZIMRoomMemberAttributesOperatedInfo> infos, ArrayList<String> errorUserList,
                                                                ZIMError errorInfo) {
                        if (callback != null) {
                            Map<String, HashMap<String, String>> attributesMap = new HashMap<>();
                            Map<String, ArrayList<String>> errorKeysMap = new HashMap<>();
                            for (ZIMRoomMemberAttributesOperatedInfo info : infos) {
                                attributesMap.put(info.attributesInfo.userID, info.attributesInfo.attributes);
                                errorKeysMap.put(info.attributesInfo.userID, info.errorKeys);
                            }
                            callback.onResult(errorInfo.code.value(), errorInfo.message, errorUserList, attributesMap,
                                    errorKeysMap);
                        }
                    }
                });
    }


    public void queryUsersInRoomAttributes(String roomID, int count, String nextFlag,
                                           QueryUsersInRoomAttributesCallback callback) {
        ZIMRoomMemberAttributesQueryConfig config = new ZIMRoomMemberAttributesQueryConfig();
        config.count = count;
        config.nextFlag = nextFlag;
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance()
                .queryRoomMemberAttributesList(roomID, config, new ZIMRoomMemberAttributesListQueriedCallback() {

                    public void onRoomMemberAttributesListQueried(String roomID,
                                                                  ArrayList<ZIMRoomMemberAttributesInfo> infos, String nextFlag, ZIMError errorInfo) {
                        if (callback != null) {
                            Map<String, HashMap<String, String>> attributesMap = new HashMap<>();
                            for (ZIMRoomMemberAttributesInfo info : infos) {
                                attributesMap.put(info.userID, info.attributes);
                            }
                            callback.onResult(errorInfo.code.value(), errorInfo.message, nextFlag, attributesMap);
                        }
                    }
                });
    }


    public void updateRoomProperty(HashMap<String, String> attributes, String roomID, boolean isForce,
                                   boolean isDeleteAfterOwnerLeft, boolean isUpdateOwner, RoomPropertyOperationCallback callback) {
        ZIMRoomAttributesSetConfig config = new ZIMRoomAttributesSetConfig();
        config.isDeleteAfterOwnerLeft = isDeleteAfterOwnerLeft;
        config.isForce = isForce;
        config.isUpdateOwner = isUpdateOwner;
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().setRoomAttributes(attributes, roomID, config, new ZIMRoomAttributesOperatedCallback() {

            public void onRoomAttributesOperated(String roomID, ArrayList<String> errorKeys, ZIMError errorInfo) {
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message, errorKeys);
                }
            }
        });
    }


    public void deleteRoomProperties(List<String> keys, String roomID, boolean isForce,
                                     RoomPropertyOperationCallback callback) {
        ZIMRoomAttributesDeleteConfig config = new ZIMRoomAttributesDeleteConfig();
        config.isForce = isForce;
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().deleteRoomAttributes(keys, roomID, config, new ZIMRoomAttributesOperatedCallback() {

            public void onRoomAttributesOperated(String roomID, ArrayList<String> errorKeys, ZIMError errorInfo) {
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message, errorKeys);
                }
            }
        });
    }


    public void queryRoomProperties(String roomID, QueryRoomPropertyCallback callback) {
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().queryRoomAllAttributes(roomID, new ZIMRoomAttributesQueriedCallback() {

            public void onRoomAttributesQueried(String roomID, HashMap<String, String> roomAttributes,
                                                ZIMError errorInfo) {
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message, roomAttributes);
                }
            }
        });
    }


    public void beginRoomPropertiesBatchOperation(String roomID, boolean isDeleteAfterOwnerLeft, boolean isForce,
                                                  boolean isUpdateOwner) {
        ZIMRoomAttributesBatchOperationConfig config = new ZIMRoomAttributesBatchOperationConfig();
        config.isForce = isForce;
        config.isDeleteAfterOwnerLeft = isDeleteAfterOwnerLeft;
        config.isUpdateOwner = isUpdateOwner;
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().beginRoomAttributesBatchOperation(roomID, config);
    }


    public void endRoomPropertiesBatchOperation(String roomID, EndRoomBatchOperationCallback callback) {
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIM.getInstance().endRoomAttributesBatchOperation(roomID, new ZIMRoomAttributesBatchOperatedCallback() {

            public void onRoomAttributesBatchOperated(String roomID, ZIMError errorInfo) {
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message);
                }
            }
        });
    }


    public void sendRoomMessage(String text, String roomID, SendRoomMessageCallback callback) {
        if (ZIM.getInstance() == null) {
            return;
        }
        ZIMTextMessage textMessage = new ZIMTextMessage(text);
        ZIMMessageSendConfig config = new ZIMMessageSendConfig();
        ZIM.getInstance()
                .sendMessage(textMessage, roomID, ZIMConversationType.ROOM, config, new ZIMMessageSentCallback() {
                    @Override
                    public void onMessageAttached(ZIMMessage message) {

                    }

                    @Override
                    public void onMessageSent(ZIMMessage message, ZIMError errorInfo) {
                        if (callback != null) {
                            callback.onResult(errorInfo.code.value(), errorInfo.message);
                        }
                    }
                });
    }

    public void registerPluginEventHandler(ZegoSignalingPluginEventHandler handler) {
        signalingPluginEventHandlerNotifyList.addListener(handler, false);
    }


    public void registerZIMEventHandler(ZIMEventHandler handler) {
        zimEventHandlerNotifyList.addListener(handler, false);
    }

    public void enableNotifyWhenAppRunningInBackgroundOrQuit(boolean enable) {
        this.notifyWhenAppRunningInBackgroundOrQuit = enable;
        try {
            if (enable) {
                ZPNsManager.enableDebug(BuildConfig.DEBUG);
                ZPNsConfig zpnsConfig = new ZPNsConfig();
                zpnsConfig.enableFCMPush(); // FCM
                ZPNsManager.setPushConfig(zpnsConfig);
                ZPNsManager.getInstance().registerPush(application);
            } else {
                ZPNsManager.getInstance().unregisterPush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
