package com.threehalf.rsrecyclerview.demo;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.threehalf.rsrecyclerview.refresh.RefreshRecyclerView;
import com.threehalf.rsrecyclerview.refresh.RefreshSwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author jayqiu
 * @describe
 * @date 2017/2/15 14:33
 */
public class ActListView extends FragmentActivity {

    @Bind(R.id.mRecyclerView)
    RefreshRecyclerView mRecyclerView;
    private boolean isVertical = true;
    private List<String> mDatas;
    private  ListViewAndGridViewAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isVertical = getIntent().getBooleanExtra("isVertical", true);
        if (isVertical) {
            setContentView(R.layout.act_layout_linear_ver);
        } else {
            setContentView(R.layout.act_layout_linear_hor);
        }
        ButterKnife.bind(this);
        mDatas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mDatas.add("item:" + i);
        }
        mRecyclerView.setOnItemClickListener(new RefreshRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(RefreshRecyclerView refreshRecyclerView, View view, int position) {
                Log.i("----------", "onItemClick = " + refreshRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ActListView.this, "onItemClick = " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setOnItemLongClickListener(new RefreshRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RefreshRecyclerView refreshRecyclerView, View view, int position) {
                Log.i("----------", "onItemLongClick = " + refreshRecyclerView + " _ " + view + " _ " + position);
                Toast.makeText(ActListView.this, "onItemLongClick = " + position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addHeaderView(new HeadView(this));
        mRecyclerView.addFooterView(new FooterView(this));
        adapter=new ListViewAndGridViewAdapter(this);
        mRecyclerView.setAdapter(adapter);
        adapter.addFirst(mDatas);
    }
}
