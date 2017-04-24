package com.shenqu.wirelessmbox.tools;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by JongLim on 2016/11/16.
 */

public class WirelessUtils {
    private static final String TAG = "WifiUtils";

    /**
     * 定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况
     */
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    public static WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static String getWifiApIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan0") || intf.getName().contains("eth0") || intf.getName().contains("ap0")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddr = enumIpAddr.nextElement();
                        if (!inetAddr.isLoopbackAddress() && (inetAddr.getAddress().length == 4)) {
                            //JLLog.LOGD(TAG, "WifiApIpAddress = " + inetAddr.getHostAddress());
                            return inetAddr.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            JLLog.LOGE(TAG, ex.toString());
        }
        return null;
    }

    public static InetAddress getInetAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan0") || intf.getName().contains("ap0") || intf.getName().contains("eth0")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddr = enumIpAddr.nextElement();
                        if (!inetAddr.isLoopbackAddress() && (inetAddr.getAddress().length == 4)) {
                            JLLog.LOGD(TAG, "InetAddress = " + inetAddr.getHostAddress());
                            return inetAddr;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            JLLog.LOGE(TAG, ex.toString());
        }
        return null;
    }

    public static InetAddress getBroadcast(InetAddress inetAddr) {
        InetAddress iAddr = null;
        if (inetAddr == null)
            return null;
        try {
            NetworkInterface temp = NetworkInterface.getByInetAddress(inetAddr);
            List<InterfaceAddress> addresses = temp.getInterfaceAddresses();
            if (addresses == null)
                return null;
            for (InterfaceAddress inetAddress : addresses) {
                iAddr = inetAddress.getBroadcast();
                JLLog.LOGD(TAG, "getBroadcast = " + iAddr);
            }
            return iAddr;
        } catch (SocketException e) {
            JLLog.LOGD(TAG, "getBroadcast " + e.getMessage());
        }
        return null;
    }

    public static String getWifiApSSID(WifiManager wm) {
        WifiInfo info = wm.getConnectionInfo();
        if (info != null)
            return info.getSSID();
        else
            return "";
    }

    public static boolean isWifiEnabled(WifiManager wm) {
        return wm.isWifiEnabled();
    }

    /**
     * 开启WiFi模式
     */
    public static void openWifi(WifiManager wm) {
        if (isWifiApEnabled(wm)) {
            closeWifiAp(wm);
        }
        if (!wm.isWifiEnabled())
            wm.setWifiEnabled(true);
    }

    public static void closeWifi(WifiManager wm) {
        if (wm.isWifiEnabled())
            wm.setWifiEnabled(false);
    }

    /**
     * 查看以前是否也配置过这个网络
     */
    public static WifiConfiguration isExsits(WifiManager wm, String SSID) {
        List<WifiConfiguration> existingConfigs = wm.getConfiguredNetworks();
        if (existingConfigs == null)
            return null;
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public static WifiConfiguration createWifiConfig(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + SSID + "\"";
        config.hiddenSSID = false;
        config.wepTxKeyIndex = 0;
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (Type == WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    /**
     * 私有接口：wifi热点是否开启
     */

    public static boolean isWifiApEnabled(WifiManager wm) {
        try {
            Method method = wm.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (boolean) method.invoke(wm);
        } catch (NoSuchMethodException e) {
            JLLog.LOGE(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            JLLog.LOGE(TAG, e.getLocalizedMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 公共接口：关闭WiFi热点
     */
    public static void closeWifiAp(WifiManager wm) {
        if (isWifiApEnabled(wm)) {
            try {
                Method method = wm.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(wm);
                Method method2 = wm.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke(wm, config, false);
            } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 5.0 打开wifiAp
     */
    public static void openWiFiAP(WifiManager wm, String ssid, String password) {
        //wifi 热点打开状态下需要先关闭热点
        if (wm.isWifiEnabled()) {
            wm.setWifiEnabled(false);
        }

        try {
            Class[] arrayOfClass = new Class[2];
            arrayOfClass[0] = WifiConfiguration.class;
            arrayOfClass[1] = Boolean.TYPE;

            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = createWifiApConfig(ssid, password);
            arrayOfObject[1] = true;

            Class<? extends WifiManager> rfClass = wm.getClass();
            Method method = rfClass.getMethod("setWifiApEnabled", arrayOfClass);
            method.invoke(wm, arrayOfObject);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | SecurityException e) {
            e.printStackTrace();
        }
    }

    private static WifiConfiguration createWifiApConfig(String ssid, String password) {
        WifiConfiguration apConfig = new WifiConfiguration();

        apConfig.SSID = ssid;
        apConfig.preSharedKey = password;
        apConfig.hiddenSSID = false;

        apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        apConfig.status = WifiConfiguration.Status.ENABLED;
        return apConfig;
    }

    /**
     * 设置手机飞行模式
     *
     * @param context
     * @param enabling true:设置为飞行模式	false:取消飞行模式
     */
    public static void setAirplaneModeOn(Context context, boolean enabling) {
        Settings.System.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, enabling ? 1 : 0);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enabling);
        context.sendBroadcast(intent);
    }

    /**
     * 判断手机是否是飞行模式
     *
     * @param context
     * @return
     */
    public static boolean getAirplaneMode(Context context) {
        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
        return (isAirplaneMode == 1);
    }

}
