/**
 * RadiosFragment.java
 * com.ximalaya.ting.android.opensdk.test
 * <p/>
 * <p/>
 * ver     date      		author
 * ──────────────────────────────────
 * 2015-5-25 		jack.qin
 * <p/>
 * Copyright (c) 2015, TNT All Rights Reserved.
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
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JongLim on 2017/1/9.
 */
public class RadiosFragment extends BaseFragment {
    private static final String TAG = "RadiosFra";
    private int mRadioType = 2;
    private RadioAdapter mRadioAdapter;
    private int mProvinceCode = 360000;
    private List<Radio> mRadios = new ArrayList<Radio>();
    private ListView mListView;

    private Context mContext;

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
        mRadioAdapter = new RadioAdapter();
        mListView.setAdapter(mRadioAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Radio radio = mRadios.get(position);
            }
        });

        loadRadios();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void refresh() {
        loadRadios();
    }

    public void loadRadios() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOTYPE, "" + mRadioType);
        map.put(DTransferConstants.PROVINCECODE, "" + mProvinceCode);
        CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {

            @Override
            public void onSuccess(RadioList object) {
                if (object != null && object.getRadios() != null) {
                    mRadios.clear();
                    mRadios.addAll(object.getRadios());
                    mRadioAdapter.notifyDataSetChanged();
                }
                mLoading = false;
            }

            @Override
            public void onError(int code, String message) {
                mLoading = false;
            }
        });
    }

    class RadioAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mRadios.size();
        }

        @Override
        public Object getItem(int position) {
            return mRadios.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.xm_item_category_track, parent, false);
                holder = new ViewHolder();
                holder.content = (ViewGroup) convertView;
                holder.title = (TextView) convertView.findViewById(R.id.trackname);
                holder.intro = (TextView) convertView.findViewById(R.id.intro);
                holder.cover = (ImageView) convertView.findViewById(R.id.imageview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Radio radio = mRadios.get(position);
            holder.title.setText(radio.getRadioName());
            holder.intro.setText(radio.getProgramName());
            x.image().bind(holder.cover, radio.getCoverUrlSmall());
            holder.content.setBackgroundColor(Color.WHITE);
            return convertView;
        }
    }
}
