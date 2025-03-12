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
import com.online.course.databinding.TabBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.AssignmentsRvAdapter
import com.online.course.manager.adapter.ViewPagerAdapter
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.Assignment
import com.online.course.model.ToolbarOptions
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.MyAssignmentsPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.abs.EmptyState

class MyAssignmentsFrag : NetworkObserverFragment(), EmptyState, OnItemClickListener {

    private lateinit var mBinding: RvBinding

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
        val presenter = MyAssignmentsPresenterImpl(this)
        presenter.getMyAssignments()
    }

    fun onAssignmentsReceived(assignments: List<Assignment>) {
        mBinding.rvProgressBar.visibility = View.GONE

        if (assignments.isEmpty()) {
            showEmptyState()
        } else {
            val rv = mBinding.rv
            rv.layoutManager = LinearLayoutManager(requireContext())
            rv.adapter = AssignmentsRvAdapter(assignments)
            rv.addOnItemTouchListener(ItemClickListener(rv, this))
        }
    }

    private fun showEmptyState() {
        showEmptyState(
            R.drawable.no_submission,
            R.string.no_assignments,
            R.string.no_assignments_student_desc
        )
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val assignment = (mBinding.rv.adapter as AssignmentsRvAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(App.ITEM, assignment)

        val overviewFrag = AssignmentOverviewFrag()
        overviewFrag.arguments = bundle
        (activity as MainActivity).transact(overviewFrag)
    }
}