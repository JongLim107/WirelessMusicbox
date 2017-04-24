package com.shenqu.wirelessmbox.tools;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by JongLim on 2016/4/9.
 */
public class JLLog {
    private final static String TAG = "JLLOG";

    /**
     * 自定义Toast显示方式，重用已有Toast，这样当有多个Toast持续显示时不会轮番new 只替换text即可
     */
    private static Toast toast = null;

    public static void showToast(Context context, int rsId) {
        if (toast == null) {
            toast = Toast.makeText(context, rsId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(rsId);
        }
        toast.show();
    }

    public static void showToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }

    public static void LOGI(String tag, String msg) {
        Log.i(tag, TAG + "\t" + msg);
    }

    public static void LOGD(String tag, String msg) {
        if (Build.BRAND.equalsIgnoreCase("HUAWEI"))
            Log.i(tag, TAG + "\t" + msg);
        else
            Log.d(tag, TAG + "\t" + msg);
    }

    public static void LOGW(String tag, String msg) {
        Log.w(tag, TAG + "\t" + msg);
    }

    public static void LOGV(String tag, String msg) {
        if (Build.BRAND.equalsIgnoreCase("HUAWEI"))
            Log.i(tag, TAG + "\t" + msg);
        else
            Log.v(tag, TAG + "\t" + msg);
    }

    public static void LOGE(String tag, String msg) {
        Log.e(tag, TAG + "\t" + msg);
    }

}
