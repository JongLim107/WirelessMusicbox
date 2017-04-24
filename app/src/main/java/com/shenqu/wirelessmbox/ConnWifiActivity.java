/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shenqu.wirelessmbox;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shenqu.wirelessmbox.action.ActionType;
import com.shenqu.wirelessmbox.action.BoxControler;
import com.shenqu.wirelessmbox.action.ThreadConnectWifi;
import com.shenqu.wirelessmbox.base.BaseActivity;
import com.shenqu.wirelessmbox.bean.MusicBox;
import com.shenqu.wirelessmbox.bean.WifiAdapter;
import com.shenqu.wirelessmbox.bean.WifiItem;
import com.shenqu.wirelessmbox.tools.JLJSON;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.tools.JLUtils;
import com.shenqu.wirelessmbox.tools.MyProgressDialog;
import com.shenqu.wirelessmbox.tools.WirelessUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConnWifiActivity extends BaseActivity implements Handler.Callback, AdapterView.OnItemClickListener, DialogInterface.OnCancelListener {
    private static final String TAG = "ConnetWifi";
    private MusicBox mBox;
    private BoxControler mBoxControler;

    private EditText mEtSsid;
    private EditText mEtPwd;
    private Button btnSetAp;
    private ToggleButton mTgShowPwd;
    private CheckBox mRestartBox;
    private Handler mHandler;
    private MyProgressDialog mProgressDialog;
    private Runnable mRunnableCancelDialog = new Runnable() {
        @Override
        public void run() {
            if (mProgressDialog != null)
                mProgressDialog.cancel();
        }
    };

    private PullToRefreshListView mWifisView;
    private ArrayList<WifiItem> mWifis;
    private WifiAdapter mAdapter;
    private WifiItem mSelectedItem;

    private void findViews() {
        mEtSsid = (EditText) findViewById(R.id.configSsid);
        mEtPwd = (EditText) findViewById(R.id.configPwd);
        btnSetAp = (Button) findViewById(R.id.btnSetAp);
        mTgShowPwd = (ToggleButton) findViewById(R.id.tgShowPwd);
        mRestartBox = (CheckBox) findViewById(R.id.cbRestart);
        mWifisView = (PullToRefreshListView) findViewById(R.id.wifiListView);
    }

    private void initDatas() {
        mTgShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mEtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    mEtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mEtPwd.postInvalidate();
                JLUtils.moveCursorToEnd(mEtPwd.getText());
            }
        });

        mWifis = new ArrayList<>();
        mAdapter = new WifiAdapter(this, mWifis);
        mWifisView.setAdapter(mAdapter);
        mWifisView.setOnItemClickListener(this);
        mWifisView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                doGetWifiList();
            }
        });

        if (mEtSsid.requestFocus()) {
            JLUtils.moveCursorToEnd(mEtSsid.getText());
        }
    }

    private void initBoxData() {
        mHandler = new Handler(this);
        mBox = MyApplication.getMusicBox();
        mBoxControler = MyApplication.getControler();
        mBoxControler.setHandler(mHandler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置标题栏要在设置view之前
        setContentView(R.layout.dialog_box_wifi);

        findViews();
        //initBaseView();
        initDatas();
        initBoxData();

        //初始化时要获取WiFi列表
        doGetWifiList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /**
         * 当 mListView 为 PullToRefreshListView 时，position从1开始，当添加了HeadView时 position从2开始
         */
        mSelectedItem = mWifis.get(position - 1);
        mAdapter.setCurName(mSelectedItem.getSsid());
        mAdapter.notifyDataSetChanged();
        mEtSsid.setText(mSelectedItem.getSsid());
        JLUtils.moveCursorToEnd(mEtSsid.getText());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }

    @Override
    public boolean handleMessage(Message msg) {
        Bundle b = msg.getData();
        String string = b.getString("JSONDATA");
        if (string == null)
            return false;

        JSONObject jobj = null;
        try {
            jobj = new JSONObject(string);
        } catch (JSONException e) {
            JLLog.showToast(this, "" + e.getLocalizedMessage());
            return false;
        }

        switch (msg.what) {
            case ActionType.WiFiScan: {
                mWifisView.onRefreshComplete();
                mHandler.removeCallbacks(mRunnableCancelDialog);
                mProgressDialog.dismiss();
                if (JLJSON.getInt(jobj, "Result") == 0) {
                    try {
                        JSONArray apList = JLJSON.getJSONObject(jobj, "Body").getJSONArray("ApList");
                        for (int i = 0; i < apList.length(); i++) {
                            mWifis.add(new WifiItem(apList.getJSONObject(i)));
                            mAdapter.notifyDataSetChanged();
                        }
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                JLLog.showToast(this, "查询失败,请重新尝试~");
                break;
            }
            case ActionType.SetNetworkConfig: {
                mHandler.removeCallbacks(mRunnableCancelDialog);
                if (JLJSON.getInt(jobj, "Result") == 0) {
                    final String ssid = mEtSsid.getText().toString();
                    final String pwd = mEtPwd.getText().toString();
                    mProgressDialog.setMessage("正在连接WiFi,请稍候...");
                    mHandler.postDelayed(mRunnableCancelDialog, 6000);
                    mBoxControler.connetToWifi(ssid, pwd);
                } else {//链接wifiAp失败，取消ProgressDialog但是不退出
                    mProgressDialog.dismiss();
                    JLLog.showToast(this, "配置失败,请重新尝试~");
                }
                break;
            }
            case ActionType.WiFiStaConnect: {
                mHandler.removeCallbacks(mRunnableCancelDialog);
                if (JLJSON.getInt(jobj, "Result") == 0) {
                    JLLog.showToast(this, "配置成功~ ");
                    if (mRestartBox.isChecked()) {
                        mProgressDialog.setMessage("尝试重启设备..");
                        mHandler.postDelayed(mRunnableCancelDialog, 6000);
                        mBoxControler.restartBox();
                    } else {
                        setResult(Activity.RESULT_CANCELED);
                        mHandler.post(mRunnableCancelDialog);
                    }
                } else {//链接wifiAp失败，取消ProgressDialog但是不退出
                    mProgressDialog.dismiss();
                    JLLog.showToast(this, "连接失败,请重新尝试~");
                }
                break;
            }
            case ActionType.RestartDevice: {
                mHandler.removeCallbacks(mRunnableCancelDialog);
                if (JLJSON.getInt(jobj, "Result") == 0) {
                    setResult(Activity.RESULT_OK);
                    mProgressDialog.setMessage("正在连接到同一个wifi..\n大概耗时 30秒 左右~");
                    mHandler.postDelayed(mRunnableCancelDialog, 15000);
                    //开关WiFi要些时间， 所以延迟连接WiFi
                    final String ssid = mEtSsid.getText().toString();
                    final String pwd = mEtPwd.getText().toString();
                    new ThreadConnectWifi(WirelessUtils.getWifiManager(ConnWifiActivity.this), ssid, pwd, mSelectedItem.getAuth()).start();

                } else {
                    setResult(Activity.RESULT_CANCELED);
                    mHandler.post(mRunnableCancelDialog);
                }
                break;
            }
            default:
                break;
        }
        return true;
    }

    /**
     * onClick widget button
     */
    public void onBtnCancel(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void onBtnSetConfig(View view) {
        mProgressDialog = new MyProgressDialog(this);
        mProgressDialog.initDialog(false, "正在配置设备,请稍候...");
        mProgressDialog.show();
        mProgressDialog.setOnCancelListener(this);
        setResult(RESULT_CANCELED);
        mHandler.postDelayed(mRunnableCancelDialog, 6000);
        mBoxControler.setNetworkConfig(2, 1, "DHCP", "ON");
    }

    private void doGetWifiList() {
        mWifis.clear();
        mAdapter.notifyDataSetChanged();
        mProgressDialog = new MyProgressDialog(this);
        mProgressDialog.initDialog(false, "获取设备附近的WiFi列表..");
        mProgressDialog.show();
        setResult(RESULT_CANCELED);
        mHandler.postDelayed(mRunnableCancelDialog, 6000);
        mBoxControler.getWifiList();
    }
}
