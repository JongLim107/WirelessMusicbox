package com.shenqu.wirelessmbox.ximalaya.childactivity;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.shenqu.wirelessmbox.R;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by JongLim on 2017/6/3.
 */

public class PhotoPagerAdapter extends PagerAdapter {

    private Context mContext;
    private OnPhotoClickListener Listener;
    private final ArrayList<String> imgs;

    PhotoPagerAdapter(Context context, String url) {
        mContext = context;
        imgs = new ArrayList<>();
        imgs.add(url);
        imgs.add("http://img1.gtimg.com/ent/pics/hv1/151/152/2012/130869211.jpg");
        imgs.add("https://pbs.twimg.com/media/CRliL4yUcAAyob8.png");
        imgs.add("http://www.huabian.com/uploadfile/wap/2016/1123/20161123082849200.png");
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mContext);
        x.image().bind(photoView, imgs.get(position));
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        photoView.setMinimumScale(0.5f);
        photoView.setMaximumScale(2.0f);
        photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                Listener.onPhotoClick();
            }
        });
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener listener) {
        Listener = listener;
    }

    interface OnPhotoClickListener {
        void onPhotoClick();
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

