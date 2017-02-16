package com.threehalf.rsrecyclerview.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.threehalf.rsrecyclerview.refresh.BaseRrAdapter;
import com.threehalf.rsrecyclerview.refresh.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author jayqiu
 * @describe
 * @date 2017/2/15 14:33
 */
public class ActStaggeredGridView extends FragmentActivity {

    @Bind(R.id.mRecyclerView)
    RefreshRecyclerView mRecyclerView;
    private boolean isVertical = true;
    private List<String> mDatas;
    private  MyAdapter adapter;
    private List<Integer> mViewHeights;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isVertical = getIntent().getBooleanExtra("isVertical", true);
        if (isVertical) {
            setContentView(R.layout.act_layout_staggered_grid_ver);
        } else {
            setContentView(R.layout.act_layout_staggered_grid_hor);
        }
        ButterKnife.bind(this);
        mDatas = new ArrayList<>();
        mViewHeights=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mDatas.add("item:" + i);
            mViewHeights.add((int)(300 + Math.random() * 300));
        }
        mRecyclerView.setOnItemClickListener(new RefreshRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RefreshRecyclerView refreshRecyclerView, View view, int position) {
                Log.i("----------", "onItemClick = " + refreshRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ActStaggeredGridView.this, "onItemClick = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setOnItemLongClickListener(new RefreshRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RefreshRecyclerView refreshRecyclerView, View view, int position) {
                Log.i("----------", "onItemLongClick = " + refreshRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ActStaggeredGridView.this, "onItemLongClick = " + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addHeaderView(new HeadView(this));
        mRecyclerView.addFooterView(new FooterView(this));
        adapter=new MyAdapter(this);
        mRecyclerView.setAdapter(adapter);
        adapter.addFirst(mDatas);
    }

    public class MyAdapter extends BaseRrAdapter<String> {
        public MyAdapter(Context context) {
            super(context);

        }
        @Override
        public int getItemLayoutID(int viewType) {
            return R.layout.item_view_linear;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        protected void onBindDataToView(RvCommonViewHolder holder, String bean, int position) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (isVertical) {
                lp.height = mViewHeights.get(position);
            } else {
                lp.width = mViewHeights.get(position);
            }
            holder.setText(R.id.tv_txt,bean);
        }
    }
}
