
package com.shenqu.wirelessmbox.ximalaya.childactivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;

import com.shenqu.wirelessmbox.R;

public class PhotoViewerActivity extends Activity {

    public static final String TRANSIT_PIC = "picture";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_layout);

        ViewPagerFixed viewPager = (ViewPagerFixed) findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomPagerAdapter(this, getIntent().getStringExtra("url_img")));
        ViewCompat.setTransitionName(viewPager, TRANSIT_PIC);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
