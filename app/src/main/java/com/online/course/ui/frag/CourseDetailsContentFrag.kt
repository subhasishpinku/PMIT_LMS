package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.course.R
import com.online.course.databinding.RvNestedBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.adapter.ChapterRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.*
import com.online.course.model.view.ContentItem
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.CommonApiPresenterImpl
import com.online.course.ui.MainActivity
import kotlin.math.roundToInt

class CourseDetailsContentFrag : NetworkObserverFragment(), ItemCallback<List<Chapter>> {

    private lateinit var mBinding: RvNestedBinding
    private lateinit var mCourse: Course
    private lateinit var mPresenter: Presenter.CommonApiPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RvNestedBinding.inflate(inflater, container, false)
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
            if (context != null) {
                val padding =
                    (btnsContainer.height + Utils.changeDpToPx(requireContext(), 20f)).roundToInt()
                mBinding.rvNestedContainer.setPadding(0, 0, 0, padding)
            }
        }
    }

    private fun init() {
        mCourse = requireArguments().getParcelable(App.COURSE)!!

        mPresenter = CommonApiPresenterImpl.getInstance()
        mPresenter.getCourseContent(mCourse.id, this)

        if (!mCourse.hasUserBought) {
            initBottomPadding()
        }
    }

    override fun onItem(items: List<Chapter>, vararg args: Any) {
        if (context == null) return

        mBinding.rvNestedProgressBar.visibility = View.GONE

        val contentItems = ArrayList<ContentItem>()

        for (chapter in items) {
            val contentItem = ContentItem()
            contentItem.title = chapter.title
            contentItem.chapterItems = chapter.items.toMutableList()
            contentItems.add(contentItem)
        }

        val adapter = ChapterRvAdapter(contentItems, mCourse, activity as MainActivity)
        mBinding.rvNestedRv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvNestedRv.adapter = adapter

        if (mCourse.hasUserBought) {
            getCerts(contentItems, adapter)
        }
    }

    private fun getCerts(
        contentItems: ArrayList<ContentItem>,
        adapter: ChapterRvAdapter
    ) {
        mPresenter.getCourseCerts(mCourse.id, object : ItemCallback<List<Quiz>> {
            override fun onItem(items: List<Quiz>, vararg args: Any) {
                if (items.isNotEmpty()) {
                    val contentItem = ContentItem()
                    contentItem.title = getString(R.string.certificates)

                    val certItems = ArrayList<ChapterItem>()
                    for (item in items) {
                        val certItem = ChapterItem()
                        certItem.type = ChapterItem.Type.CERTIFICATE.value
                        certItem.title = item.title
                        certItem.createdAt = item.createdAt
                        certItems.add(certItem)
                    }

                    contentItem.chapterItems = certItems
                    contentItems.add(contentItem)
                    adapter.notifyItemInserted(contentItems.size)
                }
            }
        })
    }
}