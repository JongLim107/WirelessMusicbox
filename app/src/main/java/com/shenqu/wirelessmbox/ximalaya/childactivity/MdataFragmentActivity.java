package com.shenqu.wirelessmbox.ximalaya.childactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragmentActivity;
import com.shenqu.wirelessmbox.ximalaya.childfragment.AlbumListFragment;
import com.shenqu.wirelessmbox.ximalaya.childfragment.AllMdataFragment;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JongLim on 2016/12/13.
 * Activity for recommend fragment.
 * */

public class MdataFragmentActivity extends BaseFragmentActivity {
    private static final String TAG = "MetaActi";
    private Context mContext;
    private TextView mBtnAllCate;

    private ArrayList<Fragment> fragments;

    private ViewPager mViewPager;

    private String mCategoryId;
    private String mCategoryName;

    private List<Tag> mTagList;

    /**
     * 初始化baseview
     */
    private void initBaseView() {
        setTitle(mCategoryName);
        getbtn_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getTv_title().getVisibility() == View.GONE) {
                    getTv_title().setVisibility(View.VISIBLE);
                    getEt_search().setVisibility(View.GONE);
                    getvLine().setVisibility(View.GONE);
                } else
                    finish();
            }
        });
        hidebtn_right();
//        getbtn_right().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (getTv_title().getVisibility() == View.VISIBLE) {
//                    getTv_title().setVisibility(View.GONE);
//                    getEt_search().setVisibility(View.VISIBLE);
//                    getvLine().setVisibility(View.VISIBLE);
//                } else {
//                    // TODO: 2017/1/9 search the context
//                }
//            }
//        });
    }

    /**
     * 初始化各种layout
     */
    private void initView() {
        mBtnAllCate = (TextView) findViewById(R.id.btnAllCate);
        ViewGroup tabContent = (ViewGroup) findViewById(R.id.tabContent);
        tabContent.addView(LayoutInflater.from(this).inflate(R.layout.layout_smart_tablayout, tabContent, false));
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.smartTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        fragments = new ArrayList<Fragment>();
        for (int i = 0; i < mTagList.size(); i++){
            if (i==0) {
                fragments.add(new AllMdataFragment());
            }
            else {
                AlbumListFragment fragment = new AlbumListFragment();
                Bundle b = new Bundle();
                b.putString("TAGNAME", mTagList.get(i).getTagName());
                b.putString("CATEGORYID", mCategoryId);
                fragment.setArguments(b);
                fragments.add(fragment);
            }
        }

        PagerAdapter adapter = new SlidingPagerAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(pageListener);

        mViewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(mViewPager);
        mBtnAllCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void doLoadMetadataList() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, mCategoryId);
        map.put(DTransferConstants.TYPE, "" + 0);
        CommonRequest.getTags(map, new IDataCallBack<TagList>() {
            @Override
            public void onSuccess(TagList tagList) {
                if (tagList != null && tagList.getTagList() != null) {
                    if (mTagList == null){
                        mTagList = new ArrayList<Tag>();
                        Tag tag = new Tag();
                        tag.setKind("");
                        tag.setTagName("全部");
                        mTagList.add(tag);
                    }
                    mTagList.addAll(tagList.getTagList());
                    initView();
                }
            }

            @Override
            public void onError(int i, String s) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.xm_activity_mdata);
        mContext = this;

        mCategoryId = getIntent().getStringExtra("CategoryId");
        mCategoryName = getIntent().getStringExtra("CategoryName");

        initBaseView();
        doLoadMetadataList();
    }

    class SlidingPagerAdapter extends FragmentPagerAdapter {

        SlidingPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.size();//CONTENT.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTagList.get(position).getTagName();
        }
    }

    /**
     * ViewPager切换监听方法
     */
    private OnPageChangeListener pageListener = new OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            mViewPager.setCurrentItem(position);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
    }
}
