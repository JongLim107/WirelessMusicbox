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
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragment;
import com.shenqu.wirelessmbox.ximalaya.childactivity.AlbumFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.childactivity.MdataFragmentActivity;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategory;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategoryList;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JongLim on 2017/1/9.
 */
public class AnnouncerFragment extends BaseFragment {
    private static final String TAG = "AnnouncerFra";
    private Context mContext;
    private ListView mListView;
    private CategoryAdapter mCategoryAdapter;
    private List<AnnouncerCategory> mAnnCategories;
    private List<AnnouncerList> mAnnouncerLists;

    private boolean mLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.xm_fragment_listview, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        return view;
    }

    private void doLoadAnnCategories() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getAnnouncerCategoryList(map, new IDataCallBack<AnnouncerCategoryList>() {
            @Override
            public void onSuccess(AnnouncerCategoryList anCategoryList) {
                //JLLog.LOGV(TAG, "getDiscoveryRecommendAlbums onSuccess.");
                if (anCategoryList != null && anCategoryList.getList() != null && anCategoryList.getList().size() != 0) {
                    mAnnCategories.clear();
                    mAnnouncerLists.clear();
                    mAnnCategories.addAll(anCategoryList.getList());
                    mCategoryAdapter.notifyDataSetChanged();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        JLLog.LOGI(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        mCategoryAdapter = new CategoryAdapter();
        mListView.setAdapter(mCategoryAdapter);

        mAnnCategories = new ArrayList<>();
        mAnnouncerLists = new ArrayList<>();
        doLoadAnnCategories();
    }

    @Override
    public void refresh() {
        if (mAnnCategories.size() == 0)
            doLoadAnnCategories();
    }

    @Override
    public void onDestroyView() {
        JLLog.LOGI(TAG, "onDestroyView");
        super.onDestroyView();
    }

    /**
     * 发现页 配置的 分类维度 -- ListView
     */
    class CategoryAdapter extends BaseAdapter {

        /**
         * 点击 缩略图 事件
         * 重写 ItemClick 事件，为了获取到 ListView 的 xm_item_album_fragment index
         */
        class OnChildItemClickListener implements AdapterView.OnItemClickListener {
            private int parentId;
            OnChildItemClickListener(int position) {
                parentId = position;
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AnnouncerCategory cate = mAnnCategories.get(parentId);
                JLLog.LOGI(TAG, "you clicked the xm_item_album_fragment " + cate.getVcategoryName());

                Intent intent = new Intent(getActivity(), AlbumFragmentActivity.class);
                Bundle b = new Bundle();
                //b.putParcelable("mAlbum", cate);
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
                AnnouncerCategory anncate = mAnnCategories.get(parentId);
                Intent intent = new Intent(getActivity(), MdataFragmentActivity.class);
                intent.putExtra("CategoryId", anncate.getId());
                intent.putExtra("CategoryName", anncate.getVcategoryName());
                startActivity(intent);
            }
        }

        class CategoryHolder {
            ViewGroup content;
            TextView category;
            TextView more;
            NoScrollGridView gridview;
        }

        @Override
        public int getCount() {
            if (mAnnCategories == null) {
                return 0;
            }
            return mAnnCategories.size();
        }

        @Override
        public AnnouncerCategory getItem(int position) {
            return mAnnCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CategoryHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.xm_item_recmd, parent, false);
                holder = new CategoryHolder();
                holder.content = (ViewGroup) convertView;
                holder.category = (TextView) convertView.findViewById(R.id.recmCategoryName);
                holder.more = (TextView) convertView.findViewById(R.id.recmCategoryMore);
                holder.gridview = (NoScrollGridView) convertView.findViewById(R.id.gvRecommend);
                convertView.setTag(holder);
            } else {
                holder = (CategoryHolder) convertView.getTag();
            }

            AnnouncerCategory anncate = getItem(position);
            holder.category.setText(anncate.getVcategoryName());
            holder.more.setOnClickListener(new OnChildClickListener(position));
            holder.content.setBackgroundColor(Color.WHITE);
            holder.gridview.setAdapter(new GridViewAdapter(position));
            holder.gridview.setOnItemClickListener(new OnChildItemClickListener(position));
            return convertView;
        }
    }

    /**
     * 某个分类 配置的 标签维度 -- GridView
     */
    class GridViewAdapter extends BaseAdapter {
        private int parentId;
        private List<Announcer> mAnnouncers;

        private void doLoadAnnouncerList(final GridViewAdapter gridViewAdapter, final List<Announcer> announcers, final int parentId) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.VCATEGORY_ID, "" + mAnnCategories.get(parentId).getId());
            map.put(DTransferConstants.CALC_DIMENSION, "3");
            map.put(DTransferConstants.PAGE_SIZE, "3");
            CommonRequest.getAnnouncerList(map, new IDataCallBack<AnnouncerList>() {
                @Override
                public void onSuccess(AnnouncerList announcerList) {
                    //JLLog.LOGV(TAG, "getDiscoveryRecommendAlbums onSuccess.");
                    if (announcerList != null && announcerList.getAnnouncerList() != null && announcerList.getAnnouncerList().size() != 0) {
                        mAnnouncerLists.add(announcerList);
                        announcers.addAll(announcerList.getAnnouncerList());
                        gridViewAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(int i, String s) {
                }
            });
        }

        GridViewAdapter(int position) {
            parentId = position;
            mAnnouncers = new ArrayList<>();
            if (parentId < mAnnouncerLists.size())
                mAnnouncers = mAnnouncerLists.get(parentId).getAnnouncerList();
            else
                doLoadAnnouncerList(this, mAnnouncers, parentId);
        }

        class GridItemHolder {
            ViewGroup content;
            ImageView ivCover;
            TextView tvIntro;
            TextView tvTitle;
        }

        @Override
        public int getCount() {
            if (mAnnouncers != null)
                return mAnnouncers.size();
            else
                return 0;
        }

        @Override
        public Announcer getItem(int position) {
            if (mAnnouncers != null)
                return mAnnouncers.get(position);
            else
                return null;
        }

        @Override
        public long getItemId(int position) {
            if (mAnnouncers != null)
                return position;
            else
                return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridViewAdapter.GridItemHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.xm_item_recmd_grid, parent, false);
                holder = new GridViewAdapter.GridItemHolder();
                holder.content = (ViewGroup) convertView;
                holder.ivCover = (ImageView) convertView.findViewById(R.id.ivCover);
                holder.tvIntro = (TextView) convertView.findViewById(R.id.tvIntro);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                convertView.setTag(holder);
            } else {
                holder = (GridViewAdapter.GridItemHolder) convertView.getTag();
            }

            Announcer announcer = getItem(position);
            if (announcer != null) {
                holder.tvTitle.setText(announcer.getNickname());
                holder.tvIntro.setVisibility(View.GONE);
                x.image().bind(holder.ivCover, announcer.getAvatarUrl());
            }
            return convertView;
        }
    }

}
