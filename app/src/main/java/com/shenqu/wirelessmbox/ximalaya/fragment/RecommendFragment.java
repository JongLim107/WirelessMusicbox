package com.shenqu.wirelessmbox.ximalaya.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.widget.NoScrollGridView;
import com.shenqu.wirelessmbox.ximalaya.childactivity.AlbumFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.childactivity.MdataFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragment;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.DiscoveryRecommendAlbums;
import com.ximalaya.ting.android.opensdk.model.album.DiscoveryRecommendAlbumsList;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by JongLim on 2017/1/9.
 * 同样包含的热门分类
 */

public class RecommendFragment extends BaseFragment {
    private static final String TAG = "RecommendFra";
    private Context mContext;
    private ListView mListView;
    private RecommendAdapter mRecommendAdapter;
    private List<DiscoveryRecommendAlbums> mRecommendAlbums;

    private boolean mLoading = false;

    private void doLoadRecommendData() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.DISPLAY_COUNT, "3");
        CommonRequest.getDiscoveryRecommendAlbums(map, new IDataCallBack<DiscoveryRecommendAlbumsList>() {
            @Override
            public void onSuccess(DiscoveryRecommendAlbumsList albumsList) {
                //JLLog.LOGV(TAG, "getDiscoveryRecommendAlbums onSuccess.");
                if (albumsList != null && albumsList.getDiscoveryRecommendAlbumses() != null && albumsList.getDiscoveryRecommendAlbumses().size() != 0) {
                    mRecommendAlbums.clear();
                    mRecommendAlbums.addAll(albumsList.getDiscoveryRecommendAlbumses());
                    mRecommendAdapter.notifyDataSetChanged();
                }
                mLoading = false;
            }

            @Override
            public void onError(int i, String s) {
                mLoading = false;
                JLLog.LOGE(TAG, "getDiscoveryRecommendAlbums onError " + s);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.xm_fragment_listview, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        JLLog.LOGI(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        mRecommendAdapter = new RecommendAdapter();
        mListView.setAdapter(mRecommendAdapter);
        mRecommendAlbums = new ArrayList<>();
        doLoadRecommendData();
    }

    @Override
    public void refresh() {
        if (mRecommendAlbums.size() == 0)
            doLoadRecommendData();
    }

    @Override
    public void onDestroyView() {
        JLLog.LOGI(TAG, "onDestroyView");
        super.onDestroyView();
    }

    /**
     * 发现页 配置的 分类维度 -- ListView
     */
    class RecommendAdapter extends BaseAdapter {

        /**重写 ItemClick 事件，为了获取到 ListView 的 xm_item_album_fragment index*/
        class OnChildItemClickListener implements AdapterView.OnItemClickListener {
            private int parentId;

            OnChildItemClickListener(int position) {
                parentId = position;
            }

            /**
             * 点击缩略图事件
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album album = mRecommendAlbums.get(parentId).getAlbumList().get(position);
                JLLog.LOGI(TAG, "you clicked the xm_item_album_fragment " + album.getAlbumTitle());

                Intent intent = new Intent(getActivity(), AlbumFragmentActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("mAlbum", album);
                intent.putExtras(b);
                startActivity(intent);
            }
        }

        /**
         * 点击 更多 事件
         * 重写 ViewClick 事件，为了获取到 ListView 的 xm_item_album_fragment index
         */
        class OnChildClickListener implements View.OnClickListener {
            private int parentId;
            OnChildClickListener(int position) {
                parentId = position;
            }

            @Override
            public void onClick(View v) {
                DiscoveryRecommendAlbums recommend = mRecommendAlbums.get(parentId);
                Intent intent = new Intent(getActivity(), MdataFragmentActivity.class);
                intent.putExtra("CategoryId", recommend.getCategoryId());
                intent.putExtra("CategoryName", recommend.getDisplayCategoryName());
                startActivity(intent);
            }
        }

        class RecommendHolder {
            ViewGroup content;
            TextView category;
            TextView more;
            NoScrollGridView gridview;
        }

        @Override
        public int getCount() {
            if (mRecommendAlbums == null) {
                return 0;
            }
            return mRecommendAlbums.size();
        }

        @Override
        public DiscoveryRecommendAlbums getItem(int position) {
            return mRecommendAlbums.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RecommendHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.xm_item_recmd, parent, false);
                holder = new RecommendHolder();
                holder.content = (ViewGroup) convertView;
                holder.category = (TextView) convertView.findViewById(R.id.recmCategoryName);
                holder.more = (TextView) convertView.findViewById(R.id.recmCategoryMore);
                holder.gridview = (NoScrollGridView) convertView.findViewById(R.id.gvRecommend);
                convertView.setTag(holder);
            } else {
                holder = (RecommendHolder) convertView.getTag();
            }

            DiscoveryRecommendAlbums recommend = getItem(position);
            holder.category.setText(recommend.getDisplayCategoryName());
            holder.more.setOnClickListener(new OnChildClickListener(position));
            holder.content.setBackgroundColor(Color.WHITE);
            holder.gridview.setAdapter(new GridViewAdapter(recommend.getAlbumList()));
            holder.gridview.setOnItemClickListener(new OnChildItemClickListener(position));
            return convertView;
        }
    }

    /**
     * 某个分类 配置的 标签维度 -- GridView
     */
    class GridViewAdapter extends BaseAdapter {

        class GridItemHolder {
            ViewGroup content;
            ImageView ivCover;
            TextView tvIntro;
            TextView tvTitle;
        }

        private List<Album> albumList;

        GridViewAdapter(List<Album> albumList) {
            this.albumList = albumList;
        }

        @Override
        public int getCount() {
            if (albumList != null)
                return albumList.size();
            else
                return 0;
        }

        @Override
        public Album getItem(int position) {
            if (albumList != null)
                return albumList.get(position);
            else
                return null;
        }

        @Override
        public long getItemId(int position) {
            if (albumList != null)
                return position;
            else
                return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridItemHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.xm_item_recmd_grid, parent, false);
                holder = new GridItemHolder();
                holder.content = (ViewGroup) convertView;
                holder.ivCover = (ImageView) convertView.findViewById(R.id.ivCover);
                holder.tvIntro = (TextView) convertView.findViewById(R.id.tvIntro);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                convertView.setTag(holder);
            } else {
                holder = (GridItemHolder) convertView.getTag();
            }

            Album album = albumList.get(position);
            if (album.getAlbumIntro() == null || album.getAlbumIntro().length() == 0)
                holder.tvIntro.setText(album.getLastUptrack().getTrackTitle());
            else
                holder.tvIntro.setText(album.getAlbumIntro());
            holder.tvTitle.setText(album.getAlbumTitle());
            x.image().bind(holder.ivCover, album.getCoverUrlMiddle());
            return convertView;
        }
    }

}
