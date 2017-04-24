package com.shenqu.wirelessmbox;

/**
 * Created by JongLim on 2016/12/13.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shenqu.wirelessmbox.action.ActionType;
import com.shenqu.wirelessmbox.action.BoxControler;
import com.shenqu.wirelessmbox.action.BoxControler.OnBoxPlayStateListener;
import com.shenqu.wirelessmbox.action.BoxService;
import com.shenqu.wirelessmbox.bean.MusicBoxState;
import com.shenqu.wirelessmbox.bean.TrackAdapter;
import com.shenqu.wirelessmbox.bean.TrackMeta;
import com.shenqu.wirelessmbox.swipemenulistview.SwipeMenu;
import com.shenqu.wirelessmbox.swipemenulistview.SwipeMenuCreator;
import com.shenqu.wirelessmbox.swipemenulistview.SwipeMenuItem;
import com.shenqu.wirelessmbox.swipemenulistview.SwipeMenuListView;
import com.shenqu.wirelessmbox.tools.FileUtils;
import com.shenqu.wirelessmbox.tools.JLJSON;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.tools.JLUtils;
import com.shenqu.wirelessmbox.tools.MyProgressDialog;
import com.shenqu.wirelessmbox.ximalaya.MainFragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, Handler.Callback, OnBoxPlayStateListener, DialogInterface.OnCancelListener,
                                                               SwipeMenuListView.OnMenuItemClickListener {
    private static final String TAG = "MainActi";
    private static final int REQCODE_BOX_CONFIG = 0x10;
    private static final int REQUEST_RADIO_ACTIVITY = 0x11;
    private Context mContext;

    private DrawerLayout mDrawerLayout;
    private MyProgressDialog mProgressDialog;
    private Runnable mRunnableCancelDialog = new Runnable() {
        @Override
        public void run() {
            if (mProgressDialog != null)
                mProgressDialog.cancel();
        }
    };

    private Handler mHandler;
    private BoxControler mBoxControler;
    private boolean isPlaying;

    /**
     * main_content widget
     */
    private LinearLayout mLyLocalMusic;

    private TextView mTvLocalFounded;
    private SeekBar mCtrlSeekBar;
    private TextView mCtrlCurTime;
    private TextView mCtrlTotalTime;
    private ImageView mBtnPlay;
    private TextView tvSongName;

    /**
     * 播放列表
     * */
    private ArrayList<HashMap<String, String>> mPlayLists;
    private SwipeMenuListView mPlayListsView;
    private SimpleAdapter mPlayListsAdapter;

    /**
     * 歌曲列表
     * */
    private PullToRefreshListView mPullRefreshListView;
    private ArrayList<TrackMeta> mTracks;           // 当前选中歌曲列表，有以下几种三种列表
    private ArrayList<TrackMeta> mLocalTracks;      // 1.本地歌曲列表数据源
    private ArrayList<TrackMeta> mFavoriteTracks;   // 2.最爱歌曲列表数据源
    private ArrayList<TrackMeta> mOtherTracks;      // 3.其他歌曲列表数据源
    private PopupWindow mTracksWindow;              // 歌曲列表弹出窗口，包含歌曲ListView
    private TrackAdapter mTracksAdapter;
    private TextView mTvListTitle;

    private void initNavigationView() {
        /*
      navigation widget
      */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //mDrawerLayout.setDrawerListener(toggle);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    private void findMyView() {
        mLyLocalMusic = (LinearLayout) findViewById(R.id.lyLocalMusic);
        mTvLocalFounded = (TextView) findViewById(R.id.tvLocalFounded);
        mCtrlSeekBar = (SeekBar) findViewById(R.id.playSeekBar);
        mCtrlCurTime = (TextView) findViewById(R.id.ctrlCurTime);
        mCtrlTotalTime = (TextView) findViewById(R.id.ctrlTotalTime);
        mCtrlSeekBar.setFadingEdgeLength(10);
        mBtnPlay = (ImageView) findViewById(R.id.miniCtrlPlay);
        tvSongName = (TextView) findViewById(R.id.tvMusicTitle);
        mPlayListsView = (SwipeMenuListView) findViewById(R.id.lvPlaylists);
    }

    private void initBoxData() {
        mHandler = new Handler(this);
        mBoxControler = new BoxControler(MyApplication.getMusicBox(), mHandler, this);
        MyApplication.setControler(mBoxControler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication.addActivity(this);
        mContext = this;

        /**
         * 启动盒子链接监听线程
         * */
        startService(new Intent(mContext, BoxService.class));

        initNavigationView();

        /**
         * 浮动窗口
         */
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });*/

        //查找该 main_content.layout 里面所有 widget
        findMyView();

        //初始化网络音响
        initBoxData();

        //listType = 0， 查找并将地音频映射到 local.json
        mLocalTracks = new ArrayList<>();
        //listType = 1， 从本地favorite.json文件获取歌曲列表
        mFavoriteTracks = new ArrayList<>();
        //listType = 2， 从本地已有.json文件获取歌曲列表
        mOtherTracks = new ArrayList<>();
        initTrackList(mLocalTracks, 0);
        initTrackList(mFavoriteTracks, 0);
        mTracks = mLocalTracks;
        //初始化声音列表窗口，这个将是动态创建的的view
        initTrackListWindow(LayoutInflater.from(mContext));

        //初始化播放列表列
        initPlayListsView();
    }

    private void initTrackList(ArrayList<TrackMeta> tracks, int begin) {
        tracks.clear();
        if (tracks.equals(mLocalTracks)) {
            FileUtils.querySongs(mContext, tracks);
            mTvLocalFounded.setText("共发现 " + mLocalTracks.size() + " 首歌曲");
            //save JSON To File
            FileUtils.writeTracksToJSONFile(tracks, FileUtils.getLocalListPath(), begin);
        } else if (tracks.equals(mFavoriteTracks)) {
            if (FileUtils.isExists(FileUtils.getFavoriteListPath())) {
                FileUtils.readTracksFromJSONFile(tracks, FileUtils.getFavoriteListPath());
                FileUtils.writeTracksToJSONFile(tracks, FileUtils.getFavoriteListPath(), begin);
            }else {
                //创建空的喜爱列表
                FileUtils.writeTracksToJSONFile(null, FileUtils.getFavoriteListPath(), begin);
            }
        } else if (tracks.equals(mOtherTracks)) {
            File[] files = FileUtils.queryJSONFiles(FileUtils.getJSONFilePath());
            if (files != null && files.length > 0) {
                FileUtils.readTracksFromJSONFile(tracks, FileUtils.getFavoriteListPath());
            }else {
                //创建空的喜爱列表
                FileUtils.writeTracksToJSONFile(null, FileUtils.getFavoriteListPath(), begin);
            }
        }
    }

    private void initTrackListWindow(LayoutInflater inflater) {
        View listLayout = inflater.inflate(R.layout.layout_listview_tracks, null);
        listLayout.setFocusableInTouchMode(true);
        mTracksAdapter = new TrackAdapter(mContext, mLocalTracks);
        mTvListTitle = (TextView) listLayout.findViewById(R.id.tvListTitle);
        mPullRefreshListView = (PullToRefreshListView) listLayout.findViewById(R.id.tracksView);
        /**
         * 当 mListView 为 PullToRefreshListView 时，position从1开始，当添加了HeadView时 position从2开始
         */
        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initTrackList(mTracks, position - 1);
                if (mTracks.equals(mLocalTracks)) {
                    mBoxControler.setPlayURI(FileUtils.getLocalListPath());
                }else if (mTracks.equals(mFavoriteTracks)){
                    mBoxControler.setPlayURI(FileUtils.getFavoriteListPath());
                }
                changeListWindowState();
            }
        });
        mPullRefreshListView.setAdapter(mTracksAdapter);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mTracksWindow = new PopupWindow(listLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mTracksWindow.setAnimationStyle(R.style.MenuAnimationFade);
        mTracksWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private void initPlayListsView() {
        mPlayLists = new ArrayList<>();
        File[] files = FileUtils.queryJSONFiles(FileUtils.getJSONFilePath());
        if (files != null && files.length > 0) {
            for (File f : files) {
                String name = f.getName();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("itemTextView", name.substring(0, name.lastIndexOf(".")));
                map.put("itemPath", f.getAbsolutePath());
                mPlayLists.add(map);
            }
        }
        mPlayListsAdapter = new SimpleAdapter(mContext, mPlayLists, R.layout.item_playlist, new String[]{"itemTextView"}, new int[]{R.id.itemTextView});
        mPlayListsView.setAdapter(mPlayListsAdapter);

        // create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {    // create "delete" xm_item_album_fragment
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(JLUtils.dp2px(mContext, 70));
                deleteItem.setIcon(R.mipmap.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        mPlayListsView.setMenuCreator(creator);

        // step 2. listener xm_item_album_fragment click event
        mPlayListsView.setOnMenuItemClickListener(this);
    }

    @Override
    protected void onResume() {
        mBoxControler.setHandler(mHandler);
        mBoxControler.setStateListener(this);
        mBoxControler.setSyncing(true);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar xm_item_album_fragment clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view xm_item_album_fragment clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_info) {
            mBoxControler.setSyncing(false);
            Intent intent = new Intent(mContext, BoxInfoActivity.class);
            startActivityForResult(intent, REQCODE_BOX_CONFIG);

        } else if (id == R.id.nav_connwifi) {
            mBoxControler.setSyncing(false);
            Intent intent = new Intent(mContext, ConnWifiActivity.class);
            startActivityForResult(intent, REQCODE_BOX_CONFIG);

        } else if (id == R.id.nav_connap) {
            mBoxControler.setSyncing(false);
            Intent intent = new Intent(mContext, ConnApActivity.class);
            startActivityForResult(intent, REQCODE_BOX_CONFIG);

        } else if (id == R.id.nav_restart) {
            mBoxControler.setSyncing(false);
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle("重启设备");
            builder.setMessage("重启大概耗时 30秒 才会重新连上网络，确认重启么？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mBoxControler.restartBox();
                    dialog.cancel();

                    mProgressDialog = new MyProgressDialog(builder.getContext());
                    mProgressDialog.initDialog(false, "正在重启,请稍候...");
                    mProgressDialog.show();
                    mProgressDialog.setOnCancelListener(MainActivity.this);
                    mHandler.postDelayed(mRunnableCancelDialog, 20000);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    mBoxControler.setSyncing(true);
                }
            });
            builder.create().show();

        } else if (id == R.id.nav_reset) {
            mBoxControler.setSyncing(false);
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("恢复出厂");
            builder.setMessage("警告！恢复出厂后将还原所有设置！\n之后需 手动连接 音响设置连接网络。\n确定要重置么？");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mBoxControler.restoreBox();
                    dialog.cancel();
                    mProgressDialog = new MyProgressDialog(builder.getContext());
                    mProgressDialog.initDialog(false, "正在重置,稍后将自动重启...");
                    mProgressDialog.show();
                    mProgressDialog.setOnCancelListener(MainActivity.this);
                    mHandler.postDelayed(mRunnableCancelDialog, 20000);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    mBoxControler.setSyncing(true);
                }
            });
            builder.create().show();

        } else if (id == R.id.nav_upgrade) {
            mBoxControler.setSyncing(false);
            /**
             * 调用文件选择软件来选择文件
             */
            Intent intent = new Intent(mContext, FileListActivity.class);
            intent.putExtra("FOLDER", "");
            startActivityForResult(intent, REQCODE_BOX_CONFIG);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_exit) {
            mBoxControler.setSyncing(false);
            /**
             * 启动盒子链接监听线程
             * */
            stopService(new Intent(mContext, BoxService.class));
            MyApplication.exitApp();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 从磁盘删除播放列表
     */
    @Override
    public void onMenuItemClick(int position, SwipeMenu menu, int index) {
        HashMap<String, String> map = mPlayLists.get(position);
        String path = map.get("itemPath");
        File f = new File(path);
        if (!f.exists())
            return;

        if (f.delete()) {
            mPlayLists.remove(position);
            mPlayListsAdapter.notifyDataSetChanged();
        }
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
            JLLog.showToast(mContext, "" + e.getLocalizedMessage());
            return false;
        }

        switch (msg.what) {
            case ActionType.RestartDevice:
                if (JLJSON.getInt(jobj, "Result") != 0) {
                    mHandler.removeCallbacks(mRunnableCancelDialog);
                    mProgressDialog.dismiss();
                }
                break;
            case ActionType.RestoreDevice:
                if (JLJSON.getInt(jobj, "Result") != 0) {
                    mHandler.removeCallbacks(mRunnableCancelDialog);
                    mProgressDialog.dismiss();
                } else {
                    mProgressDialog.setMessage("正在自动重启...");
                    mBoxControler.restartBox();
                }
                break;
            default:

                break;
        }
        return false;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }

    @Override
    public void onStateChanged(final MusicBoxState state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (state.TransportState.equals("NO_MEDIA_PRESENT"))
                    return;
                if (!state.CurrentTrackURI.isEmpty()) {
                    try {
                        String src = URLDecoder.decode(state.CurrentTrackURI, "UTF-8");//注意编码和输入时一致
                        tvSongName.setText(src.substring(src.lastIndexOf("/") + 1, src.lastIndexOf(".")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                int curDuration = JLUtils.fromHHmmSS(state.RelativeTimePosition);
                int totalDarution = JLUtils.fromHHmmSS(state.CurrentTrackDuration);
                mCtrlCurTime.setText(JLUtils.formatMediaTime(curDuration));
                mCtrlTotalTime.setText(JLUtils.formatMediaTime(totalDarution));
                if (totalDarution > 0)
                    mCtrlSeekBar.setProgress(curDuration * 100 / totalDarution);
                if (state.TransportState.equals("PLAYING")) {
                    isPlaying = true;
                    mBtnPlay.setImageResource(R.drawable.ic_mini_pause);
                } else {
                    isPlaying = false;
                    mBtnPlay.setImageResource(R.drawable.ic_mini_play);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 根据返回选择的文件，来进行操作
         */
        JLLog.LOGV(TAG, "return = " + requestCode + ":" + resultCode);
        if (requestCode == REQCODE_BOX_CONFIG && resultCode == RESULT_OK) {
            finish();
        } else if (requestCode == REQUEST_RADIO_ACTIVITY && resultCode == RESULT_OK) {
            //finish();
        } else {
            mBoxControler.setHandler(mHandler);
            mBoxControler.setSyncing(true);
        }
    }

    @Override
    protected void onDestroy() {
        mBoxControler.setControling(false);
        MyApplication.removeActivity(this);
        super.onDestroy();
    }

    /**
     * 开关播放列表
     */
    private void changeListWindowState() {
        if (mTracksWindow.isShowing()) {
            // 隐藏窗口，如果设置了点击窗口外消失，则不需要此方式隐藏
            mTracksWindow.dismiss();
        } else {
            // 弹出窗口显示内容视图,默认以锚定视图的左下角为起点，这里为点击按钮
            mTracksAdapter.setDataList(mTracks);
            mTracksWindow.showAtLocation(mLyLocalMusic, Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * onClick
     */
    public void onOpenList(View view) {
        mTvListTitle.setText("本地歌曲列表：");
        mTvListTitle.setTextColor(Color.BLACK);
        mTracks = mLocalTracks;
        changeListWindowState();
    }

    public void onOpenFavorite(View view) {
        mTvListTitle.setText("喜欢歌曲列表：");
        mTvListTitle.setTextColor(Color.RED);
        mTracks = mFavoriteTracks;
        initTrackList(mFavoriteTracks, 0);
        changeListWindowState();
    }

    public void onOpenRadio(View view) {
        mBoxControler.setSyncing(false);
        Intent intent = new Intent(mContext, MainFragmentActivity.class);
        startActivity(intent);
    }

    public void onPostList(View view) {
        mBoxControler.setPlayURI(FileUtils.getLocalListPath());
    }

    public void onCreateList(View view) {
        View layout = getLayoutInflater().inflate(R.layout.dialog_build_playlist, (ViewGroup) findViewById(R.id.buildPlDialog));
        final EditText etName = (EditText) layout.findViewById(R.id.etname);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("创建播放列表").setView(layout);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etName.getText().toString();
                if (!mPlayLists.isEmpty()) {
                    for (Map<String, String> tmp : mPlayLists) {
                        if (tmp.get("itemTextView").equals(name)) {
                            JLLog.showToast(mContext, "改名字已存在，请重新输入！");
                            return;
                        }
                    }
                }
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("itemTextView", name);
                mPlayLists.add(map);
                FileUtils.writeTracksToJSONFile(null, FileUtils.newJSONFilePath(name), 1);
                mPlayListsView.refreshDrawableState();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public void onOpenPlayer(View view) {
        Intent intent = new Intent(mContext, PlayerActivity.class);
        startActivity(intent);
    }

    public void onPlay(View view) {
        isPlaying = !isPlaying;
        mBoxControler.startPlay(isPlaying);
    }

    public void onNext(View view) {
        mBoxControler.seekTo(0, 1);
    }
}
