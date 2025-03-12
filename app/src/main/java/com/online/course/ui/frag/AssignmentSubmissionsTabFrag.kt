package com.online.course.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.online.course.R
import com.online.course.databinding.TabBinding
import com.online.course.manager.App
import com.online.course.manager.adapter.ViewPagerAdapter
import com.online.course.model.*
import com.online.course.ui.MainActivity

class AssignmentSubmissionsTabFrag : Fragment() {

    private lateinit var mBinding: TabBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = TabBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val assignments = requireArguments().getParcelableArrayList<Assignment>(App.ITEMS)!!
        val insturctorType = requireArguments().getBoolean(App.INSTRUCTOR_TYPE)

        initToolbar()

        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager

        val adapter = ViewPagerAdapter(childFragmentManager)

        var bundle = Bundle()
        bundle.putBoolean(App.INSTRUCTOR_TYPE, insturctorType)
        bundle.putParcelableArrayList(App.ITEMS, getPendingAssignments(assignments))
        val pendingFrag = AssignmentStudentsFrag()
        pendingFrag.arguments = bundle
        adapter.add(pendingFrag, getString(R.string.pending))

        bundle = Bundle()
        bundle.putBoolean(App.INSTRUCTOR_TYPE, insturctorType)
        bundle.putParcelableArrayList(App.ITEMS, assignments)
        val allSubmissionsFrag = AssignmentStudentsFrag()
        allSubmissionsFrag.arguments = bundle
        adapter.add(allSubmissionsFrag, getString(R.string.all_submissions))

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun initToolbar() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.submissions)
    }

    private fun getPendingAssignments(assignments: List<Assignment>): ArrayList<Assignment> {
        val pendingAssignments = ArrayList<Assignment>()
        for (assignment in assignments) {
            if (assignment.userStatus == Assignment.UserStatus.PENDING.value) {
                pendingAssignments.add(assignment)
            }
        }
        return pendingAssignments
    }
}