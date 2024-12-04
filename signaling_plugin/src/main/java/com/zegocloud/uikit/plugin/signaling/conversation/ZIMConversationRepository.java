package com.zegocloud.uikit.plugin.signaling.conversation;

import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMConversationDeletedCallback;
import im.zego.zim.callback.ZIMConversationListQueriedCallback;
import im.zego.zim.callback.ZIMConversationMessageReceiptReadSentCallback;
import im.zego.zim.callback.ZIMConversationNotificationStatusSetCallback;
import im.zego.zim.callback.ZIMConversationPinnedListQueriedCallback;
import im.zego.zim.callback.ZIMConversationPinnedStateUpdatedCallback;
import im.zego.zim.callback.ZIMConversationQueriedCallback;
import im.zego.zim.callback.ZIMConversationTotalUnreadMessageCountClearedCallback;
import im.zego.zim.callback.ZIMConversationTotalUnreadMessageCountQueriedCallback;
import im.zego.zim.callback.ZIMConversationUnreadMessageCountClearedCallback;
import im.zego.zim.callback.ZIMConversationsAllDeletedCallback;
import im.zego.zim.entity.ZIMConversationDeleteConfig;
import im.zego.zim.entity.ZIMConversationFilterOption;
import im.zego.zim.entity.ZIMConversationQueryConfig;
import im.zego.zim.entity.ZIMConversationTotalUnreadMessageCountQueryConfig;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.enums.ZIMConversationNotificationStatus;
import im.zego.zim.enums.ZIMConversationType;
import im.zego.zim.enums.ZIMErrorCode;

public class ZIMConversationRepository {

    public void queryConversationList(ZIMConversationQueryConfig config, ZIMConversationListQueriedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationListQueried(null, errorInfo);
            }
            return;
        }
        ZIM.getInstance().queryConversationList(config, callback);
    }

    public void queryConversationList(ZIMConversationQueryConfig config, ZIMConversationFilterOption option,
        ZIMConversationListQueriedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationListQueried(null, errorInfo);
            }
            return;
        }
        ZIM.getInstance().queryConversationList(config, option, callback);
    }

    public void queryConversation(String conversationID, ZIMConversationType conversationType,
        ZIMConversationQueriedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationQueried(null, errorInfo);
            }
            return;
        }
        ZIM.getInstance().queryConversation(conversationID, conversationType, callback);
    }

    public void queryConversationPinnedList(ZIMConversationQueryConfig config,
        ZIMConversationPinnedListQueriedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationPinnedListQueried(null, errorInfo);
            }
            return;
        }
        ZIM.getInstance().queryConversationPinnedList(config, callback);
    }

    public void queryConversationTotalUnreadMessageCount(ZIMConversationTotalUnreadMessageCountQueryConfig config,
        ZIMConversationTotalUnreadMessageCountQueriedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationTotalUnreadMessageCountQueried(0, errorInfo);
            }
            return;
        }
        ZIM.getInstance().queryConversationTotalUnreadMessageCount(config, callback);
    }

    public void updateConversationPinnedState(boolean isPinned, String conversationID,
        ZIMConversationType conversationType, ZIMConversationPinnedStateUpdatedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationPinnedStateUpdated(conversationID, conversationType, errorInfo);
            }
            return;
        }
        ZIM.getInstance().updateConversationPinnedState(isPinned, conversationID, conversationType, callback);
    }

    public void deleteConversation(String conversationID, ZIMConversationType conversationType,
        ZIMConversationDeleteConfig config, ZIMConversationDeletedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationDeleted(conversationID, conversationType, errorInfo);
            }
            return;
        }
        ZIM.getInstance().deleteConversation(conversationID, conversationType, config, callback);
    }

    public void deleteAllConversations(ZIMConversationDeleteConfig config,
        ZIMConversationsAllDeletedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationsAllDeleted(errorInfo);
            }
            return;
        }
        ZIM.getInstance().deleteAllConversations(config, callback);
    }

    public void clearConversationUnreadMessageCount(String conversationID, ZIMConversationType conversationType,
        ZIMConversationUnreadMessageCountClearedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationUnreadMessageCountCleared(conversationID, conversationType, errorInfo);
            }
            return;
        }
        ZIM.getInstance().clearConversationUnreadMessageCount(conversationID, conversationType, callback);
    }

    public void clearConversationTotalUnreadMessageCount(
        ZIMConversationTotalUnreadMessageCountClearedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationTotalUnreadMessageCountCleared(errorInfo);
            }
            return;
        }
        ZIM.getInstance().clearConversationTotalUnreadMessageCount(callback);
    }

    public void setConversationNotificationStatus(ZIMConversationNotificationStatus status, String conversationID,
        ZIMConversationType conversationType, ZIMConversationNotificationStatusSetCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationNotificationStatusSet(conversationID, conversationType, errorInfo);
            }
            return;
        }
        ZIM.getInstance().setConversationNotificationStatus(status, conversationID, conversationType, callback);
    }

    public void sendConversationMessageReceiptRead(String conversationID, ZIMConversationType conversationType,
        ZIMConversationMessageReceiptReadSentCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onConversationMessageReceiptReadSent(conversationID, conversationType, errorInfo);
            }
            return;
        }
        ZIM.getInstance().sendConversationMessageReceiptRead(conversationID, conversationType, callback);
    }

}
