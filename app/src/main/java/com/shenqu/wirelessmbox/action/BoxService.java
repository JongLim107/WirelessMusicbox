package com.shenqu.wirelessmbox.action;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.shenqu.wirelessmbox.tools.JLLog;

/**
 * Created by JongLim on 2016/12/15.
 */

public class BoxService extends Service {
    private static final String TAG = "BoxService";
    private static MyHttpServer mMyHttpServer;

    @Override
    public void onCreate() {
        super.onCreate();
        JLLog.LOGI(TAG, "onCreate()");
        if (mMyHttpServer == null)
            mMyHttpServer = new MyHttpServer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        JLLog.LOGI(TAG, "onDestroy()");
        mMyHttpServer.stop();
        mMyHttpServer = null;
    }
}
