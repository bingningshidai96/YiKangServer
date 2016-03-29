package com.yikang.app.yikangserver.fragment;

import java.util.ArrayList;
import java.util.List;

import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.CommonAdapter;
import com.yikang.app.yikangserver.adapter.ViewHolder;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.utils.LOG;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 基础的列表式的fragment,可以下拉刷新 其中子类可访问的有 mData,mAdapter,mcurentPage,mRefresh
 * <ul>
 * <li>刷新相关方法：{@link #onRefreshing},{@link #onRefreshFinish},
 * {@link ＃setRefreshEnable}
 * <li>加载更改相关方法：{@link #onLoading},{@link #onLoadFinish},
 * {@link #setLoadMoreEnable};
 * </ul>
 */
public abstract class BaseListFragment<T> extends BaseFragment implements
        OnRefreshListener, OnScrollListener, OnItemClickListener{
    private static final String TAG = "BaseListFragment";

    /**
     * 结果状态描述
     */
    public static final int STATE_NONE = 0x0;
    public static final int STATE_REFRESH = 0x1;
    public static final int STATE_LOAD_MORE = 0x2;
    public static final int STATE_NOMORE = 0x4;
    public static final int STATE_PRESSNONE = 0x8;// 正在下拉但还没有到刷新的状态

    private int mState = STATE_NONE;

    protected int mCurrentPage; // 当前的页码

    protected ListView mListView;

    protected List<T> mData;

    protected SwipeRefreshLayout mRefreshLayout;

    protected View mFootView;

    private ViewGroup tips;

    protected CommonAdapter<T> mAdapter;

    private boolean mLoadMoreEnable;

    /**
     * 内容提示，例如请求的列表数据为空,做相关提示
     */
    private View mContentTip;
    /**
     * 网络错误提示，例如网络错误
     */
    private View mNetWorkTip;

    /**
     * 判断对于用户是否可见，例如在ViewPager，只有当前显示pager为true
     */
    private boolean mIsUserVisible = true;

    /**
     * 判断数据是否已经加载过一次。例如在ViewPager中加载一次后再滑动到这一页就不再加载
     */
    private boolean mHasLoadOnce;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<>();
        mAdapter = new CommonAdapter<T>(getActivity(), mData, getItemLayoutId()) {
            @Override
            public void convert(ViewHolder holder, T item) {
                BaseListFragment.this.convert(holder, item);
            }
        };
    }

    /**
     * 当次fragment对于用户的可见性发生改变时会调用次方法
     * 此方法在onCreate方法之前调用
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsUserVisible = isVisibleToUser;
        //下面仅仅是为了请求失败时，下一次切换到这个页面的时候再次请求
        if (mIsUserVisible && getView() != null && !mHasLoadOnce) {
            onRefresh();
        }
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsUserVisible) { //为了延迟加载，只有当页面可见的时候才加载东西
            onRefresh();
        }
    }


    /**
     * 设置listView的itemlayout
     */
    abstract protected int getItemLayoutId();

    /**
     * 将数据填充到itemView中
     */
    abstract protected void convert(ViewHolder holder, T item);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_list, container,
                false);
        tips = (ViewGroup) view.findViewById(R.id.fl_tip);
        mListView = (ListView) view.findViewById(R.id.lv_listview);
        if (mLoadMoreEnable) {
            mFootView = inflater.inflate(R.layout.foot_view_baselist_tips, mListView, false);
            mListView.addFooterView(mFootView);
        }
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_swiperefreshlayout);
        mRefreshLayout.setColorSchemeResources(R.color.common_theme_color, R.color.common_orange, R.color.red);
        mRefreshLayout.setOnRefreshListener(this);

        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);

        return view;
    }


    /**
     * 是否允许刷新
     */
    public void setRefreshEnable(boolean enable) {
        mRefreshLayout.setEnabled(enable);
    }

    /**
     * 是否允许下拉
     */
    public void setLoadMoreEnable(boolean enable) {
        mLoadMoreEnable = enable;
    }

    public enum RequestType {
        refresh, loadMore
    }

    /**
     * 请求数据,此方法的调用时机是在onViewCreated中调用
     */
    abstract protected void sendRequestData(RequestType requestType);

    @Override
    public void onRefresh() {
        if ((mState & STATE_REFRESH) != 0) { // 防止多次下拉
            return;
        }
        onRefreshing(); // 设置为刷新状态
        mHasLoadOnce = true;
        sendRequestData(RequestType.refresh); // 请求数据
    }

    protected void onRefreshing() {
        if (mRefreshLayout != null) {
            //因为mRefreshLayout默认的offset需要获取父控件的高度，
            //如果此时layout没有完成，那么将无法看到刷新状态,故应将它放在UI线程中排队执行
            mRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                }
            });
        }
        mState |= STATE_REFRESH;
        mCurrentPage = 0;
    }

    protected void onRefreshFinish() {
        mRefreshLayout.setRefreshing(false);
        mState &= ~STATE_REFRESH;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        boolean scrollTop = isScrollTop();
        mRefreshLayout.setEnabled(scrollTop);
    }

    /**
     * 判断是否滑动到顶部
     */
    private boolean isScrollTop() {
        if (mListView.getChildCount() == 0) {
            return true;
        }
        return mListView.getFirstVisiblePosition() == 0
                && mListView.getChildAt(0).getTop() == 0;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (!mLoadMoreEnable || (mState & STATE_LOAD_MORE) != 0) {
            return;
        }
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            if ((mState & STATE_NOMORE) != 0) {
                AppContext.showToast(getActivity(), "已经没有更多");
            } else if (view.getLastVisiblePosition() >= mData.size() - 1) {
                mCurrentPage++;
                sendRequestData(RequestType.loadMore);
                onLoading();
            }
        }

    }

    protected void onLoading() {
        mState |= STATE_LOAD_MORE;
        if (mFootView != null) {
            mFootView.setVisibility(View.VISIBLE);
            ProgressBar progressBar = (ProgressBar) mFootView
                    .findViewById(R.id.progressbar);
            progressBar.setVisibility(View.VISIBLE);

            TextView tvText = (TextView) mFootView.findViewById(R.id.text);
            tvText.setText(getString(R.string.waiting_loading));
        }
    }

    protected void onLoadFinish() {
        mState &= ~STATE_LOAD_MORE;
        if (mFootView != null) {
            mFootView.setVisibility(View.GONE);
        }
    }

    protected void setStatus(int status) {
        if ((status & STATE_REFRESH) != 0) {
            onRefreshFinish();
        }
        if ((status & STATE_LOAD_MORE) != 0) {
            onLoadFinish();
        }
        mState |= status;
    }

    /**
     * 设置FootView中的文字。
     */
    protected void setFootViewText(String msg) {
        if (mFootView != null) {
            ProgressBar progressBar = (ProgressBar) mFootView
                    .findViewById(R.id.progressbar);
            progressBar.setVisibility(View.GONE);

            TextView tvText = (TextView) mFootView.findViewById(R.id.text);
            tvText.setText(msg);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
    }

    /**
     * 设置加载失败或者数据为空的tip,此方法必须在onCreateView之后调用才会有效
     */
    final protected void setTips(final View view) {
        if (view == null) {
            return;
        }
        if (tips == null) {
            LOG.e(TAG, "This invoke invalid.You should not invoke this method berfor onCreateView().");
            return;
        }

        if (tips.indexOfChild(view) == -1) {
            if (tips.getChildCount() > 0) {
                tips.removeAllViews();
            }
            tips.addView(view);
        }
    }

    /**
     * 让tip显示出来
     */
    protected void setTipsVisible(boolean visible) {
        tips.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置默认内容提示信息
     */
    protected View createDefaultContentTips() {
        if (getActivity() == null) {
            LOG.e(TAG, "[onLoadResult] activity is been detroyed");
            return null;
        }
        if (mContentTip == null) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            mContentTip = inflater
                    .inflate(R.layout.error_data_fail_tips, tips, false);
            ((TextView) mContentTip.findViewById(R.id.tv_data_fail_describe))
                    .setText(R.string.default_content_data_fail_describe);
            ((ImageView) mContentTip.findViewById(R.id.iv_data_fail))
                    .setImageResource(R.drawable.img_no_data);
        }
        return mContentTip;
    }

    /**
     * 创建默认的网络错误提示信息
     */
    protected View createDefaultNetWorkTips() {
        if (getActivity() == null) {
            LOG.e(TAG, "[onLoadResult] activity is been detroyed");
            return null;
        }
        if (mNetWorkTip == null) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            mNetWorkTip = inflater.inflate(R.layout.error_data_fail_tips, tips, false);
            ((TextView) mNetWorkTip.findViewById(R.id.tv_data_fail_describe))
                    .setText(R.string.default_network_data_fail_describe);
            ((ImageView) mNetWorkTip.findViewById(R.id.iv_data_fail))
                    .setImageResource(R.drawable.img_net_fail);
        }
        return mNetWorkTip;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tips.removeAllViews();
    }

}
