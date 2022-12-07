package com.zegocloud.uikit.plugin.signaling;

import android.util.Log;
import com.zegocloud.uikit.plugin.common.PluginCallbackListener;
import com.zegocloud.uikit.plugin.common.PluginEventListener;
import com.zegocloud.uikit.service.internal.UIKitCore;
import im.zego.zim.ZIM;
import im.zego.zim.callback.ZIMRoomAttributesBatchOperatedCallback;
import im.zego.zim.callback.ZIMRoomAttributesOperatedCallback;
import im.zego.zim.callback.ZIMRoomAttributesQueriedCallback;
import im.zego.zim.entity.ZIMError;
import im.zego.zim.entity.ZIMRoomAttributesBatchOperationConfig;
import im.zego.zim.entity.ZIMRoomAttributesDeleteConfig;
import im.zego.zim.entity.ZIMRoomAttributesSetConfig;
import im.zego.zim.entity.ZIMRoomAttributesUpdateInfo;
import im.zego.zim.enums.ZIMRoomAttributesUpdateAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Room Properties
 */
public class ZegoRoomPropertiesPluginService {

    private PluginEventListener pluginEventListener;

    public void setPluginEventListener(PluginEventListener pluginEventListener) {
        this.pluginEventListener = pluginEventListener;
    }

    /**
     * Set or update room properties.
     *
     * @param roomAttributes
     * @param config
     * @param listener
     */
    public void updateRoomProperty(HashMap<String, String> roomAttributes, ZIMRoomAttributesSetConfig config, PluginCallbackListener listener) {
        String roomID = UIKitCore.getInstance().getRoom().roomID;
        ZIM.getInstance().setRoomAttributes(roomAttributes, roomID, config, new ZIMRoomAttributesOperatedCallback() {
            @Override
            public void onRoomAttributesOperated(String roomID, ArrayList<String> errorKeys, ZIMError errorInfo) {
                if (listener != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", errorInfo.code.value());
                    map.put("message", errorInfo.message);
                    map.put("errorKeys", errorKeys);
                    listener.callback(map);
                }
            }
        });
    }


    /**
     * Batch delete room properties.
     *
     * @param keys
     * @param config
     * @param listener
     */
    public void deleteRoomProperties(List<String> keys, ZIMRoomAttributesDeleteConfig config, PluginCallbackListener listener) {
        String roomID = UIKitCore.getInstance().getRoom().roomID;
        ZIM.getInstance().deleteRoomAttributes(keys, roomID, config, new ZIMRoomAttributesOperatedCallback() {
            @Override
            public void onRoomAttributesOperated(String roomID, ArrayList<String> errorKeys, ZIMError errorInfo) {
                if (listener != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", errorInfo.code.value());
                    map.put("message", errorInfo.message);
                    map.put("errorKeys", errorKeys);
                    listener.callback(map);
                }
            }
        });
    }

    public void beginRoomPropertiesBatchOperation(ZIMRoomAttributesBatchOperationConfig config, PluginCallbackListener listener) {
        String roomID = UIKitCore.getInstance().getRoom().roomID;
        ZIM.getInstance().beginRoomAttributesBatchOperation(roomID, config);
    }

    public void endRoomPropertiesBatchOperation(PluginCallbackListener listener) {
        String roomID = UIKitCore.getInstance().getRoom().roomID;
        ZIM.getInstance().endRoomAttributesBatchOperation(roomID, new ZIMRoomAttributesBatchOperatedCallback() {
            @Override
            public void onRoomAttributesBatchOperated(String roomID, ZIMError errorInfo) {
                if (listener != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", errorInfo.code.value());
                    map.put("message", errorInfo.message);
                    listener.callback(map);
                }
            }
        });
    }

    public void queryRoomProperties(PluginCallbackListener listener) {
        String roomID = UIKitCore.getInstance().getRoom().roomID;
        ZIM.getInstance().queryRoomAllAttributes(roomID, new ZIMRoomAttributesQueriedCallback() {
            @Override
            public void onRoomAttributesQueried(String roomID, HashMap<String, String> roomAttributes, ZIMError errorInfo) {
                if (listener != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", errorInfo.code.value());
                    map.put("message", errorInfo.message);
                    map.put("attributes", roomAttributes);
                    listener.callback(map);
                }
            }
        });
    }

    public void notifyOnRoomPropertiesUpdated(ZIMRoomAttributesUpdateInfo info) {
        Map<String, Object> map = new HashMap<>();
        map.put("isSet", info.action == ZIMRoomAttributesUpdateAction.SET);
        map.put("properties", info.roomAttributes);
        pluginEventListener.onPluginEvent("onRoomPropertiesUpdated", map);
    }

}
