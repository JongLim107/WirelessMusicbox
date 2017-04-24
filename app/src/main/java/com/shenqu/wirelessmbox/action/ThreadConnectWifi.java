package com.shenqu.wirelessmbox.action;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.tools.WirelessUtils;

import org.apache.http.impl.conn.Wire;

/**
 * Created by JongLim on 2016/12/7.
 */

public class ThreadConnectWifi extends Thread {
    private static WifiManager mWifiManager;
    private String mSsid, mPassword;
    private WirelessUtils.WifiCipherType wifiType;

    public ThreadConnectWifi(WifiManager wm, String ssid, String pwd, String auth) {
        mWifiManager = wm;
        mSsid = ssid;
        mPassword = pwd;
        if (auth.contains("WEP"))
            wifiType = WirelessUtils.WifiCipherType.WIFICIPHER_WEP;
        else if (auth.contains("WPA"))
            wifiType = WirelessUtils.WifiCipherType.WIFICIPHER_WPA;
        else if (auth.equals("OPEN"))
            wifiType = WirelessUtils.WifiCipherType.WIFICIPHER_NOPASS;
    }

    @Override
    public void run() {
        WirelessUtils.closeWifiAp(mWifiManager);
        // 打开wifi
        WirelessUtils.openWifi(mWifiManager);
        // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (true) {
            try {
                // 为了避免程序一直while循环，让它睡个200毫秒检测……
                Thread.sleep(200);
                if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
                    break;
                else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING)
                    return;
            } catch (InterruptedException ie) {
                System.out.print(ie.getLocalizedMessage());
            }
        }

        //先删除该 ssid 网络
        WifiConfiguration tempConfig = WirelessUtils.isExsits(mWifiManager, mSsid);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        //重新配置此 ssid 信息，并将其加入列表
        WifiConfiguration wifiConfig = WirelessUtils.createWifiConfig(mSsid, mPassword, wifiType);

        int netID = mWifiManager.addNetwork(wifiConfig);
        boolean b = mWifiManager.enableNetwork(netID, true);
        boolean d = mWifiManager.reconnect();
        JLLog.LOGV("ConnectThread", "enableNetwork:" + b + ", wm.reconnect:" + d);

        WifiInfo info = mWifiManager.getConnectionInfo();
        JLLog.LOGV("ConnectThread", "ipaddr: " + WirelessUtils.getWifiApIpAddress());
        JLLog.LOGV("ConnectThread", "SSID: " + info.getSSID());
    }

}
