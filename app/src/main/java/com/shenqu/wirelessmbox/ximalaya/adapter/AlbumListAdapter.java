package com.shenqu.wirelessmbox.ximalaya.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import org.xutils.x;

import java.util.List;

/**
 * 发现页 配置的 分类维度 -- ListView
 */
public class AlbumListAdapter extends BaseAdapter {

    private List<Album> mAlbumList;
    private Context mContext;

    public AlbumListAdapter(Context context, List<Album> albumList) {
        mAlbumList = albumList;
        mContext = context;
    }

    class AlbumHolder {
        ViewGroup content;
        ImageView ivCover;
        TextView tvTitle;
        TextView tvIntro;
        TextView tvCacl;
        TextView tvInclude;
    }

    @Override
    public int getCount() {
        if (mAlbumList == null) {
            return 0;
        }
        return mAlbumList.size();
    }

    @Override
    public Album getItem(int position) {
        if (mAlbumList != null && mAlbumList.size() > 0)
            return mAlbumList.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.xm_item_meta_album, parent, false);
            holder = new AlbumHolder();
            holder.content = (ViewGroup) convertView;
            holder.ivCover = (ImageView) convertView.findViewById(R.id.ivCover);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvIntro = (TextView) convertView.findViewById(R.id.tvIntro);
            holder.tvCacl = (TextView) convertView.findViewById(R.id.tvCacl);
            holder.tvInclude = (TextView) convertView.findViewById(R.id.tvInclude);

            convertView.setTag(holder);
        } else {
            holder = (AlbumHolder) convertView.getTag();
        }

        Album album = getItem(position);
        if (album != null) {
            holder.tvTitle.setText(album.getAlbumTitle());
            if (album.getAlbumIntro() == null || album.getAlbumIntro().length() == 0)
                holder.tvIntro.setText(album.getLastUptrack().getTrackTitle());
            else
                holder.tvIntro.setText(album.getAlbumIntro());
            long count = album.getPlayCount();
            if (count > 10000) {
                holder.tvCacl.setText("" + count / 10000 + "万");
            } else {
                holder.tvCacl.setText("" + count);
            }
            holder.tvInclude.setText(album.getIncludeTrackCount() + "集");
            x.image().bind(holder.ivCover, album.getCoverUrlSmall());
        }
        return convertView;
    }
}