package com.shenqu.wirelessmbox.bean;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;

import java.util.List;

/**
 * Created by JongLim on 2016/12/5.
 */

public class WifiAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<WifiItem> dataList;
    private Context mContext;


    public void setCurName(String curName) {
        mCurName = curName;
    }

    private String mCurName;

    public WifiAdapter(Context context, List<WifiItem> wifis) {
        mContext = context;
        dataList = wifis;
        inflater = LayoutInflater.from(context);
        mCurName = null;
    }

    public int getCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    public WifiItem getItem(int paramInt) {
        if (dataList == null) {
            return null;
        }
        return dataList.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return 0L;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.item_wifiinfo, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.auth = (TextView) convertView.findViewById(R.id.auth);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WifiItem item = dataList.get(position);
        holder.name.setText(item.getSsid());
        int rssi = Integer.parseInt(item.getRssi());
        if ("OPEN".equals(item.getAuth())) {
            holder.auth.setText("开放");
            if (rssi > 75)
                holder.icon.setImageResource(R.mipmap.ic_wifi_n4);
            else if (rssi > 50)
                holder.icon.setImageResource(R.mipmap.ic_wifi_n3);
            else if (rssi > 25)
                holder.icon.setImageResource(R.mipmap.ic_wifi_n2);
            else
                holder.icon.setImageResource(R.mipmap.ic_wifi_n1);
        } else {
            holder.auth.setText(item.getAuth() + " / " + item.getEncry());
            if (rssi > 75)
                holder.icon.setImageResource(R.mipmap.ic_wifi_s4);
            else if (rssi > 50)
                holder.icon.setImageResource(R.mipmap.ic_wifi_s3);
            else if (rssi > 25)
                holder.icon.setImageResource(R.mipmap.ic_wifi_s2);
            else
                holder.icon.setImageResource(R.mipmap.ic_wifi_s1);
        }

        if (mCurName != null && mCurName.equals(item.getSsid()))
            holder.name.setTextColor(0xFF3EC3C8);
        else
            holder.name.setTextColor(Color.WHITE);

        return convertView;
    }

    private class ViewHolder {
        ImageView icon;
        TextView auth;
        TextView name;
    }
}
