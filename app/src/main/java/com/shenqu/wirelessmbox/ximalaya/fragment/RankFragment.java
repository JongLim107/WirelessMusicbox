/**
 * RecommendCateFra.java
 * com.ximalaya.ting.android.opensdk.test
 * <p/>
 * <p/>
 * ver     date      		author
 * ---------------------------------------
 * 2015-6-4 		chadwii
 * <p/>
 * Copyright (c) 2015, chadwii All Rights Reserved.
 */

package com.shenqu.wirelessmbox.ximalaya.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragment;
import com.shenqu.wirelessmbox.ximalaya.base.ViewHolder;
import com.shenqu.wirelessmbox.ximalaya.util.ToolUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.ranks.Rank;
import com.ximalaya.ting.android.opensdk.model.ranks.RankList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JongLim on 2017/1/9.
 */
public class RankFragment extends BaseFragment {
    private static final String TAG = "RankFragment";
    private Context mContext;
    private ListView mListView;

    private RankAdapter mAdapter;
    private RankList mRankList;

    private boolean mLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.xm_fragment_listview, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();

        mAdapter = new RankAdapter();
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mRankList != null && mRankList.getRankList() != null) {
                    Rank rank = mRankList.getRankList().get(position);
                    rank.getRankItemList().get(0).getDataId();
                }
            }
        });

        loadData();
    }

    private void loadData() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        Map<String, String> param = new HashMap<String, String>();
        CommonRequest.getRankList(param, new IDataCallBack<RankList>() {
            @Override
            public void onSuccess(RankList rankList) {
                
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    @Override
    public void refresh() {
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private class RankAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mRankList == null || mRankList.getRankList() == null) {
                return 0;
            }
            return mRankList.getRankList().size();
        }

        @Override
        public Object getItem(int position) {
            return mRankList.getRankList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.xm_item_rank, parent, false);
                holder = new ViewHolder();
                holder.content = (ViewGroup) convertView;
                holder.title = (TextView) convertView.findViewById(R.id.trackname);
                holder.intro = (TextView) convertView.findViewById(R.id.intro);
                holder.cover = (ImageView) convertView.findViewById(R.id.imageview);
                holder.status = (TextView) convertView.findViewById(R.id.status);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Rank rank = mRankList.getRankList().get(position);
            holder.title.setText(ToolUtil.isEmpty(rank.getRankTitle()) ? "无节目" : rank.getRankTitle());
            holder.intro.setText(rank.getRankKey());

            holder.content.setBackgroundColor(Color.WHITE);
            holder.status.setText("WAIT");
            holder.status.setBackgroundColor(Color.TRANSPARENT);
            return convertView;
        }

    }
}
