package com.shenqu.wirelessmbox.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;

import java.util.ArrayList;

/**
 * Created by LinZh107 on 2016/3/15.
 */
public class TrackAdapter extends BaseAdapter {

    private ArrayList<TrackMeta> dataList;
    private LayoutInflater inflater;

    private class ViewHolder{
        ImageView icon;
        TextView music;
        TextView artist;
        TextView number;
    }

    public TrackAdapter(Context context, ArrayList<TrackMeta> dataList) {
        this.dataList = dataList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setDataList(ArrayList<TrackMeta> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.item_musicinfo, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.music = (TextView)convertView.findViewById(R.id.name);
            holder.artist = (TextView)convertView.findViewById(R.id.artist);
            holder.number = (TextView)convertView.findViewById(R.id.tvNumber);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        //holder.icon.setImageBitmap(dataList.get(position).icon);
        holder.music.setText(dataList.get(position).getName());
        holder.artist.setText(dataList.get(position).getArtist());
        holder.number.setText("" + (position + 1));

        return convertView;
    }
}
