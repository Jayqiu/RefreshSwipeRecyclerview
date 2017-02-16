# RefreshSwipeRecyclerview
一个Android ListView、GridView、瀑布流的 下拉刷新，上拉加载更多,侧滑显示菜单.
### 效果图
![](https://github.com/Jayqiu/RefreshSwipeRecyclerview/blob/master/AndroidRsRecyclerView/screenshot/img.gif)  
### Adapter（BaseRrAdapter）适配
继承BaseRrAdapter<T>
```
/**
     * 取得ItemView的布局文件
     *
     * @return
     */
    public abstract int getItemLayoutID(int viewType);
```
### 自定义配置
    参数 | 类型 | 默认值 | 说明
--- | --- | ---| ---
rv_divider                 | reference / color               | 无        | 全局分割线divider
rv_dividerVertical         | reference / color               | 无        | 垂直分割线divider
rv_dividerHorizontal       | reference / color               | 无        | 水平分割线divider
rv_dividerHeight           | dimension                      | 1px       | 全局分割线size
rv_dividerVerticalHeight   | dimension                      | 1px       | 垂直分割线size
rv_dividerHorizontalHeight | dimension                      | 1px       | 水平分割线size
rv_isNotShowGridEndDivider | boolean                        | false     | 是否不显示Grid最后item的分割线
rv_itemViewBothSidesMargin | dimension                      | 无        | itemView两边的边距（不会设置headerView和footerView的两边）
rv_emptyView               | reference                      | 无        | emptyView id
rv_isEmptyViewKeepShowHeadOrFooter | boolean                | false     | 显示EmptyView时，是否保留显示已设置的HeadView和FooterView
rv_layoutManager           | linear / grid / staggeredGrid  | 无        | 布局类型
rv_layoutManagerOrientation| horizontal / vertical          | vertical  | 布局方向
rv_spanCount               | integer                        | 2         | 格子数量，frv_layoutManager=grid / staggeredGrid时有效
rv_headerDividersEnabled   | boolean                        | false     | 是否启用headView中的分割线
rv_footerDividersEnabled   | boolean                        | false     | 是否启用footerView中的分割线
