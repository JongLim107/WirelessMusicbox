package com.shenqu.wirelessmbox.ximalaya.childactivity;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.shenqu.wirelessmbox.R;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by JongLim on 2017/6/3.
 */

public class CustomPagerAdapter extends PagerAdapter{

    final ArrayList<String> imgs = new ArrayList<>();
    Context mContext;

    CustomPagerAdapter(Context context, String url) {
        mContext = context;
        imgs.add(url);
        imgs.add("http://img1.gtimg.com/ent/pics/hv1/151/152/2012/130869211.jpg");
        imgs.add("https://pbs.twimg.com/media/CRliL4yUcAAyob8.png");
        imgs.add("http://www.huabian.com/uploadfile/wap/2016/1123/20161123082849200.png");
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mContext);
        container.addView(photoView);

        photoView.setMinimumScale(0.5f);
        x.image().bind(photoView, imgs.get(position));
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((PhotoView) view);
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}

