package com.shenqu.wirelessmbox.ximalaya.childfragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.widget.IRefreshHeaderView;
import com.shenqu.wirelessmbox.widget.LoadMoreFooterView;
import com.shenqu.wirelessmbox.ximalaya.adapter.IRecyclerAlbumAdapter;
import com.shenqu.wirelessmbox.ximalaya.adapter.OnItemClickListener;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragment;
import com.shenqu.wirelessmbox.ximalaya.childactivity.AlbumFragmentActivity;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by JongLim on 2017/1/9.
 * MdataFragmentActivity 下面 SmartTabLayout 对应的 Fragment
 */

public class AlbumListFragment extends BaseFragment implements OnItemClickListener<Album>, OnLoadMoreListener, OnRefreshListener {
    private static final String TAG = "AlbumListFra";
    private Context mContext;
    private IRecyclerView mRecyclerView;
    private LoadMoreFooterView mFooterView;
    private IRefreshHeaderView mHeaderView;
    private IRecyclerAlbumAdapter mAlbumsAdapter;
    private List<Album> mAlbumList;
    private int iAlbumPage = 1;

    private boolean isLoading = false;

    private String mTagName = "";
    private String mCategoryId = "";

    private void doLoadAlbumsData() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, mCategoryId);
        map.put(DTransferConstants.TAG_NAME, mTagName);
        map.put(DTransferConstants.CALC_DIMENSION, "3");
        map.put(DTransferConstants.PAGE, "" + iAlbumPage);
        CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(AlbumList albumList) {
                if (albumList != null && albumList.getAlbums() != null && albumList.getAlbums().size() > 0) {
                    mAlbumList.addAll(albumList.getAlbums());
                    mAlbumsAdapter.notifyDataSetChanged();
                    mFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                } else {
                    mFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                }
                isLoading = false;
            }

            @Override
            public void onError(int i, String s) {
                mFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
                JLLog.showToast(getActivity(), "加载失败~");
                isLoading = false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.xm_fragment_albums, container, false);
        mRecyclerView = (IRecyclerView) view.findViewById(R.id.iRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAlbumList = new ArrayList<>();
        mAlbumsAdapter = new IRecyclerAlbumAdapter(mAlbumList);
        mAlbumsAdapter.setOnItemClickListener(this);

        mRecyclerView.setIAdapter(mAlbumsAdapter);
        mRecyclerView.setOnRefreshListener(this);
        mRecyclerView.setOnLoadMoreListener(this);

        mHeaderView = (IRefreshHeaderView) mRecyclerView.getRefreshHeaderView();
        mFooterView = (LoadMoreFooterView) mRecyclerView.getLoadMoreFooterView();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mTagName = getArguments().getString("TAGNAME");
        mCategoryId = getArguments().getString("CATEGORYID");
    }

    @Override
    public void onResume() {
        super.onResume();
        doLoadAlbumsData();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onItemClick(int position, Album album, View v) {
        JLLog.LOGI(TAG, "You clicked the " + position + " item.");

        Intent intent = new Intent(getActivity(), AlbumFragmentActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("mAlbum", mAlbumList.get(position));
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        iAlbumPage = 1;
        mAlbumList.clear();
        doLoadAlbumsData();
    }

    @Override
    public void onLoadMore() {
        if (mFooterView.canLoadMore() && mAlbumsAdapter.getItemCount() > 0) {
            mFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
            iAlbumPage++;
            doLoadAlbumsData();
        } else
            mFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
    }
}
