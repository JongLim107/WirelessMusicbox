package com.shenqu.wirelessmbox.bean;

import com.shenqu.wirelessmbox.tools.JLJSON;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JongLim on 2016/11/22.
 */

public class MusicBox {

    private String mAddr;
    private String mFiremwareVer;
    private String mHttpApiPort;
    private String mName;
    private String mMacAddr;

    public MusicBox(String data) throws JSONException {
        JSONObject j = new JSONObject(data);
        j = j.getJSONObject("Body");

        mAddr = JLJSON.getString(j, "DeviceIpAddr");
        mFiremwareVer = JLJSON.getString(j, "DeviceFiremwareVersion");
        mHttpApiPort = JLJSON.getString(j, "HttpApiPort");
        mName = JLJSON.getString(j, "DeviceName");
        mMacAddr = JLJSON.getString(j, "DeviceMacAddr");
    }

    public String getAddr() {
        return mAddr;
    }

    public void setAddr(String addr) {
        mAddr = addr;
    }

    public String getFiremwareVer() {
        return mFiremwareVer;
    }

    public void setFiremwareVer(String firemwareVer) {
        mFiremwareVer = firemwareVer;
    }

    public String getHttpApiPort() {
        return mHttpApiPort;
    }

    public void setHttpApiPort(String httpApiPort) {
        mHttpApiPort = httpApiPort;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getMacAddr() {
        return mMacAddr;
    }

    public void setMacAddr(String macAddr) {
        mMacAddr = macAddr;
    }

}
