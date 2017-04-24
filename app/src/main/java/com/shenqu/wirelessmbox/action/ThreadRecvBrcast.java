package com.shenqu.wirelessmbox.action;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.shenqu.wirelessmbox.MyApplication;
import com.shenqu.wirelessmbox.tools.JLLog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Created by JongLim on 2016/11/21.
 * 监视设备状态更新事件
 */

public class ThreadRecvBrcast extends Thread {
    private final static String TAG = "RecvBrcast";

    private Handler mHandler;
    private int iPort;
    private boolean isExited = false;

    public ThreadRecvBrcast(int port, Handler handler) {
        iPort = port;
        mHandler = handler;
    }

    @Override
    public void run() {
        super.run();
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(iPort));
        } catch (IOException e) {
            return;
        }
        byte[] recvBuf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
        while (!isExited && !MyApplication.isAppExited()) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                continue;
            }

            String data = new String(packet.getData()).trim();
            JLLog.LOGI(TAG, "Received from: " + packet.getAddress().getHostAddress() + ", " + data);
            Bundle b = new Bundle();
            b.putString("JSONDATA", data);
            Message msg = new Message();
            msg.what = iPort;
            msg.setData(b);
            mHandler.sendMessage(msg);
        }
        socket.close();
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void setExited(boolean exited) {
        isExited = exited;
    }

}
