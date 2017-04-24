package com.shenqu.wirelessmbox.ximalaya.childfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.ximalaya.childactivity.AlbumFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.adapter.IRecyclerAlbumAdapter;
import com.shenqu.wirelessmbox.ximalaya.adapter.OnItemClickListener;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.RelativeAlbums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JongLim on 2017/1/9.
 * Activity for AlbumFragmentActivity.
 * */

public class AlbumDetailFragment extends Fragment implements OnItemClickListener<Album> {
    private static final String TAG = "AlbumDetailFra";
    public static final String TITLE = "title";
    private String mTitle = "Defaut Value";

    private AlbumFragmentActivity mActivity;
    private Album mAlbum;

    private TextView tvAlbumIntro;
    private TextView tvAnnouncer;
    private IRecyclerView mRecyclerView;
    private IRecyclerAlbumAdapter mAlbumsAdapter;
    private List<Album> mAlbumList;

    private boolean isLoading;

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
        View view = inflater.inflate(R.layout.xm_fragment_albums, container, false);
        mRecyclerView = (IRecyclerView) view.findViewById(R.id.iRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAlbumList = new ArrayList<>();
        mAlbumsAdapter = new IRecyclerAlbumAdapter(mAlbumList);
        mAlbumsAdapter.setOnItemClickListener(this);
        mRecyclerView.setIAdapter(mAlbumsAdapter);

        View childView = inflater.inflate(R.layout.xm_layout_album_detail, mRecyclerView.getHeaderContainer(), false);
        tvAlbumIntro = (TextView) childView.findViewById(R.id.tvAlbumIntro);
        tvAnnouncer = (TextView) childView.findViewById(R.id.tvAnnouncer);
        tvAlbumIntro.setText(mAlbum.getAlbumIntro());
        tvAnnouncer.setText(mAlbum.getAnnouncer().getNickname());

        mRecyclerView.addHeaderView(childView);

        doLoadAlbumDetail();
        return view;
    }

    private void doLoadAlbumDetail() {
        if (isLoading)
            return;
        isLoading = true;
        //获取某个专辑的相关推荐
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUMID, mAlbum.getId() + "");
        CommonRequest.getRelativeAlbums(map, new IDataCallBack<RelativeAlbums>() {
            @Override
            public void onSuccess(RelativeAlbums relativeAlbums) {
                isLoading = false;
                if (relativeAlbums != null && relativeAlbums.getRelativeAlbumList() != null) {
                    mAlbumList.addAll(relativeAlbums.getRelativeAlbumList());
                    mAlbumsAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onError(int i, String s) {
                isLoading = false;
            }
        });
    }

    public static AlbumDetailFragment newInstance(String title) {
        AlbumDetailFragment tabFragment = new AlbumDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onItemClick(int position, Album album, View v) {
        JLLog.LOGI(TAG, "You clicked the " + position + " item.");

        getActivity().finish();

        JLLog.LOGV(TAG, "You clicked the " + position + " item.");
        Intent intent = new Intent(getActivity(), AlbumFragmentActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("mAlbum", mAlbumList.get(position));
        intent.putExtras(b);
        startActivity(intent);
    }
}
