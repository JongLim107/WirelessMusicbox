package com.shenqu.wirelessmbox.action;

/**
 * Created by JongLim on 2016/11/25.
 */

public class ActionType {
    public final static int SCAN_PORT = 0x1f1f;
    public final static int ScanDevice = 0x1070;
    public final static int DeviceEventNotify = 0x1070 + 1;
    public final static int SetAVTransportURI = 0x1070 + 2;
    public final static int PlayerDoPlay = 0x1070 + 3;
    public final static int PlayerDoPause = 0x1070 + 4;
    public final static int PlayerDoNext = 0x1070 + 5;
    public final static int PlayerDoPrevious = 0x1070 + 6;
    public final static int PlayerDoSeek = 0x1070 + 7;
    public final static int PlayerSetVolume = 0x1070 + 8;
    public final static int GetPlayerState = 0x1070 + 9;
    public final static int GetPlaylist = 0x1070 + 10;
    public final static int SwitchAudioSource = 0x1070 + 11;
    public final static int GetCurrentAudioSource = 0x1070 + 12;
    public final static int SetupHotKey = 0x1070 + 13;
    public final static int GetUdiskInfo = 0x1070 + 14;
    public final static int GetDeviceBasicConfig = 0x1070 + 15;
    public final static int SetDeviceBasicConfig = 0x1070 + 16;
    public final static int GetNetworkState = 0x1070 + 17;
    public final static int SetNetworkConfig = 0x1070 + 18;
    public final static int WiFiScan = 0x1070 + 19;
    public final static int WiFiStaConnect = 0x1070 + 20;
    public final static int RestartDevice = 0x1070 + 21;
    public final static int RestoreDevice = 0x1070 + 22;
    public final static int UpgradeFirmware = 0x1070 + 23;
}
