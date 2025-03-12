package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.RvBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.BlogRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.Blog
import com.online.course.model.Category
import com.online.course.model.ToolbarOptions
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.BlogPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abs.EmptyState
import com.online.course.ui.widget.BlogCategoriesDialog

class BlogsFrag : NetworkObserverFragment(), OnItemClickListener, EmptyState,
    ItemCallback<Category> {

    private lateinit var mBinding: RvBinding
    private lateinit var mPresenter: Presenter.BlogPresenter
    private var mCatId: Int? = null

    private val mSelectedCatClickCallback = object : ItemCallback<Any> {
        override fun onItem(item: Any, vararg args: Any) {
            val dialog = BlogCategoriesDialog()
            dialog.setCallback(this@BlogsFrag)
            dialog.show(childFragmentManager, null)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV
        toolbarOptions.endIcon = ToolbarOptions.Icon.FILTER

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.blogs)
        (activity as MainActivity).setOnFilterButtonClickListener(mSelectedCatClickCallback)

        mPresenter = BlogPresenterImpl(this)

        if (mCatId == null) {
            mPresenter.getBlogs()
        } else {
            mPresenter.getBlogs(mCatId!!)
        }
    }

    fun onBlogsRecevied(data: List<Blog>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE

        if (data.isNotEmpty()) {

            if (mBinding.rv.adapter == null) {
                mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
                mBinding.rv.addOnItemTouchListener(ItemClickListener(mBinding.rv, this))
                mBinding.rv.adapter = BlogRvAdapter(data)
            } else {
                val blogAdapter = mBinding.rv.adapter as BlogRvAdapter
                blogAdapter.items.addAll(data)
                blogAdapter.notifyItemRangeInserted(0, blogAdapter.itemCount)
            }

        } else {
            showEmptyState()
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val blog = (mBinding.rv.adapter as BlogRvAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(App.BLOG, blog)

        val blogDetailsFrag = BlogDetailsFrag()
        blogDetailsFrag.arguments = bundle

        (activity as MainActivity).transact(blogDetailsFrag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }


    fun showEmptyState() {
        showEmptyState(R.drawable.no_blog, R.string.no_blog, R.string.no_blog_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun onItem(item: Category, vararg args: Any) {
        mCatId = item.id

        val blogAdapter = mBinding.rv.adapter as BlogRvAdapter
        val size = blogAdapter.itemCount
        blogAdapter.items.clear()
        blogAdapter.notifyItemRangeRemoved(0, size)

        mBinding.rvProgressBar.visibility = View.VISIBLE

        mPresenter.getBlogs(item.id)
    }
}