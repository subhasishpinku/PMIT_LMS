package com.online.course.ui.frag.course

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.online.course.manager.adapter.ViewPagerAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.model.AddToCart
import com.online.course.model.AddToFav
import com.online.course.model.Course

abstract class BaseCourseAddToFav {
    abstract fun getAddToFavItem(): AddToFav
}