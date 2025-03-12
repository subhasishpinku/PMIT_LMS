package com.online.course.ui.widget

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.online.course.manager.Utils
import kotlin.math.abs
import kotlin.math.sqrt

class SliderLayoutManager(
    context: Context,
    orientation: Int,
    private val selectorView: View,
    private val onItemSelectedListener: OnItemSelectedListener
) : LinearLayoutManager(context, orientation, false) {

    private lateinit var mRv: RecyclerView

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        mRv = view!!

        // Smart snapping
        LinearSnapHelper().attachToRecyclerView(mRv)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
//        scaleDownView()
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        return if (orientation == VERTICAL) {
            val scrolled = super.scrollVerticallyBy(dy, recycler, state)
//            scaleDownView()
            scrolled
        } else {
            0
        }
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        return if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
//            scaleDownView()
            scrolled
        } else {
            0
        }
    }


    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        // When scroll stops we notify on the selected item
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            var position = -1

            if (orientation == HORIZONTAL) {
                // Find the closest child to the recyclerView center --> this is the selected item.
                val recyclerViewCenterX = getRecyclerViewCenterX()
                var minDistance = mRv.width
                for (i in 0 until mRv.childCount) {
                    val child = mRv.getChildAt(i)
                    val childCenterX =
                        getDecoratedLeft(child) + (getDecoratedRight(child) - getDecoratedLeft(child)) / 2
                    val newDistance = abs(childCenterX - recyclerViewCenterX)
                    if (newDistance < minDistance) {
                        minDistance = newDistance
                        position = mRv.getChildLayoutPosition(child)
                    }
                }
            } else {
                for (i in 0 until mRv.childCount) {
                    val child = mRv.getChildAt(i)
                    if (Utils.isViewOverlapping(child, selectorView)) {
                        position = mRv.getChildLayoutPosition(child)
                    }
                }
            }

            // Notify on item selection
            if (position >= 0)
                onItemSelectedListener.onItemSelected(position)
        }
    }

    private fun scaleDownView() {
        val mid = width / 2.0f
        for (i in 0 until childCount) {

            // Calculating the distance of the child from the center
            val child = getChildAt(i)
            if (child != null) {
                val childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f
                val distanceFromCenter = abs(mid - childMid)

                // The scaling formula
                val scale = 1 - sqrt((distanceFromCenter / width).toDouble()).toFloat() * 0.66f

                // Set scale to view
                child.scaleX = scale
                child.scaleY = scale
            }
        }
    }

    private fun getRecyclerViewCenterY(): Int {
        Log.d(
            "SliderLayoutManager",
            "getRecyclerViewCenterY -> bottom:${mRv.bottom} top:${mRv.top}"
        )
        return (mRv.bottom - mRv.top) / 2 + mRv.top
    }

    private fun getRecyclerViewCenterX(): Int {
        return (mRv.right - mRv.left) / 2 + mRv.left
    }

    interface OnItemSelectedListener {
        fun onItemSelected(position: Int)
    }
}