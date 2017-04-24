package com.shenqu.wirelessmbox.base;

import com.shenqu.wirelessmbox.MyApplication;
import com.ximalaya.ting.android.opensdk.util.Logger;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;
import com.ximalaya.ting.android.sdkdownloader.http.RequestParams;
import com.ximalaya.ting.android.sdkdownloader.http.app.RequestTracker;
import com.ximalaya.ting.android.sdkdownloader.http.request.UriRequest;

/**
 * Created by JongLim on 2017/1/7.
 */

public class XimalayaSDK {

    public XimalayaSDK(MyApplication app) {

        String mp3 = app.getExternalFilesDir("mp3").getAbsolutePath();
        System.out.println("地址是  " + mp3);

        XmDownloadManager.Builder(app)             //喜马拉雅下载模块初始化
                .maxDownloadThread(1)               // 最大的下载个数 默认为1 最大为3
                .maxSpaceSize(Long.MAX_VALUE)       // 设置下载文件占用磁盘空间最大值，单位字节。不设置没有限制
                .connectionTimeOut(15000)           // 下载时连接超时的时间 ,单位毫秒 默认 30000
                .readTimeOut(15000)                 // 下载时读取的超时时间 ,单位毫秒 默认 30000
                .fifo(false)                        // 等待队列的是否优先执行先加入的任务. false表示后添加的先执行(不会改变当前正在下载的音频的状态) 默认为true
                .maxRetryCount(3)                   // 出错时重试的次数 默认2次
                .progressCallBackMaxTimeSpan(1000)  // 进度条progress 更新的频率 默认是800
                .requestTracker(requestTracker)     // 日志 可以打印下载信息
                .savePath(mp3)                      // 保存的地址 会检查这个地址是否有效
                .create();
    }

    private RequestTracker requestTracker = new RequestTracker() {
        @Override
        public void onWaiting(RequestParams params) {
            Logger.log("TingApplication : onWaiting " + params);
        }

        @Override
        public void onStart(RequestParams params) {
            Logger.log("TingApplication : onStart " + params);
        }

        @Override
        public void onRequestCreated(UriRequest request) {
            Logger.log("TingApplication : onRequestCreated " + request);
        }

        @Override
        public void onSuccess(UriRequest request, Object result) {
            Logger.log("TingApplication : onSuccess " + request + "   result = " + result);
        }

        @Override
        public void onRemoved(UriRequest request) {
            Logger.log("TingApplication : onRemoved " + request);
        }

        @Override
        public void onCancelled(UriRequest request) {
            Logger.log("TingApplication : onCanclelled " + request);
        }

        @Override
        public void onError(UriRequest request, Throwable ex, boolean isCallbackError) {
            Logger.log("TingApplication : onError " + request + "   ex = " + ex + "   isCallbackError = " + isCallbackError);
        }

        @Override
        public void onFinished(UriRequest request) {
            Logger.log("TingApplication : onFinished " + request);
        }
    };
}
