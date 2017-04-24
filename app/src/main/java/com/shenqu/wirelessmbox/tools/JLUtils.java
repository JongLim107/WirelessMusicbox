package com.shenqu.wirelessmbox.tools;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Selection;
import android.text.Spannable;
import android.util.TypedValue;

import com.shenqu.wirelessmbox.action.BoxService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class JLUtils {
    private static SimpleDateFormat formatterHHmmss = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat formatterMMSS = new SimpleDateFormat("mm:ss");

    private final static String TAG = "JLUtils";

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * 切换后将EditText光标置于末尾
     */
    public static void moveCursorToEnd(CharSequence charSequence) {
        if (charSequence != null) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
    }

    public static String formatHHmmSS(int paramInt) {
        formatterHHmmss.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatterHHmmss.format(paramInt);
    }

    /**
     * 格式化时间为 Human-Readable
     */
    public static String formatMediaTime(int seconds) {
        int minutes = seconds / 60;
        seconds %= 60;
        int hours = minutes / 60;
        minutes %= 60;
        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

    public static int fromHHmmSS(String paramString) {
        formatterHHmmss.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        try {
            long l = formatterHHmmss.parse(paramString).getTime() / 1000L;
            return (int) l;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean checkPermission(String permission, Context context) {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        if (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permission, packageName)) {
            JLLog.LOGV(TAG, permission + " 拥有此权限");
            return true;
        } else {
            JLLog.LOGE(TAG, permission + " 获取失败");
            return false;
        }
    }

    public static String getAudioUriFromFile(Activity act, String path) {

        final String where = MediaStore.Audio.Media.DATA + "='" + path + "'";
        Uri uri = null;
        Cursor cursor = act.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,// 字段　没有字段　就是查询所有信息　相当于SQL语句中的　“ * ”
                                                       null, // 查询条件
                                                       null, // 条件的对应?的参数
                                                       MediaStore.Audio.AudioColumns.TITLE);// 排序方式
        assert cursor != null;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            if (path.equals(data)) {
                int ringtoneID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
                uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + ringtoneID);
                cursor.close();
                return uri.toString();
            }
            cursor.moveToNext();
        }
        return null;
    }

    /**
     * 获取 AndroidManifest.xml 里面各种 meta-data 的方法
     * meta-data 之所在位置不同有不懂得调用方法
     */
    public static String getAppMetaData(Activity activity, String name) {
        String msg = null;
        try {
            ApplicationInfo info = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            msg = info.metaData.getString(name);
            //System.out.println(name + ":" + msg);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static String getActivityMetaData(Activity activity, String name) {
        String msg = null;
        try {
            ActivityInfo info = activity.getPackageManager().getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
            msg = info.metaData.getString(name);
            //System.out.println(name + ":" + msg);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static String getServiveMetaData(Activity activity, Class<?> svrcls, String name) {
        String msg = null;
        try {
            ComponentName cn = new ComponentName(activity, svrcls);
            ServiceInfo info = activity.getPackageManager().getServiceInfo(cn, PackageManager.GET_META_DATA);
            msg = info.metaData.getString(name);
            //System.out.println(name + ":" + msg);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static String getRecevierMetaData(Activity activity, Class<?> reccls, String name) {
        String msg = null;
        try {
            ComponentName cn = new ComponentName(activity, reccls);
            ActivityInfo info = activity.getPackageManager().getReceiverInfo(cn, PackageManager.GET_META_DATA);
            msg = info.metaData.getString(name);
            //System.out.println(name + ":" + msg);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }
}