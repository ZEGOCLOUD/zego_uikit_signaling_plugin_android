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
import im.zego.zim.entity.ZIMConversation;
import im.zego.zim.entity.ZIMConversationChangeInfo;
import im.zego.zim.entity.ZIMConversationDeleteConfig;
import im.zego.zim.entity.ZIMConversationFilterOption;
import im.zego.zim.entity.ZIMConversationQueryConfig;
import im.zego.zim.entity.ZIMConversationTotalUnreadMessageCountQueryConfig;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.enums.ZIMConversationEvent;
import im.zego.zim.enums.ZIMConversationNotificationStatus;
import im.zego.zim.enums.ZIMConversationType;
import im.zego.zim.enums.ZIMErrorCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ZIMConversationRepository {

    private Map<String, ZIMConversation> zimConversationMap = new HashMap<>();
    private int totalUnreadMessageCount = 0;


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
        ZIM.getInstance().queryConversationList(config, new ZIMConversationListQueriedCallback() {
            @Override
            public void onConversationListQueried(ArrayList<ZIMConversation> conversationList, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    conversationList.forEach(conversation -> {
                        zimConversationMap.put(conversation.conversationID, conversation);
                    });
                }
                if (callback != null) {
                    callback.onConversationListQueried(conversationList, errorInfo);
                }
            }
        });
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
        ZIM.getInstance().queryConversationList(config, option, new ZIMConversationListQueriedCallback() {
            @Override
            public void onConversationListQueried(ArrayList<ZIMConversation> conversationList, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    conversationList.forEach(conversation -> {
                        zimConversationMap.put(conversation.conversationID, conversation);
                    });
                }
                if (callback != null) {
                    callback.onConversationListQueried(conversationList, errorInfo);
                }
            }
        });
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
        ZIM.getInstance().queryConversation(conversationID, conversationType, new ZIMConversationQueriedCallback() {
            @Override
            public void onConversationQueried(ZIMConversation conversation, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    if (conversation != null) {
                        zimConversationMap.put(conversation.conversationID, conversation);
                    }
                }
                if (callback != null) {
                    callback.onConversationQueried(conversation, errorInfo);
                }
            }
        });
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
        ZIM.getInstance().queryConversationPinnedList(config, new ZIMConversationPinnedListQueriedCallback() {
            @Override
            public void onConversationPinnedListQueried(ArrayList<ZIMConversation> conversationList,
                ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    conversationList.forEach(conversation -> {
                        zimConversationMap.put(conversation.conversationID, conversation);
                    });
                }
                if (callback != null) {
                    callback.onConversationPinnedListQueried(conversationList, errorInfo);
                }
            }
        });
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
        ZIM.getInstance()
            .deleteConversation(conversationID, conversationType, config, new ZIMConversationDeletedCallback() {
                @Override
                public void onConversationDeleted(String conversationID, ZIMConversationType conversationType,
                    ZIMError errorInfo) {
                    if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                        zimConversationMap.remove(conversationID);
                    }
                    if (callback != null) {
                        callback.onConversationDeleted(conversationID, conversationType, errorInfo);
                    }
                }
            });
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
        ZIM.getInstance().deleteAllConversations(config, new ZIMConversationsAllDeletedCallback() {
            @Override
            public void onConversationsAllDeleted(ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    zimConversationMap.clear();
                }
            }
        });
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
        ZIM.getInstance()
            .clearConversationTotalUnreadMessageCount(new ZIMConversationTotalUnreadMessageCountClearedCallback() {
                @Override
                public void onConversationTotalUnreadMessageCountCleared(ZIMError errorInfo) {
                    if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                        totalUnreadMessageCount = 0;
                    }
                }
            });
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

    public void onConversationChanged(ZIM zim, ArrayList<ZIMConversationChangeInfo> conversationChangeInfoList) {
        conversationChangeInfoList.forEach(zimConversationChangeInfo -> {
            ZIMConversation conversation = zimConversationChangeInfo.conversation;
            if (zimConversationChangeInfo.event == ZIMConversationEvent.DELETED) {
                zimConversationMap.remove(conversation.conversationID);
            } else if (zimConversationChangeInfo.event == ZIMConversationEvent.ADDED
                || zimConversationChangeInfo.event == ZIMConversationEvent.UPDATED
                || zimConversationChangeInfo.event == ZIMConversationEvent.DISABLED) {
                zimConversationMap.put(conversation.conversationID, conversation);
            }
        });
    }

    public void onConversationTotalUnreadMessageCountUpdated(ZIM zim, int totalUnreadMessageCount) {
        this.totalUnreadMessageCount = totalUnreadMessageCount;
    }

    public void onUserLogout() {
        totalUnreadMessageCount = 0;
        zimConversationMap.clear();
    }
}
