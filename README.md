# RefreshSwipeRecyclerview
一个Android ListView、GridView、瀑布流的 下拉刷新，上拉加载更多,侧滑显示菜单.

### 效果图
![](https://github.com/Jayqiu/RefreshSwipeRecyclerview/blob/master/AndroidRsRecyclerView/screenshot/img.gif)  
### 演示APK
[演示apk下载](https://github.com/Jayqiu/RefreshSwipeRecyclerview/blob/master/AndroidRsRecyclerView/sample_debug.apk) 
### 引用
[aar下载](https://github.com/Jayqiu/RefreshSwipeRecyclerview/tree/master/AndroidRsRecyclerView/app/libs) 
将下载的aar文件放在项目的libs中
你的build.gradle添加
```
repositories {
        flatDir {
            dirs 'libs'   // aar     目录
        }
    }
compile 'com.android.support:recyclerview-v7:23.0.0'
compile(name:'rsrecyclerview-1.0.0',ext:'aar')  
```
### 头部底部添加
// 添加/删除 头部View (支持多个)
mRecyclerView.addHeaderView() 和 .removeHeaderView()

// 添加/删除 底部View (支持多个)
mRecyclerView.addFooterView() 和 .removeFooterView()
### Adapter（BaseRvAdapter）适配
继承BaseRvAdapter<T>
```
/**
     * 取得ItemView的布局文件
     *
     * @return
     */
    public abstract int getItemLayoutID(int viewType);
    /**
     * 绑定数据到Item的控件中去
     *
     * @param holder
     * @param bean
     */
    protected abstract void onBindDataToView(RvCommonViewHolder holder, T bean, int position);
    
```
### SwipeMenuCreator 设置滑动删除
```
mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
 /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = dip2px(ActRefreshGridView.this, 80);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;


            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(ActRefreshGridView.this)
                        .setBackgroundColor(getResources().getColor(R.color.colorAccent))
                        .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

            }
        }
    };
    //设置滑动按钮点击监听
    mRecyclerView.setSwipeMenuItemClickListener(new OnSwipeMenuItemClickListener() {
            @Override
            public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, @RefreshRecyclerView.DirectionMode int direction) {
                Toast.makeText(ActRefreshGridView.this, adapter.getData().get(adapterPosition), Toast.LENGTH_SHORT).show();
                closeable.smoothCloseMenu();
            }
        });
```
### 下拉刷新、上拉加载更多
```
 下拉刷新监听
 mRsRecyclerView.setOnPullRefreshListener(...)
 
 下拉加载完成
 mRsRecyclerView.pullRefreshComplete();
 
 上拉加载更多监听
 mRsRecyclerView.setOnLoadMoreListener(...)
 
 上拉加载完成（解决数据请求慢导致数据重复或分页数据错乱）
 mRsRecyclerView.loadMoreComplete();
 
 是否还有更多数据（true 有更多数据、false 加载完成）
 mRsRecyclerView.setLoadMoreEnabled(true\false);
 
 上拉加载数据出错
 mRsRecyclerView.loadMoreError();
```
### 自定义配置

   | 参数 | 类型 | 默认值 | 说明
   |--- | --- | ---| ---
   | v_divider                 | reference / color               | 无        | 全局分割线divider
   | rv_dividerVertical         | reference / color               | 无        | 垂直分割线divider
   | rv_dividerHorizontal       | reference / color               | 无        | 水平分割线divider
   | rv_dividerHeight           | dimension                      | 1px       | 全局分割线size
   | rv_dividerVerticalHeight   | dimension                      | 1px       | 垂直分割线size
   | rv_dividerHorizontalHeight | dimension                      | 1px       | 水平分割线size
   | rv_isNotShowGridEndDivider | boolean                        | false     | 是否不显示Grid最后item的分割线
   | rv_itemViewBothSidesMargin | dimension                      | 无        | itemView两边的边距（不会设置headerView和footerView的两边）
   | rv_emptyView               | reference                      | 无        | emptyView id
   | rv_isEmptyViewKeepShowHeadOrFooter | boolean                | false     | 显示EmptyView时，是否保留显示已设置的HeadView和FooterView
   | rv_layoutManager           | linear / grid / staggeredGrid  | 无        | 布局类型
   | rv_layoutManagerOrientation| horizontal / vertical          | vertical  | 布局方向
   | rv_spanCount               | integer                        | 2         | 格子数量，rv_layoutManager=grid / staggeredGrid时有效
   | rv_headerDividersEnabled   | boolean                        | false     | 是否启用headView中的分割线
   | rv_footerDividersEnabled   | boolean                        | false     | 是否启用footerView中的分割线

# Thanks
RefreshSwipeRecyclerview 主要的是对 [FamiliarRecyclerView](https://github.com/iwgang/FamiliarRecyclerView) 和[SwipeRecyclerView](https://github.com/yanzhenjie/SwipeRecyclerView) 的一个整合修改
代码暂时还没有提交到Jcenter管理、希望需要的小伙伴clone代码直接引用 rsrecyclerview Module

