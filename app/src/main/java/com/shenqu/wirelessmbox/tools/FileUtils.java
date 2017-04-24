package com.shenqu.wirelessmbox.tools;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;

import com.shenqu.wirelessmbox.MyApplication;
import com.shenqu.wirelessmbox.action.MyHttpServer;
import com.shenqu.wirelessmbox.bean.TrackMeta;
import com.shenqu.wirelessmbox.bean.TrackSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JongLim on 2016/12/13.
 */

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();
    public static final String LOCAL_FILE_INDICATE = "/tracks";

    private static String mapFileToNetAddr(){
        return "http://" + WirelessUtils.getWifiApIpAddress() + ":" + MyHttpServer.LISTEN_PORT + LOCAL_FILE_INDICATE;
    }

    public static boolean isExists(String path) {
        File dir = new File(path);
        return dir.exists();
    }

    public static String[] getStorgePath(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            for (int i = 0; i < ((String[]) invoke).length; i++) {
                JLLog.LOGV(TAG, "App StorgePath : " + ( (String[]) invoke )[i]);
            }
            return (String[]) invoke;
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStorgePath(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Shenqu";
        } else
            return null;
    }

    public static String getLocalListPath() {
        return MyApplication.getACacheDir() + "/local.json";
    }

    public static String getFavoriteListPath() {
        return MyApplication.getACacheDir() + "/favorite.json";
    }

    public static String getJSONFilePath() {
        return MyApplication.getACacheDir() + "/playlist/";
    }

    public static String newJSONFilePath(String name) {
        return getJSONFilePath() + name + ".json";
    }

    public static String getLocalSongPath(String string) {
        return string.substring(string.indexOf(LOCAL_FILE_INDICATE) + LOCAL_FILE_INDICATE.length());
    }

    public static File[] queryJSONFiles(String folder) {
        File filepath = new File(folder);
        return filepath.listFiles();
    }

    public static void querySongs(Context context, ArrayList<TrackMeta> trackList) {
        assert trackList != null;
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,// 字段　没有字段　就是查询所有信息　相当于SQL语句中的　“ * ”
                                                           null, // 查询条件
                                                           null, // 条件的对应的参数
                                                           MediaStore.Audio.AudioColumns.TITLE);// 排序方式
        assert cursor != null;
        /**
         *  找到歌曲列索引,然后获取歌曲信息
         */
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));//文件的路径

            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));//歌名
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));//文件大小
            int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//文件总时长
            /**
             * 转换成网络链接
             */
            int index = path.lastIndexOf("/");
            String url = mapFileToNetAddr() + path.substring(0, index) + Uri.encode(path.substring(index));
            trackList.add(new TrackMeta(url, artist, album, String.valueOf(id), title, cursor.getPosition() + 1, TrackSource.SOURCE_LOCAL));
            //                TrackMeta(url, artist, coverUrl, String id, String name, int position, int source)
        }
        cursor.close();
    }

    public static String querySongAlbum(Context context, int position) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,// 字段　没有字段　就是查询所有信息　相当于SQL语句中的　“ * ”
                                                           null, // 查询条件
                                                           null, // 条件的对应的参数
                                                           MediaStore.Audio.AudioColumns.TITLE);// 排序方式
        assert cursor != null;
        while (cursor.moveToNext()) {
            if (cursor.getPosition() == position) {
                return cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            }
        }
        return null;
    }

    private static String getJSONFromList(List<TrackMeta> list, int begin) {
        StringBuilder string = new StringBuilder();
        string.append("{\"FileType\":\"0\",\"classItems\":[")// init playlist
                .append("{\"begin\":\"").append(begin)//position that where to begin.
                .append("\",\"id\":\"").append(String.valueOf(System.currentTimeMillis()))//list id
                .append("\",\"module\":\"cycle\",\"musics\":[");// default:play in line order why NULL, only can be "cycle".

        if ((list != null) && (!list.isEmpty())) {
            for (TrackMeta trackMeta : list) {
                string.append(trackMeta.toJsonString());//format to json type
                if (trackMeta.getSource() == 4) {
                    string.append(",\"type\":1},");
                } else {
                    string.append(",");
                }
            }
        }else {
            string.append(",");
        }
        return string.substring(0, string.length() - 1) + "]}]}";
    }

    public static String getListFromJSON(ArrayList<TrackMeta> list, JSONObject jobj) {
        try {
            JSONArray items = JLJSON.getJSONArray(jobj, "classItems");
            String listId = JLJSON.getString(items.getJSONObject(0), "id");
            JLLog.LOGV(TAG, "Local ID = " + listId);
            JSONArray musics = JLJSON.getJSONArray(items.getJSONObject(0), "musics");
            for (int i = 0; i < musics.length(); i++){
                JSONObject music = musics.getJSONObject(i);

                //设备和手机的ip回东台变化 所以本地文件映射要动态更新
                TrackMeta meta = new TrackMeta(music);
                String url = JLJSON.getString(music, "url");
                if (url.contains(LOCAL_FILE_INDICATE)){
                    meta.setUrl(mapFileToNetAddr() + FileUtils.getLocalSongPath(url));
                }

                list.add(meta);
            }
            return listId;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 将播放列表存到本地JSON中
     */
    public static void writeTracksToJSONFile(final List<TrackMeta> list, final String fileName, final int begin) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (fileName) {
                    //将 list 转换成 String
                    String string = getJSONFromList(list, begin);
                    try {
                        File saveFile = new File(fileName);
                        File dir = new File(saveFile.getParent());
                        if (!dir.exists()) {
                            dir.mkdirs();
                        } else if (saveFile.exists()) {
                            saveFile.delete();
                        }
                        saveFile.createNewFile();
                        FileOutputStream outStream = new FileOutputStream(saveFile);
                        if (string != null)
                            outStream.write(string.getBytes());
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 将本地播放列表文件读取到内存中，并转化为String
     */
    public static void readTracksFromJSONFile(ArrayList<TrackMeta> list, final String fileName) {
        //将 String 转换成 List
        if (fileName.contains(".json")) {
            InputStream instream = null;
            try {
                instream = new FileInputStream(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                JLLog.LOGD(TAG, "The File doesn't not exist.");
            }
            try {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = buffreader.readLine();
                instream.close();
                buffreader.close();
                try {
                    JSONObject object = new JSONObject(line);
                    getListFromJSON(list, object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                JLLog.LOGD("TestFile", e.getMessage());
            }
        }
    }

}
