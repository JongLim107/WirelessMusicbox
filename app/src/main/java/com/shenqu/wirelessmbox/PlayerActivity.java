package com.shenqu.wirelessmbox;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.shenqu.wirelessmbox.action.ActionType;
import com.shenqu.wirelessmbox.action.BoxControler;
import com.shenqu.wirelessmbox.base.BaseActivity;
import com.shenqu.wirelessmbox.bean.MusicBoxState;
import com.shenqu.wirelessmbox.bean.TrackAdapter;
import com.shenqu.wirelessmbox.bean.TrackMeta;
import com.shenqu.wirelessmbox.tools.FileUtils;
import com.shenqu.wirelessmbox.tools.JLJSON;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.tools.JLUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Created by JongLim on 2016/11/24.
 */

public class PlayerActivity extends BaseActivity implements Handler.Callback, BoxControler.OnBoxPlayStateListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "PlayerAct";

    private RelativeLayout lyTitle;
    private ImageView btnLeft;
    private TextView tvSongName, tvArtist;

    private BoxControler mBoxControler;
    private boolean isPlaying = false;

    //整个activity的layout， 设置背景
    private RelativeLayout mLayout;

    private ImageView mIVAlbum;
    private TextView mCtrlCurTime;
    private TextView mCtrlTotalTime;
    private SeekBar mCtrlSeekBar;
    private ImageView mIVFavorite;

    private ImageView mBtnPlay;
    private Handler mHandler = new Handler(this);
    private TextView mTvListTitle;

    /**
     * 歌曲列表
     */
    private PullToRefreshListView mPullRefreshListView;
    private TrackMeta mTrackMeta;                   // 当前播放的歌曲
    private ArrayList<TrackMeta> mTracks;           // 当前播放器歌曲列表，有以下几种2种列表
    private ArrayList<TrackMeta> mFavoriteTracks;   // 2.最爱歌曲列表数据源
    private PopupWindow mTracksWindow;              // 歌曲列表弹出窗口，包含歌曲ListView
    private TrackAdapter mTracksAdapter;
    Animation anim_loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        findViews();
        initView();

        LayoutInflater inflater = LayoutInflater.from(this);
        initTrackListWindow(inflater);
        initFavoriteList();
        mBoxControler = MyApplication.getControler();
    }

    private void initFavoriteList() {
        mFavoriteTracks = new ArrayList<TrackMeta>();
        FileUtils.readTracksFromJSONFile(mFavoriteTracks, FileUtils.getFavoriteListPath());
        FileUtils.writeTracksToJSONFile(mFavoriteTracks, FileUtils.getFavoriteListPath(), 1);
    }

    private void findViews() {
        mLayout = (RelativeLayout) findViewById(R.id.playLayout);
        lyTitle = (RelativeLayout) findViewById(R.id.lyTitle);
        btnLeft = (ImageView) findViewById(R.id.btnLeft);
        tvSongName = (TextView) findViewById(R.id.tvSongName);
        //tvArtist = (TextView) findViewById(R.id.tvArtist);

        mCtrlCurTime = (TextView) findViewById(R.id.ctrlCurTime);
        mCtrlTotalTime = (TextView) findViewById(R.id.ctrlTotalTime);
        mCtrlTotalTime = (TextView) findViewById(R.id.ctrlTotalTime);
        mCtrlSeekBar = (SeekBar) findViewById(R.id.playSeekBar);
        mIVAlbum = (ImageView) findViewById(R.id.ivAlbum);
        mIVFavorite = (ImageView) findViewById(R.id.ivFavorite);

        mBtnPlay = (ImageView) findViewById(R.id.ctrlStart);

        anim_loading = AnimationUtils.loadAnimation(this, R.anim.load_animation);
    }

    private void initView() {
        View statusBar = findViewById(R.id.statueBar);
        //判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            statusBar.setVisibility(View.GONE);
        } else
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Bitmap bmp = MyApplication.getACache().getAsBitmap(MyApplication.gCacheBmp);
        if (bmp != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mLayout.setBackground(new BitmapDrawable(getResources(), bmp));
            }
        tvSongName.setText("没有歌曲");
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        hidebtn_right();
        mCtrlSeekBar.setOnSeekBarChangeListener(this);
    }

    private void initTrackListWindow(LayoutInflater inflater) {
        View listLayout = inflater.inflate(R.layout.layout_listview_tracks, null);
        listLayout.setFocusableInTouchMode(true);
        mTvListTitle = (TextView) listLayout.findViewById(R.id.tvListTitle);
        mTvListTitle.setText("音响歌曲列表");

        mTracks = new ArrayList<TrackMeta>();
        mTracksAdapter = new TrackAdapter(this, mTracks);
        mPullRefreshListView = (PullToRefreshListView) listLayout.findViewById(R.id.tracksView);
        mPullRefreshListView.setAdapter(mTracksAdapter);
        /**
         * 当 mListView 为 PullToRefreshListView 时，position从1开始，当添加了HeadView时 position从2开始
         */
        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBoxControler.seekTo(position, 0);
                changeListWindowState();
            }
        });
        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                doGetPlayList();
            }
        });

        mTracksWindow = new PopupWindow(listLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
        mTracksWindow.setAnimationStyle(R.style.MenuAnimationFade);
        mTracksWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBoxControler.setHandler(mHandler);
        mBoxControler.setStateListener(this);
        doGetPlayList();
    }

    @Override
    public void onStateChanged(final MusicBoxState state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //if (state.RelativeTimePosition.contains("00:00:")) {
                if (state.TransportState.equals("NO_MEDIA_PRESENT"))
                    return;
                if (!state.CurrentTrackURI.isEmpty()) {
                    try {
                        String src = URLDecoder.decode(state.CurrentTrackURI, "UTF-8");//注意编码和输入时一致
                        tvSongName.setText(src.substring(src.lastIndexOf("/") + 1, src.lastIndexOf(".")));
                        //tvArtist.setText("Unknow Artist");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                int curDuration = JLUtils.fromHHmmSS(state.RelativeTimePosition);
                int totalDarution = JLUtils.fromHHmmSS(state.CurrentTrackDuration);
                mCtrlCurTime.setText(JLUtils.formatMediaTime(curDuration));
                mCtrlTotalTime.setText(JLUtils.formatMediaTime(totalDarution));
                if (!mTracks.isEmpty()) {
                    boolean notExist = true;
                    mTrackMeta = mTracks.get(state.CurrentTrack - 1);
                    for (TrackMeta track : mFavoriteTracks) {
                        if (track.getName().equals(mTrackMeta.getName()) && track.getId().equals(mTrackMeta.getId())) {
                            notExist = false;
                        }
                    }
                    if (notExist)
                        mIVFavorite.setImageResource(R.mipmap.ic_favorite);
                    else
                        mIVFavorite.setImageResource(R.mipmap.ic_favorited);
                }
                if (totalDarution > 0)
                    mCtrlSeekBar.setProgress(curDuration * 100 / totalDarution);
                if (state.TransportState.equals("PLAYING")) {
                    if (!isPlaying)
                        mIVAlbum.startAnimation(anim_loading);
                    isPlaying = true;
                    mBtnPlay.setImageResource(R.drawable.ic_pause);
                } else {
                    if (isPlaying)
                        mIVAlbum.clearAnimation();
                    isPlaying = false;
                    mBtnPlay.setImageResource(R.drawable.ic_play);
                }
                //}
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case ActionType.PlayerDoPlay: {
                mBtnPlay.setImageResource(R.drawable.ic_pause);
                break;
            }
            case ActionType.PlayerDoPause: {
                mBtnPlay.setImageResource(R.drawable.ic_play);
                break;
            }
            case ActionType.PlayerDoNext:
            case ActionType.SetAVTransportURI: {
                JLLog.showToast(this, "开始播放~");
                break;
            }
            case ActionType.GetPlaylist: {
                mPullRefreshListView.onRefreshComplete();
                Bundle b = msg.getData();
                String string = b.getString("JSONDATA");
                if (string == null) {
                    JLLog.showToast(this, "获取音响播放列表失败~");
                    return true;
                }
                JSONObject jobj = null;
                try {
                    jobj = new JSONObject(string);
                    if (JLJSON.getInt(jobj, "Result") == 0) {
                        FileUtils.getListFromJSON(mTracks, JLJSON.getJSONObject(jobj, "Body"));
                        mTracksAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    JLLog.showToast(this, "" + e.getLocalizedMessage());
                    doGetPlayList();
                    return false;
                }
                break;
            }
            default:

                break;
        }
        return false;
    }

    public void onPrevious(View view) {
        mBoxControler.seekTo(0, -1);
    }

    public void onPlay(View view) {
        isPlaying = !isPlaying;
        mBoxControler.startPlay(isPlaying);
    }

    public void onNext(View view) {
        mBoxControler.seekTo(0, 1);
    }

    public void onGetTracks(View view) {
        doGetPlayList();
        changeListWindowState();
    }

    private void doGetPlayList() {
        mTracks.clear();
        mTracksAdapter.notifyDataSetChanged();
        mBoxControler.getPlayList(mHandler, 0);
    }

    private void changeListWindowState() {
        if (mTracksWindow.isShowing()) {
            // 隐藏窗口，如果设置了点击窗口外消失，则不需要此方式隐藏
            mTracksWindow.dismiss();
        } else {
            // 弹出窗口显示内容视图,默认以锚定视图的左下角为起点，这里为点击按钮
            mTracksWindow.showAtLocation(mIVAlbum, Gravity.BOTTOM, 0, 0);
        }
    }

    // Called when a key was pressed down and not handled by any of the views
    // inside of the activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:// 菜单键监听
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        JLLog.LOGV(TAG, "Begin to tracking.");
        mBoxControler.setSyncing(false);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        String total = mBoxControler.getPlayState().CurrentTrackDuration;
        int cur = JLUtils.fromHHmmSS(total) * 10 * seekBar.getProgress(); // = (total*1000) / 100 * progress
        final String str = JLUtils.formatHHmmSS(cur);
        try {
            mBoxControler.seekTo(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JLLog.LOGV(TAG, "Ended the tracking.");
        mBoxControler.setSyncing(true);
    }

    public void onAdd2Favorite(View view) {
        if (mTrackMeta == null)
            return;
        if (!mTracks.isEmpty()) {
            for (TrackMeta track : mFavoriteTracks) {
                if ( track.getName().equals(mTrackMeta.getName()) && track.getId().equals(mTrackMeta.getId())) {
                    mFavoriteTracks.remove(track);
                    mIVFavorite.setImageResource(R.mipmap.ic_favorite);
                    FileUtils.writeTracksToJSONFile(mFavoriteTracks, FileUtils.getFavoriteListPath(), mFavoriteTracks.size() - 1);
                    return;
                }
            }
            mFavoriteTracks.add(mTrackMeta);
            JLLog.showToast(this, "已添加到 [我的最爱]");
            mIVFavorite.setImageResource(R.mipmap.ic_favorited);
            FileUtils.writeTracksToJSONFile(mFavoriteTracks, FileUtils.getFavoriteListPath(), mFavoriteTracks.size() - 1);
        }
    }

    public void onAdd2Other(View view) {

    }
}
