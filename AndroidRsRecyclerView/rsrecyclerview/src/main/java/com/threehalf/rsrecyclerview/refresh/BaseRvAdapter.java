package com.threehalf.rsrecyclerview.refresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.threehalf.rsrecyclerview.R;
import com.threehalf.rsrecyclerview.swipe.OnSwipeMenuItemClickListener;
import com.threehalf.rsrecyclerview.swipe.SwipeMenu;
import com.threehalf.rsrecyclerview.swipe.SwipeMenuCreator;
import com.threehalf.rsrecyclerview.swipe.SwipeMenuLayout;
import com.threehalf.rsrecyclerview.swipe.SwipeMenuView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jayqiu
 * @describe RecyclerView Adapter
 * @date 2016/10/11 18:42
 */
public abstract class BaseRvAdapter<T> extends RecyclerView.Adapter<BaseRvAdapter.RvCommonViewHolder> {
    protected List<T> mBeans = new ArrayList<>();
    protected Context mContext;
    protected boolean mAnimateItems = true;
    protected int mLastAnimatedPosition = -1;
    private RefreshRecyclerView mRefreshRecyclerView;
    /**
     * Swipe menu creator。
     */
    private SwipeMenuCreator mSwipeMenuCreator;

    /**
     * Swipe menu click listener。
     */
    private OnSwipeMenuItemClickListener mSwipeMenuItemClickListener;

    /**
     * Set to create menu listener.
     *
     * @param swipeMenuCreator listener.
     */
    void setSwipeMenuCreator(SwipeMenuCreator swipeMenuCreator) {
        this.mSwipeMenuCreator = swipeMenuCreator;
    }

    /**
     * Set to click menu listener.
     *
     * @param swipeMenuItemClickListener listener.
     */
    void setSwipeMenuItemClickListener(OnSwipeMenuItemClickListener swipeMenuItemClickListener) {
        this.mSwipeMenuItemClickListener = swipeMenuItemClickListener;
    }
    void setRecyclerView(RefreshRecyclerView familiarRecyclerView){
        this.mRefreshRecyclerView = familiarRecyclerView;
    }

    public BaseRvAdapter(Context context) {
        mContext = context;
    }

    public BaseRvAdapter(Context context, List<T> beans) {
        mContext = context;
        mBeans.clear();
        if (beans != null && beans.size() > 0) {
            mBeans.addAll(beans);
        }
    }

    @Override
    public BaseRvAdapter.RvCommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(getItemLayoutID(viewType), parent, false);
        if (mSwipeMenuCreator != null) {
            SwipeMenuLayout swipeMenuLayout = (SwipeMenuLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_item_default, parent, false);

            SwipeMenu swipeLeftMenu = new SwipeMenu(swipeMenuLayout, viewType);
            SwipeMenu swipeRightMenu = new SwipeMenu(swipeMenuLayout, viewType);

            mSwipeMenuCreator.onCreateMenu(swipeLeftMenu, swipeRightMenu, viewType);

            int leftMenuCount = swipeLeftMenu.getMenuItems().size();
            if (leftMenuCount > 0) {
                SwipeMenuView swipeLeftMenuView = (SwipeMenuView) swipeMenuLayout.findViewById(R.id.swipe_left);
                if(swipeLeftMenu.getOrientation()==SwipeMenu.HORIZONTAL){
                    swipeLeftMenuView.setOrientation(LinearLayout.HORIZONTAL);
                }else {
                    swipeLeftMenuView.setOrientation(LinearLayout.VERTICAL);
                }

                swipeLeftMenuView.bindMenu(swipeLeftMenu, RefreshRecyclerView.LEFT_DIRECTION);
                swipeLeftMenuView.setRecyclerView(mRefreshRecyclerView);
                swipeLeftMenuView.bindMenuItemClickListener(mSwipeMenuItemClickListener, swipeMenuLayout);
            }

            int rightMenuCount = swipeRightMenu.getMenuItems().size();
            if (rightMenuCount > 0) {
                SwipeMenuView swipeRightMenuView = (SwipeMenuView) swipeMenuLayout.findViewById(R.id.swipe_right);
                if(swipeLeftMenu.getOrientation()==SwipeMenu.HORIZONTAL){
                    swipeRightMenuView.setOrientation(LinearLayout.HORIZONTAL);
                }else {
                    swipeRightMenuView.setOrientation(LinearLayout.VERTICAL);
                }
                swipeRightMenuView.bindMenu(swipeRightMenu, RefreshRecyclerView.RIGHT_DIRECTION);
                swipeRightMenuView.setRecyclerView(mRefreshRecyclerView);
                swipeRightMenuView.bindMenuItemClickListener(mSwipeMenuItemClickListener, swipeMenuLayout);
            }

            if (leftMenuCount > 0 || rightMenuCount > 0) {
                ViewGroup viewGroup = (ViewGroup) swipeMenuLayout.findViewById(R.id.swipe_content);
                viewGroup.addView(contentView);
                contentView = swipeMenuLayout;

            }
        }
        RvCommonViewHolder holder = new RvCommonViewHolder(contentView);
        return holder;
    }


    @Override
    public void onBindViewHolder(BaseRvAdapter.RvCommonViewHolder holder, int position) {
        View itemView = holder.itemView;
        if (itemView instanceof SwipeMenuLayout) {
            SwipeMenuLayout swipeMenuLayout = (SwipeMenuLayout) itemView;
            int childCount = swipeMenuLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = swipeMenuLayout.getChildAt(i);
                if (childView instanceof SwipeMenuView) {
                    ((SwipeMenuView) childView).bindAdapterViewHolder(holder);
                }
            }
        }
        if (mBeans != null && mBeans.size() > 0) {
            onBindDataToView(holder, mBeans.get(position), position);
        }

    }


    @Override
    public abstract int getItemViewType(int position);
    /**
     * 绑定数据到Item的控件中去
     *
     * @param holder
     * @param bean
     */
    protected abstract void onBindDataToView(RvCommonViewHolder holder, T bean, int position);

    /**
     * 取得ItemView的布局文件
     *
     * @return
     */
    public abstract int getItemLayoutID(int viewType);

    @Override
    public int getItemCount() {
        int size= mBeans == null ? 0 : mBeans.size();
        return size;
    }







    public class RvCommonViewHolder extends
            RecyclerView.ViewHolder {
        private final SparseArray<View> mViews;
        public View itemView;

        public RvCommonViewHolder(View itemView) {
            super(itemView);
            this.mViews = new SparseArray<>();
            this.itemView = itemView;
            //添加Item的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBeans != null && mBeans.size() > 0) {
                        onItemClick(BaseRvAdapter.this.mBeans.get(getAdapterPosition()), getAdapterPosition());
                    }

                }
            });
        }

        public <T extends View> T getView(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        public void setText(int viewId, String text) {
            TextView tv = getView(viewId);
            tv.setText(text + "");
        }

        /**
         * 加载drawable中的图片
         *
         * @param viewId
         * @param resId
         */
        public void setImage(int viewId, int resId) {
            ImageView iv = getView(viewId);
            iv.setImageResource(resId);
        }

    }

    /**
     * ItemView的单击事件(如果需要，重写此方法就行)
     *
     * @param position
     **/
    protected void onItemClick(T data, int position) {
    }


    public List<T> getData() {
        return mBeans;
    }

    public void remove(int position) {
        if (mBeans != null && mBeans.size() >= position) {
            mBeans.remove(position);
            notifyDataSetChanged();
        }
    }

    public void remove(List<T> data) {

        if (mBeans != null && data != null) {
            mBeans.removeAll(data);
        }
        notifyDataSetChanged();
    }
    public void add(T bean) {
        mBeans.add(bean);
        notifyDataSetChanged();
    }

    public void addFirst(List<T> beans) {
        mBeans.clear();
        if (beans != null && beans.size() > 0) {
            mBeans.addAll(beans);
        }
        notifyDataSetChanged();
    }

    /**
     * 加载更多数据
     *
     * @param data
     */
    public void addMore(List<T> data) {
        if (data != null && data.size() > 0) {
            this.mBeans.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        if (mBeans != null) {
            mBeans.clear();
            notifyDataSetChanged();
        }
    }


}
