package com.shenqu.wirelessmbox.ximalaya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.tools.JLUtils;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.xutils.x;

import java.util.List;

/**
 * 发现页 配置的 分类维度 -- ListView
 */
public class TrackListAdapter extends BaseAdapter {

    private List<Track> mTrackList;
    private Context mContext;

    public TrackListAdapter(Context context, List<Track> tracks) {
        mTrackList = tracks;
        mContext = context;
    }

    class TrackHolder {
        ViewGroup content;
        ImageView ivCover;
        TextView tvIntro;
        TextView tvCacl;
        TextView tvDuration;
        TextView tvUpdateTime;
    }

    @Override
    public int getCount() {
        if (mTrackList == null) {
            return 0;
        }
        return mTrackList.size();
    }

    @Override
    public Track getItem(int position) {
        if (mTrackList != null && mTrackList.size() > 0)
            return mTrackList.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TrackHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.xm_item_meta_track, parent, false);
            holder = new TrackHolder();
            holder.content = (ViewGroup) convertView;
            holder.ivCover = (ImageView) convertView.findViewById(R.id.ivCover);
            holder.tvIntro = (TextView) convertView.findViewById(R.id.tvIntro);
            holder.tvCacl = (TextView) convertView.findViewById(R.id.tvCacl);
            holder.tvDuration = (TextView) convertView.findViewById(R.id.tvDuration);
            holder.tvUpdateTime = (TextView) convertView.findViewById(R.id.tvUpdateTime);

            convertView.setTag(holder);
        } else {
            holder = (TrackHolder) convertView.getTag();
        }

        Track track = getItem(position);
        if (track != null) {
            if (track.getTrackIntro() == null || track.getTrackIntro().length() == 0)
                holder.tvIntro.setText(track.getTrackTitle());
            else
                holder.tvIntro.setText(track.getTrackIntro());
            long count = track.getPlayCount();
            if (count > 10000) {
                holder.tvCacl.setText("" + count / 10000 + "万");
            } else {
                holder.tvCacl.setText("" + count);
            }

            String str = JLUtils.formatMediaTime(track.getDuration());
            holder.tvDuration.setText(str);

            String up = formatMsToDate(System.currentTimeMillis() - track.getCreatedAt());
            if (up.equals("0"))
                holder.tvUpdateTime.setText("今天");
            else
                holder.tvUpdateTime.setText(up + "天前");
            x.image().bind(holder.ivCover, track.getCoverUrlSmall());
        }
        return convertView;
    }

    private String formatMsToDate(long ms) {
        long sec = ms / 1000;
        return String.valueOf(sec / (24 * 60 * 60));
    }
}