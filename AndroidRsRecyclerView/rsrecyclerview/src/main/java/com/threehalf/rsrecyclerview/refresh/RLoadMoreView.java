package com.threehalf.rsrecyclerview.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.threehalf.rsrecyclerview.R;


/**
 * LoadMoreView default view
 */
public class RLoadMoreView extends FrameLayout implements IRefreshLoadMore {
    private ProgressBar mPbLoad;
    private TextView mTvLoadText;

    private boolean isLoading = false; // 是否加载中


    public RLoadMoreView(Context context) {
        this(context, null);
    }

    public RLoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_load_more, this);
        mPbLoad = (ProgressBar) findViewById(R.id.pb_load);
        mTvLoadText = (TextView) findViewById(R.id.tv_loadText);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void showNormal() {
        isLoading = false;
        mPbLoad.setVisibility(GONE);
        mTvLoadText.setVisibility(VISIBLE);
        mTvLoadText.setText(getResources().getString(R.string.load_more_normal));
    }

    @Override
    public void showNormalError() {
        isLoading = false;
        mPbLoad.setVisibility(GONE);
        mTvLoadText.setVisibility(VISIBLE);
        mTvLoadText.setText(getResources().getString(R.string.load_more_error));
    }

    @Override
    public void showNormalMsg(String msg) {
        isLoading = false;
        mPbLoad.setVisibility(GONE);
        mTvLoadText.setVisibility(VISIBLE);
        mTvLoadText.setText(msg);
    }

    @Override
    public void showLoading() {
        isLoading = true;
        mPbLoad.setVisibility(VISIBLE);
        mTvLoadText.setVisibility(VISIBLE);
        mTvLoadText.setText(getResources().getString(R.string.load_more_loading));
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }
}
