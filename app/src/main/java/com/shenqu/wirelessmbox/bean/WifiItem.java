package com.shenqu.wirelessmbox.bean;

import com.shenqu.wirelessmbox.tools.JLJSON;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by JongLim on 2016/12/5.
 */

public class WifiItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String Auth = "";
    private String MAC;
    private String Channel;
    private String Encry;
    private String Rssi;
    private String SSID;

    public WifiItem(String auth, String bssid, String channel, String encry, String rssi, String ssid) {
        Auth = auth;
        MAC = bssid;
        Channel = channel;
        Encry = encry;
        Rssi = rssi;
        SSID = ssid;
    }

    public WifiItem(JSONObject object){
        SSID = JLJSON.getString(object, "SSID");
        MAC = JLJSON.getString(object, "BSSID");
        Rssi = JLJSON.getString(object, "Rssi");
        Channel = JLJSON.getString(object, "Channel");
        Auth = JLJSON.getString(object, "Auth");
        Encry = JLJSON.getString(object, "Encry");
    }

    public String getAuth() {
        return this.Auth;
    }

    public String getMAC() {
        return this.MAC;
    }

    public String getChannel() {
        return this.Channel;
    }

    public String getEncry() {
        return this.Encry;
    }

    public String getRssi() {
        return this.Rssi;
    }

    public String getSsid() {
        return this.SSID;
    }

    public String toString() {
        return "WifiBean [SSID=" + SSID + ", MAC=" + MAC + ", Rssi=" + Rssi + ", Channel=" + Channel + ", Auth=" + Auth + ", Encry=" + Encry + "]";
    }

}
