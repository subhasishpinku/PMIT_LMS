package com.online.course.ui.frag.course

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.online.course.R
import com.online.course.manager.App
import com.online.course.manager.adapter.ViewPagerAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.model.AddToCart
import com.online.course.model.Course
import com.online.course.model.Review
import com.online.course.presenterImpl.CommonApiPresenterImpl
import com.online.course.ui.frag.BundleCoursesFrag
import com.online.course.ui.frag.*

class BundleCourseDetails(private val course: Course) : BaseCourseDetails() {

    override fun getToolbarTitle(): Int {
        return R.string.bundle_details
    }

    override fun getTabsAdapter(
        context: Context,
        fragmentManager: FragmentManager,
    ): ViewPagerAdapter {

        val bundle = Bundle()
        bundle.putParcelable(App.COURSE, course)

        val informationFrag = CourseDetailsInformationFrag()
        val coursesFrag = BundleCoursesFrag()
        val reviewsFrag = CourseDetailsReviewsFrag()
        val commentsFrag = CourseDetailsCommentsFrag()

        informationFrag.arguments = bundle
        coursesFrag.arguments = bundle
        reviewsFrag.arguments = bundle
        commentsFrag.arguments = bundle

        val adapter = ViewPagerAdapter(fragmentManager)
        adapter.add(informationFrag, context.getString(R.string.information))
        adapter.add(coursesFrag, context.getString(R.string.courses))
        adapter.add(reviewsFrag, context.getString(R.string.reviews))
        adapter.add(commentsFrag, context.getString(R.string.comments))

        return adapter
    }

    override fun getCourseDetails(id: Int, callback: ItemCallback<Course>) {
        val presenter = CommonApiPresenterImpl.getInstance()
        presenter.getBundleDetails(id, callback)
    }

    override fun getAddToCartItem(): AddToCart {
        val addToCart = AddToCart()
        addToCart.itemId = course.id
        addToCart.itemName = AddToCart.ItemType.BUNDLE.value
        return addToCart
    }

    override fun getBaseReviewObj(): Review {
        val review = Review()
        review.id = course.id
        review.item = Course.Type.BUNDLE.value
        return review
    }

    override fun getCourseType(): String {
        return Course.Type.BUNDLE.value
    }

}