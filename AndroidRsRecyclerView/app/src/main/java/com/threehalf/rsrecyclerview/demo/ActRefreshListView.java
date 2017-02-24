package com.threehalf.rsrecyclerview.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.threehalf.rsrecyclerview.refresh.RefreshRecyclerView;
import com.threehalf.rsrecyclerview.refresh.RefreshSwipeRecyclerView;
import com.threehalf.rsrecyclerview.swipe.Closeable;
import com.threehalf.rsrecyclerview.swipe.OnSwipeMenuItemClickListener;
import com.threehalf.rsrecyclerview.swipe.SwipeMenu;
import com.threehalf.rsrecyclerview.swipe.SwipeMenuCreator;
import com.threehalf.rsrecyclerview.swipe.SwipeMenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author jayqiu
 * @describe
 * @date 2017/2/15 14:33
 */
public class ActRefreshListView extends FragmentActivity {

    @Bind(R.id.refreshListRecyclerView)
    RefreshSwipeRecyclerView mRsRecyclerView;
    RefreshRecyclerView mRecyclerView;
    private boolean isVertical = true;
    private List<String> mDatas;
    private  ListViewAndGridViewAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.act_layout_refresh_listview);

        ButterKnife.bind(this);
//        mRsRecyclerView.setLoadMoreView(new LoadMoreView(this));
        mRsRecyclerView.setColorSchemeColors( Color.BLUE, Color.YELLOW, Color.GREEN);
        mRecyclerView=mRsRecyclerView.getRefreshRecyclerView();
        //
        mDatas = new ArrayList<>();
        mDatas.addAll(getDatas());
        mRsRecyclerView.setOnItemClickListener(new RefreshRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RefreshRecyclerView refreshRecyclerView, View view, int position) {
                Log.i("----------", "onItemClick = " + refreshRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ActRefreshListView.this, "onItemClick = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mRsRecyclerView.setOnItemLongClickListener(new RefreshRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RefreshRecyclerView refreshRecyclerView, View view, int position) {
                Log.i("----------", "onItemLongClick = " + refreshRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ActRefreshListView.this, "onItemLongClick = " + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mRsRecyclerView.setOnPullRefreshListener(new RefreshSwipeRecyclerView.OnPullRefreshListener() {
            @Override
            public void onPullRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.clear();
                        mDatas.addAll(getDatas());
                        adapter.addFirst(mDatas);
                        mRsRecyclerView.pullRefreshComplete();
                        mRsRecyclerView.setLoadMoreEnabled(true);
                    }
                }, 2000);
            }
        });
        mRsRecyclerView.setOnLoadMoreListener(new RefreshSwipeRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<String> newDatas = getDatas();
                        mDatas.addAll(newDatas);
                        adapter.addMore(newDatas);
                        mRsRecyclerView.loadMoreComplete();
//                        mRsRecyclerView.loadMoreError();
                        mRsRecyclerView.setLoadMoreEnabled(false);
                    }
                }, 1000);
            }
        });
        mRsRecyclerView. setLoadMoreEnabled(true);

//        mRsRecyclerView.setLayoutManager(setLayoutManager());
        mRecyclerView.setSwipeMenuItemClickListener(new OnSwipeMenuItemClickListener() {
            @Override
            public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, @RefreshRecyclerView.DirectionMode int direction) {
                Toast.makeText(ActRefreshListView.this,adapter.getData().get(adapterPosition),Toast.LENGTH_SHORT).show();
                closeable.smoothCloseMenu();
            }
        });
        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addHeaderView(new HeadView(this));
        adapter=new ListViewAndGridViewAdapter(this);
        mRecyclerView.setAdapter(adapter);
        adapter.addFirst(mDatas);
    }
    public RecyclerView.LayoutManager setLayoutManager() {
        int orientation = LinearLayoutManager.VERTICAL;
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//
//        linearLayoutManager.setOrientation(orientation); //这里
        return new GridLayoutManager(this, 2);
    }
    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = dip2px(ActRefreshListView.this, 80);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;


            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(ActRefreshListView.this)
                        .setBackgroundColor(getResources().getColor(R.color.colorAccent))
                        .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

            }
        }
    };
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private List<String> getDatas() {
        List<String> tempDatas = new ArrayList<>();
        int curMaxData =  mDatas.size();
        for (int i = 0; i < 8; i++) {
            tempDatas.add("item:" + (curMaxData + i));
        }

        return tempDatas;
    }

}
