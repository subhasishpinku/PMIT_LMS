package com.online.course.manager.adapter;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;


public abstract class BaseArrayAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private final List<T> mItems;

    public BaseArrayAdapter(List<T> items) {
        mItems = Objects.requireNonNull(items, "items=null");
    }

    @Override
    public final int getItemCount() {
        return mItems.size();
    }

    public final List<T> getItems() {
        return mItems;
    }

}
