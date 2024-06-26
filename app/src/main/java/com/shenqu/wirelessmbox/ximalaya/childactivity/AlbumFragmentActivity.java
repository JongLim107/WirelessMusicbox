package com.shenqu.wirelessmbox.ximalaya.childactivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.action.ActionType;
import com.shenqu.wirelessmbox.tools.JLJSON;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.childfragment.AlbumDetailFragment;
import com.shenqu.wirelessmbox.ximalaya.childfragment.AlbumTracksFragment;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;


/**
 * Created by JongLim on 2016/12/13.
 * Activity for recommend fragment.
 */

public class AlbumFragmentActivity extends BaseFragmentActivity implements View.OnClickListener {
    private static final String TAG = "AlbumActi";

    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvAnnouncer;
    private TextView tvCacl;
    private TextView tvCategory;

    /**
     * 因为这里的 tabLayout 位于view的中部，且要悬浮的效果，所以要用到 StickyNavLayout
     * StickyNavLayout 需要计算tab高度，这里的tabLayout必须使用其里面的id
     */
    private TabLayout tabLayout;
    private String[] mTitles = new String[]{"详情", "节目"};

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private Fragment[] mFragments = new Fragment[mTitles.length];

    public Album mAlbum;

    PopupWindow popupWindow;

    private void initBaseView() {
        setTitle("专辑详情");
        getbtn_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        hidebtn_right();
    }

    private void findViews() {
        ivCover = (ImageView) findViewById(R.id.ivCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAnnouncer = (TextView) findViewById(R.id.tvAnnouncer);
        tvCacl = (TextView) findViewById(R.id.tvCacl);
        tvCategory = (TextView) findViewById(R.id.tvCategory);

        tabLayout = (TabLayout) findViewById(R.id.id_stickynavlayout_indicator);
        mViewPager = (ViewPager) findViewById(R.id.id_stickynavlayout_viewpager);
    }

    private void initDatas() {
        x.image().bind(ivCover, mAlbum.getCoverUrlMiddle());
        tvTitle.setText(mAlbum.getAlbumTitle());
        tvAnnouncer.setText("主播：" + mAlbum.getAnnouncer().getNickname());
        long count = mAlbum.getPlayCount();
        if (count > 10000) {
            tvCacl.setText("播放：" + count / 10000 + "万");
        } else {
            tvCacl.setText("播放：" + count);
        }
        tvCategory.setText("分类：" + mAlbum.getAlbumTags());

        mFragments[0] = AlbumDetailFragment.newInstance(mTitles[0]);
        mFragments[1] = AlbumTracksFragment.newInstance(mTitles[1] + "(" + mAlbum.getIncludeTrackCount() + ")");

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragments[position].getArguments().getString("title");
            }
        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void initEvents() {
        ivCover.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tabLayout.setScrollPosition(position, positionOffset, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.xm_activity_album);

        Bundle bundle = getIntent().getExtras();
        mAlbum = bundle.getParcelable("mAlbum");

        initBaseView();

        findViews();

        initDatas();

        initEvents();
    }

    @Override
    public boolean handleMessage(Message msg) {
        super.handleMessage(msg);
        Bundle b = msg.getData();
        String string = b.getString("JSONDATA");
        if (string == null)
            return false;

        JSONObject jobj = null;
        try {
            jobj = new JSONObject(string);
        } catch (JSONException e) {
            JLLog.showToast(this, "" + e.getLocalizedMessage());
            return false;
        }

        if (msg.what == ActionType.SetAVTransportURI && JLJSON.getInt(jobj, "Result") == 0) {
            JLLog.showToast(this, "推送成功，播放器在缓冲后将会自动播放~");
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JLLog.LOGI(TAG, "onDestroy()");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivCover:
                //showPopup(v, mAlbum.getCoverUrlLarge());
                startPictureActivity(mAlbum.getCoverUrlMiddle(), v);
                break;
            default:
                popupWindow.dismiss();
                break;
        }
    }

    private void startPictureActivity(String url, View anchorView) {
        Intent intent = new Intent(getBaseContext(), PhotoViewerActivity.class);
        intent.putExtra("url_img", url);

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(AlbumFragmentActivity.this, anchorView,
                                                                                                 PhotoViewerActivity.TRANSIT_PIC);
        try {
            ActivityCompat.startActivity(AlbumFragmentActivity.this, intent, optionsCompat.toBundle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            startActivity(intent);
        }
    }

    private void showPopup(final View anchorView, final String str) {

        View rootView = getLayoutInflater().inflate(R.layout.photos_viewer, null);
        rootView.findViewById(R.id.btnClose).setOnClickListener(this);

        // Initialize widgets from `photos_viewerr.xml`
        ViewPagerFixed viewPager = (ViewPagerFixed) rootView.findViewById(R.id.viewPager);
        viewPager.setAdapter(new PhotoPagerAdapter(getBaseContext(), str));

        popupWindow = new PopupWindow(rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], location[1] + anchorView.getHeight());

    }
}
