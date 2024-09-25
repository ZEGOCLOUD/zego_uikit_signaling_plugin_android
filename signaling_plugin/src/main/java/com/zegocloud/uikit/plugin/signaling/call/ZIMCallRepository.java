package com.zegocloud.uikit.plugin.signaling.call;

import androidx.annotation.Nullable;
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
import im.zego.zim.entity.ZIMCallUserInfo;
import im.zego.zim.entity.ZIMCallUserStateChangeInfo;
import im.zego.zim.entity.ZIMCallingInvitationSentInfo;
import im.zego.zim.entity.ZIMCallingInviteConfig;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMUserInfo;
import im.zego.zim.enums.ZIMCallState;
import im.zego.zim.enums.ZIMCallUserState;
import im.zego.zim.enums.ZIMErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * cache call data,and no special business logic,
 * <br>
 * for example,no logic for check userList and make call invalid,
 *
 * <br>
 * call removed by endcall,quitcall or external remove
 */
public class ZIMCallRepository {

    private ArrayList<ZIMCallInfo> zimCallInfoList = new ArrayList<>();
    private ZIMUserRepository userRepository;

    public ZIMCallRepository(ZIMUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void callInvite(List<String> invitees, ZIMCallInviteConfig config, ZIMCallInvitationSentCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onCallInvitationSent(null, null, errorInfo);
            }
            return;
        }

        ZIM.getInstance().callInvite(invitees, config, new ZIMCallInvitationSentCallback() {

            public void onCallInvitationSent(String callID, ZIMCallInvitationSentInfo info, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    ZIMCallInfo callInfo = new ZIMCallInfo();
                    callInfo.callID = callID;
                    callInfo.extendedData = config.extendedData;
                    callInfo.mode = config.mode;
                    callInfo.state = ZIMCallState.STARTED;
                    // if not login,cannot success,so userInfo not null here.
                    callInfo.caller = userRepository.getUserInfo().userID;
                    callInfo.inviter = userRepository.getUserInfo().userID;
                    callInfo.callUserList = new ArrayList<>();
                    addCallInfo(callInfo);
                }
                if (callback != null) {
                    callback.onCallInvitationSent(callID, info, errorInfo);
                }
            }
        });
    }

    public void callCancel(List<String> invitees, String callID, ZIMCallCancelConfig config,
        ZIMCallCancelSentCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onCallCancelSent(null, null, errorInfo);
            }
        }

        Optional<ZIMCallInfo> first = zimCallInfoList.stream()
            .filter(zimCallInfo -> Objects.equals(zimCallInfo.callID, callID)).findFirst();
        if (!first.isPresent()) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.CALL_ERROR;
                errorInfo.message = ZIMErrorCode.CALL_ERROR.toString();
                callback.onCallCancelSent(callID, new ArrayList<>(invitees), errorInfo);
            }
            return;
        }

        ZIM.getInstance().callCancel(invitees, callID, config, new ZIMCallCancelSentCallback() {
            @Override
            public void onCallCancelSent(String callID, ArrayList<String> errorInvitees, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    first.get().state = ZIMCallState.ENDED;
                }
                if (callback != null) {
                    callback.onCallCancelSent(callID, errorInvitees, errorInfo);
                }
            }
        });
    }

    public void callAccept(String callID, ZIMCallAcceptConfig config, ZIMCallAcceptanceSentCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onCallAcceptanceSent(null, errorInfo);
            }
        }

        Optional<ZIMCallInfo> first = zimCallInfoList.stream()
            .filter(zimCallInfo -> Objects.equals(zimCallInfo.callID, callID)).findFirst();
        if (!first.isPresent()) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.CALL_ERROR;
                errorInfo.message = ZIMErrorCode.CALL_ERROR.toString();
                callback.onCallAcceptanceSent(callID, errorInfo);
            }
            return;
        }

        ZIM.getInstance().callAccept(callID, config, new ZIMCallAcceptanceSentCallback() {
            @Override
            public void onCallAcceptanceSent(String callID, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    first.get().state = ZIMCallState.ENDED;
                }
                if (callback != null) {
                    callback.onCallAcceptanceSent(callID, errorInfo);
                }
            }
        });
    }

    public void callReject(String callID, ZIMCallRejectConfig config, ZIMCallRejectionSentCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onCallRejectionSent(null, errorInfo);
            }
        }

        Optional<ZIMCallInfo> first = zimCallInfoList.stream()
            .filter(zimCallInfo -> Objects.equals(zimCallInfo.callID, callID)).findFirst();
        if (!first.isPresent()) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.CALL_ERROR;
                errorInfo.message = ZIMErrorCode.CALL_ERROR.toString();
                callback.onCallRejectionSent(callID, errorInfo);
            }
            return;
        }

        ZIM.getInstance().callReject(callID, config, new ZIMCallRejectionSentCallback() {
            @Override
            public void onCallRejectionSent(String callID, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    ZIMCallInfo findCall = first.get();
                    findCall.state = ZIMCallState.ENDED;
                }

                if (callback != null) {
                    callback.onCallRejectionSent(callID, errorInfo);
                }
            }
        });
    }

    public void callJoin(String callID, ZIMCallJoinConfig config, ZIMCallJoinSentCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onCallJoinSent(null, null, errorInfo);
            }
        }

        Optional<ZIMCallInfo> first = zimCallInfoList.stream()
            .filter(zimCallInfo -> Objects.equals(zimCallInfo.callID, callID)).findFirst();
        if (!first.isPresent()) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.CALL_ERROR;
                errorInfo.message = ZIMErrorCode.CALL_ERROR.toString();
                callback.onCallJoinSent(callID, null, errorInfo);
            }
            return;
        }

        ZIM.getInstance().callJoin(callID, config, new ZIMCallJoinSentCallback() {
            @Override
            public void onCallJoinSent(String callID, ZIMCallJoinSentInfo info, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    ZIMCallInfo zimCallInfo = new ZIMCallInfo();
                    zimCallInfo.callID = callID;
                    zimCallInfo.callUserList = info.callUserList;
                    zimCallInfo.createTime = info.createTime;
                    addCallInfo(zimCallInfo);
                }

                if (callback != null) {
                    callback.onCallJoinSent(callID, info, errorInfo);
                }
            }
        });
    }

    public void callQuit(String callID, ZIMCallQuitConfig config, ZIMCallQuitSentCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onCallQuitSent(null, null, errorInfo);
            }
        }

        Optional<ZIMCallInfo> first = zimCallInfoList.stream()
            .filter(zimCallInfo -> Objects.equals(zimCallInfo.callID, callID)).findFirst();
        if (!first.isPresent()) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.CALL_ERROR;
                errorInfo.message = ZIMErrorCode.CALL_ERROR.toString();
                callback.onCallQuitSent(callID, null, errorInfo);
            }
            return;
        }

        ZIM.getInstance().callQuit(callID, config, new ZIMCallQuitSentCallback() {
            @Override
            public void onCallQuitSent(String callID, ZIMCallQuitSentInfo info, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    ZIMCallInfo zimCallInfo = first.get();
                    zimCallInfo.state = ZIMCallState.ENDED;
                }

                if (callback != null) {
                    callback.onCallQuitSent(callID, info, errorInfo);
                }
            }
        });
    }

    public void callEnd(String callID, ZIMCallEndConfig config, ZIMCallEndSentCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onCallEndSent(null, null, errorInfo);
            }
        }

        Optional<ZIMCallInfo> first = zimCallInfoList.stream()
            .filter(zimCallInfo -> Objects.equals(zimCallInfo.callID, callID)).findFirst();
        if (!first.isPresent()) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.CALL_ERROR;
                errorInfo.message = ZIMErrorCode.CALL_ERROR.toString();
                callback.onCallEndSent(callID, null, errorInfo);
            }
            return;
        }

        ZIM.getInstance().callEnd(callID, config, new ZIMCallEndSentCallback() {
            @Override
            public void onCallEndSent(String callID, ZIMCallEndedSentInfo info, ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    ZIMCallInfo zimCallInfo = first.get();
                    zimCallInfo.state = ZIMCallState.ENDED;
                }

                if (callback != null) {
                    callback.onCallEndSent(callID, info, errorInfo);
                }
            }
        });
    }

    public void callingInvite(List<String> invitees, String callID, ZIMCallingInviteConfig config,
        ZIMCallingInvitationSentCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onCallingInvitationSent(null, null, errorInfo);
            }
        }

        Optional<ZIMCallInfo> first = zimCallInfoList.stream()
            .filter(zimCallInfo -> Objects.equals(zimCallInfo.callID, callID)).findFirst();
        if (!first.isPresent()) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.CALL_ERROR;
                errorInfo.message = ZIMErrorCode.CALL_ERROR.toString();
                callback.onCallingInvitationSent(callID, null, errorInfo);
            }
            return;
        }

        ZIM.getInstance().callingInvite(invitees, callID, config, new ZIMCallingInvitationSentCallback() {
            @Override
            public void onCallingInvitationSent(String callID, ZIMCallingInvitationSentInfo info, ZIMError errorInfo) {
                ZIMCallInfo findCall = first.get();

                if (callback != null) {
                    callback.onCallingInvitationSent(callID, info, errorInfo);
                }
            }
        });
    }

    public void queryCallInvitationList(ZIMCallInvitationQueryConfig config, ZIMCallInvitationListQueriedCallback callback) {
        if (ZIM.getInstance() == null) {
            if (callback != null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                callback.onCallInvitationListQueried(null, -1, errorInfo);
            }
        }
        ZIM.getInstance().queryCallInvitationList(config, new ZIMCallInvitationListQueriedCallback() {
            @Override
            public void onCallInvitationListQueried(ArrayList<ZIMCallInfo> callList, long nextFlag,
                ZIMError errorInfo) {
                if (errorInfo.code == ZIMErrorCode.SUCCESS) {
                    ZIMCallRepository.this.zimCallInfoList = new ArrayList<>(callList);
                }
                if (callback != null) {
                    callback.onCallInvitationListQueried(callList, nextFlag, errorInfo);
                }
            }
        });
    }


    public void onCallInvitationCreated(ZIM zim, ZIMCallInvitationCreatedInfo info, String callID) {
        // find the created callInfo in call-invite methods,and update
        ZIMCallInfo zimCallInfo = getZIMCallInfo(callID);
        if (zimCallInfo != null) {
            // normal,find target,and update
            zimCallInfo.callUserList = info.callUserList;
            zimCallInfo.createTime = info.createTime;
            zimCallInfo.caller = info.caller;
            zimCallInfo.extendedData = info.extendedData;
        } else {
            // exception
            zimCallInfo = ZIMCallRepUtils.convertToZIMCallInfo(info, callID);
        }
    }

    @Nullable
    public ZIMCallInfo getZIMCallInfo(String callID) {
        Optional<ZIMCallInfo> first = zimCallInfoList.stream()
            .filter(zimCallInfo -> (Objects.equals(zimCallInfo.callID, callID))).findFirst();
        return first.orElse(null);
    }

    private void addCallInfo(ZIMCallInfo zimCallInfo) {
        zimCallInfoList.add(zimCallInfo);
    }

    private void removeCallInfo(ZIMCallInfo zimCallInfo) {
        zimCallInfoList.remove(zimCallInfo);
    }


    public void onCallInvitationReceived(ZIM zim, ZIMCallInvitationReceivedInfo info, String callID) {
        addCallInfo(ZIMCallRepUtils.convertToZIMCallInfo(info, callID));
    }

    // user was invited,but was canceled then.
    // also can process in onCallUserStateChanged
    public void onCallInvitationCancelled(ZIM zim, ZIMCallInvitationCancelledInfo info, String callID) {
        //        ZIMCallInfo zimCallInfo = getZIMCallInfo(callID);
        //        removeCallInfo(zimCallInfo);
    }

    public void onCallInvitationEnded(ZIM zim, ZIMCallInvitationEndedInfo info, String callID) {
        ZIMCallInfo zimCallInfo = getZIMCallInfo(callID);
        if (zimCallInfo != null) {
            zimCallInfo.endTime = info.endTime;
            zimCallInfo.state = ZIMCallState.ENDED;
        }
        //        removeCallInfo(zimCallInfo);
    }

    // user was invited,but no response.
    public void onCallInvitationTimeout(ZIM zim, ZIMCallInvitationTimeoutInfo info, String callID) {
        //        ZIMCallInfo zimCallInfo = getZIMCallInfo(callID);
        //        removeCallInfo(zimCallInfo);
    }

    public void onCallUserStateChanged(ZIM zim, ZIMCallUserStateChangeInfo info, String callID) {
        ZIMCallInfo zimCallInfo = getZIMCallInfo(callID);
        if (zimCallInfo != null) {
            for (ZIMCallUserInfo changeUser : info.callUserList) {
                Optional<ZIMCallUserInfo> first = zimCallInfo.callUserList.stream()
                    .filter(zimCallUserInfo -> (Objects.equals(zimCallUserInfo.userID, changeUser.userID))).findFirst();
                if (first.isPresent()) {
                    first.get().state = changeUser.state;
                    first.get().extendedData = changeUser.extendedData;
                } else {
                    zimCallInfo.callUserList.add(changeUser);
                }
            }
            //            checkAllUserState(zimCallInfo);
        }
    }

    private void checkAllUserState(ZIMCallInfo zimCallInfo) {
        List<ZIMCallUserInfo> callUserList = new ArrayList<>(zimCallInfo.callUserList);

        long activeUserCount = callUserList.stream().filter(new Predicate<ZIMCallUserInfo>() {
            @Override
            public boolean test(ZIMCallUserInfo zimCallUserInfo) {
                boolean notSelf = false;
                ZIMUserInfo userInfo = userRepository.getUserInfo();
                if (userInfo != null) {
                    notSelf = !Objects.equals(zimCallUserInfo.userID, userInfo.userID);
                }
                boolean activeUser = (zimCallUserInfo.state == ZIMCallUserState.INVITING
                    || zimCallUserInfo.state == ZIMCallUserState.ACCEPTED
                    || zimCallUserInfo.state == ZIMCallUserState.RECEIVED);

                return notSelf && activeUser;
            }
        }).count();
        if (activeUserCount == 0) {
            // no active users
            removeCallInfo(zimCallInfo);
        }
    }

    @Deprecated
    public void onCallInvitationRejected(ZIM zim, ZIMCallInvitationRejectedInfo info, String callID) {
    }


    @Deprecated
    public void onCallInvitationAccepted(ZIM zim, ZIMCallInvitationAcceptedInfo info, String callID) {
    }

    @Deprecated
    public void onCallInviteesAnsweredTimeout(ZIM zim, ArrayList<String> invitees, String callID) {
    }
}
