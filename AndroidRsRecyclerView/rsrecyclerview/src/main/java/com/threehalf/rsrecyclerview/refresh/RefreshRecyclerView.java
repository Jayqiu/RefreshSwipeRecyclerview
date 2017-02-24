package com.threehalf.rsrecyclerview.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.threehalf.rsrecyclerview.R;
import com.threehalf.rsrecyclerview.swipe.Closeable;
import com.threehalf.rsrecyclerview.swipe.OnSwipeMenuItemClickListener;
import com.threehalf.rsrecyclerview.swipe.SwipeMenu;
import com.threehalf.rsrecyclerview.swipe.SwipeMenuCreator;
import com.threehalf.rsrecyclerview.swipe.SwipeMenuLayout;
import com.threehalf.rsrecyclerview.swipe.touch.DefaultItemTouchHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jayqiu
 * @describe
 * @date 2017/2/15 10:31
 */
public class RefreshRecyclerView extends RecyclerView {
    /**
     * Left menu.
     */
    public static final int LEFT_DIRECTION = 1;
    /**
     * Right menu.
     */
    public static final int RIGHT_DIRECTION = -1;

    @IntDef({LEFT_DIRECTION, RIGHT_DIRECTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DirectionMode {
    }
    private static final int INVALID_POSITION = -1;
    private SwipeMenuCreator mSwipeMenuCreator;
    private OnSwipeMenuItemClickListener mSwipeMenuItemClickListener;
    private DefaultItemTouchHelper mDefaultItemTouchHelper;
    protected ViewConfiguration mViewConfig;
    protected SwipeMenuLayout mOldSwipedLayout;
    protected int mOldTouchedPosition = INVALID_POSITION;
    private boolean isInterceptTouchEvent = true;
    private int mDownX;
    private int mDownY;
    private  boolean isSwipeMenu=false;
    //
    public static final int LAYOUT_MANAGER_TYPE_LINEAR = 0;
    public static final int LAYOUT_MANAGER_TYPE_GRID = 1;
    public static final int LAYOUT_MANAGER_TYPE_STAGGERED_GRID = 2;

    private static final int DEF_LAYOUT_MANAGER_TYPE = LAYOUT_MANAGER_TYPE_LINEAR;
    private static final int DEF_GRID_SPAN_COUNT = 2;
    private static final int DEF_LAYOUT_MANAGER_ORIENTATION = OrientationHelper.VERTICAL;
    private static final int DEF_DIVIDER_HEIGHT = 30;

    private List<View> mHeaderView = new ArrayList<View>();
    private List<View> mFooterView = new ArrayList<View>();
    private RefreshWrapRecyclerViewAdapter mRefreshWrapRecyclerViewAdapter;
    private RecyclerView.Adapter mReqAdapter;
    private GridLayoutManager mCurGridLayoutManager;
    private RefrechDefaultItemDecoration mRefrechDefaultItemDecoration;

    private Drawable mVerticalDivider;
    private Drawable mHorizontalDivider;
    private int mVerticalDividerHeight;
    private int mHorizontalDividerHeight;
    private int mItemViewBothSidesMargin;
    private boolean isHeaderDividersEnabled = false;
    private boolean isFooterDividersEnabled = false;
    private boolean isDefaultItemDecoration = true;
    private boolean isKeepShowHeadOrFooter = false;
    private boolean isNotShowGridEndDivider = false;
    private int mEmptyViewResId;
    private View mEmptyView;
    private OnItemClickListener mTempOnItemClickListener;
    private OnItemLongClickListener mTempOnItemLongClickListener;
    private OnHeadViewBindViewHolderListener mTempOnHeadViewBindViewHolderListener;
    private OnFooterViewBindViewHolderListener mTempOnFooterViewBindViewHolderListener;
    private int mLayoutManagerType;
    private Drawable mDefAllDivider;
    private int mDefAllDividerHeight;
    private int layoutManagerType;
    private int layoutManagerOrientation=DEF_LAYOUT_MANAGER_ORIENTATION;
    private boolean needInitAddItemDescration = false;
    private boolean hasShowEmptyView = false;


    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mViewConfig = ViewConfiguration.get(getContext());
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RefreshRecyclerView);
        mDefAllDivider = ta.getDrawable(R.styleable.RefreshRecyclerView_rv_divider);
        mDefAllDividerHeight = (int) ta.getDimension(R.styleable.RefreshRecyclerView_rv_dividerHeight, -1);
        mVerticalDivider = ta.getDrawable(R.styleable.RefreshRecyclerView_rv_dividerVertical);
        mHorizontalDivider = ta.getDrawable(R.styleable.RefreshRecyclerView_rv_dividerHorizontal);
        mVerticalDividerHeight = (int) ta.getDimension(R.styleable.RefreshRecyclerView_rv_dividerVerticalHeight, -1);
        mHorizontalDividerHeight = (int) ta.getDimension(R.styleable.RefreshRecyclerView_rv_dividerHorizontalHeight, -1);
        mItemViewBothSidesMargin = (int) ta.getDimension(R.styleable.RefreshRecyclerView_rv_itemViewBothSidesMargin, 0);
        mEmptyViewResId = ta.getResourceId(R.styleable.RefreshRecyclerView_rv_emptyView, -1);
        isKeepShowHeadOrFooter = ta.getBoolean(R.styleable.RefreshRecyclerView_rv_isEmptyViewKeepShowHeadOrFooter, false);
        isHeaderDividersEnabled = ta.getBoolean(R.styleable.RefreshRecyclerView_rv_headerDividersEnabled, false);
        isFooterDividersEnabled = ta.getBoolean(R.styleable.RefreshRecyclerView_rv_footerDividersEnabled, false);
        isNotShowGridEndDivider = ta.getBoolean(R.styleable.RefreshRecyclerView_rv_isNotShowGridEndDivider, false);

        if (ta.hasValue(R.styleable.RefreshRecyclerView_rv_layoutManager)) {
            layoutManagerType = ta.getInt(R.styleable.RefreshRecyclerView_rv_layoutManager, DEF_LAYOUT_MANAGER_TYPE);
            layoutManagerOrientation = ta.getInt(R.styleable.RefreshRecyclerView_rv_layoutManagerOrientation, DEF_LAYOUT_MANAGER_ORIENTATION);
            boolean isReverseLayout = ta.getBoolean(R.styleable.RefreshRecyclerView_rv_isReverseLayout, false);
            int gridSpanCount = ta.getInt(R.styleable.RefreshRecyclerView_rv_spanCount, DEF_GRID_SPAN_COUNT);

            switch (layoutManagerType) {
                case LAYOUT_MANAGER_TYPE_LINEAR:
                    setLayoutManager(new LinearLayoutManager(context, layoutManagerOrientation, isReverseLayout));
                    break;
                case LAYOUT_MANAGER_TYPE_GRID:
                    setLayoutManager(new GridLayoutManager(context, gridSpanCount, layoutManagerOrientation, isReverseLayout));
                    break;
                case LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                    setLayoutManager(new StaggeredGridLayoutManager(gridSpanCount, layoutManagerOrientation));
                    break;
            }
        }else{
            layoutManagerType= LAYOUT_MANAGER_TYPE_LINEAR;
            setLayoutManager(new LinearLayoutManager(context, layoutManagerOrientation, false));
        }
        setItemAnimator(new RefrechItemAnimator());
        ta.recycle();
    }
    private void initializeItemTouchHelper() {
        if (mDefaultItemTouchHelper == null) {
            mDefaultItemTouchHelper = new DefaultItemTouchHelper();
            mDefaultItemTouchHelper.attachToRecyclerView(this);
        }
    }
    private void processDefDivider(boolean isLinearLayoutManager, int layoutManagerOrientation) {
        if (!isDefaultItemDecoration) return;

        if ((null == mVerticalDivider || null == mHorizontalDivider) && null != mDefAllDivider) {
            if (isLinearLayoutManager) {
                if (layoutManagerOrientation == OrientationHelper.VERTICAL && null == mHorizontalDivider) {
                    mHorizontalDivider = mDefAllDivider;
                } else if (layoutManagerOrientation == OrientationHelper.HORIZONTAL && null == mVerticalDivider) {
                    mVerticalDivider = mDefAllDivider;
                }
            } else {
                if (null == mVerticalDivider) {
                    mVerticalDivider = mDefAllDivider;
                }

                if (null == mHorizontalDivider) {
                    mHorizontalDivider = mDefAllDivider;
                }
            }
        }

        if (mVerticalDividerHeight > 0 && mHorizontalDividerHeight > 0) return;

        if (mDefAllDividerHeight > 0) {
            if (isLinearLayoutManager) {
                if (layoutManagerOrientation == OrientationHelper.VERTICAL && mHorizontalDividerHeight <= 0) {
                    mHorizontalDividerHeight = mDefAllDividerHeight;
                } else if (layoutManagerOrientation == OrientationHelper.HORIZONTAL && mVerticalDividerHeight <= 0) {
                    mVerticalDividerHeight = mDefAllDividerHeight;
                }
            } else {
                if (mVerticalDividerHeight <= 0) {
                    mVerticalDividerHeight = mDefAllDividerHeight;
                }

                if (mHorizontalDividerHeight <= 0) {
                    mHorizontalDividerHeight = mDefAllDividerHeight;
                }
            }
        } else {
            if (isLinearLayoutManager) {
                if (layoutManagerOrientation == OrientationHelper.VERTICAL && mHorizontalDividerHeight <= 0) {
                    if (null != mHorizontalDivider) {
                        if (mHorizontalDivider.getIntrinsicHeight() > 0) {
                            mHorizontalDividerHeight = mHorizontalDivider.getIntrinsicHeight();
                        } else {
                            mHorizontalDividerHeight = DEF_DIVIDER_HEIGHT;
                        }
                    }
                } else if (layoutManagerOrientation == OrientationHelper.HORIZONTAL && mVerticalDividerHeight <= 0) {
                    if (null != mVerticalDivider) {
                        if (mVerticalDivider.getIntrinsicHeight() > 0) {
                            mVerticalDividerHeight = mVerticalDivider.getIntrinsicHeight();
                        } else {
                            mVerticalDividerHeight = DEF_DIVIDER_HEIGHT;
                        }
                    }
                }
            } else {
                if (mVerticalDividerHeight <= 0 && null != mVerticalDivider) {
                    if (mVerticalDivider.getIntrinsicHeight() > 0) {
                        mVerticalDividerHeight = mVerticalDivider.getIntrinsicHeight();
                    } else {
                        mVerticalDividerHeight = DEF_DIVIDER_HEIGHT;
                    }
                }

                if (mHorizontalDividerHeight <= 0 && null != mHorizontalDivider) {
                    if (mHorizontalDivider.getIntrinsicHeight() > 0) {
                        mHorizontalDividerHeight = mHorizontalDivider.getIntrinsicHeight();
                    } else {
                        mHorizontalDividerHeight = DEF_DIVIDER_HEIGHT;
                    }
                }
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        // Only once
        if (mEmptyViewResId != -1) {
            if (null != getParent()) {
                ViewGroup parentView = ((ViewGroup) getParent());
                View tempEmptyView1 = parentView.findViewById(mEmptyViewResId);

                if (null != tempEmptyView1) {
                    mEmptyView = tempEmptyView1;

                    if (isKeepShowHeadOrFooter) parentView.removeView(tempEmptyView1);
                } else {
                    ViewParent pParentView = parentView.getParent();
                    if (null != pParentView && pParentView instanceof ViewGroup) {
                        View tempEmptyView2 = ((ViewGroup) pParentView).findViewById(mEmptyViewResId);
                        if (null != tempEmptyView2) {
                            mEmptyView = tempEmptyView2;

                            if (isKeepShowHeadOrFooter)
                                ((ViewGroup) pParentView).removeView(tempEmptyView2);
                        }
                    }
                }
            }
            mEmptyViewResId = -1;
        } else if (isKeepShowHeadOrFooter && null != mEmptyView) {
            ViewParent emptyViewParent = mEmptyView.getParent();
            if (null != emptyViewParent && emptyViewParent instanceof ViewGroup) {
                ((ViewGroup) emptyViewParent).removeView(mEmptyView);
            }
        }

        if (null == adapter) {
            if (null != mReqAdapter) {
                if (!isKeepShowHeadOrFooter) {
                    mReqAdapter.unregisterAdapterDataObserver(mReqAdapterDataObserver);
                }
                mReqAdapter = null;
                mRefreshWrapRecyclerViewAdapter = null;

                processEmptyView();
            }

            return;
        }

        mReqAdapter = adapter;
        mRefreshWrapRecyclerViewAdapter = new RefreshWrapRecyclerViewAdapter(this, adapter, mHeaderView, mFooterView, mLayoutManagerType);

        mRefreshWrapRecyclerViewAdapter.setOnItemClickListener(mTempOnItemClickListener);
        mRefreshWrapRecyclerViewAdapter.setOnItemLongClickListener(mTempOnItemLongClickListener);
        mRefreshWrapRecyclerViewAdapter.setOnHeadViewBindViewHolderListener(mTempOnHeadViewBindViewHolderListener);
        mRefreshWrapRecyclerViewAdapter.setOnFooterViewBindViewHolderListener(mTempOnFooterViewBindViewHolderListener);

        mReqAdapter.registerAdapterDataObserver(mReqAdapterDataObserver);
        //--------------------------------
        if (adapter instanceof BaseRvAdapter) {
            BaseRvAdapter menuAdapter = (BaseRvAdapter) adapter;
            menuAdapter.setSwipeMenuCreator(mDefaultMenuCreator);
            menuAdapter.setRecyclerView(this);
            menuAdapter.setSwipeMenuItemClickListener(mDefaultMenuItemClickListener);
        }
        //----------------------------------------
        super.setAdapter(mRefreshWrapRecyclerViewAdapter);

        if (needInitAddItemDescration && null != mRefrechDefaultItemDecoration) {
            needInitAddItemDescration = false;
            super.addItemDecoration(mRefrechDefaultItemDecoration);
        }

        processEmptyView();
    }

    public void reRegisterAdapterDataObserver() {
        if (null != mReqAdapter && !mReqAdapter.hasObservers()) {
            mReqAdapter.registerAdapterDataObserver(mReqAdapterDataObserver);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mReqAdapter && mReqAdapter.hasObservers()) {
            mReqAdapter.unregisterAdapterDataObserver(mReqAdapterDataObserver);
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);

        if (null == layout) return;

        if (layout instanceof GridLayoutManager) {
            mCurGridLayoutManager = ((GridLayoutManager) layout);
            mCurGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position < getHeaderViewsCount() || position >= mReqAdapter.getItemCount() + getHeaderViewsCount()) {
                        // header or footer span
                        return mCurGridLayoutManager.getSpanCount();
                    } else {
                        // default item span
                        return 1;
                    }
                }
            });

            mLayoutManagerType = LAYOUT_MANAGER_TYPE_GRID;
            processDefDivider(false, mCurGridLayoutManager.getOrientation());
            initDefaultItemDecoration();
        } else if (layout instanceof StaggeredGridLayoutManager) {
            mLayoutManagerType = LAYOUT_MANAGER_TYPE_STAGGERED_GRID;
            processDefDivider(false, ((StaggeredGridLayoutManager) layout).getOrientation());
            initDefaultItemDecoration();
        } else if (layout instanceof LinearLayoutManager) {
            mLayoutManagerType = LAYOUT_MANAGER_TYPE_LINEAR;
            processDefDivider(true, ((LinearLayoutManager) layout).getOrientation());
            initDefaultItemDecoration();
        }
    }

    @Override
    public void addItemDecoration(ItemDecoration decor) {
        if (null == decor) return;

        // remove default ItemDecoration
        if (null != mRefrechDefaultItemDecoration) {
            removeItemDecoration(mRefrechDefaultItemDecoration);
            mRefrechDefaultItemDecoration = null;
        }

        isDefaultItemDecoration = false;

        super.addItemDecoration(decor);
    }

    private void initDefaultItemDecoration() {
        if (!isDefaultItemDecoration) return;

        if (null != mRefrechDefaultItemDecoration) {
            super.removeItemDecoration(mRefrechDefaultItemDecoration);
            mRefrechDefaultItemDecoration = null;
        }

        mRefrechDefaultItemDecoration = new RefrechDefaultItemDecoration(this, mVerticalDivider, mHorizontalDivider, mVerticalDividerHeight, mHorizontalDividerHeight);
        mRefrechDefaultItemDecoration.setItemViewBothSidesMargin(mItemViewBothSidesMargin);
        mRefrechDefaultItemDecoration.setHeaderDividersEnabled(isHeaderDividersEnabled);
        mRefrechDefaultItemDecoration.setFooterDividersEnabled(isFooterDividersEnabled);
        mRefrechDefaultItemDecoration.setNotShowGridEndDivider(isNotShowGridEndDivider);

        if (null != getAdapter()) {
            needInitAddItemDescration = false;
            super.addItemDecoration(mRefrechDefaultItemDecoration);
        } else {
            needInitAddItemDescration = true;
        }
    }

    private void processEmptyView() {
        if (null != mEmptyView) {
            boolean isShowEmptyView = (null != mReqAdapter ? mReqAdapter.getItemCount() : 0) == 0;

            if (isShowEmptyView == hasShowEmptyView) return;

            if (isKeepShowHeadOrFooter) {
                if (hasShowEmptyView) {
                    mRefreshWrapRecyclerViewAdapter.notifyItemRemoved(getHeaderViewsCount());
                }
            } else {
                mEmptyView.setVisibility(isShowEmptyView ? VISIBLE : GONE);
                setVisibility(isShowEmptyView ? GONE : VISIBLE);
            }

            this.hasShowEmptyView = isShowEmptyView;
        }
    }

    /**
     * Set EmptyView (before setAdapter)
     *
     * @param emptyView your EmptyView
     */
    public void setEmptyView(View emptyView) {
        setEmptyView(emptyView, false);
    }

    /**
     * Set EmptyView (before setAdapter)
     *
     * @param emptyView              your EmptyView
     * @param isKeepShowHeadOrFooter is Keep show HeadView or FooterView
     */
    public void setEmptyView(View emptyView, boolean isKeepShowHeadOrFooter) {
        this.mEmptyView = emptyView;
        this.isKeepShowHeadOrFooter = isKeepShowHeadOrFooter;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public void setEmptyViewKeepShowHeadOrFooter(boolean isKeepShowHeadOrFoot) {
        this.isKeepShowHeadOrFooter = isKeepShowHeadOrFoot;
    }

    public boolean isShowEmptyView() {
        return hasShowEmptyView;
    }

    public boolean isKeepShowHeadOrFooter() {
        return isKeepShowHeadOrFooter;
    }

    public void setDivider(int height, Drawable divider) {
        if (!isDefaultItemDecoration || height <= 0) return;

        this.mVerticalDividerHeight = height;
        this.mHorizontalDividerHeight = height;

        if (this.mVerticalDivider != divider) {
            this.mVerticalDivider = divider;
        }

        if (this.mHorizontalDivider != divider) {
            this.mHorizontalDivider = divider;
        }

        if (null != mRefrechDefaultItemDecoration) {
            mRefrechDefaultItemDecoration.setVerticalDividerDrawableHeight(mVerticalDividerHeight);
            mRefrechDefaultItemDecoration.setHorizontalDividerDrawableHeight(mHorizontalDividerHeight);

            mRefrechDefaultItemDecoration.setVerticalDividerDrawable(mVerticalDivider);
            mRefrechDefaultItemDecoration.setHorizontalDividerDrawable(mHorizontalDivider);

            if (null != mRefreshWrapRecyclerViewAdapter) {
                mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDivider(Drawable divider) {
        if (!isDefaultItemDecoration || (mVerticalDividerHeight <= 0 && mHorizontalDividerHeight <= 0))
            return;

        if (this.mVerticalDivider != divider) {
            this.mVerticalDivider = divider;
        }

        if (this.mHorizontalDivider != divider) {
            this.mHorizontalDivider = divider;
        }

        if (null != mRefrechDefaultItemDecoration) {
            mRefrechDefaultItemDecoration.setVerticalDividerDrawable(mVerticalDivider);
            mRefrechDefaultItemDecoration.setHorizontalDividerDrawable(mHorizontalDivider);

            if (null != mRefreshWrapRecyclerViewAdapter) {
                mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDivider(Drawable dividerVertical, Drawable dividerHorizontal) {
        if (!isDefaultItemDecoration || (mVerticalDividerHeight <= 0 && mHorizontalDividerHeight <= 0))
            return;

        if (this.mVerticalDivider != dividerVertical) {
            this.mVerticalDivider = dividerVertical;
        }

        if (this.mHorizontalDivider != dividerHorizontal) {
            this.mHorizontalDivider = dividerHorizontal;
        }

        if (null != mRefrechDefaultItemDecoration) {
            mRefrechDefaultItemDecoration.setVerticalDividerDrawable(mVerticalDivider);
            mRefrechDefaultItemDecoration.setHorizontalDividerDrawable(mHorizontalDivider);

            if (null != mRefreshWrapRecyclerViewAdapter) {
                mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerVertical(Drawable dividerVertical) {
        if (!isDefaultItemDecoration || mVerticalDividerHeight <= 0) return;

        if (this.mVerticalDivider != dividerVertical) {
            this.mVerticalDivider = dividerVertical;
        }

        if (null != mRefrechDefaultItemDecoration) {
            mRefrechDefaultItemDecoration.setVerticalDividerDrawable(mVerticalDivider);

            if (null != mRefreshWrapRecyclerViewAdapter) {
                mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerHorizontal(Drawable dividerHorizontal) {
        if (!isDefaultItemDecoration || mHorizontalDividerHeight <= 0) return;

        if (this.mHorizontalDivider != dividerHorizontal) {
            this.mHorizontalDivider = dividerHorizontal;
        }

        if (null != mRefrechDefaultItemDecoration) {
            mRefrechDefaultItemDecoration.setHorizontalDividerDrawable(mHorizontalDivider);

            if (null != mRefreshWrapRecyclerViewAdapter) {
                mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerHeight(int height) {
        if (!isDefaultItemDecoration) return;

        this.mVerticalDividerHeight = height;
        this.mHorizontalDividerHeight = height;

        if (null != mRefrechDefaultItemDecoration) {
            mRefrechDefaultItemDecoration.setVerticalDividerDrawableHeight(mVerticalDividerHeight);
            mRefrechDefaultItemDecoration.setHorizontalDividerDrawableHeight(mHorizontalDividerHeight);

            if (null != mRefreshWrapRecyclerViewAdapter) {
                mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerVerticalHeight(int height) {
        if (!isDefaultItemDecoration) return;

        this.mVerticalDividerHeight = height;

        if (null != mRefrechDefaultItemDecoration) {
            mRefrechDefaultItemDecoration.setVerticalDividerDrawableHeight(mVerticalDividerHeight);

            if (null != mRefreshWrapRecyclerViewAdapter) {
                mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setDividerHorizontalHeight(int height) {
        if (!isDefaultItemDecoration) return;

        this.mHorizontalDividerHeight = height;

        if (null != mRefrechDefaultItemDecoration) {
            mRefrechDefaultItemDecoration.setHorizontalDividerDrawableHeight(mHorizontalDividerHeight);

            if (null != mRefreshWrapRecyclerViewAdapter) {
                mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setItemViewBothSidesMargin(int bothSidesMargin) {
        if (!isDefaultItemDecoration) return;

        this.mItemViewBothSidesMargin = bothSidesMargin;

        if (null != mRefrechDefaultItemDecoration) {
            mRefrechDefaultItemDecoration.setItemViewBothSidesMargin(mItemViewBothSidesMargin);

            if (null != mRefreshWrapRecyclerViewAdapter) {
                mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * HeadView onBindViewHolder callback
     *
     * @param onHeadViewBindViewHolderListener OnHeadViewBindViewHolderListener
     */
    public void setOnHeadViewBindViewHolderListener(RefreshRecyclerView.OnHeadViewBindViewHolderListener onHeadViewBindViewHolderListener) {
        if (null != mRefreshWrapRecyclerViewAdapter) {
            mRefreshWrapRecyclerViewAdapter.setOnHeadViewBindViewHolderListener(onHeadViewBindViewHolderListener);
        } else {
            this.mTempOnHeadViewBindViewHolderListener = onHeadViewBindViewHolderListener;
        }
    }

    /**
     * FooterView onBindViewHolder callback
     *
     * @param onFooterViewBindViewHolderListener OnFooterViewBindViewHolderListener
     */
    public void setOnFooterViewBindViewHolderListener(RefreshRecyclerView.OnFooterViewBindViewHolderListener onFooterViewBindViewHolderListener) {
        if (null != mRefreshWrapRecyclerViewAdapter) {
            mRefreshWrapRecyclerViewAdapter.setOnFooterViewBindViewHolderListener(onFooterViewBindViewHolderListener);
        } else {
            this.mTempOnFooterViewBindViewHolderListener = onFooterViewBindViewHolderListener;
        }
    }

    public void addHeaderView(View v) {
        addHeaderView(v, false);
    }

    public void addHeaderView(View v, boolean isScrollTo) {
        if (mHeaderView.contains(v)) return;

        mHeaderView.add(v);
        if (null != mRefreshWrapRecyclerViewAdapter) {
            int pos = mHeaderView.size() - 1;
            mRefreshWrapRecyclerViewAdapter.notifyItemInserted(pos);

            if (isScrollTo) {
                scrollToPosition(pos);
            }
        }
    }

    public boolean removeHeaderView(View v) {
        if (!mHeaderView.contains(v)) return false;

        if (null != mRefreshWrapRecyclerViewAdapter) {
            mRefreshWrapRecyclerViewAdapter.notifyItemRemoved(mHeaderView.indexOf(v));
        }
        return mHeaderView.remove(v);
    }

    public void addFooterView(View v) {
        addFooterView(v, false);
    }

    public void addFooterView(View v, boolean isScrollTo) {
        if (mFooterView.contains(v)) return;

        mFooterView.add(v);
        if (null != mRefreshWrapRecyclerViewAdapter) {
            int pos = (null == mReqAdapter ? 0 : mReqAdapter.getItemCount()) + getHeaderViewsCount() + mFooterView.size() - 1;
            mRefreshWrapRecyclerViewAdapter.notifyItemInserted(pos);
            if (isScrollTo) {
                scrollToPosition(pos);
            }
        }
    }

    public boolean removeFooterView(View v) {
        if (!mFooterView.contains(v)) return false;

        if (null != mRefreshWrapRecyclerViewAdapter) {
            int pos = (null == mReqAdapter ? 0 : mReqAdapter.getItemCount()) + getHeaderViewsCount() + mFooterView.indexOf(v);
            mRefreshWrapRecyclerViewAdapter.notifyItemRemoved(pos);
        }
        return mFooterView.remove(v);
    }

    public int getHeaderViewsCount() {
        return mHeaderView.size();
    }

    public int getFooterViewsCount() {
        return mFooterView.size();
    }

    public int getFirstVisiblePosition() {
        LayoutManager layoutManager = getLayoutManager();

        if (null == layoutManager) return 0;

        int ret = -1;

        switch (mLayoutManagerType) {
            case LAYOUT_MANAGER_TYPE_LINEAR:
                ret = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition() - getHeaderViewsCount();
                break;
            case LAYOUT_MANAGER_TYPE_GRID:
                ret = ((GridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition() - getHeaderViewsCount();
                break;
            case LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                StaggeredGridLayoutManager tempStaggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] firstVisibleItemPositions = new int[tempStaggeredGridLayoutManager.getSpanCount()];
                tempStaggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(firstVisibleItemPositions);
                ret = firstVisibleItemPositions[0] - getHeaderViewsCount();
                break;
        }

        return ret < 0 ? 0 : ret;
    }

    public int getLastVisiblePosition() {
        LayoutManager layoutManager = getLayoutManager();
        if (null == layoutManager) return -1;

        int curItemCount = (null != mReqAdapter ? mReqAdapter.getItemCount() - 1 : 0);
        int ret = -1;

        switch (mLayoutManagerType) {
            case LAYOUT_MANAGER_TYPE_LINEAR:
                ret = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition() - getHeaderViewsCount();
                if (ret > curItemCount) {
                    ret -= getFooterViewsCount();
                }
                break;
            case LAYOUT_MANAGER_TYPE_GRID:
                ret = ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition() - getHeaderViewsCount();
                if (ret > curItemCount) {
                    ret -= getFooterViewsCount();
                }
                break;
            case LAYOUT_MANAGER_TYPE_STAGGERED_GRID:
                StaggeredGridLayoutManager tempStaggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] lastVisibleItemPositions = new int[tempStaggeredGridLayoutManager.getSpanCount()];
                tempStaggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(lastVisibleItemPositions);
                if (lastVisibleItemPositions.length > 0) {
                    int maxPos = lastVisibleItemPositions[0];
                    for (int curPos : lastVisibleItemPositions) {
                        if (curPos > maxPos) maxPos = curPos;
                    }
                    ret = maxPos - getHeaderViewsCount();
                    if (ret > curItemCount) {
                        ret -= getFooterViewsCount();
                    }
                }
                break;
        }

        return ret < 0 ? (null != mReqAdapter ? mReqAdapter.getItemCount() - 1 : 0) : ret;
    }

    public void setHeaderDividersEnabled(boolean isHeaderDividersEnabled) {
        this.isHeaderDividersEnabled = isHeaderDividersEnabled;
        if (isDefaultItemDecoration && null != mRefrechDefaultItemDecoration) {
            mRefrechDefaultItemDecoration.setHeaderDividersEnabled(isHeaderDividersEnabled);

            if (null != mRefreshWrapRecyclerViewAdapter) {
                mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setFooterDividersEnabled(boolean isFooterDividersEnabled) {
        this.isFooterDividersEnabled = isFooterDividersEnabled;
        if (isDefaultItemDecoration && null != mRefrechDefaultItemDecoration) {
            mRefrechDefaultItemDecoration.setFooterDividersEnabled(isFooterDividersEnabled);

            if (null != mRefreshWrapRecyclerViewAdapter) {
                mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    public void setNotShowGridEndDivider(boolean isNotShowGridEndDivider) {
        this.isNotShowGridEndDivider = isNotShowGridEndDivider;
        if (isDefaultItemDecoration && null != mRefrechDefaultItemDecoration) {
            mRefrechDefaultItemDecoration.setNotShowGridEndDivider(isNotShowGridEndDivider);

            if (null != mRefreshWrapRecyclerViewAdapter) {
                mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }
    //-----------------------------------------------------start----------------------------------------------------------------
    /**
     * Set to click menu listener.
     *
     * @param swipeMenuItemClickListener listener.
     */
    public void setSwipeMenuItemClickListener(OnSwipeMenuItemClickListener swipeMenuItemClickListener) {
        this.mSwipeMenuItemClickListener = swipeMenuItemClickListener;
    }
    /**
     * Set can long press swipe.
     *
     * @param canSwipe swipe true, otherwise is can't.
     */
    public void setItemViewSwipeEnabled(boolean canSwipe) {
        initializeItemTouchHelper();
        isInterceptTouchEvent = !canSwipe;
        mDefaultItemTouchHelper.setItemViewSwipeEnabled(canSwipe);
    }

    /**
     * Get can long press swipe.
     *
     * @return swipe true, otherwise is can't.
     */
    public boolean isItemViewSwipeEnabled() {
        initializeItemTouchHelper();
        return this.mDefaultItemTouchHelper.isItemViewSwipeEnabled();
    }
    /**
     * Set to create menu listener.
     *
     * @param swipeMenuCreator listener.
     */
    public void setSwipeMenuCreator(SwipeMenuCreator swipeMenuCreator) {
        this.mSwipeMenuCreator = swipeMenuCreator;
        isSwipeMenu=true;
    }

    /**
     * Default swipe menu creator.
     */
    private SwipeMenuCreator mDefaultMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            if (mSwipeMenuCreator != null) {
                mSwipeMenuCreator.onCreateMenu(swipeLeftMenu, swipeRightMenu, viewType);
            }
        }
    };

    /**
     * Default swipe menu item click listener.
     */
    private OnSwipeMenuItemClickListener mDefaultMenuItemClickListener = new OnSwipeMenuItemClickListener() {
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            if (mSwipeMenuItemClickListener != null) {
                mSwipeMenuItemClickListener.onItemClick(closeable, adapterPosition, menuPosition, direction);
            }
        }
    };
    /**
     * open menu on left.
     *
     * @param position position.
     */
    public void smoothOpenLeftMenu(int position) {
        smoothOpenMenu(position, LEFT_DIRECTION, SwipeMenuLayout.DEFAULT_SCROLLER_DURATION);
    }

    /**
     * open menu on left.
     *
     * @param position position.
     * @param duration time millis.
     */
    public void smoothOpenLeftMenu(int position, int duration) {
        smoothOpenMenu(position, LEFT_DIRECTION, duration);
    }

    /**
     * open menu on right.
     *
     * @param position position.
     */
    public void smoothOpenRightMenu(int position) {
        smoothOpenMenu(position, RIGHT_DIRECTION, SwipeMenuLayout.DEFAULT_SCROLLER_DURATION);
    }

    /**
     * open menu on right.
     *
     * @param position position.
     * @param duration time millis.
     */
    public void smoothOpenRightMenu(int position, int duration) {
        smoothOpenMenu(position, RIGHT_DIRECTION, duration);
    }
    /**
     * open menu.
     *
     * @param position  position.
     * @param direction use {@link #LEFT_DIRECTION}, {@link #RIGHT_DIRECTION}.
     * @param duration  time millis.
     */
    public void smoothOpenMenu(int position, @DirectionMode int direction, int duration) {
        if (mOldSwipedLayout != null) {
            if (mOldSwipedLayout.isMenuOpen()) {
                mOldSwipedLayout.smoothCloseMenu();
            }
        }
        ViewHolder vh = findViewHolderForAdapterPosition(position);
        if (vh != null) {
            View itemView = getSwipeMenuView(vh.itemView);
            if (itemView != null && itemView instanceof SwipeMenuLayout) {
                mOldSwipedLayout = (SwipeMenuLayout) itemView;
                if (direction == RIGHT_DIRECTION) {
                    mOldTouchedPosition = position;
                    mOldSwipedLayout.smoothOpenRightMenu(duration);
                } else if (direction == LEFT_DIRECTION) {
                    mOldTouchedPosition = position;
                    mOldSwipedLayout.smoothOpenLeftMenu(duration);
                }
            }
        }
    }

    /**
     * Close menu.
     */
    public void smoothCloseMenu() {
        if (mOldSwipedLayout != null && mOldSwipedLayout.isMenuOpen()) {
            mOldSwipedLayout.smoothCloseMenu();
        }
    }

    private View getSwipeMenuView(View itemView) {
        if (itemView instanceof SwipeMenuLayout) return itemView;
        List<View> unvisited = new ArrayList<>();
        unvisited.add(itemView);
        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            if (!(child instanceof ViewGroup)) { // view
                continue;
            }
            if (child instanceof SwipeMenuLayout) return child;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) unvisited.add(group.getChildAt(i));
        }
        return itemView;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean isIntercepted = super.onInterceptTouchEvent(e);
        if(!isSwipeMenu){
            return isIntercepted;
        }

        if (!isInterceptTouchEvent) {
            return isIntercepted;
        } else {
            if (e.getPointerCount() > 1) return true;
            int action = e.getAction();
            int x = (int) e.getX();
            int y = (int) e.getY();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = x;
                    mDownY = y;
                    isIntercepted = false;

                    int touchingPosition = getChildAdapterPosition(findChildViewUnder(x, y));
                    if (touchingPosition != mOldTouchedPosition && mOldSwipedLayout != null && mOldSwipedLayout.isMenuOpen()) {
                        mOldSwipedLayout.smoothCloseMenu();
                        isIntercepted = true;
                    }

                    if (isIntercepted) {
                        mOldSwipedLayout = null;
                        mOldTouchedPosition = INVALID_POSITION;
                    } else {
                        ViewHolder vh = findViewHolderForAdapterPosition(touchingPosition);
                        if (vh != null) {
                            View itemView = getSwipeMenuView(vh.itemView);
                            if (itemView != null && itemView instanceof SwipeMenuLayout) {
                                mOldSwipedLayout = (SwipeMenuLayout) itemView;
                                mOldTouchedPosition = touchingPosition;
                            }
                        }
                    }
                    break;
                // They are sensitive to retain sliding and inertia.
                case MotionEvent.ACTION_MOVE:
                    isIntercepted = handleUnDown(x, y, isIntercepted);
                    ViewParent viewParent = getParent();
                    if (viewParent != null) {
                        viewParent.requestDisallowInterceptTouchEvent(!isIntercepted);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isIntercepted = handleUnDown(x, y, isIntercepted);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    isIntercepted = handleUnDown(x, y, isIntercepted);
                    break;
            }
        }
        return isIntercepted;
    }
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(!isSwipeMenu){
            return super.onTouchEvent(e);
        }
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOldSwipedLayout != null && mOldSwipedLayout.isMenuOpen()) {
                    mOldSwipedLayout.smoothCloseMenu();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(e);
    }
    private boolean handleUnDown(int x, int y, boolean defaultValue) {
        int disX = mDownX - x;
        int disY = mDownY - y;
        // swipe
        if (Math.abs(disX) > mViewConfig.getScaledTouchSlop())
            defaultValue = false;
        // click
        if (Math.abs(disY) < mViewConfig.getScaledTouchSlop() && Math.abs(disX) < mViewConfig.getScaledTouchSlop())
            defaultValue = false;
        return defaultValue;
    }
    //-----------------------------------------------------------------------------------------------------------------------
    public int getCurLayoutManagerType() {
        return mLayoutManagerType;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        if (null == mRefreshWrapRecyclerViewAdapter) {
            mTempOnItemClickListener = listener;
        } else {
            mRefreshWrapRecyclerViewAdapter.setOnItemClickListener(listener);
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (null == mRefreshWrapRecyclerViewAdapter) {
            mTempOnItemLongClickListener = listener;
        } else {
            mRefreshWrapRecyclerViewAdapter.setOnItemLongClickListener(listener);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(RefreshRecyclerView familiarRecyclerView, View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(RefreshRecyclerView familiarRecyclerView, View view, int position);
    }

    public interface OnHeadViewBindViewHolderListener {
        void onHeadViewBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean isInitializeInvoke);
    }

    public interface OnFooterViewBindViewHolderListener {
        void onFooterViewBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean isInitializeInvoke);
    }

    private AdapterDataObserver mReqAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            mRefreshWrapRecyclerViewAdapter.notifyDataSetChanged();
            processEmptyView();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            mRefreshWrapRecyclerViewAdapter.notifyItemInserted(getHeaderViewsCount() + positionStart);
            processEmptyView();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mRefreshWrapRecyclerViewAdapter.notifyItemRemoved(getHeaderViewsCount() + positionStart);
            processEmptyView();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mRefreshWrapRecyclerViewAdapter.notifyItemRangeChanged(getHeaderViewsCount() + positionStart, itemCount);
            processEmptyView();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mRefreshWrapRecyclerViewAdapter.notifyItemMoved(getHeaderViewsCount() + fromPosition, getHeaderViewsCount() + toPosition);
        }
    };

}
