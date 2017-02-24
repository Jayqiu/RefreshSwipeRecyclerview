package com.threehalf.rsrecyclerview.demo;

import android.content.Context;

import com.threehalf.rsrecyclerview.refresh.BaseRvAdapter;

/**
 * @author jayqiu
 * @describe
 * @date 2017/2/15 14:43
 */
public class ListViewAndGridViewAdapter extends BaseRvAdapter<String> {
    public ListViewAndGridViewAdapter(Context context) {
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
        holder.setText(R.id.tv_txt,bean);
    }
}
