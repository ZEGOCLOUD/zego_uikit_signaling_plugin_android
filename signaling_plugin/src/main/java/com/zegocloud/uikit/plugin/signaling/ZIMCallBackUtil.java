package com.zegocloud.uikit.plugin.signaling;

import im.zego.zim.ZIM;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.enums.ZIMErrorCode;
import java.util.function.BiConsumer;

public class ZIMCallBackUtil {

    public static <T> void checkZIMInstance(BiConsumer<T, ZIMError> consumer, T t) {
        if (t != null) {
            if (ZIM.getInstance() == null) {
                ZIMError errorInfo = new ZIMError();
                errorInfo.code = ZIMErrorCode.NO_INIT;
                errorInfo.message = ZIMErrorCode.NO_INIT.toString();
                consumer.accept(t, errorInfo);
            }
        }
    }
}
