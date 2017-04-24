package com.shenqu.wirelessmbox.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;

import java.util.ArrayList;

/**
 * Created by JongLim on 2016/11/22.
 */

public class DevAdapter extends BaseAdapter {

    ArrayList<MusicBox> dataList;
    LayoutInflater inflater;

    class ViewHolder{
        TextView name;
        TextView ip;
    }

    public DevAdapter(Context context, ArrayList<MusicBox> devList) {
        this.dataList = devList;
        this.inflater = LayoutInflater.from(context);
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

            convertView = inflater.inflate(R.layout.item_devinfo, null);
            holder.name = (TextView)convertView.findViewById(R.id.devName);
            holder.ip = (TextView)convertView.findViewById(R.id.devAddr);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.name.setText(dataList.get(position).getName());
        holder.ip.setText(dataList.get(position).getAddr());

        return convertView;
    }
}
