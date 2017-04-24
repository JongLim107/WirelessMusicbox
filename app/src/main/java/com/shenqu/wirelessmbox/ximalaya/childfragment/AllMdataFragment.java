package com.shenqu.wirelessmbox.ximalaya.childfragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

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
import com.ximalaya.ting.android.opensdk.model.metadata.Attributes;
import com.ximalaya.ting.android.opensdk.model.metadata.MetaData;
import com.ximalaya.ting.android.opensdk.model.metadata.MetaDataList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JongLim on 2017/1/9.
 * MdataFragmentActivity 下面 SmartTabLayout 对应的 Fragment
 */

public class AllMdataFragment extends BaseFragment implements OnItemClickListener<Album>, OnRefreshListener, OnLoadMoreListener {
    private static final String TAG = "AllMdataFra";
    private LayoutInflater mInflater;

    private String mCategoryId;
    private String categoryName;
    private List<MetaData> mMetaDatas;
    /**
     * 创建标签列表 加上手动创建的 无过滤 项
     * mMetaDatas 最终解析出来后将元素 存储于mAttrList
     */
    private List<List<Attributes>> mAttrList;
    private int[] iSelectedAttr;
    
    private final String[] ATTR_PAY = {/*"不限", */"付费", "免费"};
    private final String[] ATTR_DIMENSION = {/*"默认", */"最火", "最新", "经典"};
    private final String[] CALC_DIMENSION = {"1", "1", "2", "3"};//计算维度，现支持最火（1），最新（2），经典或播放最多（3）
    private int iAlbumPage = 1;

    private IRecyclerView mRecyclerView;
    private LoadMoreFooterView mFooterView;
    private IRefreshHeaderView mRefreshView;
    private IRecyclerAlbumAdapter mAlbumsAdapter;
    private List<Album> mAlbumList;

    private boolean isLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategoryId = getActivity().getIntent().getStringExtra("CategoryId");
        categoryName = getActivity().getIntent().getStringExtra("CategoryName");
        mMetaDatas = new ArrayList<>();
        mAttrList = new ArrayList<>();
        mAlbumList = new ArrayList<>();
    }

    private void initMetaView() {
        MetaData m = new MetaData();

        //手动添加 ATTR_DIMENSION Meta 类型
        List<Attributes> as = new ArrayList<>();
        for (String st : ATTR_DIMENSION) {
            Attributes a = new Attributes();
            a.setDisplayName(st);
            a.setAttrKey("");
            a.setAttrValue("");
            as.add(a);
        }

        m.setAttributes(as);
        m.setDisplayName("默认");
        mMetaDatas.add(m);

        //各种标签栏的 content
        View header = mInflater.inflate(R.layout.xm_layout_tab_content, mRecyclerView.getHeaderContainer(), false);
        LinearLayout tabLayout = (LinearLayout) header.findViewById(R.id.tabLayoutContainer);

        int i = 0;  //mMetaDatas size, 也作为 Attrs 行数
        for (MetaData meta : mMetaDatas) {
            //包含标签栏
            View tabContainer = mInflater.inflate(R.layout.xm_item_tabholder, tabLayout, false);
            RecyclerView rcTabView = (RecyclerView) tabContainer.findViewById(R.id.rcTabView);

            //为RecyclerView设置布局管理器
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rcTabView.setLayoutManager(layoutManager);

            /**将 无过滤 选项手动加入*/
            Attributes attr = new Attributes();
            attr.setAttrKey("");
            attr.setAttrValue("");
            attr.setDisplayName(meta.getDisplayName()); //全部

            List<Attributes> attrs = new ArrayList<>();
            attrs.add(attr);
            attrs.addAll(meta.getAttributes());
            rcTabView.setAdapter(new TabAdapter(getContext(), attrs, i++));
            mAttrList.add(attrs);

            tabLayout.addView(tabContainer);
        }

        mRecyclerView.addHeaderView(header);
        mRecyclerView.setIAdapter(mAlbumsAdapter);
        mRecyclerView.setOnLoadMoreListener(this);

        /**初始化metas对应的index数组*/
        iSelectedAttr = new int[i];
    }

    private void doLoadAlbumsData(boolean isInit) {
        if (isLoading)
            return;
        isLoading = true;

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, mCategoryId);
        if (!isInit) {
            String attrStr = "";
            for (int i = 0; i < mAttrList.size(); i++) {
                Attributes attr = mAttrList.get(i).get(iSelectedAttr[i]);
                if (attr.getAttrKey().length() > 0) {
                    //attr_key1:attr_value1;attr_key2:attr_value2
                    attrStr += attr.getAttrKey() + ":" + attr.getAttrValue() + ";";
                }
            }
            if (attrStr.length() > 2) {
                map.put(DTransferConstants.METADATA_ATTRIBUTES, attrStr.substring(0, attrStr.length() - 1));
            }
        }

        map.put(DTransferConstants.CALC_DIMENSION, CALC_DIMENSION[iSelectedAttr[iSelectedAttr.length - 1]]);
        map.put(DTransferConstants.PAGE, "" + iAlbumPage);
        JLLog.LOGI(TAG, "getMetadataAlbumList param = " + map.toString());
        CommonRequest.getMetadataAlbumList(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(AlbumList albumList) {
                isLoading = false;
                if (albumList != null && albumList.getAlbums() != null && albumList.getAlbums().size() > 0) {
                    mAlbumList.addAll(albumList.getAlbums());
                    mAlbumsAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onError(int i, String s) {
                JLLog.showToast(getActivity(), "加载失败~");
                isLoading = false;
            }
        });
    }

    private void doLoadMetaData() {
        if (isLoading)
            return;
        isLoading = true;

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, mCategoryId);
        CommonRequest.getMetadataList(map, new IDataCallBack<MetaDataList>() {
            @Override
            public void onSuccess(MetaDataList metaDataList) {
                isLoading = false;
                if (metaDataList != null && metaDataList.getMetaDatas() != null) {
                    mMetaDatas.clear();
                    mMetaDatas.addAll(metaDataList.getMetaDatas());
                    initMetaView();
                    doLoadAlbumsData(true);
                }
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
                JLLog.showToast(getActivity(), "加载失败~");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View view = mInflater.inflate(R.layout.xm_fragment_albums, container, false);
        mRecyclerView = (IRecyclerView) view.findViewById(R.id.iRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAlbumsAdapter = new IRecyclerAlbumAdapter(mAlbumList);
        mAlbumsAdapter.setOnItemClickListener(this);
        mFooterView = (LoadMoreFooterView) mRecyclerView.getLoadMoreFooterView();

        mMetaDatas.clear();
        mAttrList.clear();
        mAlbumList.clear();
        doLoadMetaData();
        return view;
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
        doLoadAlbumsData(true);
    }

    @Override
    public void onLoadMore() {
        if (mFooterView.canLoadMore() && mAlbumsAdapter.getItemCount() > 0) {
            mFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
            iAlbumPage++;
            doLoadAlbumsData(false);
        } else
            mFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
    }

    /**
     * 横向listView 即：RecycleView 的 Adapter
     */
    class TabAdapter extends RecyclerView.Adapter<TabAdapter.TabHolder> implements View.OnClickListener {

        class TabHolder extends RecyclerView.ViewHolder {
            TabHolder(View itemView) {
                super(itemView);
            }
            CheckedTextView tvMeta;
        }

        private int iRow;
        private LayoutInflater mInflater;
        private List<Attributes> mAttrs;

        /**
         * 构造函数
         */
        TabAdapter(Context context, List<Attributes> a, int i) {
            mInflater = LayoutInflater.from(context);
            mAttrs = a;
            iRow = i;
        }

        @Override
        public int getItemCount() {
            return mAttrs.size();
        }

        /**
         * 创建ViewHolder
         */
        @Override
        public TabHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.xm_item_hortab, viewGroup, false);
            TabHolder holder = new TabHolder(view);
            holder.tvMeta = (CheckedTextView) view.findViewById(R.id.tvTitle);
            holder.tvMeta.setOnClickListener(this);
            return holder;
        }

        /**
         * 设置值
         */
        @Override
        public void onBindViewHolder(TabHolder holder, int i) {
            //JLLog.LOGI(TAG, "------ onBindViewHolder() " + i);
            holder.tvMeta.setText(mAttrs.get(i).getDisplayName());
            holder.tvMeta.setTag(i);
            if (i == iSelectedAttr[iRow])
                holder.tvMeta.setChecked(true);
            else
                holder.tvMeta.setChecked(false);
        }

        @Override
        public void onClick(View v) {
            iSelectedAttr[iRow] = (int) v.getTag();
            notifyDataSetChanged();
            if (mAlbumList != null)
                mAlbumList.clear();
            iAlbumPage = 1;
            doLoadAlbumsData(false);
        }
    }

}
