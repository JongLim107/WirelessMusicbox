package com.shenqu.wirelessmbox.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

/**
 * 自定义RecyclerView的GridLayoutManager，添加禁止RecyclerView滚动
 * 默认可以滚动，当下拉刷新或加载更多时不能滑动
 */
public class CustomGridLayoutManager extends GridLayoutManager {

    // 设置是否可以滚动，默认是可以滚动
    private boolean isScrollEnabled = true;

    public CustomGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        // Similarly you can customize "canScrollHorizontally()" for managing
        // horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}
