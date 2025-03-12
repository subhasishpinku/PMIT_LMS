package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.FragCourseDetailsInformationBinding
import com.online.course.manager.App
import com.online.course.manager.ExpandableTextViewHelper
import com.online.course.manager.Utils
import com.online.course.manager.Utils.isEllipsized
import com.online.course.manager.adapter.CourseCommonRvAdapter
import com.online.course.manager.adapter.FAQAdapter
import com.online.course.manager.adapter.FeaturedSliderAdapter
import com.online.course.model.Course
import com.online.course.model.view.CourseCommonItem
import com.online.course.model.view.ChapterView
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.course.BaseCourseDetailsInformation
import com.online.course.ui.frag.course.CourseDetailsInformationFactory
import kotlin.math.roundToInt

class CourseDetailsInformationFrag : Fragment(), View.OnClickListener {

    private lateinit var mBinding: FragCourseDetailsInformationBinding
    private lateinit var mInformation: BaseCourseDetailsInformation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragCourseDetailsInformationBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val course = requireArguments().getParcelable<Course>(App.COURSE)!!
        val offlineMode = requireArguments().getBoolean(App.OFFLINE)

        mInformation = CourseDetailsInformationFactory.getInformation(course, requireContext())

        mInformation.setMarkInfo(mBinding)
        initDescription(course)

        if (offlineMode) {
            hideDataInOfflineMode()
        } else {
            initBottomPadding()
            initListInfo()
            initPrerequisites(course)
        }
    }

    private fun initBottomPadding() {
        val btnsContainer =
            (parentFragment as CourseDetailsFrag).mBinding.courseDetailsPurchaseBtnsContainer
        btnsContainer.post {
            if (context == null) return@post

            val padding =
                (btnsContainer.height + Utils.changeDpToPx(requireContext(), 20f)).roundToInt()
            mBinding.courseDetailsInformationContainer.setPadding(0, 0, 0, padding)
        }
    }

    private fun hideDataInOfflineMode() {
        mBinding.courseDetailsInformationRv.visibility = View.GONE

        mBinding.courseDetailsInformationPrerequisitesTv.visibility = View.GONE
        mBinding.courseDetailsInformationPrerequisitesViewPager.visibility = View.GONE
        mBinding.courseDetailsInformationPrerequisitesViewPager.visibility = View.GONE
    }

    private fun initListInfo() {
        val infoList = mInformation.getInfoList()

        if (infoList.isEmpty()) {
            mBinding.courseDetailsInformationRv.visibility = View.GONE
        } else {
            mBinding.courseDetailsInformationRv.adapter = CourseCommonRvAdapter(infoList)
        }
    }

    private fun initDescription(course: Course) {
        val descTv = mBinding.courseDetailsInformationDescTv
        descTv.text = course.description.let {
            Utils.getTextAsHtml(it)
        }
        descTv.post {
            if (descTv.isEllipsized()) {
                mBinding.courseDetailsInformationViewMoreBtn.visibility = View.VISIBLE
                mBinding.courseDetailsInformationDescTvViewMoreBottomGradient.visibility =
                    View.VISIBLE
                mBinding.courseDetailsInformationViewMoreBtn.setOnClickListener(this)
            }
        }

        if (course.faqs.isNotEmpty()) {
            val chapterViews = ArrayList<ChapterView>()
            for (faq in course.faqs) {
                chapterViews.add(
                    ChapterView(
                        faq.title,
                        faq.answer,
                        arrayListOf(CourseCommonItem.creator("", "", 1, 1))
                    )
                )
            }

            mBinding.courseDetailsFaqsRv.adapter = FAQAdapter(chapterViews)
        } else {
            mBinding.courseDetailsFaqsRv.visibility = View.GONE
        }
    }

    private fun initPrerequisites(course: Course) {
        if (course.prerequisites.isEmpty()) {
            mBinding.courseDetailsInformationPrerequisitesTv.visibility = View.GONE
            mBinding.courseDetailsInformationPrerequisitesViewPager.visibility = View.GONE
            mBinding.courseDetailsInformationPrerequisitesIndicator.visibility = View.GONE
        } else {
            mBinding.courseDetailsInformationPrerequisitesViewPager.adapter =
                FeaturedSliderAdapter(course.prerequisites, activity as MainActivity)
            mBinding.courseDetailsInformationPrerequisitesIndicator.setViewPager2(
                mBinding.courseDetailsInformationPrerequisitesViewPager
            )
        }
    }

    override fun onClick(v: View?) {
        val btnText = mBinding.courseDetailsInformationViewMoreBtn.text
        if (btnText == getString(R.string.view_more)) {
            ExpandableTextViewHelper.expandTextView(mBinding.courseDetailsInformationDescTv)
            mBinding.courseDetailsInformationViewMoreBtn.text = getString(R.string.view_less)
            mBinding.courseDetailsInformationDescTvViewMoreBottomGradient.visibility =
                View.INVISIBLE
        } else {
            ExpandableTextViewHelper.collapseTextView(mBinding.courseDetailsInformationDescTv, 6)
            mBinding.courseDetailsInformationViewMoreBtn.text = getString(R.string.view_more)
            mBinding.courseDetailsInformationDescTvViewMoreBottomGradient.visibility = View.VISIBLE
        }
    }
}