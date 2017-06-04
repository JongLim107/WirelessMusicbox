
package com.shenqu.wirelessmbox.ximalaya.childactivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;

public class PhotoViewerActivity extends Activity implements PhotoPagerAdapter.OnPhotoClickListener{

    public static final String TRANSIT_PIC = "picture";
    PhotoPagerAdapter mPagerAdapter;
    private TextView tvIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_viewer);

        tvIndex = (TextView) findViewById(R.id.tvIndex);
        ViewPagerFixed viewPager = (ViewPagerFixed) findViewById(R.id.viewPager);
        mPagerAdapter = new PhotoPagerAdapter(this, getIntent().getStringExtra("url_img"));
        mPagerAdapter.setOnPhotoClickListener(this);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvIndex.setText((position+1) + " / " + mPagerAdapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tvIndex.setText(1 + " / " + mPagerAdapter.getCount());

        ViewCompat.setTransitionName(viewPager, TRANSIT_PIC);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClose(View view) {
        onBackPressed();
        //finishAfterTransition
    }

    @Override
    public void onPhotoClick() {
        onBackPressed();
    }
}
