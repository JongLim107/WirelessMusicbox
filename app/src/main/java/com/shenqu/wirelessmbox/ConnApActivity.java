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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.shenqu.wirelessmbox.action.ActionType;
import com.shenqu.wirelessmbox.action.BoxControler;
import com.shenqu.wirelessmbox.base.BaseActivity;
import com.shenqu.wirelessmbox.bean.MusicBox;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.tools.JLUtils;
import com.shenqu.wirelessmbox.tools.JLJSON;
import com.shenqu.wirelessmbox.tools.MyProgressDialog;
import com.shenqu.wirelessmbox.tools.WirelessUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnApActivity extends BaseActivity implements Handler.Callback, DialogInterface.OnCancelListener {
    private static final String TAG = "ConnetAP";
    private BoxControler mBoxControler;
    private EditText mEtSsid;
    private EditText mEtPwd;
    private ToggleButton mTgShowPwd;
    private CheckBox restartBox;

    private Handler mHandler;
    private MyProgressDialog mProgressDialog;
    private Runnable mRunnableCancelDialog = new Runnable() {
        @Override
        public void run() {
            if (mProgressDialog != null)
                mProgressDialog.cancel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置标题栏要在设置view之前
        setTitle("配置手机热点");
        setContentView(R.layout.dialog_box_ap);

        findViews();
        initData();
    }

    private void findViews() {
        mEtSsid = (EditText) findViewById(R.id.configSsid);
        mEtPwd = (EditText) findViewById(R.id.configPwd);
        mTgShowPwd = (ToggleButton) findViewById(R.id.tgShowPwd);
        restartBox = (CheckBox) findViewById(R.id.cbRestart);
    }

    private void initData() {
        mHandler = new Handler(this);
        mBoxControler = MyApplication.getControler();
        mBoxControler.setHandler(mHandler);
        mEtSsid.setText("wifi4mbox_" + Build.DEVICE.toLowerCase());
        if (mEtSsid.requestFocus())
            JLUtils.moveCursorToEnd(mEtSsid.getText());

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
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            case ActionType.SetNetworkConfig: {
                mHandler.removeCallbacks(mRunnableCancelDialog);
                if (JLJSON.getInt(jobj, "Result") == 0) {
                    final String ssid = mEtSsid.getText().toString();
                    final String pwd = mEtPwd.getText().toString();
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
                    if (restartBox.isChecked()) {
                        mProgressDialog.setMessage("尝试重启设备..\n并自动创建热点");
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
                    mProgressDialog.setMessage("正在创建热点..\n大概耗时 30秒 左右~");
                    mHandler.postDelayed(mRunnableCancelDialog, 25000);

                    final String ssid = mEtSsid.getText().toString();
                    final String pwd = mEtPwd.getText().toString();
                    WirelessUtils.openWiFiAP(WirelessUtils.getWifiManager(ConnApActivity.this), ssid, pwd);
                } else {
                    setResult(Activity.RESULT_CANCELED);
                    mHandler.post(mRunnableCancelDialog);
                }
            }
            default:
                break;
        }
        return true;
    }

    public void onBtnCancel(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void onBtnSetAp(View view) {
        mProgressDialog = new MyProgressDialog(this);
        mProgressDialog.initDialog(false, "正在配置设备,请稍候...");
        mProgressDialog.show();
        mProgressDialog.setOnCancelListener(this);
        setResult(Activity.RESULT_CANCELED);
        mHandler.postDelayed(mRunnableCancelDialog, 6000);
        mBoxControler.setNetworkConfig(2, 1, "DHCP", "ON");
    }

}
