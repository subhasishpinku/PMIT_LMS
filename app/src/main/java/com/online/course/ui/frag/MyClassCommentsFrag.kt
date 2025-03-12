package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.RvBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.ClassCommentRvAdapter
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.model.Comments
import com.online.course.presenterImpl.CommentsPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abs.EmptyState

class MyClassCommentsFrag : Fragment(), OnItemClickListener, ItemCallback<Comments>, EmptyState {

    private lateinit var mBinding: RvBinding
    private lateinit var mCommentsFrag: CommentsFrag

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
        mBinding.rvContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        mBinding.rvEmptyState.root.visibility = View.INVISIBLE
        val presenter = CommentsPresenterImpl()
        presenter.getComments(this)
    }

    override fun onItem(item: Comments, vararg args: Any) {
        if (context == null) return

        mBinding.rvProgressBar.visibility = View.INVISIBLE
        if (item.classComments.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = ClassCommentRvAdapter(item.classComments)
            mBinding.rv.addOnItemTouchListener(ItemClickListener(mBinding.rv, this))
        } else {
            showEmptyState()
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val comment = (mBinding.rv.adapter as ClassCommentRvAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(App.COMMENT, comment)
        bundle.putBoolean(App.INSTRUCTOR_TYPE, true)

        val frag = CommentDetailsFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun showEmptyState() {
        showEmptyState(
            R.drawable.no_comments,
            R.string.no_comments,
            R.string.no_comments_instructor_desc
        )
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}