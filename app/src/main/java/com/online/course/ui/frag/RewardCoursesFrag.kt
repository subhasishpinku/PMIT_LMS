package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.FragRewardCoursesBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.ClassListGridRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.*
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.RewardCoursesPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abs.EmptyState
import com.online.course.ui.widget.CategoriesDialog
import com.online.course.ui.widget.ClassesOptionsDialog

class RewardCoursesFrag : NetworkObserverFragment(), View.OnClickListener, EmptyState {

    private lateinit var mBinding: FragRewardCoursesBinding
    private lateinit var mPresenter: Presenter.RewardCoursesPresenter
    private var mSelectedOptions: ArrayList<KeyValuePair>? = null
    private var mSelectedCategories: ArrayList<KeyValuePair>? = null


    private val mOptionsCallback = object : ItemCallback<List<KeyValuePair>> {
        override fun onItem(item: List<KeyValuePair>, vararg args: Any) {
            removeItems()
            mSelectedOptions = item as ArrayList<KeyValuePair>
            mPresenter.getRewardCourses(mSelectedCategories, mSelectedOptions)
        }
    }

    private val mCategoriesCallback = object : ItemCallback<List<KeyValuePair>> {
        override fun onItem(item: List<KeyValuePair>, vararg args: Any) {
            removeItems()
            mSelectedCategories = item as ArrayList<KeyValuePair>
            mPresenter.getRewardCourses(mSelectedCategories, mSelectedOptions)
        }
    }

    private fun removeItems() {
        val adapter = mBinding.rewardCoursesRv.adapter as ClassListGridRvAdapter
        val itemCount = adapter.itemCount
        adapter.items.clear()
        adapter.notifyItemRangeRemoved(0, itemCount)

        mBinding.rewardCoursesRvProgressBar.visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragRewardCoursesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.reward_courses)

        mPresenter = RewardCoursesPresenterImpl(this)

        mBinding.rewardCoursesOptionsBtn.setOnClickListener(this)
        mBinding.rewardCoursesCategoriesBtn.setOnClickListener(this)
        mBinding.rewardCoursesListDisplayBtn.setOnClickListener(this)

        mPresenter.getRewardCourses(mSelectedCategories, mSelectedOptions)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rewardCoursesOptionsBtn -> {
                val dialog = ClassesOptionsDialog()

                val bundle = Bundle()
                bundle.putSerializable(App.SELECTED, mSelectedOptions)

                dialog.arguments = bundle

                dialog.setCallback(mOptionsCallback)
                dialog.show(childFragmentManager, null)
            }

            R.id.rewardCoursesCategoriesBtn -> {
                val dialog = CategoriesDialog()

                val bundle = Bundle()
                bundle.putParcelableArrayList(App.SELECTED, mSelectedCategories)

                dialog.arguments = bundle

                dialog.setCallback(mCategoriesCallback)
                dialog.show(childFragmentManager, null)
            }

            R.id.rewardCoursesListDisplayBtn -> {
                val categoryCourseRv = mBinding.rewardCoursesRv
                if (categoryCourseRv.adapter == null)
                    return

                val layoutManager = categoryCourseRv.layoutManager as GridLayoutManager

                if (layoutManager.spanCount == 1) {
                    mBinding.rewardCoursesListDisplayBtn.setImageResource(R.drawable.ic_list)
                    layoutManager.spanCount = 2
                } else {
                    mBinding.rewardCoursesListDisplayBtn.setImageResource(R.drawable.ic_grid)
                    layoutManager.spanCount = 1
                }

                val adapter = categoryCourseRv.adapter!!
                adapter.notifyItemRangeChanged(0, adapter.itemCount)
            }
        }
    }

    fun onResultReceived(courses: List<Course>) {
        mBinding.rewardCoursesRvProgressBar.visibility = View.GONE

        val rv = mBinding.rewardCoursesRv

        if (rv.adapter == null) {
            val gridLayoutManager = GridLayoutManager(requireContext(), 2)
            rv.layoutManager = gridLayoutManager

            rv.adapter = ClassListGridRvAdapter(
                courses,
                activity as MainActivity, gridLayoutManager, true
            )
        } else {
            val adapter = rv.adapter as ClassListGridRvAdapter
            adapter.items.addAll(courses)
            adapter.notifyItemRangeInserted(0, adapter.itemCount)
        }

        if (courses.isEmpty()) {
            showEmptyState()
        } else {
            hideEmptyState()
        }
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_course, R.string.no_courses, R.string.no_courses_class)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rewardCoursesEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}