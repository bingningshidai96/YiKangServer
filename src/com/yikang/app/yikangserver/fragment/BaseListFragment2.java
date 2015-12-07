package com.yikang.app.yikangserver.fragment;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.yikang.app.yikangserver.R;
import com.yikang.app.yikangserver.adapter.CommonAdapter;
import com.yikang.app.yikangserver.adapter.ViewHolder;
import com.yikang.app.yikangserver.application.AppContext;
import com.yikang.app.yikangserver.utils.LOG;

/**
 * 基础的列表式的fragment,可以下拉刷新
 * 
 */
public abstract class BaseListFragment2<T> extends BaseFragment implements
		OnRefreshListener, OnScrollListener, OnItemClickListener {
	private static final String TAG = "BaseListFragment";

	/** 结果状态描述 */
	public static final int STATE_NONE = 0x0;
	public static final int STATE_REFRESH = 0x1;
	public static final int STATE_LOADMORE = 0x2;
	public static final int STATE_NOMORE = 0x4;
	public static final int STATE_PRESSNONE = 0x8;// 正在下拉但还没有到刷新的状态

	private int mState = STATE_NONE;

	protected int mCurrentPage; // 当前的页码

	private ListView mListView;

	protected List<T> mData;

	protected SwipeRefreshLayout mRefreshLayout;

	protected View mFootView;

	private ViewGroup tips;

	protected CommonAdapter<T> mAdapter;

	private boolean mLoadMoreEnbale;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mData = new ArrayList<T>();
		mAdapter = new CommonAdapter<T>(getActivity(), mData, getItemLayoutId()) {
			@Override
			public void convert(ViewHolder holder, T item) {
				BaseListFragment2.this.convert(holder, item);
			}
		};
		onRefresh();
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
		View view = inflater.inflate(R.layout.fragment_pull_refresh, container,
				false);
		// tips = (ViewGroup) view.findViewById(R.id.fl_tip);
		mListView = (ListView) view.findViewById(R.id.lv_listview);

		if (mLoadMoreEnbale) {
			mFootView = LayoutInflater.from(getActivity()).inflate(
					R.layout.list_foot_tips, null);
			mListView.addFooterView(mFootView);
		}

		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(this);
		mListView.setOnItemClickListener(this);

		mRefreshLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.srl_swiperefreshlayout);
		mRefreshLayout.setColorSchemeResources(R.color.common_blue,
				R.color.common_orange, R.color.red);
		mRefreshLayout.setOnRefreshListener(this);

		return view;
	}

	/** 是否允许刷新 */
	public void setRefreshEnable(boolean enable) {
		mRefreshLayout.setEnabled(enable);
	}

	/** 是否允许下拉 */
	public void setLoadMoreEnbale(boolean enable) {
		mLoadMoreEnbale = enable;
	}

	public enum RequestType {
		refresh, loadMore
	}

	/**
	 * 请求数据
	 */
	abstract protected void sendRequestData(RequestType requestType);

	@Override
	public void onRefresh() {
		if ((mState & STATE_REFRESH) != 0) { // 防止多次下拉
			return;
		}
		onRefreshing(); // 设置为刷新状态
		sendRequestData(RequestType.refresh); // 请求数据
	}

	protected void onRefreshing() {
		if (mRefreshLayout != null) {
			mRefreshLayout.setRefreshing(true);
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
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		LOG.i(TAG, "[onScrollStateChanged]" + mState);
		if (!mLoadMoreEnbale || (mState & STATE_LOADMORE) != 0) {
			return;
		}
		LOG.i(TAG, "[onScrollStateChanged]" + ">>>>>>");

		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if ((mState & STATE_NOMORE) != 0) {
				AppContext.showToast(getActivity(), "已经没有更多");
			} else if (view.getLastVisiblePosition() >= mData.size() - 1) {
				mCurrentPage++;
				sendRequestData(RequestType.loadMore);
				onLoading();
				Log.i(TAG,
						"[onScrollStateChanged]"
								+ view.getLastVisiblePosition() + "****"
								+ mData.size());
			}
		}

	}

	protected void onLoading() {
		mState |= STATE_LOADMORE;
		if (mFootView != null) {
			mFootView.setVisibility(View.VISIBLE);
			ProgressBar progressBar = (ProgressBar) mFootView
					.findViewById(R.id.progressbar);
			progressBar.setVisibility(View.VISIBLE);

			// TextView tvText = (TextView) mFootView.findViewById(R.id.text);
			// tvText.setText(getString(R.string.loading));
		}
	}

	protected void onLoadFinish() {
		mState &= ~STATE_LOADMORE;
		if (mFootView != null) {
			mFootView.setVisibility(View.GONE);
		}
	}

	protected void setStatus(int status) {
		if ((status & STATE_REFRESH) != 0) {
			onRefreshFinish();
		}
		if ((status & STATE_LOADMORE) != 0) {
			onLoadFinish();
		}
		mState |= status;
	}

	/**
	 * 设置正在加载更多
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

}
