package com.online.course.ui.frag

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.FragCourseDetailsReviewsBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.adapter.ReviewRvAdapter
import com.online.course.model.Course
import com.online.course.model.Review
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.abs.EmptyState
import kotlin.math.roundToInt

class CourseDetailsReviewsFrag : Fragment(), EmptyState {

    private lateinit var mBinding: FragCourseDetailsReviewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragCourseDetailsReviewsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun initBottomPadding() {
        val btnsContainer =
            (parentFragment as CourseDetailsFrag).mBinding.courseDetailsPurchaseBtnsContainer
        btnsContainer.post {
            val padding =
                (btnsContainer.height + Utils.changeDpToPx(requireContext(), 20f)).roundToInt()
            mBinding.courseDetailsReviewsContainer.setPadding(0, 0, 0, padding)
        }
    }

    private fun init() {
        initBottomPadding()
        val course = requireArguments().getParcelable<Course>(App.COURSE)!!

        initEmptyState()

        if (course.reviews.isEmpty()) {
            mBinding.courseDetailsReviewsHeaderContainer.visibility = View.GONE
            showEmptyState()
            return
        }

        mBinding.courseDetailsReviewsRatingTv.text = course.rating.toString()
        mBinding.courseDetailsReviewsRatingBar.rating = course.rating
        mBinding.courseDetailsReviewsCountTv.text =
            ("${course.reviewsCount} ${getString(R.string.reviews)}")

        mBinding.courseDetailsReviewsContentQualityProgressBar.progress =
            course.rateType!!.contentQuality.toInt()
        mBinding.courseDetailsReviewsInstructorSkillsProgressBar.progress =
            course.rateType!!.instructorSkills.toInt()
        mBinding.courseDetailsReviewsPurchaseWorthProgressBar.progress =
            course.rateType!!.purchaseWorth.toInt()
        mBinding.courseDetailsReviewsSupportQualityProgressBar.progress =
            course.rateType!!.supportQuality.toInt()

        mBinding.courseDetailsReviewsRv.adapter = ReviewRvAdapter(course.reviews)
    }

    private fun initEmptyState() {
        val params = mBinding.courseDetailsReviewsEmptyState.root.layoutParams as LinearLayout.LayoutParams
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.topMargin = resources.getDimension(R.dimen.margin_50).toInt()
        mBinding.courseDetailsReviewsEmptyState.root.requestLayout()
    }

    fun addReview(review: Review) {
        hideEmptyState()
        val adatper = mBinding.courseDetailsReviewsRv.adapter as ReviewRvAdapter
        if (adatper.itemCount == 0) {
            mBinding.courseDetailsReviewsRv.visibility = View.VISIBLE
        }
        adatper.items.add(0, review)
        adatper.notifyItemInserted(0)
    }

    fun showEmptyState() {
        mBinding.courseDetailsReviewsRv.visibility = View.GONE
        showEmptyState(
            R.drawable.no_comments,
            R.string.no_reviews,
            R.string.no_reviews_for_this_course
        )
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.courseDetailsReviewsEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}