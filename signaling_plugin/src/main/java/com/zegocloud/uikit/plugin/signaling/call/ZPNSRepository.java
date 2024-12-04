package com.zegocloud.uikit.plugin.signaling.call;

import android.app.Application;
import com.zegocloud.uikit.plugin.signaling.user.ZIMUserRepository;
import im.zego.zpns.ZPNsManager;
import im.zego.zpns.util.ZPNsConfig;

public class ZPNSRepository {

    private ZIMUserRepository userRepository;
    private ZPNsConfig zpnsConfig = new ZPNsConfig();
    private Application application;
    private boolean notifyWhenAppRunningInBackgroundOrQuit;

    public ZPNSRepository(ZIMUserRepository userRepository) {
        this.userRepository = userRepository;
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

    public void setApplication(Application application) {
        this.application = application;
    }
}
