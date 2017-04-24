package com.shenqu.wirelessmbox;

import android.content.Intent;

import com.shenqu.wirelessmbox.action.BoxControler;
import com.shenqu.wirelessmbox.action.BoxService;
import com.shenqu.wirelessmbox.action.MyHttpServer;
import com.shenqu.wirelessmbox.base.BaseApplication;
import com.shenqu.wirelessmbox.bean.MusicBox;
import com.shenqu.wirelessmbox.simplecache.ACache;
import com.shenqu.wirelessmbox.tools.JLLog;

import org.xutils.x;

/**
 * Created by JongLim on 2016/12/13.
 */

public class MyApplication extends BaseApplication {

    /**
     * 程序缓存，存储背景图片、数据库等
     */
    public static String gCacheBmp;

    /**
     * 全局控制器
     */
    private static BoxControler gControler;

    public static BoxControler getControler() {
        return gControler;
    }

    public static void setControler(BoxControler controler) {
        gControler = controler;
    }

    /**
     * 全局盒子 一个盒子对应一个控制器
     */
    private static MusicBox gMusicBox;

    public static MusicBox getMusicBox() {
        return gMusicBox;
    }

    public static void setMusicBox(MusicBox musicBox) {
        gMusicBox = musicBox;
    }


    /**
     * 本程序所需要的权限
     */
    public final static String[] permissions = {
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.CHANGE_NETWORK_STATE",
            "android.permission.CHANGE_WIFI_MULTICAST_STATE",
            "android.permission.CHANGE_WIFI_STATE",
            "android.permission.INTERNET",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WAKE_LOCK",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.WRITE_SETTINGS"};

    @Override
    public void onCreate() {
        super.onCreate();
        gCacheBmp = ACache.getMemoryKey(getACacheDir() + "/cache") + "_";
        /**
         * 喜马拉雅接入，用到 xUtil 库
         * */
        x.Ext.init(this);
    }

    public static void exitApp(){
        exit();
        gControler = null;
        gMusicBox = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
