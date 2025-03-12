package com.online.course.manager.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class ItemClickListener implements RecyclerView.OnItemTouchListener {
    private final OnItemClickListener clicklistener;
    private final GestureDetector gestureDetector;

    public ItemClickListener(RecyclerView rv, OnItemClickListener clicklistener) {

        this.clicklistener = clicklistener;
        gestureDetector = new GestureDetector(rv.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clicklistener != null) {
                    clicklistener.onLongClick(child, rv.getChildAdapterPosition(child), rv.getId());
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
            clicklistener.onClick(child, rv.getChildAdapterPosition(child), rv.getId());
        }

        return false;
    }

    @Override
    public void onTouchEvent(@NotNull RecyclerView rv, @NotNull MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
