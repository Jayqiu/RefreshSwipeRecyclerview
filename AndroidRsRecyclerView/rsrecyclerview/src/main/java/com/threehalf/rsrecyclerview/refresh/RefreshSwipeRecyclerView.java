package com.threehalf.rsrecyclerview.refresh;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

/**
 * @author jayqiu
 * @describe
 * @date 2017/2/15 10:56
 */
public class RefreshSwipeRecyclerView  extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener{
    private RefreshRecyclerView mRvList;
    private IRefreshLoadMore mLoadMoreView;

    private OnPullRefreshListener mOnPullRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RefreshRecyclerViewOnScrollListener mRefreshRecyclerViewOnScrollListener;
    private boolean isPullRefreshEnabled = true;
    private boolean isLoadMoreEnabled = true;
    private boolean isSetLoadMoreView = false;

    public RefreshSwipeRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs) {
        mRvList = new RefreshRecyclerView(getContext(), attrs);
        addView(mRvList, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if (!isPullRefreshEnabled) return;
        callOnPullRefresh();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRvList.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        mRvList.setLayoutManager(layout);
    }

    /**
     * 设置加载更多
     * @param loadMoreView
     */
    public void setLoadMoreView(IRefreshLoadMore loadMoreView) {
        if (null == loadMoreView) {
            if (null != mLoadMoreView) {
                mRvList.removeFooterView(mLoadMoreView.getView());
                mRvList.removeOnScrollListener(mRefreshRecyclerViewOnScrollListener);
                isSetLoadMoreView = false;
                mLoadMoreView = null;
            }
            return;
        }

        mLoadMoreView = loadMoreView;
        mRvList.addFooterView(mLoadMoreView.getView());
        initializeLoadMoreView();
        isSetLoadMoreView = null != mLoadMoreView;
    }

    /**
     * Get FamiliarRecyclerView
     *
     * @return FamiliarRecyclerView
     */
    public RefreshRecyclerView getRefreshRecyclerView() {
        return mRvList;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try {
            super.onRestoreInstanceState(state);
        } catch (Exception e) {
            Log.e("FamiliarRefresh", e.getMessage());
        }
        state = null;
    }

    /**
     * Automatic pull refresh
     */
    public void autoRefresh() {
        if (!isPullRefreshEnabled) return;

        setRefreshing(true);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callOnPullRefresh();
            }
        }, 1000);
    }

    /**
     *
     * @param enabled
     */
    public void setPullRefreshEnabled(boolean enabled) {
        if (isPullRefreshEnabled == enabled) return;

        setEnabled(enabled);

        if (!enabled) {
            setRefreshing(false);
        }

        this.isPullRefreshEnabled = enabled;
    }

    /**
     *
     * @param enabled false 加载完全不 true 加载更多
     */
    public void setLoadMoreEnabled(boolean enabled) {
        if (!isSetLoadMoreView || isLoadMoreEnabled == enabled || null == mLoadMoreView) {
            mLoadMoreView.showNormalMsg("松开加载更多");
            return;
        }

        if (!enabled) {
            mLoadMoreView.showNormalMsg("已经全部加载完毕");

        } else {
            mLoadMoreView.showNormalMsg("松开加载更多");
        }
        isLoadMoreEnabled = enabled;
    }

    /**
     * 下拉刷新完成
     */
    public void pullRefreshComplete() {
        setRefreshing(false);
    }

    /**
     *
     * 加载更多完成
     */
    public void loadMoreComplete() {
        if (isSetLoadMoreView) {
            mLoadMoreView.showNormal();
        }
    }

    /**
     * 加载出错了
     */
    public void loadMoreError() {
        if (isSetLoadMoreView) {
            mLoadMoreView.showNormalError();
        }
    }

    private void initializeLoadMoreView() {
        if (null == mRefreshRecyclerViewOnScrollListener) {
            mRefreshRecyclerViewOnScrollListener = new RefreshRecyclerViewOnScrollListener(mRvList.getLayoutManager()) {
                @Override
                public void onScrolledToTop() {
                }

                @Override
                public void onScrolledToBottom() {
                    if (!isLoadMoreEnabled || !isSetLoadMoreView || mLoadMoreView.isLoading())
                        return;

                    mLoadMoreView.showLoading();
                    callOnLoadMore();
                }
            };
        }
        mRvList.addOnScrollListener(mRefreshRecyclerViewOnScrollListener);
        mLoadMoreView.getView().setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
    }

    private void callOnPullRefresh() {
        if (null != mOnPullRefreshListener) {
            mOnPullRefreshListener.onPullRefresh();
        }
    }

    private void callOnLoadMore() {
        if (null != mOnLoadMoreListener) {
            mOnLoadMoreListener.onLoadMore();
        }
    }

    /**
     * 设置下拉刷新监听
     * @param onPullRefreshListener
     */
    public void setOnPullRefreshListener(OnPullRefreshListener onPullRefreshListener) {
        this.mOnPullRefreshListener = onPullRefreshListener;
    }

    /**
     * 设置加载更多监听
     * @param onLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setOnItemClickListener(final RefreshRecyclerView.OnItemClickListener onItemClickListener) {
        if (null != onItemClickListener) {
            mRvList.setOnItemClickListener(onItemClickListener);
        }
    }

    public void setOnItemLongClickListener(final RefreshRecyclerView.OnItemLongClickListener onItemLongClickListener) {
        if (null != onItemLongClickListener) {
            mRvList.setOnItemLongClickListener(onItemLongClickListener);
        }
    }

    public interface OnPullRefreshListener {
        void onPullRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
