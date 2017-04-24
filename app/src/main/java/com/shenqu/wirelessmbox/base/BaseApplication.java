package com.shenqu.wirelessmbox.base;

import android.app.Activity;
import android.app.Application;

import com.shenqu.wirelessmbox.simplecache.ACache;
import com.shenqu.wirelessmbox.tools.FileUtils;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.tools.JLUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by JongLim on 2016/9/27.
 */
public abstract class BaseApplication extends Application {
    private final static String TAG = "BaseApp";

    /**
     * 标志位程序是否退出
     */
    private static boolean isAppExit = false;

    public static boolean isAppExited() {
        return isAppExit;
    }

    public static void setAppExit(boolean isExit) {
        isAppExit = isExit;
    }


    /**
     * 程序缓存，存储背景图片、数据库等
     */
    private static ACache gACache;
    private static String gACacheDir;

    public static ACache getACache() {
        return gACache;
    }
    public static String getACacheDir() {
        return gACacheDir;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JLLog.LOGV(TAG, "Application onCreate()");

        String path[] = FileUtils.getStorgePath(this);
        if (path != null)
            gACacheDir = path[0] + "/Shenqu";
        else
            gACacheDir = FileUtils.getStorgePath();

        assert gACacheDir != null;

        /**
         * new an ACache object
         */
        File file = new File(gACacheDir);
        gACache = ACache.get(file);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        JLLog.LOGV(TAG, "Application onTerminate()");
    }

    /**
     * ArrayList<Activity>
     */
    private static ArrayList<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity);
        }
    }

    public static void removeActivity(Activity activity) {
        if (activityList.contains(activity)) {
            activityList.remove(activity);
        }
    }

    public static Activity getCurrentActivity() {
        if (activityList != null && !activityList.isEmpty()) {
            return activityList.get(activityList.size() - 1);
        }
        return null;
    }

    public static Activity getTop2Activity() {
        if (activityList.size() >= 2) {
            return activityList.get(activityList.size() - 2);
        }
        return null;
    }

    /**
     * finish掉所有非栈顶的activity
     */
    public static void finishNoTopActivity() {
        if (activityList != null && !activityList.isEmpty()) {
            while (activityList.size() > 1) {
                Activity activity = activityList.get(0);
                activityList.remove(activity);
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
        }

    }

    /**
     * 退出所有的Activity
     */
    public static void finishAllActivity() {
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void exit() {
        isAppExit = true;
        finishAllActivity();
    }

}
