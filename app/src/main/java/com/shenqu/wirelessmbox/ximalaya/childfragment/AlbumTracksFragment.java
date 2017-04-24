package com.shenqu.wirelessmbox.ximalaya.childfragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.shenqu.wirelessmbox.MyApplication;
import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.widget.LoadMoreFooterView;
import com.shenqu.wirelessmbox.ximalaya.childactivity.AlbumFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.adapter.IRecyclerTrackAdapter;
import com.shenqu.wirelessmbox.ximalaya.adapter.OnItemClickListener;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragment;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JongLim on 2017/1/9.
 * Activity for AlbumFragmentActivity.
 * */

public class AlbumTracksFragment extends BaseFragment implements OnItemClickListener<Track>, OnLoadMoreListener, OnRefreshListener {
    private static final String TAG = "AlbumTracksFra";
    public static final String TITLE = "title";
    private String mTitle = "Defaut Value";

    private AlbumFragmentActivity mActivity;
    private Album mAlbum;

    private IRecyclerView mRecyclerView;
    private LoadMoreFooterView mFooterView;
    private IRecyclerTrackAdapter mTrackAdapter;
    private List<Track> mTrackList;

    private boolean isLoading;
    private int iTracksPage;

    public static AlbumTracksFragment newInstance(String title) {
        AlbumTracksFragment tabFragment = new AlbumTracksFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TITLE);
        }
        mActivity = (AlbumFragmentActivity) getActivity();
        mAlbum = mActivity.mAlbum;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xm_fragment_tracks, container, false);

        mRecyclerView = (IRecyclerView) view.findViewById(R.id.iRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //mRecyclerView.addHeaderView(bannerView);

        mFooterView = (LoadMoreFooterView) mRecyclerView.getLoadMoreFooterView();

        mTrackList = new ArrayList<>();
        mTrackAdapter = new IRecyclerTrackAdapter(mTrackList);
        mTrackAdapter.setOnItemClickListener(this);

        mRecyclerView.setIAdapter(mTrackAdapter);
        mRecyclerView.setOnLoadMoreListener(this);
        mRecyclerView.setOnRefreshListener(this);

        iTracksPage = 1;
        doLoadAlbumTracks();
        return view;
    }

    private void doLoadAlbumTracks() {
        if (isLoading)
            return;
        isLoading = true;
        //获取某个专辑的相关推荐
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, "" + mAlbum.getId());
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, "" + iTracksPage);
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                JLLog.LOGV(TAG, "getTracks onSuccess.");
                if (trackList == null || trackList.getTracks() == null || trackList.getTracks().size() == 0) {
                    mFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                } else {
                    mFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                    isLoading = false;
                    if (iTracksPage == 1)
                        mTrackList.clear();
                    mTrackList.addAll(trackList.getTracks());
                    mTrackAdapter.notifyDataSetChanged();
                }
                mRecyclerView.setRefreshing(false);
            }
            @Override
            public void onError(int i, String s) {
                JLLog.LOGE(TAG, "getTracks onSuccess.");
                mRecyclerView.setRefreshing(false);
                mFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
                isLoading = false;
            }
        });
    }

    @Override
    public void onItemClick(int position, Track track, View v) {
        MyApplication.getControler().setHandler(mActivity.mHandler);
        MyApplication.getControler().setPlayURI(mTrackList.get(position).getPlayUrl64());
    }

    @Override
    public void onLoadMore() {
        if (mFooterView.canLoadMore() && mTrackAdapter.getItemCount() > 0) {
            mFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
            iTracksPage++;
            doLoadAlbumTracks();
        }
        else mFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
    }

    @Override
    public void onRefresh() {
        iTracksPage = 1;
        doLoadAlbumTracks();
    }
}
