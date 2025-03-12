package com.online.course.ui.frag

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.FragForumsBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.CardStatisticsRvAdapter
import com.online.course.manager.adapter.ForumItemRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.*
import com.online.course.model.view.CommonItem
import com.online.course.presenterImpl.ForumsPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abs.EmptyState
import com.online.course.ui.widget.ForumQuestionDialog
import com.online.course.ui.widget.ForumSearchDialog
import java.lang.StringBuilder

class ForumsFrag : NetworkObserverFragment(), EmptyState, View.OnClickListener,
    ItemCallback<String> {

    private lateinit var mBinding: FragForumsBinding
    private lateinit var mCourse: Course
    private lateinit var mPresenter: ForumsPresenterImpl
    private var mSearch: String? = null

    private val mCallback = object : ItemCallback<Any> {
        override fun onItem(item: Any, vararg args: Any) {
            val forumsRvAdapter = mBinding.forumsRv.adapter as ForumItemRvAdapter
            val size = forumsRvAdapter.items.size
            forumsRvAdapter.items.clear()
            forumsRvAdapter.notifyItemRangeRemoved(0, size)
            mBinding.forumsRvProgressBar.visibility = View.VISIBLE

            mPresenter.getForumQuestions(mCourse.id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragForumsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mCourse = requireArguments().getParcelable(App.COURSE)!!
        mSearch = requireArguments().getString(App.KEY)

        initPresenter()
        initUI()
    }

    private fun initPresenter() {
        mPresenter = ForumsPresenterImpl(this)
        if (mSearch.isNullOrEmpty()) {
            mPresenter.getForumQuestions(mCourse.id)
        } else {
            hideStatistics()
            hideBtns()
            mPresenter.searchInCourseForum(mCourse.id, mSearch!!)
        }
    }

    private fun hideStatistics() {
        mBinding.forumsStatisticsRv.visibility = View.GONE
    }

    private fun hideBtns() {
        mBinding.forumsBtnsContainer.visibility = View.GONE
    }

    private fun initUI() {
        mBinding.forumsAskBtn.setOnClickListener(this)
        mBinding.forumsSearchBtn.setOnClickListener(this)
    }

    fun showEmptyState() {
        if (mSearch.isNullOrEmpty()) {
            showEmptyState(
                R.drawable.no_comments,
                R.string.no_questions,
                R.string.no_questions_forum
            )
        } else {
            showEmptyState(R.drawable.no_comments, getString(R.string.no_questions), "")
        }
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.forumsEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    fun onForumReceived(forums: Forums) {
        mBinding.forumsRvProgressBar.visibility = View.GONE

        if (forums.items.isEmpty()) {
            showEmptyState()
            updateUINoQuestions()
            return
        }

        if (mSearch.isNullOrEmpty()) {
            initStatistics(forums)
        } else {
            setToolbarText(forums.items.size)
        }

        initRV(forums.items)
    }

    private fun initRV(items: List<ForumItem>) {
        mBinding.forumsRv.adapter = ForumItemRvAdapter(items, this, mCallback)
    }

    private fun setToolbarText(foundedItems: Int) {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        val builder = StringBuilder()
            .append(foundedItems)
            .append(" ")
            .append(getString(R.string.results_found_for))
            .append("\"")
            .append(mSearch)
            .append("\"")

        (activity as MainActivity).showToolbar(
            toolbarOptions,
            builder.toString()
        )
    }

    private fun updateUINoQuestions() {
        mBinding.forumsSearchBtn.visibility = View.GONE
        val params = mBinding.forumsAskBtn.layoutParams as ConstraintLayout.LayoutParams
        params.marginEnd = resources.getDimension(R.dimen.margin_16).toInt()
        mBinding.forumsAskBtn.requestLayout()
    }

    private fun initStatistics(forums: Forums) {
        val items = ArrayList<CommonItem>()
        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return forums.questionsCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.questions)
            }

            override fun imgResource(): Int {
                return R.drawable.ic_questions_red
            }

            override fun imgBgResource(): Int {
                return R.drawable.circle_light_red
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return forums.resolvedCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.resolved)
            }

            override fun imgResource(): Int {
                return R.drawable.ic_done_green
            }

            override fun imgBgResource(): Int {
                return R.drawable.circle_light_green
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return forums.openQuestionsCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.open_questions)
            }

            override fun imgResource(): Int {
                return R.drawable.ic_more_circle_blue
            }

            override fun imgBgResource(): Int {
                return R.drawable.circle_light_blue
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return forums.commentsCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.answers)
            }

            override fun imgResource(): Int {
                return R.drawable.ic_comments_light_green
            }

            override fun imgBgResource(): Int {
                return R.drawable.circle_light_green2
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return forums.activeUsersCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.active_users)
            }

            override fun imgResource(): Int {
                return R.drawable.ic_user_dark_blue
            }

            override fun imgBgResource(): Int {
                return R.drawable.circle_light_dark_gray
            }
        })

        mBinding.forumsStatisticsRv.adapter = CardStatisticsRvAdapter(items)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.forums_search_btn -> {
                val dialog = ForumSearchDialog()
                dialog.setOnSearchListener(this)
                dialog.show(childFragmentManager, null)
            }

            R.id.forums_ask_btn -> {
                val bundle = Bundle()
                bundle.putInt(App.ID, mCourse.id)

                val dialog = ForumQuestionDialog()
                dialog.arguments = bundle
                dialog.setCallback(mCallback)
                dialog.show(childFragmentManager, null)
            }
        }
    }

    override fun onItem(s: String, vararg args: Any) {
        val bundle = Bundle()
        bundle.putString(App.KEY, s)
        bundle.putParcelable(App.COURSE, mCourse)

        val searchForumFrag = ForumsFrag()
        searchForumFrag.arguments = bundle
        (activity as MainActivity).transact(searchForumFrag)
    }
}