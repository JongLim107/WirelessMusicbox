package com.shenqu.wirelessmbox.ximalaya.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.shenqu.wirelessmbox.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import org.xutils.x;

import java.util.List;

/**
 * 发现页 配置的 分类维度 -- ListView
 */
public class IRecyclerAlbumAdapter extends RecyclerView.Adapter<IViewHolder> {

    private List<Album> mList;
    private OnItemClickListener<Album> mOnItemClickListener;

    private class ViewHolder extends IViewHolder{
        ViewGroup content;
        ImageView ivCover;
        TextView tvTitle;
        TextView tvIntro;
        TextView tvCacl;
        TextView tvInclude;

        ViewHolder (View view) {
            super(view);
            content = (ViewGroup) view;
            ivCover = (ImageView) view.findViewById(R.id.ivCover);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvIntro = (TextView) view.findViewById(R.id.tvIntro);
            tvCacl = (TextView) view.findViewById(R.id.tvCacl);
            tvInclude = (TextView) view.findViewById(R.id.tvInclude);
        }
    }

    public IRecyclerAlbumAdapter(List<Album> datas) {
        mList = datas;
    }

    public void setOnItemClickListener(OnItemClickListener<Album> listener) {
        this.mOnItemClickListener = listener;
    }

    public Album getItem(int position) {
        if (mList != null && mList.size() > 0)
            return mList.get(position);
        else
            return null;
    }

    @Override
    public IViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xm_item_meta_album, parent, false);
        final ViewHolder  holder = new ViewHolder (view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Note:
                 * in order to get the right position, you must use the method with i- prefix in
                 * {@link IViewHolder} eg:
                 * {@code IViewHolder.getIPosition()}
                 * {@code IViewHolder.getILayoutPosition()}
                 * {@code IViewHolder.getIAdapterPosition()}
                 */
                final int position = holder.getIAdapterPosition();
                final Album album= mList.get(position);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, album, v);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(IViewHolder holde, int position) {
        Album album = getItem(position);
        ViewHolder  holder = (ViewHolder ) holde;
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
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

}