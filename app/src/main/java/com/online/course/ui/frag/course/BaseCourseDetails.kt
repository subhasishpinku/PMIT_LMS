package com.online.course.ui.frag.course

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.online.course.manager.adapter.ViewPagerAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.model.AddToCart
import com.online.course.model.Course
import com.online.course.model.Review

abstract class BaseCourseDetails {
    abstract fun getToolbarTitle(): Int
    abstract fun getTabsAdapter(
        context: Context,
        fragmentManager: FragmentManager,
    ): ViewPagerAdapter
    abstract fun getCourseDetails(
        id: Int,
        callback: ItemCallback<Course>
    )
    abstract fun getAddToCartItem() : AddToCart
    abstract fun getBaseReviewObj() : Review
    abstract fun getCourseType(): String
}