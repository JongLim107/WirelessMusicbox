package com.shenqu.wirelessmbox.action;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.shenqu.wirelessmbox.MyApplication;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.tools.WirelessUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by JongLim on 2016/11/21.
 */

public class ThreadBroadcast extends Thread {
    private final static String TAG = "Broadcast";
    private Context mContext;
    private Handler mHandler;
    private int iTimes;
    private int iPort;
    public boolean isExited = false;
    private byte[] data;
    private DatagramSocket socket = null;
    private String mMyIPAddr;

    public ThreadBroadcast(Context context, int port, String dat, int times, Handler handler) {
        mContext = context;
        iPort = port;
        data = dat.getBytes();
        iTimes = times;
        mHandler = handler;
        mMyIPAddr = WirelessUtils.getWifiApIpAddress();
    }

    @Override
    public void run() {
        super.run();
        sendPacket();
        if (mHandler.getLooper().getThread().isAlive())
            mHandler.sendEmptyMessage(0);
    }

    private void sendPacket() {
        try {
            //InetAddress inet = WifiUtils.getBroadcastAddress(mContext);
            InetAddress inet = WirelessUtils.getBroadcast(WirelessUtils.getInetAddress());
            if (inet == null) {
                mHandler.sendEmptyMessage(0);
                return;
            }
            socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.setSoTimeout(2000);
            socket.bind(new InetSocketAddress(iPort));
            for (int i = 0; i < iTimes && !isExited && !MyApplication.isAppExited(); i++) {
                JLLog.LOGI(TAG, new String(data) + " to " + inet.getHostAddress() + ":" + iPort);
                socket.send(new DatagramPacket(data, data.length, inet, iPort));
                recvPacket();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recvPacket() {
        byte[] recvBuf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
        while (!isExited && !MyApplication.isAppExited()) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                //e.printStackTrace();
                return;
            }

            if (!packet.getAddress().getHostAddress().equalsIgnoreCase(mMyIPAddr)) {
                String data = new String(packet.getData()).trim();
                JLLog.LOGI(TAG, "Received from: " + packet.getAddress().getHostAddress());
                if (mHandler.getLooper().getThread().isAlive()) {
                    Bundle b = new Bundle();
                    b.putSerializable("JSONDATA", data);
                    Message msg = new Message();
                    msg.setData(b);
                    msg.what = ActionType.SCAN_PORT;
                    mHandler.sendMessage(msg);
                }
            }
        }
    }

}
