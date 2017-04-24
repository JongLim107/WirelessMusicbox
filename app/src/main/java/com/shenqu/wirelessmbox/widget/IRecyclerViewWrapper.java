package com.shenqu.wirelessmbox.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;

/**
 * 
 * 在不改动原来IRecyclerView代码情况下二次封装
 * 添加删除 headerView和footerView
 * 添加 下拉刷新和加载更多时禁止滑动
 */
public class IRecyclerViewWrapper<T extends RecyclerView.LayoutManager> {

    private IRecyclerView iRecyclerView;

    private T layoutManager;

    private LoadMoreFooterView loadMoreFooterView;

    private RecyclerView.Adapter adapter;

    public IRecyclerViewWrapper(IRecyclerView iRecyclerView, T layoutManager, RecyclerView.Adapter adapter) {
        this.iRecyclerView = iRecyclerView;
        this.layoutManager = layoutManager;
        iRecyclerView.setLayoutManager(layoutManager);
        loadMoreFooterView = (LoadMoreFooterView) iRecyclerView.getLoadMoreFooterView();
        this.adapter = adapter;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        if (listener != null) {
            iRecyclerView.setOnRefreshListener(listener);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        if (listener != null) {
            iRecyclerView.setOnLoadMoreListener(listener);
        }
    }

    /**
     * 扩展删除指定footerView
     * @param view
     */
    public void removeFooterView(View view) {
        if (iRecyclerView != null) {
            LinearLayout footerLayout = iRecyclerView.getFooterContainer();
            footerLayout.removeView(view);
            footerLayout = null;
        }
    }

    /**
     * 扩展删除所有的footerView
     */
    public void removeAllFooterView() {
        if (iRecyclerView != null) {
            LinearLayout footerLayout = iRecyclerView.getFooterContainer();
            footerLayout.removeAllViews();
            footerLayout = null;
        }
    }

    /**
     * 扩展删除指定的headerview
     * @param view
     */
    public void removeHeaderView (View view) {
        if (iRecyclerView != null) {
            LinearLayout headerLayout = iRecyclerView.getHeaderContainer();
            headerLayout.removeView(view);
            headerLayout = null;
        }
    }

    /**
     * 扩展删除所有的headerView
     */
    public void removeAllHeaderView() {
        if (iRecyclerView != null) {
            LinearLayout headerLayout = iRecyclerView.getHeaderContainer();
            headerLayout.removeAllViews();
            headerLayout = null;
        }
    }

    /**
     * 开始刷新的时候禁止滑动
     */
    public void startRefresh() {
        if (layoutManager != null && layoutManager.getClass() == CustomLinearLayoutManager.class) {
            ((CustomLinearLayoutManager)layoutManager).setScrollEnabled(false);
        } else if (layoutManager != null && layoutManager.getClass() == CustomGridLayoutManager.class) {
            ((CustomGridLayoutManager)layoutManager).setScrollEnabled(false);
        }
        if (loadMoreFooterView != null) {
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
        }
    }

    /**
     * 停止刷新状态
     */
    public void finishRefresh() {
        if (layoutManager != null && layoutManager.getClass() == CustomLinearLayoutManager.class) {
            ((CustomLinearLayoutManager)layoutManager).setScrollEnabled(true);
        } else if (layoutManager != null && layoutManager.getClass() == CustomGridLayoutManager.class) {
            ((CustomGridLayoutManager)layoutManager).setScrollEnabled(true);
        }
        iRecyclerView.setRefreshing(false);
    }

    /**
     * 设置加载更多出错
     */
    public void setLoadMoreError() {
        if (loadMoreFooterView != null) {
            if (layoutManager != null && layoutManager.getClass() == CustomLinearLayoutManager.class) {
                ((CustomLinearLayoutManager)layoutManager).setScrollEnabled(true);
            } else if (layoutManager != null && layoutManager.getClass() == CustomGridLayoutManager.class) {
                ((CustomGridLayoutManager)layoutManager).setScrollEnabled(true);
            }
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
        }
    }

    /**
     * 加载更多完成
     */
    public void setloadMoreFinish() {
        if (loadMoreFooterView != null) {
            if (layoutManager != null && layoutManager.getClass() == CustomLinearLayoutManager.class) {
                ((CustomLinearLayoutManager)layoutManager).setScrollEnabled(true);
            } else if (layoutManager != null && layoutManager.getClass() == CustomGridLayoutManager.class) {
                ((CustomGridLayoutManager)layoutManager).setScrollEnabled(true);
            }
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
        }
    }

    /**
     * 判断是否能加载更多, 并禁止滚动
     * @return
     */
    public boolean canLoadMore() {
        boolean canLoadMore = false;
        if (loadMoreFooterView != null && loadMoreFooterView.canLoadMore() && adapter.getItemCount() > 0) {
            if (layoutManager != null && layoutManager.getClass() == CustomLinearLayoutManager.class) {
                ((CustomLinearLayoutManager)layoutManager).setScrollEnabled(false);
            } else if (layoutManager != null && layoutManager.getClass() == CustomGridLayoutManager.class) {
                ((CustomGridLayoutManager)layoutManager).setScrollEnabled(false);
            }
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
            canLoadMore = true;
        }
        return canLoadMore;
    }

    /**
     * 设置服务器没有更多资源
     */
    public void setLoadMoreEnd() {
        if (loadMoreFooterView != null) {
            if (layoutManager != null && layoutManager.getClass() == CustomLinearLayoutManager.class) {
                ((CustomLinearLayoutManager)layoutManager).setScrollEnabled(true);
            } else if (layoutManager != null && layoutManager.getClass() == CustomGridLayoutManager.class) {
                ((CustomGridLayoutManager)layoutManager).setScrollEnabled(true);
            }
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
        }
    }
}
