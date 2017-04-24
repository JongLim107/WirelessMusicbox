package com.shenqu.wirelessmbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.shenqu.wirelessmbox.action.ActionType;
import com.shenqu.wirelessmbox.action.BoxControler;
import com.shenqu.wirelessmbox.action.ThreadConnectWifi;
import com.shenqu.wirelessmbox.base.BaseActivity;
import com.shenqu.wirelessmbox.tools.FileUtils;
import com.shenqu.wirelessmbox.tools.JLJSON;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.tools.MIME;
import com.shenqu.wirelessmbox.tools.MyProgressDialog;
import com.shenqu.wirelessmbox.tools.WirelessUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FileListActivity extends BaseActivity implements AdapterView.OnItemClickListener, Handler.Callback, DialogInterface.OnCancelListener {
    private static final String TAG = "FileList";

    private ListView mListView;
    private ArrayList<String> pathArray = null;   // items：存放显示的名称
    private String mFileName;

    //当前文件目录
    private String homePath;
    private String currentPath;
    private TextView tvCurPath;
    private CheckBox mRestartBox;

    private BoxControler mBoxControler;

    private Handler mHandler;
    private MyProgressDialog mProgressDialog;
    private Runnable mRunGetUpgadeStatus = new Runnable() {
        @Override
        public void run() {
            mBoxControler.getUpgadeStatus();
        }
    };
    private Runnable mRunnableCancelDialog = new Runnable() {
        @Override
        public void run() {
            if (mProgressDialog != null)
                mProgressDialog.cancel();
        }
    };

    // 界面初始化
    public void initListView(String path) {
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        pathArray = new ArrayList<String>();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File f = new File(path);
            if (!f.exists())
                f.mkdir();
            // 获取SDcard目录下所有文件名
            File[] files = f.listFiles();
            if (!files.equals(null)) {
                currentPath = f.getPath();
                tvCurPath.setText("位置:" + currentPath);
                for (File file : files) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    if (file.isFile())
                        map.put("ItemImage", R.mipmap.ic_text_plain);
                    else
                        map.put("ItemImage", R.mipmap.ic_folder);
                    map.put("ItemTitle", file.getName());
                    pathArray.add(currentPath + "/" + file.getName());
                    listItem.add(map);
                }

                // 生成适配器的Item和动态数组对应的元素
                SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem, R.layout.item_files, new String[]{"ItemImage", "ItemTitle"}, new int[]{R.id.file_Image, R.id
                        .file_Title});
                mListView.setAdapter(listItemAdapter);
            }
        } else {
            JLLog.showToast(FileListActivity.this, "没有文件");
            System.out.println("该文件夹为空");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_box_upgrade);
        mListView = (ListView) findViewById(R.id.mListView);
        mListView.setOnItemClickListener(this);

        tvCurPath = (TextView) findViewById(R.id.tvCurPath);
        mRestartBox = (CheckBox) findViewById(R.id.cbRestart);

        // 初始化文件列表
        homePath = FileUtils.getStorgePath();
        initListView(homePath);

        mHandler = new Handler(this);
        mBoxControler = MyApplication.getControler();
        mBoxControler.setHandler(mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = new File(pathArray.get(position));
        if (file.isDirectory()) {
            initListView(pathArray.get(position));
        } else {
            mFileName = pathArray.get(position);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("固件升级");
            builder.setMessage("警告！该功能可能会导致音响异常，请慎用！升级时请不要进行其他操作！保证网络顺畅！\n\n****确保文件名包含“-HIVI-”****\n\n确定继续么？");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    mProgressDialog = new MyProgressDialog(builder.getContext());
                    mProgressDialog.initDialog(false, "正在升级...\n请不要进行其他操作！！");
                    mProgressDialog.show();
                    mProgressDialog.setOnCancelListener(FileListActivity.this);

                    //确定升级
                    MyApplication.getControler().setUpgradeFirmware(mFileName);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        setResult(RESULT_OK);
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
            case ActionType.UpgradeFirmware: {
                int ret = JLJSON.getInt(jobj, "Result");
                if (ret == 0) {
                    JLLog.showToast(this, "升级成功~");
                    if (mRestartBox.isChecked()) {
                        mProgressDialog.setMessage("尝试重启设备..");
                        setResult(Activity.RESULT_CANCELED);
                        mHandler.postDelayed(mRunnableCancelDialog, 6000);
                        mBoxControler.restartBox();
                    } else {
                        setResult(Activity.RESULT_CANCELED);
                        mHandler.post(mRunnableCancelDialog);
                    }
                } else if (ret == 2003) {
                    mHandler.postDelayed(mRunGetUpgadeStatus, 5000);
                } else {//2004
                    JLLog.showToast(this, "升级失败...\n确保文件名包含 -HIVI- ");
                    mHandler.removeCallbacks(mRunGetUpgadeStatus);
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                }
                break;
            }
            case ActionType.RestartDevice: {
                mHandler.removeCallbacks(mRunnableCancelDialog);
                if (JLJSON.getInt(jobj, "Result") == 0) {
                    setResult(Activity.RESULT_OK);
                    mProgressDialog.setMessage("正在重启设备,大概耗时 30秒 左右~");
                    mHandler.postDelayed(mRunnableCancelDialog, 15000);
                } else {
                    setResult(Activity.RESULT_CANCELED);
                    mHandler.post(mRunnableCancelDialog);
                }
            }
        }
        return false;
    }

    /**
     * on widget btn clicked listen
     */
    public void onBtnCancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onBtnBack(View view) {
        if (currentPath.equalsIgnoreCase(homePath)) {
            JLLog.showToast(this, "已回到顶层文件夹");
        } else {
            initListView(currentPath.substring(0, currentPath.lastIndexOf('/')));
        }
    }

    protected void onOpenFile(String filePath) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        // 获取文件file的MIME类型
        String type = MIME.getMIMEType(filePath);
        // 设置intent的data和Type属性.
        intent.setDataAndType(Uri.parse("file://" + filePath), type);
        startActivity(intent);
    }

}
