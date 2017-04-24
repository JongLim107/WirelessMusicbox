package com.shenqu.wirelessmbox.action;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.shenqu.wirelessmbox.bean.MusicBox;
import com.shenqu.wirelessmbox.bean.MusicBoxState;
import com.shenqu.wirelessmbox.tools.JLJSON;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.tools.WirelessUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by JongLim on 2016/11/24.
 */

public class BoxControler {
    private final static String TAG = "BoxControler";

    private boolean isControling;
    private final Object mCtrlLock;

    private Handler mHandler;
    private int iActionType;
    private String mHttpAPIUrl;
    private HashMap<String, String> mStatePairs;
    private HashMap<String, String> mCtrlPairs;

    /**
     * Sync Box Play State
     */
    private boolean isSyncing;
    private MusicBoxState mPlayerState;

    public interface OnBoxPlayStateListener {
        void onStateChanged(MusicBoxState state);
    }

    private OnBoxPlayStateListener mStateListener;

    public void setStateListener(OnBoxPlayStateListener stateListener) {
        mStateListener = stateListener;
    }

    /**
     * 构造函数
     */
    public BoxControler(MusicBox box, Handler handler, OnBoxPlayStateListener listener) {
        isControling = true;
        mHandler = handler;

        mHttpAPIUrl = getApiAddr(box);
        mCtrlPairs = new HashMap<String, String>();
        mCtrlLock = new Object();
        new Thread(mCtrlRunnable).start();

        /**
         * 维护MusicBox的播放状态的线程
         * */
        mStateListener = listener;
        mStatePairs = new HashMap<String, String>();
        new Thread(mStateRunnable).start();
    }

    public MusicBoxState getPlayState() {
        return mPlayerState;
    }

    public void setControling(boolean looping) {
        isControling = looping;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void setSyncing(boolean syncing) {
        isSyncing = syncing;
    }

    private String getApiAddr(MusicBox box) {
        if (box == null) {
            JLLog.LOGD(TAG, "Get box(null) address failed.");
            return null;
        }
        return "http://" + box.getAddr() + ":" + box.getHttpApiPort() + "/httpapi.html";
    }

    /**
     * Tell the box to connect to a new wifi
     */
    public void connetToWifi(String ssid, String pwd) {
        synchronized (mCtrlLock) {
            iActionType = ActionType.WiFiStaConnect;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"WiFiStaConnect\",\"Body\":{\"WiFiStaSSID\":\"" + ssid + "\",\"WiFiStaKey\":\"" + pwd + "\"}}");
            mCtrlLock.notifyAll();
        }
    }

    /**
     * 获取设备基本信息
     */
    public void getDeviceBasicConfig() {
        synchronized (mCtrlLock) {
            iActionType = ActionType.GetDeviceBasicConfig;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"GetDeviceBasicConfig\"}");
            mCtrlLock.notifyAll();
        }
    }

    /**
     * 获取设备网络信息
     */
    public void getNetworkState() {
        synchronized (mCtrlLock) {
            iActionType = ActionType.GetNetworkState;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"GetNetworkState\"}");
            mCtrlLock.notifyAll();
        }
    }

    /**
     * Get MusicBox play state
     *
     * @param listType 0:  获取当前播放列表
     *                 1:  获取 U 盘扫描的播放列表
     *                 2:  获取当前的定时器播放列表
     *                 3:  获取 Hotkey1 播放列表
     *                 ...
     *                 8:  获取 Hotkey6 播放列表
     */
    public void getPlayList(final Handler handler, final int listType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCtrlPairs.clear();
                mCtrlPairs.put("CMD", "HTTPAPI");
                mCtrlPairs.put("JSONREQ", "{\"Req\":\"GetPlaylist\",\"Body\":{\"Type\":" + listType + "}}");

                String retStr = MyHttpClient.post(mHttpAPIUrl, mCtrlPairs);

                if (mHandler.getLooper().getThread().isAlive()) {
                    Bundle b = new Bundle();
                    b.putString("JSONDATA", retStr);
                    Message msg = new Message();
                    msg.setData(b);
                    msg.what = ActionType.GetPlaylist;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    /**
     * 获取设备U盘信息
     */
    public void getUdiskInfo() {
        synchronized (mCtrlLock) {
            iActionType = ActionType.GetUdiskInfo;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"GetUdiskInfo\"}");
            mCtrlLock.notifyAll();
        }
    }

    /**
     * 获取设备上的WiFi列表
     */
    public void getWifiList() {
        synchronized (mCtrlLock) {
            iActionType = ActionType.WiFiScan;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"WiFiScan\"}");
            mCtrlLock.notifyAll();
        }
    }

    public void restartBox() {
        synchronized (mCtrlLock) {
            iActionType = ActionType.RestartDevice;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"RestartDevice\"}");
            mCtrlLock.notifyAll();
        }
    }

    public void restoreBox() {
        synchronized (mCtrlLock) {
            iActionType = ActionType.RestoreDevice;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"RestoreDeviceFactorySettings\"}");
            mCtrlLock.notifyAll();
        }
    }

    /**
     * Tell the box to connect to a new hotspot
     *
     * @param EthMode     “0”:禁用，如果音箱没有以太网口，关闭以太网省电；
     *                    “1”:LAN 口，PC 可以通过网线连接到音箱;
     *                    “2”:WAN 口，通过网线连接到路由器的 LAN口。
     * @param NetworkMode 0/1
     * @param WanMode     DHCP/STATIC
     * @param WlanHotspot ON/OFF
     */
    public void setNetworkConfig(int EthMode, int NetworkMode, String WanMode, String WlanHotspot) {
        synchronized (mCtrlLock) {
            iActionType = ActionType.SetNetworkConfig;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"SetNetworkConfig\",\"Body\":{\"NetworkMode\":\"" + NetworkMode + "\",\"EthMode\":\"" + EthMode +
                    "\",\"WlanHotspot\":\"" + WlanHotspot + "\",\"WanMode\":\"" + WanMode + "\"}}");
            mCtrlLock.notifyAll();
        }
    }

    public void setPlayURI(String path) {
        synchronized (mCtrlLock) {
            iActionType = ActionType.SetAVTransportURI;
            String uri;
            if (path.startsWith("http://"))
                uri= path;
            else
                uri= "http://" + WirelessUtils.getWifiApIpAddress() + ":" + MyHttpServer.LISTEN_PORT + path;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"SetAVTransportURI\",\"Body\":{\"AVTransportURI\":\"" + uri + "\"}}");
            mCtrlLock.notifyAll();
        }
    }

    public void setBasicConfig(String name, String pwd) {
        synchronized (mCtrlLock) {
            iActionType = ActionType.SetDeviceBasicConfig;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"SetDeviceBasicConfig\",\"Body\":{\"DeviceName\":\"" + name + "\"," + "\"AccessPassword\":\"" + pwd +
                    "\"}}");
            mCtrlLock.notifyAll();
        }
    }

    /**
     * @return true if the box begin to play
     */
    public void startPlay(boolean is) {
        synchronized (mCtrlLock) {
            if (is) {
                iActionType = ActionType.PlayerDoPlay;
                mCtrlPairs.clear();
                mCtrlPairs.put("CMD", "HTTPAPI");
                mCtrlPairs.put("JSONREQ", "{\"Req\":\"PlayerDoPlay\"}");
            } else {
                iActionType = ActionType.PlayerDoPause;
                mCtrlPairs.clear();
                mCtrlPairs.put("CMD", "HTTPAPI");
                mCtrlPairs.put("JSONREQ", "{\"Req\":\"PlayerDoPause\"}");
            }
            mCtrlLock.notifyAll();
        }
    }

    /**
     * 上/下曲
     *
     * @param index the index of songs list
     * @return
     */
    public void seekTo(int index, int dif) {
        if (dif != 0)
            index = mPlayerState.CurrentTrack + dif;
        if (index < 0 || mPlayerState.NumberOfTracks < index)
            return;
        synchronized (mCtrlLock) {
            iActionType = ActionType.PlayerDoNext;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"PlayerDoSeek\",\"Body\":{\"Unit\":\"TRACK_NR\",\"Target\":\"" + index + "\"}}");
            mCtrlLock.notifyAll();
        }
    }

    /**
     * 前进/后退
     *
     * @param msec milliseconds from the start
     * @return
     */
    public void seekTo(final String msec) throws JSONException {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        synchronized (mCtrlLock) {
            iActionType = ActionType.PlayerDoSeek;

            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"PlayerDoSeek\",\"Body\":{\"Unit\":\"REL_TIME\",\"Target\":\"" + msec + "\"}}");

            mCtrlLock.notifyAll();
        }
//                MyHttpClient.post(mHttpAPIUrl, mCtrlPairs);
//            }
//        }).start();
    }

    /**
     * 设置播放器音量
     *
     * @param volume 0 to 100
     * @return
     */
    public void setVolume(int volume) throws JSONException {
        synchronized (mCtrlLock) {
            iActionType = ActionType.PlayerSetVolume;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"PlayerSetVolume\",\"Body\":{\"DesiredVolume\":\"" + volume + "\"}}");
            mCtrlLock.notifyAll();
        }
    }

    /**
     * 音响固件升级
     */
    public void setUpgradeFirmware(String path) {
        synchronized (mCtrlLock) {
            iActionType = ActionType.UpgradeFirmware;
            String uri = "http://" + WirelessUtils.getWifiApIpAddress() + ":" + MyHttpServer.LISTEN_PORT + path;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"UpgradeFirmware\",\"Body\":{\"UpgradeUrl\":\"" + uri + "\"}}");
            mCtrlLock.notifyAll();
        }
    }

    public void getUpgadeStatus() {
        synchronized (mCtrlLock) {
            iActionType = ActionType.UpgradeFirmware;
            mCtrlPairs.clear();
            mCtrlPairs.put("CMD", "HTTPAPI");
            mCtrlPairs.put("JSONREQ", "{\"Req\":\"UpgradeFirmware\"}");
            mCtrlLock.notifyAll();
        }
    }

    /**
     * 监控盒子播放状态
     */
    private Runnable mStateRunnable = new Runnable() {
        @Override
        public void run() {
            isSyncing = true;
            mPlayerState = new MusicBoxState();
            mStatePairs.clear();
            mStatePairs.put("CMD", "HTTPAPI");
            mStatePairs.put("JSONREQ", "{\"Req\":\"GetPlayerState\",\"Body\":{\"Variables\":\"AVTransportURI CurrentTrackURI TransportState " +
                    "CurrentTrackDuration NumberOfTracks CurrentTrack RelativeTimePosition CurrentVolume AudioSource\"}}");
            while (isControling) {
                try {
                    if (isSyncing) {
                        String retStr = MyHttpClient.post(mHttpAPIUrl, mStatePairs);
                        //JLLog.LOGD(TAG, "Post ret: " + retStr);
                        if (retStr != null) {
                            JSONObject jobj = new JSONObject(retStr);
                            if (JLJSON.getInt(jobj, "Result") == 0) {
                                mPlayerState.initFromJSON(jobj.getJSONObject("Body"));
                                if (mStateListener != null)
                                    mStateListener.onStateChanged(mPlayerState);
                            }
                        }
                    }
                    // 2秒钟更新一次状态
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    JLLog.LOGE(TAG, "StateRunnable sleep InterruptedException" + e.getMessage());
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 控制 盒子 的实现
     */
    private Runnable mCtrlRunnable = new Runnable() {
        @Override
        public void run() {
            String retStr = "";
            while (isControling) {
                synchronized (mCtrlLock) {
                    try {
                        mCtrlLock.wait();
                        retStr = MyHttpClient.post(mHttpAPIUrl, mCtrlPairs);
                        JLLog.LOGD(TAG, "Post ret: " + retStr);
                    } catch (InterruptedException e) {
                        JLLog.LOGE(TAG, e.getLocalizedMessage());
                    } finally {
                        if (mHandler.getLooper().getThread().isAlive()) {
                            Bundle b = new Bundle();
                            b.putString("JSONDATA", retStr);
                            Message msg = new Message();
                            msg.setData(b);
                            msg.what = iActionType;
                            mHandler.sendMessage(msg);
                        }
                    }
                }
            }
        }
    };
}
