package com.online.course.ui.widget

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View
import com.mindorks.placeholderview.annotations.expand.ChildPosition
import com.mindorks.placeholderview.annotations.expand.ParentPosition
import com.online.course.R
import com.online.course.model.Category

@Layout(R.layout.item_category)
class SubCategoryItem(private val subCategory: Category) {

    @ParentPosition
    var mParentPosition = 0

    @ChildPosition
    var mChildPosition = 0

    @View(R.id.itemCategoryImg)
    lateinit var mTitleTv: TextView

    @View(R.id.itemCategoryDescTv)
    lateinit var mDescTv: TextView

    @View(R.id.itemCategoryImg)
    lateinit var mIconImg: ImageView

    @View(R.id.itemCategoryArrowImg)
    lateinit var mArrowImg: ImageView

    @View(R.id.itemCategoryArrowImg)
    lateinit var mDivider: android.view.View

    init {
        mDivider.visibility = android.view.View.GONE
        mArrowImg.visibility = android.view.View.GONE
    }

    @Resolve
    fun onResolved() {
//        mTitleTv.text = subCategory.title()
//        mDescTv.text = subCategory.desc(mDescTv.context)
//        if (subCategory.img() != null)
//            Glide.with(mIconImg.context).load(subCategory.img()).into(mIconImg)
    }
}