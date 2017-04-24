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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.shenqu.wirelessmbox.action.ActionType;
import com.shenqu.wirelessmbox.action.BoxControler;
import com.shenqu.wirelessmbox.base.BaseActivity;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.tools.JLUtils;
import com.shenqu.wirelessmbox.tools.JLJSON;
import com.shenqu.wirelessmbox.tools.MyProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class BoxInfoActivity extends BaseActivity implements Handler.Callback, View.OnClickListener {
    private static final String TAG = "BoxInfoAct";

    private BoxControler mBoxControler;

    /**
     * 设备基本信息
     */
    private EditText mEtName;
    private EditText mEtPwd;
    private TextView mTvDevVer;
    private Button mBtnSetName;
    /**
     * 设备网络信息
     */
    private TextView mTvWanState;
    private TextView mTvWanIp;
    private TextView mTvWanGw;
    private TextView mTvWanMs;

    private TextView mTvWanDNS;
    /**
     * u盘信息
     */
    private TextView mTvUdiskState;
    private TextView mTvUdiskSize;
    private TextView mTvUdiskUsed;

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
        setContentView(R.layout.dialog_box_info);

        findViews();
        initData();
    }

    private void findViews() {
        /*
      content layout
      */
        RelativeLayout contentLayout = (RelativeLayout) findViewById(R.id.layoutContent);
        contentLayout.setOnClickListener(this);

        mEtName = (EditText) findViewById(R.id.devName);
        mEtPwd = (EditText) findViewById(R.id.devPwd);
        ToggleButton toggleShPwd = (ToggleButton) findViewById(R.id.tgShowPwd);
        toggleShPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        mTvDevVer = (TextView) findViewById(R.id.devVersion);
        mBtnSetName = (Button) findViewById(R.id.btnSetWifiName);
        mBtnSetName.setOnClickListener(this);

        mTvWanState = (TextView) findViewById(R.id.wanState);
        mTvWanIp = (TextView) findViewById(R.id.wanIp);
        mTvWanGw = (TextView) findViewById(R.id.wanGw);
        mTvWanMs = (TextView) findViewById(R.id.wanMask);
        mTvWanDNS = (TextView) findViewById(R.id.wanDns);

        mTvUdiskState = (TextView) findViewById(R.id.usbState);
        mTvUdiskSize = (TextView) findViewById(R.id.usbSize);
        mTvUdiskUsed = (TextView) findViewById(R.id.usbUsed);
    }

    private void initData() {
        mHandler = new Handler(this);
        mBoxControler = MyApplication.getControler();
        mBoxControler.setHandler(mHandler);
        mBoxControler.getDeviceBasicConfig();

        /**
         * 初始化加载动画
         * */
        mProgressDialog = new MyProgressDialog(this);
        mProgressDialog.initDialog(false, "正在获取设备信息..");
        mProgressDialog.show();
        mHandler.postDelayed(mRunnableCancelDialog, 6000);
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

        boolean ret = false;
        JSONObject body = null;
        try {
            JSONObject obj = new JSONObject(string);
            ret = JLJSON.getInt(obj, "Result") == 0;
            if (ret)
                body = JLJSON.getJSONObject(obj, "Body");
        } catch (JSONException e) {
            JLLog.showToast(this, "" + e.getLocalizedMessage());
        }

        switch (msg.what) {
            case ActionType.GetDeviceBasicConfig: {
                mHandler.removeCallbacks(mRunnableCancelDialog);
                String name = JLJSON.getString(body, "DeviceName");
                mEtName.setText(name);
                mTvDevVer.setText(JLJSON.getString(body, "DeviceFiremwareVersion"));
                if (name.equals("")) {
                    mEtName.setEnabled(false);
                    mBtnSetName.setClickable(false);
                    mEtPwd.setEnabled(false);
                } else {
                    mEtName.setEnabled(true);
                    mBtnSetName.setClickable(true);
                    mEtPwd.setEnabled(true);
                }

                /*********************************/
                mProgressDialog.setMessage("正在获取网络状态信息...");
                mHandler.postDelayed(mRunnableCancelDialog, 6000);
                mBoxControler.getNetworkState();
                break;
            }
            case ActionType.GetNetworkState: {
                mHandler.removeCallbacks(mRunnableCancelDialog);
                mTvWanState.setText(JLJSON.getInt(body, "DeviceWanConnect") == 1 ? "音响成功连接到路由器" : "网络断开");//1：音响成功连接到路由器 0：网络断开
                mTvWanIp.setText(JLJSON.getString(body, "DeviceWanIp"));//路由器分配给音响的 IP 地址
                mTvWanGw.setText(JLJSON.getString(body, "DeviceWanGw"));//网关地址
                mTvWanMs.setText(JLJSON.getString(body, "DeviceWanMask")); //子网掩码
                mTvWanDNS.setText(JLJSON.getString(body, "DeviceWanDNS"));//DNS 域名解析服务器
                /*********************************/
                mProgressDialog.setMessage("正在获取U盘状态...");
                mHandler.postDelayed(mRunnableCancelDialog, 6000);
                mBoxControler.getUdiskInfo();
                break;
            }
            case ActionType.GetUdiskInfo: {
                mHandler.removeCallbacks(mRunnableCancelDialog);
                mProgressDialog.cancel();
                int ista = JLJSON.getInt(body, "State");
                if (ista == 0) {
                    mTvUdiskState.setText("正常");
                } else if (ista == 1)
                    mTvUdiskState.setText("没有插入");
                else
                    mTvUdiskState.setText("无法识别");

                mTvUdiskSize.setText(JLJSON.getInt(body, "Size") / 1024 + " MB");
                mTvUdiskUsed.setText(JLJSON.getInt(body, "Used") / 1024 + " MB");
                break;
            }
            case ActionType.SetDeviceBasicConfig: {
                mHandler.removeCallbacks(mRunnableCancelDialog);
                if (ret) {
                    JLLog.showToast(this, "更改音响wifi名字成功");
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    JLLog.showToast(this, "更改失败");
                }
                mProgressDialog.cancel();
                break;
            }
            default:
                return true;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSetWifiName: {
                mProgressDialog = new MyProgressDialog(this);
                mProgressDialog.initDialog(false, "正在更改设备WiFi信息..");
                mProgressDialog.show();
                mHandler.postDelayed(mRunnableCancelDialog, 6000);
                mBoxControler.setBasicConfig(mEtName.getText().toString(), mEtPwd.getText().toString());
                break;
            }
            case R.id.layoutContent: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            }
            default:
                break;
        }
    }
}
