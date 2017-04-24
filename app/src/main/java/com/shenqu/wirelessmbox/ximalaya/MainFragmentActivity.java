package com.shenqu.wirelessmbox.ximalaya;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.shenqu.wirelessmbox.MyApplication;
import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.base.XimalayaSDK;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.tools.JLUtils;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragment;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.fragment.AnnouncerFragment;
import com.shenqu.wirelessmbox.ximalaya.fragment.CategoryFragment;
import com.shenqu.wirelessmbox.ximalaya.fragment.RankFragment;
import com.shenqu.wirelessmbox.ximalaya.fragment.RecommendFragment;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.radio.CityList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JongLim on 2016/12/13.
 */

public class MainFragmentActivity extends BaseFragmentActivity {
    private static final String TAG = "MainFragAct";
    private Context mContext;

    private ViewPager mViewPager;
    private BaseFragment mCurrFragment;

    private final String[] CONTENT = new String[]{"推荐", "分类", "榜单", "主播"};
    private RecommendFragment mRecommendFragment;
    private CategoryFragment mCategoryFragment;
    //private RadiosFragment mRadiosFragment;
    private RankFragment mRankFragment;
    private AnnouncerFragment mAnnouncerFragment;

    private void initBaseView() {
        setTitle("喜马拉雅");
        getbtn_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getTv_title().getVisibility() == View.GONE){
                    getTv_title().setVisibility(View.VISIBLE);
                    getEt_search().setVisibility(View.GONE);
                    getvLine().setVisibility(View.GONE);
                }else
                    finish();
            }
        });
        hidebtn_right();
//        getbtn_right().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (getTv_title().getVisibility() == View.VISIBLE){
//                    getTv_title().setVisibility(View.GONE);
//                    getEt_search().setVisibility(View.VISIBLE);
//                    getvLine().setVisibility(View.VISIBLE);
//                }else {
//                    // TODO: 2017/1/9 search the context
//                }
//            }
//        });
    }

    private void initXmlyData() {
        //获取xmly的秘钥
        String appSecret = JLUtils.getAppMetaData(this, "xm_key");
        // 初始化下载模块
        new XimalayaSDK((MyApplication) getApplication());
        CommonRequest ximalaya = CommonRequest.getInstanse();
        ximalaya.init(mContext, appSecret);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.xm_activity_main);
        mContext = this;

        initBaseView();
        initXmlyData();

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        PagerAdapter adapter = new SlidingPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (0 == position) {
                    mCurrFragment = mRecommendFragment;
                } else if (1 == position) {
                    mCurrFragment = mCategoryFragment;
                } else if (2 == position) {
                    mCurrFragment = mRankFragment;
                } else if (3 == position) {
                    mCurrFragment = mAnnouncerFragment;
                }

                if (mCurrFragment != null) {
                    mCurrFragment.refresh();
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onBackPressed() {
        JLLog.LOGI(TAG, "onBackPressed");
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "下载模块").setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 2, 0, "测试1");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                //startActivity(new Intent(MainFragmentActivity.this , DownloadTrackActivity.class));
                break;
            case 2:
                Map<String, String> maps = new HashMap<>();
                maps.put("province_code", "110000");
                CommonRequest.getCitys(maps, new IDataCallBack<CityList>() {
                    @Override
                    public void onSuccess(CityList object) {
                    }

                    @Override
                    public void onError(int code, String message) {
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class SlidingPagerAdapter extends FragmentPagerAdapter {

        SlidingPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = null;
            if (0 == position) {
                if (mRecommendFragment == null) {
                    mRecommendFragment = new RecommendFragment();
                }
                f = mRecommendFragment;
            } else if (1 == position) {
                if (mCategoryFragment == null) {
                    mCategoryFragment = new CategoryFragment();
                }
                f = mCategoryFragment;
            } else if (2 == position) {
                if (mRankFragment == null) {
                    mRankFragment = new RankFragment();
                }
                f = mRankFragment;
            } else if (3 == position) {
                if (mAnnouncerFragment == null) {
                    mAnnouncerFragment = new AnnouncerFragment();
                }
                f = mAnnouncerFragment;
            }
            return f;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }
    }

}
