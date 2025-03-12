package com.online.course.manager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class EndLessLoadMoreAdapter<T, VH extends RecyclerView.ViewHolder> extends
        BaseArrayAdapter<T, RecyclerView.ViewHolder> {

    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean mIsLoading = false, mIsMoreDataAvailable = true;
    private final int mLoadingRowLayout;

    public static final int TYPE_ITEMS = 0;
    public static final int TYPE_LOAD = 1;

    public abstract VH onCreateViewItem(ViewGroup parent, int viewType);

    public abstract void onBindViewItem(RecyclerView.ViewHolder viewHolder, int position);

    public EndLessLoadMoreAdapter(List<T> items, @LayoutRes int loadingRowLayout) {
        super(items);
        mLoadingRowLayout = loadingRowLayout;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItems().get(position) == null)
            return TYPE_LOAD;

        return TYPE_ITEMS;
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOAD) {
            return new LoadViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLoadingRowLayout, parent, false));
        } else {
            return onCreateViewItem(parent, viewType);
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount() - 1 && mIsMoreDataAvailable && !mIsLoading && mOnLoadMoreListener != null) {
            mIsLoading = true;
            mOnLoadMoreListener.onLoadMore();
        }

        if (getItemViewType(position) == TYPE_ITEMS) {
            onBindViewItem(holder, position);
        }
    }

    private class LoadViewHolder extends RecyclerView.ViewHolder {
        public LoadViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setLoading(boolean isLoading) {
        mIsLoading = isLoading;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mOnLoadMoreListener = loadMoreListener;
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        mIsMoreDataAvailable = moreDataAvailable;
    }

    public boolean isMoreDataAvailable() {
        return mIsMoreDataAvailable;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
