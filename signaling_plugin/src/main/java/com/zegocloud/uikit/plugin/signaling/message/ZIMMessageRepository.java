package com.zegocloud.uikit.plugin.signaling.message;

import com.zegocloud.uikit.plugin.signaling.user.ZIMUserRepository;
import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMCombineMessageDetailQueriedCallback;
import im.zego.zim.callback.ZIMConversationMessagesAllDeletedCallback;
import im.zego.zim.callback.ZIMMediaDownloadedCallback;
import im.zego.zim.callback.ZIMMediaMessageSentCallback;
import im.zego.zim.callback.ZIMMessageDeletedCallback;
import im.zego.zim.callback.ZIMMessageQueriedCallback;
import im.zego.zim.callback.ZIMMessageReactionAddedCallback;
import im.zego.zim.callback.ZIMMessageReactionDeletedCallback;
import im.zego.zim.callback.ZIMMessageReactionUserListQueriedCallback;
import im.zego.zim.callback.ZIMMessageRevokedCallback;
import im.zego.zim.callback.ZIMMessageSentCallback;
import im.zego.zim.callback.ZIMMessageSentFullCallback;
import im.zego.zim.entity.ZIMCombineMessage;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMMediaMessage;
import im.zego.zim.entity.ZIMMessage;
import im.zego.zim.entity.ZIMMessageDeleteConfig;
import im.zego.zim.entity.ZIMMessageDeletedInfo;
import im.zego.zim.entity.ZIMMessageQueryConfig;
import im.zego.zim.entity.ZIMMessageReaction;
import im.zego.zim.entity.ZIMMessageReactionUserQueryConfig;
import im.zego.zim.entity.ZIMMessageReceiptInfo;
import im.zego.zim.entity.ZIMMessageReceivedInfo;
import im.zego.zim.entity.ZIMMessageRevokeConfig;
import im.zego.zim.entity.ZIMMessageRootRepliedCountInfo;
import im.zego.zim.entity.ZIMMessageSendConfig;
import im.zego.zim.entity.ZIMMessageSentStatusChangeInfo;
import im.zego.zim.entity.ZIMRevokeMessage;
import im.zego.zim.entity.ZIMTextMessage;
import im.zego.zim.entity.ZIMUserStatus;
import im.zego.zim.enums.ZIMConversationType;
import im.zego.zim.enums.ZIMErrorCode;
import im.zego.zim.enums.ZIMMediaFileType;
import im.zego.zim.enums.ZIMMessageType;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class ZIMMessageRepository {

    private ZIMUserRepository userRepository;
    private ArrayList<ZIMMessage> messages;

    public ZIMMessageRepository(ZIMUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void replyMessage(ZIMMessage message, ZIMMessage repliedMessage, ZIMMessageSendConfig config,
        ZIMMessageSentFullCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onMessageSent(null, errorInfo);
            }
            return;
        }
        ZIM.getInstance().replyMessage(message, repliedMessage, config, callback);
    }

    public void sendMessage(ZIMMessage message, String toConversationID, ZIMConversationType conversationType,
        ZIMMessageSendConfig config, ZIMMessageSentCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onMessageSent(null, errorInfo);
            }
            return;
        }
        ZIM.getInstance().sendMessage(message, toConversationID, conversationType, config, callback);
    }

    public void sendMediaMessage(ZIMMediaMessage message, String toConversationID, ZIMConversationType conversationType,
        ZIMMessageSendConfig config, ZIMMediaMessageSentCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onMessageSent(null, errorInfo);
            }
            return;
        }
        ZIM.getInstance().sendMediaMessage(message, toConversationID, conversationType, config, callback);
    }

    public void downloadMediaFile(ZIMMediaMessage message, ZIMMediaFileType type, ZIMMediaDownloadedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onMediaDownloaded(message, errorInfo);
            }
            return;
        }
        ZIM.getInstance().downloadMediaFile(message, type, callback);
    }

    public void queryHistoryMessage(String conversationID, ZIMConversationType conversationType,
        ZIMMessageQueryConfig config, ZIMMessageQueriedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onMessageQueried(conversationID, conversationType, null, errorInfo);
            }
            return;
        }
        ZIM.getInstance()
            .queryHistoryMessage(conversationID, conversationType, config, new ZIMMessageQueriedCallback() {
                @Override
                public void onMessageQueried(String conversationID, ZIMConversationType conversationType,
                    ArrayList<ZIMMessage> messageList, ZIMError errorInfo) {
                    if (callback != null) {
                        callback.onMessageQueried(conversationID, conversationType, messageList, errorInfo);
                    }
                }
            });
    }

    public void deleteMessages(List<ZIMMessage> messageList, String conversationID,
        ZIMConversationType conversationType, ZIMMessageDeleteConfig config, ZIMMessageDeletedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onMessageDeleted(conversationID, conversationType, errorInfo);
            }
            return;
        }
        ZIM.getInstance().deleteMessages(messageList, conversationID, conversationType, config, callback);
    }

    public void deleteAllMessage(String conversationID, ZIMConversationType conversationType,
        ZIMMessageDeleteConfig config, ZIMMessageDeletedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onMessageDeleted(conversationID, conversationType, errorInfo);
            }
            return;
        }
        ZIM.getInstance().deleteAllMessage(conversationID, conversationType, config, callback);
    }

    public void deleteAllConversationMessages(ZIMMessageDeleteConfig config,
        ZIMConversationMessagesAllDeletedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationMessagesAllDeleted(errorInfo);
            }
            return;
        }
        ZIM.getInstance().deleteAllConversationMessages(config, callback);
    }

    public void revokeMessage(ZIMMessage message, ZIMMessageRevokeConfig config, ZIMMessageRevokedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onMessageRevoked(message, errorInfo);
            }
            return;
        }
        ZIM.getInstance().revokeMessage(message, config, callback);
    }

    public void addMessageReaction(String reactionType, ZIMMessage message, ZIMMessageReactionAddedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onMessageReactionAdded(null, errorInfo);
            }
            return;
        }
        ZIM.getInstance().addMessageReaction(reactionType, message, callback);
    }

    public void deleteMessageReaction(String reactionType, ZIMMessage message,
        ZIMMessageReactionDeletedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onMessageReactionDeleted(null, errorInfo);
            }
            return;
        }
        ZIM.getInstance().deleteMessageReaction(reactionType, message, callback);
    }

    public void queryMessageReactionUserList(ZIMMessage message, ZIMMessageReactionUserQueryConfig config,
        ZIMMessageReactionUserListQueriedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onMessageReactionUserListQueried(message, null, null, 0, 0, errorInfo);
            }
            return;
        }
        ZIM.getInstance().queryMessageReactionUserList(message, config, callback);
    }

    public void queryCombineMessageDetail(ZIMCombineMessage message, ZIMCombineMessageDetailQueriedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onCombineMessageDetailQueried(message, errorInfo);
            }
            return;
        }
        ZIM.getInstance().queryCombineMessageDetail(message, callback);
    }


    public void onReceiveRoomMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromRoomID) {

    }

    public void onReceiveGroupMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromGroupID) {

    }

    public void onMessageRevokeReceived(ZIM zim, ArrayList<ZIMRevokeMessage> messageList) {

    }

    public void onMessageReceiptChanged(ZIM zim, ArrayList<ZIMMessageReceiptInfo> infos) {

    }

    public void onMessageReactionsChanged(ZIM zim, ArrayList<ZIMMessageReaction> reactions) {

    }

    public void onMessageSentStatusChanged(ZIM zim,
        ArrayList<ZIMMessageSentStatusChangeInfo> messageSentStatusChangeInfoList) {

    }

    public void onMessageDeleted(ZIM zim, ZIMMessageDeletedInfo deletedInfo) {

    }

    public void onMessageRepliedCountChanged(ZIM zim, ArrayList<ZIMMessageRootRepliedCountInfo> infos) {

    }

    public void onBroadcastMessageReceived(ZIM zim, ZIMMessage message) {

    }

    public void onMessageRepliedInfoChanged(ZIM zim, ArrayList<ZIMMessage> messageList) {

    }

    public void onReceivePeerMessage(ZIM zim, ArrayList<ZIMMessage> messageList, String fromUserID) {

    }

    public void onPeerMessageReceived(ZIM zim, ArrayList<ZIMMessage> messageList, ZIMMessageReceivedInfo info,
        String fromUserID) {

    }

    public void onGroupAliasUpdated(ZIM zim, String groupAlias, String operatedUserID, String groupID) {

    }

    public void onGroupMessageReceived(ZIM zim, ArrayList<ZIMMessage> messageList, ZIMMessageReceivedInfo info,
        String fromGroupID) {

    }

    public void onRoomMessageReceived(ZIM zim, ArrayList<ZIMMessage> messageList, ZIMMessageReceivedInfo info,
        String fromRoomID) {

    }

    public void onUserStatusUpdated(ZIM zim, ArrayList<ZIMUserStatus> userStatusList) {

    }
}
