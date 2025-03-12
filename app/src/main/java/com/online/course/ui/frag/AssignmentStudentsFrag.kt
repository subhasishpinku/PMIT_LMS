package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.course.R
import com.online.course.databinding.EmptyStateBinding
import com.online.course.databinding.RvBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.AssignmentStudentRvAdapter
import com.online.course.manager.adapter.QuizResultRvAdapter
import com.online.course.manager.adapter.QuizRvAdapter
import com.online.course.manager.adapter.QuizStudentResultRvAdapter
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.Assignment
import com.online.course.model.Quiz
import com.online.course.model.QuizResult
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.QuzziesPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abs.EmptyState
import java.io.Serializable

class AssignmentStudentsFrag : NetworkObserverFragment(), OnItemClickListener, EmptyState {

    private lateinit var mBinding: RvBinding
    private var mIsInstructorType = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val assignments = requireArguments().getParcelableArrayList<Assignment>(App.ITEMS)!!
        mIsInstructorType = requireArguments().getBoolean(App.INSTRUCTOR_TYPE)

        mBinding.rvProgressBar.visibility = View.GONE
        if (assignments.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = AssignmentStudentRvAdapter(assignments)
            mBinding.rv.addOnItemTouchListener(ItemClickListener(mBinding.rv, this))
        } else {
            showEmptyState()
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val assignment = ((mBinding.rv.adapter) as AssignmentStudentRvAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(App.ITEM, assignment)
        bundle.putBoolean(App.INSTRUCTOR_TYPE, mIsInstructorType)

        val frag = AssignmentConversationFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun showEmptyState() {
        showEmptyState(
            R.drawable.no_submission,
            R.string.no_submissions,
            R.string.no_submissions_desc
        )
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}