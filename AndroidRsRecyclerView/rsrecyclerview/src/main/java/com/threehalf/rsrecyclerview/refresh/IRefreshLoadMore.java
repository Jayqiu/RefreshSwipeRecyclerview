package com.threehalf.rsrecyclerview.refresh;

import android.view.View;

/**
 * @author jayqiu
 * @describe
 * @date 2017/2/15 10:28
 */
public interface IRefreshLoadMore {
    void showLoading();

    void showNormal();
    void showNormalError();
    boolean isLoading();

    void showNormalMsg(String msg );

    View getView();
}
