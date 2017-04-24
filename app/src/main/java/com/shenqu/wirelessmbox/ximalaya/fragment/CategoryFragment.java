package com.shenqu.wirelessmbox.ximalaya.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.tools.JLLog;
import com.shenqu.wirelessmbox.ximalaya.base.BaseFragment;
import com.shenqu.wirelessmbox.ximalaya.base.ViewHolder;
import com.shenqu.wirelessmbox.ximalaya.childactivity.MdataFragmentActivity;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.DiscoveryRecommendAlbums;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JongLim on 2017/1/9.
 */

public class CategoryFragment extends BaseFragment {
    private static final String TAG = "CategoryFra";
    private Context mContext;

    //热门声音分类
    private ListView mListView;
    private List<Category> mCategories;
    private CategoryAdapter mCategoryAdapter;

    private boolean mLoading = false;

    private void doLoadCategoryData() {
        if (mLoading) {
            return;
        }
        mLoading = true;
        //Map<String, String> param = new HashMap<String, String>();
        CommonRequest.getCategories(null, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(CategoryList categoryList) {
                JLLog.LOGV(TAG, "GetCategories onSuccess " + (categoryList != null));
                if (categoryList != null && categoryList.getCategories() != null && categoryList.getCategories().size() != 0) {
                    mCategories.clear();
                    mCategories.addAll(categoryList.getCategories());
                    mCategoryAdapter.notifyDataSetChanged();
                }
                mLoading = false;
            }

            @Override
            public void onError(int i, String s) {
                JLLog.LOGE(TAG, "GetCategories onError " + i + ", " + s);
                mLoading = false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.xm_fragment_category, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        mCategories = new ArrayList<>();
        mCategoryAdapter = new CategoryAdapter(mCategories);
        mListView.setAdapter(mCategoryAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Collections.shuffle(mTrackHotList.getTracks());
            }
        });

        doLoadCategoryData();
    }

    @Override
    public void refresh() {
        if (mCategories.size() == 0)
            doLoadCategoryData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public class CategoryAdapter extends BaseAdapter{
        private List<Category> mCategories;

        class CategoryHolder {
            LinearLayout content1;
            ImageView cover1;
            TextView title1;
            LinearLayout content2;
            ImageView cover2;
            TextView title2;
        }

        CategoryAdapter(List<Category> categories) {
            mCategories = categories;
        }

        @Override
        public int getCount() {
            if (mCategories == null) {
                return 0;
            } else if (mCategories.size() % 2 == 0) {
                return mCategories.size() / 2;
            } else
                return mCategories.size() / 2 + 1;
        }

        @Override
        public Category getItem(int position) {
            if (position < mCategories.size())
                return mCategories.get(position);
            else
                return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CategoryHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.xm_item_category_track, parent, false);
                holder = new CategoryHolder();
                holder.content1 = (LinearLayout) convertView.findViewById(R.id.content1);
                holder.cover1 = (ImageView) convertView.findViewById(R.id.cover1);
                holder.title1 = (TextView) convertView.findViewById(R.id.title1);
                holder.content2 = (LinearLayout) convertView.findViewById(R.id.content2);
                holder.cover2 = (ImageView) convertView.findViewById(R.id.cover2);
                holder.title2 = (TextView) convertView.findViewById(R.id.title2);
                convertView.setTag(holder);
                if (position % 3 == 2) {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.xm_listdivider_grey, (ViewGroup) convertView, false);
                    ((ViewGroup) convertView).addView(view);
                }
            } else {
                holder = (CategoryHolder) convertView.getTag();
            }

            Category category1 = getItem(position * 2);
            holder.title1.setText(category1.getCategoryName());
            x.image().bind(holder.cover1, category1.getCoverUrlSmall());
            holder.content1.setOnClickListener(new OnChildClickListener(position * 2));

            Category category2 = getItem(position * 2 + 1);
            if (category2 != null) {
                holder.title2.setText(category2.getCategoryName());
                x.image().bind(holder.cover2, category2.getCoverUrlSmall());
                holder.content2.setOnClickListener(new OnChildClickListener(position * 2 + 1));
                holder.content2.setVisibility(View.VISIBLE);
            }else {
                holder.content2.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }


        /**
         * 和推荐页的点击 更多 事件一样
         * 重写 ViewClick 事件，为了获取到 ListView 的 xm_item_album_fragment index
         */
        class OnChildClickListener implements View.OnClickListener {
            private int parentId;
            OnChildClickListener(int position) {
                parentId = position;
            }

            @Override
            public void onClick(View v) {
                Category category = mCategories.get(parentId);
                Intent intent = new Intent(getActivity(), MdataFragmentActivity.class);
                intent.putExtra("CategoryId", "" + category.getId());
                intent.putExtra("CategoryName", category.getCategoryName());
                startActivity(intent);
            }
        }
    }

}
