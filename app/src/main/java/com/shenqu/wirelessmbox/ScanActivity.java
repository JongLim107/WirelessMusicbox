package com.shenqu.wirelessmbox;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.shenqu.wirelessmbox.action.ActionType;
import com.shenqu.wirelessmbox.action.ThreadBroadcast;
import com.shenqu.wirelessmbox.base.BaseActivity;
import com.shenqu.wirelessmbox.bean.DevAdapter;
import com.shenqu.wirelessmbox.bean.MusicBox;
import com.shenqu.wirelessmbox.tools.AnimationTest;
import com.shenqu.wirelessmbox.tools.FastBlur;
import com.shenqu.wirelessmbox.tools.JLJSON;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.tools.MyProgressDialog;
import com.shenqu.wirelessmbox.tools.WirelessUtils;
import com.shenqu.wirelessmbox.ximalaya.MainFragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

public class ScanActivity extends BaseActivity implements Callback, AdapterView.OnItemClickListener {

    private static final String TAG = "ScanActi";
    private WifiManager mWifiManager;

    private long lastBackClickTime = 0;
    private Handler mHandler;
    private MyProgressDialog mProgressDialog;
    private Runnable mRunnableCancelDialog;

    /**
     * 广播类型 port & cmd
     */
    private Button mBtnScan;
    private ThreadBroadcast mBroadcast;
    private static final String SCAN_CMD = "{\"Req\":\"ScanDevice\"}";

    /**
     * 设备列表
     */
    private ListView mListView;
    private DevAdapter mAdapter;
    private ArrayList<MusicBox> mListData;

    private class MyAsyncTask extends AsyncTask<Integer, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Integer... params) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), params[0]);
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getSize(point);
            return FastBlur.blur(bitmap, point.x, point.y, 5, 8);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            setBackground(bitmap);
            MyApplication.getACache().put(MyApplication.gCacheBmp, bitmap);
        }
    }

    /**
     * Base Activity 初始化
     */
    private void initBaseView() {
        getbtn_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - lastBackClickTime < 2000)
                    finish();
                else {
                    lastBackClickTime = System.currentTimeMillis();
                    JLLog.showToast(ScanActivity.this, "再次单击退出");
                }
            }
        });
        hidebtn_right();
        setTitle("搜索WIFI音乐盒");

        Bitmap bmp = MyApplication.getACache().getAsBitmap(MyApplication.gCacheBmp);
        if (bmp != null) {
            setBackground(bmp);
        } else
            new MyAsyncTask().execute(R.mipmap.background);
    }

    private void initThisViews() {
        mListView = (ListView) findViewById(R.id.devListView);
        mListData = new ArrayList<>();
        mAdapter = new DevAdapter(this, mListData);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mBtnScan = (Button) findViewById(R.id.btnScan);
    }

    private void doScanning() {
        mBtnScan.setEnabled(false);
        mProgressDialog = new MyProgressDialog(this);
        mProgressDialog.initDialog(false, "正在搜索设备，请稍候...");
        mProgressDialog.show();
        mHandler.postDelayed(mRunnableCancelDialog, 8000);
        mBroadcast = new ThreadBroadcast(this, ActionType.SCAN_PORT, SCAN_CMD, 8, mHandler);
        mBroadcast.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_scan);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mHandler = new Handler(this);

        initBaseView();
        initThisViews();
        mRunnableCancelDialog = new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null)
                    mProgressDialog.cancel();
                if (mBroadcast != null)
                    mBroadcast.isExited = true;
                mBtnScan.setEnabled(true);
            }
        };
    }

    private void initListData() {
        mListData.clear();
        mAdapter.notifyDataSetChanged();
        if (WirelessUtils.isWifiEnabled(mWifiManager) || WirelessUtils.isWifiApEnabled(mWifiManager)) {
            /**
             * 再次搜索设备
             */
            doScanning();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle("开启网络");
            builder.setMessage("WiFi和热点均未开启，是否开启？\n（热点共享模式需手动打开，建议设置密码，防止被蹭网）");
            builder.setPositiveButton("打开WiFi", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //打开WiFi需要时间，延迟等待开启然后连接上WiFi在搜索，往后可换成listen
                    dialog.cancel();
                    WirelessUtils.openWifi(mWifiManager);
                }
            });
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JLLog.LOGV(TAG, "onResume()");
        MyApplication.setAppExit(false);
        initListData();
    }

    @Override
    protected void onPause() {
        mHandler.post(mRunnableCancelDialog);
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyApplication.setMusicBox(mListData.get(position));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onScanAgain(View view) {
        initListData();

        //startActivity(new Intent(this, AnimationTest.class));
//
//        JSONObject j = new JSONObject();
//        JSONObject body = new JSONObject();
//
//        try {
//            body.put("DeviceIpAddr", WirelessUtils.getWifiApIpAddress());
//            body.put("DeviceFiremwareVersion", "null");
//            body.put("HttpApiPort", "9527");
//            body.put("DeviceName", "null-device");
//            body.put("DeviceMacAddr", WirelessUtils.getWifiManager(getBaseContext()).getConnectionInfo().getMacAddress());
//
//            j.put("Body", body);
//            MyApplication.setMusicBox(new MusicBox(j.toString()));
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void onWirelessSetting(View view) {
        if (Build.VERSION.SDK_INT > 14) {
            //Intent intent = new Intent(Settings.ACTION_SETTINGS);//系统设置界面
            //Intent intent = new Intent(this, PlayerActivity.class);//测试需要跳转
            Intent intent = new Intent(this, MainFragmentActivity.class);//测试需要跳转
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.post(mRunnableCancelDialog);
        if (mBroadcast != null) {
            mBroadcast.interrupt();
            mBroadcast.isExited = true;
            JLLog.LOGI(TAG, "Canceled the broadcast !");
        }
        MyApplication.exit();
        //WirelessUtils.closeWifiAp(mWifiManager);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case ActionType.SCAN_PORT: {
                Bundle b = msg.getData();
                MusicBox mbx = null;
                try {
                    mbx = new MusicBox(b.getString("JSONDATA"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }

                boolean isExist = false;
                if (!mListData.isEmpty()) {
                    for (MusicBox mb : mListData) {
                        if (mb.getAddr().equalsIgnoreCase(mbx.getAddr())) {
                            isExist = true;
                            break;
                        }
                    }
                }
                if (!isExist) {
                    JLLog.showToast(this, "搜索到一个设备，可选中进入");
                    mListData.add(mbx);
                    mAdapter.notifyDataSetChanged();
                    mProgressDialog.setMessage("正在搜索设备，请稍候...\n\n(搜索到设备，可选中进入)");
                    mProgressDialog.setCancelable(true);
                }
                break;
            }
            default:
                mHandler.removeCallbacks(mRunnableCancelDialog);
                mHandler.post(mRunnableCancelDialog);
                JLLog.showToast(this, "搜索结束~");
                break;
        }
        return true;
    }
}
