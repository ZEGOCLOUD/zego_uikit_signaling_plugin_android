package com.zegocloud.uikit.plugin.signaling;

import android.app.Application;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.CancelInvitationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ConnectUserCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.EndRoomBatchOperationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.InvitationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.QueryRoomPropertyCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.QueryUsersInRoomAttributesCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.RenewTokenCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ResponseInvitationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.RoomCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.RoomPropertyOperationCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.SendRoomMessageCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.SetUsersInRoomAttributesCallback;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingInRoomCommandMessage;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingInRoomTextMessage;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginConnectionState;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginEventHandler;
import com.zegocloud.uikit.plugin.adapter.plugins.signaling.ZegoSignalingPluginNotificationConfig;
import com.zegocloud.uikit.plugin.adapter.utils.GenericUtils;
import com.zegocloud.uikit.plugin.adapter.utils.NotifyList;
import com.zegocloud.uikit.plugin.signaling.call.ZIMCallRepository;
import com.zegocloud.uikit.plugin.signaling.room.ZIMRoomRepository;
import com.zegocloud.uikit.plugin.signaling.user.ZIMUserRepository;
import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMCallAcceptanceSentCallback;
import im.zego.zim.callback.ZIMCallCancelSentCallback;
import im.zego.zim.callback.ZIMCallEndSentCallback;
import im.zego.zim.callback.ZIMCallInvitationListQueriedCallback;
import im.zego.zim.callback.ZIMCallInvitationSentCallback;
import im.zego.zim.callback.ZIMCallJoinSentCallback;
import im.zego.zim.callback.ZIMCallQuitSentCallback;
import im.zego.zim.callback.ZIMCallRejectionSentCallback;
import im.zego.zim.callback.ZIMCallingInvitationSentCallback;
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
import im.zego.zim.callback.ZIMUsersInfoQueriedCallback;
import im.zego.zim.entity.ZIMAppConfig;
import im.zego.zim.entity.ZIMCallAcceptConfig;
import im.zego.zim.entity.ZIMCallCancelConfig;
import im.zego.zim.entity.ZIMCallEndConfig;
import im.zego.zim.entity.ZIMCallEndedSentInfo;
import im.zego.zim.entity.ZIMCallInfo;
import im.zego.zim.entity.ZIMCallInvitationAcceptedInfo;
import im.zego.zim.entity.ZIMCallInvitationCancelledInfo;
import im.zego.zim.entity.ZIMCallInvitationCreatedInfo;
import im.zego.zim.entity.ZIMCallInvitationEndedInfo;
import im.zego.zim.entity.ZIMCallInvitationQueryConfig;
import im.zego.zim.entity.ZIMCallInvitationReceivedInfo;
import im.zego.zim.entity.ZIMCallInvitationRejectedInfo;
import im.zego.zim.entity.ZIMCallInvitationSentInfo;
import im.zego.zim.entity.ZIMCallInvitationTimeoutInfo;
import im.zego.zim.entity.ZIMCallInviteConfig;
import im.zego.zim.entity.ZIMCallJoinConfig;
import im.zego.zim.entity.ZIMCallJoinSentInfo;
import im.zego.zim.entity.ZIMCallQuitConfig;
import im.zego.zim.entity.ZIMCallQuitSentInfo;
import im.zego.zim.entity.ZIMCallRejectConfig;
import im.zego.zim.entity.ZIMCallUserStateChangeInfo;
import im.zego.zim.entity.ZIMCallingInvitationSentInfo;
import im.zego.zim.entity.ZIMCallingInviteConfig;
import im.zego.zim.entity.ZIMCommandMessage;
import im.zego.zim.entity.ZIMConversationChangeInfo;
import im.zego.zim.entity.ZIMConversationsAllDeletedInfo;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMErrorUserInfo;
import im.zego.zim.entity.ZIMFriendApplicationInfo;
import im.zego.zim.entity.ZIMFriendInfo;
import im.zego.zim.entity.ZIMGroupApplicationInfo;
import im.zego.zim.entity.ZIMGroupAttributesUpdateInfo;
import im.zego.zim.entity.ZIMGroupFullInfo;
import im.zego.zim.entity.ZIMGroupMemberInfo;
import im.zego.zim.entity.ZIMGroupMuteInfo;
import im.zego.zim.entity.ZIMGroupOperatedInfo;
import im.zego.zim.entity.ZIMGroupVerifyInfo;
import im.zego.zim.entity.ZIMMessage;
import im.zego.zim.entity.ZIMMessageDeletedInfo;
import im.zego.zim.entity.ZIMMessageReaction;
import im.zego.zim.entity.ZIMMessageReceiptInfo;
import im.zego.zim.entity.ZIMMessageRootRepliedCountInfo;
import im.zego.zim.entity.ZIMMessageSendConfig;
import im.zego.zim.entity.ZIMMessageSentStatusChangeInfo;
import im.zego.zim.entity.ZIMPushConfig;
import im.zego.zim.entity.ZIMRevokeMessage;
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
import im.zego.zim.entity.ZIMUserFullInfo;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.entity.ZIMUserRule;
import im.zego.zim.entity.ZIMUsersInfoQueryConfig;
import im.zego.zim.enums.ZIMBlacklistChangeAction;
import im.zego.zim.enums.ZIMConnectionEvent;
import im.zego.zim.enums.ZIMConnectionState;
import im.zego.zim.enums.ZIMConversationType;
import im.zego.zim.enums.ZIMErrorCode;
import im.zego.zim.enums.ZIMFriendApplicationListChangeAction;
import im.zego.zim.enums.ZIMFriendListChangeAction;
import im.zego.zim.enums.ZIMGroupApplicationListChangeAction;
import im.zego.zim.enums.ZIMGroupEvent;
import im.zego.zim.enums.ZIMGroupMemberEvent;
import im.zego.zim.enums.ZIMGroupMemberState;
import im.zego.zim.enums.ZIMGroupState;
import im.zego.zim.enums.ZIMRoomAttributesUpdateAction;
import im.zego.zim.enums.ZIMRoomEvent;
import im.zego.zim.enums.ZIMRoomState;
import im.zego.zpns.ZPNsManager;
import im.zego.zpns.util.ZPNsConfig;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONObject;
import timber.log.Timber;

public class ZegoSignalingPluginService {

    private Application application;
    private boolean notifyWhenAppRunningInBackgroundOrQuit;
    private NotifyList<ZegoSignalingPluginEventHandler> signalingPluginEventHandlerNotifyList = new NotifyList<>();
    private NotifyList<ZIMEventHandler> zimEventHandlerNotifyList = new NotifyList<>();
    private final AtomicBoolean isZIMInited = new AtomicBoolean();

    private ZPNsConfig zpnsConfig = new ZPNsConfig();

    private ZIMUserRepository userRepository;
    private ZIMRoomRepository roomRepository;
    private ZIMCallRepository callRepository;
    private ZIMEventHandler zimEventHandler;

    public ZegoSignalingPluginService() {
        userRepository = new ZIMUserRepository();
        roomRepository = new ZIMRoomRepository();
        callRepository = new ZIMCallRepository(userRepository);

        zimEventHandler = new ZIMEventHandler() {
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
                userRepository.onConnectionStateChanged(zim, state, event, extendedData);

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
                Timber.d(
                    "onCallInvitationReceived() called with: zim = [" + zim + "], info = [" + info + "], callID = ["
                        + callID + "]");

                callRepository.onCallInvitationReceived(zim, info, callID);

                // should before zimEventHandlerNotifyList,else call interface has no
                // data when zim callback invoked
                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationReceived(callID, info.inviter, info.extendedData);
                });

                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationReceived(zim, info, callID);
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
                Timber.d(
                    "onCallInvitationCancelled() called with: zim = [" + zim + "], info = [" + info + "], callID = ["
                        + callID + "]");
                callRepository.onCallInvitationCancelled(zim, info, callID);

                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationCancelled(callID, info.inviter, info.extendedData);
                });

                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationCancelled(zim, info, callID);
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

                callRepository.onCallInvitationAccepted(zim, info, callID);

                signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationAccepted(callID, info.invitee, info.extendedData);
                });

                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationAccepted(zim, info, callID);
                });
            }

            @Override
            public void onBroadcastMessageReceived(ZIM zim, ZIMMessage message) {
                super.onBroadcastMessageReceived(zim, message);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onBroadcastMessageReceived(zim, message);
                });
            }

            @Override
            public void onMessageRepliedCountChanged(ZIM zim, ArrayList<ZIMMessageRootRepliedCountInfo> infos) {
                super.onMessageRepliedCountChanged(zim, infos);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onMessageRepliedCountChanged(zim, infos);
                });
            }

            @Override
            public void onMessageRepliedInfoChanged(ZIM zim, ArrayList<ZIMMessage> messageList) {
                super.onMessageRepliedInfoChanged(zim, messageList);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onMessageRepliedInfoChanged(zim, messageList);
                });
            }

            @Override
            public void onCallInvitationEnded(ZIM zim, ZIMCallInvitationEndedInfo info, String callID) {
                super.onCallInvitationEnded(zim, info, callID);
                Timber.d("onCallInvitationEnded() called with: zim = [" + zim + "], info = [" + info + "], callID = ["
                    + callID + "]");
                callRepository.onCallInvitationEnded(zim, info, callID);

                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationEnded(zim, info, callID);
                });
            }

            @Override
            public void onCallInvitationTimeout(ZIM zim, ZIMCallInvitationTimeoutInfo info, String callID) {
                super.onCallInvitationTimeout(zim, info, callID);
                Timber.d("onCallInvitationTimeout() called with: zim = [" + zim + "], info = [" + info + "], callID = ["
                    + callID + "]");
                callRepository.onCallInvitationTimeout(zim, info, callID);

                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationTimeout(zim, info, callID);
                });
            }

            @Override
            public void onCallUserStateChanged(ZIM zim, ZIMCallUserStateChangeInfo info, String callID) {
                super.onCallUserStateChanged(zim, info, callID);
                Timber.d("onCallUserStateChanged() called with: zim = [" + zim + "], info = [" + info + "], callID = ["
                    + callID + "]");
                callRepository.onCallUserStateChanged(zim, info, callID);

                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallUserStateChanged(zim, info, callID);
                });
            }

            @Override
            public void onMessageDeleted(ZIM zim, ZIMMessageDeletedInfo deletedInfo) {
                super.onMessageDeleted(zim, deletedInfo);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onMessageDeleted(zim, deletedInfo);
                });
            }

            @Override
            public void onUserInfoUpdated(ZIM zim, ZIMUserFullInfo info) {
                super.onUserInfoUpdated(zim, info);
                userRepository.onUserInfoUpdated(zim, info);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onUserInfoUpdated(zim, info);
                });
            }

            @Override
            public void onMessageSentStatusChanged(ZIM zim,
                ArrayList<ZIMMessageSentStatusChangeInfo> messageSentStatusChangeInfoList) {
                super.onMessageSentStatusChanged(zim, messageSentStatusChangeInfoList);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onMessageSentStatusChanged(zim, messageSentStatusChangeInfoList);
                });
            }

            @Override
            public void onConversationMessageReceiptChanged(ZIM zim, ArrayList<ZIMMessageReceiptInfo> infos) {
                super.onConversationMessageReceiptChanged(zim, infos);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onConversationMessageReceiptChanged(zim, infos);
                });
            }

            @Override
            public void onConversationsAllDeleted(ZIM zim, ZIMConversationsAllDeletedInfo info) {
                super.onConversationsAllDeleted(zim, info);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onConversationsAllDeleted(zim, info);
                });
            }

            @Override
            public void onMessageReactionsChanged(ZIM zim, ArrayList<ZIMMessageReaction> reactions) {
                super.onMessageReactionsChanged(zim, reactions);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onMessageReactionsChanged(zim, reactions);
                });
            }

            @Override
            public void onMessageReceiptChanged(ZIM zim, ArrayList<ZIMMessageReceiptInfo> infos) {
                super.onMessageReceiptChanged(zim, infos);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onMessageReceiptChanged(zim, infos);
                });
            }

            @Override
            public void onMessageRevokeReceived(ZIM zim, ArrayList<ZIMRevokeMessage> messageList) {
                super.onMessageRevokeReceived(zim, messageList);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onMessageRevokeReceived(zim, messageList);
                });
            }

            private static final String TAG = "ZegoSignalingPluginServ";

            /**
             * inviter received invitees reject.
             * @param zim
             * @param info
             * @param callID
             */

            public void onCallInvitationRejected(ZIM zim, ZIMCallInvitationRejectedInfo info, String callID) {
                super.onCallInvitationRejected(zim, info, callID);
                Timber.d(
                    "onCallInvitationRejected() called with: zim = [" + zim + "], info = [" + info + "], callID = ["
                        + callID + "]");
                callRepository.onCallInvitationRejected(zim, info, callID);

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
                Timber.d("onCallInvitationTimeout() called with: zim = [" + zim + "], callID = [" + callID + "]");
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
                Timber.d("onCallInviteesAnsweredTimeout() called with: zim = [" + zim + "], invitees = [" + invitees
                    + "], callID = [" + callID + "]");
                callRepository.onCallInviteesAnsweredTimeout(zim, invitees, callID);

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

                List<ZegoSignalingInRoomTextMessage> signalMessageList = new ArrayList<>();
                List<ZegoSignalingInRoomCommandMessage> signalCommandMessageList = new ArrayList<>();

                for (ZIMMessage zimMessage : messageList) {
                    if (zimMessage instanceof ZIMTextMessage) {
                        ZIMTextMessage textMessage = (ZIMTextMessage) zimMessage;
                        ZegoSignalingInRoomTextMessage message = new ZegoSignalingInRoomTextMessage();
                        message.messageID = textMessage.getMessageID();
                        message.timestamp = textMessage.getMessageID();
                        message.orderKey = textMessage.getOrderKey();
                        message.text = textMessage.message;
                        message.senderUserID = textMessage.getSenderUserID();
                        signalMessageList.add(message);
                        signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                            handler.onInRoomTextMessageReceived(signalMessageList, fromRoomID);
                        });
                    } else if (zimMessage instanceof ZIMCommandMessage) {
                        ZIMCommandMessage commandMessage = (ZIMCommandMessage) zimMessage;
                        String messageText = new String(commandMessage.message, StandardCharsets.UTF_8);
                        ZegoSignalingInRoomCommandMessage message = new ZegoSignalingInRoomCommandMessage();
                        message.messageID = commandMessage.getMessageID();
                        message.timestamp = commandMessage.getTimestamp();
                        message.orderKey = commandMessage.getOrderKey();
                        message.text = messageText;
                        message.senderUserID = commandMessage.getSenderUserID();
                        signalCommandMessageList.add(message);

                        signalingPluginEventHandlerNotifyList.notifyAllListener(handler -> {
                            handler.onInRoomCommandMessageReceived(signalCommandMessageList, fromRoomID);
                        });
                    }
                }
            }

            @Override
            public void onRoomStateChanged(ZIM zim, ZIMRoomState state, ZIMRoomEvent event, JSONObject extendedData,
                String roomID) {
                super.onRoomStateChanged(zim, state, event, extendedData, roomID);

                roomRepository.onRoomStateChanged(zim, state, event, extendedData, roomID);

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
            public void onCallInvitationCreated(ZIM zim, ZIMCallInvitationCreatedInfo info, String callID) {
                super.onCallInvitationCreated(zim, info, callID);
                Timber.d("onCallInvitationCreated() called with: zim = [" + zim + "], info = [" + info + "], callID = ["
                    + callID + "]");
                callRepository.onCallInvitationCreated(zim, info, callID);

                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onCallInvitationCreated(zim, info, callID);
                });
            }

            @Override
            public void onBlacklistChanged(ZIM zim, ArrayList<ZIMUserInfo> userList, ZIMBlacklistChangeAction action) {
                super.onBlacklistChanged(zim, userList, action);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onBlacklistChanged(zim, userList, action);
                });
            }

            @Override
            public void onGroupMutedInfoUpdated(ZIM zim, ZIMGroupMuteInfo muteInfo, ZIMGroupOperatedInfo operatedInfo,
                String groupID) {
                super.onGroupMutedInfoUpdated(zim, muteInfo, operatedInfo, groupID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onGroupMutedInfoUpdated(zim, muteInfo, operatedInfo, groupID);
                });
            }

            @Override
            public void onFriendApplicationListChanged(ZIM zim,
                ArrayList<ZIMFriendApplicationInfo> friendApplicationInfoList,
                ZIMFriendApplicationListChangeAction action) {
                super.onFriendApplicationListChanged(zim, friendApplicationInfoList, action);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onFriendApplicationListChanged(zim, friendApplicationInfoList, action);
                });
            }

            @Override
            public void onFriendApplicationUpdated(ZIM zim,
                ArrayList<ZIMFriendApplicationInfo> friendApplicationInfoList) {
                super.onFriendApplicationUpdated(zim, friendApplicationInfoList);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onFriendApplicationUpdated(zim, friendApplicationInfoList);
                });
            }

            @Override
            public void onGroupApplicationListChanged(ZIM zim, ArrayList<ZIMGroupApplicationInfo> applicationList,
                ZIMGroupApplicationListChangeAction action) {
                super.onGroupApplicationListChanged(zim, applicationList, action);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onGroupApplicationListChanged(zim, applicationList, action);
                });
            }

            @Override
            public void onGroupApplicationUpdated(ZIM zim, ArrayList<ZIMGroupApplicationInfo> applicationList) {
                super.onGroupApplicationUpdated(zim, applicationList);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onGroupApplicationUpdated(zim, applicationList);
                });
            }

            @Override
            public void onGroupVerifyInfoUpdated(ZIM zim, ZIMGroupVerifyInfo verifyInfo,
                ZIMGroupOperatedInfo operatedInfo, String groupID) {
                super.onGroupVerifyInfoUpdated(zim, verifyInfo, operatedInfo, groupID);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onGroupVerifyInfoUpdated(zim, verifyInfo, operatedInfo, groupID);
                });
            }

            @Override
            public void onUserRuleUpdated(ZIM zim, ZIMUserRule rule) {
                super.onUserRuleUpdated(zim, rule);
                userRepository.onUserRuleUpdated(zim, rule);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onUserRuleUpdated(zim, rule);
                });
            }

            @Override
            public void onFriendInfoUpdated(ZIM zim, ArrayList<ZIMFriendInfo> friendInfoList) {
                super.onFriendInfoUpdated(zim, friendInfoList);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onFriendInfoUpdated(zim, friendInfoList);
                });
            }

            @Override
            public void onFriendListChanged(ZIM zim, ArrayList<ZIMFriendInfo> friendInfoList,
                ZIMFriendListChangeAction action) {
                super.onFriendListChanged(zim, friendInfoList, action);
                zimEventHandlerNotifyList.notifyAllListener(handler -> {
                    handler.onFriendListChanged(zim, friendInfoList, action);
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
        };
    }

    public void init(Application application, Long appID, String appSign) {
        Timber.d("ZIM init() called with: application = [" + application + "], appID = [" + appID + "], isZIMInited = ["
            + isZIMInited.get() + "]");
        this.application = application;
        boolean result = isZIMInited.compareAndSet(false, true);
        if (!result) {
            return;
        }
        ZIMAppConfig zimAppConfig = new ZIMAppConfig();
        zimAppConfig.appID = appID;
        zimAppConfig.appSign = appSign;
        ZIM.create(zimAppConfig, application);

        setEventHandler(zimEventHandler);
    }

    public void setEventHandler(ZIMEventHandler zimEventHandler) {
        ZIM.getInstance().setEventHandler(zimEventHandler);
    }


    public void connectUser(String userID, String userName, ConnectUserCallback callback) {
        connectUser(userID, userName, null, callback);
    }

    public void connectUser(String userID, String userName, String token, ConnectUserCallback callback) {
        ZIMUserInfo zimUserInfo = new ZIMUserInfo();
        zimUserInfo.userID = userID;
        zimUserInfo.userName = userName;
        Timber.d("zim login() called with: userID = [" + userID + "], userName = [" + userName + "], token = [" + token
            + "], callback = [" + callback + "]");
        userRepository.login(zimUserInfo, token, new ZIMLoggedInCallback() {

            public void onLoggedIn(ZIMError errorInfo) {
                Timber.d("zim login() result called with: errorInfo = [" + errorInfo + "]");
                if (callback != null) {
                    int code = errorInfo.code == ZIMErrorCode.USER_HAS_ALREADY_LOGGED ? ZIMErrorCode.SUCCESS.value()
                        : errorInfo.code.value();
                    callback.onResult(code, errorInfo.message);
                }
            }
        });
    }


    public void disconnectUser() {
        Timber.d("disconnectUser() called");
        userRepository.logout();
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

        ZIMPushConfig pushConfig = new ZIMPushConfig();
        if (notificationConfig != null) {
            pushConfig.payload = data;
            pushConfig.title = notificationConfig.getTitle();
            pushConfig.content = notificationConfig.getMessage();
            pushConfig.resourcesID = notificationConfig.getResourceID();
            config.pushConfig = pushConfig;
        }
        callInvite(invitees, config, new ZIMCallInvitationSentCallback() {

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
        ZegoSignalingPluginNotificationConfig notificationConfig, CancelInvitationCallback callback) {
        ZIMCallCancelConfig config = new ZIMCallCancelConfig();
        config.extendedData = data;
        ZIMPushConfig pushConfig = new ZIMPushConfig();
        if (notificationConfig != null) {
            pushConfig.payload = data;
            pushConfig.title = notificationConfig.getTitle();
            pushConfig.content = notificationConfig.getMessage();
            pushConfig.resourcesID = notificationConfig.getResourceID();
            config.pushConfig = pushConfig;
        }
        callCancel(invitees, invitationID, config, new ZIMCallCancelSentCallback() {
            @Override
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
        callReject(invitationID, config, new ZIMCallRejectionSentCallback() {

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
        callAccept(invitationID, config, new ZIMCallAcceptanceSentCallback() {

            public void onCallAcceptanceSent(String callID, ZIMError errorInfo) {
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message);
                }
            }
        });
    }

    public void callReject(String callID, ZIMCallRejectConfig config, ZIMCallRejectionSentCallback callback) {
        String extendedData = config.extendedData == null ? null : config.extendedData;
        Timber.d("callReject() called with: callID = [" + callID + "], config.extendedData = [" + extendedData
            + "], callback = [" + callback + "]");
        callRepository.callReject(callID, config, new ZIMCallRejectionSentCallback() {
            @Override
            public void onCallRejectionSent(String callID, ZIMError errorInfo) {
                Timber.d("callReject result() called with: callID = [" + callID + "], errorInfo = [" + errorInfo + "]");
                if (callback != null) {
                    callback.onCallRejectionSent(callID, errorInfo);
                }
            }
        });
    }

    public void callAccept(String callID, ZIMCallAcceptConfig config, ZIMCallAcceptanceSentCallback callback) {
        String extendedData = config.extendedData == null ? null : config.extendedData;
        Timber.d("callAccept() called with: callID = [" + callID + "], config.extendedData = [" + extendedData
            + "], callback = [" + callback + "]");
        callRepository.callAccept(callID, config, new ZIMCallAcceptanceSentCallback() {
            @Override
            public void onCallAcceptanceSent(String callID, ZIMError errorInfo) {
                Timber.d("callAccept result() called with: callID = [" + callID + "], errorInfo = [" + errorInfo + "]");
                if (callback != null) {
                    callback.onCallAcceptanceSent(callID, errorInfo);
                }
            }
        });
    }

    public void callInvite(List<String> invitees, ZIMCallInviteConfig config, ZIMCallInvitationSentCallback callback) {
        String resourceID = config.pushConfig == null ? null : config.pushConfig.resourcesID;
        String payload = config.pushConfig == null ? null : config.pushConfig.payload;
        String extendedData = config.extendedData == null ? null : config.extendedData;
        Timber.d("callInvite() called with: invitees = [" + invitees + "], config.extendedData = [" + extendedData
            + "], config.mode = [" + config.mode + "],resourceID: " + resourceID + ",config.enableNotReceivedCheck:"
            + config.enableNotReceivedCheck + ",payload: " + payload);
        callRepository.callInvite(invitees, config, new ZIMCallInvitationSentCallback() {
            @Override
            public void onCallInvitationSent(String callID, ZIMCallInvitationSentInfo info, ZIMError errorInfo) {
                Timber.d(
                    "callInvite result() called with: callID = [" + callID + "], info = [" + info + "], errorInfo = ["
                        + errorInfo + "]");
                if (callback != null) {
                    callback.onCallInvitationSent(callID, info, errorInfo);
                }
            }
        });
    }

    public void callingInvite(List<String> invitees, String callID, ZIMCallingInviteConfig config,
        ZIMCallingInvitationSentCallback callback) {
        String resourceID = config.pushConfig == null ? null : config.pushConfig.resourcesID;
        String payload = config.pushConfig == null ? null : config.pushConfig.payload;
        Timber.d("callingInvite() called with: invitees = [" + invitees + "], callID = [" + callID + "], resourceID = ["
            + resourceID + "], payload = [" + payload + "]");
        callRepository.callingInvite(invitees, callID, config, new ZIMCallingInvitationSentCallback() {
            @Override
            public void onCallingInvitationSent(String callID, ZIMCallingInvitationSentInfo info, ZIMError errorInfo) {
                Timber.d("callingInvite result() called with: callID = [" + callID + "], info = [" + info
                    + "], errorInfo = [" + errorInfo + "]");
                if (callback != null) {
                    callback.onCallingInvitationSent(callID, info, errorInfo);
                }
            }
        });
    }

    public void callJoin(String callID, ZIMCallJoinConfig config, ZIMCallJoinSentCallback callback) {
        String extendedData = config.extendedData == null ? null : config.extendedData;
        Timber.d(
            "callJoin() called with: callID = [" + callID + "], extendedData = [" + extendedData + "], callback = ["
                + callback + "]");
        callRepository.callJoin(callID, config, new ZIMCallJoinSentCallback() {
            @Override
            public void onCallJoinSent(String callID, ZIMCallJoinSentInfo info, ZIMError errorInfo) {
                Timber.d("callJoin result() with: callID = [" + callID + "], info = [" + info + "], errorInfo = ["
                    + errorInfo + "]");
                if (callback != null) {
                    callback.onCallJoinSent(callID, info, errorInfo);
                }
            }
        });
    }

    public void callQuit(String callID, ZIMCallQuitConfig config, ZIMCallQuitSentCallback callback) {
        String extendedData = config.extendedData == null ? null : config.extendedData;
        String resourceID = config.pushConfig == null ? null : config.pushConfig.resourcesID;
        Timber.d("callQuit() called with: callID = [" + callID + "], config.extendedData = [" + extendedData
            + "], resourceID = [" + resourceID + "]");
        callRepository.callQuit(callID, config, new ZIMCallQuitSentCallback() {
            @Override
            public void onCallQuitSent(String callID, ZIMCallQuitSentInfo info, ZIMError errorInfo) {
                Timber.d(
                    "callQuit result() called with: callID = [" + callID + "], info = [" + info + "], errorInfo = ["
                        + errorInfo + "]");
                if (callback != null) {
                    callback.onCallQuitSent(callID, info, errorInfo);
                }
            }
        });
    }

    public void callEnd(String callID, ZIMCallEndConfig config, ZIMCallEndSentCallback callback) {
        String extendedData = config.extendedData == null ? null : config.extendedData;
        String resourceID = config.pushConfig == null ? null : config.pushConfig.resourcesID;
        Timber.d(
            "callEnd() called with: callID = [" + callID + "], extendedData = [" + extendedData + "], resourceID = ["
                + resourceID + "]");
        callRepository.callEnd(callID, config, new ZIMCallEndSentCallback() {
            @Override
            public void onCallEndSent(String callID, ZIMCallEndedSentInfo info, ZIMError errorInfo) {
                Timber.d("callEnd result() called with: callID = [" + callID + "], info = [" + info + "], errorInfo = ["
                    + errorInfo + "]");
                if (callback != null) {
                    callback.onCallEndSent(callID, info, errorInfo);
                }
            }
        });
    }

    public void callCancel(List<String> invitees, String callID, ZIMCallCancelConfig config,
        ZIMCallCancelSentCallback callback) {
        String extendedData = config.extendedData == null ? null : config.extendedData;
        String resourceID = config.pushConfig == null ? null : config.pushConfig.resourcesID;
        Timber.d("callCancel() called with: invitees = [" + invitees + "], callID = [" + callID + "], extendedData = ["
            + extendedData + "], resourcesID = [" + resourceID + "]");
        callRepository.callCancel(invitees, callID, config, new ZIMCallCancelSentCallback() {
            @Override
            public void onCallCancelSent(String callID, ArrayList<String> errorInvitees, ZIMError errorInfo) {
                Timber.d("callCancel result() called with: callID = [" + callID + "], errorInvitees = [" + errorInvitees
                    + "], errorInfo = [" + errorInfo + "]");
                if (callback != null) {
                    callback.onCallCancelSent(callID, errorInvitees, errorInfo);
                }
            }
        });
    }

    public void queryCallInvitationList(ZIMCallInvitationQueryConfig config,
        ZIMCallInvitationListQueriedCallback callback) {
        Timber.d("queryCallInvitationList() called with: config.count = [" + config.count + "], config.nextFlag = ["
            + config.nextFlag + "]");
        callRepository.queryCallInvitationList(config, new ZIMCallInvitationListQueriedCallback() {
            @Override
            public void onCallInvitationListQueried(ArrayList<ZIMCallInfo> callList, long nextFlag,
                ZIMError errorInfo) {
                Timber.d(
                    "onCallInvitationListQueried() called with: callList = [" + callList + "], nextFlag = [" + nextFlag
                        + "], errorInfo = [" + errorInfo + "]");
                if (callback != null) {
                    callback.onCallInvitationListQueried(callList, nextFlag, errorInfo);
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
        Timber.d(
            "enterRoom() called with: roomID = [" + roomID + "], roomName = [" + roomName + "], callback = [" + callback
                + "]");
        roomRepository.enterRoom(zimRoomInfo, config, new ZIMRoomEnteredCallback() {

            public void onRoomEntered(ZIMRoomFullInfo roomInfo, ZIMError errorInfo) {
                Timber.d(
                    "onRoomEntered() called with: roomInfo = [" + roomInfo.baseInfo + "], errorInfo = [" + errorInfo
                        + "]");
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message);
                }
            }
        });
    }


    public void leaveRoom(String roomID, RoomCallback callback) {
        Timber.d("leaveRoom() called with: roomID = [" + roomID + "], callback = [" + callback + "]");
        roomRepository.leaveRoom(roomID, new ZIMRoomLeftCallback() {

            public void onRoomLeft(String roomID, ZIMError errorInfo) {
                Timber.d("onRoomLeft() called with: roomID = [" + roomID + "], errorInfo = [" + errorInfo + "]");
                if (callback != null) {
                    callback.onResult(errorInfo.code.value(), errorInfo.message);
                }
            }
        });
    }

    public void destroy() {
        Timber.d("destroy() called");
        if (ZIM.getInstance() == null) {
            return;
        }
        isZIMInited.set(false);
        userRepository.clearLoginData();
        zimEventHandlerNotifyList.clear();
        signalingPluginEventHandlerNotifyList.clear();
        ZIM.getInstance().setEventHandler(null);
        ZIM.getInstance().destroy();
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

    public void sendInRoomCommandMessage(String command, String roomID, SendRoomMessageCallback callback) {
        if (ZIM.getInstance() == null) {
            return;
        }
        byte[] bytes = command.getBytes(StandardCharsets.UTF_8);
        ZIMCommandMessage commandMessage = new ZIMCommandMessage(bytes);
        ZIM.getInstance().sendMessage(commandMessage, roomID, ZIMConversationType.ROOM, new ZIMMessageSendConfig(),
            new ZIMMessageSentCallback() {
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

    public void unregisterZIMEventHandler(ZIMEventHandler handler) {
        zimEventHandlerNotifyList.removeListener(handler, false);
    }

    public void enableFCMPush() {
        zpnsConfig.enableFCMPush();
        ZPNsManager.setPushConfig(zpnsConfig);
    }

    public void disableFCMPush() {
        zpnsConfig.disableFCMPush();
        ZPNsManager.setPushConfig(zpnsConfig);
    }

    public void enableHWPush(String hwAppID) {
        zpnsConfig.enableHWPush(hwAppID);
        ZPNsManager.setPushConfig(zpnsConfig);
    }

    public void enableMiPush(String miAppID, String miAppKey) {
        zpnsConfig.enableMiPush(miAppID, miAppKey);
        ZPNsManager.setPushConfig(zpnsConfig);
    }

    public void enableVivoPush(String vivoAppID, String vivoAppKey) {
        zpnsConfig.enableVivoPush(vivoAppID, vivoAppKey);
        ZPNsManager.setPushConfig(zpnsConfig);
    }

    public void enableOppoPush(String oppoAppID, String oppoAppKey, String oppoAppSecret) {
        zpnsConfig.enableOppoPush(oppoAppID, oppoAppKey, oppoAppSecret);
        ZPNsManager.setPushConfig(zpnsConfig);
    }

    public void setAppType(int appType) {
        zpnsConfig.setAppType(appType);
        ZPNsManager.setPushConfig(zpnsConfig);
    }

    public boolean isOtherPushEnabled() {
        return zpnsConfig.enableHWPush || zpnsConfig.enableMiPush || zpnsConfig.enableOppoPush
            || zpnsConfig.enableVivoPush;
    }

    public boolean isFCMPushEnabled() {
        return zpnsConfig.enableFCMPush;
    }

    public void registerPush() {
        try {
            // if without fcm integrated,will throw exception,so need try/catch
            ZPNsManager.getInstance().registerPush(application);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterPush() {
        try {
            ZPNsManager.getInstance().unregisterPush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableNotifyWhenAppRunningInBackgroundOrQuit(boolean enable) {
        this.notifyWhenAppRunningInBackgroundOrQuit = enable;
        try {
            if (enable) {
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

    public ZIMCallInfo getZIMCallInfo(String callID) {
        return callRepository.getZIMCallInfo(callID);
    }

    public ZIMUserInfo getUserInfo() {
        return userRepository.getUserInfo();
    }

    public ZIMConnectionEvent getConnectionEvent() {
        return userRepository.getConnectionEvent();
    }

    public ZIMConnectionState getConnectionState() {
        return userRepository.getConnectionState();
    }

    public void queryUserInfo(List<String> userIDList, ZIMUsersInfoQueryConfig config,
        ZIMUsersInfoQueriedCallback callback) {
        Timber.d(
            "queryUserInfo() called with: userIDList = [" + userIDList + "], config = [" + config + "], callback = ["
                + callback + "]");
        userRepository.queryUserInfo(userIDList, config, new ZIMUsersInfoQueriedCallback() {
            @Override
            public void onUsersInfoQueried(ArrayList<ZIMUserFullInfo> userList,
                ArrayList<ZIMErrorUserInfo> errorUserList, ZIMError errorInfo) {
                Timber.d(
                    "onUsersInfoQueried() called with: userList = [" + userList + "], errorUserList = [" + errorUserList
                        + "], errorInfo = [" + errorInfo + "]");
                if (callback != null) {
                    callback.onUsersInfoQueried(userList, errorUserList, errorInfo);
                }
            }
        });
    }

    public ZIMUserFullInfo getMemoryUserInfo(String userID) {
        return userRepository.getMemoryUserInfo(userID);
    }
}
