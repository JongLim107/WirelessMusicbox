package com.shenqu.wirelessmbox.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * 自定义RecyclerView的LinearLayoutManager，添加禁止RecyclerView滚动
 * 默认可以滚动，当下拉刷新或加载更多时不能滑动
 */
public class CustomLinearLayoutManager extends LinearLayoutManager {

    // 设置是否可以滚动，默认是可以滚动
    private boolean isScrollEnabled = true;

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        // Similarly you can customize "canScrollHorizontally()" for managing
        // horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}
